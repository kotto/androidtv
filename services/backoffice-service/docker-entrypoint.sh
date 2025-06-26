#!/bin/sh
set -e

# Function to wait for database
wait_for_db() {
  echo "Waiting for database to be ready..."
  
  until npx prisma db push --accept-data-loss 2>/dev/null; do
    echo "Database is unavailable - sleeping"
    sleep 2
  done
  
  echo "Database is ready!"
}

# Function to run database migrations
run_migrations() {
  echo "Running database migrations..."
  npx prisma migrate deploy
  echo "Migrations completed!"
}

# Function to generate Prisma client
generate_client() {
  echo "Generating Prisma client..."
  npx prisma generate
  echo "Prisma client generated!"
}

# Wait for database if DATABASE_URL is set
if [ -n "$DATABASE_URL" ]; then
  wait_for_db
  run_migrations
fi

# Generate Prisma client
generate_client

# Start the application
echo "Starting Backoffice Service..."
exec node dist/index.js