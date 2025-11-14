# 🔧 Backend Scripts

Esta pasta contém todos os scripts automatizados para gerenciamento dos backends do Beauty Salon Management System.

---

## 🤖 **GUIA PARA IA GENERATIVA**

> **IMPORTANTE:** Este documento segue regras estruturadas para facilitar interpretação por IA generativa.

### **📐 REGRAS DE ORGANIZAÇÃO**

#### **R1: Localização de Scripts**
```
REGRA: Todos os scripts DEVEM estar em backend/scripts/
EXCEÇÃO: Nenhuma
VALIDAÇÃO: ls backend/scripts/*.sh
```

#### **R2: Nomenclatura Padrão**
```
FORMATO: ação-escopo.sh
PADRÃO: [verbo]-[substantivo]-[modificador?].sh
EXEMPLOS VÁLIDOS:
  ✅ build-all.sh
  ✅ test-backends-quick.sh
  ✅ stress-test-all-backends.sh
  ✅ benchmark-native-comparison.sh
EXEMPLOS INVÁLIDOS:
  ❌ buildAll.sh (camelCase)
  ❌ test_backends.sh (underscore)
  ❌ stress-test.bash (extensão errada)
```

#### **R3: Permissões**
```
REGRA: Todos os scripts DEVEM ter permissão de execução
COMANDO: chmod +x backend/scripts/*.sh
VALIDAÇÃO: test -x backend/scripts/nome-script.sh
```

#### **R4: Documentação**
```
REGRA: Todo script DEVE estar documentado neste README
SEÇÕES OBRIGATÓRIAS:
  1. Nome do script (### `nome-script.sh`)
  2. Propósito (uma linha)
  3. Tempo estimado
  4. Uso (comando exemplo)
  5. Funcionalidades (lista)
LOCALIZAÇÃO: Seção "📖 Descrição Detalhada dos Scripts"
```

#### **R5: Resultados**
```
REGRA: Resultados DEVEM ser salvos em diretórios específicos
DIRETÓRIOS PERMITIDOS:
  - ./stress-test-results/YYYYMMDD_HHMMSS/
  - ./benchmarks/results/YYYYMMDD_HHMMSS/
  - ./build-logs-YYYYMMDD-HHMMSS/
REGRA: Todos DEVEM estar no .gitignore
```

#### **R6: Categorias de Scripts**
```
CATEGORIAS VÁLIDAS:
  1. 🏗️  Build Scripts      - Compilação e construção
  2. 🧪 Test Scripts        - Testes funcionais
  3. 💪 Stress Test Scripts - Performance e carga
  4. 🚀 Deploy Scripts      - Inicialização e deploy
  5. 🔧 Utility Scripts     - Ferramentas auxiliares
```

### **🎯 FLUXO DE CRIAÇÃO DE NOVO SCRIPT**

```yaml
QUANDO: Criar novo script
ENTÃO:
  1. CRIAR: backend/scripts/ação-escopo.sh
  2. PERMISSÃO: chmod +x backend/scripts/ação-escopo.sh
  3. HEADER: Adicionar comentário no topo do script
  4. DOCUMENTAR: Adicionar seção neste README
  5. TESTAR: Executar script em ambiente limpo
  6. COMMIT: git add + commit com mensagem descritiva
```

### **📊 TEMPLATE DE DOCUMENTAÇÃO**

```markdown
### `nome-do-script.sh`

**Propósito**: Descrição clara em uma linha
**Tempo**: ~X minutos
**Uso**: `./scripts/nome-do-script.sh [argumentos]`

#### Funcionalidades:
- Item 1
- Item 2
- Item 3

#### Exemplo de uso:
\```bash
# Comentário explicativo
./scripts/nome-do-script.sh argumento1 argumento2

# Resultado esperado
Saída do comando
\```
```

### **🔍 QUERIES COMUNS PARA IA**

