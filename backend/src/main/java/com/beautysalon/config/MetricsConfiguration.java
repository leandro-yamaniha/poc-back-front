package com.beautysalon.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Phase 3: Advanced Metrics Configuration
 * Configures custom metrics for performance monitoring, alerting, and dashboards
 */
@Configuration
public class MetricsConfiguration {

    private final MeterRegistry meterRegistry;
    private final CacheManager cacheManager;
    
    // Custom metrics counters
    private final AtomicLong cacheHitCount = new AtomicLong(0);
    private final AtomicLong cacheMissCount = new AtomicLong(0);
    private final AtomicLong databaseQueryCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    @Autowired
    public MetricsConfiguration(MeterRegistry meterRegistry, CacheManager cacheManager) {
        this.meterRegistry = meterRegistry;
        this.cacheManager = cacheManager;
        initializeCustomMetrics();
    }

    private void initializeCustomMetrics() {
        // Cache Hit Rate Gauge
        Gauge.builder("beauty_salon.cache.hit_rate", this, MetricsConfiguration::calculateCacheHitRate)
                .description("Cache hit rate percentage")
                .register(meterRegistry);

        // Cache Hit Counter
        Counter.builder("beauty_salon.cache.hits")
                .description("Total cache hits")
                .register(meterRegistry);

        // Cache Miss Counter
        Counter.builder("beauty_salon.cache.misses")
                .description("Total cache misses")
                .register(meterRegistry);

        // Database Query Counter
        Counter.builder("beauty_salon.database.queries")
                .description("Total database queries executed")
                .register(meterRegistry);

        // Error Counter
        Counter.builder("beauty_salon.errors")
                .description("Total application errors")
                .tag("type", "general")
                .register(meterRegistry);

        // Response Time Timer
        Timer.builder("beauty_salon.response.time")
                .description("API response time")
                .register(meterRegistry);

        // Connection Pool Gauge
        Gauge.builder("beauty_salon.cassandra.connections.active", this, MetricsConfiguration::getActiveCassandraConnections)
                .description("Active Cassandra connections")
                .register(meterRegistry);
    }

    private double calculateCacheHitRate() {
        long hits = cacheHitCount.get();
        long misses = cacheMissCount.get();
        long total = hits + misses;
        return total > 0 ? (double) hits / total * 100.0 : 0.0;
    }

    private double getActiveCassandraConnections() {
        // This would typically integrate with Cassandra driver metrics
        // For now, return a simulated value
        return 8.0; // Based on our connection pool configuration
    }

    // Utility methods for incrementing counters
    public void incrementCacheHit() {
        cacheHitCount.incrementAndGet();
        meterRegistry.counter("beauty_salon.cache.hits").increment();
    }

    public void incrementCacheMiss() {
        cacheMissCount.incrementAndGet();
        meterRegistry.counter("beauty_salon.cache.misses").increment();
    }

    public void incrementDatabaseQuery() {
        databaseQueryCount.incrementAndGet();
        meterRegistry.counter("beauty_salon.database.queries").increment();
    }

    public void incrementError(String errorType) {
        errorCount.incrementAndGet();
        meterRegistry.counter("beauty_salon.errors", "type", errorType).increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordResponseTime(Timer.Sample sample) {
        sample.stop(meterRegistry.timer("beauty_salon.response.time"));
    }

    // Removed circular bean dependency - this class is already a @Configuration
}
