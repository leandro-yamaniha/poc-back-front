#!/bin/bash

# Script para conectar facilmente ao Cassandra
# Uso: ./scripts/connect-cassandra.sh [keyspace]

set -e

KEYSPACE=${1:-beauty_salon}
CONTAINER_NAME="beauty-salon-cassandra"

echo "🔌 Beauty Salon - Conexão ao Cassandra"
echo "======================================="

# Função para verificar se o container está rodando
check_container() {
    if docker ps --format "table {{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
        return 0
    else
        return 1
    fi
}

# Função para verificar se o Cassandra está pronto
check_cassandra_ready() {
    docker exec $CONTAINER_NAME cqlsh -e "describe keyspaces" > /dev/null 2>&1
}

# Verificar se o container está rodando
if ! check_container; then
    echo "❌ Container '$CONTAINER_NAME' não está rodando"
    echo "🚀 Iniciando container..."
    
    # Tentar iniciar com docker-compose
    if [ -f "docker-compose.yml" ]; then
        docker-compose up -d cassandra
    elif [ -f "docker-compose-fixed.yml" ]; then
        docker-compose -f docker-compose-fixed.yml up -d cassandra
    elif [ -f "docker-compose-custom.yml" ]; then
        docker-compose -f docker-compose-custom.yml up -d cassandra
    else
        echo "❌ Arquivo docker-compose não encontrado"
        exit 1
    fi
    
    echo "⏳ Aguardando container iniciar..."
    sleep 10
fi

echo "✅ Container encontrado: $CONTAINER_NAME"

# Verificar se o Cassandra está pronto
echo "🔄 Verificando se Cassandra está pronto..."
max_attempts=30
attempt=1

while [ $attempt -le $max_attempts ]; do
    if check_cassandra_ready; then
        echo "✅ Cassandra está pronto!"
        break
    fi
    
    echo "⏳ Tentativa $attempt/$max_attempts - Aguardando Cassandra..."
    sleep 5
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ Timeout: Cassandra não ficou pronto"
    echo "📋 Logs do container:"
    docker logs --tail 20 $CONTAINER_NAME
    exit 1
fi

# Verificar se o keyspace existe
echo "🔍 Verificando keyspace '$KEYSPACE'..."
if docker exec $CONTAINER_NAME cqlsh -e "DESCRIBE KEYSPACE $KEYSPACE;" > /dev/null 2>&1; then
    echo "✅ Keyspace '$KEYSPACE' encontrado"
    CONNECT_CMD="cqlsh -k $KEYSPACE"
else
    echo "⚠️  Keyspace '$KEYSPACE' não encontrado, conectando sem keyspace"
    CONNECT_CMD="cqlsh"
fi

# Mostrar informações de conexão
echo ""
echo "📊 Informações de Conexão:"
echo "   Host: localhost"
echo "   Port: 9042"
echo "   Keyspace: $KEYSPACE"
echo ""

# Conectar ao Cassandra
echo "🚀 Conectando ao Cassandra..."
echo "   (Use 'exit' ou Ctrl+D para sair)"
echo ""

docker exec -it $CONTAINER_NAME $CONNECT_CMD
