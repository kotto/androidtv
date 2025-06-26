import { Router } from 'express';
import {
  getNewsArticles,
  getNewsArticleById,
  createNewsArticle,
  updateNewsArticle,
  approveNewsArticle,
  scheduleNewsBroadcast,
  getNextNews,
  updateBroadcastStatus,
  getNewsAnalytics
} from '../controllers/newsController';
import {
  authenticate,
  requireRole
} from '../middleware/auth';
import {
  canManageNews,
  validateWebhookApiKey,
  validateArticleData
} from '../middleware/newsAuth';
import { prisma } from '../utils/database';
import { formatTextForTTS, calculateDuration } from '../utils/tts';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/news/articles
 * @desc Get all news articles with pagination and filtering
 * @access Private (Editor+)
 */
router.get('/articles',
  requireRole(['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('category').optional().isIn([
      'BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE', 
      'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL', 
      'LOCAL', 'WEATHER', 'OTHER'
    ]),
    query('status').optional().isIn([
      'PENDING', 'APPROVED', 'REJECTED', 'SCHEDULED', 
      'BROADCASTING', 'BROADCASTED', 'ARCHIVED'
    ]),
    query('priority').optional().isIn(['URGENT', 'HIGH', 'NORMAL', 'LOW']),
    query('language').optional().isString().isLength({ min: 2, max: 5 }),
    query('sortBy').optional().isIn(['title', 'publishedAt', 'createdAt', 'priority']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getNewsArticles
);

/**
 * @route GET /api/news/articles/:id
 * @desc Get news article by ID
 * @access Private (Editor+)
 */
router.get('/articles/:id',
  requireRole(['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Article ID is required')
  ],
  validateRequest,
  getNewsArticleById
);

/**
 * @route POST /api/news/articles
 * @desc Create new news article
 * @access Private (Editor+)
 */
router.post('/articles',
  requireRole(['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    body('sourceId').isString().notEmpty().withMessage('Source ID is required'),
    body('title').isString().isLength({ min: 10, max: 200 }).withMessage('Title must be between 10 and 200 characters'),
    body('content').isString().isLength({ min: 50 }).withMessage('Content must be at least 50 characters'),
    body('summary').optional().isString().isLength({ max: 500 }).withMessage('Summary must not exceed 500 characters'),
    body('originalUrl').optional().isURL().withMessage('Original URL must be valid'),
    body('imageUrl').optional().isURL().withMessage('Image URL must be valid'),
    body('category').isIn([
      'BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE', 
      'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL', 
      'LOCAL', 'WEATHER', 'OTHER'
    ]).withMessage('Invalid category'),
    body('priority').optional().isIn(['URGENT', 'HIGH', 'NORMAL', 'LOW']).withMessage('Invalid priority'),
    body('language').optional().isString().isLength({ min: 2, max: 5 }).withMessage('Invalid language code'),
    body('publishedAt').optional().isISO8601().withMessage('Published date must be valid ISO 8601 date')
  ],
  validateRequest,
  createNewsArticle
);

/**
 * @route PUT /api/news/articles/:id
 * @desc Update news article
 * @access Private (Editor+)
 */
router.put('/articles/:id',
  requireRole(['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Article ID is required'),
    body('title').optional().isString().isLength({ min: 10, max: 200 }).withMessage('Title must be between 10 and 200 characters'),
    body('content').optional().isString().isLength({ min: 50 }).withMessage('Content must be at least 50 characters'),
    body('summary').optional().isString().isLength({ max: 500 }).withMessage('Summary must not exceed 500 characters'),
    body('originalUrl').optional().isURL().withMessage('Original URL must be valid'),
    body('imageUrl').optional().isURL().withMessage('Image URL must be valid'),
    body('category').optional().isIn([
      'BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE', 
      'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL', 
      'LOCAL', 'WEATHER', 'OTHER'
    ]).withMessage('Invalid category'),
    body('priority').optional().isIn(['URGENT', 'HIGH', 'NORMAL', 'LOW']).withMessage('Invalid priority'),
    body('status').optional().isIn([
      'PENDING', 'APPROVED', 'REJECTED', 'SCHEDULED', 
      'BROADCASTING', 'BROADCASTED', 'ARCHIVED'
    ]).withMessage('Invalid status'),
    body('factCheckStatus').optional().isIn([
      'PENDING', 'VERIFIED', 'DISPUTED', 'FALSE', 'MIXED'
    ]).withMessage('Invalid fact check status')
  ],
  validateRequest,
  updateNewsArticle
);

/**
 * @route POST /api/news/articles/:id/approve
 * @desc Approve news article for broadcast
 * @access Private (Moderator+)
 */
router.post('/articles/:id/approve',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Article ID is required'),
    body('scheduledAt').optional().isISO8601().withMessage('Scheduled date must be valid ISO 8601 date')
  ],
  validateRequest,
  approveNewsArticle
);

/**
 * @route POST /api/news/broadcasts
 * @desc Schedule news broadcast with avatar
 * @access Private (Moderator+)
 */
router.post('/broadcasts',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    body('articleId').isString().notEmpty().withMessage('Article ID is required'),
    body('avatarId').isString().notEmpty().withMessage('Avatar ID is required'),
    body('broadcastType').optional().isIn(['LIVE', 'RECORDED', 'SCHEDULED']).withMessage('Invalid broadcast type'),
    body('scheduledAt').optional().isISO8601().withMessage('Scheduled date must be valid ISO 8601 date')
  ],
  validateRequest,
  scheduleNewsBroadcast
);

