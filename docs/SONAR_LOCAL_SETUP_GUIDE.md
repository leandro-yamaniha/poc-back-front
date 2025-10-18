# 🐳 SonarQube Local Docker Setup - Beauty Salon Management System

## 🚀 Quick Start

### 1. Start SonarQube Local Instance
```bash
# Start SonarQube with PostgreSQL in Docker
./scripts/start-sonarqube-local.sh
```

### 2. Access SonarQube Dashboard
- **URL**: http://localhost:9000
- **Default Login**: admin / admin
- **⚠️ Change password on first login**

### 3. Run Code Analysis
```bash
# Analyze all projects (5 backends + frontend)
./scripts/sonar-analysis-local.sh
```

### 4. Stop SonarQube
```bash
# Stop containers
./scripts/stop-sonarqube-local.sh
```

## 📋 What's Included

### **🐳 Docker Compose Configuration**
- **SonarQube 10.3 Community** - Latest stable version
- **PostgreSQL 15** - Database for SonarQube
- **Persistent volumes** - Data survives container restarts
- **Network isolation** - Secure container communication

### **🔧 Automated Scripts**
- **`start-sonarqube-local.sh`** - Start SonarQube with health checks
- **`sonar-analysis-local.sh`** - Run analysis on all projects
- **`stop-sonarqube-local.sh`** - Clean shutdown

## 🏆 Projects to be Analyzed

| Project | Key | Expected Coverage | Performance |
|---------|-----|------------------|-------------|
| **Java Reactive** | `beauty-salon-backend-java-reactive` | **95%+** | **🏆 30,000+ RPS** |
| React Frontend | `beauty-salon-frontend-react` | **90%+** | **🎯 100% Tests** |
| Node.js Backend | `beauty-salon-backend-nodejs` | 85%+ | ⭐ 6,388 RPS |
| Java Spring | `beauty-salon-backend-java-spring` | 85%+ | ⭐ 6,037 RPS |
| Go Backend | `beauty-salon-backend-go` | 80%+ | ⭐ 3,735 RPS |
| Python Backend | `beauty-salon-backend-python` | 75%+ | ✅ ~1,000 RPS |

## 🛠️ Prerequisites

### Required Software
```bash
# Docker & Docker Compose
docker --version
docker-compose --version

# SonarQube Scanner
brew install sonar-scanner  # macOS
# or download from: https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/
```

### System Requirements
- **RAM**: 4GB minimum (8GB recommended)
- **Disk**: 2GB free space
- **Ports**: 9000 (SonarQube), 5432 (PostgreSQL internal)

## 📊 Analysis Features

### **Automatic Project Creation**
- Projects are created automatically in SonarQube
- Unique project keys for each backend/frontend
- Proper naming conventions

### **Coverage Integration**
- **Java**: JaCoCo reports
- **JavaScript/React**: LCOV reports
- **Python**: pytest-cov XML reports
- **Go**: Go coverage reports

### **Quality Gates**
- **Code Coverage** thresholds
- **Maintainability** ratings
- **Reliability** checks
- **Security** vulnerability detection

## 🔍 Usage Examples

### Start SonarQube
```bash
./scripts/start-sonarqube-local.sh
# ✅ SonarQube is ready!
# 📊 SonarQube URL: http://localhost:9000
# 👤 Default Login: admin / admin
```

### Run Analysis
```bash
./scripts/sonar-analysis-local.sh
# 🏆 Performance Champion: Java Reactive Backend (30,000+ RPS)
# 📊 Analyzing all 5 backends + frontend...
# ✅ Analysis completed for all projects
```

### Check Results
- Open http://localhost:9000
- View project dashboards
- Check quality gates
- Review coverage reports

## 🚨 Troubleshooting

### SonarQube Won't Start
```bash
# Check Docker status
docker info

# Check container logs
docker-compose -f docker-compose-sonarqube.yml logs sonarqube

# Restart containers
docker-compose -f docker-compose-sonarqube.yml restart
```

### Analysis Fails
```bash
# Check SonarQube is running
curl http://localhost:9000/api/system/status

# Verify scanner installation
sonar-scanner --version

# Check project directory exists
ls -la backend-java-reactive/
```

### Port Conflicts
```bash
# Check what's using port 9000
lsof -i :9000

# Stop conflicting services or change port in docker-compose-sonarqube.yml
```

## 🎯 Quality Metrics Expected

### **🏆 Java Reactive Backend** (Performance Champion)
- **Coverage**: 95%+ (190/190 tests passing)
- **Maintainability**: A rating
- **Security**: A rating
- **Performance**: 30,000+ RPS capability

### **🎯 React Frontend** (Test Champion)
- **Coverage**: 90%+ (231/231 tests passing)
- **Maintainability**: A rating
- **Accessibility**: High score

### **Other Backends**
- **Node.js**: 85%+ coverage, excellent performance
- **Java Spring**: 85%+ coverage, enterprise-ready
- **Go**: 80%+ coverage, efficient resource usage
- **Python**: 75%+ coverage, good documentation

## 🔄 Data Management

### Persistent Data
- **SonarQube data**: Stored in Docker volumes
- **PostgreSQL data**: Persistent across restarts
- **Analysis history**: Maintained between sessions

### Clean Reset
```bash
# Remove all data and start fresh
docker-compose -f docker-compose-sonarqube.yml down -v
./scripts/start-sonarqube-local.sh
```

## 🚀 Next Steps

1. **Start SonarQube**: `./scripts/start-sonarqube-local.sh`
2. **Change default password** at http://localhost:9000
3. **Run analysis**: `./scripts/sonar-analysis-local.sh`
4. **Review results** in SonarQube dashboard
5. **Set up quality gates** for continuous monitoring

---

**🏆 Result**: Complete local SonarQube setup for analyzing the Beauty Salon Management System with the Java Reactive backend leading in both performance (30,000+ RPS) and code quality standards.
