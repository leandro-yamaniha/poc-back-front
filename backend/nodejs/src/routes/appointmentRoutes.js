const express = require('express');
const AppointmentController = require('../controllers/AppointmentController');
const Appointment = require('../models/Appointment');

const router = express.Router();

// GET /api/appointments - Get all appointments
router.get('/', AppointmentController.getAllAppointments);

// GET /api/appointments/count - Get appointment count
router.get('/count', AppointmentController.getAppointmentCount);

// GET /api/appointments/upcoming - Get upcoming appointments
router.get('/upcoming', AppointmentController.getUpcomingAppointments);

// GET /api/appointments/today - Get today's appointments
router.get('/today', AppointmentController.getTodayAppointments);

// GET /api/appointments/date-range?startDate=...&endDate=... - Get appointments by date range
router.get('/date-range', AppointmentController.getAppointmentsByDateRange);

// GET /api/appointments/customer/:customerId - Get appointments by customer ID
router.get('/customer/:customerId', AppointmentController.getAppointmentsByCustomerId);

// GET /api/appointments/staff/:staffId - Get appointments by staff ID
router.get('/staff/:staffId', AppointmentController.getAppointmentsByStaffId);

// GET /api/appointments/date/:date - Get appointments by date
router.get('/date/:date', AppointmentController.getAppointmentsByDate);

// GET /api/appointments/date/:date/staff/:staffId - Get appointments by date and staff
router.get('/date/:date/staff/:staffId', AppointmentController.getAppointmentsByDateAndStaff);

// GET /api/appointments/service/:serviceId - Get appointments by service ID
router.get('/service/:serviceId', AppointmentController.getAppointmentsByServiceId);

// GET /api/appointments/status/:status - Get appointments by status
router.get('/status/:status', AppointmentController.getAppointmentsByStatus);

// GET /api/appointments/status/:status/count - Get appointment count by status
router.get('/status/:status/count', AppointmentController.getAppointmentCountByStatus);

// GET /api/appointments/:id - Get appointment by ID
router.get('/:id', AppointmentController.getAppointmentById);

// POST /api/appointments - Create new appointment
router.post('/', 
  Appointment.getValidationRules(),
  Appointment.validateRequest,
  AppointmentController.createAppointment
);

// PUT /api/appointments/:id - Update appointment
router.put('/:id',
  Appointment.getValidationRules(),
  Appointment.validateRequest,
  AppointmentController.updateAppointment
);

// DELETE /api/appointments/:id - Delete appointment
router.delete('/:id', AppointmentController.deleteAppointment);

module.exports = router;
