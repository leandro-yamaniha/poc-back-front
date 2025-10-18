# Scripts Directory

Este diretório contém scripts utilitários para desenvolvimento, testes e manutenção do projeto.

## 📜 Scripts Disponíveis

### 🔧 **Instalação e Setup**
- **[install-dotnet.sh](install-dotnet.sh)** - Instala .NET Core SDK no sistema
  - Suporta macOS, Linux e Windows
  - Múltiplos métodos de instalação
  - Verificação automática

### 🧪 **Testes e Debug**
- **[debug-api-endpoints.js](debug-api-endpoints.js)** - Testa endpoints da API
  - Verifica status de todos os endpoints
  - Mostra dados de exemplo
  - Útil para debug e validação

### 📊 **Performance e Stress Tests**
- **[stress-test-reactive.sh](stress-test-reactive.sh)** - Teste de carga do backend reativo
  - Usa wrk para testes de performance
  - Múltiplos cenários de carga
  - Gera relatórios detalhados

## 🚀 Como Usar

### Debug de API
```bash
# Certifique-se que o backend está rodando
cd scripts
node debug-api-endpoints.js
```

### Instalar .NET
```bash
cd scripts
chmod +x install-dotnet.sh
./install-dotnet.sh
```

### Teste de Stress
```bash
cd scripts
chmod +x stress-test-reactive.sh
./stress-test-reactive.sh
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
├── install-*.sh                 # Scripts de instalação
├── test-*.sh                    # Scripts de teste
├── debug-*.js                   # Scripts de debug
└── stress-test-*.sh            # Scripts de performance
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

## 🎯 Propósito

Este diretório organiza:
- ✅ Scripts de automação
- ✅ Ferramentas de desenvolvimento
- ✅ Utilitários de teste
- ✅ Scripts de instalação

---

**📜 Scripts organizados para facilitar o desenvolvimento!**
