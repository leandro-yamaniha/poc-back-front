# 🚀 Beauty Salon Reactive Backend

Backend reativo construído com **Spring Boot 3.5.4**, **Java 21**, **WebFlux** e **Undertow** para o sistema de gerenciamento de salão de beleza.

## 🏗️ Arquitetura Reativa

### **Stack Tecnológica**
- **Spring Boot 3.5.4** - Framework principal
- **Java 21 LTS** - Linguagem com recursos modernos
- **Spring WebFlux** - Programação reativa não-bloqueante
- **Undertow** - Servidor web embarcado de alta performance
- **Spring Data Cassandra Reactive** - Acesso reativo ao banco
- **Project Reactor** - Biblioteca de programação reativa
- **Maven** - Gerenciamento de dependências

### **Características Reativas**
- ⚡ **Non-blocking I/O** - Operações assíncronas
- 🔄 **Backpressure** - Controle de fluxo automático
- 📈 **High Throughput** - Maior capacidade de requisições
- 💾 **Memory Efficient** - Menor consumo de memória
- 🧵 **Thread Efficient** - Melhor utilização de threads

## 📁 Estrutura do Projeto

```
backend-java-reactive/
├── src/main/java/com/beautysalon/reactive/
│   ├── BeautySalonReactiveApplication.java  # Main class
│   ├── config/
│   │   ├── CassandraConfig.java            # Configuração Cassandra
│   │   └── WebFluxConfig.java              # Configuração WebFlux/CORS
│   ├── model/                              # Modelos de dados (Records)
│   │   ├── Customer.java
│   │   ├── Service.java
│   │   ├── Staff.java
│   │   └── Appointment.java
│   ├── repository/                         # Repositórios reativos
│   │   ├── CustomerRepository.java
│   │   ├── ServiceRepository.java
│   │   ├── StaffRepository.java
│   │   └── AppointmentRepository.java
│   ├── service/                           # Serviços de negócio
│   │   ├── CustomerService.java
│   │   ├── ServiceService.java
│   │   ├── StaffService.java
│   │   └── AppointmentService.java
│   ├── controller/                        # Controllers reativos
│   │   ├── CustomerController.java
│   │   ├── ServiceController.java
│   │   ├── StaffController.java
│   │   └── AppointmentController.java
│   └── exception/
│       └── GlobalExceptionHandler.java    # Tratamento global de erros
├── src/main/resources/
│   └── application.yml                    # Configurações
├── src/test/java/                         # Testes reativos
└── pom.xml                               # Dependências Maven
```

## 🔧 Configuração

### **Variáveis de Ambiente**
```yaml
server:
  port: 8085                              # Porta do servidor
  undertow:
    threads:
      io: 16                              # Threads I/O
      worker: 256                         # Worker threads

spring:
  cassandra:
    keyspace-name: beauty_salon
    contact-points: localhost
    port: 9042
    local-datacenter: datacenter1
```

### **Recursos Implementados**
- 🔄 **Reactive Streams** - Mono/Flux para operações assíncronas
- 📝 **CRUD Completo** - Todas as entidades com operações reativas
- 🔍 **Busca Avançada** - Filtros e consultas otimizadas
- ✅ **Validação** - Bean Validation com tratamento reativo
- 🌐 **CORS** - Configuração para frontend
- 📊 **Health Checks** - Monitoramento de saúde
- 🧪 **Testes** - Testes unitários com WebFlux Test

## 🚀 Execução

### **Desenvolvimento Local**
```bash
cd backend-java-reactive
./mvnw spring-boot:run
```

### **Docker**
```bash
# Executar com Cassandra
docker-compose up -d cassandra backend-java-reactive
```

### **Testes**
```bash
# Testes unitários
./mvnw test

# Testes com cobertura
./mvnw test jacoco:report
```

## 📊 APIs Reativas

### **Endpoints Implementados**

#### **Customers** (`/api/customers`)
- `GET /` - Listar todos os clientes
- `GET /{id}` - Buscar cliente por ID
- `POST /` - Criar novo cliente
- `PUT /{id}` - Atualizar cliente
- `DELETE /{id}` - Deletar cliente
- `GET /search?name={name}` - Buscar por nome
- `GET /email/{email}` - Buscar por email

#### **Services** (`/api/services`)
- `GET /` - Listar todos os serviços
- `GET /active` - Serviços ativos
- `GET /{id}` - Buscar serviço por ID
- `POST /` - Criar novo serviço
- `PUT /{id}` - Atualizar serviço
- `DELETE /{id}` - Deletar serviço
- `GET /category/{category}` - Por categoria
- `GET /category/{category}/active` - Ativos por categoria

#### **Staff** (`/api/staff`)
- `GET /` - Listar funcionários
- `GET /active` - Funcionários ativos
- `GET /{id}` - Buscar por ID
- `POST /` - Criar funcionário
- `PUT /{id}` - Atualizar funcionário
- `DELETE /{id}` - Deletar funcionário
- `GET /role/{role}` - Por função
- `GET /role/{role}/active` - Ativos por função

#### **Appointments** (`/api/appointments`)
- `GET /` - Listar agendamentos
- `GET /{id}` - Buscar por ID
- `POST /` - Criar agendamento
- `PUT /{id}` - Atualizar agendamento
- `DELETE /{id}` - Deletar agendamento
- `GET /today` - Agendamentos de hoje
- `GET /date/{date}` - Por data
- `GET /date/{date}/staff/{staffId}` - Por data e funcionário

## 🧪 Testes Reativos

### **Ferramentas de Teste**
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking para testes unitários
- **WebFluxTest** - Testes de controllers reativos
- **StepVerifier** - Verificação de fluxos reativos
- **TestContainers** - Testes de integração

### **Exemplos de Teste**
```java
@Test
void getAllCustomers_ShouldReturnAllCustomers() {
    when(customerRepository.findAllByOrderByCreatedAtDesc())
        .thenReturn(Flux.just(testCustomer));

    StepVerifier.create(customerService.getAllCustomers())
        .expectNext(testCustomer)
        .verifyComplete();
}
```

## 🔄 Programação Reativa

### **Mono vs Flux**
- **Mono<T>** - 0 ou 1 elemento (operações únicas)
- **Flux<T>** - 0 a N elementos (operações de coleção)

### **Operadores Reativos**
- `map()` - Transformação
- `flatMap()` - Transformação assíncrona
- `filter()` - Filtragem
- `defaultIfEmpty()` - Valor padrão
- `switchIfEmpty()` - Alternativa reativa

### **Backpressure**
- Controle automático de fluxo
- Evita sobrecarga de memória
- Balanceamento entre produtor/consumidor

## 🚀 Performance

### **Vantagens do Modelo Reativo**
- **Escalabilidade** - Milhares de conexões concorrentes
- **Eficiência** - Menor uso de threads e memória
- **Responsividade** - Operações não-bloqueantes
- **Resiliência** - Tratamento de falhas reativo

### **Undertow Server**
- **Alto desempenho** - Servidor web otimizado
- **Baixo overhead** - Consumo mínimo de recursos
- **NIO.2** - I/O não-bloqueante nativo

## 📈 Monitoramento

### **Actuator Endpoints**
- `/actuator/health` - Status da aplicação
- `/actuator/metrics` - Métricas de performance
- `/actuator/info` - Informações da aplicação

### **Logs Estruturados**
- Logs detalhados para debugging
- Rastreamento de operações reativas
- Monitoramento de performance

---

**Backend Java Reactive** - Implementação de alta performance com programação reativa para o Beauty Salon Management System.
