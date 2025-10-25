#!/bin/bash
# Script para gerar configuração específica do Cassandra usando o agent do GraalVM

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}▶ Geração seletiva de configurações para Cassandra e Netty${NC}"
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

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Check if native-image-agent is available
if ! java --help-extra | grep -q "native-image-agent"; then
    print_error "native-image-agent não está disponível no seu JVM"
    echo "Instale o GraalVM com suporte a native-image:"
    echo "  gu install native-image"
    exit 1
fi

# Create filter file for selective tracing
print_step "Criando arquivo de filtro para tracing seletivo..."
FILTER_FILE="$REACTIVE_DIR/cassandra-filter.json"

cat > "$FILTER_FILE" << EOL
{
  "rules": [
    {"includeClasses": "com.datastax.oss.driver.**"},
    {"includeClasses": "io.netty.**"},
    {"excludeClasses": "**"}
  ]
}
EOL

print_success "Arquivo de filtro criado: $FILTER_FILE"

# Create output directory
OUTPUT_DIR="$REACTIVE_DIR/src/main/resources/META-INF/native-image/cassandra-trace"
mkdir -p "$OUTPUT_DIR"

# Build the application
print_step "Construindo aplicação para tracing..."
cd "$REACTIVE_DIR"
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    print_error "Falha ao construir a aplicação"
    exit 1
fi

# Run with agent to collect trace information
print_step "Executando aplicação com native-image-agent (isso pode levar algum tempo)..."
print_warning "A aplicação tentará se conectar ao Cassandra. Certifique-se que o Cassandra está rodando ou ajuste os parâmetros conforme necessário."
echo ""

java -agentlib:native-image-agent=config-output-dir="$OUTPUT_DIR",caller-filter-file="$FILTER_FILE" \
    -jar target/beauty-salon-reactive-1.0.0.jar \
    --spring.profiles.active=docker \
    --server.port=0 \
    --spring.data.cassandra.contact-points=localhost &

PID=$!

# Wait for the application to start and capture some behavior
print_step "Aguardando aplicação inicializar (15 segundos)..."
sleep 15

# Send a termination signal
print_step "Finalizando aplicação para gerar arquivos de configuração..."
kill $PID || true
sleep 2

if [ -f "$OUTPUT_DIR/reflect-config.json" ]; then
    print_success "Configurações geradas com sucesso em: $OUTPUT_DIR"
    echo ""
    echo "Arquivos gerados:"
    ls -l "$OUTPUT_DIR"
    echo ""
    echo "Agora você pode usar estes arquivos para completar sua configuração:"
    echo "1. Mescle o arquivo $OUTPUT_DIR/reflect-config.json com src/main/resources/META-INF/native-image/reflect-config.json"
    echo "2. Revise os arquivos de configuração para otimização"
else
    print_error "Falha ao gerar arquivos de configuração"
fi
