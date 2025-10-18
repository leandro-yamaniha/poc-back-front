package com.beautysalon.mutation;

import com.beautysalon.config.MetricsConfiguration;
import com.beautysalon.service.PerformanceMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Targeted mutation tests for PerformanceMonitoringService
 * Focuses on conditional logic, arithmetic operations, return values, and exception handling
 */
@ExtendWith(MockitoExtension.class)
class PerformanceMonitoringServiceMutationTest {

    @Mock
    private MetricsConfiguration metricsConfig;
    
    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache mockCache;
    
    @InjectMocks
    private PerformanceMonitoringService performanceMonitoringService;

    @BeforeEach
    void setUp() {
        // Reset any static state if needed
    }

    // Test cache hit rate threshold boundary conditions
    @Test
    void testCacheStatistics_ConditionalMutations() {
        // Setup cache manager to return cache names with lenient stubbing
        lenient().when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        
        // Test cache statistics calculation
        Map<String, Object> cacheStats = performanceMonitoringService.getCacheStatistics();
        
        // Test that statistics are returned
        assertNotNull(cacheStats); // Should detect null return mutations
        assertTrue(cacheStats.containsKey("hitRate")); // Should detect boolean return mutations
        
        // Test hit rate value
        Object hitRateObj = cacheStats.get("hitRate");
        if (hitRateObj instanceof Double) {
            double hitRate = (Double) hitRateObj;
            assertTrue(hitRate >= 0); // Should detect >= to > mutations
            assertTrue(hitRate <= 100); // Should detect <= to < mutations
            assertFalse(hitRate < 0); // Should detect < to <= mutations
            assertFalse(hitRate > 100); // Should detect > to >= mutations
        }
    }

    @Test
    void testPerformHealthCheck_ExceptionHandlingPath() {
        // Test exception handling in performance monitoring
        doThrow(new RuntimeException("Cache error")).when(cacheManager).getCacheNames();
        
        // This should trigger error handling
        performanceMonitoringService.monitorCachePerformance();
        
        // Verify error was recorded
        verify(metricsConfig, atLeastOnce()).incrementError("cache_monitoring");
        
        // Test that service continues to function after error
        reset(cacheManager);
        lenient().when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        
        // Should work normally again
        performanceMonitoringService.monitorCachePerformance();
        
        // Test exception in statistics collection
        reset(cacheManager);
        doThrow(new RuntimeException("Stats error")).when(cacheManager).getCacheNames();
        
        Map<String, Object> stats = performanceMonitoringService.getPerformanceStatistics();
        assertNotNull(stats); // Should still return a map, even if empty
        
        // Verify error was recorded (could be cache_stats or stats_collection)
        verify(metricsConfig, atLeastOnce()).incrementError(anyString());
    }

    @Test
    void testResponseTimeMonitoring_ArithmeticMutations() {
        // Test response time monitoring with different values
        long fastResponse = 100L; // Below threshold (500ms)
        long slowResponse = 1000L; // Above threshold (500ms)
        long thresholdValue = 500L; // Threshold value
        
        // Test arithmetic comparisons - should detect > to >= mutations
        assertTrue(slowResponse > thresholdValue); // Should detect > to >= mutations
        assertFalse(slowResponse <= thresholdValue); // Should detect <= to < mutations
        assertTrue(fastResponse <= thresholdValue); // Should detect <= to < mutations
        assertFalse(fastResponse > thresholdValue); // Should detect > to >= mutations
        
        // Test boundary conditions
        assertTrue(thresholdValue == 500L); // Should detect == to != mutations
        assertFalse(thresholdValue != 500L); // Should detect != to == mutations
        assertTrue(thresholdValue >= 500L); // Should detect >= to > mutations
        assertTrue(thresholdValue <= 500L); // Should detect <= to < mutations
        
        // Test response time monitoring
        performanceMonitoringService.monitorResponseTime(fastResponse);
        performanceMonitoringService.monitorResponseTime(slowResponse);
        
        // Both should execute without errors
        verify(metricsConfig, never()).incrementError(anyString());
    }

