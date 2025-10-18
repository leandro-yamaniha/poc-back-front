package com.beautysalon.repository;

import com.beautysalon.model.Service;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends CassandraRepository<Service, UUID> {
    
    @Query("SELECT * FROM services WHERE is_active = true ALLOW FILTERING")
    List<Service> findActiveServices();
    
    @Query("SELECT * FROM services WHERE category = ?0 ALLOW FILTERING")
    List<Service> findByCategory(String category);
    
    @Query("SELECT * FROM services WHERE category = ?0 AND is_active = true ALLOW FILTERING")
    List<Service> findActiveByCategoryServices(String category);
}
