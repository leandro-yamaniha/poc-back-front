#!/bin/bash

# Beauty Salon Backend Stress Test Runner
# Este script executa diferentes tipos de stress tests no backend

set -e

echo "🚀 Beauty Salon Backend Stress Test Runner"
echo "=========================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se o backend está rodando
check_backend() {
    log_info "Verificando se o backend está rodando..."
    if curl -f -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "Backend está rodando em http://localhost:8080"
        return 0
    else
        log_error "Backend não está rodando em http://localhost:8080"
        log_info "Por favor, inicie o backend antes de executar os stress tests"
        return 1
    fi
}

# Executar testes programáticos de stress
run_programmatic_tests() {
    log_info "Executando testes programáticos de stress..."
    echo ""
    
    cd "$(dirname "$0")/.."
    
    log_info "Executando BackendStressTest..."
    ./mvnw test -Dtest="BackendStressTest" -Dmaven.test.failure.ignore=true
    
    if [ $? -eq 0 ]; then
        log_success "Testes programáticos de stress completados com sucesso!"
    else
        log_warning "Alguns testes programáticos falharam. Verifique os logs acima."
    fi
}

# Executar JMeter stress test
run_jmeter_test() {
    log_info "Verificando se JMeter está instalado..."
    
    if command -v jmeter &> /dev/null; then
        log_success "JMeter encontrado!"
        
        log_info "Executando JMeter stress test..."
        cd "$(dirname "$0")/.."
        
        # Criar diretório para resultados se não existir
        mkdir -p target/stress-test-results
        
        # Executar JMeter em modo não-GUI
        jmeter -n -t src/test/resources/stress-test/beauty-salon-stress-test.jmx \
               -l target/stress-test-results/results.jtl \
               -e -o target/stress-test-results/html-report
        
        if [ $? -eq 0 ]; then
            log_success "JMeter stress test completado!"
            log_info "Relatório HTML disponível em: target/stress-test-results/html-report/index.html"
        else
            log_error "JMeter stress test falhou!"
        fi
    else
        log_warning "JMeter não está instalado."
        log_info "Para instalar JMeter:"
        log_info "  macOS: brew install jmeter"
        log_info "  Ubuntu: sudo apt-get install jmeter"
        log_info "  Windows: Baixe de https://jmeter.apache.org/download_jmeter.cgi"
    fi
}

# Executar teste de carga com curl
run_curl_load_test() {
    log_info "Executando teste de carga simples com curl..."
    
    # Teste simples de GET
    log_info "Testando GET /api/customers (100 requisições concorrentes)..."
    
    for i in {1..100}; do
        curl -s -o /dev/null -w "%{http_code} %{time_total}s\n" http://localhost:8080/api/customers &
    done
    wait
    
    log_success "Teste de carga com curl completado!"
}

# Menu principal
show_menu() {
    echo ""
    log_info "Escolha o tipo de stress test:"
    echo "1) Testes Programáticos (Spring Boot + MockMvc)"
    echo "2) JMeter Stress Test"
    echo "3) Teste de Carga Simples (curl)"
    echo "4) Executar Todos os Testes"
    echo "5) Sair"
    echo ""
}

# Função principal
main() {
    # Verificar se o backend está rodando
    if ! check_backend; then
        exit 1
    fi
    
    # Se argumentos foram passados, executar diretamente
    if [ $# -gt 0 ]; then
        case $1 in
            "programmatic"|"1")
                run_programmatic_tests
                ;;
            "jmeter"|"2")
                run_jmeter_test
                ;;
            "curl"|"3")
                run_curl_load_test
                ;;
            "all"|"4")
                run_programmatic_tests
                echo ""
                run_jmeter_test
                echo ""
                run_curl_load_test
                ;;
            *)
                log_error "Argumento inválido: $1"
                log_info "Uso: $0 [programmatic|jmeter|curl|all]"
                exit 1
                ;;
        esac
        exit 0
    fi
    
    # Menu interativo
    while true; do
        show_menu
        read -p "Digite sua escolha (1-5): " choice
        
        case $choice in
            1)
                run_programmatic_tests
                ;;
            2)
                run_jmeter_test
                ;;
            3)
                run_curl_load_test
                ;;
            4)
                run_programmatic_tests
                echo ""
                run_jmeter_test
                echo ""
                run_curl_load_test
                ;;
            5)
                log_info "Saindo..."
                exit 0
                ;;
            *)
                log_error "Opção inválida. Por favor, escolha 1-5."
                ;;
        esac
        
        echo ""
        read -p "Pressione Enter para continuar..."
    done
}

# Executar função principal
main "$@"
