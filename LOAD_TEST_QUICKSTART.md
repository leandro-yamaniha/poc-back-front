# Load Test Quick Start

Guia rápido para executar testes de carga comparativos.

## 🚀 Início Rápido (5 minutos)

### 1. Instalar Dependências

```bash
# macOS
brew install wrk docker

# Verificar
wrk --version
docker --version
```

### 2. Subir Backends

```bash
# Opção A: Apenas JVM (mais rápido - 2 min)
docker-compose -f docker-compose.loadtest.yml up -d \
  cassandra java-jvm java-reactive-jvm go-backend nodejs-backend

# Opção B: Todos incluindo Native (mais lento - 5 min)
docker-compose -f docker-compose.loadtest.yml up -d
```

### 3. Aguardar Backends Ficarem Prontos

```bash
# Aguardar ~2-3 minutos
docker-compose -f docker-compose.loadtest.yml ps

# Verificar health
curl http://localhost:10001/actuator/health  # Java JVM
curl http://localhost:8085/actuator/health   # Reactive JVM
curl http://localhost:8080/health            # Go
curl http://localhost:3000/health            # Node.js
```

### 4. Executar Teste

```bash
cd backend/scripts
./load-test.sh
```

### 5. Ver Resultados

```bash
# Relatório principal
cd backend/load-test-results
cd $(ls -t | head -1)
cat LOAD_TEST_REPORT.md

# Análise automatizada
cd backend/scripts
./analyze-results.sh ../load-test-results/$(ls -t ../load-test-results | head -1)
```

## 📊 O Que Será Testado

- **Duração:** 60 segundos por backend
- **Conexões:** 100 simultâneas
- **Target:** 1,000 req/s
- **Recursos:** 1 CPU, 512MB RAM (igual para todos)

## 🎯 Backends Testados

1. Java Traditional (JVM) - Port 10001
2. Java Traditional (Native) - Port 10002
3. Java Reactive (JVM) - Port 8085
4. Java Reactive (Native) - Port 8086
5. Go - Port 8080
6. Node.js - Port 3000
7. Python - Port 8000
8. .NET Core - Port 5000

## 📁 Arquivos Criados

- `docker-compose.loadtest.yml` - Configuração dos backends
- `backend/scripts/load-test.sh` - Script principal de teste
- `backend/scripts/analyze-results.sh` - Análise automatizada
- `LOAD_TEST_TUTORIAL.md` - Tutorial completo (550+ linhas)

## 🔧 Troubleshooting Rápido

### Backend não inicia
```bash
docker-compose -f docker-compose.loadtest.yml logs <service-name>
docker-compose -f docker-compose.loadtest.yml restart <service-name>
```

### Memória insuficiente
```bash
# Testar apenas alguns backends
docker-compose -f docker-compose.loadtest.yml up -d \
  cassandra java-jvm go-backend nodejs-backend
```

### wrk não encontrado
```bash
brew install wrk  # macOS
sudo apt-get install wrk  # Linux
```

## 📚 Documentação Completa

Para instruções detalhadas, consulte:
- **Tutorial Completo:** `LOAD_TEST_TUTORIAL.md`
- **Build Guides:** `backend/java/BUILD_GUIDE.md`, `backend/java-reactive/BUILD_GUIDE.md`
- **Multiprofile:** `backend/MULTIPROFILE_SUMMARY.md`

## ⚡ Comandos Úteis

```bash
# Monitorar recursos
docker stats

# Parar tudo
docker-compose -f docker-compose.loadtest.yml down

# Limpar tudo
docker-compose -f docker-compose.loadtest.yml down -v
docker system prune -a

# Testar endpoint manualmente
wrk -t2 -c10 -d10s http://localhost:8080/api/customers

# Ver logs em tempo real
docker-compose -f docker-compose.loadtest.yml logs -f
```

## 🎯 Resultados Esperados

### Performance Típica (1 CPU, 512MB)

| Backend | Req/s | Latência | Startup |
|---------|-------|----------|---------|
| Go | 15-20k | 4-6ms | <1s |
| Java Native | 12-18k | 5-8ms | 1-2s |
| Reactive Native | 12-16k | 5-9ms | 1-2s |
| Node.js | 10-15k | 7-10ms | 2-3s |
| Java JVM | 8-12k | 8-12ms | 5-10s |
| Reactive JVM | 8-12k | 8-12ms | 5-10s |
| .NET Core | 7-10k | 10-15ms | 3-5s |
| Python | 5-8k | 12-20ms | 3-5s |

*Valores aproximados, variam conforme hardware*

## 🎊 Próximos Passos

1. ✅ Execute o teste básico
2. ✅ Analise os resultados
3. ✅ Compare JVM vs Native
4. ✅ Compare Traditional vs Reactive
5. ✅ Documente suas descobertas

---

**Dúvidas?** Consulte `LOAD_TEST_TUTORIAL.md` para guia completo!
