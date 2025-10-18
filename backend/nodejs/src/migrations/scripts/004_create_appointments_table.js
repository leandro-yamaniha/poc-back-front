/**
 * Migration: Create appointments table
 * Version: 004
 * Description: Creates the appointments table with all required fields and relationships
 */

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
  
  // Criar índice secundário para customer_id
  const createCustomerIndexQuery = `
    CREATE INDEX IF NOT EXISTS appointments_customer_idx ON appointments (customer_id)
  `;
  
  await client.execute(createCustomerIndexQuery);
  
  // Criar índice secundário para staff_id
  const createStaffIndexQuery = `
    CREATE INDEX IF NOT EXISTS appointments_staff_idx ON appointments (staff_id)
  `;
  
  await client.execute(createStaffIndexQuery);
  
  // Criar índice secundário para service_id
  const createServiceIndexQuery = `
    CREATE INDEX IF NOT EXISTS appointments_service_idx ON appointments (service_id)
  `;
  
  await client.execute(createServiceIndexQuery);
  
  // Criar índice secundário para status
  const createStatusIndexQuery = `
    CREATE INDEX IF NOT EXISTS appointments_status_idx ON appointments (status)
  `;
  
  await client.execute(createStatusIndexQuery);
  
  // Criar índice secundário para appointment_date
  const createDateIndexQuery = `
    CREATE INDEX IF NOT EXISTS appointments_date_idx ON appointments (appointment_date)
  `;
  
  await client.execute(createDateIndexQuery);
  
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
