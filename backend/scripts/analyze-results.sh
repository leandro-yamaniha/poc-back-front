#!/bin/bash
# Analyze Load Test Results
# Generates comparative analysis and rankings

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() {
    echo ""
    echo -e "${CYAN}================================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}================================================${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

# Check if results directory provided
if [ -z "$1" ]; then
    echo "Usage: $0 <results-directory>"
    echo "Example: $0 ../load-test-results/20241026_174530"
    exit 1
fi

RESULTS_DIR="$1"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "Error: Directory not found: $RESULTS_DIR"
    exit 1
fi

print_header "Analyzing Load Test Results"
print_step "Results directory: $RESULTS_DIR"

# Parse results
declare -A RPS
declare -A AVG_LAT
declare -A MAX_LAT
declare -A TOTAL_REQ

backends=("java-jvm" "java-native" "java-reactive-jvm" "java-reactive-native" "go" "nodejs" "python" "dotnet")

for backend in "${backends[@]}"; do
    result_file="$RESULTS_DIR/${backend}.txt"
    
    if [ -f "$result_file" ]; then
        RPS[$backend]=$(grep "Requests/sec:" "$result_file" | awk '{print $2}' | sed 's/[^0-9.]//g')
        AVG_LAT[$backend]=$(grep "Latency" "$result_file" | head -1 | awk '{print $2}')
        MAX_LAT[$backend]=$(grep "max" "$result_file" | awk '{print $2}' | head -1)
        TOTAL_REQ[$backend]=$(grep "requests in" "$result_file" | awk '{print $1}')
    fi
done

# Generate analysis report
ANALYSIS_FILE="$RESULTS_DIR/ANALYSIS.md"

cat > "$ANALYSIS_FILE" << 'EOF'
# Load Test Analysis Report

## Executive Summary

This report provides automated analysis of load test results across all backend implementations.

## Performance Rankings

### 1. Throughput (Requests/sec) - Higher is Better

EOF

# Sort by RPS
print_step "Ranking by throughput..."
echo "| Rank | Backend | Requests/sec | Score |" >> "$ANALYSIS_FILE"
echo "|------|---------|--------------|-------|" >> "$ANALYSIS_FILE"

rank=1
for backend in $(for b in "${backends[@]}"; do
    if [ -n "${RPS[$b]}" ]; then
        echo "${RPS[$b]} $b"
    fi
done | sort -rn | awk '{print $2}'); do
    rps=${RPS[$backend]}
    if [ -n "$rps" ]; then
        # Calculate score (percentage of best)
        best_rps=$(for b in "${backends[@]}"; do echo "${RPS[$b]:-0}"; done | sort -rn | head -1)
        score=$(echo "scale=1; ($rps / $best_rps) * 100" | bc)
        
        echo "| $rank | $backend | $rps | ${score}% |" >> "$ANALYSIS_FILE"
        rank=$((rank + 1))
    fi
done

cat >> "$ANALYSIS_FILE" << 'EOF'

### 2. Latency (Average) - Lower is Better

EOF

# Sort by latency
print_step "Ranking by latency..."
echo "| Rank | Backend | Avg Latency | Score |" >> "$ANALYSIS_FILE"
echo "|------|---------|-------------|-------|" >> "$ANALYSIS_FILE"

rank=1
for backend in $(for b in "${backends[@]}"; do
    if [ -n "${AVG_LAT[$b]}" ]; then
        lat_num=$(echo "${AVG_LAT[$b]}" | sed 's/[^0-9.]//g')
        echo "$lat_num $b"
    fi
done | sort -n | awk '{print $2}'); do
    lat=${AVG_LAT[$backend]}
    if [ -n "$lat" ]; then
        # Calculate score (inverse percentage)
        lat_num=$(echo "$lat" | sed 's/[^0-9.]//g')
        best_lat=$(for b in "${backends[@]}"; do
            if [ -n "${AVG_LAT[$b]}" ]; then
                echo "${AVG_LAT[$b]}" | sed 's/[^0-9.]//g'
            fi
        done | sort -n | head -1)
        score=$(echo "scale=1; ($best_lat / $lat_num) * 100" | bc)
        
        echo "| $rank | $backend | $lat | ${score}% |" >> "$ANALYSIS_FILE"
        rank=$((rank + 1))
    fi
done

cat >> "$ANALYSIS_FILE" << 'EOF'

## Comparative Analysis

### Native vs JVM Comparison

EOF

# Compare Java Traditional
if [ -n "${RPS[java-native]}" ] && [ -n "${RPS[java-jvm]}" ]; then
    improvement=$(echo "scale=1; ((${RPS[java-native]} - ${RPS[java-jvm]}) / ${RPS[java-jvm]}) * 100" | bc)
    echo "**Java Traditional:**" >> "$ANALYSIS_FILE"
    echo "- Native: ${RPS[java-native]} req/s" >> "$ANALYSIS_FILE"
    echo "- JVM: ${RPS[java-jvm]} req/s" >> "$ANALYSIS_FILE"
    echo "- **Improvement: ${improvement}%**" >> "$ANALYSIS_FILE"
    echo "" >> "$ANALYSIS_FILE"
fi

# Compare Java Reactive
if [ -n "${RPS[java-reactive-native]}" ] && [ -n "${RPS[java-reactive-jvm]}" ]; then
    improvement=$(echo "scale=1; ((${RPS[java-reactive-native]} - ${RPS[java-reactive-jvm]}) / ${RPS[java-reactive-jvm]}) * 100" | bc)
    echo "**Java Reactive:**" >> "$ANALYSIS_FILE"
    echo "- Native: ${RPS[java-reactive-native]} req/s" >> "$ANALYSIS_FILE"
    echo "- JVM: ${RPS[java-reactive-jvm]} req/s" >> "$ANALYSIS_FILE"
    echo "- **Improvement: ${improvement}%**" >> "$ANALYSIS_FILE"
    echo "" >> "$ANALYSIS_FILE"
fi

cat >> "$ANALYSIS_FILE" << 'EOF'

### Traditional vs Reactive Comparison

EOF

# Compare JVM implementations
if [ -n "${RPS[java-jvm]}" ] && [ -n "${RPS[java-reactive-jvm]}" ]; then
    diff=$(echo "scale=1; ((${RPS[java-reactive-jvm]} - ${RPS[java-jvm]}) / ${RPS[java-jvm]}) * 100" | bc)
    echo "**JVM Mode:**" >> "$ANALYSIS_FILE"
    echo "- Traditional: ${RPS[java-jvm]} req/s" >> "$ANALYSIS_FILE"
    echo "- Reactive: ${RPS[java-reactive-jvm]} req/s" >> "$ANALYSIS_FILE"
    echo "- **Difference: ${diff}%**" >> "$ANALYSIS_FILE"
    echo "" >> "$ANALYSIS_FILE"
fi

# Compare Native implementations
if [ -n "${RPS[java-native]}" ] && [ -n "${RPS[java-reactive-native]}" ]; then
    diff=$(echo "scale=1; ((${RPS[java-reactive-native]} - ${RPS[java-native]}) / ${RPS[java-native]}) * 100" | bc)
    echo "**Native Mode:**" >> "$ANALYSIS_FILE"
    echo "- Traditional: ${RPS[java-native]} req/s" >> "$ANALYSIS_FILE"
    echo "- Reactive: ${RPS[java-reactive-native]} req/s" >> "$ANALYSIS_FILE"
    echo "- **Difference: ${diff}%**" >> "$ANALYSIS_FILE"
    echo "" >> "$ANALYSIS_FILE"
fi

cat >> "$ANALYSIS_FILE" << 'EOF'

### Language Comparison

EOF

# Top 3 performers
echo "**Top 3 Performers:**" >> "$ANALYSIS_FILE"
echo "" >> "$ANALYSIS_FILE"
rank=1
for backend in $(for b in "${backends[@]}"; do
    if [ -n "${RPS[$b]}" ]; then
        echo "${RPS[$b]} $b"
    fi
done | sort -rn | head -3 | awk '{print $2}'); do
    rps=${RPS[$backend]}
    lat=${AVG_LAT[$backend]}
    echo "$rank. **$backend**: $rps req/s, $lat latency" >> "$ANALYSIS_FILE"
    rank=$((rank + 1))
done

cat >> "$ANALYSIS_FILE" << 'EOF'

## Recommendations

### For Development
EOF

# Recommend based on build time and ease
echo "- **Best Choice:** Node.js or Java Traditional (JVM)" >> "$ANALYSIS_FILE"
echo "- **Reason:** Fast build times, easy debugging, good performance" >> "$ANALYSIS_FILE"
echo "" >> "$ANALYSIS_FILE"

cat >> "$ANALYSIS_FILE" << 'EOF'

### For Production (High Load)
EOF

# Recommend top 2 performers
top_backend=$(for b in "${backends[@]}"; do
    if [ -n "${RPS[$b]}" ]; then
        echo "${RPS[$b]} $b"
    fi
done | sort -rn | head -1 | awk '{print $2}')

echo "- **Best Choice:** $top_backend" >> "$ANALYSIS_FILE"
echo "- **Reason:** Highest throughput and best latency" >> "$ANALYSIS_FILE"
echo "" >> "$ANALYSIS_FILE"

cat >> "$ANALYSIS_FILE" << 'EOF'

### For Cost Optimization
EOF

echo "- **Best Choice:** Go or Native Java" >> "$ANALYSIS_FILE"
echo "- **Reason:** Lower memory footprint, faster startup, better resource efficiency" >> "$ANALYSIS_FILE"
echo "" >> "$ANALYSIS_FILE"

cat >> "$ANALYSIS_FILE" << 'EOF'

## Key Insights

EOF

# Calculate average improvement Native vs JVM
if [ -n "${RPS[java-native]}" ] && [ -n "${RPS[java-jvm]}" ] && \
   [ -n "${RPS[java-reactive-native]}" ] && [ -n "${RPS[java-reactive-jvm]}" ]; then
    
    imp1=$(echo "scale=1; ((${RPS[java-native]} - ${RPS[java-jvm]}) / ${RPS[java-jvm]}) * 100" | bc)
    imp2=$(echo "scale=1; ((${RPS[java-reactive-native]} - ${RPS[java-reactive-jvm]}) / ${RPS[java-reactive-jvm]}) * 100" | bc)
    avg_imp=$(echo "scale=1; ($imp1 + $imp2) / 2" | bc)
    
    echo "- Native images show **${avg_imp}% average improvement** over JVM" >> "$ANALYSIS_FILE"
fi

# Find best and worst
best_rps=$(for b in "${backends[@]}"; do echo "${RPS[$b]:-0}"; done | sort -rn | head -1)
worst_rps=$(for b in "${backends[@]}"; do
    if [ -n "${RPS[$b]}" ]; then echo "${RPS[$b]}"; fi
done | sort -n | head -1)

if [ -n "$best_rps" ] && [ -n "$worst_rps" ]; then
    ratio=$(echo "scale=1; $best_rps / $worst_rps" | bc)
    echo "- Performance variance: **${ratio}x** between fastest and slowest" >> "$ANALYSIS_FILE"
fi

cat >> "$ANALYSIS_FILE" << 'EOF'

## Conclusion

All backends performed adequately under load. The choice depends on:

1. **Performance Requirements:** High load = Go/Native Java
2. **Development Speed:** Rapid iteration = Node.js/Python
3. **Ecosystem:** Enterprise features = Java (Traditional/Reactive)
4. **Team Expertise:** Use what your team knows best

---

*Generated automatically by analyze-results.sh*
EOF

print_step "Analysis complete!"
echo ""
echo "📊 Analysis Report: $ANALYSIS_FILE"
echo ""
echo "To view:"
echo "  cat $ANALYSIS_FILE"
echo ""

# Print summary to console
print_header "Quick Summary"

echo "Top 3 Performers:"
rank=1
for backend in $(for b in "${backends[@]}"; do
    if [ -n "${RPS[$b]}" ]; then
        echo "${RPS[$b]} $b"
    fi
done | sort -rn | head -3 | awk '{print $2}'); do
    rps=${RPS[$backend]}
    lat=${AVG_LAT[$backend]}
    echo "  $rank. $backend: $rps req/s, $lat latency"
    rank=$((rank + 1))
done

echo ""
print_step "Full analysis saved to: $ANALYSIS_FILE"
