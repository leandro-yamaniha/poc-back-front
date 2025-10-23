#!/bin/bash
set -e

echo "🔨 Building Java Reactive Backend..."
echo "================================"

# Limpar builds anteriores
echo "🧹 Cleaning previous builds..."
./mvnw clean

# Build
echo "🏗️  Building with Maven..."
./mvnw package -DskipTests

# Copy artifact to standard location
mkdir -p ./target/release
cp target/*.jar ./target/release/app.jar

echo "✅ Java Reactive Backend build completed!"
echo "📁 Artifact: ./target/release/app.jar"
ls -lh ./target/release/
