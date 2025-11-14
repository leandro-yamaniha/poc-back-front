#!/bin/bash

# Script para stress test em todos os backends do Beauty Salon
# Testa cada backend individualmente com wrk

set -e

# Detectar diretório do script e mudar para o diretório backend
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(dirname "$SCRIPT_DIR")"

# Mudar para o diretório backend
cd "$BACKEND_DIR"

echo "📁 Diretório de trabalho: $(pwd)"
echo ""

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuração
RESULTS_DIR="./stress-test-results/$(date +%Y%m%d_%H%M%S)"
TEST_DURATION="30s"
CONNECTIONS="100"
THREADS="4"
CLEANUP_WAIT="5"

# Definir backends disponíveis (formato: nome|porta|diretório|compose-file)
BACKENDS=(
    "java-reactive-native|8085|java-reactive|docker-compose.native-build.yml"
    "java-reactive-jvm|8085|java-reactive|docker-compose.jvm.yml"
    "nodejs|3000|nodejs|docker-compose.backend-only.yml"
    "python|8000|python|docker-compose.backend-only.yml"
    "go|8080|go|docker-compose.backend-only.yml"
)

# Funções auxiliares
print_header() {
    echo -e "\n${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC} $1"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}\n"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ️  $1${NC}"
}

wait_for_health() {
    local port=$1
    local backend=$2
    local max_attempts=60
    local attempt=0
    
    print_info "Aguardando $backend ficar healthy (porta $port)..."
    
    while [ $attempt -lt $max_attempts ]; do
        # Tentar diferentes endpoints de health
        if curl -sf "http://localhost:$port/health" > /dev/null 2>&1 || \
           curl -sf "http://localhost:$port/actuator/health" > /dev/null 2>&1 || \
           curl -sf "http://localhost:$port/api/health" > /dev/null 2>&1; then
            print_success "$backend está healthy!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "Timeout aguardando $backend"
    return 1
}

verify_backend_config() {
    local backend_config=$1
    IFS='|' read -r backend port dir compose_file <<< "$backend_config"
    
    print_info "Verificando configuração de $backend..."
    
    # Verificar se diretório existe
    if [ ! -d "$dir" ]; then
        print_error "Diretório $dir não encontrado"
        return 1
    fi
    
    # Verificar se docker-compose existe
    if [ ! -f "$dir/$compose_file" ]; then
        print_error "Arquivo $dir/$compose_file não encontrado"
        return 1
    fi
    
    # Verificar sintaxe do docker-compose
    local original_dir=$(pwd)
    cd "$dir"
    if ! docker-compose -f "$compose_file" config > /dev/null 2>&1; then
        print_error "Erro na configuração do docker-compose"
        cd "$original_dir"
        return 1
    fi
    cd "$original_dir"
    
    print_success "Configuração de $backend OK"
    return 0
}

stop_all_containers() {
    print_info "Parando todos os containers..."
    
    # Parar containers do projeto
    local original_dir=$(pwd)
    for dir in java-reactive java nodejs python go dotnet; do
        if [ -d "$dir" ]; then
            cd "$dir"
            docker-compose down -v > /dev/null 2>&1 || true
            docker-compose -f docker-compose.native.yml down -v > /dev/null 2>&1 || true
            docker-compose -f docker-compose.jvm.yml down -v > /dev/null 2>&1 || true
            docker-compose -f docker-compose.native-build.yml down -v > /dev/null 2>&1 || true
            cd "$original_dir"
        fi
    done
    
    # Remover containers órfãos
    docker container prune -f > /dev/null 2>&1 || true
    
    # Verificar se realmente não há containers rodando
    local running=$(docker ps -q | wc -l)
    if [ $running -gt 0 ]; then
        print_warning "Ainda há $running containers rodando, forçando parada..."
        docker stop $(docker ps -q) > /dev/null 2>&1 || true
    fi
    
    sleep 3
    print_success "Todos os containers parados e removidos"
}

