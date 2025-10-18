# 🚀 Beauty Salon Backend - Guia de Stress Tests

Este guia fornece instruções completas para executar stress tests no backend da aplicação Beauty Salon, avaliando performance, robustez e capacidade de carga.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Tipos de Stress Tests](#tipos-de-stress-tests)
3. [Pré-requisitos](#pré-requisitos)
4. [Execução dos Testes](#execução-dos-testes)
5. [Interpretação dos Resultados](#interpretação-dos-resultados)
6. [Configurações Avançadas](#configurações-avançadas)
7. [Troubleshooting](#troubleshooting)

## 🎯 Visão Geral

Os stress tests implementados avaliam:

- **Performance**: Tempo de resposta e throughput
- **Concorrência**: Comportamento sob carga simultânea
- **Robustez**: Estabilidade sob stress
- **Escalabilidade**: Limites de capacidade

### Métricas Coletadas

- ⏱️ **Tempo de Resposta**: Médio, mínimo e máximo
- 🚀 **Throughput**: Requisições por segundo
- 📊 **Taxa de Sucesso**: Percentual de requisições bem-sucedidas
- 💾 **Utilização de Recursos**: CPU, memória, conexões DB

## 🧪 Tipos de Stress Tests

### 1. Testes Programáticos (Spring Boot + MockMvc)

**Localização**: `src/test/java/com/beautysalon/performance/BackendStressTest.java`

**Cenários Implementados**:
- **Criação Concorrente**: 100 usuários criando 10 clientes cada
- **Busca Concorrente**: 100 usuários fazendo 10 buscas cada
- **Carga Mista**: Operações CRUD simultâneas

**Características**:
- ✅ Integração com Testcontainers (Cassandra real)
- ✅ Relatórios detalhados com métricas
- ✅ Validação automática de performance
- ✅ Execução isolada e reproduzível

### 2. JMeter Stress Test

**Localização**: `src/test/resources/stress-test/beauty-salon-stress-test.jmx`

**Cenários Implementados**:
- **Thread Group**: 50 usuários simultâneos, 10 loops cada
- **CRUD Completo**: Create → Read → Update → Delete
- **Extração de Dados**: IDs capturados dinamicamente
- **Relatórios Visuais**: Gráficos e tabelas detalhadas

**Características**:
- ✅ Teste de carga real via HTTP
- ✅ Relatórios HTML profissionais
- ✅ Configuração flexível de carga
- ✅ Monitoramento em tempo real

### 3. Teste de Carga Simples (curl)

**Implementação**: Script bash com curl paralelo

**Características**:
- ✅ Teste rápido e simples
- ✅ Sem dependências externas
- ✅ Ideal para validação básica

## 🔧 Pré-requisitos

### Obrigatórios

1. **Backend Rodando**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Docker** (para Testcontainers):
   ```bash
   docker --version
   ```

### Opcionais

3. **JMeter** (para testes JMeter):
   ```bash
   # macOS
   brew install jmeter
   
   # Ubuntu
   sudo apt-get install jmeter
   
   # Windows
   # Baixar de: https://jmeter.apache.org/download_jmeter.cgi
   ```

## 🚀 Execução dos Testes

### Método 1: Script Automatizado (Recomendado)

```bash
cd backend
./scripts/run-stress-tests.sh
```

**Menu Interativo**:
```
🚀 Beauty Salon Backend Stress Test Runner
==========================================

Escolha o tipo de stress test:
1) Testes Programáticos (Spring Boot + MockMvc)
2) JMeter Stress Test  
3) Teste de Carga Simples (curl)
4) Executar Todos os Testes
5) Sair
```

**Execução Direta**:
```bash
# Testes programáticos
./scripts/run-stress-tests.sh programmatic

# JMeter
./scripts/run-stress-tests.sh jmeter

# Teste simples
./scripts/run-stress-tests.sh curl

# Todos os testes
./scripts/run-stress-tests.sh all
```

### Método 2: Execução Manual

#### Testes Programáticos
```bash
cd backend
./mvnw test -Dtest="BackendStressTest"
```

#### JMeter (Modo GUI)
```bash
jmeter -t src/test/resources/stress-test/beauty-salon-stress-test.jmx
```

#### JMeter (Modo CLI)
```bash
jmeter -n -t src/test/resources/stress-test/beauty-salon-stress-test.jmx \
       -l target/stress-test-results/results.jtl \
       -e -o target/stress-test-results/html-report
```

## 📊 Interpretação dos Resultados

### Testes Programáticos

**Exemplo de Output**:
```
📈 RELATÓRIO DE STRESS TEST - CRIAÇÃO DE CLIENTES
============================================================
⏱️  Tempo total: 15432ms
✅ Requisições bem-sucedidas: 995/1000
❌ Requisições com erro: 5/1000
📊 Taxa de sucesso: 99.50%
🚀 Throughput: 64.47 req/s
⚡ Tempo de resposta médio: 245.67ms
⚡ Tempo de resposta mínimo: 89ms
⚡ Tempo de resposta máximo: 1205ms
```

**Interpretação**:
- ✅ **Taxa de Sucesso > 95%**: Excelente
- ✅ **Throughput > 50 req/s**: Bom para aplicação típica
- ✅ **Tempo Médio < 500ms**: Responsivo
- ⚠️ **Tempo Máximo < 2000ms**: Aceitável

### JMeter Reports

**Localização**: `target/stress-test-results/html-report/index.html`

**Métricas Principais**:
- **Response Time Over Time**: Evolução temporal
- **Throughput Over Time**: Taxa de requisições
- **Error Rate**: Percentual de erros
- **Response Time Percentiles**: Distribuição de latência

### Benchmarks de Performance

| Métrica | Excelente | Bom | Aceitável | Ruim |
|---------|-----------|-----|-----------|------|
| Taxa de Sucesso | >99% | >95% | >90% | <90% |
| Throughput (req/s) | >100 | >50 | >20 | <20 |
| Tempo Médio (ms) | <200 | <500 | <1000 | >1000 |
| Tempo P95 (ms) | <500 | <1000 | <2000 | >2000 |

## ⚙️ Configurações Avançadas

### Ajustar Carga dos Testes Programáticos

**Arquivo**: `BackendStressTest.java`

```java
private static final int CONCURRENT_USERS = 100;    // Usuários simultâneos
private static final int REQUESTS_PER_USER = 10;    // Requisições por usuário
private static final int TIMEOUT_SECONDS = 60;      // Timeout do teste
```

### Ajustar Carga do JMeter

**Arquivo**: `beauty-salon-stress-test.jmx`

- **Thread Group → Number of Threads**: Usuários simultâneos
- **Thread Group → Ramp-up Period**: Tempo para atingir carga máxima
- **Loop Controller → Loop Count**: Repetições por usuário

### Configurações de Sistema

**JVM Options** (para testes mais intensos):
```bash
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"
```

**Docker Resources** (para Testcontainers):
```bash
# Aumentar limites do Docker Desktop
# Memory: 4GB+
# CPUs: 4+
```

## 🔍 Troubleshooting

### Problemas Comuns

#### 1. Backend Não Está Rodando
```
[ERROR] Backend não está rodando em http://localhost:8080
```
**Solução**:
```bash
cd backend
./mvnw spring-boot:run
```

#### 2. Testcontainers Falha
```
Could not start container
```
**Soluções**:
- Verificar se Docker está rodando
- Aumentar recursos do Docker
- Limpar containers antigos: `docker system prune`

#### 3. Timeout nos Testes
```
Teste não completou dentro do timeout
```
**Soluções**:
- Reduzir carga (menos usuários/requisições)
- Aumentar timeout
- Verificar recursos do sistema

#### 4. Alta Taxa de Erro
```
Taxa de sucesso muito baixa: 45.2%
```
**Investigação**:
- Verificar logs do backend
- Monitorar recursos (CPU, memória)
- Verificar conexões com Cassandra
- Reduzir carga gradualmente

### Logs Úteis

**Backend Logs**:
```bash
tail -f backend/logs/application.log
```

**Docker Logs**:
```bash
docker logs <container-id>
```

**Sistema (macOS)**:
```bash
top -pid $(pgrep java)
```

## 📈 Monitoramento Durante Testes

### Recursos do Sistema
```bash
# CPU e Memória
htop

# Conexões de Rede
netstat -an | grep 8080

# Processos Java
jps -v
```

### Métricas do Backend
- **Actuator Endpoints**: http://localhost:8080/actuator/metrics
- **Health Check**: http://localhost:8080/actuator/health
- **JVM Metrics**: http://localhost:8080/actuator/metrics/jvm.memory.used

### Cassandra Metrics
```bash
# Dentro do container Testcontainers
docker exec -it <cassandra-container> nodetool status
docker exec -it <cassandra-container> nodetool tpstats
```

## 🎯 Próximos Passos

1. **Automatização CI/CD**: Integrar stress tests no pipeline
2. **Monitoramento Contínuo**: Alertas de performance
3. **Testes de Regressão**: Comparar performance entre versões
4. **Profiling**: Identificar gargalos específicos
5. **Otimização**: Tuning baseado nos resultados

## 📞 Suporte

Para dúvidas ou problemas:
1. Verificar logs detalhados
2. Consultar documentação do Spring Boot
3. Revisar configurações do Testcontainers
4. Validar recursos do sistema

---

**Última Atualização**: $(date)
**Versão**: 1.0.0
