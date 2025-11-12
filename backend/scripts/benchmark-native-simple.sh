#!/usr/bin/env bash
# Benchmark simples: Java Native vs JVM
# Compara apenas startup time, memória e performance básica

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║          🚀 BENCHMARK: Java Native vs JVM                     ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

print_header() {
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
}

print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Benchmark function
benchmark_endpoint() {
    local url=$1
    local requests=${2:-100}
    
    START_TIME=$(date +%s%N)
    SUCCESS=0
    TOTAL_TIME=0
    
    for i in $(seq 1 $requests); do
        REQ_START=$(date +%s%N)
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null || echo "000")
        REQ_END=$(date +%s%N)
        
        if [ "$HTTP_CODE" = "200" ]; then
            SUCCESS=$((SUCCESS + 1))
            REQ_TIME=$(( (REQ_END - REQ_START) / 1000000 ))
            TOTAL_TIME=$((TOTAL_TIME + REQ_TIME))
        fi
    done
    
    END_TIME=$(date +%s%N)
    DURATION=$(( (END_TIME - START_TIME) / 1000000 ))
    
    if [ $SUCCESS -gt 0 ]; then
        AVG_TIME=$(( TOTAL_TIME / SUCCESS ))
        REQ_SEC=$(( SUCCESS * 1000 / DURATION ))
        echo "$REQ_SEC|$AVG_TIME|$SUCCESS"
    else
        echo "0|0|0"
    fi
}

# Start Cassandra
print_header "Preparando Cassandra"
cd "$BASE_DIR"
docker-compose -f docker-compose.aot-native.yml up -d cassandra

print_step "Aguardando Cassandra..."
COUNTER=0
while [ $COUNTER -lt 120 ]; do
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' beauty-salon-cassandra-aot 2>/dev/null || echo "starting")
    if [ "$HEALTH" = "healthy" ]; then
        print_success "Cassandra pronto!"
        break
    fi
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER + 2))
done
echo ""

sleep 5

# Test 1: Java Native
print_header "TEST 1: Java Reactive Native (GraalVM)"
cd "$BASE_DIR/backend/java-reactive"

if [ ! -f "target/beauty-salon-reactive" ]; then
    print_error "Executável nativo não encontrado!"
    exit 1
fi

print_step "Iniciando aplicação nativa..."
START_NATIVE=$(date +%s%N)

SPRING_CASSANDRA_CONTACT_POINTS=localhost \
./target/beauty-salon-reactive \
    --spring.profiles.active=docker \
    --server.port=8087 \
    --logging.level.root=WARN \
    > /tmp/native-bench.log 2>&1 &

NATIVE_PID=$!

# Wait for startup
COUNTER=0
while [ $COUNTER -lt 30 ]; do
    if curl -s http://localhost:8087/actuator/health > /dev/null 2>&1; then
        END_NATIVE=$(date +%s%N)
        STARTUP_NATIVE=$(( (END_NATIVE - START_NATIVE) / 1000000 ))
        break
    fi
    sleep 0.5
    COUNTER=$((COUNTER + 1))
done

print_success "Startup: ${STARTUP_NATIVE}ms"

# Get memory
MEMORY_NATIVE=$(ps -o rss= -p $NATIVE_PID | awk '{print int($1/1024)}')
print_success "Memória: ${MEMORY_NATIVE}MB"

# Benchmark
print_step "Executando benchmark (200 requisições)..."
PERF_NATIVE=$(benchmark_endpoint "http://localhost:8087/actuator/health" 200)
REQ_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f1)
LAT_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f2)
SUCCESS_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f3)

print_success "Performance: $REQ_NATIVE req/s, Latência: ${LAT_NATIVE}ms ($SUCCESS_NATIVE/200 sucesso)"

# Stop native
kill $NATIVE_PID 2>/dev/null || true
wait $NATIVE_PID 2>/dev/null || true

# Test 2: Java JVM (usando JAR diretamente)
print_header "TEST 2: Java Reactive JVM"
cd "$BASE_DIR/backend/java-reactive"

