#!/bin/bash
# Cloud Cost Calculator for Beauty Salon Backends

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Cloud Cost Calculator"
echo "================================================"
echo ""

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Backend configurations
declare -A BACKENDS=(
    ["go"]="52MB 64MB 0.1"
    ["nodejs"]="276MB 128MB 0.25"
    ["dotnet"]="367MB 256MB 0.5"
    ["java-reactive"]="378MB 512MB 0.5"
    ["java-traditional"]="380MB 512MB 0.5"
    ["python"]="393MB 256MB 0.25"
)

# Cloud pricing (per hour in USD)
declare -A AWS_PRICING=(
    ["t4g.nano"]="0.0042 2 0.5"
    ["t4g.micro"]="0.0084 2 1"
    ["t4g.small"]="0.0168 2 2"
    ["t4g.medium"]="0.0336 2 4"
    ["t4g.large"]="0.0672 2 8"
    ["t4g.xlarge"]="0.1344 4 16"
)

declare -A GCP_PRICING=(
    ["e2-micro"]="0.006 1 1"
    ["e2-small"]="0.012 1 2"
    ["e2-medium"]="0.024 1 4"
    ["e2-standard-2"]="0.067 2 8"
    ["e2-standard-4"]="0.134 4 16"
)

declare -A AZURE_PRICING=(
    ["B1ls"]="0.0052 1 0.5"
    ["B1s"]="0.0104 1 1"
    ["B1ms"]="0.0208 1 2"
    ["B2s"]="0.0416 2 4"
    ["B2ms"]="0.0832 2 8"
    ["B4ms"]="0.166 4 16"
)

# Function to calculate containers per instance
calculate_containers() {
    local container_memory=$1
    local instance_memory=$2
    local container_cpu=$3
    local instance_cpu=$4
    
    # Convert memory to MB
    local container_mb=$(echo $container_memory | sed 's/MB//')
    local instance_mb=$(echo $instance_memory | awk '{print $1 * 1024}')
    
    # Calculate based on memory constraint
    local memory_containers=$(echo "scale=0; $instance_mb / $container_mb" | bc)
    
    # Calculate based on CPU constraint  
    local cpu_containers=$(echo "scale=0; $instance_cpu / $container_cpu" | bc)
    
    # Return the minimum (bottleneck)
    if (( $(echo "$memory_containers < $cpu_containers" | bc -l) )); then
        echo $memory_containers
    else
        echo $cpu_containers
    fi
}

# Function to find best instance for backend
find_best_instance() {
    local backend=$1
    local cloud=$2
    
    # Get backend specs
    local backend_specs=(${BACKENDS[$backend]})
    local container_size=${backend_specs[0]}
    local container_memory=${backend_specs[1]}
    local container_cpu=${backend_specs[2]}
    
    local best_cost=999999
    local best_instance=""
    local best_containers=0
    
    # Get pricing array based on cloud
    local -n pricing_ref
    case $cloud in
        "aws") pricing_ref=AWS_PRICING ;;
        "gcp") pricing_ref=GCP_PRICING ;;
        "azure") pricing_ref=AZURE_PRICING ;;
    esac
    
    # Iterate through instances
    for instance in "${!pricing_ref[@]}"; do
        local instance_specs=(${pricing_ref[$instance]})
        local price=${instance_specs[0]}
        local vcpu=${instance_specs[1]}
        local memory=${instance_specs[2]}
        
        # Calculate containers that fit
        local containers=$(calculate_containers $container_memory "${memory}GB" $container_cpu $vcpu)
        
        if [ $containers -gt 0 ]; then
            local cost_per_container=$(echo "scale=6; $price / $containers" | bc)
            
            if (( $(echo "$cost_per_container < $best_cost" | bc -l) )); then
                best_cost=$cost_per_container
                best_instance=$instance
                best_containers=$containers
            fi
        fi
    done
    
    echo "$best_instance $best_containers $best_cost"
}

# Function to calculate scaling costs
calculate_scaling_cost() {
    local backend=$1
    local requests_per_second=$2
    
    print_info "Calculating scaling costs for $backend at ${requests_per_second} req/s"
    
    # Estimate containers needed (rough calculation)
    local containers_needed
    case $backend in
        "go") containers_needed=$(echo "scale=0; $requests_per_second / 200" | bc) ;;
        "nodejs") containers_needed=$(echo "scale=0; $requests_per_second / 100" | bc) ;;
        "dotnet") containers_needed=$(echo "scale=0; $requests_per_second / 80" | bc) ;;
        "java-reactive") containers_needed=$(echo "scale=0; $requests_per_second / 120" | bc) ;;
        "java-traditional") containers_needed=$(echo "scale=0; $requests_per_second / 80" | bc) ;;
        "python") containers_needed=$(echo "scale=0; $requests_per_second / 50" | bc) ;;
    esac
    
    # Minimum 1 container
    if [ $containers_needed -lt 1 ]; then
        containers_needed=1
    fi
    
    echo ""
    echo "| Cloud | Best Instance | Containers/Instance | Cost/Container/Hour | Monthly Cost |"
    echo "|-------|---------------|---------------------|---------------------|--------------|"
    
    for cloud in aws gcp azure; do
        local result=($(find_best_instance $backend $cloud))
        local instance=${result[0]}
        local containers_per_instance=${result[1]}
        local cost_per_container=${result[2]}
        
        if [ -n "$instance" ]; then
            local monthly_cost=$(echo "scale=2; $cost_per_container * $containers_needed * 24 * 30" | bc)
            echo "| $cloud | $instance | $containers_per_instance | \$${cost_per_container} | \$${monthly_cost} |"
        fi
    done
    echo ""
}

# Main execution
print_step "Starting Cloud Cost Analysis..."
echo ""

# Create results file
RESULTS_FILE="/tmp/cloud-cost-analysis.txt"
cat > $RESULTS_FILE << 'EOF'
# Beauty Salon - Cloud Cost Analysis Results

## Cost Analysis by Backend and Scale

EOF

print_step "Analyzing costs for different scales..."

for backend in "${!BACKENDS[@]}"; do
    echo "## $backend Backend" >> $RESULTS_FILE
    echo "" >> $RESULTS_FILE
    
    print_info "Analyzing $backend backend..."
    
    # Low scale (100 req/s)
    echo "### Low Scale (100 req/s)" >> $RESULTS_FILE
    calculate_scaling_cost $backend 100 >> $RESULTS_FILE
    
    # Medium scale (1000 req/s)  
    echo "### Medium Scale (1000 req/s)" >> $RESULTS_FILE
    calculate_scaling_cost $backend 1000 >> $RESULTS_FILE
    
    # High scale (10000 req/s)
    echo "### High Scale (10000 req/s)" >> $RESULTS_FILE
    calculate_scaling_cost $backend 10000 >> $RESULTS_FILE
    
    echo "---" >> $RESULTS_FILE
    echo "" >> $RESULTS_FILE
done

print_success "Analysis complete! Results saved to $RESULTS_FILE"
echo ""
print_info "Displaying summary..."
cat $RESULTS_FILE

# Copy to docs
cp $RESULTS_FILE "../docs/CLOUD_COST_CALCULATOR_RESULTS.md"
print_success "Results copied to docs/CLOUD_COST_CALCULATOR_RESULTS.md"
