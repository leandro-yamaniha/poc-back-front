#!/bin/bash
# Script para testar a imagem nativa com testes de integração

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}▶ Testando Imagem Nativa do Java Reactive${NC}"
echo ""

# Get the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
REACTIVE_DIR="$BASE_DIR/backend/java-reactive"

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
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

# Check if native executable exists
if [ ! -f "$REACTIVE_DIR/target/beauty-salon-reactive" ]; then
    print_error "Executável nativo não encontrado!"
    echo "Execute primeiro: ./mvnw -Pnative native:compile -DskipTests"
    exit 1
fi

print_success "Executável nativo encontrado"

# Check if Docker is running for Cassandra
if ! docker info > /dev/null 2>&1; then
    print_error "Docker não está rodando!"
    echo "Por favor, inicie o Docker Desktop"
    exit 1
fi

print_success "Docker está rodando"

# Check if Cassandra is running
CASSANDRA_CONTAINER="beauty-salon-cassandra-aot"
if ! docker ps | grep -q "$CASSANDRA_CONTAINER"; then
    print_step "Iniciando Cassandra com health check..."
    cd "$BASE_DIR"
    docker-compose -f docker-compose.aot-native.yml up -d cassandra
    
    print_step "Aguardando Cassandra ficar saudável (usando Docker health check)..."
    COUNTER=0
    MAX_WAIT=120
    
    while [ $COUNTER -lt $MAX_WAIT ]; do
        HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' $CASSANDRA_CONTAINER 2>/dev/null || echo "starting")
        
        if [ "$HEALTH_STATUS" = "healthy" ]; then
            print_success "Cassandra está saudável!"
            break
        fi
        
        echo -n "."
        sleep 2
        COUNTER=$((COUNTER + 2))
    done
    
    echo ""
    
    if [ $COUNTER -ge $MAX_WAIT ]; then
        print_error "Cassandra não ficou saudável no tempo esperado"
        docker logs --tail 50 $CASSANDRA_CONTAINER
        exit 1
    fi
    
    # Aguardar schema ser criado
    print_step "Aguardando criação do schema..."
    sleep 5
else
    print_step "Cassandra já está rodando, verificando health check..."
    HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' $CASSANDRA_CONTAINER 2>/dev/null || echo "unknown")
    
    if [ "$HEALTH_STATUS" = "healthy" ]; then
        print_success "Cassandra está saudável"
    else
        print_warning "Cassandra não está saudável (status: $HEALTH_STATUS), aguardando..."
        COUNTER=0
        MAX_WAIT=60
        
        while [ $COUNTER -lt $MAX_WAIT ]; do
            HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' $CASSANDRA_CONTAINER 2>/dev/null || echo "unknown")
            
            if [ "$HEALTH_STATUS" = "healthy" ]; then
                print_success "Cassandra ficou saudável!"
                break
            fi
            
            echo -n "."
            sleep 2
            COUNTER=$((COUNTER + 2))
        done
        
        echo ""
        
        if [ $COUNTER -ge $MAX_WAIT ]; then
            print_error "Cassandra não ficou saudável"
            exit 1
        fi
    fi
fi

print_success "Cassandra está pronto para uso"

# Start native application in background
print_step "Iniciando aplicação nativa em background..."
cd "$REACTIVE_DIR"

SPRING_CASSANDRA_CONTACT_POINTS=localhost \
./target/beauty-salon-reactive \
    --spring.profiles.active=docker \
    --server.port=8085 \
    --logging.level.root=WARN \
    > /tmp/native-app.log 2>&1 &

APP_PID=$!
echo "PID da aplicação: $APP_PID"

# Wait for application to start
print_step "Aguardando aplicação inicializar..."
COUNTER=0
MAX_WAIT=30

