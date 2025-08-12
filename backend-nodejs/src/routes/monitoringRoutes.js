const express = require('express');
const MonitoringController = require('../controllers/MonitoringController');

const router = express.Router();

// GET /api/monitoring/health - Health check endpoint
router.get('/health', MonitoringController.getHealthCheck);

// GET /api/monitoring/performance - Performance metrics
router.get('/performance', MonitoringController.getPerformanceMetrics);

// GET /api/monitoring/cache - Cache metrics
router.get('/cache', MonitoringController.getCacheMetrics);

// POST /api/monitoring/cache/clear - Clear all caches
router.post('/cache/clear', MonitoringController.clearCache);

// GET /api/monitoring/dashboard - Dashboard statistics
router.get('/dashboard', MonitoringController.getDashboardStats);

module.exports = router;
