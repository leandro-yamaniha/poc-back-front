package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
        log.info("AppointmentService initialized with repository: {}", appointmentRepository.getClass().getSimpleName());
    }

    public Flux<Appointment> getAllAppointments() {
        log.debug("Service: Starting to retrieve all appointments ordered by appointment date");
        return appointmentRepository.findAll()
            .doOnSubscribe(subscription -> log.debug("Repository call: findAll()"))
            .doOnNext(appointment -> log.trace("Service: Retrieved appointment from repository: id={}, customerId={}, staffId={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved all appointments from repository"))
            .doOnError(error -> log.error("Service: Error retrieving all appointments: {}", error.getMessage(), error));
    }

    public Mono<Appointment> getAppointmentById(UUID id) {
        log.debug("Service: Starting to retrieve appointment by ID: {}", id);
        return appointmentRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({})", id))
            .doOnNext(appointment -> log.debug("Service: Found appointment: id={}, customerId={}, staffId={}, serviceId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.serviceId(), appointment.appointmentDate(), appointment.status()))
            .doOnSuccess(appointment -> {
                if (appointment == null) {
                    log.debug("Service: No appointment found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Service: Error retrieving appointment by ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Appointment> createAppointment(Appointment appointment) {
        log.debug("Service: Starting to create appointment: customerId={}, serviceId={}, staffId={}, appointmentDate={}", 
                appointment.customerId(), appointment.serviceId(), appointment.staffId(), appointment.appointmentDate());
        
        Appointment newAppointment = Appointment.create(
            appointment.customerId(),
            appointment.serviceId(),
            appointment.staffId(),
            appointment.appointmentDate(),
            appointment.notes()
        );
        
        log.debug("Service: Created appointment object with ID: {}", newAppointment.id());
        
        return appointmentRepository.save(newAppointment)
            .doOnSubscribe(subscription -> log.debug("Repository call: save() for appointment ID: {}", newAppointment.id()))
            .doOnNext(savedAppointment -> log.info("Service: Appointment saved successfully: id={}, customerId={}, staffId={}, serviceId={}, appointmentDate={}, status={}", 
                    savedAppointment.id(), savedAppointment.customerId(), savedAppointment.staffId(), savedAppointment.serviceId(), savedAppointment.appointmentDate(), savedAppointment.status()))
            .doOnError(error -> log.error("Service: Error creating appointment for customerId={}, serviceId={}, staffId={}: {}", 
                    appointment.customerId(), appointment.serviceId(), appointment.staffId(), error.getMessage(), error));
    }

    public Mono<Appointment> updateAppointment(UUID id, Appointment appointment) {
        log.debug("Service: Starting to update appointment: id={}, appointmentDate={}, status={}", 
                id, appointment.appointmentDate(), appointment.status());
        
        return appointmentRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({}) for update", id))
            .doOnNext(existingAppointment -> log.debug("Service: Found existing appointment for update: id={}, customerId={}, staffId={}, status={}", 
                    existingAppointment.id(), existingAppointment.customerId(), existingAppointment.staffId(), existingAppointment.status()))
            .flatMap(existingAppointment -> {
                Appointment updatedAppointment = existingAppointment.withUpdatedFields(
                    appointment.appointmentDate(),
                    appointment.status(),
                    appointment.notes()
                );
                log.debug("Service: Created updated appointment object: id={}, appointmentDate={}, status={}, notes={}", 
                        updatedAppointment.id(), updatedAppointment.appointmentDate(), updatedAppointment.status(), updatedAppointment.notes());
                
                return appointmentRepository.save(updatedAppointment)
                    .doOnSubscribe(sub -> log.debug("Repository call: save() for updated appointment ID: {}", updatedAppointment.id()))
                    .doOnNext(savedAppointment -> log.info("Service: Appointment updated successfully: id={}, customerId={}, staffId={}, serviceId={}, appointmentDate={}, status={}", 
                            savedAppointment.id(), savedAppointment.customerId(), savedAppointment.staffId(), savedAppointment.serviceId(), savedAppointment.appointmentDate(), savedAppointment.status()));
            })
            .doOnError(error -> log.error("Service: Error updating appointment with ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Void> deleteAppointment(UUID id) {
        log.debug("Service: Starting to delete appointment with ID: {}", id);
        return appointmentRepository.deleteById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: deleteById({})", id))
            .doOnSuccess(unused -> log.info("Service: Appointment deleted successfully from repository: id={}", id))
            .doOnError(error -> log.error("Service: Error deleting appointment with ID {}: {}", id, error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByCustomer(UUID customerId) {
        log.debug("Service: Starting to retrieve appointments by customer: {}", customerId);
        return appointmentRepository.findByCustomerId(customerId)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByCustomerId({})", customerId))
            .doOnNext(appointment -> log.trace("Service: Found appointment for customer: id={}, customerId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by customer: {}", customerId))
            .doOnError(error -> log.error("Service: Error retrieving appointments by customer {}: {}", customerId, error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByStaff(UUID staffId) {
        log.debug("Service: Starting to retrieve appointments by staff: {}", staffId);
        return appointmentRepository.findByStaffId(staffId)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByStaffId({})", staffId))
            .doOnNext(appointment -> log.trace("Service: Found appointment for staff: id={}, staffId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by staff: {}", staffId))
            .doOnError(error -> log.error("Service: Error retrieving appointments by staff {}: {}", staffId, error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByService(UUID serviceId) {
        log.debug("Service: Starting to retrieve appointments by service: {}", serviceId);
        return appointmentRepository.findByServiceId(serviceId)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByServiceId({})", serviceId))
            .doOnNext(appointment -> log.trace("Service: Found appointment for service: id={}, serviceId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.serviceId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by service: {}", serviceId))
            .doOnError(error -> log.error("Service: Error retrieving appointments by service {}: {}", serviceId, error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByStatus(String status) {
        log.debug("Service: Starting to retrieve appointments by status: {}", status);
        return appointmentRepository.findByStatus(status)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByStatus({})", status))
            .doOnNext(appointment -> log.trace("Service: Found appointment with status: id={}, status={}, appointmentDate={}", 
                    appointment.id(), appointment.status(), appointment.appointmentDate()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by status: {}", status))
            .doOnError(error -> log.error("Service: Error retrieving appointments by status {}: {}", status, error.getMessage(), error));
    }

    public Flux<Appointment> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        log.debug("Service: Starting to retrieve today's appointments: {} to {}", startOfDay, endOfDay);
        return appointmentRepository.findByAppointmentDateBetween(startOfDay, endOfDay)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByAppointmentDateBetween({}, {})", startOfDay, endOfDay))
            .doOnNext(appointment -> log.trace("Service: Found today's appointment: id={}, customerId={}, staffId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed today's appointment retrieval"))
            .doOnError(error -> log.error("Service: Error retrieving today's appointments: {}", error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        log.debug("Service: Starting to retrieve appointments by date: {} ({} to {})", date, startOfDay, endOfDay);
        return appointmentRepository.findByAppointmentDateBetween(startOfDay, endOfDay)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByAppointmentDateBetween({}, {})", startOfDay, endOfDay))
            .doOnNext(appointment -> log.trace("Service: Found appointment for date: id={}, customerId={}, staffId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by date: {}", date))
            .doOnError(error -> log.error("Service: Error retrieving appointments by date {}: {}", date, error.getMessage(), error));
    }

    public Flux<Appointment> getAppointmentsByDateAndStaff(LocalDate date, UUID staffId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        log.debug("Service: Starting to retrieve appointments by date and staff: date={}, staffId={} ({} to {})", date, staffId, startOfDay, endOfDay);
        return appointmentRepository.findByStaffIdAndAppointmentDateBetween(staffId, startOfDay, endOfDay)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByStaffIdAndAppointmentDateBetween({}, {}, {})", staffId, startOfDay, endOfDay))
            .doOnNext(appointment -> log.trace("Service: Found appointment for date and staff: id={}, customerId={}, staffId={}, appointmentDate={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.debug("Service: Completed appointment retrieval by date and staff: date={}, staffId={}", date, staffId))
            .doOnError(error -> log.error("Service: Error retrieving appointments by date {} and staff {}: {}", date, staffId, error.getMessage(), error));
    }
}
