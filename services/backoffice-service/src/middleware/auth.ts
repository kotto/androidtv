import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { prisma } from '../utils/database';
import { redisClient } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';

// Extend Express Request interface
declare global {
  namespace Express {
    interface Request {
      user?: {
        id: string;
        email: string;
        role: string;
        permissions: string[];
      };
    }
  }
}

// JWT Secret
const JWT_SECRET = process.env.JWT_SECRET || 'your-super-secret-jwt-key';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '24h';

/**
 * Generate JWT token
 */
export const generateToken = (payload: any): string => {
  return jwt.sign(payload, JWT_SECRET, {
    expiresIn: JWT_EXPIRES_IN,
    issuer: 'maatcore-backoffice',
    audience: 'maatcore-users'
  });
};

/**
 * Verify JWT token
 */
export const verifyToken = (token: string): any => {
  try {
    return jwt.verify(token, JWT_SECRET, {
      issuer: 'maatcore-backoffice',
      audience: 'maatcore-users'
    });
  } catch (error) {
    throw new Error('Invalid token');
  }
};

/**
 * Authentication middleware
 */
export const authenticate = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      logHelpers.logSecurity('Missing or invalid authorization header', 'medium', {
        ip: req.ip,
        userAgent: req.get('User-Agent'),
        path: req.path
      });
      return res.status(401).json({
        success: false,
        message: 'Access token required'
      });
    }

    const token = authHeader.substring(7); // Remove 'Bearer ' prefix
    
    // Check if token is blacklisted
    const isBlacklisted = await redisClient.exists(`blacklist:${token}`);
    if (isBlacklisted) {
      logHelpers.logSecurity('Blacklisted token used', 'high', {
        token: token.substring(0, 20) + '...',
        ip: req.ip,
        userAgent: req.get('User-Agent')
      });
      return res.status(401).json({
        success: false,
        message: 'Token has been revoked'
      });
    }

    // Verify token
    const decoded = verifyToken(token);
    
    // Check if user still exists and is active
    const user = await prisma.user.findFirst({
      where: {
        id: decoded.userId,
        isActive: true,
        deletedAt: null
      },
      include: {
        role: {
          include: {
            permissions: true
          }
        }
      }
    });

    if (!user) {
      logHelpers.logSecurity('Token for non-existent or inactive user', 'high', {
        userId: decoded.userId,
        ip: req.ip,
        userAgent: req.get('User-Agent')
      });
      return res.status(401).json({
        success: false,
        message: 'User not found or inactive'
      });
    }

    // Check if user has backoffice access
    const hasBackofficeAccess = user.role.permissions.some(
      (permission: any) => permission.name === 'BACKOFFICE_ACCESS'
    );

    if (!hasBackofficeAccess) {
      logHelpers.logSecurity('User without backoffice access attempted access', 'medium', {
        userId: user.id,
        email: user.email,
        role: user.role.name,
        ip: req.ip
      });
      return res.status(403).json({
        success: false,
        message: 'Insufficient permissions for backoffice access'
      });
    }

    // Attach user to request
    req.user = {
      id: user.id,
      email: user.email,
      role: user.role.name,
      permissions: user.role.permissions.map((p: any) => p.name)
    };

    // Log successful authentication
    logHelpers.logAuth('Token verified', user.id, req.ip, true);
    
    next();
  } catch (error) {
    logHelpers.logSecurity('Authentication error', 'medium', {
      error: error instanceof Error ? error.message : 'Unknown error',
      ip: req.ip,
      userAgent: req.get('User-Agent')
    });
    
    return res.status(401).json({
      success: false,
      message: 'Invalid or expired token'
    });
  }
};

/**
 * Authorization middleware factory
 */
export const authorize = (requiredPermissions: string | string[]) => {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user) {
      return res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
    }

    const permissions = Array.isArray(requiredPermissions) 
      ? requiredPermissions 
      : [requiredPermissions];

    const hasPermission = permissions.some(permission => 
      req.user!.permissions.includes(permission)
    );

    if (!hasPermission) {
      logHelpers.logSecurity('Insufficient permissions', 'medium', {
        userId: req.user.id,
        requiredPermissions: permissions,
        userPermissions: req.user.permissions,
        path: req.path,
        method: req.method
      });
      
      return res.status(403).json({
        success: false,
        message: 'Insufficient permissions',
        required: permissions
      });
    }

    next();
  };
};

/**
 * Role-based authorization middleware
 */
export const requireRole = (requiredRoles: string | string[]) => {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user) {
      return res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
    }

    const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];
    
    if (!roles.includes(req.user.role)) {
      logHelpers.logSecurity('Insufficient role', 'medium', {
        userId: req.user.id,
        userRole: req.user.role,
        requiredRoles: roles,
        path: req.path,
        method: req.method
      });
      
      return res.status(403).json({
        success: false,
        message: 'Insufficient role',
        required: roles
      });
    }

    next();
  };
};

/**
 * Optional authentication middleware (doesn't fail if no token)
 */
export const optionalAuth = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return next();
    }

    const token = authHeader.substring(7);
    
    // Check if token is blacklisted
    const isBlacklisted = await redisClient.exists(`blacklist:${token}`);
    if (isBlacklisted) {
      return next();
    }

    // Verify token
    const decoded = verifyToken(token);
    
    // Get user
    const user = await prisma.user.findFirst({
      where: {
        id: decoded.userId,
        isActive: true,
        deletedAt: null
      },
      include: {
        role: {
          include: {
            permissions: true
          }
        }
      }
    });

    if (user) {
      req.user = {
        id: user.id,
        email: user.email,
        role: user.role.name,
        permissions: user.role.permissions.map((p: any) => p.name)
      };
    }
    
    next();
  } catch (error) {
    // Silently continue without authentication
    next();
  }
};

/**
 * Logout middleware (blacklist token)
 */
export const logout = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (authHeader && authHeader.startsWith('Bearer ')) {
      const token = authHeader.substring(7);
      
      // Decode token to get expiration
      const decoded = jwt.decode(token) as any;
      if (decoded && decoded.exp) {
        const expiresIn = decoded.exp - Math.floor(Date.now() / 1000);
        if (expiresIn > 0) {
          // Blacklist token until it expires
          await redisClient.set(`blacklist:${token}`, 'true', expiresIn);
        }
      }
      
      logHelpers.logAuth('User logged out', req.user?.id, req.ip, true);
    }
    
    next();
  } catch (error) {
    logger.error('Logout error:', error);
    next();
  }
};

/**
 * Admin only middleware
 */
export const adminOnly = requireRole(['PLATFORM_ADMIN']);

/**
 * Moderator or Admin middleware
 */
export const moderatorOrAdmin = requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']);

/**
 * Content management permissions
 */
export const canManageContent = authorize(['BACKOFFICE_CONTENT_MODERATE', 'PLATFORM_ADMIN']);

/**
 * Avatar management permissions
 */
export const canManageAvatars = authorize(['BACKOFFICE_AVATAR_MANAGE', 'PLATFORM_ADMIN']);

/**
 * Studio management permissions
 */
export const canManageStudios = authorize(['BACKOFFICE_STUDIO_MANAGE', 'PLATFORM_ADMIN']);

/**
 * RSS management permissions
 */
export const canManageRSS = authorize(['BACKOFFICE_RSS_MANAGE', 'PLATFORM_ADMIN']);