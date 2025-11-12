# Workflow de Testes de Stress - Beauty Salon Backends

**Objetivo:** Executar testes de stress comparativos em todos os backends  
**Data:** 26 de Outubro de 2025  
**Backends:** 8 implementações

---

## 📋 Backends a Testar

1. **dotnet** - .NET Core ASP.NET
2. **python** - Python FastAPI
3. **nodejs** - Node.js Express
4. **go** - Go Gin
5. **java-reactive** - Java Spring WebFlux (JVM)
6. **java-traditional** - Java Spring Boot (JVM)
7. **java-traditional-native** - Java Spring Boot (GraalVM Native)
8. **java-reactive-native** - Java Spring WebFlux (GraalVM Native)

---

## 🔄 Fluxo Principal

```mermaid
graph TD
    A[Início] --> B[Fase 1: Build Inicial]
    B --> C[Fase 2: Testes com Config Padrão]
    C --> D[Fase 3: Análise e Relatório]
    D --> E[Fase 4: Otimização de Recursos]
    E --> F[Fase 5: Reteste Otimizado]
    F --> G[Fase 6: Relatório Final]
    G --> H[Fim]
    
    style B fill:#4CAF50
    style C fill:#2196F3
    style D fill:#FF9800
    style E fill:#9C27B0
    style F fill:#2196F3
    style G fill:#FF9800
```

---

## 📦 Fase 1: Build de Todos os Backends

```mermaid
graph LR
    A[Build Inicial] --> B[dotnet]
    A --> C[python]
    A --> D[nodejs]
    A --> E[go]
    A --> F[java-reactive]
    A --> G[java-traditional]
    A --> H[java-traditional-native]
    A --> I[java-reactive-native]
    
    B --> J[Imagens Prontas]
    C --> J
    D --> J
    E --> J
    F --> J
    G --> J
    H --> J
    I --> J
    
    style A fill:#4CAF50
    style J fill:#4CAF50
```

### Comandos

```bash
# Build de todos os backends
docker-compose -f docker-compose.loadtest.yml build

# Verificar imagens criadas
docker images | grep beauty-salon
```

---

## 🔁 Fase 2: Loop de Testes (Config Padrão)

### ⚠️ REGRA CRÍTICA: Isolamento de Containers

**Cada teste DEVE executar com apenas 2 containers:**
1. ✅ Cassandra (banco de dados)
2. ✅ Backend sendo testado

**Nenhum outro container pode estar rodando!**

```mermaid
graph TD
    A[Para cada Backend] --> B[Parar TODOS Containers]
    B --> C[Verificar Ambiente Limpo]
    C --> D{Containers Rodando?}
    D -->|Sim| E[Forçar Parada]
    D -->|Não| F[Iniciar Cassandra]
    E --> F
    F --> G[Aguardar Cassandra Healthy]
    G --> H[Iniciar APENAS Backend]
    H --> I[Verificar Isolamento]
    I --> J{Apenas 2 Containers?}
    J -->|Não| K[ALERTA: Isolamento Falhou]
    J -->|Sim| L[Aguardar Backend Healthy]
    K --> L
    L --> M[Executar Stress Tests]
    M --> N[Coletar Métricas]
    N --> O[Salvar Resultados]
    O --> P[Parar Containers]
    P --> Q{Mais Backends?}
    Q -->|Sim| A
    Q -->|Não| R[Fim Loop]
    
    style C fill:#FF9800
    style F fill:#2196F3
    style H fill:#2196F3
    style I fill:#FF9800
    style J fill:#FF9800
    style K fill:#FF5722
    style M fill:#FF5722
    style N fill:#FF9800
    style O fill:#4CAF50
```

### Detalhamento por Backend

