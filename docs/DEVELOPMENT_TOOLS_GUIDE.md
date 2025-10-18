# 🔧 Beauty Salon Management System - Development Tools & Environment Setup

## 🎯 Quick Start Checklist

- [ ] Install core tools (Node.js, Java, Maven, Docker)
- [ ] Setup IDE with extensions
- [ ] Configure environment variables
- [ ] Start database (Cassandra)
- [ ] Run application stack
- [ ] Verify all services are running

---

## 🛠️ Essential Development Tools

### **Package Managers**

#### **macOS - Homebrew**
```bash
# Install Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Essential packages
brew install node@18 openjdk@21 maven git docker curl jq wrk bc
```

#### **Ubuntu - APT**
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install essential packages
sudo apt install nodejs npm openjdk-21-jdk maven git docker.io docker-compose curl jq wrk bc
```

#### **Windows - Chocolatey**
```powershell
# Install Chocolatey
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install packages
choco install nodejs openjdk maven git docker-desktop curl jq
```

---

## 💻 IDE Configuration

### **Visual Studio Code Setup**

#### **Essential Extensions**
```bash
# Java development
code --install-extension vscjava.vscode-java-pack
code --install-extension vscjava.vscode-spring-boot-dashboard
code --install-extension vscjava.vscode-maven

# React/JavaScript development
code --install-extension ms-vscode.vscode-typescript-next
code --install-extension bradlc.vscode-tailwindcss
code --install-extension esbenp.prettier-vscode
code --install-extension ms-vscode.vscode-eslint

# General development
code --install-extension ms-azuretools.vscode-docker
code --install-extension ms-vscode.vscode-json
code --install-extension redhat.vscode-yaml
code --install-extension ms-vscode.vscode-markdown
```

#### **VS Code Settings (settings.json)**
```json
{
  "java.home": "/opt/homebrew/opt/openjdk@21",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/opt/homebrew/opt/openjdk@21"
    }
  ],
  "spring-boot.ls.java.home": "/opt/homebrew/opt/openjdk@21",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  },
  "prettier.requireConfig": true,
  "eslint.autoFixOnSave": true,
  "docker.showStartPage": false
}
```

### **IntelliJ IDEA Setup**

#### **Required Plugins**
- Spring Boot
- Docker
- Database Tools and SQL
- Node.js
- React
- Markdown

#### **Project Configuration**
```bash
# Set Project SDK to Java 21
# Configure Maven settings
# Enable annotation processing
# Set code style to Google Java Style
```

---

## 🔧 Environment Configuration

### **Java Environment**

#### **JAVA_HOME Setup**
```bash
# macOS (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH=$JAVA_HOME/bin:$PATH

# Ubuntu (add to ~/.bashrc)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Windows (System Environment Variables)
JAVA_HOME=C:\Program Files\OpenJDK\openjdk-21
PATH=%JAVA_HOME%\bin;%PATH%
```

#### **Maven Configuration**
```bash
# Check Maven uses correct Java
mvn --version

# Configure Maven memory (add to ~/.mavenrc)
export MAVEN_OPTS="-Xmx2g -Xms1g -XX:ReservedCodeCacheSize=512m"
```

### **Node.js Environment**

#### **Node Version Management**
```bash
# Install nvm (Node Version Manager)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Install and use Node 18
nvm install 18
nvm use 18
nvm alias default 18

# Verify installation
node --version  # Should show v18.x.x
npm --version   # Should show 9.x.x+
```

#### **Global NPM Packages**
```bash
# Useful global packages
npm install -g @angular/cli create-react-app serve http-server nodemon
```

---

## 🐳 Docker Configuration

### **Docker Desktop Setup**

#### **Installation**
- **macOS**: Download from [Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Ubuntu**: `sudo apt install docker.io docker-compose`
- **Windows**: Download Docker Desktop for Windows

#### **Docker Configuration**
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
newgrp docker

# Configure Docker resources
# - Memory: 8GB minimum (16GB recommended)
# - CPU: 4 cores minimum (8 cores recommended)
# - Disk: 50GB minimum
```

#### **Docker Compose Version**
```bash
# Verify Docker Compose v2
docker compose version

# If using old version, upgrade
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

---

## 📊 Database Tools

### **Cassandra Management**

#### **CQL Shell Access**
```bash
# Connect via Docker
docker exec -it beauty-salon-cassandra cqlsh

# Connect directly (if Cassandra installed locally)
cqlsh localhost 9042
```

#### **Database GUI Tools**
```bash
# DataStax DevCenter (Recommended)
# Download from DataStax website

# DBeaver (Universal)
brew install --cask dbeaver-community

# Cassandra Web (Web-based)
docker run -d -p 3000:3000 --name cassandra-web \
  -e CASSANDRA_HOST=localhost \
  -e CASSANDRA_PORT=9042 \
  delermando/cassandra-web
```

#### **Database Scripts**
```bash
# Initialize database
./scripts/connect-cassandra.sh

# Reset database
docker-compose down -v
docker-compose up -d cassandra
sleep 30
./scripts/connect-cassandra.sh
```

---

## 🧪 Testing Tools

### **Frontend Testing**

#### **Jest Configuration**
```json
// frontend/package.json
{
  "scripts": {
    "test": "react-scripts test",
    "test:coverage": "react-scripts test --coverage --watchAll=false",
    "test:ci": "CI=true react-scripts test --coverage --watchAll=false"
  },
  "jest": {
    "collectCoverageFrom": [
      "src/**/*.{js,jsx}",
      "!src/index.js",
      "!src/reportWebVitals.js"
    ]
  }
}
```

#### **Cypress E2E Testing**
```bash
# Install Cypress
cd frontend
npm install --save-dev cypress

