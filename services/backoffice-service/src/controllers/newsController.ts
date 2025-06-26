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
import { formatTextForTTS, calculateDuration, generateTTSAudio, generateMedia, getDefaultTTSConfig, getDefaultVideoConfig } from '../utils/tts';

/**
 * Get all news articles with pagination and filtering
 */
export const getNewsArticles = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    category,
    status,
    priority,
    language = 'fr',
    sortBy = 'publishedAt',
    sortOrder = 'desc'
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {};

  if (status) {
    where.status = status;
  }

  if (category) {
    where.category = category;
  }

  if (priority) {
    where.priority = priority;
  }

  if (language) {
    where.language = language;
  }

  if (search) {
    where.OR = [
      { title: { contains: search, mode: 'insensitive' } },
      { content: { contains: search, mode: 'insensitive' } },
      { summary: { contains: search, mode: 'insensitive' } }
    ];
  }

  try {
    const result = await prisma.findManyWithPagination('newsArticle', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { [sortBy as string]: sortOrder },
      include: {
        source: {
          select: {
            id: true,
            name: true,
            sourceType: true
          }
        },
        broadcasts: {
          select: {
            id: true,
            status: true,
            broadcastType: true,
            startedAt: true,
            avatar: {
              select: {
                id: true,
                name: true
              }
            }
          }
        }
      }
    });

    logHelpers.logBusiness('News articles retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, category, status, priority, language }
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
 * Get news article by ID
 */
export const getNewsArticleById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    const article = await prisma.newsArticle.findUnique({
      where: { id },
      include: {
        source: true,
        broadcasts: {
          include: {
            avatar: {
              select: {
                id: true,
                name: true,
                type: true
              }
            }
          }
        }
      }
    });

    throwIfNotFound(article, 'Article not found');

    logHelpers.logBusiness('News article retrieved', {
      articleId: id,
      title: article.title
    }, req.user?.id);

    res.json({
      success: true,
      data: article
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Create news article (manual or from N8N)
 */
export const createNewsArticle = asyncHandler(async (req: Request, res: Response) => {
  const {
    sourceId,
    title,
    content,
    summary,
    originalUrl,
    imageUrl,
    category,
    priority = 'NORMAL',
    language = 'fr',
    publishedAt
  } = req.body;

  try {
    // Verify source exists
    const source = await prisma.newsSource.findUnique({
      where: { id: sourceId }
    });
    throwIfNotFound(source, 'News source not found');

    // Generate formatted text for TTS
    const formattedText = await formatTextForTTS(content);
    const estimatedDuration = calculateDuration(formattedText);

    const article = await prisma.newsArticle.create({
      data: {
        sourceId,
        title,
        content,
        summary,
        originalUrl,
        imageUrl,
        formattedText,
        duration: estimatedDuration,
        category,
        priority,
        language,
        publishedAt: publishedAt ? new Date(publishedAt) : new Date(),
        createdBy: req.user?.id
      },
      include: {
        source: {
          select: {
            id: true,
            name: true,
            sourceType: true
          }
        }
      }
    });

    // Clear cache
    await redisClient.del(CacheKeys.NEWS_ARTICLES);

    logHelpers.logBusiness('News article created', {
      articleId: article.id,
      title: article.title,
      category: article.category,
      priority: article.priority
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: article
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Update news article
 */
export const updateNewsArticle = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = req.body;

  try {
    // Check if article exists
    const existingArticle = await prisma.newsArticle.findUnique({
      where: { id }
    });
    throwIfNotFound(existingArticle, 'Article not found');

    // Update formatted text if content changed
    if (updateData.content && updateData.content !== existingArticle.content) {
      updateData.formattedText = await formatTextForTTS(updateData.content);
      updateData.duration = calculateDuration(updateData.formattedText);
    }

    const article = await prisma.newsArticle.update({
      where: { id },
      data: {
        ...updateData,
        updatedBy: req.user?.id
      },
      include: {
        source: {
          select: {
            id: true,
            name: true,
            sourceType: true
          }
        }
      }
    });

    // Clear cache
    await redisClient.del(CacheKeys.NEWS_ARTICLES);

    logHelpers.logBusiness('News article updated', {
      articleId: id,
      changes: Object.keys(updateData)
    }, req.user?.id);

    res.json({
      success: true,
      data: article
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Approve news article for broadcast
 */
export const approveNewsArticle = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { scheduledAt } = req.body;

  try {
    const article = await prisma.newsArticle.update({
      where: { id },
      data: {
        status: 'APPROVED',
        scheduledAt: scheduledAt ? new Date(scheduledAt) : null,
        updatedBy: req.user?.id
      }
    });

    logHelpers.logBusiness('News article approved', {
      articleId: id,
      scheduledAt
    }, req.user?.id);

    res.json({
      success: true,
      data: article
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Schedule news broadcast with avatar
 */
export const scheduleNewsBroadcast = asyncHandler(async (req: Request, res: Response) => {
  const { articleId, avatarId, broadcastType = 'LIVE', scheduledAt } = req.body;

  try {
    // Verify article and avatar exist
    const article = await prisma.newsArticle.findUnique({
      where: { id: articleId }
    });
    throwIfNotFound(article, 'Article not found');

    const avatar = await prisma.avatar.findUnique({
      where: { id: avatarId }
    });
    throwIfNotFound(avatar, 'Avatar not found');

    // Check if article is approved
    if (article.status !== 'APPROVED') {
      throw new ValidationError('Article must be approved before scheduling broadcast');
    }

    const broadcast = await prisma.newsBroadcast.create({
      data: {
        articleId,
        avatarId,
        broadcastType,
        status: 'SCHEDULED'
      },
      include: {
        article: {
          select: {
            id: true,
            title: true,
            formattedText: true,
            duration: true
          }
        },
        avatar: {
          select: {
            id: true,
            name: true,
            voiceId: true,
            voiceProvider: true
          }
        }
      }
    });

    // Update article status
    await prisma.newsArticle.update({
      where: { id: articleId },
      data: {
        status: 'SCHEDULED',
        scheduledAt: scheduledAt ? new Date(scheduledAt) : new Date()
      }
    });

    // Start TTS generation process
    if (broadcastType === 'RECORDED') {
      await generateTTSAudio(broadcast.id);
    }

    logHelpers.logBusiness('News broadcast scheduled', {
      broadcastId: broadcast.id,
      articleId,
      avatarId,
      broadcastType
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: broadcast
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get next news for avatar broadcast
 */
export const getNextNews = asyncHandler(async (req: Request, res: Response) => {
  const { avatarId } = req.params;

  try {
    const nextBroadcast = await prisma.newsBroadcast.findFirst({
      where: {
        avatarId,
        status: 'READY'
      },
      orderBy: {
        article: {
          priority: 'desc'
        }
      },
      include: {
        article: {
          include: {
            source: {
              select: {
                name: true
              }
            }
          }
        },
        avatar: {
          select: {
            name: true,
            voiceId: true,
            voiceProvider: true
          }
        }
      }
    });

    if (!nextBroadcast) {
      return res.json({
        success: true,
        data: null,
        message: 'No news ready for broadcast'
      });
    }

    res.json({
      success: true,
      data: nextBroadcast
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Update broadcast status
 */
export const updateBroadcastStatus = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { status, startedAt, endedAt, viewCount } = req.body;

  try {
    const updateData: any = { status };

    if (startedAt) updateData.startedAt = new Date(startedAt);
    if (endedAt) updateData.endedAt = new Date(endedAt);
    if (viewCount !== undefined) updateData.viewCount = viewCount;

    const broadcast = await prisma.newsBroadcast.update({
      where: { id },
      data: updateData
    });

    // Update article status if broadcast completed
    if (status === 'COMPLETED') {
      await prisma.newsArticle.update({
        where: { id: broadcast.articleId },
        data: {
          status: 'BROADCASTED',
          broadcastAt: new Date()
        }
      });
    }

    res.json({
      success: true,
      data: broadcast
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get news analytics
 */
export const getNewsAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const { period = '7d', avatarId } = req.query;

  try {
    const startDate = getStartDateForPeriod(period as string);
    
    const where: any = {
      createdAt: {
        gte: startDate
      }
    };

    if (avatarId) {
      where.broadcasts = {
        some: {
          avatarId: avatarId as string
        }
      };
    }

    const [totalArticles, broadcastStats, categoryStats] = await Promise.all([
      prisma.newsArticle.count({ where }),
      prisma.newsBroadcast.groupBy({
        by: ['status'],
        where: {
          createdAt: {
            gte: startDate
          },
          ...(avatarId && { avatarId: avatarId as string })
        },
        _count: true
      }),
      prisma.newsArticle.groupBy({
        by: ['category'],
        where,
        _count: true
      })
    ]);

    res.json({
      success: true,
      data: {
        totalArticles,
        broadcastStats,
        categoryStats,
        period
      }
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

// Helper functions

/**
 * Format text for Text-to-Speech
 */
async function formatTextForTTS(content: string): Promise<string> {
  // Remove HTML tags
  let formatted = content.replace(/<[^>]*>/g, '');
  
  // Remove URLs
  formatted = formatted.replace(/https?:\/\/[^\s]+/g, '');
  
  // Replace abbreviations
  const abbreviations = {
    'M.': 'Monsieur',
    'Mme': 'Madame',
    'Dr': 'Docteur',
    'etc.': 'et cetera',
    'vs': 'contre',
    'USA': 'États-Unis',
    'UE': 'Union Européenne'
  };
  
  Object.entries(abbreviations).forEach(([abbr, full]) => {
    formatted = formatted.replace(new RegExp(`\\b${abbr}\\b`, 'g'), full);
  });
  
  // Add pauses for better speech rhythm
  formatted = formatted.replace(/\./g, '. ');
  formatted = formatted.replace(/,/g, ', ');
  
  return formatted.trim();
}

/**
 * Calculate estimated duration in seconds
 */
function calculateDuration(text: string): number {
  const wordsPerMinute = 150; // Average speaking rate
  const wordCount = text.split(/\s+/).length;
  return Math.ceil((wordCount / wordsPerMinute) * 60);
}

/**
 * Generate TTS audio or video for broadcast
 */
async function generateTTSAudio(broadcastId: string, mediaType: 'audio' | 'video' = 'audio'): Promise<void> {
  try {
    // Update status to preparing
    await prisma.newsBroadcast.update({
      where: { id: broadcastId },
      data: { status: 'PREPARING' }
    });

    // Get broadcast details
    const broadcast = await prisma.newsBroadcast.findUnique({
      where: { id: broadcastId },
      include: {
        articles: {
          include: {
            article: true
          }
        }
      }
    });

    if (!broadcast) {
      throw new NotFoundError('Broadcast not found');
    }

    // Prepare text content
    const textContent = broadcast.articles
      .map(ba => `${ba.article.title}. ${ba.article.summary || ba.article.content}`)
      .join(' ');

    const formattedText = formatTextForTTS(textContent);
    
    logger.info(`Starting ${mediaType} generation for broadcast ${broadcastId}`);
    
    try {
      let result;
      
      if (mediaType === 'video') {
        // Generate video with HeyGen
        const videoConfig = getDefaultVideoConfig();
        result = await generateMedia(formattedText, {
          provider: 'heygen',
          voiceId: videoConfig.voiceId,
          model: 'heygen_v1',
          language: broadcast.language || 'fr',
          avatarId: videoConfig.avatarId,
          videoQuality: videoConfig.quality,
          backgroundId: videoConfig.backgroundId,
          enableGestures: videoConfig.enableGestures
        });
        
        await prisma.newsBroadcast.update({
          where: { id: broadcastId },
          data: {
            status: 'READY',
            audioUrl: result.audioUrl,
            videoUrl: result.videoUrl,
            thumbnailUrl: result.thumbnailUrl,
            duration: result.duration
          }
        });
      } else {
        // Generate audio with ElevenLabs
        const ttsConfig = getDefaultTTSConfig('elevenlabs');
        result = await generateMedia(formattedText, ttsConfig);
        
        await prisma.newsBroadcast.update({
          where: { id: broadcastId },
          data: {
            status: 'READY',
            audioUrl: result.audioUrl,
            duration: result.duration
          }
        });
      }
      
      logger.info(`${mediaType} generation completed for broadcast ${broadcastId}`);
      
    } catch (error) {
      logger.error(`${mediaType} generation failed for broadcast ${broadcastId}:`, error);
      await prisma.newsBroadcast.update({
        where: { id: broadcastId },
        data: { status: 'FAILED' }
      });
      throw error;
    }
    
  } catch (error) {
    logger.error(`Error starting ${mediaType} generation for broadcast ${broadcastId}:`, error);
    throw error;
  }
}

/**
 * Get start date for analytics period
 */
function getStartDateForPeriod(period: string): Date {
  const now = new Date();
  
  switch (period) {
    case '1d':
      return new Date(now.getTime() - 24 * 60 * 60 * 1000);
    case '7d':
      return new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    case '30d':
      return new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    case '90d':
      return new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
    default:
      return new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
  }
}