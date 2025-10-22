#!/bin/bash
# Test script for Docker Entrypoint Solution

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Entrypoint Solution Test"
echo "================================================"
echo ""

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Step 1: Clean previous containers
print_step "Step 1: Cleaning previous containers..."
docker-compose down -v
print_success "Cleanup complete"
echo ""

# Step 2: Rebuild images
print_step "Step 2: Rebuilding backend images with entrypoint..."
echo "This may take a few minutes..."
docker-compose build backend-nodejs backend-go backend-java
print_success "Images rebuilt"
echo ""

# Step 3: Start services
print_step "Step 3: Starting all services with docker-compose up..."
docker-compose up -d

print_success "Services started"
echo ""

# Step 4: Monitor Cassandra
print_step "Step 4: Waiting for Cassandra to be ready (40s)..."
for i in {1..40}; do
    echo -n "."
    sleep 1
done
echo ""
print_success "Cassandra should be ready"
echo ""

# Step 5: Check service status
print_step "Step 5: Checking service status..."
echo ""
docker-compose ps
echo ""

# Step 6: Show entrypoint logs
print_step "Step 6: Showing entrypoint logs for each backend..."
echo ""

echo -e "${YELLOW}=== Node.js Backend Logs ===${NC}"
docker logs beauty-salon-backend-nodejs --tail 30
echo ""

echo -e "${YELLOW}=== Go Backend Logs ===${NC}"
docker logs beauty-salon-backend-go --tail 30
echo ""

echo -e "${YELLOW}=== Java Backend Logs ===${NC}"
docker logs beauty-salon-backend --tail 30
echo ""

# Step 7: Count running backends
print_step "Step 7: Counting running backends..."
running=$(docker ps | grep "backend-" | wc -l | tr -d ' ')
total=6

echo "Running backends: $running/$total"
echo ""

if [ "$running" -eq "$total" ]; then
    print_success "ALL BACKENDS ARE RUNNING! 🎉"
    echo ""
    echo "Endpoints available:"
    echo "  - .NET:          http://localhost:8081/api/customer"
    echo "  - Python:        http://localhost:8082/api/customers"
    echo "  - Node.js:       http://localhost:8083/api/customers"
    echo "  - Go:            http://localhost:8084/api/customers"
    echo "  - Java:          http://localhost:8080/api/customers"
    echo "  - Java Reactive: http://localhost:8085/api/customers"
elif [ "$running" -ge 3 ]; then
    print_warning "Some backends running ($running/$total)"
    echo ""
    echo "Check individual logs for failures:"
    echo "  docker logs beauty-salon-backend-nodejs"
    echo "  docker logs beauty-salon-backend-go"
    echo "  docker logs beauty-salon-backend"
else
    print_error "Most backends failed ($running/$total)"
    echo ""
    echo "Troubleshooting steps:"
    echo "  1. Check Cassandra logs: docker logs beauty-salon-cassandra"
    echo "  2. Check backend logs: docker logs <backend-name>"
    echo "  3. Try: docker-compose restart cassandra && sleep 30"
fi

echo ""
echo "================================================"
echo "Test complete!"
echo "================================================"
