const { v4: uuidv4 } = require('uuid');
const { body, validationResult } = require('express-validator');

class Customer {
  constructor(data = {}) {
    this.id = data.id || uuidv4();
    this.name = data.name || '';
    this.email = data.email || '';
    this.phone = data.phone || '';
    this.address = data.address || '';
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
      
      body('address')
        .notEmpty()
        .withMessage('Endereço é obrigatório')
        .isLength({ min: 5, max: 200 })
        .withMessage('Endereço deve ter entre 5 e 200 caracteres')
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
    if (data.address !== undefined) this.address = data.address;
    this.updated_at = new Date();
  }

  // Convert to plain object for database storage
  toPlainObject() {
    return {
      id: this.id,
      name: this.name,
      email: this.email,
      phone: this.phone,
      address: this.address,
      created_at: this.created_at,
      updated_at: this.updated_at
    };
  }

  // Create from database row
  static fromRow(row) {
    return new Customer({
      id: row.id,
      name: row.name,
      email: row.email,
      phone: row.phone,
      address: row.address,
      created_at: row.created_at,
      updated_at: row.updated_at
    });
  }
}

module.exports = Customer;
