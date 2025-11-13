#!/bin/bash

echo "🚀 Starting Backends for Stress Test"
echo "====================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Start Cassandra
echo -e "${BLUE}🗄️  Starting Cassandra...${NC}"
cd "$BACKEND_DIR/java"
docker-compose up -d cassandra
echo "⏳ Waiting for Cassandra (30s)..."
sleep 30
echo -e "${GREEN}✅ Cassandra ready${NC}"
echo ""

# Start backends
echo -e "${BLUE}🔨 Starting backends...${NC}"
echo ""

# Java JVM
echo "Starting Java JVM (port 8080)..."
cd "$BACKEND_DIR/java"
SERVER_PORT=8080 nohup java -jar target/beauty-salon-jvm.jar > /tmp/java-jvm.log 2>&1 &
echo $! > /tmp/java-jvm.pid
sleep 5

# Java Native  
echo "Starting Java Native (port 8081)..."
cd "$BACKEND_DIR/java"
SERVER_PORT=8081 nohup ./target/beauty-salon > /tmp/java-native.log 2>&1 &
echo $! > /tmp/java-native.pid
sleep 3

# Java Reactive JVM
echo "Starting Java Reactive JVM (port 8085)..."
cd "$BACKEND_DIR/java-reactive"
SERVER_PORT=8085 nohup java -jar target/beauty-salon-reactive-jvm.jar > /tmp/java-reactive-jvm.log 2>&1 &
echo $! > /tmp/java-reactive-jvm.pid
sleep 5

# Java Reactive Native
echo "Starting Java Reactive Native (port 8086)..."
cd "$BACKEND_DIR/java-reactive"
SERVER_PORT=8086 nohup ./target/beauty-salon-reactive > /tmp/java-reactive-native.log 2>&1 &
echo $! > /tmp/java-reactive-native.pid
sleep 3

# Go
echo "Starting Go (port 8082)..."
cd "$BACKEND_DIR/go"
PORT=8082 nohup ./beauty-salon > /tmp/go.log 2>&1 &
echo $! > /tmp/go.pid
sleep 2

# Node.js
echo "Starting Node.js (port 3000)..."
cd "$BACKEND_DIR/nodejs"
PORT=3000 nohup node src/server.js > /tmp/nodejs.log 2>&1 &
echo $! > /tmp/nodejs.pid
sleep 2

# Python
echo "Starting Python (port 8000)..."
cd "$BACKEND_DIR/python"
nohup python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 > /tmp/python.log 2>&1 &
echo $! > /tmp/python.pid
sleep 2

# .NET
echo "Starting .NET (port 5001)..."
cd "$BACKEND_DIR/dotnet"
ASPNETCORE_URLS="http://localhost:5001" nohup dotnet run > /tmp/dotnet.log 2>&1 &
echo $! > /tmp/dotnet.pid
sleep 3

echo ""
echo -e "${GREEN}✅ All backends started!${NC}"
echo ""
echo "⏳ Waiting for backends to be ready (20s)..."
sleep 20

echo ""
echo "📊 Checking backend status..."
echo ""

# Check each backend
for backend_port in "Java JVM:8080" "Java Native:8081" "Java Reactive JVM:8085" "Java Reactive Native:8086" "Go:8082" "Node.js:3000" "Python:8000" ".NET:5001"; do
    IFS=':' read -r name port <<< "$backend_port"
    status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/health" 2>/dev/null || echo "000")
    if [ "$status" = "200" ] || [ "$status" = "404" ]; then
        echo -e "  ${GREEN}✅${NC} $name (port $port) - Status: $status"
    else
        echo -e "  ${YELLOW}⚠️${NC}  $name (port $port) - Status: $status (may still be starting)"
    fi
done

echo ""
echo "📝 Logs available at: /tmp/*.log"
echo ""
echo "🎯 Ready for stress testing!"
echo ""
