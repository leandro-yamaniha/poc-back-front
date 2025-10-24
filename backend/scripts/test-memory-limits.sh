#!/bin/bash
# Memory Limit Testing Script for Backend Services
# Tests minimum memory requirements for each backend

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Beauty Salon - Memory Limit Testing"
echo "================================================"
echo ""

# Function to print colored messages
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Memory configurations for testing (in ascending order)
declare -A JAVA_MEMORY_CONFIGS=(
    ["minimal"]="256m 128m"
    ["low"]="512m 256m"
    ["medium"]="1g 512m"
    ["high"]="2g 1g"
)

declare -A DOTNET_MEMORY_CONFIGS=(
    ["minimal"]="256m"
    ["low"]="512m"
    ["medium"]="1g"
    ["high"]="2g"
)

declare -A NODE_MEMORY_CONFIGS=(
    ["minimal"]="256"
    ["low"]="512"
    ["medium"]="1024"
    ["high"]="2048"
)

declare -A PYTHON_MEMORY_CONFIGS=(
    ["minimal"]="256m"
    ["low"]="512m"
    ["medium"]="1g"
    ["high"]="2g"
)

# Function to test Java backend with specific memory settings
test_java_memory() {
    local backend=$1
    local max_mem=$2
    local min_mem=$3
    local config_name=$4
    
    print_step "Testing $backend with $config_name memory ($max_mem max, $min_mem min)"
    
    # Build with memory parameters
    docker build \
        --build-arg JAVA_MAX_MEMORY=$max_mem \
        --build-arg JAVA_MIN_MEMORY=$min_mem \
        -t beauty-salon-$backend-memory-test \
        -f backend/$backend/Dockerfile \
        backend/$backend/ > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Build successful for $backend with $config_name memory"
        
        # Try to run the container
        local port
        if [ "$backend" = "java" ]; then
            port=10001
        else
            port=10006
        fi
        
        docker run -d --name $backend-memory-test-$config_name \
            -p $port:$port \
            --memory=${max_mem} \
            beauty-salon-$backend-memory-test > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            sleep 10
            
            # Check if container is still running
            if docker ps | grep -q $backend-memory-test-$config_name; then
                print_success "$backend container running successfully with $config_name memory"
                
                # Test health endpoint
                if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
                    print_success "$backend health check passed with $config_name memory"
                else
                    print_warning "$backend health check failed with $config_name memory"
                fi
            else
                print_error "$backend container crashed with $config_name memory"
            fi
            
            # Cleanup
            docker stop $backend-memory-test-$config_name > /dev/null 2>&1
            docker rm $backend-memory-test-$config_name > /dev/null 2>&1
        else
            print_error "Failed to start $backend container with $config_name memory"
        fi
        
        docker rmi beauty-salon-$backend-memory-test > /dev/null 2>&1
    else
        print_error "Build failed for $backend with $config_name memory"
    fi
    
    echo ""
}

# Function to test .NET backend with specific memory settings
test_dotnet_memory() {
    local max_mem=$1
    local config_name=$2
    
    print_step "Testing .NET with $config_name memory ($max_mem)"
    
    # Build with memory parameters
    docker build \
        --build-arg DOTNET_MAX_MEMORY=$max_mem \
        -t beauty-salon-dotnet-memory-test \
        -f backend/dotnet/Dockerfile \
        backend/dotnet/ > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Build successful for .NET with $config_name memory"
        
        # Try to run the container
        docker run -d --name dotnet-memory-test-$config_name \
            -p 10002:10002 \
            --memory=${max_mem} \
            beauty-salon-dotnet-memory-test > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            sleep 10
            
            # Check if container is still running
            if docker ps | grep -q dotnet-memory-test-$config_name; then
                print_success ".NET container running successfully with $config_name memory"
                
                # Test health endpoint
                if curl -f http://localhost:10002/health > /dev/null 2>&1; then
                    print_success ".NET health check passed with $config_name memory"
                else
                    print_warning ".NET health check failed with $config_name memory"
                fi
            else
                print_error ".NET container crashed with $config_name memory"
            fi
            
            # Cleanup
            docker stop dotnet-memory-test-$config_name > /dev/null 2>&1
            docker rm dotnet-memory-test-$config_name > /dev/null 2>&1
        else
            print_error "Failed to start .NET container with $config_name memory"
        fi
        
        docker rmi beauty-salon-dotnet-memory-test > /dev/null 2>&1
    else
        print_error "Build failed for .NET with $config_name memory"
    fi
    
    echo ""
}

# Function to test Node.js backend with specific memory settings
test_nodejs_memory() {
    local max_mem=$1
    local config_name=$2
    
    print_step "Testing Node.js with $config_name memory (${max_mem}MB)"
    
    # Build with memory parameters
    docker build \
        --build-arg NODE_MAX_OLD_SPACE_SIZE=$max_mem \
        -t beauty-salon-nodejs-memory-test \
        -f backend/nodejs/Dockerfile \
        backend/nodejs/ > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Build successful for Node.js with $config_name memory"
        
        # Try to run the container
        docker run -d --name nodejs-memory-test-$config_name \
            -p 10004:10004 \
            --memory=${max_mem}m \
            beauty-salon-nodejs-memory-test > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            sleep 10
            
            # Check if container is still running
            if docker ps | grep -q nodejs-memory-test-$config_name; then
                print_success "Node.js container running successfully with $config_name memory"
                
                # Test health endpoint
                if curl -f http://localhost:10004/health > /dev/null 2>&1; then
                    print_success "Node.js health check passed with $config_name memory"
                else
                    print_warning "Node.js health check failed with $config_name memory"
                fi
            else
                print_error "Node.js container crashed with $config_name memory"
            fi
            
            # Cleanup
            docker stop nodejs-memory-test-$config_name > /dev/null 2>&1
            docker rm nodejs-memory-test-$config_name > /dev/null 2>&1
        else
            print_error "Failed to start Node.js container with $config_name memory"
        fi
        
        docker rmi beauty-salon-nodejs-memory-test > /dev/null 2>&1
    else
        print_error "Build failed for Node.js with $config_name memory"
    fi
    
    echo ""
}

