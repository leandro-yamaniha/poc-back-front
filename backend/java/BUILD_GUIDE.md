# Build Guide - Java Traditional (JVM vs Native)

Guia completo para compilar e executar o backend Java tradicional usando os profiles Maven.

## 📋 Profiles Disponíveis

### 1. JVM Profile
- Build rápido (~10 segundos)
- Ideal para desenvolvimento
- Suporte a Virtual Threads (Java 21)
- Requer JVM para executar

### 2. Native Profile
- Build lento (~3-4 minutos)
- Ideal para produção
- Executável standalone
- Suporte a Virtual Threads nativo

## 🛠️ Scripts de Build

### Build JVM
```bash
cd backend/scripts
./build-java-jvm.sh
```

**Output:**
- `target/beauty-salon-jvm.jar` (~50MB)

**Executar:**
```bash
java --enable-preview -jar target/beauty-salon-jvm.jar
```

### Build Native
```bash
cd backend/scripts
./build-java-native.sh
```

**Output:**
- `target/beauty-salon` (~140MB executável)

**Executar:**
```bash
./target/beauty-salon
```

## 🐳 Dockerfiles Disponíveis

### 1. Dockerfile.jvm
Build JVM tradicional com multi-stage

**Build:**
```bash
docker build -f Dockerfile.jvm -t beauty-salon:jvm .
```

**Run:**
```bash
docker run -p 10001:10001 beauty-salon:jvm
```

### 2. Dockerfile.native-profile
Build Native completo (compila dentro do Docker)

**Build:**
```bash
docker build -f Dockerfile.native-profile -t beauty-salon:native .
```

**Run:**
```bash
docker run -p 10001:10001 beauty-salon:native
```

### 3. Dockerfile.native-prebuilt
Usa executável nativo pré-compilado (mais rápido)

**Pré-requisito:**
```bash
./mvnw package -Pnative -DskipTests
```

**Build:**
```bash
docker build -f Dockerfile.native-prebuilt -t beauty-salon:native-prebuilt .
```

**Run:**
```bash
docker run -p 10001:10001 beauty-salon:native-prebuilt
```

### 4. Dockerfile (Legacy)
Dockerfile antigo usando artefatos pré-buildados

## 📊 Comparação

| Aspecto | JVM | Native Prebuilt | Native Full Build |
|---------|-----|-----------------|-------------------|
| **Build Local** | 10s | 3-4min | N/A |
| **Build Docker** | 2-3min | 30s | 5-6min |
| **Image Size** | ~220MB | ~160MB | ~160MB |
| **Startup** | 5-10s | 1-2s | 1-2s |
| **Memory** | 256-512MB | 128-256MB | 128-256MB |
| **Virtual Threads** | ✅ | ✅ | ✅ |
| **Uso** | Dev | Prod (rápido) | Prod (completo) |

## 🚀 Workflows Recomendados

### Desenvolvimento Local
```bash
# Build rápido
./build-java-jvm.sh

# Executar com Virtual Threads
java --enable-preview -jar target/beauty-salon-jvm.jar
```

### Deploy Produção (Rápido)
```bash
# 1. Build nativo local
./build-java-native.sh

# 2. Build Docker com executável pré-compilado
docker build -f Dockerfile.native-prebuilt -t beauty-salon:prod .

# 3. Deploy
docker run -d -p 10001:10001 beauty-salon:prod
```

### Deploy Produção (CI/CD)
```bash
# Build completo no Docker (não precisa de GraalVM local)
docker build -f Dockerfile.native-profile -t beauty-salon:prod .

# Deploy
docker run -d -p 10001:10001 beauty-salon:prod
```

## 🔧 Comandos Maven Diretos

### Build JVM
```bash
./mvnw clean package -Pjvm
```

### Build Native
```bash
./mvnw clean package -Pnative -DskipTests
```

