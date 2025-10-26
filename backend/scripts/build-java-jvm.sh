#!/bin/bash
# Build script for Java Traditional JVM Profile
# Fast build for development and testing

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "================================================"
echo "  Building Java Traditional with JVM Profile"
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

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Build with JVM profile
print_step "Building with Maven JVM profile..."
cd "$JAVA_DIR"

./mvnw clean package -Pjvm -DskipTests

if [ $? -eq 0 ]; then
    print_success "JVM build completed successfully!"
    
    echo ""
    echo "📦 Build Artifacts:"
    ls -lh target/beauty-salon-jvm.jar
    
    JAR_SIZE=$(stat -f%z target/beauty-salon-jvm.jar 2>/dev/null || stat -c%s target/beauty-salon-jvm.jar)
    JAR_MB=$((JAR_SIZE / 1024 / 1024))
    
    echo ""
    echo "📊 JAR Size: ${JAR_MB}MB"
    echo ""
    echo "✅ To run:"
    echo "  java --enable-preview -jar target/beauty-salon-jvm.jar"
    echo ""
    echo "🐳 To build Docker image:"
    echo "  docker build -f Dockerfile.jvm -t beauty-salon:jvm ."
    echo ""
else
    print_error "JVM build failed"
    exit 1
fi
