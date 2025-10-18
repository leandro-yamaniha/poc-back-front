# Tests Directory

Este diretório contém a configuração de testes automatizados do projeto.

## 🧪 Ambiente de Testes

### Docker Compose para Testes

O arquivo `docker-compose.yml` neste diretório configura um ambiente isolado para testes automatizados.

#### Como Usar

```bash
# A partir da raiz do projeto
cd tests
docker-compose up

# Ou diretamente da raiz
docker-compose -f tests/docker-compose.yml up
```

#### Configuração

- **Backend**: Backend Java para testes
- **Cassandra**: Banco de dados de teste
- **Perfil**: `test` (configuração específica para testes)
- **Dados**: Volume isolado para não interferir com desenvolvimento

#### Uso em CI/CD

```yaml
# Exemplo para GitHub Actions
- name: Run Tests
  run: |
    cd tests
    docker-compose up -d
    # Execute seus testes aqui
    docker-compose down
```

## 📝 Estrutura

```
tests/
├── docker-compose.yml    # Configuração do ambiente de teste
└── README.md            # Este arquivo
```

## 🎯 Propósito

Este ambiente é usado para:
- ✅ Testes de integração
- ✅ Testes automatizados em CI/CD
- ✅ Validação antes de deploy
- ✅ Testes isolados do ambiente de desenvolvimento

---

**🧪 Ambiente de testes isolado e pronto para CI/CD!**
