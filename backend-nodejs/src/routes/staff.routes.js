const express = require('express');
const router = express.Router();
const staffController = require('../controllers/staff.controller');

router.post('/', staffController.createStaff);
router.get('/', staffController.getAllStaff);
router.get('/role/:role', staffController.getStaffByRole);
router.get('/role/:role/active', staffController.getActiveStaffByRole);
router.get('/:id', staffController.getStaffById);
router.put('/:id', staffController.updateStaff);
router.delete('/:id', staffController.deleteStaff);

module.exports = router;
