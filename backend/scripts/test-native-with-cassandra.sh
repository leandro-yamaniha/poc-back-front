#!/bin/bash
# Script para testar o executável nativo com Cassandra

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}▶ Testando executável nativo com Cassandra${NC}"
echo ""

# Get the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
BACKEND_DIR="$BASE_DIR/backend"
REACTIVE_DIR="$BACKEND_DIR/java-reactive"

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

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker não está rodando!"
    echo "Por favor, inicie o Docker Desktop e tente novamente."
    exit 1
fi

print_success "Docker está rodando"

# Check if Cassandra is already running
if docker ps | grep -q "beauty-salon-cassandra-aot"; then
    print_success "Cassandra já está rodando"
else
    print_step "Iniciando Cassandra..."
    cd "$BASE_DIR"
    docker-compose -f docker-compose.aot-native.yml up -d cassandra
    
    print_step "Aguardando Cassandra inicializar (isso pode levar ~60 segundos)..."
    sleep 10
    
    # Wait for Cassandra to be healthy
    COUNTER=0
    MAX_WAIT=60
    while [ $COUNTER -lt $MAX_WAIT ]; do
        if docker exec beauty-salon-cassandra-aot cqlsh -e "describe cluster" > /dev/null 2>&1; then
            print_success "Cassandra está pronto!"
            break
        fi
        echo -n "."
        sleep 2
        COUNTER=$((COUNTER + 2))
    done
    
    if [ $COUNTER -ge $MAX_WAIT ]; then
        print_warning "Cassandra pode não estar completamente pronto, mas vamos tentar..."
    fi
    
    echo ""
fi

# Test the native executable
print_step "Iniciando executável nativo..."
cd "$REACTIVE_DIR"

print_step "Executando: ./target/beauty-salon-reactive --spring.profiles.active=docker --server.port=8085"
echo ""
echo "Pressione Ctrl+C para parar a aplicação"
echo ""

./target/beauty-salon-reactive \
    --spring.profiles.active=docker \
    --spring.data.cassandra.contact-points=localhost \
    --spring.data.cassandra.port=9042 \
    --spring.data.cassandra.keyspace-name=beauty_salon \
    --spring.data.cassandra.local-datacenter=datacenter1 \
    --server.port=8085
