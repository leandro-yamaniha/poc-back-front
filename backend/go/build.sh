#!/bin/bash
set -e

echo "🔨 Building Go Backend..."
echo "================================"

# Limpar builds anteriores
rm -rf ./bin
mkdir -p ./bin

# Build main application
echo "🏗️  Building main application..."
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -installsuffix cgo -ldflags="-w -s" -o ./bin/main cmd/server/main.go

# Build migration tool
echo "🏗️  Building migration tool..."
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -installsuffix cgo -ldflags="-w -s" -o ./bin/migrate cmd/migrate/main.go

echo "✅ Go Backend build completed!"
echo "📁 Artifacts in: ./bin"
ls -lh ./bin/
