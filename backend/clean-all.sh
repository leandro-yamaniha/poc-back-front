#!/bin/bash

echo "🧹 Cleaning ALL Backend Artifacts..."
echo "================================"
echo ""

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Backends e seus artefatos
BACKENDS=("dotnet" "java" "java-reactive" "go" "nodejs" "python")

TOTAL_SIZE=0

# Função para calcular tamanho
get_size() {
    local path=$1
    if [ -e "$path" ]; then
        du -sh "$path" 2>/dev/null | cut -f1
    else
        echo "0"
    fi
}

# Função para limpar um backend
clean_backend() {
    local backend=$1
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🧹 Cleaning: $backend"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -d "$backend" ]; then
        echo "⚠️  Directory not found: $backend"
        echo ""
        return
    fi
    
    cd "$backend"
    
    # Limpar artefatos específicos por backend
    case $backend in
        "dotnet")
            [ -d "publish" ] && echo "  🗑️  Removing: publish" && rm -rf publish
            [ -d "bin" ] && echo "  🗑️  Removing: bin" && rm -rf bin
            [ -d "obj" ] && echo "  🗑️  Removing: obj" && rm -rf obj
            ;;
        "java"|"java-reactive")
            [ -d "target" ] && echo "  🗑️  Removing: target" && rm -rf target
            ;;
        "go")
            [ -d "bin" ] && echo "  🗑️  Removing: bin" && rm -rf bin
            ;;
        "nodejs")
            [ -d "node_modules" ] && echo "  🗑️  Removing: node_modules" && rm -rf node_modules
            [ -d "dist" ] && echo "  🗑️  Removing: dist" && rm -rf dist
            ;;
        "python")
            [ -d "venv" ] && echo "  🗑️  Removing: venv" && rm -rf venv
            find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
            find . -type f -name "*.pyc" -delete 2>/dev/null || true
            ;;
    esac
    
    cd ..
    echo -e "${GREEN}✅ $backend cleaned${NC}"
    echo ""
}

# Limpar cada backend
for backend in "${BACKENDS[@]}"; do
    clean_backend "$backend"
done

# Limpar logs de build paralelo
if ls build-logs-* 1> /dev/null 2>&1; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🧹 Cleaning build logs"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    rm -rf build-logs-*
    echo -e "${GREEN}✅ Build logs cleaned${NC}"
    echo ""
fi

echo "================================"
echo -e "${GREEN}🎉 All artifacts cleaned!${NC}"
echo "================================"
