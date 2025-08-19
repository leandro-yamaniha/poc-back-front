package com.beautysalon.reactive.repository;

import com.beautysalon.reactive.model.Customer;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CustomerRepository extends ReactiveCassandraRepository<Customer, UUID> {
    
    Flux<Customer> findByNameContainingIgnoreCase(String name);
    
    Mono<Customer> findByEmail(String email);
    
    Flux<Customer> findAllByOrderByCreatedAtDesc();
}
