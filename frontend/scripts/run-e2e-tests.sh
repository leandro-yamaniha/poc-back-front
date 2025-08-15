#!/bin/bash

# Beauty Salon E2E Test Runner
# Script para executar testes end-to-end com Cypress

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_message() {
    echo -e "${2}${1}${NC}"
}

# Função para verificar se o backend está rodando
check_backend() {
    print_message "Verificando se o backend está rodando..." $BLUE
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        print_message "✅ Backend está rodando na porta 8080" $GREEN
        return 0
    else
        print_message "❌ Backend não está rodando na porta 8080" $RED
        return 1
    fi
}

# Função para verificar se o frontend está rodando
check_frontend() {
    print_message "Verificando se o frontend está rodando..." $BLUE
    if curl -s http://localhost:3000 > /dev/null 2>&1; then
        print_message "✅ Frontend está rodando na porta 3000" $GREEN
        return 0
    else
        print_message "❌ Frontend não está rodando na porta 3000" $RED
        return 1
    fi
}

# Função para iniciar o backend
start_backend() {
    print_message "Iniciando backend..." $YELLOW
    cd ../backend-nodejs
    npm start &
    BACKEND_PID=$!
    echo $BACKEND_PID > .backend.pid
    
    # Aguarda o backend iniciar
    for i in {1..30}; do
        if check_backend; then
            break
        fi
        sleep 2
    done
    
    cd ../frontend
}

# Função para iniciar o frontend
start_frontend() {
    print_message "Iniciando frontend..." $YELLOW
    npm start &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > .frontend.pid
    
    # Aguarda o frontend iniciar
    for i in {1..30}; do
        if check_frontend; then
            break
        fi
        sleep 2
    done
}

# Função para parar serviços
cleanup() {
    print_message "Parando serviços..." $YELLOW
    
    if [ -f .frontend.pid ]; then
        kill $(cat .frontend.pid) 2>/dev/null || true
        rm .frontend.pid
    fi
    
    if [ -f ../backend-nodejs/.backend.pid ]; then
        kill $(cat ../backend-nodejs/.backend.pid) 2>/dev/null || true
        rm ../backend-nodejs/.backend.pid
    fi
    
    # Mata processos por porta se necessário
    lsof -ti:3000 | xargs kill -9 2>/dev/null || true
    lsof -ti:8080 | xargs kill -9 2>/dev/null || true
}

# Função para executar testes
run_tests() {
    local mode=$1
    local spec=$2
    
    print_message "Executando testes E2E..." $BLUE
    
    case $mode in
        "headless")
            if [ -n "$spec" ]; then
                npx cypress run --spec "cypress/e2e/$spec"
            else
                npx cypress run
            fi
            ;;
        "headed")
            if [ -n "$spec" ]; then
                npx cypress open --e2e --spec "cypress/e2e/$spec"
            else
                npx cypress open --e2e
            fi
            ;;
        "interactive")
            npx cypress open
            ;;
        *)
            print_message "Modo inválido: $mode" $RED
            exit 1
            ;;
    esac
}

# Função para mostrar ajuda
show_help() {
    echo "Beauty Salon E2E Test Runner"
    echo ""
    echo "Uso: $0 [OPÇÕES]"
    echo ""
    echo "Opções:"
    echo "  -m, --mode MODE        Modo de execução: headless, headed, interactive (padrão: headless)"
    echo "  -s, --spec SPEC        Arquivo de teste específico (ex: homepage.cy.js)"
    echo "  -k, --keep-running     Manter serviços rodando após os testes"
    echo "  -n, --no-start         Não iniciar serviços (assumir que já estão rodando)"
    echo "  -h, --help             Mostrar esta ajuda"
    echo ""
    echo "Exemplos:"
    echo "  $0                                    # Executa todos os testes em modo headless"
    echo "  $0 -m headed                         # Executa testes com interface gráfica"
    echo "  $0 -m interactive                    # Abre Cypress Test Runner"
    echo "  $0 -s homepage.cy.js                 # Executa apenas teste da homepage"
    echo "  $0 -n -m headed                      # Executa testes assumindo serviços já rodando"
}

# Valores padrão
MODE="headless"
SPEC=""
KEEP_RUNNING=false
NO_START=false

# Parse dos argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -m|--mode)
            MODE="$2"
            shift 2
            ;;
        -s|--spec)
            SPEC="$2"
            shift 2
            ;;
        -k|--keep-running)
            KEEP_RUNNING=true
            shift
            ;;
        -n|--no-start)
            NO_START=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            print_message "Opção desconhecida: $1" $RED
            show_help
            exit 1
            ;;
    esac
done

# Trap para cleanup em caso de interrupção
trap cleanup EXIT

print_message "🚀 Beauty Salon E2E Test Runner" $BLUE
print_message "Modo: $MODE" $BLUE
if [ -n "$SPEC" ]; then
    print_message "Spec: $SPEC" $BLUE
fi

# Verifica se Cypress está instalado
if ! command -v npx &> /dev/null; then
    print_message "❌ npx não encontrado. Instale Node.js e npm." $RED
    exit 1
fi

if ! npx cypress version &> /dev/null; then
    print_message "❌ Cypress não encontrado. Execute: npm install" $RED
    exit 1
fi

# Inicia serviços se necessário
if [ "$NO_START" = false ]; then
    if ! check_backend; then
        start_backend
    fi
    
    if ! check_frontend; then
        start_frontend
    fi
else
    # Verifica se os serviços estão rodando
    if ! check_backend || ! check_frontend; then
        print_message "❌ Serviços não estão rodando. Use sem -n ou inicie manualmente." $RED
        exit 1
    fi
fi

# Aguarda um pouco para garantir que tudo está pronto
sleep 3

# Executa os testes
print_message "🧪 Iniciando execução dos testes..." $GREEN
if run_tests "$MODE" "$SPEC"; then
    print_message "✅ Testes executados com sucesso!" $GREEN
    exit_code=0
else
    print_message "❌ Alguns testes falharam!" $RED
    exit_code=1
fi

# Cleanup se não for para manter rodando
if [ "$KEEP_RUNNING" = false ]; then
    cleanup
else
    print_message "🔄 Serviços mantidos em execução conforme solicitado" $YELLOW
    print_message "Frontend: http://localhost:3000" $BLUE
    print_message "Backend: http://localhost:8080" $BLUE
fi

exit $exit_code
