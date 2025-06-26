import { Request, Response, NextFunction } from 'express';
import { prisma } from '../utils/database';
import { logger } from '../utils/logger';

/**
 * Extended request interface for news-specific authentication
 */
export interface NewsAuthRequest extends Request {
  user?: {
    id: string;
    email: string;
    role: string;
    permissions: string[];
  };
  newsSource?: {
    id: string;
    name: string;
    priority: number;
    isActive: boolean;
  };
}

/**
 * Check if user can manage news content
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const canManageNews = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.user;
    
    if (!user) {
      res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
      return;
    }

    // Check if user has news management permissions
    const allowedRoles = ['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN'];
    
    if (!allowedRoles.includes(user.role)) {
      res.status(403).json({
        success: false,
        message: 'Insufficient permissions for news management'
      });
      return;
    }

    next();
  } catch (error) {
    logger.error('Error in canManageNews middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};

/**
 * Check if user can approve news articles
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const canApproveNews = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.user;
    
    if (!user) {
      res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
      return;
    }

    // Only moderators and admins can approve news
    const allowedRoles = ['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN'];
    
    if (!allowedRoles.includes(user.role)) {
      res.status(403).json({
        success: false,
        message: 'Insufficient permissions for news approval'
      });
      return;
    }

    next();
  } catch (error) {
    logger.error('Error in canApproveNews middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};

/**
 * Check if user can manage news sources
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const canManageSources = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.user;
    
    if (!user) {
      res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
      return;
    }

    // Only moderators and admins can manage sources
    const allowedRoles = ['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN'];
    
    if (!allowedRoles.includes(user.role)) {
      res.status(403).json({
        success: false,
        message: 'Insufficient permissions for source management'
      });
      return;
    }

    next();
  } catch (error) {
    logger.error('Error in canManageSources middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};

/**
 * Validate API key for webhook endpoints
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const validateWebhookApiKey = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const apiKey = req.headers['x-api-key'] as string;
    const expectedApiKey = process.env.N8N_WEBHOOK_API_KEY;

    if (!apiKey) {
      res.status(401).json({
        success: false,
        message: 'API key required'
      });
      return;
    }

    if (!expectedApiKey) {
      logger.error('N8N_WEBHOOK_API_KEY not configured');
      res.status(500).json({
        success: false,
        message: 'Webhook API key not configured'
      });
      return;
    }

    if (apiKey !== expectedApiKey) {
      logger.warn('Invalid webhook API key attempt', {
        providedKey: apiKey.substring(0, 8) + '...',
        ip: req.ip,
        userAgent: req.get('User-Agent')
      });
      
      res.status(401).json({
        success: false,
        message: 'Invalid API key'
      });
      return;
    }

    // Log successful webhook authentication
    logger.info('Webhook API key validated', {
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      endpoint: req.path
    });

    next();
  } catch (error) {
    logger.error('Error in validateWebhookApiKey middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};

/**
 * Load news source information for requests
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const loadNewsSource = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const sourceId = req.params.sourceId || req.body.sourceId;
    
    if (!sourceId) {
      next();
      return;
    }

    const source = await prisma.newsSource.findUnique({
      where: { id: sourceId },
      select: {
        id: true,
        name: true,
        priority: true,
        isActive: true
      }
    });

    if (source) {
      req.newsSource = source;
    }

    next();
  } catch (error) {
    logger.error('Error in loadNewsSource middleware:', error);
    next(); // Continue even if source loading fails
  }
};

/**
 * Check if user can access specific news article
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const canAccessArticle = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.user;
    const articleId = req.params.id;
    
    if (!user || !articleId) {
      res.status(401).json({
        success: false,
        message: 'Authentication required'
      });
      return;
    }

    // Admins and moderators can access all articles
    if (['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN'].includes(user.role)) {
      next();
      return;
    }

    // News editors can only access articles they created or from their assigned sources
    if (user.role === 'NEWS_EDITOR') {
      const article = await prisma.newsArticle.findUnique({
        where: { id: articleId },
        include: {
          source: {
            select: {
              id: true,
              name: true
            }
          }
        }
      });

      if (!article) {
        res.status(404).json({
          success: false,
          message: 'Article not found'
        });
        return;
      }

      // For now, allow all news editors to access all articles
      // In the future, implement source-based access control
      next();
      return;
    }

    res.status(403).json({
      success: false,
      message: 'Insufficient permissions'
    });
  } catch (error) {
    logger.error('Error in canAccessArticle middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};

/**
 * Rate limiting for webhook endpoints
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const webhookRateLimit = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const clientIp = req.ip;
    const rateLimitKey = `webhook_rate_limit:${clientIp}`;
    const maxRequests = 100; // Max requests per window
    const windowMs = 15 * 60 * 1000; // 15 minutes

    // This is a simplified rate limiting implementation
    // In production, use Redis with proper sliding window
    const redis = req.app.locals.redis;
    
    if (redis) {
      const current = await redis.incr(rateLimitKey);
      
      if (current === 1) {
        await redis.expire(rateLimitKey, Math.ceil(windowMs / 1000));
      }
      
      if (current > maxRequests) {
        res.status(429).json({
          success: false,
          message: 'Rate limit exceeded',
          retryAfter: windowMs / 1000
        });
        return;
      }
    }

    next();
  } catch (error) {
    logger.error('Error in webhookRateLimit middleware:', error);
    next(); // Continue even if rate limiting fails
  }
};

/**
 * Validate news article data
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
export const validateArticleData = async (
  req: NewsAuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const { title, content, category } = req.body;
    const maxLength = parseInt(process.env.NEWS_MAX_ARTICLE_LENGTH || '5000');
    const minLength = parseInt(process.env.NEWS_MIN_ARTICLE_LENGTH || '50');

    // Validate content length
    if (content && content.length > maxLength) {
      res.status(400).json({
        success: false,
        message: `Article content exceeds maximum length of ${maxLength} characters`
      });
      return;
    }

    if (content && content.length < minLength) {
      res.status(400).json({
        success: false,
        message: `Article content must be at least ${minLength} characters`
      });
      return;
    }

    // Validate title length
    if (title && title.length > 200) {
      res.status(400).json({
        success: false,
        message: 'Article title exceeds maximum length of 200 characters'
      });
      return;
    }

    // Validate category
    const validCategories = [
      'BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE',
      'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL',
      'LOCAL', 'WEATHER', 'OTHER'
    ];

    if (category && !validCategories.includes(category)) {
      res.status(400).json({
        success: false,
        message: 'Invalid news category'
      });
      return;
    }

    next();
  } catch (error) {
    logger.error('Error in validateArticleData middleware:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
};