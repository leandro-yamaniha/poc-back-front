# 🗄️ Database - Beauty Salon App

Este diretório contém documentação, scripts e ferramentas para gerenciar o banco de dados Cassandra.

## 📁 Estrutura

```
database/
├── README.md                    # Este arquivo
├── CASSANDRA_COMMANDS.md        # Guia completo de comandos Cassandra
├── scripts/                     # Scripts utilitários
│   ├── check-cassandra.sh       # Verificação completa do Cassandra
│   ├── backup-cassandra.sh      # Backup de todas as tabelas
│   ├── restore-cassandra.sh     # Restore de backup
│   └── query-examples.sh        # Exemplos de queries
└── backups/                     # Diretório para backups (gitignored)
```

## 🚀 Quick Start

### Verificar Status do Cassandra

```bash
./database/scripts/check-cassandra.sh
```

**Output esperado:**
- Status do container
- Status do cluster
- Lista de keyspaces e tabelas
- Contagem de registros
- Uso de disco
- Migrations aplicadas

### Fazer Backup

```bash
./database/scripts/backup-cassandra.sh
```

**Resultado:**
- Cria backup em `database/backups/YYYYMMDD_HHMMSS/`
- Exporta todas as tabelas em formato CSV
- Preserva headers para facilitar restore

### Restaurar Backup

```bash
./database/scripts/restore-cassandra.sh database/backups/20251022_120000/
```

**⚠️ ATENÇÃO:** Isso sobrescreverá os dados existentes!

### Executar Queries de Exemplo

```bash
./database/scripts/query-examples.sh
```

**Mostra:**
- Lista de clientes
- Lista de serviços com preços
- Staff com especialidades
- Serviços ativos
- Contagem de registros

## 📚 Documentação

### [CASSANDRA_COMMANDS.md](CASSANDRA_COMMANDS.md)

Guia completo com:
- ✅ Comandos básicos do Docker
- ✅ Acesso ao CQLsh
- ✅ Queries (SELECT, INSERT, UPDATE, DELETE)
- ✅ Schema management
- ✅ Backup e restore
- ✅ Monitoramento
- ✅ Troubleshooting
- ✅ Scripts prontos

## 🔧 Comandos Rápidos

### Acesso Direto ao CQLsh

```bash
docker exec -it beauty-salon-cassandra cqlsh -k beauty_salon
```

### Ver Todos os Clientes

```bash
docker exec beauty-salon-cassandra cqlsh -e "SELECT * FROM beauty_salon.customers LIMIT 10;"
```

### Contar Registros

```bash
docker exec beauty-salon-cassandra cqlsh -e "SELECT COUNT(*) FROM beauty_salon.customers;"
```

### Ver Logs

```bash
docker logs --tail 50 beauty-salon-cassandra
```

### Status do Cluster

```bash
docker exec beauty-salon-cassandra nodetool status
```

## 📊 Schema do Banco

### Keyspace: `beauty_salon`

**Replication Strategy:** SimpleStrategy  
**Replication Factor:** 1

### Tabelas:

| Tabela | Descrição | PK |
|--------|-----------|-----|
| **customers** | Clientes do salão | `id` (UUID) |
| **services** | Serviços oferecidos | `id` (UUID) |
| **staff** | Funcionários | `id` (UUID) |
| **appointments** | Agendamentos | `id` (UUID) |
| **payments** | Pagamentos | `id` (UUID) |
| **schema_migrations** | Controle de migrations | `version` (TEXT) |

## 🔍 Troubleshooting

### Container não inicia

```bash
# Ver logs de erro
docker logs beauty-salon-cassandra 2>&1 | grep -i error

# Reiniciar
docker-compose restart cassandra
```

### Erro de conexão

```bash
# Verificar se a porta está aberta
nc -zv localhost 9042

# Verificar status do container
docker ps | grep cassandra
```

### Keyspace não existe

```bash
# Listar keyspaces
docker exec beauty-salon-cassandra cqlsh -e "DESCRIBE KEYSPACES;"

# Recriar keyspace (migrations)
docker-compose restart backend-java
```

### Dados inconsistentes

```bash
# Reparar tabela
docker exec beauty-salon-cassandra nodetool repair beauty_salon customers

# Reparar keyspace completo
docker exec beauty-salon-cassandra nodetool repair beauty_salon
```

## 📝 Exemplos de Uso

### Inserir Novo Cliente

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'João Silva', 'joao@email.com', '11999887766', 'Rua Example, 100', toTimestamp(now()), toTimestamp(now()));
"
```

### Buscar Serviços por Categoria

```bash
docker exec beauty-salon-cassandra cqlsh -e "
SELECT name, price FROM beauty_salon.services 
WHERE category = 'Cabelo' ALLOW FILTERING;
"
```

### Atualizar Preço de Serviço

```bash
docker exec beauty-salon-cassandra cqlsh -e "
USE beauty_salon;
UPDATE services 
SET price = 55.00, updated_at = toTimestamp(now())
WHERE id = <uuid-do-serviço>;
"
```

## 🔐 Segurança

Por padrão, o Cassandra está configurado **sem autenticação** para ambiente de desenvolvimento.

### Para habilitar autenticação em produção:

1. Editar `cassandra.yaml`:
```yaml
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer
```

2. Criar usuários:
```sql
CREATE ROLE admin WITH PASSWORD = 'strong_password' AND SUPERUSER = true AND LOGIN = true;
CREATE ROLE app_user WITH PASSWORD = 'app_password' AND LOGIN = true;
GRANT ALL ON KEYSPACE beauty_salon TO app_user;
```

## 📈 Performance

### Monitorar Performance

```bash
# Estatísticas de thread pools
docker exec beauty-salon-cassandra nodetool tpstats

# Estatísticas de compaction
docker exec beauty-salon-cassandra nodetool compactionstats

# Cache statistics
docker exec beauty-salon-cassandra nodetool info | grep -i cache
```

### Otimização

```bash
# Forçar compaction
docker exec beauty-salon-cassandra nodetool compact beauty_salon

# Limpar snapshots antigos
docker exec beauty-salon-cassandra nodetool clearsnapshot
```

## 📚 Recursos Adicionais

- [Cassandra Documentation](https://cassandra.apache.org/doc/latest/)
- [CQL Reference](https://cassandra.apache.org/doc/latest/cql/)
- [DataStax Academy](https://academy.datastax.com/)
- [Cassandra Best Practices](https://cassandra.apache.org/doc/latest/operating/index.html)

## 🆘 Suporte

Para problemas específicos:
1. Verificar logs: `docker logs beauty-salon-cassandra`
2. Executar script de verificação: `./database/scripts/check-cassandra.sh`
3. Consultar [CASSANDRA_COMMANDS.md](CASSANDRA_COMMANDS.md) para comandos específicos

---

**Versão Cassandra:** 4.1  
**Container:** beauty-salon-cassandra  
**Port:** 9042  
**Última atualização:** October 22, 2025
