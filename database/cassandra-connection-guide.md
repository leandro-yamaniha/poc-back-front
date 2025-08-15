# Guia de Conexão ao Cassandra

## 🔧 CLIs e Ferramentas Gratuitas para Cassandra

### 1. CQLSH (Oficial - Linha de Comando)

#### Via Docker (Mais Fácil)
```bash
# Conectar ao container Cassandra
docker exec -it beauty-salon-cassandra cqlsh

# Conectar especificando keyspace
docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon

# Conectar de fora do container
docker run -it --rm --network beauty-salon-app_beauty-salon-network cassandra:4.1 cqlsh cassandra 9042
```

#### Instalação Local
```bash
# macOS
brew install cassandra

# Ubuntu/Debian  
sudo apt-get install cassandra-tools

# CentOS/RHEL
sudo yum install cassandra-tools

# Conectar
cqlsh localhost 9042
cqlsh -k beauty_salon localhost 9042
```

### 2. DBeaver (GUI Gratuito - Recomendado)

**Download**: https://dbeaver.io/download/

**Configuração de Conexão:**
- **Driver**: Cassandra
- **Host**: localhost
- **Port**: 9042
- **Keyspace**: beauty_salon
- **Local Datacenter**: datacenter1

### 3. DataStax Studio (GUI Gratuito)

**Download**: https://downloads.datastax.com/datastax-studio/

**Configuração:**
- **Connection Type**: Cassandra Native
- **Host**: localhost:9042
- **Keyspace**: beauty_salon

### 4. Cassandra Web (Interface Web)

```bash
# Instalar via npm
npm install -g cassandra-web

# Executar
cassandra-web --host localhost --port 9042

# Acessar: http://localhost:3000
```

### 5. TablePlus (GUI - Versão Gratuita Limitada)

**Download**: https://tableplus.com/

**Configuração:**
- **Connection Type**: Cassandra
- **Host**: localhost
- **Port**: 9042
- **Keyspace**: beauty_salon

## 🚀 Scripts Úteis

### Verificar Status do Cassandra
```bash
# Via Docker
docker exec beauty-salon-cassandra nodetool status

# Local
nodetool status
```

### Comandos CQL Básicos
```sql
-- Listar keyspaces
DESCRIBE KEYSPACES;

-- Usar keyspace
USE beauty_salon;

-- Listar tabelas
DESCRIBE TABLES;

-- Ver estrutura de uma tabela
DESCRIBE TABLE customers;

-- Consultar dados
SELECT * FROM customers LIMIT 10;
```

### Script de Conexão Rápida
```bash
#!/bin/bash
# Arquivo: connect-cassandra.sh

echo "🔌 Conectando ao Cassandra..."

# Verifica se o container está rodando
if docker ps | grep -q beauty-salon-cassandra; then
    echo "✅ Container encontrado, conectando..."
    docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon
else
    echo "❌ Container não encontrado. Iniciando..."
    docker-compose up -d cassandra
    echo "⏳ Aguardando Cassandra estar pronto..."
    sleep 30
    docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon
fi
```

## 🐛 Troubleshooting

### Problema: "Connection error: Could not connect to localhost:9042"
```bash
# Verificar se Cassandra está rodando
docker ps | grep cassandra

# Verificar logs
docker logs beauty-salon-cassandra

# Verificar porta
netstat -an | grep 9042
```

### Problema: "Keyspace 'beauty_salon' does not exist"
```bash
# Conectar sem keyspace
docker exec -it beauty-salon-cassandra cqlsh

# Criar keyspace manualmente
CREATE KEYSPACE beauty_salon WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};
```

### Problema: "Unable to connect to any servers"
```bash
# Verificar network
docker network ls

# Conectar usando network específico
docker run -it --rm --network beauty-salon-app_beauty-salon-network cassandra:4.1 cqlsh cassandra 9042
```

## 📊 Comandos de Monitoramento

```bash
# Status do cluster
docker exec beauty-salon-cassandra nodetool status

# Informações do ring
docker exec beauty-salon-cassandra nodetool ring

# Estatísticas
docker exec beauty-salon-cassandra nodetool info

# Verificar keyspaces
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"
```
