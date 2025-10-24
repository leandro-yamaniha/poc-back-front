# Backend Directory

Este diretório contém todas as implementações de backend para o sistema de gerenciamento de salão de beleza.

## 📁 Estrutura Organizada

```
backend/
├── java/              # Java Spring Boot 3.5.4 (tradicional)
├── java-reactive/     # Java Spring Boot 3.5.4 + WebFlux (reativo)
├── dotnet/           # .NET Core 8.0 + Entity Framework
├── go/               # Go + Gin Framework
├── nodejs/           # Node.js + Express
└── python/           # Python + FastAPI
```

## 🚀 Backends Disponíveis

### 1. **Java Tradicional** (`java/`) ✅
- **Framework**: Spring Boot 3.5.4 + Java 21
- **Arquitetura**: MVC tradicional com Spring Boot Actuator
- **Banco**: Cassandra (porta 9043)
- **Características**: Estabilidade, maturidade, dados de exemplo completos
- **Porta**: 10001
- **Health Check**: `/actuator/health`
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/java/README.md](java/README.md)

### 2. **Java Reactive** (`java-reactive/`) ✅
- **Framework**: Spring Boot 3.5.4 + Java 21 + WebFlux
- **Arquitetura**: Reactive Streams (Mono/Flux)
- **Banco**: Cassandra Reactive (porta 9048)
- **Características**: Não-bloqueante, alta concorrência, Undertow
- **Porta**: 10006
- **Health Check**: `/actuator/health`
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/java-reactive/README.md](java-reactive/README.md)

### 3. **.NET** (`dotnet/`) ✅
- **Framework**: ASP.NET Core 8.0
- **Linguagem**: C# 12 (.NET 8.0 LTS)
- **Driver**: Cassandra C# Driver
- **Banco**: Cassandra (porta 9044)
- **Características**: 
  - Enterprise-grade architecture
  - Native async/await pattern
  - Built-in Dependency Injection
  - Health endpoint integration
  - Cassandra distributed database
- **Porta**: 10002
- **Health Check**: `/health`
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/dotnet/README.md](dotnet/README.md)

### 4. **Python** (`python/`) ✅
- **Framework**: FastAPI + Uvicorn
- **Driver**: Cassandra Python Driver
- **Banco**: Cassandra (porta 9045)
- **Características**: Desenvolvimento rápido, tipagem, async/await, Swagger UI
- **Porta**: 10003
- **Health Check**: Simplificado (sem psutil)
- **API Docs**: `/api/docs` (Swagger UI)
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/python/README.md](python/README.md)

### 5. **Node.js** (`nodejs/`) ✅
- **Framework**: Express.js
- **Driver**: Cassandra Node.js Driver
- **Banco**: Cassandra (porta 9046)
- **Características**: JavaScript, grande ecossistema, dados de exemplo
- **Porta**: 10004
- **Health Check**: `/health`
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/nodejs/README.md](nodejs/README.md)

### 6. **Go** (`go/`) ✅
- **Framework**: Go nativo com HTTP server
- **Driver**: Cassandra Go Driver
- **Banco**: Cassandra (porta 9047)
- **Características**: Performance nativa, concorrência, dados de exemplo
- **Porta**: 10005
- **Health Check**: `/health`
- **Status**: ✅ 100% Operacional
- **Documentação**: [backend/go/README.md](go/README.md)

## 🔧 Como Executar

### 🚀 Scripts Automatizados (Recomendado)

```bash
# Testar builds de todos os backends
./test-backends-quick.sh

# Buildar todos os backends
./build-all.sh

# Testar startup completo (sequencial)
./test-backends-sequential.sh
```

### 🐳 Backend Específico (Docker Isolado)

#### Java Tradicional (Porta 10001)
```bash
cd backend/java
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: http://localhost:10001/actuator/health
# API: http://localhost:10001/api/customers
```

#### Java Reactive (Porta 10006)
```bash
cd backend/java-reactive
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: http://localhost:10006/actuator/health
# API: http://localhost:10006/api/customers
```

#### .NET (Porta 10002)
```bash
cd backend/dotnet
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: http://localhost:10002/health
# API: http://localhost:10002/api/customers
```

#### Python (Porta 10003)
```bash
cd backend/python
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: Simplificado
# API: http://localhost:10003/api/customers/
# Docs: http://localhost:10003/api/docs
```

#### Node.js (Porta 10004)
```bash
cd backend/nodejs
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: http://localhost:10004/health
# API: http://localhost:10004/api/customers
```

#### Go (Porta 10005)
```bash
cd backend/go
./build.sh
docker-compose -f docker-compose-backend-only.yml up -d
# Health: http://localhost:10005/health
# API: http://localhost:10005/api/customers
```

## 🔗 Endpoints Comuns

Todos os backends implementam os mesmos endpoints:

### Health Check
- `GET /health` - Verificar status do serviço

### Customers (Clientes)
- `GET /api/customer` - Listar todos
- `GET /api/customer/{id}` - Buscar por ID
- `POST /api/customer` - Criar novo
- `PUT /api/customer/{id}` - Atualizar
- `DELETE /api/customer/{id}` - Deletar

### Services (Serviços)
- `GET /api/service` - Listar todos
- `GET /api/service/active` - Apenas ativos
- `POST /api/service` - Criar novo
- `PUT /api/service/{id}` - Atualizar

