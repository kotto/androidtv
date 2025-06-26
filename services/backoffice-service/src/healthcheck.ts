import axios from 'axios';
import { prisma } from './utils/database';
import { redisClient } from './utils/redis';

/**
 * Health check script for Docker container
 * This script is used by Docker's HEALTHCHECK instruction
 */
async function healthCheck() {
  try {
    const port = process.env.PORT || 3000;
    const host = process.env.HOST || 'localhost';
    
    // Check if the HTTP server is responding
    const response = await axios.get(`http://${host}:${port}/health`, {
      timeout: 3000
    });
    
    if (response.status !== 200) {
      throw new Error(`HTTP health check failed with status: ${response.status}`);
    }
    
    // Check database connection
    await prisma.healthCheck();
    
    // Check Redis connection
    await redisClient.ping();
    
    console.log('Health check passed');
    process.exit(0);
  } catch (error) {
    console.error('Health check failed:', error instanceof Error ? error.message : 'Unknown error');
    process.exit(1);
  }
}

// Run health check if this script is executed directly
if (require.main === module) {
  healthCheck();
}

export default healthCheck;