#!/opt/homebrew/bin/bash

# Single Backend Benchmark with Full Metrics
# Requires bash 4.0+ for associative arrays
# Usage: ./benchmark-single.sh <backend-key>
# Example: ./benchmark-single.sh java-spring

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration
BACKEND_KEY=$1
RESULTS_DIR="stress-test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
SCENARIOS=(10 50 100 200 500)
DURATION=30
WARMUP_REQUESTS=50

# Backend configurations
declare -A BACKENDS=(
    ["java-spring"]="8080|http://localhost:8080/api/customers|backend-java|Spring MVC + Tomcat"
    ["dotnet"]="8081|http://localhost:8081/api/customers|backend-dotnet|ASP.NET Core 8.0"
    ["python"]="8082|http://localhost:8082/api/customers|backend-python|FastAPI + Uvicorn"
    ["nodejs"]="8083|http://localhost:8083/api/customers|backend-nodejs|Express.js"
    ["go"]="8084|http://localhost:8084/api/v1/customers|backend-go|Gin Framework"
    ["java-reactive"]="8085|http://localhost:8085/api/customers|backend-java-reactive|Spring WebFlux"
)

declare -A BACKEND_NAMES=(
    ["java-spring"]="Java Spring Boot"
    ["dotnet"]=".NET Core"
    ["python"]="Python FastAPI"
    ["nodejs"]="Node.js Express"
    ["go"]="Go Gin"
    ["java-reactive"]="Java Reactive"
)

mkdir -p "$RESULTS_DIR"

# Print functions
print_header() {
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║${NC}  $1"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
}

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Validate input
if [ -z "$BACKEND_KEY" ]; then
    echo -e "${RED}Error: Backend key required${NC}"
    echo
    echo "Usage: $0 <backend-key>"
    echo
    echo "Available backends:"
    echo "  - java-spring   (Java Spring Boot - Port 8080)"
    echo "  - dotnet        (.NET Core - Port 8081)"
    echo "  - python        (Python FastAPI - Port 8082)"
    echo "  - nodejs        (Node.js Express - Port 8083)"
    echo "  - go            (Go Gin - Port 8084)"
    echo "  - java-reactive (Java Reactive - Port 8085)"
    echo
    exit 1
fi

if [ -z "${BACKENDS[$BACKEND_KEY]}" ]; then
    print_error "Invalid backend key: $BACKEND_KEY"
    exit 1
fi

# Parse backend config
IFS='|' read -r PORT URL CONTAINER TECH <<< "${BACKENDS[$BACKEND_KEY]}"
NAME="${BACKEND_NAMES[$BACKEND_KEY]}"

print_header "🔥 BENCHMARK: $NAME"
echo

# Check wrk
if ! command -v wrk &> /dev/null; then
    print_error "wrk is not installed!"
    echo "Install with: brew install wrk"
    exit 1
fi

# Stop backend
print_status "Stopping $NAME if running..."
docker-compose stop "$CONTAINER" 2>/dev/null || true
sleep 3

# Recreate database
print_status "Recreating database..."
docker-compose down -v 2>/dev/null || true
sleep 2
docker-compose up -d postgres mongodb cassandra sqlserver 2>/dev/null || true
print_status "Waiting for databases (30s)..."
sleep 30
print_success "Database ready"

# Measure startup
print_status "Starting $NAME and measuring startup time..."
START_TIME=$(date +%s)
docker-compose up -d "$CONTAINER" 2>/dev/null || true

MAX_WAIT=120
ELAPSED=0

while [ $ELAPSED -lt $MAX_WAIT ]; do
    if curl -s -f "$URL" > /dev/null 2>&1; then
        END_TIME=$(date +%s)
        STARTUP_TIME=$((END_TIME - START_TIME))
        print_success "$NAME started in ${STARTUP_TIME}s"
        break
    fi
    sleep 2
    ELAPSED=$((ELAPSED + 2))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
    print_error "Failed to start in ${MAX_WAIT}s"
    exit 1
fi

# Wait for full initialization
sleep 5

# Get resource usage
print_status "Measuring resource usage..."
STATS=$(docker stats --no-stream --format "{{.CPUPerc}}|{{.MemUsage}}" "$CONTAINER" 2>/dev/null || echo "N/A|N/A")
IFS='|' read -r CPU_USAGE MEM_USAGE <<< "$STATS"
print_status "CPU: $CPU_USAGE | Memory: $MEM_USAGE"

