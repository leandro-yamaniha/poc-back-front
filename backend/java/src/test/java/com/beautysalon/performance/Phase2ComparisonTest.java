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
 * Phase 2 Comparison Test - Validates performance improvements from optimizations
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class Phase2ComparisonTest {

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

    private List<String> testCustomerIds = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        // Pre-populate test customers for cache and index testing
        for (int i = 0; i < 20; i++) {
            Customer customer = new Customer();
            customer.setId(UUID.randomUUID());
            customer.setName("Phase2 Test Customer " + i);
            customer.setEmail("phase2test" + i + "@example.com");
            customer.setPhone("555000" + String.format("%04d", i));
            customer.setAddress("123 Phase2 St " + i);
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
            testCustomerIds.add(created.getId().toString());
        }
    }

    @Test
    public void testCachePerformanceComparison() throws Exception {
        System.out.println("\n=== Phase 2: Cache Performance Comparison ===");
        
        // Test 1: First read (cache miss)
        long startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/" + testCustomerIds.get(0)))
                .andExpect(status().isOk());
        long cacheMissTime = (System.nanoTime() - startTime) / 1_000_000;
        
        // Test 2: Second read (cache hit)
        startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/" + testCustomerIds.get(0)))
                .andExpect(status().isOk());
        long cacheHitTime = (System.nanoTime() - startTime) / 1_000_000;
        
        // Test 3: Multiple cache hits
        List<Long> cacheHitTimes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            startTime = System.nanoTime();
            mockMvc.perform(get("/api/customers/" + testCustomerIds.get(0)))
                    .andExpect(status().isOk());
            cacheHitTimes.add((System.nanoTime() - startTime) / 1_000_000);
        }
        
        double avgCacheHitTime = cacheHitTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        System.out.println("📊 CACHE PERFORMANCE RESULTS:");
        System.out.println("🔍 Cache Miss Time: " + cacheMissTime + " ms");
        System.out.println("⚡ Cache Hit Time: " + cacheHitTime + " ms");
        System.out.println("📈 Average Cache Hit Time (10 requests): " + String.format("%.2f", avgCacheHitTime) + " ms");
        System.out.println("🚀 Cache Performance Improvement: " + String.format("%.1f%%", ((double)(cacheMissTime - cacheHitTime) / cacheMissTime) * 100));
        
        // Assertions
        assert cacheHitTime < cacheMissTime : "Cache hit should be faster than cache miss";
        assert avgCacheHitTime < cacheMissTime : "Average cache hit time should be faster than cache miss";
        
        System.out.println("✅ Cache Performance Test PASSED!");
    }

    @Test
    public void testIndexPerformanceComparison() throws Exception {
        System.out.println("\n=== Phase 2: Index Performance Comparison ===");
        
        // Test email index performance
        long startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/email/phase2test5@example.com"))
                .andExpect(status().isOk());
        long emailSearchTime = (System.nanoTime() - startTime) / 1_000_000;
        
        // Test name search index performance
        startTime = System.nanoTime();
        mockMvc.perform(get("/api/customers/search?name=Phase2 Test Customer 10"))
                .andExpect(status().isOk());
        long nameSearchTime = (System.nanoTime() - startTime) / 1_000_000;
        
        // Test multiple searches to validate index consistency
        List<Long> emailSearchTimes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            startTime = System.nanoTime();
            mockMvc.perform(get("/api/customers/email/phase2test" + i + "@example.com"))
                    .andExpect(status().isOk());
            emailSearchTimes.add((System.nanoTime() - startTime) / 1_000_000);
        }
        
        double avgEmailSearchTime = emailSearchTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        System.out.println("📊 INDEX PERFORMANCE RESULTS:");
        System.out.println("📧 Email Search Time: " + emailSearchTime + " ms");
        System.out.println("🔍 Name Search Time: " + nameSearchTime + " ms");
        System.out.println("📈 Average Email Search Time (5 requests): " + String.format("%.2f", avgEmailSearchTime) + " ms");
        
        // Performance assertions (with indices, searches should be fast)
        assert emailSearchTime <= 100 : "Email search should be fast with index, got: " + emailSearchTime + "ms";
        assert nameSearchTime <= 150 : "Name search should be fast with index, got: " + nameSearchTime + "ms";
        assert avgEmailSearchTime <= 120 : "Average email search should be consistent, got: " + avgEmailSearchTime + "ms";
        
        System.out.println("✅ Index Performance Test PASSED!");
    }

    @Test
    public void testConnectionPoolPerformance() throws Exception {
        System.out.println("\n=== Phase 2: Connection Pool Performance Test ===");
        
        int numRequests = 100;
        int numThreads = 20; // Test connection pool with moderate concurrency
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
                    
                    // Mix of operations to test connection pool efficiency
                    if (requestIndex % 3 == 0) {
                        // Create operation
                        Customer customer = new Customer();
                        customer.setId(UUID.randomUUID());
                        customer.setName("Pool Test " + requestIndex);
                        customer.setEmail("pooltest" + requestIndex + "@example.com");
                        customer.setPhone("555" + String.format("%07d", requestIndex));
                        customer.setAddress("Pool Address " + requestIndex);
                        customer.setCreatedAt(Instant.now());
                        customer.setUpdatedAt(Instant.now());
                        
                        mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                                .andExpect(status().isCreated());
                    } else if (requestIndex % 3 == 1) {
                        // Read operation (may hit cache)
                        String customerId = testCustomerIds.get(requestIndex % testCustomerIds.size());
                        mockMvc.perform(get("/api/customers/" + customerId))
                                .andExpect(status().isOk());
                    } else {
                        // List operation
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
        assert successRate >= 95.0 : "Success rate should be >= 95% with optimized pool, got: " + successRate + "%";
        assert throughput >= 40.0 : "Throughput should be >= 40 req/s with optimized pool, got: " + throughput;
        assert avgResponseTime <= 300.0 : "Average response time should be <= 300ms with optimized pool, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Connection Pool Performance Test PASSED!");
    }

    @Test
    public void testOverallPhase2Performance() throws Exception {
        System.out.println("\n=== Phase 2: Overall Performance Summary ===");
        
        // Combined test with all optimizations
        int numRequests = 50;
        List<Long> responseTimes = new ArrayList<>();
        
        long overallStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < numRequests; i++) {
            long requestStart = System.nanoTime();
            
            if (i % 4 == 0) {
                // Cache hit test
                String customerId = testCustomerIds.get(i % testCustomerIds.size());
                mockMvc.perform(get("/api/customers/" + customerId))
                        .andExpect(status().isOk());
            } else if (i % 4 == 1) {
                // Index-optimized email search
                mockMvc.perform(get("/api/customers/email/phase2test" + (i % 10) + "@example.com"))
                        .andExpect(status().isOk());
            } else if (i % 4 == 2) {
                // Index-optimized name search
                mockMvc.perform(get("/api/customers/search?name=Phase2 Test Customer " + (i % 10)))
                        .andExpect(status().isOk());
            } else {
                // List all (may use cache)
                mockMvc.perform(get("/api/customers"))
                        .andExpect(status().isOk());
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
        assert throughput >= 100.0 : "Overall throughput should benefit from all optimizations, got: " + throughput;
        assert avgResponseTime <= 100.0 : "Average response time should be improved with all optimizations, got: " + avgResponseTime + "ms";
        
        System.out.println("✅ Phase 2 Overall Performance Test PASSED!");
        System.out.println("\n🎉 ALL PHASE 2 OPTIMIZATIONS VALIDATED SUCCESSFULLY! 🎉");
    }
}
