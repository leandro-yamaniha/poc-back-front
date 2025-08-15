# 🗄️ Database Migrations Guide

## Overview

The Node.js Beauty Salon backend includes an automated database migration system that creates and manages Cassandra schema and data during application startup.

## Features

- ✅ **Automatic Execution**: Runs on application startup
- ✅ **Version Control**: Tracks executed migrations
- ✅ **Rollback Support**: Optional rollback functionality
- ✅ **Idempotent**: Safe to run multiple times
- ✅ **Logging**: Detailed execution logs

## Migration Structure

```
backend-nodejs/src/migrations/
├── MigrationManager.js          # Migration orchestrator
└── scripts/                     # Migration scripts
    ├── 001_create_customers_table.js
    ├── 002_create_services_table.js
    ├── 003_create_staff_table.js
    ├── 004_create_appointments_table.js
    └── 005_insert_sample_data.js
```

## How It Works

### 1. Startup Integration

The migration system is integrated into the application startup process:

```javascript
// In src/app.js
const migrationManager = new MigrationManager(cassandraClient);
await migrationManager.runMigrations();
```

### 2. Migration Tracking

- Creates a `migrations` table to track executed migrations
- Stores version, name, and execution timestamp
- Prevents duplicate execution

### 3. Migration Scripts

Each migration script exports `up` and `down` functions:

```javascript
// Example: 001_create_customers_table.js
async function up(client) {
  // Create table and indexes
  await client.execute(`CREATE TABLE IF NOT EXISTS customers (...)`);
}

async function down(client) {
  // Rollback changes
  await client.execute('DROP TABLE IF EXISTS customers');
}

module.exports = { up, down };
```

## Current Migrations

| Version | Name | Description |
|---------|------|-------------|
| 001 | create_customers_table | Creates customers table with email index |
| 002 | create_services_table | Creates services table with category/active indexes |
| 003 | create_staff_table | Creates staff table with email/role/active indexes |
| 004 | create_appointments_table | Creates appointments table with relationship indexes |
| 005 | insert_sample_data | Inserts sample data for development/testing |

## Database Schema

### Tables Created

1. **customers**
   - `id` (uuid, PRIMARY KEY)
   - `name`, `email`, `phone`, `address` (text)
   - `created_at`, `updated_at` (timestamp)
   - Index: `customers_email_idx`

2. **services**
   - `id` (uuid, PRIMARY KEY)
   - `name`, `description`, `category` (text)
   - `price` (decimal), `duration` (int)
   - `is_active` (boolean)
   - `created_at`, `updated_at` (timestamp)
   - Indexes: `services_category_idx`, `services_active_idx`

3. **staff**
   - `id` (uuid, PRIMARY KEY)
   - `name`, `email`, `phone`, `role` (text)
   - `specialties` (list<text>)
   - `is_active` (boolean)
   - `hire_date`, `created_at`, `updated_at` (timestamp)
   - Indexes: `staff_email_idx`, `staff_role_idx`, `staff_active_idx`

4. **appointments**
   - `id` (uuid, PRIMARY KEY)
   - `customer_id`, `staff_id`, `service_id` (uuid)
   - `appointment_date` (timestamp)
   - `status`, `notes` (text)
   - `created_at`, `updated_at` (timestamp)
   - Indexes: Multiple indexes for relationships and queries

## Sample Data

The migration system includes sample data for development:

- **3 customers**: Maria Silva, Ana Santos, Carla Oliveira
- **5 services**: Hair cuts, manicure, pedicure, facial cleaning
- **3 staff members**: João (hairdresser), Fernanda (esthetician), Paula (manicurist)
- **2 appointments**: Scheduled sample appointments

## Testing Migrations

### Manual Testing

Run the migration test script:

```bash
cd backend-nodejs
node test-migrations.js
```

This will:
- Connect to Cassandra
- Check migration status
- Run migrations
- Verify table creation
- Display sample data

### Application Testing

Start the application and check logs:

```bash
cd backend-nodejs
npm start
```

Look for migration logs:
```
🔄 Running database migrations...
📋 Criando tabela customers...
✅ Tabela customers criada com sucesso
...
✅ Database migrations completed
```

## Troubleshooting

### Common Issues

1. **Cassandra Connection Failed**
   ```
   ❌ Failed to connect to Cassandra
   ```
   - Ensure Cassandra is running
   - Check connection settings in `.env`
   - Verify keyspace exists

2. **Migration Already Executed**
   ```
   ⏭️ Migration 001_create_customers_table already executed, skipping
   ```
   - This is normal behavior
   - Migrations are idempotent

3. **Table Already Exists**
   ```
   ConfigurationError: Table already exists
   ```
   - Use `CREATE TABLE IF NOT EXISTS`
   - Check migration script syntax

### Debugging

Enable detailed logging:

```javascript
// In migration script
console.log('🔍 Executing query:', query);
```

Check migration status:

```cql
SELECT * FROM migrations ORDER BY executed_at;
```

## Adding New Migrations

### 1. Create Migration Script

```bash
# Create new migration file
touch src/migrations/scripts/006_add_new_feature.js
```

### 2. Implement Migration

```javascript
/**
 * Migration: Add new feature
 * Version: 006
 * Description: Adds new feature to the system
 */

async function up(client) {
  console.log('📋 Adding new feature...');
  
  const query = `
    ALTER TABLE customers 
    ADD loyalty_points int
  `;
  
  await client.execute(query);
  console.log('✅ New feature added');
}

async function down(client) {
  console.log('🔄 Removing new feature...');
  
  const query = `
    ALTER TABLE customers 
    DROP loyalty_points
  `;
  
  await client.execute(query);
  console.log('✅ New feature removed');
}

module.exports = { up, down };
```

### 3. Test Migration

```bash
node test-migrations.js
```

### 4. Restart Application

The new migration will run automatically on next startup.

## Best Practices

1. **Naming Convention**: Use `XXX_descriptive_name.js` format
2. **Idempotent Operations**: Use `IF NOT EXISTS` / `IF EXISTS`
3. **Rollback Support**: Always implement `down` function
4. **Testing**: Test migrations before deployment
5. **Documentation**: Document schema changes
6. **Backup**: Backup data before major migrations

## Production Considerations

1. **Backup Strategy**: Always backup before migrations
2. **Rollback Plan**: Test rollback procedures
3. **Monitoring**: Monitor migration execution
4. **Staging**: Test migrations in staging environment
5. **Downtime**: Plan for potential downtime during migrations

## Environment Variables

Configure migration behavior:

```env
# Migration settings
MIGRATION_TIMEOUT=30000
MIGRATION_RETRY_COUNT=3
SKIP_SAMPLE_DATA=false
```

## Conclusion

The migration system provides a robust, automated way to manage database schema and data changes. It ensures consistency across environments and simplifies deployment processes.

For questions or issues, refer to the troubleshooting section or check the application logs.
