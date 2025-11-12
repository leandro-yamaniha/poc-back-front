#!/bin/bash
set -e

echo "🔨 Building Java Backend..."
echo "================================"

# Limpar builds anteriores
echo "🧹 Cleaning previous builds..."
./mvnw clean

# Build
echo "🏗️  Building with Maven..."
./mvnw package -Pnative -DskipTests

# Copy artifact to standard location
mkdir -p ./target/release
cp target/beauty-salon ./target/release/beauty-salon

echo "✅ Java Backend build completed!"
echo "📁 Artifact: ./target/release/beauty-salon.jar"
ls -lh ./target/release/
