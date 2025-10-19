/**
 * Migration: Create customers table
 * Version: 001
 * Description: Creates the customers table with all required fields
 */

const { executeWithRetry } = require('../utils/retry');

async function up(client) {
  console.log('📋 Criando tabela customers...');
  
  const createTableQuery = `
    CREATE TABLE IF NOT EXISTS customers (
      id uuid PRIMARY KEY,
      name text,
      email text,
      phone text,
      address text,
      created_at timestamp,
      updated_at timestamp
    )
  `;
  
  await client.execute(createTableQuery);
  
  // Aguardar propagação do schema com delay maior
  console.log('⏳ Aguardando propagação do schema (3 segundos)...');
  await new Promise(resolve => setTimeout(resolve, 3000));
  
  // Criar índice com retry logic
  await executeWithRetry(
    async () => {
      const createEmailIndexQuery = `
        CREATE INDEX IF NOT EXISTS customers_email_idx ON customers (email)
      `;
      await client.execute(createEmailIndexQuery);
    },
    {
      maxRetries: 5,
      initialDelay: 2000,
      operationName: 'Create customers email index'
    }
  );
  
  console.log('✅ Tabela customers criada com sucesso');
}

async function down(client) {
  console.log('🔄 Removendo tabela customers...');
  
  // Remove índices primeiro
  await client.execute('DROP INDEX IF EXISTS customers_email_idx');
  
  // Remove tabela
  await client.execute('DROP TABLE IF EXISTS customers');
  
  console.log('✅ Tabela customers removida com sucesso');
}

module.exports = { up, down };
