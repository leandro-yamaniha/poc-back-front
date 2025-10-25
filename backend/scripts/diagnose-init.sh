#!/bin/bash
# Diagnostic build to find initialization issues

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}▶ Diagnóstico de inicialização para imagem nativa${NC}"
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

print_step "Construindo imagem nativa com rastreamento detalhado de inicialização..."
cd "$REACTIVE_DIR"

# Clean previous builds
./mvnw clean

# Build with diagnostic options
print_step "Compilando com flags de diagnóstico (isso pode levar vários minutos)..."
./mvnw -Pnative native:compile -DskipTests \
  -Dspring.aot.enabled=true \
  -Dorg.graalvm.nativeimage.imagecode=agent \
  -Dnative.image.build-arguments="-H:+TraceClassInitialization,-H:+PrintClassInitialization,-H:+ReportExceptionStackTraces"

if [ $? -eq 0 ]; then
  print_success "Build diagnóstico concluído com sucesso!"
  echo ""
  echo "Executável criado em: target/beauty-salon-reactive"
  echo ""
  echo "Para testar com o perfil docker (com Cassandra):"
  echo "  ./target/beauty-salon-reactive --spring.profiles.active=docker"
  echo ""
  echo "Para testar sem Cassandra:"
  echo "  ./target/beauty-salon-reactive --spring.profiles.active=test"
else
  print_error "Build falhou - verifique os erros de inicialização acima"
  echo ""
  print_step "Dicas para resolução:"
  echo "1. Procure mensagens 'initialization of class' no log de erro"
  echo "2. Para cada classe problemática, adicione uma flag --initialize-at-run-time"
  echo "3. Classes com problemas de tempo de construção podem precisar de --initialize-at-build-time"
  echo "4. Erros de reflexão podem exigir adição na classe NativeRuntimeHints"
fi

echo ""
print_step "Logs detalhados estão disponíveis em target/native-image.log"
