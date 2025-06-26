#!/usr/bin/env node

/**
 * Setup script for the News Module
 * This script helps initialize the news module with all required dependencies and configurations
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const readline = require('readline');

// Colors for console output
const colors = {
  reset: '\x1b[0m',
  bright: '\x1b[1m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m'
};

const log = {
  info: (msg) => console.log(`${colors.blue}ℹ${colors.reset} ${msg}`),
  success: (msg) => console.log(`${colors.green}✅${colors.reset} ${msg}`),
  warning: (msg) => console.log(`${colors.yellow}⚠️${colors.reset} ${msg}`),
  error: (msg) => console.log(`${colors.red}❌${colors.reset} ${msg}`),
  title: (msg) => console.log(`\n${colors.bright}${colors.cyan}${msg}${colors.reset}\n`)
};

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

const question = (query) => new Promise((resolve) => rl.question(query, resolve));

/**
 * Check if required files exist
 */
const checkRequiredFiles = () => {
  log.title('🔍 Checking required files...');
  
  const requiredFiles = [
    'package.json',
    'prisma/schema.prisma',
    'src/index.ts',
    '.env.example'
  ];
  
  const missingFiles = [];
  
  requiredFiles.forEach(file => {
    if (fs.existsSync(file)) {
      log.success(`Found: ${file}`);
    } else {
      log.error(`Missing: ${file}`);
      missingFiles.push(file);
    }
  });
  
  if (missingFiles.length > 0) {
    log.error('Some required files are missing. Please ensure you are in the correct directory.');
    process.exit(1);
  }
};

/**
 * Install npm dependencies
 */
const installDependencies = async () => {
  log.title('📦 Installing dependencies...');
  
  const packageUpdatePath = './package-update.json';
  
  if (!fs.existsSync(packageUpdatePath)) {
    log.error('package-update.json not found. Please ensure the news module files are properly set up.');
    return false;
  }
  
  try {
    const packageUpdate = JSON.parse(fs.readFileSync(packageUpdatePath, 'utf8'));
    const packageJson = JSON.parse(fs.readFileSync('./package.json', 'utf8'));
    
    // Add new dependencies
    packageJson.dependencies = { ...packageJson.dependencies, ...packageUpdate.newDependencies };
    packageJson.devDependencies = { ...packageJson.devDependencies, ...packageUpdate.newDevDependencies };
    packageJson.scripts = { ...packageJson.scripts, ...packageUpdate.scripts };
    
    // Write updated package.json
    fs.writeFileSync('./package.json', JSON.stringify(packageJson, null, 2));
    log.success('Updated package.json with new dependencies');
    
    // Install dependencies
    log.info('Installing npm packages...');
    execSync('npm install', { stdio: 'inherit' });
    log.success('Dependencies installed successfully');
    
    return true;
  } catch (error) {
    log.error(`Failed to install dependencies: ${error.message}`);
    return false;
  }
};

/**
 * Setup environment variables
 */
const setupEnvironment = async () => {
  log.title('🔧 Setting up environment variables...');
  
  const envPath = './.env';
  const envExamplePath = './.env.example';
  
  if (!fs.existsSync(envPath)) {
    if (fs.existsSync(envExamplePath)) {
      const copyEnv = await question('No .env file found. Copy from .env.example? (y/n): ');
      if (copyEnv.toLowerCase() === 'y') {
        fs.copyFileSync(envExamplePath, envPath);
        log.success('Created .env file from .env.example');
        log.warning('Please update the .env file with your actual configuration values.');
      }
    } else {
      log.warning('No .env.example file found. Please create .env manually.');
    }
  } else {
    log.success('.env file already exists');
  }
  
  // Check for required news module environment variables
  if (fs.existsSync(envPath)) {
    const envContent = fs.readFileSync(envPath, 'utf8');
    const requiredVars = [
      'N8N_WEBHOOK_API_KEY',
      'TTS_SERVICE_URL',
      'TTS_API_KEY'
    ];
    
    const missingVars = requiredVars.filter(varName => !envContent.includes(varName));
    
    if (missingVars.length > 0) {
      log.warning(`Missing environment variables: ${missingVars.join(', ')}`);
      log.info('Please add these variables to your .env file.');
    } else {
      log.success('All required environment variables are present');
    }
  }
};

/**
 * Run database migrations
 */
const runMigrations = async () => {
  log.title('🗄️ Setting up database...');
  
  try {
    // Generate Prisma client
    log.info('Generating Prisma client...');
    execSync('npx prisma generate', { stdio: 'inherit' });
    log.success('Prisma client generated');
    
    // Run migrations
    log.info('Running database migrations...');
    execSync('npx prisma migrate deploy', { stdio: 'inherit' });
    log.success('Database migrations completed');
    
    return true;
  } catch (error) {
    log.error(`Database setup failed: ${error.message}`);
    log.info('Please ensure your database is running and DATABASE_URL is correctly set in .env');
    return false;
  }
};

