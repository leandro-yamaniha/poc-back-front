# Script Analysis Report - Beauty Salon Management System

## 📊 Análise Completa de Scripts

**Total de scripts .sh**: 19 arquivos
**Scripts duplicados**: 2 pares identificados
**Scripts similares**: 2 pares identificados

---

## ❌ SCRIPTS DUPLICADOS/SIMILARES

### 1. **Stress Test Scripts** (2 arquivos similares)

#### `scripts/simple-stress-test.sh` (278 linhas)
- **Propósito**: Teste de carga simples usando curl
- **Método**: curl com requests concorrentes
- **Configuração**: Básica, sem wrk

#### `scripts/stress-test-reactive.sh` (249 linhas)
- **Propósito**: Teste de stress completo usando wrk
- **Método**: wrk (ferramenta profissional)
- **Configuração**: Avançada com múltiplos cenários

**Problema**: Dois scripts fazem basicamente a mesma coisa (teste de performance)
**Diferença**: Um usa curl (simples), outro usa wrk (profissional)

**Recomendação**: ⚠️ **CONSOLIDAR**
- Manter apenas `stress-test-reactive.sh` (mais completo)
- Remover `simple-stress-test.sh` (menos útil)
- Ou renomear para `stress-test-basic.sh` se quiser manter alternativa

---

### 2. **SonarQube Analysis Scripts** (2 arquivos similares)

#### `tools/sonarqube/sonar-analysis.sh` (105 linhas)
- **Propósito**: Análise SonarQube genérica
- **Target**: Servidor SonarQube remoto/genérico
- **Requer**: sonar-scanner instalado

#### `tools/sonarqube/sonar-analysis-local.sh` (156 linhas)
- **Propósito**: Análise SonarQube local
- **Target**: Docker local (localhost:9000)
- **Requer**: sonar-scanner + Docker local rodando

**Problema**: Funcionalidade sobreposta
**Diferença**: Um é genérico, outro específico para Docker local

**Recomendação**: ✅ **MANTER AMBOS**
- Propósitos diferentes (remoto vs local)
- Mas adicionar documentação clara sobre quando usar cada um

---

## 📋 SCRIPTS POR CATEGORIA

### **Raiz - scripts/** (6 arquivos)
```
scripts/
├── connect-cassandra.sh           # Conectar ao Cassandra
├── docker-start.sh                # Start Docker (27 linhas - muito simples)
├── install-dotnet.sh              # Instalar .NET Core
├── simple-stress-test.sh          # ⚠️ Teste simples (DUPLICADO)
├── start-reactive-stack.sh        # Start stack reativo
└── stress-test-reactive.sh        # ✅ Teste completo
```

### **SonarQube - tools/sonarqube/** (4 arquivos)
```
tools/sonarqube/
├── sonar-analysis.sh              # ✅ Análise genérica
├── sonar-analysis-local.sh        # ✅ Análise local
├── start-sonarqube-local.sh       # Start SonarQube
└── stop-sonarqube-local.sh        # Stop SonarQube
```

### **Backend Específicos** (9 arquivos)
```
backend/go/
├── scripts/start.sh
└── test_endpoints.sh

backend/java/scripts/
├── run-standard-vs-virtual-threads-g1gc.sh
├── run-stress-tests.sh
├── run-test-profiles.sh
└── run-virtual-threads-comparison.sh

backend/nodejs/scripts/
└── test-migrations-with-docker.sh

frontend/scripts/
└── run-e2e-tests.sh

database/
└── docker-entrypoint.sh
```

---

## 🔍 ANÁLISE DETALHADA

### Scripts Muito Simples (Candidatos a Remoção)

#### `scripts/docker-start.sh` (27 linhas)
```bash
#!/bin/bash
docker-compose up -d
```
**Problema**: Muito simples, apenas um wrapper
**Recomendação**: ❌ **REMOVER** - Usuários podem usar `docker-compose up -d` diretamente

---

## 📊 RESUMO DE AÇÕES

### REMOVER (2 arquivos)
```bash
# 1. Script muito simples
rm scripts/docker-start.sh

# 2. Teste duplicado (manter o completo)
rm scripts/simple-stress-test.sh
```

### MANTER COM DOCUMENTAÇÃO (2 pares)
- `sonar-analysis.sh` + `sonar-analysis-local.sh` - Propósitos diferentes
- `stress-test-reactive.sh` - Manter apenas este

### ATUALIZAR DOCUMENTAÇÃO
- Adicionar em `scripts/README.md` quando usar cada script
- Adicionar em `tools/sonarqube/README.md` diferença entre scripts

---

## ✅ ESTRUTURA RECOMENDADA FINAL

```
scripts/
├── README.md                      # ✅ Documentação
├── connect-cassandra.sh           # ✅ Útil
├── install-dotnet.sh              # ✅ Útil
├── start-reactive-stack.sh        # ✅ Útil
└── stress-test-reactive.sh        # ✅ Completo (manter)

tools/sonarqube/
├── README.md                      # ✅ Documentação
├── sonar-analysis.sh              # ✅ Remoto
├── sonar-analysis-local.sh        # ✅ Local
├── start-sonarqube-local.sh       # ✅ Útil
└── stop-sonarqube-local.sh        # ✅ Útil
```

---

## 🎯 BENEFÍCIOS DA LIMPEZA

### Antes
- 19 scripts
- 2 duplicados
- 1 muito simples
- Confusão sobre qual usar

### Depois
- 17 scripts (-11%)
- Zero duplicação
- Apenas scripts úteis
- Documentação clara

---

## 📝 PRÓXIMOS PASSOS

1. **Remover** `docker-start.sh` e `simple-stress-test.sh`
2. **Atualizar** `scripts/README.md` com guia de uso
3. **Atualizar** `tools/sonarqube/README.md` explicando diferença entre scripts
4. **Adicionar** seção "Quando usar" em cada README

---

**Conclusão**: 2 scripts podem ser removidos com segurança, reduzindo duplicação e simplificando o projeto.
