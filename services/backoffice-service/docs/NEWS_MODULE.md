# News Module Documentation

## Overview

The News Module is a comprehensive system for automated news collection, editorial validation, and virtual avatar broadcasting within the MaâtCore ecosystem. It integrates with N8N workflows for content aggregation and provides a complete pipeline from news source to avatar presentation.

## Architecture

### Core Components

1. **News Sources Management**
   - RSS feeds integration
   - API endpoints
   - Webhooks for real-time updates
   - Social media monitoring
   - Press agency feeds
   - Manual content entry

2. **Editorial Workflow**
   - Automated content ingestion
   - Editorial validation and approval
   - Fact-checking integration
   - Content categorization
   - Priority management

3. **Avatar Broadcasting**
   - TTS (Text-to-Speech) generation
   - Avatar video synthesis
   - Scheduling system
   - Live and recorded broadcasts
   - Multi-language support

4. **Analytics & Monitoring**
   - Broadcast performance metrics
   - Content engagement tracking
   - Source reliability scoring
   - Editorial workflow analytics

## Data Models

### NewsSource
Manages different types of news sources and their configurations.

```typescript
interface NewsSource {
  id: string;
  name: string;
  description?: string;
  sourceType: 'RSS_FEED' | 'API_ENDPOINT' | 'WEBHOOK' | 'SOCIAL_MEDIA' | 'PRESS_AGENCY' | 'MANUAL';
  n8nWorkflowId?: string;
  webhookUrl?: string;
  apiEndpoint?: string;
  keywords: string[];
  categories: NewsCategory[];
  languages: string[];
  priority: number; // 1-10, higher = more trusted
  isActive: boolean;
  syncInterval?: number; // seconds
}
```

### NewsArticle
Represents individual news articles with editorial metadata.

```typescript
interface NewsArticle {
  id: string;
  sourceId: string;
  title: string;
  content: string;
  summary?: string;
  formattedText?: string; // Formatted for TTS
  originalUrl?: string;
  imageUrl?: string;
  category: NewsCategory;
  priority: 'URGENT' | 'HIGH' | 'NORMAL' | 'LOW';
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SCHEDULED' | 'BROADCASTING' | 'BROADCASTED' | 'ARCHIVED';
  factCheckStatus: 'PENDING' | 'VERIFIED' | 'DISPUTED' | 'FALSE' | 'MIXED';
  language: string;
  duration?: number; // Estimated TTS duration in seconds
  publishedAt: Date;
  approvedAt?: Date;
  approvedBy?: string;
}
```

### NewsBroadcast
Manages avatar broadcasts of news content.

```typescript
interface NewsBroadcast {
  id: string;
  articleId: string;
  avatarId: string;
  broadcastType: 'LIVE' | 'RECORDED' | 'SCHEDULED';
  status: 'SCHEDULED' | 'PREPARING' | 'READY' | 'BROADCASTING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  scheduledAt: Date;
  startedAt?: Date;
  endedAt?: Date;
  duration?: number;
  viewCount?: number;
  audioUrl?: string; // Generated TTS audio
  videoUrl?: string; // Generated avatar video
  thumbnailUrl?: string;
}
```

### NewsSchedule
Defines broadcasting schedules for avatars.

```typescript
interface NewsSchedule {
  id: string;
  avatarId: string;
  name: string;
  description?: string;
  isActive: boolean;
  timeSlots: TimeSlot[]; // [{day: 'monday', time: '08:00', duration: 300}]
  categories: NewsCategory[];
  priority: NewsPriority[];
  maxDuration?: number;
}
```

## API Endpoints

### News Articles

- `GET /api/news/articles` - List articles with filtering
- `GET /api/news/articles/:id` - Get article by ID
- `POST /api/news/articles` - Create new article
- `PUT /api/news/articles/:id` - Update article
- `POST /api/news/articles/:id/approve` - Approve article for broadcast

### News Sources

- `GET /api/news/sources` - List all news sources
- `POST /api/news/sources` - Create news source
- `PUT /api/news/sources/:id` - Update news source
- `DELETE /api/news/sources/:id` - Delete news source

### Broadcasts

- `POST /api/news/broadcasts` - Schedule news broadcast
- `GET /api/news/next/:avatarId` - Get next news for avatar
- `PUT /api/news/broadcasts/:id/status` - Update broadcast status

### Analytics

- `GET /api/news/analytics` - Get news analytics

### Webhooks

- `POST /api/news/webhook/n8n` - N8N webhook endpoint

## N8N Integration

### Workflow Setup

1. **RSS Feed Monitoring**
   ```json
   {
     "nodes": [
       {
         "type": "RSS Feed Trigger",
         "parameters": {
           "url": "https://example.com/rss",
           "pollInterval": 3600
         }
       },
       {
         "type": "HTTP Request",
         "parameters": {
           "url": "http://backoffice:3001/api/news/webhook/n8n",
           "method": "POST",
           "headers": {
             "x-api-key": "${N8N_WEBHOOK_API_KEY}"
           }
         }
       }
     ]
   }
   ```

