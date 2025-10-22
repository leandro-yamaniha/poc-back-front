# 🗄️ Guia de Comandos Cassandra via Docker

## 📋 Índice

- [Comandos Básicos](#comandos-básicos)
- [Acesso ao CQLsh](#acesso-ao-cqlsh)
- [Consultas (SELECT)](#consultas-select)
- [Inserção de Dados](#inserção-de-dados)
- [Atualização de Dados](#atualização-de-dados)
- [Exclusão de Dados](#exclusão-de-dados)
- [Schema Management](#schema-management)
- [Backup e Restore](#backup-e-restore)
- [Monitoramento](#monitoramento)
- [Troubleshooting](#troubleshooting)

---

## 🚀 Comandos Básicos

### Verificar Status do Container

```bash
# Verificar se Cassandra está rodando
docker ps | grep cassandra

# Ver logs em tempo real
docker logs -f beauty-salon-cassandra

# Ver últimas 50 linhas de log
docker logs --tail 50 beauty-salon-cassandra

# Verificar health status
docker inspect beauty-salon-cassandra | grep -A 5 Health
```

### Iniciar/Parar Cassandra

```bash
# Iniciar apenas Cassandra
docker-compose up -d cassandra

# Parar Cassandra
docker-compose stop cassandra

# Reiniciar Cassandra
docker-compose restart cassandra

# Remover container e volumes (CUIDADO: apaga dados!)
docker-compose down -v cassandra
```

---

## 💻 Acesso ao CQLsh

### Acesso Interativo

```bash
# Entrar no shell Cassandra
docker exec -it beauty-salon-cassandra cqlsh

# Entrar direto no keyspace beauty_salon
docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon

# Executar comando único
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"
```

### Executar Arquivo CQL

```bash
# Executar arquivo .cql
docker exec -i beauty-salon-cassandra cqlsh < /path/to/script.cql

# Executar arquivo dentro do container
docker exec beauty-salon-cassandra cqlsh -f /path/to/script.cql
```

---

## 🔍 Consultas (SELECT)

### Listar Todos os Clientes

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT * FROM customers LIMIT 10;
"
```

### Buscar Cliente por ID

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT * FROM customers WHERE id = 42ae337f-01de-4654-9223-00ad8d04fd4f;
"
```

### Listar Todos os Serviços

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, price, category, is_active FROM services;
"
```

### Listar Staff com Especialidades

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT name, email, role, specialties FROM staff;
"
```

### Contar Registros

```bash
# Contar clientes
docker exec beauty-salon-cassandra cqlsh -e "
SELECT COUNT(*) FROM beauty_salon.customers;
"

# Contar serviços ativos
docker exec beauty-salon-cassandra cqlsh -e "
SELECT COUNT(*) FROM beauty_salon.services WHERE is_active = true ALLOW FILTERING;
"

# Contar por tabela
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT COUNT(*) AS total_customers FROM customers;
SELECT COUNT(*) AS total_services FROM services;
SELECT COUNT(*) AS total_staff FROM staff;
"
```

### Filtros e Ordenação

```bash
# Buscar serviços por categoria (requer ALLOW FILTERING)
docker exec beauty-salon-cassandra cqlsh -e "
SELECT name, price FROM beauty_salon.services 
WHERE category = 'Cabelo' ALLOW FILTERING;
"

# Buscar staff por role
docker exec beauty-salon-cassandra cqlsh -e "
SELECT name, email, role FROM beauty_salon.staff 
WHERE role = 'Cabeleireira' ALLOW FILTERING;
"
```

---

## ➕ Inserção de Dados

### Inserir Novo Cliente

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
VALUES (
  uuid(), 
  'João Silva', 
  'joao.silva@email.com', 
  '11999887766',
  'Rua Example, 100',
  toTimestamp(now()),
  toTimestamp(now())
);
"
```

### Inserir Novo Serviço

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (
  uuid(),
  'Massagem Relaxante',
  'Massagem com óleos essenciais',
  60,
  120.00,
  'Estética',
  true,
  toTimestamp(now()),
  toTimestamp(now())
);
"
```

### Inserir Novo Staff (com lista de especialidades)

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
INSERT INTO staff (id, name, email, phone, role, specialties, is_active, hire_date, created_at, updated_at)
VALUES (
  uuid(),
  'Paula Santos',
  'paula@salao.com',
  '11988776655',
  'Massagista',
  ['Massagem', 'Reflexologia', 'Drenagem'],
  true,
  toTimestamp(now()),
  toTimestamp(now()),
  toTimestamp(now())
);
"
```

### Inserir Múltiplos Registros

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;

BEGIN BATCH
  INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
  VALUES (uuid(), 'Cliente 1', 'c1@email.com', '11111111111', 'Rua 1', toTimestamp(now()), toTimestamp(now()));
  
  INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
  VALUES (uuid(), 'Cliente 2', 'c2@email.com', '11222222222', 'Rua 2', toTimestamp(now()), toTimestamp(now()));
  
  INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
  VALUES (uuid(), 'Cliente 3', 'c3@email.com', '11333333333', 'Rua 3', toTimestamp(now()), toTimestamp(now()));
APPLY BATCH;
"
```

---

## 🔄 Atualização de Dados

### Atualizar Cliente

```bash
# Atualizar telefone e endereço
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
UPDATE customers 
SET phone = '11888889999', 
    address = 'Novo Endereço, 200',
    updated_at = toTimestamp(now())
WHERE id = 42ae337f-01de-4654-9223-00ad8d04fd4f;
"
```

### Atualizar Preço de Serviço

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
UPDATE services 
SET price = 55.00,
    updated_at = toTimestamp(now())
WHERE id = 988b15eb-d593-489c-9621-c75d03213078;
"
```

### Atualizar Lista (Especialidades do Staff)

```bash
# Adicionar especialidade
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
UPDATE staff 
SET specialties = specialties + ['Nova Especialidade']
WHERE id = 974cadf5-1554-43a6-86d2-e9ac95f1ff10;
"

# Remover especialidade
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
UPDATE staff 
SET specialties = specialties - ['Especialidade Antiga']
WHERE id = 974cadf5-1554-43a6-86d2-e9ac95f1ff10;
"
```

---

## 🗑️ Exclusão de Dados

### Deletar Cliente por ID

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
DELETE FROM customers WHERE id = 383fb3dd-cab7-4add-b7ae-90df1b63bd2b;
"
```

### Deletar Múltiplos Registros

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;

BEGIN BATCH
  DELETE FROM customers WHERE id = uuid1;
  DELETE FROM customers WHERE id = uuid2;
  DELETE FROM customers WHERE id = uuid3;
APPLY BATCH;
"
```

### Truncar Tabela (CUIDADO!)

```bash
# Remove TODOS os dados da tabela
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
TRUNCATE customers;
"
```

---

## 🏗️ Schema Management

### Listar Keyspaces

```bash
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"
```

### Descrever Keyspace

```bash
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACE beauty_salon;"
```

### Listar Tabelas

```bash
docker exec beauty-salon-cassandra cqlsh -e "USE beauty_salon; DESCRIBE TABLES;"
```

### Descrever Tabela

```bash
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE TABLE beauty_salon.customers;"
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE TABLE beauty_salon.staff;"
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE TABLE beauty_salon.services;"
```

### Criar Índice

```bash
# Criar índice no email do cliente
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
CREATE INDEX IF NOT EXISTS customers_email_idx ON customers (email);
"

# Criar índice no role do staff
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
CREATE INDEX IF NOT EXISTS staff_role_idx ON staff (role);
"
```

### Listar Índices

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT * FROM system_schema.indexes WHERE keyspace_name = 'beauty_salon';
"
```

### Remover Índice

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
DROP INDEX IF EXISTS customers_email_idx;
"
```

---

## 💾 Backup e Restore

### Backup de Dados

```bash
# Exportar todos os dados de uma tabela
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
COPY customers TO '/tmp/customers_backup.csv';
"

# Copiar arquivo do container para host
docker cp beauty-salon-cassandra:/tmp/customers_backup.csv ./database/backups/
```

### Restore de Dados

```bash
# Copiar arquivo do host para container
docker cp ./database/backups/customers_backup.csv beauty-salon-cassandra:/tmp/

# Importar dados
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
COPY customers FROM '/tmp/customers_backup.csv';
"
```

### Snapshot (Backup Completo)

```bash
# Criar snapshot
docker exec beauty-salon-cassandra nodetool snapshot beauty_salon

# Listar snapshots
docker exec beauty-salon-cassandra nodetool listsnapshots

# Limpar snapshots antigos
docker exec beauty-salon-cassandra nodetool clearsnapshot
```

---

## 📊 Monitoramento

### Status do Cluster

```bash
# Status geral
docker exec beauty-salon-cassandra nodetool status

# Info do node
docker exec beauty-salon-cassandra nodetool info

# Espaço em disco
docker exec beauty-salon-cassandra nodetool tablestats beauty_salon
```

### Estatísticas de Tabelas

```bash
# Estatísticas gerais
docker exec beauty-salon-cassandra nodetool tablestats beauty_salon

# Estatísticas de uma tabela específica
docker exec beauty-salon-cassandra nodetool tablestats beauty_salon.customers
```

### Performance e Compaction

```bash
# Status de compaction
docker exec beauty-salon-cassandra nodetool compactionstats

# Forçar compaction
docker exec beauty-salon-cassandra nodetool compact beauty_salon customers

# Estatísticas de thread pools
docker exec beauty-salon-cassandra nodetool tpstats
```

### Cache Statistics

```bash
# Estatísticas de cache
docker exec beauty-salon-cassandra nodetool info | grep -i cache
```

---

## 🐛 Troubleshooting

### Verificar Conectividade

```bash
# Testar conexão
nc -zv localhost 9042

# Testar dentro do container
docker exec beauty-salon-cassandra nodetool status
```

### Verificar Logs de Erro

```bash
# Logs do Cassandra
docker logs beauty-salon-cassandra 2>&1 | grep -i error

# Logs do sistema
docker exec beauty-salon-cassandra tail -f /var/log/cassandra/system.log
```

### Reparar Tabela

```bash
# Reparar tabela específica
docker exec beauty-salon-cassandra nodetool repair beauty_salon customers

# Reparar keyspace completo
docker exec beauty-salon-cassandra nodetool repair beauty_salon
```

### Limpar Cache

```bash
# Limpar cache de chaves
docker exec beauty-salon-cassandra nodetool clearsnapshot

# Invalidar cache
docker exec beauty-salon-cassandra nodetool invalidatekeycache
```

### Verificar Migrations

```bash
# Ver histórico de migrations
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT version, name, executed_at FROM schema_migrations;
"
```

---

## 🔧 Comandos Úteis Adicionais

### Ver Versão do Cassandra

```bash
docker exec beauty-salon-cassandra cqlsh --version
docker exec beauty-salon-cassandra nodetool version
```

### Verificar Configuração

```bash
# Ver configuração do cassandra.yaml
docker exec beauty-salon-cassandra cat /etc/cassandra/cassandra.yaml | grep -A 5 "cluster_name"
```

### Estatísticas de Uso

```bash
# Uso de disco por tabela
docker exec beauty-salon-cassandra du -sh /var/lib/cassandra/data/beauty_salon/*

# Uso total de disco
docker exec beauty-salon-cassandra df -h
```

### Verificar Replication

```bash
docker exec beauty-salon-cassandra cqlsh -e "
SELECT * FROM system_schema.keyspaces WHERE keyspace_name = 'beauty_salon';
"
```

---

## 📝 Scripts Prontos

### Script de Verificação Completa

```bash
#!/bin/bash
# database/scripts/check-cassandra.sh

echo "=== Status do Container ==="
docker ps | grep cassandra

echo -e "\n=== Status do Cluster ==="
docker exec beauty-salon-cassandra nodetool status

echo -e "\n=== Keyspaces ==="
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"

echo -e "\n=== Tabelas do beauty_salon ==="
docker exec beauty-salon-cassandra cqlsh -e "USE beauty_salon; DESCRIBE TABLES;"

echo -e "\n=== Contagem de Registros ==="
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
SELECT COUNT(*) AS customers FROM customers;
SELECT COUNT(*) AS services FROM services;
SELECT COUNT(*) AS staff FROM staff;
"

echo -e "\n=== Uso de Disco ==="
docker exec beauty-salon-cassandra nodetool tablestats beauty_salon | grep -E "(Space|Count)"
```

### Script de Backup Completo

```bash
#!/bin/bash
# database/scripts/backup-cassandra.sh

BACKUP_DIR="./database/backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "Criando backup em: $BACKUP_DIR"

# Backup de cada tabela
for table in customers services staff appointments payments; do
  echo "Backup de $table..."
  docker exec beauty-salon-cassandra cqlsh -e "
    USE beauty_salon;
    COPY $table TO '/tmp/${table}_backup.csv';
  "
  docker cp beauty-salon-cassandra:/tmp/${table}_backup.csv "$BACKUP_DIR/"
done

echo "Backup completo em: $BACKUP_DIR"
```

---

## 🎯 Comandos Mais Usados (Quick Reference)

```bash
# Acesso rápido
docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon

# Ver todos os clientes
docker exec beauty-salon-cassandra cqlsh -e "SELECT * FROM beauty_salon.customers LIMIT 10;"

# Ver todos os serviços
docker exec beauty-salon-cassandra cqlsh -e "SELECT * FROM beauty_salon.services;"

# Ver todo o staff
docker exec beauty-salon-cassandra cqlsh -e "SELECT * FROM beauty_salon.staff;"

# Contar registros
docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.customers;"

# Status do cluster
docker exec beauty-salon-cassandra nodetool status

# Ver logs
docker logs --tail 50 beauty-salon-cassandra
```

---

## 📚 Recursos Adicionais

- [Documentação Oficial Cassandra](https://cassandra.apache.org/doc/latest/)
- [CQL Reference](https://cassandra.apache.org/doc/latest/cql/)
- [Nodetool Commands](https://cassandra.apache.org/doc/latest/tools/nodetool/)

---

**Última atualização:** October 22, 2025  
**Versão Cassandra:** 4.1  
**Container:** beauty-salon-cassandra
