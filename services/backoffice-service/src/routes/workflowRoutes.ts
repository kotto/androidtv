import { Router } from 'express';
import {
  getWorkflows,
  getWorkflowById,
  createWorkflow,
  updateWorkflow,
  deleteWorkflow,
  restoreWorkflow,
  executeWorkflow,
  getWorkflowExecutions,
  getWorkflowAnalytics
} from '../controllers/workflowController';
import {
  authenticate,
  requireRole,
  canManageWorkflows
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/workflows
 * @desc Get all workflows with pagination and filtering
 * @access Private (Moderator+)
 */
router.get('/',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('search').optional().isString().trim(),
    query('category').optional().isIn(['CONTENT_PROCESSING', 'DATA_SYNC', 'NOTIFICATION', 'ANALYTICS', 'AUTOMATION', 'INTEGRATION']),
    query('isActive').optional().isBoolean(),
    query('sortBy').optional().isIn(['name', 'createdAt', 'updatedAt', 'lastExecutedAt']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getWorkflows
);

/**
 * @route GET /api/workflows/:id
 * @desc Get workflow by ID
 * @access Private (Moderator+)
 */
router.get('/:id',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid workflow ID')
  ],
  validateRequest,
  getWorkflowById
);

/**
 * @route POST /api/workflows
 * @desc Create new workflow
 * @access Private (Workflow Manager+)
 */
router.post('/',
  canManageWorkflows,
  [
    body('name').notEmpty().trim().isLength({ min: 1, max: 255 }).withMessage('Name is required and must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('category').isIn(['CONTENT_PROCESSING', 'DATA_SYNC', 'NOTIFICATION', 'ANALYTICS', 'AUTOMATION', 'INTEGRATION']).withMessage('Invalid category'),
    body('n8nWorkflowId').notEmpty().isString().trim().withMessage('n8n workflow ID is required'),
    body('triggerType').isIn(['MANUAL', 'SCHEDULED', 'WEBHOOK', 'EVENT']).withMessage('Invalid trigger type'),
    body('scheduleConfig').optional().isObject(),
    body('webhookConfig').optional().isObject(),
    body('eventConfig').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  createWorkflow
);

/**
 * @route PUT /api/workflows/:id
 * @desc Update workflow
 * @access Private (Workflow Manager+)
 */
router.put('/:id',
  canManageWorkflows,
  [
    param('id').isUUID().withMessage('Invalid workflow ID'),
    body('name').optional().trim().isLength({ min: 1, max: 255 }).withMessage('Name must be between 1-255 characters'),
    body('description').optional().isString().trim().isLength({ max: 1000 }).withMessage('Description must be less than 1000 characters'),
    body('category').optional().isIn(['CONTENT_PROCESSING', 'DATA_SYNC', 'NOTIFICATION', 'ANALYTICS', 'AUTOMATION', 'INTEGRATION']).withMessage('Invalid category'),
    body('triggerType').optional().isIn(['MANUAL', 'SCHEDULED', 'WEBHOOK', 'EVENT']).withMessage('Invalid trigger type'),
    body('scheduleConfig').optional().isObject(),
    body('webhookConfig').optional().isObject(),
    body('eventConfig').optional().isObject(),
    body('isActive').optional().isBoolean(),
    body('tags').optional().isArray(),
    body('tags.*').optional().isString().trim()
  ],
  validateRequest,
  updateWorkflow
);

/**
 * @route DELETE /api/workflows/:id
 * @desc Soft delete workflow
 * @access Private (Workflow Manager+)
 */
router.delete('/:id',
  canManageWorkflows,
  [
    param('id').isUUID().withMessage('Invalid workflow ID')
  ],
  validateRequest,
  deleteWorkflow
);

/**
 * @route POST /api/workflows/:id/restore
 * @desc Restore soft deleted workflow
 * @access Private (Workflow Manager+)
 */
router.post('/:id/restore',
  canManageWorkflows,
  [
    param('id').isUUID().withMessage('Invalid workflow ID')
  ],
  validateRequest,
  restoreWorkflow
);

/**
 * @route POST /api/workflows/:id/execute
 * @desc Execute workflow manually
 * @access Private (Workflow Manager+)
 */
router.post('/:id/execute',
  canManageWorkflows,
  [
    param('id').isUUID().withMessage('Invalid workflow ID'),
    body('inputData').optional().isObject(),
    body('waitForCompletion').optional().isBoolean()
  ],
  validateRequest,
  executeWorkflow
);

/**
 * @route GET /api/workflows/:id/executions
 * @desc Get workflow executions
 * @access Private (Moderator+)
 */
router.get('/:id/executions',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid workflow ID'),
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('status').optional().isIn(['RUNNING', 'SUCCESS', 'FAILED', 'CANCELLED']),
    query('startDate').optional().isISO8601().withMessage('Invalid start date'),
    query('endDate').optional().isISO8601().withMessage('Invalid end date'),
    query('sortBy').optional().isIn(['startedAt', 'finishedAt', 'status']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getWorkflowExecutions
);

/**
 * @route GET /api/workflows/:id/analytics
 * @desc Get workflow analytics
 * @access Private (Moderator+)
 */
router.get('/:id/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    param('id').isUUID().withMessage('Invalid workflow ID'),
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period')
  ],
  validateRequest,
  getWorkflowAnalytics
);

/**
 * @route GET /api/workflows/analytics
 * @desc Get overall workflow analytics
 * @access Private (Moderator+)
 */
router.get('/analytics/overview',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period'),
    query('category').optional().isIn(['CONTENT_PROCESSING', 'DATA_SYNC', 'NOTIFICATION', 'ANALYTICS', 'AUTOMATION', 'INTEGRATION']).withMessage('Invalid category')
  ],
  validateRequest,
  getWorkflowAnalytics
);

export default router;