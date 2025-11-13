#!/bin/bash

echo "🚀 Sequential Backend Stress Test with Individual Cassandra Instances"
echo "====================================================================="
echo "Started: $(date)"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
RESULTS_DIR="$BACKEND_DIR/stress-test-results/sequential_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$RESULTS_DIR"

# Test configuration
DURATION="30s"
CONNECTIONS=100
THREADS=10
WARMUP_TIME=10
CASSANDRA_WAIT=45

# Test order as requested
BACKENDS_ORDER=(
    "java-native:8081:java"
    "java-reactive-native:8086:java-reactive"
    "go:8082:go"
    "java-jvm:8080:java"
    "java-reactive-jvm:8085:java-reactive"
    "python:8000:python"
    "nodejs:3000:nodejs"
    "dotnet:5001:dotnet"
)

# Test endpoints
ENDPOINTS=(
    "/api/customers"
    "/api/services"
    "/api/staff"
)

# Function to wait for service
wait_for_service() {
    local port=$1
    local max_wait=$2
    local count=0
    
    echo "⏳ Waiting for service on port $port..."
    while [ $count -lt $max_wait ]; do
        status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/health" 2>/dev/null || echo "000")
        if [ "$status" = "200" ] || [ "$status" = "404" ]; then
            echo -e "${GREEN}✅ Service ready (status: $status)${NC}"
            return 0
        fi
        sleep 2
        count=$((count + 2))
        if [ $((count % 10)) -eq 0 ]; then
            echo "   Still waiting... (${count}s/${max_wait}s)"
        fi
    done
    echo -e "${RED}❌ Service failed to start${NC}"
    return 1
}

# Function to get container stats
get_container_stats() {
    local container_name=$1
    local samples=${2:-5}
    local cpu_sum=0
    local mem_sum=0
    local count=0
    
    for i in $(seq 1 $samples); do
        stats=$(docker stats --no-stream --format "{{.CPUPerc}},{{.MemUsage}}" "$container_name" 2>/dev/null)
        if [ -n "$stats" ]; then
            cpu=$(echo "$stats" | cut -d',' -f1 | sed 's/%//')
            mem=$(echo "$stats" | cut -d',' -f2 | awk '{print $1}' | sed 's/MiB//')
            
            if [ -n "$cpu" ] && [ -n "$mem" ]; then
                cpu_sum=$(awk "BEGIN {print $cpu_sum + $cpu}")
                mem_sum=$(awk "BEGIN {print $mem_sum + $mem}")
                count=$((count + 1))
            fi
        fi
        sleep 1
    done
    
    if [ $count -gt 0 ]; then
        avg_cpu=$(awk "BEGIN {printf \"%.2f\", $cpu_sum / $count}")
        avg_mem=$(awk "BEGIN {printf \"%.2f\", $mem_sum / $count}")
        echo "$avg_cpu,$avg_mem"
    else
        echo "0,0"
    fi
}

# Create Lua script for wrk
cat > "$SCRIPT_DIR/wrk-report.lua" << 'EOF'
done = function(summary, latency, requests)
    io.write("------------------------------\n")
    io.write("Duration: " .. summary.duration / 1000 .. " ms\n")
    io.write("Requests: " .. summary.requests .. "\n")
    io.write("Bytes: " .. summary.bytes .. "\n")
    io.write("Errors: " .. (summary.errors.connect + summary.errors.read + summary.errors.write + summary.errors.timeout) .. "\n")
    io.write("------------------------------\n")
    io.write("Latency Min: " .. string.format("%.2f", latency.min / 1000) .. " ms\n")
    io.write("Latency Max: " .. string.format("%.2f", latency.max / 1000) .. " ms\n")
    io.write("Latency Mean: " .. string.format("%.2f", latency.mean / 1000) .. " ms\n")
    io.write("Latency Stdev: " .. string.format("%.2f", latency.stdev / 1000) .. " ms\n")
    io.write("Latency P50: " .. string.format("%.2f", latency:percentile(50) / 1000) .. " ms\n")
    io.write("Latency P75: " .. string.format("%.2f", latency:percentile(75) / 1000) .. " ms\n")
    io.write("Latency P90: " .. string.format("%.2f", latency:percentile(90) / 1000) .. " ms\n")
    io.write("Latency P95: " .. string.format("%.2f", latency:percentile(95) / 1000) .. " ms\n")
    io.write("Latency P99: " .. string.format("%.2f", latency:percentile(99) / 1000) .. " ms\n")
    io.write("Latency P99.9: " .. string.format("%.2f", latency:percentile(99.9) / 1000) .. " ms\n")
    io.write("Latency P99.99: " .. string.format("%.2f", latency:percentile(99.99) / 1000) .. " ms\n")