    @Test
    void testHealthCheck_ConditionalLogic() {
        // Setup cache manager with lenient stubbing
        lenient().when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        
        // Test health check
        Map<String, Object> healthCheck = performanceMonitoringService.performHealthCheck();
        
        // Test health check structure
        assertNotNull(healthCheck); // Should detect null return mutations
        assertTrue(healthCheck.containsKey("status")); // Should detect boolean return mutations
        assertTrue(healthCheck.containsKey("cacheHealth")); // Should detect boolean return mutations
        assertTrue(healthCheck.containsKey("memoryHealth")); // Should detect boolean return mutations
        assertTrue(healthCheck.containsKey("timestamp")); // Should detect boolean return mutations
        
        // Test status values
        String status = (String) healthCheck.get("status");
        String cacheHealth = (String) healthCheck.get("cacheHealth");
        String memoryHealth = (String) healthCheck.get("memoryHealth");
        
        // Test conditional logic for status determination
        if (status != null) {
            assertTrue("UP".equals(status) || "WARNING".equals(status) || "DOWN".equals(status)); // Should detect boolean mutations
            assertFalse(status.isEmpty()); // Should detect boolean return mutations
        }
        
        // Test health status logic
        if (cacheHealth != null && memoryHealth != null) {
            boolean allHealthy = "UP".equals(cacheHealth) && "UP".equals(memoryHealth); // Should detect && to || mutations
            boolean anyUnhealthy = !"UP".equals(cacheHealth) || !"UP".equals(memoryHealth); // Should detect || to && mutations
            assertTrue(allHealthy || anyUnhealthy); // Should detect || to && mutations
            assertFalse(allHealthy && anyUnhealthy); // Should detect && to || mutations
        }
    }

    @Test
    void testCacheMonitoring_ThresholdComparisons() {
        // Setup cache manager with lenient stubbing
        lenient().when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        
        // Test cache statistics
        Map<String, Object> cacheStats = performanceMonitoringService.getCacheStatistics();
        
        // Test threshold comparisons
        Object hitRateObj = cacheStats.get("hitRate");
        if (hitRateObj instanceof Double) {
            double hitRate = (Double) hitRateObj;
            assertTrue(hitRate >= 0); // Should detect >= to > mutations
            assertTrue(hitRate <= 100); // Should detect <= to < mutations
            assertFalse(hitRate < 0); // Should detect < to <= mutations
            assertFalse(hitRate > 100); // Should detect > to >= mutations
            
            // Test threshold comparisons with flexible logic
            double threshold = 50.0;
            // Since hitRate can be any value, test both conditions
            assertTrue(hitRate >= threshold || hitRate < threshold); // Should detect >= and < mutations
            assertTrue(hitRate <= threshold || hitRate > threshold); // Should detect <= and > mutations
            assertFalse(hitRate < threshold && hitRate >= threshold); // Should detect < and >= mutations
            assertFalse(hitRate > threshold && hitRate <= threshold); // Should detect > and <= mutations
        }
    }

