# Beauty Salon Management System - Docker Compose Guide

## Overview
This guide provides instructions for running the Beauty Salon Management System using Docker Compose with different backend configurations.

## Available Configurations

### 🚀 Quick Start Options

| Configuration | Frontend Port | Backend Port | Database Port | Command |
|---------------|---------------|--------------|---------------|---------|
| **Node.js** | 3000 | 8083 | 9042 | `docker-compose -f docker-compose-nodejs.yml up -d` |
| **Java Spring Boot** | 3001 | 8084 | 9043 | `docker-compose -f docker-compose-java.yml up -d` |
| **Go** | 3002 | 8080 | 9044 | `docker-compose -f docker-compose-go.yml up -d` |
| **Python FastAPI** | 3003 | 8081 | 9045 | `docker-compose -f docker-compose-python.yml up -d` |

## Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 8GB+ RAM available (for running multiple stacks)
- Ports available as listed above

## Individual Stack Deployment

### 1. Node.js Stack (Recommended for Production)
```bash
# Start the Node.js stack
docker-compose -f docker-compose-nodejs.yml up -d

# Check services
docker-compose -f docker-compose-nodejs.yml ps

# View logs
docker-compose -f docker-compose-nodejs.yml logs -f

# Access points
Frontend: http://localhost:3000
Backend API: http://localhost:8083/api
Health Check: http://localhost:8083/health
Database: localhost:9042
```

### 2. Java Spring Boot Stack (Enterprise Ready)
```bash
# Start the Java stack
docker-compose -f docker-compose-java.yml up -d

# Check services
docker-compose -f docker-compose-java.yml ps

# View logs
docker-compose -f docker-compose-java.yml logs -f

# Access points
Frontend: http://localhost:3001
Backend API: http://localhost:8084/api
Health Check: http://localhost:8084/actuator/health
Database: localhost:9043
```

### 3. Go Stack (High Performance)
```bash
# Start the Go stack
docker-compose -f docker-compose-go.yml up -d

# Check services
docker-compose -f docker-compose-go.yml ps

# View logs
docker-compose -f docker-compose-go.yml logs -f

# Access points
Frontend: http://localhost:3002
Backend API: http://localhost:8080/api/v1
Health Check: http://localhost:8080/health
Database: localhost:9044
```

### 4. Python FastAPI Stack (Rapid Development)
```bash
# Start the Python stack
docker-compose -f docker-compose-python.yml up -d

# Check services
docker-compose -f docker-compose-python.yml ps

# View logs
docker-compose -f docker-compose-python.yml logs -f

# Access points
Frontend: http://localhost:3003
Backend API: http://localhost:8081/api
Health Check: http://localhost:8081/api/customers/
Database: localhost:9045
```

## Running Multiple Stacks Simultaneously

You can run all stacks at the same time for comparison:

```bash
# Start all stacks
docker-compose -f docker-compose-nodejs.yml up -d
docker-compose -f docker-compose-java.yml up -d
docker-compose -f docker-compose-go.yml up -d
docker-compose -f docker-compose-python.yml up -d

# Check all running containers
docker ps

# Access different frontends
Node.js Frontend: http://localhost:3000
Java Frontend: http://localhost:3001
Go Frontend: http://localhost:3002
Python Frontend: http://localhost:3003
```

## Service Management

### Starting Services
```bash
# Start specific stack
docker-compose -f docker-compose-[backend].yml up -d

# Start with build (if code changed)
docker-compose -f docker-compose-[backend].yml up -d --build

# Start and view logs
docker-compose -f docker-compose-[backend].yml up
```

### Stopping Services
```bash
# Stop specific stack
docker-compose -f docker-compose-[backend].yml down

# Stop and remove volumes (clean slate)
docker-compose -f docker-compose-[backend].yml down -v

# Stop all stacks
docker-compose -f docker-compose-nodejs.yml down
docker-compose -f docker-compose-java.yml down
docker-compose -f docker-compose-go.yml down
docker-compose -f docker-compose-python.yml down
```

### Monitoring Services
```bash
# Check service status
docker-compose -f docker-compose-[backend].yml ps

# View logs
docker-compose -f docker-compose-[backend].yml logs -f [service-name]

# Execute commands in containers
docker-compose -f docker-compose-[backend].yml exec [service-name] [command]
```

## Database Management

### Accessing Cassandra
```bash
# Node.js stack database
docker exec -it beauty-salon-cassandra-nodejs cqlsh

# Java stack database
docker exec -it beauty-salon-cassandra-java cqlsh

# Go stack database
docker exec -it beauty-salon-cassandra-go cqlsh

# Python stack database
docker exec -it beauty-salon-cassandra-python cqlsh
```

### Sample Data Verification
```sql
-- Check keyspace
DESCRIBE KEYSPACES;

-- Use beauty salon keyspace
USE beauty_salon;

-- Check tables
DESCRIBE TABLES;

-- Verify sample data
SELECT COUNT(*) FROM customers;
SELECT COUNT(*) FROM services;
SELECT COUNT(*) FROM staff;
```

## Performance Testing

### Load Testing Individual Backends
```bash
# Node.js backend
ab -n 1000 -c 10 http://localhost:8083/api/customers

# Java backend
ab -n 1000 -c 10 http://localhost:8084/api/customers

# Go backend
ab -n 1000 -c 10 http://localhost:8080/api/v1/customers

# Python backend (manual testing recommended)
curl -w "Response Time: %{time_total}s\n" http://localhost:8081/api/customers/
```

## Troubleshooting

### Common Issues

#### Port Conflicts
```bash
# Check what's using a port
lsof -ti:3000

# Kill process using port
kill $(lsof -ti:3000)
```

#### Database Connection Issues
```bash
# Check Cassandra logs
docker-compose -f docker-compose-[backend].yml logs cassandra

# Wait for Cassandra to be ready
docker-compose -f docker-compose-[backend].yml exec cassandra cqlsh -e "DESCRIBE KEYSPACES;"
```

#### Backend Health Check Failures
```bash
# Check backend logs
docker-compose -f docker-compose-[backend].yml logs backend-[type]

# Manual health check
curl http://localhost:[port]/health
```

### Resource Requirements

| Stack | CPU | RAM | Disk |
|-------|-----|-----|------|
| Node.js | 1 core | 1GB | 2GB |
| Java | 2 cores | 2GB | 3GB |
| Go | 1 core | 512MB | 1GB |
| Python | 1 core | 1GB | 2GB |
| **All Stacks** | 4+ cores | 8GB+ | 10GB+ |

## Environment Variables

### Customization Options
Each stack supports environment variable customization:

```bash
# Create .env file for specific stack
cat > .env-nodejs << EOF
NODE_ENV=production
PORT=8083
CASSANDRA_KEYSPACE=beauty_salon_prod
EOF

# Use with docker-compose
docker-compose -f docker-compose-nodejs.yml --env-file .env-nodejs up -d
```

## Production Considerations

### Security
- Change default passwords
- Enable SSL/TLS
- Configure firewalls
- Use secrets management

### Monitoring
- Add health check endpoints
- Configure logging
- Set up alerting
- Monitor resource usage

### Scaling
- Use external load balancer
- Configure Cassandra cluster
- Implement horizontal scaling
- Add caching layer

## Support

For issues or questions:
1. Check logs: `docker-compose -f docker-compose-[backend].yml logs`
2. Verify health checks: `curl http://localhost:[port]/health`
3. Review documentation: See individual README files
4. Performance benchmarks: See PERFORMANCE_TEST_RESULTS.md
