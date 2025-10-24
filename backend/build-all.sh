#!/bin/bash
set -e

echo "🚀 Building ALL Backends..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Contadores
SUCCESS=0
FAILED=0
TOTAL=6

# Array de backends
BACKENDS=("dotnet" "java" "java-reactive" "go" "nodejs" "python")

echo "📋 Backends to build: ${BACKENDS[@]}"
echo ""

# Função para build de um backend
build_backend() {
    local backend=$1
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🔨 Building: $backend"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ -f "$backend/build.sh" ]; then
        cd "$backend"
        if ./build.sh; then
            echo -e "${GREEN}✅ $backend build completed successfully!${NC}"
            cd ..
            return 0
        else
            echo -e "${RED}❌ $backend build failed!${NC}"
            cd ..
            return 1
        fi
    else
        echo -e "${RED}❌ build.sh not found for $backend${NC}"
        return 1
    fi
}

# Build de cada backend
for backend in "${BACKENDS[@]}"; do
    if build_backend "$backend"; then
        ((SUCCESS++))
    else
        ((FAILED++))
    fi
    echo ""
done

# Resumo
echo "================================"
echo "📊 BUILD SUMMARY"
echo "================================"
echo -e "Total backends: ${TOTAL}"
echo -e "${GREEN}✅ Successful: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

# Exit code baseado em falhas
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All builds completed successfully!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some builds failed. Check logs above.${NC}"
    exit 1
fi
