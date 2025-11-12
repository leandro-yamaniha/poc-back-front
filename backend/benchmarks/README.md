# 🚀 Benchmarks e Testes de Stress

Documentação completa para execução de testes de stress em todos os backends do Beauty Salon Management System.

---

## 📚 Documentação Disponível

| Documento | Descrição |
|-----------|-----------|
| **WORKFLOW_STRESS_TESTS.md** | Workflow completo com diagramas Mermaid |
| **DOCKER_COMPOSE_SUMMARY.md** | Resumo das configurações Docker |
| **fluxo-recomendado-testes-stress.txt** | Fluxo textual dos testes |

---

## 🎯 Quick Start

### 1. Executar Todos os Testes

```bash
# Executar testes em todos os backends
./backend/scripts/stress-test-all.sh
```

### 2. Analisar Resultados

```bash
# Gerar relatório comparativo
./backend/scripts/analyze-results.sh ./backend/benchmarks/results/[data-hora]
```

### 3. Visualizar Resultados

```bash
# Ver relatório gerado
cat ./backend/benchmarks/results/[data-hora]/ANALYSIS.md
```

---

## 📊 Backends Testados

1. ✅ **dotnet** - .NET Core ASP.NET
2. ✅ **python** - Python FastAPI
3. ✅ **nodejs** - Node.js Express
4. ✅ **go** - Go Gin
5. ✅ **java-reactive-jvm** - Java Spring WebFlux (JVM)
6. ✅ **java-traditional-jvm** - Java Spring Boot (JVM)
7. ⏸️ **java-traditional-native** - Java Spring Boot (GraalVM Native)
8. ⏸️ **java-reactive-native** - Java Spring WebFlux (GraalVM Native)

---

## 🔄 Workflow Completo

```mermaid
graph LR
    A[Build] --> B[Test Loop]
    B --> C[Análise]
    C --> D[Otimização]
    D --> E[Reteste]
    E --> F[Relatório Final]
```

**Veja detalhes completos em:** `WORKFLOW_STRESS_TESTS.md`

---

## 📁 Estrutura de Resultados

```
backend/benchmarks/results/
└── 20241026_183045/
    ├── nodejs_wrk.txt              # Resultados wrk
    ├── nodejs_docker_stats.txt     # Métricas Docker
    ├── nodejs_logs.txt             # Logs do container
    ├── nodejs_inspect.json         # Inspeção do container
    ├── go_wrk.txt
    ├── go_docker_stats.txt
    ├── ...
    └── ANALYSIS.md                 # Relatório comparativo
```

---

## 🛠️ Scripts Disponíveis

### stress-test-all.sh

Executa testes de stress em todos os backends automaticamente.

**Características:**
- ✅ Loop automático por todos os backends
- ✅ Health checks antes dos testes
- ✅ Coleta de métricas completa
- ✅ Logs coloridos e informativos
- ✅ Cleanup automático entre testes

**Configuração:**
```bash
TEST_DURATION="60s"    # Duração do teste
CONNECTIONS="50"       # Conexões concorrentes
THREADS="2"            # Threads do wrk
```

### analyze-results.sh

Gera relatório comparativo automaticamente.

**Análises Incluídas:**
- 📊 Ranking por throughput
- ⏱️ Ranking por latência
- 🔄 Comparação Native vs JVM
- 🆚 Comparação Traditional vs Reactive
- 💡 Recomendações por cenário

---

## 📈 Métricas Coletadas

| Categoria | Métricas |
|-----------|----------|
| **Performance** | Throughput, Latência (Avg, P50, P90, P99) |
| **Recursos** | CPU %, Memory MB, Network I/O, Disk I/O |
| **Confiabilidade** | Taxa Sucesso %, Taxa Erro %, Total Req |

---

## 🎯 Exemplos de Uso

### Teste Rápido (1 backend)

```bash
# Testar apenas Node.js
docker-compose -f docker-compose.loadtest.yml up -d cassandra nodejs-backend
sleep 30
wrk -t2 -c50 -d10s --latency http://localhost:3000/api/customers
docker-compose -f docker-compose.loadtest.yml down
```

### Teste Completo (todos backends)

```bash
# Executar suite completa
./backend/scripts/stress-test-all.sh

# Aguardar conclusão (4-6 horas)

# Gerar relatório
./backend/scripts/analyze-results.sh ./backend/benchmarks/results/[timestamp]
```

### Teste com Recursos Otimizados

```bash
# 1. Editar docker-compose.loadtest.yml
# Reduzir CPU/Memory limits

# 2. Rebuild
docker-compose -f docker-compose.loadtest.yml build

# 3. Executar testes
./backend/scripts/stress-test-all.sh
```

---

## 🔍 Troubleshooting

### Backend não fica healthy

```bash
# Verificar logs
docker logs [backend]-loadtest

# Aumentar timeout
# Editar stress-test-all.sh: max_attempts=120
```

### Cassandra não inicia

```bash
# Verificar recursos
docker stats

# Limpar volumes
docker-compose -f docker-compose.loadtest.yml down -v
```

### wrk não encontrado

```bash
# macOS
brew install wrk

# Linux
sudo apt-get install wrk
```

---

## 📊 Resultados Anteriores

### Testes Baseline (26/10/2025)

| Backend | Req/s | P99 | Status |
|---------|-------|-----|--------|
| Node.js | 4,667 | 50.69ms | ✅ Campeão |
| Java JVM | 3,664 | 321.96ms | ⚠️ P99 Alto |
| Go | 3,148 | 56.88ms | ⚠️ Socket Errors |

**Relatórios:**
- `RELATORIO_COMPLETO_3_BACKENDS.md`
- `RELATORIO_EXECUTIVO_FINAL.md`

---

## 🚀 Próximos Passos

1. ✅ Corrigir problemas identificados
   - Java JVM P99
   - Go socket errors
   - Python/NET builds

2. ✅ Testar backends Native
   - java-traditional-native
   - java-reactive-native

3. ✅ Testes de longa duração
   - 5 minutos
   - 10 minutos
   - Stress test até falha

4. ✅ Otimização de recursos
   - Reduzir CPU/Memory
   - Reteste com limites mínimos

---

## 📞 Suporte

**Documentação Completa:**
- `/backend/benchmarks/WORKFLOW_STRESS_TESTS.md`
- `/RELATORIO_EXECUTIVO_FINAL.md`
- `/LOAD_TEST_TUTORIAL.md`

**Scripts:**
- `/backend/scripts/stress-test-all.sh`
- `/backend/scripts/analyze-results.sh`

---

**Status:** ✅ Pronto para Uso  
**Última Atualização:** 26/10/2025  
**Automação:** 95%
