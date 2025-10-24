# 🔨 Guia de Scripts de Build

Scripts automatizados para build de todos os backends.

---

## 📋 SCRIPTS DISPONÍVEIS

### **1. build-all.sh** (Sequencial)
Build de todos os backends em sequência.

```bash
./build-all.sh
```

**Características:**
- ✅ Executa um backend por vez
- ✅ Output em tempo real
- ✅ Para no primeiro erro (se quiser)
- ✅ Mais fácil de debugar
- ⏱️  Tempo: ~5-10 minutos

---

### **2. build-all-parallel.sh** (Paralelo)
Build de todos os backends simultaneamente.

```bash
./build-all-parallel.sh
```

**Características:**
- ✅ Executa todos em paralelo
- ✅ Logs salvos em arquivos
- ✅ Muito mais rápido
- ✅ Resumo final com status
- ⏱️  Tempo: ~2-3 minutos

**Logs:**
```bash
# Logs salvos automaticamente em:
build-logs-YYYYMMDD-HHMMSS/
├── dotnet.log
├── java.log
├── java-reactive.log
├── go.log
├── nodejs.log
└── python.log
```

---

### **3. build-specific.sh** (Seletivo)
Build apenas de backends específicos.

```bash
# Build de um backend
./build-specific.sh dotnet

# Build de múltiplos backends
./build-specific.sh java go python

# Build de todos os Java
./build-specific.sh java java-reactive
```

**Backends disponíveis:**
- `dotnet`
- `java`
- `java-reactive`
- `go`
- `nodejs`
- `python`

---

### **4. clean-all.sh** (Limpeza)
Remove todos os artefatos de build.

```bash
./clean-all.sh
```

**Remove:**
- `.NET:` publish, bin, obj
- `Java:` target
- `Go:` bin
- `Node.js:` node_modules, dist
- `Python:` venv, __pycache__, *.pyc

---

## 🚀 EXEMPLOS DE USO

### **Build Completo (Primeira vez):**
```bash
# Paralelo (mais rápido)
./build-all-parallel.sh

# Sequencial (mais fácil debugar)
./build-all.sh
```

### **Build Após Mudança de Código:**
```bash
# Se mudou apenas .NET
./build-specific.sh dotnet

# Se mudou Java e Go
./build-specific.sh java go
```

### **Limpar e Rebuildar:**
```bash
# Limpar tudo
./clean-all.sh

# Build tudo novamente
./build-all-parallel.sh
```

### **Build Seletivo para Teste:**
```bash
# Testar build dos backends mais rápidos primeiro
./build-specific.sh go python nodejs

# Depois os mais lentos
./build-specific.sh java java-reactive dotnet
```

---

## 📊 COMPARAÇÃO

| Script | Velocidade | Output | Uso Recomendado |
|--------|-----------|--------|-----------------|
| **build-all.sh** | 🐢 Lento | 👁️ Tempo real | Debug, primeira vez |
| **build-all-parallel.sh** | 🚀 Rápido | 📄 Logs | CI/CD, desenvolvimento |
| **build-specific.sh** | ⚡ Variável | 👁️ Tempo real | Mudanças pontuais |
| **clean-all.sh** | ⚡ Rápido | 👁️ Tempo real | Limpeza |

---

## 🔧 WORKFLOW RECOMENDADO

### **Desenvolvimento Local:**
```bash
# 1. Fazer mudanças no código
vim backend/dotnet/Controllers/CustomerController.cs

# 2. Build apenas o que mudou
./build-specific.sh dotnet

# 3. Testar
cd dotnet
docker build -t backend-dotnet .
docker run -p 10002:10002 backend-dotnet
```

### **CI/CD:**
```bash
# Build paralelo para speed
./build-all-parallel.sh

# Se sucesso, build Docker images
if [ $? -eq 0 ]; then
    docker-compose build
fi
```

### **Limpeza Periódica:**
```bash
# Toda sexta-feira
./clean-all.sh
./build-all-parallel.sh
```

---

## 📋 EXIT CODES

Todos os scripts seguem o padrão:
- `0` - Sucesso (todos builds OK)
- `1` - Falha (um ou mais builds falharam)

