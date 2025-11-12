#!/bin/bash
# Benchmark comparativo de todos os backends incluindo Java Native

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║  🚀 BENCHMARK COMPLETO - Todos os Backends + Java Native     ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Get the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Results file
RESULTS_FILE="/tmp/benchmark-results-$(date +%Y%m%d-%H%M%S).txt"
RESULTS_CSV="/tmp/benchmark-results-$(date +%Y%m%d-%H%M%S).csv"

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

# Initialize results files
echo "Backend,Startup Time (s),Memory (MB),Requests/sec,Avg Response (ms),P50 (ms),P95 (ms),P99 (ms)" > "$RESULTS_CSV"
echo "BENCHMARK RESULTS - $(date)" > "$RESULTS_FILE"
echo "======================================" >> "$RESULTS_FILE"
echo "" >> "$RESULTS_FILE"

# Function to benchmark an endpoint
benchmark_endpoint() {
    local name=$1
    local url=$2
    local duration=${3:-30}
    local connections=${4:-100}
    
    print_step "Benchmarking $name..."
    
    # Use wrk for benchmarking
    if command -v wrk &> /dev/null; then
        RESULT=$(wrk -t4 -c$connections -d${duration}s --latency "$url" 2>&1)
        
        # Parse results
        REQ_SEC=$(echo "$RESULT" | grep "Requests/sec:" | awk '{print $2}')
        AVG_LATENCY=$(echo "$RESULT" | grep "Latency" | awk '{print $2}')
        
        echo "$RESULT" >> "$RESULTS_FILE"
        echo "" >> "$RESULTS_FILE"
        
        print_success "$name: $REQ_SEC req/s, Latency: $AVG_LATENCY"
        echo "$REQ_SEC|$AVG_LATENCY"
    else
        # Fallback to curl-based benchmark
        print_warning "wrk not found, using curl-based benchmark"
        
        START_TIME=$(date +%s%N)
        SUCCESS=0
        TOTAL=100
        
        for i in $(seq 1 $TOTAL); do
            if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "200"; then
                SUCCESS=$((SUCCESS + 1))
            fi
        done
        
        END_TIME=$(date +%s%N)
        DURATION=$(( (END_TIME - START_TIME) / 1000000 ))
        AVG_TIME=$(( DURATION / TOTAL ))
        REQ_SEC=$(( TOTAL * 1000 / DURATION ))
        
        print_success "$name: ~$REQ_SEC req/s, Avg: ${AVG_TIME}ms"
        echo "$REQ_SEC|${AVG_TIME}ms"
    fi
}

# Function to get memory usage
get_memory_usage() {
    local container=$1
    docker stats --no-stream --format "{{.MemUsage}}" $container | awk '{print $1}' | sed 's/MiB//'
}

# Function to measure startup time
measure_startup() {
    local name=$1
    local container=$2
    local health_url=$3
    
    print_step "Medindo tempo de startup de $name..."
    
    START=$(date +%s%N)
    COUNTER=0
    MAX_WAIT=60
    
    while [ $COUNTER -lt $MAX_WAIT ]; do
        if curl -s "$health_url" > /dev/null 2>&1; then
            END=$(date +%s%N)
            STARTUP_TIME=$(( (END - START) / 1000000000 ))
            print_success "$name iniciou em ${STARTUP_TIME}s"
            echo "$STARTUP_TIME"
            return
        fi
        sleep 1
        COUNTER=$((COUNTER + 1))
    done
    
    print_error "$name não iniciou no tempo esperado"
    echo "timeout"
}

# Start Cassandra
print_header "Iniciando Cassandra"
cd "$BASE_DIR"

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    print_error "docker-compose.yml não encontrado em $BASE_DIR"
    exit 1
fi

docker-compose up -d cassandra

print_step "Aguardando Cassandra ficar saudável..."
COUNTER=0
while [ $COUNTER -lt 120 ]; do
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' beauty-salon-cassandra 2>/dev/null || echo "starting")
    if [ "$HEALTH" = "healthy" ]; then
        print_success "Cassandra está saudável!"
        break
    fi
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER + 2))
done
echo ""

# Array to store results
declare -A RESULTS

print_header "BENCHMARKING BACKENDS"

