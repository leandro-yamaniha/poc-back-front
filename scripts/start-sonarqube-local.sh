#!/bin/bash

# Start SonarQube Local Instance with Docker
# Beauty Salon Management System - Local Code Quality Analysis

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting SonarQube Local Instance${NC}"
echo "=========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose not found. Please install it first.${NC}"
    exit 1
fi

echo -e "${YELLOW}📦 Starting SonarQube and PostgreSQL containers...${NC}"

# Start SonarQube with PostgreSQL
docker-compose -f ../docker-compose-sonarqube.yml up -d

echo -e "${YELLOW}⏳ Waiting for SonarQube to start...${NC}"

# Wait for SonarQube to be ready
max_attempts=60
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -s http://localhost:9000/api/system/status | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✅ SonarQube is ready!${NC}"
        break
    fi
    
    echo -e "${YELLOW}⏳ Waiting for SonarQube... (attempt $((attempt + 1))/$max_attempts)${NC}"
    sleep 5
    attempt=$((attempt + 1))
done

if [ $attempt -eq $max_attempts ]; then
    echo -e "${RED}❌ SonarQube failed to start within expected time${NC}"
    echo -e "${YELLOW}📋 Check logs with: docker-compose -f docker-compose-sonarqube.yml logs${NC}"
    exit 1
fi

echo
echo -e "${GREEN}🎉 SonarQube Local Instance Started Successfully!${NC}"
echo "=================================================="
echo -e "${BLUE}📊 SonarQube URL: http://localhost:9000${NC}"
echo -e "${BLUE}👤 Default Login: admin / admin${NC}"
echo -e "${YELLOW}🔧 Change password on first login${NC}"
echo
echo -e "${GREEN}Next Steps:${NC}"
echo -e "${GREEN}1. Open http://localhost:9000 in your browser${NC}"
echo -e "${GREEN}2. Login with admin/admin${NC}"
echo -e "${GREEN}3. Change the default password${NC}"
echo -e "${GREEN}4. Create projects for each backend/frontend${NC}"
echo -e "${GREEN}5. Run analysis with: ./scripts/sonar-analysis-local.sh${NC}"
echo
echo -e "${BLUE}🏆 Ready to analyze the Performance Champion Java Reactive Backend!${NC}"
