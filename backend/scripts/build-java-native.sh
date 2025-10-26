#!/bin/bash
# Build script for Java Traditional Native Profile
# GraalVM Native Image compilation

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "================================================"
echo "  Building Java Traditional with Native Profile"
echo "================================================"
echo ""

# Get directories
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_DIR="$BASE_DIR/java"

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

# Build native image
build_native() {
    print_step "Building with Maven Native profile..."
    cd "$JAVA_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-Xms4g"
    
    print_warning "Native build will take 3-4 minutes..."
    echo ""
    
    ./mvnw clean package -Pnative -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Native image built successfully!"
        
        echo ""
        echo "📦 Build Artifacts:"
        ls -lh target/beauty-salon
        
        NATIVE_SIZE=$(stat -f%z target/beauty-salon 2>/dev/null || stat -c%s target/beauty-salon)
        NATIVE_MB=$((NATIVE_SIZE / 1024 / 1024))
        
        echo ""
        echo "📊 Native Executable Size: ${NATIVE_MB}MB"
        echo ""
        echo "✅ To run:"
        echo "  ./target/beauty-salon"
        echo ""
        echo "🐳 To build Docker image (prebuilt):"
        echo "  docker build -f Dockerfile.native-prebuilt -t beauty-salon:native ."
        echo ""
        echo "🚀 Performance:"
        echo "  - Startup: ~1-2 seconds (vs 5-10s JVM)"
        echo "  - Memory: ~128-256MB (vs 256-512MB JVM)"
        echo "  - No warmup required"
        echo ""
    else
        print_error "Native image build failed"
        exit 1
    fi
}

# Main execution
check_graalvm
build_native
