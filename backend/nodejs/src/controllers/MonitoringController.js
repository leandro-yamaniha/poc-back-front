const CustomerService = require('../services/CustomerService');
const ServiceService = require('../services/ServiceService');
const StaffService = require('../services/StaffService');
const AppointmentService = require('../services/AppointmentService');
const cassandraClient = require('../config/cassandra');

class MonitoringController {
  async getHealthCheck(req, res, next) {
    try {
      const health = {
        status: 'UP',
        timestamp: new Date().toISOString(),
        service: 'beauty-salon-backend-nodejs',
        version: '1.0.0',
        checks: {
          database: 'UP',
          memory: 'UP',
          cache: 'UP'
        }
      };

      // Check Cassandra connection
      try {
        if (!cassandraClient.isClientConnected()) {
          await cassandraClient.connect();
        }
        health.checks.database = 'UP';
      } catch (error) {
        health.checks.database = 'DOWN';
        health.status = 'DOWN';
      }

      // Check memory usage
      const memoryUsage = process.memoryUsage();
      const memoryUsageMB = Math.round(memoryUsage.heapUsed / 1024 / 1024);
      health.checks.memory = memoryUsageMB < 500 ? 'UP' : 'WARNING';
      health.memoryUsageMB = memoryUsageMB;

      res.status(health.status === 'UP' ? 200 : 503).json(health);
    } catch (error) {
      next(error);
    }
  }

  async getPerformanceMetrics(req, res, next) {
    try {
      const startTime = Date.now();

      // Get basic counts
      const [customerCount, serviceCount, staffCount, appointmentCount] = await Promise.all([
        CustomerService.getCustomerCount(),
        ServiceService.getServiceCount(),
        StaffService.getStaffCount(),
        AppointmentService.getAppointmentCount()
      ]);

      const responseTime = Date.now() - startTime;

      // Memory usage
      const memoryUsage = process.memoryUsage();
      
      // System uptime
      const uptime = process.uptime();

      const metrics = {
        timestamp: new Date().toISOString(),
        responseTime: `${responseTime}ms`,
        database: {
          customerCount,
          serviceCount,
          staffCount,
          appointmentCount,
          totalRecords: customerCount + serviceCount + staffCount + appointmentCount
        },
        system: {
          uptime: `${Math.floor(uptime / 3600)}h ${Math.floor((uptime % 3600) / 60)}m`,
          memoryUsage: {
            heapUsed: `${Math.round(memoryUsage.heapUsed / 1024 / 1024)}MB`,
            heapTotal: `${Math.round(memoryUsage.heapTotal / 1024 / 1024)}MB`,
            external: `${Math.round(memoryUsage.external / 1024 / 1024)}MB`
          },
          nodeVersion: process.version,
          platform: process.platform
        },
        cache: {
          status: 'ACTIVE',
          type: 'node-cache'
        }
      };

      res.status(200).json(metrics);
    } catch (error) {
      next(error);
    }
  }

  async getCacheMetrics(req, res, next) {
    try {
      // Since we're using multiple cache instances in services, 
      // we'll provide a general cache status
      const cacheMetrics = {
        timestamp: new Date().toISOString(),
        status: 'ACTIVE',
        type: 'node-cache',
        configuration: {
          defaultTTL: process.env.CACHE_TTL || 600,
          maxKeys: process.env.CACHE_MAX_KEYS || 1000
        },
        services: {
          customers: 'ACTIVE',
          services: 'ACTIVE',
          staff: 'ACTIVE',
          appointments: 'ACTIVE'
        }
      };

      res.status(200).json(cacheMetrics);
    } catch (error) {
      next(error);
    }
  }

  async clearCache(req, res, next) {
    try {
      // Clear all service caches
      CustomerService.clearCache();
      ServiceService.clearCache();
      StaffService.clearCache();
      AppointmentService.clearCache();

      const result = {
        timestamp: new Date().toISOString(),
        message: 'All caches cleared successfully',
        services: ['customers', 'services', 'staff', 'appointments']
      };

      res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  }

  async getDashboardStats(req, res, next) {
    try {
      const [
        totalCustomers,
        totalServices,
        totalStaff,
        totalAppointments,
        todayAppointments,
        upcomingAppointments
      ] = await Promise.all([
        CustomerService.getCustomerCount(),
        ServiceService.getServiceCount(),
        StaffService.getStaffCount(),
        AppointmentService.getAppointmentCount(),
        AppointmentService.getTodayAppointments(),
        AppointmentService.getUpcomingAppointments()
      ]);

      const stats = {
        timestamp: new Date().toISOString(),
        totals: {
          customers: totalCustomers,
          services: totalServices,
          staff: totalStaff,
          appointments: totalAppointments
        },
        appointments: {
          today: todayAppointments.length,
          upcoming: upcomingAppointments.length,
          todayList: todayAppointments.slice(0, 5), // First 5 for preview
          upcomingList: upcomingAppointments.slice(0, 5) // First 5 for preview
        }
      };

      res.status(200).json(stats);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new MonitoringController();
