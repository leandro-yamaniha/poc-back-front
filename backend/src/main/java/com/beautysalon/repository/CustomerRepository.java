package com.beautysalon.repository;

import com.beautysalon.model.Customer;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CassandraRepository<Customer, UUID> {
    
    @Query("SELECT * FROM customers WHERE email = ?0 ALLOW FILTERING")
    Optional<Customer> findByEmail(String email);
    
    @Query("SELECT * FROM customers WHERE name LIKE ?0 ALLOW FILTERING")
    List<Customer> findByNameContaining(String name);
}
