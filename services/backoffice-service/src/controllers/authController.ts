import { Request, Response } from 'express';
import { prisma } from '../utils/database';
import { redisClient, CacheKeys } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import {
  AppError,
  NotFoundError,
  ValidationError,
  AuthenticationError,
  asyncHandler,
  throwIfNotFound,
  handleDatabaseError
} from '../utils/errors';
import { generateToken, verifyToken } from '../middleware/auth';
import bcrypt from 'bcryptjs';
import axios from 'axios';

/**
 * Login user
 */
export const login = asyncHandler(async (req: Request, res: Response) => {
  const { email, password } = req.body;

  if (!email || !password) {
    throw new ValidationError('Email and password are required');
  }

  try {
    // Authenticate with user service
    const userServiceUrl = process.env.USER_SERVICE_URL;
    if (!userServiceUrl) {
      throw new AppError('User service not configured', 500);
    }

    const authResponse = await axios.post(`${userServiceUrl}/api/auth/login`, {
      email,
      password
    }, {
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const { user, token: userServiceToken } = authResponse.data.data;

    // Check if user has backoffice access
    const hasBackofficeAccess = user.roles?.some((role: any) => 
      ['PLATFORM_ADMIN', 'BACKOFFICE_MODERATOR'].includes(role.name)
    ) || user.permissions?.some((permission: any) => 
      permission.name === 'BACKOFFICE_ACCESS'
    );

    if (!hasBackofficeAccess) {
      throw new AuthenticationError('Access denied: Insufficient permissions for backoffice');
    }

    // Generate backoffice-specific token
    const backofficeToken = generateToken({
      userId: user.id,
      email: user.email,
      roles: user.roles?.map((role: any) => role.name) || [],
      permissions: user.permissions?.map((permission: any) => permission.name) || []
    });

    // Store session in Redis
    const sessionKey = CacheKeys.userSession(user.id);
    await redisClient.setex(sessionKey, 24 * 60 * 60, JSON.stringify({
      userId: user.id,
      email: user.email,
      roles: user.roles?.map((role: any) => role.name) || [],
      permissions: user.permissions?.map((permission: any) => permission.name) || [],
      loginAt: new Date().toISOString(),
      userAgent: req.get('User-Agent'),
      ip: req.ip
    }));

    logHelpers.logAuth('User logged in to backoffice', {
      userId: user.id,
      email: user.email,
      roles: user.roles?.map((role: any) => role.name) || [],
      ip: req.ip,
      userAgent: req.get('User-Agent')
    });

    res.json({
      success: true,
      data: {
        user: {
          id: user.id,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          roles: user.roles?.map((role: any) => role.name) || [],
          permissions: user.permissions?.map((permission: any) => permission.name) || []
        },
        token: backofficeToken,
        expiresIn: '24h'
      },
      message: 'Login successful'
    });
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401) {
        throw new AuthenticationError('Invalid email or password');
      } else if (error.response?.status === 404) {
        throw new NotFoundError('User not found');
      } else {
        throw new AppError('Authentication service unavailable', 503);
      }
    }
    
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Logout user
 */
export const logout = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Remove session from Redis
    const sessionKey = CacheKeys.userSession(userId);
    await redisClient.del(sessionKey);

    // Add token to blacklist
    const token = req.get('Authorization')?.replace('Bearer ', '');
    if (token) {
      const blacklistKey = CacheKeys.tokenBlacklist(token);
      await redisClient.setex(blacklistKey, 24 * 60 * 60, 'blacklisted');
    }

    logHelpers.logAuth('User logged out from backoffice', {
      userId,
      ip: req.ip
    });

    res.json({
      success: true,
      message: 'Logout successful'
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get current user profile
 */
export const getProfile = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Get user details from user service
    const userServiceUrl = process.env.USER_SERVICE_URL;
    if (!userServiceUrl) {
      throw new AppError('User service not configured', 500);
    }

    const userResponse = await axios.get(`${userServiceUrl}/api/users/${userId}`, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.INTERNAL_API_TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    const user = userResponse.data.data;

    res.json({
      success: true,
      data: {
        id: user.id,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        roles: user.roles?.map((role: any) => role.name) || [],
        permissions: user.permissions?.map((permission: any) => permission.name) || [],
        createdAt: user.createdAt,
        lastLoginAt: user.lastLoginAt
      }
    });
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 404) {
        throw new NotFoundError('User not found');
      } else {
        throw new AppError('User service unavailable', 503);
      }
    }
    
    throw handleDatabaseError(error);
  }
});

/**
 * Refresh token
 */
export const refreshToken = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Get current session from Redis
    const sessionKey = CacheKeys.userSession(userId);
    const sessionData = await redisClient.get(sessionKey);

    if (!sessionData) {
      throw new AuthenticationError('Session expired');
    }

    const session = JSON.parse(sessionData);

    // Generate new token
    const newToken = generateToken({
      userId: session.userId,
      email: session.email,
      roles: session.roles,
      permissions: session.permissions
    });

    // Update session expiry
    await redisClient.setex(sessionKey, 24 * 60 * 60, JSON.stringify({
      ...session,
      refreshedAt: new Date().toISOString()
    }));

    // Blacklist old token
    const oldToken = req.get('Authorization')?.replace('Bearer ', '');
    if (oldToken) {
      const blacklistKey = CacheKeys.tokenBlacklist(oldToken);
      await redisClient.setex(blacklistKey, 24 * 60 * 60, 'blacklisted');
    }

    logHelpers.logAuth('Token refreshed', {
      userId,
      ip: req.ip
    });

    res.json({
      success: true,
      data: {
        token: newToken,
        expiresIn: '24h'
      },
      message: 'Token refreshed successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get user permissions
 */
export const getPermissions = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Get user permissions from user service
    const userServiceUrl = process.env.USER_SERVICE_URL;
    if (!userServiceUrl) {
      throw new AppError('User service not configured', 500);
    }

    const permissionsResponse = await axios.get(`${userServiceUrl}/api/users/${userId}/permissions`, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.INTERNAL_API_TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    const permissions = permissionsResponse.data.data;

    // Filter backoffice-related permissions
    const backofficePermissions = permissions.filter((permission: any) => 
      permission.name.startsWith('BACKOFFICE_') || 
      ['PLATFORM_ADMIN', 'CONTENT_MODERATE', 'USER_MANAGE'].includes(permission.name)
    );

    res.json({
      success: true,
      data: {
        permissions: backofficePermissions.map((permission: any) => ({
          name: permission.name,
          description: permission.description,
          category: permission.category
        }))
      }
    });
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 404) {
        throw new NotFoundError('User not found');
      } else {
        throw new AppError('User service unavailable', 503);
      }
    }
    
    throw handleDatabaseError(error);
  }
});

