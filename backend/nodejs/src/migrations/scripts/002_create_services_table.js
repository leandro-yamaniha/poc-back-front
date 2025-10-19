/**
 * Migration: Create services table
 * Version: 002
 * Description: Creates the services table with all required fields
 */

const { executeWithRetry } = require('../utils/retry');

async function up(client) {
  console.log('📋 Criando tabela services...');
  
  const createTableQuery = `
    CREATE TABLE IF NOT EXISTS services (
      id uuid PRIMARY KEY,
      name text,
      description text,
      price double,
      duration int,
      category text,
      is_active boolean,
      created_at timestamp,
      updated_at timestamp
    )
  `;
  
  await client.execute(createTableQuery);
  
  // Aguardar propagação do schema com delay maior
  console.log('⏳ Aguardando propagação do schema (3 segundos)...');
  await new Promise(resolve => setTimeout(resolve, 3000));
  
  // Criar índices com retry logic
  try {
    // Criar índice secundário para categoria
    const createCategoryIndexQuery = `
      CREATE INDEX IF NOT EXISTS services_category_idx ON services (category)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createCategoryIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create services category index' }
    );
    
    // Criar índice secundário para is_active
    const createActiveIndexQuery = `
      CREATE INDEX IF NOT EXISTS services_active_idx ON services (is_active)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createActiveIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create services active index' }
    );
  } catch (error) {
    console.warn('⚠️  Aviso ao criar índices:', error.message);
    // Continua mesmo se falhar, os índices não são críticos
  }
  
  console.log('✅ Tabela services criada com sucesso');
}

async function down(client) {
  console.log('🔄 Removendo tabela services...');
  
  // Remove índices primeiro
  await client.execute('DROP INDEX IF EXISTS services_category_idx');
  await client.execute('DROP INDEX IF EXISTS services_active_idx');
  
  // Remove tabela
  await client.execute('DROP TABLE IF EXISTS services');
  
  console.log('✅ Tabela services removida com sucesso');
}

module.exports = { up, down };
