-- CreateEnum
CREATE TYPE "NewsSourceType" AS ENUM ('RSS_FEED', 'API_ENDPOINT', 'WEBHOOK', 'SOCIAL_MEDIA', 'PRESS_AGENCY', 'MANUAL');

-- CreateEnum
CREATE TYPE "NewsCategory" AS ENUM ('BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'TECHNOLOGY', 'SCIENCE', 'HEALTH', 'SPORTS', 'ENTERTAINMENT', 'CULTURE', 'INTERNATIONAL', 'LOCAL', 'WEATHER', 'OTHER');

-- CreateEnum
CREATE TYPE "NewsStatus" AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'SCHEDULED', 'BROADCASTING', 'BROADCASTED', 'ARCHIVED');

-- CreateEnum
CREATE TYPE "NewsPriority" AS ENUM ('URGENT', 'HIGH', 'NORMAL', 'LOW');

-- CreateEnum
CREATE TYPE "FactCheckStatus" AS ENUM ('PENDING', 'VERIFIED', 'DISPUTED', 'FALSE', 'MIXED');

-- CreateEnum
CREATE TYPE "BroadcastType" AS ENUM ('LIVE', 'RECORDED', 'SCHEDULED');

-- CreateEnum
CREATE TYPE "BroadcastStatus" AS ENUM ('SCHEDULED', 'PREPARING', 'READY', 'BROADCASTING', 'COMPLETED', 'FAILED', 'CANCELLED');

-- CreateTable
CREATE TABLE "NewsSource" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "sourceType" "NewsSourceType" NOT NULL,
    "n8nWorkflowId" TEXT,
    "webhookUrl" TEXT,
    "apiEndpoint" TEXT,
    "apiKey" TEXT,
    "keywords" TEXT[],
    "categories" "NewsCategory"[],
    "languages" TEXT[],
    "priority" INTEGER NOT NULL DEFAULT 5,
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "lastSync" TIMESTAMP(3),
    "syncInterval" INTEGER DEFAULT 3600,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "NewsSource_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "NewsArticle" (
    "id" TEXT NOT NULL,
    "sourceId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "content" TEXT NOT NULL,
    "summary" TEXT,
    "formattedText" TEXT,
    "originalUrl" TEXT,
    "imageUrl" TEXT,
    "category" "NewsCategory" NOT NULL,
    "priority" "NewsPriority" NOT NULL DEFAULT 'NORMAL',
    "status" "NewsStatus" NOT NULL DEFAULT 'PENDING',
    "factCheckStatus" "FactCheckStatus" NOT NULL DEFAULT 'PENDING',
    "language" TEXT NOT NULL DEFAULT 'fr',
    "duration" INTEGER,
    "publishedAt" TIMESTAMP(3) NOT NULL,
    "approvedAt" TIMESTAMP(3),
    "approvedBy" TEXT,
    "rejectedAt" TIMESTAMP(3),
    "rejectedBy" TEXT,
    "rejectionReason" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "NewsArticle_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "NewsBroadcast" (
    "id" TEXT NOT NULL,
    "articleId" TEXT NOT NULL,
    "avatarId" TEXT NOT NULL,
    "broadcastType" "BroadcastType" NOT NULL DEFAULT 'SCHEDULED',
    "status" "BroadcastStatus" NOT NULL DEFAULT 'SCHEDULED',
    "scheduledAt" TIMESTAMP(3) NOT NULL,
    "startedAt" TIMESTAMP(3),
    "endedAt" TIMESTAMP(3),
    "duration" INTEGER,
    "viewCount" INTEGER DEFAULT 0,
    "audioUrl" TEXT,
    "videoUrl" TEXT,
    "thumbnailUrl" TEXT,
    "metadata" JSONB,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "NewsBroadcast_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "NewsSchedule" (
    "id" TEXT NOT NULL,
    "avatarId" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "timeSlots" JSONB NOT NULL,
    "categories" "NewsCategory"[],
    "priority" "NewsPriority"[],
    "maxDuration" INTEGER DEFAULT 300,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "NewsSchedule_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "NewsSource_name_key" ON "NewsSource"("name");

-- CreateIndex
CREATE INDEX "NewsSource_sourceType_idx" ON "NewsSource"("sourceType");

-- CreateIndex
CREATE INDEX "NewsSource_isActive_idx" ON "NewsSource"("isActive");

-- CreateIndex
CREATE INDEX "NewsArticle_sourceId_idx" ON "NewsArticle"("sourceId");

-- CreateIndex
CREATE INDEX "NewsArticle_status_idx" ON "NewsArticle"("status");

-- CreateIndex
CREATE INDEX "NewsArticle_category_idx" ON "NewsArticle"("category");

-- CreateIndex
CREATE INDEX "NewsArticle_priority_idx" ON "NewsArticle"("priority");

-- CreateIndex
CREATE INDEX "NewsArticle_publishedAt_idx" ON "NewsArticle"("publishedAt");

-- CreateIndex
CREATE INDEX "NewsArticle_createdAt_idx" ON "NewsArticle"("createdAt");

-- CreateIndex
CREATE INDEX "NewsBroadcast_articleId_idx" ON "NewsBroadcast"("articleId");

-- CreateIndex
CREATE INDEX "NewsBroadcast_avatarId_idx" ON "NewsBroadcast"("avatarId");

-- CreateIndex
CREATE INDEX "NewsBroadcast_status_idx" ON "NewsBroadcast"("status");

-- CreateIndex
CREATE INDEX "NewsBroadcast_scheduledAt_idx" ON "NewsBroadcast"("scheduledAt");

-- CreateIndex
CREATE INDEX "NewsSchedule_avatarId_idx" ON "NewsSchedule"("avatarId");

-- CreateIndex
CREATE INDEX "NewsSchedule_isActive_idx" ON "NewsSchedule"("isActive");

-- AddForeignKey
ALTER TABLE "NewsArticle" ADD CONSTRAINT "NewsArticle_sourceId_fkey" FOREIGN KEY ("sourceId") REFERENCES "NewsSource"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "NewsBroadcast" ADD CONSTRAINT "NewsBroadcast_articleId_fkey" FOREIGN KEY ("articleId") REFERENCES "NewsArticle"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "NewsBroadcast" ADD CONSTRAINT "NewsBroadcast_avatarId_fkey" FOREIGN KEY ("avatarId") REFERENCES "Avatar"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "NewsSchedule" ADD CONSTRAINT "NewsSchedule_avatarId_fkey" FOREIGN KEY ("avatarId") REFERENCES "Avatar"("id") ON DELETE RESTRICT ON UPDATE CASCADE;