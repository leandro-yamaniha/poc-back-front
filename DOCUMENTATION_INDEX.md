# 📚 Beauty Salon Management System - Documentation Index

**Última atualização:** 18 de Outubro de 2025

Este documento serve como índice central para toda a documentação do projeto, refletindo a estrutura atual após reorganização de diretórios.

---

## 📂 Estrutura de Documentação

### 🏠 Raiz do Projeto

- **[README.md](README.md)** - Visão geral do projeto, tecnologias, status
- **[DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)** - Este arquivo (índice central)

### 📖 Documentação Geral (`/docs/`)

- **[docs/README.md](docs/README.md)** - Índice da documentação técnica
- **[docs/PERFORMANCE_TEST_RESULTS.md](docs/PERFORMANCE_TEST_RESULTS.md)** - Resultados completos de performance

### 🔧 Backend (`/backend/`)

#### Backend Principal
- **[backend/README.md](backend/README.md)** - Visão geral de todos os backends
- **[backend/docs/README.md](backend/docs/README.md)** - Documentação técnica dos backends

#### Backend .NET (Port 8081)
- **[backend/dotnet/README.md](backend/dotnet/README.md)** - ASP.NET Core + SQL Server
- **[backend/dotnet/install-dotnet.sh](backend/dotnet/install-dotnet.sh)** - Script de instalação .NET

#### Backend Go (Port 8084)
- **[backend/go/README.md](backend/go/README.md)** - Gin + PostgreSQL
- **[backend/go/scripts/start.sh](backend/go/scripts/start.sh)** - Iniciar backend Go

