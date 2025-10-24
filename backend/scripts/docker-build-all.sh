#!/bin/bash
set -e

echo "🐳 Building Docker Images for ALL Backends..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

SUCCESS=0
FAILED=0
TOTAL=6

# Lista de backends (backend:image)
BACKENDS=(
    "dotnet:backend-dotnet:latest"
    "java:backend-java:latest"
    "java-reactive:backend-java-reactive:latest"
    "go:backend-go:latest"
    "nodejs:backend-nodejs:latest"
    "python:backend-python:latest"
)

# Função para build Docker
docker_build() {
    local backend=$1
    local image_name=$2
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🐳 Building Docker image: $backend → $image_name"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        return 1
    fi
    
    cd "$backend"
    
    if docker build -t "$image_name" . ; then
        echo -e "${GREEN}✅ Image built: $image_name${NC}"
        cd ..
        return 0
    else
        echo -e "${RED}❌ Failed to build: $image_name${NC}"
        cd ..
        return 1
    fi
}

# Build de cada backend
for item in "${BACKENDS[@]}"; do
    IFS=':' read -r backend image_name tag <<< "$item"
    full_image="$image_name:$tag"
    
    if docker_build "$backend" "$full_image"; then
        ((SUCCESS++))
    else
        ((FAILED++))
    fi
    echo ""
done

# Resumo
echo "================================"
echo "📊 DOCKER BUILD SUMMARY"
echo "================================"
echo -e "Total backends: ${TOTAL}"
echo -e "${GREEN}✅ Successful: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""

# Listar imagens criadas
if [ $SUCCESS -gt 0 ]; then
    echo "📦 Docker images created:"
    docker images | grep "backend-" | grep "latest"
fi

echo ""
echo "Finished: $(date)"
echo ""

# Exit code
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All Docker images built successfully!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some builds failed.${NC}"
    exit 1
fi
