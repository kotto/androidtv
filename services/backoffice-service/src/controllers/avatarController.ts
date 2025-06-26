import { Request, Response } from 'express';
import { prisma } from '../utils/database';
import { redisClient, CacheKeys } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import {
  AppError,
  NotFoundError,
  ConflictError,
  ValidationError,
  asyncHandler,
  throwIfNotFound,
  handleDatabaseError
} from '../utils/errors';

/**
 * Get all avatars with pagination and filtering
 */
export const getAvatars = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    type,
    status = 'ACTIVE'
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    deletedAt: null
  };

  if (status !== 'ALL') {
    where.isActive = status === 'ACTIVE';
  }

  if (type) {
    where.type = type;
  }

  if (search) {
    where.OR = [
      { name: { contains: search, mode: 'insensitive' } },
      { personality: { contains: search, mode: 'insensitive' } },
      { expertise: { hasSome: [search] } }
    ];
  }

  try {
    const result = await prisma.findManyWithPagination('aiAvatar', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { createdAt: 'desc' },
      include: {
        sessions: {
          where: { isActive: true },
          select: { id: true, status: true }
        },
        _count: {
          select: {
            sessions: true,
            contentItems: true
          }
        }
      }
    });

    logHelpers.logBusiness('Avatars retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, type, status }
    }, req.user?.id);

    res.json({
      success: true,
      data: result.data,
      pagination: result.pagination
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get avatar by ID
 */
export const getAvatarById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  // Try to get from cache first
  const cached = await redisClient.getJSON(CacheKeys.avatar(id));
  if (cached) {
    return res.json({
      success: true,
      data: cached,
      cached: true
    });
  }

  try {
    const avatar = await prisma.aiAvatar.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        sessions: {
          where: { isActive: true },
          orderBy: { createdAt: 'desc' },
          take: 10
        },
        contentItems: {
          where: { isActive: true },
          orderBy: { createdAt: 'desc' },
          take: 5,
          select: {
            id: true,
            title: true,
            type: true,
            createdAt: true
          }
        },
        _count: {
          select: {
            sessions: true,
            contentItems: true
          }
        }
      }
    });

    throwIfNotFound(avatar, 'Avatar');

    // Cache the result for 5 minutes
    await redisClient.setJSON(CacheKeys.avatar(id), avatar, 300);

    logHelpers.logBusiness('Avatar retrieved', { avatarId: id }, req.user?.id);

    res.json({
      success: true,
      data: avatar
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Create new avatar
 */
export const createAvatar = asyncHandler(async (req: Request, res: Response) => {
  const {
    name,
    type,
    personality,
    expertise,
    voiceSettings,
    appearance,
    isActive = true
  } = req.body;

  try {
    // Check if avatar with same name exists
    const existingAvatar = await prisma.aiAvatar.findFirst({
      where: {
        name,
        deletedAt: null
      }
    });

    if (existingAvatar) {
      throw new ConflictError(`Avatar with name '${name}' already exists`);
    }

    const avatar = await prisma.aiAvatar.create({
      data: {
        name,
        type,
        personality,
        expertise,
        voiceSettings,
        appearance,
        isActive,
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            sessions: true,
            contentItems: true
          }
        }
      }
    });

    // Cache the new avatar
    await redisClient.setJSON(CacheKeys.avatar(avatar.id), avatar, 300);

    logHelpers.logBusiness('Avatar created', {
      avatarId: avatar.id,
      name: avatar.name,
      type: avatar.type
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: avatar,
      message: 'Avatar created successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Update avatar
 */
export const updateAvatar = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = { ...req.body };
  delete updateData.id; // Remove id from update data
  updateData.updatedBy = req.user!.id;

  try {
    // Check if avatar exists
    const existingAvatar = await prisma.aiAvatar.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingAvatar, 'Avatar');

    // Check if name is being changed and if it conflicts
    if (updateData.name && updateData.name !== existingAvatar!.name) {
      const nameConflict = await prisma.aiAvatar.findFirst({
        where: {
          name: updateData.name,
          id: { not: id },
          deletedAt: null
        }
      });

      if (nameConflict) {
        throw new ConflictError(`Avatar with name '${updateData.name}' already exists`);
      }
    }

    const avatar = await prisma.aiAvatar.update({
      where: { id },
      data: updateData,
      include: {
        sessions: {
          where: { isActive: true },
          orderBy: { createdAt: 'desc' },
          take: 10
        },
        _count: {
          select: {
            sessions: true,
            contentItems: true
          }
        }
      }
    });

    // Update cache
    await redisClient.setJSON(CacheKeys.avatar(id), avatar, 300);

    logHelpers.logBusiness('Avatar updated', {
      avatarId: id,
      changes: Object.keys(updateData)
    }, req.user?.id);

    res.json({
      success: true,
      data: avatar,
      message: 'Avatar updated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Delete avatar (soft delete)
 */
export const deleteAvatar = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if avatar exists
    const existingAvatar = await prisma.aiAvatar.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        sessions: {
          where: { isActive: true }
        }
      }
    });

    throwIfNotFound(existingAvatar, 'Avatar');

    // Check if avatar has active sessions
    if (existingAvatar!.sessions.length > 0) {
      throw new ConflictError('Cannot delete avatar with active sessions');
    }

    await prisma.softDelete('aiAvatar', { id });

    // Remove from cache
    await redisClient.del(CacheKeys.avatar(id));

    logHelpers.logBusiness('Avatar deleted', {
      avatarId: id,
      name: existingAvatar!.name
    }, req.user?.id);

    res.json({
      success: true,
      message: 'Avatar deleted successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Restore deleted avatar
 */
export const restoreAvatar = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if avatar exists and is deleted
    const deletedAvatar = await prisma.aiAvatar.findFirst({
      where: {
        id,
        deletedAt: { not: null }
      }
    });

    throwIfNotFound(deletedAvatar, 'Deleted avatar');

    const avatar = await prisma.restore('aiAvatar', { id });

    // Cache the restored avatar
    await redisClient.setJSON(CacheKeys.avatar(id), avatar, 300);

    logHelpers.logBusiness('Avatar restored', {
      avatarId: id,
      name: avatar.name
    }, req.user?.id);

    res.json({
      success: true,
      data: avatar,
      message: 'Avatar restored successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get avatar sessions
 */
export const getAvatarSessions = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const {
    page = 1,
    limit = 10,
    status
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    avatarId: id,
    deletedAt: null
  };

  if (status) {
    where.status = status;
  }

  try {
    // Check if avatar exists
    const avatar = await prisma.aiAvatar.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(avatar, 'Avatar');

    const result = await prisma.findManyWithPagination('avatarSession', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { createdAt: 'desc' },
      include: {
        contentItem: {
          select: {
            id: true,
            title: true,
            type: true
          }
        }
      }
    });

    res.json({
      success: true,
      data: result.data,
      pagination: result.pagination
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get avatar analytics
 */
export const getAvatarAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { period = '30d' } = req.query;

  try {
    // Check if avatar exists
    const avatar = await prisma.aiAvatar.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(avatar, 'Avatar');

    // Calculate date range
    const now = new Date();
    let startDate: Date;
    
    switch (period) {
      case '7d':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case '30d':
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
        break;
      case '90d':
        startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
        break;
      default:
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    }

    // Get analytics data
    const [totalSessions, activeSessions, contentCount, avgSessionDuration] = await Promise.all([
      // Total sessions
      prisma.avatarSession.count({
        where: {
          avatarId: id,
          createdAt: { gte: startDate }
        }
      }),
      
      // Active sessions
      prisma.avatarSession.count({
        where: {
          avatarId: id,
          status: 'ACTIVE',
          createdAt: { gte: startDate }
        }
      }),
      
      // Content items
      prisma.unifiedContent.count({
        where: {
          avatarId: id,
          createdAt: { gte: startDate }
        }
      }),
      
      // Average session duration
      prisma.avatarSession.aggregate({
        where: {
          avatarId: id,
          status: 'COMPLETED',
          createdAt: { gte: startDate }
        },
        _avg: {
          duration: true
        }
      })
    ]);

    // Get daily session counts for chart
    const dailySessions = await prisma.$queryRaw`
      SELECT 
        DATE(created_at) as date,
        COUNT(*) as sessions
      FROM avatar_sessions 
      WHERE avatar_id = ${id} 
        AND created_at >= ${startDate}
        AND deleted_at IS NULL
      GROUP BY DATE(created_at)
      ORDER BY date ASC
    `;

    const analytics = {
      summary: {
        totalSessions,
        activeSessions,
        contentCount,
        avgSessionDuration: avgSessionDuration._avg.duration || 0
      },
      chart: {
        dailySessions
      },
      period,
      avatar: {
        id: avatar.id,
        name: avatar.name,
        type: avatar.type
      }
    };

    res.json({
      success: true,
      data: analytics
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Duplicate avatar
 */
export const duplicateAvatar = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { name } = req.body;

  if (!name) {
    throw new ValidationError('Name is required for duplicated avatar');
  }

  try {
    // Get original avatar
    const originalAvatar = await prisma.aiAvatar.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(originalAvatar, 'Avatar');

    // Check if new name conflicts
    const nameConflict = await prisma.aiAvatar.findFirst({
      where: {
        name,
        deletedAt: null
      }
    });

    if (nameConflict) {
      throw new ConflictError(`Avatar with name '${name}' already exists`);
    }

    // Create duplicate
    const duplicatedAvatar = await prisma.aiAvatar.create({
      data: {
        name,
        type: originalAvatar!.type,
        personality: originalAvatar!.personality,
        expertise: originalAvatar!.expertise,
        voiceSettings: originalAvatar!.voiceSettings,
        appearance: originalAvatar!.appearance,
        isActive: false, // Start as inactive
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            sessions: true,
            contentItems: true
          }
        }
      }
    });

    logHelpers.logBusiness('Avatar duplicated', {
      originalId: id,
      duplicatedId: duplicatedAvatar.id,
      newName: name
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: duplicatedAvatar,
      message: 'Avatar duplicated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});