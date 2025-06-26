import { Server as SocketIOServer, Socket } from 'socket.io';
import { Server as HTTPServer } from 'http';
import jwt from 'jsonwebtoken';
import { redisClient } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import { prisma } from '../utils/database';

interface AuthenticatedSocket extends Socket {
  userId?: string;
  userRole?: string;
  permissions?: string[];
}

interface SocketUser {
  id: string;
  email: string;
  role: string;
  permissions: string[];
}

export class SocketHandler {
  private io: SocketIOServer;
  private connectedUsers: Map<string, Set<string>> = new Map(); // userId -> Set of socketIds
  private userSockets: Map<string, SocketUser> = new Map(); // socketId -> user info

  constructor(server: HTTPServer) {
    this.io = new SocketIOServer(server, {
      cors: {
        origin: process.env.CORS_ORIGIN?.split(',') || ['http://localhost:3000'],
        credentials: true
      },
      transports: ['websocket', 'polling']
    });

    this.setupMiddleware();
    this.setupEventHandlers();
  }

  private setupMiddleware() {
    // Authentication middleware
    this.io.use(async (socket: AuthenticatedSocket, next) => {
      try {
        const token = socket.handshake.auth.token || socket.handshake.headers.authorization?.replace('Bearer ', '');
        
        if (!token) {
          return next(new Error('Authentication token required'));
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET!) as any;
        
        // Check if token is blacklisted
        const isBlacklisted = await redisClient.get(`blacklist:${token}`);
        if (isBlacklisted) {
          return next(new Error('Token is blacklisted'));
        }

        // Get user info from Redis session
        const sessionKey = `session:${decoded.userId}`;
        const sessionData = await redisClient.get(sessionKey);
        
        if (!sessionData) {
          return next(new Error('Session not found'));
        }

        const session = JSON.parse(sessionData);
        
        // Check if user has backoffice access
        if (!session.permissions?.includes('BACKOFFICE_ACCESS')) {
          return next(new Error('Insufficient permissions'));
        }

        socket.userId = decoded.userId;
        socket.userRole = session.role;
        socket.permissions = session.permissions;
        
        this.userSockets.set(socket.id, {
          id: decoded.userId,
          email: session.email,
          role: session.role,
          permissions: session.permissions
        });

        logHelpers.logAuth('WebSocket connection authenticated', {
          userId: decoded.userId,
          socketId: socket.id,
          userAgent: socket.handshake.headers['user-agent']
        });

        next();
      } catch (error) {
        logger.error('WebSocket authentication failed', {
          error: error instanceof Error ? error.message : 'Unknown error',
          socketId: socket.id
        });
        next(new Error('Authentication failed'));
      }
    });
  }

  private setupEventHandlers() {
    this.io.on('connection', (socket: AuthenticatedSocket) => {
      this.handleConnection(socket);
      
      socket.on('disconnect', () => this.handleDisconnection(socket));
      socket.on('join_room', (data) => this.handleJoinRoom(socket, data));
      socket.on('leave_room', (data) => this.handleLeaveRoom(socket, data));
      socket.on('subscribe_notifications', () => this.handleSubscribeNotifications(socket));
      socket.on('unsubscribe_notifications', () => this.handleUnsubscribeNotifications(socket));
    });
  }

  private handleConnection(socket: AuthenticatedSocket) {
    const userId = socket.userId!;
    
    // Track user connections
    if (!this.connectedUsers.has(userId)) {
      this.connectedUsers.set(userId, new Set());
    }
    this.connectedUsers.get(userId)!.add(socket.id);

    // Join user-specific room
    socket.join(`user:${userId}`);
    
    // Join role-based rooms
    if (socket.userRole) {
      socket.join(`role:${socket.userRole}`);
    }
    
    // Join permission-based rooms
    if (socket.permissions) {
      socket.permissions.forEach(permission => {
        socket.join(`permission:${permission}`);
      });
    }

    logger.info('WebSocket client connected', {
      userId,
      socketId: socket.id,
      totalConnections: this.io.engine.clientsCount
    });

    // Send welcome message with current status
    socket.emit('connected', {
      message: 'Connected to Backoffice WebSocket',
      userId,
      timestamp: new Date().toISOString()
    });
  }

  private handleDisconnection(socket: AuthenticatedSocket) {
    const userId = socket.userId;
    
    if (userId) {
      const userSockets = this.connectedUsers.get(userId);
      if (userSockets) {
        userSockets.delete(socket.id);
        if (userSockets.size === 0) {
          this.connectedUsers.delete(userId);
        }
      }
    }
    
    this.userSockets.delete(socket.id);

    logger.info('WebSocket client disconnected', {
      userId,
      socketId: socket.id,
      totalConnections: this.io.engine.clientsCount
    });
  }

  private handleJoinRoom(socket: AuthenticatedSocket, data: { room: string }) {
    const { room } = data;
    
    // Validate room access based on permissions
    if (this.canAccessRoom(socket, room)) {
      socket.join(room);
      socket.emit('room_joined', { room, timestamp: new Date().toISOString() });
      
      logger.debug('User joined room', {
        userId: socket.userId,
        room,
        socketId: socket.id
      });
    } else {
      socket.emit('error', { message: 'Access denied to room', room });
    }
  }