/**
 * @route GET /api/news/next/:avatarId
 * @desc Get next news for avatar broadcast
 * @access Private (System/Avatar)
 */
router.get('/next/:avatarId',
  requireRole(['SYSTEM', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('avatarId').isString().notEmpty().withMessage('Avatar ID is required')
  ],
  validateRequest,
  getNextNews
);

/**
 * @route PUT /api/news/broadcasts/:id/status
 * @desc Update broadcast status
 * @access Private (System/Moderator+)
 */
router.put('/broadcasts/:id/status',
  requireRole(['SYSTEM', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Broadcast ID is required'),
    body('status').isIn([
      'SCHEDULED', 'PREPARING', 'READY', 'BROADCASTING', 
      'COMPLETED', 'FAILED', 'CANCELLED'
    ]).withMessage('Invalid broadcast status'),
    body('startedAt').optional().isISO8601().withMessage('Started date must be valid ISO 8601 date'),
    body('endedAt').optional().isISO8601().withMessage('Ended date must be valid ISO 8601 date'),
    body('viewCount').optional().isInt({ min: 0 }).withMessage('View count must be a positive integer')
  ],
  validateRequest,
  updateBroadcastStatus
);

/**
 * @route POST /api/news/broadcasts/:id/generate-video
 * @desc Generate video for broadcast using HeyGen
 * @access Private (Moderator+)
 */
router.post('/broadcasts/:id/generate-video',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Broadcast ID is required'),
    body('avatarId').optional().isString().withMessage('Avatar ID must be a string'),
    body('voiceId').optional().isString().withMessage('Voice ID must be a string'),
    body('quality').optional().isIn(['low', 'medium', 'high', 'ultra']).withMessage('Invalid video quality'),
    body('aspectRatio').optional().isIn(['16:9', '9:16', '1:1']).withMessage('Invalid aspect ratio'),
    body('backgroundId').optional().isString().withMessage('Background ID must be a string'),
    body('enableGestures').optional().isBoolean().withMessage('Enable gestures must be a boolean')
  ],
  validateRequest,
  async (req, res) => {
    try {
      const { id } = req.params;
      const {
        avatarId,
        voiceId,
        quality = 'medium',
        aspectRatio = '16:9',
        backgroundId,
        enableGestures = true
      } = req.body;

      // Check if broadcast exists
      const broadcast = await prisma.newsBroadcast.findUnique({
        where: { id },
        include: {
          articles: {
            include: {
              article: true
            }
          }
        }
      });

      if (!broadcast) {
        return res.status(404).json({
          success: false,
          message: 'Broadcast not found'
        });
      }

      // Start video generation (async process)
      const { generateTTSAudio } = await import('../controllers/newsController');
      generateTTSAudio(id, 'video').catch(error => {
        console.error('Video generation failed:', error);
      });

      res.json({
        success: true,
        message: 'Video generation started',
        data: {
          broadcastId: id,
          status: 'PREPARING'
        }
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        message: 'Error starting video generation'
      });
    }
  }
);

/**
 * @route POST /api/news/broadcasts/:id/generate-audio
 * @desc Generate audio for broadcast using ElevenLabs
 * @access Private (Moderator+)
 */
router.post('/broadcasts/:id/generate-audio',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isString().notEmpty().withMessage('Broadcast ID is required'),
    body('voiceId').optional().isString().withMessage('Voice ID must be a string'),
    body('model').optional().isString().withMessage('Model must be a string'),
    body('stability').optional().isFloat({ min: 0, max: 1 }).withMessage('Stability must be between 0 and 1'),
    body('clarity').optional().isFloat({ min: 0, max: 1 }).withMessage('Clarity must be between 0 and 1'),
    body('speed').optional().isFloat({ min: 0.25, max: 4.0 }).withMessage('Speed must be between 0.25 and 4.0')
  ],
  validateRequest,
  async (req, res) => {
    try {
      const { id } = req.params;

      // Check if broadcast exists
      const broadcast = await prisma.newsBroadcast.findUnique({
        where: { id }
      });

      if (!broadcast) {
        return res.status(404).json({
          success: false,
          message: 'Broadcast not found'
        });
      }

      // Start audio generation (async process)
      const { generateTTSAudio } = await import('../controllers/newsController');
      generateTTSAudio(id, 'audio').catch(error => {
        console.error('Audio generation failed:', error);
      });

      res.json({
        success: true,
        message: 'Audio generation started',
        data: {
          broadcastId: id,
          status: 'PREPARING'
        }
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        message: 'Error starting audio generation'
      });
    }
  }
);

