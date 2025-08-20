package com.beautysalon.reactive.repository;

import com.beautysalon.reactive.model.Staff;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface StaffRepository extends ReactiveCassandraRepository<Staff, UUID> {
    
    Flux<Staff> findByActiveTrue();
    
    Flux<Staff> findByRole(String role);
    
    Flux<Staff> findByRoleAndActiveTrue(String role);
    
    Mono<Staff> findByEmail(String email);
    
    Flux<Staff> findByNameContainingIgnoreCase(String name);
    
    Flux<Staff> findAllByOrderByCreatedAtDesc();
}
