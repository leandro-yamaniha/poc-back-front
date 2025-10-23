#!/bin/bash
set -e

echo "🔨 Building Python Backend..."
echo "================================"

# Limpar builds anteriores
rm -rf ./venv
rm -rf ./__pycache__
find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
find . -type f -name "*.pyc" -delete 2>/dev/null || true

# Detect Python version
if command -v python3.11 &> /dev/null; then
    PYTHON_CMD=python3.11
    echo "📦 Using Python 3.11"
elif command -v python3.12 &> /dev/null; then
    PYTHON_CMD=python3.12
    echo "📦 Using Python 3.12"
else
    PYTHON_CMD=python3
    echo "📦 Using default Python 3"
fi

# Create virtual environment
echo "📦 Creating virtual environment..."
$PYTHON_CMD -m venv venv

# Activate and install dependencies
echo "📦 Installing dependencies..."
source venv/bin/activate

# Upgrade pip
pip install --upgrade pip

# Install dependencies with compatibility fixes
echo "📦 Installing requirements (this may take a while)..."

# Try fixed versions first (pre-compiled, no Rust needed)
if [ -f "requirements-fixed.txt" ]; then
    echo "📦 Using fixed requirements (pre-compiled versions)..."
    pip install -r requirements-fixed.txt
else
    # Fallback to original requirements
    pip install -r requirements.txt --prefer-binary || {
        echo "⚠️  Warning: Some packages may need compilation"
        echo "💡 Tip: Install Rust if build fails: curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh"
        pip install -r requirements.txt
    }
fi

echo "✅ Python Backend build completed!"
echo "📁 Virtual environment created"
du -sh ./venv
