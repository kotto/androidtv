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
import Parser from 'rss-parser';

const rssParser = new Parser();

/**
 * Get all RSS feeds with pagination and filtering
 */
export const getRSSFeeds = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    category,
    language,
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

  if (category) {
    where.category = category;
  }

  if (language) {
    where.language = language;
  }

  if (search) {
    where.OR = [
      { name: { contains: search, mode: 'insensitive' } },
      { url: { contains: search, mode: 'insensitive' } }
    ];
  }

  try {
    const result = await prisma.findManyWithPagination('rssFeed', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { createdAt: 'desc' },
      include: {
        articles: {
          where: { isActive: true },
          orderBy: { publishedAt: 'desc' },
          take: 3,
          select: {
            id: true,
            title: true,
            publishedAt: true,
            factCheckStatus: true
          }
        },
        _count: {
          select: {
            articles: true
          }
        }
      }
    });

    logHelpers.logBusiness('RSS feeds retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, category, language, status }
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
 * Get RSS feed by ID
 */
export const getRSSFeedById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    const rssFeed = await prisma.rssFeed.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        articles: {
          where: { isActive: true },
          orderBy: { publishedAt: 'desc' },
          take: 20,
          select: {
            id: true,
            title: true,
            summary: true,
            publishedAt: true,
            factCheckStatus: true,
            factCheckResult: true,
            aiSummary: true
          }
        },
        _count: {
          select: {
            articles: true
          }
        }
      }
    });

    throwIfNotFound(rssFeed, 'RSS Feed');

    logHelpers.logBusiness('RSS feed retrieved', { feedId: id }, req.user?.id);

    res.json({
      success: true,
      data: rssFeed
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Create new RSS feed
 */
export const createRSSFeed = asyncHandler(async (req: Request, res: Response) => {
  const {
    name,
    url,
    category,
    language,
    updateFrequency,
    factCheckEnabled,
    aiSummaryEnabled,
    isActive = true
  } = req.body;

  try {
    // Check if RSS feed with same URL exists
    const existingFeed = await prisma.rssFeed.findFirst({
      where: {
        url,
        deletedAt: null
      }
    });

    if (existingFeed) {
      throw new ConflictError(`RSS feed with URL '${url}' already exists`);
    }

    // Validate RSS feed URL
    await validateRSSFeed(url);

    const rssFeed = await prisma.rssFeed.create({
      data: {
        name,
        url,
        category,
        language,
        updateFrequency,
        factCheckEnabled,
        aiSummaryEnabled,
        isActive,
        lastFetchedAt: null,
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            articles: true
          }
        }
      }
    });

    // Schedule initial fetch
    if (isActive) {
      await scheduleRSSFetch(rssFeed.id);
    }

    logHelpers.logBusiness('RSS feed created', {
      feedId: rssFeed.id,
      name: rssFeed.name,
      url: rssFeed.url,
      category: rssFeed.category
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: rssFeed,
      message: 'RSS feed created successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Update RSS feed
 */
export const updateRSSFeed = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = { ...req.body };
  delete updateData.id;
  updateData.updatedBy = req.user!.id;

  try {
    // Check if RSS feed exists
    const existingFeed = await prisma.rssFeed.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingFeed, 'RSS Feed');

    // Check if URL is being changed and if it conflicts
    if (updateData.url && updateData.url !== existingFeed!.url) {
      const urlConflict = await prisma.rssFeed.findFirst({
        where: {
          url: updateData.url,
          id: { not: id },
          deletedAt: null
        }
      });

      if (urlConflict) {
        throw new ConflictError(`RSS feed with URL '${updateData.url}' already exists`);
      }

      // Validate new RSS feed URL
      await validateRSSFeed(updateData.url);
    }

    const rssFeed = await prisma.rssFeed.update({
      where: { id },
      data: updateData,
      include: {
        articles: {
          where: { isActive: true },
          orderBy: { publishedAt: 'desc' },
          take: 5
        },
        _count: {
          select: {
            articles: true
          }
        }
      }
    });

    // Update fetch schedule if needed
    if (updateData.isActive !== undefined || updateData.updateFrequency) {
      if (rssFeed.isActive) {
        await scheduleRSSFetch(id);
      } else {
        await unscheduleRSSFetch(id);
      }
    }

    logHelpers.logBusiness('RSS feed updated', {
      feedId: id,
      changes: Object.keys(updateData)
    }, req.user?.id);

    res.json({
      success: true,
      data: rssFeed,
      message: 'RSS feed updated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Delete RSS feed (soft delete)
 */
export const deleteRSSFeed = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if RSS feed exists
    const existingFeed = await prisma.rssFeed.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingFeed, 'RSS Feed');

    await prisma.softDelete('rssFeed', { id });

    // Unschedule RSS fetch
    await unscheduleRSSFetch(id);

    logHelpers.logBusiness('RSS feed deleted', {
      feedId: id,
      name: existingFeed!.name
    }, req.user?.id);

    res.json({
      success: true,
      message: 'RSS feed deleted successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Manually fetch RSS feed
 */
export const fetchRSSFeed = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if RSS feed exists
    const rssFeed = await prisma.rssFeed.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(rssFeed, 'RSS Feed');

    if (!rssFeed!.isActive) {
      throw new ValidationError('Cannot fetch inactive RSS feed');
    }

    // Fetch and process RSS feed
    const result = await processRSSFeed(rssFeed!);

    // Update last fetched timestamp
    await prisma.rssFeed.update({
      where: { id },
      data: {
        lastFetchedAt: new Date(),
        updatedBy: req.user!.id
      }
    });

    logHelpers.logBusiness('RSS feed fetched manually', {
      feedId: id,
      articlesProcessed: result.articlesProcessed,
      newArticles: result.newArticles
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        feed: {
          id: rssFeed!.id,
          name: rssFeed!.name,
          url: rssFeed!.url
        },
        result
      },
      message: 'RSS feed fetched successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get RSS articles with pagination and filtering
 */
export const getRSSArticles = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const {
    page = 1,
    limit = 10,
    search,
    factCheckStatus,
    startDate,
    endDate
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    feedId: id,
    deletedAt: null
  };

  if (factCheckStatus) {
    where.factCheckStatus = factCheckStatus;
  }

  if (search) {
    where.OR = [
      { title: { contains: search, mode: 'insensitive' } },
      { summary: { contains: search, mode: 'insensitive' } }
    ];
  }

  if (startDate || endDate) {
    where.publishedAt = {};
    if (startDate) where.publishedAt.gte = new Date(startDate as string);
    if (endDate) where.publishedAt.lte = new Date(endDate as string);
  }

  try {
    // Check if RSS feed exists
    const rssFeed = await prisma.rssFeed.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(rssFeed, 'RSS Feed');

    const result = await prisma.findManyWithPagination('rssArticle', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { publishedAt: 'desc' }
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
 * Fact-check RSS article
 */
export const factCheckArticle = asyncHandler(async (req: Request, res: Response) => {
  const { id, articleId } = req.params;

  try {
    // Check if article exists
    const article = await prisma.rssArticle.findFirst({
      where: {
        id: articleId,
        feedId: id,
        deletedAt: null
      },
      include: {
        feed: true
      }
    });

    throwIfNotFound(article, 'RSS Article');

    if (!article!.feed.factCheckEnabled) {
      throw new ValidationError('Fact-checking is not enabled for this RSS feed');
    }

    // Perform fact-checking
    const factCheckResult = await performFactCheck(article!);

    // Update article with fact-check result
    const updatedArticle = await prisma.rssArticle.update({
      where: { id: articleId },
      data: {
        factCheckStatus: factCheckResult.status,
        factCheckResult: factCheckResult.details,
        factCheckedAt: new Date(),
        updatedBy: req.user!.id
      }
    });

    logHelpers.logBusiness('Article fact-checked', {
      articleId,
      feedId: id,
      status: factCheckResult.status,
      score: factCheckResult.details.score
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        article: updatedArticle,
        factCheck: factCheckResult
      },
      message: 'Article fact-checked successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Generate AI summary for RSS article
 */
export const generateAISummary = asyncHandler(async (req: Request, res: Response) => {
  const { id, articleId } = req.params;

  try {
    // Check if article exists
    const article = await prisma.rssArticle.findFirst({
      where: {
        id: articleId,
        feedId: id,
        deletedAt: null
      },
      include: {
        feed: true
      }
    });

    throwIfNotFound(article, 'RSS Article');

    if (!article!.feed.aiSummaryEnabled) {
      throw new ValidationError('AI summary is not enabled for this RSS feed');
    }

    // Generate AI summary
    const aiSummary = await generateArticleSummary(article!);

    // Update article with AI summary
    const updatedArticle = await prisma.rssArticle.update({
      where: { id: articleId },
      data: {
        aiSummary,
        aiSummaryGeneratedAt: new Date(),
        updatedBy: req.user!.id
      }
    });

    logHelpers.logBusiness('AI summary generated', {
      articleId,
      feedId: id,
      summaryLength: aiSummary.length
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        article: updatedArticle,
        aiSummary
      },
      message: 'AI summary generated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get RSS feed analytics
 */
export const getRSSFeedAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { period = '30d' } = req.query;

  try {
    // Check if RSS feed exists
    const rssFeed = await prisma.rssFeed.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(rssFeed, 'RSS Feed');

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
    const [totalArticles, factCheckedArticles, aiSummaryArticles, factCheckStats] = await Promise.all([
      // Total articles
      prisma.rssArticle.count({
        where: {
          feedId: id,
          publishedAt: { gte: startDate }
        }
      }),
      
      // Fact-checked articles
      prisma.rssArticle.count({
        where: {
          feedId: id,
          factCheckStatus: { not: 'PENDING' },
          publishedAt: { gte: startDate }
        }
      }),
      
      // AI summary articles
      prisma.rssArticle.count({
        where: {
          feedId: id,
          aiSummary: { not: null },
          publishedAt: { gte: startDate }
        }
      }),
      
      // Fact-check status distribution
      prisma.rssArticle.groupBy({
        by: ['factCheckStatus'],
        where: {
          feedId: id,
          publishedAt: { gte: startDate }
        },
        _count: true
      })
    ]);

    // Get daily article counts for chart
    const dailyArticles = await prisma.$queryRaw`
      SELECT 
        DATE(published_at) as date,
        COUNT(*) as article_count
      FROM rss_articles 
      WHERE feed_id = ${id} 
        AND published_at >= ${startDate}
        AND deleted_at IS NULL
      GROUP BY DATE(published_at)
      ORDER BY date ASC
    `;

    const analytics = {
      summary: {
        totalArticles,
        factCheckedArticles,
        aiSummaryArticles,
        factCheckRate: totalArticles > 0 ? (factCheckedArticles / totalArticles * 100).toFixed(2) : 0,
        aiSummaryRate: totalArticles > 0 ? (aiSummaryArticles / totalArticles * 100).toFixed(2) : 0
      },
      factCheckStats: factCheckStats.reduce((acc: any, stat: any) => {
        acc[stat.factCheckStatus] = stat._count;
        return acc;
      }, {}),
      chart: {
        dailyArticles
      },
      period,
      feed: {
        id: rssFeed.id,
        name: rssFeed.name,
        category: rssFeed.category
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
 * Helper function to validate RSS feed
 */
async function validateRSSFeed(url: string): Promise<void> {
  try {
    const feed = await rssParser.parseURL(url);
    if (!feed.title || !feed.items || feed.items.length === 0) {
      throw new ValidationError('Invalid RSS feed: No title or items found');
    }
  } catch (error) {
    if (error instanceof ValidationError) throw error;
    throw new ValidationError(`Invalid RSS feed URL: ${error instanceof Error ? error.message : 'Unknown error'}`);
  }
}

/**
 * Helper function to process RSS feed
 */
async function processRSSFeed(rssFeed: any): Promise<any> {
  try {
    const feed = await rssParser.parseURL(rssFeed.url);
    let articlesProcessed = 0;
    let newArticles = 0;

    for (const item of feed.items) {
      if (!item.title || !item.link) continue;

      articlesProcessed++;

      // Check if article already exists
      const existingArticle = await prisma.rssArticle.findFirst({
        where: {
          feedId: rssFeed.id,
          originalUrl: item.link
        }
      });

      if (!existingArticle) {
        // Create new article
        await prisma.rssArticle.create({
          data: {
            feedId: rssFeed.id,
            title: item.title,
            summary: item.contentSnippet || item.content || '',
            originalUrl: item.link,
            publishedAt: item.pubDate ? new Date(item.pubDate) : new Date(),
            factCheckStatus: rssFeed.factCheckEnabled ? 'PENDING' : 'DISABLED',
            isActive: true,
            createdBy: 'system',
            updatedBy: 'system'
          }
        });
        newArticles++;
      }
    }

    return {
      articlesProcessed,
      newArticles,
      feedTitle: feed.title,
      feedDescription: feed.description
    };
  } catch (error) {
    throw new ExternalServiceError('RSS Parser', error instanceof Error ? error.message : 'Unknown error');
  }
}

/**
 * Helper function to perform fact-checking
 */
async function performFactCheck(article: any): Promise<any> {
  try {
    const factCheckApiUrl = process.env.FACT_CHECK_API_URL;
    if (!factCheckApiUrl) {
      throw new ExternalServiceError('Fact Check', 'API URL not configured');
    }

    const response = await axios.post(`${factCheckApiUrl}/check`, {
      title: article.title,
      content: article.summary,
      url: article.originalUrl
    }, {
      timeout: 30000,
      headers: {
        'Authorization': `Bearer ${process.env.FACT_CHECK_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    return {
      status: response.data.status, // VERIFIED, DISPUTED, FALSE, MIXED
      details: response.data.details
    };
  } catch (error) {
    throw handleExternalServiceError('Fact Check API', error);
  }
}

/**
 * Helper function to generate AI summary
 */
async function generateArticleSummary(article: any): Promise<string> {
  try {
    const aiApiUrl = process.env.AI_SUMMARY_API_URL;
    if (!aiApiUrl) {
      throw new ExternalServiceError('AI Summary', 'API URL not configured');
    }

    const response = await axios.post(`${aiApiUrl}/summarize`, {
      title: article.title,
      content: article.summary,
      language: article.feed.language,
      maxLength: 200
    }, {
      timeout: 30000,
      headers: {
        'Authorization': `Bearer ${process.env.AI_SUMMARY_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    return response.data.summary;
  } catch (error) {
    throw handleExternalServiceError('AI Summary API', error);
  }
}

/**
 * Helper function to schedule RSS fetch
 */
async function scheduleRSSFetch(feedId: string): Promise<void> {
  // Implementation would depend on your job scheduler (e.g., Bull, Agenda, etc.)
  logger.info(`Scheduling RSS fetch for feed ${feedId}`);
}

/**
 * Helper function to unschedule RSS fetch
 */
async function unscheduleRSSFetch(feedId: string): Promise<void> {
  // Implementation would depend on your job scheduler
  logger.info(`Unscheduling RSS fetch for feed ${feedId}`);
}