**Exemplo em script:**
```bash
if ./build-all-parallel.sh; then
    echo "✅ Todos builds OK, pode fazer deploy"
    ./deploy.sh
else
    echo "❌ Build falhou, verificar logs"
    exit 1
fi
```

---

## 🎨 OUTPUT COLORIDO

Os scripts usam cores para facilitar visualização:
- 🟢 **Verde:** Sucesso
- 🔴 **Vermelho:** Erro
- 🟡 **Amarelo:** Aviso

---

## 🧪 TESTANDO OS SCRIPTS

### **Teste Rápido:**
```bash
# Build apenas Go (mais rápido)
./build-specific.sh go

# Se OK, testar todos
./build-all-parallel.sh
```

### **Teste de Limpeza:**
```bash
# Limpar
./clean-all.sh

# Verificar que foi limpo
ls -la */publish */bin */target */node_modules */venv 2>/dev/null

# Build novamente
./build-all-parallel.sh
```

---

## 💡 DICAS

### **1. Primeira Execução:**
```bash
# Dar permissão de execução
chmod +x *.sh

# Build paralelo (mais rápido)
./build-all-parallel.sh
```

### **2. Verificar Logs de Build Paralelo:**
```bash
# Ver último log do .NET
cat build-logs-*/dotnet.log

# Ver apenas erros
grep -i error build-logs-*/*.log
```

### **3. Build em Background:**
```bash
# Build paralelo em background
nohup ./build-all-parallel.sh > build.out 2>&1 &

# Ver progresso
tail -f build.out
```

### **4. Integração com Git Hooks:**
```bash
# .git/hooks/pre-commit
#!/bin/bash
./backend/build-all-parallel.sh
```

---

## 🚨 TROUBLESHOOTING

### **Erro: Permission denied**
```bash
chmod +x *.sh
```

### **Build falha mas logs não mostram erro**
```bash
# Ver log completo do backend específico
cat build-logs-*/dotnet.log
```

### **Out of memory durante build paralelo**
```bash
# Use sequencial ao invés de paralelo
./build-all.sh
```

### **Build funciona individualmente mas falha no script**
```bash
# Verificar permissões dos build.sh individuais
chmod +x */build.sh
```

---

## 📦 ESTRUTURA DE ARQUIVOS

```
backend/
├── build-all.sh              # Build sequencial
├── build-all-parallel.sh     # Build paralelo
├── build-specific.sh         # Build seletivo
├── clean-all.sh              # Limpeza
├── BUILD_SCRIPTS_GUIDE.md    # Este guia
├── dotnet/
│   └── build.sh
├── java/
│   └── build.sh
├── java-reactive/
│   └── build.sh
├── go/
│   └── build.sh
├── nodejs/
│   └── build.sh
└── python/
    └── build.sh
```

---

## ⏱️ TEMPOS ESPERADOS

### **Build Sequencial (build-all.sh):**
- .NET: ~1min
- Java: ~1min
- Java Reactive: ~1min
- Go: ~10s
- Node.js: ~30s
- Python: ~20s
- **Total: ~5min**

### **Build Paralelo (build-all-parallel.sh):**
- Todos simultaneamente
- **Total: ~2min** (tempo do mais lento)

---

## 🎯 QUANDO USAR CADA SCRIPT

### **build-all.sh:**
- ✅ Primeira vez usando o projeto
- ✅ Debugging de problemas de build
- ✅ Ver output em tempo real
- ✅ Build em máquinas com pouca RAM

### **build-all-parallel.sh:**
- ✅ CI/CD pipelines
- ✅ Desenvolvimento ativo
- ✅ Quando velocidade é prioridade
- ✅ Máquinas com boa RAM/CPU

### **build-specific.sh:**
- ✅ Mudou código de um backend específico
- ✅ Testando fix em backend específico
- ✅ Build incremental

### **clean-all.sh:**
- ✅ Problemas estranhos de build
- ✅ Limpeza semanal
- ✅ Antes de commit importante
- ✅ Liberar espaço em disco

---

**Criado:** October 22, 2025  
**Status:** ✅ **PRONTO PARA USO**  
**Scripts:** 4 utilitários de build
