import { Router } from 'express';
import {
  getStudios,
  getStudioById,
  createStudio,
  updateStudio,
  deleteStudio,
  restoreStudio,
  testStudioRendering,
  getStudioRenderingStatus,
  getStudioAnalytics,
  duplicateStudio
} from '../controllers/studioController';
import {
  authenticate,
  requireRole,
  canManageStudios
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/studios
 * @desc Get all studios with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('type').optional().isIn(['NEWS_STUDIO', 'TALK_SHOW', 'CLASSROOM', 'PODCAST', 'INTERVIEW', 'PRESENTATION']),
    query('isActive').optional().isBoolean(),
    query('sortBy').optional().isIn(['name', 'createdAt', 'updatedAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getStudios
);

/**
 * @route GET /api/studios/:id
 * @desc Get studio by ID
 * @access Private (Moderator+)
 */
router.get('/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid studio ID')
  ],
  validateRequest,
  getStudioById
);

/**
 * @route POST /api/studios
 * @desc Create new studio
 * @access Private (Studio Manager+)
 */
router.post('/',
  canManageStudios,
  [
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('type').isIn(['NEWS_STUDIO', 'TALK_SHOW', 'CLASSROOM', 'PODCAST', 'INTERVIEW', 'PRESENTATION']).withMessage('Invalid studio type'),
    body('unrealProjectPath').notEmpty().isString().trim().withMessage('Unreal project path is required'),
    body('environmentSettings').optional().isObject(),
    body('lightingSettings').optional().isObject(),
    body('cameraSettings').optional().isObject(),
    body('audioSettings').optional().isObject(),
    body('renderSettings').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  createStudio
);

/**
 * @route PUT /api/studios/:id
 * @desc Update studio
 * @access Private (Studio Manager+)
 */
router.put('/:id',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID'),
    body('name').optional().trim().isLength({ min: 1, max: 255 }).withMessage('Name must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('type').optional().isIn(['NEWS_STUDIO', 'TALK_SHOW', 'CLASSROOM', 'PODCAST', 'INTERVIEW', 'PRESENTATION']).withMessage('Invalid studio type'),
    body('unrealProjectPath').optional().isString().trim().withMessage('Unreal project path must be a string'),
    body('environmentSettings').optional().isObject(),
    body('lightingSettings').optional().isObject(),
    body('cameraSettings').optional().isObject(),
    body('audioSettings').optional().isObject(),
    body('renderSettings').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  updateStudio
);

/**
 * @route DELETE /api/studios/:id
 * @desc Soft delete studio
 * @access Private (Studio Manager+)
 */
router.delete('/:id',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID')
  ],
  validateRequest,
  deleteStudio
);

/**
 * @route POST /api/studios/:id/restore
 * @desc Restore soft deleted studio
 * @access Private (Studio Manager+)
 */
router.post('/:id/restore',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID')
  ],
  validateRequest,
  restoreStudio
);

/**
 * @route POST /api/studios/:id/test-rendering
 * @desc Test studio rendering
 * @access Private (Studio Manager+)
 */
router.post('/:id/test-rendering',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID'),
    body('duration').optional().isInt({ min: 1, max: 300 }).withMessage('Duration must be between 1 and 300 seconds'),
    body('quality').optional().isIn(['LOW', 'MEDIUM', 'HIGH', 'ULTRA']).withMessage('Invalid quality setting'),
    body('resolution').optional().isIn(['720p', '1080p', '4K']).withMessage('Invalid resolution')
  ],
  validateRequest,
  testStudioRendering
);

/**
 * @route GET /api/studios/:id/rendering-status/:jobId
 * @desc Get studio rendering status
 * @access Private (Studio Manager+)
 */
router.get('/:id/rendering-status/:jobId',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID'),
    param('jobId').isString().trim().withMessage('Job ID is required')
  ],
  validateRequest,
  getStudioRenderingStatus
);

/**
 * @route GET /api/studios/:id/analytics
 * @desc Get studio analytics
 * @access Private (Moderator+)
 */
router.get('/:id/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid studio ID'),
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period')
  ],
  validateRequest,
  getStudioAnalytics
);

/**
 * @route POST /api/studios/:id/duplicate
 * @desc Duplicate studio
 * @access Private (Studio Manager+)
 */
router.post('/:id/duplicate',
  canManageStudios,
  [
    param('id').isUUID().withMessage('Invalid studio ID'),
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters')
  ],
  validateRequest,
  duplicateStudio
);

export default router;