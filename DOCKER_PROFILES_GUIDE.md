# Docker Compose Profiles Guide

Guia completo para usar Docker Compose com suporte aos profiles Maven (JVM e Native).

## 📋 Arquivos Criados

### Dockerfiles
- **`Dockerfile.jvm`**: Build e execução JVM tradicional
- **`Dockerfile.native-profile`**: Build e execução GraalVM Native Image

### Docker Compose
- **`docker-compose.profiles.yml`**: Orquestração com suporte a profiles

## 🚀 Como Usar

### 1. Executar apenas JVM

```bash
# Iniciar Cassandra + API JVM
docker-compose -f docker-compose.profiles.yml --profile jvm up -d

# Acessar
curl http://localhost:8085/actuator/health

# Parar
docker-compose -f docker-compose.profiles.yml --profile jvm down
```

**Características:**
- Porta: `8085`
- Memória: 256-512MB
- Startup: ~5-10 segundos
- Container: `beauty-salon-api-jvm`

---

### 2. Executar apenas Native

```bash
# Iniciar Cassandra + API Native
docker-compose -f docker-compose.profiles.yml --profile native up -d

# Acessar
curl http://localhost:8086/actuator/health

# Parar
docker-compose -f docker-compose.profiles.yml --profile native down
```

**Características:**
- Porta: `8086` (diferente para evitar conflito)
- Memória: 128-256MB
- Startup: ~2-3 segundos
- Container: `beauty-salon-api-native`

---

### 3. Executar Ambos (Comparação)

```bash
# Iniciar Cassandra + JVM + Native simultaneamente
docker-compose -f docker-compose.profiles.yml --profile compare up -d

# Testar JVM
curl http://localhost:8085/actuator/health

# Testar Native
curl http://localhost:8086/actuator/health

# Ver logs
docker-compose -f docker-compose.profiles.yml --profile compare logs -f

# Parar
docker-compose -f docker-compose.profiles.yml --profile compare down
```

**Características:**
- JVM na porta `8085`
- Native na porta `8086`
- Ambos compartilham o mesmo Cassandra
- Ideal para benchmarks comparativos

---

## 🔧 Build das Imagens

### Build JVM
```bash
cd backend/java-reactive
docker build -f Dockerfile.jvm -t beauty-salon-reactive:jvm .
```

### Build Native
```bash
cd backend/java-reactive
docker build -f Dockerfile.native-profile -t beauty-salon-reactive:native .
```

**⚠️ Nota**: O build Native demora 3-4 minutos devido à compilação GraalVM.

---

## 📊 Comparação de Recursos

| Aspecto | JVM Profile | Native Profile |
|---------|-------------|----------------|
| **Porta** | 8085 | 8086 |
| **Memória Limite** | 512MB | 256MB |
| **Memória Reservada** | 256MB | 128MB |
| **CPU Limite** | 1.0 | 0.5 |
| **CPU Reservada** | 0.5 | 0.25 |
| **Startup Time** | 30s | 15s |
| **Health Check Interval** | 30s | 30s |
| **Build Time** | ~2 min | ~4 min |

---

## 🎯 Casos de Uso

### Desenvolvimento Local (JVM)
```bash
# Rápido para testar mudanças
docker-compose -f docker-compose.profiles.yml --profile jvm up -d

# Rebuild rápido
docker-compose -f docker-compose.profiles.yml --profile jvm up -d --build
```

### Teste de Performance (Native)
```bash
# Startup e memória otimizados
docker-compose -f docker-compose.profiles.yml --profile native up -d
```

### Benchmark Comparativo
```bash
# Rodar ambos simultaneamente
docker-compose -f docker-compose.profiles.yml --profile compare up -d

# Monitorar recursos
docker stats beauty-salon-api-jvm-compare beauty-salon-api-native-compare
```

---

## 📈 Monitoramento

### Ver Logs
```bash
# JVM
docker logs -f beauty-salon-api-jvm

# Native
docker logs -f beauty-salon-api-native

# Ambos (modo compare)
docker logs -f beauty-salon-api-jvm-compare
docker logs -f beauty-salon-api-native-compare
```

### Estatísticas de Recursos
```bash
# Tempo real
docker stats

# Específico
docker stats beauty-salon-api-jvm
docker stats beauty-salon-api-native
```

