# 🤖 Beauty Salon App - AI Generative Rules

> **PROPÓSITO:** Este arquivo define regras estruturadas para facilitar a interpretação e automação por IA generativa.

---

## 📋 **ÍNDICE**

1. [Estrutura do Projeto](#estrutura-do-projeto)
2. [Regras de Código](#regras-de-código)
3. [Regras de Scripts](#regras-de-scripts)
4. [Regras de Documentação](#regras-de-documentação)
5. [Regras de Git](#regras-de-git)
6. [Regras de Testes](#regras-de-testes)
7. [Queries Comuns](#queries-comuns)

---

## 🏗️ **ESTRUTURA DO PROJETO**

### **RULE-001: Organização de Diretórios**

```yaml
REGRA: Estrutura de diretórios DEVE seguir este padrão
ESTRUTURA:
  beauty-salon-app/
  ├── frontend/              # React application
  │   ├── src/
  │   ├── public/
  │   └── package.json
  ├── backend/               # Multiple backend implementations
  │   ├── java/              # Spring Boot tradicional
  │   ├── java-reactive/     # Spring Boot WebFlux
  │   ├── nodejs/            # Express.js
  │   ├── python/            # FastAPI
  │   ├── go/                # Gin framework
  │   ├── dotnet/            # ASP.NET Core
  │   ├── scripts/           # Automation scripts
  │   ├── docs/              # Documentation
  │   └── benchmarks/        # Performance results
  └── .windsurf/             # AI rules and configs

VALIDAÇÃO: tree -L 2 -d
```

### **RULE-002: Localização de Arquivos**

```yaml
TIPO: Documentação
LOCALIZAÇÃO: backend/docs/
EXEMPLOS:
  - backend/docs/GRAALVM_NATIVE_COMPILATION_GUIDE.md
  - backend/docs/BUILD_GUIDE.md

TIPO: Scripts
LOCALIZAÇÃO: backend/scripts/
FORMATO: ação-escopo.sh
EXEMPLOS:
  - backend/scripts/build-all.sh
  - backend/scripts/stress-test-all-backends.sh

TIPO: Resultados de Testes
LOCALIZAÇÃO: backend/[tipo]-results/YYYYMMDD_HHMMSS/
EXEMPLOS:
  - backend/stress-test-results/20251112_223846/
  - backend/benchmarks/results/20251112_223846/
REGRA: DEVEM estar no .gitignore
```

---

## 💻 **REGRAS DE CÓDIGO**

### **RULE-101: Nomenclatura de Arquivos**

```yaml
BACKEND:
  Java:
    Classes: PascalCase (CustomerController.java)
    Packages: lowercase (com.beautysalon.reactive)
  JavaScript/TypeScript:
    Componentes: PascalCase (CustomerForm.jsx)
    Utilitários: camelCase (apiService.js)
  Python:
    Módulos: snake_case (customer_service.py)
    Classes: PascalCase (CustomerService)
  Go:
    Arquivos: snake_case (customer_handler.go)
    Tipos: PascalCase (CustomerService)

SCRIPTS:
  Formato: kebab-case (build-all.sh)
  Padrão: ação-escopo.sh
```

### **RULE-102: Padrões de Código**

```yaml
JAVA:
  Spring Boot Version: 3.5.4
  Java Version: 21 LTS
  Build Tool: Maven
  Reactive: WebFlux + Project Reactor
  Database: Spring Data Cassandra Reactive

JAVASCRIPT:
  React Version: 18.x
  State Management: Context API
  Styling: Bootstrap + Custom CSS
  Build Tool: Create React App

PYTHON:
  Framework: FastAPI
  Async: asyncio + uvicorn
  Database: cassandra-driver

GO:
  Framework: Gin
  Version: 1.21+
  Database: gocql

DOTNET:
  Framework: ASP.NET Core 8.0
  Language: C# 12
```

### **RULE-103: Estrutura de Classes**

```yaml
BACKEND_STRUCTURE:
  Controllers: Endpoints REST
  Services: Lógica de negócio
  Repositories: Acesso a dados
  Models: Entidades de domínio
  DTOs: Objetos de transferência
  Exceptions: Tratamento de erros

ORDEM_NO_ARQUIVO:
  1. Imports/Packages
  2. Class declaration
  3. Constants
  4. Fields/Properties
  5. Constructors
  6. Public methods
  7. Private methods
```

---

## 🔧 **REGRAS DE SCRIPTS**

### **RULE-201: Nomenclatura de Scripts**

```yaml
FORMATO: ação-escopo.sh
PADRÃO: [verbo]-[substantivo]-[modificador?].sh

VERBOS_VÁLIDOS:
  - build (compilação)
  - test (testes)
  - stress-test (performance)
  - benchmark (comparação)
  - start (inicialização)
  - stop (parada)
  - clean (limpeza)
  - deploy (implantação)
  - analyze (análise)

EXEMPLOS_VÁLIDOS:
  ✅ build-all.sh
  ✅ test-backends-quick.sh
  ✅ stress-test-all-backends.sh
  ✅ benchmark-native-comparison.sh
  ✅ start-all-compose.sh

EXEMPLOS_INVÁLIDOS:
  ❌ buildAll.sh (camelCase)
  ❌ test_backends.sh (underscore)
  ❌ stress-test.bash (extensão errada)
  ❌ script.sh (nome genérico)
```

### **RULE-202: Localização de Scripts**

```yaml
REGRA: Todos os scripts DEVEM estar em backend/scripts/
EXCEÇÃO: Nenhuma
VALIDAÇÃO: ls backend/scripts/*.sh
PERMISSÃO: chmod +x backend/scripts/*.sh
```

### **RULE-203: Documentação de Scripts**

```yaml
REGRA: Todo script DEVE estar documentado em backend/scripts/README.md

TEMPLATE:
  ### `nome-do-script.sh`
  
  **Propósito**: Descrição clara em uma linha
  **Tempo**: ~X minutos
  **Uso**: `./scripts/nome-do-script.sh [argumentos]`
  
  #### Funcionalidades:
  - Item 1
  - Item 2
  
  #### Exemplo de uso:
  ```bash
  ./scripts/nome-do-script.sh argumento
  ```
```

---

## 📚 **REGRAS DE DOCUMENTAÇÃO**

### **RULE-301: Localização de Documentação**

```yaml
DOCUMENTAÇÃO_GERAL: backend/docs/
DOCUMENTAÇÃO_ESPECÍFICA: backend/[linguagem]/[arquivo].md

EXEMPLOS:
  - backend/docs/GRAALVM_NATIVE_COMPILATION_GUIDE.md
  - backend/java-reactive/BUILD_GUIDE.md
  - backend/scripts/README.md
```

### **RULE-302: Formato de Documentação**

```yaml
FORMATO: Markdown (.md)
ENCODING: UTF-8
LINHA_MAX: 120 caracteres (recomendado)

ESTRUTURA_OBRIGATÓRIA:
  1. Título principal (# H1)
  2. Descrição breve
  3. Índice (para docs > 100 linhas)
  4. Seções com H2 (##)
  5. Exemplos de código com syntax highlighting
  6. Referências (se aplicável)

SYNTAX_HIGHLIGHTING:
  ```bash
  # Comandos shell
  ```
  
  ```java
  // Código Java
  ```
  
  ```yaml
  # Configurações YAML
  ```
```

### **RULE-303: Documentação de Problemas e Soluções**

```yaml
FORMATO_PROBLEMA:
  ## Problema: [Título do Problema]
  
  **Erro:**
  ```
  Mensagem de erro exata
  ```
  
  **Causa:**
  Explicação da causa raiz
  
  **Solução:**
  1. Passo 1
  2. Passo 2
  
  **Validação:**
  Como verificar se foi resolvido
  
  **Status:** ✅ Resolvido / ⚠️ Workaround / ❌ Não resolvido
```

---

## 🔀 **REGRAS DE GIT**

### **RULE-401: Formato de Commits**

```yaml
FORMATO: tipo(escopo): descrição

TIPOS_VÁLIDOS:
  - feat: Nova funcionalidade
  - fix: Correção de bug
  - docs: Documentação
  - style: Formatação
  - refactor: Refatoração
  - test: Testes
  - chore: Tarefas de manutenção
  - perf: Performance

EXEMPLOS:
  ✅ feat(backend): add native compilation support
  ✅ fix(frontend): resolve loading state issue
  ✅ docs(scripts): update README with stress test guide
  ✅ perf(java-reactive): optimize database queries

REGRAS:
  - Usar presente do indicativo
  - Primeira letra minúscula
  - Sem ponto final
  - Máximo 72 caracteres no título
```

### **RULE-402: Branches**

```yaml
PADRÃO: tipo/descrição-curta

TIPOS:
  - feature/nome-feature
  - fix/nome-bug
  - docs/nome-doc
  - refactor/nome-refactor

EXEMPLOS:
  ✅ feature/native-compilation
  ✅ fix/cassandra-connection
  ✅ docs/stress-test-guide
```

### **RULE-403: .gitignore**

```yaml
REGRA: Resultados de testes e builds DEVEM estar no .gitignore

PADRÕES_OBRIGATÓRIOS:
  # Build artifacts
  target/
  build/
  dist/
  node_modules/
  
  # Test results
  stress-test-results/
  benchmarks/results/
  build-logs-*/
  
  # IDE
  .idea/
  .vscode/
  *.iml
  
  # OS
  .DS_Store
  Thumbs.db
```

---

## 🧪 **REGRAS DE TESTES**

### **RULE-501: Nomenclatura de Testes**

```yaml
JAVA:
  Formato: ClasseTesteTest.java
  Exemplo: CustomerServiceTest.java
  Métodos: should_[ação]_when_[condição]

JAVASCRIPT:
  Formato: componente.test.js
  Exemplo: CustomerForm.test.js
  Describes: describe('ComponentName', ...)

PYTHON:
  Formato: test_modulo.py
  Exemplo: test_customer_service.py
  Funções: test_[ação]_[condição]
```

### **RULE-502: Estrutura de Testes**

```yaml
PADRÃO: AAA (Arrange, Act, Assert)

EXEMPLO:
  @Test
  void should_create_customer_when_valid_data() {
      // Arrange
      Customer customer = new Customer(...);
      
      // Act
      Customer result = service.create(customer);
      
      // Assert
      assertNotNull(result.getId());
  }
```

### **RULE-503: Cobertura de Testes**

```yaml
META_MÍNIMA: 80%
META_IDEAL: 90%+

PRIORIDADE:
  1. Services (lógica de negócio) - 90%+
  2. Controllers (endpoints) - 85%+
  3. Repositories (dados) - 80%+
  4. Models (validações) - 75%+
```

---

## 🔍 **QUERIES COMUNS PARA IA**

### **Q1: Como fazer build?**

```yaml
PERGUNTA: "Como fazer build de todos os backends?"
RESPOSTA: |
  Rápido (paralelo): ./backend/scripts/build-all-parallel.sh
  Sequencial: ./backend/scripts/build-all.sh
  Específico: ./backend/scripts/build-specific.sh [backend]
```

### **Q2: Como testar performance?**

```yaml
PERGUNTA: "Como testar performance dos backends?"
RESPOSTA: |
  Todos: ./backend/scripts/stress-test-all-backends.sh all
  Individual: ./backend/scripts/stress-test-individual.sh [backend]
  Comparação JVM vs Native: ./backend/scripts/benchmark-native-comparison.sh
```

### **Q3: Como iniciar backends?**

```yaml
PERGUNTA: "Como iniciar os backends?"
RESPOSTA: |
  Todos: ./backend/scripts/start-all-compose.sh
  Específico: cd backend/[linguagem] && docker-compose up -d
```

### **Q4: Como limpar artefatos?**

```yaml
PERGUNTA: "Como limpar artefatos de build?"
RESPOSTA: ./backend/scripts/clean-all.sh
```

### **Q5: Onde está a documentação?**

```yaml
PERGUNTA: "Onde encontro documentação sobre [tópico]?"
RESPOSTAS:
  Native Compilation: backend/docs/GRAALVM_NATIVE_COMPILATION_GUIDE.md
  Scripts: backend/scripts/README.md
  Build: backend/[linguagem]/BUILD_GUIDE.md
  Docker: backend/DOCKER_COMPOSE_GUIDE.md
```

---

## 🎯 **FLUXOS DE TRABALHO**

### **FLOW-001: Criar Novo Backend**

```yaml
QUANDO: Adicionar novo backend em nova linguagem
PASSOS:
  1. Criar diretório: backend/[linguagem]/
  2. Estrutura padrão:
     - src/ (código fonte)
     - Dockerfile
     - docker-compose.yml
     - README.md
     - .gitignore
  3. Implementar endpoints:
     - GET /health
     - GET /api/customers
     - POST /api/customers
     - PUT /api/customers/{id}
     - DELETE /api/customers/{id}
  4. Adicionar ao docker-compose principal
  5. Criar script de build em backend/scripts/
  6. Documentar em backend/README.md
  7. Adicionar testes
```

### **FLOW-002: Criar Novo Script**

```yaml
QUANDO: Adicionar novo script de automação
PASSOS:
  1. Criar: backend/scripts/ação-escopo.sh
  2. Adicionar header com comentários
  3. Implementar funcionalidade
  4. Adicionar permissão: chmod +x
  5. Documentar em backend/scripts/README.md
  6. Testar em ambiente limpo
  7. Commit: git add + commit
```

### **FLOW-003: Adicionar Nova Documentação**

```yaml
QUANDO: Criar nova documentação
PASSOS:
  1. Determinar tipo:
     - Geral → backend/docs/
     - Específica → backend/[linguagem]/
  2. Criar arquivo .md
  3. Seguir template (RULE-302)
  4. Adicionar ao índice principal
  5. Validar links
  6. Commit
```

### **FLOW-004: Resolver Problema**

```yaml
QUANDO: Encontrar e resolver problema
PASSOS:
  1. Reproduzir problema
  2. Identificar causa raiz
  3. Implementar solução
  4. Testar solução
  5. Documentar em [LINGUAGEM]_TROUBLESHOOTING.md:
     - Problema
     - Erro
     - Causa
     - Solução
     - Validação
  6. Commit com fix(escopo): descrição
```

---

## ⚠️ **VALIDAÇÕES AUTOMÁTICAS**

### **VAL-001: Validar Estrutura de Scripts**

```bash
# Verificar nomenclatura
find backend/scripts -name "*.sh" | grep -Ev "^backend/scripts/[a-z-]+\.sh$" && \
  echo "❌ Nomenclatura inválida" || echo "✅ Nomenclatura OK"

# Verificar permissões
find backend/scripts -name "*.sh" ! -perm -u+x && \
  echo "❌ Scripts sem permissão de execução" || echo "✅ Permissões OK"

# Verificar documentação
for script in backend/scripts/*.sh; do
  grep -q "$(basename $script)" backend/scripts/README.md || \
    echo "❌ $script não documentado"
done
```

### **VAL-002: Validar Commits**

```bash
# Verificar formato de commit
git log -1 --pretty=%B | grep -E "^(feat|fix|docs|style|refactor|test|chore|perf)\(.+\): .+" || \
  echo "❌ Formato de commit inválido"
```

### **VAL-003: Validar Documentação**

```bash
# Verificar links quebrados em Markdown
find backend/docs -name "*.md" -exec markdown-link-check {} \;

# Verificar syntax highlighting
grep -r '```$' backend/docs/ && echo "❌ Code blocks sem linguagem especificada"
```

---

## 🤖 **INSTRUÇÕES PARA IA**

### **AI-001: Ao Criar Código**

```yaml
SEMPRE:
  - Seguir padrões da linguagem (RULE-102)
  - Usar nomenclatura correta (RULE-101)
  - Adicionar comentários explicativos
  - Incluir tratamento de erros
  - Escrever testes

NUNCA:
  - Hardcoded credentials
  - Código duplicado
  - Magic numbers sem constantes
  - Logs de informações sensíveis
```

### **AI-002: Ao Criar Scripts**

```yaml
SEMPRE:
  - Usar nomenclatura kebab-case (RULE-201)
  - Colocar em backend/scripts/ (RULE-202)
  - Documentar no README (RULE-203)
  - Adicionar permissão de execução
  - Incluir header com descrição
  - Usar cores para output (RED, GREEN, YELLOW, BLUE)
  - Adicionar validações

TEMPLATE:
  #!/bin/bash
  # Script: nome-do-script.sh
  # Propósito: Descrição clara
  # Uso: ./scripts/nome-do-script.sh [args]
  
  set -e  # Exit on error
  
  # Cores
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  NC='\033[0m'
  
  # Funções
  print_success() { echo -e "${GREEN}✅ $1${NC}"; }
  print_error() { echo -e "${RED}❌ $1${NC}"; }
  
  # Main
  main() {
    # Implementação
  }
  
  main "$@"
```

### **AI-003: Ao Criar Documentação**

```yaml
SEMPRE:
  - Usar Markdown (RULE-302)
  - Incluir índice se > 100 linhas
  - Adicionar exemplos de código
  - Usar syntax highlighting
  - Incluir seção de troubleshooting
  - Adicionar links para docs relacionadas

ESTRUTURA:
  # Título Principal
  
  Descrição breve do documento.
  
  ## Índice
  
  ## Seção 1
  
  ### Subseção 1.1
  
  ## Exemplos
  
  ## Troubleshooting
  
  ## Referências
```

### **AI-004: Ao Resolver Problemas**

```yaml
PROCESSO:
  1. Ler documentação existente
  2. Verificar TROUBLESHOOTING.md
  3. Reproduzir problema
  4. Identificar causa raiz
  5. Implementar solução
  6. Testar solução
  7. Documentar (RULE-303)
  8. Commit com formato correto (RULE-401)
```

---

## 📊 **MÉTRICAS E BENCHMARKS**

### **BENCH-001: Performance Esperada**

```yaml
JAVA_REACTIVE_NATIVE:
  Startup: < 1 segundo
  Memória: 50-80 MB
  Throughput: 10,000+ req/s
  Latency P50: < 10ms

JAVA_REACTIVE_JVM:
  Startup: 3-5 segundos
  Memória: 200-300 MB
  Throughput: 8,000+ req/s
  Latency P50: < 15ms

NODEJS:
  Startup: < 2 segundos
  Memória: 100-150 MB
  Throughput: 5,000+ req/s
  Latency P50: < 20ms

GO:
  Startup: < 1 segundo
  Memória: 30-50 MB
  Throughput: 15,000+ req/s
  Latency P50: < 5ms
```

---

## 🔐 **SEGURANÇA**

### **SEC-001: Credenciais**

```yaml
REGRA: NUNCA commitar credenciais
USAR: Variáveis de ambiente
ARQUIVO: .env (no .gitignore)

EXEMPLO:
  # ❌ ERRADO
  cassandra.password=mypassword123
  
  # ✅ CORRETO
  cassandra.password=${CASSANDRA_PASSWORD}
```

### **SEC-002: Secrets**

```yaml
REGRA: Secrets DEVEM ser gerenciados externamente
FERRAMENTAS:
  - Docker Secrets
  - Kubernetes Secrets
  - AWS Secrets Manager
  - Azure Key Vault
```

---

## 📝 **CHANGELOG**

```yaml
2025-11-12: Criação inicial do arquivo de regras
  - Estrutura do projeto
  - Regras de código
  - Regras de scripts
  - Regras de documentação
  - Regras de Git
  - Regras de testes
  - Queries comuns
  - Fluxos de trabalho
  - Validações automáticas
  - Instruções para IA
```

---

**🎯 Este arquivo é a fonte única de verdade para regras do projeto Beauty Salon App.**