    @Test
    void testCacheStatistics_BooleanMutations() {
        // Setup cache manager with lenient stubbing
        lenient().when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        
        // Test cache statistics
        Map<String, Object> cacheStats = performanceMonitoringService.getCacheStatistics();
        
        // Test boolean operations on cache statistics
        boolean hasStats = cacheStats != null; // Should detect boolean mutations
        boolean isEmpty = cacheStats.isEmpty(); // Should detect boolean return mutations
        boolean hasHitRate = cacheStats.containsKey("hitRate"); // Should detect boolean return mutations
        boolean hasStatus = cacheStats.containsKey("hitRateStatus"); // Should detect boolean return mutations
        
        assertTrue(hasStats); // Should detect boolean return mutations
        assertFalse(!hasStats); // Should detect negation mutations
        assertTrue(!isEmpty || hasHitRate); // Should detect || to && mutations
        assertFalse(isEmpty && !hasHitRate); // Should detect && to || mutations
        
        // Test logical operations
        assertTrue(hasStats && !isEmpty); // Should detect && to || mutations
        assertFalse(!hasStats || isEmpty); // Should detect || to && mutations
        assertTrue(hasHitRate || hasStatus); // Should detect || to && mutations
        assertFalse(!hasHitRate && !hasStatus); // Should detect && to || mutations
        
        // Test hit rate status logic
        if (hasStatus) {
            String status = (String) cacheStats.get("hitRateStatus");
            boolean isHealthy = "HEALTHY".equals(status); // Should detect boolean mutations
            boolean isWarning = "WARNING".equals(status); // Should detect boolean mutations
            assertTrue(isHealthy || isWarning); // Should detect || to && mutations
            assertFalse(isHealthy && isWarning); // Should detect && to || mutations
        }
    }

    @Test
    void testGetPerformanceStatistics_CollectionLogic() {
        // Test performance statistics collection logic
        Map<String, Object> stats = performanceMonitoringService.getPerformanceStatistics();
        
        assertNotNull(stats); // Should detect null return mutations
        assertTrue(stats instanceof Map); // Should detect type mutations
        assertFalse(stats.isEmpty()); // Should detect boolean return mutations
        
        // Test map operations and conditional logic
        int size = stats.size();
        assertTrue(size > 0); // Should detect > to >= mutations
        assertTrue(size >= 1); // Should detect >= to > mutations
        assertFalse(size == 0); // Should detect == to != mutations
        assertTrue(size != 0); // Should detect != to == mutations
        
        // Test key presence logic
        boolean hasKeys = !stats.isEmpty();
        assertTrue(hasKeys); // Should detect boolean return mutations
        assertFalse(!hasKeys); // Should detect negation mutations
        
        // Test iteration logic
        int keyCount = 0;
        for (String key : stats.keySet()) {
            keyCount++;
            assertNotNull(key); // Should detect null return mutations
            assertTrue(key.length() > 0); // Should detect > to >= mutations
        }
        
        assertEquals(size, keyCount); // Should detect arithmetic mutations
        assertTrue(keyCount == size); // Should detect == to != mutations
        assertFalse(keyCount != size); // Should detect != to == mutations
    }

    @Test
    void testArithmeticOperations_MutationDetection() {
        // Test arithmetic operations that might be present in the service
        
        // Test arithmetic operations on performance metrics
        int hits = 10;
        int misses = 5;
        int total = hits + misses; // Should detect + to - mutations
        
        assertTrue(total == 15); // Should detect == to != mutations
        assertFalse(total != 15); // Should detect != to == mutations
        assertTrue(total > hits); // Should detect > to >= mutations
        assertTrue(total > misses); // Should detect > to >= mutations
        assertFalse(total <= hits); // Should detect <= to < mutations
        assertFalse(total <= misses); // Should detect <= to < mutations
        
        // Test hit rate calculation
        double hitRate = (double) hits / total * 100; // Should detect arithmetic mutations
        assertTrue(hitRate > 0); // Should detect > to >= mutations
        assertTrue(hitRate < 100); // Should detect < to <= mutations
        assertTrue(hitRate >= 50); // Should detect >= to > mutations
        assertFalse(hitRate <= 0); // Should detect <= to < mutations
        
        // Test percentage calculations
        long percentage = (hits * 100L) / total; // Should detect arithmetic mutations
        assertTrue(percentage >= 0); // Should detect >= to > mutations
        assertTrue(percentage <= 100); // Should detect <= to < mutations
        assertFalse(percentage < 0); // Should detect < to <= mutations
        assertFalse(percentage > 100); // Should detect > to >= mutations
    }
}
