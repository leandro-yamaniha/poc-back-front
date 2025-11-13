#!/bin/bash

echo "🚀 Individual Backend Stress Test with Detailed Metrics"
echo "========================================================"
echo "Started: $(date)"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
RESULTS_DIR="$BACKEND_DIR/stress-test-results/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$RESULTS_DIR"

# Test configuration
DURATION="30s"
CONNECTIONS=100
THREADS=10
WARMUP_TIME=5

# Backends to test (format: name:port)
BACKENDS=(
    "dotnet:5001"
    "java-jvm:8080"
    "java-native:8081"
    "java-reactive-jvm:8085"
    "java-reactive-native:8086"
    "go:8082"
    "nodejs:3000"
    "python:8000"
)

# Test endpoints
ENDPOINTS=(
    "/api/customers"
    "/api/services"
    "/api/staff"
)

# Function to check if backend is running
check_backend() {
    local port=$1
    curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/health" 2>/dev/null || echo "000"
}

# Function to get container stats
get_container_stats() {
    local container_name=$1
    docker stats --no-stream --format "{{.CPUPerc}},{{.MemUsage}}" "$container_name" 2>/dev/null
}

# Function to run wrk test
run_wrk_test() {
    local backend=$1
    local port=$2
    local endpoint=$3
    local output_file=$4
    
    echo "  Testing: $endpoint"
    
    wrk -t${THREADS} -c${CONNECTIONS} -d${DURATION} \
        --latency \
        -s "$SCRIPT_DIR/wrk-report.lua" \
        "http://localhost:${port}${endpoint}" > "$output_file" 2>&1
}

# Create Lua script for wrk detailed reporting
cat > "$SCRIPT_DIR/wrk-report.lua" << 'EOF'
done = function(summary, latency, requests)
    io.write("------------------------------\n")
    io.write("WRK DETAILED RESULTS\n")
    io.write("------------------------------\n")
    io.write(string.format("Duration: %d ms\n", summary.duration / 1000))
    io.write(string.format("Requests: %d\n", summary.requests))
    io.write(string.format("Bytes: %d\n", summary.bytes))
    io.write(string.format("Errors - connect: %d, read: %d, write: %d, timeout: %d\n",
        summary.errors.connect, summary.errors.read, summary.errors.write, summary.errors.timeout))
    io.write("------------------------------\n")
    io.write("LATENCY DISTRIBUTION\n")
    io.write("------------------------------\n")
    io.write(string.format("Min: %.2f ms\n", latency.min / 1000))
    io.write(string.format("Max: %.2f ms\n", latency.max / 1000))
    io.write(string.format("Mean: %.2f ms\n", latency.mean / 1000))
    io.write(string.format("Stdev: %.2f ms\n", latency.stdev / 1000))
    io.write(string.format("P50: %.2f ms\n", latency:percentile(50) / 1000))
    io.write(string.format("P75: %.2f ms\n", latency:percentile(75) / 1000))
    io.write(string.format("P90: %.2f ms\n", latency:percentile(90) / 1000))
    io.write(string.format("P95: %.2f ms\n", latency:percentile(95) / 1000))
    io.write(string.format("P99: %.2f ms\n", latency:percentile(99) / 1000))
    io.write(string.format("P99.9: %.2f ms\n", latency:percentile(99.9) / 1000))
    io.write(string.format("P99.99: %.2f ms\n", latency:percentile(99.99) / 1000))
    io.write("------------------------------\n")
end
EOF

# Function to parse wrk output
parse_wrk_output() {
    local file=$1
    local backend=$2
    local endpoint=$3
    
    # Extract metrics
    local requests=$(grep "Requests:" "$file" | awk '{print $2}')
    local duration=$(grep "Duration:" "$file" | awk '{print $2}')
    local bytes=$(grep "Bytes:" "$file" | awk '{print $2}')
    local errors=$(grep "Errors -" "$file" | awk '{print $4 + $6 + $8 + $10}')
    local p50=$(grep "P50:" "$file" | awk '{print $2}')
    local p90=$(grep "P90:" "$file" | awk '{print $2}')
    local p99=$(grep "P99:" "$file" | awk '{print $2}')
    local mean=$(grep "Mean:" "$file" | awk '{print $2}')
    local throughput=$(grep "Requests/sec:" "$file" | awk '{print $2}')
    
    # Calculate success rate
    local success_rate="N/A"
    if [ -n "$requests" ] && [ -n "$errors" ] && [ "$requests" != "0" ]; then
        success_rate=$(awk "BEGIN {printf \"%.2f\", (($requests - $errors) / $requests) * 100}")
    fi
    
    echo "$backend,$endpoint,$requests,$duration,$throughput,$p50,$p90,$p99,$mean,$errors,$success_rate"
}