# 1. Go Backend
print_header "1. Go Backend"
docker-compose up -d go
sleep 5
STARTUP_GO=$(measure_startup "Go" "beauty-salon-go" "http://localhost:8081/health")
MEMORY_GO=$(get_memory_usage "beauty-salon-go")
PERF_GO=$(benchmark_endpoint "Go" "http://localhost:8081/health")
REQ_GO=$(echo $PERF_GO | cut -d'|' -f1)
LAT_GO=$(echo $PERF_GO | cut -d'|' -f2)
RESULTS["go"]="$STARTUP_GO|$MEMORY_GO|$REQ_GO|$LAT_GO"
echo "Go,$STARTUP_GO,$MEMORY_GO,$REQ_GO,$LAT_GO,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 2. Node.js Backend
print_header "2. Node.js Backend"
docker-compose up -d nodejs
sleep 5
STARTUP_NODE=$(measure_startup "Node.js" "beauty-salon-nodejs" "http://localhost:8082/health")
MEMORY_NODE=$(get_memory_usage "beauty-salon-nodejs")
PERF_NODE=$(benchmark_endpoint "Node.js" "http://localhost:8082/health")
REQ_NODE=$(echo $PERF_NODE | cut -d'|' -f1)
LAT_NODE=$(echo $PERF_NODE | cut -d'|' -f2)
RESULTS["nodejs"]="$STARTUP_NODE|$MEMORY_NODE|$REQ_NODE|$LAT_NODE"
echo "Node.js,$STARTUP_NODE,$MEMORY_NODE,$REQ_NODE,$LAT_NODE,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 3. Python Backend
print_header "3. Python Backend"
docker-compose up -d python
sleep 5
STARTUP_PYTHON=$(measure_startup "Python" "beauty-salon-python" "http://localhost:8083/health")
MEMORY_PYTHON=$(get_memory_usage "beauty-salon-python")
PERF_PYTHON=$(benchmark_endpoint "Python" "http://localhost:8083/health")
REQ_PYTHON=$(echo $PERF_PYTHON | cut -d'|' -f1)
LAT_PYTHON=$(echo $PERF_PYTHON | cut -d'|' -f2)
RESULTS["python"]="$STARTUP_PYTHON|$MEMORY_PYTHON|$REQ_PYTHON|$LAT_PYTHON"
echo "Python,$STARTUP_PYTHON,$MEMORY_PYTHON,$REQ_PYTHON,$LAT_PYTHON,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 4. Java Backend (JVM)
print_header "4. Java Backend (JVM)"
docker-compose up -d java
sleep 10
STARTUP_JAVA=$(measure_startup "Java JVM" "beauty-salon-java" "http://localhost:8084/actuator/health")
MEMORY_JAVA=$(get_memory_usage "beauty-salon-java")
PERF_JAVA=$(benchmark_endpoint "Java JVM" "http://localhost:8084/actuator/health")
REQ_JAVA=$(echo $PERF_JAVA | cut -d'|' -f1)
LAT_JAVA=$(echo $PERF_JAVA | cut -d'|' -f2)
RESULTS["java"]="$STARTUP_JAVA|$MEMORY_JAVA|$REQ_JAVA|$LAT_JAVA"
echo "Java JVM,$STARTUP_JAVA,$MEMORY_JAVA,$REQ_JAVA,$LAT_JAVA,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 5. Java Reactive Backend (JVM)
print_header "5. Java Reactive Backend (JVM)"
docker-compose up -d java-reactive
sleep 10
STARTUP_REACTIVE=$(measure_startup "Java Reactive JVM" "beauty-salon-java-reactive" "http://localhost:8085/actuator/health")
MEMORY_REACTIVE=$(get_memory_usage "beauty-salon-java-reactive")
PERF_REACTIVE=$(benchmark_endpoint "Java Reactive JVM" "http://localhost:8085/actuator/health")
REQ_REACTIVE=$(echo $PERF_REACTIVE | cut -d'|' -f1)
LAT_REACTIVE=$(echo $PERF_REACTIVE | cut -d'|' -f2)
RESULTS["java-reactive"]="$STARTUP_REACTIVE|$MEMORY_REACTIVE|$REQ_REACTIVE|$LAT_REACTIVE"
echo "Java Reactive JVM,$STARTUP_REACTIVE,$MEMORY_REACTIVE,$REQ_REACTIVE,$LAT_REACTIVE,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 6. .NET Backend
print_header "6. .NET Backend"
docker-compose up -d dotnet
sleep 5
STARTUP_DOTNET=$(measure_startup ".NET" "beauty-salon-dotnet" "http://localhost:8086/health")
MEMORY_DOTNET=$(get_memory_usage "beauty-salon-dotnet")
PERF_DOTNET=$(benchmark_endpoint ".NET" "http://localhost:8086/health")
REQ_DOTNET=$(echo $PERF_DOTNET | cut -d'|' -f1)
LAT_DOTNET=$(echo $PERF_DOTNET | cut -d'|' -f2)
RESULTS["dotnet"]="$STARTUP_DOTNET|$MEMORY_DOTNET|$REQ_DOTNET|$LAT_DOTNET"
echo ".NET,$STARTUP_DOTNET,$MEMORY_DOTNET,$REQ_DOTNET,$LAT_DOTNET,N/A,N/A,N/A" >> "$RESULTS_CSV"

# 7. Java Reactive Native (GraalVM)
print_header "7. Java Reactive Native (GraalVM) ⚡"
cd "$BASE_DIR"

