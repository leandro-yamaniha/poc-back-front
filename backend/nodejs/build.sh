#!/bin/bash
set -e

echo "🔨 Building Node.js Backend..."
echo "================================"

# Limpar builds anteriores
rm -rf ./dist
rm -rf ./node_modules

# Install dependencies
echo "📦 Installing dependencies..."
npm ci --production

# Build (se tiver TypeScript ou processo de build)
if [ -f "tsconfig.json" ]; then
    echo "🏗️  Building TypeScript..."
    npm run build
fi

echo "✅ Node.js Backend build completed!"
echo "📁 Dependencies installed"
du -sh ./node_modules
