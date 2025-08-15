#!/bin/bash
set -e

# Script de entrada personalizado para Cassandra com inicialização automática do banco

echo "🚀 Iniciando Cassandra com inicialização automática do banco..."

# Inicia o Cassandra em background
echo "📊 Iniciando servidor Cassandra..."
/usr/local/bin/docker-entrypoint.sh cassandra -f &
CASSANDRA_PID=$!

# Função para aguardar o Cassandra estar pronto
wait_for_cassandra() {
    echo "⏳ Aguardando Cassandra estar pronto..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if cqlsh -e "describe keyspaces" > /dev/null 2>&1; then
            echo "✅ Cassandra está pronto!"
            return 0
        fi
        
        echo "🔄 Tentativa $attempt/$max_attempts - Cassandra ainda não está pronto..."
        sleep 10
        attempt=$((attempt + 1))
    done
    
    echo "❌ Timeout: Cassandra não ficou pronto após $max_attempts tentativas"
    return 1
}

# Função para executar scripts de inicialização
init_database() {
    echo "📋 Executando scripts de inicialização do banco..."
    
    local script_dir="/docker-entrypoint-initdb.d"
    
    if [ -d "$script_dir" ]; then
        echo "📁 Encontrados scripts em $script_dir"
        
        # Executa scripts .cql em ordem alfabética
        for script in "$script_dir"/*.cql; do
            if [ -f "$script" ]; then
                echo "🔧 Executando script: $(basename "$script")"
                if cqlsh -f "$script"; then
                    echo "✅ Script $(basename "$script") executado com sucesso"
                else
                    echo "❌ Erro ao executar script $(basename "$script")"
                    return 1
                fi
            fi
        done
        
        echo "🎉 Todos os scripts de inicialização foram executados com sucesso!"
    else
        echo "⚠️  Diretório de scripts não encontrado: $script_dir"
    fi
}

# Aguarda o Cassandra estar pronto
if wait_for_cassandra; then
    # Executa a inicialização do banco apenas uma vez
    INIT_FLAG="/var/lib/cassandra/.beauty_salon_initialized"
    
    if [ ! -f "$INIT_FLAG" ]; then
        echo "🔄 Primeira execução detectada, inicializando banco de dados..."
        
        if init_database; then
            # Marca como inicializado
            touch "$INIT_FLAG"
            echo "✅ Banco de dados inicializado com sucesso!"
        else
            echo "❌ Falha na inicialização do banco de dados"
            exit 1
        fi
    else
        echo "ℹ️  Banco de dados já foi inicializado anteriormente"
    fi
else
    echo "❌ Falha ao aguardar Cassandra ficar pronto"
    exit 1
fi

# Aguarda o processo do Cassandra
echo "🔄 Cassandra rodando em foreground..."
wait $CASSANDRA_PID
