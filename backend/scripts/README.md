# 🔧 Backend Scripts

Esta pasta contém todos os scripts automatizados para gerenciamento dos backends do Beauty Salon Management System.

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
