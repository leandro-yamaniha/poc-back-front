/**
 * Migration: Create staff table
 * Version: 003
 * Description: Creates the staff table with all required fields
 */

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
  
  // Criar índice secundário para email
  const createEmailIndexQuery = `
    CREATE INDEX IF NOT EXISTS staff_email_idx ON staff (email)
  `;
  
  await client.execute(createEmailIndexQuery);
  
  // Criar índice secundário para role
  const createRoleIndexQuery = `
    CREATE INDEX IF NOT EXISTS staff_role_idx ON staff (role)
  `;
  
  await client.execute(createRoleIndexQuery);
  
  // Criar índice secundário para is_active
  const createActiveIndexQuery = `
    CREATE INDEX IF NOT EXISTS staff_active_idx ON staff (is_active)
  `;
  
  await client.execute(createActiveIndexQuery);
  
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