start_backend() {
    local backend_config=$1
    IFS='|' read -r backend port dir compose_file <<< "$backend_config"
    
    print_header "Iniciando $backend"
    
    # Salvar diretório atual
    local original_dir=$(pwd)
    
    # Ir para o diretório do backend
    cd "$dir"
    
    # Iniciar com docker-compose (forçar rebuild)
    print_info "Executando: docker-compose -f $compose_file up -d --build"
    
    if docker-compose -f "$compose_file" up -d --build; then
        print_success "Containers iniciados"
        
        # Voltar ao diretório original
        cd "$original_dir"
        
        # Aguardar backend ficar healthy
        if wait_for_health "$port" "$backend"; then
            # Aguardar estabilização
            print_info "Aguardando estabilização (10s)..."
            sleep 10
            return 0
        else
            return 1
        fi
    else
        print_error "Falha ao iniciar $backend"
        cd "$original_dir"
        return 1
    fi
}

run_stress_test() {
    local backend_config=$1
    IFS='|' read -r backend port dir compose_file <<< "$backend_config"
    
    print_header "Stress Test: $backend"
    
    local backend_dir="$RESULTS_DIR/$backend"
    mkdir -p "$backend_dir"
    
    # Endpoint para teste
    local endpoint="http://localhost:$port/api/customers"
    
    print_info "Endpoint: $endpoint"
    print_info "Duração: $TEST_DURATION"
    print_info "Conexões: $CONNECTIONS"
    print_info "Threads: $THREADS"
    echo ""
    
    # Executar wrk
    print_info "Executando wrk..."
    if wrk -t"$THREADS" -c"$CONNECTIONS" -d"$TEST_DURATION" --latency "$endpoint" \
        > "$backend_dir/wrk_results.txt" 2>&1; then
        
        # Mostrar resumo
        echo ""
        print_success "Teste concluído!"
        echo ""
        grep -E "Requests/sec|Latency|Transfer/sec" "$backend_dir/wrk_results.txt" || true
        echo ""
        
        # Coletar métricas Docker
        print_info "Coletando métricas Docker..."
        docker stats --no-stream --format \
            "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}" \
            > "$backend_dir/docker_stats.txt" 2>&1
        
        # Salvar logs
        print_info "Salvando logs..."
        local original_dir=$(pwd)
        cd "$dir"
        docker-compose -f "$compose_file" logs --tail=100 \
            > "$backend_dir/logs.txt" 2>&1
        cd "$original_dir"
        
        print_success "Resultados salvos em: $backend_dir"
        return 0
    else
        print_error "Falha no stress test"
        return 1
    fi
}

