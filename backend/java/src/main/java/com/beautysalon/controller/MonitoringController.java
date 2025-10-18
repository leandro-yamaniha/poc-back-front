package com.beautysalon.controller;

import com.beautysalon.service.PerformanceMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Phase 3: Monitoring Controller
 * Provides REST endpoints for performance monitoring, metrics, and health checks
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final PerformanceMonitoringService performanceMonitoringService;

    @Autowired
    public MonitoringController(PerformanceMonitoringService performanceMonitoringService) {
        this.performanceMonitoringService = performanceMonitoringService;
    }

    /**
     * Get comprehensive performance statistics
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceStatistics() {
        Map<String, Object> stats = performanceMonitoringService.getPerformanceStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get cache statistics
     */
    @GetMapping("/cache")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        Map<String, Object> cacheStats = performanceMonitoringService.getCacheStatistics();
        return ResponseEntity.ok(cacheStats);
    }

    /**
     * Perform health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> performHealthCheck() {
        Map<String, Object> healthCheck = performanceMonitoringService.performHealthCheck();
        
        // Return appropriate HTTP status based on health
        String status = (String) healthCheck.get("status");
        if ("UP".equals(status)) {
            return ResponseEntity.ok(healthCheck);
        } else if ("WARNING".equals(status)) {
            return ResponseEntity.status(200).body(healthCheck); // Still OK but with warnings
        } else {
            return ResponseEntity.status(503).body(healthCheck); // Service Unavailable
        }
    }

    /**
     * Trigger manual cache performance monitoring
     */
    @GetMapping("/monitor/cache")
    public ResponseEntity<String> monitorCachePerformance() {
        performanceMonitoringService.monitorCachePerformance();
        return ResponseEntity.ok("Cache performance monitoring triggered");
    }
}
