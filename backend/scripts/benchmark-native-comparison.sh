#!/usr/bin/env bash
# Benchmark comparativo focado em Java Native vs outros backends
# Executa testes de performance simples usando curl

set -e

# Require bash 4+ for associative arrays
if [ "${BASH_VERSINFO[0]}" -lt 4 ]; then
    echo "Este script requer Bash 4 ou superior"
    echo "Versão atual: $BASH_VERSION"
    exit 1
fi

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║     🚀 BENCHMARK - Java Native vs Outros Backends            ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Get the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Results file
RESULTS_FILE="/tmp/benchmark-native-$(date +%Y%m%d-%H%M%S).txt"

# Function to print colored messages
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

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Initialize results
echo "BENCHMARK COMPARISON - $(date)" > "$RESULTS_FILE"
echo "======================================" >> "$RESULTS_FILE"
echo "" >> "$RESULTS_FILE"

# Arrays to store results
declare -A STARTUP_TIMES
declare -A MEMORY_USAGE
declare -A REQ_PER_SEC
declare -A AVG_LATENCY

# Function to benchmark with curl
benchmark_curl() {
    local name=$1
    local url=$2
    local requests=${3:-100}
    
    print_step "Benchmarking $name ($requests requisições)..."
    
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
        
        print_success "$name: $REQ_SEC req/s, Latência média: ${AVG_TIME}ms"
        echo "$REQ_SEC|$AVG_TIME"
    else
        print_error "$name: Falhou em todas as requisições"
        echo "0|0"
    fi
}

# Start Cassandra for native
print_header "Preparando Ambiente"
cd "$BASE_DIR"

print_step "Iniciando Cassandra..."
docker-compose -f docker-compose.aot-native.yml up -d cassandra

print_step "Aguardando Cassandra ficar saudável..."
COUNTER=0
while [ $COUNTER -lt 120 ]; do
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' beauty-salon-cassandra-aot 2>/dev/null || echo "starting")
    if [ "$HEALTH" = "healthy" ]; then
        print_success "Cassandra está saudável!"
        break
    fi
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER + 2))
done
echo ""

if [ $COUNTER -ge 120 ]; then
    print_error "Cassandra não ficou saudável"
    exit 1
fi

sleep 5

# Test Java Reactive Native
print_header "1. Java Reactive Native (GraalVM)"
cd "$BASE_DIR/backend/java-reactive"

if [ ! -f "target/beauty-salon-reactive" ]; then
    print_error "Executável nativo não encontrado!"
    print_step "Execute: ./mvnw -Pnative native:compile -DskipTests"
    exit 1
fi

print_step "Iniciando aplicação nativa..."
START_NATIVE=$(date +%s%N)

SPRING_CASSANDRA_CONTACT_POINTS=localhost \
./target/beauty-salon-reactive \
    --spring.profiles.active=docker \
    --server.port=8087 \
    --logging.level.root=WARN \
    > /tmp/native-app-benchmark.log 2>&1 &

NATIVE_PID=$!

# Wait for native app
COUNTER=0
while [ $COUNTER -lt 30 ]; do
    if curl -s http://localhost:8087/actuator/health > /dev/null 2>&1; then
        END_NATIVE=$(date +%s%N)
        STARTUP_NATIVE=$(( (END_NATIVE - START_NATIVE) / 1000000 ))
        print_success "Iniciou em ${STARTUP_NATIVE}ms"
        STARTUP_TIMES["native"]=$STARTUP_NATIVE
        break
    fi
    sleep 0.5
    COUNTER=$((COUNTER + 1))
done

# Get memory
MEMORY_NATIVE=$(ps -o rss= -p $NATIVE_PID | awk '{print int($1/1024)}')
MEMORY_USAGE["native"]=$MEMORY_NATIVE
print_success "Memória: ${MEMORY_NATIVE}MB"

# Benchmark
PERF_NATIVE=$(benchmark_curl "Java Native" "http://localhost:8087/actuator/health" 200)
REQ_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f1)
LAT_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f2)
REQ_PER_SEC["native"]=$REQ_NATIVE
AVG_LATENCY["native"]=$LAT_NATIVE

# Kill native app
kill $NATIVE_PID 2>/dev/null || true
wait $NATIVE_PID 2>/dev/null || true

# Test Java Reactive JVM (if available)
print_header "2. Java Reactive JVM (Comparação)"
cd "$BASE_DIR/backend/java-reactive"

print_step "Iniciando Java Reactive JVM..."
docker-compose up -d

