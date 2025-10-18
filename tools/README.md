# Tools Directory

Este diretório contém ferramentas e utilitários para desenvolvimento e análise de qualidade.

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

## 📁 Estrutura

```
tools/
├── sonarqube/
│   ├── docker-compose.yml    # Configuração do SonarQube
│   └── README.md             # Documentação específica
└── README.md                 # Este arquivo
```

## 🎯 Propósito

Este diretório organiza ferramentas de:
- ✅ Análise de qualidade de código
- ✅ Métricas e cobertura
- ✅ Segurança e vulnerabilidades
- ✅ Melhores práticas

## 🚀 Futuras Ferramentas

Outras ferramentas que podem ser adicionadas:
- Prometheus (métricas)
- Grafana (visualização)
- Jenkins (CI/CD)
- Portainer (gerenciamento Docker)

---

**🛠️ Ferramentas organizadas para melhor desenvolvimento!**