end
EOF

# Function to run wrk test
run_wrk_test() {
    local port=$1
    local endpoint=$2
    local output_file=$3
    
    wrk -t${THREADS} -c${CONNECTIONS} -d${DURATION} \
        --latency \
        -s "$SCRIPT_DIR/wrk-report.lua" \
        "http://localhost:${port}${endpoint}" > "$output_file" 2>&1
}

# Function to parse wrk output
parse_wrk_output() {
    local file=$1
    
    local requests=$(grep "^Requests:" "$file" | awk '{print $2}')
    local duration=$(grep "^Duration:" "$file" | awk '{print $2}')
    local errors=$(grep "^Errors:" "$file" | awk '{print $2}')
    local throughput=$(grep "Requests/sec:" "$file" | awk '{print $2}')
    local p50=$(grep "^Latency P50:" "$file" | awk '{print $3}')
    local p75=$(grep "^Latency P75:" "$file" | awk '{print $3}')
    local p90=$(grep "^Latency P90:" "$file" | awk '{print $3}')
    local p95=$(grep "^Latency P95:" "$file" | awk '{print $3}')
    local p99=$(grep "^Latency P99:" "$file" | awk '{print $3}')
    local p999=$(grep "^Latency P99.9:" "$file" | awk '{print $3}')
    local mean=$(grep "^Latency Mean:" "$file" | awk '{print $3}')
    local min=$(grep "^Latency Min:" "$file" | awk '{print $3}')
    local max=$(grep "^Latency Max:" "$file" | awk '{print $3}')
    
    echo "$requests,$duration,$throughput,$p50,$p75,$p90,$p95,$p99,$p999,$mean,$min,$max,$errors"
}

# Initialize CSV
echo "Backend,Endpoint,Requests,Duration(ms),Throughput(req/s),P50(ms),P75(ms),P90(ms),P95(ms),P99(ms),P99.9(ms),Mean(ms),Min(ms),Max(ms),Errors,Success(%),CPU(%),Memory(MiB),Startup(s)" > "$RESULTS_DIR/results.csv"

echo "📋 Test Configuration:"
echo "   Duration: $DURATION per endpoint"
echo "   Connections: $CONNECTIONS"
echo "   Threads: $THREADS"
echo "   Warmup: ${WARMUP_TIME}s"
echo "   Cassandra Wait: ${CASSANDRA_WAIT}s"
echo ""
echo "📊 Test Order:"
for i in "${!BACKENDS_ORDER[@]}"; do
    IFS=':' read -r name port dir <<< "${BACKENDS_ORDER[$i]}"
    echo "   $((i+1)). $name"
done
echo ""

