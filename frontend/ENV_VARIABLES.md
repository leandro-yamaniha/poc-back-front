# Environment Variables

Este diretório contém exemplos de arquivos de variáveis de ambiente para o frontend.

## 📝 Arquivos Disponíveis

### `.env.example`
Arquivo de exemplo com todas as variáveis de ambiente necessárias para o frontend React.

**Variáveis incluídas:**
- `REACT_APP_API_URL` - URL da API backend
- `BACKEND_HOST_PORT` - Porta do backend
- `FRONTEND_HOST_PORT` - Porta do frontend

### `.env-react.example`
Arquivo alternativo com configurações específicas para React.

## 🚀 Como Usar

### 1. Copiar o arquivo de exemplo

```bash
# Copiar para .env (arquivo usado pelo React)
cp .env.example .env

# Ou copiar a versão alternativa
cp .env-react.example .env
```

### 2. Ajustar as variáveis

Edite o arquivo `.env` conforme necessário:

```bash
# Exemplo de configuração
REACT_APP_API_URL=http://localhost:8085/api
BACKEND_HOST_PORT=8085
FRONTEND_HOST_PORT=3001
```

### 3. Executar o projeto

```bash
npm start
```

## ⚠️ Importante

- **Não commitar** arquivos `.env` com valores reais
- Apenas arquivos `.env.example` devem ser versionados
- O arquivo `.env` está no `.gitignore`

## 🔧 Variáveis Disponíveis

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `REACT_APP_API_URL` | URL da API backend | `http://localhost:8085/api` |
| `BACKEND_HOST_PORT` | Porta do backend | `8085` |
| `BACKEND_PORT` | Porta interna do backend | `8085` |
| `FRONTEND_HOST_PORT` | Porta do frontend | `3001` |

## 📚 Documentação

Para mais informações sobre variáveis de ambiente no React:
- [Create React App - Environment Variables](https://create-react-app.dev/docs/adding-custom-environment-variables/)

---

**⚙️ Configure suas variáveis de ambiente antes de executar!**
