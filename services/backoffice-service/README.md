# Backoffice Service

The Backoffice Service is a comprehensive management system for the MaâtCore platform, providing centralized control over AI avatars, virtual studios, content management, RSS feeds, and workflow automation.

## Features

### Core Functionality
- **AI Avatar Management**: Create, configure, and manage AI avatars with voice, appearance, and behavior settings
- **Virtual Studio Management**: Manage Unreal Engine-based virtual studios for content production
- **Unified Content Management**: Centralized content management across all MaâtCore services
- **RSS Feed Management**: Automated RSS feed processing with fact-checking and AI summarization
- **Workflow Automation**: Integration with n8n for automated business processes
- **Real-time Dashboard**: Comprehensive analytics and system monitoring

### Technical Features
- **Authentication & Authorization**: JWT-based auth with role-based access control
- **Real-time Updates**: WebSocket support for live updates
- **Caching**: Redis-based caching for improved performance
- **Database**: PostgreSQL with Prisma ORM
- **API Documentation**: Comprehensive REST API
- **Health Monitoring**: Built-in health checks and monitoring
- **Containerization**: Docker support for easy deployment

## Architecture

### Technology Stack
- **Backend**: Node.js, Express.js, TypeScript
- **Database**: PostgreSQL with Prisma ORM
- **Cache**: Redis
- **Real-time**: Socket.IO
- **Authentication**: JWT tokens
- **Validation**: express-validator
- **Logging**: Winston
- **Testing**: Jest
- **Containerization**: Docker

### Service Integration
- **User Service**: Authentication and user management
- **MaâtTV Service**: Video content management
- **MaâtClass Service**: Educational content management
- **MaâtTube Service**: Video platform integration
- **MaâtCare Service**: Healthcare content management
- **n8n**: Workflow automation
- **Unreal Engine**: Virtual studio rendering
- **AI Services**: Content processing and analysis

## Getting Started

### Prerequisites
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- Docker (optional)

### Installation

1. **Clone and navigate to the service directory**:
   ```bash
   cd services/backoffice-service
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Set up environment variables**:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Set up the database**:
   ```bash
   # Generate Prisma client
   npx prisma generate
   
   # Run database migrations
   npx prisma migrate dev
   
   # Seed the database (optional)
   npx prisma db seed
   ```

5. **Start the development server**:
   ```bash
   npm run dev
   ```

### Docker Setup

1. **Using Docker Compose (recommended for development)**:
   ```bash
   docker-compose up -d
   ```

2. **Building for production**:
   ```bash
   docker build --target production -t backoffice-service .
   ```

## API Documentation

### Authentication
All API endpoints require authentication except for login and health check.

**Login**:
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}
```

**Authorization Header**:
```http
Authorization: Bearer <jwt_token>
```

### Core Endpoints

#### Avatars
- `GET /api/avatars` - List avatars
- `POST /api/avatars` - Create avatar
- `GET /api/avatars/:id` - Get avatar details
- `PUT /api/avatars/:id` - Update avatar
- `DELETE /api/avatars/:id` - Delete avatar
- `POST /api/avatars/:id/duplicate` - Duplicate avatar

#### Studios
- `GET /api/studios` - List studios
- `POST /api/studios` - Create studio
- `GET /api/studios/:id` - Get studio details
- `PUT /api/studios/:id` - Update studio
- `DELETE /api/studios/:id` - Delete studio
- `POST /api/studios/:id/test-rendering` - Test studio rendering

#### Content
- `GET /api/content` - List content
- `POST /api/content` - Create content
- `GET /api/content/:id` - Get content details
- `PUT /api/content/:id` - Update content
- `DELETE /api/content/:id` - Delete content
- `POST /api/content/sync/:service/:sourceId` - Sync from external service

#### RSS Feeds
- `GET /api/rss/feeds` - List RSS feeds
- `POST /api/rss/feeds` - Create RSS feed
- `GET /api/rss/articles` - List RSS articles
- `POST /api/rss/articles/:id/fact-check` - Fact check article
- `POST /api/rss/articles/:id/ai-summary` - Generate AI summary

#### Workflows
- `GET /api/workflows` - List workflows
- `POST /api/workflows` - Create workflow
- `POST /api/workflows/:id/execute` - Execute workflow
- `GET /api/workflows/:id/executions` - Get workflow executions

#### Dashboard
- `GET /api/dashboard/overview` - Dashboard overview
- `GET /api/dashboard/health` - System health
- `GET /api/dashboard/activities` - Recent activities
- `GET /api/dashboard/performance` - Performance metrics

### WebSocket Events

#### Connection
```javascript
const socket = io('ws://localhost:3000', {
  auth: {
    token: 'your-jwt-token'
  }
});
```

