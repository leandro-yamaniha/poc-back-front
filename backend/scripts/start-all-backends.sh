#!/bin/bash

echo "🚀 Starting All Backends..."
echo "=============================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Function to check if port is in use
check_port() {
    local port=$1
    lsof -i :$port > /dev/null 2>&1
    return $?
}

# Function to wait for backend to be ready
wait_for_backend() {
    local port=$1
    local max_wait=30
    local count=0
    
    while [ $count -lt $max_wait ]; do
        if curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/health" 2>/dev/null | grep -q "200\|404"; then
            return 0
        fi
        sleep 1
        count=$((count + 1))
    done
    return 1
}

echo "📋 Starting backends..."
echo ""

# Start Cassandra first
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🗄️  Starting Cassandra...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

cd "$BACKEND_DIR"
if ! docker ps | grep -q cassandra; then
    docker-compose up -d cassandra
    echo "⏳ Waiting for Cassandra to be ready (30s)..."
    sleep 30
    echo -e "${GREEN}✅ Cassandra started${NC}"
else
    echo -e "${YELLOW}⚠️  Cassandra already running${NC}"
fi

echo ""

# Start .NET backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting .NET Backend (port 5001)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 5001; then
    echo -e "${YELLOW}⚠️  Port 5001 already in use${NC}"
else
    cd "$BACKEND_DIR/dotnet"
    nohup dotnet run > /tmp/dotnet-backend.log 2>&1 &
    echo $! > /tmp/dotnet-backend.pid
    if wait_for_backend 5001; then
        echo -e "${GREEN}✅ .NET Backend started${NC}"
    else
        echo -e "${RED}❌ .NET Backend failed to start${NC}"
    fi
fi

echo ""

# Start Java JVM backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Java JVM Backend (port 8080)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8080; then
    echo -e "${YELLOW}⚠️  Port 8080 already in use${NC}"
else
    cd "$BACKEND_DIR/java"
    nohup java -jar target/beauty-salon-jvm.jar > /tmp/java-jvm-backend.log 2>&1 &
    echo $! > /tmp/java-jvm-backend.pid
    if wait_for_backend 8080; then
        echo -e "${GREEN}✅ Java JVM Backend started${NC}"
    else
        echo -e "${RED}❌ Java JVM Backend failed to start${NC}"
    fi
fi

echo ""

# Start Java Native backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Java Native Backend (port 8081)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8081; then
    echo -e "${YELLOW}⚠️  Port 8081 already in use${NC}"
else
    cd "$BACKEND_DIR/java"
    nohup ./target/beauty-salon > /tmp/java-native-backend.log 2>&1 &
    echo $! > /tmp/java-native-backend.pid
    if wait_for_backend 8081; then
        echo -e "${GREEN}✅ Java Native Backend started${NC}"
    else
        echo -e "${RED}❌ Java Native Backend failed to start${NC}"
    fi
fi

echo ""

# Start Go backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Go Backend (port 8082)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8082; then
    echo -e "${YELLOW}⚠️  Port 8082 already in use${NC}"
else
    cd "$BACKEND_DIR/go"
    nohup ./beauty-salon > /tmp/go-backend.log 2>&1 &
    echo $! > /tmp/go-backend.pid
    if wait_for_backend 8082; then
        echo -e "${GREEN}✅ Go Backend started${NC}"
    else
        echo -e "${RED}❌ Go Backend failed to start${NC}"
    fi
fi

echo ""

# Start Node.js backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Node.js Backend (port 3000)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 3000; then
    echo -e "${YELLOW}⚠️  Port 3000 already in use${NC}"
else
    cd "$BACKEND_DIR/nodejs"
    nohup node src/server.js > /tmp/nodejs-backend.log 2>&1 &
    echo $! > /tmp/nodejs-backend.pid
    if wait_for_backend 3000; then
        echo -e "${GREEN}✅ Node.js Backend started${NC}"
    else
        echo -e "${RED}❌ Node.js Backend failed to start${NC}"
    fi
fi

echo ""

# Start Python backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Python Backend (port 8000)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8000; then
    echo -e "${YELLOW}⚠️  Port 8000 already in use${NC}"
else
    cd "$BACKEND_DIR/python"
    nohup python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 > /tmp/python-backend.log 2>&1 &
    echo $! > /tmp/python-backend.pid
    if wait_for_backend 8000; then
        echo -e "${GREEN}✅ Python Backend started${NC}"
    else
        echo -e "${RED}❌ Python Backend failed to start${NC}"
    fi
fi

echo ""

# Start Java Reactive JVM backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Java Reactive JVM Backend (port 8085)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8085; then
    echo -e "${YELLOW}⚠️  Port 8085 already in use${NC}"
else
    cd "$BACKEND_DIR/java-reactive"
    nohup java -jar target/beauty-salon-reactive-jvm.jar > /tmp/java-reactive-jvm-backend.log 2>&1 &
    echo $! > /tmp/java-reactive-jvm-backend.pid
    if wait_for_backend 8085; then
        echo -e "${GREEN}✅ Java Reactive JVM Backend started${NC}"
    else
        echo -e "${RED}❌ Java Reactive JVM Backend failed to start${NC}"
    fi
fi

echo ""

# Start Java Reactive Native backend
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}🔨 Starting Java Reactive Native Backend (port 8086)...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if check_port 8086; then
    echo -e "${YELLOW}⚠️  Port 8086 already in use${NC}"
else
    cd "$BACKEND_DIR/java-reactive"
    nohup ./target/beauty-salon-reactive > /tmp/java-reactive-native-backend.log 2>&1 &
    echo $! > /tmp/java-reactive-native-backend.pid
    if wait_for_backend 8086; then
        echo -e "${GREEN}✅ Java Reactive Native Backend started${NC}"
    else
        echo -e "${RED}❌ Java Reactive Native Backend failed to start${NC}"
    fi
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}✅ All backends started!${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📊 Backend Status:"
echo ""

# Check status of all backends
for backend_port in "dotnet:5001" "java-jvm:8080" "java-native:8081" "go:8082" "nodejs:3000" "python:8000" "java-reactive-jvm:8085" "java-reactive-native:8086"; do
    IFS=':' read -r name port <<< "$backend_port"
    if check_port $port; then
        echo -e "  ${GREEN}✅${NC} $name (port $port)"
    else
        echo -e "  ${RED}❌${NC} $name (port $port)"
    fi
done

echo ""
echo "📝 Logs available at:"
echo "  /tmp/*-backend.log"
echo ""
echo "🛑 To stop all backends:"
echo "  ./stop-all-backends.sh"
echo ""
