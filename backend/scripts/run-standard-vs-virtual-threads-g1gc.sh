#!/bin/bash

# Script para comparar performance entre configuração padrão e Virtual Threads
# Ambos usando G1GC para isolar o impacto específico das Virtual Threads

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configurações
RESULTS_DIR="standard-vs-virtual-threads-g1gc-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
STANDARD_LOG="$RESULTS_DIR/standard_g1gc_$TIMESTAMP.log"
VIRTUAL_THREADS_LOG="$RESULTS_DIR/virtual_threads_g1gc_$TIMESTAMP.log"
COMPARISON_REPORT="$RESULTS_DIR/comparison_report_g1gc_$TIMESTAMP.md"

echo -e "${BLUE}🚀 COMPARAÇÃO PADRÃO vs VIRTUAL THREADS (G1GC)${NC}"
echo -e "${BLUE}====================================================${NC}"
echo -e "${YELLOW}📁 Resultados serão salvos em: $RESULTS_DIR${NC}"
echo ""

# Criar diretório de resultados
mkdir -p "$RESULTS_DIR"

echo -e "${GREEN}🎯 Iniciando Testes de Comparação de Performance${NC}"
echo ""

# Função para extrair métricas dos logs
extract_metrics() {
    local log_file="$1"
    local config_name="$2"
    
    echo "## $config_name Results" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    
    # Extrair informações de configuração
    if grep -q "Virtual Threads: HABILITADAS" "$log_file"; then
        echo "### Configuration Details" >> "$COMPARISON_REPORT"
        echo "   📋 Active Profile: virtual-threads-g1gc" >> "$COMPARISON_REPORT"
        echo "   🧵 Virtual Threads: ENABLED" >> "$COMPARISON_REPORT"
        echo "   🗑️  GC Type: G1GC" >> "$COMPARISON_REPORT"
        echo "   ⚙️  Java Version: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)" >> "$COMPARISON_REPORT"
    else
        echo "### Configuration Details" >> "$COMPARISON_REPORT"
        echo "   📋 Active Profile: test" >> "$COMPARISON_REPORT"
        echo "   🧵 Virtual Threads: DISABLED" >> "$COMPARISON_REPORT"
        echo "   🗑️  GC Type: G1GC" >> "$COMPARISON_REPORT"
        echo "   ⚙️  Java Version: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)" >> "$COMPARISON_REPORT"
    fi
    echo "" >> "$COMPARISON_REPORT"
    
    # Extrair métricas de performance
    echo "### Performance Metrics" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    
    # Throughput médio
    if grep -q "Throughput Médio:" "$log_file"; then
        local avg_throughput=$(grep "Throughput Médio:" "$log_file" | tail -1 | sed 's/.*Throughput Médio: \([0-9,\.]*\) req\/s.*/\1/')
        echo "#### Overall Performance:" >> "$COMPARISON_REPORT"
        echo "   🚀 Average Throughput: $avg_throughput req/s" >> "$COMPARISON_REPORT"
    fi
    
    # Tempo de resposta médio
    if grep -q "Tempo de Resposta Médio:" "$log_file"; then
        local avg_response_time=$(grep "Tempo de Resposta Médio:" "$log_file" | tail -1 | sed 's/.*Tempo de Resposta Médio: \([0-9]*\) ms.*/\1/')
        echo "   ⏱️  Average Response Time: $avg_response_time ms" >> "$COMPARISON_REPORT"
    fi
    
    # Total de requisições
    if grep -q "Total de Requisições:" "$log_file"; then
        local total_requests=$(grep "Total de Requisições:" "$log_file" | tail -1 | sed 's/.*Total de Requisições: \([0-9]*\).*/\1/')
        echo "   📊 Total Requests: $total_requests" >> "$COMPARISON_REPORT"
    fi
    
    echo "   ✅ Configuration: $config_name" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    
    # Extrair resultados detalhados dos testes
    echo "#### Test Results:" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    
    # Teste de Criação
    if grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep -q "Taxa de Sucesso:"; then
        echo "**Customer Creation:**" >> "$COMPARISON_REPORT"
        local creation_success=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Taxa de Sucesso:" | head -1 | sed 's/.*Taxa de Sucesso: \([0-9,\.]*%\).*/\1/')
        local creation_throughput=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Throughput:" | head -1 | sed 's/.*Throughput: \([0-9,\.]*\) req\/s.*/\1/')
        local creation_avg_time=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Tempo Médio:" | head -1 | sed 's/.*Tempo Médio: \([0-9]*\) ms.*/\1/')
        local creation_min_time=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Tempo Mínimo:" | head -1 | sed 's/.*Tempo Mínimo: \([0-9]*\) ms.*/\1/')
        local creation_max_time=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Tempo Máximo:" | head -1 | sed 's/.*Tempo Máximo: \([0-9]*\) ms.*/\1/')
        local creation_total_time=$(grep -A 10 "Teste 1 - Criação de Clientes:" "$log_file" | grep "Tempo Total:" | head -1 | sed 's/.*Tempo Total: \([0-9]*\) ms.*/\1/')
        
        echo "   📊 Creation Results:" >> "$COMPARISON_REPORT"
        echo "      ✅ Success Rate: $creation_success" >> "$COMPARISON_REPORT"
        echo "      🚀 Throughput: $creation_throughput req/s" >> "$COMPARISON_REPORT"
        echo "      ⏱️  Avg Response Time: $creation_avg_time ms" >> "$COMPARISON_REPORT"
        echo "      ⚡ Min Response Time: $creation_min_time ms" >> "$COMPARISON_REPORT"
        echo "      🔥 Max Response Time: $creation_max_time ms" >> "$COMPARISON_REPORT"
        echo "      ⏰ Total Time: $creation_total_time ms" >> "$COMPARISON_REPORT"
        echo "" >> "$COMPARISON_REPORT"
    fi
    
    # Teste de Busca
    if grep -A 10 "Teste 2 - Busca de Clientes:" "$log_file" | grep -q "Taxa de Sucesso:"; then
        echo "**Customer Retrieval:**" >> "$COMPARISON_REPORT"
        local retrieval_success=$(grep -A 10 "Teste 2 - Busca de Clientes:" "$log_file" | grep "Taxa de Sucesso:" | head -1 | sed 's/.*Taxa de Sucesso: \([0-9,\.]*%\).*/\1/')
        local retrieval_throughput=$(grep -A 10 "Teste 2 - Busca de Clientes:" "$log_file" | grep "Throughput:" | head -1 | sed 's/.*Throughput: \([0-9,\.]*\) req\/s.*/\1/')
        local retrieval_avg_time=$(grep -A 10 "Teste 2 - Busca de Clientes:" "$log_file" | grep "Tempo Médio:" | head -1 | sed 's/.*Tempo Médio: \([0-9]*\) ms.*/\1/')
        
        echo "   📊 Retrieval Results:" >> "$COMPARISON_REPORT"
        echo "      ✅ Success Rate: $retrieval_success" >> "$COMPARISON_REPORT"
        echo "      🚀 Throughput: $retrieval_throughput req/s" >> "$COMPARISON_REPORT"
        echo "      ⏱️  Avg Response Time: $retrieval_avg_time ms" >> "$COMPARISON_REPORT"
        echo "" >> "$COMPARISON_REPORT"
    fi
    
    # Teste CRUD Misto
    if grep -A 10 "Teste 3 - Operações CRUD Mistas:" "$log_file" | grep -q "Taxa de Sucesso:"; then
        echo "**Mixed CRUD:**" >> "$COMPARISON_REPORT"
        local mixed_success=$(grep -A 10 "Teste 3 - Operações CRUD Mistas:" "$log_file" | grep "Taxa de Sucesso:" | head -1 | sed 's/.*Taxa de Sucesso: \([0-9,\.]*%\).*/\1/')
        local mixed_throughput=$(grep -A 10 "Teste 3 - Operações CRUD Mistas:" "$log_file" | grep "Throughput:" | head -1 | sed 's/.*Throughput: \([0-9,\.]*\) req\/s.*/\1/')
        local mixed_avg_time=$(grep -A 10 "Teste 3 - Operações CRUD Mistas:" "$log_file" | grep "Tempo Médio:" | head -1 | sed 's/.*Tempo Médio: \([0-9]*\) ms.*/\1/')
        
        echo "   📊 Mixed CRUD Results:" >> "$COMPARISON_REPORT"
        echo "      ✅ Success Rate: $mixed_success" >> "$COMPARISON_REPORT"
        echo "      🚀 Throughput: $mixed_throughput req/s" >> "$COMPARISON_REPORT"
        echo "      ⏱️  Avg Response Time: $mixed_avg_time ms" >> "$COMPARISON_REPORT"
        echo "" >> "$COMPARISON_REPORT"
    fi
    
    echo "---" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
}

