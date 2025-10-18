# 🔥 Stress Test Tools - Beauty Salon Management System

Ferramentas e scripts para testes de carga e performance dos backends.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Scripts Disponíveis](#scripts-disponíveis)
- [Instalação de Ferramentas](#instalação-de-ferramentas)
- [Como Executar](#como-executar)
- [Interpretando Resultados](#interpretando-resultados)
- [Cenários de Teste](#cenários-de-teste)
- [Melhores Práticas](#melhores-práticas)

---

## 🎯 Visão Geral

Este diretório contém scripts e documentação para realizar testes de stress e performance nos backends do Beauty Salon Management System.

### Backends Testados

| Backend | Porta | Tecnologia | Performance Esperada |
|---------|-------|------------|---------------------|
| **Java Reactive** | 8085 | Spring WebFlux + Undertow | 🏆 30,000+ RPS |
| **Node.js** | 8083 | Express + MongoDB | ✅ 5,000-8,000 RPS |
| **Python** | 8082 | FastAPI + PostgreSQL | ✅ 3,000-5,000 RPS |
| **Go** | 8084 | Gin + PostgreSQL | ✅ 8,000-12,000 RPS |
| **.NET** | 8081 | ASP.NET Core + SQL Server | ✅ 6,000-10,000 RPS |

---

## 📜 Scripts Disponíveis

### 1. **stress-test-reactive.sh** (Localização: `/scripts/`)

Script completo para teste de stress do backend reativo.

**Características:**
- ✅ Múltiplos cenários de carga (10, 50, 100, 200, 500 usuários)
- ✅ Testa todos os endpoints principais
- ✅ Gera relatórios detalhados em Markdown
- ✅ Usa `wrk` (ferramenta profissional)

**Localização:**
```bash
/scripts/stress-test-reactive.sh
```

**Uso:**
```bash
cd scripts
chmod +x stress-test-reactive.sh
./stress-test-reactive.sh
```

**Resultados salvos em:**
```
performance-test-results/
└── stress_test_report_YYYYMMDD_HHMMSS.md
```

---

## 🛠️ Instalação de Ferramentas

### wrk (Recomendado - Profissional)

**macOS:**
```bash
brew install wrk
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install wrk
```

**Linux (CentOS/RHEL):**
```bash
sudo yum install wrk
```

**Verificar instalação:**
```bash
wrk --version
```

### Apache Bench (ab) - Alternativa

**macOS:**
```bash
# Já vem instalado com Apache
ab -V
```

**Linux:**
```bash
sudo apt-get install apache2-utils
```

### curl (Testes Básicos)

**Já instalado na maioria dos sistemas:**
```bash
curl --version
```

---

## 🚀 Como Executar

### Teste Completo (Recomendado)

```bash
# 1. Certifique-se que o backend está rodando
docker-compose up -d backend-java-reactive

# 2. Execute o script de stress test
cd scripts
./stress-test-reactive.sh

# 3. Aguarde conclusão (pode levar 5-10 minutos)

# 4. Veja os resultados
cat performance-test-results/stress_test_report_*.md
```

### Teste Manual com wrk

#### Teste Básico
```bash
# 10 conexões, 2 threads, 30 segundos
wrk -t2 -c10 -d30s http://localhost:8085/api/customers
```

#### Teste de Carga Média
```bash
# 100 conexões, 4 threads, 30 segundos
wrk -t4 -c100 -d30s http://localhost:8085/api/customers
```

#### Teste de Carga Alta
```bash
# 500 conexões, 8 threads, 60 segundos
wrk -t8 -c500 -d60s http://localhost:8085/api/customers
```

#### Teste com Script Lua (POST)
```bash
# Criar arquivo post.lua
cat > post.lua << 'EOF'
wrk.method = "POST"
wrk.body   = '{"name":"Test Customer","email":"test@example.com","phone":"1234567890"}'
wrk.headers["Content-Type"] = "application/json"
EOF

# Executar teste
wrk -t4 -c100 -d30s -s post.lua http://localhost:8085/api/customers
```

### Teste Manual com Apache Bench

```bash
# Teste GET simples
ab -n 1000 -c 10 http://localhost:8085/api/customers

# Teste com mais concorrência
ab -n 10000 -c 100 http://localhost:8085/api/customers

# Teste POST
ab -n 1000 -c 10 -p data.json -T application/json http://localhost:8085/api/customers
```

### Teste Manual com curl

```bash
# Teste de latência simples
time curl http://localhost:8085/api/customers

# Teste com múltiplas requisições
for i in {1..100}; do
  curl -s http://localhost:8085/api/customers > /dev/null
done
```

---

## 📊 Interpretando Resultados

### Métricas do wrk

```
Running 30s test @ http://localhost:8085/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.50ms    1.20ms   50.00ms   95.00%
    Req/Sec     8.00k     1.50k    12.00k    75.00%
  960000 requests in 30.00s, 500.00MB read
Requests/sec:  32000.00
Transfer/sec:     16.67MB
```

**O que significa:**

| Métrica | Descrição | Bom | Excelente |
|---------|-----------|-----|-----------|
| **Latency (Avg)** | Tempo médio de resposta | < 50ms | < 10ms |
| **Req/Sec** | Requisições por segundo | > 1000 | > 10000 |
| **Failed requests** | Requisições com erro | 0 | 0 |
| **Stdev** | Desvio padrão (consistência) | Baixo | Muito baixo |

### Distribuição de Latência

```
Latency Distribution
  50%    2.00ms    # Metade das requisições em 2ms
  75%    3.00ms    # 75% das requisições em 3ms
  90%    5.00ms    # 90% das requisições em 5ms
  99%   10.00ms    # 99% das requisições em 10ms
```

**Interpretação:**
- ✅ **Excelente**: 99% < 20ms
- ✅ **Bom**: 99% < 50ms
- ⚠️ **Aceitável**: 99% < 100ms
- ❌ **Ruim**: 99% > 100ms

### Sinais de Problemas

❌ **Problemas Comuns:**
- **Failed requests > 0**: Erros no servidor
- **Latency muito alta**: Gargalo de performance
- **Stdev muito alto**: Performance inconsistente
- **Timeout errors**: Servidor sobrecarregado

---

## 🎯 Cenários de Teste

### 1. Teste de Carga Leve (Baseline)

**Objetivo:** Estabelecer baseline de performance

```bash
wrk -t2 -c10 -d30s http://localhost:8085/api/customers
```

**Esperado:**
- Latência: < 5ms
- RPS: > 5,000
- Erros: 0

### 2. Teste de Carga Média (Uso Normal)

**Objetivo:** Simular uso normal em produção

```bash
wrk -t4 -c50 -d60s http://localhost:8085/api/customers
```

**Esperado:**
- Latência: < 10ms
- RPS: > 10,000
- Erros: 0

### 3. Teste de Carga Alta (Pico)

**Objetivo:** Simular pico de tráfego

```bash
wrk -t4 -c100 -d60s http://localhost:8085/api/customers
```

**Esperado:**
- Latência: < 20ms
- RPS: > 15,000
- Erros: 0

### 4. Teste de Stress (Limite)

**Objetivo:** Encontrar limite do sistema

```bash
wrk -t8 -c500 -d60s http://localhost:8085/api/customers
```

**Esperado:**
- Latência: < 50ms
- RPS: > 20,000
- Erros: < 1%

### 5. Teste de Endurance (Longa Duração)

**Objetivo:** Verificar memory leaks e degradação

```bash
wrk -t4 -c100 -d600s http://localhost:8085/api/customers
```

**Esperado:**
- Performance estável durante 10 minutos
- Sem degradação de latência
- Memória estável

---

## 🎓 Melhores Práticas

### Antes de Testar

1. ✅ **Ambiente Limpo**
   ```bash
   docker-compose down
   docker-compose up -d
   ```

2. ✅ **Dados de Teste**
   - Popule o banco com dados realistas
   - Use dataset consistente entre testes

3. ✅ **Monitoramento**
   ```bash
   # Terminal 1: Logs do backend
   docker-compose logs -f backend-java-reactive
   
   # Terminal 2: Recursos do sistema
   docker stats
   ```

4. ✅ **Baseline**
   - Execute teste leve primeiro
   - Estabeleça métricas de referência

### Durante o Teste

1. ✅ **Não interferir**
   - Não use o sistema durante o teste
   - Feche aplicações pesadas

2. ✅ **Monitorar**
   - Observe logs em tempo real
   - Verifique uso de CPU/memória

3. ✅ **Documentar**
   - Anote condições do teste
   - Registre configurações

### Depois do Teste

1. ✅ **Analisar Resultados**
   - Compare com baseline
   - Identifique gargalos

2. ✅ **Salvar Relatórios**
   ```bash
   cp performance-test-results/*.md docs/performance/
   ```

3. ✅ **Limpar**
   ```bash
   docker-compose down
   docker system prune -f
   ```

---

## 📈 Comparação de Backends

### Resultados Típicos (100 conexões, 30s)

| Backend | RPS | Latência (p99) | Memória/Conn | Status |
|---------|-----|----------------|--------------|--------|
| **Java Reactive** | 30,000+ | < 20ms | ~2KB | 🏆 Champion |
| **Go** | 10,000+ | < 30ms | ~5KB | ✅ Excelente |
| **.NET** | 8,000+ | < 40ms | ~8KB | ✅ Muito Bom |
| **Node.js** | 6,000+ | < 50ms | ~15KB | ✅ Bom |
| **Python** | 4,000+ | < 60ms | ~20KB | ✅ Aceitável |

---

## 🔧 Troubleshooting

### Problema: "Connection refused"

**Solução:**
```bash
# Verificar se backend está rodando
docker-compose ps

# Verificar logs
docker-compose logs backend-java-reactive

# Reiniciar
docker-compose restart backend-java-reactive
```

### Problema: "Too many open files"

**Solução (macOS/Linux):**
```bash
# Aumentar limite de file descriptors
ulimit -n 10000

# Verificar
ulimit -n
```

### Problema: Performance inconsistente

**Solução:**
```bash
# 1. Limpar cache
docker-compose down
docker system prune -f

# 2. Reiniciar com recursos dedicados
docker-compose up -d

# 3. Aguardar warmup (30s)
sleep 30

# 4. Executar teste
./stress-test-reactive.sh
```

### Problema: wrk não instalado

**Solução:**
```bash
# macOS
brew install wrk

# Linux
sudo apt-get install wrk

# Verificar
wrk --version
```

---

## 📚 Recursos Adicionais

### Documentação Oficial

- **wrk**: https://github.com/wg/wrk
- **Apache Bench**: https://httpd.apache.org/docs/2.4/programs/ab.html
- **Spring WebFlux**: https://docs.spring.io/spring-framework/reference/web/webflux.html

### Resultados Completos

Ver documentação completa de resultados:
```bash
cat docs/PERFORMANCE_TEST_RESULTS.md
```

### Scripts Relacionados

- `/scripts/stress-test-reactive.sh` - Script principal
- `/scripts/start-reactive-stack.sh` - Iniciar ambiente
- `/scripts/connect-cassandra.sh` - Debug do banco

---

## 🎯 Objetivos de Performance

### Java Reactive Backend (Port 8085)

| Métrica | Objetivo | Atual |
|---------|----------|-------|
| RPS (100 conn) | > 20,000 | ✅ 30,000+ |
| Latência p99 | < 50ms | ✅ < 20ms |
| Erros | 0% | ✅ 0% |
| Memória/Conn | < 5KB | ✅ ~2KB |
| Uptime | 99.9% | ✅ 100% |

### Outros Backends

Consulte `docs/PERFORMANCE_TEST_RESULTS.md` para objetivos específicos de cada backend.

---

## 🚀 Quick Start

```bash
# 1. Instalar wrk
brew install wrk  # macOS
# ou
sudo apt-get install wrk  # Linux

# 2. Iniciar backend
cd /path/to/beauty-salon-app
docker-compose up -d backend-java-reactive

# 3. Executar teste
cd scripts
./stress-test-reactive.sh

# 4. Ver resultados
ls -la performance-test-results/
```

---

**🏆 Performance Champion: Java Reactive Backend - 30,000+ RPS!**
