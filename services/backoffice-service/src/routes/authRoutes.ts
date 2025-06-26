import { Router } from 'express';
import {
  login,
  logout,
  getUserProfile,
  getUserPermissions,
  refreshToken,
  validateSession,
  revokeSession,
  changePassword,
  getAuthLogs
} from '../controllers/authController';
import {
  authenticate,
  requireRole,
  optionalAuth
} from '../middleware/auth';
import { validateRequest } from '../middleware/validation';
import { body, param, query } from 'express-validator';

const router = Router();

/**
 * @route POST /api/auth/login
 * @desc Login user
 * @access Public
 */
router.post('/login',
  [
    body('email').isEmail().normalizeEmail().withMessage('Valid email is required'),
    body('password').notEmpty().withMessage('Password is required'),
    body('rememberMe').optional().isBoolean()
  ],
  validateRequest,
  login
);

/**
 * @route POST /api/auth/logout
 * @desc Logout user
 * @access Private
 */
router.post('/logout',
  authenticate,
  logout
);

/**
 * @route GET /api/auth/profile
 * @desc Get user profile
 * @access Private
 */
router.get('/profile',
  authenticate,
  getUserProfile
);

/**
 * @route GET /api/auth/permissions
 * @desc Get user permissions
 * @access Private
 */
router.get('/permissions',
  authenticate,
  getUserPermissions
);

/**
 * @route POST /api/auth/refresh
 * @desc Refresh access token
 * @access Public
 */
router.post('/refresh',
  [
    body('refreshToken').notEmpty().withMessage('Refresh token is required')
  ],
  validateRequest,
  refreshToken
);

/**
 * @route GET /api/auth/validate
 * @desc Validate session
 * @access Private
 */
router.get('/validate',
  authenticate,
  validateSession
);

/**
 * @route POST /api/auth/revoke-session
 * @desc Revoke user session
 * @access Private
 */
router.post('/revoke-session',
  authenticate,
  [
    body('sessionId').optional().isString().trim().withMessage('Session ID must be a string')
  ],
  validateRequest,
  revokeSession
);

/**
 * @route POST /api/auth/change-password
 * @desc Change user password
 * @access Private
 */
router.post('/change-password',
  authenticate,
  [
    body('currentPassword').notEmpty().withMessage('Current password is required'),
    body('newPassword').isLength({ min: 8 }).withMessage('New password must be at least 8 characters long'),
    body('confirmPassword').custom((value, { req }) => {
      if (value !== req.body.newPassword) {
        throw new Error('Password confirmation does not match');
      }
      return true;
    })
  ],
  validateRequest,
  changePassword
);

/**
 * @route GET /api/auth/logs
 * @desc Get authentication logs
 * @access Private (Admin only)
 */
router.get('/logs',
  requireRole(['PLATFORM_ADMIN']),
  [
    query('page').optional().isInt({ min: 1 }).withMessage('Page must be a positive integer'),
    query('limit').optional().isInt({ min: 1, max: 100 }).withMessage('Limit must be between 1 and 100'),
    query('userId').optional().isUUID().withMessage('Invalid user ID'),
    query('action').optional().isIn(['LOGIN', 'LOGOUT', 'TOKEN_REFRESH', 'PASSWORD_CHANGE', 'SESSION_REVOKE']),
    query('startDate').optional().isISO8601().withMessage('Invalid start date'),
    query('endDate').optional().isISO8601().withMessage('Invalid end date'),
    query('sortBy').optional().isIn(['timestamp', 'action', 'userId']),
    query('sortOrder').optional().isIn(['asc', 'desc'])
  ],
  validateRequest,
  getAuthLogs
);

/**
 * @route GET /api/auth/check
 * @desc Check authentication status (optional auth)
 * @access Public/Private
 */
router.get('/check',
  optionalAuth,
  (req, res) => {
    res.json({
      success: true,
      data: {
        authenticated: !!req.user,
        user: req.user || null
      }
    });
  }
);

export default router;