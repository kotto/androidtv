import { Router } from 'express';
import {
  getAvatars,
  getAvatarById,
  createAvatar,
  updateAvatar,
  deleteAvatar,
  restoreAvatar,
  getAvatarSessions,
  getAvatarAnalytics,
  duplicateAvatar
} from '../controllers/avatarController';
import {
  authenticate,
  requireRole,
  canManageAvatars
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/avatars
 * @desc Get all avatars with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('type').optional().isIn(['REALISTIC', 'CARTOON', 'ANIME', 'ABSTRACT']),
    query('isActive').optional().isBoolean(),
    query('sortBy').optional().isIn(['name', 'createdAt', 'updatedAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getAvatars
);

/**
 * @route GET /api/avatars/:id
 * @desc Get avatar by ID
 * @access Private (Moderator+)
 */
router.get('/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid avatar ID')
  ],
  validateRequest,
  getAvatarById
);

/**
 * @route POST /api/avatars
 * @desc Create new avatar
 * @access Private (Avatar Manager+)
 */
router.post('/',
  canManageAvatars,
  [
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('type').isIn(['REALISTIC', 'CARTOON', 'ANIME', 'ABSTRACT']).withMessage('Invalid avatar type'),
    body('gender').optional().isIn(['MALE', 'FEMALE', 'NON_BINARY']).withMessage('Invalid gender'),
    body('age').optional().isInt({ min: 1, max: 150 }).withMessage('Age must be between 1 and 150'),
    body('ethnicity').optional().isString().trim().isLength({ max: 100 }).withMessage('Ethnicity must be less than 100 characters'),
    body('voiceSettings').optional().isObject(),
    body('appearanceSettings').optional().isObject(),
    body('behaviorSettings').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  createAvatar
);

/**
 * @route PUT /api/avatars/:id
 * @desc Update avatar
 * @access Private (Avatar Manager+)
 */
router.put('/:id',
  canManageAvatars,
  [
    param('id').isUUID().withMessage('Invalid avatar ID'),
    body('name').optional().trim().isLength({ min: 1, max: 255 }).withMessage('Name must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('type').optional().isIn(['REALISTIC', 'CARTOON', 'ANIME', 'ABSTRACT']).withMessage('Invalid avatar type'),
    body('gender').optional().isIn(['MALE', 'FEMALE', 'NON_BINARY']).withMessage('Invalid gender'),
    body('age').optional().isInt({ min: 1, max: 150 }).withMessage('Age must be between 1 and 150'),
    body('ethnicity').optional().isString().trim().isLength({ max: 100 }).withMessage('Ethnicity must be less than 100 characters'),
    body('voiceSettings').optional().isObject(),
    body('appearanceSettings').optional().isObject(),
    body('behaviorSettings').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  updateAvatar
);

/**
 * @route DELETE /api/avatars/:id
 * @desc Soft delete avatar
 * @access Private (Avatar Manager+)
 */
router.delete('/:id',
  canManageAvatars,
  [
    param('id').isUUID().withMessage('Invalid avatar ID')
  ],
  validateRequest,
  deleteAvatar
);

/**
 * @route POST /api/avatars/:id/restore
 * @desc Restore soft deleted avatar
 * @access Private (Avatar Manager+)
 */
router.post('/:id/restore',
  canManageAvatars,
  [
    param('id').isUUID().withMessage('Invalid avatar ID')
  ],
  validateRequest,
  restoreAvatar
);

/**
 * @route GET /api/avatars/:id/sessions
 * @desc Get avatar sessions
 * @access Private (Moderator+)
 */
router.get('/:id/sessions',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid avatar ID'),
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('startDate').optional().isISO8601().withMessage('Invalid start date'),
    query('endDate').optional().isISO8601().withMessage('Invalid end date')
  ],
  validateRequest,
  getAvatarSessions
);

/**
 * @route GET /api/avatars/:id/analytics
 * @desc Get avatar analytics
 * @access Private (Moderator+)
 */
router.get('/:id/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid avatar ID'),
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period')
  ],
  validateRequest,
  getAvatarAnalytics
);

/**
 * @route POST /api/avatars/:id/duplicate
 * @desc Duplicate avatar
 * @access Private (Avatar Manager+)
 */
router.post('/:id/duplicate',
  canManageAvatars,
  [
    param('id').isUUID().withMessage('Invalid avatar ID'),
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters')
  ],
  validateRequest,
  duplicateAvatar
);

export default router;