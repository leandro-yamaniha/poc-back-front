const cassandraClient = require('../config/cassandra');
const Service = require('../models/Service');

class ServiceRepository {
  constructor() {
    this.tableName = 'services';
  }

  async findAll() {
    const query = `SELECT * FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Service.fromRow(row));
  }

  async findById(id) {
    const query = `SELECT * FROM ${this.tableName} WHERE id = ?`;
    const result = await cassandraClient.execute(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Service.fromRow(result.rows[0]);
  }

  async findByCategory(category) {
    const query = `SELECT * FROM ${this.tableName} WHERE category = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [category]);
    return result.rows.map(row => Service.fromRow(row));
  }

  async findActiveServices() {
    const query = `SELECT * FROM ${this.tableName} WHERE is_active = true ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Service.fromRow(row));
  }

  async findActiveByCategory(category) {
    const query = `SELECT * FROM ${this.tableName} WHERE category = ? AND is_active = true ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [category]);
    return result.rows.map(row => Service.fromRow(row));
  }

  async searchByName(name) {
    const query = `SELECT * FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    
    const services = result.rows.map(row => Service.fromRow(row));
    
    return services.filter(service => 
      service.name.toLowerCase().includes(name.toLowerCase())
    );
  }

  async save(service) {
    const serviceData = service.toPlainObject();
    
    const query = `
      INSERT INTO ${this.tableName} 
      (id, name, description, price, duration, category, is_active, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;
    
    const params = [
      serviceData.id,
      serviceData.name,
      serviceData.description,
      serviceData.price,
      serviceData.duration,
      serviceData.category,
      serviceData.is_active,
      serviceData.created_at,
      serviceData.updated_at
    ];
    
    await cassandraClient.execute(query, params);
    return service;
  }

  async update(id, service) {
    const serviceData = service.toPlainObject();
    
    const query = `
      UPDATE ${this.tableName}
      SET name = ?, description = ?, price = ?, duration = ?, category = ?, is_active = ?, updated_at = ?
      WHERE id = ?
    `;
    
    const params = [
      serviceData.name,
      serviceData.description,
      serviceData.price,
      serviceData.duration,
      serviceData.category,
      serviceData.is_active,
      serviceData.updated_at,
      id
    ];
    
    await cassandraClient.execute(query, params);
    return service;
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

  async count() {
    const query = `SELECT COUNT(*) as count FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return parseInt(result.rows[0].count);
  }

  async getCategories() {
    const query = `SELECT DISTINCT category FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => row.category);
  }
}

module.exports = new ServiceRepository();
