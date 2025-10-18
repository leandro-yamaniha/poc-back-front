const cassandraClient = require('../config/cassandra');
const Customer = require('../models/Customer');

class CustomerRepository {
  constructor() {
    this.tableName = 'customers';
  }

  async findAll() {
    const query = `SELECT * FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Customer.fromRow(row));
  }

  async findById(id) {
    const query = `SELECT * FROM ${this.tableName} WHERE id = ?`;
    const result = await cassandraClient.execute(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Customer.fromRow(result.rows[0]);
  }

  async findByEmail(email) {
    // Note: This requires a secondary index on email in Cassandra
    const query = `SELECT * FROM ${this.tableName} WHERE email = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [email]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Customer.fromRow(result.rows[0]);
  }

  async searchByName(name) {
    // Note: This is a simple implementation. For production, consider using Elasticsearch or Solr
    const query = `SELECT * FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    
    const customers = result.rows.map(row => Customer.fromRow(row));
    
    // Filter by name containing the search term (case-insensitive)
    return customers.filter(customer => 
      customer.name.toLowerCase().includes(name.toLowerCase())
    );
  }

  async save(customer) {
    const customerData = customer.toPlainObject();
    
    const query = `
      INSERT INTO ${this.tableName} 
      (id, name, email, phone, address, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `;
    
    const params = [
      customerData.id,
      customerData.name,
      customerData.email,
      customerData.phone,
      customerData.address,
      customerData.created_at,
      customerData.updated_at
    ];
    
    await cassandraClient.execute(query, params);
    return customer;
  }

  async update(id, customer) {
    const customerData = customer.toPlainObject();
    
    const query = `
      UPDATE ${this.tableName}
      SET name = ?, email = ?, phone = ?, address = ?, updated_at = ?
      WHERE id = ?
    `;
    
    const params = [
      customerData.name,
      customerData.email,
      customerData.phone,
      customerData.address,
      customerData.updated_at,
      id
    ];
    
    await cassandraClient.execute(query, params);
    return customer;
  }

  async deleteById(id) {
    const query = `DELETE FROM ${this.tableName} WHERE id = ?`;
    await cassandraClient.execute(query, [id]);
    return true;
  }

  async existsById(id) {
    const query = `SELECT id FROM ${this.tableName} WHERE id = ?`;
    const result = await cassandraClient.execute(query, [id]);
    return result.rows.length > 0;
  }

  async existsByEmail(email) {
    const query = `SELECT id FROM ${this.tableName} WHERE email = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [email]);
    return result.rows.length > 0;
  }

  async count() {
    const query = `SELECT COUNT(*) as count FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return parseInt(result.rows[0].count);
  }
}

module.exports = new CustomerRepository();
