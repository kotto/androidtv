import { Request, Response } from 'express';
import { prisma } from '../utils/database';
import { redisClient, CacheKeys } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import {
  AppError,
  asyncHandler,
  handleDatabaseError
} from '../utils/errors';
import axios from 'axios';

/**
 * Get dashboard overview
 */
export const getDashboardOverview = asyncHandler(async (req: Request, res: Response) => {
  const { period = '30d' } = req.query;

  try {
    // Try to get from cache first
    const cacheKey = CacheKeys.dashboardOverview(period as string);
    const cached = await redisClient.get(cacheKey);
    
    if (cached) {
      const overview = JSON.parse(cached);
      
      logHelpers.logBusiness('Dashboard overview retrieved from cache', { period }, req.user?.id);
      
      return res.json({
        success: true,
        data: overview,
        cached: true
      });
    }

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

    // Get overview data in parallel
    const [avatarStats, studioStats, contentStats, workflowStats, rssStats] = await Promise.all([
      getAvatarStats(startDate),
      getStudioStats(startDate),
      getContentStats(startDate),
      getWorkflowStats(startDate),
      getRSSStats(startDate)
    ]);

    // Get external service stats
    const externalStats = await getExternalServiceStats();

    const overview = {
      period,
      generatedAt: new Date().toISOString(),
      stats: {
        avatars: avatarStats,
        studios: studioStats,
        content: contentStats,
        workflows: workflowStats,
        rss: rssStats,
        external: externalStats
      },
      summary: {
        totalAvatars: avatarStats.total,
        activeAvatars: avatarStats.active,
        totalStudios: studioStats.total,
        activeStudios: studioStats.active,
        totalContent: contentStats.total,
        publishedContent: contentStats.published,
        totalWorkflows: workflowStats.total,
        activeWorkflows: workflowStats.active,
        totalRSSFeeds: rssStats.total,
        activeRSSFeeds: rssStats.active
      }
    };

    // Cache the result for 5 minutes
    await redisClient.setex(cacheKey, 300, JSON.stringify(overview));

    logHelpers.logBusiness('Dashboard overview generated', { period }, req.user?.id);

    res.json({
      success: true,
      data: overview,
      cached: false
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get system health status
 */
export const getSystemHealth = asyncHandler(async (req: Request, res: Response) => {
  try {
    const health = {
      timestamp: new Date().toISOString(),
      status: 'healthy',
      services: {
        database: await checkDatabaseHealth(),
        redis: await checkRedisHealth(),
        userService: await checkUserServiceHealth(),
        maattvService: await checkMaatTVServiceHealth(),
        maatclassService: await checkMaatClassServiceHealth(),
        maattubeService: await checkMaatTubeServiceHealth(),
        maatcareService: await checkMaatCareServiceHealth(),
        n8nService: await checkN8nServiceHealth()
      }
    };

    // Determine overall status
    const serviceStatuses = Object.values(health.services);
    const hasUnhealthy = serviceStatuses.some(service => service.status !== 'healthy');
    const hasDegraded = serviceStatuses.some(service => service.status === 'degraded');
    
    if (hasUnhealthy) {
      health.status = 'unhealthy';
    } else if (hasDegraded) {
      health.status = 'degraded';
    }

    res.json({
      success: true,
      data: health
    });
  } catch (error) {
    res.status(503).json({
      success: false,
      data: {
        timestamp: new Date().toISOString(),
        status: 'unhealthy',
        error: error instanceof Error ? error.message : 'Unknown error'
      }
    });
  }
});

/**
 * Get recent activities
 */
export const getRecentActivities = asyncHandler(async (req: Request, res: Response) => {
  const { limit = 20 } = req.query;
  const limitNum = parseInt(limit as string);

  try {
    // Get recent activities from different entities
    const [recentAvatars, recentStudios, recentContent, recentWorkflows, recentRSS] = await Promise.all([
      prisma.aiAvatar.findMany({
        where: { deletedAt: null },
        orderBy: { updatedAt: 'desc' },
        take: 5,
        select: {
          id: true,
          name: true,
          updatedAt: true,
          updatedBy: true
        }
      }),
      
      prisma.virtualStudio.findMany({
        where: { deletedAt: null },
        orderBy: { updatedAt: 'desc' },
        take: 5,
        select: {
          id: true,
          name: true,
          updatedAt: true,
          updatedBy: true
        }
      }),
      
      prisma.unifiedContent.findMany({
        where: { deletedAt: null },
        orderBy: { updatedAt: 'desc' },
        take: 5,
        select: {
          id: true,
          title: true,
          contentType: true,
          updatedAt: true,
          updatedBy: true
        }
      }),
      
      prisma.workflow.findMany({
        where: { deletedAt: null },
        orderBy: { updatedAt: 'desc' },
        take: 5,
        select: {
          id: true,
          name: true,
          updatedAt: true,
          updatedBy: true
        }
      }),
      
      prisma.rssFeed.findMany({
        where: { deletedAt: null },
        orderBy: { updatedAt: 'desc' },
        take: 5,
        select: {
          id: true,
          name: true,
          updatedAt: true,
          updatedBy: true
        }
      })
    ]);

    // Combine and sort all activities
    const activities = [
      ...recentAvatars.map(item => ({
        id: item.id,
        type: 'avatar',
        title: item.name,
        action: 'updated',
        timestamp: item.updatedAt,
        userId: item.updatedBy
      })),
      ...recentStudios.map(item => ({
        id: item.id,
        type: 'studio',
        title: item.name,
        action: 'updated',
        timestamp: item.updatedAt,
        userId: item.updatedBy
      })),
      ...recentContent.map(item => ({
        id: item.id,
        type: 'content',
        title: item.title,
        contentType: item.contentType,
        action: 'updated',
        timestamp: item.updatedAt,
        userId: item.updatedBy
      })),
      ...recentWorkflows.map(item => ({
        id: item.id,
        type: 'workflow',
        title: item.name,
        action: 'updated',
        timestamp: item.updatedAt,
        userId: item.updatedBy
      })),
      ...recentRSS.map(item => ({
        id: item.id,
        type: 'rss',
        title: item.name,
        action: 'updated',
        timestamp: item.updatedAt,
        userId: item.updatedBy
      }))
    ].sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
     .slice(0, limitNum);

    res.json({
      success: true,
      data: {
        activities,
        total: activities.length
      }
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get performance metrics
 */
export const getPerformanceMetrics = asyncHandler(async (req: Request, res: Response) => {
  const { period = '24h' } = req.query;

  try {
    // Calculate date range
    const now = new Date();
    let startDate: Date;
    
    switch (period) {
      case '1h':
        startDate = new Date(now.getTime() - 60 * 60 * 1000);
        break;
      case '24h':
        startDate = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        break;
      case '7d':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      default:
        startDate = new Date(now.getTime() - 24 * 60 * 60 * 1000);
    }

    // Get workflow execution metrics
    const workflowMetrics = await getWorkflowExecutionMetrics(startDate);
    
    // Get content processing metrics
    const contentMetrics = await getContentProcessingMetrics(startDate);
    
    // Get RSS processing metrics
    const rssMetrics = await getRSSProcessingMetrics(startDate);

    const metrics = {
      period,
      generatedAt: new Date().toISOString(),
      workflows: workflowMetrics,
      content: contentMetrics,
      rss: rssMetrics,
      system: {
        uptime: process.uptime(),
        memoryUsage: process.memoryUsage(),
        cpuUsage: process.cpuUsage()
      }
    };

    res.json({
      success: true,
      data: metrics
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get analytics summary
 */
export const getAnalyticsSummary = asyncHandler(async (req: Request, res: Response) => {
  const { period = '30d' } = req.query;

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

    // Get analytics data
    const [contentAnalytics, workflowAnalytics, rssAnalytics] = await Promise.all([
      getContentAnalyticsData(startDate),
      getWorkflowAnalyticsData(startDate),
      getRSSAnalyticsData(startDate)
    ]);

    const summary = {
      period,
      generatedAt: new Date().toISOString(),
      content: contentAnalytics,
      workflows: workflowAnalytics,
      rss: rssAnalytics
    };

    res.json({
      success: true,
      data: summary
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Helper functions for stats collection
 */
async function getAvatarStats(startDate: Date) {
  const [total, active, recent] = await Promise.all([
    prisma.aiAvatar.count({ where: { deletedAt: null } }),
    prisma.aiAvatar.count({ where: { deletedAt: null, isActive: true } }),
    prisma.aiAvatar.count({ where: { deletedAt: null, createdAt: { gte: startDate } } })
  ]);

  return { total, active, recent };
}

async function getStudioStats(startDate: Date) {
  const [total, active, recent] = await Promise.all([
    prisma.virtualStudio.count({ where: { deletedAt: null } }),
    prisma.virtualStudio.count({ where: { deletedAt: null, isActive: true } }),
    prisma.virtualStudio.count({ where: { deletedAt: null, createdAt: { gte: startDate } } })
  ]);

  return { total, active, recent };
}

async function getContentStats(startDate: Date) {
  const [total, published, recent] = await Promise.all([
    prisma.unifiedContent.count({ where: { deletedAt: null } }),
    prisma.unifiedContent.count({ where: { deletedAt: null, status: 'PUBLISHED' } }),
    prisma.unifiedContent.count({ where: { deletedAt: null, createdAt: { gte: startDate } } })
  ]);

  return { total, published, recent };
}

async function getWorkflowStats(startDate: Date) {
  const [total, active, recent, executions] = await Promise.all([
    prisma.workflow.count({ where: { deletedAt: null } }),
    prisma.workflow.count({ where: { deletedAt: null, isActive: true } }),
    prisma.workflow.count({ where: { deletedAt: null, createdAt: { gte: startDate } } }),
    prisma.workflowExecution.count({ where: { startedAt: { gte: startDate } } })
  ]);

  return { total, active, recent, executions };
}

async function getRSSStats(startDate: Date) {
  const [total, active, recent, articles] = await Promise.all([
    prisma.rssFeed.count({ where: { deletedAt: null } }),
    prisma.rssFeed.count({ where: { deletedAt: null, isActive: true } }),
    prisma.rssFeed.count({ where: { deletedAt: null, createdAt: { gte: startDate } } }),
    prisma.rssArticle.count({ where: { createdAt: { gte: startDate } } })
  ]);

  return { total, active, recent, articles };
}

async function getExternalServiceStats() {
  // This would typically make API calls to external services
  // For now, return mock data
  return {
    userService: { status: 'healthy', responseTime: 45 },
    maattvService: { status: 'healthy', responseTime: 52 },
    maatclassService: { status: 'healthy', responseTime: 38 },
    maattubeService: { status: 'healthy', responseTime: 41 },
    maatcareService: { status: 'healthy', responseTime: 47 },
    n8nService: { status: 'healthy', responseTime: 33 }
  };
}

/**
 * Health check functions
 */
async function checkDatabaseHealth() {
  try {
    await prisma.healthCheck();
    return { status: 'healthy', responseTime: 10 };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkRedisHealth() {
  try {
    const start = Date.now();
    await redisClient.ping();
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkUserServiceHealth() {
  try {
    const userServiceUrl = process.env.USER_SERVICE_URL;
    if (!userServiceUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${userServiceUrl}/health`, { timeout: 5000 });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkMaatTVServiceHealth() {
  try {
    const serviceUrl = process.env.MAATTV_SERVICE_URL;
    if (!serviceUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${serviceUrl}/health`, { timeout: 5000 });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkMaatClassServiceHealth() {
  try {
    const serviceUrl = process.env.MAATCLASS_SERVICE_URL;
    if (!serviceUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${serviceUrl}/health`, { timeout: 5000 });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkMaatTubeServiceHealth() {
  try {
    const serviceUrl = process.env.MAATTUBE_SERVICE_URL;
    if (!serviceUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${serviceUrl}/health`, { timeout: 5000 });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkMaatCareServiceHealth() {
  try {
    const serviceUrl = process.env.MAATCARE_SERVICE_URL;
    if (!serviceUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${serviceUrl}/health`, { timeout: 5000 });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

async function checkN8nServiceHealth() {
  try {
    const n8nApiUrl = process.env.N8N_API_URL;
    if (!n8nApiUrl) {
      return { status: 'unhealthy', error: 'Service URL not configured' };
    }

    const start = Date.now();
    await axios.get(`${n8nApiUrl}/healthz`, { 
      timeout: 5000,
      headers: {
        'X-N8N-API-KEY': process.env.N8N_API_KEY
      }
    });
    const responseTime = Date.now() - start;
    return { status: 'healthy', responseTime };
  } catch (error) {
    return { status: 'unhealthy', error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

/**
 * Metrics helper functions
 */
async function getWorkflowExecutionMetrics(startDate: Date) {
  const [totalExecutions, successfulExecutions, failedExecutions] = await Promise.all([
    prisma.workflowExecution.count({ where: { startedAt: { gte: startDate } } }),
    prisma.workflowExecution.count({ where: { startedAt: { gte: startDate }, status: 'SUCCESS' } }),
    prisma.workflowExecution.count({ where: { startedAt: { gte: startDate }, status: 'FAILED' } })
  ]);

  return {
    total: totalExecutions,
    successful: successfulExecutions,
    failed: failedExecutions,
    successRate: totalExecutions > 0 ? (successfulExecutions / totalExecutions * 100).toFixed(2) : 0
  };
}

async function getContentProcessingMetrics(startDate: Date) {
  const [totalContent, publishedContent, draftContent] = await Promise.all([
    prisma.unifiedContent.count({ where: { createdAt: { gte: startDate } } }),
    prisma.unifiedContent.count({ where: { createdAt: { gte: startDate }, status: 'PUBLISHED' } }),
    prisma.unifiedContent.count({ where: { createdAt: { gte: startDate }, status: 'DRAFT' } })
  ]);

  return {
    total: totalContent,
    published: publishedContent,
    draft: draftContent,
    publishRate: totalContent > 0 ? (publishedContent / totalContent * 100).toFixed(2) : 0
  };
}

async function getRSSProcessingMetrics(startDate: Date) {
  const [totalArticles, factCheckedArticles, aiSummaryArticles] = await Promise.all([
    prisma.rssArticle.count({ where: { createdAt: { gte: startDate } } }),
    prisma.rssArticle.count({ where: { createdAt: { gte: startDate }, factCheckStatus: { not: 'PENDING' } } }),
    prisma.rssArticle.count({ where: { createdAt: { gte: startDate }, aiSummary: { not: null } } })
  ]);

  return {
    total: totalArticles,
    factChecked: factCheckedArticles,
    aiSummary: aiSummaryArticles,
    factCheckRate: totalArticles > 0 ? (factCheckedArticles / totalArticles * 100).toFixed(2) : 0
  };
}

/**
 * Analytics helper functions
 */
async function getContentAnalyticsData(startDate: Date) {
  const contentByType = await prisma.unifiedContent.groupBy({
    by: ['contentType'],
    where: { createdAt: { gte: startDate } },
    _count: true
  });

  const contentByService = await prisma.unifiedContent.groupBy({
    by: ['sourceService'],
    where: { createdAt: { gte: startDate } },
    _count: true
  });

  return {
    byType: contentByType.reduce((acc: any, item: any) => {
      acc[item.contentType] = item._count;
      return acc;
    }, {}),
    byService: contentByService.reduce((acc: any, item: any) => {
      acc[item.sourceService] = item._count;
      return acc;
    }, {})
  };
}

async function getWorkflowAnalyticsData(startDate: Date) {
  const executionsByStatus = await prisma.workflowExecution.groupBy({
    by: ['status'],
    where: { startedAt: { gte: startDate } },
    _count: true
  });

  return {
    executionsByStatus: executionsByStatus.reduce((acc: any, item: any) => {
      acc[item.status] = item._count;
      return acc;
    }, {})
  };
}

async function getRSSAnalyticsData(startDate: Date) {
  const articlesByStatus = await prisma.rssArticle.groupBy({
    by: ['factCheckStatus'],
    where: { createdAt: { gte: startDate } },
    _count: true
  });

  return {
    articlesByFactCheckStatus: articlesByStatus.reduce((acc: any, item: any) => {
      acc[item.factCheckStatus] = item._count;
      return acc;
    }, {})
  };
}