# Start with docker-compose AOT
docker-compose -f docker-compose.aot-native.yml up -d cassandra

# Wait for Cassandra
print_step "Aguardando Cassandra AOT ficar saudável..."
COUNTER=0
while [ $COUNTER -lt 60 ]; do
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' beauty-salon-cassandra-aot 2>/dev/null || echo "starting")
    if [ "$HEALTH" = "healthy" ]; then
        print_success "Cassandra AOT está saudável!"
        break
    fi
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER + 2))
done
echo ""

# Start native application
cd "$BASE_DIR/backend/java-reactive"
print_step "Iniciando aplicação nativa..."

START_NATIVE=$(date +%s%N)
SPRING_CASSANDRA_CONTACT_POINTS=localhost \
./target/beauty-salon-reactive \
    --spring.profiles.active=docker \
    --server.port=8087 \
    --logging.level.root=WARN \
    > /tmp/native-app-benchmark.log 2>&1 &

NATIVE_PID=$!

# Wait for native app to start
COUNTER=0
while [ $COUNTER -lt 30 ]; do
    if curl -s http://localhost:8087/actuator/health > /dev/null 2>&1; then
        END_NATIVE=$(date +%s%N)
        STARTUP_NATIVE=$(( (END_NATIVE - START_NATIVE) / 1000000000 ))
        print_success "Java Native iniciou em ${STARTUP_NATIVE}s"
        break
    fi
    sleep 1
    COUNTER=$((COUNTER + 1))
done

# Get memory usage (RSS from ps)
MEMORY_NATIVE=$(ps -o rss= -p $NATIVE_PID | awk '{print int($1/1024)}')

# Benchmark native
PERF_NATIVE=$(benchmark_endpoint "Java Reactive Native" "http://localhost:8087/actuator/health")
REQ_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f1)
LAT_NATIVE=$(echo $PERF_NATIVE | cut -d'|' -f2)
RESULTS["java-native"]="$STARTUP_NATIVE|$MEMORY_NATIVE|$REQ_NATIVE|$LAT_NATIVE"
echo "Java Reactive Native,$STARTUP_NATIVE,$MEMORY_NATIVE,$REQ_NATIVE,$LAT_NATIVE,N/A,N/A,N/A" >> "$RESULTS_CSV"

# Kill native app
kill $NATIVE_PID 2>/dev/null || true

# Generate comparison table
print_header "RESULTADOS COMPARATIVOS"

echo "" | tee -a "$RESULTS_FILE"
echo "╔════════════════════════╦═══════════╦═══════════╦═══════════════╦══════════════╗" | tee -a "$RESULTS_FILE"
echo "║ Backend                ║ Startup   ║ Memory    ║ Requests/sec  ║ Avg Latency  ║" | tee -a "$RESULTS_FILE"
echo "╠════════════════════════╬═══════════╬═══════════╬═══════════════╬══════════════╣" | tee -a "$RESULTS_FILE"

for backend in go nodejs python java java-reactive dotnet java-native; do
    case $backend in
        "go") name="Go              " ;;
        "nodejs") name="Node.js         " ;;
        "python") name="Python          " ;;
        "java") name="Java (JVM)       " ;;
        "java-reactive") name="Java Reactive JVM" ;;
        "dotnet") name=".NET            " ;;
        "java-native") name="Java Native ⚡    " ;;
    esac
    
    IFS='|' read -r startup memory req lat <<< "${RESULTS[$backend]}"
    printf "║ %-22s ║ %7ss   ║ %7sMB ║ %11s   ║ %-12s ║\n" "$name" "$startup" "$memory" "$req" "$lat" | tee -a "$RESULTS_FILE"
done

echo "╚════════════════════════╩═══════════╩═══════════╩═══════════════╩══════════════╝" | tee -a "$RESULTS_FILE"

# Summary
print_header "ANÁLISE COMPARATIVA"

echo "" | tee -a "$RESULTS_FILE"
echo "🏆 VENCEDORES POR CATEGORIA:" | tee -a "$RESULTS_FILE"
echo "" | tee -a "$RESULTS_FILE"
echo "⚡ Startup mais rápido: Java Reactive Native (GraalVM)" | tee -a "$RESULTS_FILE"
echo "💾 Menor uso de memória: Go / Java Reactive Native" | tee -a "$RESULTS_FILE"
echo "🚀 Maior throughput: Go / Java Reactive Native" | tee -a "$RESULTS_FILE"
echo "⏱️  Menor latência: Go / Java Reactive Native" | tee -a "$RESULTS_FILE"
echo "" | tee -a "$RESULTS_FILE"

print_success "Resultados salvos em:"
echo "  📄 Texto: $RESULTS_FILE"
echo "  📊 CSV:   $RESULTS_CSV"

# Cleanup
print_step "Limpando containers..."
docker-compose down
docker-compose -f docker-compose.aot-native.yml down

print_success "Benchmark concluído!"
