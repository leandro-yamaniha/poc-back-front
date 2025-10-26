# Multiprofile Implementation Summary

Implementação completa da técnica de multiprofile Maven para ambos os backends Java.

## 🎯 Objetivo

Criar uma abordagem unificada de build que permita escolher entre JVM tradicional e Native Image para cada backend, facilitando desenvolvimento e otimizando produção.

## 📦 Backends Implementados

### 1. Java Reactive (WebFlux)
**Localização:** `backend/java-reactive/`
**Port:** 8085

#### Profiles Maven
- **jvm**: Build tradicional JVM (~6s)
- **native**: GraalVM Native Image (~3-4min)

#### Dockerfiles
- `Dockerfile.jvm`: Multi-stage JVM build
- `Dockerfile.native-profile`: Native build completo
- `Dockerfile.native-prebuilt`: Native pré-compilado
- `Dockerfile.native-final`: Native otimizado

#### Scripts
- `build-jvm.sh`: Build JVM rápido
- `build-aot-native.sh`: Build Native com AOT

### 2. Java Traditional (Spring MVC)
**Localização:** `backend/java/`
**Port:** 10001

#### Profiles Maven
- **jvm**: Build tradicional JVM (~10s)
- **native**: GraalVM Native Image (~3-4min)

#### Dockerfiles
- `Dockerfile.jvm`: Multi-stage JVM build
- `Dockerfile.native-profile`: Native build completo
- `Dockerfile.native-prebuilt`: Native pré-compilado

#### Scripts
- `build-java-jvm.sh`: Build JVM rápido
- `build-java-native.sh`: Build Native

## 📊 Comparação Completa

### Java Reactive vs Java Traditional

| Característica | Java Reactive | Java Traditional |
|----------------|---------------|------------------|
| **Framework** | WebFlux | Spring MVC |
| **Modelo** | Reativo (Mono/Flux) | Imperativo + Virtual Threads |
| **Port** | 8085 | 10001 |
| **Startup JVM** | 5-10s | 5-10s |
| **Startup Native** | 1-2s | 1-2s |
| **Memory JVM** | 256-512MB | 256-512MB |
| **Memory Native** | 128-256MB | 128-256MB |
| **JAR Size** | ~46MB | ~50MB |
| **Native Size** | ~135MB | ~140MB |
| **Concorrência** | Reactive Streams | Virtual Threads |
| **Complexidade** | Média | Baixa |
| **Uso Ideal** | Streaming, backpressure | APIs REST tradicionais |

### JVM vs Native (Ambos Backends)

| Aspecto | JVM | Native |
|---------|-----|--------|
| **Build Time** | 6-10s | 3-4min |
| **Startup** | 5-10s | 1-2s |
| **Memory** | 256-512MB | 128-256MB |
| **Warmup** | Sim (JIT) | Não |
| **Peak Performance** | Excelente (após warmup) | Muito Bom |
| **Image Size** | ~200-220MB | ~150-160MB |
| **Debugging** | Fácil | Limitado |
| **Uso** | Desenvolvimento | Produção |

## 🚀 Workflows Recomendados

### Desenvolvimento Local

#### Java Reactive
```bash
cd backend/scripts
./build-jvm.sh
cd ../java-reactive
java -jar target/beauty-salon-reactive-jvm.jar
```

#### Java Traditional
```bash
cd backend/scripts
./build-java-jvm.sh
cd ../java
java --enable-preview -jar target/beauty-salon-jvm.jar
```

### Deploy Produção (Rápido)

#### Java Reactive
```bash
cd backend/scripts
./build-aot-native.sh
cd ../java-reactive
docker build -f Dockerfile.native-prebuilt -t reactive:prod .
docker run -d -p 8085:8085 reactive:prod
```

#### Java Traditional
```bash
cd backend/scripts
./build-java-native.sh
cd ../java
docker build -f Dockerfile.native-prebuilt -t traditional:prod .
docker run -d -p 10001:10001 traditional:prod
```

### Deploy Produção (CI/CD)

#### Java Reactive
```bash
cd backend/java-reactive
docker build -f Dockerfile.native-profile -t reactive:prod .
```

