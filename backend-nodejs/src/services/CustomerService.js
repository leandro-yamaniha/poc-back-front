const CustomerRepository = require('../repositories/CustomerRepository');
const Customer = require('../models/Customer');
const NodeCache = require('node-cache');

class CustomerService {
  constructor() {
    // Cache with 10 minutes TTL
    this.cache = new NodeCache({ 
      stdTTL: parseInt(process.env.CACHE_TTL) || 600,
      maxKeys: parseInt(process.env.CACHE_MAX_KEYS) || 1000
    });
  }

  async getAllCustomers() {
    const cacheKey = 'customers:all';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const customers = await CustomerRepository.findAll();
    this.cache.set(cacheKey, customers);
    return customers;
  }

  async getCustomerById(id) {
    const cacheKey = `customer:${id}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const customer = await CustomerRepository.findById(id);
    if (customer) {
      this.cache.set(cacheKey, customer);
    }
    return customer;
  }

  async getCustomerByEmail(email) {
    const cacheKey = `customer:email:${email}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const customer = await CustomerRepository.findByEmail(email);
    if (customer) {
      this.cache.set(cacheKey, customer);
    }
    return customer;
  }

  async searchCustomersByName(name) {
    if (!name || name.trim().length < 2) {
      throw new Error('Nome deve ter pelo menos 2 caracteres para busca');
    }

    const cacheKey = `customers:search:${name.toLowerCase()}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const customers = await CustomerRepository.searchByName(name);
    this.cache.set(cacheKey, customers, 300); // Shorter cache for search results
    return customers;
  }

  async createCustomer(customerData) {
    // Check if email already exists
    const existingCustomer = await CustomerRepository.findByEmail(customerData.email);
    if (existingCustomer) {
      throw new Error('Email já está em uso por outro cliente');
    }

    const customer = new Customer(customerData);
    const savedCustomer = await CustomerRepository.save(customer);
    
    // Clear relevant caches
    this.cache.del('customers:all');
    this.cache.set(`customer:${savedCustomer.id}`, savedCustomer);
    this.cache.set(`customer:email:${savedCustomer.email}`, savedCustomer);
    
    return savedCustomer;
  }

  async updateCustomer(id, customerData) {
    const existingCustomer = await CustomerRepository.findById(id);
    if (!existingCustomer) {
      return null;
    }

    // Check if email is being changed and if it's already in use
    if (customerData.email && customerData.email !== existingCustomer.email) {
      const emailExists = await CustomerRepository.findByEmail(customerData.email);
      if (emailExists) {
        throw new Error('Email já está em uso por outro cliente');
      }
    }

    existingCustomer.update(customerData);
    const updatedCustomer = await CustomerRepository.update(id, existingCustomer);
    
    // Clear relevant caches
    this.cache.del('customers:all');
    this.cache.del(`customer:${id}`);
    this.cache.del(`customer:email:${existingCustomer.email}`);
    if (customerData.email && customerData.email !== existingCustomer.email) {
      this.cache.del(`customer:email:${customerData.email}`);
    }
    
    return updatedCustomer;
  }

  async deleteCustomer(id) {
    const existingCustomer = await CustomerRepository.findById(id);
    if (!existingCustomer) {
      return false;
    }

    await CustomerRepository.deleteById(id);
    
    // Clear relevant caches
    this.cache.del('customers:all');
    this.cache.del(`customer:${id}`);
    this.cache.del(`customer:email:${existingCustomer.email}`);
    
    return true;
  }

  async customerExists(id) {
    const customer = await this.getCustomerById(id);
    return customer !== null;
  }

  async getCustomerCount() {
    const cacheKey = 'customers:count';
    const cached = this.cache.get(cacheKey);
    
    if (cached !== undefined) {
      return cached;
    }

    const count = await CustomerRepository.count();
    this.cache.set(cacheKey, count, 300); // 5 minutes cache for count
    return count;
  }

  // Clear all customer-related cache
  clearCache() {
    const keys = this.cache.keys();
    const customerKeys = keys.filter(key => key.startsWith('customer'));
    customerKeys.forEach(key => this.cache.del(key));
  }
}

module.exports = new CustomerService();
