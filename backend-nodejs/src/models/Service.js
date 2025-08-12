const { v4: uuidv4 } = require('uuid');
const { body, validationResult } = require('express-validator');

class Service {
  constructor(data = {}) {
    this.id = data.id || uuidv4();
    this.name = data.name || '';
    this.description = data.description || '';
    this.price = data.price || 0.0;
    this.duration = data.duration || 0;
    this.category = data.category || '';
    this.is_active = data.is_active !== undefined ? data.is_active : true;
    this.created_at = data.created_at || new Date();
    this.updated_at = data.updated_at || new Date();
  }

  // Static method to create validation rules
  static getValidationRules() {
    return [
      body('name')
        .notEmpty()
        .withMessage('Nome do serviço é obrigatório')
        .isLength({ min: 2, max: 100 })
        .withMessage('Nome deve ter entre 2 e 100 caracteres'),
      
      body('description')
        .optional()
        .isLength({ max: 500 })
        .withMessage('Descrição deve ter no máximo 500 caracteres'),
      
      body('price')
        .notEmpty()
        .withMessage('Preço é obrigatório')
        .isFloat({ min: 0 })
        .withMessage('Preço deve ser um número positivo'),
      
      body('duration')
        .notEmpty()
        .withMessage('Duração é obrigatória')
        .isInt({ min: 1 })
        .withMessage('Duração deve ser um número inteiro positivo (em minutos)'),
      
      body('category')
        .notEmpty()
        .withMessage('Categoria é obrigatória')
        .isLength({ min: 2, max: 50 })
        .withMessage('Categoria deve ter entre 2 e 50 caracteres'),
      
      body('is_active')
        .optional()
        .isBoolean()
        .withMessage('Status ativo deve ser verdadeiro ou falso')
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
    if (data.description !== undefined) this.description = data.description;
    if (data.price !== undefined) this.price = parseFloat(data.price);
    if (data.duration !== undefined) this.duration = parseInt(data.duration);
    if (data.category !== undefined) this.category = data.category;
    if (data.is_active !== undefined) this.is_active = data.is_active;
    this.updated_at = new Date();
  }

  // Convert to plain object for database storage
  toPlainObject() {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      price: this.price,
      duration: this.duration,
      category: this.category,
      is_active: this.is_active,
      created_at: this.created_at,
      updated_at: this.updated_at
    };
  }

  // Create from database row
  static fromRow(row) {
    return new Service({
      id: row.id,
      name: row.name,
      description: row.description,
      price: row.price,
      duration: row.duration,
      category: row.category,
      is_active: row.is_active,
      created_at: row.created_at,
      updated_at: row.updated_at
    });
  }
}

module.exports = Service;