```mermaid
sequenceDiagram
    participant T as Tester
    participant D as Docker
    participant C as Cassandra
    participant B as Backend
    participant W as wrk
    
    Note over T,D: FASE 1: Garantir Ambiente Limpo
    T->>D: docker-compose down
    T->>D: docker ps -q
    D-->>T: Lista containers
    alt Containers rodando
        T->>D: docker stop $(docker ps -q)
        Note over T,D: Forçar parada de todos
    end
    Note over T,D: ✅ Ambiente limpo
    
    Note over T,C: FASE 2: Iniciar Cassandra
    T->>D: docker-compose up -d cassandra
    D->>C: Iniciar Cassandra
    C-->>D: Starting...
    loop Health Check
        T->>C: curl health
        C-->>T: Status
    end
    C-->>T: Healthy!
    
    Note over T,B: FASE 3: Iniciar APENAS Backend
    T->>D: docker-compose up -d [backend]
    D->>B: Iniciar Backend
    B-->>D: Starting...
    
    Note over T,D: FASE 4: Verificar Isolamento
    T->>D: docker ps --format '{{.Names}}'
    D-->>T: cassandra-loadtest, [backend]-loadtest
    alt Mais de 2 containers
        Note over T: ⚠️ ALERTA: Isolamento falhou!
    else Exatamente 2 containers
        Note over T: ✅ Isolamento confirmado
    end
    
    loop Health Check
        T->>B: curl health
        B-->>T: Status
    end
    B-->>T: Healthy!
    
    Note over T,W: FASE 5: Executar Stress Test
    T->>W: wrk -t2 -c50 -d60s
    W->>B: Stress Test
    B-->>W: Responses
    W-->>T: Métricas
    T->>D: docker stats
    D-->>T: CPU/Mem/Net
    T->>T: Salvar Resultados
    T->>D: docker-compose down
```

### 🎯 Por que Isolamento é Crítico?

**Razões para executar apenas Cassandra + Backend:**

1. **Precisão das Métricas**
   - CPU e memória medidos são exclusivos do backend testado
   - Sem interferência de outros processos
   - Resultados comparáveis entre testes

2. **Recursos Dedicados**
   - Todo CPU disponível para o backend
   - Toda memória disponível para o backend
   - Sem competição por recursos

3. **Reprodutibilidade**
   - Ambiente idêntico em todos os testes
   - Resultados consistentes
   - Comparações justas

4. **Detecção de Problemas**
   - Vazamentos de memória ficam evidentes
   - CPU spikes são do backend testado
   - Network I/O é real do teste

**⚠️ Violação do Isolamento = Resultados Inválidos!**

---

## 📊 Fase 3: Geração de Relatório Comparativo

```mermaid
graph TD
    A[Coletar Resultados] --> B[Processar Métricas]
    B --> C[Gerar Gráficos]
    C --> D[Análise Comparativa]
    
    B --> B1[CPU Usage]
    B --> B2[Memory Usage]
    B --> B3[Network I/O]
    B --> B4[Disk I/O]
    B --> B5[Latências P50/P90/P99]
    B --> B6[Throughput]
    B --> B7[Taxa Erro/Sucesso]
    
    B1 --> C
    B2 --> C
    B3 --> C
    B4 --> C
    B5 --> C
    B6 --> C
    B7 --> C
    
    D --> E[Relatório Final]
    
    style A fill:#2196F3
    style C fill:#FF9800
    style E fill:#4CAF50
```

### Métricas Coletadas

| Categoria | Métricas |
|-----------|----------|
| **Performance** | Throughput (req/s), Latência Média, P50, P90, P99, Latência Mín/Máx |
| **Recursos** | CPU (%), Memory (MB), Disk I/O (MB/s), Network I/O (MB/s) |
| **Confiabilidade** | Taxa de Sucesso (%), Taxa de Erro (%), Total Requisições |
| **Escalabilidade** | Req/s por CPU, Req/s por MB RAM |

---

## ⚙️ Fase 4: Otimização de Recursos

```mermaid
graph TD
    A[Analisar Uso Atual] --> B{Recursos Ociosos?}
    B -->|Sim| C[Calcular Mínimo]
    B -->|Não| D[Manter Config]
    
    C --> E[Reduzir CPU Limit]
    C --> F[Reduzir Memory Limit]
    
    E --> G[Atualizar docker-compose]
    F --> G
    
    G --> H[Rebuild Containers]
    H --> I[Configuração Otimizada]
    
    style A fill:#2196F3
    style C fill:#FF9800
    style I fill:#4CAF50
```

### Estratégia de Otimização

```yaml
# Antes (Config Padrão)
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 512M
    reservations:
      cpus: '0.5'
      memory: 256M

# Depois (Otimizado)
deploy:
  resources:
    limits:
      cpus: '0.5'      # Reduzido 50%
      memory: 256M     # Reduzido 50%
    reservations:
      cpus: '0.25'     # Reduzido 50%
      memory: 128M     # Reduzido 50%
```

---

## 🔁 Fase 5: Reteste com Recursos Otimizados

### ⚠️ MESMA REGRA: Isolamento Obrigatório

**Fase 5 segue as MESMAS regras da Fase 2:**
- ✅ Apenas Cassandra + Backend sendo testado
- ✅ Verificação de ambiente limpo antes de cada teste
- ✅ Verificação de isolamento após iniciar backend
- ✅ Nenhum outro container pode estar rodando

