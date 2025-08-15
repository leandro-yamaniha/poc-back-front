/**
 * Migration: Insert sample data
 * Version: 005
 * Description: Inserts sample data for testing and development
 */

const { v4: uuidv4 } = require('uuid');

async function up(client) {
  console.log('📋 Inserindo dados de exemplo...');
  
  // Sample customers only (simplified for demonstration)
  const customers = [
    {
      id: uuidv4(),
      name: 'Maria Silva',
      email: 'maria.silva@email.com',
      phone: '11987654321',
      address: 'Rua das Flores, 123 - São Paulo, SP'
    },
    {
      id: uuidv4(),
      name: 'Ana Santos',
      email: 'ana.santos@email.com',
      phone: '11876543210',
      address: 'Av. Paulista, 456 - São Paulo, SP'
    }
  ];
  
  // Insert customers
  for (const customer of customers) {
    const insertCustomerQuery = `
      INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `;
    
    const now = new Date();
    await client.execute(insertCustomerQuery, [
      customer.id,
      customer.name,
      customer.email,
      customer.phone,
      customer.address,
      now,
      now
    ]);
  }
  
  console.log(`✅ ${customers.length} clientes inseridos`);
  console.log('🎉 Dados de exemplo (simplificados) inseridos com sucesso!');
}

async function down(client) {
  console.log('🔄 Removendo dados de exemplo...');
  
  // Remove in reverse order due to relationships
  await client.execute('TRUNCATE appointments');
  await client.execute('TRUNCATE staff');
  await client.execute('TRUNCATE services');
  await client.execute('TRUNCATE customers');
  
  console.log('✅ Dados de exemplo removidos com sucesso');
}

module.exports = { up, down };