/**
 * Seed the database
 */
const seedDatabase = async () => {
  log.title('🌱 Seeding database...');
  
  const seedPath = './prisma/seeds/news-seed.ts';
  
  if (!fs.existsSync(seedPath)) {
    log.warning('News seed file not found. Skipping database seeding.');
    return true;
  }
  
  try {
    const runSeed = await question('Run database seeding with sample data? (y/n): ');
    if (runSeed.toLowerCase() === 'y') {
      log.info('Running news module seed...');
      execSync('npx ts-node prisma/seeds/news-seed.ts', { stdio: 'inherit' });
      log.success('Database seeded successfully');
    } else {
      log.info('Skipping database seeding');
    }
    return true;
  } catch (error) {
    log.error(`Database seeding failed: ${error.message}`);
    log.info('You can run seeding manually later with: npm run news:seed');
    return false;
  }
};

/**
 * Run tests
 */
const runTests = async () => {
  log.title('🧪 Running tests...');
  
  try {
    const runTestsNow = await question('Run news module tests? (y/n): ');
    if (runTestsNow.toLowerCase() === 'y') {
      log.info('Running tests...');
      execSync('npm test -- src/tests/news.test.ts', { stdio: 'inherit' });
      log.success('All tests passed!');
    } else {
      log.info('Skipping tests. You can run them later with: npm test');
    }
    return true;
  } catch (error) {
    log.warning(`Some tests failed: ${error.message}`);
    log.info('This is normal for a fresh setup. Please review and fix any configuration issues.');
    return false;
  }
};

/**
 * Display setup completion summary
 */
const displaySummary = () => {
  log.title('🎉 News Module Setup Complete!');
  
  console.log(`${colors.bright}Next Steps:${colors.reset}`);
  console.log('1. Review and update your .env file with actual API keys and configuration');
  console.log('2. Ensure your database is running and accessible');
  console.log('3. Start the development server: npm run dev');
  console.log('4. Test the news API endpoints using the provided documentation');
  
  console.log(`\n${colors.bright}Available Scripts:${colors.reset}`);
  console.log('• npm run news:migrate  - Run database migrations');
  console.log('• npm run news:generate - Generate Prisma client');
  console.log('• npm run news:seed     - Seed database with sample data');
  console.log('• npm run news:setup    - Run all setup steps');
  
  console.log(`\n${colors.bright}Documentation:${colors.reset}`);
  console.log('• API Documentation: ./docs/NEWS_MODULE.md');
  console.log('• Test Examples: ./src/tests/news.test.ts');
  
  console.log(`\n${colors.bright}API Endpoints:${colors.reset}`);
  console.log('• GET  /api/news/articles     - List articles');
  console.log('• POST /api/news/articles     - Create article');
  console.log('• GET  /api/news/sources      - List news sources');
  console.log('• POST /api/news/broadcasts/schedule - Schedule broadcast');
  console.log('• GET  /api/news/analytics    - View analytics');
  console.log('• POST /api/news/webhook/n8n  - N8N webhook endpoint');
  
  console.log(`\n${colors.green}${colors.bright}Happy coding! 🚀${colors.reset}`);
};

/**
 * Main setup function
 */
const main = async () => {
  try {
    console.log(`${colors.bright}${colors.magenta}`);
    console.log('╔══════════════════════════════════════════════════════════════╗');
    console.log('║                    MaâtCore News Module                     ║');
    console.log('║                      Setup Assistant                        ║');
    console.log('╚══════════════════════════════════════════════════════════════╝');
    console.log(colors.reset);
    
    // Check if user wants to proceed
    const proceed = await question('\nThis will set up the News Module for MaâtCore. Continue? (y/n): ');
    if (proceed.toLowerCase() !== 'y') {
      log.info('Setup cancelled by user.');
      process.exit(0);
    }
    
    // Run setup steps
    checkRequiredFiles();
    
    const depsInstalled = await installDependencies();
    if (!depsInstalled) {
      log.error('Failed to install dependencies. Please fix the issues and try again.');
      process.exit(1);
    }
    
    await setupEnvironment();
    
    const migrationsSuccess = await runMigrations();
    if (!migrationsSuccess) {
      log.warning('Database setup failed. You may need to configure your database connection.');
    }
    
    if (migrationsSuccess) {
      await seedDatabase();
    }
    
    await runTests();
    
    displaySummary();
    
  } catch (error) {
    log.error(`Setup failed: ${error.message}`);
    process.exit(1);
  } finally {
    rl.close();
  }
};

// Run setup if called directly
if (require.main === module) {
  main();
}

module.exports = {
  checkRequiredFiles,
  installDependencies,
  setupEnvironment,
  runMigrations,
  seedDatabase,
  runTests
};