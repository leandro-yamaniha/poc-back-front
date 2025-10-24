#!/bin/bash

echo "🚀 Starting ALL Backend Docker Compose..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Array de backends com suas portas
declare -a BACKENDS=(
    "go:10005"
    "nodejs:10004"
    "python:10003"
    "dotnet:10002"
    "java:10001"
    "java-reactive:10006"
)

echo -e "${YELLOW}⚠️  Note: Todos compartilharão o mesmo Cassandra (porta 9042)${NC}"
echo ""

# Iniciar cada backend
for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$item"
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🚀 Starting: $backend (port $port)${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        continue
    fi
    
    cd "$backend"
    
    # Iniciar apenas backend (Cassandra será compartilhado)
    echo "🔧 Starting docker-compose..."
    docker-compose up -d 2>&1 | tail -5
    
    cd ..
    echo ""
done

echo "⏳ Waiting for all services to initialize (30s)..."
sleep 30

echo ""
echo "================================"
echo "📊 SERVICES STATUS"
echo "================================"

# Verificar status de cada backend
for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$item"
    
    cd "$backend" 2>/dev/null || continue
    
    # Contar containers rodando
    running=$(docker-compose ps --services --filter "status=running" 2>/dev/null | wc -l | tr -d ' ')
    total=$(docker-compose ps --services 2>/dev/null | wc -l | tr -d ' ')
    
    if [ "$running" -gt 0 ]; then
        echo -e "${GREEN}✅ $backend: $running/$total containers running${NC}"
    else
        echo -e "${RED}❌ $backend: No containers running${NC}"
    fi
    
    cd ..
done

echo ""
echo "================================"
echo "🔍 HEALTH CHECKS"
echo "================================"

# Testar health checks
sleep 10

for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$item"
    
    # Definir path de health check
    case $backend in
        java|java-reactive)
            health_path="/actuator/health"
            ;;
        python)
            health_path="/api/customers"
            ;;
        *)
            health_path="/health"
            ;;
    esac
    
    http_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port$health_path" 2>/dev/null)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ $backend (port $port): HTTP $http_code${NC}"
    else
        echo -e "${YELLOW}⏳ $backend (port $port): HTTP $http_code (may still be starting)${NC}"
    fi
done

echo ""
echo "================================"
echo "📋 QUICK REFERENCE"
echo "================================"
echo ""
echo "Health Check URLs:"
echo "  Java:          http://localhost:10001/actuator/health"
echo "  .NET:          http://localhost:10002/health"
echo "  Python:        http://localhost:10003/api/customers"
echo "  Node.js:       http://localhost:10004/health"
echo "  Go:            http://localhost:10005/health"
echo "  Java Reactive: http://localhost:10006/actuator/health"
echo ""
echo "View logs:"
echo "  cd <backend> && docker-compose logs -f"
echo ""
echo "Stop all:"
echo "  ./stop-all-compose.sh"
echo ""
echo "Finished: $(date)"
