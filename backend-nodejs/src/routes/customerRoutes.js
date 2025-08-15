const express = require('express');
const CustomerController = require('../controllers/CustomerController');
const Customer = require('../models/Customer');

const router = express.Router();

// GET /api/customers - Get all customers
router.get('/', CustomerController.getAllCustomers);

// GET /api/customers/count - Get customer count
router.get('/count', CustomerController.getCustomerCount);

// GET /api/customers/search?name=... - Search customers by name
router.get('/search', CustomerController.searchCustomers);

// GET /api/customers/email/:email - Get customer by email
router.get('/email/:email', CustomerController.getCustomerByEmail);

// GET /api/customers/:id - Get customer by ID
router.get('/:id', CustomerController.getCustomerById);

// POST /api/customers - Create new customer
router.post('/', 
  Customer.getValidationRules(),
  Customer.validateRequest,
  CustomerController.createCustomer
);

// PUT /api/customers/:id - Update customer
router.put('/:id',
  Customer.getValidationRules(),
  Customer.validateRequest,
  CustomerController.updateCustomer
);

// DELETE /api/customers/:id - Delete customer
router.delete('/:id', CustomerController.deleteCustomer);

module.exports = router;