# Function to test Python backend with specific memory settings
test_python_memory() {
    local max_mem=$1
    local config_name=$2
    
    print_step "Testing Python with $config_name memory ($max_mem)"
    
    # Build with memory parameters
    docker build \
        --build-arg PYTHON_MAX_MEMORY=$max_mem \
        -t beauty-salon-python-memory-test \
        -f backend/python/Dockerfile \
        backend/python/ > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Build successful for Python with $config_name memory"
        
        # Try to run the container
        docker run -d --name python-memory-test-$config_name \
            -p 10003:10003 \
            --memory=${max_mem} \
            beauty-salon-python-memory-test > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            sleep 15
            
            # Check if container is still running
            if docker ps | grep -q python-memory-test-$config_name; then
                print_success "Python container running successfully with $config_name memory"
                
                # Test health endpoint
                if curl -f http://localhost:10003/ > /dev/null 2>&1; then
                    print_success "Python health check passed with $config_name memory"
                else
                    print_warning "Python health check failed with $config_name memory"
                fi
            else
                print_error "Python container crashed with $config_name memory"
            fi
            
            # Cleanup
            docker stop python-memory-test-$config_name > /dev/null 2>&1
            docker rm python-memory-test-$config_name > /dev/null 2>&1
        else
            print_error "Failed to start Python container with $config_name memory"
        fi
        
        docker rmi beauty-salon-python-memory-test > /dev/null 2>&1
    else
        print_error "Build failed for Python with $config_name memory"
    fi
    
    echo ""
}

# Main execution
print_info "Starting memory limit tests for all backends"
print_info "This will test different memory configurations to find minimum requirements"
echo ""

# Test Java Traditional
print_step "=== JAVA TRADITIONAL MEMORY TESTS ==="
for config in minimal low medium high; do
    memory_config=${JAVA_MEMORY_CONFIGS[$config]}
    max_mem=$(echo $memory_config | cut -d' ' -f1)
    min_mem=$(echo $memory_config | cut -d' ' -f2)
    test_java_memory "java" $max_mem $min_mem $config
done

# Test Java Reactive
print_step "=== JAVA REACTIVE MEMORY TESTS ==="
for config in minimal low medium high; do
    memory_config=${JAVA_MEMORY_CONFIGS[$config]}
    max_mem=$(echo $memory_config | cut -d' ' -f1)
    min_mem=$(echo $memory_config | cut -d' ' -f2)
    test_java_memory "java-reactive" $max_mem $min_mem $config
done

# Test .NET
print_step "=== .NET MEMORY TESTS ==="
for config in minimal low medium high; do
    max_mem=${DOTNET_MEMORY_CONFIGS[$config]}
    test_dotnet_memory $max_mem $config
done

# Test Node.js
print_step "=== NODE.JS MEMORY TESTS ==="
for config in minimal low medium high; do
    max_mem=${NODE_MEMORY_CONFIGS[$config]}
    test_nodejs_memory $max_mem $config
done

# Test Python
print_step "=== PYTHON MEMORY TESTS ==="
for config in minimal low medium high; do
    max_mem=${PYTHON_MEMORY_CONFIGS[$config]}
    test_python_memory $max_mem $config
done

# Go doesn't have specific memory parameters in Dockerfile, but we can test with Docker limits
print_step "=== GO MEMORY TESTS ==="
print_info "Go backend uses native binary - testing with Docker memory limits only"

for config in minimal low medium high; do
    case $config in
        "minimal") mem_limit="256m" ;;
        "low") mem_limit="512m" ;;
        "medium") mem_limit="1g" ;;
        "high") mem_limit="2g" ;;
    esac
    
    print_step "Testing Go with $config memory ($mem_limit)"
    
    # Build Go backend
    docker build -t beauty-salon-go-memory-test -f backend/go/Dockerfile backend/go/ > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Build successful for Go"
        
        # Try to run with memory limit
        docker run -d --name go-memory-test-$config \
            -p 10005:10005 \
            --memory=$mem_limit \
            beauty-salon-go-memory-test > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            sleep 10
            
            # Check if container is still running
            if docker ps | grep -q go-memory-test-$config; then
                print_success "Go container running successfully with $config memory"
                
                # Test health endpoint
                if curl -f http://localhost:10005/health > /dev/null 2>&1; then
                    print_success "Go health check passed with $config memory"
                else
                    print_warning "Go health check failed with $config memory"
                fi
            else
                print_error "Go container crashed with $config memory"
            fi
            
            # Cleanup
            docker stop go-memory-test-$config > /dev/null 2>&1
            docker rm go-memory-test-$config > /dev/null 2>&1
        else
            print_error "Failed to start Go container with $config memory"
        fi
    else
        print_error "Build failed for Go"
    fi
    
    echo ""
done

# Cleanup Go image
docker rmi beauty-salon-go-memory-test > /dev/null 2>&1

print_success "Memory limit testing completed!"
print_info "Check the results above to determine minimum memory requirements for each backend"
