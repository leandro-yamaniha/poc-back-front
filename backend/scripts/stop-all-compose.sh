#!/bin/bash

echo "🛑 Stopping ALL Backend Docker Compose..."
echo "================================"

# Cores
GREEN='\033[0;32m'
NC='\033[0m'

# Array de backends
BACKENDS=("dotnet" "java" "java-reactive" "go" "nodejs" "python")

for backend in "${BACKENDS[@]}"; do
    if [ -d "$backend" ]; then
        echo "🛑 Stopping: $backend"
        cd "$backend"
        docker-compose down -v > /dev/null 2>&1
        cd ..
        echo -e "${GREEN}✅ $backend stopped${NC}"
    fi
done

echo ""
echo "🧹 Cleaning up dangling containers..."
docker container prune -f > /dev/null 2>&1

echo ""
echo -e "${GREEN}✅ All services stopped!${NC}"
