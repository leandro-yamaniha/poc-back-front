# 🧬 Plano Estratégico de Teste de Mutação - Meta 90%

## 📊 Situação Atual (Baseline)

### **Resumo Geral**
- **Cobertura de Mutação Atual**: 30% (68/228 mutações)
- **Meta**: 90% (≈205/228 mutações)
- **Gap a Cobrir**: +137 mutações detectadas
- **Força dos Testes**: 76% (excelente quando há cobertura)

### **Breakdown por Pacote**

| Pacote | Classes | Mutação Atual | Meta 90% | Gap | Prioridade |
|--------|---------|---------------|----------|-----|------------|
| **Controllers** | 5 | 0% (0/64) | 90% (58/64) | +58 | 🔴 ALTA |
| **Models** | 4 | 32% (12/38) | 90% (34/38) | +22 | 🟡 MÉDIA |
| **Services** | 5 | 44% (56/126) | 90% (113/126) | +57 | 🟠 ALTA |

### **Análise Detalhada por Classe**

#### **🔴 PRIORIDADE CRÍTICA - Controllers (0% → 90%)**
- **AppointmentController**: 0% → 90% (+13 mutações)
- **CustomerController**: 0% → 90% (+13 mutações)
- **MonitoringController**: 0% → 90% (+13 mutações)
- **ServiceController**: 0% → 90% (+12 mutações)
- **StaffController**: 0% → 90% (+12 mutações)

#### **🟠 PRIORIDADE ALTA - Services (44% → 90%)**
- **PerformanceMonitoringService**: 0% → 90% (+37 mutações) 🔴
- **AppointmentService**: 56% → 90% (+9 mutações)
- **ServiceService**: 57% → 90% (+7 mutações)
- **StaffService**: 57% → 90% (+7 mutações)
- **CustomerService**: 100% ✅ (mantido)

#### **🟡 PRIORIDADE MÉDIA - Models (32% → 90%)**
- **Appointment**: 18% → 90% (+8 mutações)
- **Service**: 20% → 90% (+7 mutações)
- **Staff**: 20% → 90% (+7 mutações)
- **Customer**: 86% → 90% (+1 mutação)

---

## 🎯 Estratégia de Implementação

### **FASE 1: Controllers (Semana 1) - +58 mutações**

#### **1.1 Configuração do PiTest para Controllers**
```xml
<targetTests>
    <param>com.beautysalon.controller.*Test</param>
    <param>com.beautysalon.mutation.*ControllerMutationTest</param>
</targetTests>
```

#### **1.2 Testes de Mutação para Controllers**
**Para cada Controller, implementar:**

1. **Testes de Endpoint HTTP**:
   - Status codes (200, 201, 404, 500)
   - Content-Type validation
   - Request/Response body validation

2. **Testes de Validação**:
   - Parâmetros inválidos
   - Dados malformados
   - Boundary conditions

3. **Testes de Exception Handling**:
   - Service layer exceptions
   - Validation errors
   - Runtime exceptions

4. **Testes de Security**:
   - Authentication scenarios
   - Authorization checks
   - Input sanitization

**Exemplo - AppointmentController**:
```java
@ExtendWith(MockitoExtension.class)
class AppointmentControllerMutationTest {
    
    @Test
    void createAppointment_InvalidData_ShouldReturn400() {
        // Test boundary conditions, null values, invalid formats
    }
    
    @Test
    void updateAppointment_NonExistentId_ShouldReturn404() {
        // Test edge cases for ID validation
    }
    
    @Test
    void deleteAppointment_ServiceException_ShouldReturn500() {
        // Test exception propagation
    }
}
```

### **FASE 2: PerformanceMonitoringService (Semana 2) - +37 mutações**

#### **2.1 Problema Identificado**
- **0% cobertura no PiTest** (mas 92% no JaCoCo)
- PiTest não está executando os testes corretos para esta classe

#### **2.2 Solução**
1. **Configurar targetTests específico**:
```xml
<targetTests>
    <param>com.beautysalon.service.PerformanceMonitoringServiceTest</param>
    <param>com.beautysalon.mutation.*PerformanceMonitoringMutationTest</param>
</targetTests>
```

2. **Criar testes focados em mutação**:
```java
@ExtendWith(MockitoExtension.class)
class PerformanceMonitoringServiceMutationTest {
    
    @Test
    void performHealthCheck_BoundaryConditions() {
        // Test cache hit rate thresholds (>=, >, <, <=)
        // Test memory usage boundaries (80%, 79%, 81%)
    }
    
    @Test
    void monitorCachePerformance_ConditionalLogic() {
        // Test all conditional branches
        // Test alert triggering conditions
    }
    
    @Test
    void calculateOverallCacheHitRate_MathMutations() {
        // Test arithmetic operations
        // Test division by zero scenarios
    }
}
```

