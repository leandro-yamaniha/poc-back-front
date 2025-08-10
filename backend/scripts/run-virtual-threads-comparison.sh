#!/bin/bash

# Virtual Threads + ZGC vs Standard Configuration Performance Comparison Script
# This script runs comprehensive performance tests comparing both configurations

set -e

echo "🚀 VIRTUAL THREADS + ZGC vs STANDARD PERFORMANCE COMPARISON"
echo "=============================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
RESULTS_DIR="performance-comparison-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
STANDARD_RESULTS="${RESULTS_DIR}/standard_${TIMESTAMP}.log"
VIRTUAL_THREADS_RESULTS="${RESULTS_DIR}/virtual_threads_${TIMESTAMP}.log"
COMPARISON_REPORT="${RESULTS_DIR}/comparison_report_${TIMESTAMP}.md"

# Create results directory
mkdir -p ${RESULTS_DIR}

echo -e "${BLUE}📁 Results will be saved to: ${RESULTS_DIR}${NC}"
echo ""

# Function to run test with specific configuration
run_test() {
    local config_name=$1
    local jvm_args=$2
    local spring_profile=$3
    local output_file=$4
    
    echo -e "${YELLOW}🔍 Running ${config_name} configuration test...${NC}"
    echo "   JVM Args: ${jvm_args}"
    echo "   Spring Profile: ${spring_profile}"
    echo "   Output: ${output_file}"
    echo ""
    
    # Set environment variables
    export MAVEN_OPTS="${jvm_args}"
    export SPRING_PROFILES_ACTIVE="${spring_profile}"
    
    # Run the test and capture output
    echo "Starting ${config_name} test at $(date)" > ${output_file}
    echo "JVM Args: ${jvm_args}" >> ${output_file}
    echo "Spring Profile: ${spring_profile}" >> ${output_file}
    echo "========================================" >> ${output_file}
    echo "" >> ${output_file}
    
    if ./mvnw test -Dtest=VirtualThreadsComparisonTest -Dspring.profiles.active=${spring_profile} >> ${output_file} 2>&1; then
        echo -e "${GREEN}✅ ${config_name} test completed successfully${NC}"
    else
        echo -e "${RED}❌ ${config_name} test failed${NC}"
        echo "Check ${output_file} for details"
    fi
    
    echo "Test completed at $(date)" >> ${output_file}
    echo "" >> ${output_file}
}

# Function to extract metrics from log file
extract_metrics() {
    local log_file=$1
    local config_name=$2
    
    echo "## ${config_name} Results" >> ${COMPARISON_REPORT}
    echo "" >> ${COMPARISON_REPORT}
    
    # Extract key metrics using grep and awk
    if [ -f "${log_file}" ]; then
        echo "### Configuration Details" >> ${COMPARISON_REPORT}
        grep -E "(Active Profile|Virtual Threads|GC Type|Java Version)" ${log_file} | head -4 >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        echo "### Performance Metrics" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        # Extract overall performance summary
        echo "#### Overall Performance:" >> ${COMPARISON_REPORT}
        grep -A 5 "OVERALL PERFORMANCE SUMMARY" ${log_file} | tail -5 >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        # Extract individual test results
        echo "#### Test Results:" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        echo "**Customer Creation:**" >> ${COMPARISON_REPORT}
        grep -A 7 "Test 1 - Customer Creation:" ${log_file} | tail -7 >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        echo "**Customer Retrieval:**" >> ${COMPARISON_REPORT}
        grep -A 7 "Test 2 - Customer Retrieval:" ${log_file} | tail -7 >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        echo "**Mixed CRUD:**" >> ${COMPARISON_REPORT}
        grep -A 7 "Test 3 - Mixed CRUD:" ${log_file} | tail -7 >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        
        echo "---" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
    else
        echo "❌ Log file ${log_file} not found" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
    fi
}

# Main execution
echo -e "${BLUE}🎯 Starting Performance Comparison Tests${NC}"
echo ""

# Initialize comparison report
echo "# Virtual Threads + ZGC vs Standard Configuration Performance Comparison" > ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "**Test Date:** $(date)" >> ${COMPARISON_REPORT}
echo "**Java Version:** $(java -version 2>&1 | head -1)" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "## Test Configuration" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "- **Concurrent Users:** 200" >> ${COMPARISON_REPORT}
echo "- **Requests per User:** 20" >> ${COMPARISON_REPORT}
echo "- **Total Requests per Test:** 4,000" >> ${COMPARISON_REPORT}
echo "- **Total Tests:** 3 (Creation, Retrieval, Mixed CRUD)" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "---" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}

# Test 1: Standard Configuration (G1GC)
echo -e "${YELLOW}🔧 Test 1: Standard Configuration${NC}"
run_test "Standard (G1GC)" \
         "-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200" \
         "test" \
         "${STANDARD_RESULTS}"

