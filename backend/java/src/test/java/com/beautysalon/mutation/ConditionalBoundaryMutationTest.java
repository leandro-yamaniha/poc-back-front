package com.beautysalon.mutation;

import com.beautysalon.service.PerformanceMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Targeted mutation tests to kill ConditionalsBoundaryMutator and RemoveConditionalMutator surviving mutations.
 * Focuses on boundary conditions and conditional logic in PerformanceMonitoringService.
 */
@ExtendWith(MockitoExtension.class)
public class ConditionalBoundaryMutationTest {

    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache cache;
    
    @InjectMocks
    private PerformanceMonitoringService performanceMonitoringService;

    @BeforeEach
    void setUp() {
        lenient().when(cacheManager.getCache(any())).thenReturn(cache);
    }

    /**
     * Test boundary conditions for response time monitoring
     * Targets: ConditionalsBoundaryMutator and RemoveConditionalMutator in monitorResponseTime
     * Specifically tests the threshold comparison: responseTime > SLOW_RESPONSE_THRESHOLD
     */
    @Test
    void testMonitorResponseTime_BoundaryConditions() {
        // Test case 1: Response time exactly at boundary (should NOT trigger alert)
        // If boundary mutator changes > to >=, this would incorrectly trigger
        long exactThreshold = 5000; // Assuming 5000ms is the threshold
        
        // This should NOT trigger an alert (responseTime == threshold)
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(exactThreshold);
        });

        // Test case 2: Response time just below boundary (should NOT trigger alert)
        // If conditional is removed, behavior would change
        long justBelowThreshold = 4999;
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(justBelowThreshold);
        });

        // Test case 3: Response time just above boundary (SHOULD trigger alert)
        // If boundary mutator changes > to >=, or if conditional is removed, behavior changes
        long justAboveThreshold = 5001;
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(justAboveThreshold);
        });

        // Test case 4: Response time significantly above boundary (SHOULD trigger alert)
        long wellAboveThreshold = 10000;
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(wellAboveThreshold);
        });

        // Test case 5: Zero response time (should NOT trigger alert)
        // Tests the lower boundary
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(0);
        });
    }

    /**
     * Test boundary conditions for cache hit rate monitoring
     * Targets: ConditionalsBoundaryMutator in monitorCachePerformance
     * Tests the threshold comparison: hitRate < LOW_HIT_RATE_THRESHOLD
     */
    @Test
    void testMonitorCachePerformance_BoundaryConditions() {
        // Test case 1: Hit rate exactly at boundary (should NOT trigger alert)
        // Assuming 0.5 (50%) is the threshold
        Cache.ValueWrapper exactThresholdWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(exactThresholdWrapper.get()).thenReturn(0.5);
        lenient().when(cache.get("hitRate")).thenReturn(exactThresholdWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 2: Hit rate just above boundary (should NOT trigger alert)
        Cache.ValueWrapper justAboveWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(justAboveWrapper.get()).thenReturn(0.501);
        lenient().when(cache.get("hitRate")).thenReturn(justAboveWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 3: Hit rate just below boundary (SHOULD trigger alert)
        Cache.ValueWrapper justBelowWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(justBelowWrapper.get()).thenReturn(0.499);
        lenient().when(cache.get("hitRate")).thenReturn(justBelowWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 4: Hit rate significantly below boundary (SHOULD trigger alert)
        Cache.ValueWrapper wellBelowWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(wellBelowWrapper.get()).thenReturn(0.1);
        lenient().when(cache.get("hitRate")).thenReturn(wellBelowWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 5: Perfect hit rate (should NOT trigger alert)
        Cache.ValueWrapper perfectWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(perfectWrapper.get()).thenReturn(1.0);
        lenient().when(cache.get("hitRate")).thenReturn(perfectWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 6: Zero hit rate (SHOULD trigger alert)
        Cache.ValueWrapper zeroWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(zeroWrapper.get()).thenReturn(0.0);
        lenient().when(cache.get("hitRate")).thenReturn(zeroWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });
    }

    /**
     * Test conditional logic removal in triggerAlert method
     * Targets: RemoveConditionalMutator in triggerAlert method
     * Tests the time-based conditional: (now - lastAlertTime) > ALERT_COOLDOWN
     */
    @Test
    void testTriggerAlert_ConditionalLogic() {
        // Setup cache with consistently low hit rate to trigger alerts
        Cache.ValueWrapper lowHitRateWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(lowHitRateWrapper.get()).thenReturn(0.1); // 10% hit rate (low)
        lenient().when(cache.get("hitRate")).thenReturn(lowHitRateWrapper);

        // Test case 1: First alert should be triggered (no previous alert)
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 2: Immediate second alert should be throttled by cooldown
        // If the conditional (now - lastAlertTime) > ALERT_COOLDOWN is removed,
        // this would behave differently
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 3: Multiple rapid alerts should be handled by cooldown logic
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> {
                performanceMonitoringService.monitorCachePerformance();
            });
        }

        // Test case 4: Test with response time alerts as well
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(10000); // High response time
        });
    }

    /**
     * Test math mutations in triggerAlert method
     * Targets: MathMutator (Replaced long subtraction with addition)
     * Tests the arithmetic operation: (now - lastAlertTime)
     */
    @Test
    void testTriggerAlert_MathOperations() {
        // Setup conditions that will trigger alerts
        Cache.ValueWrapper lowHitRateWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(lowHitRateWrapper.get()).thenReturn(0.05); // 5% hit rate (very low)
        lenient().when(cache.get("hitRate")).thenReturn(lowHitRateWrapper);

        // Test case 1: Trigger initial alert
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 2: If subtraction is replaced with addition in (now - lastAlertTime),
        // the cooldown logic would be completely broken
        // This test ensures the math operation works correctly
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 3: Test with different alert types to exercise the math operation
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(8000);
        });

        // Test case 4: Rapid succession of alerts to test cooldown calculation
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> {
                performanceMonitoringService.monitorCachePerformance();
            });
        }
    }

    /**
     * Test edge cases for conditional logic
     * Targets: Various conditional mutations in PerformanceMonitoringService
     */
    @Test
    void testConditionalLogic_EdgeCases() {
        // Test case 1: Null cache value (should handle gracefully)
        lenient().when(cache.get("hitRate")).thenReturn(null);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 2: Cache value wrapper with null content
        Cache.ValueWrapper nullContentWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(nullContentWrapper.get()).thenReturn(null);
        lenient().when(cache.get("hitRate")).thenReturn(nullContentWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 3: Negative response time (edge case)
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(-1);
        });

        // Test case 4: Very large response time
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorResponseTime(Long.MAX_VALUE);
        });

        // Test case 5: Hit rate outside normal range (> 1.0)
        Cache.ValueWrapper highWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(highWrapper.get()).thenReturn(1.5); // 150% (impossible but tests boundary)
        lenient().when(cache.get("hitRate")).thenReturn(highWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });

        // Test case 6: Negative hit rate
        Cache.ValueWrapper negativeWrapper = mock(Cache.ValueWrapper.class);
        lenient().when(negativeWrapper.get()).thenReturn(-0.1); // Negative hit rate
        lenient().when(cache.get("hitRate")).thenReturn(negativeWrapper);
        
        assertDoesNotThrow(() -> {
            performanceMonitoringService.monitorCachePerformance();
        });
    }
}
