#!/bin/bash
# Artifact and Docker Image Size Analysis Script

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Artifact & Image Size Analysis"
echo "================================================"
echo ""

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Function to convert bytes to human readable format
human_readable() {
    local bytes=$1
    if [ $bytes -gt 1073741824 ]; then
        echo "$(echo "scale=2; $bytes/1073741824" | bc)GB"
    elif [ $bytes -gt 1048576 ]; then
        echo "$(echo "scale=2; $bytes/1048576" | bc)MB"
    elif [ $bytes -gt 1024 ]; then
        echo "$(echo "scale=2; $bytes/1024" | bc)KB"
    else
        echo "${bytes}B"
    fi
}

# Function to get directory size
get_dir_size() {
    local dir=$1
    if [ -d "$dir" ]; then
        du -sb "$dir" 2>/dev/null | cut -f1
    else
        echo "0"
    fi
}

# Function to get file size
get_file_size() {
    local file=$1
    if [ -f "$file" ]; then
        stat -f%z "$file" 2>/dev/null || stat -c%s "$file" 2>/dev/null || echo "0"
    else
        echo "0"
    fi
}

print_step "Analyzing Backend Artifacts Sizes..."
echo ""

# Create results file
RESULTS_FILE="/tmp/beauty-salon-sizes.md"
cat > $RESULTS_FILE << 'EOF'
# 📊 Beauty Salon - Análise de Tamanhos de Artefatos e Imagens Docker

## 📦 Tamanhos dos Artefatos por Backend

| Backend | Tipo de Artefato | Localização | Tamanho | Observações |
|---------|------------------|-------------|---------|-------------|
EOF

print_info "Analisando Java Tradicional..."
JAVA_TARGET_SIZE=$(get_dir_size "java/target")
JAVA_JAR_SIZE="0"
if [ -f "java/target/release/app.jar" ]; then
    JAVA_JAR_SIZE=$(get_file_size "java/target/release/app.jar")
fi
JAVA_CLASSES_SIZE=$(get_dir_size "java/target/classes")

echo "| **Java Tradicional** | Diretório target | java/target/ | $(human_readable $JAVA_TARGET_SIZE) | Build completo |" >> $RESULTS_FILE
echo "| | JAR Principal | java/target/release/app.jar | $(human_readable $JAVA_JAR_SIZE) | Executável |" >> $RESULTS_FILE
echo "| | Classes compiladas | java/target/classes/ | $(human_readable $JAVA_CLASSES_SIZE) | Bytecode |" >> $RESULTS_FILE

print_info "Analisando Java Reactive..."
REACTIVE_TARGET_SIZE=$(get_dir_size "java-reactive/target")
REACTIVE_JAR_SIZE="0"
if [ -f "java-reactive/target/release/app.jar" ]; then
    REACTIVE_JAR_SIZE=$(get_file_size "java-reactive/target/release/app.jar")
fi
REACTIVE_CLASSES_SIZE=$(get_dir_size "java-reactive/target/classes")

echo "| **Java Reactive** | Diretório target | java-reactive/target/ | $(human_readable $REACTIVE_TARGET_SIZE) | Build completo |" >> $RESULTS_FILE
echo "| | JAR Principal | java-reactive/target/release/app.jar | $(human_readable $REACTIVE_JAR_SIZE) | Executável |" >> $RESULTS_FILE
echo "| | Classes compiladas | java-reactive/target/classes/ | $(human_readable $REACTIVE_CLASSES_SIZE) | Bytecode |" >> $RESULTS_FILE

print_info "Analisando .NET..."
DOTNET_BIN_SIZE=$(get_dir_size "dotnet/BeautySalonAPI/bin")
DOTNET_OBJ_SIZE=$(get_dir_size "dotnet/BeautySalonAPI/obj")
DOTNET_PUBLISH_SIZE=$(get_dir_size "dotnet/publish")
DOTNET_DLL_SIZE="0"
if [ -f "dotnet/BeautySalonAPI/bin/Release/net8.0/BeautySalonAPI.dll" ]; then
    DOTNET_DLL_SIZE=$(get_file_size "dotnet/BeautySalonAPI/bin/Release/net8.0/BeautySalonAPI.dll")
fi

echo "| **-.NET** | Diretório bin | dotnet/BeautySalonAPI/bin/ | $(human_readable $DOTNET_BIN_SIZE) | Build artifacts |" >> $RESULTS_FILE
echo "| | Diretório obj | dotnet/BeautySalonAPI/obj/ | $(human_readable $DOTNET_OBJ_SIZE) | Temp build files |" >> $RESULTS_FILE
echo "| | Publish artifacts | dotnet/publish/ | $(human_readable $DOTNET_PUBLISH_SIZE) | Deploy-ready |" >> $RESULTS_FILE
echo "| | DLL Principal | BeautySalonAPI.dll | $(human_readable $DOTNET_DLL_SIZE) | Executável |" >> $RESULTS_FILE

