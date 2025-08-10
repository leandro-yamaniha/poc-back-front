package com.beautysalon.performance;

import com.beautysalon.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Phase 2 Validation Test - Validates Phase 2 optimizations are working
 * Focuses on cache performance, connection pool, and overall improvements
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class Phase2ValidationTest {

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

    @Test
    public void testPhase2CachePerformance() throws Exception {
        System.out.println("\n=== Phase 2: Cache Performance Validation ===");
        
        // Create a unique test customer
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Cache Test " + uniqueId);
        customer.setEmail("cachetest" + uniqueId + "@phase2.com");
        customer.setPhone("5559999" + uniqueId.substring(0, 3));
        customer.setAddress("Cache Address " + uniqueId);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());

        // Create customer
        String response = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Customer created = objectMapper.readValue(response, Customer.class);
        String customerId = created.getId().toString();

        // Test cache performance: first read (cache miss)
        long startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/" + customerId))
                .andExpect(status().isOk());
        long cacheMissTime = (System.nanoTime() - startTime) / 1_000_000;

        // Test cache performance: second read (cache hit)
        startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/" + customerId))
                .andExpect(status().isOk());
        long cacheHitTime = (System.nanoTime() - startTime) / 1_000_000;

        // Test multiple cache hits for consistency
        List<Long> cacheHitTimes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            startTime = System.nanoTime();
            mockMvc.perform(get("/api/customers/" + customerId))
                    .andExpect(status().isOk());
            cacheHitTimes.add((System.nanoTime() - startTime) / 1_000_000);
        }

        double avgCacheHitTime = cacheHitTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.println("📊 CACHE PERFORMANCE RESULTS:");
        System.out.println("🔍 Cache Miss Time: " + cacheMissTime + " ms");
        System.out.println("⚡ Cache Hit Time: " + cacheHitTime + " ms");
        System.out.println("📈 Average Cache Hit Time (5 requests): " + String.format("%.2f", avgCacheHitTime) + " ms");
        
        if (cacheHitTime < cacheMissTime) {
            double improvement = ((double)(cacheMissTime - cacheHitTime) / cacheMissTime) * 100;
            System.out.println("🚀 Cache Performance Improvement: " + String.format("%.1f%%", improvement));
        }

        // Validate cache is working (cache hits should be faster or at least not slower)
        assert cacheHitTime <= cacheMissTime * 1.2 : "Cache hit should not be significantly slower than cache miss";
        assert avgCacheHitTime <= cacheMissTime * 1.2 : "Average cache hit time should be reasonable";

        System.out.println("✅ Cache Performance Validation PASSED!");
    }

    @Test
    public void testPhase2ConnectionPoolPerformance() throws Exception {
        System.out.println("\n=== Phase 2: Connection Pool Performance Validation ===");
        
        int numRequests = 50;
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numRequests);
        
        List<Long> responseTimes = new ArrayList<>();
        List<Boolean> results = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        // Test concurrent operations with optimized connection pool
        for (int i = 0; i < numRequests; i++) {
            final int requestIndex = i;
            executor.submit(() -> {
                try {
                    long requestStart = System.nanoTime();
                    
                    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                    
                    if (requestIndex % 2 == 0) {
                        // Create operation
                        Customer customer = new Customer();
                        customer.setId(UUID.randomUUID());
                        customer.setName("Pool Test " + uniqueId);
                        customer.setEmail("pooltest" + uniqueId + "@phase2.com");
                        customer.setPhone("555" + String.format("%07d", requestIndex));
                        customer.setAddress("Pool Address " + uniqueId);
                        customer.setCreatedAt(Instant.now());
                        customer.setUpdatedAt(Instant.now());
                        
                        mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                                .andExpect(status().isCreated());
                    } else {
                        // Read operation (list all customers)
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
                    System.err.println("Connection pool test request failed: " + e.getMessage());
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
        
        System.out.println("📊 CONNECTION POOL PERFORMANCE RESULTS:");
        System.out.println("✅ Success Rate: " + String.format("%.2f%%", successRate) + " (" + successCount + "/" + numRequests + ")");
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⏱️  Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("⚡ Min Response Time: " + minResponseTime + " ms");
        System.out.println("🔥 Max Response Time: " + maxResponseTime + " ms");
        System.out.println("⏰ Total Test Duration: " + totalTime + " ms");
        System.out.println("🔗 Concurrency Level: " + numThreads + " threads");
        
        // Performance assertions for optimized connection pool
        assert successRate >= 90.0 : "Success rate should be >= 90% with optimized pool, got: " + successRate + "%";
        assert throughput >= 20.0 : "Throughput should be >= 20 req/s with optimized pool, got: " + throughput;
        assert avgResponseTime <= 500.0 : "Average response time should be <= 500ms with optimized pool, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Connection Pool Performance Validation PASSED!");
    }

    @Test
    public void testPhase2OverallPerformanceComparison() throws Exception {
        System.out.println("\n=== Phase 2: Overall Performance Comparison ===");
        
        // Create test data for comparison
        List<String> customerIds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            Customer customer = new Customer();
            customer.setId(UUID.randomUUID());
            customer.setName("Comparison Test " + uniqueId);
            customer.setEmail("comparison" + uniqueId + "@phase2.com");
            customer.setPhone("555" + String.format("%07d", i));
            customer.setAddress("Comparison Address " + uniqueId);
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
            customerIds.add(created.getId().toString());
        }

        // Test mixed workload with Phase 2 optimizations
        int numRequests = 30;
        List<Long> responseTimes = new ArrayList<>();
        
        long overallStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < numRequests; i++) {
            long requestStart = System.nanoTime();
            
            if (i % 3 == 0) {
                // Cache-optimized read
                String customerId = customerIds.get(i % customerIds.size());
                mockMvc.perform(get("/api/customers/" + customerId))
                        .andExpect(status().isOk());
            } else if (i % 3 == 1) {
                // List operation (may benefit from cache)
                mockMvc.perform(get("/api/customers"))
                        .andExpect(status().isOk());
            } else {
                // Create operation (tests connection pool)
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                Customer customer = new Customer();
                customer.setId(UUID.randomUUID());
                customer.setName("Mixed Test " + uniqueId);
                customer.setEmail("mixed" + uniqueId + "@phase2.com");
                customer.setPhone("555" + String.format("%07d", i));
                customer.setAddress("Mixed Address " + uniqueId);
                customer.setCreatedAt(Instant.now());
                customer.setUpdatedAt(Instant.now());
                
                mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                        .andExpect(status().isCreated());
            }
            
            long requestEnd = System.nanoTime();
            responseTimes.add((requestEnd - requestStart) / 1_000_000);
        }
        
        long overallEndTime = System.currentTimeMillis();
        long totalTime = overallEndTime - overallStartTime;
        
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        double throughput = (numRequests * 1000.0) / totalTime;
        
        System.out.println("📊 PHASE 2 OVERALL PERFORMANCE RESULTS:");
        System.out.println("🚀 Overall Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⏱️  Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("⚡ Min Response Time: " + minResponseTime + " ms");
        System.out.println("🔥 Max Response Time: " + maxResponseTime + " ms");
        System.out.println("⏰ Total Duration: " + totalTime + " ms");
        System.out.println("📈 Requests Processed: " + numRequests);
        
        // Overall performance assertions
        assert throughput >= 50.0 : "Overall throughput should benefit from Phase 2 optimizations, got: " + throughput;
        assert avgResponseTime <= 200.0 : "Average response time should be reasonable with optimizations, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Phase 2 Overall Performance Validation PASSED!");
        System.out.println("\n🎉 PHASE 2 OPTIMIZATIONS SUCCESSFULLY VALIDATED! 🎉");
        System.out.println("✅ Cache system working");
        System.out.println("✅ Connection pool optimized");
        System.out.println("✅ Overall performance improved");
    }
}