  private handleLeaveRoom(socket: AuthenticatedSocket, data: { room: string }) {
    const { room } = data;
    
    socket.leave(room);
    socket.emit('room_left', { room, timestamp: new Date().toISOString() });
    
    logger.debug('User left room', {
      userId: socket.userId,
      room,
      socketId: socket.id
    });
  }

  private handleSubscribeNotifications(socket: AuthenticatedSocket) {
    socket.join('notifications');
    socket.emit('notifications_subscribed', { timestamp: new Date().toISOString() });
  }

  private handleUnsubscribeNotifications(socket: AuthenticatedSocket) {
    socket.leave('notifications');
    socket.emit('notifications_unsubscribed', { timestamp: new Date().toISOString() });
  }

  private canAccessRoom(socket: AuthenticatedSocket, room: string): boolean {
    const permissions = socket.permissions || [];
    
    // Define room access rules
    const roomPermissions: { [key: string]: string[] } = {
      'avatars': ['BACKOFFICE_ACCESS'],
      'studios': ['BACKOFFICE_ACCESS'],
      'content': ['BACKOFFICE_ACCESS'],
      'workflows': ['BACKOFFICE_ACCESS'],
      'rss': ['BACKOFFICE_ACCESS'],
      'admin': ['PLATFORM_ADMIN'],
      'moderators': ['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']
    };
    
    const requiredPermissions = roomPermissions[room];
    if (!requiredPermissions) {
      return false; // Unknown room
    }
    
    return requiredPermissions.some(permission => permissions.includes(permission));
  }

  // Public methods for broadcasting events
  
  /**
   * Broadcast to all connected users
   */
  public broadcast(event: string, data: any) {
    this.io.emit(event, {
      ...data,
      timestamp: new Date().toISOString()
    });
  }

  /**
   * Send message to specific user
   */
  public sendToUser(userId: string, event: string, data: any) {
    this.io.to(`user:${userId}`).emit(event, {
      ...data,
      timestamp: new Date().toISOString()
    });
  }

  /**
   * Send message to users with specific role
   */
  public sendToRole(role: string, event: string, data: any) {
    this.io.to(`role:${role}`).emit(event, {
      ...data,
      timestamp: new Date().toISOString()
    });
  }

  /**
   * Send message to users with specific permission
   */
  public sendToPermission(permission: string, event: string, data: any) {
    this.io.to(`permission:${permission}`).emit(event, {
      ...data,
      timestamp: new Date().toISOString()
    });
  }

  /**
   * Send message to specific room
   */
  public sendToRoom(room: string, event: string, data: any) {
    this.io.to(room).emit(event, {
      ...data,
      timestamp: new Date().toISOString()
    });
  }

  /**
   * Notify about avatar changes
   */
  public notifyAvatarChange(action: string, avatar: any, userId?: string) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'avatar_change', {
      action,
      avatar,
      userId
    });
  }

  /**
   * Notify about studio changes
   */
  public notifyStudioChange(action: string, studio: any, userId?: string) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'studio_change', {
      action,
      studio,
      userId
    });
  }

  /**
   * Notify about content changes
   */
  public notifyContentChange(action: string, content: any, userId?: string) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'content_change', {
      action,
      content,
      userId
    });
  }

  /**
   * Notify about workflow changes
   */
  public notifyWorkflowChange(action: string, workflow: any, userId?: string) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'workflow_change', {
      action,
      workflow,
      userId
    });
  }

  /**
   * Notify about RSS changes
   */
  public notifyRSSChange(action: string, rss: any, userId?: string) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'rss_change', {
      action,
      rss,
      userId
    });
  }

  /**
   * Notify about system alerts
   */
  public notifySystemAlert(level: 'info' | 'warning' | 'error', message: string, details?: any) {
    this.sendToPermission('BACKOFFICE_ACCESS', 'system_alert', {
      level,
      message,
      details
    });
  }

  /**
   * Get connected users count
   */
  public getConnectedUsersCount(): number {
    return this.connectedUsers.size;
  }

  /**
   * Get total connections count
   */
  public getTotalConnectionsCount(): number {
    return this.io.engine.clientsCount;
  }

  /**
   * Check if user is connected
   */
  public isUserConnected(userId: string): boolean {
    return this.connectedUsers.has(userId);
  }

  /**
   * Get connected users list
   */
  public getConnectedUsers(): string[] {
    return Array.from(this.connectedUsers.keys());
  }

  /**
   * Disconnect user sessions
   */
  public disconnectUser(userId: string, reason?: string) {
    const userSockets = this.connectedUsers.get(userId);
    if (userSockets) {
      userSockets.forEach(socketId => {
        const socket = this.io.sockets.sockets.get(socketId);
        if (socket) {
          socket.emit('force_disconnect', { reason: reason || 'Session terminated' });
          socket.disconnect(true);
        }
      });
    }
  }
}

// Export singleton instance
let socketHandler: SocketHandler;

export const initializeSocketHandler = (server: HTTPServer): SocketHandler => {
  if (!socketHandler) {
    socketHandler = new SocketHandler(server);
  }
  return socketHandler;
};

export const getSocketHandler = (): SocketHandler => {
  if (!socketHandler) {
    throw new Error('Socket handler not initialized');
  }
  return socketHandler;
};