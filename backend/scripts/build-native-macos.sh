#!/bin/bash
# Build Native macOS Script for Java Reactive Backend

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Native macOS Build"
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
    if ! java -version 2>&1 | grep -q "GraalVM\|21"; then
        print_error "GraalVM Java 21 é necessário"
        exit 1
    fi
    
    # Check native-image
    if ! command -v native-image &> /dev/null; then
        print_error "native-image não encontrado. Execute: gu install native-image"
        exit 1
    fi
    
    # Check macOS
    if [[ "$OSTYPE" != "darwin"* ]]; then
        print_warning "Este script é otimizado para macOS"
    fi
    
    print_success "Pré-requisitos verificados"
}

# Clean previous builds
clean_build() {
    print_step "Limpando builds anteriores..."
    cd "$REACTIVE_DIR"
    ./mvnw clean
    print_success "Build limpo"
}

# Build JAR first
build_jar() {
    print_step "Construindo JAR..."
    cd "$REACTIVE_DIR"
    ./mvnw package -DskipTests -Pnative
    print_success "JAR construído com sucesso"
}

# Build native image
build_native() {
    print_step "Construindo imagem nativa para macOS..."
    cd "$REACTIVE_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g"
    
    # Build with Maven
    ./mvnw native:compile -Pnative -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Imagem nativa construída com sucesso!"
        
        # Check if binary exists
        if [ -f "target/beauty-salon-reactive" ]; then
            print_success "Executável nativo criado: target/beauty-salon-reactive"
            
            # Show file info
            ls -lh target/beauty-salon-reactive
            file target/beauty-salon-reactive
            
            # Test execution (quick check)
            print_step "Testando execução rápida..."
            timeout 10s ./target/beauty-salon-reactive --help || true
            
        else
            print_error "Executável nativo não encontrado"
            exit 1
        fi
    else
        print_error "Falha na construção da imagem nativa"
        exit 1
    fi
}

# Performance test
performance_test() {
    print_step "Teste de performance de startup..."
    cd "$REACTIVE_DIR"
    
    if [ -f "target/beauty-salon-reactive" ]; then
        print_step "Medindo tempo de startup..."
        
        # Measure startup time
        time timeout 5s ./target/beauty-salon-reactive \
            --spring.profiles.active=test \
            --spring.data.cassandra.contact-points=localhost \
            --spring.data.cassandra.port=9042 \
            --spring.data.cassandra.keyspace-name=test \
            --spring.data.cassandra.local-datacenter=datacenter1 \
            --logging.level.root=WARN \
            2>/dev/null || true
            
        print_success "Teste de startup concluído"
    fi
}

# Main execution
main() {
    echo "Iniciando build nativo para macOS..."
    echo "Diretório: $REACTIVE_DIR"
    echo ""
    
    check_prerequisites
    clean_build
    build_jar
    build_native
    performance_test
    
    echo ""
    print_success "Build nativo para macOS concluído com sucesso!"
    echo ""
    echo "Para executar:"
    echo "  cd $REACTIVE_DIR"
    echo "  ./target/beauty-salon-reactive"
    echo ""
    echo "Configurações recomendadas:"
    echo "  --spring.profiles.active=docker"
    echo "  --spring.data.cassandra.contact-points=localhost"
    echo "  --server.port=8085"
    echo ""
}

# Execute main function
main "$@"
