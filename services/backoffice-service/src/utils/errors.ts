import { Request, Response, NextFunction } from 'express';
import { logger, logHelpers } from './logger';

/**
 * Custom error classes
 */
export class AppError extends Error {
  public statusCode: number;
  public isOperational: boolean;
  public code?: string;
  public details?: any;

  constructor(
    message: string,
    statusCode: number = 500,
    isOperational: boolean = true,
    code?: string,
    details?: any
  ) {
    super(message);
    this.statusCode = statusCode;
    this.isOperational = isOperational;
    this.code = code;
    this.details = details;

    Error.captureStackTrace(this, this.constructor);
  }
}

export class ValidationError extends AppError {
  constructor(message: string, details?: any) {
    super(message, 400, true, 'VALIDATION_ERROR', details);
  }
}

export class AuthenticationError extends AppError {
  constructor(message: string = 'Authentication required') {
    super(message, 401, true, 'AUTHENTICATION_ERROR');
  }
}

export class AuthorizationError extends AppError {
  constructor(message: string = 'Insufficient permissions') {
    super(message, 403, true, 'AUTHORIZATION_ERROR');
  }
}

export class NotFoundError extends AppError {
  constructor(resource: string = 'Resource') {
    super(`${resource} not found`, 404, true, 'NOT_FOUND_ERROR');
  }
}

export class ConflictError extends AppError {
  constructor(message: string, details?: any) {
    super(message, 409, true, 'CONFLICT_ERROR', details);
  }
}

export class RateLimitError extends AppError {
  constructor(message: string = 'Rate limit exceeded') {
    super(message, 429, true, 'RATE_LIMIT_ERROR');
  }
}

export class ExternalServiceError extends AppError {
  constructor(service: string, message: string, details?: any) {
    super(`External service error: ${service} - ${message}`, 502, true, 'EXTERNAL_SERVICE_ERROR', details);
  }
}

export class DatabaseError extends AppError {
  constructor(message: string, details?: any) {
    super(`Database error: ${message}`, 500, true, 'DATABASE_ERROR', details);
  }
}

export class FileUploadError extends AppError {
  constructor(message: string, details?: any) {
    super(`File upload error: ${message}`, 400, true, 'FILE_UPLOAD_ERROR', details);
  }
}

export class WorkflowError extends AppError {
  constructor(workflowId: string, step: string, message: string, details?: any) {
    super(`Workflow ${workflowId} failed at step ${step}: ${message}`, 500, true, 'WORKFLOW_ERROR', {
      workflowId,
      step,
      ...details
    });
  }
}

/**
 * Error response formatter
 */
interface ErrorResponse {
  success: false;
  message: string;
  code?: string;
  details?: any;
  stack?: string;
  timestamp: string;
  path: string;
  method: string;
  requestId?: string;
}

/**
 * Format error response
 */
const formatErrorResponse = (error: AppError, req: Request): ErrorResponse => {
  const response: ErrorResponse = {
    success: false,
    message: error.message,
    timestamp: new Date().toISOString(),
    path: req.path,
    method: req.method
  };

  if (error.code) {
    response.code = error.code;
  }

  if (error.details) {
    response.details = error.details;
  }

  // Include stack trace in development
  if (process.env.NODE_ENV === 'development') {
    response.stack = error.stack;
  }

  // Include request ID if available
  if (req.headers['x-request-id']) {
    response.requestId = req.headers['x-request-id'] as string;
  }

  return response;
};

/**
 * Global error handler middleware
 */
export const errorHandler = (error: Error, req: Request, res: Response, next: NextFunction) => {
  let appError: AppError;

  // Convert known errors to AppError
  if (error instanceof AppError) {
    appError = error;
  } else if (error.name === 'ValidationError') {
    appError = new ValidationError(error.message);
  } else if (error.name === 'CastError') {
    appError = new ValidationError('Invalid data format');
  } else if (error.name === 'JsonWebTokenError') {
    appError = new AuthenticationError('Invalid token');
  } else if (error.name === 'TokenExpiredError') {
    appError = new AuthenticationError('Token expired');
  } else if (error.name === 'MulterError') {
    appError = new FileUploadError(error.message);
  } else {
    // Unknown error
    appError = new AppError(
      process.env.NODE_ENV === 'development' ? error.message : 'Internal server error',
      500,
      false
    );
  }

  // Log error
  if (appError.statusCode >= 500) {
    logger.error('Server Error:', {
      error: {
        message: appError.message,
        stack: appError.stack,
        code: appError.code,
        details: appError.details
      },
      request: {
        method: req.method,
        url: req.url,
        headers: req.headers,
        body: req.body,
        params: req.params,
        query: req.query,
        ip: req.ip,
        userAgent: req.get('User-Agent')
      },
      user: req.user
    });
  } else {
    logger.warn('Client Error:', {
      error: {
        message: appError.message,
        code: appError.code,
        details: appError.details
      },
      request: {
        method: req.method,
        url: req.url,
        ip: req.ip
      },
      user: req.user
    });
  }

  // Security logging for authentication/authorization errors
  if (appError instanceof AuthenticationError || appError instanceof AuthorizationError) {
    logHelpers.logSecurity(
      appError instanceof AuthenticationError ? 'Authentication failure' : 'Authorization failure',
      'medium',
      {
        message: appError.message,
        ip: req.ip,
        userAgent: req.get('User-Agent'),
        path: req.path,
        method: req.method,
        userId: req.user?.id
      }
    );
  }

  // Send error response
  const errorResponse = formatErrorResponse(appError, req);
  res.status(appError.statusCode).json(errorResponse);
};

