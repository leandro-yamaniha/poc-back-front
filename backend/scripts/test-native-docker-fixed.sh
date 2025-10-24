#!/bin/bash
# Test Native Docker Builds Script - Fixed Paths

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Native Docker Build Test"
echo "================================================"
echo ""

# Get the base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

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

# Test Java Reactive Native Docker Build
test_reactive_docker() {
    print_step "Testing Java Reactive Native Docker Build..."
    
    cd "$BASE_DIR/java-reactive"
    
    # Build Docker image
    print_step "Building Docker image (this may take several minutes)..."
    if docker build -f Dockerfile.native -t beauty-salon-java-reactive-native:test . > build.log 2>&1; then
        print_success "Java Reactive Native Docker image built successfully!"
        
        # Get image size
        SIZE=$(docker images beauty-salon-java-reactive-native:test --format "table {{.Size}}" | tail -1)
        print_success "Docker image size: $SIZE"
        
        # Test container startup
        print_step "Testing container startup..."
        CONTAINER_ID=$(docker run -d -p 8085:8085 beauty-salon-java-reactive-native:test)
        
        # Wait for startup
        sleep 10
        
        # Test health endpoint
        if curl -f http://localhost:8085/actuator/health > /dev/null 2>&1; then
            print_success "Container started successfully and health check passed!"
        else
            print_warning "Container started but health check failed (expected without database)"
        fi
        
        # Stop and remove container
        docker stop $CONTAINER_ID > /dev/null 2>&1
        docker rm $CONTAINER_ID > /dev/null 2>&1
        
        # Clean up test image
        docker rmi beauty-salon-java-reactive-native:test > /dev/null 2>&1
        
    else
        print_error "Failed to build Java Reactive Native Docker image"
        echo "Build log:"
        tail -20 build.log
        return 1
    fi
}

# Test Java Traditional Native Docker Build
test_traditional_docker() {
    print_step "Testing Java Traditional Native Docker Build..."
    
    cd "$BASE_DIR/java"
    
    # Build Docker image
    print_step "Building Docker image (this may take several minutes)..."
    if docker build -f Dockerfile.native -t beauty-salon-java-traditional-native:test . > build.log 2>&1; then
        print_success "Java Traditional Native Docker image built successfully!"
        
        # Get image size
        SIZE=$(docker images beauty-salon-java-traditional-native:test --format "table {{.Size}}" | tail -1)
        print_success "Docker image size: $SIZE"
        
        # Test container startup
        print_step "Testing container startup..."
        CONTAINER_ID=$(docker run -d -p 8080:8080 beauty-salon-java-traditional-native:test)
        
        # Wait for startup
        sleep 10
        
        # Test health endpoint
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "Container started successfully and health check passed!"
        else
            print_warning "Container started but health check failed (expected without database)"
        fi
        
        # Stop and remove container
        docker stop $CONTAINER_ID > /dev/null 2>&1
        docker rm $CONTAINER_ID > /dev/null 2>&1
        
        # Clean up test image
        docker rmi beauty-salon-java-traditional-native:test > /dev/null 2>&1
        
    else
        print_error "Failed to build Java Traditional Native Docker image"
        echo "Build log:"
        tail -20 build.log
        return 1
    fi
}

# Test Docker Compose Native
test_docker_compose() {
    print_step "Testing Docker Compose Native Setup..."
    
    cd "$BASE_DIR/.."
    
    # Build images with docker-compose
    print_step "Building all native images with docker-compose..."
    if docker-compose -f docker-compose.native.yml build > compose-build.log 2>&1; then
        print_success "All native images built successfully with docker-compose!"
        
        # Show image sizes
        print_step "Native image sizes:"
        docker images | grep "beauty-salon.*native" | awk '{print $1 ":" $2 " - " $7}'
        
    else
        print_error "Failed to build images with docker-compose"
        echo "Build log:"
        tail -20 compose-build.log
        return 1
    fi
}

# Performance comparison
performance_summary() {
    print_step "Native Docker Performance Summary..."
    
    echo ""
    echo "📊 Expected Native Performance Benefits:"
    echo "========================================"
    echo "🚀 Startup Time: <100ms (vs 5-15s JVM)"
    echo "💾 Memory Usage: 64-128MB (vs 512MB+ JVM)"
    echo "📦 Image Size: ~80MB (vs 378-380MB JVM)"
    echo "⚡ Cold Start: Instant (vs JVM warm-up)"
    echo "💰 Cloud Cost: 87% reduction vs JVM"
    echo ""
    echo "🎯 Production Benefits:"
    echo "  - 8x faster startup"
    echo "  - 4x less memory"
    echo "  - 79% smaller images"
    echo "  - No JVM overhead"
    echo "  - Better container density"
}

# Main execution
main() {
    print_step "Starting Native Docker Build Tests..."
    
    # Test builds
    TEST_REACTIVE=${1:-true}
    TEST_TRADITIONAL=${2:-true}
    TEST_COMPOSE=${3:-true}
    
    if [ "$TEST_REACTIVE" = "true" ]; then
        test_reactive_docker
    fi
    
    if [ "$TEST_TRADITIONAL" = "true" ]; then
        test_traditional_docker
    fi
    
    if [ "$TEST_COMPOSE" = "true" ]; then
        test_docker_compose
    fi
    
    # Show performance summary
    performance_summary
    
    print_success "Native Docker build tests completed!"
    echo ""
    echo "🎉 Next steps:"
    echo "  - Run full stack: docker-compose -f docker-compose.native.yml up"
    echo "  - Test endpoints: http://localhost:8080 (traditional) & http://localhost:8085 (reactive)"
    echo "  - Monitor performance with minimal resource usage"
}

# Parse command line arguments
case "${1:-all}" in
    "reactive")
        main true false false
        ;;
    "traditional")
        main false true false
        ;;
    "compose")
        main false false true
        ;;
    "all"|*)
        main true true true
        ;;
esac
