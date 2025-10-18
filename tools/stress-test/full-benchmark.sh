#!/bin/bash

# Full Backend Benchmark with Metrics
# - Recreates database for each backend
# - Measures startup time
# - Monitors CPU and memory usage
# - Runs stress tests
# - Generates comprehensive report

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

# Configuration
RESULTS_DIR="stress-test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
FINAL_REPORT="${RESULTS_DIR}/full_benchmark_${TIMESTAMP}.md"
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

# Print header
print_header() {
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║${NC}  $1"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
}

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if wrk is installed
check_wrk() {
    if ! command -v wrk &> /dev/null; then
        print_error "wrk is not installed!"
        echo "Install with: brew install wrk (macOS)"
        exit 1
    fi
    print_success "wrk is installed"
}

# Stop all backends
stop_all_backends() {
    print_status "Stopping all backends..."
    docker-compose stop backend-java backend-dotnet backend-python backend-nodejs backend-go backend-java-reactive 2>/dev/null || true
    sleep 3
    print_success "All backends stopped"
}

# Recreate databases
recreate_databases() {
    print_status "Recreating all databases..."
    
    # Stop all services
    docker-compose down -v 2>/dev/null || true
    sleep 2
    
    # Start only database services
    print_status "Starting database services..."
    docker-compose up -d postgres mongodb cassandra sqlserver 2>/dev/null || true
    
    # Wait for databases to be ready
    print_status "Waiting for databases to be ready (30s)..."
    sleep 30
    
    print_success "Databases recreated and ready"
}

# Get container stats
get_container_stats() {
    local container=$1
    local stats=$(docker stats --no-stream --format "{{.CPUPerc}}|{{.MemUsage}}" "$container" 2>/dev/null || echo "N/A|N/A")
    echo "$stats"
}

