#!/bin/bash

# Script para build de backends específicos
# Uso: ./build-specific.sh dotnet java go

if [ $# -eq 0 ]; then
    echo "❌ Erro: Nenhum backend especificado"
    echo ""
    echo "Uso: $0 <backend1> [backend2] [backend3] ..."
    echo ""
    echo "Backends disponíveis:"
    echo "  - dotnet"
    echo "  - java"
    echo "  - java-reactive"
    echo "  - go"
    echo "  - nodejs"
    echo "  - python"
    echo ""
    echo "Exemplos:"
    echo "  $0 dotnet              # Build apenas .NET"
    echo "  $0 java java-reactive  # Build Java e Java Reactive"
    echo "  $0 dotnet go python    # Build .NET, Go e Python"
    exit 1
fi

set -e

echo "🚀 Building Specific Backends..."
echo "================================"
echo "Backends: $@"
echo "Starting: $(date)"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

SUCCESS=0
FAILED=0

# Build de cada backend especificado
for backend in "$@"; do
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🔨 Building: $backend"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo -e "${RED}❌ Directory not found: $backend${NC}"
        ((FAILED++))
        echo ""
        continue
    fi
    
    if [ ! -f "$backend/build.sh" ]; then
        echo -e "${RED}❌ build.sh not found for $backend${NC}"
        ((FAILED++))
        echo ""
        continue
    fi
    
    cd "$backend"
    if ./build.sh; then
        echo -e "${GREEN}✅ $backend build completed successfully!${NC}"
        ((SUCCESS++))
    else
        echo -e "${RED}❌ $backend build failed!${NC}"
        ((FAILED++))
    fi
    cd ..
    echo ""
done

# Resumo
echo "================================"
echo "📊 BUILD SUMMARY"
echo "================================"
echo -e "Total requested: $#"
echo -e "${GREEN}✅ Successful: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "Finished: $(date)"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All requested builds completed successfully!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some builds failed.${NC}"
    exit 1
fi
