package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Flux<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByAppointmentDateDesc();
    }

    public Mono<Appointment> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }

    public Mono<Appointment> createAppointment(Appointment appointment) {
        return appointmentRepository.save(Appointment.create(
            appointment.customerId(),
            appointment.serviceId(),
            appointment.staffId(),
            appointment.appointmentDate(),
            appointment.notes()
        ));
    }

    public Mono<Appointment> updateAppointment(UUID id, Appointment appointment) {
        return appointmentRepository.findById(id)
            .flatMap(existingAppointment -> {
                Appointment updatedAppointment = existingAppointment.withUpdatedFields(
                    appointment.appointmentDate(),
                    appointment.status(),
                    appointment.notes()
                );
                return appointmentRepository.save(updatedAppointment);
            });
    }

    public Mono<Void> deleteAppointment(UUID id) {
        return appointmentRepository.deleteById(id);
    }

    public Flux<Appointment> getAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public Flux<Appointment> getAppointmentsByStaff(UUID staffId) {
        return appointmentRepository.findByStaffId(staffId);
    }

    public Flux<Appointment> getAppointmentsByService(UUID serviceId) {
        return appointmentRepository.findByServiceId(serviceId);
    }

    public Flux<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public Flux<Appointment> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return appointmentRepository.findByAppointmentDateBetween(startOfDay, endOfDay);
    }

    public Flux<Appointment> getAppointmentsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByAppointmentDateBetween(startOfDay, endOfDay);
    }

    public Flux<Appointment> getAppointmentsByDateAndStaff(LocalDate date, UUID staffId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByStaffIdAndAppointmentDateBetween(staffId, startOfDay, endOfDay);
    }
}
