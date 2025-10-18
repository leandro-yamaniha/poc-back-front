package com.beautysalon.performance;

import com.beautysalon.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 2 Performance Tests - Testing optimizations:
 * - Connection pool optimization
 * - Caching for read operations
 * - Query optimization
 * - Performance monitoring
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class Phase2PerformanceTest {

    @Container
    static final CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
            .withInitScript("init.cql")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", cassandra::getHost);
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "beauty_salon_test");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<String> createdCustomerIds = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        // Pre-populate some customers for cache testing
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();
            customer.setId(UUID.randomUUID());
            customer.setName("Cache Test Customer " + i);
            customer.setEmail("cache" + i + "@test.com");
            customer.setPhone("555000000" + i);
            customer.setAddress("123 Cache St " + i);
            customer.setCreatedAt(Instant.now());
            customer.setUpdatedAt(Instant.now());

            String response = mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customer)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Customer created = objectMapper.readValue(response, Customer.class);
            createdCustomerIds.add(created.getId().toString());
        }
    }

    @Test
    public void testCachePerformance() throws Exception {
        System.out.println("\n=== Phase 2: Cache Performance Test ===");
        
        int numRequests = 100;
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numRequests);
        
        List<Long> responseTimes = new ArrayList<>();
        List<Boolean> results = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        // Test repeated reads (should benefit from cache)
        for (int i = 0; i < numRequests; i++) {
            final int requestIndex = i;
            executor.submit(() -> {
                try {
                    long requestStart = System.nanoTime();
                    
                    // Alternate between different read operations
                    if (requestIndex % 4 == 0) {
                        // Get all customers (cached)
                        mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk());
                    } else if (requestIndex % 4 == 1) {
                        // Get specific customer (cached)
                        String customerId = createdCustomerIds.get(requestIndex % createdCustomerIds.size());
                        mockMvc.perform(get("/api/customers/" + customerId))
                                .andExpect(status().isOk());
                    } else if (requestIndex % 4 == 2) {
                        // Search by name (cached)
                        mockMvc.perform(get("/api/customers/search?name=Cache Test"))
                                .andExpect(status().isOk());
                    } else {
                        // Get by email (cached)
                        mockMvc.perform(get("/api/customers/email/cache0@test.com"))
                                .andExpect(status().isOk());
                    }
                    
                    long requestEnd = System.nanoTime();
                    long responseTime = (requestEnd - requestStart) / 1_000_000; // Convert to ms
                    
                    synchronized (responseTimes) {
                        responseTimes.add(responseTime);
                        results.add(true);
                    }
                    
                } catch (Exception e) {
                    synchronized (results) {
                        results.add(false);
                    }
                    System.err.println("Request failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Calculate metrics
        long successCount = results.stream().mapToLong(b -> b ? 1 : 0).sum();
        double successRate = (successCount * 100.0) / numRequests;
        double throughput = (successCount * 1000.0) / totalTime;
        
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        
        // Print results
        System.out.println("📊 PHASE 2 CACHE PERFORMANCE RESULTS:");
        System.out.println("✅ Success Rate: " + String.format("%.2f%%", successRate) + " (" + successCount + "/" + numRequests + ")");
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⏱️  Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("⚡ Min Response Time: " + minResponseTime + " ms");
        System.out.println("🔥 Max Response Time: " + maxResponseTime + " ms");
        System.out.println("⏰ Total Test Duration: " + totalTime + " ms");
        
        // Performance assertions
        assert successRate >= 95.0 : "Success rate should be >= 95%, got: " + successRate + "%";
        assert throughput >= 50.0 : "Throughput should be >= 50 req/s, got: " + throughput;
        assert avgResponseTime <= 200.0 : "Average response time should be <= 200ms, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Phase 2 Cache Performance Test PASSED!");
    }

    @Test
    public void testConnectionPoolPerformance() throws Exception {
        System.out.println("\n=== Phase 2: Connection Pool Performance Test ===");
        
        int numRequests = 200;
        int numThreads = 50; // Higher concurrency to test connection pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numRequests);
        
        List<Long> responseTimes = new ArrayList<>();
        List<Boolean> results = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        // Test high concurrency operations
        for (int i = 0; i < numRequests; i++) {
            final int requestIndex = i;
            executor.submit(() -> {
                try {
                    long requestStart = System.nanoTime();
                    
                    // Mix of read and write operations to test connection pool
                    if (requestIndex % 3 == 0) {
                        // Create operation
                        Customer customer = new Customer();
                        customer.setId(UUID.randomUUID());
                        customer.setName("Pool Test " + requestIndex);
                        customer.setEmail("pool" + requestIndex + "@test.com");
                        customer.setPhone("555" + String.format("%07d", requestIndex));
                        customer.setAddress("Pool St " + requestIndex);
                        customer.setCreatedAt(Instant.now());
                        customer.setUpdatedAt(Instant.now());
                        
                        mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                                .andExpect(status().isCreated());
                    } else {
                        // Read operation
                        mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk());
                    }
                    
                    long requestEnd = System.nanoTime();
                    long responseTime = (requestEnd - requestStart) / 1_000_000;
                    
                    synchronized (responseTimes) {
                        responseTimes.add(responseTime);
                        results.add(true);
                    }
                    
                } catch (Exception e) {
                    synchronized (results) {
                        results.add(false);
                    }
                    System.err.println("Pool test request failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Calculate metrics
        long successCount = results.stream().mapToLong(b -> b ? 1 : 0).sum();
        double successRate = (successCount * 100.0) / numRequests;
        double throughput = (successCount * 1000.0) / totalTime;
        
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        
        // Print results
        System.out.println("📊 PHASE 2 CONNECTION POOL PERFORMANCE RESULTS:");
        System.out.println("✅ Success Rate: " + String.format("%.2f%%", successRate) + " (" + successCount + "/" + numRequests + ")");
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⏱️  Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("⚡ Min Response Time: " + minResponseTime + " ms");
        System.out.println("🔥 Max Response Time: " + maxResponseTime + " ms");
        System.out.println("⏰ Total Test Duration: " + totalTime + " ms");
        System.out.println("🔗 Concurrency Level: " + numThreads + " threads");
        
        // Performance assertions
        assert successRate >= 95.0 : "Success rate should be >= 95%, got: " + successRate + "%";
        assert throughput >= 30.0 : "Throughput should be >= 30 req/s with high concurrency, got: " + throughput;
        assert avgResponseTime <= 500.0 : "Average response time should be <= 500ms with high concurrency, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Phase 2 Connection Pool Performance Test PASSED!");
    }
}
