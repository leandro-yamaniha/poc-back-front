# 📝 Guia de Commits - Beauty Salon Backend

## Como Incluir Relatórios de Stress Test nos Commits

Os relatórios de stress test estão no `.gitignore` por padrão para evitar poluir o repositório com resultados de testes locais. No entanto, para commits importantes (como releases ou análises comparativas), você pode incluí-los.

---

## 🚀 Método 1: Script Automatizado (Recomendado)

Use o script `commit-with-reports.sh` que adiciona automaticamente os relatórios:

```bash
cd backend
./scripts/commit-with-reports.sh
```

**O que o script faz:**
1. ✅ Adiciona arquivos de configuração modificados
2. ✅ Força a adição dos relatórios (com `-f`)
3. ✅ Mostra o que será commitado
4. ✅ Pede mensagem de commit
5. ✅ Pergunta se quer fazer push

---

## 🔧 Método 2: Manual

### Passo 1: Adicionar Configurações
```bash
git add backend/*/docker-compose*.yml
git add backend/java-reactive/*.md
```

### Passo 2: Adicionar Relatórios (forçado)
```bash
git add -f backend/stress-test-results/RELATORIO_*.md
git add -f backend/stress-test-results/*.txt
```

### Passo 3: Commit
```bash
git commit -m "feat: sua mensagem aqui"
```

### Passo 4: Push
```bash
git push
```

---

## 📊 Quais Relatórios Incluir?

### Sempre Incluir:
- ✅ `RELATORIO_COMPARATIVO_V*.md` - Análises comparativas
- ✅ `RELATORIO_COMPLETO_FINAL.md` - Relatório consolidado
- ✅ `RESUMO_EXECUTIVO.txt` - Resumo em texto

### Opcional (geralmente não incluir):
- ❌ Resultados individuais por data (`20251113_*/`)
- ❌ Logs detalhados (`*.txt` dentro de pastas de data)
- ❌ Arquivos temporários

---

## 📋 Template de Mensagem de Commit

### Para Testes de Performance:
```
feat: stress test v3 - CPU 1.0 vCPU com memória 135 MiB

Configuração:
- Memória: 135 MiB
- CPU: 1.0 vCPU

Resultados:
- Go: 22,457 req/s (+59% vs V1)
- Python: 16,629 req/s (+7% vs V1)
- Node.js: 5,392 req/s (+53% vs V1)

Descobertas:
- CPU é mais importante que memória
- 135 MiB / 1.0 vCPU é configuração ideal

Arquivos:
- Configurações docker-compose atualizadas
- RELATORIO_COMPARATIVO_V3.md incluído
```

### Para Mudanças de Configuração:
```
config: aumentar CPU para 1.0 vCPU em todos backends

Mudança:
- CPU: 0.5 → 1.0 vCPU
- Memória: mantida em 135 MiB

Motivo:
- Testes mostraram que CPU é fator limitante
- Memória de 135 MiB é suficiente

Arquivos modificados:
- backend/*/docker-compose*.yml
```

---

## 🎯 Boas Práticas

### ✅ FAÇA:
1. Inclua relatórios em commits de análise/comparação
2. Use mensagens descritivas com resultados principais
3. Documente descobertas importantes
4. Inclua contexto da configuração testada
5. Use o script automatizado para consistência

### ❌ NÃO FAÇA:
1. Não commite resultados de testes locais/temporários
2. Não inclua logs muito grandes (> 1 MB)
3. Não commite sem revisar o que está sendo adicionado
4. Não use mensagens genéricas ("update", "fix")

---

## 📁 Estrutura de Arquivos

```
backend/
├── stress-test-results/
│   ├── RELATORIO_COMPARATIVO_V1.md    ✅ Incluir
│   ├── RELATORIO_COMPARATIVO_V2.md    ✅ Incluir
│   ├── RELATORIO_COMPARATIVO_V3.md    ✅ Incluir
│   ├── RELATORIO_COMPLETO_FINAL.md    ✅ Incluir
│   ├── RESUMO_EXECUTIVO.txt           ✅ Incluir
│   ├── 20251113_222307/               ❌ Não incluir (gitignore)
│   ├── 20251113_230853/               ❌ Não incluir (gitignore)
│   └── 20251113_233303/               ❌ Não incluir (gitignore)
└── scripts/
    └── commit-with-reports.sh         ✅ Script helper
```

---

## 🔍 Verificar o que será Commitado

Antes de commitar, sempre verifique:

```bash
git status
git diff --cached
```

---

## 🚨 Troubleshooting

### Problema: "The following paths are ignored by .gitignore"
**Solução:** Use `-f` para forçar:
```bash
git add -f backend/stress-test-results/RELATORIO_*.md
```

### Problema: Arquivo muito grande
**Solução:** Não commite logs/resultados detalhados, apenas relatórios consolidados.

### Problema: Esqueci de incluir relatórios
**Solução:** Adicione ao commit anterior:
```bash
git add -f backend/stress-test-results/RELATORIO_*.md
git commit --amend --no-edit
git push --force-with-lease
```

---

## 📚 Referências

- Script: `backend/scripts/commit-with-reports.sh`
- Gitignore: `backend/.gitignore`
- Resultados: `backend/stress-test-results/`

---

**Última atualização:** 2025-11-13
