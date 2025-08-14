const ServiceRepository = require('../repositories/ServiceRepository');
const Service = require('../models/Service');
const NodeCache = require('node-cache');

class ServiceService {
  constructor() {
    // Cache with 10 minutes TTL
    this.cache = new NodeCache({ 
      stdTTL: parseInt(process.env.CACHE_TTL) || 600,
      maxKeys: parseInt(process.env.CACHE_MAX_KEYS) || 1000
    });
  }

  async getAllServices() {
    const cacheKey = 'services:all';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const services = await ServiceRepository.findAll();
    this.cache.set(cacheKey, services);
    return services;
  }

  async getActiveServices() {
    const cacheKey = 'services:active';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const services = await ServiceRepository.findActiveServices();
    this.cache.set(cacheKey, services);
    return services;
  }

  async getServiceById(id) {
    const cacheKey = `service:${id}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const service = await ServiceRepository.findById(id);
    if (service) {
      this.cache.set(cacheKey, service);
    }
    return service;
  }

  async getServicesByCategory(category) {
    const cacheKey = `services:category:${category}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const services = await ServiceRepository.findByCategory(category);
    this.cache.set(cacheKey, services);
    return services;
  }

  async getActiveServicesByCategory(category) {
    const cacheKey = `services:category:${category}:active`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const services = await ServiceRepository.findActiveByCategory(category);
    this.cache.set(cacheKey, services);
    return services;
  }

  async searchServicesByName(name) {
    if (!name || name.trim().length < 2) {
      throw new Error('Nome deve ter pelo menos 2 caracteres para busca');
    }

    const cacheKey = `services:search:${name.toLowerCase()}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const services = await ServiceRepository.searchByName(name);
    this.cache.set(cacheKey, services, 300); // Shorter cache for search results
    return services;
  }

  async createService(serviceData) {
    const service = new Service(serviceData);
    const savedService = await ServiceRepository.save(service);
    
    // Clear relevant caches
    this.cache.del('services:all');
    this.cache.del('services:active');
    this.cache.del(`services:category:${savedService.category}`);
    this.cache.set(`service:${savedService.id}`, savedService);
    
    return savedService;
  }

  async updateService(id, serviceData) {
    const existingService = await ServiceRepository.findById(id);
    if (!existingService) {
      return null;
    }

    const oldCategory = existingService.category;
    existingService.update(serviceData);
    const updatedService = await ServiceRepository.update(id, existingService);
    
    // Clear relevant caches
    this.cache.del('services:all');
    this.cache.del('services:active');
    this.cache.del(`service:${id}`);
    this.cache.del(`services:category:${oldCategory}`);
    if (serviceData.category && serviceData.category !== oldCategory) {
      this.cache.del(`services:category:${serviceData.category}`);
    }
    
    return updatedService;
  }

  async deleteService(id) {
    const existingService = await ServiceRepository.findById(id);
    if (!existingService) {
      return false;
    }

    await ServiceRepository.deleteById(id);
    
    // Clear relevant caches
    this.cache.del('services:all');
    this.cache.del('services:active');
    this.cache.del(`service:${id}`);
    this.cache.del(`services:category:${existingService.category}`);
    
    return true;
  }

  async serviceExists(id) {
    const service = await this.getServiceById(id);
    return service !== null;
  }

  async getServiceCount() {
    const cacheKey = 'services:count';
    const cached = this.cache.get(cacheKey);
    
    if (cached !== undefined) {
      return cached;
    }

    const count = await ServiceRepository.count();
    this.cache.set(cacheKey, count, 300); // 5 minutes cache for count
    return count;
  }

  async getCategories() {
    const cacheKey = 'services:categories';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const categories = await ServiceRepository.getCategories();
    this.cache.set(cacheKey, categories, 1800); // 30 minutes cache for categories
    return categories;
  }

  // Clear all service-related cache
  clearCache() {
    const keys = this.cache.keys();
    const serviceKeys = keys.filter(key => key.startsWith('service'));
    serviceKeys.forEach(key => this.cache.del(key));
  }
}

module.exports = new ServiceService();
