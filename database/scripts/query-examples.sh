#!/bin/bash
# Exemplos de Queries Comuns no Cassandra
# Usage: ./database/scripts/query-examples.sh

echo "=========================================="
echo "📊 EXEMPLOS DE QUERIES - CASSANDRA"
echo "=========================================="
echo ""

echo "1️⃣  Listar todos os clientes:"
echo "----------------------------------------"
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, email, phone FROM customers LIMIT 10;
"
echo ""

echo "2️⃣  Listar todos os serviços com preços:"
echo "----------------------------------------"
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, price, category, is_active FROM services;
"
echo ""

echo "3️⃣  Listar staff com especialidades:"
echo "----------------------------------------"
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, role, specialties FROM staff;
"
echo ""

echo "4️⃣  Buscar serviços ativos:"
echo "----------------------------------------"
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, price FROM services WHERE is_active = true ALLOW FILTERING;
"
echo ""

echo "5️⃣  Contar total de registros por tabela:"
echo "----------------------------------------"
echo "Customers: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.customers;" | grep -oE '[0-9]+' | head -1)"
echo "Services: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.services;" | grep -oE '[0-9]+' | head -1)"
echo "Staff: $(docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.staff;" | grep -oE '[0-9]+' | head -1)"
echo ""

echo "=========================================="
echo "✅ Queries executadas com sucesso!"
echo "=========================================="
