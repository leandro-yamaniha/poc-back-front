package com.beautysalon.repository;

import com.beautysalon.model.Staff;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffRepository extends CassandraRepository<Staff, UUID> {
    
    @Query("SELECT * FROM staff WHERE is_active = true ALLOW FILTERING")
    List<Staff> findActiveStaff();
    
    @Query("SELECT * FROM staff WHERE role = ?0 ALLOW FILTERING")
    List<Staff> findByRole(String role);
    
    @Query("SELECT * FROM staff WHERE role = ?0 AND is_active = true ALLOW FILTERING")
    List<Staff> findActiveByRole(String role);
}
