# 🔥 Comprehensive Stress Test Plan - All Backends

## 📋 Overview

Plano completo de stress test para avaliar e comparar o desempenho de todos os 6 backends do Beauty Salon Management System sob diferentes condições de carga.

---

## 🎯 Objetivos

1. **Medir Performance**: RPS, latência, throughput de cada backend
2. **Identificar Limites**: Encontrar o breaking point de cada tecnologia
3. **Comparar Backends**: Ranking objetivo baseado em métricas reais
4. **Validar Escalabilidade**: Como cada backend se comporta sob carga crescente
5. **Detectar Problemas**: Memory leaks, timeouts, erros

---

## 🏗️ Backends a Testar

| # | Backend | Port | URL | Tecnologia |
|---|---------|------|-----|------------|
| 1 | .NET Core | 8081 | http://localhost:8081/api/customers | ASP.NET Core 8.0 |
| 2 | Python | 8082 | http://localhost:8082/api/customers | FastAPI + Uvicorn |
| 3 | Node.js | 8083 | http://localhost:8083/api/customers | Express.js |
| 4 | Go | 8084 | http://localhost:8084/api/v1/customers | Gin Framework |
| 5 | Java Reactive | 8085 | http://localhost:8085/api/customers | Spring WebFlux |

---

## 📊 Cenários de Teste

### Cenário 1: Light Load (Baseline)
- **Usuários Concorrentes**: 10
- **Duração**: 30 segundos
- **Objetivo**: Estabelecer baseline de performance
- **Esperado**: Latência mínima, 0 erros

### Cenário 2: Medium Load (Normal Traffic)
- **Usuários Concorrentes**: 50
- **Duração**: 30 segundos
- **Objetivo**: Simular tráfego normal
- **Esperado**: Performance estável

### Cenário 3: High Load (Peak Hours)
- **Usuários Concorrentes**: 100
- **Duração**: 30 segundos
- **Objetivo**: Simular horário de pico
- **Esperado**: Degradação mínima

### Cenário 4: Very High Load (Stress)
- **Usuários Concorrentes**: 200
- **Duração**: 30 segundos
- **Objetivo**: Testar limites
- **Esperado**: Identificar gargalos

### Cenário 5: Extreme Load (Breaking Point)
- **Usuários Concorrentes**: 500
- **Duração**: 30 segundos
- **Objetivo**: Encontrar breaking point
- **Esperado**: Possíveis erros/timeouts

---

## 🛠️ Ferramentas de Teste

### 1. wrk (Recomendado - Profissional)

**Instalação:**
```bash
# macOS
brew install wrk

# Linux (Ubuntu/Debian)
sudo apt-get install wrk

# Linux (CentOS/RHEL)
sudo yum install wrk
```

**Uso Básico:**
```bash
# Sintaxe
wrk -t<threads> -c<connections> -d<duration> --latency <url>

# Exemplo: 100 conexões, 4 threads, 30 segundos
wrk -t4 -c100 -d30s --latency http://localhost:8081/api/customers
```

**Vantagens:**
- ✅ Muito rápido e eficiente
- ✅ Suporta Lua scripts para testes complexos
- ✅ Métricas detalhadas de latência
- ✅ Baixo overhead

### 2. Apache Bench (ab) - Alternativa

**Instalação:**
```bash
# macOS (já vem instalado)
ab -V

# Linux
sudo apt-get install apache2-utils
```

**Uso Básico:**
```bash
# Sintaxe
ab -n <requests> -c <concurrency> <url>

# Exemplo: 1000 requests, 100 concorrentes
ab -n 1000 -c 100 http://localhost:8081/api/customers
```

### 3. JMeter (GUI - Testes Complexos)

**Instalação:**
```bash
# macOS
brew install jmeter

# Linux
wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
tar -xzf apache-jmeter-5.6.3.tgz
```

**Uso:**
```bash
# Iniciar GUI
jmeter

# Modo CLI (headless)
jmeter -n -t test-plan.jmx -l results.jtl
```

**Vantagens:**
- ✅ Interface gráfica
- ✅ Testes complexos (POST, PUT, DELETE)
- ✅ Gráficos e relatórios visuais
- ✅ Suporta múltiplos protocolos

---

## 📝 Script Automatizado

### comprehensive-stress-test.sh

Script completo que testa todos os backends automaticamente.

**Uso:**
```bash
cd tools/stress-test
chmod +x comprehensive-stress-test.sh
./comprehensive-stress-test.sh
```

**O que o script faz:**
1. ✅ Verifica se backends estão rodando
2. ✅ Faz warmup de cada backend
3. ✅ Executa 5 cenários de teste para cada backend
4. ✅ Coleta métricas: RPS, latência, erros
5. ✅ Gera relatório Markdown completo
6. ✅ Cria comparação entre backends

