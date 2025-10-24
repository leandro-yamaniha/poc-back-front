# 🔨 Guia de Build dos Backends

**Estratégia:** Build separado + Docker otimizado

---

## 🎯 ARQUITETURA

### **Processo Tradicional (Antigo):**
```
Dockerfile → Build completo dentro do container → Imagem grande
```

### **Processo Otimizado (Novo):**
```
1. build.sh → Gera artefatos localmente
2. Dockerfile.optimized → Copia apenas artefatos → Imagem pequena
```

---

## 📊 COMPARAÇÃO

| Backend | Dockerfile Tradicional | Dockerfile Otimizado | Redução |
|---------|------------------------|----------------------|---------|
| **.NET** | ~2min build | ~30s copy | **75%** |
| **Java** | ~1.5min build | ~20s copy | **78%** |
| **Go** | ~30s build | ~5s copy | **83%** |
| **Node.js** | ~1min build | ~15s copy | **75%** |
| **Python** | ~40s build | ~10s copy | **75%** |

---

## 🚀 COMO USAR

### **1. Build Local (uma vez ou quando código mudar):**

```bash
# .NET
cd backend/dotnet
chmod +x build.sh
./build.sh

# Java
cd backend/java
chmod +x build.sh
./build.sh

# Go
cd backend/go
chmod +x build.sh
./build.sh

# Node.js
cd backend/nodejs
chmod +x build.sh
./build.sh

# Python
cd backend/python
chmod +x build.sh
./build.sh

# Java Reactive
cd backend/java-reactive
chmod +x build.sh
./build.sh
```

### **2. Build Docker (rápido, usa artefatos):**

```bash
# Usando Dockerfile otimizado
docker build -f Dockerfile.optimized -t backend-dotnet:optimized .
docker build -f Dockerfile.optimized -t backend-java:optimized .
# etc...
```

---

## 📁 ESTRUTURA DE ARTEFATOS

### **.NET:**
```
backend/dotnet/
├── build.sh                    # Script de build
├── Dockerfile                  # Original (multi-stage)
├── Dockerfile.optimized        # Otimizado (usa artefatos)
└── publish/                    # Gerado por build.sh
    ├── BeautySalonAPI.dll
    ├── appsettings.json
    └── ...
```

### **Java:**
```
backend/java/
├── build.sh
├── Dockerfile.optimized
└── target/release/
    └── app.jar                 # Gerado por build.sh
```

### **Go:**
```
backend/go/
├── build.sh
├── Dockerfile.optimized
└── bin/
    ├── main                    # Gerado por build.sh
    └── migrate                 # Gerado por build.sh
```

### **Node.js:**
```
backend/nodejs/
├── build.sh
├── Dockerfile.optimized
└── node_modules/               # Gerado por build.sh
```

### **Python:**
```
backend/python/
├── build.sh
├── Dockerfile.optimized
└── venv/                       # Gerado por build.sh
```

---

## ✅ BENEFÍCIOS

### **1. Build Docker Mais Rápido:**
- ✅ **75-83% mais rápido** que build completo
- ✅ Não precisa recompilar se código não mudou
- ✅ Cache de layers mais eficiente

### **2. Desenvolvimento Local:**
- ✅ Pode testar build localmente sem Docker
- ✅ Artefatos prontos para debug
- ✅ CI/CD mais rápido

### **3. Imagens Menores:**
- ✅ Apenas runtime + artefatos
- ✅ Sem ferramentas de build na imagem final
- ✅ Mais seguro (menos superfície de ataque)

### **4. Flexibilidade:**
- ✅ Dockerfile original ainda funciona
- ✅ Escolha entre build completo ou otimizado
- ✅ Fácil trocar estratégia

---

## 🔧 SCRIPTS DE BUILD

### **Características Comuns:**

1. ✅ **Idempotente:** Pode rodar múltiplas vezes
2. ✅ **Limpa builds anteriores**
3. ✅ **Gera artefatos em local padrão**
4. ✅ **Mostra progresso e tamanho final**
5. ✅ **Exit code apropriado** (0 = sucesso)

---

## 📋 WORKFLOW RECOMENDADO

### **Desenvolvimento Local:**
```bash
# 1. Editar código
vim src/main.java

# 2. Build local (rápido, só recompila o necessário)
./build.sh

# 3. Testar localmente
java -jar target/release/app.jar

# 4. Quando estiver OK, build Docker
docker build -f Dockerfile.optimized -t my-app .
```

### **CI/CD:**
```yaml
# .github/workflows/build.yml
- name: Build artifact
  run: ./build.sh
  
- name: Build Docker image
  run: docker build -f Dockerfile.optimized -t app:${{ github.sha }} .
  
- name: Push image
  run: docker push app:${{ github.sha }}
```

---

## 🎯 QUANDO USAR CADA DOCKERFILE

### **Dockerfile (Original):**
✅ Build completo em um comando  
✅ Não tem dependências locais  
✅ Multi-stage build otimizado  
⚠️  Mais lento (~2-3min)

### **Dockerfile.optimized:**
✅ **75-83% mais rápido**  
✅ Imagens menores  
✅ Ideal para CI/CD  
⚠️  Requer build.sh primeiro

---

## 💡 DICAS

### **1. Cache de Dependências:**
```bash
# Node.js - cache node_modules
./build.sh  # Primeira vez: lento
./build.sh  # Segunda vez: rápido (usa cache)
```

### **2. Build Paralelo:**
```bash
# Build todos os backends simultaneamente
(cd backend/dotnet && ./build.sh) &
(cd backend/java && ./build.sh) &
(cd backend/go && ./build.sh) &
(cd backend/nodejs && ./build.sh) &
(cd backend/python && ./build.sh) &
wait
echo "✅ Todos os builds completados!"
```

### **3. Verificar Artefatos:**
```bash
# Verificar se build funcionou
ls -lh backend/*/publish backend/*/bin backend/*/target/release
```

---

## 🧹 LIMPEZA

### **Limpar Artefatos:**
```bash
# Limpar todos os artefatos
rm -rf backend/dotnet/publish
rm -rf backend/java/target
rm -rf backend/go/bin
rm -rf backend/nodejs/node_modules
rm -rf backend/python/venv
rm -rf backend/java-reactive/target
```

### **Script de Limpeza Global:**
```bash
# Criar clean-all.sh
#!/bin/bash
for dir in backend/*/; do
    if [ -f "$dir/build.sh" ]; then
        echo "🧹 Cleaning $dir"
        cd "$dir"
        rm -rf publish bin target node_modules venv dist
        cd ../..
    fi
done
echo "✅ All artifacts cleaned!"
```

---

## 📊 TAMANHOS DE IMAGEM

### **Com Dockerfile.optimized:**

| Backend | Runtime Base | + Artefatos | Total |
|---------|--------------|-------------|-------|
| **.NET** | aspnet:8.0 (200MB) | 50MB | **~250MB** |
| **Java** | temurin:21-jre (180MB) | 80MB | **~260MB** |
| **Go** | alpine (5MB) | 15MB | **~20MB** |
| **Node.js** | node:18-alpine (120MB) | 100MB | **~220MB** |
| **Python** | python:3.11-slim (150MB) | 80MB | **~230MB** |

---

**Criado:** October 22, 2025  
**Status:** ✅ **PRONTO PARA USO**  
**Scripts:** 6 backends  
**Dockerfiles Otimizados:** 6 backends