### Staff (Funcionários)
- `GET /api/staff` - Listar todos
- `GET /api/staff/active` - Apenas ativos
- `POST /api/staff` - Criar novo

### Appointments (Agendamentos)
- `GET /api/appointment` - Listar todos
- `POST /api/appointment` - Criar novo
- `PUT /api/appointment/{id}` - Atualizar

## 📋 Comparação de Características

| Backend | Linguagem | Framework | Banco | Arquitetura | Performance |
|---------|-----------|-----------|-------|-------------|-------------|
| Java | Java 21 | Spring Boot 3.5.4 | Cassandra | MVC | ⭐⭐⭐⭐ |
| Java Reactive | Java 21 | Spring WebFlux | Cassandra | Reativa | ⭐⭐⭐⭐⭐ |
| .NET Core | C# 12 | ASP.NET Core 8.0 | Cassandra | MVC + DI | ⭐⭐⭐⭐ |
| Go | Go | Gin | PostgreSQL | Concorrente | ⭐⭐⭐⭐⭐ |
| Node.js | JavaScript | Express | PostgreSQL | Event-driven | ⭐⭐⭐ |
| Python | Python | FastAPI | PostgreSQL | Assíncrona | ⭐⭐⭐⭐ |

## 🧪 Testes

Cada backend possui sua própria suíte de testes:

### Executar Testes
```bash
# Java Tradicional
cd backend/java && ./mvnw test

# Java Reactive
cd backend/java-reactive && ./mvnw test

# .NET Core
cd backend/dotnet && dotnet test

# Go
cd backend/go && go test ./...

# Node.js
cd backend/nodejs && npm test

# Python
cd backend/python && pytest
```

## 🚀 Deploy

### Docker Compose (Todos os Backends)
```bash
docker-compose up -d --build
```

### Docker Compose Individual
```bash
# Apenas Java Reactive
docker-compose -f docker-compose-reactive.yml up -d

# Apenas .NET
docker-compose -f docker-compose-dotnet.yml up -d

# Apenas Go
docker-compose -f docker-compose-go.yml up -d

# Etc...
```

## 📊 Métricas de Performance

| Backend | Startup Time | Memory Usage | Throughput | Latency |
|---------|-------------|--------------|------------|---------|
| Java | ~15s | ~400MB | ⭐⭐⭐ | ~50ms |
| Java Reactive | ~12s | ~350MB | ⭐⭐⭐⭐⭐ | ~10ms |
| .NET Core | ~3s | ~200MB | ⭐⭐⭐⭐ | ~1.0-1.7ms |
| Go | ~1s | ~50MB | ⭐⭐⭐⭐⭐ | ~5ms |
| Node.js | ~2s | ~150MB | ⭐⭐⭐ | ~30ms |
| Python | ~2s | ~100MB | ⭐⭐⭐ | ~40ms |

## 🔧 Desenvolvimento

Para contribuir com qualquer backend:

1. Escolha o backend desejado
2. Entre no diretório específico: `cd backend/{tecnologia}`
3. Siga as instruções do README individual
4. Execute testes antes de commitar
5. Atualize documentação se necessário

## 📈 Status dos Backends

| Backend | Status | Testes | Cobertura | Documentação |
|---------|--------|--------|-----------|--------------|
| Java | ✅ Estável | 195/195 | 85%+ | ✅ Completa |
| Java Reactive | ✅ Estável | 190/190 | 90%+ | ✅ Completa |
| .NET Core | ✅ Estável | 2/2 | 70%+ | ✅ Completa |
| Go | ✅ Estável | - | - | ✅ Completa |
| Node.js | ✅ Estável | - | - | ✅ Completa |
| Python | ✅ Estável | - | - | ✅ Completa |

## 🎯 Escolhendo um Backend

### Para Produção de Alta Performance
- **Recomendado**: Java Reactive ou Go
- **Motivo**: Melhor throughput e menor latência

### Para Desenvolvimento Rápido
- **Recomendado**: Python (FastAPI) ou Node.js
- **Motivo**: Desenvolvimento mais ágil

### Para Aplicações Enterprise
- **Recomendado**: Java Reactive ou .NET Core
- **Motivo**: Maturidade, suporte, ferramentas

### Para Microserviços
- **Recomendado**: Go ou .NET Core
- **Motivo**: Baixo consumo de memória, startup rápido

## 📚 **Documentação Completa**

Para documentação detalhada, consulte o **[📖 Índice de Documentação](docs/README.md)** que contém:

### 🚀 **Guias e Tutoriais**
- [🔧 Guia de Build](docs/guides/BUILD_GUIDE.md) - Como buildar todos os backends
- [📜 Guia de Scripts](docs/guides/BUILD_SCRIPTS_GUIDE.md) - Scripts automatizados

### ⚙️ **Configuração e Deploy**
- [🔌 Configuração de Portas](docs/configuration/PORT_CONFIGURATION_SUMMARY.md)
- [🐳 Migração Dockerfile](docs/deployment/DOCKERFILE_MIGRATION.md)

### 📋 **Documentação por Backend**
- **Java**: [Tradicional](java/README.md) | [Reactive](java-reactive/README.md)
- **Outros**: [.NET](dotnet/README.md) | [Go](go/README.md) | [Node.js](nodejs/README.md) | [Python](python/README.md)

---

**🎊 Todos os backends estão organizados e prontos para uso!**