```mermaid
graph TD
    A[Recursos Otimizados] --> B[Repetir Loop de Testes]
    B --> C[Para cada Backend]
    C --> D[Parar TODOS Containers]
    D --> E[Verificar Ambiente Limpo]
    E --> F[Iniciar Cassandra]
    F --> G[Iniciar APENAS Backend]
    G --> H[Verificar Isolamento]
    H --> I{Apenas 2 Containers?}
    I -->|Não| J[ALERTA: Isolamento Falhou]
    I -->|Sim| K[Executar Stress Tests]
    J --> K
    K --> L[Coletar Métricas]
    L --> M{Mais Backends?}
    M -->|Sim| C
    M -->|Não| N[Comparar com Baseline]
    
    N --> O{Performance OK?}
    O -->|Sim| P[Config Otimizada Aprovada]
    O -->|Não| Q[Ajustar Recursos]
    Q --> B
    
    style A fill:#9C27B0
    style D fill:#FF9800
    style E fill:#FF9800
    style H fill:#FF9800
    style I fill:#FF9800
    style J fill:#FF5722
    style K fill:#FF5722
    style P fill:#4CAF50
    style Q fill:#FF9800
```

---

## 📈 Fase 6: Relatório Final Comparativo

```mermaid
graph TD
    A[Resultados Baseline] --> C[Análise Comparativa]
    B[Resultados Otimizados] --> C
    
    C --> D[Gráficos Comparativos]
    C --> E[Tabelas Métricas]
    C --> F[Recomendações]
    
    D --> G[Relatório Final]
    E --> G
    F --> G
    
    G --> H[Decisão Técnica]
    
    style C fill:#FF9800
    style G fill:#4CAF50
    style H fill:#4CAF50
```

---

## 🔧 Scripts de Automação

### Script Principal

```bash
#!/bin/bash
# stress-test-all.sh

BACKENDS=(
    "dotnet-backend:5001"
    "python-backend:8000"
    "nodejs-backend:3000"
    "go-backend:8080"
    "java-reactive-jvm:8085"
    "java-jvm:10001"
    "java-native:10002"
    "java-reactive-native:8086"
)

RESULTS_DIR="./results/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$RESULTS_DIR"

echo "🚀 Iniciando testes de stress em todos os backends..."

for backend_config in "${BACKENDS[@]}"; do
    IFS=':' read -r backend port <<< "$backend_config"
    
    echo ""
    echo "========================================="
    echo "🔍 Testando: $backend"
    echo "========================================="
    
    # 1. Parar todos os containers
    echo "⏹️  Parando containers..."
    docker-compose -f docker-compose.loadtest.yml down
    
    # 2. Iniciar Cassandra
    echo "🗄️  Iniciando Cassandra..."
    docker-compose -f docker-compose.loadtest.yml up -d cassandra
    
    # 3. Aguardar Cassandra healthy
    echo "⏳ Aguardando Cassandra..."
    until docker exec cassandra-loadtest cqlsh -e "DESCRIBE KEYSPACES" > /dev/null 2>&1; do
        sleep 2
    done
    echo "✅ Cassandra pronto!"
    
    # 4. Iniciar backend
    echo "🚀 Iniciando $backend..."
    docker-compose -f docker-compose.loadtest.yml up -d "$backend"
    
    # 5. Aguardar backend healthy
    echo "⏳ Aguardando $backend..."
    sleep 30
    until curl -f "http://localhost:$port/health" > /dev/null 2>&1; do
        sleep 5
    done
    echo "✅ $backend pronto!"
    
    # 6. Executar stress test
    echo "💪 Executando stress test..."
    wrk -t2 -c50 -d60s --latency "http://localhost:$port/api/customers" \
        > "$RESULTS_DIR/${backend}_wrk.txt"
    
    # 7. Coletar métricas Docker
    echo "📊 Coletando métricas..."
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" \
        > "$RESULTS_DIR/${backend}_docker_stats.txt"
    
    # 8. Salvar logs
    docker logs "${backend}-loadtest" > "$RESULTS_DIR/${backend}_logs.txt" 2>&1
    
    echo "✅ Teste concluído: $backend"
done

echo ""
echo "========================================="
echo "✅ Todos os testes concluídos!"
echo "📁 Resultados salvos em: $RESULTS_DIR"
echo "========================================="

# Parar todos os containers
docker-compose -f docker-compose.loadtest.yml down
```

### Script de Análise