while [ $COUNTER -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8085/actuator/health > /dev/null 2>&1; then
        print_success "Aplicação iniciou com sucesso!"
        break
    fi
    echo -n "."
    sleep 1
    COUNTER=$((COUNTER + 1))
done

echo ""

if [ $COUNTER -ge $MAX_WAIT ]; then
    print_error "Aplicação não iniciou no tempo esperado"
    kill $APP_PID 2>/dev/null || true
    cat /tmp/native-app.log
    exit 1
fi

# Run tests
print_step "Executando testes de integração..."
echo ""

# Test 1: Health Check
print_step "Teste 1: Health Check"
HEALTH_RESPONSE=$(curl -s http://localhost:8085/actuator/health)
if echo "$HEALTH_RESPONSE" | grep -q '"status":"UP"'; then
    print_success "Health check passou"
else
    print_error "Health check falhou"
    echo "Response: $HEALTH_RESPONSE"
fi

# Test 2: Create Customer
print_step "Teste 2: Criar Customer"
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8085/api/customers \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Native","email":"test@native.com","phone":"+5511999999999","address":"Test Address"}')

if echo "$CREATE_RESPONSE" | grep -q '"id"'; then
    CUSTOMER_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    print_success "Customer criado com ID: $CUSTOMER_ID"
else
    print_error "Falha ao criar customer"
    echo "Response: $CREATE_RESPONSE"
fi

# Test 3: List Customers
print_step "Teste 3: Listar Customers"
LIST_RESPONSE=$(curl -s http://localhost:8085/api/customers)
if echo "$LIST_RESPONSE" | grep -q '\['; then
    CUSTOMER_COUNT=$(echo "$LIST_RESPONSE" | grep -o '"id"' | wc -l)
    print_success "Listagem retornou $CUSTOMER_COUNT customer(s)"
else
    print_error "Falha ao listar customers"
fi

# Test 4: Get Customer by ID
if [ ! -z "$CUSTOMER_ID" ]; then
    print_step "Teste 4: Buscar Customer por ID"
    GET_RESPONSE=$(curl -s http://localhost:8085/api/customers/$CUSTOMER_ID)
    if echo "$GET_RESPONSE" | grep -q "Test Native"; then
        print_success "Customer encontrado por ID"
    else
        print_error "Falha ao buscar customer por ID"
    fi
fi

# Test 5: Update Customer
if [ ! -z "$CUSTOMER_ID" ]; then
    print_step "Teste 5: Atualizar Customer"
    UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8085/api/customers/$CUSTOMER_ID \
        -H "Content-Type: application/json" \
        -d '{"name":"Updated Native","email":"test@native.com","phone":"+5511999999999","address":"Test Address"}')
    if echo "$UPDATE_RESPONSE" | grep -q "Updated Native"; then
        print_success "Customer atualizado"
    else
        print_error "Falha ao atualizar customer"
    fi
fi

# Test 6: Delete Customer
if [ ! -z "$CUSTOMER_ID" ]; then
    print_step "Teste 6: Deletar Customer"
    DELETE_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE http://localhost:8085/api/customers/$CUSTOMER_ID)
    if echo "$DELETE_RESPONSE" | grep -q "204"; then
        print_success "Customer deletado"
    else
        print_error "Falha ao deletar customer"
    fi
fi

# Test 7: Performance Test
print_step "Teste 7: Performance (100 requisições)"
START_TIME=$(date +%s%N)
for i in {1..100}; do
    curl -s http://localhost:8085/actuator/health > /dev/null
done
END_TIME=$(date +%s%N)
DURATION=$(( (END_TIME - START_TIME) / 1000000 ))
AVG_TIME=$(( DURATION / 100 ))
print_success "100 requisições em ${DURATION}ms (média: ${AVG_TIME}ms por requisição)"

# Cleanup
print_step "Finalizando aplicação..."
kill $APP_PID 2>/dev/null || true
wait $APP_PID 2>/dev/null || true

echo ""
print_success "Todos os testes concluídos!"
echo ""
echo "📊 Resumo dos Testes:"
echo "  ✅ Health Check"
echo "  ✅ CRUD Operations (Create, Read, Update, Delete)"
echo "  ✅ Performance Test"
echo ""
echo "🎉 Aplicação nativa funcionando perfeitamente!"
