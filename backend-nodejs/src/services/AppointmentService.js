const AppointmentRepository = require('../repositories/AppointmentRepository');
const CustomerService = require('./CustomerService');
const StaffService = require('./StaffService');
const ServiceService = require('./ServiceService');
const Appointment = require('../models/Appointment');
const NodeCache = require('node-cache');

class AppointmentService {
  constructor() {
    // Cache with 10 minutes TTL
    this.cache = new NodeCache({ 
      stdTTL: parseInt(process.env.CACHE_TTL) || 600,
      maxKeys: parseInt(process.env.CACHE_MAX_KEYS) || 1000
    });
  }

  async getAllAppointments() {
    const cacheKey = 'appointments:all';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findAll();
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentById(id) {
    const cacheKey = `appointment:${id}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointment = await AppointmentRepository.findById(id);
    if (appointment) {
      this.cache.set(cacheKey, appointment);
    }
    return appointment;
  }

  async getAppointmentsByCustomerId(customerId) {
    const cacheKey = `appointments:customer:${customerId}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findByCustomerId(customerId);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByStaffId(staffId) {
    const cacheKey = `appointments_staff_${staffId}`;
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }
    const appointments = await AppointmentRepository.findByStaffId(staffId);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByDate(date) {
    const cacheKey = `appointments_date_${date}`;
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }
    const appointments = await AppointmentRepository.findByDate(date);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByDateAndStaff(date, staffId) {
    const cacheKey = `appointments_date_${date}_staff_${staffId}`;
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }
    const appointments = await AppointmentRepository.findByDateAndStaff(date, staffId);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByServiceId(serviceId) {
    const cacheKey = `appointments:service:${serviceId}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findByServiceId(serviceId);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByStatus(status) {
    const cacheKey = `appointments:status:${status}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findByStatus(status);
    this.cache.set(cacheKey, appointments);
    return appointments;
  }

  async getAppointmentsByDateRange(startDate, endDate) {
    const cacheKey = `appointments:range:${startDate.toISOString()}:${endDate.toISOString()}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findByDateRange(startDate, endDate);
    this.cache.set(cacheKey, appointments, 300); // Shorter cache for date ranges
    return appointments;
  }

  async getUpcomingAppointments() {
    const cacheKey = 'appointments:upcoming';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findUpcomingAppointments();
    this.cache.set(cacheKey, appointments, 300); // 5 minutes cache for upcoming
    return appointments;
  }

  async getTodayAppointments() {
    const cacheKey = 'appointments:today';
    const cached = this.cache.get(cacheKey);
    
    if (cached) {
      return cached;
    }

    const appointments = await AppointmentRepository.findTodayAppointments();
    this.cache.set(cacheKey, appointments, 300); // 5 minutes cache for today
    return appointments;
  }

  async createAppointment(appointmentData) {
    // Validate that customer, staff, and service exist
    const customer = await CustomerService.getCustomerById(appointmentData.customer_id);
    if (!customer) {
      throw new Error('Cliente não encontrado');
    }

    const staff = await StaffService.getStaffById(appointmentData.staff_id);
    if (!staff) {
      throw new Error('Funcionário não encontrado');
    }

    const service = await ServiceService.getServiceById(appointmentData.service_id);
    if (!service) {
      throw new Error('Serviço não encontrado');
    }

    // Check for scheduling conflicts
    const hasConflict = await AppointmentRepository.checkConflict(
      appointmentData.staff_id,
      appointmentData.appointment_date,
      service.duration
    );

    if (hasConflict) {
      throw new Error('Conflito de horário: funcionário já possui agendamento neste período');
    }

    const appointment = new Appointment(appointmentData);
    const savedAppointment = await AppointmentRepository.save(appointment);
    
    // Clear relevant caches
    this.cache.del('appointments:all');
    this.cache.del('appointments:upcoming');
    this.cache.del('appointments:today');
    this.cache.del(`appointments:customer:${savedAppointment.customer_id}`);
    this.cache.del(`appointments:staff:${savedAppointment.staff_id}`);
    this.cache.del(`appointments:service:${savedAppointment.service_id}`);
    this.cache.del(`appointments:status:${savedAppointment.status}`);
    
    return savedAppointment;
  }

  async updateAppointment(id, appointmentData) {
    const existingAppointment = await AppointmentRepository.findById(id);
    if (!existingAppointment) {
      return null;
    }

    // Validate entities if they are being updated
    if (appointmentData.customer_id && appointmentData.customer_id !== existingAppointment.customer_id) {
      const customer = await CustomerService.getCustomerById(appointmentData.customer_id);
      if (!customer) {
        throw new Error('Cliente não encontrado');
      }
    }

    if (appointmentData.staff_id && appointmentData.staff_id !== existingAppointment.staff_id) {
      const staff = await StaffService.getStaffById(appointmentData.staff_id);
      if (!staff) {
        throw new Error('Funcionário não encontrado');
      }
    }

    if (appointmentData.service_id && appointmentData.service_id !== existingAppointment.service_id) {
      const service = await ServiceService.getServiceById(appointmentData.service_id);
      if (!service) {
        throw new Error('Serviço não encontrado');
      }
    }

    // Check for conflicts if staff or date is being changed
    if ((appointmentData.staff_id && appointmentData.staff_id !== existingAppointment.staff_id) ||
        (appointmentData.appointment_date && appointmentData.appointment_date !== existingAppointment.appointment_date)) {
      
      const staffId = appointmentData.staff_id || existingAppointment.staff_id;
      const appointmentDate = appointmentData.appointment_date || existingAppointment.appointment_date;
      const serviceId = appointmentData.service_id || existingAppointment.service_id;
      
      const service = await ServiceService.getServiceById(serviceId);
      const hasConflict = await AppointmentRepository.checkConflict(
        staffId,
        appointmentDate,
        service.duration,
        id // Exclude current appointment from conflict check
      );

      if (hasConflict) {
        throw new Error('Conflito de horário: funcionário já possui agendamento neste período');
      }
    }

    existingAppointment.update(appointmentData);
    const updatedAppointment = await AppointmentRepository.update(id, existingAppointment);
    
    // Clear relevant caches
    this.cache.del('appointments:all');
    this.cache.del('appointments:upcoming');
    this.cache.del('appointments:today');
    this.cache.del(`appointment:${id}`);
    this.cache.del(`appointments:customer:${existingAppointment.customer_id}`);
    this.cache.del(`appointments:staff:${existingAppointment.staff_id}`);
    this.cache.del(`appointments:service:${existingAppointment.service_id}`);
    this.cache.del(`appointments:status:${existingAppointment.status}`);
    
    return updatedAppointment;
  }

  async deleteAppointment(id) {
    const existingAppointment = await AppointmentRepository.findById(id);
    if (!existingAppointment) {
      return false;
    }

    await AppointmentRepository.deleteById(id);
    
    // Clear relevant caches
    this.cache.del('appointments:all');
    this.cache.del('appointments:upcoming');
    this.cache.del('appointments:today');
    this.cache.del(`appointment:${id}`);
    this.cache.del(`appointments:customer:${existingAppointment.customer_id}`);
    this.cache.del(`appointments:staff:${existingAppointment.staff_id}`);
    this.cache.del(`appointments:service:${existingAppointment.service_id}`);
    this.cache.del(`appointments:status:${existingAppointment.status}`);
    
    return true;
  }

  async appointmentExists(id) {
    const appointment = await this.getAppointmentById(id);
    return appointment !== null;
  }

  async getAppointmentCount() {
    const cacheKey = 'appointments:count';
    const cached = this.cache.get(cacheKey);
    
    if (cached !== undefined) {
      return cached;
    }

    const count = await AppointmentRepository.count();
    this.cache.set(cacheKey, count, 300); // 5 minutes cache for count
    return count;
  }

  async getAppointmentCountByStatus(status) {
    const cacheKey = `appointments:count:status:${status}`;
    const cached = this.cache.get(cacheKey);
    
    if (cached !== undefined) {
      return cached;
    }

    const count = await AppointmentRepository.countByStatus(status);
    this.cache.set(cacheKey, count, 300); // 5 minutes cache for count
    return count;
  }

  // Clear all appointment-related cache
  clearCache() {
    const keys = this.cache.keys();
    const appointmentKeys = keys.filter(key => key.startsWith('appointment'));
    appointmentKeys.forEach(key => this.cache.del(key));
  }
}

module.exports = new AppointmentService();
