#!/bin/bash

# SonarQube Analysis Script for Beauty Salon Management System
# This script runs SonarQube analysis for all backend and frontend projects

set -e

echo "🚀 Starting SonarQube Analysis for Beauty Salon Management System"
echo "=================================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if SonarQube scanner is available
if ! command -v sonar-scanner &> /dev/null; then
    echo -e "${RED}❌ SonarQube scanner not found. Please install it first.${NC}"
    echo "Installation: https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/"
    exit 1
fi

# Function to run analysis for a project
run_analysis() {
    local project_dir=$1
    local project_name=$2
    
    echo -e "\n${BLUE}📊 Analyzing $project_name...${NC}"
    echo "----------------------------------------"
    
    if [ -d "$project_dir" ]; then
        cd "$project_dir"
        
        # Run tests and generate coverage if needed
        case "$project_name" in
            "Frontend (React)")
                if [ -f "package.json" ]; then
                    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
                    npm ci
                    echo -e "${YELLOW}🧪 Running tests with coverage...${NC}"
                    npm run test:coverage || npm test -- --coverage --watchAll=false
                fi
                ;;
            "Backend Java Reactive" | "Backend Java Spring")
                if [ -f "pom.xml" ]; then
                    echo -e "${YELLOW}🧪 Running Maven tests with coverage...${NC}"
                    mvn clean verify
                fi
                ;;
            "Backend Node.js")
                if [ -f "package.json" ]; then
                    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
                    npm ci
                    echo -e "${YELLOW}🧪 Running tests with coverage...${NC}"
                    npm run test:coverage || npm test -- --coverage
                fi
                ;;
            "Backend Python")
                if [ -f "requirements.txt" ]; then
                    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
                    pip install -r requirements.txt
                    pip install pytest-cov
                    echo -e "${YELLOW}🧪 Running tests with coverage...${NC}"
                    pytest --cov=app --cov-report=xml --cov-report=term-missing
                fi
                ;;
            "Backend Go")
                if [ -f "go.mod" ]; then
                    echo -e "${YELLOW}🧪 Running Go tests with coverage...${NC}"
                    go test -v -coverprofile=coverage.out ./...
                fi
                ;;
        esac
        
        # Run SonarQube analysis
        echo -e "${YELLOW}🔍 Running SonarQube analysis...${NC}"
        sonar-scanner
        
        echo -e "${GREEN}✅ Analysis completed for $project_name${NC}"
        cd - > /dev/null
    else
        echo -e "${RED}❌ Directory $project_dir not found${NC}"
    fi
}

# Main script execution
echo -e "${BLUE}🏆 Performance Champion: Java Reactive Backend${NC}"
echo -e "${BLUE}📊 Analyzing all 5 backends + frontend...${NC}"
echo

# Analyze each project
run_analysis "frontend" "Frontend (React)"
run_analysis "backend-java-reactive" "Backend Java Reactive"
run_analysis "backend" "Backend Java Spring"
run_analysis "backend-nodejs" "Backend Node.js"
run_analysis "backend-python" "Backend Python"
run_analysis "backend-go" "Backend Go"

echo
echo -e "${GREEN}🎉 SonarQube Analysis Complete!${NC}"
echo -e "${GREEN}=================================================================${NC}"
echo -e "${GREEN}All projects have been analyzed for code quality, security, and maintainability.${NC}"
echo -e "${BLUE}Check your SonarQube dashboard for detailed results.${NC}"
