#!/bin/bash

# Beauty Salon Go Backend Startup Script

set -e

echo "🏥 Beauty Salon Go Backend Setup & Start"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check if Docker is installed and running
check_docker() {
    print_header "Checking Docker installation..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi
    
    print_status "Docker is installed and running ✓"
}

# Check if docker-compose is available
check_docker_compose() {
    print_header "Checking Docker Compose..."
    
    if command -v docker-compose &> /dev/null; then
        COMPOSE_CMD="docker-compose"
    elif docker compose version &> /dev/null; then
        COMPOSE_CMD="docker compose"
    else
        print_error "Docker Compose is not available. Please install Docker Compose."
        exit 1
    fi
    
    print_status "Docker Compose is available ✓"
}

# Create .env file if it doesn't exist
setup_env() {
    print_header "Setting up environment configuration..."
    
    if [ ! -f .env ]; then
        print_status "Creating .env file from template..."
        cp .env.example .env
        print_warning "Please review and update the .env file with your specific configuration."
    else
        print_status "Environment file already exists ✓"
    fi
}

# Build and start services
start_services() {
    print_header "Building and starting services..."
    
    print_status "Building Go backend image..."
    $COMPOSE_CMD build --no-cache
    
    print_status "Starting services (Cassandra + Go API)..."
    $COMPOSE_CMD up -d
    
    print_status "Services started successfully ✓"
}

# Wait for services to be healthy
wait_for_services() {
    print_header "Waiting for services to be ready..."
    
    print_status "Waiting for Cassandra to be healthy..."
    timeout=300  # 5 minutes
    elapsed=0
    
    while [ $elapsed -lt $timeout ]; do
        if $COMPOSE_CMD ps cassandra | grep -q "healthy"; then
            print_status "Cassandra is healthy ✓"
            break
        fi
        
        if [ $((elapsed % 30)) -eq 0 ]; then
            print_status "Still waiting for Cassandra... (${elapsed}s elapsed)"
        fi
        
        sleep 5
        elapsed=$((elapsed + 5))
    done
    
    if [ $elapsed -ge $timeout ]; then
        print_error "Cassandra failed to become healthy within ${timeout} seconds"
        print_error "Check logs with: $COMPOSE_CMD logs cassandra"
        exit 1
    fi
    
    print_status "Waiting for Go API to be healthy..."
    sleep 10  # Give API a moment to start
    
    elapsed=0
    while [ $elapsed -lt 60 ]; do
        if curl -f http://localhost:8081/health &> /dev/null; then
            print_status "Go API is healthy ✓"
            break
        fi
        
        sleep 5
        elapsed=$((elapsed + 5))
    done
    
    if [ $elapsed -ge 60 ]; then
        print_warning "Go API health check failed, but continuing..."
        print_warning "Check logs with: $COMPOSE_CMD logs api"
    fi
}

# Show service status and endpoints
show_status() {
    print_header "Service Status"
    
    echo ""
    $COMPOSE_CMD ps
    
    echo ""
    print_status "🚀 Services are running!"
    echo ""
    echo -e "${BLUE}Available Endpoints:${NC}"
    echo "  • Go API:          http://localhost:8081"
    echo "  • Health Check:    http://localhost:8081/health"
    echo "  • API Docs:        http://localhost:8081/api/v1"
    echo "  • Cassandra:       localhost:9043"
    echo ""
    echo -e "${BLUE}Useful Commands:${NC}"
    echo "  • View logs:       $COMPOSE_CMD logs -f"
    echo "  • Stop services:   $COMPOSE_CMD down"
    echo "  • Restart:         $COMPOSE_CMD restart"
    echo "  • Rebuild:         $COMPOSE_CMD up --build -d"
    echo ""
}

# Main execution
main() {
    cd "$(dirname "$0")/.."
    
    check_docker
    check_docker_compose
    setup_env
    start_services
    wait_for_services
    show_status
    
    print_status "✨ Beauty Salon Go Backend is ready!"
}

# Handle script arguments
case "${1:-start}" in
    "start")
        main
        ;;
    "stop")
        print_header "Stopping services..."
        $COMPOSE_CMD down
        print_status "Services stopped ✓"
        ;;
    "restart")
        print_header "Restarting services..."
        $COMPOSE_CMD restart
        print_status "Services restarted ✓"
        ;;
    "logs")
        $COMPOSE_CMD logs -f
        ;;
    "status")
        $COMPOSE_CMD ps
        ;;
    "clean")
        print_header "Cleaning up..."
        $COMPOSE_CMD down -v --remove-orphans
        docker system prune -f
        print_status "Cleanup completed ✓"
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs|status|clean}"
        echo ""
        echo "Commands:"
        echo "  start   - Build and start all services (default)"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  logs    - Show service logs"
        echo "  status  - Show service status"
        echo "  clean   - Stop services and clean up volumes"
        exit 1
        ;;
esac
