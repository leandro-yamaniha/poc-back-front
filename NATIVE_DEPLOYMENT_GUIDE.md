# 🚀 Native AOT Deployment Guide

## 📋 **Quick Start**

### **1. Build Native Executable**
```bash
cd backend
./scripts/build-aot-native.sh
```
**Time**: ~3 minutes  
**Output**: `backend/java-reactive/target/beauty-salon-reactive` (133MB)

### **2. Build Docker Images**
```bash
cd backend
./scripts/build-docker-native.sh
```
**Time**: ~30 seconds  
**Output**: 
- `beauty-salon-reactive:native-alpine` (145MB)
- `beauty-salon-reactive:native-distroless` (140MB)

### **3. Run with Docker Compose**
```bash
# Interactive menu
./scripts/run-native-aot.sh

# Or directly
docker-compose -f docker-compose.aot-native.yml up -d
```

---

## 🎯 **Deployment Modes**

### **Production Mode (Distroless)**
```bash
docker-compose -f docker-compose.aot-native.yml up -d cassandra api-native-distroless
```

**Features**:
- ✅ Ultra-secure (no shell)
- ✅ Minimal attack surface
- ✅ Port: 8085
- ✅ Memory: 128-256MB
- ✅ Startup: <1 second

### **Development Mode (Alpine)**
```bash
docker-compose -f docker-compose.aot-native.yml --profile debug up -d
```

**Features**:
- ✅ Shell access for debugging
- ✅ Port: 8086
- ✅ Memory: 128-256MB
- ✅ Startup: <1 second

---

## 📊 **Resource Requirements**

### **Minimum**
- **CPU**: 0.25 cores
- **Memory**: 128MB
- **Disk**: 500MB

### **Recommended**
- **CPU**: 0.5 cores
- **Memory**: 256MB
- **Disk**: 1GB

### **Cassandra**
- **CPU**: 0.5-1.0 cores
- **Memory**: 512MB-1GB
- **Disk**: 5GB+

---

## 🔧 **Configuration**

### **Environment Variables**

```yaml
# Spring Boot
SPRING_PROFILES_ACTIVE: docker

# Cassandra
SPRING_DATA_CASSANDRA_CONTACT_POINTS: cassandra
SPRING_DATA_CASSANDRA_PORT: 9042
SPRING_DATA_CASSANDRA_KEYSPACE_NAME: beauty_salon
SPRING_DATA_CASSANDRA_LOCAL_DATACENTER: datacenter1

# Server
SERVER_PORT: 8085
SERVER_COMPRESSION_ENABLED: true

# Logging
LOGGING_LEVEL_ROOT: INFO
LOGGING_LEVEL_COM_BEAUTYSALON: DEBUG
```

---

## 🌐 **Endpoints**

### **Production (Distroless)**
- **API**: http://localhost:8085
- **Health**: http://localhost:8085/actuator/health
- **Swagger**: http://localhost:8085/swagger-ui/index.html

### **Development (Alpine)**
- **API**: http://localhost:8086
- **Health**: http://localhost:8086/actuator/health
- **Swagger**: http://localhost:8086/swagger-ui/index.html

### **Cassandra**
- **Host**: localhost
- **Port**: 9042
- **Keyspace**: beauty_salon

---

## 📝 **Common Commands**

### **Start Services**
```bash
# Production only
docker-compose -f docker-compose.aot-native.yml up -d cassandra api-native-distroless

# Development only
docker-compose -f docker-compose.aot-native.yml --profile debug up -d cassandra api-native-alpine

# Both
docker-compose -f docker-compose.aot-native.yml --profile debug up -d
```

### **Stop Services**
```bash
docker-compose -f docker-compose.aot-native.yml down
```

### **View Logs**
```bash
# All services
docker-compose -f docker-compose.aot-native.yml logs -f

# Specific service
docker-compose -f docker-compose.aot-native.yml logs -f api-native-distroless
docker-compose -f docker-compose.aot-native.yml logs -f cassandra
```

### **Check Status**
```bash
docker-compose -f docker-compose.aot-native.yml ps
```

