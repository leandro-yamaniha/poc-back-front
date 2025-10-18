#!/bin/bash

# Beauty Salon Backend - Test Profiles Runner
# Spring Boot 3.5.4 + Java 21
# Author: Beauty Salon Development Team
# Date: 2025-08-10

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_header() {
    echo -e "${CYAN}================================${NC}"
    echo -e "${CYAN}$1${NC}"
    echo -e "${CYAN}================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Function to check Docker availability
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed or not in PATH"
        return 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker Desktop."
        return 1
    fi
    
    print_success "Docker is available and running"
    return 0
}

# Function to run tests with profile
run_test_profile() {
    local profile=$1
    local description=$2
    local requires_docker=$3
    
    print_header "Running $description"
    
    if [ "$requires_docker" = "true" ]; then
        if ! check_docker; then
            print_error "Skipping $description - Docker required but not available"
            return 1
        fi
    fi
    
    print_info "Executing: ./mvnw test -P $profile"
    
    if ./mvnw test -P "$profile"; then
        print_success "$description completed successfully"
        return 0
    else
        print_error "$description failed"
        return 1
    fi
}

# Function to show menu
show_menu() {
    echo -e "${PURPLE}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                Beauty Salon Test Profiles                   ║"
    echo "║                   Spring Boot 3.5.4                         ║"
    echo "╠══════════════════════════════════════════════════════════════╣"
    echo "║  1) Unit Tests         - Fast tests with mocks (Default)    ║"
    echo "║  2) Integration Tests  - Database + API tests (Docker req.) ║"
    echo "║  3) Performance Tests  - Stress tests (Docker req.)         ║"
    echo "║  4) Mutation Tests     - Code quality analysis              ║"
    echo "║  5) All Tests          - Complete test suite (Docker req.)  ║"
    echo "║                                                              ║"
    echo "║  6) Show Test Coverage - JaCoCo report                      ║"
    echo "║  7) List Active Profiles                                     ║"
    echo "║  8) Clean + Compile                                          ║"
    echo "║                                                              ║"
    echo "║  0) Exit                                                     ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Function to open coverage report
open_coverage_report() {
    local report_path="target/site/jacoco/index.html"
    
    if [ -f "$report_path" ]; then
        print_success "Opening JaCoCo coverage report..."
        if command -v open &> /dev/null; then
            open "$report_path"
        elif command -v xdg-open &> /dev/null; then
            xdg-open "$report_path"
        else
            print_info "Coverage report available at: $(pwd)/$report_path"
        fi
    else
        print_warning "Coverage report not found. Run tests first to generate coverage."
    fi
}

# Main execution
main() {
    # Change to backend directory if not already there
    if [ ! -f "pom.xml" ]; then
        if [ -f "../backend/pom.xml" ]; then
            cd ../backend
        elif [ -f "backend/pom.xml" ]; then
            cd backend
        else
            print_error "Could not find backend directory with pom.xml"
            exit 1
        fi
    fi
    
    print_header "Beauty Salon Backend Test Profiles"
    print_info "Current directory: $(pwd)"
    print_info "Spring Boot 3.5.4 + Java 21"
    echo
    
    # If arguments provided, run directly
    if [ $# -gt 0 ]; then
        case $1 in
            "unit"|"1")
                run_test_profile "unit-tests" "Unit Tests" false
                ;;
            "integration"|"2")
                run_test_profile "integration-tests" "Integration Tests" true
                ;;
            "performance"|"3")
                run_test_profile "performance-tests" "Performance Tests" true
                ;;
            "mutation"|"4")
                run_test_profile "mutation-tests" "Mutation Tests" false
                ;;
            "all"|"5")
                run_test_profile "all-tests" "All Tests" true
                ;;
            "coverage"|"6")
                print_header "Generating Coverage Report"
                ./mvnw test jacoco:report -P unit-tests
                open_coverage_report
                ;;
            *)
                print_error "Invalid argument: $1"
                print_info "Valid arguments: unit, integration, performance, mutation, all, coverage"
                exit 1
                ;;
        esac
        exit $?
    fi
    
    # Interactive menu
    while true; do
        show_menu
        read -p "Select an option (0-8): " choice
        echo
        
        case $choice in
            1)
                run_test_profile "unit-tests" "Unit Tests" false
                ;;
            2)
                run_test_profile "integration-tests" "Integration Tests" true
                ;;
            3)
                run_test_profile "performance-tests" "Performance Tests" true
                ;;
            4)
                run_test_profile "mutation-tests" "Mutation Tests" false
                ;;
            5)
                run_test_profile "all-tests" "All Tests" true
                ;;
            6)
                print_header "Generating Coverage Report"
                if ./mvnw test jacoco:report -P unit-tests; then
                    open_coverage_report
                else
                    print_error "Failed to generate coverage report"
                fi
                ;;
            7)
                print_header "Active Maven Profiles"
                ./mvnw help:active-profiles
                ;;
            8)
                print_header "Clean + Compile"
                ./mvnw clean compile
                ;;
            0)
                print_info "Goodbye!"
                exit 0
                ;;
            *)
                print_error "Invalid option. Please select 0-8."
                ;;
        esac
        
        echo
        read -p "Press Enter to continue..."
        echo
    done
}

# Help function
show_help() {
    echo "Beauty Salon Backend Test Profiles Runner"
    echo
    echo "Usage:"
    echo "  $0                    - Interactive menu"
    echo "  $0 [profile]          - Run specific profile"
    echo
    echo "Available profiles:"
    echo "  unit                  - Unit tests (fast, no Docker)"
    echo "  integration           - Integration tests (requires Docker)"
    echo "  performance           - Performance tests (requires Docker)"
    echo "  mutation              - Mutation tests (PiTest)"
    echo "  all                   - All tests (requires Docker)"
    echo "  coverage              - Generate coverage report"
    echo
    echo "Examples:"
    echo "  $0 unit               - Run unit tests only"
    echo "  $0 integration        - Run integration tests"
    echo "  $0 coverage           - Generate and open coverage report"
}

# Check for help argument
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    show_help
    exit 0
fi

# Run main function
main "$@"
