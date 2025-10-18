# 🔥 Complete Backend Benchmark Guide

Guia completo para executar benchmarks individuais com métricas detalhadas de cada backend.

---

## 📋 Metodologia

### Premissas do Benchmark

1. **🗄️ Banco de Dados Limpo**: Cada backend é testado com banco de dados recriado do zero
2. **⚡ Medição de Startup**: Tempo desde o start do container até primeira resposta bem-sucedida
3. **📊 Recursos Monitorados**: CPU e memória durante os testes
4. **🔥 Stress Test**: 5 cenários de carga (10, 50, 100, 200, 500 usuários)
5. **🔒 Isolamento**: Cada backend testado independentemente

---

## 🚀 Scripts Disponíveis

### 1. **full-benchmark.sh** - Benchmark Completo ⭐

Testa TODOS os 6 backends sequencialmente com banco limpo para cada um.

**Características:**
- ✅ Recria banco de dados para cada backend
- ✅ Mede tempo de startup
- ✅ Monitora CPU e memória
- ✅ Executa 5 cenários de stress test
- ✅ Gera relatório comparativo final
- ✅ Relatórios individuais por backend

**Tempo estimado:** ~30-40 minutos

**Uso:**
```bash
cd tools/stress-test
./full-benchmark.sh
```

**Resultados:**
```
stress-test-results/
├── full_benchmark_YYYYMMDD_HHMMSS.md  # Relatório comparativo
├── java-spring_YYYYMMDD_HHMMSS.md     # Relatório individual
├── dotnet_YYYYMMDD_HHMMSS.md
├── python_YYYYMMDD_HHMMSS.md
├── nodejs_YYYYMMDD_HHMMSS.md
├── go_YYYYMMDD_HHMMSS.md
└── java-reactive_YYYYMMDD_HHMMSS.md
```

---

### 2. **benchmark-single.sh** - Benchmark Individual

Testa UM backend específico com banco limpo.

**Características:**
- ✅ Recria banco de dados
- ✅ Mede tempo de startup
- ✅ Monitora CPU e memória
- ✅ Executa 5 cenários de stress test
- ✅ Gera relatório detalhado

**Tempo estimado:** ~5 minutos

**Uso:**
```bash
cd tools/stress-test
./benchmark-single.sh <backend-key>
```

**Backends disponíveis:**
- `java-spring` - Java Spring Boot (Port 8080)
- `dotnet` - .NET Core (Port 8081)
- `python` - Python FastAPI (Port 8082)
- `nodejs` - Node.js Express (Port 8083)
- `go` - Go Gin (Port 8084)
- `java-reactive` - Java Reactive (Port 8085)

**Exemplos:**
```bash
# Testar Java Spring Boot
./benchmark-single.sh java-spring

# Testar .NET Core
./benchmark-single.sh dotnet

# Testar Java Reactive
./benchmark-single.sh java-reactive
```

---

## 📊 Métricas Coletadas

### 1. ⚡ Startup Metrics

| Métrica | Descrição | Importância |
|---------|-----------|-------------|
| **Startup Time** | Tempo do start até primeira resposta | Tempo de deploy/restart |
| **CPU Usage** | Uso de CPU após startup | Eficiência de recursos |
| **Memory Usage** | Uso de memória após startup | Footprint de memória |

### 2. 🔥 Performance Metrics

| Métrica | Descrição | Bom | Excelente |
|---------|-----------|-----|-----------|
| **RPS** | Requisições por segundo | >1,000 | >5,000 |
| **Avg Latency** | Latência média | <50ms | <10ms |
| **p50 Latency** | 50% das requisições | <30ms | <5ms |
| **p99 Latency** | 99% das requisições | <100ms | <20ms |
| **Max Latency** | Pior caso | <200ms | <50ms |
| **Error Rate** | Taxa de erros | <1% | 0% |

### 3. 📈 Load Scenarios

| Cenário | Usuários | Duração | Objetivo |
|---------|----------|---------|----------|
| **Light Load** | 10 | 30s | Baseline performance |
| **Medium Load** | 50 | 30s | Tráfego normal |
| **High Load** | 100 | 30s | Horário de pico |
| **Very High Load** | 200 | 30s | Condição de stress |
| **Extreme Load** | 500 | 30s | Breaking point |

---

## 📝 Exemplo de Relatório Gerado

