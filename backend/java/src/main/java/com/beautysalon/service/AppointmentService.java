package com.beautysalon.service;

import com.beautysalon.model.Appointment;
import com.beautysalon.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }
    
    public List<Appointment> getAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }
    
    public List<Appointment> getAppointmentsByStaff(UUID staffId) {
        return appointmentRepository.findByStaffId(staffId);
    }
    
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }
    
    public List<Appointment> getAppointmentsByDateAndStaff(LocalDate date, UUID staffId) {
        return appointmentRepository.findByDateAndStaff(date, staffId);
    }
    
    public Appointment createAppointment(Appointment appointment) {
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());
        return appointmentRepository.save(appointment);
    }
    
    public Appointment updateAppointment(UUID id, Appointment appointmentDetails) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setCustomerId(appointmentDetails.getCustomerId());
            appointment.setStaffId(appointmentDetails.getStaffId());
            appointment.setServiceId(appointmentDetails.getServiceId());
            appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
            appointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
            appointment.setStatus(appointmentDetails.getStatus());
            appointment.setNotes(appointmentDetails.getNotes());
            appointment.setTotalPrice(appointmentDetails.getTotalPrice());
            appointment.setUpdatedAt(Instant.now());
            return appointmentRepository.save(appointment);
        }
        return null;
    }
    
    public boolean deleteAppointment(UUID id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
