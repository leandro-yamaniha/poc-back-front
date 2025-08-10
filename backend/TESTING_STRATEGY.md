# Testing Strategy - Beauty Salon Backend

## 🎯 Overview

The Beauty Salon backend implements a comprehensive testing strategy using Maven profiles to organize different types of tests. This approach enables targeted test execution, faster feedback loops, and better resource management.

## 📊 Test Organization

### **Test Categories**

| Category | Profile ID | Test Count | Execution Time | Dependencies |
|----------|------------|------------|----------------|--------------|
| **Unit Tests** | `unit-tests` | ~42 | < 30s | None |
| **Integration Tests** | `integration-tests` | ~25 | 2-5 min | Docker + Cassandra |
| **Performance Tests** | `performance-tests` | ~8 | 5-15 min | Docker + Cassandra |
| **Mutation Tests** | `mutation-tests` | ~20 | 10-30 min | None |
| **All Tests** | `all-tests` | ~210 | 15-30 min | Docker + Cassandra |

## 🔧 Test Profiles Configuration

### 1. **Unit Tests** (Default)
```xml
<profile>
    <id>unit-tests</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
</profile>
```

**Includes**:
- `*SimpleTest.java` - Service layer tests with mocks
- `model/*Test.java` - Entity/model validation tests
- `BeautySalonApplicationTest.java` - Application context test

**Characteristics**:
- ✅ Fast execution (< 30 seconds)
- ✅ No external dependencies
- ✅ Mockito-based isolation
- ✅ High reliability

### 2. **Integration Tests**
```xml
<profile>
    <id>integration-tests</id>
</profile>
```

**Includes**:
- `*IntegrationTest.java` - Database integration tests
- `RunCucumberTest.java` - BDD scenarios
- `controller/*Test.java` - REST API tests
- `*ValidationTest.java` - Data validation tests

**Characteristics**:
- ⚠️ Requires Docker + Testcontainers
- ⚠️ Moderate execution time (2-5 minutes)
- ✅ Real database interactions
- ✅ End-to-end API validation

### 3. **Performance Tests**
```xml
<profile>
    <id>performance-tests</id>
</profile>
```

**Includes**:
- `*PerformanceTest.java` - Performance benchmarks
- `*StressTest.java` - Load testing
- `*ComparisonTest.java` - Performance comparisons
- `*MonitoringTest.java` - Monitoring validation

**Characteristics**:
- ⚠️ Requires Docker + Testcontainers
- ⚠️ Extended execution time (5-15 minutes)
- ⚠️ Resource intensive
- ✅ Performance validation

### 4. **Mutation Tests**
```xml
<profile>
    <id>mutation-tests</id>
</profile>
```

**Includes**:
- `*MutationTest.java` - Custom mutation tests
- PiTest mutation coverage analysis

**Characteristics**:
- ✅ No external dependencies
- ⚠️ Long execution time (10-30 minutes)
- ✅ Code quality analysis
- ✅ Mutation score reporting

## 🚀 Quick Start Commands

### Development Workflow
```bash
# Quick unit tests (default)
./mvnw test

# Unit tests with coverage
./mvnw test jacoco:report

# Integration tests (requires Docker)
./mvnw test -P integration-tests

# Interactive test runner
./scripts/run-test-profiles.sh
```

### CI/CD Pipeline
```bash
# Stage 1: Unit Tests
./mvnw test -P unit-tests

# Stage 2: Integration Tests
./mvnw test -P integration-tests

# Stage 3: Performance Validation
./mvnw test -P performance-tests

# Stage 4: Full Suite
./mvnw test -P all-tests
```

### Code Quality Analysis
```bash
# Mutation testing
./mvnw test -P mutation-tests
./mvnw pitest:mutationCoverage -P mutation-tests

# Coverage analysis
./mvnw test jacoco:report
open target/site/jacoco/index.html
```

## 📁 Test File Organization

