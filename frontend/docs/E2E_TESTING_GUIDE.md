# Guia de Testes End-to-End (E2E) - Beauty Salon App

## 📋 Visão Geral

Este guia documenta a implementação completa de testes E2E usando Cypress para a aplicação Beauty Salon, cobrindo fluxos críticos, integração frontend-backend e validação de funcionalidades.

## 🏗️ Estrutura dos Testes

### Arquivos de Teste

```
cypress/
├── e2e/
│   ├── homepage.cy.js           # Testes da página inicial/dashboard
│   ├── navigation.cy.js         # Testes de navegação entre páginas
│   ├── dashboard.cy.js          # Testes específicos do dashboard
│   ├── customers.cy.js          # Testes de gerenciamento de clientes
│   ├── services.cy.js           # Testes de gerenciamento de serviços
│   ├── staff.cy.js              # Testes de gerenciamento de funcionários
│   ├── appointments.cy.js       # Testes de gerenciamento de agendamentos
│   ├── integration.cy.js        # Testes de integração completa
│   └── advanced-flows.cy.js     # Testes avançados e cenários complexos
├── fixtures/
│   ├── customers.json           # Dados mock de clientes
│   ├── services.json            # Dados mock de serviços
│   ├── staff.json               # Dados mock de funcionários
│   ├── appointments.json        # Dados mock de agendamentos
│   └── todayAppointments.json   # Dados mock de agendamentos do dia
├── support/
│   ├── commands.js              # Comandos customizados do Cypress
│   └── e2e.js                   # Configurações globais
└── scripts/
    └── run-e2e-tests.sh         # Script automatizado para execução
```

## 🚀 Execução dos Testes

### Métodos de Execução

#### 1. Script Automatizado (Recomendado)
```bash
# Execução padrão (headless)
./scripts/run-e2e-tests.sh

# Execução com interface gráfica
./scripts/run-e2e-tests.sh -m headed

# Execução interativa (Cypress Test Runner)
./scripts/run-e2e-tests.sh -m interactive

# Teste específico
./scripts/run-e2e-tests.sh -s homepage.cy.js

# Manter serviços rodando após testes
./scripts/run-e2e-tests.sh -k

# Assumir que serviços já estão rodando
./scripts/run-e2e-tests.sh -n
```

#### 2. Comandos Cypress Diretos
```bash
# Modo headless
npx cypress run

# Modo interativo
npx cypress open

# Teste específico
npx cypress run --spec "cypress/e2e/homepage.cy.js"
```

### Pré-requisitos

1. **Backend rodando**: `http://localhost:8080`
2. **Frontend rodando**: `http://localhost:3000`
3. **Dependências instaladas**: `npm install`

## 🧪 Tipos de Testes Implementados

### 1. Testes de Navegação
- **Arquivo**: `navigation.cy.js`
- **Cobertura**: Navegação entre todas as páginas principais
- **Validações**: URLs corretas, elementos visíveis, botões funcionais

### 2. Testes de Dashboard
- **Arquivo**: `dashboard.cy.js`
- **Cobertura**: Carregamento de estatísticas, elementos da UI
- **Validações**: Dados exibidos corretamente, responsividade

### 3. Testes de CRUD
- **Arquivos**: `customers.cy.js`, `services.cy.js`, `staff.cy.js`, `appointments.cy.js`
- **Cobertura**: Criação, leitura, atualização e exclusão
- **Validações**: Formulários, modais, validações, filtros

### 4. Testes de Integração
- **Arquivo**: `integration.cy.js`
- **Cobertura**: Fluxos completos de negócio
- **Validações**: Integração frontend-backend, interceptação de APIs

### 5. Testes Avançados
- **Arquivo**: `advanced-flows.cy.js`
- **Cobertura**: Cenários complexos, tratamento de erros, performance
- **Validações**: Acessibilidade, responsividade, recuperação de falhas

## 🛠️ Comandos Customizados

### Comandos de Setup
```javascript
cy.setupApiMocks()           // Configura mocks para todas as APIs
cy.waitForDashboard()        // Aguarda carregamento completo do dashboard
cy.navigateAndWait(path)     // Navega e aguarda carregamento
```

### Comandos de Criação
```javascript
cy.createCustomer(data)      // Cria cliente através da UI
cy.createService(data)       // Cria serviço através da UI
cy.createAppointment(data)   // Cria agendamento através da UI
```

### Comandos de Validação
```javascript
cy.checkToast(message)       // Verifica notificações toast
cy.checkA11y()              // Verifica acessibilidade básica
cy.isInViewport(element)     // Verifica se elemento está visível
```

### Comandos de Simulação
```javascript
cy.simulateSlowNetwork()     // Simula rede lenta
cy.simulateApiError(endpoint) // Simula erro de API
cy.testResponsive(callback)  // Testa diferentes viewports
```

### Comandos de Formulário
```javascript
cy.fillForm(data)           // Preenche formulário por data-testid/name
cy.waitForLoading()         // Aguarda fim do carregamento
```

## 📊 Cobertura de Testes

### Funcionalidades Testadas

#### ✅ Dashboard
- [x] Carregamento de estatísticas
- [x] Exibição de números corretos
- [x] Botões de ação rápida
- [x] Responsividade
- [x] Estados de loading/erro

