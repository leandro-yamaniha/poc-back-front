const CustomerService = require('../services/CustomerService');
const Customer = require('../models/Customer');

class CustomerController {
  async getAllCustomers(req, res, next) {
    try {
      const customers = await CustomerService.getAllCustomers();
      res.status(200).json(customers);
    } catch (error) {
      next(error);
    }
  }

  async getCustomerById(req, res, next) {
    try {
      const { id } = req.params;
      const customer = await CustomerService.getCustomerById(id);
      
      if (!customer) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Cliente não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(customer);
    } catch (error) {
      next(error);
    }
  }

  async getCustomerByEmail(req, res, next) {
    try {
      const { email } = req.params;
      const customer = await CustomerService.getCustomerByEmail(email);
      
      if (!customer) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Cliente não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(customer);
    } catch (error) {
      next(error);
    }
  }

  async searchCustomers(req, res, next) {
    try {
      const { name } = req.query;
      
      if (!name) {
        return res.status(400).json({
          error: 'Bad Request',
          message: 'Parâmetro "name" é obrigatório',
          timestamp: new Date().toISOString()
        });
      }
      
      const customers = await CustomerService.searchCustomersByName(name);
      res.status(200).json(customers);
    } catch (error) {
      next(error);
    }
  }

  async createCustomer(req, res, next) {
    try {
      const customer = await CustomerService.createCustomer(req.body);
      res.status(201).json(customer);
    } catch (error) {
      next(error);
    }
  }

  async updateCustomer(req, res, next) {
    try {
      const { id } = req.params;
      const updatedCustomer = await CustomerService.updateCustomer(id, req.body);
      
      if (!updatedCustomer) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Cliente não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(200).json(updatedCustomer);
    } catch (error) {
      next(error);
    }
  }

  async deleteCustomer(req, res, next) {
    try {
      const { id } = req.params;
      const deleted = await CustomerService.deleteCustomer(id);
      
      if (!deleted) {
        return res.status(404).json({
          error: 'Not Found',
          message: 'Cliente não encontrado',
          timestamp: new Date().toISOString()
        });
      }
      
      res.status(204).send();
    } catch (error) {
      next(error);
    }
  }

  async getCustomerCount(req, res, next) {
    try {
      const count = await CustomerService.getCustomerCount();
      res.status(200).json({ count });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new CustomerController();
