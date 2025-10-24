# 🔢 Padronização de Portas dos Backends

**Data:** October 22, 2025, 23:32  
**Mudança:** Portas externas e internas agora são iguais

---

## 🎯 MUDANÇA APLICADA

### **Antes (Portas Diferentes):**
```yaml
# Mapeamento: EXTERNA:INTERNA
ports:
  - "10001:8080"  # Porta externa diferente da interna
```

### **Agora (Portas Iguais):**
```yaml
# Mapeamento: MESMA_PORTA:MESMA_PORTA
ports:
  - "10001:10001"  # Porta externa = interna
```

---

## 📊 NOVA CONFIGURAÇÃO

| Backend | Porta Antiga | Nova Porta | Mudança |
|---------|--------------|------------|---------|
| **Java Traditional** | 10001:8080 | **10001:10001** | ✅ Padronizado |
| **.NET Core** | 10002:8080 | **10002:10002** | ✅ Padronizado |
| **Python FastAPI** | 10003:8000 | **10003:10003** | ✅ Padronizado |
| **Node.js Express** | 10004:8080 | **10004:10004** | ✅ Padronizado |
| **Go Gin** | 10005:8080 | **10005:10005** | ✅ Padronizado |
| **Java Reactive** | 10006:8080 | **10006:10006** | ✅ Padronizado |

---

## ✅ BENEFÍCIOS

### **1. Simplicidade:**
- ✅ Mesma porta interna e externa
- ✅ Mais fácil de lembrar
- ✅ Menos confusão em debug

### **2. Configuração Mais Clara:**
```yaml
# Antes
ports: "10001:8080"  # Qual é qual?

# Agora
ports: "10001:10001"  # Óbvio!
```

### **3. Health Checks Consistentes:**
```bash
# Tanto dentro quanto fora do container
curl http://localhost:10001/health
```

### **4. Logs Mais Claros:**
```
Server listening on port 10001
# Mesma porta em todo lugar!
```

---

## 🔧 ARQUIVOS ATUALIZADOS

### **Para cada backend:**

1. ✅ **docker-compose.yml**
   - Mapeamento de portas
   - Variáveis de ambiente (PORT, SERVER_PORT)
   - Health checks

2. ✅ **Dockerfile**
   - EXPOSE (porta exposta)
   - HEALTHCHECK (porta do health check)
   - CMD (porta da aplicação)

---

## 📋 DETALHES POR BACKEND

### **Java Traditional (10001):**
```yaml
# docker-compose.yml
ports: ["10001:10001"]
environment:
  - SERVER_PORT=10001

# Dockerfile
EXPOSE 10001
HEALTHCHECK CMD curl -f http://localhost:10001/actuator/health
```

### **.NET Core (10002):**
```yaml
# docker-compose.yml
ports: ["10002:10002"]
environment:
  - ASPNETCORE_URLS=http://+:10002

# Dockerfile
EXPOSE 10002
HEALTHCHECK CMD curl -f http://localhost:10002/health
```

### **Python FastAPI (10003):**
```yaml
# docker-compose.yml
ports: ["10003:10003"]

# Dockerfile
EXPOSE 10003
CMD ["uvicorn", "main:app", "--port", "10003"]
HEALTHCHECK CMD curl -f http://localhost:10003/api/customers
```

### **Node.js Express (10004):**
```yaml
# docker-compose.yml
ports: ["10004:10004"]
environment:
  - PORT=10004

# Dockerfile
EXPOSE 10004
HEALTHCHECK CMD curl -f http://localhost:10004/health
```

### **Go Gin (10005):**
```yaml
# docker-compose.yml
ports: ["10005:10005"]
environment:
  - SERVER_PORT=10005

# Dockerfile
EXPOSE 10005
HEALTHCHECK CMD wget ... http://localhost:10005/health
```

### **Java Reactive (10006):**
```yaml
# docker-compose.yml
ports: ["10006:10006"]
environment:
  - SERVER_PORT=10006

# Dockerfile
EXPOSE 10006
HEALTHCHECK CMD curl -f http://localhost:10006/actuator/health
```

---

## 🧪 TESTANDO

### **Verificar Configuração:**
```bash
# Verificar mapeamento de portas
docker-compose config | grep ports

# Verificar variáveis de ambiente
docker-compose config | grep -A5 environment
```

### **Teste Individual:**
```bash
# Iniciar um backend
cd backend/go
docker-compose up -d

# Testar acesso (mesma porta)
curl http://localhost:10005/health
docker exec backend-go curl http://localhost:10005/health
```

---

## 📊 COMPARAÇÃO

### **Acesso Externo (Host):**
```bash
# Antes
curl http://localhost:10001  # Porta externa

# Agora (igual!)
curl http://localhost:10001  # Porta externa
```

### **Acesso Interno (Container):**
```bash
# Antes
docker exec backend curl http://localhost:8080  # Porta interna DIFERENTE

# Agora (igual!)
docker exec backend curl http://localhost:10001  # Porta interna IGUAL
```

---

## 💡 VANTAGENS PRÁTICAS

### **1. Debug Mais Fácil:**
```bash
# Uma porta para lembrar
Backend Java → 10001 (sempre)
```

### **2. Documentação Simplificada:**
```markdown
# Antes
- Porta externa: 10001
- Porta interna: 8080
- Health check: 8080

# Agora
- Porta: 10001 (tudo)
```

### **3. Scripts Mais Simples:**
```bash
# Antes
EXTERNAL_PORT=10001
INTERNAL_PORT=8080
curl http://localhost:$EXTERNAL_PORT
docker exec backend curl http://localhost:$INTERNAL_PORT

# Agora
PORT=10001
curl http://localhost:$PORT
docker exec backend curl http://localhost:$PORT
```

---

## 🚀 IMPACTO

### **Para Desenvolvimento:**
- ✅ Mais intuitivo
- ✅ Menos configuração mental
- ✅ Menos erros

### **Para Testes:**
- ✅ Scripts mais simples
- ✅ Uma porta para testar
- ✅ Consistente com produção

### **Para Documentação:**
- ✅ Mais clara
- ✅ Menos confusão
- ✅ Exemplos mais diretos

---

## 📝 NOTAS

### **Faixas de Porta:**
- ✅ **10001-10006:** Backends
- ✅ **3001-3006:** Frontends
- ✅ **9043-9048:** Cassandra

### **Padrão de Numeração:**
1. **10001** - Java Traditional
2. **10002** - .NET Core
3. **10003** - Python FastAPI
4. **10004** - Node.js Express
5. **10005** - Go Gin
6. **10006** - Java Reactive

---

## ✅ CHECKLIST

- [x] ✅ Atualizar docker-compose.yml (6 backends)
- [x] ✅ Atualizar Dockerfile EXPOSE (6 backends)
- [x] ✅ Atualizar health checks (6 backends)
- [x] ✅ Atualizar variáveis de ambiente (6 backends)
- [x] ✅ Documentar mudanças
- [ ] ⏳ Testar cada backend
- [ ] ⏳ Atualizar documentação geral
- [ ] ⏳ Atualizar scripts de teste

---

**Status:** ✅ **PADRONIZAÇÃO COMPLETA**  
**Backends atualizados:** 6/6  
**Portas padronizadas:** 10001-10006  
**Padrão:** Externa = Interna