/**
 * Validate session
 */
export const validateSession = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Check if session exists in Redis
    const sessionKey = CacheKeys.userSession(userId);
    const sessionData = await redisClient.get(sessionKey);

    if (!sessionData) {
      throw new AuthenticationError('Session not found');
    }

    const session = JSON.parse(sessionData);

    res.json({
      success: true,
      data: {
        valid: true,
        session: {
          userId: session.userId,
          email: session.email,
          roles: session.roles,
          permissions: session.permissions,
          loginAt: session.loginAt,
          refreshedAt: session.refreshedAt
        }
      }
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get active sessions
 */
export const getActiveSessions = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.id;

  try {
    // Get session from Redis
    const sessionKey = CacheKeys.userSession(userId);
    const sessionData = await redisClient.get(sessionKey);

    const sessions = [];
    if (sessionData) {
      const session = JSON.parse(sessionData);
      sessions.push({
        id: sessionKey,
        userId: session.userId,
        loginAt: session.loginAt,
        refreshedAt: session.refreshedAt,
        userAgent: session.userAgent,
        ip: session.ip,
        current: true
      });
    }

    res.json({
      success: true,
      data: {
        sessions,
        total: sessions.length
      }
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Revoke session
 */
export const revokeSession = asyncHandler(async (req: Request, res: Response) => {
  const { sessionId } = req.params;
  const userId = req.user!.id;

  try {
    // For security, only allow revoking own sessions
    const sessionKey = CacheKeys.userSession(userId);
    
    if (sessionId !== sessionKey) {
      throw new ValidationError('Cannot revoke session of another user');
    }

    // Remove session from Redis
    await redisClient.del(sessionKey);

    logHelpers.logAuth('Session revoked', {
      userId,
      sessionId,
      ip: req.ip
    });

    res.json({
      success: true,
      message: 'Session revoked successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Change password
 */
export const changePassword = asyncHandler(async (req: Request, res: Response) => {
  const { currentPassword, newPassword } = req.body;
  const userId = req.user!.id;

  if (!currentPassword || !newPassword) {
    throw new ValidationError('Current password and new password are required');
  }

  if (newPassword.length < 8) {
    throw new ValidationError('New password must be at least 8 characters long');
  }

  try {
    // Change password via user service
    const userServiceUrl = process.env.USER_SERVICE_URL;
    if (!userServiceUrl) {
      throw new AppError('User service not configured', 500);
    }

    await axios.put(`${userServiceUrl}/api/users/${userId}/password`, {
      currentPassword,
      newPassword
    }, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.INTERNAL_API_TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    // Revoke current session to force re-login
    const sessionKey = CacheKeys.userSession(userId);
    await redisClient.del(sessionKey);

    logHelpers.logAuth('Password changed', {
      userId,
      ip: req.ip
    });

    res.json({
      success: true,
      message: 'Password changed successfully. Please login again.'
    });
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 400) {
        throw new ValidationError('Invalid current password');
      } else if (error.response?.status === 404) {
        throw new NotFoundError('User not found');
      } else {
        throw new AppError('User service unavailable', 503);
      }
    }
    
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get authentication logs
 */
export const getAuthLogs = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    startDate,
    endDate,
    action
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);
  const userId = req.user!.id;

  try {
    // This would typically come from a logging service or database
    // For now, we'll return a mock response
    const logs = [
      {
        id: '1',
        userId,
        action: 'LOGIN',
        timestamp: new Date().toISOString(),
        ip: req.ip,
        userAgent: req.get('User-Agent'),
        success: true
      }
    ];

    res.json({
      success: true,
      data: logs,
      pagination: {
        page: pageNum,
        limit: limitNum,
        total: logs.length,
        totalPages: Math.ceil(logs.length / limitNum)
      }
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});