### Relatório Individual

```markdown
# 🔥 Benchmark Report: .NET Core

**Test Date:** 2025-10-18 01:45:00
**Backend:** .NET Core
**Port:** 8081
**Technology:** ASP.NET Core 8.0
**Container:** backend-dotnet
**Database:** Fresh (recreated)

---

## ⚡ Startup & Resource Metrics

| Metric | Value |
|--------|-------|
| **Startup Time** | 12s |
| **CPU Usage (Initial)** | 2.5% |
| **Memory Usage (Initial)** | 256MB / 512MB |

---

## 📊 Stress Test Results

| Scenario | Users | RPS | Avg Latency | p50 | p99 | Max | Errors |
|----------|-------|-----|-------------|-----|-----|-----|--------|
| Light Load | 10 | 8,234 | 1.2ms | 1.1ms | 3.4ms | 5.6ms | 0 |
| Medium Load | 50 | 7,892 | 6.3ms | 5.8ms | 12.1ms | 18.3ms | 0 |
| High Load | 100 | 7,234 | 13.8ms | 12.4ms | 28.5ms | 42.1ms | 0 |
| Very High Load | 200 | 6,456 | 31.0ms | 28.2ms | 65.3ms | 98.7ms | 0 |
| Extreme Load | 500 | 4,892 | 102.3ms | 95.1ms | 198.4ms | 312.5ms | 3 |

---

## 📈 Resource Usage After Tests

| Metric | Value |
|--------|-------|
| **CPU Usage (Final)** | 45.2% |
| **Memory Usage (Final)** | 512MB / 512MB |
```

### Relatório Comparativo (Full Benchmark)

```markdown
# 🏆 Complete Backend Benchmark Report

## ⚡ Startup Times Comparison

| Backend | Startup Time | CPU Usage | Memory Usage | Technology |
|---------|--------------|-----------|--------------|------------|
| Go Gin | 3s | 1.2% | 45MB | Gin Framework |
| Node.js | 5s | 2.1% | 128MB | Express.js |
| Python | 8s | 3.5% | 156MB | FastAPI |
| .NET Core | 12s | 2.5% | 256MB | ASP.NET Core |
| Java Spring | 18s | 5.2% | 512MB | Spring MVC |
| Java Reactive | 22s | 4.8% | 480MB | Spring WebFlux |

---

## 🚀 Performance Comparison (100 users)

| Backend | RPS | Avg Latency | p99 Latency | Errors |
|---------|-----|-------------|-------------|--------|
| Java Reactive | 28,456 | 3.5ms | 12.3ms | 0 |
| .NET Core | 7,234 | 13.8ms | 28.5ms | 0 |
| Node.js | 6,892 | 14.5ms | 32.1ms | 0 |
| Go Gin | 5,234 | 19.1ms | 45.2ms | 0 |
| Python | 2,456 | 40.7ms | 98.3ms | 0 |
| Java Spring | 6,123 | 16.3ms | 38.7ms | 0 |
```

---

## 🎯 Casos de Uso

### 1. Benchmark Completo (Comparação)

**Quando usar:**
- Comparar todos os backends
- Decisão de arquitetura
- Relatório executivo
- Documentação oficial

**Como executar:**
```bash
cd tools/stress-test
./full-benchmark.sh

# Aguardar ~30-40 minutos
# Ver relatório comparativo
cat stress-test-results/full_benchmark_*.md
```

---

### 2. Benchmark Individual (Otimização)

**Quando usar:**
- Otimizar backend específico
- Validar mudanças de código
- Troubleshooting de performance
- Testes rápidos

**Como executar:**
```bash
cd tools/stress-test

# Teste inicial
./benchmark-single.sh dotnet

# Fazer otimizações no código .NET
# ...

# Testar novamente
./benchmark-single.sh dotnet

# Comparar relatórios
diff stress-test-results/dotnet_benchmark_*.md
```

---

### 3. Validação de Deploy

**Quando usar:**
- Após deploy em produção
- Validar configurações
- Verificar regressões
- CI/CD pipeline

**Como executar:**
```bash
# Testar backend específico após deploy
./benchmark-single.sh java-reactive

# Verificar se métricas estão dentro do esperado
```

---

## 🔧 Troubleshooting

### Problema: "wrk: command not found"