# Warmup
print_status "Warming up..."
for i in $(seq 1 $WARMUP_REQUESTS); do
    curl -s "$URL" > /dev/null 2>&1 || true
done
sleep 2
print_success "Warmup complete"

# Create report
REPORT_FILE="${RESULTS_DIR}/${BACKEND_KEY}_benchmark_${TIMESTAMP}.md"

cat > "$REPORT_FILE" << EOF
# 🔥 Benchmark Report: ${NAME}

**Test Date:** $(date)  
**Backend:** ${NAME}  
**Port:** ${PORT}  
**Technology:** ${TECH}  
**Container:** ${CONTAINER}  
**Database:** Fresh (recreated)  

---

## ⚡ Startup & Resource Metrics

| Metric | Value |
|--------|-------|
| **Startup Time** | ${STARTUP_TIME}s |
| **CPU Usage (Initial)** | ${CPU_USAGE} |
| **Memory Usage (Initial)** | ${MEM_USAGE} |

---

## 📊 Stress Test Results

| Scenario | Users | RPS | Avg Latency | p50 | p99 | Max | Errors |
|----------|-------|-----|-------------|-----|-----|-----|--------|
EOF

# Run stress tests
echo
print_header "🧪 RUNNING STRESS TESTS"

for SCENARIO in "${SCENARIOS[@]}"; do
    echo
    print_status "Testing with $SCENARIO concurrent users..."
    
    THREADS=$((SCENARIO / 10))
    [ $THREADS -lt 1 ] && THREADS=1
    [ $THREADS -gt 8 ] && THREADS=8
    
    OUTPUT=$(wrk -t${THREADS} -c${SCENARIO} -d${DURATION}s --latency "$URL" 2>&1)
    
    RPS=$(echo "$OUTPUT" | grep "Requests/sec:" | awk '{print $2}')
    LAT_AVG=$(echo "$OUTPUT" | grep "Latency" | head -1 | awk '{print $2}')
    LAT_P50=$(echo "$OUTPUT" | grep "50%" | awk '{print $2}')
    LAT_P99=$(echo "$OUTPUT" | grep "99%" | awk '{print $2}')
    LAT_MAX=$(echo "$OUTPUT" | grep "Latency" | head -1 | awk '{print $4}')
    ERRORS=$(echo "$OUTPUT" | grep "Non-2xx" | awk '{print $3}' || echo "0")
    
    print_success "RPS: $RPS | Avg: $LAT_AVG | p99: $LAT_P99 | Errors: $ERRORS"
    
    SCENARIO_NAME=""
    case $SCENARIO in
        10) SCENARIO_NAME="Light Load" ;;
        50) SCENARIO_NAME="Medium Load" ;;
        100) SCENARIO_NAME="High Load" ;;
        200) SCENARIO_NAME="Very High Load" ;;
        500) SCENARIO_NAME="Extreme Load" ;;
    esac
    
    echo "| $SCENARIO_NAME | $SCENARIO | $RPS | $LAT_AVG | $LAT_P50 | $LAT_P99 | $LAT_MAX | $ERRORS |" >> "$REPORT_FILE"
    
    sleep 3
done

# Final resource usage
FINAL_STATS=$(docker stats --no-stream --format "{{.CPUPerc}}|{{.MemUsage}}" "$CONTAINER" 2>/dev/null || echo "N/A|N/A")
IFS='|' read -r FINAL_CPU FINAL_MEM <<< "$FINAL_STATS"

# Finalize report
cat >> "$REPORT_FILE" << EOF

---

## 📈 Resource Usage After Tests

| Metric | Value |
|--------|-------|
| **CPU Usage (Final)** | ${FINAL_CPU} |
| **Memory Usage (Final)** | ${FINAL_MEM} |

---

## 🎯 Summary

- **Startup Time**: ${STARTUP_TIME}s
- **Technology**: ${TECH}
- **Database**: Fresh (recreated before test)
- **Test Duration**: ~3 minutes
- **Scenarios**: 5 load levels (10-500 users)

---

**Test completed at:** $(date)
EOF

echo
print_header "✅ BENCHMARK COMPLETE"
echo
print_success "Report saved: $REPORT_FILE"
echo
print_status "Stopping backend..."
docker-compose stop "$CONTAINER" 2>/dev/null || true
echo
print_success "Done! 🎉"
echo