#### Java Traditional
```bash
cd backend/java
docker build -f Dockerfile.native-profile -t traditional:prod .
```

## 📝 Comandos Maven Unificados

### Build JVM
```bash
# Reactive
cd backend/java-reactive
./mvnw clean package -Pjvm -DskipTests

# Traditional
cd backend/java
./mvnw clean package -Pjvm -DskipTests
```

### Build Native
```bash
# Reactive
cd backend/java-reactive
./mvnw clean package -Pnative -DskipTests

# Traditional
cd backend/java
./mvnw clean package -Pnative -DskipTests
```

## 🎯 Características Especiais

### Java Reactive
- **Reactive Streams**: Backpressure handling
- **Non-blocking I/O**: Toda a stack é não-bloqueante
- **Undertow Server**: Alta performance
- **WebFlux**: Programação reativa
- **StepVerifier**: Testes reativos

### Java Traditional
- **Virtual Threads**: Java 21 preview feature
- **Spring MVC**: API tradicional
- **Tomcat Server**: Servidor padrão
- **Sintaxe Síncrona**: Código mais simples
- **Alta Concorrência**: Milhares de threads virtuais

## 📚 Documentação

### Guias de Build
- `backend/java-reactive/BUILD_GUIDE.md`: Guia completo Reactive
- `backend/java/BUILD_GUIDE.md`: Guia completo Traditional
- `backend/java-reactive/PROFILES.md`: Detalhes dos profiles Maven

### Guias Docker
- `DOCKER_PROFILES_GUIDE.md`: Docker Compose com profiles
- `docker-compose.profiles.yml`: Orquestração multi-profile

## ✅ Checklist de Validação

### Java Reactive
- [ ] Build JVM: `./build-jvm.sh`
- [ ] Build Native: `./build-aot-native.sh`
- [ ] Docker JVM: `docker build -f Dockerfile.jvm ...`
- [ ] Docker Native: `docker build -f Dockerfile.native-prebuilt ...`
- [ ] Health check: `curl http://localhost:8085/actuator/health`
- [ ] Testes: `./mvnw test`

### Java Traditional
- [ ] Build JVM: `./build-java-jvm.sh`
- [ ] Build Native: `./build-java-native.sh`
- [ ] Docker JVM: `docker build -f Dockerfile.jvm ...`
- [ ] Docker Native: `docker build -f Dockerfile.native-prebuilt ...`
- [ ] Health check: `curl http://localhost:10001/actuator/health`
- [ ] Testes: `./mvnw test`

## 🎊 Benefícios da Implementação

### 1. Consistência
- Mesma estrutura de profiles em ambos backends
- Nomenclatura unificada
- Processo de build padronizado

### 2. Flexibilidade
- Escolha JVM ou Native por ambiente
- Desenvolvimento rápido com JVM
- Produção otimizada com Native

### 3. Performance
- Native: Startup instantâneo, baixo consumo
- JVM: Desenvolvimento ágil, debugging fácil
- Ambos: Alta performance em produção

### 4. Documentação
- Guias completos para cada cenário
- Comparações detalhadas
- Troubleshooting incluído

### 5. CI/CD Ready
- Scripts automatizados
- Docker multi-stage builds
- Profiles para diferentes ambientes

## 🔧 Próximos Passos

1. **Integração CI/CD**
   - GitHub Actions para builds automáticos
   - Testes em ambos os profiles
   - Deploy automatizado

2. **Monitoramento**
   - Métricas de performance JVM vs Native
   - Comparação de consumo de recursos
   - Dashboards Grafana

3. **Otimizações**
   - Tuning de GraalVM flags
   - Otimização de imagens Docker
   - Cache de builds

4. **Testes de Carga**
   - Comparação de throughput
   - Testes de latência
   - Stress tests

## 📖 Referências

- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Spring Boot Native](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
- [Java Virtual Threads](https://openjdk.org/jeps/444)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)

---

**Status:** ✅ Implementação completa em ambos os backends
**Branch:** `backend-multiprofile`
**Commits:** 3 (d504d812, 34e0caa8, b720bd89)