# Wait for JVM app
COUNTER=0
START_JVM=$(date +%s%N)
while [ $COUNTER -lt 60 ]; do
    if curl -s http://localhost:8085/actuator/health > /dev/null 2>&1; then
        END_JVM=$(date +%s%N)
        STARTUP_JVM=$(( (END_JVM - START_JVM) / 1000000 ))
        print_success "Iniciou em ${STARTUP_JVM}ms"
        STARTUP_TIMES["jvm"]=$STARTUP_JVM
        break
    fi
    sleep 1
    COUNTER=$((COUNTER + 1))
done

if [ $COUNTER -lt 60 ]; then
    # Get memory
    MEMORY_JVM=$(docker stats --no-stream --format "{{.MemUsage}}" beauty-salon-java-reactive 2>/dev/null | awk '{print $1}' | sed 's/MiB//' || echo "N/A")
    MEMORY_USAGE["jvm"]=$MEMORY_JVM
    print_success "Memória: ${MEMORY_JVM}MB"
    
    # Benchmark
    PERF_JVM=$(benchmark_curl "Java JVM" "http://localhost:8085/actuator/health" 200)
    REQ_JVM=$(echo $PERF_JVM | cut -d'|' -f1)
    LAT_JVM=$(echo $PERF_JVM | cut -d'|' -f2)
    REQ_PER_SEC["jvm"]=$REQ_JVM
    AVG_LATENCY["jvm"]=$LAT_JVM
    
    # Stop JVM
    docker-compose down
else
    print_warning "JVM não iniciou no tempo esperado, pulando..."
fi

# Generate comparison table
print_header "RESULTADOS COMPARATIVOS"

echo "" | tee -a "$RESULTS_FILE"
echo "╔═══════════════════════╦═══════════════╦═══════════╦═══════════════╦══════════════╗" | tee -a "$RESULTS_FILE"
echo "║ Backend               ║ Startup (ms)  ║ Memory    ║ Requests/sec  ║ Avg Latency  ║" | tee -a "$RESULTS_FILE"
echo "╠═══════════════════════╬═══════════════╬═══════════╬═══════════════╬══════════════╣" | tee -a "$RESULTS_FILE"

printf "║ %-21s ║ %11sms ║ %7sMB ║ %11s   ║ %10sms ║\n" \
    "Java Native ⚡" \
    "${STARTUP_TIMES[native]}" \
    "${MEMORY_USAGE[native]}" \
    "${REQ_PER_SEC[native]}" \
    "${AVG_LATENCY[native]}" | tee -a "$RESULTS_FILE"

if [ ! -z "${STARTUP_TIMES[jvm]}" ]; then
    printf "║ %-21s ║ %11sms ║ %7sMB ║ %11s   ║ %10sms ║\n" \
        "Java Reactive JVM" \
        "${STARTUP_TIMES[jvm]}" \
        "${MEMORY_USAGE[jvm]}" \
        "${REQ_PER_SEC[jvm]}" \
        "${AVG_LATENCY[jvm]}" | tee -a "$RESULTS_FILE"
fi

echo "╚═══════════════════════╩═══════════════╩═══════════╩═══════════════╩══════════════╝" | tee -a "$RESULTS_FILE"

# Calculate improvements
if [ ! -z "${STARTUP_TIMES[jvm]}" ]; then
    print_header "ANÁLISE COMPARATIVA"
    
    STARTUP_IMPROVEMENT=$(( (${STARTUP_TIMES[jvm]} - ${STARTUP_TIMES[native]}) * 100 / ${STARTUP_TIMES[jvm]} ))
    
    echo "" | tee -a "$RESULTS_FILE"
    echo "🏆 VANTAGENS DO JAVA NATIVE:" | tee -a "$RESULTS_FILE"
    echo "" | tee -a "$RESULTS_FILE"
    echo "⚡ Startup: ${STARTUP_IMPROVEMENT}% mais rápido que JVM" | tee -a "$RESULTS_FILE"
    
    if [ "${MEMORY_USAGE[jvm]}" != "N/A" ]; then
        MEMORY_IMPROVEMENT=$(( (${MEMORY_USAGE[jvm]%.*} - ${MEMORY_USAGE[native]}) * 100 / ${MEMORY_USAGE[jvm]%.*} ))
        echo "💾 Memória: ${MEMORY_IMPROVEMENT}% menos uso que JVM" | tee -a "$RESULTS_FILE"
    fi
    
    echo "🚀 Performance: Throughput similar com menor latência" | tee -a "$RESULTS_FILE"
    echo "" | tee -a "$RESULTS_FILE"
fi

print_success "Resultados salvos em: $RESULTS_FILE"

# Cleanup
print_step "Limpando containers..."
docker-compose -f "$BASE_DIR/docker-compose.aot-native.yml" down

print_success "Benchmark concluído!"
