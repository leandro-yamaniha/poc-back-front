#!/bin/bash
# Build AOT Native Image inside Linux container (correct target)

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  AOT Native Build in Linux Container"
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

# Build native executable inside Linux container
build_native_linux() {
    print_step "Building native executable inside Linux container..."
    cd "$REACTIVE_DIR"

    # Build using the Linux Dockerfile
    docker build -f Dockerfile.aot-build \
        -t beauty-salon-aot-builder \
        .

    if [ $? -eq 0 ]; then
        print_success "Linux build completed"
    else
        print_error "Linux build failed"
        exit 1
    fi
}

# Extract the native executable from container
extract_executable() {
    print_step "Extracting native executable from container..."

    # Create a temporary container to extract the file
    docker create --name temp-extractor beauty-salon-aot-builder

    # Copy the executable to host
    docker cp temp-extractor:/app/beauty-salon-reactive target/beauty-salon-reactive-linux

    # Clean up
    docker rm temp-extractor
    docker rmi beauty-salon-aot-builder

    # Make executable and show info
    chmod +x target/beauty-salon-reactive-linux

    print_success "Executable extracted: target/beauty-salon-reactive-linux"
    ls -lh target/beauty-salon-reactive-linux
    file target/beauty-salon-reactive-linux
}

# Test the Linux executable
test_executable() {
    print_step "Testing Linux executable..."

    # Run in a container to verify it works
    docker run --rm -d --name test-native \
        -e SPRING_PROFILES_ACTIVE=test \
        beauty-salon-aot-builder \
        --server.port=8085 \
        --logging.level.root=ERROR

    # Wait a bit
    sleep 5

    # Check if container is still running (means executable works)
    if docker ps | grep -q test-native; then
        print_success "Executable works correctly!"

        # Stop the test container
        docker stop test-native
    else
        print_error "Executable failed to run"

        # Show logs
        docker logs test-native
        docker rm test-native 2>/dev/null || true
        exit 1
    fi

    # Clean up
    docker rm test-native 2>/dev/null || true
}

# Replace the macOS executable with Linux one
replace_executable() {
    print_step "Replacing macOS executable with Linux version..."

    # Backup macOS version
    mv target/beauty-salon-reactive target/beauty-salon-reactive-macos

    # Use Linux version
    mv target/beauty-salon-reactive-linux target/beauty-salon-reactive

    print_success "Executables swapped"
    echo "  macOS version: target/beauty-salon-reactive-macos"
    echo "  Linux version: target/beauty-salon-reactive"
}

# Show results
show_results() {
    cd "$REACTIVE_DIR"

    echo ""
    echo "================================================"
    echo "  LINUX AOT BUILD RESULTS"
    echo "================================================"

    if [ -f "target/beauty-salon-reactive" ]; then
        echo "✅ Native executable (Linux):"
        ls -lh target/beauty-salon-reactive
        file target/beauty-salon-reactive
    fi

    if [ -f "target/beauty-salon-reactive-macos" ]; then
        echo ""
        echo "💻 macOS executable (backup):"
        ls -lh target/beauty-salon-reactive-macos
    fi

    echo ""
    echo "🎯 Next steps:"
    echo "  1. Rebuild Docker images: ./scripts/build-docker-native.sh"
    echo "  2. Run with Docker: docker-compose -f docker-compose.aot-native.yml up -d"
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Building AOT native executable for Linux target..."
    echo "This ensures compatibility with Docker containers."
    echo ""

    build_native_linux
    extract_executable
    test_executable
    replace_executable
    show_results

    print_success "Linux AOT build completed successfully!"
    echo ""
    echo "🎉 Your native executable is now compatible with Docker/Linux!"
}

# Execute main function
main "$@"
