# SonarQube

Análise estática de código para o Beauty Salon Management System.

## 🎯 O que é SonarQube?

SonarQube é uma plataforma de análise contínua de qualidade de código que detecta:
- 🐛 Bugs e code smells
- 🔒 Vulnerabilidades de segurança
- 📊 Cobertura de testes
- 📈 Duplicação de código
- 📝 Complexidade ciclomática

## 🚀 Início Rápido

### 1. Iniciar SonarQube

```bash
# A partir deste diretório
docker-compose up -d

# Aguardar inicialização (pode levar 1-2 minutos)
docker-compose logs -f
```

### 2. Acessar Interface

- **URL**: http://localhost:9000
- **Login inicial**: admin / admin
- **Altere a senha** no primeiro acesso

### 3. Analisar Código

#### Frontend (React)
```bash
cd ../../frontend
npm run test:coverage
npm run sonar
```

#### Backend Java Reactive
```bash
cd ../../backend/java-reactive
./mvnw clean verify sonar:sonar
```

## 📊 Projetos Configurados

### Frontend
- **Project Key**: `beauty-salon-frontend-react`
- **Dashboard**: http://localhost:9000/dashboard?id=beauty-salon-frontend-react
- **Cobertura**: 50%+

### Backend Java Reactive
- **Project Key**: `java-backend`
- **Dashboard**: http://localhost:9000/dashboard?id=java-backend
- **Cobertura**: 85%+
- **Testes**: 190/190 (100%)

## 🔧 Configuração

### Arquivo de Configuração

Cada projeto tem seu `sonar-project.properties`:

```properties
# Frontend
sonar.projectKey=beauty-salon-frontend-react
sonar.sources=src
sonar.tests=src
sonar.test.inclusions=**/*.test.js,**/*.test.jsx
sonar.javascript.lcov.reportPaths=coverage/lcov.info

# Backend
sonar.projectKey=java-backend
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

### Variáveis de Ambiente

```bash
# Token de autenticação (opcional)
export SONAR_TOKEN=your-token-here

# URL do servidor
export SONAR_HOST_URL=http://localhost:9000
```

## 📚 Documentação Completa

Para guias detalhados, consulte:
- [../../docs/SONAR_SETUP_GUIDE.md](../../docs/SONAR_SETUP_GUIDE.md)
- [../../docs/SONAR_LOCAL_SETUP_GUIDE.md](../../docs/SONAR_LOCAL_SETUP_GUIDE.md)

## 🛑 Parar SonarQube

```bash
# Parar serviços
docker-compose down

# Parar e remover volumes (limpar dados)
docker-compose down -v
```

## 🔍 Troubleshooting

### Problema: SonarQube não inicia
```bash
# Verificar logs
docker-compose logs sonarqube

# Aumentar memória do Docker (mínimo 2GB)
```

### Problema: Análise falha
```bash
# Verificar token de autenticação
# Verificar conectividade: curl http://localhost:9000

# Reexecutar com verbose
./mvnw sonar:sonar -X
```

### Problema: Porta 9000 em uso
```yaml
# Editar docker-compose.yml
ports:
  - "9001:9000"  # Usar porta diferente
```

## 📈 Métricas Importantes

- **Coverage**: Cobertura de testes (meta: >80%)
- **Duplications**: Duplicação de código (meta: <3%)
- **Maintainability**: Índice de manutenibilidade (meta: A)
- **Reliability**: Confiabilidade (meta: A)
- **Security**: Segurança (meta: A)

---

**📊 Análise de qualidade de código em execução!**
