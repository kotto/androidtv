import { createClient, RedisClientType } from 'redis';
import { logger } from './logger';

class RedisManager {
  private client: RedisClientType;
  private isConnected: boolean = false;

  constructor() {
    this.client = createClient({
      url: process.env.REDIS_URL || 'redis://localhost:6379',
      password: process.env.REDIS_PASSWORD || undefined,
      socket: {
        reconnectStrategy: (retries) => {
          if (retries > 10) {
            logger.error('Redis: Too many reconnection attempts, giving up');
            return new Error('Too many reconnection attempts');
          }
          return Math.min(retries * 50, 1000);
        }
      }
    });

    this.setupEventHandlers();
    this.connect();
  }

  private setupEventHandlers() {
    this.client.on('connect', () => {
      logger.info('✅ Redis client connected');
      this.isConnected = true;
    });

    this.client.on('ready', () => {
      logger.info('✅ Redis client ready');
    });

    this.client.on('error', (error) => {
      logger.error('❌ Redis client error:', error);
      this.isConnected = false;
    });

    this.client.on('end', () => {
      logger.info('Redis client disconnected');
      this.isConnected = false;
    });

    this.client.on('reconnecting', () => {
      logger.info('Redis client reconnecting...');
    });
  }

  private async connect() {
    try {
      await this.client.connect();
    } catch (error) {
      logger.error('Failed to connect to Redis:', error);
    }
  }

  /**
   * Get value from Redis
   */
  async get(key: string): Promise<string | null> {
    try {
      if (!this.isConnected) {
        logger.warn('Redis not connected, skipping get operation');
        return null;
      }
      return await this.client.get(key);
    } catch (error) {
      logger.error(`Redis GET error for key ${key}:`, error);
      return null;
    }
  }

  /**
   * Set value in Redis with optional expiration
   */
  async set(key: string, value: string, expireInSeconds?: number): Promise<boolean> {
    try {
      if (!this.isConnected) {
        logger.warn('Redis not connected, skipping set operation');
        return false;
      }

      if (expireInSeconds) {
        await this.client.setEx(key, expireInSeconds, value);
      } else {
        await this.client.set(key, value);
      }
      return true;
    } catch (error) {
      logger.error(`Redis SET error for key ${key}:`, error);
      return false;
    }
  }

  /**
   * Delete key from Redis
   */
  async del(key: string): Promise<boolean> {
    try {
      if (!this.isConnected) {
        logger.warn('Redis not connected, skipping delete operation');
        return false;
      }
      const result = await this.client.del(key);
      return result > 0;
    } catch (error) {
      logger.error(`Redis DEL error for key ${key}:`, error);
      return false;
    }
  }

  /**
   * Check if key exists
   */
  async exists(key: string): Promise<boolean> {
    try {
      if (!this.isConnected) {
        return false;
      }
      const result = await this.client.exists(key);
      return result === 1;
    } catch (error) {
      logger.error(`Redis EXISTS error for key ${key}:`, error);
      return false;
    }
  }

  /**
   * Set expiration for a key
   */
  async expire(key: string, seconds: number): Promise<boolean> {
    try {
      if (!this.isConnected) {
        return false;
      }
      const result = await this.client.expire(key, seconds);
      return result;
    } catch (error) {
      logger.error(`Redis EXPIRE error for key ${key}:`, error);
      return false;
    }
  }

  /**
   * Get JSON object from Redis
   */
  async getJSON<T>(key: string): Promise<T | null> {
    try {
      const value = await this.get(key);
      if (!value) return null;
      return JSON.parse(value) as T;
    } catch (error) {
      logger.error(`Redis JSON GET error for key ${key}:`, error);
      return null;
    }
  }

  /**
   * Set JSON object in Redis
   */
  async setJSON<T>(key: string, value: T, expireInSeconds?: number): Promise<boolean> {
    try {
      const jsonString = JSON.stringify(value);
      return await this.set(key, jsonString, expireInSeconds);
    } catch (error) {
      logger.error(`Redis JSON SET error for key ${key}:`, error);
      return false;
    }
  }

  /**
   * Increment a counter
   */
  async incr(key: string): Promise<number | null> {
    try {
      if (!this.isConnected) {
        return null;
      }
      return await this.client.incr(key);
    } catch (error) {
      logger.error(`Redis INCR error for key ${key}:`, error);
      return null;
    }
  }

  /**
   * Add to a set
   */
  async sadd(key: string, ...members: string[]): Promise<number | null> {
    try {
      if (!this.isConnected) {
        return null;
      }
      return await this.client.sAdd(key, members);
    } catch (error) {
      logger.error(`Redis SADD error for key ${key}:`, error);
      return null;
    }
  }

  /**
   * Get all members of a set
   */
  async smembers(key: string): Promise<string[]> {
    try {
      if (!this.isConnected) {
        return [];
      }
      return await this.client.sMembers(key);
    } catch (error) {
      logger.error(`Redis SMEMBERS error for key ${key}:`, error);
      return [];
    }
  }

  /**
   * Remove from a set
   */
  async srem(key: string, ...members: string[]): Promise<number | null> {
    try {
      if (!this.isConnected) {
        return null;
      }
      return await this.client.sRem(key, members);
    } catch (error) {
      logger.error(`Redis SREM error for key ${key}:`, error);
      return null;
    }
  }

  /**
   * Ping Redis server
   */
  async ping(): Promise<string | null> {
    try {
      if (!this.isConnected) {
        return null;
      }
      return await this.client.ping();
    } catch (error) {
      logger.error('Redis PING error:', error);
      return null;
    }
  }

  /**
   * Flush all data (use with caution!)
   */
  async flushAll(): Promise<boolean> {
    try {
      if (!this.isConnected) {
        return false;
      }
      await this.client.flushAll();
      return true;
    } catch (error) {
      logger.error('Redis FLUSHALL error:', error);
      return false;
    }
  }

  /**
   * Get Redis client for advanced operations
   */
  getClient(): RedisClientType {
    return this.client;
  }

  /**
   * Check if Redis is connected
   */
  isRedisConnected(): boolean {
    return this.isConnected;
  }

  /**
   * Graceful shutdown
   */
  async quit(): Promise<void> {
    try {
      if (this.isConnected) {
        await this.client.quit();
        logger.info('Redis client disconnected gracefully');
      }
    } catch (error) {
      logger.error('Error during Redis shutdown:', error);
    }
  }
}

// Create a single instance
const redisManager = new RedisManager();

// Export both the manager and the client for convenience
export const redisClient = redisManager;
export default redisManager;

// Cache key generators
export const CacheKeys = {
  avatar: (id: string) => `avatar:${id}`,
  avatarSession: (id: string) => `avatar:session:${id}`,
  studio: (id: string) => `studio:${id}`,
  rssArticle: (id: string) => `rss:article:${id}`,
  content: (id: string) => `content:${id}`,
  workflow: (id: string) => `workflow:${id}`,
  userSession: (userId: string) => `user:session:${userId}`,
  rateLimitUser: (userId: string) => `rate_limit:user:${userId}`,
  rateLimitIP: (ip: string) => `rate_limit:ip:${ip}`
};