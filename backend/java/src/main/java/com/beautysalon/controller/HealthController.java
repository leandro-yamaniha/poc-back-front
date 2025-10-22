package com.beautysalon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides application health status and readiness checks
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private CassandraTemplate cassandraTemplate;

    /**
     * Basic health check endpoint
     * Returns 200 OK if application is running
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        health.put("application", "beauty-salon-backend");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    /**
     * Readiness check - validates Cassandra connection
     * Returns 200 OK if ready to serve traffic
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        Map<String, Object> readiness = new HashMap<>();
        
        try {
            // Test Cassandra connection
            cassandraTemplate.getCqlOperations().queryForObject(
                "SELECT release_version FROM system.local", String.class);
            
            readiness.put("status", "READY");
            readiness.put("database", "UP");
            readiness.put("timestamp", Instant.now().toString());
            
            return ResponseEntity.ok(readiness);
            
        } catch (Exception e) {
            readiness.put("status", "NOT_READY");
            readiness.put("database", "DOWN");
            readiness.put("error", e.getMessage());
            readiness.put("timestamp", Instant.now().toString());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(readiness);
        }
    }

    /**
     * Liveness check - simple ping
     * Returns 200 OK if application is alive
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> live() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(liveness);
    }

    /**
     * Detailed health check with component status
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> components = new HashMap<>();
        
        // Check Cassandra
        try {
            String version = cassandraTemplate.getCqlOperations().queryForObject(
                "SELECT release_version FROM system.local", String.class);
            
            Map<String, Object> cassandra = new HashMap<>();
            cassandra.put("status", "UP");
            cassandra.put("version", version);
            components.put("cassandra", cassandra);
            
        } catch (Exception e) {
            Map<String, Object> cassandra = new HashMap<>();
            cassandra.put("status", "DOWN");
            cassandra.put("error", e.getMessage());
            components.put("cassandra", cassandra);
        }
        
        // Overall status
        boolean allUp = components.values().stream()
            .allMatch(c -> ((Map<?, ?>) c).get("status").equals("UP"));
        
        health.put("status", allUp ? "UP" : "DOWN");
        health.put("timestamp", Instant.now().toString());
        health.put("application", "beauty-salon-backend");
        health.put("version", "1.0.0");
        health.put("components", components);
        
        HttpStatus status = allUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }
}
