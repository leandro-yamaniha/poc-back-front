const express = require('express');
const StaffController = require('../controllers/StaffController');
const Staff = require('../models/Staff');

const router = express.Router();

// GET /api/staff - Get all staff
router.get('/', StaffController.getAllStaff);

// GET /api/staff/active - Get active staff only
router.get('/active', StaffController.getActiveStaff);

// GET /api/staff/count - Get staff count
router.get('/count', StaffController.getStaffCount);

// GET /api/staff/roles - Get all roles
router.get('/roles', StaffController.getRoles);

// GET /api/staff/search?name=... - Search staff by name
router.get('/search', StaffController.searchStaff);

// GET /api/staff/email/:email - Get staff by email
router.get('/email/:email', StaffController.getStaffByEmail);

// GET /api/staff/role/:role - Get staff by role
router.get('/role/:role', StaffController.getStaffByRole);

// GET /api/staff/specialty/:specialty - Get staff by specialty
router.get('/specialty/:specialty', StaffController.getStaffBySpecialty);

// GET /api/staff/:id - Get staff by ID
router.get('/:id', StaffController.getStaffById);

// POST /api/staff - Create new staff
router.post('/', 
  Staff.getValidationRules(),
  Staff.validateRequest,
  StaffController.createStaff
);

// PUT /api/staff/:id - Update staff
router.put('/:id',
  Staff.getValidationRules(),
  Staff.validateRequest,
  StaffController.updateStaff
);

// DELETE /api/staff/:id - Delete staff
router.delete('/:id', StaffController.deleteStaff);

module.exports = router;
