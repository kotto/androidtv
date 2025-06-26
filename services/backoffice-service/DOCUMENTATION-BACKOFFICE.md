# 📋 Documentation du Backoffice MaâtCore

## 🎯 Vue d'ensemble

Le **Backoffice MaâtCore** est le système de gestion centralisé de la plateforme MaâtCore. Il fournit une interface de contrôle unifiée pour gérer tous les aspects de l'écosystème MaâtCore, incluant les avatars IA, les studios virtuels, la gestion de contenu, les flux RSS, et l'automatisation des workflows.

## 🏗️ Architecture Générale

### Stack Technologique
- **Backend** : Node.js avec Express.js et TypeScript
- **Base de données** : PostgreSQL avec Prisma ORM
- **Cache** : Redis pour l'optimisation des performances
- **Temps réel** : Socket.IO pour les mises à jour en direct
- **Authentification** : JWT avec contrôle d'accès basé sur les rôles
- **Conteneurisation** : Docker pour le déploiement

### Services Intégrés
- **User Service** : Gestion des utilisateurs et authentification
- **MaâtTV Service** : Gestion du contenu vidéo
- **MaâtClass Service** : Contenu éducatif
- **MaâtTube Service** : Intégration plateforme vidéo
- **MaâtCare Service** : Contenu de santé
- **n8n** : Automatisation des workflows
- **Unreal Engine** : Rendu des studios virtuels
- **Services IA** : Traitement et analyse de contenu

## 🎭 Gestion des Avatars IA

### Fonctionnalités Principales

#### Création et Configuration d'Avatars
- **Modèles 3D** : Gestion des modèles, textures et animations
- **Configuration vocale** : Intégration avec ElevenLabs, Azure TTS
- **Personnalité** : Définition du caractère et de l'expertise
- **Apparence** : Gestion des tenues et styles visuels

#### Types d'Avatars
```typescript
enum AvatarType {
  NEWS_PRESENTER    // Présentateur de nouvelles
  TEACHER          // Enseignant virtuel
  ASSISTANT        // Assistant personnel
  ENTERTAINER      // Divertissement
  HEALTHCARE       // Santé et bien-être
  CUSTOM           // Personnalisé
}
```

#### Gestion des Sessions
- **Sessions en temps réel** : Contrôle des avatars actifs
- **États de session** : IDLE, ACTIVE, RECORDING, STREAMING
- **Scripts dynamiques** : Gestion du contenu à présenter
- **Émotions et poses** : Contrôle expressif en temps réel

### API Endpoints Avatars
```http
# Lister les avatars
GET /api/avatars?page=1&limit=10&search=news&type=NEWS_PRESENTER

# Créer un avatar
POST /api/avatars
{
  "name": "Sophie News",
  "type": "NEWS_PRESENTER",
  "gender": "FEMALE",
  "language": "fr",
  "voiceId": "elevenlabs-voice-id",
  "personality": "Professionnelle et engageante"
}

# Démarrer une session
POST /api/avatars/{id}/sessions
{
  "sessionType": "LIVE_STREAMING",
  "script": "Bonjour et bienvenue..."
}
```

## 🎬 Studios Virtuels Unreal Engine

### Fonctionnalités

#### Gestion des Studios
- **Environnements 3D** : Studios de news, salles de classe, espaces de divertissement
- **Éclairage dynamique** : Presets d'éclairage configurables
- **Paramètres de caméra** : Angles, mouvements, transitions
- **Rendu en temps réel** : Intégration Unreal Engine

#### Types de Studios
```typescript
enum StudioType {
  NEWS_STUDIO      // Studio d'information
  CLASSROOM        // Salle de classe virtuelle
  TALK_SHOW        // Plateau de talk-show
  WEATHER_STUDIO   // Studio météo
  SPORTS_STUDIO    // Studio sportif
  CUSTOM           // Studio personnalisé
}
```

#### Configuration Technique
- **Chemins de projet Unreal** : Gestion des assets et scènes
- **Paramètres de rendu** : Qualité, résolution, effets
- **Presets d'éclairage** : Configurations prédéfinies
- **Intégration temps réel** : Communication avec Unreal Engine

### API Endpoints Studios
```http
# Lister les studios
GET /api/studios?environment=NEWS_STUDIO&status=ACTIVE

# Créer un studio
POST /api/studios
{
  "name": "Studio News Principal",
  "studioType": "NEWS_STUDIO",
  "unrealProjectPath": "/path/to/unreal/project",
  "renderSettings": {
    "quality": "HIGH",
    "resolution": "1920x1080"
  }
}

# Activer un studio
POST /api/studios/{id}/activate
```

## 📰 Module de Gestion des Actualités

### Architecture du Module News

