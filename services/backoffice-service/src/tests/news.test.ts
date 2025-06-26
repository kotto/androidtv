import request from 'supertest';
import { app } from '../index';
import { PrismaClient } from '@prisma/client';
import { generateToken } from '../utils/auth';
import { formatTextForTTS, calculateDuration } from '../utils/tts';

const prisma = new PrismaClient();

// Test user tokens
const adminToken = generateToken({ id: 'admin-1', role: 'ADMIN', email: 'admin@test.com' });
const editorToken = generateToken({ id: 'editor-1', role: 'EDITOR', email: 'editor@test.com' });
const userToken = generateToken({ id: 'user-1', role: 'USER', email: 'user@test.com' });

describe('News Module API Tests', () => {
  let testNewsSource: any;
  let testNewsArticle: any;
  let testAvatar: any;

  beforeAll(async () => {
    // Create test data
    testAvatar = await prisma.avatar.create({
      data: {
        name: 'Test News Avatar',
        description: 'Avatar for news testing',
        voiceId: 'test-voice-id',
        isActive: true
      }
    });

    testNewsSource = await prisma.newsSource.create({
      data: {
        name: 'Test News Source',
        description: 'Test source for unit tests',
        sourceType: 'MANUAL',
        keywords: ['test', 'news'],
        categories: ['TECHNOLOGY'],
        languages: ['fr'],
        priority: 5,
        isActive: true
      }
    });
  });

  afterAll(async () => {
    // Cleanup test data
    await prisma.newsBroadcast.deleteMany({ where: { avatarId: testAvatar.id } });
    await prisma.newsSchedule.deleteMany({ where: { avatarId: testAvatar.id } });
    await prisma.newsArticle.deleteMany({ where: { sourceId: testNewsSource.id } });
    await prisma.newsSource.delete({ where: { id: testNewsSource.id } });
    await prisma.avatar.delete({ where: { id: testAvatar.id } });
    await prisma.$disconnect();
  });

  describe('News Sources API', () => {
    test('GET /api/news/sources - should return news sources', async () => {
      const response = await request(app)
        .get('/api/news/sources')
        .set('Authorization', `Bearer ${adminToken}`)
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(Array.isArray(response.body.data)).toBe(true);
      expect(response.body.data.length).toBeGreaterThan(0);
    });

    test('POST /api/news/sources - should create news source (admin only)', async () => {
      const newSource = {
        name: 'Test API Source',
        description: 'Created via API test',
        sourceType: 'RSS_FEED',
        apiEndpoint: 'https://example.com/rss',
        keywords: ['api', 'test'],
        categories: ['TECHNOLOGY'],
        languages: ['fr'],
        priority: 7,
        isActive: true,
        syncInterval: 3600
      };

      const response = await request(app)
        .post('/api/news/sources')
        .set('Authorization', `Bearer ${adminToken}`)
        .send(newSource)
        .expect(201);

      expect(response.body.success).toBe(true);
      expect(response.body.data.name).toBe(newSource.name);

      // Cleanup
      await prisma.newsSource.delete({ where: { id: response.body.data.id } });
    });

    test('POST /api/news/sources - should deny access for non-admin', async () => {
      const newSource = {
        name: 'Unauthorized Source',
        sourceType: 'MANUAL'
      };

      await request(app)
        .post('/api/news/sources')
        .set('Authorization', `Bearer ${userToken}`)
        .send(newSource)
        .expect(403);
    });
  });

  describe('News Articles API', () => {
    beforeEach(async () => {
      testNewsArticle = await prisma.newsArticle.create({
        data: {
          sourceId: testNewsSource.id,
          title: 'Test Article',
          content: 'This is a test article content for testing purposes.',
          summary: 'Test article summary',
          category: 'TECHNOLOGY',
          priority: 'NORMAL',
          status: 'PENDING',
          factCheckStatus: 'PENDING',
          language: 'fr',
          formattedText: 'This is a test article content for testing purposes.',
          duration: 15
        }
      });
    });

    afterEach(async () => {
      if (testNewsArticle) {
        await prisma.newsArticle.deleteMany({ where: { id: testNewsArticle.id } });
      }
    });

    test('GET /api/news/articles - should return paginated articles', async () => {
      const response = await request(app)
        .get('/api/news/articles?page=1&limit=10')
        .set('Authorization', `Bearer ${editorToken}`)
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(Array.isArray(response.body.data)).toBe(true);
      expect(response.body.pagination).toBeDefined();
      expect(response.body.pagination.page).toBe(1);
      expect(response.body.pagination.limit).toBe(10);
    });

    test('POST /api/news/articles - should create new article', async () => {
      const newArticle = {
        sourceId: testNewsSource.id,
        title: 'New Test Article',
        content: 'Content of the new test article with sufficient length for testing.',
        summary: 'Summary of new article',
        category: 'TECHNOLOGY',
        priority: 'HIGH',
        language: 'fr',
        tags: ['test', 'api']
      };

      const response = await request(app)
        .post('/api/news/articles')
        .set('Authorization', `Bearer ${editorToken}`)
        .send(newArticle)
        .expect(201);

      expect(response.body.success).toBe(true);
      expect(response.body.data.title).toBe(newArticle.title);
      expect(response.body.data.status).toBe('PENDING');
      expect(response.body.data.formattedText).toBeDefined();
      expect(response.body.data.duration).toBeGreaterThan(0);

      // Cleanup
      await prisma.newsArticle.delete({ where: { id: response.body.data.id } });
    });

    test('PUT /api/news/articles/:id/approve - should approve article (admin only)', async () => {
      const response = await request(app)
        .put(`/api/news/articles/${testNewsArticle.id}/approve`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ factCheckStatus: 'VERIFIED' })
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.data.status).toBe('APPROVED');
      expect(response.body.data.factCheckStatus).toBe('VERIFIED');
      expect(response.body.data.approvedAt).toBeDefined();
    });

    test('PUT /api/news/articles/:id/approve - should deny access for non-admin', async () => {
      await request(app)
        .put(`/api/news/articles/${testNewsArticle.id}/approve`)
        .set('Authorization', `Bearer ${editorToken}`)
        .send({ factCheckStatus: 'VERIFIED' })
        .expect(403);
    });
  });

  describe('News Broadcasts API', () => {
    let testBroadcast: any;

    beforeEach(async () => {
      // Create approved article first
      const approvedArticle = await prisma.newsArticle.create({
        data: {
          sourceId: testNewsSource.id,
          title: 'Approved Test Article',
          content: 'This is an approved test article for broadcast testing.',
          summary: 'Approved article summary',
          category: 'TECHNOLOGY',
          priority: 'HIGH',
          status: 'APPROVED',
          factCheckStatus: 'VERIFIED',
          language: 'fr',
          formattedText: 'This is an approved test article for broadcast testing.',
          duration: 20,
          approvedAt: new Date(),
          approvedBy: 'admin'
        }
      });

      testBroadcast = await prisma.newsBroadcast.create({
        data: {
          avatarId: testAvatar.id,
          title: 'Test Broadcast',
          description: 'Test broadcast description',
          broadcastType: 'LIVE',
          scheduledAt: new Date(Date.now() + 3600000), // 1 hour from now
          status: 'SCHEDULED',
          articles: {
            connect: [{ id: approvedArticle.id }]
          }
        }
      });
    });

    afterEach(async () => {
      if (testBroadcast) {
        await prisma.newsBroadcast.delete({ where: { id: testBroadcast.id } });
      }
      await prisma.newsArticle.deleteMany({ 
        where: { 
          sourceId: testNewsSource.id,
          title: 'Approved Test Article'
        } 
      });
    });

    test('POST /api/news/broadcasts/schedule - should schedule broadcast', async () => {
      const broadcastData = {
        avatarId: testAvatar.id,
        title: 'Scheduled Test Broadcast',
        description: 'Test scheduled broadcast',
        broadcastType: 'RECORDED',
        scheduledAt: new Date(Date.now() + 7200000).toISOString(), // 2 hours from now
        articleIds: []
      };

      const response = await request(app)
        .post('/api/news/broadcasts/schedule')
        .set('Authorization', `Bearer ${adminToken}`)
        .send(broadcastData)
        .expect(201);

      expect(response.body.success).toBe(true);
      expect(response.body.data.title).toBe(broadcastData.title);
      expect(response.body.data.status).toBe('SCHEDULED');

      // Cleanup
      await prisma.newsBroadcast.delete({ where: { id: response.body.data.id } });
    });

    test('PUT /api/news/broadcasts/:id/status - should update broadcast status', async () => {
      const response = await request(app)
        .put(`/api/news/broadcasts/${testBroadcast.id}/status`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ status: 'LIVE' })
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.data.status).toBe('LIVE');
    });

    test('GET /api/news/broadcasts/next - should return next scheduled broadcast', async () => {
      const response = await request(app)
        .get(`/api/news/broadcasts/next?avatarId=${testAvatar.id}`)
        .set('Authorization', `Bearer ${editorToken}`)
        .expect(200);

      expect(response.body.success).toBe(true);
      if (response.body.data) {
        expect(response.body.data.avatarId).toBe(testAvatar.id);
        expect(['SCHEDULED', 'READY'].includes(response.body.data.status)).toBe(true);
      }
    });
  });

  describe('News Analytics API', () => {
    test('GET /api/news/analytics - should return analytics data', async () => {
      const response = await request(app)
        .get('/api/news/analytics?period=week')
        .set('Authorization', `Bearer ${adminToken}`)
        .expect(200);

      expect(response.body.success).toBe(true);
      expect(response.body.data).toBeDefined();
      expect(response.body.data.totalArticles).toBeDefined();
      expect(response.body.data.totalBroadcasts).toBeDefined();
      expect(response.body.data.categoryStats).toBeDefined();
      expect(response.body.data.sourceStats).toBeDefined();
    });

    test('GET /api/news/analytics - should deny access for non-admin', async () => {
      await request(app)
        .get('/api/news/analytics')
        .set('Authorization', `Bearer ${userToken}`)
        .expect(403);
    });
  });

  describe('N8N Webhook Integration', () => {
    test('POST /api/news/webhook/n8n - should process webhook with valid API key', async () => {
      const webhookData = {
        title: 'Webhook Test Article',
        content: 'This article was created via N8N webhook integration for testing purposes.',
        summary: 'Webhook test summary',
        category: 'TECHNOLOGY',
        priority: 'NORMAL',
        language: 'fr',
        sourceUrl: 'https://example.com/test-article',
        tags: ['webhook', 'n8n', 'test']
      };

      const response = await request(app)
        .post('/api/news/webhook/n8n')
        .set('X-API-Key', process.env.N8N_WEBHOOK_API_KEY || 'test-api-key')
        .send(webhookData)
        .expect(201);

      expect(response.body.success).toBe(true);
      expect(response.body.data.title).toBe(webhookData.title);
      expect(response.body.data.status).toBe('PENDING');

      // Cleanup
      await prisma.newsArticle.delete({ where: { id: response.body.data.id } });
    });

    test('POST /api/news/webhook/n8n - should reject invalid API key', async () => {
      const webhookData = {
        title: 'Invalid Webhook Test',
        content: 'This should be rejected',
        category: 'TECHNOLOGY'
      };

      await request(app)
        .post('/api/news/webhook/n8n')
        .set('X-API-Key', 'invalid-api-key')
        .send(webhookData)
        .expect(401);
    });
  });

  describe('TTS Utilities', () => {
    test('formatTextForTTS - should format text correctly', () => {
      const inputText = 'Voici un texte avec des "guillemets" et des caractères spéciaux : é, à, ç.';
      const formatted = formatTextForTTS(inputText);
      
      expect(formatted).toBeDefined();
      expect(formatted.length).toBeGreaterThan(0);
      expect(formatted).not.toContain('"');
    });

    test('calculateDuration - should calculate duration correctly', () => {
      const text = 'This is a test text with exactly twenty words to test the duration calculation function properly and accurately.';
      const duration = calculateDuration(text);
      
      expect(duration).toBeGreaterThan(0);
      expect(typeof duration).toBe('number');
      // Should be around 8-10 seconds for 20 words (150 WPM + buffer)
      expect(duration).toBeGreaterThan(5);
      expect(duration).toBeLessThan(20);
    });
  });

  describe('Authentication & Authorization', () => {
    test('should require authentication for protected routes', async () => {
      await request(app)
        .get('/api/news/articles')
        .expect(401);
    });

    test('should require proper role for admin routes', async () => {
      await request(app)
        .post('/api/news/sources')
        .set('Authorization', `Bearer ${userToken}`)
        .send({ name: 'Test', sourceType: 'MANUAL' })
        .expect(403);
    });

    test('should allow editor access to article management', async () => {
      await request(app)
        .get('/api/news/articles')
        .set('Authorization', `Bearer ${editorToken}`)
        .expect(200);
    });
  });

  describe('Input Validation', () => {
    test('should validate required fields for article creation', async () => {
      const invalidArticle = {
        title: '', // Empty title
        content: 'Short', // Too short content
        category: 'INVALID_CATEGORY' // Invalid category
      };

      await request(app)
        .post('/api/news/articles')
        .set('Authorization', `Bearer ${editorToken}`)
        .send(invalidArticle)
        .expect(400);
    });

    test('should validate broadcast scheduling data', async () => {
      const invalidBroadcast = {
        avatarId: 'invalid-uuid',
        title: '',
        scheduledAt: 'invalid-date'
      };

      await request(app)
        .post('/api/news/broadcasts/schedule')
        .set('Authorization', `Bearer ${adminToken}`)
        .send(invalidBroadcast)
        .expect(400);
    });
  });

  describe('Error Handling', () => {
    test('should handle non-existent article gracefully', async () => {
      await request(app)
        .get('/api/news/articles/non-existent-id')
        .set('Authorization', `Bearer ${editorToken}`)
        .expect(404);
    });

    test('should handle database errors gracefully', async () => {
      // This test would require mocking Prisma to simulate database errors
      // For now, we'll test a scenario that might cause a database constraint error
      const duplicateSource = {
        name: testNewsSource.name, // Duplicate name
        sourceType: 'MANUAL',
        keywords: ['test'],
        categories: ['TECHNOLOGY'],
        languages: ['fr'],
        priority: 5
      };

      await request(app)
        .post('/api/news/sources')
        .set('Authorization', `Bearer ${adminToken}`)
        .send(duplicateSource)
        .expect(400);
    });
  });
});

// Helper function to clean up test data
export const cleanupTestData = async () => {
  await prisma.newsBroadcast.deleteMany({ where: { title: { contains: 'Test' } } });
  await prisma.newsArticle.deleteMany({ where: { title: { contains: 'Test' } } });
  await prisma.newsSource.deleteMany({ where: { name: { contains: 'Test' } } });
  await prisma.avatar.deleteMany({ where: { name: { contains: 'Test' } } });
};