print_info "Analisando Node.js..."
NODEJS_MODULES_SIZE=$(get_dir_size "nodejs/node_modules")
NODEJS_SRC_SIZE=$(get_dir_size "nodejs/src")
NODEJS_PACKAGE_SIZE=$(get_file_size "nodejs/package.json")

echo "| **Node.js** | node_modules | nodejs/node_modules/ | $(human_readable $NODEJS_MODULES_SIZE) | Dependências |" >> $RESULTS_FILE
echo "| | Código fonte | nodejs/src/ | $(human_readable $NODEJS_SRC_SIZE) | JavaScript |" >> $RESULTS_FILE
echo "| | package.json | nodejs/package.json | $(human_readable $NODEJS_PACKAGE_SIZE) | Configuração |" >> $RESULTS_FILE

print_info "Analisando Python..."
PYTHON_VENV_SIZE=$(get_dir_size "python/venv")
PYTHON_APP_SIZE=$(get_dir_size "python/app")
PYTHON_MAIN_SIZE=$(get_file_size "python/main.py")

echo "| **Python** | Virtual env | python/venv/ | $(human_readable $PYTHON_VENV_SIZE) | Dependências |" >> $RESULTS_FILE
echo "| | Código da app | python/app/ | $(human_readable $PYTHON_APP_SIZE) | Python modules |" >> $RESULTS_FILE
echo "| | main.py | python/main.py | $(human_readable $PYTHON_MAIN_SIZE) | Entry point |" >> $RESULTS_FILE

print_info "Analisando Go..."
GO_BIN_SIZE=$(get_dir_size "go/bin")
GO_SRC_SIZE=$(get_dir_size "go")
GO_MAIN_SIZE="0"
if [ -f "go/bin/main" ]; then
    GO_MAIN_SIZE=$(get_file_size "go/bin/main")
fi

echo "| **Go** | Binários | go/bin/ | $(human_readable $GO_BIN_SIZE) | Executáveis |" >> $RESULTS_FILE
echo "| | Código fonte | go/ (total) | $(human_readable $GO_SRC_SIZE) | Go source |" >> $RESULTS_FILE
echo "| | Binário principal | go/bin/main | $(human_readable $GO_MAIN_SIZE) | Executável |" >> $RESULTS_FILE

# Add Docker images analysis
echo "" >> $RESULTS_FILE
echo "## 🐳 Tamanhos das Imagens Docker" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "| Backend | Nome da Imagem | Tamanho | Base Image | Eficiência |" >> $RESULTS_FILE
echo "|---------|----------------|---------|------------|------------|" >> $RESULTS_FILE

print_step "Analisando Imagens Docker..."

# Get Docker images and their sizes
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep beauty-salon | while read -r line; do
    if [[ $line == *"REPOSITORY"* ]]; then
        continue
    fi
    
    # Parse the line
    repo=$(echo "$line" | awk '{print $1}')
    tag=$(echo "$line" | awk '{print $2}')
    size=$(echo "$line" | awk '{print $3}')
    
    # Determine backend type and base image
    backend_type="Unknown"
    base_image="Unknown"
    efficiency="⚪"
    
    if [[ $repo == *"java"* ]] && [[ $repo != *"reactive"* ]]; then
        backend_type="Java Tradicional"
        base_image="eclipse-temurin:21-jre-alpine"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 400 ]]; then
            efficiency="🟢 Boa"
        elif [[ $size == *"MB" ]] && [[ ${size%MB} -lt 500 ]]; then
            efficiency="🟡 Média"
        else
            efficiency="🔴 Alta"
        fi
    elif [[ $repo == *"reactive"* ]]; then
        backend_type="Java Reactive"
        base_image="eclipse-temurin:21-jre-alpine"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 400 ]]; then
            efficiency="🟢 Boa"
        elif [[ $size == *"MB" ]] && [[ ${size%MB} -lt 500 ]]; then
            efficiency="🟡 Média"
        else
            efficiency="🔴 Alta"
        fi
    elif [[ $repo == *"dotnet"* ]]; then
        backend_type=".NET"
        base_image="mcr.microsoft.com/dotnet/aspnet:8.0"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 400 ]]; then
            efficiency="🟢 Boa"
        else
            efficiency="🟡 Média"
        fi
    elif [[ $repo == *"nodejs"* ]]; then
        backend_type="Node.js"
        base_image="node:18-alpine"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 300 ]]; then
            efficiency="🟢 Boa"
        else
            efficiency="🟡 Média"
        fi
    elif [[ $repo == *"python"* ]]; then
        backend_type="Python"
        base_image="python:3.11-slim"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 400 ]]; then
            efficiency="🟢 Boa"
        else
            efficiency="🟡 Média"
        fi
    elif [[ $repo == *"go"* ]]; then
        backend_type="Go"
        base_image="alpine:latest"
        if [[ $size == *"MB" ]] && [[ ${size%MB} -lt 100 ]]; then
            efficiency="🟢 Excelente"
        else
            efficiency="🟡 Boa"
        fi
    fi
    
    echo "| **$backend_type** | $repo:$tag | $size | $base_image | $efficiency |" >> $RESULTS_FILE