# Open Cypress
npx cypress open

# Run Cypress tests
npx cypress run
```

### **Backend Testing**

#### **Maven Test Configuration**
```xml
<!-- backend-java-reactive/pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

#### **JaCoCo Coverage**
```bash
# Generate coverage report
cd backend-java-reactive
./mvnw test jacoco:report

# View report
open target/site/jacoco/index.html
```

---

## 🚀 Performance Tools

### **Load Testing**

#### **wrk (HTTP Benchmarking)**
```bash
# Install wrk
brew install wrk  # macOS
sudo apt install wrk  # Ubuntu

# Basic usage
wrk -t4 -c100 -d30s http://localhost:8085/api/customers

# Advanced usage with script
wrk -t4 -c100 -d30s -s script.lua http://localhost:8085/api/customers
```

#### **Apache Bench (Alternative)**
```bash
# Install (if available)
brew install httpie  # macOS (includes ab alternative)

# Usage
ab -n 1000 -c 10 http://localhost:8085/api/customers
```

### **Monitoring Tools**

#### **Application Monitoring**
```bash
# Spring Boot Actuator endpoints
curl http://localhost:8085/actuator/health
curl http://localhost:8085/actuator/metrics
curl http://localhost:8085/actuator/info

# Node.js monitoring
npm install --save express-status-monitor
```

#### **System Monitoring**
```bash
# Docker stats
docker stats

# System resources
htop  # Install: brew install htop
iostat 1  # I/O statistics
```

---

## 🔍 Debugging Tools

### **Application Debugging**

#### **Java Debugging**
```bash
# Enable remote debugging
export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
./mvnw spring-boot:run

# Connect debugger to port 5005
```

#### **Node.js Debugging**
```bash
# Start with debugging
node --inspect-brk=0.0.0.0:9229 app.js

# Or with nodemon
nodemon --inspect app.js
```

### **Network Debugging**

#### **HTTP Debugging**
```bash
# curl with verbose output
curl -v http://localhost:8085/api/customers

# HTTPie (user-friendly)
http GET localhost:8085/api/customers

# Postman collections
# Import API collections for testing
```

#### **Docker Network Debugging**
```bash
# Inspect Docker networks
docker network ls
docker network inspect beauty-salon-app_beauty-salon-network

# Check container connectivity
docker exec -it beauty-salon-backend ping beauty-salon-cassandra
```

---

## 📝 Code Quality Tools

### **Linting and Formatting**

#### **Frontend (ESLint + Prettier)**
```bash
cd frontend

# Install ESLint and Prettier
npm install --save-dev eslint prettier eslint-config-prettier eslint-plugin-prettier

# ESLint configuration (.eslintrc.json)
{
  "extends": ["react-app", "react-app/jest", "prettier"],
  "plugins": ["prettier"],
  "rules": {
    "prettier/prettier": "error"
  }
}

# Prettier configuration (.prettierrc)
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2
}
```

#### **Backend (Checkstyle + SpotBugs)**
```xml
<!-- backend-java-reactive/pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
    </configuration>
</plugin>
```

### **Git Hooks**

#### **Pre-commit Hooks**
```bash
# Install husky
npm install --save-dev husky

# Setup pre-commit hook
npx husky add .husky/pre-commit "npm run lint && npm run test:ci"

# Setup commit message hook
npx husky add .husky/commit-msg "npx commitlint --edit $1"
```

---

## 🔧 Troubleshooting Common Issues

### **Port Conflicts**
```bash
# Find process using port
lsof -i :8085  # macOS/Linux
netstat -ano | findstr :8085  # Windows

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

### **Memory Issues**
```bash
# Increase Node.js memory
export NODE_OPTIONS="--max-old-space-size=4096"

# Increase JVM memory
export MAVEN_OPTS="-Xmx4g -Xms2g"
```

### **Docker Issues**
```bash
# Clean Docker system
docker system prune -a

# Reset Docker Desktop
# macOS: Docker Desktop -> Troubleshoot -> Reset to factory defaults
# Windows: Docker Desktop -> Settings -> Reset to factory defaults
```

---

## 📚 Development Workflow

### **Daily Development Routine**
```bash
# 1. Start development environment
docker-compose up -d cassandra
cd backend-java-reactive && ./mvnw spring-boot:run &
cd frontend && npm start &

# 2. Run tests before committing
npm run test:ci  # Frontend
./mvnw test  # Backend

# 3. Check code quality
npm run lint  # Frontend
./mvnw checkstyle:check  # Backend

# 4. Commit changes
git add .
git commit -m "feat: add new feature"
git push
```

### **Release Workflow**
```bash
# 1. Run full test suite
npm run test:coverage
./mvnw clean test jacoco:report

# 2. Build production artifacts
npm run build
./mvnw clean package -DskipTests

# 3. Run performance tests
./scripts/stress-test-reactive.sh

# 4. Create release
git tag v1.0.0
git push origin v1.0.0
```

---

**Development Tools Guide Complete** - Comprehensive setup for Beauty Salon Management System development environment