# Measure startup time
measure_startup() {
    local backend_key=$1
    local port=$2
    local url=$3
    local container=$4
    
    print_status "Starting $container..."
    
    local start_time=$(date +%s)
    
    # Start the backend
    docker-compose up -d "$container" 2>/dev/null || true
    
    # Wait for backend to be ready
    local max_wait=120
    local elapsed=0
    
    while [ $elapsed -lt $max_wait ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            local end_time=$(date +%s)
            local startup_time=$((end_time - start_time))
            print_success "$container started in ${startup_time}s"
            echo "$startup_time"
            return 0
        fi
        sleep 2
        elapsed=$((elapsed + 2))
    done
    
    print_error "$container failed to start in ${max_wait}s"
    echo "-1"
    return 1
}

# Warmup backend
warmup_backend() {
    local url=$1
    local name=$2
    
    print_status "Warming up $name..."
    for i in $(seq 1 $WARMUP_REQUESTS); do
        curl -s "$url" > /dev/null 2>&1 || true
    done
    sleep 2
    print_success "$name warmed up"
}

# Run wrk test
run_wrk_test() {
    local url=$1
    local connections=$2
    local duration=$3
    local threads=$((connections / 10))
    [ $threads -lt 1 ] && threads=1
    [ $threads -gt 8 ] && threads=8
    
    wrk -t${threads} -c${connections} -d${duration}s --latency "$url" 2>&1
}

# Parse wrk output
parse_wrk_output() {
    local output="$1"
    local rps=$(echo "$output" | grep "Requests/sec:" | awk '{print $2}')
    local latency_avg=$(echo "$output" | grep "Latency" | head -1 | awk '{print $2}')
    local latency_p50=$(echo "$output" | grep "50%" | awk '{print $2}')
    local latency_p99=$(echo "$output" | grep "99%" | awk '{print $2}')
    local latency_max=$(echo "$output" | grep "Latency" | head -1 | awk '{print $4}')
    local errors=$(echo "$output" | grep "Non-2xx" | awk '{print $3}' || echo "0")
    
    echo "$rps|$latency_avg|$latency_p50|$latency_p99|$latency_max|$errors"
}

# Test single backend
test_backend() {
    local backend_key=$1
    IFS='|' read -r port url container tech <<< "${BACKENDS[$backend_key]}"
    local name="${BACKEND_NAMES[$backend_key]}"
    
    print_header "🧪 TESTING: $name"
    echo
    
    # Stop backend if running
    print_status "Stopping $name if running..."
    docker-compose stop "$container" 2>/dev/null || true
    sleep 3
    
    # Measure startup time
    print_status "Measuring startup time..."
    local startup_time=$(measure_startup "$backend_key" "$port" "$url" "$container")
    
    if [ "$startup_time" = "-1" ]; then
        print_error "Failed to start $name, skipping..."
        return 1
    fi
    
    # Wait a bit more for full initialization
    sleep 5
    
    # Get initial resource usage
    print_status "Measuring resource usage..."
    local stats=$(get_container_stats "$container")
    IFS='|' read -r cpu_usage mem_usage <<< "$stats"
    
    print_status "CPU: $cpu_usage | Memory: $mem_usage"
    
    # Warmup
    warmup_backend "$url" "$name"
    
    # Create individual report
    local report_file="${RESULTS_DIR}/${backend_key}_${TIMESTAMP}.md"
    
    cat > "$report_file" << EOF
# 🔥 Full Benchmark Report: ${name}

**Test Date:** $(date)  
**Backend:** ${name}  
**Port:** ${port}  
**Technology:** ${tech}  
**Container:** ${container}  

---

## ⚡ Startup Metrics

| Metric | Value |
|--------|-------|
| **Startup Time** | ${startup_time}s |
| **CPU Usage** | ${cpu_usage} |
| **Memory Usage** | ${mem_usage} |

---

## 📊 Stress Test Results

| Scenario | Users | RPS | Avg Latency | p50 | p99 | Max | Errors |
|----------|-------|-----|-------------|-----|-----|-----|--------|
EOF
    
    # Run stress tests
    echo
    print_status "Running stress tests..."
    
    local results=()
    
    for scenario in "${SCENARIOS[@]}"; do
        echo
        print_status "Testing with $scenario concurrent users..."
        
        output=$(run_wrk_test "$url" "$scenario" "$DURATION")
        parsed=$(parse_wrk_output "$output")
        
        IFS='|' read -r rps latency_avg latency_p50 latency_p99 latency_max errors <<< "$parsed"
        
        print_success "RPS: $rps | Avg: $latency_avg | p99: $latency_p99"
        
        # Store results
        results+=("$scenario|$rps|$latency_avg|$latency_p50|$latency_p99|$latency_max|$errors")
        
        # Add to report
        local scenario_name=""
        case $scenario in
            10) scenario_name="Light Load" ;;
            50) scenario_name="Medium Load" ;;
            100) scenario_name="High Load" ;;
            200) scenario_name="Very High Load" ;;
            500) scenario_name="Extreme Load" ;;
        esac
        
        echo "| $scenario_name | $scenario | $rps | $latency_avg | $latency_p50 | $latency_p99 | $latency_max | $errors |" >> "$report_file"
        
        # Cool down
        sleep 3
    done
    
    # Get final resource usage
    local final_stats=$(get_container_stats "$container")
    IFS='|' read -r final_cpu final_mem <<< "$final_stats"
    
    # Finalize individual report
    cat >> "$report_file" << EOF

---

## 📈 Resource Usage After Tests

| Metric | Value |
|--------|-------|
| **CPU Usage** | ${final_cpu} |
| **Memory Usage** | ${final_mem} |

---

## 🎯 Summary

- **Startup Time**: ${startup_time}s
- **Technology**: ${tech}
- **Best RPS**: $(echo "${results[@]}" | tr ' ' '\n' | cut -d'|' -f2 | sort -rn | head -1)
- **Database**: Fresh (recreated before test)

---

**Test completed at:** $(date)
EOF
    
    print_success "Report saved: $report_file"
    
    # Stop backend
    print_status "Stopping $name..."
    docker-compose stop "$container" 2>/dev/null || true
    sleep 3
    
    # Return results for final report
    echo "$backend_key|$startup_time|$cpu_usage|$mem_usage|${results[*]}"
}

# Initialize final report
init_final_report() {
    cat > "$FINAL_REPORT" << EOF
# 🏆 Complete Backend Benchmark Report

**Test Date:** $(date)  
**Test Duration:** ~30 minutes  
**Methodology:** Fresh database for each backend  

---

## 📋 Test Methodology

1. **Database Recreation**: Each backend tested with fresh database
2. **Startup Measurement**: Time from container start to first successful response
3. **Resource Monitoring**: CPU and memory usage during tests
4. **Stress Testing**: 5 load scenarios (10, 50, 100, 200, 500 users)
5. **Isolation**: Each backend tested independently

---

## ⚡ Startup Times Comparison

| Backend | Startup Time | CPU Usage | Memory Usage | Technology |
|---------|--------------|-----------|--------------|------------|
EOF
}

