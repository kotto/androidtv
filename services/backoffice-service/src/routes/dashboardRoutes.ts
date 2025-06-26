import { Router } from 'express';
import {
  getDashboardOverview,
  getSystemHealth,
  getRecentActivities,
  getPerformanceMetrics,
  getAnalyticsSummary
} from '../controllers/dashboardController';
import {
  authenticate,
  requireRole
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { query } from 'express-validator';

const router = Router();

// Apply authentication to all routes
router.use(authenticate);

/**
 * @route GET /api/dashboard/overview
 * @desc Get dashboard overview with key metrics
 * @access Private (Moderator+)
 */
router.get('/overview',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period')
  ],
  validateRequest,
  getDashboardOverview
);

/**
 * @route GET /api/dashboard/health
 * @desc Get system health status
 * @access Private (Moderator+)
 */
router.get('/health',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  getSystemHealth
);

/**
 * @route GET /api/dashboard/activities
 * @desc Get recent activities
 * @access Private (Moderator+)
 */
router.get('/activities',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100')
  ],
  validateRequest,
  getRecentActivities
);

/**
 * @route GET /api/dashboard/performance
 * @desc Get performance metrics
 * @access Private (Moderator+)
 */
router.get('/performance',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['1h', '24h', '7d']).withMessage('Invalid period')
  ],
  validateRequest,
  getPerformanceMetrics
);

/**
 * @route GET /api/dashboard/analytics
 * @desc Get analytics summary
 * @access Private (Moderator+)
 */
router.get('/analytics',
  requireRole(['BACKOFFICE_MODERATOR', 'PLATFORM_ADMIN']),
  [
    query('period').optional().isIn(['7d', '30d', '90d']).withMessage('Invalid period')
  ],
  validateRequest,
  getAnalyticsSummary
);

export default router;