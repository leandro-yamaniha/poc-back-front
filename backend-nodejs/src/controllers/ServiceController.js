const ServiceService = require('../services/ServiceService');
const Service = require('../models/Service');

class ServiceController {
  async getAllServices(req, res, next) {
    try {
      const services = await ServiceService.getAllServices();
      res.status(200).json(services);
    } catch (error) {
      next(error);
    }
  }

  async getActiveServices(req, res, next) {
    try {
      const services = await ServiceService.getActiveServices();
      res.status(200).json(services);
    } catch (error) {
      next(error);
    }
  }

  async getServiceById(req, res, next) {
    try {
      const { id } = req.params;
      const service = await ServiceService.getServiceById(id);
      
      if (!service) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Serviço não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(service);
    } catch (error) {
      next(error);
    }
  }

  async getServicesByCategory(req, res, next) {
    try {
      const { category } = req.params;
      const services = await ServiceService.getServicesByCategory(category);
      res.status(200).json(services);
    } catch (error) {
      next(error);
    }
  }

  async searchServices(req, res, next) {
    try {
      const { name } = req.query;
      
      if (!name) {
        return res.status(400).json({
          error: 'Bad Request',
          message: 'Parâmetro "name" é obrigatório',
          timestamp: new Date().toISOString()
        });
      }
      
      const services = await ServiceService.searchServicesByName(name);
      res.status(200).json(services);
    } catch (error) {
      next(error);
    }
  }

  async createService(req, res, next) {
    try {
      const service = await ServiceService.createService(req.body);
      res.status(201).json(service);
    } catch (error) {
      next(error);
    }
  }

  async updateService(req, res, next) {
    try {
      const { id } = req.params;
      const updatedService = await ServiceService.updateService(id, req.body);
      
      if (!updatedService) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Serviço não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(updatedService);
    } catch (error) {
      next(error);
    }
  }

  async deleteService(req, res, next) {
    try {
      const { id } = req.params;
      const deleted = await ServiceService.deleteService(id);
      
      if (!deleted) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Serviço não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(204).send();
    } catch (error) {
      next(error);
    }
  }

  async getServiceCount(req, res, next) {
    try {
      const count = await ServiceService.getServiceCount();
      res.status(200).json({ count });
    } catch (error) {
      next(error);
    }
  }

  async getCategories(req, res, next) {
    try {
      const categories = await ServiceService.getCategories();
      res.status(200).json(categories);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new ServiceController();
