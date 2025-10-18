# Test Profiles Guide - Beauty Salon Backend

## 📋 Overview

The Beauty Salon backend uses Maven profiles to organize tests into different categories, allowing for targeted test execution based on requirements and environment capabilities.

## 🎯 Available Test Profiles

### 1. **Unit Tests** (Default Profile)
**Profile ID**: `unit-tests`  
**Activation**: Active by default  
**Purpose**: Fast tests with mocks, no external dependencies

**Includes**:
- `*SimpleTest.java` - Service layer unit tests with mocks
- `model/*Test.java` - Model/entity tests
- `BeautySalonApplicationTest.java` - Application context test

**Command**:
```bash
./mvnw test
# or explicitly
./mvnw test -P unit-tests
```

**Expected Results**: ~42 tests, execution time < 30 seconds

---

### 2. **Integration Tests**
**Profile ID**: `integration-tests`  
**Purpose**: Tests requiring database/containers  
**Requirements**: Docker environment for Testcontainers

**Includes**:
- `*IntegrationTest.java` - Database integration tests
- `RunCucumberTest.java` - BDD integration tests
- `controller/*Test.java` - REST API controller tests
- `*ValidationTest.java` - Data validation tests

**Command**:
```bash
./mvnw test -P integration-tests
```

**Prerequisites**: 
- Docker Desktop running
- Cassandra Testcontainers support

---

### 3. **Performance Tests**
**Profile ID**: `performance-tests`  
**Purpose**: Stress tests and performance validation  
**Requirements**: Docker environment + extended timeouts

**Includes**:
- `*PerformanceTest.java` - Performance benchmarks
- `*StressTest.java` - Load and stress testing
- `*ComparisonTest.java` - Performance comparisons
- `*MonitoringTest.java` - Monitoring and metrics tests

**Command**:
```bash
./mvnw test -P performance-tests
```

**Configuration**:
- Extended timeout: 30 minutes
- System properties for test timeouts
- Requires Docker for Cassandra containers

---

### 4. **Mutation Tests**
**Profile ID**: `mutation-tests`  
**Purpose**: PiTest mutation testing for code quality  
**Tool**: PiTest Maven Plugin

**Includes**:
- `*MutationTest.java` - Custom mutation test classes
- PiTest mutation coverage analysis

**Command**:
```bash
./mvnw test -P mutation-tests
# or run PiTest directly
./mvnw pitest:mutationCoverage -P mutation-tests
```

**Output**: HTML and XML reports in `target/pit-reports/`

---

### 5. **All Tests**
**Profile ID**: `all-tests`  
**Purpose**: Execute complete test suite  
**Requirements**: Docker environment + extended timeouts

**Command**:
```bash
./mvnw test -P all-tests
```

**Expected Results**: ~210 tests (when Docker is available)

## 🚀 Usage Examples

### Quick Development Testing
```bash
# Run only unit tests (fast feedback)
./mvnw test

# Run with coverage report
./mvnw test jacoco:report
```

### Pre-Commit Testing
```bash
# Run unit + integration tests
./mvnw test -P integration-tests
```

### CI/CD Pipeline
```bash
# Full test suite
./mvnw test -P all-tests

# Performance validation
./mvnw test -P performance-tests
```

### Code Quality Analysis
```bash
# Mutation testing
./mvnw test -P mutation-tests
./mvnw pitest:mutationCoverage -P mutation-tests
```

## 📊 Test Categories Breakdown

| Category | Test Count | Execution Time | Dependencies |
|----------|------------|----------------|--------------|
| **Unit Tests** | ~42 | < 30s | None |
| **Integration Tests** | ~25 | 2-5 min | Docker + Cassandra |
| **Performance Tests** | ~8 | 5-15 min | Docker + Cassandra |
| **Mutation Tests** | ~20 | 10-30 min | None |
| **All Tests** | ~210 | 15-30 min | Docker + Cassandra |

## 🔧 Configuration Details

### Profile Activation
- **Default**: `unit-tests` profile is active by default
- **Explicit**: Use `-P <profile-id>` to activate specific profiles
- **Multiple**: Combine profiles with `-P profile1,profile2`

### Test Timeouts
- **Unit Tests**: Standard timeout (30s per test)
- **Integration Tests**: Standard timeout (60s per test)
- **Performance Tests**: Extended timeout (30 min total, 30s per test)

### Environment Requirements
- **Unit Tests**: ✅ No external dependencies
- **Integration Tests**: ⚠️ Requires Docker + Testcontainers
- **Performance Tests**: ⚠️ Requires Docker + Testcontainers
- **Mutation Tests**: ✅ No external dependencies

## 🐛 Troubleshooting

### Docker Issues
If you see "Could not find a valid Docker environment":
1. Ensure Docker Desktop is running
2. Check Docker socket permissions
3. Run unit tests only: `./mvnw test -P unit-tests`

### Test Failures
- **Unit Tests**: Should always pass (no external dependencies)
- **Integration Tests**: May fail without proper Docker setup
- **Performance Tests**: May fail due to resource constraints

### Profile Issues
```bash
# List available profiles
./mvnw help:all-profiles

# Verify active profiles
./mvnw help:active-profiles
```

## 📈 Best Practices

1. **Development**: Use unit tests for quick feedback
2. **Pre-commit**: Run integration tests to verify functionality
3. **CI/CD**: Use all-tests profile for comprehensive validation
4. **Code Quality**: Run mutation tests weekly/monthly
5. **Performance**: Run performance tests before releases

## 🎯 Success Criteria

- **Unit Tests**: 100% pass rate (no external dependencies)
- **Integration Tests**: >95% pass rate (with Docker)
- **Performance Tests**: Meet defined SLA thresholds
- **Mutation Tests**: >80% mutation score
- **Code Coverage**: >90% instruction coverage (JaCoCo)
