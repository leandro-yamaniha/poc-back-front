# 📋 Docker Compose - Resumo de Configurações

**Data:** October 22, 2025  
**Status:** ✅ Todos os backends padronizados

---

## 🎯 PADRONIZAÇÃO APLICADA

### **Estrutura Uniforme:**
Todos os backends agora têm:
- ✅ **Cassandra** (banco de dados)
- ✅ **Backend** (API)
- ✅ **Frontend** (React)

### **Porta Interna Unificada:**
- 🔒 **Backend:** Porta interna **8080** (todos)
- 🌐 **Frontend:** Porta interna **3000** (todos)
- 💾 **Cassandra:** Porta interna **9042** (todos)

---

## 📊 MAPEAMENTO DE PORTAS

### **Portas Externas (Host):**

| Backend | Backend Port | Frontend Port | Cassandra Port | Comando |
|---------|--------------|---------------|----------------|---------|
| **Java Traditional** | **10001** | **3001** | **9043** | `cd backend/java && docker-compose up` |
| **.NET Core** | **10002** | **3002** | **9044** | `cd backend/dotnet && docker-compose up` |
| **Python FastAPI** | **10003** | **3003** | **9045** | `cd backend/python && docker-compose up` |
| **Node.js Express** | **10004** | **3004** | **9046** | `cd backend/nodejs && docker-compose up` |
| **Go Gin** | **10005** | **3005** | **9047** | `cd backend/go && docker-compose up` |
| **Java Reactive** | **10006** | **3006** | **9048** | `cd backend/java-reactive && docker-compose up` |

---

## 🚀 COMO USAR

### **Iniciar um Backend Completo:**

```bash
# Java Traditional
cd backend/java
docker-compose up -d

# Acessar:
# - Backend API: http://localhost:10001/api
# - Frontend: http://localhost:3001
# - Cassandra: localhost:9043
```

### **Iniciar Múltiplos Backends:**

```bash
# Terminal 1 - Java
cd backend/java && docker-compose up

# Terminal 2 - .NET
cd backend/dotnet && docker-compose up

# Terminal 3 - Node.js
cd backend/nodejs && docker-compose up
```

✅ **Todos podem rodar simultaneamente** (portas diferentes)

---

## 🔧 VARIÁVEIS DE AMBIENTE

Cada docker-compose aceita variáveis para customizar portas:

```bash
# Exemplo: Mudar portas do .NET
BACKEND_HOST_PORT=11002 FRONTEND_HOST_PORT=4002 docker-compose up

# Customizar API URL
REACT_APP_API_URL=http://api.example.com docker-compose up
```

### **Variáveis Disponíveis:**

| Variável | Default | Descrição |
|----------|---------|-----------|
| `BACKEND_HOST_PORT` | Ver tabela acima | Porta externa do backend |
| `FRONTEND_HOST_PORT` | Ver tabela acima | Porta externa do frontend |
| `REACT_APP_API_URL` | Auto-gerada | URL da API para o frontend |

---

## 📦 VOLUMES ISOLADOS

Cada backend tem seu próprio volume Cassandra:

| Backend | Volume Name |
|---------|-------------|
| Java Traditional | `cassandra_data_java` |
| .NET Core | `cassandra_data_dotnet` |
| Python | `cassandra_data_python` |
| Node.js | `cassandra_data_nodejs` |
| Go | `cassandra_data_go` |
| Java Reactive | `cassandra_data_reactive` |

**Vantagem:** Dados isolados, sem conflito entre backends

---

## 🌐 NETWORKS ISOLADAS

Cada stack tem sua própria rede:

| Backend | Network Name |
|---------|--------------|
| Java Traditional | `beauty-salon-java` |
| .NET Core | `beauty-salon-dotnet` |
| Python | `beauty-salon-python` |
| Node.js | `beauty-salon-nodejs` |
| Go | `beauty-salon-go` |
| Java Reactive | `beauty-salon-reactive` |

**Vantagem:** Isolamento completo entre stacks

---

## ✅ HEALTH CHECKS

Todos os serviços têm health checks configurados:

### **Cassandra:**
```yaml
test: ["CMD-SHELL", "cqlsh -e 'describe keyspaces'"]
interval: 30s
timeout: 10s
retries: 5
```