### Executar Testes (por categoria)
```bash
# Unit tests (padrão)
./mvnw test

# Integration tests
./mvnw test -Pintegration-tests

# Performance tests
./mvnw test -Pperformance-tests

# Mutation tests
./mvnw test -Pmutation-tests

# Todos os testes
./mvnw test -Pall-tests
```

## 📝 Variáveis de Ambiente

### Comuns
```bash
SPRING_PROFILES_ACTIVE=docker
SPRING_DATA_CASSANDRA_CONTACT_POINTS=localhost
SERVER_PORT=10001
```

### JVM Específicas
```bash
JAVA_OPTS="--enable-preview -Xmx512m -Xms256m"
```

### Native Específicas
```bash
# Nenhuma configuração JVM necessária
# O executável já está otimizado com Virtual Threads
```

## 🎯 Características Especiais

### Virtual Threads (Java 21)
Este backend usa Virtual Threads para alta concorrência:

**JVM Mode:**
```java
// Configuração automática via Spring Boot
@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

**Native Mode:**
```bash
# Virtual Threads habilitados via GraalVM flags
-H:+UnlockExperimentalVMOptions
-H:+UseVirtualThread
```

### Benefícios dos Virtual Threads
- **Alta Concorrência**: Milhares de threads simultâneas
- **Baixo Overhead**: ~1KB por thread (vs 1MB threads tradicionais)
- **Código Simples**: Sintaxe síncrona com performance assíncrona
- **Escalabilidade**: Ideal para I/O-bound operations

## 🐛 Troubleshooting

### Build Native Falha
```bash
# Verificar GraalVM
native-image --version

# Aumentar memória
export NATIVE_IMAGE_OPTS="-J-Xmx8g"

# Rebuild
./mvnw clean package -Pnative -DskipTests
```

### Virtual Threads Não Funcionam
```bash
# Verificar versão Java
java --version  # Deve ser 21+

# Executar com preview features
java --enable-preview -jar target/beauty-salon-jvm.jar
```

### Docker Build Lento
```bash
# Usar build cache
docker build --cache-from beauty-salon:latest ...

# Ou usar executável pré-compilado
./mvnw package -Pnative -DskipTests
docker build -f Dockerfile.native-prebuilt ...
```

### Executável Native Não Inicia
```bash
# Verificar bibliotecas
ldd target/beauty-salon

# Testar com verbose
./target/beauty-salon --verbose
```

## 📚 Referências

- [Maven Profiles](../PROFILES.md)
- [Docker Profiles](../../DOCKER_PROFILES_GUIDE.md)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Java Virtual Threads](https://openjdk.org/jeps/444)

## ✅ Checklist de Validação

- [ ] Build JVM funciona: `./build-java-jvm.sh`
- [ ] JAR executa: `java --enable-preview -jar target/beauty-salon-jvm.jar`
- [ ] Build Native funciona: `./build-java-native.sh`
- [ ] Executável Native funciona: `./target/beauty-salon`
- [ ] Docker JVM funciona: `docker build -f Dockerfile.jvm ...`
- [ ] Docker Native funciona: `docker build -f Dockerfile.native-prebuilt ...`
- [ ] Health check responde: `curl http://localhost:10001/actuator/health`
- [ ] Virtual Threads ativos: Verificar logs de startup
- [ ] Testes passam: `./mvnw test`

## 🎊 Diferenças vs Backend Reactive

| Característica | Java Traditional | Java Reactive |
|----------------|------------------|---------------|
| **Modelo** | Imperativo + Virtual Threads | Reativo (WebFlux) |
| **Port** | 10001 | 8085 |
| **Concorrência** | Virtual Threads | Reactive Streams |
| **Sintaxe** | Síncrona | Mono/Flux |
| **Complexidade** | Baixa | Média |
| **Performance** | Excelente | Excelente |
| **Uso** | APIs REST tradicionais | Streaming, backpressure |

Agora você tem controle total sobre o processo de build do backend Java tradicional! 🎯