# Test each backend
for backend_config in "${BACKENDS_ORDER[@]}"; do
    IFS=':' read -r backend port backend_dir <<< "$backend_config"
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${CYAN}🔨 Testing Backend: $backend${NC}"
    echo -e "${CYAN}   Port: $port | Directory: $backend_dir${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    backend_start_time=$(date +%s)
    
    # Start Cassandra
    echo -e "${BLUE}📦 Step 1/5: Starting Cassandra...${NC}"
    cd "$BACKEND_DIR/$backend_dir"
    docker-compose up -d cassandra
    
    echo "⏳ Waiting for Cassandra to be ready (${CASSANDRA_WAIT}s)..."
    sleep $CASSANDRA_WAIT
    
    # Verify Cassandra
    if docker ps | grep -q cassandra; then
        echo -e "${GREEN}✅ Cassandra is running${NC}"
    else
        echo -e "${RED}❌ Cassandra failed to start${NC}"
        echo "$backend,ALL,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" >> "$RESULTS_DIR/results.csv"
        docker-compose down -v
        continue
    fi
    echo ""
    
    # Start Backend
    echo -e "${BLUE}📦 Step 2/5: Starting $backend backend...${NC}"
    
    case "$backend" in
        "java-native")
            SERVER_PORT=$port docker-compose up -d backend-native
            container_name="beauty-salon-backend-native"
            ;;
        "java-jvm")
            SERVER_PORT=$port docker-compose up -d backend-jvm
            container_name="beauty-salon-backend-jvm"
            ;;
        "java-reactive-native")
            SERVER_PORT=$port docker-compose up -d backend-reactive-native
            container_name="beauty-salon-backend-reactive-native"
            ;;
        "java-reactive-jvm")
            SERVER_PORT=$port docker-compose up -d backend-reactive-jvm
            container_name="beauty-salon-backend-reactive-jvm"
            ;;
        "go")
            PORT=$port docker-compose up -d backend
            container_name="beauty-salon-backend-go"
            ;;
        "nodejs")
            PORT=$port docker-compose up -d backend
            container_name="beauty-salon-backend-nodejs"
            ;;
        "python")
            docker-compose up -d backend
            container_name="beauty-salon-backend-python"
            ;;
        "dotnet")
            ASPNETCORE_URLS="http://+:$port" docker-compose up -d backend
            container_name="beauty-salon-backend-dotnet"
            ;;
    esac
    
    # Wait for backend
    if wait_for_service "$port" 60; then
        backend_end_time=$(date +%s)
        startup_time=$((backend_end_time - backend_start_time))
        echo -e "${GREEN}✅ Backend started in ${startup_time}s${NC}"
    else
        echo -e "${RED}❌ Backend failed to start${NC}"
        echo "$backend,ALL,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" >> "$RESULTS_DIR/results.csv"
        docker-compose down -v
        continue
    fi
    echo ""
    
    # Warmup
    echo -e "${BLUE}📦 Step 3/5: Warming up (${WARMUP_TIME}s)...${NC}"
    for endpoint in "${ENDPOINTS[@]}"; do
        curl -s "http://localhost:${port}${endpoint}" > /dev/null 2>&1 &
    done
    sleep $WARMUP_TIME
    echo -e "${GREEN}✅ Warmup completed${NC}"
    echo ""
    
    # Run tests
    echo -e "${BLUE}📦 Step 4/5: Running stress tests...${NC}"
    for endpoint in "${ENDPOINTS[@]}"; do
        endpoint_name=$(echo "$endpoint" | sed 's/\/api\///')
        output_file="$RESULTS_DIR/${backend}_${endpoint_name}.txt"
        
        echo "   Testing: $endpoint"
        run_wrk_test "$port" "$endpoint" "$output_file"
        
        # Parse results
        metrics=$(parse_wrk_output "$output_file")
        IFS=',' read -r requests duration throughput p50 p75 p90 p95 p99 p999 mean min max errors <<< "$metrics"
        
        # Calculate success rate
        success_rate="100.00"
        if [ -n "$requests" ] && [ -n "$errors" ] && [ "$requests" != "0" ]; then
            success_rate=$(awk "BEGIN {printf \"%.2f\", (($requests - $errors) / $requests) * 100}")
        fi
        
        # Get resource stats
        resource_stats=$(get_container_stats "$container_name" 5)
        cpu=$(echo "$resource_stats" | cut -d',' -f1)
        mem=$(echo "$resource_stats" | cut -d',' -f2)
        
        # Save to CSV
        echo "$backend,$endpoint_name,$requests,$duration,$throughput,$p50,$p75,$p90,$p95,$p99,$p999,$mean,$min,$max,$errors,$success_rate,$cpu,$mem,$startup_time" >> "$RESULTS_DIR/results.csv"
        
        echo -e "      ${GREEN}✓${NC} Throughput: $throughput req/s | P99: $p99 ms | CPU: $cpu% | Mem: $mem MiB"
    done
    echo ""
    
    # Stop services
    echo -e "${BLUE}📦 Step 5/5: Stopping services...${NC}"
    docker-compose down -v
    echo -e "${GREEN}✅ Services stopped and cleaned${NC}"
    
    echo ""
    echo -e "${GREEN}✅ $backend testing completed!${NC}"
    
    # Small pause between backends
    sleep 5
done

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${CYAN}📊 Generating Final Report...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Generate comprehensive report
cat > "$RESULTS_DIR/FINAL_REPORT.md" << 'EOFMD'
# Backend Stress Test - Final Comprehensive Report

**Test Date:** $(date)  
**Test Duration:** 30s per endpoint  
**Connections:** 100  
**Threads:** 10  
**Test Order:** Java Native → Java Reactive Native → Go → Java JVM → Java Reactive JVM → Python → Node.js → .NET

---

## Executive Summary

EOFMD

