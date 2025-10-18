package com.beautysalon.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.InetSocketAddress;

/**
 * Custom Cassandra configuration that creates a session without specifying a keyspace initially.
 * This allows the migration runner to create the keyspace before the session is used.
 */
@Configuration
public class CassandraConfig {

    @Value("${spring.cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${spring.cassandra.local-datacenter:datacenter1}")
    private String localDatacenter;

    /**
     * Creates a CqlSession without specifying a keyspace initially.
     * The migration runner will handle keyspace creation and switching.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public CqlSession cqlSession() {
        CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter);

        // Note: Advanced timeout configuration would require DriverConfigLoader setup
        // For now, using default timeouts from the driver

        return builder.build();
    }
}
