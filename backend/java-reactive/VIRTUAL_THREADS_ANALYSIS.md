# ⚠️ Virtual Threads com WebFlux - Análise de Performance

## 📊 Resultado do Teste

**Data:** 2025-11-13  
**Configuração:** 135 MiB / 1.0 vCPU

### Comparação: SEM vs COM Virtual Threads

| Configuração | Req/s | Latência | Memória | CPU | Resultado |
|--------------|-------|----------|---------|-----|-----------|
| **SEM Virtual Threads** | 1,688 | 89.94ms | 123 MiB | 85.50% | ✅ MELHOR |
| **COM Virtual Threads** | 1,299 | 111.38ms | 119 MiB | 89.90% | ❌ PIOR |

### Diferença

- **Throughput:** -23% (PIOR) ❌
- **Latência:** +24% (PIOR) ❌
- **Memória:** -3% (melhor) ✅
- **CPU:** +5% (pior) ❌

---

## 🔍 Por Que Virtual Threads Pioraram a Performance?

### 1. Incompatibilidade Arquitetural

**WebFlux é Reativo (Não-Bloqueante):**
- Usa Reactor (Mono/Flux)
- Event loop (Netty)
- Backpressure
- Já é não-bloqueante por natureza

**Virtual Threads são para Código Bloqueante:**
- Substituem threads tradicionais
- Ideais para operações bloqueantes (JDBC, REST clients síncronos)
- Não trazem benefício para código já não-bloqueante

### 2. Overhead Adicional

Ao misturar Virtual Threads com WebFlux:
- Overhead de gerenciamento de Virtual Threads
- Conflito entre event loop (Netty) e Virtual Threads
- Scheduler do Reactor não se beneficia de Virtual Threads
- Resultado: Performance PIOR

### 3. Heap Pequeno

Com Virtual Threads ativado:
- Mais objetos na memória
- GC mais frequente
- Pausas maiores
- Latência aumenta

---

## 💡 Quando Usar Virtual Threads?

### ✅ USE Virtual Threads com:

1. **Spring MVC (Não WebFlux)**
   ```java
   @RestController
   public class BlockingController {
       @GetMapping("/data")
       public String getData() {
           // Código bloqueante (JDBC, REST client)
           return jdbcTemplate.queryForObject(...);
       }
   }
   ```

2. **JDBC Bloqueante**
   ```java
   // Virtual Threads são PERFEITOS aqui
   jdbcTemplate.query("SELECT * FROM users", ...);
   ```

3. **REST Clients Síncronos**
   ```java
   // Virtual Threads ajudam muito aqui
   RestTemplate restTemplate = new RestTemplate();
   String result = restTemplate.getForObject(url, String.class);
   ```

4. **Muitas Threads Esperando I/O**
   - File I/O bloqueante
   - Network I/O síncrono
   - Locks e sincronização

### ❌ NÃO USE Virtual Threads com:

1. **Spring WebFlux**
   ```java
   @RestController
   public class ReactiveController {
       @GetMapping("/data")
       public Mono<String> getData() {
           // Já é não-bloqueante, Virtual Threads não ajudam
           return reactiveRepository.findAll();
       }
   }
   ```

2. **Reactor (Mono/Flux)**
   - Já é não-bloqueante
   - Virtual Threads adicionam overhead
   - Performance piora

3. **Event-Driven Architecture**
   - Event loops (Netty, Vert.x)
   - Já são não-bloqueantes
   - Virtual Threads não trazem benefício

4. **Aplicações com CPU-Bound**
   - Processamento intensivo
   - Virtual Threads não ajudam
   - Use threads tradicionais ou ForkJoinPool

---

## 🎯 Recomendações para Este Projeto

### Para WebFlux + Reactor (Este Projeto)

**❌ NÃO USE Virtual Threads**

```yaml
environment:
  # Otimizações para 135 MiB (SEM Virtual Threads)
  - JAVA_OPTS=-Xmx100m -Xms50m -XX:+UseG1GC -XX:MaxGCPauseMillis=100
  # Virtual Threads NÃO devem ser usados com WebFlux
  # SPRING_THREADS_VIRTUAL_ENABLED=true
```

**Resultado:**
- Throughput: 1,688 req/s
- Latência: 89.94ms
- Performance: 30% MELHOR

### Se Migrar para Spring MVC

**✅ USE Virtual Threads**

```yaml
environment:
  - JAVA_OPTS=-Xmx256m -Xms128m -XX:+UseG1GC
  - SPRING_THREADS_VIRTUAL_ENABLED=true
```

**Benefícios Esperados:**
- Suporta milhares de conexões simultâneas
- Menor uso de memória por thread
- Melhor throughput para operações bloqueantes

---

## 📚 Referências

### Virtual Threads (Project Loom)

- Introduzidos no Java 21
- JEP 444: Virtual Threads
- Threads leves gerenciadas pela JVM
- Ideais para código bloqueante

### WebFlux vs Spring MVC

| Característica | WebFlux | Spring MVC + Virtual Threads |
|----------------|---------|------------------------------|
| **Modelo** | Reativo (Não-bloqueante) | Bloqueante |
| **Threads** | Event loop (poucos) | Thread por request (muitos) |
| **Backpressure** | Sim | Não |
| **Ideal para** | I/O intensivo, streaming | CRUD tradicional, JDBC |
| **Virtual Threads** | ❌ Não compatível | ✅ Ideal |

---

## 🔬 Conclusão

### Virtual Threads são EXCELENTES, mas...

**✅ Para Spring MVC:**
- Substituem threads tradicionais
- Permitem milhares de conexões
- Reduzem uso de memória
- Melhoram throughput

**❌ Para WebFlux:**
- Adicionam overhead
- Conflitam com event loop
- Pioram performance
- Não trazem benefício

### Para Este Projeto (WebFlux + Reactor):

**Mantenha SEM Virtual Threads**
- Performance: 1,688 req/s (30% melhor)
- Latência: 89.94ms (20% melhor)
- Configuração ideal já está otimizada

---

## 📊 Dados do Teste

### Teste COM Virtual Threads
- **Data:** 2025-11-13 23:54:45
- **Diretório:** `./stress-test-results/20251113_235445/`
- **Throughput:** 1,299 req/s
- **Latência:** 111.38ms
- **Memória:** 119 MiB
- **CPU:** 89.90%

### Teste SEM Virtual Threads (V3)
- **Data:** 2025-11-13 23:33:03
- **Diretório:** `./stress-test-results/20251113_233303/`
- **Throughput:** 1,688 req/s
- **Latência:** 89.94ms
- **Memória:** 123 MiB
- **CPU:** 85.50%

---

**Última atualização:** 2025-11-13
