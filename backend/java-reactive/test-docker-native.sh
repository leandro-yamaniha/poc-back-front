#!/bin/bash

# Script de teste para containerização do executável nativo
# Beauty Salon - Backend Reactive Native

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Verificar Docker
check_docker() {
    print_header "Verificando Docker"
    
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker não está rodando!"
        print_info "Por favor, inicie o Docker Desktop e tente novamente."
        exit 1
    fi
    
    print_success "Docker está rodando"
    docker --version
}

# Verificar executável nativo
check_executable() {
    print_header "Verificando Executável Nativo"
    
    if [ ! -f "target/beauty-salon-reactive" ]; then
        print_error "Executável não encontrado em target/beauty-salon-reactive"
        print_info "Execute: ./mvnw clean package native:compile -Pnative -DskipTests"
        exit 1
    fi
    
    print_success "Executável encontrado"
    ls -lh target/beauty-salon-reactive
}

# Parar containers existentes
stop_existing() {
    print_header "Parando Containers Existentes"
    
    if docker-compose -f docker-compose.native.yml ps -q 2>/dev/null | grep -q .; then
        print_info "Parando containers..."
        docker-compose -f docker-compose.native.yml down -v
        print_success "Containers parados"
    else
        print_info "Nenhum container rodando"
    fi
}

# Iniciar containers
start_containers() {
    print_header "Iniciando Containers"
    
    print_info "Iniciando docker-compose..."
    docker-compose -f docker-compose.native.yml up -d
    
    print_success "Containers iniciados"
}

# Aguardar Cassandra
wait_cassandra() {
    print_header "Aguardando Cassandra"
    
    print_info "Aguardando Cassandra ficar healthy (pode levar ~60s)..."
    
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if docker-compose -f docker-compose.native.yml ps cassandra | grep -q "healthy"; then
            print_success "Cassandra está healthy!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "Timeout aguardando Cassandra"
    return 1
}

# Aguardar Backend
wait_backend() {
    print_header "Aguardando Backend Native"
    
    print_info "Aguardando backend iniciar..."
    
    local max_attempts=15
    local attempt=0
    local port=${SERVER_PORT:-8086}
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
            print_success "Backend está respondendo!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "Timeout aguardando backend"
    return 1
}

# Testar endpoints
test_endpoints() {
    print_header "Testando Endpoints"
    
    local port=${SERVER_PORT:-8086}
    local base_url="http://localhost:$port"
    
    # Health check
    print_info "Testando /actuator/health..."
    if response=$(curl -s "$base_url/actuator/health"); then
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        print_success "Health check OK"
    else
        print_error "Health check falhou"
        return 1
    fi
    
    # Test endpoint
    print_info "Testando /api/test/hello..."
    if response=$(curl -s "$base_url/api/test/hello"); then
        echo "$response"
        print_success "Test endpoint OK"
    else
        print_warning "Test endpoint não disponível"
    fi
    
    # Customers endpoint
    print_info "Testando /api/customers..."
    if response=$(curl -s "$base_url/api/customers"); then
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        print_success "Customers endpoint OK"
    else
        print_warning "Customers endpoint não disponível"
    fi
}

# Mostrar logs
show_logs() {
    print_header "Logs do Backend"
    
    print_info "Últimas 30 linhas dos logs:"
    docker-compose -f docker-compose.native.yml logs --tail=30 backend-reactive-native
}

# Mostrar estatísticas
show_stats() {
    print_header "Estatísticas dos Containers"
    
    print_info "Uso de recursos:"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}" \
        beauty-salon-backend-reactive-native beauty-salon-cassandra-reactive-native
}

# Mostrar informações de acesso
show_access_info() {
    print_header "Informações de Acesso"
    
    local port=${SERVER_PORT:-8086}
    
    echo -e "${GREEN}Backend Reactive Native:${NC}"
    echo "  URL: http://localhost:$port"
    echo "  Health: http://localhost:$port/actuator/health"
    echo "  API: http://localhost:$port/api/customers"
    echo "  Swagger: http://localhost:$port/swagger-ui/index.html"
    echo ""
    echo -e "${GREEN}Cassandra:${NC}"
    echo "  Host: localhost"
    echo "  Port: 9045"
    echo "  Keyspace: beauty_salon"
    echo ""
    echo -e "${YELLOW}Comandos úteis:${NC}"
    echo "  Ver logs: docker-compose -f docker-compose.native.yml logs -f backend-reactive-native"
    echo "  Parar: docker-compose -f docker-compose.native.yml down"
    echo "  Parar e limpar: docker-compose -f docker-compose.native.yml down -v"
}

# Menu principal
show_menu() {
    echo -e "\n${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}  Teste de Containerização - Backend Reactive Native"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}\n"
    
    echo "1) Teste Completo (start + wait + test)"
    echo "2) Iniciar containers"
    echo "3) Parar containers"
    echo "4) Ver logs"
    echo "5) Ver estatísticas"
    echo "6) Testar endpoints"
    echo "7) Mostrar informações de acesso"
    echo "0) Sair"
    echo ""
    read -p "Escolha uma opção: " choice
    
    case $choice in
        1)
            check_docker
            check_executable
            stop_existing
            start_containers
            wait_cassandra
            wait_backend
            test_endpoints
            show_stats
            show_access_info
            ;;
        2)
            check_docker
            check_executable
            start_containers
            ;;
        3)
            stop_existing
            ;;
        4)
            show_logs
            ;;
        5)
            show_stats
            ;;
        6)
            test_endpoints
            ;;
        7)
            show_access_info
            ;;
        0)
            print_info "Saindo..."
            exit 0
            ;;
        *)
            print_error "Opção inválida"
            ;;
    esac
}

# Main
main() {
    # Se não houver argumentos, mostrar menu
    if [ $# -eq 0 ]; then
        while true; do
            show_menu
            echo ""
            read -p "Pressione ENTER para continuar..."
        done
    fi
    
    # Processar argumentos
    case "$1" in
        start)
            check_docker
            check_executable
            stop_existing
            start_containers
            wait_cassandra
            wait_backend
            test_endpoints
            show_stats
            show_access_info
            ;;
        stop)
            stop_existing
            ;;
        test)
            test_endpoints
            ;;
        logs)
            show_logs
            ;;
        stats)
            show_stats
            ;;
        info)
            show_access_info
            ;;
        *)
            echo "Uso: $0 {start|stop|test|logs|stats|info}"
            echo "  ou execute sem argumentos para menu interativo"
            exit 1
            ;;
    esac
}

main "$@"