### Health Checks
```bash
# JVM
curl http://localhost:8085/actuator/health

# Native
curl http://localhost:8086/actuator/health

# Métricas
curl http://localhost:8085/actuator/metrics
curl http://localhost:8086/actuator/metrics
```

---

## 🐛 Troubleshooting

### Erro: "Port already in use"
```bash
# Verificar portas em uso
lsof -i :8085
lsof -i :8086

# Parar containers
docker-compose -f docker-compose.profiles.yml down
```

### Erro: "Cassandra not healthy"
```bash
# Verificar status
docker ps | grep cassandra

# Ver logs
docker logs beauty-salon-cassandra-profiles

# Restart
docker-compose -f docker-compose.profiles.yml restart cassandra
```

### Build Native Falha
```bash
# Aumentar memória do Docker Desktop
# Settings > Resources > Memory: 8GB+

# Rebuild
docker-compose -f docker-compose.profiles.yml --profile native build --no-cache
```

### Container não inicia
```bash
# Ver logs detalhados
docker-compose -f docker-compose.profiles.yml --profile jvm logs

# Verificar health check
docker inspect beauty-salon-api-jvm | grep -A 10 Health
```

---

## 🧪 Testes de Integração

### Script de Teste Automatizado
```bash
#!/bin/bash
# test-profiles.sh

echo "🧪 Testando Profile JVM..."
docker-compose -f docker-compose.profiles.yml --profile jvm up -d
sleep 30
curl -f http://localhost:8085/actuator/health || echo "❌ JVM falhou"
docker-compose -f docker-compose.profiles.yml --profile jvm down

echo "🧪 Testando Profile Native..."
docker-compose -f docker-compose.profiles.yml --profile native up -d
sleep 15
curl -f http://localhost:8086/actuator/health || echo "❌ Native falhou"
docker-compose -f docker-compose.profiles.yml --profile native down

echo "✅ Testes concluídos!"
```

### Benchmark Comparativo
```bash
# Usar script existente
cd backend/scripts
./benchmark-native-simple.sh

# Ou criar novo com Docker
docker-compose -f docker-compose.profiles.yml --profile compare up -d
# Executar testes de carga em ambas as portas
docker-compose -f docker-compose.profiles.yml --profile compare down
```

---

## 📝 Variáveis de Ambiente

### Comuns (JVM e Native)
```yaml
SPRING_PROFILES_ACTIVE=docker
SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra
SPRING_DATA_CASSANDRA_PORT=9042
SPRING_DATA_CASSANDRA_KEYSPACE_NAME=beauty_salon
SERVER_PORT=8085
LOGGING_LEVEL_ROOT=INFO
```

### Específicas JVM
```yaml
JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC
```

### Customizar
```bash
# Criar arquivo .env
cat > .env << EOF
CASSANDRA_MAX_HEAP=1G
JVM_MEMORY_MAX=768m
NATIVE_MEMORY_MAX=384m
EOF

# Usar no docker-compose
docker-compose -f docker-compose.profiles.yml --profile jvm --env-file .env up -d
```

---

## 🎉 Resumo dos Comandos

```bash
# JVM apenas
docker-compose -f docker-compose.profiles.yml --profile jvm up -d

# Native apenas
docker-compose -f docker-compose.profiles.yml --profile native up -d

# Ambos para comparação
docker-compose -f docker-compose.profiles.yml --profile compare up -d

# Parar tudo
docker-compose -f docker-compose.profiles.yml down

# Rebuild
docker-compose -f docker-compose.profiles.yml --profile jvm up -d --build

# Logs
docker-compose -f docker-compose.profiles.yml logs -f

# Status
docker-compose -f docker-compose.profiles.yml ps
```

---

## 🔗 Referências

- [Docker Compose Profiles](https://docs.docker.com/compose/profiles/)
- [Spring Boot Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [GraalVM Container Images](https://www.graalvm.org/latest/docs/getting-started/container-images/)

---

## ✅ Checklist de Validação

- [ ] Cassandra inicia e fica healthy
- [ ] JVM profile inicia em ~30 segundos
- [ ] Native profile inicia em ~15 segundos
- [ ] Health checks passam para ambos
- [ ] Portas 8085 (JVM) e 8086 (Native) acessíveis
- [ ] Logs não mostram erros críticos
- [ ] Memória dentro dos limites configurados
- [ ] Ambos profiles podem rodar simultaneamente

Agora você tem controle total sobre JVM e Native via Docker Compose! 🚀
