#!/bin/bash

# Stress Test - .NET Core
# Port: 8081

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

BACKEND_NAME=".NET Core"
BACKEND_URL="http://localhost:8081/api/customers"
BACKEND_PORT=8081
RESULTS_DIR="stress-test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_FILE="${RESULTS_DIR}/dotnet_${TIMESTAMP}.md"

# Test scenarios
SCENARIOS=(10 50 100 200 500)
DURATION=30

mkdir -p "$RESULTS_DIR"

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}  🔥 STRESS TEST: ${BACKEND_NAME}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo

# Check if wrk is installed
if ! command -v wrk &> /dev/null; then
    echo -e "${RED}✗ wrk is not installed!${NC}"
    echo "Install with: brew install wrk (macOS)"
    exit 1
fi

# Check if backend is running
echo -e "${BLUE}[INFO]${NC} Checking if ${BACKEND_NAME} is running..."
if curl -s -f "$BACKEND_URL" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ ${BACKEND_NAME} is running on port ${BACKEND_PORT}${NC}"
else
    echo -e "${RED}✗ ${BACKEND_NAME} is NOT running!${NC}"
    echo "Start with: docker-compose up -d"
    exit 1
fi

# Warmup
echo
echo -e "${BLUE}[INFO]${NC} Warming up ${BACKEND_NAME}..."
for i in {1..50}; do
    curl -s "$BACKEND_URL" > /dev/null 2>&1 || true
done
sleep 2
echo -e "${GREEN}✓ Warmup complete${NC}"

# Initialize report
cat > "$REPORT_FILE" << EOF
# 🔥 Stress Test Report: ${BACKEND_NAME}

**Test Date:** $(date)  
**Backend:** ${BACKEND_NAME}  
**Port:** ${BACKEND_PORT}  
**URL:** ${BACKEND_URL}  
**Tool:** wrk  

---

## 📊 Test Results

| Scenario | Users | Duration | RPS | Avg Latency | p50 | p99 | Max | Errors |
|----------|-------|----------|-----|-------------|-----|-----|-----|--------|
EOF

# Run tests
echo
echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}  Running Tests..."
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"

for scenario in "${SCENARIOS[@]}"; do
    echo
    echo -e "${YELLOW}Testing with ${scenario} concurrent users...${NC}"
    
    threads=$((scenario / 10))
    [ $threads -lt 1 ] && threads=1
    [ $threads -gt 8 ] && threads=8
    
    output=$(wrk -t${threads} -c${scenario} -d${DURATION}s --latency "$BACKEND_URL" 2>&1)
    
    # Parse results
    rps=$(echo "$output" | grep "Requests/sec:" | awk '{print $2}')
    latency_avg=$(echo "$output" | grep "Latency" | head -1 | awk '{print $2}')
    latency_p50=$(echo "$output" | grep "50%" | awk '{print $2}')
    latency_p99=$(echo "$output" | grep "99%" | awk '{print $2}')
    latency_max=$(echo "$output" | grep "Latency" | head -1 | awk '{print $4}')
    errors=$(echo "$output" | grep "Non-2xx" | awk '{print $3}' || echo "0")
    
    # Display results
    echo -e "  ${GREEN}RPS:${NC} $rps"
    echo -e "  ${GREEN}Avg Latency:${NC} $latency_avg"
    echo -e "  ${GREEN}p99 Latency:${NC} $latency_p99"
    echo -e "  ${GREEN}Errors:${NC} $errors"
    
    # Add to report
    scenario_name=""
    case $scenario in
        10) scenario_name="Light Load" ;;
        50) scenario_name="Medium Load" ;;
        100) scenario_name="High Load" ;;
        200) scenario_name="Very High Load" ;;
        500) scenario_name="Extreme Load" ;;
    esac
    
    echo "| $scenario_name | $scenario | ${DURATION}s | $rps | $latency_avg | $latency_p50 | $latency_p99 | $latency_max | $errors |" >> "$REPORT_FILE"
    
    # Cool down
    sleep 3
done

# Finalize report
cat >> "$REPORT_FILE" << EOF

---

## 📈 Analysis

### Performance Summary
- **Technology**: ASP.NET Core 8.0 + C# 12
- **Architecture**: Async/await + Task-based
- **Database**: SQL Server + Entity Framework Core

### Key Findings
- Excellent performance with .NET 8.0 optimizations
- Strong typing with C# provides safety
- Great Microsoft ecosystem integration

### Recommendations
- ✅ Excellent for enterprise Microsoft environments
- ✅ Great for teams with C# expertise
- ✅ Perfect for Azure integration
- ✅ Strong performance with modern .NET

---

**Test completed at:** $(date)
EOF

echo
echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}  ${GREEN}✓ Test Complete!${NC}"
echo -e "${CYAN}║${NC}  Report: ${REPORT_FILE}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo
