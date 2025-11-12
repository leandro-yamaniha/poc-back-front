# Comparação: Go vs Node.js - Teste de Carga

**Data:** 26 de Outubro de 2025  
**Configuração:** 1 CPU, 512MB RAM (equivalente)  
**Duração:** 10 segundos  
**Ferramenta:** wrk

---

## 📊 Resultados Comparativos

### Backend Go

**Configuração do Teste:**
- Conexões: 50 concorrentes
- Threads: 2
- Endpoint: `/api/customers`

**Performance:**
- ✅ **Throughput:** 6,434 req/s
- ✅ **Latência Média:** 11.70ms
- ✅ **Total Requisições:** 64,451
- ✅ **Transfer Rate:** 4.67 MB/s
- ✅ **Taxa de Sucesso:** 100%

**Latência por Percentil:**
- P50: 5.60ms
- P75: 13.75ms
- P90: 33.42ms
- P99: 54.38ms

**Análise:**
- 🟢 Sem rate limiting
- 🟢 Performance consistente
- 🟢 Latência baixa e previsível
- 🟢 100% de sucesso nas requisições

---

### Backend Node.js

**Configuração do Teste:**
- Conexões: 10 concorrentes (limitado por rate limit)
- Threads: 2
- Endpoint: `/api/customers`

**Performance:**
- ⚠️ **Throughput:** 10,855 req/s (com rate limit)
- ✅ **Latência Média:** 3.17ms
- ⚠️ **Total Requisições:** 109,634 (todas bloqueadas)
- ⚠️ **Transfer Rate:** 10.97 MB/s
- ❌ **Taxa de Sucesso:** 0% (429 Too Many Requests)

**Latência por Percentil:**
- P50: 0.74ms
- P75: 1.39ms
- P90: 12.13ms
- P99: 27.71ms

**Análise:**
- 🔴 Rate limiting ativo (1000 req/min)
- 🟡 Latência excelente quando não bloqueado
- 🔴 Todas requisições retornaram 429
- 🟡 Throughput alto mas inútil devido ao rate limit

---

## 🔍 Análise Detalhada

### Vencedor: Go 🏆

**Motivos:**
1. **Sem Rate Limiting:** Pronto para alta carga
2. **100% Taxa de Sucesso:** Todas requisições processadas
3. **Performance Real:** 6.4k req/s reais vs 0 req/s úteis do Node.js
4. **Produção Ready:** Configuração adequada para load testing

### Node.js - Observações

**Pontos Positivos:**
- ✅ Latência muito baixa (3.17ms vs 11.70ms do Go)
- ✅ Throughput potencial alto (10.8k req/s)
- ✅ Segurança: Rate limiting ativo por padrão

**Pontos Negativos:**
- ❌ Rate limit muito restritivo para testes
- ❌ Configuração inadequada para benchmarking
- ❌ Necessita ajuste para testes de carga

**Recomendação:**
Para testes justos, o Node.js precisa de:
1. Desabilitar ou aumentar rate limit
2. Ajustar configuração para ambiente de teste
3. Reexecutar com mesmas condições do Go

---

## 📈 Comparação Visual

### Throughput (req/s)
```
Go:      ████████████████████████████████ 6,434 req/s ✅
Node.js: ████████████████████████████████████████████ 0 req/s úteis ❌
         (10,855 req/s bloqueados por rate limit)
```

### Latência Média
```
Go:      ████████████ 11.70ms
Node.js: ███ 3.17ms ✅ (mas todas bloqueadas)
```

### Taxa de Sucesso
```
Go:      ████████████████████████████████████████████████ 100% ✅
Node.js: 0% ❌
```

---

## 🎯 Próximos Passos

### 1. Corrigir Node.js para Testes
```javascript
// Desabilitar rate limit em src/app.js ou config
// Ou aumentar limite para testes:
const rateLimit = require('express-rate-limit');
const limiter = rateLimit({
  windowMs: 1 * 60 * 1000,
  max: 100000, // Aumentar para testes
  standardHeaders: true,
  legacyHeaders: false,
});
```

### 2. Subir Mais Backends
```bash
# Java JVM
docker-compose -f docker-compose.loadtest.yml up -d java-jvm

# Java Reactive JVM
docker-compose -f docker-compose.loadtest.yml up -d java-reactive-jvm

# Python
docker-compose -f docker-compose.loadtest.yml up -d python-backend

# .NET
docker-compose -f docker-compose.loadtest.yml up -d dotnet-backend
```

### 3. Executar Teste Completo
```bash
cd backend/scripts
./load-test.sh
```

---

## 📝 Conclusões Preliminares

### Go - Pronto para Produção
- ✅ Configuração adequada para alta carga
- ✅ Performance excelente e consistente
- ✅ Sem limitações artificiais
- ✅ Ideal para benchmarking

### Node.js - Necessita Ajustes
- ⚠️ Rate limiting precisa ser ajustado
- ⚠️ Latência potencialmente melhor que Go
- ⚠️ Throughput potencial alto
- ❌ Não comparável no estado atual

### Recomendação
1. **Ajustar Node.js** para testes justos
2. **Reexecutar** com mesmas condições
3. **Comparar** resultados reais
4. **Testar** outros backends (Java, Python, .NET)

---

## 🎊 Status Atual

### Backends Funcionando
- ✅ Cassandra (Database)
- ✅ Go (Port 8080) - **TESTADO COM SUCESSO**
- ✅ Node.js (Port 3000) - **TESTADO (com rate limit)**

### Próximos a Testar
- ⏸️ Java Traditional JVM
- ⏸️ Java Reactive JVM
- ⏸️ Python FastAPI
- ⏸️ .NET Core

### Backends Native (Requerem Build)
- ⏸️ Java Traditional Native
- ⏸️ Java Reactive Native

---

**Nota:** Esta comparação será atualizada após ajustes no Node.js e testes dos demais backends.
