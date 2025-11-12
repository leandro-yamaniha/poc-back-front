package com.beautysalon.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
@EnableReactiveCassandraRepositories
public class BeautySalonReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySalonReactiveApplication.class, args);
    }
}