echo ""
sleep 5  # Brief pause between tests

# Test 2: Virtual Threads + ZGC Configuration
echo -e "${YELLOW}🔧 Test 2: Virtual Threads + ZGC Configuration${NC}"
run_test "Virtual Threads + ZGC" \
         "-Xmx2g -Xms1g -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+UnlockDiagnosticVMOptions -XX:+LogVMOutput" \
         "virtual-threads,test" \
         "${VIRTUAL_THREADS_RESULTS}"

echo ""

# Generate comparison report
echo -e "${BLUE}📊 Generating comparison report...${NC}"

extract_metrics "${STANDARD_RESULTS}" "Standard Configuration (G1GC)"
extract_metrics "${VIRTUAL_THREADS_RESULTS}" "Virtual Threads + ZGC"

# Add summary comparison
echo "## Performance Comparison Summary" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}

# Extract key metrics for comparison
if [ -f "${STANDARD_RESULTS}" ] && [ -f "${VIRTUAL_THREADS_RESULTS}" ]; then
    echo "### Throughput Comparison" >> ${COMPARISON_REPORT}
    echo "" >> ${COMPARISON_REPORT}
    echo "| Configuration | Creation (req/s) | Retrieval (req/s) | Mixed CRUD (req/s) | Overall Avg |" >> ${COMPARISON_REPORT}
    echo "|---------------|------------------|-------------------|-------------------|-------------|" >> ${COMPARISON_REPORT}
    
    # Extract throughput values (this is a simplified extraction)
    standard_throughput=$(grep "Average Throughput:" ${STANDARD_RESULTS} | awk '{print $3}' | head -1)
    virtual_throughput=$(grep "Average Throughput:" ${VIRTUAL_THREADS_RESULTS} | awk '{print $3}' | head -1)
    
    echo "| Standard (G1GC) | - | - | - | ${standard_throughput:-N/A} |" >> ${COMPARISON_REPORT}
    echo "| Virtual Threads + ZGC | - | - | - | ${virtual_throughput:-N/A} |" >> ${COMPARISON_REPORT}
    echo "" >> ${COMPARISON_REPORT}
    
    # Performance improvement calculation
    if [ ! -z "${standard_throughput}" ] && [ ! -z "${virtual_throughput}" ]; then
        echo "### Performance Improvement" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
        improvement=$(echo "scale=2; (${virtual_throughput} - ${standard_throughput}) / ${standard_throughput} * 100" | bc -l 2>/dev/null || echo "N/A")
        echo "**Virtual Threads + ZGC vs Standard:** ${improvement}% improvement" >> ${COMPARISON_REPORT}
        echo "" >> ${COMPARISON_REPORT}
    fi
fi

# Add recommendations
echo "## Recommendations" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "Based on the performance comparison results:" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "### When to use Virtual Threads + ZGC:" >> ${COMPARISON_REPORT}
echo "- High concurrency applications (1000+ concurrent users)" >> ${COMPARISON_REPORT}
echo "- I/O intensive workloads" >> ${COMPARISON_REPORT}
echo "- Applications with many blocking operations" >> ${COMPARISON_REPORT}
echo "- Low-latency requirements" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}
echo "### When to use Standard Configuration:" >> ${COMPARISON_REPORT}
echo "- CPU-intensive workloads" >> ${COMPARISON_REPORT}
echo "- Lower concurrency applications" >> ${COMPARISON_REPORT}
echo "- Memory-constrained environments" >> ${COMPARISON_REPORT}
echo "- Production stability requirements (G1GC is more mature)" >> ${COMPARISON_REPORT}
echo "" >> ${COMPARISON_REPORT}

# Final output
echo -e "${GREEN}🎉 Performance comparison completed!${NC}"
echo ""
echo -e "${BLUE}📊 Results Summary:${NC}"
echo "   📁 Standard Results: ${STANDARD_RESULTS}"
echo "   📁 Virtual Threads Results: ${VIRTUAL_THREADS_RESULTS}"
echo "   📋 Comparison Report: ${COMPARISON_REPORT}"
echo ""
echo -e "${YELLOW}📖 View the comparison report:${NC}"
echo "   cat ${COMPARISON_REPORT}"
echo ""
echo -e "${YELLOW}🔍 View detailed logs:${NC}"
echo "   tail -f ${STANDARD_RESULTS}"
echo "   tail -f ${VIRTUAL_THREADS_RESULTS}"
echo ""

# Display quick summary if files exist
if [ -f "${COMPARISON_REPORT}" ]; then
    echo -e "${BLUE}📊 Quick Summary:${NC}"
    echo ""
    grep -A 10 "Performance Comparison Summary" ${COMPARISON_REPORT} | head -15
fi

echo -e "${GREEN}✅ Comparison script completed successfully!${NC}"
