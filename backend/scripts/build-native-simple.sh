#!/bin/bash
# Simple Native Build for macOS - Java Reactive Backend

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Simple Native Build (macOS)"
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

# Build JAR first
build_jar() {
    print_step "Construindo JAR..."
    cd "$REACTIVE_DIR"
    ./mvnw clean package -DskipTests
    
    if [ ! -f "target/beauty-salon-reactive-1.0.0.jar" ]; then
        print_error "JAR não encontrado"
        exit 1
    fi
    
    print_success "JAR construído: target/beauty-salon-reactive-1.0.0.jar"
}

# Build native using direct native-image command
build_native_direct() {
    print_step "Construindo imagem nativa diretamente..."
    cd "$REACTIVE_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-Xms4g"
    
    # Build with direct native-image command
    native-image \
        -jar target/beauty-salon-reactive-1.0.0.jar \
        --no-fallback \
        --install-exit-handlers \
        --enable-url-protocols=http,https \
        --enable-all-security-services \
        --allow-incomplete-classpath \
        -H:+ReportExceptionStackTraces \
        -H:+AddAllCharsets \
        -H:IncludeResources='.*\.properties$' \
        -H:IncludeResources='.*\.yml$' \
        -H:IncludeResources='.*\.yaml$' \
        -H:IncludeResources='META-INF/.*' \
        --initialize-at-build-time=org.slf4j \
        --initialize-at-run-time=io.netty \
        --initialize-at-run-time=com.datastax.oss.driver \
        --initialize-at-run-time=reactor.core.scheduler \
        --initialize-at-run-time=reactor.netty \
        -H:+OptimizeForSize \
        -H:+RemoveUnusedSymbols \
        -H:Name=beauty-salon-reactive-native \
        --verbose
    
    if [ $? -eq 0 ]; then
        print_success "Imagem nativa construída com sucesso!"
        
        # Check if binary exists
        if [ -f "beauty-salon-reactive-native" ]; then
            print_success "Executável nativo criado: beauty-salon-reactive-native"
            
            # Show file info
            ls -lh beauty-salon-reactive-native
            file beauty-salon-reactive-native
            
            # Move to target directory
            mkdir -p target
            mv beauty-salon-reactive-native target/
            print_success "Executável movido para: target/beauty-salon-reactive-native"
            
        else
            print_error "Executável nativo não encontrado"
            exit 1
        fi
    else
        print_error "Falha na construção da imagem nativa"
        exit 1
    fi
}

# Test the native binary
test_native() {
    print_step "Testando executável nativo..."
    cd "$REACTIVE_DIR"
    
    if [ -f "target/beauty-salon-reactive-native" ]; then
        # Quick version check
        timeout 5s ./target/beauty-salon-reactive-native --version 2>/dev/null || true
        
        # Quick help check
        timeout 5s ./target/beauty-salon-reactive-native --help 2>/dev/null || true
        
        print_success "Teste básico concluído"
    fi
}

# Show results
show_results() {
    cd "$REACTIVE_DIR"
    
    echo ""
    echo "================================================"
    echo "  RESULTADOS DO BUILD NATIVO"
    echo "================================================"
    
    if [ -f "target/beauty-salon-reactive-1.0.0.jar" ]; then
        echo "📦 JAR Original:"
        ls -lh target/beauty-salon-reactive-1.0.0.jar
    fi
    
    if [ -f "target/beauty-salon-reactive-native" ]; then
        echo ""
        echo "🚀 Executável Nativo:"
        ls -lh target/beauty-salon-reactive-native
        echo ""
        echo "📊 Informações do arquivo:"
        file target/beauty-salon-reactive-native
        echo ""
        echo "✅ Para executar:"
        echo "  cd $REACTIVE_DIR"
        echo "  ./target/beauty-salon-reactive-native"
        echo ""
        echo "🔧 Configurações recomendadas:"
        echo "  --spring.profiles.active=docker"
        echo "  --spring.data.cassandra.contact-points=localhost"
        echo "  --server.port=8085"
    fi
    
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Iniciando build nativo simples para macOS..."
    echo "Diretório: $REACTIVE_DIR"
    echo ""
    
    build_jar
    build_native_direct
    test_native
    show_results
    
    print_success "Build nativo concluído!"
}

# Execute main function
main "$@"
