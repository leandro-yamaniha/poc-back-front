# 🧠 Memory Optimization Guide

Este guia documenta a parametrização de memória implementada em todos os Dockerfiles dos backends e como realizar testes de memória mínima.

## 📋 **Visão Geral**

Todos os backends agora suportam parametrização de memória através de build arguments, permitindo otimização para diferentes ambientes (desenvolvimento, teste, produção).

## 🔧 **Parametrização por Backend**

### **Java Tradicional** (`backend/java/`)

#### Build Arguments Disponíveis:
```dockerfile
ARG JAVA_MAX_MEMORY=1g        # Memória máxima da JVM
ARG JAVA_MIN_MEMORY=512m      # Memória inicial da JVM
ARG JAVA_OPTS=""              # Opções adicionais da JVM
```

#### Exemplo de Build:
```bash
docker build \
  --build-arg JAVA_MAX_MEMORY=512m \
  --build-arg JAVA_MIN_MEMORY=256m \
  --build-arg JAVA_OPTS="-XX:+UseG1GC" \
  -t beauty-salon-java-optimized \
  backend/java/
```

### **Java Reactive** (`backend/java-reactive/`)

#### Build Arguments Disponíveis:
```dockerfile
ARG JAVA_MAX_MEMORY=1g        # Memória máxima da JVM
ARG JAVA_MIN_MEMORY=512m      # Memória inicial da JVM
ARG JAVA_OPTS=""              # Opções adicionais da JVM
```

#### Exemplo de Build:
```bash
docker build \
  --build-arg JAVA_MAX_MEMORY=768m \
  --build-arg JAVA_MIN_MEMORY=384m \
  -t beauty-salon-java-reactive-optimized \
  backend/java-reactive/
```

### **.NET** (`backend/dotnet/`)

#### Build Arguments Disponíveis:
```dockerfile
ARG DOTNET_MAX_MEMORY=1g      # Limite de heap do .NET
ARG DOTNET_GC_SERVER=1        # Habilitar GC de servidor
ARG DOTNET_GC_CONCURRENT=1    # Habilitar GC concorrente
```

#### Exemplo de Build:
```bash
docker build \
  --build-arg DOTNET_MAX_MEMORY=512m \
  --build-arg DOTNET_GC_SERVER=0 \
  -t beauty-salon-dotnet-optimized \
  backend/dotnet/
```

### **Node.js** (`backend/nodejs/`)

#### Build Arguments Disponíveis:
```dockerfile
ARG NODE_MAX_MEMORY=1024           # Memória total (MB)
ARG NODE_MAX_OLD_SPACE_SIZE=1024   # Memória do V8 (MB)
```

#### Exemplo de Build:
```bash
docker build \
  --build-arg NODE_MAX_OLD_SPACE_SIZE=512 \
  -t beauty-salon-nodejs-optimized \
  backend/nodejs/
```

### **Python** (`backend/python/`)

#### Build Arguments Disponíveis:
```dockerfile
ARG PYTHON_MAX_MEMORY=1g      # Memória máxima (informativo)
ARG UVICORN_WORKERS=1         # Número de workers do Uvicorn
```

#### Exemplo de Build:
```bash
docker build \
  --build-arg UVICORN_WORKERS=2 \
  -t beauty-salon-python-optimized \
  backend/python/
```

### **Go** (`backend/go/`)

#### Características:
- **Binário nativo**: Não requer parametrização específica de memória
- **Controle via Docker**: Use `--memory` no `docker run`
- **Eficiência**: Geralmente consome menos memória que outras linguagens

#### Exemplo de Execução:
```bash
docker run --memory=256m beauty-salon-go
```

## 🧪 **Teste de Memória Mínima**

### **Script Automatizado**

Execute o script de teste para encontrar os limites mínimos de memória:

```bash
./scripts/test-memory-limits.sh
```

#### O que o script testa:
- **4 configurações de memória**: minimal, low, medium, high
- **Todos os backends**: Java, Java Reactive, .NET, Node.js, Python, Go
- **Verificações**: Build, startup, health check
- **Limpeza automática**: Remove containers e imagens de teste

### **Configurações de Teste**

#### Java (Tradicional e Reactive)
| Configuração | Max Memory | Min Memory |
|--------------|------------|------------|
| minimal      | 256m       | 128m       |
| low          | 512m       | 256m       |
| medium       | 1g         | 512m       |
| high         | 2g         | 1g         |

#### .NET
| Configuração | Max Memory |
|--------------|------------|
| minimal      | 256m       |
| low          | 512m       |
| medium       | 1g         |
| high         | 2g         |

#### Node.js
| Configuração | Old Space Size (MB) |
|--------------|---------------------|
| minimal      | 256                 |
| low          | 512                 |
| medium       | 1024                |
| high         | 2048                |

#### Python
| Configuração | Max Memory |
|--------------|------------|
| minimal      | 256m       |
| low          | 512m       |
| medium       | 1g         |
| high         | 2g         |

#### Go
| Configuração | Docker Memory Limit |
|--------------|---------------------|
| minimal      | 256m                |
| low          | 512m                |
| medium       | 1g                  |
| high         | 2g                  |

## 📊 **Recomendações de Memória**

