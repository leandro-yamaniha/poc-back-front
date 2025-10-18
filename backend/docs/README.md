# Backend Documentation

Documentação específica dos backends do Beauty Salon Management System.

## 📚 Documentação Disponível

### 🏆 **Comparação e Performance**
- **[BACKEND_COMPARISON.md](BACKEND_COMPARISON.md)** - Comparação detalhada entre todos os backends
  - Arquitetura de cada backend
  - Métricas de performance
  - Casos de uso recomendados
  - Prós e contras

- **[REACTIVE_BACKEND_SUCCESS.md](REACTIVE_BACKEND_SUCCESS.md)** - Conquistas do backend reativo
  - 190/190 testes (100% sucesso)
  - Arquitetura reativa completa
  - Benchmarks de performance
  - Guia técnico detalhado

## 🚀 Backends Implementados

### 1. **Java Reactive** (Recomendado para Produção)
- **Localização**: `backend/java-reactive/`
- **Framework**: Spring Boot 3.5.4 + WebFlux
- **Performance**: 30,000+ RPS
- **Documentação**: [backend/java-reactive/README.md](../java-reactive/README.md)

### 2. **Java Tradicional**
- **Localização**: `backend/java/`
- **Framework**: Spring Boot 3.5.4
- **Performance**: Alta
- **Documentação**: [backend/java/README.md](../java/README.md)

### 3. **.NET** (Enterprise Ready)
- **Localização**: `backend/dotnet/`
- **Framework**: ASP.NET Core 8.0
- **Linguagem**: C# 12 (.NET 8.0 LTS)
- **Banco de Dados**: SQL Server + Entity Framework Core
- **Performance**: 6,000-10,000 RPS
- **Características**:
  - LINQ para queries type-safe
  - Dependency Injection nativo
  - Async/await pattern
  - Swagger/OpenAPI integrado
  - Integração Microsoft completa
- **Porta**: 8081
- **Status**: ✅ Enterprise Ready
- **Documentação**: [backend/dotnet/README.md](../dotnet/README.md)

### 4. **Go**
- **Localização**: `backend/go/`
- **Framework**: Gin
- **Performance**: Muito Alta
- **Documentação**: [backend/go/README.md](../go/README.md)

### 5. **Node.js**
- **Localização**: `backend/nodejs/`
- **Framework**: Express
- **Performance**: Boa
- **Documentação**: [backend/nodejs/README.md](../nodejs/README.md)

### 6. **Python**
- **Localização**: `backend/python/`
- **Framework**: FastAPI
- **Performance**: Boa
- **Documentação**: [backend/python/README.md](../python/README.md)

## 📊 Comparação Rápida

| Backend | Performance | Desenvolvimento | Produção | Escalabilidade |
|---------|-------------|-----------------|----------|----------------|
| Java Reactive | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| .NET Core | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Go | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Node.js | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| Python | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| Java | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

## 🎯 Guia de Escolha

### Para Produção de Alta Performance
→ **Java Reactive** ou **Go**

### Para Desenvolvimento Rápido
→ **Python (FastAPI)** ou **Node.js**

### Para Aplicações Enterprise
→ **Java Reactive** ou **.NET Core**

### Para Microserviços
→ **Go** ou **.NET Core**

## 🔗 Links Úteis

- **Visão Geral**: [../README.md](../README.md)
- **Documentação Geral**: [../../docs/README.md](../../docs/README.md)
- **Deployment**: [../../docs/DEPLOYMENT_GUIDE.md](../../docs/DEPLOYMENT_GUIDE.md)
- **Performance**: [../../docs/PERFORMANCE_TEST_RESULTS.md](../../docs/PERFORMANCE_TEST_RESULTS.md)

---

**📚 Documentação específica de backends organizada!**