```yaml
PERGUNTA: "Como fazer build de todos os backends?"
RESPOSTA: ./scripts/build-all-parallel.sh (mais rápido) OU ./scripts/build-all.sh (sequencial)

PERGUNTA: "Como testar performance?"
RESPOSTA: ./scripts/stress-test-all-backends.sh all

PERGUNTA: "Como comparar JVM vs Native?"
RESPOSTA: ./scripts/benchmark-native-comparison.sh

PERGUNTA: "Como limpar artefatos?"
RESPOSTA: ./scripts/clean-all.sh

PERGUNTA: "Como iniciar todos os backends?"
RESPOSTA: ./scripts/start-all-compose.sh
```

### **⚠️ VALIDAÇÕES AUTOMÁTICAS**

```bash
# Verificar nomenclatura
find backend/scripts -name "*.sh" | grep -v "^[a-z-]*\.sh$" && echo "❌ Nomenclatura inválida"

# Verificar permissões
find backend/scripts -name "*.sh" ! -perm -u+x && echo "❌ Sem permissão de execução"

# Verificar documentação
for script in backend/scripts/*.sh; do
  grep -q "$(basename $script)" backend/scripts/README.md || echo "❌ $script não documentado"
done
```

---

## 📋 **Índice de Scripts**

### 🏗️ **Scripts de Build**
- [`build-all.sh`](#build-allsh) - Build sequencial de todos os backends
- [`build-all-parallel.sh`](#build-all-parallelsh) - Build paralelo de todos os backends
- [`build-specific.sh`](#build-specificsh) - Build de backend específico
- [`clean-all.sh`](#clean-allsh) - Limpeza de artefatos de build

### 🐳 **Scripts Docker**
- [`docker-build-all.sh`](#docker-build-allsh) - Build de todas as imagens Docker

### 🧪 **Scripts de Teste**
- [`test-backends-quick.sh`](#test-backends-quicksh) - Teste rápido de builds
- [`test-backends-sequential.sh`](#test-backends-sequentialsh) - Teste sequencial completo
- [`test-backends-one-by-one.sh`](#test-backends-one-by-onesh) - Teste individual detalhado
- [`test-all-sequential.sh`](#test-all-sequentialsh) - Teste completo sequencial
- [`test-all-compose.sh`](#test-all-composesh) - Teste com Docker Compose
- [`test-compose-with-build.sh`](#test-compose-with-buildsh) - Teste com build Docker
- [`test-memory-limits.sh`](#test-memory-limitssh) - Teste de limites de memória

### 💪 **Scripts de Stress Test / Performance**
- [`stress-test-all.sh`](#stress-test-allsh) - Stress test em todos os backends (loadtest.yml)
- [`stress-test-all-backends.sh`](#stress-test-all-backendssh) - Stress test sequencial com wrk
- [`stress-test-individual.sh`](#stress-test-individualsh) - Stress test em backend específico
- [`stress-test-sequential.sh`](#stress-test-sequentialsh) - Stress test sequencial detalhado
- [`benchmark-all-with-native.sh`](#benchmark-all-with-nativesh) - Benchmark completo incluindo native
- [`benchmark-native-comparison.sh`](#benchmark-native-comparisonsh) - Comparação JVM vs Native
- [`load-test.sh`](#load-testsh) - Load test com configurações customizadas
- [`analyze-results.sh`](#analyze-resultssh) - Análise de resultados de stress test

### 🚀 **Scripts de Deploy**
- [`start-all-compose.sh`](#start-all-composesh) - Iniciar todos os backends
- [`stop-all-compose.sh`](#stop-all-composesh) - Parar todos os backends

## 📖 **Descrição Detalhada dos Scripts**

### `build-all.sh`
**Propósito**: Build sequencial de todos os backends  
**Tempo**: ~30 segundos  
**Uso**: `./scripts/build-all.sh`

```bash
# Executa build de todos os backends em sequência
# Mostra output em tempo real
# Para no primeiro erro
```

### `build-all-parallel.sh`
**Propósito**: Build paralelo de todos os backends  
**Tempo**: ~17 segundos  
**Uso**: `./scripts/build-all-parallel.sh`

```bash
# Executa builds simultaneamente
# Mais rápido que o sequencial
# Logs salvos em build-logs-*/
```

### `build-specific.sh`
**Propósito**: Build de backend específico  
**Uso**: `./scripts/build-specific.sh <backend>`

```bash
# Exemplos:
./scripts/build-specific.sh java
./scripts/build-specific.sh nodejs
./scripts/build-specific.sh python
```

### `clean-all.sh`
**Propósito**: Limpeza de artefatos de build  
**Uso**: `./scripts/clean-all.sh`

```bash
# Remove:
# - target/ (Java)
# - node_modules/ (Node.js)
# - venv/ (Python)
# - bin/ (Go)
# - publish/ (dotnet)
```

### `docker-build-all.sh`
**Propósito**: Build de todas as imagens Docker  
**Uso**: `./scripts/docker-build-all.sh`

```bash
# Constrói imagens Docker para todos os backends
# Usa artefatos pré-buildados
```

### `test-backends-quick.sh`
**Propósito**: Teste rápido de builds (apenas build)  
**Tempo**: ~26 segundos  
**Uso**: `./scripts/test-backends-quick.sh`

```bash
# Testa apenas se os builds funcionam
# Não inicia os serviços
# Ideal para CI/CD
```

### `test-backends-sequential.sh`
**Propósito**: Teste completo sequencial (build + startup)  
**Tempo**: ~5-10 minutos  
**Uso**: `./scripts/test-backends-sequential.sh`

```bash
# Testa build + startup + health checks
# Um backend por vez
# Teste mais completo
```

### `test-backends-one-by-one.sh`
**Propósito**: Teste individual detalhado  
**Uso**: `./scripts/test-backends-one-by-one.sh`

```bash
# Teste detalhado de cada backend
# Inclui testes de API
# Relatório completo
```

### `test-all-sequential.sh`
**Propósito**: Teste completo de todos os backends  
**Uso**: `./scripts/test-all-sequential.sh`

```bash
# Teste abrangente
# Build + Docker + Health + APIs
```

### `test-all-compose.sh`
**Propósito**: Teste com Docker Compose  
**Uso**: `./scripts/test-all-compose.sh`

```bash
# Testa usando docker-compose
# Inclui dependências (Cassandra)
```

### `test-compose-with-build.sh`
**Propósito**: Teste Docker Compose com build  
**Uso**: `./scripts/test-compose-with-build.sh`

```bash
# Reconstrói imagens Docker
# Teste completo de deploy
```

### `start-all-compose.sh`
**Propósito**: Iniciar todos os backends  
**Uso**: `./scripts/start-all-compose.sh`

```bash
# Inicia todos os backends via Docker Compose
# Inclui health checks
```

### `stop-all-compose.sh`
**Propósito**: Parar todos os backends  
**Uso**: `./scripts/stop-all-compose.sh`

```bash
# Para todos os containers
# Limpa volumes se necessário
```

## 🚀 **Fluxos de Trabalho Recomendados**

### **Desenvolvimento Rápido**
```bash
# 1. Build rápido
./scripts/build-all-parallel.sh

# 2. Teste básico
./scripts/test-backends-quick.sh
```

### **Teste Completo**
```bash
# 1. Limpeza
./scripts/clean-all.sh

# 2. Build
./scripts/build-all.sh

# 3. Teste completo
./scripts/test-backends-sequential.sh
```

### **Deploy Local**
```bash
# 1. Build
./scripts/build-all.sh

# 2. Iniciar serviços
./scripts/start-all-compose.sh

# 3. Parar quando terminar
./scripts/stop-all-compose.sh
```

### **CI/CD Pipeline**
```bash
# 1. Teste rápido
./scripts/test-backends-quick.sh

# 2. Build Docker
./scripts/docker-build-all.sh

# 3. Teste deploy
./scripts/test-compose-with-build.sh
```

## ⚙️ **Configuração**

### **Pré-requisitos**
- **Docker** e **Docker Compose** instalados
- **Permissões de execução** nos scripts
- **Dependências** de cada backend instaladas

### **Permissões**
```bash
# Dar permissão de execução (se necessário)
chmod +x scripts/*.sh
```

### **Variáveis de Ambiente**
Alguns scripts respeitam variáveis de ambiente:
- `BACKEND_HOST_PORT` - Porta do backend
- `FRONTEND_HOST_PORT` - Porta do frontend
- `CASSANDRA_PORT` - Porta do Cassandra

## 📊 **Performance dos Scripts**

| Script | Tempo Aprox. | Tipo | Recomendado Para |
|--------|--------------|------|------------------|
| `build-all-parallel.sh` | 17s | Build | Desenvolvimento |
| `build-all.sh` | 30s | Build | Debug |
| `test-backends-quick.sh` | 26s | Teste | CI/CD |
| `test-backends-sequential.sh` | 5-10min | Teste | QA |
| `clean-all.sh` | 2s | Limpeza | Manutenção |

## 🔍 **Troubleshooting**

### **Problemas Comuns**

#### Script não executa
```bash
# Verificar permissões
ls -la scripts/
chmod +x scripts/*.sh
```

#### Build falha
```bash
# Limpar artefatos
./scripts/clean-all.sh

# Tentar build individual
./scripts/build-specific.sh <backend>
```

#### Docker não funciona
```bash
# Verificar Docker
docker --version
docker-compose --version

# Limpar containers
docker system prune -f
```

### `test-memory-limits.sh`

**Propósito**: Teste de limites mínimos de memória para todos os backends  
**Tempo**: ~15-20 minutos (dependendo da máquina)  
**Uso**: `./scripts/test-memory-limits.sh`

#### Funcionalidades:
- **Teste de 4 configurações**: minimal (256m), low (512m), medium (1g), high (2g)
- **Todos os backends**: Java, Java Reactive, .NET, Node.js, Python, Go
- **Verificações completas**: Build, startup, health check
- **Limpeza automática**: Remove containers e imagens de teste
- **Relatório detalhado**: Mostra quais configurações funcionam

#### Exemplo de saída:
```bash
=== JAVA TRADITIONAL MEMORY TESTS ===
▶ Testing java with minimal memory (256m max, 128m min)
✅ Build successful for java with minimal memory
❌ Java container crashed with minimal memory

▶ Testing java with low memory (512m max, 256m min)  
✅ Build successful for java with low memory
✅ Java container running successfully with low memory
✅ Java health check passed with low memory
```

#### Como interpretar os resultados:
- **✅ Build + Container + Health**: Configuração recomendada
- **✅ Build + Container, ⚠️ Health**: Configuração limítrofe
- **❌ Container crash**: Memória insuficiente

---

## 💪 **Scripts de Stress Test e Performance**

### `stress-test-all-backends.sh`

**Propósito**: Stress test sequencial em todos os backends usando wrk  
**Tempo**: ~10-15 minutos (5 backends)  
**Uso**: `./scripts/stress-test-all-backends.sh all`

#### Funcionalidades:
- **Teste isolado**: Cada backend é testado individualmente
- **Configuração padrão**: 30s duração, 100 conexões, 4 threads
- **Backends testados**:
  - java-reactive-native (porta 8085)
  - java-reactive-jvm (porta 8085)
  - nodejs (porta 3000)
  - python (porta 8000)
  - go (porta 8080)
- **Métricas coletadas**:
  - Requests/sec
  - Latency (avg, max, distribution)
  - Transfer/sec
  - Docker stats (CPU, memória)
- **Relatório automático**: SUMMARY.md com comparação

#### Exemplo de uso:
```bash
# Testar todos os backends
./scripts/stress-test-all-backends.sh all

# Resultados salvos em:
./stress-test-results/YYYYMMDD_HHMMSS/
```

### `stress-test-all.sh`

**Propósito**: Stress test usando docker-compose.loadtest.yml  
**Tempo**: Variável  
**Uso**: `./scripts/stress-test-all.sh`

#### Funcionalidades:
- Usa configuração loadtest específica
- Build paralelo de todos os backends
- Teste sequencial com isolamento
- Análise automática de resultados

### `stress-test-individual.sh`

**Propósito**: Stress test em backend específico  
**Uso**: `./scripts/stress-test-individual.sh <backend> <duration> <connections>`

```bash
# Exemplos:
./scripts/stress-test-individual.sh java-reactive-native 60s 200
./scripts/stress-test-individual.sh nodejs 30s 100
```

### `stress-test-sequential.sh`

**Propósito**: Stress test sequencial detalhado  
**Tempo**: ~20-30 minutos  
**Uso**: `./scripts/stress-test-sequential.sh`

#### Funcionalidades:
- Teste completo de todos os backends
- Múltiplas configurações de carga
- Análise de latência P50/P95/P99
- Gráficos de performance

### `benchmark-all-with-native.sh`

**Propósito**: Benchmark completo incluindo executáveis nativos  
**Tempo**: ~30-40 minutos  
**Uso**: `./scripts/benchmark-all-with-native.sh`

#### Funcionalidades:
- Compara JVM vs Native
- Métricas de startup time
- Uso de memória
- Throughput
- Latência

### `benchmark-native-comparison.sh`

**Propósito**: Comparação focada JVM vs Native  
**Tempo**: ~15-20 minutos  
**Uso**: `./scripts/benchmark-native-comparison.sh`

#### Funcionalidades:
- Foco em Java e Java Reactive
- Comparação lado a lado
- Relatório detalhado de diferenças
- Recomendações de uso

### `load-test.sh`

**Propósito**: Load test com configurações customizadas  
**Uso**: `./scripts/load-test.sh [options]`

```bash
# Exemplos:
./scripts/load-test.sh --duration 60s --connections 500
./scripts/load-test.sh --backend nodejs --threads 8
```

### `analyze-results.sh`

**Propósito**: Análise de resultados de stress test  
**Uso**: `./scripts/analyze-results.sh <results-dir>`

```bash
# Analisar resultados
./scripts/analyze-results.sh ./stress-test-results/20251112_223846/

# Gera:
# - ANALYSIS.md (relatório comparativo)
# - Gráficos de performance
# - Recomendações
```

---

## 📊 **Fluxo de Trabalho: Stress Test**

### **Teste Rápido de Performance**
```bash
# 1. Testar todos os backends (10-15 min)
./scripts/stress-test-all-backends.sh all

# 2. Ver resultados
cat ./stress-test-results/*/SUMMARY.md
```

### **Teste Completo de Performance**
```bash
# 1. Benchmark completo com native
./scripts/benchmark-all-with-native.sh

# 2. Análise detalhada
./scripts/analyze-results.sh ./benchmarks/results/latest/
```

### **Comparação JVM vs Native**
```bash
# Comparação focada
./scripts/benchmark-native-comparison.sh
```

### **Teste Individual Customizado**
```bash
# Teste específico com configurações personalizadas
./scripts/stress-test-individual.sh java-reactive-native 120s 500
```

## 📝 **Contribuindo**

Para adicionar novos scripts:

1. **Coloque o script** em `backend/scripts/`
2. **Adicione permissão de execução**: `chmod +x script.sh`
3. **Documente aqui** no README
4. **Siga o padrão** de nomenclatura: `ação-escopo.sh`
5. **Inclua comentários** no script
6. **Teste** antes de commitar

## 📚 **Documentação Relacionada**

- [📖 Guia de Build](../docs/guides/BUILD_GUIDE.md)
- [📜 Guia de Scripts](../docs/guides/BUILD_SCRIPTS_GUIDE.md)
- [🔧 README Principal](../README.md)
- [📚 Índice de Documentação](../docs/README.md)

---

**🎯 Scripts organizados e documentados para máxima produtividade!**