/**
 * 404 handler middleware
 */
export const notFoundHandler = (req: Request, res: Response, next: NextFunction) => {
  const error = new NotFoundError(`Route ${req.method} ${req.path}`);
  next(error);
};

/**
 * Async error wrapper
 */
export const asyncHandler = (fn: Function) => {
  return (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
};

/**
 * Database error handler
 */
export const handleDatabaseError = (error: any): AppError => {
  if (error.code === 'P2002') {
    // Unique constraint violation
    const field = error.meta?.target?.[0] || 'field';
    return new ConflictError(`${field} already exists`, {
      field,
      constraint: error.meta?.target
    });
  }
  
  if (error.code === 'P2025') {
    // Record not found
    return new NotFoundError('Record');
  }
  
  if (error.code === 'P2003') {
    // Foreign key constraint violation
    return new ValidationError('Referenced record does not exist', {
      constraint: error.meta?.field_name
    });
  }
  
  if (error.code === 'P2014') {
    // Required relation violation
    return new ValidationError('Required relation missing', {
      relation: error.meta?.relation_name
    });
  }
  
  // Generic database error
  return new DatabaseError(error.message, {
    code: error.code,
    meta: error.meta
  });
};

/**
 * External service error handler
 */
export const handleExternalServiceError = (service: string, error: any): AppError => {
  if (error.response) {
    // HTTP error response
    return new ExternalServiceError(
      service,
      `HTTP ${error.response.status}: ${error.response.statusText}`,
      {
        status: error.response.status,
        data: error.response.data
      }
    );
  }
  
  if (error.request) {
    // Network error
    return new ExternalServiceError(
      service,
      'Network error - no response received',
      {
        timeout: error.timeout,
        code: error.code
      }
    );
  }
  
  // Other error
  return new ExternalServiceError(service, error.message);
};

/**
 * Validation helper functions
 */
export const throwIfNotFound = (resource: any, name: string = 'Resource'): void => {
  if (!resource) {
    throw new NotFoundError(name);
  }
};

export const throwIfExists = (resource: any, message: string): void => {
  if (resource) {
    throw new ConflictError(message);
  }
};

export const throwIfUnauthorized = (condition: boolean, message?: string): void => {
  if (!condition) {
    throw new AuthorizationError(message);
  }
};

export const throwIfInvalid = (condition: boolean, message: string, details?: any): void => {
  if (!condition) {
    throw new ValidationError(message, details);
  }
};

/**
 * Error codes enum
 */
export enum ErrorCodes {
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  AUTHENTICATION_ERROR = 'AUTHENTICATION_ERROR',
  AUTHORIZATION_ERROR = 'AUTHORIZATION_ERROR',
  NOT_FOUND_ERROR = 'NOT_FOUND_ERROR',
  CONFLICT_ERROR = 'CONFLICT_ERROR',
  RATE_LIMIT_ERROR = 'RATE_LIMIT_ERROR',
  EXTERNAL_SERVICE_ERROR = 'EXTERNAL_SERVICE_ERROR',
  DATABASE_ERROR = 'DATABASE_ERROR',
  FILE_UPLOAD_ERROR = 'FILE_UPLOAD_ERROR',
  WORKFLOW_ERROR = 'WORKFLOW_ERROR'
}

/**
 * HTTP status codes enum
 */
export enum HttpStatusCodes {
  OK = 200,
  CREATED = 201,
  NO_CONTENT = 204,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  CONFLICT = 409,
  UNPROCESSABLE_ENTITY = 422,
  TOO_MANY_REQUESTS = 429,
  INTERNAL_SERVER_ERROR = 500,
  BAD_GATEWAY = 502,
  SERVICE_UNAVAILABLE = 503
}