### **Ambiente de Desenvolvimento**
```bash
# Java/Java Reactive
--build-arg JAVA_MAX_MEMORY=1g --build-arg JAVA_MIN_MEMORY=512m

# .NET
--build-arg DOTNET_MAX_MEMORY=1g

# Node.js
--build-arg NODE_MAX_OLD_SPACE_SIZE=1024

# Python
--build-arg UVICORN_WORKERS=1

# Go
--memory=512m (no docker run)
```

### **Ambiente de Produção**
```bash
# Java/Java Reactive
--build-arg JAVA_MAX_MEMORY=2g --build-arg JAVA_MIN_MEMORY=1g

# .NET
--build-arg DOTNET_MAX_MEMORY=2g

# Node.js
--build-arg NODE_MAX_OLD_SPACE_SIZE=2048

# Python
--build-arg UVICORN_WORKERS=4

# Go
--memory=1g (no docker run)
```

### **Ambiente de Teste/CI**
```bash
# Java/Java Reactive
--build-arg JAVA_MAX_MEMORY=512m --build-arg JAVA_MIN_MEMORY=256m

# .NET
--build-arg DOTNET_MAX_MEMORY=512m

# Node.js
--build-arg NODE_MAX_OLD_SPACE_SIZE=512

# Python
--build-arg UVICORN_WORKERS=1

# Go
--memory=256m (no docker run)
```

## 🚀 **Uso com Docker Compose**

### **Exemplo de docker-compose.yml com Parametrização**

```yaml
version: '3.8'
services:
  backend-java:
    build:
      context: ./backend/java
      args:
        JAVA_MAX_MEMORY: ${JAVA_MAX_MEMORY:-1g}
        JAVA_MIN_MEMORY: ${JAVA_MIN_MEMORY:-512m}
    deploy:
      resources:
        limits:
          memory: ${JAVA_DOCKER_MEMORY:-1.5g}
    ports:
      - "10001:10001"

  backend-dotnet:
    build:
      context: ./backend/dotnet
      args:
        DOTNET_MAX_MEMORY: ${DOTNET_MAX_MEMORY:-1g}
    deploy:
      resources:
        limits:
          memory: ${DOTNET_DOCKER_MEMORY:-1.5g}
    ports:
      - "10002:10002"

  backend-nodejs:
    build:
      context: ./backend/nodejs
      args:
        NODE_MAX_OLD_SPACE_SIZE: ${NODE_MAX_OLD_SPACE_SIZE:-1024}
    deploy:
      resources:
        limits:
          memory: ${NODE_DOCKER_MEMORY:-1.5g}
    ports:
      - "10004:10004"
```

### **Arquivo .env para Configuração**

```bash
# Java Configuration
JAVA_MAX_MEMORY=1g
JAVA_MIN_MEMORY=512m
JAVA_DOCKER_MEMORY=1.5g

# .NET Configuration
DOTNET_MAX_MEMORY=1g
DOTNET_DOCKER_MEMORY=1.5g

# Node.js Configuration
NODE_MAX_OLD_SPACE_SIZE=1024
NODE_DOCKER_MEMORY=1.5g

# Python Configuration
UVICORN_WORKERS=2
PYTHON_DOCKER_MEMORY=1g

# Go Configuration
GO_DOCKER_MEMORY=512m
```

## 🔍 **Monitoramento de Memória**

### **Comandos Úteis**

#### Verificar uso de memória dos containers:
```bash
docker stats --no-stream
```

#### Verificar limites de memória:
```bash
docker inspect <container_name> | grep -i memory
```

#### Logs de OutOfMemory:
```bash
docker logs <container_name> | grep -i "out of memory\|oom"
```

### **Sinais de Problemas de Memória**

#### Java:
- `OutOfMemoryError: Java heap space`
- `OutOfMemoryError: GC overhead limit exceeded`

#### .NET:
- `OutOfMemoryException`
- Container restart frequente

#### Node.js:
- `FATAL ERROR: Ineffective mark-compacts near heap limit`
- `JavaScript heap out of memory`

#### Python:
- `MemoryError`
- Workers sendo mortos pelo OOM killer

#### Go:
- `runtime: out of memory`
- Container sendo morto pelo sistema

## 📈 **Otimização Avançada**

### **Java/Java Reactive**
```bash
# Otimizações adicionais via JAVA_OPTS
--build-arg JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler"
```

### **.NET**
```bash
# Configurações de GC otimizadas
--build-arg DOTNET_GC_SERVER=1
--build-arg DOTNET_GC_CONCURRENT=1
```

### **Node.js**
```bash
# Otimizações do V8
NODE_OPTIONS="--max-old-space-size=1024 --optimize-for-size"
```

## 🎯 **Resultados Esperados**

Após executar `./scripts/test-memory-limits.sh`, você deve ver:

- **✅ Configurações que funcionam**: Build, startup e health check bem-sucedidos
- **⚠️ Configurações limítrofes**: Build e startup ok, mas health check pode falhar
- **❌ Configurações insuficientes**: Falha no build, startup ou crash do container

Use essas informações para determinar a configuração mínima viável para cada ambiente.

## 📚 **Recursos Adicionais**

- [Scripts de Automação](../scripts/README.md)
- [Guia de Build](BUILD_GUIDE.md)
- [Configuração de Portas](configuration/PORT_CONFIGURATION_SUMMARY.md)

---

**🧠 Otimização de memória implementada para máxima eficiência em todos os ambientes!**
