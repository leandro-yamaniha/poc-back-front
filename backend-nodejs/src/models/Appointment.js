const { v4: uuidv4 } = require('uuid');
const { body, validationResult } = require('express-validator');

class Appointment {
  constructor(data = {}) {
    this.id = data.id || uuidv4();
    this.customer_id = data.customer_id || '';
    this.staff_id = data.staff_id || '';
    this.service_id = data.service_id || '';
    this.appointment_date = data.appointment_date || new Date();
    this.status = data.status || 'SCHEDULED';
    this.notes = data.notes || '';
    this.created_at = data.created_at || new Date();
    this.updated_at = data.updated_at || new Date();
  }

  // Static method to create validation rules
  static getValidationRules() {
    return [
      body('customer_id')
        .notEmpty()
        .withMessage('ID do cliente é obrigatório')
        .isUUID()
        .withMessage('ID do cliente deve ser um UUID válido'),
      
      body('staff_id')
        .notEmpty()
        .withMessage('ID do funcionário é obrigatório')
        .isUUID()
        .withMessage('ID do funcionário deve ser um UUID válido'),
      
      body('service_id')
        .notEmpty()
        .withMessage('ID do serviço é obrigatório')
        .isUUID()
        .withMessage('ID do serviço deve ser um UUID válido'),
      
      body('appointment_date')
        .notEmpty()
        .withMessage('Data do agendamento é obrigatória')
        .isISO8601()
        .withMessage('Data deve estar no formato ISO 8601')
        .custom((value) => {
          const appointmentDate = new Date(value);
          const now = new Date();
          if (appointmentDate <= now) {
            throw new Error('Data do agendamento deve ser no futuro');
          }
          return true;
        }),
      
      body('status')
        .optional()
        .isIn(['SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW'])
        .withMessage('Status deve ser um dos valores válidos: SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW'),
      
      body('notes')
        .optional()
        .isLength({ max: 500 })
        .withMessage('Observações devem ter no máximo 500 caracteres')
    ];
  }

  // Static method to validate request
  static validateRequest(req, res, next) {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array(),
        timestamp: new Date().toISOString()
      });
    }
    next();
  }

  // Update method
  update(data) {
    if (data.customer_id !== undefined) this.customer_id = data.customer_id;
    if (data.staff_id !== undefined) this.staff_id = data.staff_id;
    if (data.service_id !== undefined) this.service_id = data.service_id;
    if (data.appointment_date !== undefined) this.appointment_date = new Date(data.appointment_date);
    if (data.status !== undefined) this.status = data.status;
    if (data.notes !== undefined) this.notes = data.notes;
    this.updated_at = new Date();
  }

  // Convert to plain object for database storage
  toPlainObject() {
    return {
      id: this.id,
      customer_id: this.customer_id,
      staff_id: this.staff_id,
      service_id: this.service_id,
      appointment_date: this.appointment_date,
      status: this.status,
      notes: this.notes,
      created_at: this.created_at,
      updated_at: this.updated_at
    };
  }

  // Create from database row
  static fromRow(row) {
    return new Appointment({
      id: row.id,
      customer_id: row.customer_id,
      staff_id: row.staff_id,
      service_id: row.service_id,
      appointment_date: row.appointment_date,
      status: row.status,
      notes: row.notes,
      created_at: row.created_at,
      updated_at: row.updated_at
    });
  }

  // Static method to get valid status values
  static getValidStatuses() {
    return ['SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW'];
  }
}

module.exports = Appointment;
