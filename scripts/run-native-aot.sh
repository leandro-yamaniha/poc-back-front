#!/bin/bash
# Helper script to run Native AOT with Docker Compose

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

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

# Get the base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "================================================"
echo "  Beauty Salon - Native AOT Runner"
echo "================================================"
echo ""

# Check if Docker images exist
check_images() {
    print_step "Checking Docker images..."
    
    if ! docker images | grep -q "beauty-salon-reactive.*native"; then
        print_warning "Native images not found!"
        echo ""
        echo "Please build the native images first:"
        echo "  cd backend"
        echo "  ./scripts/build-aot-native.sh"
        echo "  ./scripts/build-docker-native.sh"
        echo ""
        read -p "Do you want to build them now? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            cd "$BASE_DIR/backend"
            ./scripts/build-aot-native.sh
            ./scripts/build-docker-native.sh
            cd "$BASE_DIR"
        else
            exit 1
        fi
    fi
    
    print_success "Docker images found"
}

# Show menu
show_menu() {
    echo ""
    echo "Select deployment mode:"
    echo ""
    echo "  1) Production (Native AOT with Cassandra)"
    echo "  2) Test Mode (Native AOT without Cassandra)"
    echo "  3) Stop all"
    echo "  4) View logs"
    echo "  5) Status"
    echo "  6) Clean up"
    echo ""
    read -p "Enter your choice [1-6]: " choice
}

# Start production (with Cassandra)
start_production() {
    print_step "Starting Production mode (Native AOT with Cassandra)..."
    cd "$BASE_DIR"
    docker-compose -f docker-compose.aot-native.yml up -d cassandra api-native-production
    print_success "Production mode started"
    show_info
}

# Start test mode (without Cassandra)
start_test() {
    print_step "Starting Test mode (Native AOT without Cassandra)..."
    cd "$BASE_DIR"
    docker run -d --name beauty-salon-api-test \
        -p 8085:8085 \
        --rm \
        beauty-salon-reactive:native-final \
        --server.port=8085 \
        --spring.profiles.active=test
    print_success "Test mode started"
    show_info_test
}

# Stop all
stop_all() {
    print_step "Stopping all services..."
    cd "$BASE_DIR"
    docker-compose -f docker-compose.aot-native.yml down
    docker stop beauty-salon-api-test 2>/dev/null || true
    docker rm beauty-salon-api-test 2>/dev/null || true
    print_success "All services stopped"
}

# View logs
view_logs() {
    echo ""
    echo "Select service:"
    echo "  1) Cassandra"
    echo "  2) API Native Production"
    echo "  3) API Test Mode"
    echo "  4) All"
    echo ""
    read -p "Enter your choice [1-4]: " log_choice
    
    cd "$BASE_DIR"
    case $log_choice in
        1) docker-compose -f docker-compose.aot-native.yml logs -f cassandra ;;
        2) docker-compose -f docker-compose.aot-native.yml logs -f api-native-production ;;
        3) docker logs -f beauty-salon-api-test ;;
        4) docker-compose -f docker-compose.aot-native.yml logs -f && docker logs -f beauty-salon-api-test ;;
        *) print_error "Invalid choice" ;;
    esac
}

# Show status
show_status() {
    print_step "Service Status:"
    cd "$BASE_DIR"
    docker-compose -f docker-compose.aot-native.yml ps
    
    echo ""
    echo "Test containers:"
    docker ps --filter name=beauty-salon-api-test --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo ""
    print_step "Resource Usage:"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}" \
        $(docker-compose -f docker-compose.aot-native.yml ps -q 2>/dev/null) \
        $(docker ps -q --filter name=beauty-salon-api-test 2>/dev/null)
}

# Clean up
clean_up() {
    print_warning "This will remove all containers, volumes, and networks!"
    read -p "Are you sure? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_step "Cleaning up..."
        cd "$BASE_DIR"
        docker-compose -f docker-compose.aot-native.yml --profile debug down -v
        print_success "Clean up completed"
    else
        print_step "Clean up cancelled"
    fi
}

# Show connection info
show_info() {
    echo ""
    echo "================================================"
    echo "  CONNECTION INFO"
    echo "================================================"
    echo ""
    echo "🚀 API Endpoints:"
    echo "  Production: http://localhost:8085"
    echo ""
    echo "📊 Health Checks:"
    echo "  Production: http://localhost:8085/actuator/health"
    echo ""
    echo "📚 API Documentation:"
    echo "  Production: http://localhost:8085/swagger-ui/index.html"
    echo ""
    echo "🗄️  Cassandra:"
    echo "  Host: localhost"
    echo "  Port: 9042"
    echo "  Keyspace: beauty_salon"
    echo ""
    echo "💡 Useful Commands:"
    echo "  View logs:    docker-compose -f docker-compose.aot-native.yml logs -f"
    echo "  Stop all:     docker-compose -f docker-compose.aot-native.yml down"
    echo "  Restart:      docker-compose -f docker-compose.aot-native.yml restart"
    echo ""
    echo "⚡ Native AOT Benefits:"
    echo "  • Startup: <1 second (vs 3-5s JVM)"
    echo "  • Memory: 128-256MB (vs 256-384MB JVM)"
    echo "  • Size: 140MB (vs 380MB JAR+JRE)"
    echo ""
    echo "================================================"
}

# Show test mode info
show_info_test() {
    echo ""
    echo "================================================"
    echo "  TEST MODE INFO"
    echo "================================================"
    echo ""
    echo "🚀 API Endpoint:"
    echo "  Test Mode: http://localhost:8085"
    echo ""
    echo "📊 Health Check:"
    echo "  Test: http://localhost:8085/actuator/health"
    echo ""
    echo "📚 API Documentation:"
    echo "  Test: http://localhost:8085/swagger-ui/index.html"
    echo ""
    echo "💡 Test Mode Features:"
    echo "  • No Cassandra dependency"
    echo "  • In-memory H2 database"
    echo "  • Fast startup for testing"
    echo ""
    echo "================================================"
}

# Main execution
main() {
    check_images
    show_menu
    
    case $choice in
        1) start_production ;;
        2) start_test ;;
        3) stop_all ;;
        4) view_logs ;;
        5) show_status ;;
        6) clean_up ;;
        *) print_error "Invalid choice" ;;
    esac
}

# Execute main function
main "$@"
