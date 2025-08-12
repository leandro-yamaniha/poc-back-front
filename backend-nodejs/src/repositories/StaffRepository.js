const cassandraClient = require('../config/cassandra');
const Staff = require('../models/Staff');

class StaffRepository {
  constructor() {
    this.tableName = 'staff';
  }

  async findAll() {
    const query = `SELECT * FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Staff.fromRow(row));
  }

  async findById(id) {
    const query = `SELECT * FROM ${this.tableName} WHERE id = ?`;
    const result = await cassandraClient.execute(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Staff.fromRow(result.rows[0]);
  }

  async findByEmail(email) {
    const query = `SELECT * FROM ${this.tableName} WHERE email = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [email]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Staff.fromRow(result.rows[0]);
  }

  async findByRole(role) {
    const query = `SELECT * FROM ${this.tableName} WHERE role = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [role]);
    return result.rows.map(row => Staff.fromRow(row));
  }

  async findActiveStaff() {
    const query = `SELECT * FROM ${this.tableName} WHERE is_active = true ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Staff.fromRow(row));
  }

  async findBySpecialty(specialty) {
    const query = `SELECT * FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    
    const staff = result.rows.map(row => Staff.fromRow(row));
    
    return staff.filter(member => 
      member.specialties && member.specialties.includes(specialty)
    );
  }

  async searchByName(name) {
    const query = `SELECT * FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    
    const staff = result.rows.map(row => Staff.fromRow(row));
    
    return staff.filter(member => 
      member.name.toLowerCase().includes(name.toLowerCase())
    );
  }

  async save(staff) {
    const staffData = staff.toPlainObject();
    
    const query = `
      INSERT INTO ${this.tableName} 
      (id, name, email, phone, role, specialties, is_active, hire_date, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;
    
    const params = [
      staffData.id,
      staffData.name,
      staffData.email,
      staffData.phone,
      staffData.role,
      staffData.specialties,
      staffData.is_active,
      staffData.hire_date,
      staffData.created_at,
      staffData.updated_at
    ];
    
    await cassandraClient.execute(query, params);
    return staff;
  }

  async update(id, staff) {
    const staffData = staff.toPlainObject();
    
    const query = `
      UPDATE ${this.tableName}
      SET name = ?, email = ?, phone = ?, role = ?, specialties = ?, is_active = ?, hire_date = ?, updated_at = ?
      WHERE id = ?
    `;
    
    const params = [
      staffData.name,
      staffData.email,
      staffData.phone,
      staffData.role,
      staffData.specialties,
      staffData.is_active,
      staffData.hire_date,
      staffData.updated_at,
      id
    ];
    
    await cassandraClient.execute(query, params);
    return staff;
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

  async getRoles() {
    const query = `SELECT DISTINCT role FROM ${this.tableName} ALLOW FILTERING`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => row.role);
  }
}

module.exports = new StaffRepository();
