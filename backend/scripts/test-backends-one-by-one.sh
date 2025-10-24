#!/bin/bash

echo "🧪 Testing Backends One by One (Backend + Cassandra only)..."
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

# Array de backends
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
    
    # 1. Build artefatos
    if [ -f "build.sh" ]; then
        echo "🔨 Step 1/6: Building artifacts..."
        if ./build.sh > /dev/null 2>&1; then
            echo -e "   ${GREEN}✓ Build successful${NC}"
        else
            echo -e "   ${YELLOW}⚠ Build had issues, continuing...${NC}"
        fi
    fi
    
    # 2. Parar containers existentes
    echo "🛑 Step 2/6: Stopping previous containers..."
    docker-compose down -v > /dev/null 2>&1
    
    # 3. Iniciar apenas backend e cassandra (sem frontend)
    echo "🚀 Step 3/6: Starting backend and cassandra..."
    
    # Determinar nome do serviço backend
    local backend_service="backend-${backend}"
    
    # Iniciar apenas cassandra e backend
    docker-compose up -d cassandra "$backend_service" > docker-start.log 2>&1
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to start services${NC}"
        echo "Error log:"
        cat docker-start.log | tail -10
        cd ..
        return 1
    fi
    
    # 4. Aguardar inicialização
    echo "⏳ Step 4/6: Waiting for services (60s)..."
    sleep 60
    
    # 5. Verificar containers
    echo "🔍 Step 5/6: Checking containers..."
    docker-compose ps | grep -E "(cassandra|$backend_service)"
    
    # 6. Testar health check
    echo "🏥 Step 6/6: Testing health endpoint..."
    local http_code
    local attempts=0
    local max_attempts=4
    
    while [ $attempts -lt $max_attempts ]; do
        http_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port$health_path" 2>/dev/null)
        
        if [ "$http_code" = "200" ]; then
            break
        fi
        
        attempts=$((attempts + 1))
        if [ $attempts -lt $max_attempts ]; then
            echo "   Attempt $attempts: HTTP $http_code, retrying in 10s..."
            sleep 10
        fi
    done
    
    # Resultado
    echo ""
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ SUCCESS: $backend is working! (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Response:"
        curl -s "http://localhost:$port$health_path" 2>/dev/null | head -5
        echo ""
        echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo -e "${GREEN}✓ $backend PASSED${NC}"
        echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        
        # Limpar
        echo ""
        echo "🛑 Cleaning up..."
        docker-compose down -v > /dev/null 2>&1
        
        cd ..
        return 0
    else
        echo -e "${RED}❌ FAILED: Not responding (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Logs:"
        docker-compose logs --tail=30 "$backend_service" 2>&1 | tail -30
        
        docker-compose down -v > /dev/null 2>&1
        cd ..
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
    
    # Pausa entre backends
    echo ""
    echo "⏸️  Pausing 5s before next..."
    sleep 5
done

# Resumo
echo ""
echo "================================"
echo "📊 FINAL RESULTS"
echo "================================"
echo -e "Total: ${#BACKENDS[@]}"
echo -e "${GREEN}✅ Passed: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"

if [ $FAILED -eq 0 ]; then
    echo ""
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}🎉 ALL BACKENDS WORKING!${NC}"
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    exit 0
else
    exit 1
fi
