# Beauty Salon Management System - Deployment Guide

## Overview
This guide provides comprehensive instructions for deploying the Beauty Salon Management System with multiple backend options and a React frontend.

## Architecture Summary
- **Frontend**: React 18 (Port 3000)
- **Backend Options**:
  - Node.js Express (Port 8083) - **RECOMMENDED FOR PRODUCTION**
  - Java Spring Boot (Port 8084) - **ENTERPRISE READY**
  - Go Gin (Port 8080) - **HIGH PERFORMANCE**
  - Python FastAPI (Port 8081) - **RAPID DEVELOPMENT**
- **Database**: Apache Cassandra 4.1 (Port 9042)

## Quick Start (Docker Compose)

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ RAM available
- Ports 3000, 8080-8084, 9042 available

### 1. Clone and Start
```bash
git clone <repository-url>
cd beauty-salon-app
docker-compose up -d
```

### 2. Verify Services
```bash
# Check all services
docker-compose ps

# Test backends
curl http://localhost:8083/api/customers  # Node.js
curl http://localhost:8084/actuator/health # Java
curl http://localhost:8080/health         # Go
curl http://localhost:8081/api/customers/ # Python

# Access frontend
open http://localhost:3000
```

## Manual Deployment

### Database Setup (Cassandra)

#### Option 1: Docker (Recommended)
```bash
docker run -d \
  --name beauty-salon-cassandra \
  -p 9042:9042 \
  -e CASSANDRA_CLUSTER_NAME=BeautySalonCluster \
  -e CASSANDRA_DC=datacenter1 \
  cassandra:4.1
```

#### Option 2: Local Installation
1. Download Cassandra 4.1 from Apache website
2. Extract and configure `cassandra.yaml`
3. Start: `bin/cassandra -f`
4. Initialize schema: `cqlsh -f database/init/01-keyspace.cql`

### Backend Deployment

#### Node.js Backend (Recommended)
```bash
cd backend-nodejs
npm install
npm start
# Runs on port 8083
```

**Environment Variables:**
```bash
export CASSANDRA_CONTACT_POINTS=localhost
export CASSANDRA_PORT=9042
export CASSANDRA_KEYSPACE=beauty_salon
export PORT=8083
```

#### Java Spring Boot Backend
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/beauty-salon-backend-1.0.0.jar
# Runs on port 8084
```

**Environment Variables:**
```bash
export CASSANDRA_CONTACT_POINTS=localhost
export CASSANDRA_PORT=9042
export CASSANDRA_KEYSPACE=beauty_salon
export SERVER_PORT=8084
```

#### Go Backend
```bash
cd backend-go
go mod tidy
go build -o bin/beauty-salon cmd/server/main.go
./bin/beauty-salon
# Runs on port 8080
```

**Environment Variables:**
```bash
export CASSANDRA_HOSTS=localhost
export CASSANDRA_PORT=9042
export CASSANDRA_KEYSPACE=beauty_salon
export PORT=8080
```

#### Python FastAPI Backend
```bash
cd backend-python
pip install -r requirements.txt
python -m uvicorn main:app --host 0.0.0.0 --port 8081
# Runs on port 8081
```

**Environment Variables:**
```bash
export CASSANDRA_HOSTS=localhost
export CASSANDRA_PORT=9042
export CASSANDRA_KEYSPACE=beauty_salon
```

### Frontend Deployment

#### Development
```bash
cd frontend
npm install
npm start
# Runs on port 3000
```

#### Production Build
```bash
cd frontend
npm install
npm run build
# Serve build/ directory with nginx or apache
```

## Production Deployment

### Recommended Architecture
```
Load Balancer (nginx)
├── Frontend (React) - Static files
├── Backend (Node.js) - Primary API
└── Database (Cassandra) - 3-node cluster
```

### Nginx Configuration
```nginx
upstream backend {
    server localhost:8083;
    # Add more backend instances for load balancing
}

