import winston from 'winston';
import path from 'path';

// Define log levels
const levels = {
  error: 0,
  warn: 1,
  info: 2,
  http: 3,
  debug: 4,
};

// Define colors for each level
const colors = {
  error: 'red',
  warn: 'yellow',
  info: 'green',
  http: 'magenta',
  debug: 'white',
};

// Tell winston that you want to link the colors
winston.addColors(colors);

// Define which logs to print based on environment
const level = () => {
  const env = process.env.NODE_ENV || 'development';
  const isDevelopment = env === 'development';
  return isDevelopment ? 'debug' : 'warn';
};

// Define different log formats
const formats = {
  // Format for console output
  console: winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss:ms' }),
    winston.format.colorize({ all: true }),
    winston.format.printf(
      (info) => `${info.timestamp} ${info.level}: ${info.message}`
    )
  ),

  // Format for file output
  file: winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss:ms' }),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),

  // Format for error files
  error: winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss:ms' }),
    winston.format.errors({ stack: true }),
    winston.format.json(),
    winston.format.prettyPrint()
  )
};

// Define transports
const transports = [
  // Console transport
  new winston.transports.Console({
    format: formats.console,
  }),

  // File transport for all logs
  new winston.transports.File({
    filename: path.join(process.cwd(), 'logs', 'all.log'),
    format: formats.file,
    maxsize: 5242880, // 5MB
    maxFiles: 5,
  }),

  // File transport for error logs
  new winston.transports.File({
    filename: path.join(process.cwd(), 'logs', 'error.log'),
    level: 'error',
    format: formats.error,
    maxsize: 5242880, // 5MB
    maxFiles: 5,
  }),

  // File transport for HTTP logs
  new winston.transports.File({
    filename: path.join(process.cwd(), 'logs', 'http.log'),
    level: 'http',
    format: formats.file,
    maxsize: 5242880, // 5MB
    maxFiles: 3,
  }),
];

// Create the logger
const logger = winston.createLogger({
  level: level(),
  levels,
  transports,
  // Handle uncaught exceptions
  exceptionHandlers: [
    new winston.transports.File({
      filename: path.join(process.cwd(), 'logs', 'exceptions.log'),
      format: formats.error,
    }),
  ],
  // Handle unhandled promise rejections
  rejectionHandlers: [
    new winston.transports.File({
      filename: path.join(process.cwd(), 'logs', 'rejections.log'),
      format: formats.error,
    }),
  ],
  // Exit on handled exceptions
  exitOnError: false,
});

// Create a stream object for Morgan HTTP logging
const stream = {
  write: (message: string) => {
    logger.http(message.trim());
  },
};

// Helper functions for structured logging
const logHelpers = {
  // Log API requests
  logRequest: (req: any, res: any, responseTime?: number) => {
    const logData = {
      method: req.method,
      url: req.url,
      statusCode: res.statusCode,
      userAgent: req.get('User-Agent'),
      ip: req.ip,
      userId: req.user?.id,
      responseTime: responseTime ? `${responseTime}ms` : undefined,
    };
    
    if (res.statusCode >= 400) {
      logger.warn('HTTP Request', logData);
    } else {
      logger.http('HTTP Request', logData);
    }
  },

  // Log database operations
  logDatabase: (operation: string, table: string, duration?: number, error?: any) => {
    const logData = {
      operation,
      table,
      duration: duration ? `${duration}ms` : undefined,
      error: error?.message,
    };

    if (error) {
      logger.error('Database Operation Failed', logData);
    } else {
      logger.debug('Database Operation', logData);
    }
  },

  // Log authentication events
  logAuth: (event: string, userId?: string, ip?: string, success: boolean = true) => {
    const logData = {
      event,
      userId,
      ip,
      success,
      timestamp: new Date().toISOString(),
    };

    if (success) {
      logger.info('Auth Event', logData);
    } else {
      logger.warn('Auth Event Failed', logData);
    }
  },

  // Log business logic events
  logBusiness: (event: string, data: any, userId?: string) => {
    const logData = {
      event,
      data,
      userId,
      timestamp: new Date().toISOString(),
    };

    logger.info('Business Event', logData);
  },

  // Log external API calls
  logExternalAPI: (service: string, endpoint: string, method: string, statusCode?: number, duration?: number, error?: any) => {
    const logData = {
      service,
      endpoint,
      method,
      statusCode,
      duration: duration ? `${duration}ms` : undefined,
      error: error?.message,
    };

    if (error || (statusCode && statusCode >= 400)) {
      logger.error('External API Call Failed', logData);
    } else {
      logger.info('External API Call', logData);
    }
  },

  // Log security events
  logSecurity: (event: string, severity: 'low' | 'medium' | 'high' | 'critical', details: any) => {
    const logData = {
      event,
      severity,
      details,
      timestamp: new Date().toISOString(),
    };

    switch (severity) {
      case 'critical':
      case 'high':
        logger.error('Security Event', logData);
        break;
      case 'medium':
        logger.warn('Security Event', logData);
        break;
      case 'low':
      default:
        logger.info('Security Event', logData);
        break;
    }
  },

  // Log performance metrics
  logPerformance: (metric: string, value: number, unit: string, context?: any) => {
    const logData = {
      metric,
      value,
      unit,
      context,
      timestamp: new Date().toISOString(),
    };

    logger.info('Performance Metric', logData);
  },

  // Log workflow events
  logWorkflow: (workflowId: string, step: string, status: 'started' | 'completed' | 'failed', data?: any, error?: any) => {
    const logData = {
      workflowId,
      step,
      status,
      data,
      error: error?.message,
      timestamp: new Date().toISOString(),
    };

    if (status === 'failed') {
      logger.error('Workflow Step', logData);
    } else {
      logger.info('Workflow Step', logData);
    }
  },
};

// Export logger and helpers
export { logger, stream, logHelpers };
export default logger;

// Log startup message
logger.info('🚀 Logger initialized', {
  level: level(),
  environment: process.env.NODE_ENV || 'development',
  timestamp: new Date().toISOString(),
});