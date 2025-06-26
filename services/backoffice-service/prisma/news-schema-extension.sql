-- Extension du schéma Prisma pour le module News avec Avatar Virtuel
-- À ajouter au fichier schema.prisma existant

-- ============================================================================
-- GESTION DES ACTUALITÉS ET AVATAR VIRTUEL
-- ============================================================================

-- Sources d'actualités (N8N workflows)
model NewsSource {
  id          String   @id @default(cuid())
  name        String   @unique
  description String?
  sourceType  NewsSourceType
  
  -- Configuration N8N
  n8nWorkflowId String?  -- ID du workflow N8N
  webhookUrl    String?  -- URL webhook pour recevoir les actualités
  apiEndpoint   String?  -- Endpoint API pour récupérer les actualités
  
  -- Configuration de filtrage
  keywords      String[] -- Mots-clés pour filtrage
  categories    String[] -- Catégories acceptées
  languages     String[] @default(["fr"])
  priority      Int      @default(5) -- Priorité 1-10
  
  -- Métadonnées
  isActive      Boolean  @default(true)
  lastFetchedAt DateTime?
  createdAt     DateTime @default(now())
  updatedAt     DateTime @updatedAt
  
  -- Relations
  articles      NewsArticle[]
  
  @@map("news_sources")
}

-- Articles d'actualités
model NewsArticle {
  id            String   @id @default(cuid())
  sourceId      String
  
  -- Contenu original
  title         String
  content       String   @db.Text
  summary       String?  @db.Text
  originalUrl   String?
  imageUrl      String?
  
  -- Contenu formaté pour l'avatar
  formattedText String?  @db.Text -- Texte optimisé pour TTS
  duration      Int?     -- Durée estimée en secondes
  
  -- Métadonnées
  category      NewsCategory
  priority      NewsPriority @default(NORMAL)
  language      String   @default("fr")
  publishedAt   DateTime
  
  -- Statuts éditoriaux
  status        NewsStatus @default(PENDING)
  factCheckStatus FactCheckStatus @default(PENDING)
  
  -- Programmation
  scheduledAt   DateTime?
  broadcastAt   DateTime?
  
  -- Relations
  source        NewsSource @relation(fields: [sourceId], references: [id], onDelete: Cascade)
  broadcasts    NewsBroadcast[]
  
  -- Audit
  createdAt     DateTime @default(now())
  updatedAt     DateTime @updatedAt
  createdBy     String?
  updatedBy     String?
  
  @@map("news_articles")
}

-- Diffusions d'actualités par l'avatar
model NewsBroadcast {
  id          String   @id @default(cuid())
  articleId   String
  avatarId    String
  
  -- Configuration de diffusion
  broadcastType BroadcastType @default(LIVE)
  status      BroadcastStatus @default(SCHEDULED)
  
  -- Fichiers générés
  audioUrl    String?  -- Fichier audio TTS généré
  videoUrl    String?  -- Vidéo avec avatar générée
  
  -- Métadonnées de diffusion
  startedAt   DateTime?
  endedAt     DateTime?
  duration    Int?     -- Durée réelle en secondes
  
  -- Statistiques
  viewCount   Int      @default(0)
  
  -- Relations
  article     NewsArticle @relation(fields: [articleId], references: [id], onDelete: Cascade)
  avatar      Avatar   @relation(fields: [avatarId], references: [id])
  
  -- Audit
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt
  
  @@map("news_broadcasts")
}

-- Planning de diffusion
model NewsSchedule {
  id          String   @id @default(cuid())
  name        String
  description String?
  
  -- Configuration temporelle
  startTime   String   -- Format HH:MM
  endTime     String   -- Format HH:MM
  daysOfWeek  Int[]    -- 0=Dimanche, 1=Lundi, etc.
  timezone    String   @default("Europe/Paris")
  
  -- Configuration de contenu
  maxArticles Int      @default(5)
  categories  NewsCategory[]
  priority    NewsPriority @default(NORMAL)
  
  -- Avatar assigné
  avatarId    String
  
  -- Métadonnées
  isActive    Boolean  @default(true)
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt
  
  -- Relations
  avatar      Avatar   @relation(fields: [avatarId], references: [id])
  
  @@map("news_schedules")
}

-- ============================================================================
-- ENUMS POUR LE MODULE NEWS
-- ============================================================================

enum NewsSourceType {
  RSS_FEED
  API_ENDPOINT
  WEBHOOK
  SOCIAL_MEDIA
  PRESS_AGENCY
  MANUAL
}

enum NewsCategory {
  BREAKING_NEWS
  POLITICS
  ECONOMY
  TECHNOLOGY
  SCIENCE
  HEALTH
  SPORTS
  ENTERTAINMENT
  CULTURE
  INTERNATIONAL
  LOCAL
  WEATHER
  OTHER
}

enum NewsPriority {
  URGENT      // Breaking news
  HIGH        // Actualités importantes
  NORMAL      // Actualités standard
  LOW         // Actualités secondaires
}

enum NewsStatus {
  PENDING     // En attente de validation
  APPROVED    // Validé pour diffusion
  REJECTED    // Rejeté
  SCHEDULED   // Programmé
  BROADCASTING // En cours de diffusion
  BROADCASTED  // Diffusé
  ARCHIVED    // Archivé
}

enum FactCheckStatus {
  PENDING     // En attente de vérification
  VERIFIED    // Vérifié et approuvé
  DISPUTED    // Informations contestées
  FALSE       // Fausses informations
  MIXED       // Partiellement vrai
}

enum BroadcastType {
  LIVE        // Diffusion en direct
  RECORDED    // Enregistrement
  SCHEDULED   // Programmé
}

enum BroadcastStatus {
  SCHEDULED   // Programmé
  PREPARING   // Génération en cours
  READY       // Prêt à diffuser
  BROADCASTING // En cours
  COMPLETED   // Terminé
  FAILED      // Échec
  CANCELLED   // Annulé
}

-- ============================================================================
-- RELATIONS À AJOUTER AUX MODÈLES EXISTANTS
-- ============================================================================

-- À ajouter au modèle Avatar existant :
-- broadcasts    NewsBroadcast[]
-- schedules     NewsSchedule[]

-- À ajouter au modèle Content existant :
-- newsArticle   NewsArticle? @relation(fields: [newsArticleId], references: [id])
-- newsArticleId String?