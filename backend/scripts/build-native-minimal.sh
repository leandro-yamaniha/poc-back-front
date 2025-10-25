#!/bin/bash
# Minimal Native Build for macOS - Java Reactive Backend

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Minimal Native Build (macOS)"
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

# Build native with minimal options
build_native_minimal() {
    print_step "Construindo imagem nativa com configurações mínimas..."
    cd "$REACTIVE_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-Xms4g"
    
    # Build with minimal native-image options
    native-image \
        -jar target/beauty-salon-reactive-1.0.0.jar \
        --no-fallback \
        --enable-url-protocols=http,https \
        --allow-incomplete-classpath \
        -H:+ReportExceptionStackTraces \
        -H:+AddAllCharsets \
        --initialize-at-run-time=io.netty \
        --initialize-at-run-time=com.datastax.oss.driver \
        --initialize-at-run-time=reactor.core.scheduler \
        --initialize-at-run-time=reactor.netty \
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
        # Quick startup test
        print_step "Teste de startup rápido (5 segundos)..."
        timeout 5s ./target/beauty-salon-reactive-native \
            --spring.profiles.active=test \
            --spring.data.cassandra.contact-points=localhost \
            --spring.data.cassandra.port=9042 \
            --spring.data.cassandra.keyspace-name=test \
            --spring.data.cassandra.local-datacenter=datacenter1 \
            --logging.level.root=ERROR \
            --server.port=8085 \
            2>/dev/null || true
        
        print_success "Teste básico concluído"
    fi
}

# Show results and comparison
show_results() {
    cd "$REACTIVE_DIR"
    
    echo ""
    echo "================================================"
    echo "  RESULTADOS DO BUILD NATIVO MINIMAL"
    echo "================================================"
    
    if [ -f "target/beauty-salon-reactive-1.0.0.jar" ]; then
        echo "📦 JAR Original:"
        ls -lh target/beauty-salon-reactive-1.0.0.jar
        JAR_SIZE=$(stat -f%z target/beauty-salon-reactive-1.0.0.jar)
    fi
    
    if [ -f "target/beauty-salon-reactive-native" ]; then
        echo ""
        echo "🚀 Executável Nativo:"
        ls -lh target/beauty-salon-reactive-native
        NATIVE_SIZE=$(stat -f%z target/beauty-salon-reactive-native)
        
        echo ""
        echo "📊 Informações do arquivo:"
        file target/beauty-salon-reactive-native
        
        # Calculate size comparison
        if [ ! -z "$JAR_SIZE" ] && [ ! -z "$NATIVE_SIZE" ]; then
            RATIO=$(echo "scale=2; $NATIVE_SIZE / $JAR_SIZE" | bc)
            echo ""
            echo "📈 Comparação de tamanho:"
            echo "  JAR:    $(numfmt --to=iec $JAR_SIZE)"
            echo "  Native: $(numfmt --to=iec $NATIVE_SIZE)"
            echo "  Ratio:  ${RATIO}x"
        fi
        
        echo ""
        echo "✅ Para executar:"
        echo "  cd $REACTIVE_DIR"
        echo "  ./target/beauty-salon-reactive-native"
        echo ""
        echo "🔧 Configurações recomendadas:"
        echo "  --spring.profiles.active=docker"
        echo "  --spring.data.cassandra.contact-points=localhost"
        echo "  --server.port=8085"
        echo ""
        echo "🚀 Benefícios esperados:"
        echo "  - Startup 3-5x mais rápido"
        echo "  - Menor uso de memória"
        echo "  - Sem JVM overhead"
        echo "  - Executável standalone"
    fi
    
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Iniciando build nativo minimal para macOS..."
    echo "Diretório: $REACTIVE_DIR"
    echo ""
    
    build_jar
    build_native_minimal
    test_native
    show_results
    
    print_success "Build nativo minimal concluído!"
}

# Execute main function
main "$@"
