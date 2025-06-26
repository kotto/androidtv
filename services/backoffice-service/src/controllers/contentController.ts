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
 * Get all unified content with pagination and filtering
 */
export const getUnifiedContent = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    contentType,
    sourceService,
    status = 'PUBLISHED',
    category,
    language,
    startDate,
    endDate
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    deletedAt: null
  };

  if (status !== 'ALL') {
    where.status = status;
  }

  if (contentType) {
    where.contentType = contentType;
  }

  if (sourceService) {
    where.sourceService = sourceService;
  }

  if (category) {
    where.category = category;
  }

  if (language) {
    where.language = language;
  }

  if (search) {
    where.OR = [
      { title: { contains: search, mode: 'insensitive' } },
      { description: { contains: search, mode: 'insensitive' } },
      { tags: { hasSome: [search] } }
    ];
  }

  if (startDate || endDate) {
    where.publishedAt = {};
    if (startDate) where.publishedAt.gte = new Date(startDate as string);
    if (endDate) where.publishedAt.lte = new Date(endDate as string);
  }

  try {
    const result = await prisma.findManyWithPagination('unifiedContent', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { publishedAt: 'desc' },
      include: {
        _count: {
          select: {
            views: true,
            likes: true,
            comments: true
          }
        }
      }
    });

    logHelpers.logBusiness('Unified content retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, contentType, sourceService, status, category, language }
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
 * Get unified content by ID
 */
export const getUnifiedContentById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Try to get from cache first
    const cacheKey = CacheKeys.unifiedContent(id);
    const cached = await redisClient.get(cacheKey);
    
    if (cached) {
      const content = JSON.parse(cached);
      
      logHelpers.logBusiness('Unified content retrieved from cache', { contentId: id }, req.user?.id);
      
      return res.json({
        success: true,
        data: content,
        cached: true
      });
    }

    const content = await prisma.unifiedContent.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        _count: {
          select: {
            views: true,
            likes: true,
            comments: true
          }
        }
      }
    });

    throwIfNotFound(content, 'Unified Content');

    // Cache the result
    await redisClient.setex(cacheKey, 300, JSON.stringify(content)); // 5 minutes

    logHelpers.logBusiness('Unified content retrieved', { contentId: id }, req.user?.id);

    res.json({
      success: true,
      data: content,
      cached: false
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Create new unified content
 */
export const createUnifiedContent = asyncHandler(async (req: Request, res: Response) => {
  const {
    title,
    description,
    contentType,
    sourceService,
    sourceId,
    category,
    language,
    tags,
    metadata,
    thumbnailUrl,
    contentUrl,
    duration,
    status = 'DRAFT'
  } = req.body;

  try {
    // Check if content with same sourceService and sourceId exists
    if (sourceService && sourceId) {
      const existingContent = await prisma.unifiedContent.findFirst({
        where: {
          sourceService,
          sourceId,
          deletedAt: null
        }
      });

      if (existingContent) {
        throw new ConflictError(`Content from ${sourceService} with ID '${sourceId}' already exists`);
      }
    }

    const content = await prisma.unifiedContent.create({
      data: {
        title,
        description,
        contentType,
        sourceService,
        sourceId,
        category,
        language,
        tags: tags || [],
        metadata: metadata || {},
        thumbnailUrl,
        contentUrl,
        duration,
        status,
        publishedAt: status === 'PUBLISHED' ? new Date() : null,
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            views: true,
            likes: true,
            comments: true
          }
        }
      }
    });

    // Sync to external services if published
    if (status === 'PUBLISHED') {
      await syncContentToServices(content);
    }

    logHelpers.logBusiness('Unified content created', {
      contentId: content.id,
      title: content.title,
      contentType: content.contentType,
      sourceService: content.sourceService,
      status: content.status
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: content,
      message: 'Unified content created successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Update unified content
 */
export const updateUnifiedContent = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = { ...req.body };
  delete updateData.id;
  updateData.updatedBy = req.user!.id;

  try {
    // Check if content exists
    const existingContent = await prisma.unifiedContent.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingContent, 'Unified Content');

    // Handle status change to published
    if (updateData.status === 'PUBLISHED' && existingContent!.status !== 'PUBLISHED') {
      updateData.publishedAt = new Date();
    }

    const content = await prisma.unifiedContent.update({
      where: { id },
      data: updateData,
      include: {
        _count: {
          select: {
            views: true,
            likes: true,
            comments: true
          }
        }
      }
    });

    // Clear cache
    const cacheKey = CacheKeys.unifiedContent(id);
    await redisClient.del(cacheKey);

    // Sync to external services if published
    if (content.status === 'PUBLISHED') {
      await syncContentToServices(content);
    }

    logHelpers.logBusiness('Unified content updated', {
      contentId: id,
      changes: Object.keys(updateData),
      newStatus: content.status
    }, req.user?.id);

    res.json({
      success: true,
      data: content,
      message: 'Unified content updated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Delete unified content (soft delete)
 */
export const deleteUnifiedContent = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if content exists
    const existingContent = await prisma.unifiedContent.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingContent, 'Unified Content');

    await prisma.softDelete('unifiedContent', { id });

    // Clear cache
    const cacheKey = CacheKeys.unifiedContent(id);
    await redisClient.del(cacheKey);

    // Remove from external services
    await removeContentFromServices(existingContent!);

    logHelpers.logBusiness('Unified content deleted', {
      contentId: id,
      title: existingContent!.title,
      contentType: existingContent!.contentType
    }, req.user?.id);

    res.json({
      success: true,
      message: 'Unified content deleted successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Restore unified content
 */
export const restoreUnifiedContent = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    const content = await prisma.softRestore('unifiedContent', { id });

    if (!content) {
      throw new NotFoundError('Unified Content not found or not deleted');
    }

    // Sync to external services if published
    if (content.status === 'PUBLISHED') {
      await syncContentToServices(content);
    }

    logHelpers.logBusiness('Unified content restored', {
      contentId: id,
      title: content.title
    }, req.user?.id);

    res.json({
      success: true,
      data: content,
      message: 'Unified content restored successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Sync content from external service
 */
export const syncContentFromService = asyncHandler(async (req: Request, res: Response) => {
  const { sourceService, sourceId } = req.params;

  try {
    // Fetch content from source service
    const sourceContent = await fetchContentFromService(sourceService, sourceId);

    if (!sourceContent) {
      throw new NotFoundError(`Content not found in ${sourceService}`);
    }

    // Check if content already exists
    let content = await prisma.unifiedContent.findFirst({
      where: {
        sourceService,
        sourceId,
        deletedAt: null
      }
    });

    if (content) {
      // Update existing content
      content = await prisma.unifiedContent.update({
        where: { id: content.id },
        data: {
          title: sourceContent.title,
          description: sourceContent.description,
          category: sourceContent.category,
          language: sourceContent.language,
          tags: sourceContent.tags,
          metadata: sourceContent.metadata,
          thumbnailUrl: sourceContent.thumbnailUrl,
          contentUrl: sourceContent.contentUrl,
          duration: sourceContent.duration,
          updatedBy: req.user!.id
        }
      });
    } else {
      // Create new content
      content = await prisma.unifiedContent.create({
        data: {
          ...sourceContent,
          sourceService,
          sourceId,
          status: 'PUBLISHED',
          publishedAt: new Date(),
          createdBy: req.user!.id,
          updatedBy: req.user!.id
        }
      });
    }

    logHelpers.logBusiness('Content synced from service', {
      contentId: content.id,
      sourceService,
      sourceId,
      action: content.createdAt === content.updatedAt ? 'created' : 'updated'
    }, req.user?.id);

    res.json({
      success: true,
      data: content,
      message: `Content synced from ${sourceService} successfully`
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Bulk sync content from service
 */
export const bulkSyncFromService = asyncHandler(async (req: Request, res: Response) => {
  const { sourceService } = req.params;
  const { limit = 50, offset = 0 } = req.query;

  try {
    // Fetch content list from source service
    const sourceContentList = await fetchContentListFromService(
      sourceService,
      parseInt(limit as string),
      parseInt(offset as string)
    );

    let syncedCount = 0;
    let updatedCount = 0;
    let createdCount = 0;
    const errors: any[] = [];

    for (const sourceContent of sourceContentList) {
      try {
        // Check if content already exists
        let content = await prisma.unifiedContent.findFirst({
          where: {
            sourceService,
            sourceId: sourceContent.sourceId,
            deletedAt: null
          }
        });

        if (content) {
          // Update existing content
          await prisma.unifiedContent.update({
            where: { id: content.id },
            data: {
              title: sourceContent.title,
              description: sourceContent.description,
              category: sourceContent.category,
              language: sourceContent.language,
              tags: sourceContent.tags,
              metadata: sourceContent.metadata,
              thumbnailUrl: sourceContent.thumbnailUrl,
              contentUrl: sourceContent.contentUrl,
              duration: sourceContent.duration,
              updatedBy: req.user!.id
            }
          });
          updatedCount++;
        } else {
          // Create new content
          await prisma.unifiedContent.create({
            data: {
              ...sourceContent,
              sourceService,
              status: 'PUBLISHED',
              publishedAt: new Date(),
              createdBy: req.user!.id,
              updatedBy: req.user!.id
            }
          });
          createdCount++;
        }
        syncedCount++;
      } catch (error) {
        errors.push({
          sourceId: sourceContent.sourceId,
          error: error instanceof Error ? error.message : 'Unknown error'
        });
      }
    }

    logHelpers.logBusiness('Bulk content sync completed', {
      sourceService,
      syncedCount,
      createdCount,
      updatedCount,
      errorCount: errors.length
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        sourceService,
        syncedCount,
        createdCount,
        updatedCount,
        errorCount: errors.length,
        errors: errors.slice(0, 10) // Limit error details
      },
      message: `Bulk sync from ${sourceService} completed`
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get content analytics
 */
export const getContentAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const {
    period = '30d',
    contentType,
    sourceService,
    category
  } = req.query;

  try {
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

    // Build where clause for filtering
    const where: any = {
      deletedAt: null,
      publishedAt: { gte: startDate }
    };

    if (contentType) where.contentType = contentType;
    if (sourceService) where.sourceService = sourceService;
    if (category) where.category = category;

    // Get analytics data
    const [totalContent, contentByType, contentByService, contentByStatus] = await Promise.all([
      // Total content
      prisma.unifiedContent.count({ where }),
      
      // Content by type
      prisma.unifiedContent.groupBy({
        by: ['contentType'],
        where,
        _count: true
      }),
      
      // Content by service
      prisma.unifiedContent.groupBy({
        by: ['sourceService'],
        where,
        _count: true
      }),
      
      // Content by status
      prisma.unifiedContent.groupBy({
        by: ['status'],
        where,
        _count: true
      })
    ]);

    // Get daily content creation for chart
    const dailyContent = await prisma.$queryRaw`
      SELECT 
        DATE(published_at) as date,
        COUNT(*) as content_count
      FROM unified_content 
      WHERE published_at >= ${startDate}
        AND deleted_at IS NULL
        ${contentType ? `AND content_type = ${contentType}` : ''}
        ${sourceService ? `AND source_service = ${sourceService}` : ''}
        ${category ? `AND category = ${category}` : ''}
      GROUP BY DATE(published_at)
      ORDER BY date ASC
    `;

    const analytics = {
      summary: {
        totalContent,
        period
      },
      distribution: {
        byType: contentByType.reduce((acc: any, item: any) => {
          acc[item.contentType] = item._count;
          return acc;
        }, {}),
        byService: contentByService.reduce((acc: any, item: any) => {
          acc[item.sourceService] = item._count;
          return acc;
        }, {}),
        byStatus: contentByStatus.reduce((acc: any, item: any) => {
          acc[item.status] = item._count;
          return acc;
        }, {})
      },
      chart: {
        dailyContent
      },
      filters: {
        contentType,
        sourceService,
        category
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
 * Helper function to sync content to external services
 */
async function syncContentToServices(content: any): Promise<void> {
  try {
    // Sync to search service
    await syncToSearchService(content);
    
    // Sync to recommendation service
    await syncToRecommendationService(content);
    
    // Sync to CDN
    await syncToCDN(content);
    
    logger.info(`Content ${content.id} synced to external services`);
  } catch (error) {
    logger.error('Failed to sync content to external services', {
      contentId: content.id,
      error: error instanceof Error ? error.message : 'Unknown error'
    });
  }
}

/**
 * Helper function to remove content from external services
 */
async function removeContentFromServices(content: any): Promise<void> {
  try {
    // Remove from search service
    await removeFromSearchService(content.id);
    
    // Remove from recommendation service
    await removeFromRecommendationService(content.id);
    
    // Remove from CDN
    await removeFromCDN(content);
    
    logger.info(`Content ${content.id} removed from external services`);
  } catch (error) {
    logger.error('Failed to remove content from external services', {
      contentId: content.id,
      error: error instanceof Error ? error.message : 'Unknown error'
    });
  }
}

/**
 * Helper function to fetch content from source service
 */
async function fetchContentFromService(sourceService: string, sourceId: string): Promise<any> {
  const serviceUrls: { [key: string]: string } = {
    'maattv': process.env.MAATTV_SERVICE_URL || '',
    'maatclass': process.env.MAATCLASS_SERVICE_URL || '',
    'maattube': process.env.MAATTUBE_SERVICE_URL || '',
    'maatcare': process.env.MAATCARE_SERVICE_URL || ''
  };

  const serviceUrl = serviceUrls[sourceService];
  if (!serviceUrl) {
    throw new ValidationError(`Unknown source service: ${sourceService}`);
  }

  try {
    const response = await axios.get(`${serviceUrl}/api/content/${sourceId}`, {
      timeout: 10000,
      headers: {
        'Authorization': `Bearer ${process.env.INTERNAL_API_TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    return response.data.data;
  } catch (error) {
    throw handleExternalServiceError(sourceService, error);
  }
}

/**
 * Helper function to fetch content list from source service
 */
async function fetchContentListFromService(sourceService: string, limit: number, offset: number): Promise<any[]> {
  const serviceUrls: { [key: string]: string } = {
    'maattv': process.env.MAATTV_SERVICE_URL || '',
    'maatclass': process.env.MAATCLASS_SERVICE_URL || '',
    'maattube': process.env.MAATTUBE_SERVICE_URL || '',
    'maatcare': process.env.MAATCARE_SERVICE_URL || ''
  };

  const serviceUrl = serviceUrls[sourceService];
  if (!serviceUrl) {
    throw new ValidationError(`Unknown source service: ${sourceService}`);
  }

  try {
    const response = await axios.get(`${serviceUrl}/api/content`, {
      params: { limit, offset },
      timeout: 30000,
      headers: {
        'Authorization': `Bearer ${process.env.INTERNAL_API_TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    return response.data.data;
  } catch (error) {
    throw handleExternalServiceError(sourceService, error);
  }
}

/**
 * Helper functions for external service sync
 */
async function syncToSearchService(content: any): Promise<void> {
  // Implementation for search service sync
}

async function syncToRecommendationService(content: any): Promise<void> {
  // Implementation for recommendation service sync
}

async function syncToCDN(content: any): Promise<void> {
  // Implementation for CDN sync
}

async function removeFromSearchService(contentId: string): Promise<void> {
  // Implementation for search service removal
}

async function removeFromRecommendationService(contentId: string): Promise<void> {
  // Implementation for recommendation service removal
}

async function removeFromCDN(content: any): Promise<void> {
  // Implementation for CDN removal
}