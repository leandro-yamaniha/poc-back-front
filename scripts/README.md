# Scripts Directory

Este diretório contém scripts utilitários para desenvolvimento, testes e manutenção do projeto.

## 📜 Scripts Disponíveis

### 🚀 **Inicialização**
- **[start-reactive-stack.sh](start-reactive-stack.sh)** - Inicia stack completo do backend reativo
  - Cassandra + Backend Reactive + Frontend
  - Health checks automáticos
  - Configuração completa

### 🔌 **Banco de Dados**
- **[connect-cassandra.sh](connect-cassandra.sh)** - Conecta ao Cassandra via cqlsh
  - Conexão rápida ao banco
  - Útil para debug e queries manuais

### 📊 **Performance e Stress Tests**
- **[stress-test-reactive.sh](stress-test-reactive.sh)** - Teste de carga profissional do backend reativo
  - Usa wrk para testes de performance
  - Múltiplos cenários de carga (10, 50, 100, 200, 500 usuários)
  - Gera relatórios detalhados em Markdown
  - Testa todos os endpoints principais

### 🧪 **Debug**
- **[debug-api-endpoints.js](debug-api-endpoints.js)** - Testa endpoints da API (gitignored)
  - Verifica status de todos os endpoints
  - Mostra dados de exemplo
  - Útil para debug e validação

## 🚀 Como Usar

### Iniciar Stack Reativo
```bash
cd scripts
chmod +x start-reactive-stack.sh
./start-reactive-stack.sh
```

### Conectar ao Cassandra
```bash
cd scripts
chmod +x connect-cassandra.sh
./connect-cassandra.sh
```

### Teste de Stress/Performance
```bash
cd scripts
chmod +x stress-test-reactive.sh
./stress-test-reactive.sh

# Resultados salvos em: performance-test-results/
```

### Debug de API
```bash
# Certifique-se que o backend está rodando
cd scripts
node debug-api-endpoints.js
```

## 📝 Convenções

### Nomenclatura
- Scripts shell: `nome-descritivo.sh`
- Scripts Node.js: `nome-descritivo.js`
- Scripts Python: `nome-descritivo.py`

### Estrutura
```
scripts/
├── README.md                    # Este arquivo
├── start-*.sh                   # Scripts de inicialização
├── connect-*.sh                 # Scripts de conexão
├── stress-test-*.sh             # Scripts de performance
└── debug-*.js                   # Scripts de debug
```

## ⚠️ Requisitos

### debug-api-endpoints.js
```bash
npm install axios
```

### stress-test-reactive.sh
```bash
# macOS
brew install wrk

# Linux
sudo apt-get install wrk
```

### connect-cassandra.sh
```bash
# Cassandra deve estar rodando
docker-compose up -d cassandra
```

## 🎯 Quando Usar Cada Script

### Para Desenvolvimento
1. `start-reactive-stack.sh` - Inicia ambiente completo
2. `connect-cassandra.sh` - Debug do banco de dados
3. `debug-api-endpoints.js` - Testa API

### Para Testes de Performance
1. `stress-test-reactive.sh` - Teste completo com wrk

## 📊 Scripts de Performance

O script `stress-test-reactive.sh` testa com:
- **10 usuários** - Carga leve
- **50 usuários** - Carga média
- **100 usuários** - Carga alta
- **200 usuários** - Carga muito alta
- **500 usuários** - Carga extrema

Endpoints testados:
- `/api/customers`
- `/api/services`
- `/api/staff`
- `/api/appointments`

## 🎯 Propósito

Este diretório organiza:
- ✅ Scripts de automação
- ✅ Ferramentas de desenvolvimento
- ✅ Utilitários de teste
- ✅ Scripts de instalação
- ✅ Scripts de performance

---

**📜 Scripts organizados para facilitar o desenvolvimento!**