#### Events
- `avatar_change` - Avatar created/updated/deleted
- `studio_change` - Studio created/updated/deleted
- `content_change` - Content created/updated/deleted
- `workflow_change` - Workflow created/updated/deleted
- `rss_change` - RSS feed created/updated/deleted
- `system_alert` - System alerts and notifications

## Configuration

### Environment Variables

#### Database
- `DATABASE_URL` - PostgreSQL connection string
- `REDIS_URL` - Redis connection string

#### Server
- `PORT` - Server port (default: 3000)
- `HOST` - Server host (default: localhost)
- `NODE_ENV` - Environment (development/production)

#### Authentication
- `JWT_SECRET` - JWT signing secret
- `JWT_REFRESH_SECRET` - JWT refresh token secret
- `JWT_EXPIRES_IN` - JWT expiration time
- `JWT_REFRESH_EXPIRES_IN` - Refresh token expiration time

#### External Services
- `USER_SERVICE_URL` - User service URL
- `MAATTV_SERVICE_URL` - MaâtTV service URL
- `MAATCLASS_SERVICE_URL` - MaâtClass service URL
- `MAATTUBE_SERVICE_URL` - MaâtTube service URL
- `MAATCARE_SERVICE_URL` - MaâtCare service URL
- `N8N_API_URL` - n8n API URL
- `N8N_API_KEY` - n8n API key
- `UNREAL_ENGINE_API_URL` - Unreal Engine service URL
- `AI_SERVICE_URL` - AI service URL
- `FACT_CHECK_API_URL` - Fact checking service URL

#### Security
- `CORS_ORIGIN` - Allowed CORS origins
- `RATE_LIMIT_WINDOW_MS` - Rate limiting window
- `RATE_LIMIT_MAX_REQUESTS` - Max requests per window

## Development

### Scripts
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm test` - Run tests
- `npm run test:watch` - Run tests in watch mode
- `npm run lint` - Run ESLint
- `npm run lint:fix` - Fix ESLint issues
- `npm run db:migrate` - Run database migrations
- `npm run db:seed` - Seed database
- `npm run db:studio` - Open Prisma Studio

### Database Management

#### Migrations
```bash
# Create new migration
npx prisma migrate dev --name migration_name

# Apply migrations
npx prisma migrate deploy

# Reset database
npx prisma migrate reset
```

#### Prisma Studio
```bash
npx prisma studio
```

### Testing

#### Unit Tests
```bash
npm test
```

#### Integration Tests
```bash
npm run test:integration
```

#### Test Coverage
```bash
npm run test:coverage
```

## Deployment

### Production Build
```bash
npm run build
npm start
```

### Docker Deployment
```bash
# Build production image
docker build --target production -t backoffice-service .

# Run container
docker run -p 3000:3000 --env-file .env backoffice-service
```

### Health Checks
The service includes built-in health checks:
- HTTP endpoint: `GET /health`
- Docker health check: Automatic container health monitoring
- Database connectivity check
- Redis connectivity check
- External service connectivity checks

## Monitoring and Logging

### Logging
- **Winston** for structured logging
- Log levels: error, warn, info, debug
- Separate log files for different log types
- Request/response logging
- Database operation logging
- Authentication event logging

### Metrics
- System performance metrics
- Database query performance
- API response times
- WebSocket connection metrics
- External service response times

### Alerts
- System health alerts
- Performance degradation alerts
- Error rate alerts
- External service failure alerts

## Security

### Authentication
- JWT-based authentication
- Refresh token rotation
- Session management with Redis
- Token blacklisting

### Authorization
- Role-based access control (RBAC)
- Permission-based access control
- Resource-level permissions
- API endpoint protection

### Security Headers
- CORS configuration
- Rate limiting
- Request size limits
- Security headers (helmet.js)

### Data Protection
- Input validation and sanitization
- SQL injection prevention (Prisma)
- XSS protection
- CSRF protection

## Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database connectivity
npx prisma db push

# Verify database URL
echo $DATABASE_URL
```

#### Redis Connection Issues
```bash
# Test Redis connection
redis-cli ping

# Check Redis URL
echo $REDIS_URL
```

#### Authentication Issues
- Verify JWT secrets are set
- Check token expiration
- Verify user permissions
- Check session in Redis

#### Performance Issues
- Check database query performance
- Monitor Redis cache hit rates
- Review API response times
- Check external service connectivity

### Logs
Logs are stored in:
- `logs/combined.log` - All logs
- `logs/error.log` - Error logs only
- `logs/http.log` - HTTP request logs

### Debug Mode
```bash
LOG_LEVEL=debug npm run dev
```

## Contributing

1. Follow the existing code style
2. Write tests for new features
3. Update documentation
4. Follow commit message conventions
5. Create pull requests for review

## License

This project is part of the MaâtCore platform and is proprietary software.