server {
    listen 80;
    server_name yourdomain.com;

    # Frontend
    location / {
        root /var/www/beauty-salon/build;
        try_files $uri $uri/ /index.html;
    }

    # API
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Environment Configuration

#### Production Environment Variables
```bash
# Database
CASSANDRA_CONTACT_POINTS=cassandra1,cassandra2,cassandra3
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=beauty_salon_prod
CASSANDRA_USERNAME=beauty_salon_user
CASSANDRA_PASSWORD=secure_password

# Backend
NODE_ENV=production
PORT=8083
LOG_LEVEL=info

# Frontend
REACT_APP_API_URL=https://yourdomain.com/api
```

### SSL/TLS Configuration
```bash
# Install certbot
sudo apt install certbot python3-certbot-nginx

# Get SSL certificate
sudo certbot --nginx -d yourdomain.com

# Auto-renewal
sudo crontab -e
0 12 * * * /usr/bin/certbot renew --quiet
```

## Performance Optimization

### Backend Performance
- **Node.js**: Use PM2 for process management
- **Java**: Configure JVM heap size (-Xmx2g -Xms1g)
- **Go**: Built-in performance optimization
- **Python**: Use Gunicorn with multiple workers

### Database Optimization
```cql
-- Create additional indexes for performance
CREATE INDEX IF NOT EXISTS ON customers (phone);
CREATE INDEX IF NOT EXISTS ON appointments (appointment_date, status);
CREATE INDEX IF NOT EXISTS ON services (category, is_active);
```

### Caching Strategy
- Redis for session management
- Application-level caching for frequently accessed data
- CDN for static assets

## Monitoring and Logging

### Health Checks
```bash
# Backend health endpoints
curl http://localhost:8083/health      # Node.js
curl http://localhost:8084/actuator/health # Java
curl http://localhost:8080/health      # Go
curl http://localhost:8081/health      # Python (if implemented)
```

### Logging Configuration
- Centralized logging with ELK stack
- Application logs in JSON format
- Database query logging for performance analysis

### Monitoring Tools
- **Application**: New Relic, Datadog, or Prometheus
- **Database**: Cassandra metrics via JMX
- **Infrastructure**: CloudWatch, Grafana

## Backup and Recovery

### Database Backup
```bash
# Create snapshot
nodetool snapshot beauty_salon

# Backup to S3 (example)
aws s3 sync /var/lib/cassandra/data/beauty_salon/ s3://backup-bucket/cassandra/
```

### Application Backup
- Source code in Git repository
- Configuration files in secure storage
- Regular database schema exports

## Security Considerations

### Network Security
- Use VPC/private networks
- Restrict database access to application servers only
- Enable SSL/TLS for all communications

### Application Security
- Implement authentication (JWT recommended)
- Input validation and sanitization
- Rate limiting for API endpoints
- CORS configuration for frontend

### Database Security
- Enable authentication and authorization
- Use encrypted connections
- Regular security updates

## Troubleshooting

### Common Issues

#### Database Connection Errors
```bash
# Check Cassandra status
docker exec -it beauty-salon-cassandra nodetool status

# Test connection
docker exec -it beauty-salon-cassandra cqlsh
```

#### Backend Startup Issues
```bash
# Check logs
docker logs beauty-salon-backend

# Verify environment variables
env | grep CASSANDRA
```

#### Frontend Build Issues
```bash
# Clear cache and rebuild
rm -rf node_modules package-lock.json
npm install
npm run build
```

### Performance Issues
- Monitor CPU and memory usage
- Check database query performance
- Analyze application logs for bottlenecks

## Scaling Strategies

### Horizontal Scaling
- Multiple backend instances behind load balancer
- Cassandra cluster with 3+ nodes
- CDN for static content delivery

### Vertical Scaling
- Increase server resources (CPU, RAM)
- Optimize database configuration
- Tune JVM settings for Java backend

## Support and Maintenance

### Regular Maintenance Tasks
- Database compaction and cleanup
- Log rotation and cleanup
- Security updates for all components
- Performance monitoring and optimization

### Backup Verification
- Regular restore testing
- Data integrity checks
- Disaster recovery procedures

## Contact Information
For deployment support and questions:
- Technical Documentation: See README.md files in each component
- Performance Benchmarks: See PERFORMANCE_TEST_RESULTS.md
- Testing Guide: See TEST_PROFILES_GUIDE.md