# Função para gerar análise comparativa
generate_comparison_analysis() {
    echo "" >> "$COMPARISON_REPORT"
    echo "## Performance Comparison Analysis" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "### Key Findings" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "Esta comparação isola o impacto específico das **Virtual Threads** mantendo o mesmo garbage collector (G1GC) em ambas as configurações." >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "### Expected Benefits of Virtual Threads:" >> "$COMPARISON_REPORT"
    echo "- 🧵 **Melhor Concorrência**: Gerenciamento mais eficiente de threads para I/O intensivo" >> "$COMPARISON_REPORT"
    echo "- 💾 **Menor Uso de Memória**: Threads virtuais consomem menos memória que threads de plataforma" >> "$COMPARISON_REPORT"
    echo "- 🚀 **Maior Throughput**: Especialmente para operações que fazem muitas chamadas de I/O (banco de dados)" >> "$COMPARISON_REPORT"
    echo "- ⚡ **Menor Latência**: Redução no tempo de context switching entre threads" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "### Recommendations:" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "**Use Virtual Threads when:**" >> "$COMPARISON_REPORT"
    echo "- Aplicação faz muitas operações I/O (banco de dados, APIs externas)" >> "$COMPARISON_REPORT"
    echo "- Necessita lidar com alta concorrência (muitos usuários simultâneos)" >> "$COMPARISON_REPORT"
    echo "- Throughput é mais importante que latência individual" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "**Use Standard Configuration when:**" >> "$COMPARISON_REPORT"
    echo "- Aplicação é CPU-intensiva" >> "$COMPARISON_REPORT"
    echo "- Latência individual é crítica" >> "$COMPARISON_REPORT"
    echo "- Compatibilidade com bibliotecas legadas é necessária" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
    echo "### Test Environment:" >> "$COMPARISON_REPORT"
    echo "- **Java Version:** $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)" >> "$COMPARISON_REPORT"
    echo "- **GC:** G1GC (ambas configurações)" >> "$COMPARISON_REPORT"
    echo "- **Concurrent Users:** 100" >> "$COMPARISON_REPORT"
    echo "- **Requests per User:** 10" >> "$COMPARISON_REPORT"
    echo "- **Total Requests per Test:** 1,000" >> "$COMPARISON_REPORT"
    echo "- **Database:** Cassandra 4.1 (Testcontainers)" >> "$COMPARISON_REPORT"
    echo "" >> "$COMPARISON_REPORT"
}

