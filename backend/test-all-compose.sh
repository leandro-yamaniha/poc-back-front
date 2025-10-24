#!/bin/bash

echo "🧪 Testing ALL Docker Compose Configurations..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SUCCESS=0
FAILED=0
TOTAL=6

# Array de backends com suas portas
declare -a BACKENDS=(
    "dotnet:10002:/health"
    "java:10001:/actuator/health"
    "java-reactive:10006:/actuator/health"
    "go:10005:/health"
    "nodejs:10004:/health"
    "python:10003:/api/customers"
)

# Função para testar um backend
test_backend() {
    local backend=$1
    local port=$2
    local health_path=$3
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🧪 Testing: $backend (port $port)${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        return 1
    fi
    
    cd "$backend"
    
    # Parar containers existentes
    echo "🛑 Stopping existing containers..."
    docker-compose down -v > /dev/null 2>&1
    
    # Iniciar apenas backend e cassandra
    echo "🚀 Starting docker-compose..."
    if ! docker-compose up -d cassandra backend-${backend} 2>&1 | grep -q "Started"; then
        docker-compose up -d cassandra backend-${backend} > /dev/null 2>&1
    fi
    
    # Aguardar inicialização
    echo "⏳ Waiting for services to be ready (30s)..."
    sleep 30
    
    # Verificar se containers estão rodando
    local containers=$(docker-compose ps --services --filter "status=running" 2>/dev/null | wc -l)
    if [ "$containers" -lt 1 ]; then
        echo -e "${RED}❌ Containers not running${NC}"
        docker-compose ps
        docker-compose down -v > /dev/null 2>&1
        cd ..
        return 1
    fi
    
    # Testar health check
    echo "🏥 Testing health endpoint: http://localhost:$port$health_path"
    local http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port$health_path 2>/dev/null)
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "000" ]; then
        # Tentar novamente após 10s
        echo "⏳ Retrying in 10s..."
        sleep 10
        http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port$health_path 2>/dev/null)
    fi
    
    # Limpar
    echo "🧹 Cleaning up..."
    docker-compose down -v > /dev/null 2>&1
    
    cd ..
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ $backend: PASSED (HTTP $http_code)${NC}"
        return 0
    else
        echo -e "${RED}❌ $backend: FAILED (HTTP $http_code)${NC}"
        return 1
    fi
}

# Testar cada backend
for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port health_path <<< "$item"
    
    if test_backend "$backend" "$port" "$health_path"; then
        ((SUCCESS++))
    else
        ((FAILED++))
    fi
    echo ""
done

# Resumo final
echo "================================"
echo "📊 TEST SUMMARY"
echo "================================"
echo -e "Total backends: ${TOTAL}"
echo -e "${GREEN}✅ Passed: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All docker-compose tests passed!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some tests failed. Check logs above.${NC}"
    exit 1
fi