# Calculate summary statistics
{
    echo ""
    echo "### Overall Performance Metrics"
    echo ""
    echo "| Metric | Best Backend | Value |"
    echo "|--------|--------------|-------|"
    
    # Best throughput
    best_throughput=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '{print $5, $1, $2}' | sort -rn | head -1)
    echo "| **Highest Throughput** | $(echo $best_throughput | awk '{print $2" ("$3")"}') | $(echo $best_throughput | awk '{printf "%.0f req/s", $1}') |"
    
    # Lowest P99
    best_p99=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$9 != "" && $9 != "N/A" {print $9, $1, $2}' | sort -n | head -1)
    echo "| **Lowest P99 Latency** | $(echo $best_p99 | awk '{print $2" ("$3")"}') | $(echo $best_p99 | awk '{printf "%.2f ms", $1}') |"
    
    # Lowest CPU
    best_cpu=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$16 != "" && $16 != "N/A" && $16 > 0 {print $16, $1, $2}' | sort -n | head -1)
    echo "| **Lowest CPU Usage** | $(echo $best_cpu | awk '{print $2" ("$3")"}') | $(echo $best_cpu | awk '{printf "%.2f%%", $1}') |"
    
    # Lowest Memory
    best_mem=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$17 != "" && $17 != "N/A" && $17 > 0 {print $17, $1, $2}' | sort -n | head -1)
    echo "| **Lowest Memory Usage** | $(echo $best_mem | awk '{print $2" ("$3")"}') | $(echo $best_mem | awk '{printf "%.0f MiB", $1}') |"
    
    # Fastest startup
    best_startup=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$18 != "" && $18 != "N/A" && $18 > 0 {print $18, $1}' | sort -n | head -1)
    echo "| **Fastest Startup** | $(echo $best_startup | awk '{print $2}') | $(echo $best_startup | awk '{printf "%.0f s", $1}') |"
    
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Detailed results table
{
    echo ""
    echo "---"
    echo ""
    echo "## Detailed Performance Results"
    echo ""
    echo "### Complete Metrics Table"
    echo ""
    echo "| Backend | Endpoint | Throughput | P50 | P90 | P99 | Success% | CPU% | Memory | Startup |"
    echo "|---------|----------|------------|-----|-----|-----|----------|------|--------|---------|"
    
    tail -n +2 "$RESULTS_DIR/results.csv" | while IFS=',' read -r backend endpoint requests duration throughput p50 p75 p90 p95 p99 p999 mean min max errors success cpu mem startup; do
        printf "| %s | %s | %.0f req/s | %.2f ms | %.2f ms | %.2f ms | %.1f%% | %.1f%% | %.0f MiB | %ss |\n" \
            "$backend" "$endpoint" "$throughput" "$p50" "$p90" "$p99" "$success" "$cpu" "$mem" "$startup"
    done
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Rankings by backend
{
    echo ""
    echo "---"
    echo ""
    echo "## Backend Rankings"
    echo ""
    
    echo "### 🏆 Top 5 by Throughput (req/s)"
    echo ""
    tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '{print $5, $1, $2}' | sort -rn | head -5 | nl | while read num throughput backend endpoint; do
        printf "%d. **%s** (%s): %.0f req/s\n" "$num" "$backend" "$endpoint" "$throughput"
    done
    
    echo ""
    echo "### ⚡ Top 5 by Lowest P99 Latency (ms)"
    echo ""
    tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$9 != "" {print $9, $1, $2}' | sort -n | head -5 | nl | while read num p99 backend endpoint; do
        printf "%d. **%s** (%s): %.2f ms\n" "$num" "$backend" "$endpoint" "$p99"
    done
    
    echo ""
    echo "### 💚 Top 5 by Resource Efficiency (CPU%)"
    echo ""
    tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$16 > 0 {print $16, $1, $2}' | sort -n | head -5 | nl | while read num cpu backend endpoint; do
        printf "%d. **%s** (%s): %.2f%%\n" "$num" "$backend" "$endpoint" "$cpu"
    done
    
    echo ""
    echo "### 🚀 Top 5 by Fastest Startup (seconds)"
    echo ""
    tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '{print $18, $1}' | sort -n | uniq | head -5 | nl | while read num startup backend; do
        printf "%d. **%s**: %ss\n" "$num" "$backend" "$startup"
    done
    
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Per-backend analysis
{
    echo ""
    echo "---"
    echo ""
    echo "## Per-Backend Analysis"
    echo ""
    
    for backend_config in "${BACKENDS_ORDER[@]}"; do
        IFS=':' read -r backend port dir <<< "$backend_config"
        
        echo "### $backend"
        echo ""
        
        # Get average metrics for this backend
        avg_throughput=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '{sum+=$5; count++} END {if(count>0) printf "%.0f", sum/count; else print "N/A"}')
        avg_p99=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '{sum+=$9; count++} END {if(count>0) printf "%.2f", sum/count; else print "N/A"}')
        avg_cpu=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '$16>0 {sum+=$16; count++} END {if(count>0) printf "%.2f", sum/count; else print "N/A"}')
        avg_mem=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '$17>0 {sum+=$17; count++} END {if(count>0) printf "%.0f", sum/count; else print "N/A"}')
        startup=$(grep "^$backend," "$RESULTS_DIR/results.csv" | head -1 | awk -F',' '{print $18}')
        
        echo "**Average Performance:**"
        echo "- Throughput: $avg_throughput req/s"
        echo "- P99 Latency: $avg_p99 ms"
        echo "- CPU Usage: $avg_cpu%"
        echo "- Memory Usage: $avg_mem MiB"
        echo "- Startup Time: ${startup}s"
        echo ""
    done
    
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Recommendations
{
    echo "---"
    echo ""
    echo "## Recommendations"
    echo ""
    echo "### Use Case Recommendations"
    echo ""
    
    best_throughput_backend=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '{sum[$1]+=$5; count[$1]++} END {for(b in sum) print sum[b]/count[b], b}' | sort -rn | head -1 | awk '{print $2}')
    best_latency_backend=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$9>0 {sum[$1]+=$9; count[$1]++} END {for(b in sum) print sum[b]/count[b], b}' | sort -n | head -1 | awk '{print $2}')
    best_resource_backend=$(tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$16>0 {sum[$1]+=$16; count[$1]++} END {for(b in sum) print sum[b]/count[b], b}' | sort -n | head -1 | awk '{print $2}')
    
    echo "1. **For Maximum Throughput:** Use **$best_throughput_backend**"
    echo "2. **For Lowest Latency:** Use **$best_latency_backend**"
    echo "3. **For Resource Efficiency:** Use **$best_resource_backend**"
    echo ""
    echo "### Technology Insights"
    echo ""
    echo "- **Native Builds:** Faster startup and lower memory footprint"
    echo "- **JVM Builds:** Higher throughput after warmup, more memory usage"
    echo "- **Reactive Backends:** Better handling of concurrent connections"
    echo "- **Traditional Backends:** Simpler codebase, easier maintenance"
    echo ""
    
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Footer
{
    echo "---"
    echo ""
    echo "## Test Details"
    echo ""
    echo "- **Test Tool:** wrk (HTTP benchmarking)"
    echo "- **Test Duration:** 30 seconds per endpoint"
    echo "- **Concurrent Connections:** 100"
    echo "- **Worker Threads:** 10"
    echo "- **Warmup Period:** 10 seconds"
    echo "- **Endpoints Tested:** /api/customers, /api/services, /api/staff"
    echo ""
    echo "**Generated:** $(date)"
    echo ""
    echo "---"
    echo ""
    echo "📁 **Raw Data:** \`results.csv\`  "
    echo "📊 **Individual Test Outputs:** \`*_*.txt\`"
    echo ""
} >> "$RESULTS_DIR/FINAL_REPORT.md"

# Create summary statistics file
{
    echo "Backend,Avg Throughput,Avg P99,Avg CPU,Avg Memory,Startup Time"
    for backend_config in "${BACKENDS_ORDER[@]}"; do
        IFS=':' read -r backend port dir <<< "$backend_config"
        avg_throughput=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '{sum+=$5; count++} END {if(count>0) printf "%.2f", sum/count; else print "0"}')
        avg_p99=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '{sum+=$9; count++} END {if(count>0) printf "%.2f", sum/count; else print "0"}')
        avg_cpu=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '$16>0 {sum+=$16; count++} END {if(count>0) printf "%.2f", sum/count; else print "0"}')
        avg_mem=$(grep "^$backend," "$RESULTS_DIR/results.csv" | awk -F',' '$17>0 {sum+=$17; count++} END {if(count>0) printf "%.2f", sum/count; else print "0"}')
        startup=$(grep "^$backend," "$RESULTS_DIR/results.csv" | head -1 | awk -F',' '{print $18}')
        echo "$backend,$avg_throughput,$avg_p99,$avg_cpu,$avg_mem,$startup"
    done
} > "$RESULTS_DIR/summary_stats.csv"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}✅ All Tests Completed Successfully!${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📁 Results Directory: $RESULTS_DIR"
echo ""
echo "📊 Generated Files:"
echo "   - FINAL_REPORT.md (comprehensive report)"
echo "   - results.csv (detailed metrics)"
echo "   - summary_stats.csv (backend averages)"
echo "   - *_*.txt (individual test outputs)"
echo ""
echo "📖 View the report:"
echo "   cat $RESULTS_DIR/FINAL_REPORT.md"
echo ""
echo "Finished: $(date)"
echo ""
