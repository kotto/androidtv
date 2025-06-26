import { Router } from 'express';
import {
  getRSSFeeds,
  getRSSFeedById,
  createRSSFeed,
  updateRSSFeed,
  deleteRSSFeed,
  restoreRSSFeed,
  manualFetchRSSFeed,
  getRSSArticles,
  getRSSArticleById,
  factCheckArticle,
  generateAISummary,
  getRSSAnalytics
} from '../controllers/rssController';
import {
  authenticate,
  requireRole,
  canManageRSS
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/rss/feeds
 * @desc Get all RSS feeds with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/feeds',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('category').optional().isIn(['NEWS', 'TECHNOLOGY', 'SCIENCE', 'HEALTH', 'BUSINESS', 'ENTERTAINMENT', 'SPORTS', 'POLITICS', 'OTHER']),
    query('isActive').optional().isBoolean(),
    query('sortBy').optional().isIn(['name', 'createdAt', 'updatedAt', 'lastFetchedAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getRSSFeeds
);

/**
 * @route GET /api/rss/feeds/:id
 * @desc Get RSS feed by ID
 * @access Private (Moderator+)
 */
router.get('/feeds/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid RSS feed ID')
  ],
  validateRequest,
  getRSSFeedById
);

/**
 * @route POST /api/rss/feeds
 * @desc Create new RSS feed
 * @access Private (RSS Manager+)
 */
router.post('/feeds',
  canManageRSS,
  [
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters'),
    body('url').notEmpty().isURL().withMessage('Valid RSS URL is required'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('category').isIn(['NEWS', 'TECHNOLOGY', 'SCIENCE', 'HEALTH', 'BUSINESS', 'ENTERTAINMENT', 'SPORTS', 'POLITICS', 'OTHER']).withMessage('Invalid category'),
    body('fetchInterval').optional().isInt({ min: 300, max: 86400 }).withMessage('Fetch interval must be between 300 and 86400 seconds'),
    body('isActive').optional().isBoolean(),
    body('enableFactCheck').optional().isBoolean(),
    body('enableAISummary').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim(),
    body('customHeaders').optional().isObject(),
    body('authConfig').optional().isObject()
  ],
  validateRequest,
  createRSSFeed
);

/**
 * @route PUT /api/rss/feeds/:id
 * @desc Update RSS feed
 * @access Private (RSS Manager+)
 */
router.put('/feeds/:id',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid RSS feed ID'),
    body('name').optional().trim().isLength({ min: 1, max: 255 }).withMessage('Name must be between 1-255 characters'),
    body('url').optional().isURL().withMessage('Valid RSS URL is required'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('category').optional().isIn(['NEWS', 'TECHNOLOGY', 'SCIENCE', 'HEALTH', 'BUSINESS', 'ENTERTAINMENT', 'SPORTS', 'POLITICS', 'OTHER']).withMessage('Invalid category'),
    body('fetchInterval').optional().isInt({ min: 300, max: 86400 }).withMessage('Fetch interval must be between 300 and 86400 seconds'),
    body('isActive').optional().isBoolean(),
    body('enableFactCheck').optional().isBoolean(),
    body('enableAISummary').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim(),
    body('customHeaders').optional().isObject(),
    body('authConfig').optional().isObject()
  ],
  validateRequest,
  updateRSSFeed
);

/**
 * @route DELETE /api/rss/feeds/:id
 * @desc Soft delete RSS feed
 * @access Private (RSS Manager+)
 */
router.delete('/feeds/:id',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid RSS feed ID')
  ],
  validateRequest,
  deleteRSSFeed
);

/**
 * @route POST /api/rss/feeds/:id/restore
 * @desc Restore soft deleted RSS feed
 * @access Private (RSS Manager+)
 */
router.post('/feeds/:id/restore',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid RSS feed ID')
  ],
  validateRequest,
  restoreRSSFeed
);

/**
 * @route POST /api/rss/feeds/:id/fetch
 * @desc Manually fetch RSS feed
 * @access Private (RSS Manager+)
 */
router.post('/feeds/:id/fetch',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid RSS feed ID')
  ],
  validateRequest,
  manualFetchRSSFeed
);

/**
 * @route GET /api/rss/articles
 * @desc Get RSS articles with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/articles',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('feedId').optional().isUUID().withMessage('Invalid feed ID'),
    query('factCheckStatus').optional().isIn(['PENDING', 'VERIFIED', 'DISPUTED', 'FALSE']),
    query('hasAISummary').optional().isBoolean(),
    query('startDate').optional().isISO8601().withMessage('Invalid start date'),
    query('endDate').optional().isISO8601().withMessage('Invalid end date'),
    query('sortBy').optional().isIn(['title', 'publishedAt', 'createdAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getRSSArticles
);

/**
 * @route GET /api/rss/articles/:id
 * @desc Get RSS article by ID
 * @access Private (Moderator+)
 */
router.get('/articles/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid article ID')
  ],
  validateRequest,
  getRSSArticleById
);

/**
 * @route POST /api/rss/articles/:id/fact-check
 * @desc Fact check RSS article
 * @access Private (RSS Manager+)
 */
router.post('/articles/:id/fact-check',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid article ID')
  ],
  validateRequest,
  factCheckArticle
);

/**
 * @route POST /api/rss/articles/:id/ai-summary
 * @desc Generate AI summary for RSS article
 * @access Private (RSS Manager+)
 */
router.post('/articles/:id/ai-summary',
  canManageRSS,
  [
    param('id').isUUID().withMessage('Invalid article ID'),
    body('maxLength').optional().isInt({ min: 50, max: 1000 }).withMessage('Max length must be between 50 and 1000 characters'),
    body('style').optional().isIn(['BRIEF', 'DETAILED', 'BULLET_POINTS']).withMessage('Invalid summary style')
  ],
  validateRequest,
  generateAISummary
);

/**
 * @route GET /api/rss/analytics
 * @desc Get RSS analytics
 * @access Private (Moderator+)
 */
router.get('/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period'),
    query('feedId').optional().isUUID().withMessage('Invalid feed ID')
  ],
  validateRequest,
  getRSSAnalytics
);

export default router;