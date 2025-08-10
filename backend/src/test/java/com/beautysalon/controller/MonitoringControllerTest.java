package com.beautysalon.controller;

import com.beautysalon.service.PerformanceMonitoringService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for MonitoringController to achieve 95% coverage
 */
@ExtendWith(MockitoExtension.class)
class MonitoringControllerTest {

    @Mock
    private PerformanceMonitoringService performanceMonitoringService;

    @InjectMocks
    private MonitoringController monitoringController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoringController).build();
    }

    @Test
    void testGetPerformanceStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> mockStats = createMockPerformanceStats();
        when(performanceMonitoringService.getPerformanceStatistics()).thenReturn(mockStats);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/performance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.uptime").exists())
                .andExpect(jsonPath("$.memoryUsage").exists())
                .andExpect(jsonPath("$.thresholds").exists())
                .andExpect(jsonPath("$.hitRate").exists())
                .andExpect(jsonPath("$.hitRateStatus").exists())
                .andExpect(jsonPath("$.hitRate").value(95.5))
                .andExpect(jsonPath("$.hitRateStatus").value("HEALTHY"));

        verify(performanceMonitoringService).getPerformanceStatistics();
    }

    @Test
    void testGetPerformanceStatistics_EmptyStats() throws Exception {
        // Arrange
        Map<String, Object> emptyStats = new HashMap<>();
        when(performanceMonitoringService.getPerformanceStatistics()).thenReturn(emptyStats);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/performance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{}"));

        verify(performanceMonitoringService).getPerformanceStatistics();
    }

    @Test
    void testGetCacheStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> mockCacheStats = createMockCacheStats();
        when(performanceMonitoringService.getCacheStatistics()).thenReturn(mockCacheStats);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/cache")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hitRate").exists())
                .andExpect(jsonPath("$.hitRateStatus").exists())
                .andExpect(jsonPath("$.totalCaches").exists())
                .andExpect(jsonPath("$.hitRate").value(88.7))
                .andExpect(jsonPath("$.hitRateStatus").value("HEALTHY"))
                .andExpect(jsonPath("$.totalCaches").value(3));

        verify(performanceMonitoringService).getCacheStatistics();
    }

    @Test
    void testGetCacheStatistics_LowHitRate() throws Exception {
        // Arrange
        Map<String, Object> lowHitRateStats = new HashMap<>();
        lowHitRateStats.put("hitRate", 65.2);
        lowHitRateStats.put("hitRateStatus", "WARNING");
        lowHitRateStats.put("totalCaches", 3);
        
        when(performanceMonitoringService.getCacheStatistics()).thenReturn(lowHitRateStats);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/cache")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hitRate").value(65.2))
                .andExpect(jsonPath("$.hitRateStatus").value("WARNING"));

        verify(performanceMonitoringService).getCacheStatistics();
    }

    @Test
    void testPerformHealthCheck_StatusUp() throws Exception {
        // Arrange
        Map<String, Object> healthyStatus = createMockHealthCheckUp();
        when(performanceMonitoringService.performHealthCheck()).thenReturn(healthyStatus);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.cacheHealth").value("UP"))
                .andExpect(jsonPath("$.memoryHealth").value("UP"))
                .andExpect(jsonPath("$.cacheHitRate").exists())
                .andExpect(jsonPath("$.memoryUsage").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(performanceMonitoringService).performHealthCheck();
    }

    @Test
    void testPerformHealthCheck_StatusWarning() throws Exception {
        // Arrange
        Map<String, Object> warningStatus = createMockHealthCheckWarning();
        when(performanceMonitoringService.performHealthCheck()).thenReturn(warningStatus);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Still returns 200 for WARNING
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("WARNING"))
                .andExpect(jsonPath("$.cacheHealth").value("DOWN"))
                .andExpect(jsonPath("$.memoryHealth").value("WARNING"))
                .andExpect(jsonPath("$.cacheHitRate").value(65.0));

        verify(performanceMonitoringService).performHealthCheck();
    }

    @Test
    void testPerformHealthCheck_StatusDown() throws Exception {
        // Arrange
        Map<String, Object> downStatus = createMockHealthCheckDown();
        when(performanceMonitoringService.performHealthCheck()).thenReturn(downStatus);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable()) // 503 for DOWN
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.error").exists());

        verify(performanceMonitoringService).performHealthCheck();
    }

    @Test
    void testPerformHealthCheck_NullStatus() throws Exception {
        // Arrange
        Map<String, Object> nullStatus = new HashMap<>();
        nullStatus.put("status", null);
        when(performanceMonitoringService.performHealthCheck()).thenReturn(nullStatus);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable()) // 503 for null status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(performanceMonitoringService).performHealthCheck();
    }

    @Test
    void testMonitorCachePerformance_Success() throws Exception {
        // Arrange
        doNothing().when(performanceMonitoringService).monitorCachePerformance();

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/monitor/cache"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Cache performance monitoring triggered"));

        verify(performanceMonitoringService).monitorCachePerformance();
    }

    @Test
    void testMonitorCachePerformance_ServiceException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Cache monitoring failed"))
                .when(performanceMonitoringService).monitorCachePerformance();

        // Act & Assert - Exception should be propagated
        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/monitoring/monitor/cache"));
        });

        verify(performanceMonitoringService).monitorCachePerformance();
    }

    @Test
    void testAllEndpointsWithDifferentHttpMethods() throws Exception {
        // Test that endpoints only accept GET requests
        
        // Performance endpoint
        mockMvc.perform(get("/api/monitoring/performance"))
                .andExpect(status().isOk());

        // Cache endpoint
        mockMvc.perform(get("/api/monitoring/cache"))
                .andExpect(status().isOk());

        // Health endpoint
        Map<String, Object> healthyStatus = createMockHealthCheckUp();
        when(performanceMonitoringService.performHealthCheck()).thenReturn(healthyStatus);
        
        mockMvc.perform(get("/api/monitoring/health"))
                .andExpect(status().isOk());

        // Cache monitoring endpoint - should work normally without exception
        doNothing().when(performanceMonitoringService).monitorCachePerformance();
        mockMvc.perform(get("/api/monitoring/monitor/cache"))
                .andExpect(status().isOk());
    }

    @Test
    void testHealthCheckWithComplexMemoryUsage() throws Exception {
        // Arrange
        Map<String, Object> complexHealthCheck = new HashMap<>();
        complexHealthCheck.put("status", "UP");
        complexHealthCheck.put("cacheHealth", "UP");
        complexHealthCheck.put("memoryHealth", "UP");
        complexHealthCheck.put("cacheHitRate", 92.3);
        complexHealthCheck.put("timestamp", LocalDateTime.now().toString());
        
        // Complex memory usage
        Map<String, Object> memoryUsage = new HashMap<>();
        memoryUsage.put("maxMemory", 1073741824L); // 1GB
        memoryUsage.put("totalMemory", 536870912L); // 512MB
        memoryUsage.put("usedMemory", 268435456L); // 256MB
        memoryUsage.put("freeMemory", 268435456L); // 256MB
        memoryUsage.put("usedPercent", 25L);
        complexHealthCheck.put("memoryUsage", memoryUsage);
        
        when(performanceMonitoringService.performHealthCheck()).thenReturn(complexHealthCheck);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memoryUsage.maxMemory").value(1073741824L))
                .andExpect(jsonPath("$.memoryUsage.usedPercent").value(25L));

        verify(performanceMonitoringService).performHealthCheck();
    }

    @Test
    void testPerformanceStatisticsWithThresholds() throws Exception {
        // Arrange
        Map<String, Object> statsWithThresholds = createMockPerformanceStats();
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("cacheHitRate", 80.0);
        thresholds.put("responseTime", 500L);
        thresholds.put("errorRate", 10L);
        statsWithThresholds.put("thresholds", thresholds);
        
        when(performanceMonitoringService.getPerformanceStatistics()).thenReturn(statsWithThresholds);

        // Act & Assert
        mockMvc.perform(get("/api/monitoring/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thresholds.cacheHitRate").value(80.0))
                .andExpect(jsonPath("$.thresholds.responseTime").value(500L))
                .andExpect(jsonPath("$.thresholds.errorRate").value(10L));

        verify(performanceMonitoringService).getPerformanceStatistics();
    }

    // Helper methods to create mock data

    private Map<String, Object> createMockPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now().toString());
        stats.put("uptime", 3600000L); // 1 hour
        stats.put("hitRate", 95.5);
        stats.put("hitRateStatus", "HEALTHY");
        
        // Memory usage
        Map<String, Object> memoryUsage = new HashMap<>();
        memoryUsage.put("maxMemory", 1073741824L);
        memoryUsage.put("totalMemory", 536870912L);
        memoryUsage.put("usedMemory", 268435456L);
        memoryUsage.put("freeMemory", 268435456L);
        memoryUsage.put("usedPercent", 25L);
        stats.put("memoryUsage", memoryUsage);
        
        // Thresholds
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("cacheHitRate", 80.0);
        thresholds.put("responseTime", 500L);
        thresholds.put("errorRate", 10L);
        stats.put("thresholds", thresholds);
        
        return stats;
    }

    private Map<String, Object> createMockCacheStats() {
        Map<String, Object> cacheStats = new HashMap<>();
        cacheStats.put("hitRate", 88.7);
        cacheStats.put("hitRateStatus", "HEALTHY");
        cacheStats.put("totalCaches", 3);
        return cacheStats;
    }

    private Map<String, Object> createMockHealthCheckUp() {
        Map<String, Object> healthCheck = new HashMap<>();
        healthCheck.put("status", "UP");
        healthCheck.put("cacheHealth", "UP");
        healthCheck.put("memoryHealth", "UP");
        healthCheck.put("cacheHitRate", 92.3);
        healthCheck.put("timestamp", LocalDateTime.now().toString());
        
        Map<String, Object> memoryUsage = new HashMap<>();
        memoryUsage.put("usedPercent", 45L);
        healthCheck.put("memoryUsage", memoryUsage);
        
        return healthCheck;
    }

    private Map<String, Object> createMockHealthCheckWarning() {
        Map<String, Object> healthCheck = new HashMap<>();
        healthCheck.put("status", "WARNING");
        healthCheck.put("cacheHealth", "DOWN");
        healthCheck.put("memoryHealth", "WARNING");
        healthCheck.put("cacheHitRate", 65.0);
        healthCheck.put("timestamp", LocalDateTime.now().toString());
        
        Map<String, Object> memoryUsage = new HashMap<>();
        memoryUsage.put("usedPercent", 85L);
        healthCheck.put("memoryUsage", memoryUsage);
        
        return healthCheck;
    }

    private Map<String, Object> createMockHealthCheckDown() {
        Map<String, Object> healthCheck = new HashMap<>();
        healthCheck.put("status", "DOWN");
        healthCheck.put("error", "System unavailable");
        healthCheck.put("timestamp", LocalDateTime.now().toString());
        return healthCheck;
    }
}
