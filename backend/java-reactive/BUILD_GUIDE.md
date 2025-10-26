# Build Guide - JVM vs Native

Guia completo para compilar e executar a aplicação usando os profiles Maven.

## 📋 Profiles Disponíveis

### 1. JVM Profile (Padrão)
- Build rápido (~6 segundos)
- Ideal para desenvolvimento
- Requer JVM para executar

### 2. Native Profile
- Build lento (~3-4 minutos)
- Ideal para produção
- Executável standalone

## 🛠️ Scripts de Build

### Build JVM
```bash
cd backend/scripts
./build-jvm.sh
```

**Output:**
- `target/beauty-salon-reactive-jvm.jar` (~46MB)

**Executar:**
```bash
java -jar target/beauty-salon-reactive-jvm.jar
```

### Build Native
```bash
cd backend/scripts
./build-aot-native.sh
```

**Output:**
- `target/beauty-salon-reactive` (~135MB executável)

**Executar:**
```bash
./target/beauty-salon-reactive
```

## 🐳 Dockerfiles Disponíveis

### 1. Dockerfile.jvm
Build JVM tradicional com multi-stage

**Build:**
```bash
docker build -f Dockerfile.jvm -t beauty-salon-reactive:jvm .
```

**Run:**
```bash
docker run -p 8085:8085 beauty-salon-reactive:jvm
```

### 2. Dockerfile.native-profile
Build Native completo (compila dentro do Docker)

**Build:**
```bash
docker build -f Dockerfile.native-profile -t beauty-salon-reactive:native .
```

**Run:**
```bash
docker run -p 8085:8085 beauty-salon-reactive:native
```

### 3. Dockerfile.native-prebuilt
Usa executável nativo pré-compilado (mais rápido)

**Pré-requisito:**
```bash
./mvnw package -Pnative -DskipTests
```

**Build:**
```bash
docker build -f Dockerfile.native-prebuilt -t beauty-salon-reactive:native-prebuilt .
```

**Run:**
```bash
docker run -p 8085:8085 beauty-salon-reactive:native-prebuilt
```

### 4. Dockerfile.native-final
Build Native otimizado com Ubuntu runtime

**Build:**
```bash
docker build -f Dockerfile.native-final -t beauty-salon-reactive:native-final .
```

## 📊 Comparação

| Aspecto | JVM | Native Prebuilt | Native Full Build |
|---------|-----|-----------------|-------------------|
| **Build Local** | 6s | 3-4min | N/A |
| **Build Docker** | 2min | 30s | 4-5min |
| **Image Size** | ~200MB | ~150MB | ~150MB |
| **Startup** | 5-10s | 1-2s | 1-2s |
| **Memory** | 256-512MB | 128-256MB | 128-256MB |
| **Uso** | Dev | Prod (rápido) | Prod (completo) |

## 🚀 Workflows Recomendados

### Desenvolvimento Local
```bash
# Build rápido
./build-jvm.sh

# Executar
java -jar target/beauty-salon-reactive-jvm.jar
```

### Deploy Produção (Rápido)
```bash
# 1. Build nativo local
./build-aot-native.sh

# 2. Build Docker com executável pré-compilado
docker build -f Dockerfile.native-prebuilt -t beauty-salon-reactive:prod .

# 3. Deploy
docker run -d -p 8085:8085 beauty-salon-reactive:prod
```

### Deploy Produção (CI/CD)
```bash
# Build completo no Docker (não precisa de GraalVM local)
docker build -f Dockerfile.native-profile -t beauty-salon-reactive:prod .

# Deploy
docker run -d -p 8085:8085 beauty-salon-reactive:prod
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

### Executar Testes
```bash
# Com profile JVM (padrão)
./mvnw test

# Com profile Native (não recomendado - muito lento)
./mvnw test -Pnative
```

## 📝 Variáveis de Ambiente

### Comuns
```bash
SPRING_PROFILES_ACTIVE=docker
SPRING_DATA_CASSANDRA_CONTACT_POINTS=localhost
SERVER_PORT=8085
```

### JVM Específicas
```bash
JAVA_OPTS=-Xmx512m -Xms256m
```

### Native Específicas
```bash
# Nenhuma configuração JVM necessária
# O executável já está otimizado
```

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

### Docker Build Lento
```bash
# Usar build cache
docker build --cache-from beauty-salon-reactive:latest ...

# Ou usar executável pré-compilado
./mvnw package -Pnative -DskipTests
docker build -f Dockerfile.native-prebuilt ...
```

### Executável Native Não Inicia
```bash
# Verificar bibliotecas
ldd target/beauty-salon-reactive

# Testar com verbose
./target/beauty-salon-reactive --verbose
```

## 📚 Referências

- [Maven Profiles](../PROFILES.md)
- [Docker Profiles](../../DOCKER_PROFILES_GUIDE.md)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)

## ✅ Checklist de Validação

- [ ] Build JVM funciona: `./build-jvm.sh`
- [ ] JAR executa: `java -jar target/beauty-salon-reactive-jvm.jar`
- [ ] Build Native funciona: `./build-aot-native.sh`
- [ ] Executável Native funciona: `./target/beauty-salon-reactive`
- [ ] Docker JVM funciona: `docker build -f Dockerfile.jvm ...`
- [ ] Docker Native funciona: `docker build -f Dockerfile.native-prebuilt ...`
- [ ] Health check responde: `curl http://localhost:8085/actuator/health`

Agora você tem controle total sobre o processo de build! 🎯
