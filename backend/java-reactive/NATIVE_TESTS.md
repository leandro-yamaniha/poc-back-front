# Testes para Imagem Nativa - Java Reactive

## 📋 Visão Geral

Este documento descreve os testes criados para validar a funcionalidade da aplicação Spring Boot Reactive compilada como imagem nativa com GraalVM.

## 🧪 Tipos de Testes

### 1. Testes de Integração (`NativeImageIntegrationTest.java`)

Testes completos de integração que validam todas as operações CRUD da API.

**Localização**: `src/test/java/com/beautysalon/reactive/native_tests/NativeImageIntegrationTest.java`

**Testes Incluídos**:
- ✅ `shouldStartApplicationSuccessfully()` - Valida que o contexto da aplicação carrega
- ✅ `shouldAccessHealthEndpoint()` - Testa o endpoint de health check
- ✅ `shouldListCustomers()` - Lista todos os customers
- ✅ `shouldCreateCustomer()` - Cria um novo customer
- ✅ `shouldGetCustomerById()` - Busca customer por ID
- ✅ `shouldUpdateCustomer()` - Atualiza um customer existente
- ✅ `shouldDeleteCustomer()` - Deleta um customer
- ✅ `shouldSearchCustomersByName()` - Busca customers por nome

**Como Executar**:
```bash
cd backend/java-reactive
./mvnw test -Dtest=NativeImageIntegrationTest -Dnative.image.test=true
```

### 2. Testes de Performance (`NativePerformanceTest.java`)

Testes que validam as características de performance da imagem nativa.

**Localização**: `src/test/java/com/beautysalon/reactive/native_tests/NativePerformanceTest.java`

**Testes Incluídos**:
- ✅ `shouldRespondQuicklyToHealthCheck()` - Valida tempo de resposta < 100ms
- ✅ `shouldHandleMultipleRequestsEfficiently()` - Processa 100 requisições com média < 50ms
- ✅ `shouldMaintainLowMemoryFootprint()` - Valida uso de memória < 256MB
- ✅ `shouldHaveInstantStartupTime()` - Confirma startup instantâneo

**Como Executar**:
```bash
cd backend/java-reactive
./mvnw test -Dtest=NativePerformanceTest -Dnative.image.test=true
```

### 3. Script de Teste Automatizado (`test-native-image.sh`)

Script bash que executa testes end-to-end contra o executável nativo.

**Localização**: `backend/scripts/test-native-image.sh`

**Funcionalidades**:
- 🚀 Inicia o Cassandra automaticamente
- 🔧 Inicia a aplicação nativa em background
- ✅ Executa 7 testes de integração via curl
- 📊 Mede performance (100 requisições)
- 🧹 Cleanup automático

**Como Executar**:
```bash
cd backend/scripts
chmod +x test-native-image.sh
./test-native-image.sh
```

## 📊 Métricas Esperadas

### Performance
- **Startup Time**: < 2 segundos
- **Response Time**: < 100ms por requisição
- **Throughput**: > 1000 req/s
- **Memory Usage**: < 256MB

### Funcionalidade
- **Health Check**: Status UP
- **CRUD Operations**: 100% funcionais
- **Cassandra Connection**: Estável
- **API Endpoints**: Todos operacionais

## 🔧 Pré-requisitos

### Para Testes Java
```bash
# Compilar a imagem nativa
cd backend/java-reactive
./mvnw -Pnative native:compile -DskipTests

# Cassandra rodando (via Docker)
docker-compose -f docker-compose.aot-native.yml up -d cassandra
```

### Para Script de Teste
```bash
# Docker Desktop rodando
# Executável nativo compilado em target/beauty-salon-reactive
```

## 🎯 Casos de Uso

### Teste Rápido de Sanidade
```bash
# Apenas verifica se a aplicação inicia e responde
curl http://localhost:8085/actuator/health
```

### Teste Completo de Integração
```bash
# Executa todos os testes de integração
./backend/scripts/test-native-image.sh
```

### Teste de Performance
```bash
# Executa testes de performance
cd backend/java-reactive
./mvnw test -Dtest=NativePerformanceTest -Dnative.image.test=true
```

### Teste de Carga
```bash
# Usando Apache Bench
ab -n 1000 -c 10 http://localhost:8085/actuator/health

# Usando wrk
wrk -t4 -c100 -d30s http://localhost:8085/actuator/health
```

## 📝 Exemplo de Saída

### Script de Teste Automatizado
```
▶ Testando Imagem Nativa do Java Reactive

✅ Executável nativo encontrado
✅ Docker está rodando
✅ Cassandra está rodando
▶ Iniciando aplicação nativa em background...
PID da aplicação: 12345
▶ Aguardando aplicação inicializar...
✅ Aplicação iniciou com sucesso!

▶ Executando testes de integração...

▶ Teste 1: Health Check
✅ Health check passou

▶ Teste 2: Criar Customer
✅ Customer criado com ID: 4045000d-2cad-4fc7-8a09-4e65284250d9

▶ Teste 3: Listar Customers
✅ Listagem retornou 1 customer(s)

▶ Teste 4: Buscar Customer por ID
✅ Customer encontrado por ID

▶ Teste 5: Atualizar Customer
✅ Customer atualizado

▶ Teste 6: Deletar Customer
✅ Customer deletado

▶ Teste 7: Performance (100 requisições)
✅ 100 requisições em 2450ms (média: 24ms por requisição)

▶ Finalizando aplicação...

✅ Todos os testes concluídos!

📊 Resumo dos Testes:
  ✅ Health Check
  ✅ CRUD Operations (Create, Read, Update, Delete)
  ✅ Performance Test

🎉 Aplicação nativa funcionando perfeitamente!
```

## 🐛 Troubleshooting

### Problema: Aplicação não inicia
```bash
# Verificar logs
cat /tmp/native-app.log

# Verificar se a porta está livre
lsof -i :8085

# Verificar Cassandra
docker logs beauty-salon-cassandra-aot
```

### Problema: Testes falham
```bash
# Recompilar imagem nativa
cd backend/java-reactive
./mvnw clean -Pnative native:compile -DskipTests

# Reiniciar Cassandra
docker-compose -f docker-compose.aot-native.yml restart cassandra
```

### Problema: Performance baixa
```bash
# Verificar recursos do sistema
top
docker stats

# Verificar configuração do GraalVM
./target/beauty-salon-reactive --help
```

## 📚 Referências

- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [GraalVM Native Image Testing](https://www.graalvm.org/latest/reference-manual/native-image/guides/test-native-executable/)
- [WebTestClient Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/reactive/server/WebTestClient.html)

## 🏆 Resultados Esperados

Todos os testes devem passar com:
- ✅ 100% de sucesso nos testes de integração
- ✅ Performance dentro dos limites esperados
- ✅ Memória abaixo de 256MB
- ✅ Startup em menos de 2 segundos

Estes testes validam que a aplicação nativa mantém toda a funcionalidade da versão JVM com performance superior!
