#!/bin/bash
# Direct Native Build for macOS - Java Reactive Backend (using main class directly)

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Direct Native Build (macOS)"
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

# Extract JAR contents for direct class access
extract_jar() {
    print_step "Extraindo conteúdo do JAR..."
    cd "$REACTIVE_DIR"
    
    # Create extraction directory
    rm -rf target/extracted
    mkdir -p target/extracted
    
    # Extract JAR
    cd target/extracted
    jar -xf ../beauty-salon-reactive-1.0.0.jar
    
    print_success "JAR extraído para target/extracted"
}

# Build native using direct main class
build_native_direct() {
    print_step "Construindo imagem nativa usando classe principal direta..."
    cd "$REACTIVE_DIR"
    
    # Set memory for native-image build
    export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-Xms4g"
    
    # Build classpath from extracted JAR
    CLASSPATH="target/extracted/BOOT-INF/classes"
    for jar in target/extracted/BOOT-INF/lib/*.jar; do
        CLASSPATH="$CLASSPATH:$jar"
    done
    
    # Build with direct native-image command using main class
    native-image \
        -cp "$CLASSPATH" \
        com.beautysalon.reactive.BeautySalonReactiveApplication \
        --no-fallback \
        --enable-url-protocols=http,https \
        -H:+ReportExceptionStackTraces \
        -H:+AddAllCharsets \
        --initialize-at-run-time=io.netty \
        --initialize-at-run-time=com.datastax.oss.driver \
        --initialize-at-run-time=reactor.core.scheduler \
        --initialize-at-run-time=reactor.netty \
        --initialize-at-run-time=org.springframework.boot.context.config \
        -H:+UnlockExperimentalVMOptions \
        -H:IncludeResources='.*\.properties$' \
        -H:IncludeResources='.*\.yml$' \
        -H:IncludeResources='.*\.yaml$' \
        -H:IncludeResources='META-INF/.*' \
        -H:Name=beauty-salon-reactive-native-direct \
        --verbose
    
    if [ $? -eq 0 ]; then
        print_success "Imagem nativa construída com sucesso!"
        
        # Check if binary exists
        if [ -f "beauty-salon-reactive-native-direct" ]; then
            print_success "Executável nativo criado: beauty-salon-reactive-native-direct"
            
            # Show file info
            ls -lh beauty-salon-reactive-native-direct
            file beauty-salon-reactive-native-direct
            
            # Move to target directory
            mkdir -p target
            mv beauty-salon-reactive-native-direct target/
            print_success "Executável movido para: target/beauty-salon-reactive-native-direct"
            
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
    
    if [ -f "target/beauty-salon-reactive-native-direct" ]; then
        # Quick startup test
        print_step "Teste de startup rápido (10 segundos)..."
        timeout 10s ./target/beauty-salon-reactive-native-direct \
            --spring.profiles.active=test \
            --spring.data.cassandra.contact-points=localhost \
            --spring.data.cassandra.port=9042 \
            --spring.data.cassandra.keyspace-name=test \
            --spring.data.cassandra.local-datacenter=datacenter1 \
            --logging.level.root=ERROR \
            --server.port=8085 \
            2>&1 | head -10 || true
        
        print_success "Teste básico concluído"
    fi
}

# Show results and comparison
show_results() {
    cd "$REACTIVE_DIR"
    
    echo ""
    echo "================================================"
    echo "  RESULTADOS DO BUILD NATIVO DIRETO"
    echo "================================================"
    
    if [ -f "target/beauty-salon-reactive-1.0.0.jar" ]; then
        echo "📦 JAR Original:"
        ls -lh target/beauty-salon-reactive-1.0.0.jar
        JAR_SIZE=$(stat -f%z target/beauty-salon-reactive-1.0.0.jar)
    fi
    
    if [ -f "target/beauty-salon-reactive-native-direct" ]; then
        echo ""
        echo "🚀 Executável Nativo Direto:"
        ls -lh target/beauty-salon-reactive-native-direct
        NATIVE_SIZE=$(stat -f%z target/beauty-salon-reactive-native-direct)
        
        echo ""
        echo "📊 Informações do arquivo:"
        file target/beauty-salon-reactive-native-direct
        
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
        echo "  ./target/beauty-salon-reactive-native-direct"
        echo ""
        echo "🔧 Configurações recomendadas:"
        echo "  --spring.profiles.active=docker"
        echo "  --spring.data.cassandra.contact-points=localhost"
        echo "  --server.port=8085"
        echo ""
        echo "🚀 Benefícios esperados:"
        echo "  - Startup instantâneo (< 1 segundo)"
        echo "  - Menor uso de memória (50-80% menos)"
        echo "  - Sem JVM overhead"
        echo "  - Executável standalone"
        echo "  - Classe principal direta (sem JarLauncher)"
    fi
    
    echo ""
    echo "================================================"
}

# Main execution
main() {
    echo "Iniciando build nativo direto para macOS..."
    echo "Diretório: $REACTIVE_DIR"
    echo ""
    
    build_jar
    extract_jar
    build_native_direct
    test_native
    show_results
    
    print_success "Build nativo direto concluído!"
}

# Execute main function
main "$@"
