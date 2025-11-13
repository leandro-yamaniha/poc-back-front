# Docker Compose Guide - Beauty Salon Backends

Este guia explica como executar cada backend usando Docker Compose com suas respectivas instâncias do Cassandra.

## 📋 Índice

- [Java Backends](#java-backends)
- [Outros Backends](#outros-backends)
- [Portas Utilizadas](#portas-utilizadas)
- [Comandos Úteis](#comandos-úteis)

---

## Java Backends

Os backends Java possuem **2 versões** cada: JVM e Native.

### 1. Java Traditional - JVM

**Porta Backend:** 8080  
**Porta Cassandra:** 9042

```bash
cd backend/java

# Iniciar
docker-compose -f docker-compose.jvm.yml up -d

# Ver logs
docker-compose -f docker-compose.jvm.yml logs -f backend-java-jvm

# Parar
docker-compose -f docker-compose.jvm.yml down

# Parar e remover volumes
docker-compose -f docker-compose.jvm.yml down -v
```

**Características:**
- Executa via `java -jar beauty-salon-jvm.jar`
- Memória: 384-768MB
- Startup: ~30s
- Requer: `target/beauty-salon-jvm.jar`

### 2. Java Traditional - Native

**Porta Backend:** 8081  
**Porta Cassandra:** 9043

```bash
cd backend/java

# Iniciar
docker-compose -f docker-compose.native.yml up -d

# Ver logs
docker-compose -f docker-compose.native.yml logs -f backend-java-native

# Parar
docker-compose -f docker-compose.native.yml down -v
```

**Características:**
- Executa binário nativo `./beauty-salon`
- Memória: 128-256MB
- Startup: ~3s
- Requer: `target/beauty-salon` (executável nativo)

### 3. Java Reactive - JVM

**Porta Backend:** 8085  
**Porta Cassandra:** 9044

```bash
cd backend/java-reactive

# Iniciar
docker-compose -f docker-compose.jvm.yml up -d

# Ver logs
docker-compose -f docker-compose.jvm.yml logs -f backend-reactive-jvm

# Parar
docker-compose -f docker-compose.jvm.yml down -v
```

**Características:**
- Executa via `java -jar beauty-salon-reactive-jvm.jar`
- Memória: 384-768MB
- Startup: ~30s
- Requer: `target/beauty-salon-reactive-jvm.jar`

### 4. Java Reactive - Native

**Porta Backend:** 8086  
**Porta Cassandra:** 9045

```bash
cd backend/java-reactive

# Iniciar
docker-compose -f docker-compose.native.yml up -d

# Ver logs
docker-compose -f docker-compose.native.yml logs -f backend-reactive-native

# Parar
docker-compose -f docker-compose.native.yml down -v
```

**Características:**
- Executa binário nativo `./beauty-salon-reactive`
- Memória: 128-256MB
- Startup: ~3s
- Requer: `target/beauty-salon-reactive` (executável nativo)

---

## Outros Backends

### 5. Go Backend

**Porta Backend:** 8082  
**Porta Cassandra:** 9046

```bash
cd backend/go

# Iniciar
docker-compose -f docker-compose.simple.yml up -d

# Ver logs
docker-compose -f docker-compose.simple.yml logs -f backend-go

# Parar
docker-compose -f docker-compose.simple.yml down -v
```

**Características:**
- Executa binário nativo `./beauty-salon`
- Memória: 64-128MB
- Startup: ~2s
- Requer: `beauty-salon` (executável Go)

### 6. Python Backend

**Porta Backend:** 8000  
**Porta Cassandra:** 9047

```bash
cd backend/python

# Iniciar
docker-compose -f docker-compose.simple.yml up -d

# Ver logs
docker-compose -f docker-compose.simple.yml logs -f backend-python

# Parar
docker-compose -f docker-compose.simple.yml down -v
```

**Características:**
- Executa via `uvicorn app.main:app`
- Memória: 256-512MB
- Startup: ~15s
- Requer: código fonte + `requirements.txt`

### 7. Node.js Backend

**Porta Backend:** 3000  
**Porta Cassandra:** 9048

```bash
cd backend/nodejs

# Iniciar
docker-compose -f docker-compose.simple.yml up -d

# Ver logs
docker-compose -f docker-compose.simple.yml logs -f backend-nodejs

# Parar
docker-compose -f docker-compose.simple.yml down -v
```

**Características:**
- Executa via `node src/server.js`
- Memória: 256-512MB
- Startup: ~10s
- Requer: código fonte + `package.json`

### 8. .NET Backend

**Porta Backend:** 5001  
**Porta Cassandra:** 9049

```bash
cd backend/dotnet

# Iniciar
docker-compose -f docker-compose.simple.yml up -d

# Ver logs
docker-compose -f docker-compose.simple.yml logs -f backend-dotnet

# Parar
docker-compose -f docker-compose.simple.yml down -v
```

**Características:**
- Executa via `dotnet BeautySalon.dll`
- Memória: 256-512MB
- Startup: ~15s
- Requer: `bin/Release/net8.0/publish/`

---

## Portas Utilizadas

| Backend | Porta Backend | Porta Cassandra | Arquivo Docker Compose |
|---------|---------------|-----------------|------------------------|
| Java JVM | 8080 | 9042 | `java/docker-compose.jvm.yml` |
| Java Native | 8081 | 9043 | `java/docker-compose.native.yml` |
| Go | 8082 | 9046 | `go/docker-compose.simple.yml` |
| Java Reactive JVM | 8085 | 9044 | `java-reactive/docker-compose.jvm.yml` |
| Java Reactive Native | 8086 | 9045 | `java-reactive/docker-compose.native.yml` |
| Python | 8000 | 9047 | `python/docker-compose.simple.yml` |
| Node.js | 3000 | 9048 | `nodejs/docker-compose.simple.yml` |
| .NET | 5001 | 9049 | `dotnet/docker-compose.simple.yml` |

---

## Comandos Úteis

### Verificar Status de Todos os Backends

```bash
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

### Parar Todos os Backends

```bash
# Java
cd backend/java && docker-compose -f docker-compose.jvm.yml down -v
cd backend/java && docker-compose -f docker-compose.native.yml down -v

# Java Reactive
cd backend/java-reactive && docker-compose -f docker-compose.jvm.yml down -v
cd backend/java-reactive && docker-compose -f docker-compose.native.yml down -v

# Outros
cd backend/go && docker-compose -f docker-compose.simple.yml down -v
cd backend/python && docker-compose -f docker-compose.simple.yml down -v
cd backend/nodejs && docker-compose -f docker-compose.simple.yml down -v
cd backend/dotnet && docker-compose -f docker-compose.simple.yml down -v
```

### Testar Saúde do Backend

```bash
# Substituir PORT pela porta do backend
curl http://localhost:PORT/health
curl http://localhost:PORT/actuator/health  # Para Java backends
```

### Ver Logs em Tempo Real

```bash
# Exemplo para Java JVM
docker-compose -f docker-compose.jvm.yml logs -f

# Ver apenas logs do backend (não do Cassandra)
docker-compose -f docker-compose.jvm.yml logs -f backend-java-jvm
```

### Reiniciar um Backend

```bash
docker-compose -f docker-compose.jvm.yml restart backend-java-jvm
```

### Verificar Uso de Recursos

```bash
docker stats
```

---

## 🔧 Pré-requisitos

### Para Backends Java

**JVM:**
```bash
cd backend/java
./mvnw clean package -DskipTests
# Gera: target/beauty-salon-jvm.jar
```

**Native:**
```bash
cd backend/java
./mvnw clean package -Pnative -DskipTests
# Gera: target/beauty-salon
```

### Para Backends Java Reactive

**JVM:**
```bash
cd backend/java-reactive
./mvnw clean package -DskipTests
# Gera: target/beauty-salon-reactive-jvm.jar
```

**Native:**
```bash
cd backend/java-reactive
./mvnw clean package -Pnative -DskipTests
# Gera: target/beauty-salon-reactive
```

### Para Backend Go

```bash
cd backend/go
go build -o beauty-salon cmd/server/main.go
# Gera: beauty-salon
```

### Para Backend .NET

```bash
cd backend/dotnet
dotnet publish -c Release
# Gera: bin/Release/net8.0/publish/
```

### Para Backends Python e Node.js

Não requerem build prévio, pois usam código fonte diretamente.

---

## 📊 Comparação de Recursos

| Backend | Memória (MB) | Startup (s) | CPU (cores) | Tipo |
|---------|--------------|-------------|-------------|------|
| Java Native | 128-256 | ~3 | 0.25-0.5 | Nativo |
| Java JVM | 384-768 | ~30 | 0.5-1.0 | JVM |
| Java Reactive Native | 128-256 | ~3 | 0.25-0.5 | Nativo |
| Java Reactive JVM | 384-768 | ~30 | 0.5-1.0 | JVM |
| Go | 64-128 | ~2 | 0.25-0.5 | Nativo |
| Python | 256-512 | ~15 | 0.5-1.0 | Interpretado |
| Node.js | 256-512 | ~10 | 0.5-1.0 | Interpretado |
| .NET | 256-512 | ~15 | 0.5-1.0 | Compilado |

---

## 🎯 Recomendações

### Para Desenvolvimento
- Use versões **JVM** para facilitar debug
- Cada backend tem sua própria instância Cassandra

### Para Produção
- Use versões **Native** (Java e Go) para melhor performance
- Considere usar Cassandra compartilhado
- Configure limites de recursos apropriados

### Para Testes de Performance
- Execute apenas um backend por vez
- Aguarde 60s após iniciar Cassandra
- Use as versões Native para benchmarks

---

## 🐛 Troubleshooting

### Backend não inicia

1. Verifique se o binário/JAR existe:
```bash
ls -lh target/
```

2. Verifique logs do Cassandra:
```bash
docker-compose -f docker-compose.*.yml logs cassandra
```

3. Verifique se a porta está livre:
```bash
lsof -i :PORT
```

### Cassandra não fica saudável

- Aguarde pelo menos 60 segundos
- Verifique memória disponível no sistema
- Aumente `start_period` no healthcheck se necessário

### Erro de conexão com Cassandra

- Verifique se o Cassandra está healthy:
```bash
docker-compose -f docker-compose.*.yml ps
```

- Teste conexão manual:
```bash
docker exec -it beauty-salon-cassandra-* cqlsh
```

---

**Última atualização:** $(date)
