import { PrismaClient } from '@prisma/client';
import { logger } from '../../src/utils/logger';

const prisma = new PrismaClient();

/**
 * Seed data for news module
 */
const seedNewsModule = async () => {
  try {
    console.log('🌱 Seeding news module data...');

    // Create news sources
    const newsSources = [
      {
        name: 'Le Monde RSS',
        description: 'Flux RSS du journal Le Monde',
        sourceType: 'RSS_FEED' as const,
        apiEndpoint: 'https://www.lemonde.fr/rss/une.xml',
        keywords: ['actualité', 'france', 'monde', 'politique'],
        categories: ['POLITICS', 'INTERNATIONAL', 'ECONOMY'] as const,
        languages: ['fr'],
        priority: 9,
        isActive: true,
        syncInterval: 1800 // 30 minutes
      },
      {
        name: 'France Info RSS',
        description: 'Flux RSS de France Info',
        sourceType: 'RSS_FEED' as const,
        apiEndpoint: 'https://www.francetvinfo.fr/titres.rss',
        keywords: ['info', 'france', 'actualité'],
        categories: ['BREAKING_NEWS', 'LOCAL', 'POLITICS'] as const,
        languages: ['fr'],
        priority: 8,
        isActive: true,
        syncInterval: 900 // 15 minutes
      },
      {
        name: 'Reuters API',
        description: 'Reuters News API for international news',
        sourceType: 'API_ENDPOINT' as const,
        apiEndpoint: 'https://api.reuters.com/v1/news',
        keywords: ['international', 'business', 'technology'],
        categories: ['INTERNATIONAL', 'ECONOMY', 'TECHNOLOGY'] as const,
        languages: ['en', 'fr'],
        priority: 9,
        isActive: true,
        syncInterval: 1800
      },
      {
        name: 'Tech News Webhook',
        description: 'Webhook for technology news aggregation',
        sourceType: 'WEBHOOK' as const,
        webhookUrl: 'https://api.maatcore.com/webhooks/tech-news',
        keywords: ['technology', 'innovation', 'startup', 'AI'],
        categories: ['TECHNOLOGY', 'SCIENCE'] as const,
        languages: ['fr', 'en'],
        priority: 7,
        isActive: true
      },
      {
        name: 'Manual Editorial',
        description: 'Manual news entry by editorial team',
        sourceType: 'MANUAL' as const,
        keywords: ['editorial', 'exclusive', 'analysis'],
        categories: ['BREAKING_NEWS', 'POLITICS', 'ECONOMY', 'CULTURE'] as const,
        languages: ['fr'],
        priority: 10,
        isActive: true
      },
      {
        name: 'Sports RSS',
        description: 'Flux RSS pour les actualités sportives',
        sourceType: 'RSS_FEED' as const,
        apiEndpoint: 'https://www.lequipe.fr/rss/actu_rss.xml',
        keywords: ['sport', 'football', 'tennis', 'olympiques'],
        categories: ['SPORTS'] as const,
        languages: ['fr'],
        priority: 6,
        isActive: true,
        syncInterval: 3600 // 1 hour
      }
    ];

    console.log('📰 Creating news sources...');
    const createdSources = [];
    
    for (const sourceData of newsSources) {
      const source = await prisma.newsSource.upsert({
        where: { name: sourceData.name },
        update: sourceData,
        create: sourceData
      });
      createdSources.push(source);
      console.log(`✅ Created/Updated source: ${source.name}`);
    }

    // Create sample news articles
    console.log('📝 Creating sample news articles...');
    
    const sampleArticles = [
      {
        sourceId: createdSources[0].id, // Le Monde
        title: 'Nouvelle réforme économique annoncée par le gouvernement',
        content: 'Le gouvernement français a annoncé aujourd\'hui une nouvelle réforme économique visant à stimuler la croissance et l\'emploi. Cette réforme comprend plusieurs mesures importantes pour les entreprises et les particuliers. Les détails de cette réforme seront présentés lors d\'une conférence de presse prévue demain matin.',
        summary: 'Le gouvernement annonce une nouvelle réforme économique pour stimuler la croissance.',
        category: 'POLITICS' as const,
        priority: 'HIGH' as const,
        status: 'APPROVED' as const,
        factCheckStatus: 'VERIFIED' as const,
        language: 'fr',
        publishedAt: new Date(),
        approvedAt: new Date(),
        approvedBy: 'system'
      },
      {
        sourceId: createdSources[1].id, // France Info
        title: 'Alerte météo : tempête attendue sur la côte atlantique',
        content: 'Météo France a émis une alerte orange pour les départements de la côte atlantique. Une tempête importante est attendue dans la nuit de demain avec des vents pouvant atteindre 120 km/h. Les autorités recommandent aux habitants de rester chez eux et d\'éviter les déplacements non essentiels.',
        summary: 'Alerte orange pour une tempête sur la côte atlantique avec des vents jusqu\'à 120 km/h.',
        category: 'WEATHER' as const,
        priority: 'URGENT' as const,
        status: 'APPROVED' as const,
        factCheckStatus: 'VERIFIED' as const,
        language: 'fr',
        publishedAt: new Date(),
        approvedAt: new Date(),
        approvedBy: 'system'
      },
      {
        sourceId: createdSources[2].id, // Reuters
        title: 'Major breakthrough in artificial intelligence research',
        content: 'Researchers at a leading technology institute have announced a major breakthrough in artificial intelligence that could revolutionize how machines understand and process human language. The new model demonstrates unprecedented accuracy in natural language understanding tasks and could have significant implications for various industries.',
        summary: 'Breakthrough in AI research could revolutionize natural language processing.',
        category: 'TECHNOLOGY' as const,
        priority: 'HIGH' as const,
        status: 'PENDING' as const,
        factCheckStatus: 'PENDING' as const,
        language: 'en',
        publishedAt: new Date()
      },
      {
        sourceId: createdSources[3].id, // Tech Webhook
        title: 'Nouvelle startup française lève 50 millions d\'euros',
        content: 'Une startup française spécialisée dans l\'intelligence artificielle vient de lever 50 millions d\'euros lors d\'un tour de financement de série B. Cette levée de fonds permettra à l\'entreprise d\'accélérer son développement international et de recruter de nouveaux talents. La startup compte déjà plus de 100 employés et prévoit de doubler ses effectifs d\'ici la fin de l\'année.',
        summary: 'Startup française IA lève 50M€ en série B pour son développement international.',
        category: 'TECHNOLOGY' as const,
        priority: 'NORMAL' as const,
        status: 'APPROVED' as const,
        factCheckStatus: 'VERIFIED' as const,
        language: 'fr',
        publishedAt: new Date(),
        approvedAt: new Date(),
        approvedBy: 'system'
      }
    ];

    for (const articleData of sampleArticles) {
      // Format text for TTS (simplified version)
      const formattedText = articleData.content
        .replace(/'/g, "'")
        .replace(/'/g, "'")
        .replace(/"/g, '"')
        .replace(/"/g, '"');
      
      // Calculate estimated duration (150 words per minute)
      const words = formattedText.split(' ').length;
      const duration = Math.ceil((words / 150) * 60 * 1.2); // Add 20% buffer

      const article = await prisma.newsArticle.create({
        data: {
          ...articleData,
          formattedText,
          duration
        }
      });
      
      console.log(`✅ Created article: ${article.title}`);
    }

    // Create news schedules for avatars (if avatars exist)
    console.log('📅 Creating news schedules...');
    
    const avatars = await prisma.avatar.findMany({
      where: { isActive: true },
      take: 2
    });

    if (avatars.length > 0) {
      const schedules = [
        {
          avatarId: avatars[0].id,
          name: 'Journal du Matin',
          description: 'Bulletin d\'information matinal',
          isActive: true,
          timeSlots: [
            { day: 'monday', time: '08:00', duration: 300 },
            { day: 'tuesday', time: '08:00', duration: 300 },
            { day: 'wednesday', time: '08:00', duration: 300 },
            { day: 'thursday', time: '08:00', duration: 300 },
            { day: 'friday', time: '08:00', duration: 300 }
          ],
          categories: ['BREAKING_NEWS', 'POLITICS', 'ECONOMY'] as const,
          priority: ['URGENT', 'HIGH'] as const,
          maxDuration: 600
        }
      ];

      if (avatars.length > 1) {
        schedules.push({
          avatarId: avatars[1].id,
          name: 'Tech News Evening',
          description: 'Actualités technologiques du soir',
          isActive: true,
          timeSlots: [
            { day: 'monday', time: '18:00', duration: 240 },
            { day: 'wednesday', time: '18:00', duration: 240 },
            { day: 'friday', time: '18:00', duration: 240 }
          ],
          categories: ['TECHNOLOGY', 'SCIENCE'] as const,
          priority: ['HIGH', 'NORMAL'] as const,
          maxDuration: 480
        });
      }

      for (const scheduleData of schedules) {
        const schedule = await prisma.newsSchedule.create({
          data: scheduleData
        });
        console.log(`✅ Created schedule: ${schedule.name}`);
      }
    } else {
      console.log('⚠️  No active avatars found, skipping schedule creation');
    }

    console.log('🎉 News module seeding completed successfully!');
    
  } catch (error) {
    console.error('❌ Error seeding news module:', error);
    throw error;
  }
};

/**
 * Main seed function
 */
const main = async () => {
  try {
    await seedNewsModule();
  } catch (error) {
    console.error('Seeding failed:', error);
    process.exit(1);
  } finally {
    await prisma.$disconnect();
  }
};

// Run if called directly
if (require.main === module) {
  main();
}

export { seedNewsModule };