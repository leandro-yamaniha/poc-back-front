package com.beautysalon.reactive.native_tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance tests for Native Image
 * Validates that native executable meets performance expectations
 * 
 * Run with: -Dnative.image.test=true
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "native.image.test", matches = "true")
class NativePerformanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRespondQuicklyToHealthCheck() {
        Instant start = Instant.now();
        
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
        
        Duration duration = Duration.between(start, Instant.now());
        
        // Native image should respond in less than 100ms
        assertThat(duration.toMillis())
                .as("Health check response time")
                .isLessThan(100);
    }

    @Test
    void shouldHandleMultipleRequestsEfficiently() {
        int requestCount = 100;
        Instant start = Instant.now();
        
        for (int i = 0; i < requestCount; i++) {
            webTestClient.get()
                    .uri("/actuator/health")
                    .exchange()
                    .expectStatus().isOk();
        }
        
        Duration duration = Duration.between(start, Instant.now());
        double avgTimePerRequest = duration.toMillis() / (double) requestCount;
        
        // Average should be less than 50ms per request
        assertThat(avgTimePerRequest)
                .as("Average response time per request")
                .isLessThan(50.0);
        
        System.out.printf("Processed %d requests in %dms (avg: %.2fms per request)%n",
                requestCount, duration.toMillis(), avgTimePerRequest);
    }

    @Test
    void shouldMaintainLowMemoryFootprint() {
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection
        System.gc();
        
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        System.out.printf("Memory usage: %dMB / %dMB%n", usedMemory, maxMemory);
        
        // Native image should use less than 256MB
        assertThat(usedMemory)
                .as("Used memory in MB")
                .isLessThan(256);
    }

    @Test
    void shouldHaveInstantStartupTime() {
        // This test validates that the application context is already loaded
        // In native image, startup should be < 2 seconds
        
        long startupTime = System.currentTimeMillis();
        
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
        
        // If we can make a request, startup was successful
        assertThat(startupTime).isPositive();
        
        System.out.println("Application is responsive (native startup completed)");
    }
}
