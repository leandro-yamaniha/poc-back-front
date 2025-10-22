#!/bin/bash
# Wait for Cassandra to be ready before starting backend services

set -e

host="$1"
port="${2:-9042}"
shift 2
cmd="$@"

echo "⏳ Waiting for Cassandra at $host:$port..."

# Wait for Cassandra to accept connections
until nc -z "$host" "$port"; do
  echo "   Cassandra is unavailable - sleeping"
  sleep 2
done

echo "✅ Cassandra is up - checking if ready to accept queries..."

# Wait for Cassandra to be ready to accept CQL queries
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
  if docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES" > /dev/null 2>&1; then
    echo "✅ Cassandra is ready to accept queries!"
    break
  fi
  
  attempt=$((attempt + 1))
  echo "   Cassandra not ready yet (attempt $attempt/$max_attempts) - waiting..."
  sleep 2
done

if [ $attempt -eq $max_attempts ]; then
  echo "❌ Timeout waiting for Cassandra to be ready"
  exit 1
fi

# Additional wait for schema to stabilize
echo "⏳ Waiting for schema stabilization (5 seconds)..."
sleep 5

echo "🚀 Starting backend service..."
exec $cmd