/**
 * @route GET /api/news/analytics
 * @desc Get news analytics
 * @access Private (Moderator+)
 */
router.get('/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['1d', '7d', '30d', '90d']).withMessage('Invalid period'),
    query('avatarId').optional().isString().withMessage('Avatar ID must be a string')
  ],
  validateRequest,
  getNewsAnalytics
);

// ============================================================================
// NEWS SOURCES MANAGEMENT
// ============================================================================

/**
 * @route GET /api/news/sources
 * @desc Get all news sources
 * @access Private (Editor+)
 */
router.get('/sources',
  requireRole(['NEWS_EDITOR', 'BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  async (req, res) => {
    try {
      const sources = await prisma.newsSource.findMany({
        orderBy: { name: 'asc' },
        include: {
          _count: {
            select: {
              articles: true
            }
          }
        }
      });

      res.json({
        success: true,
        data: sources
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        message: 'Error fetching news sources'
      });
    }
  }
);

/**
 * @route POST /api/news/sources
 * @desc Create news source
 * @access Private (Moderator+)
 */
router.post('/sources',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    body('name').isString().isLength({ min: 2, max: 100 }).withMessage('Name must be between 2 and 100 characters'),
    body('description').optional().isString().isLength({ max: 500 }).withMessage('Description must not exceed 500 characters'),
    body('sourceType').isIn([
      'RSS_FEED', 'API_ENDPOINT', 'WEBHOOK', 'SOCIAL_MEDIA', 'PRESS_AGENCY', 'MANUAL'
    ]).withMessage('Invalid source type'),
    body('n8nWorkflowId').optional().isString().withMessage('N8N Workflow ID must be a string'),
    body('webhookUrl').optional().isURL().withMessage('Webhook URL must be valid'),
    body('apiEndpoint').optional().isURL().withMessage('API Endpoint must be valid'),
    body('keywords').optional().isArray().withMessage('Keywords must be an array'),
    body('categories').optional().isArray().withMessage('Categories must be an array'),
    body('languages').optional().isArray().withMessage('Languages must be an array'),
    body('priority').optional().isInt({ min: 1, max: 10 }).withMessage('Priority must be between 1 and 10')
  ],
  validateRequest,
  async (req, res) => {
    try {
      const source = await prisma.newsSource.create({
        data: req.body
      });

      res.status(201).json({
        success: true,
        data: source
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        message: 'Error creating news source'
      });
    }
  }
);

// ============================================================================
// WEBHOOK ENDPOINTS FOR N8N INTEGRATION
// ============================================================================

/**
 * @route POST /api/news/webhook/n8n
 * @desc Webhook endpoint for N8N to submit news articles
 * @access Public (with API key validation)
 */
router.post('/webhook/n8n',
  validateWebhookApiKey,
  [
    body('sourceId').isString().notEmpty().withMessage('Source ID is required'),
    body('title').isString().isLength({ min: 10, max: 200 }).withMessage('Title must be between 10 and 200 characters'),
    body('content').isString().isLength({ min: 50 }).withMessage('Content must be at least 50 characters'),
    body('originalUrl').optional().isURL().withMessage('Original URL must be valid'),
    body('imageUrl').optional().isURL().withMessage('Image URL must be valid'),
    body('category').isIn([
      'BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE', 
      'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL', 
      'LOCAL', 'WEATHER', 'OTHER'
    ]).withMessage('Invalid category'),
    body('priority').optional().isIn(['URGENT', 'HIGH', 'NORMAL', 'LOW']).withMessage('Invalid priority'),
    body('publishedAt').optional().isISO8601().withMessage('Published date must be valid ISO 8601 date')
  ],
  validateRequest,
  validateArticleData,
  async (req, res) => {
    try {
      // Create article with auto-approval for trusted sources
      const { sourceId, ...articleData } = req.body;
      
      // Check if source is trusted for auto-approval
      const source = await prisma.newsSource.findUnique({
        where: { id: sourceId }
      });

      if (!source) {
        return res.status(404).json({
          success: false,
          message: 'News source not found'
        });
      }

      // Format text for TTS
      const formattedText = await formatTextForTTS(articleData.content);
      const estimatedDuration = calculateDuration(formattedText);

      const article = await prisma.newsArticle.create({
        data: {
          sourceId,
          ...articleData,
          formattedText,
          duration: estimatedDuration,
          publishedAt: articleData.publishedAt ? new Date(articleData.publishedAt) : new Date(),
          status: source.priority >= 8 ? 'APPROVED' : 'PENDING' // Auto-approve high priority sources
        }
      });

      res.status(201).json({
        success: true,
        data: {
          id: article.id,
          status: article.status
        }
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        message: 'Error processing webhook'
      });
    }
  }
);

export default router;