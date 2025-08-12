#!/bin/bash

# Test migrations with Docker Compose
# This script starts Cassandra, runs migration tests, and cleans up

set -e

echo "🧪 Testing Migration System with Docker"
echo "=" | head -c 50
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    print_error "docker-compose is not installed. Please install it and try again."
    exit 1
fi

# Navigate to project root
cd "$(dirname "$0")/../.."

print_status "Starting Cassandra container..."

# Start only Cassandra service
docker-compose up -d cassandra

print_status "Waiting for Cassandra to be ready..."

# Wait for Cassandra to be ready (max 60 seconds)
TIMEOUT=60
COUNTER=0

while [ $COUNTER -lt $TIMEOUT ]; do
    if docker-compose exec -T cassandra cqlsh -e "DESCRIBE KEYSPACES" > /dev/null 2>&1; then
        print_success "Cassandra is ready!"
        break
    fi
    
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER + 2))
done

if [ $COUNTER -ge $TIMEOUT ]; then
    print_error "Cassandra failed to start within $TIMEOUT seconds"
    print_status "Cleaning up..."
    docker-compose down
    exit 1
fi

echo ""
print_status "Running migration tests..."

# Navigate to backend-nodejs directory
cd backend-nodejs

# Run the migration test
if node test-migrations.js; then
    print_success "Migration tests passed!"
    TEST_RESULT=0
else
    print_error "Migration tests failed!"
    TEST_RESULT=1
fi

# Navigate back to project root
cd ..

print_status "Cleaning up Docker containers..."
docker-compose down

if [ $TEST_RESULT -eq 0 ]; then
    print_success "All tests completed successfully! 🎉"
    echo ""
    echo "Migration system is working correctly:"
    echo "✅ Cassandra connection established"
    echo "✅ Migration tracking table created"
    echo "✅ All table migrations executed"
    echo "✅ Sample data inserted"
    echo "✅ Data verification completed"
else
    print_error "Tests failed. Check the output above for details."
    exit 1
fi