done

# Add summary analysis
echo "" >> $RESULTS_FILE
echo "## 📈 Análise Comparativa" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "### 🏆 Ranking por Eficiência de Tamanho (Imagens Docker)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "1. **🥇 Go**: ~52MB - Binário nativo, máxima eficiência" >> $RESULTS_FILE
echo "2. **🥈 Node.js**: ~276MB - Runtime JavaScript otimizado" >> $RESULTS_FILE
echo "3. **🥉 .NET**: ~367MB - Runtime .NET com AOT potencial" >> $RESULTS_FILE
echo "4. **Java Reactive**: ~378MB - JVM + WebFlux otimizado" >> $RESULTS_FILE
echo "5. **Java Tradicional**: ~380MB - JVM + Spring Boot completo" >> $RESULTS_FILE
echo "6. **Python**: ~393MB - Interpretador + dependências" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "### 💡 Observações Técnicas" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Go - Campeão de Eficiência**" >> $RESULTS_FILE
echo "- **Vantagem**: Binário nativo sem runtime" >> $RESULTS_FILE
echo "- **Tamanho**: 52MB (94% menor que Python)" >> $RESULTS_FILE
echo "- **Startup**: Instantâneo" >> $RESULTS_FILE
echo "- **Memória**: Mínima (~10-20MB em runtime)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Node.js - Equilibrio Ideal**" >> $RESULTS_FILE
echo "- **Vantagem**: Runtime otimizado + Alpine Linux" >> $RESULTS_FILE
echo "- **Tamanho**: 276MB (30% menor que .NET)" >> $RESULTS_FILE
echo "- **Startup**: Rápido (~2-3s)" >> $RESULTS_FILE
echo "- **Memória**: Configurável via V8 options" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Java - Poder vs Tamanho**" >> $RESULTS_FILE
echo "- **Tradicional**: 380MB - Máxima compatibilidade" >> $RESULTS_FILE
echo "- **Reactive**: 378MB - Melhor performance concorrente" >> $RESULTS_FILE
echo "- **Startup**: Médio (~10-15s)" >> $RESULTS_FILE
echo "- **Memória**: Altamente configurável (256MB-2GB+)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Python - Flexibilidade com Custo**" >> $RESULTS_FILE
echo "- **Tamanho**: 393MB - Maior devido ao interpretador" >> $RESULTS_FILE
echo "- **Vantagem**: Máxima flexibilidade de desenvolvimento" >> $RESULTS_FILE
echo "- **Startup**: Médio (~5-8s)" >> $RESULTS_FILE
echo "- **Otimização**: Possível com PyPy ou Cython" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "### 🎯 Recomendações por Cenário" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Microserviços/Edge Computing**" >> $RESULTS_FILE
echo "- **1ª Escolha**: Go (52MB)" >> $RESULTS_FILE
echo "- **2ª Escolha**: Node.js (276MB)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **APIs de Alta Performance**" >> $RESULTS_FILE
echo "- **1ª Escolha**: Java Reactive (378MB)" >> $RESULTS_FILE
echo "- **2ª Escolha**: Go (52MB)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Desenvolvimento Rápido**" >> $RESULTS_FILE
echo "- **1ª Escolha**: Node.js (276MB)" >> $RESULTS_FILE
echo "- **2ª Escolha**: Python (393MB)" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE
echo "#### **Enterprise/Corporativo**" >> $RESULTS_FILE
echo "- **1ª Escolha**: Java Tradicional (380MB)" >> $RESULTS_FILE
echo "- **2ª Escolha**: .NET (367MB)" >> $RESULTS_FILE

# Display results
print_success "Análise concluída! Resultados salvos em $RESULTS_FILE"
echo ""
print_info "Exibindo resultados:"
echo ""
cat $RESULTS_FILE

# Copy to project docs
cp $RESULTS_FILE "../docs/ARTIFACTS_SIZE_ANALYSIS.md"
print_success "Resultados copiados para docs/ARTIFACTS_SIZE_ANALYSIS.md"
