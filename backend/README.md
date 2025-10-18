# Backend Directory

Este diretório contém todas as implementações de backend para o sistema de gerenciamento de salão de beleza.

## 📁 Estrutura Organizada

```
backend/
├── java/              # Java Spring Boot 2.x (tradicional)
├── java-reactive/     # Java Spring Boot 3.x + WebFlux (reativo)
├── dotnet/           # .NET Core 8.0 + Entity Framework
├── go/               # Go + Gin Framework
├── nodejs/           # Node.js + Express
└── python/           # Python + FastAPI
```

## 🚀 Backends Disponíveis

### 1. **Java Tradicional** (`java/`)
- **Framework**: Spring Boot 3.5.4 + Java 21
- **Arquitetura**: MVC tradicional
- **Banco**: Cassandra
- **Características**: Estabilidade, maturidade
- **Porta**: 8081
- **Documentação**: [backend/java/README.md](java/README.md)

### 2. **Java Reactive** (`java-reactive/`)
- **Framework**: Spring Boot 3.5.4 + Java 21
- **Arquitetura**: WebFlux (Reactive Streams)
- **Banco**: Cassandra Reactive
- **Características**: Não-bloqueante, alta concorrência
- **Porta**: 8085
- **Documentação**: [backend/java-reactive/README.md](java-reactive/README.md)

### 3. **.NET Core** (`dotnet/`)
- **Framework**: ASP.NET Core 8.0
- **ORM**: Entity Framework Core
- **Banco**: In-Memory (desenvolvimento)
- **Características**: Performance, tipagem forte
- **Porta**: 5063
- **Documentação**: [backend/dotnet/README.md](dotnet/README.md)

### 4. **Go** (`go/`)
- **Framework**: Gin Web Framework
- **ORM**: GORM
- **Banco**: PostgreSQL
- **Características**: Performance nativa, concorrência
- **Porta**: 8082
- **Documentação**: [backend/go/README.md](go/README.md)

### 5. **Node.js** (`nodejs/`)
- **Framework**: Express.js
- **ORM**: Sequelize
- **Banco**: PostgreSQL
- **Características**: JavaScript, grande ecossistema
- **Porta**: 8083
- **Documentação**: [backend/nodejs/README.md](nodejs/README.md)

### 6. **Python** (`python/`)
- **Framework**: FastAPI
- **ORM**: SQLAlchemy
- **Banco**: PostgreSQL
- **Características**: Desenvolvimento rápido, tipagem
- **Porta**: 8084
- **Documentação**: [backend/python/README.md](python/README.md)

## 🔧 Como Executar

### Todos os Backends
```bash
# Usando Docker Compose (recomendado)
docker-compose up -d

# Todos os backends iniciam automaticamente
# Cada um na sua porta específica
```

### Backend Específico

#### Java Tradicional
```bash
cd backend/java
./mvnw spring-boot:run
# ou
docker-compose -f docker-compose-java.yml up
```

#### Java Reactive
```bash
cd backend/java-reactive
./mvnw spring-boot:run
# ou
docker-compose -f docker-compose-reactive.yml up
```

#### .NET Core
```bash
cd backend/dotnet/BeautySalonAPI
export PATH="$HOME/.dotnet:$PATH"
dotnet run
# ou
docker-compose -f docker-compose-dotnet.yml up
```

#### Go
```bash
cd backend/go
go run main.go
# ou
docker-compose -f docker-compose-go.yml up
```

#### Node.js
```bash
cd backend/nodejs
npm install
npm start
# ou
docker-compose -f docker-compose-nodejs.yml up
```

#### Python
```bash
cd backend/python
pip install -r requirements.txt
python main.py
# ou
docker-compose -f docker-compose-python.yml up
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
| .NET Core | C# | ASP.NET Core | In-Memory | MVC | ⭐⭐⭐⭐ |
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
| .NET Core | ~3s | ~200MB | ⭐⭐⭐⭐ | ~20ms |
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

## 📚 Recursos Adicionais

- **Documentação Geral**: [../README.md](../README.md)
- **Guia de Deploy**: [../DEPLOYMENT_GUIDE.md](../DEPLOYMENT_GUIDE.md)
- **Comparação de Backends**: [../BACKEND_COMPARISON.md](../BACKEND_COMPARISON.md)
- **Testes de Performance**: [../PERFORMANCE_TEST_RESULTS.md](../PERFORMANCE_TEST_RESULTS.md)

---

**🎊 Todos os backends estão organizados e prontos para uso!**
