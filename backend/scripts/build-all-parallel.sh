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

# Determinar diretório base
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Mudar para diretório backend
cd "$BACKEND_DIR"

echo "📁 Working directory: $BACKEND_DIR"

# Criar diretório de logs
LOGS_DIR="build-logs-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$LOGS_DIR"

echo "📋 Logs will be saved to: $LOGS_DIR"
echo ""

# Array de backends com formato: "nome:diretório:tipo:script"
# Formato: nome_log:diretório_build:tipo_build:script_path
BACKENDS=(
    "dotnet:dotnet:jvm:dotnet/build.sh"
    "java-jvm:java:jvm:java/build.sh"
    "java-native:java:native:java/build-native.sh"
    "go:go:native:go/build.sh"   
    "java-reactive-jvm:java-reactive:jvm:java-reactive/build.sh"
    "java-reactive-native:java-reactive:native:scripts/build-aot-native.sh"
    "nodejs:nodejs:jvm:nodejs/build.sh"
    "python:python:jvm:python/build.sh"
)

# Função para build paralelo
build_backend_parallel() {
    local backend_config=$1
    IFS=':' read -r backend_name backend_dir build_type build_script <<< "$backend_config"
    local log_file="$BACKEND_DIR/$LOGS_DIR/${backend_name}.log"
    local script_path="$BACKEND_DIR/$build_script"
    local work_dir="$BACKEND_DIR/$backend_dir"
    
    {
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "🔨 Building: $backend_name"
        echo "   Directory: $backend_dir"
        echo "   Type: $build_type"
        echo "   Script: $build_script"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "Started: $(date)"
        echo ""
        
        if [ -f "$script_path" ]; then
            # Mudar para o diretório do backend antes de executar
            cd "$work_dir" || {
                echo "❌ Failed to change to directory: $work_dir"
                return 1
            }
            
            # Executar script de build
            if [ "$build_type" = "native" ]; then
                echo "🔧 Building NATIVE version with $build_script..."
            else
                echo "🔧 Building JVM version with $build_script..."
            fi
            
            echo "📁 Working in: $(pwd)"
            echo ""
            
            if bash "$script_path" 2>&1; then
                echo ""
                echo "✅ $backend_name ($build_type) build completed successfully!"
                echo "Finished: $(date)"
                cd "$BACKEND_DIR"
                return 0
            else
                echo ""
                echo "❌ $backend_name ($build_type) build failed!"
                echo "Finished: $(date)"
                cd "$BACKEND_DIR"
                return 1
            fi
        else
            echo "❌ Build script not found at: $script_path"
            echo "   Listing backend directory:"
            ls -la "$work_dir" 2>&1 || echo "   Directory not found"
            return 1
        fi
    } > "$log_file" 2>&1
    
    return $?
}

echo "🏗️  Starting parallel builds..."
echo ""

# Iniciar builds em paralelo
pids=()
backend_names=()
for backend_config in "${BACKENDS[@]}"; do
    IFS=':' read -r backend_name backend_dir build_type build_script <<< "$backend_config"
    echo "▶️  Starting build: $backend_name ($build_type)"
    build_backend_parallel "$backend_config" &
    pids+=($!)
    backend_names+=("$backend_name")
done

echo ""
echo "⏳ Waiting for all builds to complete..."
echo ""

# Aguardar todos os processos
SUCCESS=0
FAILED=0
for i in "${!pids[@]}"; do
    pid=${pids[$i]}
    backend_name=${backend_names[$i]}
    
    if wait $pid; then
        echo -e "${GREEN}✅ ${backend_name} completed${NC}"
        ((SUCCESS++))
    else
        echo -e "${RED}❌ ${backend_name} failed${NC}"
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
    for backend_config in "${BACKENDS[@]}"; do
        IFS=':' read -r backend_name backend_dir build_type build_script <<< "$backend_config"
        if ! grep -q "✅" "$LOGS_DIR/${backend_name}.log" 2>/dev/null; then
            echo ""
            echo "── $backend_name ($build_type) ──"
            tail -20 "$LOGS_DIR/${backend_name}.log" 2>/dev/null || echo "No log found"
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