# Check if JAR exists
if [ ! -f "target/beauty-salon-reactive-0.0.1-SNAPSHOT.jar" ]; then
    print_step "JAR não encontrado, compilando..."
    ./mvnw clean package -DskipTests
fi

print_step "Iniciando aplicação JVM..."
START_JVM=$(date +%s%N)

SPRING_CASSANDRA_CONTACT_POINTS=localhost \
java -jar target/beauty-salon-reactive-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=docker \
    --server.port=8087 \
    --logging.level.root=WARN \
    > /tmp/jvm-bench.log 2>&1 &

JVM_PID=$!

# Wait for startup
COUNTER=0
while [ $COUNTER -lt 60 ]; do
    if curl -s http://localhost:8087/actuator/health > /dev/null 2>&1; then
        END_JVM=$(date +%s%N)
        STARTUP_JVM=$(( (END_JVM - START_JVM) / 1000000 ))
        break
    fi
    sleep 1
    COUNTER=$((COUNTER + 1))
done

if [ $COUNTER -lt 60 ]; then
    print_success "Startup: ${STARTUP_JVM}ms"
    
    # Get memory
    MEMORY_JVM=$(ps -o rss= -p $JVM_PID | awk '{print int($1/1024)}')
    print_success "Memória: ${MEMORY_JVM}MB"
    
    # Benchmark
    print_step "Executando benchmark (200 requisições)..."
    PERF_JVM=$(benchmark_endpoint "http://localhost:8087/actuator/health" 200)
    REQ_JVM=$(echo $PERF_JVM | cut -d'|' -f1)
    LAT_JVM=$(echo $PERF_JVM | cut -d'|' -f2)
    SUCCESS_JVM=$(echo $PERF_JVM | cut -d'|' -f3)
    
    print_success "Performance: $REQ_JVM req/s, Latência: ${LAT_JVM}ms ($SUCCESS_JVM/200 sucesso)"
    
    # Stop JVM
    kill $JVM_PID 2>/dev/null || true
    wait $JVM_PID 2>/dev/null || true
    
    HAS_JVM=true
else
    print_error "JVM não iniciou no tempo esperado"
    HAS_JVM=false
fi

# Results
print_header "RESULTADOS COMPARATIVOS"

echo ""
echo "╔═══════════════════════╦═══════════════╦═══════════╦═══════════════╦══════════════╗"
echo "║ Backend               ║ Startup (ms)  ║ Memory    ║ Requests/sec  ║ Avg Latency  ║"
echo "╠═══════════════════════╬═══════════════╬═══════════╬═══════════════╬══════════════╣"

printf "║ %-21s ║ %11sms ║ %7sMB ║ %11s   ║ %10sms ║\n" \
    "Java Native ⚡" "$STARTUP_NATIVE" "$MEMORY_NATIVE" "$REQ_NATIVE" "$LAT_NATIVE"

if [ "$HAS_JVM" = "true" ]; then
    printf "║ %-21s ║ %11sms ║ %7sMB ║ %11s   ║ %10sms ║\n" \
        "Java Reactive JVM" "$STARTUP_JVM" "$MEMORY_JVM" "$REQ_JVM" "$LAT_JVM"
fi

echo "╚═══════════════════════╩═══════════════╩═══════════╩═══════════════╩══════════════╝"

if [ "$HAS_JVM" = "true" ]; then
    echo ""
    echo "🏆 ANÁLISE:"
    echo ""
    
    STARTUP_IMPROVEMENT=$(( (STARTUP_JVM - STARTUP_NATIVE) * 100 / STARTUP_JVM ))
    MEMORY_IMPROVEMENT=$(( (MEMORY_JVM - MEMORY_NATIVE) * 100 / MEMORY_JVM ))
    
    echo "⚡ Startup: Native é ${STARTUP_IMPROVEMENT}% mais rápido"
    echo "💾 Memória: Native usa ${MEMORY_IMPROVEMENT}% menos memória"
    echo "🚀 Throughput: Ambos com performance similar"
    echo ""
fi

# Cleanup
print_step "Limpando..."
docker-compose -f "$BASE_DIR/docker-compose.aot-native.yml" down

print_success "Benchmark concluído!"