```
src/test/java/com/beautysalon/
├── controller/           # REST API integration tests
│   ├── CustomerControllerTest.java
│   ├── AppointmentControllerTest.java
│   ├── ServiceControllerTest.java
│   └── StaffControllerTest.java
├── integration/          # Database integration tests
│   ├── CassandraIntegrationTest.java
│   └── RunCucumberTest.java
├── model/               # Entity/model unit tests
│   ├── CustomerTest.java
│   ├── AppointmentTest.java
│   ├── ServiceTest.java
│   └── StaffTest.java
├── mutation/            # Mutation testing
│   ├── CustomerServiceMutationTest.java
│   ├── AppointmentServiceMutationTest.java
│   └── [...]MutationTest.java
├── performance/         # Performance and stress tests
│   ├── BackendStressTest.java
│   ├── Phase2PerformanceTest.java
│   ├── Phase3MonitoringTest.java
│   └── [...]PerformanceTest.java
├── service/             # Service layer unit tests
│   ├── CustomerServiceSimpleTest.java
│   ├── AppointmentServiceSimpleTest.java
│   ├── ServiceServiceSimpleTest.java
│   └── StaffServiceSimpleTest.java
└── validation/          # Data validation tests
    └── CustomerValidationTest.java
```

## 🎯 Testing Best Practices

### **Development Phase**
1. **TDD Approach**: Write unit tests first
2. **Fast Feedback**: Use unit tests for immediate validation
3. **Mock Dependencies**: Isolate units under test
4. **Coverage Target**: Maintain >90% code coverage

### **Integration Phase**
1. **Database Tests**: Validate data persistence
2. **API Tests**: Test REST endpoints
3. **BDD Tests**: Validate business scenarios
4. **Container Tests**: Use Testcontainers for isolation

### **Performance Phase**
1. **Stress Testing**: Validate under load
2. **Benchmark Tests**: Measure performance metrics
3. **Monitoring Tests**: Validate observability
4. **Comparison Tests**: Track performance trends

### **Quality Phase**
1. **Mutation Testing**: Validate test effectiveness
2. **Coverage Analysis**: Identify untested code
3. **Static Analysis**: Code quality checks
4. **Security Testing**: Vulnerability scanning

## 📈 Success Criteria

### **Unit Tests**
- ✅ 100% pass rate
- ✅ Execution time < 30 seconds
- ✅ >90% code coverage
- ✅ No external dependencies

### **Integration Tests**
- ✅ >95% pass rate
- ✅ All API endpoints tested
- ✅ Database operations validated
- ✅ BDD scenarios passing

### **Performance Tests**
- ✅ Response time < 500ms (95th percentile)
- ✅ Throughput > 100 req/s
- ✅ Success rate > 95%
- ✅ Memory usage stable

### **Mutation Tests**
- ✅ Mutation score > 80%
- ✅ Critical paths covered
- ✅ Test quality validated
- ✅ No surviving mutants in core logic

## 🔍 Monitoring and Reporting

### **Coverage Reports**
- **JaCoCo**: `target/site/jacoco/index.html`
- **PiTest**: `target/pit-reports/index.html`
- **Surefire**: `target/surefire-reports/`

### **Metrics Collection**
- Test execution times
- Coverage percentages
- Performance benchmarks
- Mutation scores

### **Continuous Integration**
```yaml
# Example CI pipeline
stages:
  - unit-tests      # Fast feedback
  - integration     # Functional validation
  - performance     # Load validation
  - mutation        # Quality validation
```

## 🚨 Troubleshooting

### **Docker Issues**
```bash
# Check Docker status
docker info

# Start Docker Desktop
open -a Docker

# Alternative: Run unit tests only
./mvnw test -P unit-tests
```

### **Test Failures**
```bash
# Detailed error output
./mvnw test -P unit-tests -X

# Skip failing tests temporarily
./mvnw test -P unit-tests -DskipTests=false -Dmaven.test.failure.ignore=true
```

### **Performance Issues**
```bash
# Increase memory for tests
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"

# Run with verbose output
./mvnw test -P performance-tests -X
```

## 📚 Additional Resources

- **Test Profiles Guide**: `TEST_PROFILES_GUIDE.md`
- **Stress Test Guide**: `STRESS_TEST_GUIDE.md`
- **Interactive Runner**: `./scripts/run-test-profiles.sh`
- **JaCoCo Reports**: `target/site/jacoco/`
- **PiTest Reports**: `target/pit-reports/`
