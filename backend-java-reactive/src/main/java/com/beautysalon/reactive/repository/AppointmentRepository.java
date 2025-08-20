package com.beautysalon.reactive.repository;

import com.beautysalon.reactive.model.Appointment;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends ReactiveCassandraRepository<Appointment, UUID> {
    
    Flux<Appointment> findByCustomerId(UUID customerId);
    
    Flux<Appointment> findByStaffId(UUID staffId);
    
    Flux<Appointment> findByServiceId(UUID serviceId);
    
    Flux<Appointment> findByStatus(String status);
    
    Flux<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    
    Flux<Appointment> findByStaffIdAndAppointmentDateBetween(UUID staffId, LocalDateTime start, LocalDateTime end);
    
    Flux<Appointment> findAllByOrderByAppointmentDateDesc();
}