#### Sources d'Information
- **Flux RSS** : Surveillance automatique des feeds
- **APIs externes** : News API, Guardian, Reuters
- **Webhooks N8N** : Intégration workflow automatisé
- **Saisie manuelle** : Contenu éditorial exclusif
- **Réseaux sociaux** : Monitoring Twitter, Facebook

#### Workflow Éditorial
1. **Collecte** : Agrégation multi-sources
2. **Traitement** : Nettoyage et formatage du texte
3. **Validation** : Vérification éditoriale
4. **Fact-checking** : Processus de vérification
5. **Approbation** : Validation finale
6. **Optimisation TTS** : Formatage pour synthèse vocale
7. **Diffusion** : Programmation des broadcasts

#### Modèles de Données
```typescript
// Source d'actualités
model NewsSource {
  name: string
  sourceType: NewsSourceType  // RSS_FEED, API_ENDPOINT, WEBHOOK, MANUAL
  apiEndpoint?: string
  keywords: string[]
  categories: NewsCategory[]
  languages: string[]
  priority: number
  syncInterval?: number
}

// Article d'actualité
model NewsArticle {
  title: string
  content: string
  summary: string
  category: NewsCategory
  priority: NewsPriority      // URGENT, HIGH, NORMAL, LOW
  status: NewsStatus          // PENDING, APPROVED, REJECTED
  factCheckStatus: FactCheckStatus
  language: string
  formattedText: string       // Texte optimisé pour TTS
  duration: number            // Durée estimée en secondes
}
```

### API Endpoints News
```http
# Articles
GET /api/news/articles?category=TECHNOLOGY&status=APPROVED
POST /api/news/articles
PUT /api/news/articles/{id}/approve

# Sources
GET /api/news/sources
POST /api/news/sources

# Broadcasts
POST /api/news/broadcasts/schedule
GET /api/news/broadcasts/next?avatarId={id}

# Analytics
GET /api/news/analytics?period=week

# Webhook N8N
POST /api/news/webhook/n8n
```

## 📡 Gestion des Flux RSS

### Fonctionnalités RSS

#### Surveillance Automatique
- **Parsing RSS/Atom** : Traitement des flux standards
- **Intervalles configurables** : Synchronisation personnalisée
- **Filtrage intelligent** : Mots-clés et catégories
- **Déduplication** : Éviter les contenus dupliqués

#### Traitement du Contenu
- **Extraction de métadonnées** : Titre, description, date
- **Nettoyage HTML** : Suppression des balises
- **Résumé automatique** : IA pour synthèse
- **Classification** : Catégorisation automatique

#### Intégration IA
- **Analyse de sentiment** : Détection du ton
- **Extraction d'entités** : Personnes, lieux, organisations
- **Fact-checking automatique** : Vérification préliminaire
- **Traduction automatique** : Support multilingue

### Configuration RSS
```typescript
model RSSFeed {
  name: string
  url: string
  category: string
  language: string
  updateInterval: number      // en minutes
  isActive: boolean
  lastSync: DateTime
  keywords: string[]          // Filtres de contenu
  aiProcessing: boolean       // Traitement IA activé
}
```

## 🔄 Automatisation avec N8N

### Workflows Intégrés

#### Traitement de Contenu
- **Agrégation multi-sources** : Collecte automatisée
- **Enrichissement IA** : Analyse et métadonnées
- **Validation qualité** : Contrôles automatiques
- **Distribution** : Publication multi-plateforme

#### Workflows News
1. **Monitoring RSS** → **Extraction** → **Nettoyage** → **Classification**
2. **Réseaux sociaux** → **Filtrage** → **Analyse sentiment** → **Validation**
3. **APIs externes** → **Normalisation** → **Fact-check** → **Approbation**

#### Configuration N8N
```json
{
  "webhookUrl": "https://api.maatcore.com/api/news/webhook/n8n",
  "apiKey": "secure-webhook-key",
  "workflows": {
    "rss_monitoring": "workflow-id-1",
    "social_media": "workflow-id-2",
    "fact_checking": "workflow-id-3"
  }
}
```

## 📊 Dashboard et Analytics

### Métriques Temps Réel

#### Avatars
- **Sessions actives** : Nombre d'avatars en cours d'utilisation
- **Temps d'utilisation** : Statistiques par avatar
- **Performance vocale** : Qualité TTS et latence
- **Engagement utilisateur** : Interactions et feedback

#### Contenu
- **Articles traités** : Volume quotidien/hebdomadaire
- **Taux d'approbation** : Pourcentage de contenu validé
- **Sources performantes** : Classement par qualité
- **Catégories populaires** : Tendances de contenu

#### Système
- **Santé des services** : Status des composants
- **Performance base de données** : Temps de réponse
- **Utilisation cache** : Efficacité Redis
- **Charge serveur** : CPU, mémoire, réseau