### **FASE 3: Services Restantes (Semana 3) - +23 mutações**

#### **3.1 AppointmentService (56% → 90%)**
- Focar em conditional logic não coberto
- Testes de boundary conditions
- Exception handling scenarios

#### **3.2 ServiceService & StaffService (57% → 90%)**
- Testes similares para ambos (mesma estrutura)
- Validação de dados
- Operações CRUD edge cases

### **FASE 4: Models (Semana 4) - +22 mutações**

#### **4.1 Estratégia para Models**
1. **Testes de Equals/HashCode**:
```java
@Test
void equals_BoundaryConditions() {
    // Test null comparisons
    // Test same object reference
    // Test different class types
}
```

2. **Testes de Validation**:
```java
@Test
void validation_EdgeCases() {
    // Test field boundaries
    // Test null/empty values
    // Test format validation
}
```

3. **Testes de Builder/Constructor**:
```java
@Test
void builder_AllCombinations() {
    // Test all possible field combinations
    // Test default values
    // Test null handling
}
```

---

## 🛠️ Implementação Técnica

### **Configuração Maven Otimizada**

```xml
<profile>
    <id>mutation-tests-enhanced</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <configuration>
                    <targetClasses>
                        <param>com.beautysalon.controller.*</param>
                        <param>com.beautysalon.service.*</param>
                        <param>com.beautysalon.model.*</param>
                    </targetClasses>
                    <targetTests>
                        <param>com.beautysalon.**.*Test</param>
                        <param>com.beautysalon.mutation.*</param>
                    </targetTests>
                    <mutators>
                        <mutator>STRONGER</mutator>
                    </mutators>
                    <coverageThreshold>90</coverageThreshold>
                    <mutationThreshold>90</mutationThreshold>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

### **Ferramentas de Apoio**

1. **Script de Execução**:
```bash
#!/bin/bash
# run-mutation-tests.sh
echo "🧬 Executando Testes de Mutação..."
./mvnw clean compile test-compile
./mvnw org.pitest:pitest-maven:mutationCoverage -P mutation-tests-enhanced
echo "📊 Relatório disponível em: target/pit-reports/index.html"
```

2. **Monitoramento de Progresso**:
```bash
# Verificar progresso por fase
./mvnw pitest:mutationCoverage -DtargetClasses=com.beautysalon.controller.*
./mvnw pitest:mutationCoverage -DtargetClasses=com.beautysalon.service.*
./mvnw pitest:mutationCoverage -DtargetClasses=com.beautysalon.model.*
```

---

## 📈 Cronograma e Marcos

### **Semana 1: Controllers**
- **Meta**: 0% → 90% (+58 mutações)
- **Entregáveis**:
  - 5 classes *ControllerMutationTest
  - Configuração PiTest para controllers
  - Relatório intermediário

### **Semana 2: PerformanceMonitoringService**
- **Meta**: 0% → 90% (+37 mutações)
- **Entregáveis**:
  - PerformanceMonitoringServiceMutationTest
  - Correção configuração PiTest
  - Testes de boundary conditions

### **Semana 3: Services Restantes**
- **Meta**: 56-57% → 90% (+23 mutações)
- **Entregáveis**:
  - 3 classes *ServiceMutationTest
  - Testes de conditional logic
  - Exception handling coverage

### **Semana 4: Models**
- **Meta**: 32% → 90% (+22 mutações)
- **Entregáveis**:
  - 4 classes *ModelMutationTest
  - Testes de equals/hashCode
  - Validation edge cases

### **Semana 5: Refinamento**
- **Meta**: Atingir 90% total
- **Atividades**:
  - Análise de mutações sobreviventes
  - Refinamento de testes
  - Documentação final

---

## 🎯 Métricas de Sucesso

### **KPIs por Fase**
- **Fase 1**: Controllers 90% (58/64 mutações)
- **Fase 2**: PerformanceMonitoringService 90% (37/41 mutações)
- **Fase 3**: Services 90% (113/126 mutações)
- **Fase 4**: Models 90% (34/38 mutações)

### **Meta Final**
- **Cobertura de Mutação**: ≥90% (≥205/228 mutações)
- **Test Strength**: Manter ≥76%
- **Tempo de Execução**: <15 minutos
- **Qualidade**: Zero mutações equivalentes

---

## 🚀 Próximos Passos

1. **Imediato**: Implementar Fase 1 (Controllers)
2. **Configurar**: Profile Maven otimizado
3. **Executar**: Baseline measurement
4. **Monitorar**: Progresso semanal
5. **Ajustar**: Estratégia conforme necessário

---

**📊 Estimativa Total**: 4-5 semanas para atingir 90% de cobertura de mutação
**🎯 ROI**: Qualidade de código significativamente superior com detecção de bugs em produção
