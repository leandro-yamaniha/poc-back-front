# 🛠️ Beauty Salon Management System - Installation & Setup Guide

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Core Technologies](#core-technologies)
3. [Development Tools](#development-tools)
4. [Installation Steps](#installation-steps)
5. [Configuration](#configuration)
6. [Running the Application](#running-the-application)
7. [Testing](#testing)
8. [Deployment](#deployment)
9. [Troubleshooting](#troubleshooting)

---

## 🖥️ System Requirements

### **Minimum Requirements**
- **OS**: macOS 10.15+, Ubuntu 18.04+, Windows 10+
- **RAM**: 8GB (16GB recommended)
- **Storage**: 10GB free space
- **CPU**: 4 cores (8 cores recommended)
- **Network**: Internet connection for dependencies

### **Recommended Development Environment**
- **OS**: macOS Monterey+ or Ubuntu 20.04+
- **RAM**: 16GB+
- **Storage**: 20GB+ SSD
- **CPU**: 8+ cores
- **IDE**: VS Code, IntelliJ IDEA, or WebStorm

---

## 🚀 Core Technologies

### **Frontend Stack**
| Technology | Version | Purpose | Installation |
|------------|---------|---------|--------------|
| **Node.js** | 18.x+ | JavaScript runtime | `brew install node` |
| **React** | 18.2.0 | UI framework | `npx create-react-app` |
| **React Router** | 6.x | Client-side routing | `npm install react-router-dom` |
| **Axios** | 1.x | HTTP client | `npm install axios` |
| **React Testing Library** | 13.x | Testing framework | `npm install @testing-library/react` |
| **Jest** | 29.x | Test runner | Included with React |

### **Backend Stack (Reactive Java)**
| Technology | Version | Purpose | Installation |
|------------|---------|---------|--------------|
| **Java** | 21 LTS | Programming language | `brew install openjdk@21` |
| **Spring Boot** | 3.5.4 | Application framework | Maven dependency |
| **Spring WebFlux** | 6.x | Reactive web framework | Maven dependency |
| **Undertow** | 2.x | Web server | Maven dependency |
| **Project Reactor** | 3.x | Reactive programming | Maven dependency |
| **Maven** | 3.9+ | Build tool | `brew install maven` |

### **Backend Stack (Node.js)**
| Technology | Version | Purpose | Installation |
|------------|---------|---------|--------------|
| **Express.js** | 4.x | Web framework | `npm install express` |
| **Node-cache** | 5.x | In-memory caching | `npm install node-cache` |
| **Swagger UI** | 4.x | API documentation | `npm install swagger-ui-express` |
| **Jest** | 29.x | Testing framework | `npm install jest` |
| **ESLint** | 8.x | Code linting | `npm install eslint` |

### **Database**
| Technology | Version | Purpose | Installation |
|------------|---------|---------|--------------|
| **Apache Cassandra** | 4.1+ | NoSQL database | `brew install cassandra` |
| **Docker** | 20.x+ | Containerization | `brew install docker` |
| **Docker Compose** | 2.x+ | Multi-container orchestration | Included with Docker |

---

## 🛠️ Development Tools

### **Essential Tools**
| Tool | Purpose | Installation | Configuration |
|------|---------|--------------|---------------|
| **Git** | Version control | `brew install git` | `git config --global user.name "Your Name"` |
| **VS Code** | Code editor | Download from website | Install extensions |
| **Homebrew** (macOS) | Package manager | `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"` | - |
| **curl** | HTTP testing | `brew install curl` | - |
| **jq** | JSON processing | `brew install jq` | - |

### **Performance Testing Tools**
| Tool | Purpose | Installation | Usage |
|------|---------|--------------|-------|
| **wrk** | HTTP benchmarking | `brew install wrk` | `wrk -t4 -c100 -d30s http://localhost:8085/api/customers` |
| **Apache Bench** | Load testing | `brew install httpie` | Alternative to wrk |
| **bc** | Calculator for scripts | `brew install bc` | Used in stress test scripts |

### **VS Code Extensions**
```bash
# Essential extensions
code --install-extension ms-vscode.vscode-java-pack
code --install-extension vscjava.vscode-spring-boot-dashboard
code --install-extension ms-vscode.vscode-typescript-next
code --install-extension bradlc.vscode-tailwindcss
code --install-extension esbenp.prettier-vscode
code --install-extension ms-vscode.vscode-eslint
code --install-extension ms-azuretools.vscode-docker
```

---

## 📦 Installation Steps

### **Step 1: Install Core Dependencies**

#### **macOS Installation**
```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install core tools
brew install node@18 openjdk@21 maven git docker curl jq wrk bc

# Set Java environment
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@21' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# Verify installations
node --version    # Should show v18.x.x
java --version    # Should show Java 21
mvn --version     # Should show Maven 3.9+
docker --version  # Should show Docker 20.x+
```

#### **Ubuntu Installation**
```bash
# Update package manager
sudo apt update && sudo apt upgrade -y

# Install Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install Java 21
sudo apt install openjdk-21-jdk

# Install Maven
sudo apt install maven

# Install Docker
sudo apt install docker.io docker-compose

# Install other tools
sudo apt install git curl jq

# Install wrk
sudo apt install wrk

# Verify installations
node --version
java --version
mvn --version
docker --version
```

### **Step 2: Clone and Setup Project**

```bash
# Clone the repository
git clone https://github.com/leandro-yamaniha/poc-back-front.git
cd poc-back-front

# Verify project structure
ls -la
# Should show: frontend/, backend-java-reactive/, backend-nodejs/, database/, scripts/
```

### **Step 3: Setup Frontend**

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Verify installation
npm list --depth=0

# Expected dependencies:
# ├── react@18.2.0
# ├── react-router-dom@6.x
# ├── axios@1.x
# ├── @testing-library/react@13.x
# └── ... (other dependencies)
```

### **Step 4: Setup Backend (Reactive Java)**

```bash
# Navigate to reactive backend
cd ../backend-java-reactive

# Verify Maven configuration
mvn --version

# Download dependencies (optional - will happen on first run)
mvn dependency:resolve

# Verify Spring Boot version
grep -A 5 "<parent>" pom.xml
# Should show Spring Boot 3.5.4
```

### **Step 5: Setup Database (Cassandra)**

```bash
# Return to project root
cd ..

# Start Cassandra with Docker
docker-compose up -d cassandra

# Wait for Cassandra to start (30-60 seconds)
docker-compose logs cassandra

# Verify Cassandra is running
docker ps | grep cassandra
# Should show running container on port 9042

# Initialize database
./scripts/connect-cassandra.sh
```

---

## ⚙️ Configuration

### **Environment Variables**

#### **Frontend (.env)**
```bash
# Create frontend/.env
cd frontend
cat > .env << EOF
REACT_APP_API_URL=http://localhost:8085
REACT_APP_NODE_API_URL=http://localhost:3001
REACT_APP_ENVIRONMENT=development
EOF
```

#### **Backend Reactive (application.yml)**
```yaml
# backend-java-reactive/src/main/resources/application.yml
server:
  port: 8085
  undertow:
    threads:
      io: 16
      worker: 256

spring:
  profiles:
    active: dev
  cassandra:
    keyspace-name: beauty_salon
    contact-points: localhost
    port: 9042
    local-datacenter: datacenter1
    schema-action: create_if_not_exists

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

#### **Backend Node.js (.env)**
```bash
# Create backend-nodejs/.env
cd ../backend-nodejs
cat > .env << EOF
PORT=3001
CASSANDRA_HOSTS=localhost
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=beauty_salon
CASSANDRA_DATACENTER=datacenter1
NODE_ENV=development
EOF
```

### **Database Configuration**

#### **Cassandra Keyspace Setup**
```bash
# Connect to Cassandra
docker exec -it beauty-salon-cassandra cqlsh

# Create keyspace and tables
SOURCE '/docker-entrypoint-initdb.d/01-keyspace.cql';
SOURCE '/docker-entrypoint-initdb.d/02-tables.cql';
SOURCE '/docker-entrypoint-initdb.d/03-sample-data.cql';

# Verify setup
USE beauty_salon;
DESCRIBE TABLES;
SELECT COUNT(*) FROM customers;
```

---

## 🏃‍♂️ Running the Application

### **Development Mode (All Services)**

#### **Option 1: Docker Compose (Recommended)**
```bash
# Start all services
docker-compose up -d

# Services will be available at:
# - Frontend: http://localhost:3000
# - Reactive Backend: http://localhost:8085
# - Node.js Backend: http://localhost:3001
# - Cassandra: localhost:9042

# Check status
docker-compose ps
```

#### **Option 2: Manual Startup**
```bash
# Terminal 1: Start Cassandra
docker-compose up -d cassandra

# Terminal 2: Start Reactive Backend
cd backend-java-reactive
./mvnw spring-boot:run

# Terminal 3: Start Node.js Backend
cd backend-nodejs
npm start

# Terminal 4: Start Frontend
cd frontend
npm start
```

### **Production Mode**

#### **Build for Production**
```bash
# Build frontend
cd frontend
npm run build

# Build reactive backend
cd ../backend-java-reactive
./mvnw clean package -DskipTests

# Build Node.js backend
cd ../backend-nodejs
npm run build  # if build script exists
```

#### **Production Docker Deployment**
```bash
# Build and start production containers
docker-compose -f docker-compose.yml up -d --build

# Scale services
docker-compose up -d --scale backend-java-reactive=3
```

---

## 🧪 Testing

### **Frontend Testing**
```bash
cd frontend

# Run all tests
npm test

# Run tests with coverage
npm run test:coverage

# Run E2E tests (if Cypress is configured)
npm run test:e2e

# Expected: 231/231 tests passing (100%)
```

### **Backend Testing (Reactive)**
```bash
cd backend-java-reactive

# Run unit tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Expected: 190/190 tests passing (100%)
```

### **Performance Testing**
```bash
# Run stress test on reactive backend
./scripts/stress-test-reactive.sh

# Run simple stress test
./scripts/simple-stress-test.sh

# Expected: 30,000+ RPS performance
```

### **Integration Testing**
```bash
# Test all endpoints
curl http://localhost:8085/actuator/health
curl http://localhost:8085/api/customers
curl http://localhost:8085/swagger-ui/index.html

# Test Node.js backend
curl http://localhost:3001/health
curl http://localhost:3001/api/customers
```

---

## 🚀 Deployment

### **Local Development Deployment**
```bash
# Quick start for development
./scripts/start-reactive-stack.sh
```

### **Production Deployment Options**

#### **Docker Swarm**
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml beauty-salon

# Scale services
docker service scale beauty-salon_backend-java-reactive=3
```

#### **Kubernetes**
```bash
# Create namespace
kubectl create namespace beauty-salon

# Apply configurations (if k8s manifests exist)
kubectl apply -f k8s/ -n beauty-salon

# Check deployment
kubectl get pods -n beauty-salon
```

#### **Cloud Deployment (AWS/GCP/Azure)**
```bash
# Build and push Docker images
docker build -t beauty-salon-frontend ./frontend
docker build -t beauty-salon-backend ./backend-java-reactive

# Push to container registry
docker tag beauty-salon-frontend your-registry/beauty-salon-frontend
docker push your-registry/beauty-salon-frontend
```

---

## 🔧 Troubleshooting

### **Common Issues**

#### **Port Conflicts**
```bash
# Check what's using port 8085
lsof -i :8085

# Kill process if needed
kill -9 <PID>

# Change port in application.yml if needed
```

#### **Java Version Issues**
```bash
# Check Java version
java --version

# Set correct JAVA_HOME
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH=$JAVA_HOME/bin:$PATH

# Verify Maven uses correct Java
mvn --version
```

#### **Cassandra Connection Issues**
```bash
# Check Cassandra status
docker-compose logs cassandra

# Restart Cassandra
docker-compose restart cassandra

# Wait for startup (check logs)
docker-compose logs -f cassandra
```

#### **Node.js Issues**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Check Node version
node --version  # Should be 18.x+
```

#### **Docker Issues**
```bash
# Check Docker status
docker info

# Restart Docker service
sudo systemctl restart docker  # Linux
# or restart Docker Desktop on macOS/Windows

# Clean Docker system
docker system prune -a
```

### **Performance Issues**

#### **Frontend Slow Loading**
```bash
# Check bundle size
npm run build
npm install -g serve
serve -s build

# Analyze bundle
npm install --save-dev webpack-bundle-analyzer
npm run build && npx webpack-bundle-analyzer build/static/js/*.js
```

#### **Backend Memory Issues**
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx2g -Xms1g"
./mvnw spring-boot:run

# Monitor memory usage
docker stats
```

### **Testing Issues**

#### **Tests Failing**
```bash
# Clear test cache
npm test -- --clearCache  # Frontend
./mvnw clean test  # Backend

# Run tests in verbose mode
npm test -- --verbose
./mvnw test -X
```

#### **Coverage Issues**
```bash
# Generate fresh coverage report
npm run test:coverage -- --watchAll=false
./mvnw clean test jacoco:report
```

---

## 📚 Additional Resources

### **Documentation Links**
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [React Documentation](https://reactjs.org/docs/getting-started.html)
- [Cassandra Documentation](https://cassandra.apache.org/doc/latest/)
- [Docker Documentation](https://docs.docker.com/)

### **Project-Specific Guides**
- `README.md` - Project overview
- `REACTIVE_BACKEND_SUCCESS.md` - Backend testing achievements
- `PERFORMANCE_TEST_RESULTS.md` - Performance benchmarks
- `DEPLOYMENT_GUIDE.md` - Deployment instructions

### **Support**
- **Issues**: Create GitHub issues for bugs
- **Discussions**: Use GitHub discussions for questions
- **Documentation**: Check project wiki for detailed guides

---

**Installation Guide Complete** - Beauty Salon Management System setup with enterprise-grade development environment
