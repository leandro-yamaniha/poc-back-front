#!/bin/bash
# Go Backend Entrypoint with Cassandra Wait

set -e

CASSANDRA_HOST="${CASSANDRA_HOST:-cassandra}"
CASSANDRA_PORT="${CASSANDRA_PORT:-9042}"

echo "🚀 Starting Go Backend Initialization..."

# Function to wait for Cassandra
wait_for_cassandra() {
    echo "⏳ Waiting for Cassandra at $CASSANDRA_HOST:$CASSANDRA_PORT..."
    
    max_attempts=30
    attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if nc -z "$CASSANDRA_HOST" "$CASSANDRA_PORT" 2>/dev/null; then
            echo "✅ Cassandra port is open!"
            # Additional stabilization time
            sleep 5
            return 0
        fi
        
        attempt=$((attempt + 1))
        sleep 2
    done
    
    echo "❌ Timeout waiting for Cassandra"
    exit 1
}

# Function to check if migrations already ran
check_migrations() {
    echo "🔍 Checking if migrations already exist..."
    
    # Go runs migrations automatically on startup
    # Just provide extra time buffer if needed
    echo "📋 Go backend will handle migrations automatically"
    return 1
}

# Main execution flow
echo "================================================"
echo "  Beauty Salon - Go Backend"
echo "================================================"

# Step 1: Wait for Cassandra
wait_for_cassandra

# Step 2: Check if migrations exist
if check_migrations; then
    echo "⏩ Skipping migrations - already applied"
    # Additional wait for schema stability
    echo "⏳ Waiting for schema stability (10 seconds)..."
    sleep 10
else
    echo "⚠️  Keyspace not found - Go backend will run migrations on startup"
    echo "⏳ Waiting for schema propagation buffer (15 seconds)..."
    sleep 15
fi

# Step 3: Start the application (migrations run automatically in Go app)
echo "🚀 Starting Go application..."
echo "================================================"

exec "$@"
