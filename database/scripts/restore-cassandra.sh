#!/bin/bash
# Script de Restore do Cassandra
# Usage: ./database/scripts/restore-cassandra.sh <backup_directory>

set -e

if [ -z "$1" ]; then
  echo "❌ Erro: Especifique o diretório de backup"
  echo "Usage: $0 <backup_directory>"
  echo "Exemplo: $0 ./database/backups/20251022_120000"
  exit 1
fi

BACKUP_DIR="$1"

if [ ! -d "$BACKUP_DIR" ]; then
  echo "❌ Erro: Diretório não encontrado: $BACKUP_DIR"
  exit 1
fi

echo "=========================================="
echo "♻️  RESTORE DO CASSANDRA"
echo "=========================================="
echo ""
echo "📁 Restaurando de: $BACKUP_DIR"
echo ""
echo "⚠️  ATENÇÃO: Isso irá sobrescrever os dados existentes!"
read -p "Deseja continuar? (y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "❌ Operação cancelada."
  exit 0
fi

echo ""

# Lista de tabelas para restore
for csv_file in "$BACKUP_DIR"/*.csv; do
  if [ -f "$csv_file" ]; then
    table_name=$(basename "$csv_file" _backup.csv)
    
    echo "📋 Restaurando: $table_name..."
    
    # Copiar arquivo para o container
    docker cp "$csv_file" beauty-salon-cassandra:/tmp/restore_${table_name}.csv
    
    # Importar dados
    docker exec beauty-salon-cassandra cqlsh -e "
      USE beauty_salon;
      COPY $table_name FROM '/tmp/restore_${table_name}.csv' WITH HEADER = TRUE;
    " 2>/dev/null || echo "⚠️  Erro ao restaurar $table_name"
    
    # Limpar arquivo temporário
    docker exec beauty-salon-cassandra rm -f "/tmp/restore_${table_name}.csv"
    
    echo "   ✅ $table_name restaurado"
    echo ""
  fi
done

echo "=========================================="
echo "✅ RESTORE COMPLETO!"
echo "=========================================="