### **Restart Services**
```bash
docker-compose -f docker-compose.aot-native.yml restart
```

### **Clean Up**
```bash
# Stop and remove containers
docker-compose -f docker-compose.aot-native.yml down

# Stop, remove containers and volumes
docker-compose -f docker-compose.aot-native.yml down -v
```

---

## 🐛 **Debugging**

### **Access Alpine Container**
```bash
docker exec -it beauty-salon-api-native-alpine sh
```

### **Check Cassandra**
```bash
docker exec -it beauty-salon-cassandra-aot cqlsh
```

### **View Resource Usage**
```bash
docker stats
```

### **Inspect Container**
```bash
docker inspect beauty-salon-api-native-distroless
```

---

## 🔍 **Health Checks**

### **API Health**
```bash
curl http://localhost:8085/actuator/health
```

**Expected Response**:
```json
{
  "status": "UP",
  "components": {
    "cassandra": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### **Cassandra Health**
```bash
docker exec beauty-salon-cassandra-aot cqlsh -e "describe cluster"
```

---

## 📈 **Performance Monitoring**

### **Metrics Endpoint**
```bash
curl http://localhost:8085/actuator/metrics
```

### **Prometheus Metrics**
```bash
curl http://localhost:8085/actuator/prometheus
```

### **Resource Usage**
```bash
docker stats beauty-salon-api-native-distroless
```

---

## 🚨 **Troubleshooting**

### **Issue: Container won't start**
```bash
# Check logs
docker logs beauty-salon-api-native-distroless

# Check Cassandra health
docker logs beauty-salon-cassandra-aot
```

### **Issue: Can't connect to Cassandra**
```bash
# Verify Cassandra is running
docker ps | grep cassandra

# Check network
docker network inspect beauty_salon_aot_network

# Wait for Cassandra to be ready (takes ~60s)
docker-compose -f docker-compose.aot-native.yml logs -f cassandra
```

### **Issue: High memory usage**
```bash
# Check current usage
docker stats

# Adjust limits in docker-compose.aot-native.yml
deploy:
  resources:
    limits:
      memory: 256M  # Adjust this
```

---

## 🎯 **Production Checklist**

- [ ] Native executable built successfully
- [ ] Docker images created (distroless)
- [ ] Cassandra data volume configured
- [ ] Environment variables set
- [ ] Health checks passing
- [ ] Resource limits configured
- [ ] Logging configured
- [ ] Backup strategy in place
- [ ] Monitoring configured
- [ ] SSL/TLS configured (if needed)

---

## 📊 **Comparison**

| Metric | JAR + JRE | Native AOT | Improvement |
|--------|-----------|------------|-------------|
| **Image Size** | 380MB | 140MB | **63% smaller** |
| **Startup Time** | 3-5s | <1s | **5x faster** |
| **Memory Usage** | 256-384MB | 128-256MB | **40% less** |
| **CPU Usage** | Higher | Lower | **Better** |

---

## 🔐 **Security**

### **Distroless Benefits**
- ✅ No shell (can't execute commands)
- ✅ No package manager
- ✅ Minimal attack surface
- ✅ Only essential libraries
- ✅ Non-root user by default

### **Best Practices**
- ✅ Use distroless for production
- ✅ Keep images updated
- ✅ Scan for vulnerabilities
- ✅ Use secrets management
- ✅ Enable network policies

---

## 📚 **Additional Resources**

- [Spring AOT Results](./docs/SPRING_AOT_RESULTS.md)
- [Docker Native Comparison](./docs/DOCKER_NATIVE_COMPARISON.md)
- [Native Build Guide](./docs/SPRING_AOT_IMPLEMENTATION_PLAN.md)

---

## 🎉 **Success Metrics**

After deployment, you should see:
- ✅ **Startup**: <1 second
- ✅ **Memory**: 128-256MB
- ✅ **CPU**: <10% idle
- ✅ **Response Time**: <50ms
- ✅ **Throughput**: 1000+ req/s

**Your Java application now deploys like Go! 🚀**