# Inicializar relatório de comparação
echo "# Standard vs Virtual Threads (G1GC) Performance Comparison" > "$COMPARISON_REPORT"
echo "" >> "$COMPARISON_REPORT"
echo "**Test Date:** $(date)" >> "$COMPARISON_REPORT"
echo "**Java Version:** $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)" >> "$COMPARISON_REPORT"
echo "" >> "$COMPARISON_REPORT"
echo "## Test Configuration" >> "$COMPARISON_REPORT"
echo "" >> "$COMPARISON_REPORT"
echo "- **Concurrent Users:** 100" >> "$COMPARISON_REPORT"
echo "- **Requests per User:** 10" >> "$COMPARISON_REPORT"
echo "- **Total Requests per Test:** 1,000" >> "$COMPARISON_REPORT"
echo "- **Total Tests:** 3 (Creation, Retrieval, Mixed CRUD)" >> "$COMPARISON_REPORT"
echo "- **GC:** G1GC (both configurations)" >> "$COMPARISON_REPORT"
echo "- **Objective:** Isolate Virtual Threads impact using same GC" >> "$COMPARISON_REPORT"
echo "" >> "$COMPARISON_REPORT"
echo "---" >> "$COMPARISON_REPORT"
echo "" >> "$COMPARISON_REPORT"