**Resultados:**
- Salvos em: `stress-test-results/comprehensive_report_YYYYMMDD_HHMMSS.md`

---

## 📊 Métricas Coletadas

### 1. RPS (Requests per Second)
- **Descrição**: Número de requisições processadas por segundo
- **Importância**: Métrica principal de throughput
- **Bom**: > 1,000 RPS
- **Excelente**: > 5,000 RPS
- **Excepcional**: > 10,000 RPS

### 2. Latência Média
- **Descrição**: Tempo médio de resposta
- **Importância**: Experiência do usuário
- **Bom**: < 50ms
- **Excelente**: < 10ms
- **Excepcional**: < 5ms

### 3. Latência p50 (Mediana)
- **Descrição**: 50% das requisições abaixo deste valor
- **Importância**: Performance típica
- **Bom**: < 30ms

### 4. Latência p99 (Percentil 99)
- **Descrição**: 99% das requisições abaixo deste valor
- **Importância**: Worst-case performance
- **Bom**: < 100ms
- **Excelente**: < 50ms
- **Excepcional**: < 20ms

### 5. Taxa de Erro
- **Descrição**: Porcentagem de requisições com erro
- **Importância**: Confiabilidade
- **Aceitável**: < 1%
- **Bom**: < 0.1%
- **Excelente**: 0%

### 6. Throughput (MB/s)
- **Descrição**: Volume de dados transferidos
- **Importância**: Capacidade de rede
- **Bom**: > 10 MB/s

---

## 🎯 Resultados Esperados

### Java Reactive (Champion Esperado)
```
Light Load (10):    30,000+ RPS, <5ms latency
Medium Load (50):   28,000+ RPS, <10ms latency
High Load (100):    25,000+ RPS, <15ms latency
Very High (200):    20,000+ RPS, <20ms latency
Extreme (500):      15,000+ RPS, <40ms latency
```

### .NET Core (Excellent)
```
Light Load (10):    10,000+ RPS, <2ms latency
Medium Load (50):   9,000+ RPS, <5ms latency
High Load (100):    8,000+ RPS, <10ms latency
Very High (200):    6,000+ RPS, <20ms latency
Extreme (500):      4,000+ RPS, <50ms latency
```

### Node.js (Excellent)
```
Light Load (10):    8,000+ RPS, <2ms latency
Medium Load (50):   7,000+ RPS, <5ms latency
High Load (100):    6,000+ RPS, <10ms latency
Very High (200):    4,000+ RPS, <20ms latency
Extreme (500):      2,000+ RPS, <50ms latency
```

### Go (Very Good)
```
Light Load (10):    5,000+ RPS, <3ms latency
Medium Load (50):   4,500+ RPS, <5ms latency
High Load (100):    4,000+ RPS, <10ms latency
Very High (200):    3,000+ RPS, <20ms latency
Extreme (500):      2,000+ RPS, <50ms latency
```

### Python (Good)
```
Light Load (10):    2,000+ RPS, <10ms latency
Medium Load (50):   1,500+ RPS, <15ms latency
High Load (100):    1,000+ RPS, <30ms latency
Very High (200):    500+ RPS, <60ms latency
Extreme (500):      300+ RPS, <100ms latency
```

---

## 🔬 Testes Avançados com JMeter

### Test Plan Structure

```
Test Plan
├── Thread Group (Users)
│   ├── Number of Threads: 100
│   ├── Ramp-up Period: 10s
│   └── Loop Count: 100
├── HTTP Request Defaults
│   ├── Server: localhost
│   └── Port: 8081-8085
├── HTTP Requests
│   ├── GET /api/customers
│   ├── POST /api/customers
│   ├── PUT /api/customers/{id}
│   └── DELETE /api/customers/{id}
├── Listeners
│   ├── View Results Tree
│   ├── Summary Report
│   ├── Aggregate Report
│   └── Response Time Graph
└── Assertions
    ├── Response Code: 200
    └── Response Time < 100ms
```

### JMeter Test Plan (XML)

Arquivo: `beauty-salon-stress-test.jmx` (pode ser criado via GUI)

**Cenários JMeter:**
1. **CRUD Operations Test**: Testa todas operações (GET, POST, PUT, DELETE)
2. **Read-Heavy Test**: 90% GET, 10% outros
3. **Write-Heavy Test**: 50% POST/PUT, 50% GET
4. **Mixed Workload**: Distribuição realista

---

## 📈 Análise de Resultados

### Critérios de Avaliação

#### Performance (40%)
- RPS sob carga alta (100 users)
- Latência p99
- Throughput

#### Escalabilidade (30%)
- Degradação de performance com aumento de carga
- Comportamento sob extreme load
- Recovery após pico

#### Confiabilidade (20%)
- Taxa de erro
- Estabilidade sob carga
- Consistência de performance