### **Backend:**
```yaml
test: ["CMD-SHELL", "curl -f http://localhost:8080/health"]
interval: 30s
timeout: 10s
retries: 5
start_period: 40s
```

### **Frontend:**
Depende do backend estar saudável (`condition: service_healthy`)

---

## 🎯 CASOS DE USO

### **1. Desenvolvimento Local:**
```bash
# Escolha um backend
cd backend/java
docker-compose up

# Desenvolva usando:
# - API: http://localhost:8080
# - Frontend: http://localhost:3001
```

### **2. Comparação de Performance:**
```bash
# Inicie múltiplos backends
cd backend/java && docker-compose up -d
cd backend/dotnet && docker-compose up -d
cd backend/go && docker-compose up -d

# Compare:
# - Java: http://localhost:10001
# - .NET: http://localhost:10002
# - Go: http://localhost:10005
```

### **3. Demonstração:**
```bash
# Inicie o mais rápido
cd backend/dotnet
docker-compose up -d

# Acesse: http://localhost:3002
```

---

## 🧹 LIMPEZA

### **Parar um Backend:**
```bash
cd backend/java
docker-compose down
```

### **Parar e Remover Volumes:**
```bash
docker-compose down -v
```

### **Limpar Tudo:**
```bash
# Parar todos os containers
docker stop $(docker ps -q)

# Remover volumes não usados
docker volume prune -f

# Remover networks não usadas
docker network prune -f
```

---

## 📊 RECURSOS NECESSÁRIOS

### **Por Backend (aproximado):**

| Backend | RAM | CPU | Disk |
|---------|-----|-----|------|
| Cassandra | 1GB | 2 cores | 500MB |
| Java Traditional | 512MB | 1 core | 100MB |
| .NET Core | 256MB | 1 core | 50MB |
| Python | 256MB | 1 core | 100MB |
| Node.js | 256MB | 1 core | 80MB |
| Go | 128MB | 1 core | 30MB |
| Java Reactive | 512MB | 1 core | 100MB |
| Frontend | 256MB | 1 core | 50MB |

### **Para Rodar Todos Simultaneamente:**
- **RAM:** ~6GB
- **CPU:** ~12 cores
- **Disk:** ~5GB

---

## 🔍 TROUBLESHOOTING

### **Porta já em uso:**
```bash
# Ver qual processo está usando
lsof -i :10001

# Matar processo
kill -9 <PID>

# Ou usar porta diferente
BACKEND_HOST_PORT=11001 docker-compose up
```

### **Cassandra não inicia:**
```bash
# Aumentar memória do Docker
# Docker Desktop → Settings → Resources → Memory: 4GB+

# Ou aumentar timeout
healthcheck:
  start_period: 120s
```

### **Frontend não conecta ao backend:**
```bash
# Verificar se backend está rodando
curl http://localhost:10001/health

# Verificar variável de ambiente
docker-compose config | grep REACT_APP_API_URL
```

---

## 📚 DOCUMENTAÇÃO ADICIONAL

### **Arquivos docker-compose.yml:**
- `backend/java/docker-compose.yml`
- `backend/dotnet/docker-compose.yml`
- `backend/python/docker-compose.yml`
- `backend/nodejs/docker-compose.yml`
- `backend/go/docker-compose.yml`
- `backend/java-reactive/docker-compose.yml`

### **Dockerfiles:**
- Cada backend tem seu `Dockerfile` no diretório raiz
- Frontend: `frontend/Dockerfile`

---

## ✅ CHECKLIST DE VERIFICAÇÃO

Antes de iniciar um backend, verifique:

- [ ] Docker e Docker Compose instalados
- [ ] Portas disponíveis (ver tabela de portas)
- [ ] Mínimo 2GB RAM disponível
- [ ] Diretório correto (`cd backend/<nome>`)
- [ ] Frontend existe em `../../frontend`

---

**Status:** ✅ **TODOS OS DOCKER-COMPOSE PADRONIZADOS**  
**Backends:** 6  
**Estrutura:** Cassandra + Backend + Frontend  
**Porta Interna:** 8080 (unificada)  
**Data:** October 22, 2025
