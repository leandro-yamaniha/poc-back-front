# GraalVM Native Image - Configuração Avançada

## 📊 Opções de Garbage Collector

### ✅ Disponíveis no Native Image:

#### 1. **Serial GC (Padrão)**
```yaml
environment:
  - JAVA_OPTS=-XX:+UseSerialGC -XX:MaxRAMPercentage=75.0
```
- ✅ Menor uso de memória
- ✅ Melhor para aplicações pequenas
- ⚠️ Pausa única (stop-the-world)
- 📊 **Recomendado para containers com < 256 MiB**

#### 2. **G1 GC (Disponível)**
```yaml
environment:
  - JAVA_OPTS=-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:MaxGCPauseMillis=200
```
- ✅ Pausas mais previsíveis
- ✅ Melhor para aplicações maiores
- ⚠️ Usa mais memória que Serial GC
- 📊 **Recomendado para containers com > 256 MiB**

### ❌ NÃO Disponíveis no Native Image:

- **ZGC** - Apenas em JVM tradicional
- **Shenandoah** - Apenas em JVM tradicional
- **Parallel GC** - Apenas em JVM tradicional

---

## 🧵 Virtual Threads (Project Loom)

### ✅ Suporte no GraalVM Native Image:

**Requisitos:**
- GraalVM 21+ ou 22+
- Java 21+
- Configuração em tempo de build

### Configuração no `pom.xml`:

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <configuration>
        <buildArgs>
            <!-- Habilitar Virtual Threads -->
            <arg>--enable-preview</arg>
            <arg>-H:+UnlockExperimentalVMOptions</arg>
            <arg>-H:+UseVirtualThreads</arg>
            
            <!-- Otimizações adicionais -->
            <arg>-H:+ReportExceptionStackTraces</arg>
            <arg>--initialize-at-build-time=org.slf4j</arg>
        </buildArgs>
    </configuration>
</plugin>
```

### Uso no Código (Spring Boot 3.2+):

```java
@Configuration
public class VirtualThreadConfig {
    
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(
            Executors.newVirtualThreadPerTaskExecutor()
        );
    }
    
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(
                Executors.newVirtualThreadPerTaskExecutor()
            );
        };
    }
}
```

### Configuração no `application.yml`:

```yaml
spring:
  threads:
    virtual:
      enabled: true  # Habilita Virtual Threads no Spring Boot 3.2+
```

---

## 🚀 Configurações Recomendadas por Cenário

### Cenário 1: Container Pequeno (135 MiB)

```yaml
environment:
  - JAVA_OPTS=-XX:+UseSerialGC -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=50.0
```

**Por quê?**
- Serial GC usa menos memória
- MaxRAMPercentage=75% deixa 25% para overhead do sistema
- Ideal para containers com limite de memória restrito

### Cenário 2: Container Médio (256-512 MiB)

```yaml
environment:
  - JAVA_OPTS=-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=1M
```

**Por quê?**
- G1 GC oferece pausas mais previsíveis
- MaxGCPauseMillis=200ms limita pausas
- G1HeapRegionSize=1M otimiza para heaps pequenos

### Cenário 3: Alta Performance (512+ MiB)

```yaml
environment:
  - JAVA_OPTS=-XX:+UseG1GC -XX:MaxRAMPercentage=80.0 -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication
```

**Por quê?**
- Mais memória disponível para heap
- Pausas menores (100ms)
- String deduplication economiza memória

---

## 📊 Comparação: Native Image vs JVM

| Recurso | Native Image | JVM Tradicional |
|---------|--------------|-----------------|
| **Serial GC** | ✅ Disponível | ✅ Disponível |
| **G1 GC** | ✅ Disponível | ✅ Disponível |
| **ZGC** | ❌ Não disponível | ✅ Disponível |
| **Shenandoah** | ❌ Não disponível | ✅ Disponível |
| **Virtual Threads** | ✅ Disponível (21+) | ✅ Disponível (21+) |
| **Startup Time** | 🚀 ~50ms | ⏱️ ~3-5s |
| **Memory Footprint** | 💚 ~100-150 MiB | 📊 ~250-400 MiB |
| **Peak Performance** | 📈 Boa | 🚀 Excelente |
| **Warmup Time** | ⚡ Nenhum | ⏳ 30-60s |

---

## 🎯 Configuração Recomendada para o Projeto

### Para o Beauty Salon App (135 MiB limite):

```yaml
environment:
  # GC Configuration
  - JAVA_OPTS=-XX:+UseSerialGC -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=50.0 -XX:+PrintGC -XX:+PrintGCDetails
  
  # Memory Configuration
  - MALLOC_ARENA_MAX=2
  
  # Performance Tuning
  - SPRING_THREADS_VIRTUAL_ENABLED=true
