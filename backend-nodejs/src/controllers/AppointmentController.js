const AppointmentService = require('../services/AppointmentService');
const Appointment = require('../models/Appointment');

class AppointmentController {
  async getAllAppointments(req, res, next) {
    try {
      const appointments = await AppointmentService.getAllAppointments();
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentById(req, res, next) {
    try {
      const { id } = req.params;
      const appointment = await AppointmentService.getAppointmentById(id);
      
      if (!appointment) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Agendamento não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(appointment);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentsByCustomerId(req, res, next) {
    try {
      const { customerId } = req.params;
      const appointments = await AppointmentService.getAppointmentsByCustomerId(customerId);
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentsByStaffId(req, res, next) {
    try {
      const { staffId } = req.params;
      const appointments = await AppointmentService.getAppointmentsByStaffId(staffId);
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentsByServiceId(req, res, next) {
    try {
      const { serviceId } = req.params;
      const appointments = await AppointmentService.getAppointmentsByServiceId(serviceId);
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentsByStatus(req, res, next) {
    try {
      const { status } = req.params;
      const appointments = await AppointmentService.getAppointmentsByStatus(status);
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentsByDateRange(req, res, next) {
    try {
      const { startDate, endDate } = req.query;
      
      if (!startDate || !endDate) {
        return res.status(400).json({
          error: 'Bad Request',
          message: 'Parâmetros "startDate" e "endDate" são obrigatórios',
          timestamp: new Date().toISOString()
        });
      }
      
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return res.status(400).json({
          error: 'Bad Request',
          message: 'Datas devem estar no formato ISO 8601',
          timestamp: new Date().toISOString()
        });
      }
      
      const appointments = await AppointmentService.getAppointmentsByDateRange(start, end);
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getUpcomingAppointments(req, res, next) {
    try {
      const appointments = await AppointmentService.getUpcomingAppointments();
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async getTodayAppointments(req, res, next) {
    try {
      const appointments = await AppointmentService.getTodayAppointments();
      res.status(200).json(appointments);
    } catch (error) {
      next(error);
    }
  }

  async createAppointment(req, res, next) {
    try {
      const appointment = await AppointmentService.createAppointment(req.body);
      res.status(201).json(appointment);
    } catch (error) {
      next(error);
    }
  }

  async updateAppointment(req, res, next) {
    try {
      const { id } = req.params;
      const updatedAppointment = await AppointmentService.updateAppointment(id, req.body);
      
      if (!updatedAppointment) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Agendamento não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(updatedAppointment);
    } catch (error) {
      next(error);
    }
  }

  async deleteAppointment(req, res, next) {
    try {
      const { id } = req.params;
      const deleted = await AppointmentService.deleteAppointment(id);
      
      if (!deleted) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Agendamento não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(204).send();
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentCount(req, res, next) {
    try {
      const count = await AppointmentService.getAppointmentCount();
      res.status(200).json({ count });
    } catch (error) {
      next(error);
    }
  }

  async getAppointmentCountByStatus(req, res, next) {
    try {
      const { status } = req.params;
      const count = await AppointmentService.getAppointmentCountByStatus(status);
      res.status(200).json({ count, status });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new AppointmentController();