### Endpoints Analytics
```http
# Dashboard général
GET /api/dashboard/overview

# Métriques avatars
GET /api/dashboard/avatars/metrics?period=24h

# Statistiques contenu
GET /api/dashboard/content/stats?category=NEWS

# Santé système
GET /api/dashboard/system/health
```

## 🔐 Sécurité et Authentification

### Système d'Authentification

#### JWT et Rôles
```typescript
enum UserRole {
  ADMIN     // Accès complet
  EDITOR    // Gestion contenu
  OPERATOR  // Opérations techniques
  VIEWER    // Lecture seule
}
```

#### Contrôle d'Accès
- **ADMIN** : Gestion complète du système
- **EDITOR** : Création/modification de contenu
- **OPERATOR** : Gestion technique des avatars/studios
- **VIEWER** : Consultation des données

#### Sécurité API
- **Rate limiting** : Protection contre les abus
- **Validation d'entrée** : Sanitisation des données
- **Chiffrement** : HTTPS obligatoire
- **Audit logging** : Traçabilité des actions

### Middleware de Sécurité
```typescript
// Authentification
app.use('/api', authMiddleware);

// Rate limiting
app.use(rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limite par IP
}));

// Validation
app.use('/api/news', newsAuthMiddleware);
```

## 🔧 Configuration et Déploiement

### Variables d'Environnement

#### Base de Données
```env
DATABASE_URL=postgresql://user:pass@localhost:5432/maatcore
REDIS_URL=redis://localhost:6379
```

#### Services Externes
```env
# TTS Services
ELEVENLABS_API_KEY=your-key
AZURE_TTS_KEY=your-key

# News APIs
NEWS_API_KEY=your-key
GUARDIAN_API_KEY=your-key

# N8N Integration
N8N_WEBHOOK_URL=http://localhost:5678
N8N_WEBHOOK_API_KEY=secure-key

# Unreal Engine
UNREAL_ENGINE_HOST=localhost
UNREAL_ENGINE_PORT=8080
```

#### Sécurité
```env
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=24h
CORS_ORIGIN=http://localhost:3000
RATE_LIMIT_MAX_REQUESTS=100
```

### Déploiement Docker

#### Docker Compose
```yaml
version: '3.8'
services:
  backoffice:
    build: .
    ports:
      - "3001:3001"
    environment:
      - NODE_ENV=production
    depends_on:
      - postgres
      - redis
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: maatcore
      POSTGRES_USER: maatcore
      POSTGRES_PASSWORD: password
  
  redis:
    image: redis:7-alpine
```

### Scripts de Déploiement
```bash
# Installation
npm install

# Configuration base de données
npx prisma migrate deploy
npx prisma generate

# Seed données initiales
npm run news:seed

# Démarrage production
npm run build
npm start
```

## 🔄 Intégrations Temps Réel

### WebSocket avec Socket.IO

#### Événements Temps Réel
```typescript
// Statut des avatars
socket.emit('avatar:status', {
  avatarId: 'avatar-123',
  status: 'ACTIVE',
  currentScript: 'Bonjour...'
});

// Nouveaux articles
socket.emit('news:article:new', {
  articleId: 'article-456',
  title: 'Breaking News',
  priority: 'URGENT'
});

// Métriques système
socket.emit('system:metrics', {
  cpu: 45,
  memory: 67,
  activeUsers: 12
});
```

#### Salles de Communication
- **admin** : Notifications administratives
- **editors** : Alertes éditoriales
- **operators** : Événements techniques
- **public** : Informations générales

## 📈 Monitoring et Maintenance

### Health Checks
```http
GET /health
{
  "status": "healthy",
  "timestamp": "2024-01-15T10:30:00Z",
  "services": {
    "database": "connected",
    "redis": "connected",
    "unreal": "connected"
  }
}
```

### Logs et Debugging
```typescript
// Configuration Winston
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.json(),
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' })
  ]
});
```

### Métriques de Performance
- **Temps de réponse API** : Latence moyenne
- **Utilisation base de données** : Connexions actives
- **Cache hit ratio** : Efficacité Redis
- **Erreurs système** : Taux d'erreur par endpoint

## 🚀 Évolutions Futures

### Fonctionnalités Prévues
- **IA avancée** : GPT-4 pour génération de contenu
- **Réalité augmentée** : Intégration AR/VR
- **Multi-tenant** : Support multi-organisation
- **API GraphQL** : Alternative REST
- **Microservices** : Architecture distribuée

### Optimisations
- **Cache distribué** : Redis Cluster
- **Load balancing** : Répartition de charge
- **CDN** : Distribution de contenu
- **Monitoring avancé** : Prometheus/Grafana

---

**Le Backoffice MaâtCore** constitue le cœur de l'écosystème, orchestrant l'ensemble des services pour offrir une expérience utilisateur fluide et des capacités de gestion avancées. Cette architecture modulaire et extensible permet une évolution continue selon les besoins de la plateforme.