2. **Social Media Monitoring**
   ```json
   {
     "nodes": [
       {
         "type": "Twitter Trigger",
         "parameters": {
           "keywords": ["breaking news", "urgent"]
         }
       },
       {
         "type": "Content Filter",
         "parameters": {
           "minLength": 50,
           "language": "fr"
         }
       },
       {
         "type": "HTTP Request",
         "parameters": {
           "url": "http://backoffice:3001/api/news/webhook/n8n"
         }
       }
     ]
   }
   ```

### Webhook Payload

```json
{
  "sourceId": "source-uuid",
  "title": "Article Title",
  "content": "Full article content...",
  "summary": "Brief summary",
  "originalUrl": "https://source.com/article",
  "imageUrl": "https://source.com/image.jpg",
  "category": "BREAKING_NEWS",
  "priority": "HIGH",
  "publishedAt": "2024-12-20T10:00:00Z"
}
```

## TTS Integration

### Text Formatting

The system automatically formats news content for optimal TTS output:

- Removes HTML tags and special characters
- Expands abbreviations
- Adds pronunciation guides for proper nouns
- Inserts natural pauses
- Handles numbers and dates

### Voice Configuration

```typescript
interface TTSConfig {
  provider: 'elevenlabs' | 'azure' | 'google';
  voiceId: string;
  model: string;
  language: string;
  speed: number;
  pitch: number;
  stability: number;
  clarity: number;
}
```

## Avatar Integration

### Broadcast Workflow

1. **Content Preparation**
   - Article approval and scheduling
   - TTS audio generation
   - Avatar animation preparation

2. **Video Generation**
   - Lip-sync with TTS audio
   - Background and lighting setup
   - Camera positioning
   - Final video rendering

3. **Broadcasting**
   - Live streaming setup
   - Recorded video delivery
   - Thumbnail generation
   - Metadata attachment

## Security & Authentication

### Role-Based Access Control

- **NEWS_EDITOR**: Create and edit articles
- **BACKOFFICE_MODERATOR**: Approve articles, manage broadcasts
- **PLATFORM_ADMIN**: Full access to all features
- **SYSTEM**: Automated processes and webhooks

### API Security

- JWT authentication for all endpoints
- API key validation for webhooks
- Rate limiting and request validation
- Input sanitization and XSS protection

## Configuration

### Environment Variables

```bash
# TTS Configuration
TTS_SERVICE_URL=https://api.elevenlabs.io/v1/text-to-speech
TTS_API_KEY=your-tts-api-key
TTS_DEFAULT_VOICE=21m00Tcm4TlvDq8ikWAM
TTS_MODEL=eleven_multilingual_v2

# News Processing
NEWS_AUTO_APPROVAL_THRESHOLD=8
NEWS_DEFAULT_LANGUAGE=fr
NEWS_MAX_ARTICLE_LENGTH=5000
NEWS_MIN_ARTICLE_LENGTH=50

# Broadcast Settings
BROADCAST_DEFAULT_DURATION=300
BROADCAST_MAX_DURATION=1800
BROADCAST_STORAGE_PATH=./storage/broadcasts

# N8N Integration
N8N_WEBHOOK_API_KEY=your-webhook-secret
```

## Deployment

### Database Migration

```bash
# Apply news module schema
npx prisma migrate deploy

# Generate Prisma client
npx prisma generate
```

### Service Dependencies

- PostgreSQL database
- Redis for caching
- N8N for workflow automation
- TTS service (ElevenLabs/Azure/Google)
- Avatar rendering service
- File storage (AWS S3/local)

## Monitoring & Analytics

### Key Metrics

- **Content Metrics**
  - Articles processed per hour
  - Approval rate by source
  - Average processing time
  - Content quality scores

- **Broadcast Metrics**
  - Broadcast completion rate
  - Average view duration
  - Audience engagement
  - Technical performance

- **System Metrics**
  - API response times
  - Error rates
  - Resource utilization
  - Queue processing times

### Logging

```typescript
// Structured logging example
logger.info('Article processed', {
  articleId: 'article-uuid',
  sourceId: 'source-uuid',
  category: 'BREAKING_NEWS',
  processingTime: 1250,
  status: 'APPROVED'
});
```

## Troubleshooting

### Common Issues

1. **TTS Generation Failures**
   - Check API key validity
   - Verify text formatting
   - Monitor rate limits

2. **N8N Webhook Errors**
   - Validate webhook URL
   - Check API key configuration
   - Review payload format

3. **Avatar Rendering Issues**
   - Verify avatar service connectivity
   - Check resource availability
   - Monitor rendering queue

### Debug Commands

```bash
# Check service health
curl http://localhost:3001/api/health

# Test webhook endpoint
curl -X POST http://localhost:3001/api/news/webhook/n8n \
  -H "x-api-key: your-key" \
  -H "Content-Type: application/json" \
  -d '{"sourceId":"test","title":"Test","content":"Test content"}'

# Monitor logs
docker logs backoffice-service -f
```

## Future Enhancements

- Real-time fact-checking integration
- Multi-language content translation
- Advanced content personalization
- AI-powered content summarization
- Interactive avatar responses
- Social media integration
- Advanced analytics dashboard