#### Eficiência (10%)
- Uso de memória
- Uso de CPU
- Recursos por requisição

### Ranking System

```
🏆 REVOLUTIONARY: >20,000 RPS, <20ms p99
⭐ EXCELLENT:     >5,000 RPS, <50ms p99
✅ VERY GOOD:     >3,000 RPS, <100ms p99
✅ GOOD:          >1,000 RPS, <200ms p99
⚠️ FUNCTIONAL:    >100 RPS, <500ms p99
```

---

## 🚀 Como Executar

### Passo 1: Preparar Ambiente

```bash
# 1. Iniciar todos os backends
docker-compose up -d

# 2. Verificar se estão rodando
curl http://localhost:8081/api/customers  # .NET
curl http://localhost:8082/api/customers  # Python
curl http://localhost:8083/api/customers  # Node.js
curl http://localhost:8084/api/v1/customers  # Go
curl http://localhost:8085/api/customers  # Java Reactive

# 3. Instalar wrk (se necessário)
brew install wrk  # macOS
```

### Passo 2: Executar Teste Automatizado

```bash
cd tools/stress-test
chmod +x comprehensive-stress-test.sh
./comprehensive-stress-test.sh
```

### Passo 3: Analisar Resultados

```bash
# Ver relatório
cat stress-test-results/comprehensive_report_*.md

# Ou abrir no navegador
open stress-test-results/comprehensive_report_*.md
```

### Passo 4: Testes Manuais (Opcional)

```bash
# Testar backend específico
wrk -t4 -c100 -d30s --latency http://localhost:8081/api/customers

# Comparar dois backends
wrk -t4 -c100 -d30s http://localhost:8081/api/customers > dotnet.txt
wrk -t4 -c100 -d30s http://localhost:8085/api/customers > java.txt
diff dotnet.txt java.txt
```

---

## 📊 Template de Relatório

### Estrutura do Relatório Gerado

```markdown
# Comprehensive Backend Stress Test Report

## Executive Summary
- Backends testados
- Melhor performance
- Recomendações

## Test Scenarios
- Descrição de cada cenário
- Parâmetros utilizados

## Results per Backend
### .NET Core
- Tabela de resultados
- Gráficos (se disponível)

### Python FastAPI
- Tabela de resultados

### Node.js Express
- Tabela de resultados

### Go Gin
- Tabela de resultados

### Java Reactive
- Tabela de resultados

## Comparison
- Ranking geral
- Comparação lado a lado
- Análise de trade-offs

## Key Findings
- Insights principais
- Surpresas
- Problemas encontrados

## Recommendations
- Para produção
- Para desenvolvimento
- Para casos específicos

## Test Environment
- Hardware
- Software
- Configurações
```

---

## 🔧 Troubleshooting

### Problema: "wrk: command not found"
**Solução:**
```bash
brew install wrk  # macOS
sudo apt-get install wrk  # Linux
```

### Problema: "Connection refused"
**Solução:**
```bash
# Verificar se backend está rodando
docker-compose ps
docker-compose logs <backend-name>

# Reiniciar backend
docker-compose restart <backend-name>
```

### Problema: "Too many open files"
**Solução:**
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

# 2. Reiniciar
docker-compose up -d

# 3. Aguardar warmup
sleep 30

# 4. Executar teste
./comprehensive-stress-test.sh
```

---

## 📚 Recursos Adicionais

### Documentação
- **wrk**: https://github.com/wg/wrk
- **Apache Bench**: https://httpd.apache.org/docs/2.4/programs/ab.html
- **JMeter**: https://jmeter.apache.org/usermanual/index.html

### Tutoriais
- **wrk Tutorial**: https://www.baeldung.com/wrk-benchmarking
- **JMeter Tutorial**: https://www.guru99.com/jmeter-tutorials.html

### Scripts Relacionados
- `comprehensive-stress-test.sh` - Script principal
- `tools/stress-test/README.md` - Documentação de stress test
- `docs/PERFORMANCE_TEST_RESULTS.md` - Resultados anteriores

---

## 🎯 Próximos Passos

1. ✅ Executar teste inicial com todos backends
2. ✅ Analisar resultados e identificar gargalos
3. ✅ Otimizar backends com baixa performance
4. ✅ Re-testar após otimizações
5. ✅ Documentar findings no README principal
6. ✅ Criar dashboard de monitoramento (opcional)
7. ✅ Integrar testes no CI/CD (opcional)

---

## 📝 Checklist de Execução

- [ ] Instalar wrk/ab
- [ ] Iniciar todos os backends
- [ ] Verificar conectividade
- [ ] Executar comprehensive-stress-test.sh
- [ ] Aguardar conclusão (~15-20 minutos)
- [ ] Analisar relatório gerado
- [ ] Documentar findings
- [ ] Compartilhar resultados com time

---

**🔥 Plano completo de stress test pronto para execução!**
