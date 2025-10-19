/**
 * Migration: Create appointments table
 * Version: 004
 * Description: Creates the appointments table with all required fields and relationships
 */

const { executeWithRetry } = require('../utils/retry');

async function up(client) {
  console.log('📋 Criando tabela appointments...');
  
  const createTableQuery = `
    CREATE TABLE IF NOT EXISTS appointments (
      id uuid PRIMARY KEY,
      customer_id uuid,
      staff_id uuid,
      service_id uuid,
      appointment_date timestamp,
      status text,
      notes text,
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
    // Criar índice secundário para customer_id
    const createCustomerIndexQuery = `
      CREATE INDEX IF NOT EXISTS appointments_customer_idx ON appointments (customer_id)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createCustomerIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create appointments customer index' }
    );
    
    // Criar índice secundário para staff_id
    const createStaffIndexQuery = `
      CREATE INDEX IF NOT EXISTS appointments_staff_idx ON appointments (staff_id)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createStaffIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create appointments staff index' }
    );
    
    // Criar índice secundário para service_id
    const createServiceIndexQuery = `
      CREATE INDEX IF NOT EXISTS appointments_service_idx ON appointments (service_id)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createServiceIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create appointments service index' }
    );
    
    // Criar índice secundário para status
    const createStatusIndexQuery = `
      CREATE INDEX IF NOT EXISTS appointments_status_idx ON appointments (status)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createStatusIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create appointments status index' }
    );
    
    // Criar índice secundário para appointment_date
    const createDateIndexQuery = `
      CREATE INDEX IF NOT EXISTS appointments_date_idx ON appointments (appointment_date)
    `;
    
    await executeWithRetry(
      async () => await client.execute(createDateIndexQuery),
      { maxRetries: 5, initialDelay: 2000, operationName: 'Create appointments date index' }
    );
  } catch (error) {
    console.warn('⚠️  Aviso ao criar índices:', error.message);
    // Continua mesmo se falhar, os índices não são críticos
  }
  
  console.log('✅ Tabela appointments criada com sucesso');
}

async function down(client) {
  console.log('🔄 Removendo tabela appointments...');
  
  // Remove índices primeiro
  await client.execute('DROP INDEX IF EXISTS appointments_customer_idx');
  await client.execute('DROP INDEX IF EXISTS appointments_staff_idx');
  await client.execute('DROP INDEX IF EXISTS appointments_service_idx');
  await client.execute('DROP INDEX IF EXISTS appointments_status_idx');
  await client.execute('DROP INDEX IF EXISTS appointments_date_idx');
  
  // Remove tabela
  await client.execute('DROP TABLE IF EXISTS appointments');
  
  console.log('✅ Tabela appointments removida com sucesso');
}

module.exports = { up, down };