**Solução:**
```bash
# macOS
brew install wrk

# Linux (Ubuntu/Debian)
sudo apt-get install wrk
```

---

### Problema: "Failed to start in 120s"

**Possíveis causas:**
- Banco de dados não está pronto
- Porta já em uso
- Erro de configuração

**Solução:**
```bash
# Verificar logs
docker-compose logs <backend-container>

# Reiniciar tudo
docker-compose down -v
docker-compose up -d

# Aguardar mais tempo
# Editar MAX_WAIT no script
```

---

### Problema: "Connection refused"

**Solução:**
```bash
# Verificar se container está rodando
docker-compose ps

# Verificar logs
docker-compose logs <backend-container>

# Testar manualmente
curl http://localhost:8080/api/customers
```

---

### Problema: Performance inconsistente

**Solução:**
```bash
# Limpar tudo
docker-compose down -v
docker system prune -f

# Reiniciar Docker
# Executar benchmark novamente
```

---

## 📊 Interpretação de Resultados

### Startup Time

| Tempo | Avaliação | Impacto |
|-------|-----------|---------|
| < 5s | ⭐ Excelente | Deploy rápido |
| 5-15s | ✅ Bom | Aceitável |
| 15-30s | ⚠️ Moderado | Deploy lento |
| > 30s | ❌ Ruim | Problema |

### RPS (100 users)

| RPS | Avaliação | Capacidade |
|-----|-----------|------------|
| > 10,000 | ⭐ Excepcional | Alta carga |
| 5,000-10,000 | ✅ Excelente | Produção |
| 1,000-5,000 | ✅ Bom | Médio porte |
| < 1,000 | ⚠️ Limitado | Baixa carga |

### Latência p99

| Latência | Avaliação | UX |
|----------|-----------|-----|
| < 20ms | ⭐ Excepcional | Instantâneo |
| 20-50ms | ✅ Excelente | Muito rápido |
| 50-100ms | ✅ Bom | Rápido |
| > 100ms | ⚠️ Aceitável | Perceptível |

### Memory Usage

| Memória | Avaliação | Escalabilidade |
|---------|-----------|----------------|
| < 100MB | ⭐ Excelente | Muito eficiente |
| 100-300MB | ✅ Bom | Eficiente |
| 300-500MB | ✅ Aceitável | Moderado |
| > 500MB | ⚠️ Alto | Limitado |

---

## 🎯 Recomendações por Cenário

### Alta Performance (>10k RPS)
```bash
# Testar Java Reactive
./benchmark-single.sh java-reactive

# Esperado: 20k-30k RPS
```

### Startup Rápido (<5s)
```bash
# Testar Go
./benchmark-single.sh go

# Esperado: 3-5s startup
```

### Baixo Uso de Memória (<200MB)
```bash
# Testar Go ou Node.js
./benchmark-single.sh go
./benchmark-single.sh nodejs
```

### Balanceado (Performance + Recursos)
```bash
# Testar .NET Core ou Node.js
./benchmark-single.sh dotnet
./benchmark-single.sh nodejs
```

---

## 📁 Estrutura de Resultados

```
stress-test-results/
├── full_benchmark_20251018_013000.md       # Relatório comparativo
├── java-spring_20251018_013500.md          # Individual
├── java-spring_benchmark_20251018_020000.md # Benchmark individual
├── dotnet_20251018_014000.md
├── dotnet_benchmark_20251018_021000.md
├── python_20251018_014500.md
├── nodejs_20251018_015000.md
├── go_20251018_015500.md
└── java-reactive_20251018_016000.md
```

---

## 🚀 Quick Start

### Teste Completo (Todos os Backends)
```bash
cd tools/stress-test
./full-benchmark.sh
# Aguardar ~30-40 minutos
# Ver: stress-test-results/full_benchmark_*.md
```

### Teste Individual (Um Backend)
```bash
cd tools/stress-test
./benchmark-single.sh java-reactive
# Aguardar ~5 minutos
# Ver: stress-test-results/java-reactive_benchmark_*.md
```

---

## 📚 Recursos Adicionais

- **wrk Documentation**: https://github.com/wg/wrk
- **Docker Stats**: https://docs.docker.com/engine/reference/commandline/stats/
- **Performance Testing**: https://www.baeldung.com/wrk-benchmarking

---

**🔥 Benchmark completo com métricas detalhadas de startup, recursos e performance!**
