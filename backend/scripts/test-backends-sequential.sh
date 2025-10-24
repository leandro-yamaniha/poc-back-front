#!/bin/bash

echo "🧪 Testing ALL Backends Sequentially (with build)..."
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
    
    # 1. Build artefatos se existir build.sh
    if [ -f "build.sh" ]; then
        echo "🔨 Step 1/6: Building artifacts..."
        if ./build.sh > /dev/null 2>&1; then
            echo -e "   ${GREEN}✓ Build successful${NC}"
        else
            echo -e "   ${RED}✗ Build failed${NC}"
            cd ..
            return 1
        fi
    else
        echo "⚠️  Step 1/6: No build.sh found, skipping..."
    fi
    
    # 2. Parar containers existentes
    echo "🛑 Step 2/6: Stopping previous containers..."
    docker-compose down -v > /dev/null 2>&1
    
    # 3. Build e iniciar docker-compose
    echo "🚀 Step 3/6: Building and starting docker-compose..."
    docker-compose up -d --build > docker-compose-build.log 2>&1
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to start docker-compose${NC}"
        echo "Last 10 lines of build log:"
        tail -10 docker-compose-build.log
        cd ..
        return 1
    fi
    
    # 4. Aguardar inicialização
    echo "⏳ Step 4/6: Waiting for services to start (45s)..."
    sleep 45
    
    # 5. Verificar containers
    echo "🔍 Step 5/6: Checking containers..."
    docker-compose ps
    
    local running=$(docker-compose ps --services --filter "status=running" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Containers running: $running"
    
    if [ "$running" -lt 1 ]; then
        echo -e "${RED}❌ No containers running${NC}"
        echo "Showing logs:"
        docker-compose logs --tail=20
        docker-compose down -v > /dev/null 2>&1
        cd ..
        return 1
    fi
    
    # 6. Testar health check (com retry)
    echo "🏥 Step 6/6: Testing health endpoint..."
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
    echo ""
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ SUCCESS: $backend responding (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Response preview:"
        curl -s "http://localhost:$port$health_path" 2>/dev/null | head -5 || echo "   (no output)"
        echo ""
        echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo -e "${GREEN}✓ Backend $backend is working!${NC}"
        echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        
        # Parar antes do próximo
        echo ""
        echo "🛑 Stopping $backend to free resources..."
        docker-compose down -v > /dev/null 2>&1
        
        cd ..
        return 0
    else
        echo -e "${RED}❌ FAILED: $backend not responding (HTTP $http_code)${NC}"
        echo ""
        echo "📋 Last 30 lines of logs:"
        docker-compose logs --tail=30 2>&1
        echo ""
        
        docker-compose down -v > /dev/null 2>&1
        cd ..
        return 1
    fi
}

# Testar cada backend
echo -e "${BLUE}Testing backends in order (fastest to slowest)${NC}"
echo ""

for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port health_path <<< "$item"
    
    if test_backend "$backend" "$port" "$health_path"; then
        ((SUCCESS++))
    else
        ((FAILED++))
    fi
    
    # Pausa entre backends
    if [ $backend != "java-reactive" ]; then
        echo ""
        echo "⏸️  Pausing 5s before next backend..."
        sleep 5
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
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}🎉 All backends passed! System is working!${NC}"
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  $FAILED backend(s) failed.${NC}"
    exit 1
fi
