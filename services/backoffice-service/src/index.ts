import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import morgan from 'morgan';
import rateLimit from 'express-rate-limit';
import { createServer } from 'http';
import { Server as SocketIOServer } from 'socket.io';
import dotenv from 'dotenv';

import { errorHandler } from './middleware/errorHandler';
import { authMiddleware } from './middleware/auth';
import { logger } from './utils/logger';
import { prisma } from './utils/database';
import { redisClient } from './utils/redis';

// Import routes
import avatarRoutes from './routes/avatars';
import studioRoutes from './routes/studios';
import rssRoutes from './routes/rss';
import contentRoutes from './routes/content';
import workflowRoutes from './routes/workflows';
import authRoutes from './routes/auth';
import dashboardRoutes from './routes/dashboard';
import newsRoutes from './routes/news';

// Load environment variables
dotenv.config();

const app = express();
const server = createServer(app);
const io = new SocketIOServer(server, {
  cors: {
    origin: process.env.WS_CORS_ORIGIN?.split(',') || ['http://localhost:3000'],
    methods: ['GET', 'POST']
  }
});

const PORT = process.env.PORT || 3001;
const NODE_ENV = process.env.NODE_ENV || 'development';

// Rate limiting
const limiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS || '900000'), // 15 minutes
  max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS || '100'),
  message: 'Trop de requêtes depuis cette IP, veuillez réessayer plus tard.',
  standardHeaders: true,
  legacyHeaders: false,
});

// Middleware
app.use(helmet());
app.use(compression());
app.use(limiter);
app.use(cors({
  origin: process.env.CORS_ORIGIN?.split(',') || ['http://localhost:3000'],
  credentials: true
}));
app.use(morgan('combined', { stream: { write: (message) => logger.info(message.trim()) } }));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Health check endpoint
app.get('/health', async (req, res) => {
  try {
    // Check database connection
    await prisma.$queryRaw`SELECT 1`;
    
    // Check Redis connection
    await redisClient.ping();
    
    res.status(200).json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
      version: process.env.npm_package_version || '1.0.0',
      environment: NODE_ENV,
      services: {
        database: 'connected',
        redis: 'connected'
      }
    });
  } catch (error) {
    logger.error('Health check failed:', error);
    res.status(503).json({
      status: 'unhealthy',
      timestamp: new Date().toISOString(),
      error: 'Service unavailable'
    });
  }
});

// API Routes
const apiRouter = express.Router();

// Public routes
apiRouter.use('/auth', authRoutes);

// Protected routes
apiRouter.use('/avatars', authMiddleware, avatarRoutes);
apiRouter.use('/studios', authMiddleware, studioRoutes);
apiRouter.use('/rss', authMiddleware, rssRoutes);
apiRouter.use('/content', authMiddleware, contentRoutes);
apiRouter.use('/workflows', authMiddleware, workflowRoutes);
apiRouter.use('/dashboard', authMiddleware, dashboardRoutes);
apiRouter.use('/news', authMiddleware, newsRoutes);

app.use(`/api/${process.env.API_VERSION || 'v1'}`, apiRouter);

// WebSocket handling
io.use((socket, next) => {
  // Add authentication middleware for WebSocket if needed
  next();
});

io.on('connection', (socket) => {
  logger.info(`Client connected: ${socket.id}`);
  
  // Join rooms based on user permissions
  socket.on('join-room', (room) => {
    socket.join(room);
    logger.info(`Client ${socket.id} joined room: ${room}`);
  });
  
  // Handle avatar status updates
  socket.on('avatar-status-update', (data) => {
    socket.to('avatars').emit('avatar-status-changed', data);
  });
  
  // Handle studio status updates
  socket.on('studio-status-update', (data) => {
    socket.to('studios').emit('studio-status-changed', data);
  });
  
  // Handle workflow execution updates
  socket.on('workflow-execution-update', (data) => {
    socket.to('workflows').emit('workflow-execution-changed', data);
  });
  
  socket.on('disconnect', () => {
    logger.info(`Client disconnected: ${socket.id}`);
  });
});

// Make io available globally for other modules
app.set('io', io);

// Error handling middleware (must be last)
app.use(errorHandler);

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({
    error: 'Route non trouvée',
    path: req.originalUrl,
    method: req.method
  });
});

// Graceful shutdown
process.on('SIGTERM', async () => {
  logger.info('SIGTERM received, shutting down gracefully');
  
  server.close(() => {
    logger.info('HTTP server closed');
  });
  
  await prisma.$disconnect();
  await redisClient.quit();
  
  process.exit(0);
});

process.on('SIGINT', async () => {
  logger.info('SIGINT received, shutting down gracefully');
  
  server.close(() => {
    logger.info('HTTP server closed');
  });
  
  await prisma.$disconnect();
  await redisClient.quit();
  
  process.exit(0);
});

// Start server
server.listen(PORT, () => {
  logger.info(`🚀 MaâtCore Backoffice Service démarré sur le port ${PORT}`);
  logger.info(`📊 Dashboard disponible sur http://localhost:${PORT}/api/v1/dashboard`);
  logger.info(`🔧 Environnement: ${NODE_ENV}`);
});

export { app, io };