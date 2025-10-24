#!/bin/bash

echo "🚀 Building ALL Backends (PARALLEL)..."
echo "================================"
echo "Starting: $(date)"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Criar diretório de logs
LOGS_DIR="build-logs-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$LOGS_DIR"

echo "📋 Logs will be saved to: $LOGS_DIR"
echo ""

# Array de backends
BACKENDS=("dotnet" "java" "java-reactive" "go" "nodejs" "python")

# Função para build paralelo
build_backend_parallel() {
    local backend=$1
    local log_file="$LOGS_DIR/$backend.log"
    
    {
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "🔨 Building: $backend"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "Started: $(date)"
        echo ""
        
        if [ -f "$backend/build.sh" ]; then
            cd "$backend"
            if ./build.sh 2>&1; then
                echo ""
                echo "✅ $backend build completed successfully!"
                echo "Finished: $(date)"
                cd ..
                return 0
            else
                echo ""
                echo "❌ $backend build failed!"
                echo "Finished: $(date)"
                cd ..
                return 1
            fi
        else
            echo "❌ build.sh not found for $backend"
            return 1
        fi
    } > "$log_file" 2>&1
    
    return $?
}

echo "🏗️  Starting parallel builds..."
echo ""

# Iniciar builds em paralelo
pids=()
for backend in "${BACKENDS[@]}"; do
    echo "▶️  Starting build: $backend"
    build_backend_parallel "$backend" &
    pids+=($!)
done

echo ""
echo "⏳ Waiting for all builds to complete..."
echo ""

# Aguardar todos os processos
SUCCESS=0
FAILED=0
for i in "${!pids[@]}"; do
    pid=${pids[$i]}
    backend=${BACKENDS[$i]}
    
    if wait $pid; then
        echo -e "${GREEN}✅ ${backend} completed${NC}"
        ((SUCCESS++))
    else
        echo -e "${RED}❌ ${backend} failed${NC}"
        ((FAILED++))
    fi
done

echo ""
echo "================================"
echo "📊 BUILD SUMMARY"
echo "================================"
echo -e "Total backends: ${#BACKENDS[@]}"
echo -e "${GREEN}✅ Successful: ${SUCCESS}${NC}"
echo -e "${RED}❌ Failed: ${FAILED}${NC}"
echo ""
echo "📁 Detailed logs: $LOGS_DIR/"
echo ""
echo "Finished: $(date)"
echo ""

# Mostrar preview dos logs em caso de falha
if [ $FAILED -gt 0 ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "❌ FAILED BUILDS - Last 20 lines:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    for backend in "${BACKENDS[@]}"; do
        if ! grep -q "✅" "$LOGS_DIR/$backend.log" 2>/dev/null; then
            echo ""
            echo "── $backend ──"
            tail -20 "$LOGS_DIR/$backend.log" 2>/dev/null || echo "No log found"
        fi
    done
fi

# Exit code
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All builds completed successfully!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some builds failed. Check logs in $LOGS_DIR/${NC}"
    exit 1
fi
