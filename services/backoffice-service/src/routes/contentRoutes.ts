import { Router } from 'express';
import {
  getContent,
  getContentById,
  createContent,
  updateContent,
  deleteContent,
  restoreContent,
  syncContentFromService,
  bulkSyncContent,
  getContentAnalytics
} from '../controllers/contentController';
import {
  authenticate,
  requireRole,
  canManageContent
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/content
 * @desc Get all content with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('contentType').optional().isIn(['VIDEO', 'AUDIO', 'ARTICLE', 'COURSE', 'LIVE_STREAM']),
    query('sourceService').optional().isIn(['MAATTV', 'MAATCLASS', 'MAATTUBE', 'MAATCARE']),
    query('status').optional().isIn(['DRAFT', 'PUBLISHED', 'ARCHIVED', 'DELETED']),
    query('startDate').optional().isISO8601().withMessage('Invalid start date'),
    query('endDate').optional().isISO8601().withMessage('Invalid end date'),
    query('sortBy').optional().isIn(['title', 'createdAt', 'updatedAt', 'publishedAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getContent
);

/**
 * @route GET /api/content/:id
 * @desc Get content by ID
 * @access Private (Moderator+)
 */
router.get('/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid content ID')
  ],
  validateRequest,
  getContentById
);

/**
 * @route POST /api/content
 * @desc Create new content
 * @access Private (Content Manager+)
 */
router.post('/',
  canManageContent,
  [
    body('title').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Title is required and must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 2000 }).withMessage('Description must be less than 2000 characters'),
    body('contentType').isIn(['VIDEO', 'AUDIO', 'ARTICLE', 'COURSE', 'LIVE_STREAM']).withMessage('Invalid content type'),
    body('sourceService').isIn(['MAATTV', 'MAATCLASS', 'MAATTUBE', 'MAATCARE']).withMessage('Invalid source service'),
    body('sourceId').notEmpty().isString().trim().withMessage('Source ID is required'),
    body('status').optional().isIn(['DRAFT', 'PUBLISHED', 'ARCHIVED']).withMessage('Invalid status'),
    body('metadata').optional().isObject(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim(),
    body('thumbnailUrl').optional().isURL().withMessage('Invalid thumbnail URL'),
    body('duration').optional().isInt({ min: 0 }).withMessage('Duration must be a positive integer'),
    body('publishedAt').optional().isISO8601().withMessage('Invalid published date')
  ],
  validateRequest,
  createContent
);

/**
 * @route PUT /api/content/:id
 * @desc Update content
 * @access Private (Content Manager+)
 */
router.put('/:id',
  canManageContent,
  [
    param('id').isUUID().withMessage('Invalid content ID'),
    body('title').optional().trim().isLength({ min: 1, max: 255 }).withMessage('Title must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 2000 }).withMessage('Description must be less than 2000 characters'),
    body('contentType').optional().isIn(['VIDEO', 'AUDIO', 'ARTICLE', 'COURSE', 'LIVE_STREAM']).withMessage('Invalid content type'),
    body('status').optional().isIn(['DRAFT', 'PUBLISHED', 'ARCHIVED']).withMessage('Invalid status'),
    body('metadata').optional().isObject(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim(),
    body('thumbnailUrl').optional().isURL().withMessage('Invalid thumbnail URL'),
    body('duration').optional().isInt({ min: 0 }).withMessage('Duration must be a positive integer'),
    body('publishedAt').optional().isISO8601().withMessage('Invalid published date')
  ],
  validateRequest,
  updateContent
);

/**
 * @route DELETE /api/content/:id
 * @desc Soft delete content
 * @access Private (Content Manager+)
 */
router.delete('/:id',
  canManageContent,
  [
    param('id').isUUID().withMessage('Invalid content ID')
  ],
  validateRequest,
  deleteContent
);

/**
 * @route POST /api/content/:id/restore
 * @desc Restore soft deleted content
 * @access Private (Content Manager+)
 */
router.post('/:id/restore',
  canManageContent,
  [
    param('id').isUUID().withMessage('Invalid content ID')
  ],
  validateRequest,
  restoreContent
);

/**
 * @route POST /api/content/sync/:service/:sourceId
 * @desc Sync content from external service
 * @access Private (Content Manager+)
 */
router.post('/sync/:service/:sourceId',
  canManageContent,
  [
    param('service').isIn(['MAATTV', 'MAATCLASS', 'MAATTUBE', 'MAATCARE']).withMessage('Invalid service'),
    param('sourceId').notEmpty().isString().trim().withMessage('Source ID is required')
  ],
  validateRequest,
  syncContentFromService
);

/**
 * @route POST /api/content/bulk-sync
 * @desc Bulk sync content from external services
 * @access Private (Content Manager+)
 */
router.post('/bulk-sync',
  canManageContent,
  [
    body('service').isIn(['MAATTV', 'MAATCLASS', 'MAATTUBE', 'MAATCARE']).withMessage('Invalid service'),
    body('sourceIds').isArray({ min: 1, max: 50 }).withMessage('Source IDs must be an array with 1-50 items'),
    body('sourceIds.*').isString().trim().withMessage('Each source ID must be a string')
  ],
  validateRequest,
  bulkSyncContent
);

/**
 * @route GET /api/content/analytics
 * @desc Get content analytics
 * @access Private (Moderator+)
 */
router.get('/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period'),
    query('service').optional().isIn(['MAATTV', 'MAATCLASS', 'MAATTUBE', 'MAATCARE']).withMessage('Invalid service'),
    query('contentType').optional().isIn(['VIDEO', 'AUDIO', 'ARTICLE', 'COURSE', 'LIVE_STREAM']).withMessage('Invalid content type')
  ],
  validateRequest,
  getContentAnalytics
);

export default router;