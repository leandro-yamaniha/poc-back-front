#!/bin/bash

# Stop SonarQube Local Instance
# Beauty Salon Management System - Local Code Quality Analysis

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🛑 Stopping SonarQube Local Instance${NC}"
echo "====================================="

# Stop SonarQube containers
echo -e "${YELLOW}📦 Stopping SonarQube and PostgreSQL containers...${NC}"
docker-compose -f ./docker-compose.yml down

echo -e "${GREEN}✅ SonarQube Local Instance Stopped${NC}"
echo
echo -e "${BLUE}💡 To start again: cd tools/sonarqube && ./start-sonarqube-local.sh${NC}"
echo -e "${BLUE}🗑️  To remove all data: docker-compose -f ./docker-compose.yml down -v${NC}"