# Function to get memory and CPU stats
get_resource_stats() {
    local backend=$1
    local container_name="beauty-salon-${backend}"
    
    # Get stats 3 times and average
    local cpu_sum=0
    local mem_sum=0
    local count=0
    
    for i in {1..3}; do
        local stats=$(get_container_stats "$container_name")
        if [ -n "$stats" ]; then
            local cpu=$(echo "$stats" | cut -d',' -f1 | sed 's/%//')
            local mem=$(echo "$stats" | cut -d',' -f2 | awk '{print $1}' | sed 's/MiB//')
            
            if [ -n "$cpu" ] && [ -n "$mem" ]; then
                cpu_sum=$(awk "BEGIN {print $cpu_sum + $cpu}")
                mem_sum=$(awk "BEGIN {print $mem_sum + $mem}")
                count=$((count + 1))
            fi
        fi
        sleep 1
    done
    
    if [ $count -gt 0 ]; then
        local avg_cpu=$(awk "BEGIN {printf \"%.2f\", $cpu_sum / $count}")
        local avg_mem=$(awk "BEGIN {printf \"%.2f\", $mem_sum / $count}")
        echo "$avg_cpu,$avg_mem"
    else
        echo "N/A,N/A"
    fi
}

# Main test execution
echo "📋 Test Configuration:"
echo "   Duration: $DURATION"
echo "   Connections: $CONNECTIONS"
echo "   Threads: $THREADS"
echo "   Warmup: ${WARMUP_TIME}s"
echo ""

# Create CSV header
echo "Backend,Endpoint,Requests,Duration(ms),Throughput(req/s),P50(ms),P90(ms),P99(ms),Mean(ms),Errors,Success(%),CPU(%),Memory(MiB)" > "$RESULTS_DIR/results.csv"

