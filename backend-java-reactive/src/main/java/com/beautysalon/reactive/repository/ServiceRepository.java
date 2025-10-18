package com.beautysalon.reactive.repository;

import com.beautysalon.reactive.model.Service;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ServiceRepository extends ReactiveCassandraRepository<Service, UUID> {
    
    Flux<Service> findByActiveTrue();
    
    Flux<Service> findByCategoryAndActiveTrue(String category);
    
    Flux<Service> findByCategory(String category);
    
    Flux<Service> findByNameContainingIgnoreCase(String name);
    
    // Removed ORDER BY createdAt - not supported without partition key restriction
    // Use findAll() and sort in service layer if needed
}