# Teste 1: Configuração Padrão (G1GC)
echo -e "${BLUE}🔧 Teste 1: Configuração Padrão (G1GC)${NC}"
echo -e "${YELLOW}🔍 Executando teste com configuração padrão...${NC}"
echo -e "${YELLOW}   JVM Args: -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200${NC}"
echo -e "${YELLOW}   Spring Profile: test${NC}"
echo -e "${YELLOW}   Output: $STANDARD_LOG${NC}"

# Executar teste padrão
if MAVEN_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200" \
    ./mvnw test -Dtest=StandardVsVirtualThreadsTest -Dspring.profiles.active=test \
    -Dmaven.test.skip=false \
    > "$STANDARD_LOG" 2>&1; then
    echo -e "${GREEN}✅ Teste padrão concluído com sucesso${NC}"
else
    echo -e "${RED}❌ Teste padrão falhou${NC}"
    echo -e "${YELLOW}📋 Verificar logs em: $STANDARD_LOG${NC}"
fi

echo ""

# Teste 2: Virtual Threads (G1GC)
echo -e "${BLUE}🔧 Teste 2: Virtual Threads (G1GC)${NC}"
echo -e "${YELLOW}🔍 Executando teste com Virtual Threads...${NC}"
echo -e "${YELLOW}   JVM Args: -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200${NC}"
echo -e "${YELLOW}   Spring Profile: virtual-threads-g1gc${NC}"
echo -e "${YELLOW}   Output: $VIRTUAL_THREADS_LOG${NC}"

# Executar teste Virtual Threads
if MAVEN_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200" \
    ./mvnw test -Dtest=StandardVsVirtualThreadsTest -Dspring.profiles.active=virtual-threads-g1gc \
    -Dmaven.test.skip=false \
    > "$VIRTUAL_THREADS_LOG" 2>&1; then
    echo -e "${GREEN}✅ Teste Virtual Threads concluído com sucesso${NC}"
else
    echo -e "${RED}❌ Teste Virtual Threads falhou${NC}"
    echo -e "${YELLOW}📋 Verificar logs em: $VIRTUAL_THREADS_LOG${NC}"
fi

echo ""

# Gerar relatório de comparação
echo -e "${BLUE}📊 Gerando Relatório de Comparação${NC}"

# Extrair métricas dos logs
if [ -f "$STANDARD_LOG" ]; then
    extract_metrics "$STANDARD_LOG" "Standard (G1GC)"
fi

if [ -f "$VIRTUAL_THREADS_LOG" ]; then
    extract_metrics "$VIRTUAL_THREADS_LOG" "Virtual Threads (G1GC)"
fi

# Gerar análise comparativa
generate_comparison_analysis

echo -e "${GREEN}✅ Relatório de comparação gerado: $COMPARISON_REPORT${NC}"
echo ""

# Exibir resumo
echo -e "${BLUE}📋 RESUMO DA COMPARAÇÃO${NC}"
echo -e "${BLUE}========================${NC}"
echo -e "${YELLOW}📁 Diretório de Resultados: $RESULTS_DIR${NC}"
echo -e "${YELLOW}📊 Relatório Principal: $COMPARISON_REPORT${NC}"
echo -e "${YELLOW}📋 Log Padrão: $STANDARD_LOG${NC}"
echo -e "${YELLOW}📋 Log Virtual Threads: $VIRTUAL_THREADS_LOG${NC}"
echo ""

# Exibir preview do relatório se existir
if [ -f "$COMPARISON_REPORT" ]; then
    echo -e "${GREEN}🔍 Preview do Relatório:${NC}"
    echo -e "${GREEN}========================${NC}"
    head -30 "$COMPARISON_REPORT"
    echo ""
    echo -e "${YELLOW}📖 Para ver o relatório completo: cat $COMPARISON_REPORT${NC}"
fi

echo ""
echo -e "${GREEN}🏆 Comparação Standard vs Virtual Threads (G1GC) concluída!${NC}"
echo -e "${BLUE}🎯 Objetivo alcançado: Impacto isolado das Virtual Threads analisado${NC}"