generate_summary() {
    print_header "Gerando Relatório Comparativo Detalhado"
    
    local summary_file="$RESULTS_DIR/SUMMARY.md"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    cat > "$summary_file" << EOF
# Beauty Salon - Stress Test Results

**Data:** $timestamp  
**Duração do teste completo:** $(date -u -d @$SECONDS +%H:%M:%S 2>/dev/null || echo "N/A")  
**Diretório:** $RESULTS_DIR

## ⚙️ Configuração dos Testes

- **Duração por backend:** $TEST_DURATION
- **Conexões simultâneas:** $CONNECTIONS
- **Threads:** $THREADS
- **Endpoint testado:** /api/customers (GET)
- **Intervalo entre testes:** ${CLEANUP_WAIT}s

## 📊 Resumo Executivo

| Backend | Status | Req/s | Latency (avg) | Memória | CPU |
|---------|--------|-------|---------------|---------|-----|
EOF
    
    # Gerar tabela resumo
    for backend_config in "${BACKENDS[@]}"; do
        IFS='|' read -r backend port dir compose_file <<< "$backend_config"
        local backend_dir="$RESULTS_DIR/$backend"
        
        if [ -f "$backend_dir/wrk_results.txt" ]; then
            local status="✅ OK"
            local req_sec=$(grep "Requests/sec" "$backend_dir/wrk_results.txt" | awk '{print $2}' || echo "N/A")
            local latency=$(grep "Latency" "$backend_dir/wrk_results.txt" | head -1 | awk '{print $2}' || echo "N/A")
            local memory=$(grep -v "NAME" "$backend_dir/docker_stats.txt" | awk '{print $3}' | head -1 || echo "N/A")
            local cpu=$(grep -v "NAME" "$backend_dir/docker_stats.txt" | awk '{print $2}' | head -1 || echo "N/A")
        else
            local status="❌ FALHOU"
            local req_sec="-"
            local latency="-"
            local memory="-"
            local cpu="-"
        fi
        
        echo "| $backend | $status | $req_sec | $latency | $memory | $cpu |" >> "$summary_file"
    done
    
    cat >> "$summary_file" << 'EOF'

## 📈 Resultados Detalhados por Backend

EOF
    
    # Processar resultados detalhados de cada backend
    for backend_config in "${BACKENDS[@]}"; do
        IFS='|' read -r backend port dir compose_file <<< "$backend_config"
        local backend_dir="$RESULTS_DIR/$backend"
        
        echo "" >> "$summary_file"
        echo "---" >> "$summary_file"
        echo "" >> "$summary_file"
        echo "### 🔹 $backend" >> "$summary_file"
        echo "" >> "$summary_file"
        
        if [ -f "$backend_dir/wrk_results.txt" ]; then
            echo "**Status:** ✅ Teste concluído com sucesso" >> "$summary_file"
            echo "" >> "$summary_file"
            echo "#### Métricas de Performance" >> "$summary_file"
            echo '```' >> "$summary_file"
            grep -A 20 "Running" "$backend_dir/wrk_results.txt" >> "$summary_file" 2>/dev/null || true
            echo '```' >> "$summary_file"
            echo "" >> "$summary_file"
            
            # Adicionar métricas Docker
            if [ -f "$backend_dir/docker_stats.txt" ]; then
                echo "#### Recursos Docker" >> "$summary_file"
                echo '```' >> "$summary_file"
                cat "$backend_dir/docker_stats.txt" >> "$summary_file"
                echo '```' >> "$summary_file"
                echo "" >> "$summary_file"
            fi
            
            # Adicionar informações de erros nos logs
            if [ -f "$backend_dir/logs.txt" ]; then
                local errors=$(grep -i "error\|exception\|failed" "$backend_dir/logs.txt" | wc -l)
                if [ $errors -gt 0 ]; then
                    echo "#### ⚠️ Erros Detectados nos Logs" >> "$summary_file"
                    echo "" >> "$summary_file"
                    echo "Total de linhas com erros: $errors" >> "$summary_file"
                    echo "" >> "$summary_file"
                fi
            fi
        else
            echo "**Status:** ❌ Teste falhou ou não foi executado" >> "$summary_file"
            echo "" >> "$summary_file"
        fi
    done
    
    # Adicionar recomendações
    cat >> "$summary_file" << 'EOF'

## 💡 Recomendações

### Análise Comparativa

Baseado nos resultados acima:

1. **Melhor Performance (Throughput):** Verifique qual backend teve maior Requests/sec
2. **Menor Latência:** Identifique o backend com menor latência média
3. **Menor Uso de Recursos:** Compare memória e CPU utilizados
4. **Estabilidade:** Verifique backends sem erros nos logs

### Próximos Passos

- [ ] Analisar logs detalhados dos backends com erros
- [ ] Comparar resultados com benchmarks anteriores
- [ ] Identificar gargalos de performance
- [ ] Otimizar configurações dos backends com pior performance
- [ ] Executar testes com maior carga (mais conexões/duração)

---

**Gerado automaticamente por:** `stress-test-all-backends.sh`  
**Documentação:** `backend/scripts/README.md`

EOF
    
    print_success "Relatório detalhado gerado: $summary_file"
    
    # Criar arquivo de índice
    local index_file="$RESULTS_DIR/INDEX.txt"
    cat > "$index_file" << EOF
Beauty Salon - Stress Test Results
===================================

Data: $timestamp
Diretório: $RESULTS_DIR

Arquivos Disponíveis:
- SUMMARY.md (Relatório principal)

Resultados por Backend:
EOF
    
    for backend_config in "${BACKENDS[@]}"; do
        IFS='|' read -r backend port dir compose_file <<< "$backend_config"
        if [ -d "$RESULTS_DIR/$backend" ]; then
            echo "- $backend/" >> "$index_file"
            echo "  - wrk_results.txt (Métricas wrk)" >> "$index_file"
            echo "  - docker_stats.txt (Recursos Docker)" >> "$index_file"
            echo "  - logs.txt (Logs do container)" >> "$index_file"
        fi
    done
    
    print_success "Índice criado: $index_file"
}

# Menu de seleção
show_menu() {
    echo -e "\n${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}  Beauty Salon - Stress Test em Todos os Backends"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}\n"
    
    echo "Backends disponíveis:"
    echo ""
    local i=1
    for backend_config in "${BACKENDS[@]}"; do
        IFS='|' read -r backend port dir compose_file <<< "$backend_config"
        echo "  $i) $backend (porta $port)"
        i=$((i + 1))
    done
    echo ""
    echo "  a) Testar TODOS os backends (sequencial)"
    echo "  0) Sair"
    echo ""
}

