#!/bin/bash
# Build Docker images for GraalVM Native executable

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Docker Native Image Builder"
echo "================================================"
echo ""

# Get the base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REACTIVE_DIR="$BASE_DIR/java-reactive"

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if native executable exists
check_executable() {
    print_step "Checking for native executable..."
    cd "$REACTIVE_DIR"
    
    if [ ! -f "target/beauty-salon-reactive" ]; then
        print_error "Native executable not found!"
        echo "Please run: ./scripts/build-aot-native.sh first"
        exit 1
    fi
    
    print_success "Native executable found"
}

# Build Alpine-based image
build_alpine() {
    print_step "Building Alpine-based image..."
    cd "$REACTIVE_DIR"
    
    docker build -f Dockerfile.native-minimal \
        -t beauty-salon-reactive:native-alpine \
        -t beauty-salon-reactive:native-alpine-latest \
        .
    
    if [ $? -eq 0 ]; then
        print_success "Alpine image built successfully"
    else
        print_error "Alpine image build failed"
        exit 1
    fi
}

# Build Distroless image
build_distroless() {
    print_step "Building Distroless image..."
    cd "$REACTIVE_DIR"
    
    docker build -f Dockerfile.native-distroless \
        -t beauty-salon-reactive:native-distroless \
        -t beauty-salon-reactive:native-distroless-latest \
        .
    
    if [ $? -eq 0 ]; then
        print_success "Distroless image built successfully"
    else
        print_error "Distroless image build failed"
        exit 1
    fi
}

# Show image sizes
show_sizes() {
    print_step "Comparing image sizes..."
    echo ""
    
    echo "📦 Docker Images:"
    docker images | grep "beauty-salon-reactive" | grep "native"
    
    echo ""
    echo "📊 Size Comparison:"
    
    ALPINE_SIZE=$(docker images beauty-salon-reactive:native-alpine --format "{{.Size}}")
    DISTROLESS_SIZE=$(docker images beauty-salon-reactive:native-distroless --format "{{.Size}}")
    
    echo "  Alpine:     $ALPINE_SIZE"
    echo "  Distroless: $DISTROLESS_SIZE"
    
    echo ""
}

# Test images
test_images() {
    print_step "Testing images..."
    
    # Test Alpine
    print_step "Testing Alpine image..."
    docker run --rm beauty-salon-reactive:native-alpine --version || true
    
    # Test Distroless
    print_step "Testing Distroless image..."
    docker run --rm beauty-salon-reactive:native-distroless --version || true
    
    print_success "Image tests completed"
}

# Show usage instructions
show_usage() {
    echo ""
    echo "================================================"
    echo "  BUILD COMPLETE"
    echo "================================================"
    echo ""
    echo "🚀 Available Images:"
    echo "  1. beauty-salon-reactive:native-alpine (Alpine 3.19)"
    echo "  2. beauty-salon-reactive:native-distroless (Google Distroless)"
    echo ""
    echo "📝 Run with Docker:"
    echo ""
    echo "# Alpine version (with shell access)"
    echo "docker run -p 8085:8085 \\"
    echo "  -e SPRING_PROFILES_ACTIVE=docker \\"
    echo "  -e SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra \\"
    echo "  beauty-salon-reactive:native-alpine"
    echo ""
    echo "# Distroless version (ultra-secure, no shell)"
    echo "docker run -p 8085:8085 \\"
    echo "  -e SPRING_PROFILES_ACTIVE=docker \\"
    echo "  -e SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra \\"
    echo "  beauty-salon-reactive:native-distroless"
    echo ""
    echo "🔍 Inspect images:"
    echo "  docker inspect beauty-salon-reactive:native-alpine"
    echo "  docker inspect beauty-salon-reactive:native-distroless"
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Starting Docker native image build..."
    echo "Directory: $REACTIVE_DIR"
    echo ""
    
    check_executable
    build_alpine
    build_distroless
    show_sizes
    test_images
    show_usage
    
    print_success "Docker native images built successfully!"
}

# Execute main function
main "$@"