# Test each backend
for backend_config in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$backend_config"
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}🔨 Testing: $backend (port $port)${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # Check if backend is running
    status=$(check_backend "$port")
    if [ "$status" != "200" ] && [ "$status" != "404" ]; then
        echo -e "${RED}❌ Backend not running (status: $status)${NC}"
        echo "$backend,ALL,0,0,0,0,0,0,0,0,0,N/A,N/A" >> "$RESULTS_DIR/results.csv"
        continue
    fi
    
    echo -e "${GREEN}✅ Backend is running${NC}"
    
    # Warmup
    echo "🔥 Warming up ($WARMUP_TIME seconds)..."
    for endpoint in "${ENDPOINTS[@]}"; do
        curl -s "http://localhost:${port}${endpoint}" > /dev/null 2>&1 &
    done
    sleep $WARMUP_TIME
    
    # Test each endpoint
    for endpoint in "${ENDPOINTS[@]}"; do
        output_file="$RESULTS_DIR/${backend}_${endpoint//\//_}.txt"
        
        # Run test
        run_wrk_test "$backend" "$port" "$endpoint" "$output_file"
        
        # Get resource stats during test
        resource_stats=$(get_resource_stats "$backend")
        cpu=$(echo "$resource_stats" | cut -d',' -f1)
        mem=$(echo "$resource_stats" | cut -d',' -f2)
        
        # Parse results
        result=$(parse_wrk_output "$output_file" "$backend" "$endpoint")
        
        # Add resource stats
        echo "$result,$cpu,$mem" >> "$RESULTS_DIR/results.csv"
        
        # Display summary
        echo -e "  ${GREEN}✓${NC} Completed: $endpoint"
    done
    
    echo -e "${GREEN}✅ $backend testing completed${NC}"
done

# Generate summary report
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 Generating Summary Report..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Create summary report
cat > "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'
# Backend Stress Test - Summary Report

**Test Date:** $(date)
**Duration:** 30s per endpoint
**Connections:** 100
**Threads:** 10

## Test Configuration

- **Tool:** wrk (HTTP benchmarking tool)
- **Endpoints Tested:**
  - `/api/customers`
  - `/api/services`
  - `/api/staff`

## Results Summary

### Performance Metrics by Backend

EOFMD

# Process CSV and create markdown table
{
    echo ""
    echo "| Backend | Endpoint | Throughput | P50 | P90 | P99 | Success % | CPU % | Memory (MiB) |"
    echo "|---------|----------|------------|-----|-----|-----|-----------|-------|--------------|"
    
    tail -n +2 "$RESULTS_DIR/results.csv" | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 mean errors success cpu mem; do
        # Format endpoint name
        endpoint_name=$(echo "$endpoint" | sed 's/\/api\///')
        
        # Format numbers
        throughput_fmt=$(printf "%.0f req/s" "$throughput" 2>/dev/null || echo "N/A")
        p50_fmt=$(printf "%.2f ms" "$p50" 2>/dev/null || echo "N/A")
        p90_fmt=$(printf "%.2f ms" "$p90" 2>/dev/null || echo "N/A")
        p99_fmt=$(printf "%.2f ms" "$p99" 2>/dev/null || echo "N/A")
        success_fmt=$(printf "%.2f%%" "$success" 2>/dev/null || echo "N/A")
        cpu_fmt=$(printf "%.2f%%" "$cpu" 2>/dev/null || echo "N/A")
        mem_fmt=$(printf "%.0f MiB" "$mem" 2>/dev/null || echo "N/A")
        
        echo "| $backend | $endpoint_name | $throughput_fmt | $p50_fmt | $p90_fmt | $p99_fmt | $success_fmt | $cpu_fmt | $mem_fmt |"
    done
} >> "$RESULTS_DIR/SUMMARY_REPORT.md"

# Add rankings
cat >> "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'

## Rankings

### Best Throughput (req/s)
EOFMD

tail -n +2 "$RESULTS_DIR/results.csv" | sort -t',' -k5 -rn | head -5 | while IFS=',' read -r backend endpoint requests duration throughput rest; do
    echo "- **$backend** ($endpoint): $(printf "%.0f" "$throughput" 2>/dev/null || echo "N/A") req/s" >> "$RESULTS_DIR/SUMMARY_REPORT.md"
done

cat >> "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'

### Lowest Latency P99 (ms)
EOFMD

tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$8 != "N/A" && $8 != "" {print}' | sort -t',' -k8 -n | head -5 | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 rest; do
    echo "- **$backend** ($endpoint): $(printf "%.2f" "$p99" 2>/dev/null || echo "N/A") ms" >> "$RESULTS_DIR/SUMMARY_REPORT.md"
done

cat >> "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'

### Lowest CPU Usage (%)
EOFMD

tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$12 != "N/A" && $12 != "" {print}' | sort -t',' -k12 -n | head -5 | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 mean errors success cpu rest; do
    echo "- **$backend** ($endpoint): $(printf "%.2f" "$cpu" 2>/dev/null || echo "N/A")%" >> "$RESULTS_DIR/SUMMARY_REPORT.md"
done

cat >> "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'

### Lowest Memory Usage (MiB)
EOFMD

tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$13 != "N/A" && $13 != "" {print}' | sort -t',' -k13 -n | head -5 | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 mean errors success cpu mem; do
    echo "- **$backend** ($endpoint): $(printf "%.0f" "$mem" 2>/dev/null || echo "N/A") MiB" >> "$RESULTS_DIR/SUMMARY_REPORT.md"
done

cat >> "$RESULTS_DIR/SUMMARY_REPORT.md" << 'EOFMD'

## Detailed Results

All detailed test results are available in the `results.csv` file.

### Files Generated

- `results.csv` - Complete results in CSV format
- `SUMMARY_REPORT.md` - This summary report
- `*_api_*.txt` - Individual wrk output files per backend/endpoint

## Recommendations

Based on the test results:

1. **For High Throughput:** Choose the backend with highest req/s
2. **For Low Latency:** Choose the backend with lowest P99 latency
3. **For Resource Efficiency:** Choose the backend with lowest CPU/Memory usage
4. **For Production:** Consider the balance of all metrics

EOFMD

# Display summary
echo ""
echo -e "${GREEN}✅ Testing completed!${NC}"
echo ""
echo "📁 Results saved to: $RESULTS_DIR"
echo ""
echo "📊 Files generated:"
echo "   - results.csv (raw data)"
echo "   - SUMMARY_REPORT.md (summary)"
echo "   - Individual test outputs"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📈 Quick Summary:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Display top performers
echo ""
echo "🏆 Top 3 by Throughput:"
tail -n +2 "$RESULTS_DIR/results.csv" | sort -t',' -k5 -rn | head -3 | while IFS=',' read -r backend endpoint requests duration throughput rest; do
    printf "   %s (%s): %.0f req/s\n" "$backend" "$endpoint" "$throughput" 2>/dev/null || echo "   $backend: N/A"
done

echo ""
echo "⚡ Top 3 by Lowest P99 Latency:"
tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$8 != "N/A" && $8 != "" {print}' | sort -t',' -k8 -n | head -3 | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 rest; do
    printf "   %s (%s): %.2f ms\n" "$backend" "$endpoint" "$p99" 2>/dev/null || echo "   $backend: N/A"
done

echo ""
echo "💚 Top 3 by Resource Efficiency (CPU):"
tail -n +2 "$RESULTS_DIR/results.csv" | awk -F',' '$12 != "N/A" && $12 != "" {print}' | sort -t',' -k12 -n | head -3 | while IFS=',' read -r backend endpoint requests duration throughput p50 p90 p99 mean errors success cpu rest; do
    printf "   %s (%s): %.2f%%\n" "$backend" "$endpoint" "$cpu" 2>/dev/null || echo "   $backend: N/A"
done

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Finished: $(date)"
echo ""
echo "To view the full report:"
echo "  cat $RESULTS_DIR/SUMMARY_REPORT.md"
echo ""
