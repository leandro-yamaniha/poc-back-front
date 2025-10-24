#!/bin/bash

echo "🧪 Full Test: Build + Docker Compose..."
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
    "go:10005:/health"
    "nodejs:10004:/health"
    "python:10003:/api/customers"
)

# Função para build e testar um backend
test_backend_full() {
    local backend=$1
    local port=$2
    local health_path=$3
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🔨 Building + Testing: $backend (port $port)${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        return 1
    fi
    
    # 1. Build artefatos
    echo "🔨 Step 1/3: Building artifacts..."
    cd "$backend"
    if [ -f "build.sh" ]; then
        if ! ./build.sh > /dev/null 2>&1; then
            echo -e "${RED}❌ Artifact build failed${NC}"
            cd ..
            return 1
        fi
        echo -e "${GREEN}✅ Artifacts built${NC}"
    else
        echo -e "${YELLOW}⚠️  No build.sh found, skipping...${NC}"
    fi
    
    # 2. Parar containers existentes
    echo "🛑 Step 2/3: Stopping existing containers..."
    docker-compose down -v > /dev/null 2>&1
    
    # 3. Iniciar docker-compose
    echo "🚀 Step 3/3: Starting docker-compose..."
    docker-compose up -d --build > /dev/null 2>&1
    
    # Aguardar inicialização
    echo "⏳ Waiting for services (45s)..."
    sleep 45
    
    # Verificar containers
    echo "🔍 Checking containers..."
    docker-compose ps
    
    # Testar health check
    echo "🏥 Testing health endpoint..."
    local http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port$health_path 2>/dev/null)
    
    if [ "$http_code" != "200" ]; then
        echo "⏳ Retrying in 15s..."
        sleep 15
        http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port$health_path 2>/dev/null)
    fi
    
    # Mostrar logs se falhar
    if [ "$http_code" != "200" ]; then
        echo -e "${YELLOW}📋 Backend logs:${NC}"
        docker-compose logs --tail=20 backend-${backend} 2>&1 | head -30
    fi
    
    # Limpar
    echo "🧹 Cleaning up..."
    docker-compose down -v > /dev/null 2>&1
    
    cd ..
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ $backend: PASSED ✨${NC}"
        return 0
    else
        echo -e "${RED}❌ $backend: FAILED (HTTP $http_code)${NC}"
        return 1
    fi
}

# Testar apenas backends mais rápidos (Go, Node.js, Python)
echo -e "${BLUE}Testing quick backends (Go, Node.js, Python)...${NC}"
echo ""

for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port health_path <<< "$item"
    
    if test_backend_full "$backend" "$port" "$health_path"; then
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
echo -e "Total tested: ${#BACKENDS[@]}"
echo -e "${GREEN}✅ Passed: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some tests failed.${NC}"
    exit 1
fi
