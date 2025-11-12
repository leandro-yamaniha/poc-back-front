#!/bin/bash
# stress-test-all.sh
# Script para executar testes de stress em todos os backends

# Removido set -e para continuar mesmo se um backend falhar
# set -e

# Configuração
BACKENDS=(
    "nodejs-backend:3000"
    "java-jvm:10001"
    "java-reactive-jvm:8085"
    "go-backend:8080"
    "python-backend:8000"
    "dotnet-backend:5001"
    "java-native:10002"
    "java-reactive-native:8086"
)

RESULTS_DIR="./backend/benchmarks/results/$(date +%Y%m%d_%H%M%S)"
COMPOSE_FILE="docker-compose.loadtest.yml"
TEST_DURATION="60s"
CONNECTIONS="50"
THREADS="2"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções auxiliares
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

wait_for_health() {
    local url=$1
    local max_attempts=60
    local attempt=0
    
    log_info "Aguardando health check: $url"
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -f "$url" > /dev/null 2>&1; then
            log_success "Serviço healthy!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    log_error "Timeout aguardando health check"
    return 1
}

wait_for_cassandra() {
    local max_attempts=60
    local attempt=0
    
    log_info "Aguardando Cassandra..."
    
    while [ $attempt -lt $max_attempts ]; do
        if docker exec cassandra-loadtest cqlsh -e "DESCRIBE KEYSPACES" > /dev/null 2>&1; then
            log_success "Cassandra pronto!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    log_error "Timeout aguardando Cassandra"
    return 1
}

# Criar diretório de resultados
mkdir -p "$RESULTS_DIR"

log_info "🚀 Iniciando testes de stress em todos os backends..."
log_info "📁 Resultados serão salvos em: $RESULTS_DIR"
echo ""

# FASE 1: Build de todos os backends
log_info "🔨 Fase 1: Buildando todos os backends..."
echo ""

# Usar script de build paralelo existente
BUILD_SCRIPT="./backend/scripts/build-all-parallel.sh"

if [ -f "$BUILD_SCRIPT" ]; then
    log_info "📦 Executando build paralelo de todos os backends..."
    
    # Executar build paralelo
    if bash "$BUILD_SCRIPT"; then
        log_success "Build de todos os backends concluído com sucesso!"
    else
        log_error "Erro no build de um ou mais backends"
        log_info "Verifique os logs de build para detalhes"
        log_warning "Continuando com backends que buildaram com sucesso..."
    fi
else
    log_warning "Script build-all-parallel.sh não encontrado"
    log_info "Tentando build via docker-compose..."
    
    # Fallback: docker-compose build
    if docker-compose -f "$COMPOSE_FILE" build --parallel; then
        log_success "Build via docker-compose concluído!"
    else
        log_error "Erro no build das imagens"
        exit 1
    fi
fi
echo ""

# FASE 2: Loop de testes com config padrão
total_backends=${#BACKENDS[@]}
current=0

for backend_config in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$backend_config"
    current=$((current + 1))
    
    echo ""
    echo "========================================="
    log_info "🔍 Fase 2: Testando $backend [$current/$total_backends]"
    echo "========================================="
    
    # 1. Garantir ambiente limpo - parar TODOS os containers
    log_info "⏹️  Parando TODOS os containers..."
    docker-compose -f "$COMPOSE_FILE" down > /dev/null 2>&1
    sleep 2
    
    # Verificar se realmente não há containers rodando
    log_info "🔍 Verificando containers..."
    running_containers=$(docker ps -q)
    if [ -n "$running_containers" ]; then
        log_warning "Containers ainda rodando, forçando parada..."
        docker stop $(docker ps -q) > /dev/null 2>&1 || true
    fi
    log_success "Ambiente limpo - nenhum container rodando"
    
    # 2. Iniciar Cassandra
    log_info "🗄️  Iniciando Cassandra..."
    docker-compose -f "$COMPOSE_FILE" up -d cassandra > /dev/null 2>&1
    
    # 3. Aguardar Cassandra healthy
    if ! wait_for_cassandra; then
        log_error "Falha ao iniciar Cassandra para $backend"
        continue
    fi
    
    # 4. Iniciar APENAS o backend sendo testado
    log_info "🚀 Iniciando APENAS $backend..."
    docker-compose -f "$COMPOSE_FILE" up -d "$backend" > /dev/null 2>&1
    
    # Verificar que apenas Cassandra e o backend estão rodando
    log_info "🔍 Verificando isolamento..."
    container_count=$(docker ps --format '{{.Names}}' | wc -l)
    expected_count=2  # Cassandra + Backend
    
    if [ "$container_count" -ne "$expected_count" ]; then
        log_warning "Esperado $expected_count containers, encontrado $container_count"
        log_info "Containers rodando:"
        docker ps --format 'table {{.Names}}\t{{.Status}}'
    else
        log_success "Isolamento confirmado - apenas Cassandra + $backend rodando"
    fi
    
    # 5. Aguardar backend healthy
    sleep 10  # Aguardar inicialização
    if ! wait_for_health "http://localhost:$port/health"; then
        log_warning "Backend $backend não ficou healthy, tentando mesmo assim..."
    fi
    
    # Aguardar mais um pouco para estabilizar
    log_info "⏳ Aguardando estabilização (30s)..."
    sleep 30
    
    # 6. Executar stress test
    log_info "💪 Executando stress test ($TEST_DURATION, $CONNECTIONS conexões)..."
    wrk -t"$THREADS" -c"$CONNECTIONS" -d"$TEST_DURATION" --latency \
        "http://localhost:$port/api/customers" \
        > "$RESULTS_DIR/${backend}_wrk.txt" 2>&1
    
    if [ $? -eq 0 ]; then
        log_success "Stress test concluído"
    else
        log_error "Erro no stress test"
    fi
    
    # 7. Coletar métricas Docker
    log_info "📊 Coletando métricas Docker..."
    docker stats --no-stream --format \
        "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" \
        > "$RESULTS_DIR/${backend}_docker_stats.txt" 2>&1
    
    # 8. Salvar logs do backend
    log_info "📝 Salvando logs..."
    docker logs "${backend}-loadtest" > "$RESULTS_DIR/${backend}_logs.txt" 2>&1
    
    # 9. Salvar informações do container
    docker inspect "${backend}-loadtest" > "$RESULTS_DIR/${backend}_inspect.json" 2>&1
    
    log_success "Teste concluído: $backend"
    
    # Pequena pausa entre testes
    sleep 5
done

# Parar todos os containers
log_info "⏹️  Parando todos os containers..."
docker-compose -f "$COMPOSE_FILE" down > /dev/null 2>&1

echo ""
echo "========================================="
log_success "✅ Fase 2 concluída - Todos os testes finalizados!"
echo "========================================="
log_info "📁 Resultados salvos em: $RESULTS_DIR"
echo ""

# FASE 3: Gerar relatório comparativo automaticamente
log_info "📊 Fase 3: Gerando relatório comparativo..."
if [ -f "./backend/scripts/analyze-results.sh" ]; then
    if bash ./backend/scripts/analyze-results.sh "$RESULTS_DIR" 2>/dev/null; then
        log_success "Relatório gerado: $RESULTS_DIR/ANALYSIS.md"
    else
        log_warning "Erro ao gerar relatório automático"
        log_info "Execute manualmente: ./backend/scripts/analyze-results.sh $RESULTS_DIR"
    fi
else
    log_warning "Script analyze-results.sh não encontrado"
    log_info "Execute manualmente: ./backend/scripts/analyze-results.sh $RESULTS_DIR"
fi
echo ""
