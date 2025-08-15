# Beauty Salon Go Backend - Deployment Guide

This guide covers different deployment options for the Beauty Salon Go backend.

## Quick Start with Docker Compose

The easiest way to run the complete backend is using Docker Compose:

```bash
# Navigate to the backend-go directory
cd backend-go

# Start all services (Cassandra + Go API)
./scripts/start.sh

# Or manually with docker-compose
docker-compose up -d
```

The API will be available at:
- **Go API**: http://localhost:8081
- **Health Check**: http://localhost:8081/health
- **Cassandra**: localhost:9043

## Environment Configuration

### Required Environment Variables

```bash
# Server Configuration
SERVER_PORT=8080
SERVER_HOST=0.0.0.0
SERVER_READ_TIMEOUT=30
SERVER_WRITE_TIMEOUT=30
SERVER_IDLE_TIMEOUT=120

# Database Configuration
CASSANDRA_HOSTS=localhost:9042
CASSANDRA_KEYSPACE=beauty_salon_go
CASSANDRA_USERNAME=
CASSANDRA_PASSWORD=
CASSANDRA_DATACENTER=datacenter1

# Logging Configuration
LOG_LEVEL=info
LOG_FORMAT=json

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,PATCH,OPTIONS
CORS_ALLOWED_HEADERS=Origin,Content-Type,Accept,Authorization,X-Requested-With

# Health Check Configuration
HEALTH_ENABLED=true
HEALTH_ENDPOINT=/health

# Cache Configuration
CACHE_ENABLED=true
CACHE_TTL=300
```

## Deployment Options

### 1. Docker Compose (Recommended for Development)

**Advantages:**
- Complete stack (API + Database)
- Easy setup and teardown
- Isolated environment
- Automatic health checks

**Usage:**
```bash
# Start services
./scripts/start.sh

# View logs
docker-compose logs -f

# Stop services
./scripts/start.sh stop

# Clean up
./scripts/start.sh clean
```

### 2. Docker Container Only

If you have an existing Cassandra instance:

```bash
# Build the image
docker build -t beauty-salon-go .

# Run the container
docker run -d \
  --name beauty-salon-api \
  -p 8080:8080 \
  -e CASSANDRA_HOSTS=your-cassandra-host:9042 \
  -e CASSANDRA_KEYSPACE=beauty_salon \
  beauty-salon-go
```

### 3. Native Go Binary

If you have Go installed locally:

```bash
# Install dependencies
go mod download

# Build the binary
go build -o beauty-salon-api cmd/server/main.go

# Run the binary
./beauty-salon-api
```

### 4. Production Deployment

For production environments, consider:

#### Docker Swarm
```yaml
version: '3.8'
services:
  api:
    image: beauty-salon-go:latest
    deploy:
      replicas: 3
      restart_policy:
        condition: on-failure
    ports:
      - "8080:8080"
    environment:
      - CASSANDRA_HOSTS=cassandra-cluster:9042
      - LOG_LEVEL=warn
```

#### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: beauty-salon-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: beauty-salon-api
  template:
    metadata:
      labels:
        app: beauty-salon-api
    spec:
      containers:
      - name: api
        image: beauty-salon-go:latest
        ports:
        - containerPort: 8080
        env:
        - name: CASSANDRA_HOSTS
          value: "cassandra-service:9042"
        - name: LOG_LEVEL
          value: "warn"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## Database Setup

### Automatic Setup
The application automatically:
1. Creates the keyspace if it doesn't exist
2. Creates all required tables
3. Creates necessary indexes

### Manual Setup
If you prefer manual database setup:

```cql
-- Connect to Cassandra
cqlsh

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS beauty_salon_go 
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Use keyspace
USE beauty_salon_go;

-- Create tables (see pkg/database/cassandra.go for full schema)
```

## Monitoring and Health Checks

### Health Check Endpoint
```bash
curl http://localhost:8081/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "2024-01-01T12:00:00Z",
  "service": "beauty-salon-api",
  "version": "1.0.0"
}
```

### Docker Health Checks
The Docker container includes built-in health checks:

```bash
# Check container health
docker ps

# View health check logs
docker inspect beauty-salon-api | grep Health -A 10
```

## Troubleshooting

### Common Issues

1. **Cassandra Connection Failed**
   ```bash
   # Check Cassandra is running
   docker-compose ps cassandra
   
   # View Cassandra logs
   docker-compose logs cassandra
   
   # Test connection
   docker exec -it beauty-salon-cassandra-go cqlsh
   ```

2. **Port Already in Use**
   ```bash
   # Check what's using the port
   lsof -i :8081
   
   # Change port in docker-compose.yml or .env
   ```

3. **Go Build Errors**
   ```bash
   # Clean Go module cache
   go clean -modcache
   
   # Re-download dependencies
   go mod download
   ```

### Logs and Debugging

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f api
docker-compose logs -f cassandra

# Enable debug logging
# Set LOG_LEVEL=debug in .env
```

## Performance Tuning

### Cassandra Optimization
```bash
# Increase memory for Cassandra
# Add to docker-compose.yml:
environment:
  - MAX_HEAP_SIZE=2G
  - HEAP_NEWSIZE=400M
```

### Go Application Tuning
```bash
# Set Go runtime parameters
environment:
  - GOMAXPROCS=4
  - GOGC=100
```

## Security Considerations

1. **Environment Variables**: Never commit sensitive data to version control
2. **Network Security**: Use proper firewall rules in production
3. **Database Security**: Enable Cassandra authentication in production
4. **CORS**: Configure appropriate origins for production
5. **HTTPS**: Use reverse proxy (nginx/traefik) for TLS termination

## Backup and Recovery

### Database Backup
```bash
# Backup Cassandra data
docker exec beauty-salon-cassandra-go nodetool snapshot beauty_salon_go

# Export data
docker exec beauty-salon-cassandra-go cqlsh -e "COPY beauty_salon_go.customers TO '/tmp/customers.csv'"
```

### Application Backup
- Container images are stateless
- Configuration is in environment variables
- Database contains all persistent data

## Scaling

### Horizontal Scaling
- Run multiple API instances behind a load balancer
- Cassandra handles distributed data automatically
- Use container orchestration (Docker Swarm, Kubernetes)

### Vertical Scaling
- Increase container memory/CPU limits
- Tune Cassandra heap settings
- Optimize Go garbage collection settings
