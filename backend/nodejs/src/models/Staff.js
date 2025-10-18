const { v4: uuidv4 } = require('uuid');
const { body, validationResult } = require('express-validator');

class Staff {
  constructor(data = {}) {
    this.id = data.id || uuidv4();
    this.name = data.name || '';
    this.email = data.email || '';
    this.phone = data.phone || '';
    this.role = data.role || '';
    this.specialties = data.specialties || [];
    this.is_active = data.is_active !== undefined ? data.is_active : true;
    this.hire_date = data.hire_date || new Date();
    this.created_at = data.created_at || new Date();
    this.updated_at = data.updated_at || new Date();
  }

  // Static method to create validation rules
  static getValidationRules() {
    return [
      body('name')
        .notEmpty()
        .withMessage('Nome é obrigatório')
        .isLength({ min: 2, max: 100 })
        .withMessage('Nome deve ter entre 2 e 100 caracteres'),
      
      body('email')
        .notEmpty()
        .withMessage('Email é obrigatório')
        .isEmail()
        .withMessage('Email deve ter formato válido')
        .normalizeEmail(),
      
      body('phone')
        .notEmpty()
        .withMessage('Telefone é obrigatório')
        .matches(/^\d{10,11}$/)
        .withMessage('Telefone deve ter 10-11 dígitos'),
      
      body('role')
        .notEmpty()
        .withMessage('Cargo é obrigatório')
        .isLength({ min: 2, max: 50 })
        .withMessage('Cargo deve ter entre 2 e 50 caracteres'),
      
      body('specialties')
        .optional()
        .isArray()
        .withMessage('Especialidades deve ser uma lista'),
      
      body('specialties.*')
        .optional()
        .isLength({ min: 2, max: 50 })
        .withMessage('Cada especialidade deve ter entre 2 e 50 caracteres'),
      
      body('is_active')
        .optional()
        .isBoolean()
        .withMessage('Status ativo deve ser verdadeiro ou falso'),
      
      body('hire_date')
        .optional()
        .isISO8601()
        .withMessage('Data de contratação deve estar no formato ISO 8601')
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
    if (data.name !== undefined) this.name = data.name;
    if (data.email !== undefined) this.email = data.email;
    if (data.phone !== undefined) this.phone = data.phone;
    if (data.role !== undefined) this.role = data.role;
    if (data.specialties !== undefined) this.specialties = data.specialties;
    if (data.is_active !== undefined) this.is_active = data.is_active;
    if (data.hire_date !== undefined) this.hire_date = new Date(data.hire_date);
    this.updated_at = new Date();
  }

  // Convert to plain object for database storage
  toPlainObject() {
    return {
      id: this.id,
      name: this.name,
      email: this.email,
      phone: this.phone,
      role: this.role,
      specialties: this.specialties,
      is_active: this.is_active,
      hire_date: this.hire_date,
      created_at: this.created_at,
      updated_at: this.updated_at
    };
  }

  // Create from database row
  static fromRow(row) {
    return new Staff({
      id: row.id,
      name: row.name,
      email: row.email,
      phone: row.phone,
      role: row.role,
      specialties: row.specialties || [],
      is_active: row.is_active,
      hire_date: row.hire_date,
      created_at: row.created_at,
      updated_at: row.updated_at
    });
  }
}

module.exports = Staff;
