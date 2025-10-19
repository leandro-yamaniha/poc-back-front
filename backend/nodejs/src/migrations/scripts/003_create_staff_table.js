/**
 * Migration: Create staff table
 * Version: 003
 * Description: Creates the staff table with all required fields
 */

const { executeWithRetry } = require('../utils/retry');

async function up(client) {
  console.log('📋 Criando tabela staff...');
  
  const createTableQuery = `
    CREATE TABLE IF NOT EXISTS staff (
      id uuid PRIMARY KEY,
      name text,
      email text,
      phone text,
      role text,
      specialties list<text>,
      is_active boolean,
      hire_date timestamp,
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
    // Criar índice secundário para email
    const createEmailIndexQuery = `
      CREATE INDEX IF NOT EXISTS staff_email_idx ON staff (email)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createEmailIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create staff email index' }
    );
    
    // Criar índice secundário para role
    const createRoleIndexQuery = `
      CREATE INDEX IF NOT EXISTS staff_role_idx ON staff (role)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createRoleIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create staff role index' }
    );
    
    // Criar índice secundário para is_active
    const createActiveIndexQuery = `
      CREATE INDEX IF NOT EXISTS staff_active_idx ON staff (is_active)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createActiveIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create staff active index' }
    );
  } catch (error) {
    console.warn('⚠️  Aviso ao criar índices:', error.message);
    // Continua mesmo se falhar, os índices não são críticos
  }
  
  console.log('✅ Tabela staff criada com sucesso');
}

async function down(client) {
  console.log('🔄 Removendo tabela staff...');
  
  // Remove índices primeiro
  await client.execute('DROP INDEX IF EXISTS staff_email_idx');
  await client.execute('DROP INDEX IF EXISTS staff_role_idx');
  await client.execute('DROP INDEX IF EXISTS staff_active_idx');
  
  // Remove tabela
  await client.execute('DROP TABLE IF EXISTS staff');
  
  console.log('✅ Tabela staff removida com sucesso');
}

module.exports = { up, down };
