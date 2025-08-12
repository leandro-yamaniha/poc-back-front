const StaffRepository = require('../repositories/StaffRepository');
const Staff = require('../models/Staff');
const NodeCache = require('node-cache');

class StaffService {
  constructor() {
    // Cache with 10 minutes TTL
    this.cache = new NodeCache({ 
      stdTTL: parseInt(process.env.CACHE_TTL) || 600,
      maxKeys: parseInt(process.env.CACHE_MAX_KEYS) || 1000
    });
  }

  async getAllStaff() {
    const cacheKey = 'staff:all';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findAll();
    this.cache.set(cacheKey, staff);
    return staff;
  }

  async getActiveStaff() {
    const cacheKey = 'staff:active';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findActiveStaff();
    this.cache.set(cacheKey, staff);
    return staff;
  }

  async getStaffById(id) {
    const cacheKey = `staff:${id}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findById(id);
    if (staff) {
      this.cache.set(cacheKey, staff);
    }
    return staff;
  }

  async getStaffByEmail(email) {
    const cacheKey = `staff:email:${email}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findByEmail(email);
    if (staff) {
      this.cache.set(cacheKey, staff);
    }
    return staff;
  }

  async getStaffByRole(role) {
    const cacheKey = `staff:role:${role}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findByRole(role);
    this.cache.set(cacheKey, staff);
    return staff;
  }

  async getStaffBySpecialty(specialty) {
    const cacheKey = `staff:specialty:${specialty}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.findBySpecialty(specialty);
    this.cache.set(cacheKey, staff);
    return staff;
  }

  async searchStaffByName(name) {
    if (!name || name.trim().length < 2) {
      throw new Error('Nome deve ter pelo menos 2 caracteres para busca');
    }

    const cacheKey = `staff:search:${name.toLowerCase()}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const staff = await StaffRepository.searchByName(name);
    this.cache.set(cacheKey, staff, 300); // Shorter cache for search results
    return staff;
  }

  async createStaff(staffData) {
    // Check if email already exists
    const existingStaff = await StaffRepository.findByEmail(staffData.email);
    if (existingStaff) {
      throw new Error('Email já está em uso por outro funcionário');
    }

    const staff = new Staff(staffData);
    const savedStaff = await StaffRepository.save(staff);
    
    // Clear relevant caches
    this.cache.del('staff:all');
    this.cache.del('staff:active');
    this.cache.del(`staff:role:${savedStaff.role}`);
    this.cache.set(`staff:${savedStaff.id}`, savedStaff);
    this.cache.set(`staff:email:${savedStaff.email}`, savedStaff);
    
    return savedStaff;
  }

  async updateStaff(id, staffData) {
    const existingStaff = await StaffRepository.findById(id);
    if (!existingStaff) {
      return null;
    }

    // Check if email is being changed and if it's already in use
    if (staffData.email && staffData.email !== existingStaff.email) {
      const emailExists = await StaffRepository.findByEmail(staffData.email);
      if (emailExists) {
        throw new Error('Email já está em uso por outro funcionário');
      }
    }

    const oldRole = existingStaff.role;
    existingStaff.update(staffData);
    const updatedStaff = await StaffRepository.update(id, existingStaff);
    
    // Clear relevant caches
    this.cache.del('staff:all');
    this.cache.del('staff:active');
    this.cache.del(`staff:${id}`);
    this.cache.del(`staff:email:${existingStaff.email}`);
    this.cache.del(`staff:role:${oldRole}`);
    if (staffData.email && staffData.email !== existingStaff.email) {
      this.cache.del(`staff:email:${staffData.email}`);
    }
    if (staffData.role && staffData.role !== oldRole) {
      this.cache.del(`staff:role:${staffData.role}`);
    }
    
    return updatedStaff;
  }

  async deleteStaff(id) {
    const existingStaff = await StaffRepository.findById(id);
    if (!existingStaff) {
      return false;
    }

    await StaffRepository.deleteById(id);
    
    // Clear relevant caches
    this.cache.del('staff:all');
    this.cache.del('staff:active');
    this.cache.del(`staff:${id}`);
    this.cache.del(`staff:email:${existingStaff.email}`);
    this.cache.del(`staff:role:${existingStaff.role}`);
    
    return true;
  }

  async staffExists(id) {
    const staff = await this.getStaffById(id);
    return staff !== null;
  }

  async getStaffCount() {
    const cacheKey = 'staff:count';
    const cached = this.cache.get(cacheKey);
    
    if (cached !== undefined) {
      return cached;
    }

    const count = await StaffRepository.count();
    this.cache.set(cacheKey, count, 300); // 5 minutes cache for count
    return count;
  }

  async getRoles() {
    const cacheKey = 'staff:roles';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const roles = await StaffRepository.getRoles();
    this.cache.set(cacheKey, roles, 1800); // 30 minutes cache for roles
    return roles;
  }

  // Clear all staff-related cache
  clearCache() {
    const keys = this.cache.keys();
    const staffKeys = keys.filter(key => key.startsWith('staff'));
    staffKeys.forEach(key => this.cache.del(key));
  }
}

module.exports = new StaffService();
