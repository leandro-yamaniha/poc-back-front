#!/bin/bash

echo "🧪 Quick Backend Test (Build Only)..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SUCCESS=0
FAILED=0

# Array de backends
declare -a BACKENDS=(
    "go"
    "nodejs"
    "python"
    "dotnet"
    "java"
    "java-reactive"
)

# Função para testar build de um backend
test_backend_build() {
    local backend=$1
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🧪 Testing Build: $backend${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        return 1
    fi
    
    cd "$backend"
    
    # 1. Build artefatos se existir build.sh
    if [ -f "build.sh" ]; then
        echo "🔨 Building artifacts..."
        if ./build.sh > build-test.log 2>&1; then
            echo -e "   ${GREEN}✓ Build successful${NC}"
            cd ..
            return 0
        else
            echo -e "   ${RED}✗ Build failed${NC}"
            echo "Last 5 lines of build log:"
            tail -5 build-test.log
            cd ..
            return 1
        fi
    else
        echo "⚠️  No build.sh found, skipping..."
        cd ..
        return 0
    fi
}

# Testar cada backend
echo -e "${BLUE}Testing backend builds${NC}"
echo ""

for backend in "${BACKENDS[@]}"; do
    if test_backend_build "$backend"; then
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
echo -e "${GREEN}✅ Successful builds: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed builds: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}🎉 All backend builds passed!${NC}"
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  $FAILED backend build(s) failed.${NC}"
    exit 1
fi
