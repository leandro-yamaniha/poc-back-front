#!/bin/bash

# Interactive Backend Stress Test
# Choose which backend to test individually

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

# Backend configurations
declare -A BACKENDS=(
    [1]="java-spring|Java Spring Boot|8080|http://localhost:8080/api/customers"
    [2]="dotnet|.NET Core|8081|http://localhost:8081/api/customers"
    [3]="python|Python FastAPI|8082|http://localhost:8082/api/customers"
    [4]="nodejs|Node.js Express|8083|http://localhost:8083/api/customers"
    [5]="go|Go Gin|8084|http://localhost:8084/api/v1/customers"
    [6]="java-reactive|Java Reactive|8085|http://localhost:8085/api/customers"
)

RESULTS_DIR="stress-test-results"
SCENARIOS=(10 50 100 200 500)
DURATION=30

mkdir -p "$RESULTS_DIR"

# Display menu
show_menu() {
    clear
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║${NC}          🔥 BACKEND STRESS TEST - INDIVIDUAL                  ${CYAN}║${NC}"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo
    echo -e "${YELLOW}Select a backend to test:${NC}"
    echo
    echo -e "  ${GREEN}1${NC}) Java Spring Boot (Port 8080) - Traditional MVC"
    echo -e "  ${GREEN}2${NC}) .NET Core (Port 8081) - ASP.NET Core 8.0"
    echo -e "  ${GREEN}3${NC}) Python FastAPI (Port 8082) - Async Python"
    echo -e "  ${GREEN}4${NC}) Node.js Express (Port 8083) - JavaScript"
    echo -e "  ${GREEN}5${NC}) Go Gin (Port 8084) - Compiled Performance"
    echo -e "  ${GREEN}6${NC}) Java Reactive (Port 8085) - WebFlux 🏆"
    echo
    echo -e "  ${MAGENTA}7${NC}) Test ALL backends (comprehensive)"
    echo -e "  ${RED}0${NC}) Exit"
    echo
    echo -ne "${YELLOW}Enter your choice [0-7]: ${NC}"
}

# Check if backend is running
check_backend() {
    local url=$1
    curl -s -f "$url" > /dev/null 2>&1
}

# Run test for specific backend
test_backend() {
    local backend_key=$1
    local backend_name=$2
    local backend_port=$3
    local backend_url=$4
    
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local report_file="${RESULTS_DIR}/${backend_key}_${timestamp}.md"
    
    echo
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║${NC}  Testing: ${backend_name} (Port ${backend_port})"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo
    
    # Check if wrk is installed
    if ! command -v wrk &> /dev/null; then
        echo -e "${RED}✗ wrk is not installed!${NC}"
        echo "Install with: brew install wrk (macOS)"
        return 1
    fi
    
    # Check if backend is running
    echo -e "${BLUE}[INFO]${NC} Checking if ${backend_name} is running..."
    if check_backend "$backend_url"; then
        echo -e "${GREEN}✓ ${backend_name} is running${NC}"
    else
        echo -e "${RED}✗ ${backend_name} is NOT running!${NC}"
        echo "Start with: docker-compose up -d"
        return 1
    fi
    
    # Warmup
    echo
    echo -e "${BLUE}[INFO]${NC} Warming up ${backend_name}..."
    for i in {1..50}; do
        curl -s "$backend_url" > /dev/null 2>&1 || true
    done
    sleep 2
    echo -e "${GREEN}✓ Warmup complete${NC}"
    
    # Initialize report
    cat > "$report_file" << EOF
# 🔥 Stress Test Report: ${backend_name}

**Test Date:** $(date)  
**Backend:** ${backend_name}  
**Port:** ${backend_port}  
**URL:** ${backend_url}  
**Tool:** wrk  

---

## 📊 Test Results

| Scenario | Users | Duration | RPS | Avg Latency | p50 | p99 | Max | Errors |
|----------|-------|----------|-----|-------------|-----|-----|-----|--------|
EOF
    
    # Run tests
    echo
    echo -e "${CYAN}Running stress tests...${NC}"
    echo
    
    for scenario in "${SCENARIOS[@]}"; do
        echo -e "${YELLOW}→ Testing with ${scenario} concurrent users...${NC}"
        
        threads=$((scenario / 10))
        [ $threads -lt 1 ] && threads=1
        [ $threads -gt 8 ] && threads=8
        
        output=$(wrk -t${threads} -c${scenario} -d${DURATION}s --latency "$backend_url" 2>&1)
        
        # Parse results
        rps=$(echo "$output" | grep "Requests/sec:" | awk '{print $2}')
        latency_avg=$(echo "$output" | grep "Latency" | head -1 | awk '{print $2}')
        latency_p50=$(echo "$output" | grep "50%" | awk '{print $2}')
        latency_p99=$(echo "$output" | grep "99%" | awk '{print $2}')
        latency_max=$(echo "$output" | grep "Latency" | head -1 | awk '{print $4}')
        errors=$(echo "$output" | grep "Non-2xx" | awk '{print $3}' || echo "0")
        
        # Display results
        echo -e "  ${GREEN}RPS:${NC} $rps | ${GREEN}Avg:${NC} $latency_avg | ${GREEN}p99:${NC} $latency_p99 | ${GREEN}Errors:${NC} $errors"
        
        # Add to report
        scenario_name=""
        case $scenario in
            10) scenario_name="Light Load" ;;
            50) scenario_name="Medium Load" ;;
            100) scenario_name="High Load" ;;
            200) scenario_name="Very High Load" ;;
            500) scenario_name="Extreme Load" ;;
        esac
        
        echo "| $scenario_name | $scenario | ${DURATION}s | $rps | $latency_avg | $latency_p50 | $latency_p99 | $latency_max | $errors |" >> "$report_file"
        
        # Cool down
        sleep 3
    done
    
    # Finalize report
    cat >> "$report_file" << EOF

---

## 📈 Analysis

### Performance Summary
Backend: ${backend_name}  
Port: ${backend_port}  

### Test Scenarios Completed
- Light Load (10 users)
- Medium Load (50 users)
- High Load (100 users)
- Very High Load (200 users)
- Extreme Load (500 users)

---

**Test completed at:** $(date)
EOF
    
    echo
    echo -e "${GREEN}✓ Test complete!${NC}"
    echo -e "${BLUE}Report saved to:${NC} $report_file"
    echo
}

# Main loop
while true; do
    show_menu
    read -r choice
    
    case $choice in
        0)
            echo
            echo -e "${GREEN}Goodbye!${NC}"
            exit 0
            ;;
        1|2|3|4|5|6)
            IFS='|' read -r backend_key backend_name backend_port backend_url <<< "${BACKENDS[$choice]}"
            test_backend "$backend_key" "$backend_name" "$backend_port" "$backend_url"
            echo
            echo -ne "${YELLOW}Press Enter to continue...${NC}"
            read -r
            ;;
        7)
            echo
            echo -e "${CYAN}Running comprehensive test for ALL backends...${NC}"
            ./comprehensive-stress-test.sh
            echo
            echo -ne "${YELLOW}Press Enter to continue...${NC}"
            read -r
            ;;
        *)
            echo
            echo -e "${RED}Invalid option!${NC}"
            sleep 2
            ;;
    esac
done
