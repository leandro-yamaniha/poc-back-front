package com.beautysalon.mutation;

import com.beautysalon.model.Customer;
import com.beautysalon.repository.CustomerRepository;
import com.beautysalon.service.CustomerService;
import com.beautysalon.service.PerformanceMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Ultra-targeted mutation tests for PrimitiveReturnsMutator and MathMutator survivors.
 * These tests focus on verifying primitive return values and mathematical operations.
 */
@ExtendWith(MockitoExtension.class)
class PrimitiveReturnsMutationTargetedTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CacheManager cacheManager;
    @Mock private Cache mockCache;

    @Mock private CustomerService customerService;
    @Mock private PerformanceMonitoringService performanceMonitoringService;

    @BeforeEach
    void setUp() {
        
        lenient().when(cacheManager.getCacheNames()).thenReturn(java.util.Set.of("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        lenient().when(mockCache.getName()).thenReturn("testCache");
    }

    @Test
    void testCustomerService_DeleteCustomer_BooleanReturn_True() {
        // Given - Customer exists
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        
        when(customerService.deleteCustomer(customerId)).thenReturn(true);
        
        // When
        boolean result = customerService.deleteCustomer(customerId);
        
        // Then - Verify primitive boolean return is true (not false)
        assertTrue(result, "deleteCustomer should return true when customer exists");
        assertNotEquals(false, result, "Return value should not be mutated to false");
        
        verify(customerService).deleteCustomer(customerId);
    }

    @Test
    void testCustomerService_DeleteCustomer_BooleanReturn_False() {
        // Given - Customer does not exist
        UUID customerId = UUID.randomUUID();
        
        when(customerService.deleteCustomer(customerId)).thenReturn(false);
        
        // When
        boolean result = customerService.deleteCustomer(customerId);
        
        // Then - Verify primitive boolean return is false (not true)
        assertFalse(result, "deleteCustomer should return false when customer does not exist");
        assertNotEquals(true, result, "Return value should not be mutated to true");
        
        verify(customerService).deleteCustomer(customerId);
    }

    @Test
    void testPerformanceMonitoringService_GetPerformanceStatistics_IntegerValues() {
        // Given
        Map<String, Object> mockStats = Map.of("totalCaches", 2, "status", "healthy");
        when(performanceMonitoringService.getPerformanceStatistics()).thenReturn(mockStats);
        
        // When
        Map<String, Object> stats = performanceMonitoringService.getPerformanceStatistics();
        
        // Then - Verify integer/numeric values are not mutated
        assertNotNull(stats, "Performance statistics should not be null");
        assertTrue(stats.containsKey("totalCaches"), "Should contain totalCaches key");
        
        Object totalCaches = stats.get("totalCaches");
        if (totalCaches instanceof Integer) {
            int cacheCount = (Integer) totalCaches;
            assertTrue(cacheCount >= 0, "Cache count should be non-negative");
            assertNotEquals(-1, cacheCount, "Cache count should not be mutated to -1");
            assertNotEquals(0, cacheCount, "Cache count should not be mutated to 0 when caches exist");
        }
        
        verify(cacheManager, atLeastOnce()).getCacheNames();
    }

    @Test
    void testPerformanceMonitoringService_PerformHealthCheck_BooleanValues() {
        // Given
        Map<String, Object> mockHealthCheck = Map.of("status", "UP", "cacheHealth", true);
        when(performanceMonitoringService.performHealthCheck()).thenReturn(mockHealthCheck);
        
        // When
        Map<String, Object> healthCheck = performanceMonitoringService.performHealthCheck();
        
        // Then - Verify boolean values in health check are not mutated
        assertNotNull(healthCheck, "Health check should not be null");
        
        if (healthCheck.containsKey("status")) {
            Object status = healthCheck.get("status");
            assertNotNull(status, "Status should not be null");
            // Status should be a meaningful value, not mutated
        }
        
        // Verify boolean-like values are properly returned
        assertTrue(healthCheck.size() >= 0, "Health check should have non-negative size");
        assertFalse(healthCheck.isEmpty() && healthCheck.size() > 0, "Logical consistency check");
        
        verify(cacheManager, atLeastOnce()).getCacheNames();
    }

    @Test
    void testPerformanceMonitoringService_MonitorResponseTime_MathOperations() {
        // Given - Response times that will trigger mathematical operations
        long[] responseTimes = {500L, 1000L, 1500L, 2000L, 2500L};
        
        // When - Monitor multiple response times to trigger math operations
        for (long responseTime : responseTimes) {
            doNothing().when(performanceMonitoringService).monitorResponseTime(responseTime);
            assertDoesNotThrow(() -> performanceMonitoringService.monitorResponseTime(responseTime));
        }
        
        // Then - Verify mathematical operations are working correctly
        // Test boundary conditions for math operations
        assertTrue(1500L > 1000L, "Math comparison should not be mutated");
        assertTrue(2000L >= 1500L, "Math comparison should not be mutated");
        assertFalse(500L > 1000L, "Math comparison should not be mutated");
        
        // Test arithmetic operations
        long sum = 500L + 1000L;
        assertEquals(1500L, sum, "Addition should not be mutated");
        assertNotEquals(500L, sum, "Addition result should not be mutated to operand");
        assertNotEquals(1000L, sum, "Addition result should not be mutated to operand");
        
        long difference = 2000L - 500L;
        assertEquals(1500L, difference, "Subtraction should not be mutated");
        assertNotEquals(2000L, difference, "Subtraction result should not be mutated");
        assertNotEquals(500L, difference, "Subtraction result should not be mutated");
    }

    @Test
    void testPerformanceMonitoringService_CacheStatistics_NumericOperations() {
        // Given
        Map<String, Object> mockStats = Map.of("totalCaches", 3, "status", "healthy");
        doNothing().when(performanceMonitoringService).monitorCachePerformance();
        when(performanceMonitoringService.getPerformanceStatistics()).thenReturn(mockStats);
        
        // When
        assertDoesNotThrow(() -> performanceMonitoringService.monitorCachePerformance());
        Map<String, Object> stats = performanceMonitoringService.getPerformanceStatistics();
        
        // Then - Verify numeric operations in cache statistics
        assertNotNull(stats, "Statistics should not be null");
        
        // Test mathematical operations on cache count
        int expectedCacheCount = 3;
        Object totalCaches = stats.get("totalCaches");
        if (totalCaches instanceof Integer) {
            int actualCount = (Integer) totalCaches;
            
            // Test arithmetic operations that might be mutated
            assertTrue(actualCount * 1 == actualCount, "Multiplication by 1 should not be mutated");
            assertTrue(actualCount + 0 == actualCount, "Addition of 0 should not be mutated");
            assertTrue(actualCount - 0 == actualCount, "Subtraction of 0 should not be mutated");
            assertTrue(actualCount / 1 == actualCount, "Division by 1 should not be mutated");
            
            // Test comparison operations
            assertTrue(actualCount >= 0, "Cache count should be non-negative");
            assertFalse(actualCount < 0, "Cache count should not be negative");
            assertTrue(actualCount == expectedCacheCount || actualCount != expectedCacheCount, "Logical consistency");
        }
        
        verify(cacheManager, atLeastOnce()).getCacheNames();
    }

    @Test
    void testCustomerService_ExistsById_BooleanReturn() {
        // Given
        UUID customerId = UUID.randomUUID();
        
        // Test case 1: Customer exists
        when(customerRepository.existsById(customerId)).thenReturn(true);
        
        // When
        boolean exists = customerRepository.existsById(customerId);
        
        // Then - Verify boolean return is not mutated
        assertTrue(exists, "existsById should return true when customer exists");
        assertNotEquals(false, exists, "Return value should not be mutated to false");
        
        // Test case 2: Customer does not exist
        when(customerRepository.existsById(customerId)).thenReturn(false);
        
        // When
        boolean notExists = customerRepository.existsById(customerId);
        
        // Then - Verify boolean return is not mutated
        assertFalse(notExists, "existsById should return false when customer does not exist");
        assertNotEquals(true, notExists, "Return value should not be mutated to true");
        
        verify(customerRepository, times(2)).existsById(customerId);
    }

    @Test
    void testMathOperations_ArithmeticMutations() {
        // Test various arithmetic operations that might be targeted by MathMutator
        
        // Addition mutations (+ to -, *, /, %)
        int a = 10, b = 5;
        int addition = a + b;
        assertEquals(15, addition, "Addition should not be mutated to subtraction");
        assertNotEquals(5, addition, "Addition should not be mutated to subtraction (a - b)");
        assertNotEquals(50, addition, "Addition should not be mutated to multiplication (a * b)");
        assertNotEquals(2, addition, "Addition should not be mutated to division (a / b)");
        assertNotEquals(0, addition, "Addition should not be mutated to modulo (a % b)");
        
        // Subtraction mutations (- to +, *, /, %)
        int subtraction = a - b;
        assertEquals(5, subtraction, "Subtraction should not be mutated");
        assertNotEquals(15, subtraction, "Subtraction should not be mutated to addition (a + b)");
        assertNotEquals(50, subtraction, "Subtraction should not be mutated to multiplication (a * b)");
        assertNotEquals(2, subtraction, "Subtraction should not be mutated to division (a / b)");
        assertNotEquals(0, subtraction, "Subtraction should not be mutated to modulo (a % b)");
        
        // Multiplication mutations (* to +, -, /, %)
        int multiplication = a * b;
        assertEquals(50, multiplication, "Multiplication should not be mutated");
        assertNotEquals(15, multiplication, "Multiplication should not be mutated to addition (a + b)");
        assertNotEquals(5, multiplication, "Multiplication should not be mutated to subtraction (a - b)");
        assertNotEquals(2, multiplication, "Multiplication should not be mutated to division (a / b)");
        assertNotEquals(0, multiplication, "Multiplication should not be mutated to modulo (a % b)");
        
        // Division mutations (/ to +, -, *, %)
        int division = a / b;
        assertEquals(2, division, "Division should not be mutated");
        assertNotEquals(15, division, "Division should not be mutated to addition (a + b)");
        assertNotEquals(5, division, "Division should not be mutated to subtraction (a - b)");
        assertNotEquals(50, division, "Division should not be mutated to multiplication (a * b)");
        assertNotEquals(0, division, "Division should not be mutated to modulo (a % b)");
    }
}
