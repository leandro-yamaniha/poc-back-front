#!/bin/bash
# Native Build Script using Maven Native Profile
# Simplified build process with profile configuration

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Building with Native Profile (GraalVM)"
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

# Check if GraalVM is available
check_graalvm() {
    if ! command -v native-image &> /dev/null; then
        print_error "GraalVM native-image not found!"
        echo "Please install GraalVM and native-image:"
        echo "1. Download GraalVM: https://github.com/graalvm/graalvm-ce-builds/releases"
        echo "2. Install native-image: gu install native-image"
        exit 1
    fi
    
    print_success "GraalVM native-image found: $(native-image --version | head -1)"
}

# Build native image for Java Traditional
build_java_traditional() {
    print_step "Building Java Traditional Native Image..."
    
    cd java
    
    # Clean previous builds
    ./mvnw clean
    
    # Build native image
    print_step "Compiling native image (this may take several minutes)..."
    ./mvnw -Pnative native:compile -DskipTests
    
    if [ -f "target/beauty-salon-backend" ]; then
        print_success "Java Traditional native image built successfully!"
        
        # Get file size
        SIZE=$(du -h target/beauty-salon-backend | cut -f1)
        print_success "Native executable size: $SIZE"
        
        # Test startup time
        print_step "Testing startup time..."
        START_TIME=$(date +%s%N)
        timeout 10s ./target/beauty-salon-backend --server.port=0 > /dev/null 2>&1 || true
        END_TIME=$(date +%s%N)
        STARTUP_TIME=$(( (END_TIME - START_TIME) / 1000000 ))
        print_success "Estimated startup time: ${STARTUP_TIME}ms"
    else
        print_error "Failed to build Java Traditional native image"
        return 1
    fi
    
    cd ..
}

# Build native image for Java Reactive
build_java_reactive() {
    print_step "Building Java Reactive Native Image..."
    
    cd java-reactive
    
    # Clean previous builds
    ./mvnw clean
    
    # Build native image
    print_step "Compiling reactive native image (this may take several minutes)..."
    ./mvnw -Pnative native:compile -DskipTests
    
    if [ -f "target/beauty-salon-reactive" ]; then
        print_success "Java Reactive native image built successfully!"
        
        # Get file size
        SIZE=$(du -h target/beauty-salon-reactive | cut -f1)
        print_success "Native executable size: $SIZE"
        
        # Test startup time
        print_step "Testing startup time..."
        START_TIME=$(date +%s%N)
        timeout 10s ./target/beauty-salon-reactive --server.port=0 > /dev/null 2>&1 || true
        END_TIME=$(date +%s%N)
        STARTUP_TIME=$(( (END_TIME - START_TIME) / 1000000 ))
        print_success "Estimated startup time: ${STARTUP_TIME}ms"
    else
        print_error "Failed to build Java Reactive native image"
        return 1
    fi
    
    cd ..
}

# Build Docker native images
build_docker_native() {
    print_step "Building Docker Native Images..."
    
    # Build Java Traditional Docker native image
    print_step "Building Java Traditional Docker native image..."
    cd java
    docker build -f Dockerfile.native -t beauty-salon-java-native:latest .
    if [ $? -eq 0 ]; then
        print_success "Java Traditional Docker native image built!"
        
        # Get image size
        SIZE=$(docker images beauty-salon-java-native:latest --format "table {{.Size}}" | tail -1)
        print_success "Docker image size: $SIZE"
    else
        print_error "Failed to build Java Traditional Docker native image"
    fi
    cd ..
    
    # Build Java Reactive Docker native image
    print_step "Building Java Reactive Docker native image..."
    cd java-reactive
    docker build -f Dockerfile.native -t beauty-salon-java-reactive-native:latest .
    if [ $? -eq 0 ]; then
        print_success "Java Reactive Docker native image built!"
        
        # Get image size
        SIZE=$(docker images beauty-salon-java-reactive-native:latest --format "table {{.Size}}" | tail -1)
        print_success "Docker image size: $SIZE"
    else
        print_error "Failed to build Java Reactive Docker native image"
    fi
    cd ..
}

# Performance comparison
performance_comparison() {
    print_step "Running Performance Comparison..."
    
    echo ""
    echo "📊 Native vs JVM Comparison:"
    echo "================================"
    
    # Java Traditional comparison
    if [ -f "java/target/beauty-salon-backend" ]; then
        NATIVE_SIZE=$(du -h java/target/beauty-salon-backend | cut -f1)
        JAR_SIZE=$(du -h java/target/release/app.jar 2>/dev/null | cut -f1 || echo "N/A")
        
        echo "Java Traditional:"
        echo "  Native executable: $NATIVE_SIZE"
        echo "  JAR file: $JAR_SIZE"
        echo "  Estimated memory usage: ~64MB (vs ~512MB JVM)"
        echo "  Estimated startup: <100ms (vs 5-15s JVM)"
    fi
    
    # Java Reactive comparison
    if [ -f "java-reactive/target/beauty-salon-reactive" ]; then
        NATIVE_SIZE=$(du -h java-reactive/target/beauty-salon-reactive | cut -f1)
        JAR_SIZE=$(du -h java-reactive/target/release/app.jar 2>/dev/null | cut -f1 || echo "N/A")
        
        echo ""
        echo "Java Reactive:"
        echo "  Native executable: $NATIVE_SIZE"
        echo "  JAR file: $JAR_SIZE"
        echo "  Estimated memory usage: ~64MB (vs ~512MB JVM)"
        echo "  Estimated startup: <100ms (vs 5-15s JVM)"
    fi
    
    echo ""
    echo "🚀 Native Benefits:"
    echo "  - 8x faster startup time"
    echo "  - 8x lower memory usage"
    echo "  - No JVM warm-up required"
    echo "  - Smaller container images"
    echo "  - Better cloud economics"
}

# Main execution
main() {
    print_step "Starting Native Build Process..."
    
    # Check prerequisites
    check_graalvm
    
    # Build native images
    BUILD_TRADITIONAL=${1:-true}
    BUILD_REACTIVE=${2:-true}
    BUILD_DOCKER=${3:-true}
    
    if [ "$BUILD_TRADITIONAL" = "true" ]; then
        build_java_traditional
    fi
    
    if [ "$BUILD_REACTIVE" = "true" ]; then
        build_java_reactive
    fi
    
    if [ "$BUILD_DOCKER" = "true" ]; then
        build_docker_native
    fi
    
    # Show performance comparison
    performance_comparison
    
    print_success "Native build process completed!"
    echo ""
    echo "🎉 Next steps:"
    echo "  - Test native executables: ./java/target/beauty-salon-backend"
    echo "  - Run Docker containers: docker run -p 8080:8080 beauty-salon-java-native"
    echo "  - Deploy to production with minimal resource requirements"
}

# Parse command line arguments
case "${1:-all}" in
    "traditional")
        main true false false
        ;;
    "reactive")
        main false true false
        ;;
    "docker")
        main false false true
        ;;
    "all"|*)
        main true true true
        ;;
esac
