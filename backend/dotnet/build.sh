#!/bin/bash
set -e

echo "🔨 Building .NET Backend..."
echo "================================"

# Limpar builds anteriores
rm -rf ./publish
mkdir -p ./publish

# Restore dependencies
echo "📦 Restoring dependencies..."
dotnet restore BeautySalonAPI/BeautySalonAPI.csproj

# Build
echo "🏗️  Building project..."
dotnet build BeautySalonAPI/BeautySalonAPI.csproj -c Release

# Publish
echo "📦 Publishing application..."
dotnet publish BeautySalonAPI/BeautySalonAPI.csproj -c Release -o ./publish

echo "✅ .NET Backend build completed!"
echo "📁 Artifacts in: ./publish"
ls -lh ./publish/
