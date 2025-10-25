# 🐳 Docker Native Images - Comparison Guide

## 📋 **Overview**

Three Dockerfile options for GraalVM Native executable, optimized for minimal size and maximum security - similar to Go's deployment approach.

---

## 🎯 **Available Options**

### **1. Alpine Linux** (`Dockerfile.native-minimal`)
- **Base**: `alpine:3.19`
- **Size**: ~145-150MB
- **Pros**: Shell access, debugging tools available
- **Cons**: Slightly larger
- **Use Case**: Development, debugging, troubleshooting

### **2. Google Distroless** (`Dockerfile.native-distroless`)
- **Base**: `gcr.io/distroless/base-debian12:nonroot`
- **Size**: ~140-145MB
- **Pros**: Ultra-secure, no shell, minimal attack surface
- **Cons**: No debugging tools, harder to troubleshoot
- **Use Case**: Production, security-critical deployments

### **3. Full Build** (`Dockerfile.native`)
- **Base**: Multi-stage with GraalVM builder + Ubuntu runtime
- **Size**: ~200MB+
- **Pros**: Complete build pipeline in Docker
- **Cons**: Larger size, longer build time
- **Use Case**: CI/CD pipelines, automated builds

---

## 📊 **Size Comparison**

| Image Type | Base Image | Executable | Total Size | vs JAR |
|------------|------------|------------|------------|--------|
| **JAR + JRE** | eclipse-temurin:21-jre | 46MB | ~380MB | 100% |
| **Native + Alpine** | alpine:3.19 | 133MB | ~145MB | **38%** |
| **Native + Distroless** | distroless/base | 133MB | ~140MB | **37%** |
| **Go Equivalent** | alpine/scratch | varies | ~10-50MB | - |

---

## 🚀 **Quick Start**

### **Build Native Executable First**
```bash
cd backend
./scripts/build-aot-native.sh
```

### **Build Docker Images**
```bash
cd backend
./scripts/build-docker-native.sh
```

This will create:
- `beauty-salon-reactive:native-alpine`
- `beauty-salon-reactive:native-distroless`

---

## 🔧 **Usage Examples**

### **Alpine Version (Development)**
```bash
# Run with shell access
docker run -it -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra \
  beauty-salon-reactive:native-alpine

# Debug inside container
docker run -it beauty-salon-reactive:native-alpine sh
```

### **Distroless Version (Production)**
```bash
# Run in production
docker run -d -p 8085:8085 \
  --name beauty-salon-api \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra \
  -e SPRING_DATA_CASSANDRA_KEYSPACE_NAME=beauty_salon \
  --restart unless-stopped \
  beauty-salon-reactive:native-distroless

# View logs
docker logs -f beauty-salon-api
```

### **Docker Compose Integration**
```yaml
version: '3.8'

services:
  api-native:
    image: beauty-salon-reactive:native-distroless
    ports:
      - "8085:8085"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATA_CASSANDRA_CONTACT_POINTS: cassandra
      SPRING_DATA_CASSANDRA_KEYSPACE_NAME: beauty_salon
      SPRING_DATA_CASSANDRA_LOCAL_DATACENTER: datacenter1
    depends_on:
      - cassandra
    restart: unless-stopped
    
  cassandra:
    image: cassandra:5.0
    ports:
      - "9042:9042"
    volumes:
      - cassandra_data:/var/lib/cassandra

volumes:
  cassandra_data:
```

---

## 🔐 **Security Comparison**

| Feature | Alpine | Distroless | Full Build |
|---------|--------|------------|------------|
| **Shell Access** | ✅ Yes | ❌ No | ✅ Yes |
| **Package Manager** | ✅ apk | ❌ None | ✅ apt |
| **Attack Surface** | Medium | **Minimal** | Large |
| **CVE Exposure** | Low | **Very Low** | Medium |
| **Debugging** | ✅ Easy | ❌ Hard | ✅ Easy |
| **Production Ready** | ✅ Yes | ✅ **Best** | ⚠️ OK |

---

## 📈 **Performance Comparison**

| Metric | Alpine | Distroless | Difference |
|--------|--------|------------|------------|
| **Startup Time** | <1s | <1s | Same |
| **Memory Usage** | 128-256MB | 128-256MB | Same |
| **Image Pull** | ~5-10s | ~5-10s | Same |
| **Build Time** | ~30s | ~30s | Same |

---

## 💡 **Best Practices**

### **For Development**
```dockerfile
# Use Alpine for debugging
FROM alpine:3.19
# ... includes shell, wget, etc.
```

**Benefits**:
- Shell access for debugging
- Can install additional tools
- Easier troubleshooting

### **For Production**
```dockerfile
# Use Distroless for security
FROM gcr.io/distroless/base-debian12:nonroot
# ... minimal, no shell
```

**Benefits**:
- Minimal attack surface
- No unnecessary packages
- Compliant with security standards

---

## 🎯 **Recommendations**

### **Use Alpine When:**
- ✅ Development environment
- ✅ Need debugging capabilities
- ✅ Want to install additional tools
- ✅ Troubleshooting issues

### **Use Distroless When:**
- ✅ Production deployment
- ✅ Security is critical
- ✅ Compliance requirements
- ✅ Minimal attack surface needed
- ✅ No debugging required

### **Use Full Build When:**
- ✅ CI/CD pipeline
- ✅ Automated builds
- ✅ No pre-built executable
- ✅ Complete build traceability

---

## 🔍 **Inspection Commands**

### **Check Image Size**
```bash
docker images | grep beauty-salon-reactive
```

### **Inspect Image Layers**
```bash
docker history beauty-salon-reactive:native-alpine
docker history beauty-salon-reactive:native-distroless
```

### **Check Image Vulnerabilities**
```bash
docker scan beauty-salon-reactive:native-alpine
docker scan beauty-salon-reactive:native-distroless
```

### **Compare with Go**
```bash
# Go binary size
ls -lh /path/to/go-binary

# Docker image size
docker images | grep go-app
docker images | grep beauty-salon-reactive
```

---

## 📦 **Image Contents**

### **Alpine Image Includes:**
- Alpine Linux 3.19 base (~5MB)
- libstdc++ (C++ standard library)
- zlib (compression library)
- ca-certificates (SSL certificates)
- tzdata (timezone data)
- Native executable (133MB)
- **Total: ~145MB**

### **Distroless Image Includes:**
- Minimal Debian base (~2MB)
- Essential C libraries only
- ca-certificates
- Native executable (133MB)
- **Total: ~140MB**

---

## 🚀 **Deployment Strategies**

### **Kubernetes Deployment**
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
        image: beauty-salon-reactive:native-distroless
        ports:
        - containerPort: 8085
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8085
          initialDelaySeconds: 5
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8085
          initialDelaySeconds: 3
          periodSeconds: 5
```

---

## 🎉 **Conclusion**

### **Size Achievement**
- ✅ **62% smaller** than JAR + JRE (380MB → 140MB)
- ✅ **Similar to Go** deployment approach
- ✅ **Production-ready** with minimal footprint

### **Security Achievement**
- ✅ **Minimal attack surface** with Distroless
- ✅ **No shell** in production
- ✅ **Non-root user** by default

### **Performance Achievement**
- ✅ **Instant startup** (<1 second)
- ✅ **Low memory** (128-256MB)
- ✅ **Fast deployment** (small image size)

**The Java Spring Boot Native backend now deploys like Go with enterprise-grade features!** 🚀🐳
