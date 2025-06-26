import { Request, Response } from 'express';
import { prisma } from '../utils/database';
import { redisClient, CacheKeys } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import {
  AppError,
  NotFoundError,
  ConflictError,
  ValidationError,
  ExternalServiceError,
  asyncHandler,
  throwIfNotFound,
  handleDatabaseError,
  handleExternalServiceError
} from '../utils/errors';
import axios from 'axios';

/**
 * Get all virtual studios with pagination and filtering
 */
export const getStudios = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    environment,
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

  if (environment) {
    where.environment = environment;
  }

  if (search) {
    where.OR = [
      { name: { contains: search, mode: 'insensitive' } },
      { description: { contains: search, mode: 'insensitive' } }
    ];
  }

  try {
    const result = await prisma.findManyWithPagination('virtualStudio', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { createdAt: 'desc' },
      include: {
        contentItems: {
          where: { isActive: true },
          select: { id: true, title: true, type: true }
        },
        _count: {
          select: {
            contentItems: true
          }
        }
      }
    });

    logHelpers.logBusiness('Studios retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, environment, status }
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
 * Get studio by ID
 */
export const getStudioById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  // Try to get from cache first
  const cached = await redisClient.getJSON(CacheKeys.studio(id));
  if (cached) {
    return res.json({
      success: true,
      data: cached,
      cached: true
    });
  }

  try {
    const studio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        contentItems: {
          where: { isActive: true },
          orderBy: { createdAt: 'desc' },
          take: 10,
          select: {
            id: true,
            title: true,
            type: true,
            createdAt: true,
            metadata: true
          }
        },
        _count: {
          select: {
            contentItems: true
          }
        }
      }
    });

    throwIfNotFound(studio, 'Virtual Studio');

    // Cache the result for 5 minutes
    await redisClient.setJSON(CacheKeys.studio(id), studio, 300);

    logHelpers.logBusiness('Studio retrieved', { studioId: id }, req.user?.id);

    res.json({
      success: true,
      data: studio
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Create new virtual studio
 */
export const createStudio = asyncHandler(async (req: Request, res: Response) => {
  const {
    name,
    description,
    environment,
    unrealEngineConfig,
    lighting,
    isActive = true
  } = req.body;

  try {
    // Check if studio with same name exists
    const existingStudio = await prisma.virtualStudio.findFirst({
      where: {
        name,
        deletedAt: null
      }
    });

    if (existingStudio) {
      throw new ConflictError(`Virtual studio with name '${name}' already exists`);
    }

    // Validate Unreal Engine project path if provided
    if (unrealEngineConfig?.projectPath) {
      await validateUnrealEngineProject(unrealEngineConfig.projectPath);
    }

    const studio = await prisma.virtualStudio.create({
      data: {
        name,
        description,
        environment,
        unrealEngineConfig,
        lighting,
        isActive,
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            contentItems: true
          }
        }
      }
    });

    // Cache the new studio
    await redisClient.setJSON(CacheKeys.studio(studio.id), studio, 300);

    logHelpers.logBusiness('Studio created', {
      studioId: studio.id,
      name: studio.name,
      environment: studio.environment
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: studio,
      message: 'Virtual studio created successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Update virtual studio
 */
export const updateStudio = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = { ...req.body };
  delete updateData.id;
  updateData.updatedBy = req.user!.id;

  try {
    // Check if studio exists
    const existingStudio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingStudio, 'Virtual Studio');

    // Check if name is being changed and if it conflicts
    if (updateData.name && updateData.name !== existingStudio!.name) {
      const nameConflict = await prisma.virtualStudio.findFirst({
        where: {
          name: updateData.name,
          id: { not: id },
          deletedAt: null
        }
      });

      if (nameConflict) {
        throw new ConflictError(`Virtual studio with name '${updateData.name}' already exists`);
      }
    }

    // Validate Unreal Engine project path if being updated
    if (updateData.unrealEngineConfig?.projectPath) {
      await validateUnrealEngineProject(updateData.unrealEngineConfig.projectPath);
    }

    const studio = await prisma.virtualStudio.update({
      where: { id },
      data: updateData,
      include: {
        contentItems: {
          where: { isActive: true },
          orderBy: { createdAt: 'desc' },
          take: 5
        },
        _count: {
          select: {
            contentItems: true
          }
        }
      }
    });

    // Update cache
    await redisClient.setJSON(CacheKeys.studio(id), studio, 300);

    logHelpers.logBusiness('Studio updated', {
      studioId: id,
      changes: Object.keys(updateData)
    }, req.user?.id);

    res.json({
      success: true,
      data: studio,
      message: 'Virtual studio updated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Delete virtual studio (soft delete)
 */
export const deleteStudio = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if studio exists
    const existingStudio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        contentItems: {
          where: { isActive: true }
        }
      }
    });

    throwIfNotFound(existingStudio, 'Virtual Studio');

    // Check if studio has active content
    if (existingStudio!.contentItems.length > 0) {
      throw new ConflictError('Cannot delete studio with active content items');
    }

    await prisma.softDelete('virtualStudio', { id });

    // Remove from cache
    await redisClient.del(CacheKeys.studio(id));

    logHelpers.logBusiness('Studio deleted', {
      studioId: id,
      name: existingStudio!.name
    }, req.user?.id);

    res.json({
      success: true,
      message: 'Virtual studio deleted successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Restore deleted virtual studio
 */
export const restoreStudio = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if studio exists and is deleted
    const deletedStudio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: { not: null }
      }
    });

    throwIfNotFound(deletedStudio, 'Deleted virtual studio');

    const studio = await prisma.restore('virtualStudio', { id });

    // Cache the restored studio
    await redisClient.setJSON(CacheKeys.studio(id), studio, 300);

    logHelpers.logBusiness('Studio restored', {
      studioId: id,
      name: studio.name
    }, req.user?.id);

    res.json({
      success: true,
      data: studio,
      message: 'Virtual studio restored successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Test studio rendering
 */
export const testStudioRendering = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { testDuration = 30 } = req.body; // Test duration in seconds

  try {
    // Check if studio exists
    const studio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(studio, 'Virtual Studio');

    if (!studio!.isActive) {
      throw new ValidationError('Cannot test inactive studio');
    }

    // Start test rendering via Unreal Engine API
    const renderingResult = await startTestRendering(studio!, testDuration);

    logHelpers.logBusiness('Studio rendering test started', {
      studioId: id,
      testDuration,
      renderJobId: renderingResult.jobId
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        studio: {
          id: studio!.id,
          name: studio!.name,
          environment: studio!.environment
        },
        test: {
          jobId: renderingResult.jobId,
          status: renderingResult.status,
          estimatedDuration: testDuration,
          startedAt: new Date().toISOString()
        }
      },
      message: 'Studio rendering test started successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get studio rendering status
 */
export const getStudioRenderingStatus = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { jobId } = req.query;

  if (!jobId) {
    throw new ValidationError('Job ID is required');
  }

  try {
    // Check if studio exists
    const studio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(studio, 'Virtual Studio');

    // Get rendering status from Unreal Engine API
    const renderingStatus = await getRenderingStatus(jobId as string);

    res.json({
      success: true,
      data: {
        studio: {
          id: studio!.id,
          name: studio!.name
        },
        rendering: renderingStatus
      }
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get studio analytics
 */
export const getStudioAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { period = '30d' } = req.query;

  try {
    // Check if studio exists
    const studio = await prisma.virtualStudio.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(studio, 'Virtual Studio');

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
    const [totalContent, activeContent, totalRenderTime] = await Promise.all([
      // Total content items
      prisma.unifiedContent.count({
        where: {
          studioId: id,
          createdAt: { gte: startDate }
        }
      }),
      
      // Active content items
      prisma.unifiedContent.count({
        where: {
          studioId: id,
          isActive: true,
          createdAt: { gte: startDate }
        }
      }),
      
      // Total render time (from metadata)
      prisma.unifiedContent.aggregate({
        where: {
          studioId: id,
          createdAt: { gte: startDate }
        },
        _sum: {
          // Assuming render time is stored in metadata
        }
      })
    ]);

    // Get daily content creation for chart
    const dailyContent = await prisma.$queryRaw`
      SELECT 
        DATE(created_at) as date,
        COUNT(*) as content_count
      FROM unified_content 
      WHERE studio_id = ${id} 
        AND created_at >= ${startDate}
        AND deleted_at IS NULL
      GROUP BY DATE(created_at)
      ORDER BY date ASC
    `;

    const analytics = {
      summary: {
        totalContent,
        activeContent,
        totalRenderTime: totalRenderTime._sum || 0,
        utilizationRate: totalContent > 0 ? (activeContent / totalContent * 100).toFixed(2) : 0
      },
      chart: {
        dailyContent
      },
      period,
      studio: {
        id: studio.id,
        name: studio.name,
        environment: studio.environment
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
 * Duplicate virtual studio
 */
export const duplicateStudio = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { name } = req.body;

  if (!name) {
    throw new ValidationError('Name is required for duplicated studio');
  }

  try {
    // Get original studio
    const originalStudio = await prisma.virtualStudio.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(originalStudio, 'Virtual Studio');

    // Check if new name conflicts
    const nameConflict = await prisma.virtualStudio.findFirst({
      where: {
        name,
        deletedAt: null
      }
    });

    if (nameConflict) {
      throw new ConflictError(`Virtual studio with name '${name}' already exists`);
    }

    // Create duplicate
    const duplicatedStudio = await prisma.virtualStudio.create({
      data: {
        name,
        description: originalStudio!.description,
        environment: originalStudio!.environment,
        unrealEngineConfig: originalStudio!.unrealEngineConfig,
        lighting: originalStudio!.lighting,
        isActive: false, // Start as inactive
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            contentItems: true
          }
        }
      }
    });

    logHelpers.logBusiness('Studio duplicated', {
      originalId: id,
      duplicatedId: duplicatedStudio.id,
      newName: name
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: duplicatedStudio,
      message: 'Virtual studio duplicated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Helper function to validate Unreal Engine project
 */
async function validateUnrealEngineProject(projectPath: string): Promise<void> {
  try {
    const unrealEngineApiUrl = process.env.UNREAL_ENGINE_API_URL;
    if (!unrealEngineApiUrl) {
      logger.warn('Unreal Engine API URL not configured, skipping validation');
      return;
    }

    const response = await axios.post(`${unrealEngineApiUrl}/validate-project`, {
      projectPath
    }, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.UNREAL_ENGINE_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.data.valid) {
      throw new ValidationError(`Invalid Unreal Engine project: ${response.data.message}`);
    }
  } catch (error) {
    if (error instanceof ValidationError) throw error;
    throw handleExternalServiceError('Unreal Engine', error);
  }
}

/**
 * Helper function to start test rendering
 */
async function startTestRendering(studio: any, duration: number): Promise<any> {
  try {
    const unrealEngineApiUrl = process.env.UNREAL_ENGINE_API_URL;
    if (!unrealEngineApiUrl) {
      throw new ExternalServiceError('Unreal Engine', 'API URL not configured');
    }

    const response = await axios.post(`${unrealEngineApiUrl}/render/test`, {
      projectPath: studio.unrealEngineConfig.projectPath,
      levelName: studio.unrealEngineConfig.levelName,
      renderSettings: studio.unrealEngineConfig.renderSettings,
      lighting: studio.lighting,
      duration
    }, {
      timeout: 30000,
      headers: {
        'Authorization': `Bearer ${process.env.UNREAL_ENGINE_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('Unreal Engine', error);
  }
}

/**
 * Helper function to get rendering status
 */
async function getRenderingStatus(jobId: string): Promise<any> {
  try {
    const unrealEngineApiUrl = process.env.UNREAL_ENGINE_API_URL;
    if (!unrealEngineApiUrl) {
      throw new ExternalServiceError('Unreal Engine', 'API URL not configured');
    }

    const response = await axios.get(`${unrealEngineApiUrl}/render/status/${jobId}`, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.UNREAL_ENGINE_API_KEY}`
      }
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('Unreal Engine', error);
  }
}