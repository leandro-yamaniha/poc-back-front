#!/bin/bash
# Script de Backup Completo do Cassandra
# Usage: ./database/scripts/backup-cassandra.sh

set -e

BACKUP_DIR="./database/backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "=========================================="
echo "💾 BACKUP DO CASSANDRA"
echo "=========================================="
echo ""
echo "📁 Diretório de backup: $BACKUP_DIR"
echo ""

# Lista de tabelas para backup
TABLES=("customers" "services" "staff" "appointments" "payments" "schema_migrations")

for table in "${TABLES[@]}"; do
  echo "📋 Fazendo backup de: $table..."
  
  # Exportar tabela para CSV dentro do container
  docker exec beauty-salon-cassandra cqlsh -e "
    USE beauty_salon;
    COPY $table TO '/tmp/${table}_backup.csv' WITH HEADER = TRUE;
  " 2>/dev/null || echo "⚠️  Tabela $table não encontrada ou vazia"
  
  # Copiar do container para o host
  if docker exec beauty-salon-cassandra test -f "/tmp/${table}_backup.csv"; then
    docker cp beauty-salon-cassandra:/tmp/${table}_backup.csv "$BACKUP_DIR/"
    
    # Limpar arquivo temporário
    docker exec beauty-salon-cassandra rm -f "/tmp/${table}_backup.csv"
    
    echo "   ✅ Backup de $table concluído"
  else
    echo "   ⚠️  Nenhum dado em $table"
  fi
  
  echo ""
done

echo "=========================================="
echo "✅ BACKUP COMPLETO!"
echo "📁 Local: $BACKUP_DIR"
echo "📊 Arquivos criados:"
ls -lh "$BACKUP_DIR"
echo "=========================================="
