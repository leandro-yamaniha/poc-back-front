#!/bin/bash
# Node.js Backend Entrypoint with Cassandra Wait and Migration

set -e

CASSANDRA_HOST="${CASSANDRA_HOST:-cassandra}"
CASSANDRA_PORT="${CASSANDRA_PORT:-9042}"

# Set PYTHONPATH for cqlsh
export PYTHONPATH="/usr/lib/python3.12/site-packages:$PYTHONPATH"

echo "🚀 Starting Node.js Backend Initialization..."

# Function to wait for Cassandra
wait_for_cassandra() {
    echo "⏳ Waiting for Cassandra at $CASSANDRA_HOST:$CASSANDRA_PORT..."
    
    max_attempts=30
    attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if nc -z "$CASSANDRA_HOST" "$CASSANDRA_PORT" 2>/dev/null; then
            echo "✅ Cassandra port is open!"
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
    echo "🔍 Checking if migrations already ran..."
    
    # Try to query the migrations table - if it exists, migrations ran
    if npm run migrate:check > /dev/null 2>&1; then
        echo "✅ Migrations already applied"
        return 0
    else
        echo "📋 Migrations not yet applied"
        return 1
    fi
}

# Function to run migrations
run_migrations() {
    echo "📋 Running database migrations..."
    
    # Use lock mechanism to ensure only one backend runs migrations
    LOCK_FILE="/tmp/cassandra-migrations.lock"
    
    # Try to create lock (only first backend will succeed)
    if mkdir "$LOCK_FILE" 2>/dev/null; then
        echo "🔒 Acquired migration lock - this instance will run migrations"
        
        # Run migrations
        npm run migrate
        
        if [ $? -eq 0 ]; then
            echo "✅ Migrations completed successfully!"
            sleep 5  # Wait for schema propagation
        else
            echo "❌ Migrations failed!"
            rmdir "$LOCK_FILE"
            exit 1
        fi
        
        # Release lock
        rmdir "$LOCK_FILE"
    else
        echo "⏳ Another instance is running migrations - waiting..."
        
        # Wait for migrations to complete (check for schema_migrations table)
        max_wait=60
        waited=0
        while [ $waited -lt $max_wait ]; do
            if check_migrations; then
                echo "✅ Migrations completed by another instance"
                return 0
            fi
            sleep 2
            waited=$((waited + 2))
        done
        
        echo "❌ Timeout waiting for migrations"
        exit 1
    fi
}

# Main execution flow
echo "================================================"
echo "  Beauty Salon - Node.js Backend"
echo "================================================"

# Step 1: Wait for Cassandra
wait_for_cassandra

# Step 2: Check/Run migrations
if ! check_migrations; then
    run_migrations
fi

# Step 3: Additional stabilization wait
echo "⏳ Waiting for schema stabilization (3 seconds)..."
sleep 3

# Step 4: Start the application
echo "🚀 Starting Node.js application..."
echo "================================================"

exec "$@"
