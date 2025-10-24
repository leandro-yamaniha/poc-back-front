#!/bin/bash

echo "🧪 Testing ALL Backends Sequentially..."
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

# Array de backends (ordem: mais rápido para mais lento)
declare -a BACKENDS=(
    "go:10005:/health"
    "nodejs:10004:/health"
    "python:10003:/api/customers"
    "dotnet:10002:/health"
    "java:10001:/actuator/health"
    "java-reactive:10006:/actuator/health"
)

# Função para testar um backend
test_backend() {
    local backend=$1
    local port=$2
    local health_path=$3
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🧪 Testing: $backend (port $port)${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        return 1
    fi
    
    cd "$backend"
    
    # 1. Parar containers existentes
    echo "🛑 Step 1/5: Stopping previous containers..."
    docker-compose down -v > /dev/null 2>&1
    
    # 2. Iniciar docker-compose
    echo "🚀 Step 2/5: Starting docker-compose..."
    docker-compose up -d > /dev/null 2>&1
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to start docker-compose${NC}"
        cd ..
        return 1
    fi
    
    # 3. Aguardar inicialização
    echo "⏳ Step 3/5: Waiting for services to start (45s)..."
    sleep 45
    
    # 4. Verificar containers
    echo "🔍 Step 4/5: Checking containers..."
    local running=$(docker-compose ps --services --filter "status=running" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Containers running: $running"
    
    if [ "$running" -lt 1 ]; then
        echo -e "${RED}❌ No containers running${NC}"
        docker-compose ps
        docker-compose down -v > /dev/null 2>&1
        cd ..
        return 1
    fi
    
    # 5. Testar health check (com retry)
    echo "🏥 Step 5/5: Testing health endpoint..."
    local http_code
    local attempts=0
    local max_attempts=3
    
    while [ $attempts -lt $max_attempts ]; do
        http_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port$health_path" 2>/dev/null)
        
        if [ "$http_code" = "200" ]; then
            break
        fi
        
        attempts=$((attempts + 1))
        if [ $attempts -lt $max_attempts ]; then
            echo "   Attempt $attempts failed (HTTP $http_code), retrying in 10s..."
            sleep 10
        fi
    done
    
    # Mostrar resultado
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ SUCCESS: $backend responding (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Quick test:"
        curl -s "http://localhost:$port$health_path" | head -3 || echo "   (no output)"
        echo ""
        
        # Perguntar se deve continuar
        echo -e "${YELLOW}Press ENTER to stop this backend and continue to next...${NC}"
        read -r
        
        # Parar antes do próximo
        echo "🛑 Stopping $backend..."
        docker-compose down -v > /dev/null 2>&1
        
        cd ..
        return 0
    else
        echo -e "${RED}❌ FAILED: $backend not responding (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Last 20 lines of logs:"
        docker-compose logs --tail=20 2>&1 | tail -20
        echo ""
        
        # Perguntar se deve continuar
        echo -e "${YELLOW}Continue to next backend anyway? (y/N)${NC}"
        read -r response
        
        docker-compose down -v > /dev/null 2>&1
        cd ..
        
        if [[ ! "$response" =~ ^[Yy]$ ]]; then
            echo "Stopping tests."
            exit 1
        fi
        
        return 1
    fi
}

# Testar cada backend
echo -e "${BLUE}Will test backends in order: ${BACKENDS[@]}${NC}"
echo ""

for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port health_path <<< "$item"
    
    if test_backend "$backend" "$port" "$health_path"; then
        ((SUCCESS++))
    else
        ((FAILED++))
    fi
done

# Resumo final
echo ""
echo "================================"
echo "📊 FINAL SUMMARY"
echo "================================"
echo -e "Total tested: ${#BACKENDS[@]}"
echo -e "${GREEN}✅ Successful: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All backends passed!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some backends failed.${NC}"
    exit 1
fi
