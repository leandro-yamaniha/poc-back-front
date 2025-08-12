const express = require('express');
const ServiceController = require('../controllers/ServiceController');
const Service = require('../models/Service');

const router = express.Router();

// GET /api/services - Get all services
router.get('/', ServiceController.getAllServices);

// GET /api/services/active - Get active services only
router.get('/active', ServiceController.getActiveServices);

// GET /api/services/count - Get service count
router.get('/count', ServiceController.getServiceCount);

// GET /api/services/categories - Get all categories
router.get('/categories', ServiceController.getCategories);

// GET /api/services/search?name=... - Search services by name
router.get('/search', ServiceController.searchServices);

// GET /api/services/category/:category - Get services by category
router.get('/category/:category', ServiceController.getServicesByCategory);

// GET /api/services/:id - Get service by ID
router.get('/:id', ServiceController.getServiceById);

// POST /api/services - Create new service
router.post('/', 
  Service.getValidationRules(),
  Service.validateRequest,
  ServiceController.createService
);

// PUT /api/services/:id - Update service
router.put('/:id',
  Service.getValidationRules(),
  Service.validateRequest,
  ServiceController.updateService
);

// DELETE /api/services/:id - Delete service
router.delete('/:id', ServiceController.deleteService);

module.exports = router;