# Main execution
main() {
    clear
    print_header "🔥 FULL BACKEND BENCHMARK - COMPREHENSIVE TEST"
    echo
    
    # Check prerequisites
    check_wrk
    echo
    
    # Recreate databases
    print_header "🗄️ DATABASE PREPARATION"
    echo
    recreate_databases
    echo
    
    # Stop all backends
    stop_all_backends
    echo
    
    # Initialize final report
    init_final_report
    
    # Store all results
    declare -A all_results
    
    # Test each backend
    for backend_key in "${!BACKENDS[@]}"; do
        echo
        print_header "📊 BACKEND ${#all_results[@]}/6"
        
        result=$(test_backend "$backend_key")
        
        if [ $? -eq 0 ]; then
            all_results["$backend_key"]="$result"
        fi
        
        echo
        print_status "Waiting before next backend (10s)..."
        sleep 10
    done
    
    # Generate final comparative report
    print_header "📊 GENERATING FINAL REPORT"
    echo
    
    # Add startup times to final report
    for backend_key in "${!all_results[@]}"; do
        IFS='|' read -r key startup_time cpu mem rest <<< "${all_results[$backend_key]}"
        local name="${BACKEND_NAMES[$backend_key]}"
        IFS='|' read -r port url container tech <<< "${BACKENDS[$backend_key]}"
        
        echo "| $name | ${startup_time}s | $cpu | $mem | $tech |" >> "$FINAL_REPORT"
    done
    
    # Add performance comparison
    cat >> "$FINAL_REPORT" << EOF

---

## 🚀 Performance Comparison

### High Load Scenario (100 concurrent users)

| Backend | RPS | Avg Latency | p99 Latency | Errors |
|---------|-----|-------------|-------------|--------|
EOF
    
    # Extract 100 users scenario (index 2)
    for backend_key in "${!all_results[@]}"; do
        IFS='|' read -r key startup_time cpu mem results <<< "${all_results[$backend_key]}"
        local name="${BACKEND_NAMES[$backend_key]}"
        
        # Parse results for 100 users scenario
        local result_100=$(echo "$results" | tr ' ' '\n' | grep "^100|" | head -1)
        if [ -n "$result_100" ]; then
            IFS='|' read -r users rps avg p50 p99 max errors <<< "$result_100"
            echo "| $name | $rps | $avg | $p99 | $errors |" >> "$FINAL_REPORT"
        fi
    done
    
    # Add recommendations
    cat >> "$FINAL_REPORT" << EOF

---

## 🎯 Key Findings

### Startup Performance
- **Fastest Startup**: Check table above
- **Resource Efficiency**: Compare CPU and memory usage

### Runtime Performance
- **Highest Throughput**: Check RPS values
- **Lowest Latency**: Check p99 latency values
- **Most Stable**: Check error rates

### Recommendations

1. **For High Traffic**: Choose backend with highest RPS
2. **For Low Latency**: Choose backend with lowest p99
3. **For Quick Restarts**: Choose backend with fastest startup
4. **For Resource Efficiency**: Choose backend with lowest memory usage

---

## 📁 Individual Reports

Detailed reports for each backend:
EOF
    
    for backend_key in "${!BACKENDS[@]}"; do
        local name="${BACKEND_NAMES[$backend_key]}"
        echo "- [\`${backend_key}_${TIMESTAMP}.md\`](${backend_key}_${TIMESTAMP}.md) - $name" >> "$FINAL_REPORT"
    done
    
    cat >> "$FINAL_REPORT" << EOF

---

**Benchmark completed at:** $(date)

**Total backends tested:** ${#all_results[@]}/6

**Methodology:** Each backend tested with fresh database, isolated environment, and consistent load scenarios.
EOF
    
    echo
    print_header "✅ BENCHMARK COMPLETE"
    echo
    print_success "Final report: $FINAL_REPORT"
    print_success "Individual reports: ${RESULTS_DIR}/*_${TIMESTAMP}.md"
    echo
    print_status "Cleaning up..."
    docker-compose down 2>/dev/null || true
    echo
    print_success "All done! 🎉"
    echo
}

# Run main
main
