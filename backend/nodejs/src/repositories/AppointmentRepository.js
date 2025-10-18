const cassandraClient = require('../config/cassandra');
const Appointment = require('../models/Appointment');

class AppointmentRepository {
  constructor() {
    this.tableName = 'appointments';
  }

  async findAll() {
    const query = `SELECT * FROM ${this.tableName}`;
    const result = await cassandraClient.execute(query);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findById(id) {
    const query = `SELECT * FROM ${this.tableName} WHERE id = ?`;
    const result = await cassandraClient.execute(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return Appointment.fromRow(result.rows[0]);
  }

  async findByCustomerId(customerId) {
    const query = `SELECT * FROM ${this.tableName} WHERE customer_id = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [customerId]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findByStaffId(staffId) {
    const query = `SELECT * FROM ${this.tableName} WHERE staff_id = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [staffId]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findByDate(date) {
    // Parse the date string (YYYY-MM-DD format) and create proper Date objects
    const dateStr = date.toString();
    const [year, month, day] = dateStr.split('-').map(num => parseInt(num, 10));
    
    // Create start and end of day (month is 0-indexed in JavaScript Date)
    const startOfDay = new Date(year, month - 1, day, 0, 0, 0, 0);
    const endOfDay = new Date(year, month - 1, day, 23, 59, 59, 999);
    
    return await this.findByDateRange(startOfDay, endOfDay);
  }

  async findByDateAndStaff(date, staffId) {
    const query = `SELECT * FROM ${this.tableName} WHERE appointment_date = ? AND staff_id = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [date, staffId]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findByServiceId(serviceId) {
    const query = `SELECT * FROM ${this.tableName} WHERE service_id = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [serviceId]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findByStatus(status) {
    const query = `SELECT * FROM ${this.tableName} WHERE status = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [status]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findByDateRange(startDate, endDate) {
    const query = `SELECT * FROM ${this.tableName} WHERE appointment_date >= ? AND appointment_date <= ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [startDate, endDate]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findUpcomingAppointments() {
    const now = new Date();
    const query = `SELECT * FROM ${this.tableName} WHERE appointment_date > ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [now]);
    return result.rows.map(row => Appointment.fromRow(row));
  }

  async findTodayAppointments() {
    const today = new Date();
    const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const endOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59);
    
    return await this.findByDateRange(startOfDay, endOfDay);
  }

  async save(appointment) {
    const appointmentData = appointment.toPlainObject();
    
    const query = `
      INSERT INTO ${this.tableName} 
      (id, customer_id, staff_id, service_id, appointment_date, status, notes, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;
    
    const params = [
      appointmentData.id,
      appointmentData.customer_id,
      appointmentData.staff_id,
      appointmentData.service_id,
      appointmentData.appointment_date,
      appointmentData.status,
      appointmentData.notes,
      appointmentData.created_at,
      appointmentData.updated_at
    ];
    
    await cassandraClient.execute(query, params);
    return appointment;
  }

  async update(id, appointment) {
    const appointmentData = appointment.toPlainObject();
    
    const query = `
      UPDATE ${this.tableName}
      SET customer_id = ?, staff_id = ?, service_id = ?, appointment_date = ?, status = ?, notes = ?, updated_at = ?
      WHERE id = ?
    `;
    
    const params = [
      appointmentData.customer_id,
      appointmentData.staff_id,
      appointmentData.service_id,
      appointmentData.appointment_date,
      appointmentData.status,
      appointmentData.notes,
      appointmentData.updated_at,
      id
    ];
    
    await cassandraClient.execute(query, params);
    return appointment;
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

  async countByStatus(status) {
    const query = `SELECT COUNT(*) as count FROM ${this.tableName} WHERE status = ? ALLOW FILTERING`;
    const result = await cassandraClient.execute(query, [status]);
    return parseInt(result.rows[0].count);
  }

  async checkConflict(staffId, appointmentDate, duration, excludeId = null) {
    const startTime = new Date(appointmentDate);
    const endTime = new Date(startTime.getTime() + (duration * 60000)); // duration in minutes
    
    let query = `SELECT * FROM ${this.tableName} WHERE staff_id = ? AND appointment_date >= ? AND appointment_date <= ? ALLOW FILTERING`;
    let params = [staffId, startTime, endTime];
    
    if (excludeId) {
      query += ` AND id != ?`;
      params.push(excludeId);
    }
    
    const result = await cassandraClient.execute(query, params);
    return result.rows.length > 0;
  }
}

module.exports = new AppointmentRepository();
