package com.beautysalon.repository;

import com.beautysalon.model.Appointment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends CassandraRepository<Appointment, UUID> {
    
    @Query("SELECT * FROM appointments WHERE appointment_date = ?0 ALLOW FILTERING")
    List<Appointment> findByAppointmentDate(LocalDate date);
    
    @Query("SELECT * FROM appointments WHERE customer_id = ?0 ALLOW FILTERING")
    List<Appointment> findByCustomerId(UUID customerId);
    
    @Query("SELECT * FROM appointments WHERE staff_id = ?0 ALLOW FILTERING")
    List<Appointment> findByStaffId(UUID staffId);
    
    @Query("SELECT * FROM appointments WHERE status = ?0 ALLOW FILTERING")
    List<Appointment> findByStatus(String status);
    
    @Query("SELECT * FROM appointments WHERE appointment_date = ?0 AND staff_id = ?1 ALLOW FILTERING")
    List<Appointment> findByDateAndStaff(LocalDate date, UUID staffId);
}
