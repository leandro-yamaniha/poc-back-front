#!/bin/bash
# Spring Boot AOT Native Build Script for macOS

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Spring Boot AOT Native Build"
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

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_step "Verificando pré-requisitos..."
    
    # Check Java version
    if ! java -version 2>&1 | grep -q "21"; then
        print_error "Java 21 é necessário"
        exit 1
    fi
    
    # Check native-image
    if ! command -v native-image &> /dev/null; then
        print_error "native-image não encontrado. Execute: gu install native-image"
        exit 1
    fi
    
    print_success "Pré-requisitos verificados"
}

# Step 1: Compile and Process AOT
process_aot() {
    print_step "Step 1: Compiling and Processing Spring Boot AOT with Native Profile..."
    cd "$REACTIVE_DIR"
    
    # Build with Native profile (includes AOT processing)
    ./mvnw clean compile spring-boot:process-aot -Pnative -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "AOT processing completed with Native profile"
    else
        print_error "AOT processing failed"
        exit 1
    fi
}

# Step 2: Build Native Image
build_native() {
    print_step "Step 2: Building Native Image with Native Profile..."
    cd "$REACTIVE_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-Xms4g"
    
    # Use Maven Native profile which includes all native configuration
    ./mvnw package -Pnative -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Native image built successfully with Native profile!"
    else
        print_error "Native image build failed"
        exit 1
    fi
}

# Step 3: Verify Build
verify_build() {
    print_step "Step 3: Verifying Native Image..."
    cd "$REACTIVE_DIR"
    
    if [ -f "target/beauty-salon-reactive" ]; then
        print_success "Native image created: target/beauty-salon-reactive"
        
        # Show file info
        echo ""
        echo "📊 File Information:"
        ls -lh target/beauty-salon-reactive
        file target/beauty-salon-reactive
        
        # Calculate size
        NATIVE_SIZE=$(stat -f%z target/beauty-salon-reactive 2>/dev/null || stat -c%s target/beauty-salon-reactive)
        NATIVE_SIZE_MB=$((NATIVE_SIZE / 1024 / 1024))
        
        echo ""
        echo "📦 Size: ${NATIVE_SIZE_MB}MB"
        
    else
        print_error "Native image not found!"
        exit 1
    fi
}

# Step 4: Quick Test
quick_test() {
    print_step "Step 4: Quick Startup Test..."
    cd "$REACTIVE_DIR"
    
    print_warning "Testing startup (will timeout after 10s)..."
    
    timeout 10s ./target/beauty-salon-reactive \
        --spring.profiles.active=test \
        --server.port=8085 \
        --logging.level.root=ERROR \
        2>&1 | head -20 || true
    
    print_success "Quick test completed"
}

# Show results
show_results() {
    cd "$REACTIVE_DIR"
    
    echo ""
    echo "================================================"
    echo "  AOT NATIVE BUILD RESULTS"
    echo "================================================"
    
    if [ -f "target/beauty-salon-reactive-1.0.0.jar" ]; then
        echo "📦 JAR Original:"
        ls -lh target/beauty-salon-reactive-1.0.0.jar
        JAR_SIZE=$(stat -f%z target/beauty-salon-reactive-1.0.0.jar 2>/dev/null || stat -c%s target/beauty-salon-reactive-1.0.0.jar)
    fi
    
    if [ -f "target/beauty-salon-reactive" ]; then
        echo ""
        echo "🚀 Native Executable (AOT):"
        ls -lh target/beauty-salon-reactive
        NATIVE_SIZE=$(stat -f%z target/beauty-salon-reactive 2>/dev/null || stat -c%s target/beauty-salon-reactive)
        
        if [ ! -z "$JAR_SIZE" ] && [ ! -z "$NATIVE_SIZE" ]; then
            RATIO=$(echo "scale=2; $NATIVE_SIZE / $JAR_SIZE" | bc)
            JAR_MB=$((JAR_SIZE / 1024 / 1024))
            NATIVE_MB=$((NATIVE_SIZE / 1024 / 1024))
            
            echo ""
            echo "📈 Comparison:"
            echo "  JAR:    ${JAR_MB}MB"
            echo "  Native: ${NATIVE_MB}MB"
            echo "  Ratio:  ${RATIO}x"
        fi
        
        echo ""
        echo "✅ To run:"
        echo "  cd $REACTIVE_DIR"
        echo "  ./target/beauty-salon-reactive --server.port=8085"
        echo ""
        echo "🔧 Recommended settings:"
        echo "  --spring.profiles.active=docker"
        echo "  --spring.data.cassandra.contact-points=localhost"
        echo "  --server.port=8085"
        echo ""
        echo "🚀 Expected benefits:"
        echo "  - Instant startup (< 1 second)"
        echo "  - 50-80% less memory"
        echo "  - No JVM overhead"
        echo "  - Standalone executable"
    fi
    
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Starting Spring Boot AOT Native Build..."
    echo "Directory: $REACTIVE_DIR"
    echo ""
    
    check_prerequisites
    process_aot
    build_native
    verify_build
    quick_test
    show_results
    
    print_success "AOT Native Build Complete!"
}

# Execute main function
main "$@"
