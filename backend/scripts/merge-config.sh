#!/bin/bash
# Script para mesclar configurações do tracing com configurações manuais

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}▶ Mesclando configurações de reflexão${NC}"
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

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    print_error "jq não está instalado. É necessário para mesclar arquivos JSON."
    echo "Instale o jq:"
    echo "  macOS: brew install jq"
    echo "  Ubuntu/Debian: apt-get install jq"
    exit 1
fi

# Directory paths
TRACE_DIR="$REACTIVE_DIR/src/main/resources/META-INF/native-image/cassandra-trace"
CONFIG_DIR="$REACTIVE_DIR/src/main/resources/META-INF/native-image"
BACKUP_DIR="$REACTIVE_DIR/src/main/resources/META-INF/native-image/backup-$(date +%Y%m%d-%H%M%S)"

# Check if trace directory exists
if [ ! -d "$TRACE_DIR" ] || [ ! -f "$TRACE_DIR/reflect-config.json" ]; then
    print_error "Diretório de trace não encontrado ou vazio: $TRACE_DIR"
    echo "Execute primeiro o script cassandra-tracing.sh"
    exit 1
fi

# Check if config directory exists
if [ ! -d "$CONFIG_DIR" ] || [ ! -f "$CONFIG_DIR/reflect-config.json" ]; then
    print_error "Arquivo de configuração não encontrado: $CONFIG_DIR/reflect-config.json"
    echo "Crie o arquivo de configuração primeiro ou verifique o caminho"
    exit 1
fi

# Create backup
print_step "Criando backup das configurações atuais..."
mkdir -p "$BACKUP_DIR"
cp "$CONFIG_DIR"/*.json "$BACKUP_DIR/" 2>/dev/null || true
print_success "Backup criado em: $BACKUP_DIR"

# Merge reflect-config.json
print_step "Mesclando configurações de reflexão..."
jq -s '.[0] + .[1] | unique_by(.name)' \
  "$CONFIG_DIR/reflect-config.json" "$TRACE_DIR/reflect-config.json" > "/tmp/merged-reflect.json"

if [ $? -ne 0 ]; then
    print_error "Erro ao mesclar arquivos JSON"
    exit 1
fi

mv "/tmp/merged-reflect.json" "$CONFIG_DIR/reflect-config.json"
print_success "Configurações mescladas com sucesso"

# Copy other files if they don't exist
print_step "Verificando outras configurações..."
for CONFIG_FILE in resource-config.json proxy-config.json serialization-config.json; do
    if [ -f "$TRACE_DIR/$CONFIG_FILE" ] && [ ! -f "$CONFIG_DIR/$CONFIG_FILE" ]; then
        cp "$TRACE_DIR/$CONFIG_FILE" "$CONFIG_DIR/"
        print_success "Copiado $CONFIG_FILE"
    fi
done

print_success "Configurações mescladas com sucesso!"
echo ""
echo "Agora você pode compilar a aplicação nativa novamente."