```

**Justificativa:**
1. **Serial GC**: Menor footprint de memória (ideal para 135 MiB)
2. **MaxRAMPercentage=75%**: ~100 MiB para heap, 35 MiB para overhead
3. **PrintGC**: Monitorar comportamento do GC
4. **MALLOC_ARENA_MAX=2**: Reduz uso de memória nativa
5. **Virtual Threads**: Melhor throughput sem overhead de threads

---

## 🔧 Como Habilitar Virtual Threads

### 1. Atualizar `pom.xml`:

```xml
<properties>
    <java.version>21</java.version>
    <graalvm.version>21.0.0</graalvm.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.graalvm.buildtools</groupId>
            <artifactId>native-maven-plugin</artifactId>
            <version>0.10.0</version>
            <configuration>
                <buildArgs>
                    <arg>--enable-preview</arg>
                    <arg>-H:+UnlockExperimentalVMOptions</arg>
                    <arg>-H:+UseVirtualThreads</arg>
                </buildArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. Atualizar `application.yml`:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

### 3. Rebuild Native Image:

```bash
./mvnw clean native:compile -Pnative
```

---

## ⚠️ Limitações do Native Image

### O que NÃO funciona:

1. **ZGC**: Não disponível
2. **Shenandoah GC**: Não disponível
3. **JMX**: Suporte limitado
4. **JVMTI**: Não disponível
5. **Dynamic Class Loading**: Limitado
6. **Reflection**: Requer configuração prévia

### O que FUNCIONA:

1. ✅ Serial GC
2. ✅ G1 GC
3. ✅ Virtual Threads (Java 21+)
4. ✅ Reactive Streams
5. ✅ WebFlux
6. ✅ R2DBC
7. ✅ Netty

---

## 📈 Monitoramento de GC

### Habilitar logs de GC:

```yaml
environment:
  - JAVA_OPTS=-XX:+UseSerialGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
```

### Ver logs:

```bash
docker logs beauty-salon-backend-reactive-native | grep GC
```

### Métricas esperadas com Serial GC (135 MiB):

```
[GC (Allocation Failure)  33M->8M(100M), 0.0045s]
[GC (Allocation Failure)  41M->12M(100M), 0.0052s]
```

- Pausas: ~5-10ms
- Frequência: A cada 30-60s sob carga
- Memória recuperada: ~70-80%

---

## 🚀 Próximos Passos

### Para usar ZGC (requer JVM):

Use o `docker-compose.jvm.yml` com:

```yaml
environment:
  - JAVA_OPTS=-XX:+UseZGC -XX:MaxRAMPercentage=75.0 -Xlog:gc*
  - JAVA_TOOL_OPTIONS=-XX:+UnlockExperimentalVMOptions
```

**Nota:** ZGC requer mínimo de 256 MiB de heap.

### Para usar Virtual Threads + ZGC (melhor configuração JVM):

```yaml
environment:
  - JAVA_OPTS=-XX:+UseZGC -XX:MaxRAMPercentage=75.0 -Xlog:gc*
  - SPRING_THREADS_VIRTUAL_ENABLED=true
```

**Requisitos:**
- Memória: 300+ MiB
- Java: 21+
- Spring Boot: 3.2+

---

## 📚 Referências

- [GraalVM Native Image Reference](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Virtual Threads (JEP 444)](https://openjdk.org/jeps/444)
- [Spring Boot 3.2 Virtual Threads](https://spring.io/blog/2023/09/09/all-together-now-spring-boot-3-2-graalvm-native-images-java-21-and-virtual)
- [GraalVM GC Options](https://www.graalvm.org/latest/reference-manual/native-image/optimizations-and-performance/MemoryManagement/)
