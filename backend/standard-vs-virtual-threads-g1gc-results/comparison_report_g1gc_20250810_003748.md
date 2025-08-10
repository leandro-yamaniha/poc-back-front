# Standard vs Virtual Threads (G1GC) Performance Comparison

**Test Date:** Sun Aug 10 00:37:48 -03 2025
**Java Version:** 24.0.1

## Test Configuration

- **Concurrent Users:** 100
- **Requests per User:** 10
- **Total Requests per Test:** 1,000
- **Total Tests:** 3 (Creation, Retrieval, Mixed CRUD)
- **GC:** G1GC (both configurations)
- **Objective:** Isolate Virtual Threads impact using same GC

---

## Standard (G1GC) Results

### Configuration Details
   📋 Active Profile: test
   🧵 Virtual Threads: DISABLED
   🗑️  GC Type: G1GC
   ⚙️  Java Version: 24.0.1

### Performance Metrics

   ✅ Configuration: Standard (G1GC)


#### Test Results:

---

## Virtual Threads (G1GC) Results

### Configuration Details
   📋 Active Profile: test
   🧵 Virtual Threads: DISABLED
   🗑️  GC Type: G1GC
   ⚙️  Java Version: 24.0.1

### Performance Metrics

   ✅ Configuration: Virtual Threads (G1GC)


#### Test Results:

---


## Performance Comparison Analysis

### Key Findings

Esta comparação isola o impacto específico das **Virtual Threads** mantendo o mesmo garbage collector (G1GC) em ambas as configurações.

### Expected Benefits of Virtual Threads:
- 🧵 **Melhor Concorrência**: Gerenciamento mais eficiente de threads para I/O intensivo
- 💾 **Menor Uso de Memória**: Threads virtuais consomem menos memória que threads de plataforma
- 🚀 **Maior Throughput**: Especialmente para operações que fazem muitas chamadas de I/O (banco de dados)
- ⚡ **Menor Latência**: Redução no tempo de context switching entre threads

### Recommendations:

**Use Virtual Threads when:**
- Aplicação faz muitas operações I/O (banco de dados, APIs externas)
- Necessita lidar com alta concorrência (muitos usuários simultâneos)
- Throughput é mais importante que latência individual

**Use Standard Configuration when:**
- Aplicação é CPU-intensiva
- Latência individual é crítica
- Compatibilidade com bibliotecas legadas é necessária

### Test Environment:
- **Java Version:** 24.0.1
- **GC:** G1GC (ambas configurações)
- **Concurrent Users:** 100
- **Requests per User:** 10
- **Total Requests per Test:** 1,000
- **Database:** Cassandra 4.1 (Testcontainers)