# Main
main() {
    # Criar diretório de resultados
    mkdir -p "$RESULTS_DIR"
    
    print_header "Beauty Salon - Stress Test Suite"
    
    print_info "Resultados serão salvos em: $RESULTS_DIR"
    print_info "Configuração: $TEST_DURATION, $CONNECTIONS conexões, $THREADS threads"
    echo ""
    
    # Verificar se wrk está instalado
    if ! command -v wrk &> /dev/null; then
        print_error "wrk não está instalado!"
        print_info "Instale com: brew install wrk"
        exit 1
    fi
    
    # Se receber argumento "all", testar todos
    if [ "$1" == "all" ]; then
        print_info "Modo: Testar TODOS os backends"
        echo ""
        
        # Verificar configurações antes de iniciar
        print_header "Verificando Configurações"
        local config_errors=0
        for backend_config in "${BACKENDS[@]}"; do
            if ! verify_backend_config "$backend_config"; then
                config_errors=$((config_errors + 1))
            fi
        done
        
        if [ $config_errors -gt 0 ]; then
            print_error "$config_errors backend(s) com erro de configuração"
            print_warning "Deseja continuar mesmo assim? (s/N)"
            read -r response
            if [[ ! "$response" =~ ^[Ss]$ ]]; then
                print_info "Teste cancelado"
                exit 1
            fi
        else
            print_success "Todas as configurações estão OK!"
        fi
        echo ""
        
        # Garantir ambiente limpo antes de começar
        print_info "Limpando ambiente antes de iniciar testes..."
        stop_all_containers
        sleep 5
        
        local total=${#BACKENDS[@]}
        local current=0
        local successful=0
        local failed=0
        local start_time=$SECONDS
        
        for backend_config in "${BACKENDS[@]}"; do
            IFS='|' read -r backend port dir compose_file <<< "$backend_config"
            current=$((current + 1))
            
            echo ""
            echo "═══════════════════════════════════════════════════════════════" 
            print_info "Testando $backend [$current/$total]"
            echo "═══════════════════════════════════════════════════════════════"
            
            # Garantir que não há containers rodando
            stop_all_containers
            
            # Iniciar backend
            if start_backend "$backend_config"; then
                # Executar stress test
                if run_stress_test "$backend_config"; then
                    successful=$((successful + 1))
                else
                    failed=$((failed + 1))
                fi
            else
                print_error "Falha ao iniciar $backend"
                failed=$((failed + 1))
            fi
            
            # Parar backend e limpar completamente
            print_info "Parando $backend e limpando infraestrutura..."
            stop_all_containers
            
            # Pausa entre testes (30 segundos)
            if [ $current -lt $total ]; then
                print_info "Aguardando ${CLEANUP_WAIT}s antes do próximo teste..."
                for i in $(seq $CLEANUP_WAIT -1 1); do
                    echo -ne "\r${CYAN}⏳ Aguardando: ${i}s restantes...${NC}"
                    sleep 1
                done
                echo -e "\r${GREEN}✅ Pronto para próximo teste!${NC}          "
            fi
        done
        
        # Gerar relatório
        generate_summary
        
        # Limpeza final
        print_info "Limpeza final do ambiente..."
        stop_all_containers
        
        local end_time=$SECONDS
        local duration=$((end_time - start_time))
        local duration_formatted=$(date -u -d @$duration +%H:%M:%S 2>/dev/null || echo "${duration}s")
        
        # Resumo final
        echo ""
        echo "═══════════════════════════════════════════════════════════════" 
        print_header "Resumo Final"
        echo "═══════════════════════════════════════════════════════════════"
        echo ""
        print_success "✅ Testes concluídos: $successful/$total"
        if [ $failed -gt 0 ]; then
            print_error "❌ Testes falhados: $failed/$total"
        fi
        print_info "⏱️  Tempo total: $duration_formatted"
        print_info "📁 Resultados em: $RESULTS_DIR"
        print_info "📊 Relatório: $RESULTS_DIR/SUMMARY.md"
        print_info "📋 Índice: $RESULTS_DIR/INDEX.txt"
        echo ""
        
        # Mostrar preview do relatório
        if [ -f "$RESULTS_DIR/SUMMARY.md" ]; then
            print_header "Preview do Relatório"
            head -30 "$RESULTS_DIR/SUMMARY.md"
            echo ""
            print_info "Ver relatório completo: cat $RESULTS_DIR/SUMMARY.md"
        fi
        echo ""
        
    else
        # Testar backends específicos passados como argumentos
        print_header "Beauty Salon - Stress Test em Backends Específicos"
        echo ""
        print_info "Backends solicitados: $@"
        echo ""
        
        # Verificar se wrk está instalado
        if ! command -v wrk &> /dev/null; then
            print_error "wrk não está instalado. Instale com: brew install wrk"
            exit 1
        fi
        
        # Criar diretório de resultados
        mkdir -p "$RESULTS_DIR"
        
        # Verificar configurações dos backends solicitados
        print_header "Verificando Configurações"
        local config_errors=0
        local backends_to_test=()
        
        for requested_backend in "$@"; do
            local found=false
            for backend_config in "${BACKENDS[@]}"; do
                IFS='|' read -r backend port dir compose_file <<< "$backend_config"
                if [ "$backend" == "$requested_backend" ]; then
                    found=true
                    if ! verify_backend_config "$backend_config"; then
                        config_errors=$((config_errors + 1))
                    else
                        backends_to_test+=("$backend_config")
                    fi
                    break
                fi
            done
            
            if [ "$found" = false ]; then
                print_error "Backend '$requested_backend' não encontrado"
                print_info "Backends disponíveis: java-reactive-native, java-reactive-jvm, nodejs, python, go"
                config_errors=$((config_errors + 1))
            fi
        done
        
        if [ $config_errors -gt 0 ]; then
            print_error "$config_errors erro(s) de configuração encontrado(s)"
            exit 1
        fi
        
        print_success "Todas as configurações estão OK!"
        echo ""
        
        # Limpar ambiente antes de iniciar
        print_info "Limpando ambiente antes de iniciar testes..."
        stop_all_containers
        echo ""
        
        # Executar testes nos backends solicitados
        local total=${#backends_to_test[@]}
        local current=0
        local successful=0
        local failed=0
        local start_time=$(date +%s)
        
        for backend_config in "${backends_to_test[@]}"; do
            IFS='|' read -r backend port dir compose_file <<< "$backend_config"
            current=$((current + 1))
            
            echo "═══════════════════════════════════════════════════════════════"
            print_info "Testando $backend [$current/$total]"
            echo "═══════════════════════════════════════════════════════════════"
            
            # Garantir que não há containers rodando
            stop_all_containers
            
            # Iniciar backend
            if start_backend "$backend_config"; then
                # Executar stress test
                if run_stress_test "$backend_config"; then
                    successful=$((successful + 1))
                else
                    failed=$((failed + 1))
                fi
            else
                print_error "Falha ao iniciar $backend"
                failed=$((failed + 1))
            fi
            
            # Parar backend e limpar completamente
            print_info "Parando $backend e limpando infraestrutura..."
            stop_all_containers
            
            # Pausa entre testes
            if [ $current -lt $total ]; then
                print_info "Aguardando ${CLEANUP_WAIT}s antes do próximo teste..."
                for i in $(seq $CLEANUP_WAIT -1 1); do
                    echo -ne "\r${CYAN}⏳ Aguardando: ${i}s restantes...${NC}"
                    sleep 1
                done
                echo -e "\r${GREEN}✅ Pronto para próximo teste!${NC}          "
            fi
        done
        
        # Gerar relatório
        generate_summary
        
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        local duration_formatted=$(date -u -d @$duration +%H:%M:%S 2>/dev/null || echo "${duration}s")
        
        # Resumo final
        echo ""
        echo "═══════════════════════════════════════════════════════════════" 
        print_header "Resumo Final"
        echo "═══════════════════════════════════════════════════════════════"
        echo ""
        print_success "✅ Testes concluídos: $successful/$total"
        if [ $failed -gt 0 ]; then
            print_error "❌ Testes falhados: $failed/$total"
        fi
        print_info "⏱️  Tempo total: $duration_formatted"
        print_info "📁 Resultados em: $RESULTS_DIR"
        print_info "📊 Relatório: $RESULTS_DIR/SUMMARY.md"
        echo ""
    fi
}

main "$@"
