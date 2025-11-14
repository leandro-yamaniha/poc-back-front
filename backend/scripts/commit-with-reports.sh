#!/bin/bash

# Script para fazer commit incluindo os relatórios de stress test
# Os relatórios estão no .gitignore mas podem ser forçados com -f

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                                                                      ║${NC}"
echo -e "${BLUE}║   📝  Commit com Relatórios de Stress Test  📝                      ║${NC}"
echo -e "${BLUE}║                                                                      ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Verificar se há mudanças
if [[ -z $(git status -s) ]]; then
    echo -e "${YELLOW}⚠️  Nenhuma mudança para commitar${NC}"
    exit 0
fi

# Mostrar status
echo -e "${BLUE}ℹ️  Status atual:${NC}"
git status -s
echo ""

# Adicionar arquivos de configuração modificados
echo -e "${GREEN}✅ Adicionando arquivos de configuração...${NC}"
git add backend/*/docker-compose*.yml 2>/dev/null || true
git add backend/java-reactive/*.md 2>/dev/null || true

# Adicionar relatórios (forçando, pois estão no gitignore)
echo -e "${GREEN}✅ Adicionando relatórios de stress test (forçado)...${NC}"
git add -f backend/stress-test-results/RELATORIO_*.md 2>/dev/null || true
git add -f backend/stress-test-results/*.txt 2>/dev/null || true

# Mostrar o que será commitado
echo ""
echo -e "${BLUE}ℹ️  Arquivos que serão commitados:${NC}"
git status -s
echo ""

# Pedir mensagem de commit
echo -e "${YELLOW}📝 Digite a mensagem do commit:${NC}"
read -r COMMIT_MESSAGE

if [[ -z "$COMMIT_MESSAGE" ]]; then
    echo -e "${RED}❌ Mensagem de commit não pode ser vazia${NC}"
    exit 1
fi

# Fazer commit
echo ""
echo -e "${GREEN}✅ Fazendo commit...${NC}"
git commit -m "$COMMIT_MESSAGE"

# Perguntar se quer fazer push
echo ""
echo -e "${YELLOW}🚀 Fazer push para origin? (y/n)${NC}"
read -r DO_PUSH

if [[ "$DO_PUSH" == "y" || "$DO_PUSH" == "Y" ]]; then
    echo -e "${GREEN}✅ Fazendo push...${NC}"
    git push
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                                      ║${NC}"
    echo -e "${GREEN}║   ✅  Commit e Push realizados com sucesso!  ✅                     ║${NC}"
    echo -e "${GREEN}║                                                                      ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════════╝${NC}"
else
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                                      ║${NC}"
    echo -e "${GREEN}║   ✅  Commit realizado com sucesso!  ✅                             ║${NC}"
    echo -e "${GREEN}║                                                                      ║${NC}"
    echo -e "${GREEN}║   Execute 'git push' quando estiver pronto                          ║${NC}"
    echo -e "${GREEN}║                                                                      ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════════╝${NC}"
fi
