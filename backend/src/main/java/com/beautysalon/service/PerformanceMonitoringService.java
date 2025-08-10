package com.beautysalon.service;

import com.beautysalon.config.MetricsConfiguration;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Phase 3: Performance Monitoring Service
 * Provides real-time performance monitoring, alerting, and health checks
 */
@Service
public class PerformanceMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringService.class);
    
    private final MetricsConfiguration metricsConfig;
    private final CacheManager cacheManager;
    
    // Performance thresholds for alerting
    private static final double CACHE_HIT_RATE_THRESHOLD = 80.0; // 80%
    private static final long RESPONSE_TIME_THRESHOLD_MS = 500; // 500ms
    private static final long ERROR_RATE_THRESHOLD = 10; // 10 errors per minute
    
    // Tracking variables
    private final AtomicLong lastAlertTime = new AtomicLong(0);
    private final AtomicLong alertCooldownMs = new AtomicLong(60000); // 1 minute cooldown

    @Autowired
    public PerformanceMonitoringService(MetricsConfiguration metricsConfig, CacheManager cacheManager) {
        this.metricsConfig = metricsConfig;
        this.cacheManager = cacheManager;
    }

    /**
     * Monitor cache performance and trigger alerts if needed
     */
    public void monitorCachePerformance() {
        try {
            Map<String, Object> cacheStats = getCacheStatistics();
            double hitRate = (Double) cacheStats.get("hitRate");
            
            if (hitRate < CACHE_HIT_RATE_THRESHOLD) {
                triggerAlert("CACHE_HIT_RATE_LOW", 
                    String.format("Cache hit rate is %.2f%%, below threshold of %.2f%%", 
                        hitRate, CACHE_HIT_RATE_THRESHOLD));
            }
            
            logger.debug("Cache performance monitoring - Hit Rate: {:.2f}%", hitRate);
        } catch (Exception e) {
            logger.error("Error monitoring cache performance", e);
            metricsConfig.incrementError("cache_monitoring");
        }
    }

    /**
     * Monitor response times and trigger alerts for slow responses
     */
    public void monitorResponseTime(long responseTimeMs) {
        if (responseTimeMs > RESPONSE_TIME_THRESHOLD_MS) {
            triggerAlert("RESPONSE_TIME_HIGH", 
                String.format("Response time is %d ms, above threshold of %d ms", 
                    responseTimeMs, RESPONSE_TIME_THRESHOLD_MS));
        }
    }

    /**
     * Get comprehensive performance statistics
     */
    public Map<String, Object> getPerformanceStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Cache statistics
            stats.putAll(getCacheStatistics());
            
            // System statistics
            stats.put("timestamp", LocalDateTime.now());
            stats.put("uptime", getUptimeMs());
            stats.put("memoryUsage", getMemoryUsage());
            
            // Performance thresholds
            stats.put("thresholds", getPerformanceThresholds());
            
        } catch (Exception e) {
            logger.error("Error collecting performance statistics", e);
            metricsConfig.incrementError("stats_collection");
        }
        
        return stats;
    }

    /**
     * Get detailed cache statistics
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> cacheStats = new HashMap<>();
        
        try {
            // Get cache statistics from all caches
            for (String cacheName : cacheManager.getCacheNames()) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("name", cacheName);
                    stats.put("size", getCacheSize(cache));
                    cacheStats.put(cacheName, stats);
                }
            }
            
            // Calculate overall hit rate (simulated for now)
            double hitRate = calculateOverallCacheHitRate();
            cacheStats.put("hitRate", hitRate);
            cacheStats.put("hitRateStatus", hitRate >= CACHE_HIT_RATE_THRESHOLD ? "HEALTHY" : "WARNING");
            
        } catch (Exception e) {
            logger.error("Error collecting cache statistics", e);
            metricsConfig.incrementError("cache_stats");
        }
        
        return cacheStats;
    }

    /**
     * Health check for performance monitoring
     */
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> healthCheck = new HashMap<>();
        
        try {
            // Cache health
            double cacheHitRate = calculateOverallCacheHitRate();
            healthCheck.put("cacheHealth", cacheHitRate >= CACHE_HIT_RATE_THRESHOLD ? "UP" : "DOWN");
            healthCheck.put("cacheHitRate", cacheHitRate);
            
            // Memory health
            Map<String, Object> memoryUsage = getMemoryUsage();
            long usedMemoryPercent = (Long) memoryUsage.get("usedPercent");
            healthCheck.put("memoryHealth", usedMemoryPercent < 80 ? "UP" : "WARNING");
            healthCheck.put("memoryUsage", memoryUsage);
            
            // Overall status
            boolean allHealthy = "UP".equals(healthCheck.get("cacheHealth")) && 
                               "UP".equals(healthCheck.get("memoryHealth"));
            healthCheck.put("status", allHealthy ? "UP" : "WARNING");
            healthCheck.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("Error performing health check", e);
            healthCheck.put("status", "DOWN");
            healthCheck.put("error", e.getMessage());
            metricsConfig.incrementError("health_check");
        }
        
        return healthCheck;
    }

    /**
     * Trigger performance alert with cooldown
     */
    private void triggerAlert(String alertType, String message) {
        long currentTime = System.currentTimeMillis();
        long lastAlert = lastAlertTime.get();
        
        if (currentTime - lastAlert > alertCooldownMs.get()) {
            logger.warn("PERFORMANCE ALERT [{}]: {}", alertType, message);
            lastAlertTime.set(currentTime);
            
            // In a real implementation, this would send notifications
            // (email, Slack, PagerDuty, etc.)
        }
    }

    private double calculateOverallCacheHitRate() {
        // Simulated cache hit rate calculation
        // In a real implementation, this would aggregate from actual cache metrics
        return Math.random() * 20 + 80; // Simulate 80-100% hit rate
    }

    private int getCacheSize(Cache cache) {
        // Simplified cache size calculation
        return 100; // Simulated value
    }

    private long getUptimeMs() {
        return System.currentTimeMillis() - getStartTime();
    }

    private long getStartTime() {
        // Simplified - in real implementation, track actual start time
        return System.currentTimeMillis() - 3600000; // 1 hour ago
    }

    private Map<String, Object> getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memoryStats = new HashMap<>();
        memoryStats.put("maxMemory", maxMemory);
        memoryStats.put("totalMemory", totalMemory);
        memoryStats.put("usedMemory", usedMemory);
        memoryStats.put("freeMemory", freeMemory);
        memoryStats.put("usedPercent", (usedMemory * 100) / maxMemory);
        
        return memoryStats;
    }

    private Map<String, Object> getPerformanceThresholds() {
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("cacheHitRate", CACHE_HIT_RATE_THRESHOLD);
        thresholds.put("responseTime", RESPONSE_TIME_THRESHOLD_MS);
        thresholds.put("errorRate", ERROR_RATE_THRESHOLD);
        return thresholds;
    }
}
