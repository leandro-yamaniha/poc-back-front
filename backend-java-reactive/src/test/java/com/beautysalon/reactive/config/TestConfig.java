package com.beautysalon.reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.mockito.Mockito;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ReactiveCassandraTemplate reactiveCassandraTemplate() {
        return Mockito.mock(ReactiveCassandraTemplate.class);
    }
}
