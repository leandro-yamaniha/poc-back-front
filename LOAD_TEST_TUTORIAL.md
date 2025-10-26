# Tutorial: Teste de Carga Comparativo de Backends

Guia completo para executar testes de carga comparativos entre todos os backends do Beauty Salon Management System.

## 📋 Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Configuração do Ambiente](#configuração-do-ambiente)
3. [Preparação dos Backends](#preparação-dos-backends)
4. [Execução dos Testes](#execução-dos-testes)
5. [Análise dos Resultados](#análise-dos-resultados)
6. [Troubleshooting](#troubleshooting)

---

## 🎯 Objetivo

Comparar a performance de 8 implementações diferentes do backend:

1. **Java Traditional (JVM)** - Spring MVC com Virtual Threads
2. **Java Traditional (Native)** - GraalVM Native Image
3. **Java Reactive (JVM)** - Spring WebFlux
4. **Java Reactive (Native)** - WebFlux Native Image
5. **Go** - Implementação nativa
6. **Node.js** - Express/Fastify
7. **Python** - FastAPI
8. **.NET Core** - ASP.NET Core

---

## 📦 Pré-requisitos

### 1. Ferramentas Necessárias

#### macOS
```bash
# Instalar Homebrew (se não tiver)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar wrk (ferramenta de load testing)
brew install wrk

# Instalar Docker Desktop
brew install --cask docker

# Instalar jq (opcional, para parsing JSON)
brew install jq
```

#### Linux (Ubuntu/Debian)
```bash
# wrk
sudo apt-get update
sudo apt-get install -y wrk

# Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# jq
sudo apt-get install -y jq
```

#### Windows (WSL2)
```bash
# Usar WSL2 com Ubuntu e seguir instruções Linux acima
```

### 2. Verificar Instalação

```bash
# Verificar wrk
wrk --version

# Verificar Docker
docker --version
docker-compose --version

# Verificar recursos disponíveis
docker info | grep -E "CPUs|Total Memory"
```

### 3. Recursos Mínimos Recomendados

- **CPU:** 8 cores (mínimo 4)
- **RAM:** 16GB (mínimo 8GB)
- **Disco:** 20GB livres
- **Docker:** 8GB RAM alocado, 4 CPUs

---

## ⚙️ Configuração do Ambiente

### 1. Clonar/Atualizar Repositório

```bash
cd /path/to/beauty-salon-app
git pull origin backend-multiprofile
```

### 2. Configurar Docker

```bash
# Aumentar recursos do Docker Desktop
# Settings > Resources:
# - CPUs: 4-8
# - Memory: 8-12GB
# - Swap: 2GB
# - Disk: 60GB
```

### 3. Limpar Ambiente

```bash
# Parar containers existentes
docker-compose down -v

# Limpar imagens antigas (opcional)
docker system prune -a --volumes
```

---

## 🏗️ Preparação dos Backends

### Opção 1: Build Rápido (Recomendado para Teste)

Use imagens JVM para Java (mais rápido):

```bash
# Subir apenas backends JVM e outros
docker-compose -f docker-compose.loadtest.yml up -d \
  cassandra \
  java-jvm \
  java-reactive-jvm \
  go-backend \
  nodejs-backend \
  python-backend \
  dotnet-backend
```

### Opção 2: Build Completo (Inclui Native)

**Atenção:** Builds nativos levam 3-4 minutos cada!

```bash
# 1. Build executáveis nativos localmente
cd backend/scripts

# Java Traditional Native
./build-java-native.sh

# Java Reactive Native
./build-aot-native.sh

# 2. Subir todos os backends
cd ../..
docker-compose -f docker-compose.loadtest.yml up -d
```

### 3. Verificar Status dos Backends

```bash
# Aguardar todos os serviços ficarem healthy (2-3 minutos)
docker-compose -f docker-compose.loadtest.yml ps

# Verificar logs se algum falhar
docker-compose -f docker-compose.loadtest.yml logs <service-name>

# Testar health checks manualmente
curl http://localhost:10001/actuator/health  # Java Traditional JVM
curl http://localhost:10002/actuator/health  # Java Traditional Native
curl http://localhost:8085/actuator/health   # Java Reactive JVM
curl http://localhost:8086/actuator/health   # Java Reactive Native
curl http://localhost:8080/health            # Go
curl http://localhost:3000/health            # Node.js
curl http://localhost:8000/health            # Python
curl http://localhost:5000/health            # .NET
```

---

## 🚀 Execução dos Testes

### 1. Executar Script de Teste

```bash
cd backend/scripts

# Tornar executável
chmod +x load-test.sh

# Executar teste completo
./load-test.sh
```

### 2. O Que o Script Faz

1. **Verifica dependências** (wrk, docker, jq)
2. **Cria diretório de resultados** com timestamp
3. **Para cada backend:**
   - Verifica se está healthy
   - Executa warmup (10s)
   - Roda teste de carga (60s)
   - Coleta estatísticas do container
4. **Gera relatório** em Markdown

### 3. Parâmetros do Teste

Configuração padrão (editável no script):

```bash
DURATION=60              # 60 segundos por backend
CONNECTIONS=100          # 100 conexões concorrentes
REQUESTS_PER_SEC=1000    # Target de 1000 req/s
WARMUP_TIME=10           # 10 segundos de warmup
CPU_LIMIT="1.0"          # 1 CPU core
MEMORY_LIMIT="512m"      # 512MB RAM
```

### 4. Personalizar Teste

Edite `load-test.sh` para ajustar:

```bash
# Teste mais leve (desenvolvimento)
DURATION=30
CONNECTIONS=50
REQUESTS_PER_SEC=500

# Teste mais pesado (produção)
DURATION=120
CONNECTIONS=200
REQUESTS_PER_SEC=2000
```

### 5. Monitorar Execução

Em outro terminal:

```bash
# Monitorar recursos em tempo real
docker stats

# Monitorar logs de um backend específico
docker logs -f java-jvm-loadtest

# Monitorar todos os logs
docker-compose -f docker-compose.loadtest.yml logs -f
```

---

## 📊 Análise dos Resultados

### 1. Localizar Resultados

```bash
cd backend/load-test-results

# Listar execuções
ls -la

# Entrar na execução mais recente
cd $(ls -t | head -1)

# Listar arquivos
ls -la
```

### 2. Estrutura de Arquivos

```
load-test-results/
└── 20241026_174530/
    ├── config.txt                      # Configuração do teste
    ├── LOAD_TEST_REPORT.md            # Relatório principal
    ├── java-jvm.txt                   # Resultado wrk
    ├── java-jvm_stats.txt             # Stats do container
    ├── java-native.txt
    ├── java-native_stats.txt
    ├── java-reactive-jvm.txt
    ├── java-reactive-jvm_stats.txt
    ├── java-reactive-native.txt
    ├── java-reactive-native_stats.txt
    ├── go.txt
    ├── go_stats.txt
    ├── nodejs.txt
    ├── nodejs_stats.txt
    ├── python.txt
    ├── python_stats.txt
    ├── dotnet.txt
    └── dotnet_stats.txt
```

### 3. Visualizar Relatório

```bash
# Visualizar no terminal
cat LOAD_TEST_REPORT.md

# Abrir no editor
code LOAD_TEST_REPORT.md

# Converter para HTML (opcional)
brew install pandoc
pandoc LOAD_TEST_REPORT.md -o report.html
open report.html
```

### 4. Métricas Importantes

#### Throughput (Requests/sec)
- **Maior = Melhor**
- Indica quantas requisições o backend processa por segundo
- Exemplo: 15,000 req/s vs 8,000 req/s

#### Latência Média (Avg Latency)
- **Menor = Melhor**
- Tempo médio de resposta
- Exemplo: 5ms vs 15ms

#### Latência Máxima (Max Latency)
- **Menor = Melhor**
- Pior caso observado
- Importante para SLA

#### Transfer/sec
- Volume de dados transferido
- Indica eficiência de rede

#### Uso de CPU/Memória
- Recursos consumidos durante o teste
- Importante para custo operacional

### 5. Exemplo de Análise

```markdown
## Ranking por Performance

1. **Go**: 18,500 req/s, 4.2ms latência
2. **Java Native**: 16,200 req/s, 5.1ms latência
3. **Java Reactive Native**: 15,800 req/s, 5.5ms latência
4. **Node.js**: 12,300 req/s, 7.8ms latência
5. **Java JVM**: 11,500 req/s, 8.2ms latência
6. **Java Reactive JVM**: 10,800 req/s, 8.9ms latência
7. **.NET Core**: 9,200 req/s, 10.5ms latência
8. **Python**: 6,500 req/s, 14.2ms latência

## Insights

- **Native vs JVM**: ~40% mais throughput
- **Go**: Líder absoluto em performance
- **Python**: Melhor para prototipagem, não para alta carga
```

---

## 🔧 Troubleshooting

### Problema: Backend não inicia

```bash
# Verificar logs
docker-compose -f docker-compose.loadtest.yml logs <service>

# Verificar Cassandra
docker-compose -f docker-compose.loadtest.yml logs cassandra

# Reiniciar serviço específico
docker-compose -f docker-compose.loadtest.yml restart <service>
```

### Problema: wrk não encontrado

```bash
# macOS
brew install wrk

# Linux
sudo apt-get install wrk

# Verificar
wrk --version
```

### Problema: Memória insuficiente

```bash
# Reduzir número de backends testados
docker-compose -f docker-compose.loadtest.yml up -d \
  cassandra java-jvm go-backend nodejs-backend

# Ou aumentar memória do Docker
# Docker Desktop > Settings > Resources > Memory: 8GB+
```

### Problema: Build Native falha

```bash
# Verificar GraalVM
native-image --version

# Aumentar memória do build
export NATIVE_IMAGE_OPTS="-J-Xmx8g"

# Rebuild
cd backend/scripts
./build-java-native.sh
```

### Problema: Portas em uso

```bash
# Verificar portas
lsof -i :10001
lsof -i :8085

# Matar processo
kill -9 <PID>

# Ou usar portas alternativas no docker-compose
```

### Problema: Teste muito lento

```bash
# Reduzir duração e conexões
# Editar load-test.sh:
DURATION=30
CONNECTIONS=50
```

---

## 📈 Testes Avançados

### 1. Teste de Stress (Encontrar Limite)

```bash
# Aumentar gradualmente até falhar
for conn in 50 100 200 400 800; do
  wrk -t4 -c$conn -d30s http://localhost:8080/api/customers
  sleep 10
done
```

### 2. Teste de Latência (Percentis)

```bash
# wrk com script Lua para percentis
wrk -t4 -c100 -d60s --latency \
  -s scripts/latency.lua \
  http://localhost:8080/api/customers
```

### 3. Teste de Endurance (Longa Duração)

```bash
# Teste de 30 minutos
wrk -t4 -c100 -d1800s http://localhost:8080/api/customers
```

### 4. Teste de Diferentes Endpoints

```bash
# GET
wrk -t4 -c100 -d60s http://localhost:8080/api/customers

# POST (com script Lua)
wrk -t4 -c100 -d60s -s post.lua http://localhost:8080/api/customers
```

---

## 🎯 Melhores Práticas

### 1. Antes do Teste

- ✅ Fechar aplicações pesadas
- ✅ Desabilitar antivírus temporariamente
- ✅ Conectar laptop na tomada
- ✅ Usar rede cabeada (não Wi-Fi)
- ✅ Limpar cache do Docker

### 2. Durante o Teste

- ✅ Não usar o computador
- ✅ Monitorar recursos (docker stats)
- ✅ Verificar logs por erros
- ✅ Anotar observações

### 3. Depois do Teste

- ✅ Salvar resultados com nome descritivo
- ✅ Comparar com testes anteriores
- ✅ Documentar mudanças de configuração
- ✅ Compartilhar insights com equipe

---

## 📚 Referências

### Ferramentas

- **wrk**: https://github.com/wg/wrk
- **Docker**: https://docs.docker.com/
- **Docker Compose**: https://docs.docker.com/compose/

### Documentação dos Backends

- **Java Traditional**: `backend/java/BUILD_GUIDE.md`
- **Java Reactive**: `backend/java-reactive/BUILD_GUIDE.md`
- **Multiprofile**: `backend/MULTIPROFILE_SUMMARY.md`

### Conceitos

- **Load Testing**: https://en.wikipedia.org/wiki/Load_testing
- **Throughput vs Latency**: https://www.nginx.com/blog/testing-the-performance-of-nginx-and-nginx-plus-web-servers/
- **GraalVM Native Image**: https://www.graalvm.org/latest/reference-manual/native-image/

---

## ✅ Checklist de Execução

### Preparação
- [ ] Ferramentas instaladas (wrk, docker, jq)
- [ ] Docker com recursos adequados (4 CPU, 8GB RAM)
- [ ] Repositório atualizado
- [ ] Ambiente limpo (docker-compose down -v)

### Build
- [ ] Backends compilados (JVM ou Native)
- [ ] Imagens Docker criadas
- [ ] Cassandra iniciado e healthy

### Teste
- [ ] Script load-test.sh executável
- [ ] Todos backends healthy
- [ ] Teste executado sem erros
- [ ] Resultados salvos

### Análise
- [ ] Relatório gerado
- [ ] Métricas analisadas
- [ ] Insights documentados
- [ ] Resultados compartilhados

---

## 🎉 Conclusão

Agora você tem um sistema completo de testes de carga para comparar todos os backends!

**Próximos passos:**
1. Execute o teste básico
2. Analise os resultados
3. Ajuste configurações conforme necessário
4. Compare diferentes cenários
5. Documente suas descobertas

**Dúvidas?** Consulte a seção de Troubleshooting ou os guias de build específicos.

Bons testes! 🚀
