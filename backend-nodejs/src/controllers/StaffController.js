const StaffService = require('../services/StaffService');
const Staff = require('../models/Staff');

class StaffController {
  async getAllStaff(req, res, next) {
    try {
      const staff = await StaffService.getAllStaff();
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getActiveStaff(req, res, next) {
    try {
      const staff = await StaffService.getActiveStaff();
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getStaffById(req, res, next) {
    try {
      const { id } = req.params;
      const staff = await StaffService.getStaffById(id);
      
      if (!staff) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Funcionário não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getStaffByEmail(req, res, next) {
    try {
      const { email } = req.params;
      const staff = await StaffService.getStaffByEmail(email);
      
      if (!staff) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Funcionário não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getStaffByRole(req, res, next) {
    try {
      const { role } = req.params;
      const staff = await StaffService.getStaffByRole(role);
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getActiveStaffByRole(req, res, next) {
    try {
      const { role } = req.params;
      const staff = await StaffService.getActiveStaffByRole(role);
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async getStaffBySpecialty(req, res, next) {
    try {
      const { specialty } = req.params;
      const staff = await StaffService.getStaffBySpecialty(specialty);
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async searchStaff(req, res, next) {
    try {
      const { name } = req.query;
      
      if (!name) {
        return res.status(400).json({
          error: 'Bad Request',
          message: 'Parâmetro "name" é obrigatório',
          timestamp: new Date().toISOString()
        });
      }
      
      const staff = await StaffService.searchStaffByName(name);
      res.status(200).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async createStaff(req, res, next) {
    try {
      const staff = await StaffService.createStaff(req.body);
      res.status(201).json(staff);
    } catch (error) {
      next(error);
    }
  }

  async updateStaff(req, res, next) {
    try {
      const { id } = req.params;
      const updatedStaff = await StaffService.updateStaff(id, req.body);
      
      if (!updatedStaff) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Funcionário não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(updatedStaff);
    } catch (error) {
      next(error);
    }
  }

  async deleteStaff(req, res, next) {
    try {
      const { id } = req.params;
      const deleted = await StaffService.deleteStaff(id);
      
      if (!deleted) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Funcionário não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(204).send();
    } catch (error) {
      next(error);
    }
  }

  async getStaffCount(req, res, next) {
    try {
      const count = await StaffService.getStaffCount();
      res.status(200).json({ count });
    } catch (error) {
      next(error);
    }
  }

  async getRoles(req, res, next) {
    try {
      const roles = await StaffService.getRoles();
      res.status(200).json(roles);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new StaffController();
