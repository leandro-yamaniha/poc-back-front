#!/bin/bash
# Script de Verificação Completa do Cassandra
# Usage: ./database/scripts/check-cassandra.sh

set -e

echo "=========================================="
echo "🔍 VERIFICAÇÃO COMPLETA DO CASSANDRA"
echo "=========================================="
echo ""

echo "=== 📦 Status do Container ==="
docker ps | grep cassandra || echo "❌ Container não está rodando!"
echo ""

echo "=== 🌐 Status do Cluster ==="
docker exec beauty-salon-cassandra nodetool status
echo ""

echo "=== 🗄️  Keyspaces Disponíveis ==="
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"
echo ""

echo "=== 📋 Tabelas do beauty_salon ==="
docker exec beauty-salon-cassandra cqlsh -e "USE beauty_salon; DESCRIBE TABLES;"
echo ""

echo "=== 📊 Contagem de Registros ==="
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT COUNT(*) AS total FROM customers;
" | grep -A 3 "total"
echo "Customers: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.customers;" | grep -oE '[0-9]+')"

echo "Services: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.services;" | grep -oE '[0-9]+')"

echo "Staff: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.staff;" | grep -oE '[0-9]+')"
echo ""

echo "=== 💾 Uso de Disco ==="
docker exec beauty-salon-cassandra nodetool tablestats beauty_salon | grep -E "(Space|Count)" | head -10
echo ""

echo "=== 📝 Migrations Aplicadas ==="
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT version, name FROM schema_migrations;
"
echo ""

echo "=== ✅ Verificação Concluída ==="
echo "=========================================="
