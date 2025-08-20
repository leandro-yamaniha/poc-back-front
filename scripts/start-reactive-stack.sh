#!/bin/bash

# Beauty Salon Reactive Stack Startup Script
# This script starts the complete reactive stack: Cassandra + Java Backend + React Frontend

set -e

echo "🚀 Starting Beauty Salon Reactive Stack..."
echo "================================================"

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
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    print_error "docker-compose is not installed. Please install it first."
    exit 1
fi

print_status "Stopping any existing containers..."
docker-compose -f docker-compose-reactive.yml down

print_status "Building and starting services..."
docker-compose -f docker-compose-reactive.yml up --build -d

print_status "Waiting for services to be healthy..."

# Wait for Cassandra
print_status "Waiting for Cassandra to be ready..."
timeout=300
counter=0
while ! docker-compose -f docker-compose-reactive.yml exec -T cassandra cqlsh -e 'describe keyspaces' > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        print_error "Cassandra failed to start within $timeout seconds"
        exit 1
    fi
    sleep 5
    counter=$((counter + 5))
    echo -n "."
done
print_success "Cassandra is ready!"

# Initialize Cassandra schema
print_status "Initializing Cassandra schema..."
docker-compose -f docker-compose-reactive.yml exec -T cassandra cqlsh -f /docker-entrypoint-initdb.d/01-keyspace.cql
docker-compose -f docker-compose-reactive.yml exec -T cassandra cqlsh -f /docker-entrypoint-initdb.d/02-tables.cql
docker-compose -f docker-compose-reactive.yml exec -T cassandra cqlsh -f /docker-entrypoint-initdb.d/03-sample-data.cql
print_success "Cassandra schema initialized!"

# Wait for Backend
print_status "Waiting for Java Reactive Backend..."
timeout=180
counter=0
while ! curl -f http://localhost:8085/actuator/health > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        print_error "Backend failed to start within $timeout seconds"
        exit 1
    fi
    sleep 5
    counter=$((counter + 5))
    echo -n "."
done
print_success "Java Reactive Backend is ready!"

# Wait for Frontend
print_status "Waiting for React Frontend..."
timeout=120
counter=0
while ! curl -f http://localhost:3000 > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        print_error "Frontend failed to start within $timeout seconds"
        exit 1
    fi
    sleep 5
    counter=$((counter + 5))
    echo -n "."
done
print_success "React Frontend is ready!"

echo ""
echo "================================================"
print_success "🎉 Beauty Salon Reactive Stack is running!"
echo "================================================"
echo ""
echo "📋 Service URLs:"
echo "   • Frontend:  http://localhost:3000"
echo "   • Backend:   http://localhost:8085"
echo "   • Health:    http://localhost:8085/actuator/health"
echo "   • Cassandra: localhost:9042"
echo ""
echo "📊 Container Status:"
docker-compose -f docker-compose-reactive.yml ps
echo ""
echo "🔍 To view logs:"
echo "   docker-compose -f docker-compose-reactive.yml logs -f [service-name]"
echo ""
echo "🛑 To stop all services:"
echo "   docker-compose -f docker-compose-reactive.yml down"
echo ""
print_success "Setup complete! Your reactive beauty salon app is ready to use."