```bash
#!/bin/bash
# analyze-results.sh

RESULTS_DIR="$1"

if [ -z "$RESULTS_DIR" ]; then
    echo "Uso: $0 <diretório_resultados>"
    exit 1
fi

echo "# Relatório Comparativo de Performance" > "$RESULTS_DIR/REPORT.md"
echo "" >> "$RESULTS_DIR/REPORT.md"
echo "**Data:** $(date)" >> "$RESULTS_DIR/REPORT.md"
echo "" >> "$RESULTS_DIR/REPORT.md"

echo "| Backend | Req/s | Latência Média | P50 | P90 | P99 | CPU | Memory |" >> "$RESULTS_DIR/REPORT.md"
echo "|---------|-------|----------------|-----|-----|-----|-----|--------|" >> "$RESULTS_DIR/REPORT.md"

for wrk_file in "$RESULTS_DIR"/*_wrk.txt; do
    backend=$(basename "$wrk_file" _wrk.txt)
    
    # Extrair métricas do wrk
    req_sec=$(grep "Requests/sec:" "$wrk_file" | awk '{print $2}')
    latency_avg=$(grep "Latency" "$wrk_file" | head -1 | awk '{print $2}')
    p50=$(grep "50%" "$wrk_file" | awk '{print $2}')
    p90=$(grep "90%" "$wrk_file" | awk '{print $2}')
    p99=$(grep "99%" "$wrk_file" | awk '{print $2}')
    
    # Extrair métricas Docker
    stats_file="$RESULTS_DIR/${backend}_docker_stats.txt"
    cpu=$(grep "$backend" "$stats_file" | awk '{print $2}')
    mem=$(grep "$backend" "$stats_file" | awk '{print $3}')
    
    echo "| $backend | $req_sec | $latency_avg | $p50 | $p90 | $p99 | $cpu | $mem |" >> "$RESULTS_DIR/REPORT.md"
done

echo "" >> "$RESULTS_DIR/REPORT.md"
echo "✅ Relatório gerado: $RESULTS_DIR/REPORT.md"
```

---

## 📊 Exemplo de Relatório Final

```markdown
# Relatório Comparativo - Baseline vs Otimizado

## Configuração Baseline (1 CPU, 512MB)

| Backend | Req/s | P99 | CPU | Memory |
|---------|-------|-----|-----|--------|
| nodejs | 4,667 | 50.69ms | 85% | 380MB |
| java-jvm | 3,664 | 321.96ms | 92% | 450MB |
| go | 3,148 | 56.88ms | 78% | 120MB |

## Configuração Otimizada (0.5 CPU, 256MB)

| Backend | Req/s | P99 | CPU | Memory | Δ Req/s |
|---------|-------|-----|-----|--------|---------|
| nodejs | 3,200 | 65ms | 95% | 220MB | -31% |
| java-jvm | 2,100 | 450ms | 98% | 240MB | -43% |
| go | 2,800 | 70ms | 92% | 110MB | -11% |

## Recomendações

1. **Go** - Melhor eficiência com recursos limitados (-11% throughput)
2. **Node.js** - Boa performance mas requer mais recursos
3. **Java JVM** - Não recomendado para recursos limitados
```

---

## ✅ Checklist de Execução

### Pré-requisitos
- [ ] Docker e Docker Compose instalados
- [ ] wrk instalado (`brew install wrk`)
- [ ] Cassandra configurado
- [ ] Todos os backends buildados

### Fase 1: Build
- [ ] Build de todos os backends
- [ ] Verificar imagens criadas
- [ ] Testar inicialização manual

### Fase 2: Testes Baseline
- [ ] Executar testes em todos os backends
- [ ] Coletar métricas completas
- [ ] Salvar resultados

### Fase 3: Análise
- [ ] Processar resultados
- [ ] Gerar gráficos
- [ ] Criar relatório comparativo

### Fase 4: Otimização
- [ ] Analisar uso de recursos
- [ ] Calcular configuração mínima
- [ ] Atualizar docker-compose

### Fase 5: Reteste
- [ ] Executar testes otimizados
- [ ] Comparar com baseline
- [ ] Validar performance

### Fase 6: Relatório Final
- [ ] Consolidar resultados
- [ ] Gerar recomendações
- [ ] Documentar decisões

---

## 🎯 Objetivos de Sucesso

| Objetivo | Meta |
|----------|------|
| Backends Testados | 8/8 (100%) |
| Métricas Coletadas | Completas |
| Relatório Gerado | Sim |
| Otimização Aplicada | Sim |
| Decisão Técnica | Clara |

---

**Status:** 📋 Pronto para Execução  
**Tempo Estimado:** 4-6 horas  
**Automação:** 90%
