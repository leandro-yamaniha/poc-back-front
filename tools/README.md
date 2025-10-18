# Tools Directory

Este diretório contém ferramentas e utilitários para desenvolvimento, análise de código, qualidade e testes de performance.

## 🛠️ Ferramentas Disponíveis

### SonarQube

Ferramenta de análise estática de código para garantir qualidade e segurança.

#### Como Usar

```bash
# A partir da raiz do projeto
cd tools/sonarqube
docker-compose up -d

# Ou diretamente da raiz
docker-compose -f tools/sonarqube/docker-compose.yml up -d
```

#### Acesso

- **URL**: http://localhost:9000
- **Usuário padrão**: admin
- **Senha padrão**: admin (altere no primeiro acesso)

#### Análise de Código

```bash
# Frontend
cd frontend
npm run test:coverage
npm run sonar

# Backend Java Reactive
cd backend/java-reactive
./mvnw clean verify sonar:sonar
```

#### Documentação

Para mais detalhes, consulte:
- [docs/SONAR_SETUP_GUIDE.md](../docs/SONAR_SETUP_GUIDE.md)
- [docs/SONAR_LOCAL_SETUP_GUIDE.md](../docs/SONAR_LOCAL_SETUP_GUIDE.md)

---

### Stress Test

Documentação completa para testes de carga e performance dos backends.

#### Conteúdo

- Guia completo de stress testing
- Instalação de ferramentas (wrk, Apache Bench)
- Cenários de teste detalhados
- Interpretação de resultados
- Melhores práticas e troubleshooting

#### Documentação

**Ver:** [stress-test/README.md](stress-test/README.md)

**Scripts relacionados:** `/scripts/stress-test-reactive.sh`

#### Quick Start

```bash
# 1. Instalar wrk
brew install wrk  # macOS

# 2. Executar teste
cd scripts
./stress-test-reactive.sh

# 3. Ver resultados
ls -la performance-test-results/
```

---

## 📁 Estrutura

```
tools/
├── sonarqube/
│   ├── docker-compose.yml       # Configuração do SonarQube
│   ├── sonar-analysis.sh        # Script de análise remota
│   ├── sonar-analysis-local.sh  # Script de análise local
│   ├── start-sonarqube-local.sh # Iniciar SonarQube
│   ├── stop-sonarqube-local.sh  # Parar SonarQube
│   └── README.md                # Documentação SonarQube
├── stress-test/
│   └── README.md                # Documentação de stress tests
└── README.md                    # Este arquivo
```

## 🎯 Propósito

Este diretório organiza ferramentas de:
- ✅ Análise de qualidade de código (SonarQube)
- ✅ Testes de performance e carga (Stress Test)
- ✅ Métricas e cobertura
- ✅ Segurança e vulnerabilidades
- ✅ Melhores práticas
- ✅ Benchmarking

## 📊 Quick Links

| Ferramenta | Descrição | Documentação |
|------------|-----------|--------------|
| **SonarQube** | Análise de código e qualidade | [Docs](sonarqube/README.md) |
| **Stress Test** | Testes de carga e performance | [Docs](stress-test/README.md) |

## 🚀 Futuras Ferramentas

Outras ferramentas que podem ser adicionadas:
- Prometheus (métricas em tempo real)
- Grafana (visualização de métricas)
- Jenkins (CI/CD)
- Portainer (gerenciamento Docker)
- K6 (testes de carga modernos)

---

**🛠️ Ferramentas organizadas para melhor desenvolvimento!**