#### Backend Java (Port 8080)
- **Backend/java/** - Spring Boot tradicional

#### Backend Java Reactive (Port 8085) 🏆
- **[backend/java-reactive/README.md](backend/java-reactive/README.md)** - Spring WebFlux + Cassandra
- **Performance:** 30,000+ RPS (Champion)

#### Backend Node.js (Port 8083)
- **[backend/nodejs/README.md](backend/nodejs/README.md)** - Express + MongoDB
- **[backend/nodejs/scripts/test-migrations-with-docker.sh](backend/nodejs/scripts/test-migrations-with-docker.sh)** - Testes de migração

#### Backend Python (Port 8082)
- **[backend/python/README.md](backend/python/README.md)** - FastAPI + PostgreSQL

---

## 🛠️ Ferramentas (`/tools/`)

### Índice de Ferramentas
- **[tools/README.md](tools/README.md)** - Visão geral das ferramentas disponíveis

### SonarQube (Análise de Código)
- **[tools/sonarqube/README.md](tools/sonarqube/README.md)** - Guia completo do SonarQube
- **Scripts:**
  - `tools/sonarqube/start-sonarqube-local.sh` - Iniciar SonarQube local
  - `tools/sonarqube/stop-sonarqube-local.sh` - Parar SonarQube local
  - `tools/sonarqube/sonar-analysis.sh` - Análise remota
  - `tools/sonarqube/sonar-analysis-local.sh` - Análise local

### Stress Test (Performance)
- **[tools/stress-test/README.md](tools/stress-test/README.md)** - Guia completo de stress testing
- **Scripts:**
  - `tools/stress-test/start-stress-test.sh` - Iniciar ambiente para testes
  - Ver também: `backend/java/scripts/run-stress-tests.sh`

---

## 🗄️ Banco de Dados (`/database/`)

- **[database/connect-cassandra.sh](database/connect-cassandra.sh)** - Conectar ao Cassandra via cqlsh
- **[database/docker-entrypoint.sh](database/docker-entrypoint.sh)** - Script de inicialização Docker

---

## 🎨 Frontend (`/frontend/`)

- **Frontend React** (Port 3000)
- **Scripts:**
  - `frontend/scripts/run-e2e-tests.sh` - Testes E2E

---

## 📊 Mapa de Portas

| Serviço | Porta | Tecnologia | Documentação |
|---------|-------|------------|--------------|
| **Frontend** | 3000 | React | - |
| **.NET Backend** | 8081 | ASP.NET Core | [Docs](backend/dotnet/README.md) |
| **Python Backend** | 8082 | FastAPI | [Docs](backend/python/README.md) |
| **Node.js Backend** | 8083 | Express | [Docs](backend/nodejs/README.md) |
| **Go Backend** | 8084 | Gin | [Docs](backend/go/README.md) |
| **Java Reactive** 🏆 | 8085 | Spring WebFlux | [Docs](backend/java-reactive/README.md) |
| **SonarQube** | 9000 | Code Quality | [Docs](tools/sonarqube/README.md) |
| **Cassandra** | 9042 | Database | - |
| **MongoDB** | 27017 | Database | - |
| **PostgreSQL** | 5432 | Database | - |
| **SQL Server** | 1433 | Database | - |

---

## 🚀 Quick Start por Tecnologia

### .NET Backend
```bash
cd backend/dotnet
./install-dotnet.sh          # Instalar .NET (primeira vez)
docker-compose up -d         # Iniciar
```

### Java Reactive Backend (Recomendado)
```bash
cd backend/java-reactive
./mvnw spring-boot:run       # Iniciar
```

### Node.js Backend
```bash
cd backend/nodejs
npm install
npm start
```

### Python Backend
```bash
cd backend/python
pip install -r requirements.txt
uvicorn main:app --reload
```

### Go Backend
```bash
cd backend/go
./scripts/start.sh
```

---

## 🔍 Análise de Código (SonarQube)

### Iniciar SonarQube Local
```bash
cd tools/sonarqube
./start-sonarqube-local.sh
# Acesse: http://localhost:9000
```

### Executar Análise
```bash
cd tools/sonarqube
./sonar-analysis-local.sh
```

**Documentação:** [tools/sonarqube/README.md](tools/sonarqube/README.md)

---

## 🔥 Testes de Performance

### Executar Stress Test
```bash
cd tools/stress-test
./start-stress-test.sh       # Iniciar ambiente
# Em outro terminal:
cd backend/java/scripts
./run-stress-tests.sh        # Executar testes
```

**Documentação:** [tools/stress-test/README.md](tools/stress-test/README.md)

**Resultados:** [docs/PERFORMANCE_TEST_RESULTS.md](docs/PERFORMANCE_TEST_RESULTS.md)

---

## 📋 Checklist de Documentação

### ✅ Documentação Atualizada
- [x] README principal
- [x] Backend .NET incluído
- [x] Estrutura de diretórios atualizada
- [x] Scripts reorganizados
- [x] Tools/SonarQube documentado
- [x] Tools/Stress-test documentado
- [x] Mapa de portas completo

### ⚠️ Documentação a Revisar
- [ ] README.md principal - incluir .NET
- [ ] backend/README.md - atualizar estrutura
- [ ] backend/docs/README.md - adicionar .NET
- [ ] docs/README.md - verificar links
- [ ] docs/PERFORMANCE_TEST_RESULTS.md - adicionar .NET

---

## 🎯 Estrutura Atual do Projeto

```
beauty-salon-app/
├── README.md                           # Visão geral do projeto
├── DOCUMENTATION_INDEX.md              # Este arquivo
│
├── backend/                            # Todos os backends
│   ├── README.md                       # Visão geral dos backends
│   ├── docs/README.md                  # Documentação técnica
│   ├── dotnet/                         # .NET Backend (Port 8081)
│   │   ├── README.md
│   │   └── install-dotnet.sh
│   ├── go/                             # Go Backend (Port 8084)
│   │   ├── README.md
│   │   └── scripts/start.sh
│   ├── java/                           # Java Spring Boot (Port 8080)
│   ├── java-reactive/                  # Java Reactive (Port 8085) 🏆
│   │   └── README.md
│   ├── nodejs/                         # Node.js (Port 8083)
│   │   ├── README.md
│   │   └── scripts/test-migrations-with-docker.sh
│   └── python/                         # Python (Port 8082)
│       └── README.md
│
├── frontend/                           # React Frontend (Port 3000)
│   └── scripts/run-e2e-tests.sh
│
├── database/                           # Scripts de banco de dados
│   ├── connect-cassandra.sh
│   └── docker-entrypoint.sh
│
├── docs/                               # Documentação geral
│   ├── README.md
│   └── PERFORMANCE_TEST_RESULTS.md
│
└── tools/                              # Ferramentas de desenvolvimento
    ├── README.md
    ├── sonarqube/                      # Análise de código
    │   ├── README.md
    │   ├── start-sonarqube-local.sh
    │   ├── stop-sonarqube-local.sh
    │   ├── sonar-analysis.sh
    │   └── sonar-analysis-local.sh
    └── stress-test/                    # Testes de performance
        ├── README.md
        └── start-stress-test.sh
```

---

## 🔗 Links Rápidos

### Documentação Técnica
- [Visão Geral dos Backends](backend/README.md)
- [Documentação Técnica Backends](backend/docs/README.md)
- [Resultados de Performance](docs/PERFORMANCE_TEST_RESULTS.md)

### Ferramentas
- [SonarQube - Análise de Código](tools/sonarqube/README.md)
- [Stress Test - Performance](tools/stress-test/README.md)

### Backends Específicos
- [.NET Backend](backend/dotnet/README.md)
- [Go Backend](backend/go/README.md)
- [Java Reactive Backend 🏆](backend/java-reactive/README.md)
- [Node.js Backend](backend/nodejs/README.md)
- [Python Backend](backend/python/README.md)

---

## 📝 Convenções de Documentação

### Localização de Arquivos

1. **README.md na raiz de cada diretório** - Visão geral do componente
2. **docs/** - Documentação técnica detalhada
3. **tools/** - Documentação de ferramentas
4. **Scripts .sh** - Junto com o código que executam

### Nomenclatura

- **README.md** - Documentação principal
- **NOME_EM_CAPS.md** - Documentação especial/índice
- **nome-kebab-case.md** - Documentação técnica

---

## 🏆 Performance Champion

**Java Reactive Backend (Port 8085)**
- 30,000+ RPS
- Latência < 20ms (p99)
- 100% testes passando (190/190)
- Documentação: [backend/java-reactive/README.md](backend/java-reactive/README.md)

---

**📚 Documentação organizada e atualizada!**