#### ✅ Gerenciamento de Clientes
- [x] Listagem de clientes
- [x] Criação de novo cliente
- [x] Validação de formulário
- [x] Busca/filtro
- [x] Modal de edição
- [x] Responsividade

#### ✅ Gerenciamento de Serviços
- [x] Listagem de serviços
- [x] Criação de novo serviço
- [x] Filtro por categoria
- [x] Validação de preço/duração
- [x] Modal de edição

#### ✅ Gerenciamento de Funcionários
- [x] Listagem de funcionários
- [x] Criação de novo funcionário
- [x] Filtro por função
- [x] Status ativo/inativo
- [x] Modal de edição

#### ✅ Gerenciamento de Agendamentos
- [x] Listagem de agendamentos
- [x] Criação de novo agendamento
- [x] Seleção de cliente/serviço/funcionário
- [x] Validação de data/hora
- [x] Filtros diversos

#### ✅ Integração e Fluxos Complexos
- [x] Jornada completa do cliente
- [x] Workflow de agendamento
- [x] Tratamento de erros de API
- [x] Recuperação de falhas
- [x] Validação de dados
- [x] Prevenção de duplicatas

#### ✅ Performance e UX
- [x] Tempos de carregamento
- [x] Interações concorrentes
- [x] Manutenção de estado
- [x] Navegação por teclado
- [x] Acessibilidade básica

## 🔧 Configuração Avançada

### Cypress Config (`cypress.config.js`)
```javascript
{
  viewportWidth: 1280,
  viewportHeight: 720,
  defaultCommandTimeout: 10000,
  retries: { runMode: 2, openMode: 0 },
  video: true,
  screenshotOnRunFailure: true
}
```

### Variáveis de Ambiente
```javascript
env: {
  apiUrl: 'http://localhost:8080/api'
}
```

### Fixtures de Dados
- Dados realistas para testes
- Estrutura consistente com API
- IDs únicos para evitar conflitos
- Relacionamentos entre entidades

## 🐛 Tratamento de Erros

### Cenários Testados
1. **Falhas de API**: Status 500, timeout, rede indisponível
2. **Validação de Formulários**: Campos obrigatórios, formatos inválidos
3. **Duplicação de Dados**: Email já cadastrado, conflitos
4. **Estados de Loading**: Indicadores visuais, timeouts
5. **Recuperação**: Retry automático, mensagens de erro

### Estratégias de Mock
- Interceptação de requisições HTTP
- Simulação de diferentes cenários
- Controle de timing e delays
- Dados consistentes e previsíveis

## 📈 Métricas e Relatórios

### Métricas Coletadas
- **Tempo de Execução**: Por teste e suíte completa
- **Taxa de Sucesso**: Percentual de testes passando
- **Performance**: Tempos de carregamento de página
- **Cobertura**: Funcionalidades testadas vs. implementadas

### Relatórios Gerados
- **Videos**: Gravação de execução dos testes
- **Screenshots**: Capturas em caso de falha
- **Logs**: Detalhes de execução e erros
- **Métricas**: Tempos e estatísticas de performance

## 🚦 CI/CD Integration

### Configuração para Pipeline
```bash
# Modo CI (sem vídeos, com screenshots)
CYPRESS_environment=ci npx cypress run

# Com relatórios JUnit
npx cypress run --reporter junit --reporter-options mochaFile=results/test-results.xml
```

### Variáveis de Ambiente CI
```bash
CYPRESS_baseUrl=http://localhost:3000
CYPRESS_apiUrl=http://localhost:8080/api
CYPRESS_environment=ci
```

## 🎯 Próximos Passos

### Melhorias Planejadas
1. **Testes de Acessibilidade**: Integração com axe-core
2. **Testes de Performance**: Lighthouse CI
3. **Testes Visuais**: Comparação de screenshots
4. **Testes de API**: Validação direta de endpoints
5. **Testes de Segurança**: XSS, CSRF, autenticação

### Expansão de Cobertura
1. **Fluxos de Erro**: Mais cenários de falha
2. **Dispositivos Móveis**: Testes específicos mobile
3. **Browsers**: Testes cross-browser
4. **Dados**: Volumes maiores, edge cases
5. **Integração**: Testes com banco real

## 📚 Recursos e Referências

- [Cypress Documentation](https://docs.cypress.io/)
- [Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Custom Commands](https://docs.cypress.io/api/cypress-api/custom-commands)
- [API Testing](https://docs.cypress.io/guides/guides/network-requests)

---

## 🎉 Conclusão

A implementação de testes E2E está completa e cobre todos os fluxos críticos da aplicação Beauty Salon. Os testes garantem:

- ✅ **Funcionalidade**: Todas as features principais testadas
- ✅ **Integração**: Frontend e backend funcionando em conjunto
- ✅ **Robustez**: Tratamento adequado de erros e edge cases
- ✅ **Performance**: Validação de tempos de resposta
- ✅ **Acessibilidade**: Verificações básicas de usabilidade
- ✅ **Manutenibilidade**: Código de teste bem estruturado e documentado

Execute `./scripts/run-e2e-tests.sh -h` para ver todas as opções disponíveis.
