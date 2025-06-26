import { PrismaClient } from '@prisma/client';
import { logger } from './logger';

// Extend PrismaClient with custom methods if needed
class ExtendedPrismaClient extends PrismaClient {
  constructor() {
    super({
      log: [
        {
          emit: 'event',
          level: 'query',
        },
        {
          emit: 'event',
          level: 'error',
        },
        {
          emit: 'event',
          level: 'info',
        },
        {
          emit: 'event',
          level: 'warn',
        },
      ],
    });

    // Log database queries in development
    if (process.env.NODE_ENV === 'development') {
      this.$on('query', (e) => {
        logger.debug(`Query: ${e.query}`);
        logger.debug(`Params: ${e.params}`);
        logger.debug(`Duration: ${e.duration}ms`);
      });
    }

    this.$on('error', (e) => {
      logger.error('Database error:', e);
    });

    this.$on('info', (e) => {
      logger.info('Database info:', e.message);
    });

    this.$on('warn', (e) => {
      logger.warn('Database warning:', e.message);
    });
  }

  /**
   * Soft delete implementation
   */
  async softDelete(model: string, where: any) {
    const modelDelegate = (this as any)[model];
    if (!modelDelegate) {
      throw new Error(`Model ${model} not found`);
    }

    return modelDelegate.update({
      where,
      data: {
        deletedAt: new Date(),
        isActive: false
      }
    });
  }

  /**
   * Restore soft deleted record
   */
  async restore(model: string, where: any) {
    const modelDelegate = (this as any)[model];
    if (!modelDelegate) {
      throw new Error(`Model ${model} not found`);
    }

    return modelDelegate.update({
      where,
      data: {
        deletedAt: null,
        isActive: true
      }
    });
  }

  /**
   * Find many with pagination
   */
  async findManyWithPagination(
    model: string,
    {
      page = 1,
      limit = 10,
      where = {},
      orderBy = {},
      include = {}
    }: {
      page?: number;
      limit?: number;
      where?: any;
      orderBy?: any;
      include?: any;
    }
  ) {
    const modelDelegate = (this as any)[model];
    if (!modelDelegate) {
      throw new Error(`Model ${model} not found`);
    }

    const skip = (page - 1) * limit;

    const [data, total] = await Promise.all([
      modelDelegate.findMany({
        where,
        orderBy,
        include,
        skip,
        take: limit
      }),
      modelDelegate.count({ where })
    ]);

    const totalPages = Math.ceil(total / limit);
    const hasNextPage = page < totalPages;
    const hasPreviousPage = page > 1;

    return {
      data,
      pagination: {
        page,
        limit,
        total,
        totalPages,
        hasNextPage,
        hasPreviousPage
      }
    };
  }

  /**
   * Health check for database connection
   */
  async healthCheck(): Promise<boolean> {
    try {
      await this.$queryRaw`SELECT 1`;
      return true;
    } catch (error) {
      logger.error('Database health check failed:', error);
      return false;
    }
  }
}

// Create a single instance
const prisma = new ExtendedPrismaClient();

// Handle connection events
prisma.$connect()
  .then(() => {
    logger.info('✅ Database connected successfully');
  })
  .catch((error) => {
    logger.error('❌ Database connection failed:', error);
    process.exit(1);
  });

// Graceful shutdown
process.on('beforeExit', async () => {
  await prisma.$disconnect();
  logger.info('Database disconnected');
});

export { prisma };
export default prisma;