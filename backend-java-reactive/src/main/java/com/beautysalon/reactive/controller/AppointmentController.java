package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@Validated
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
        log.info("AppointmentController initialized successfully");
    }

    @GetMapping
    public Flux<Appointment> getAllAppointments() {
        log.info("Request received: GET /api/appointments - retrieving all appointments");
        return appointmentService.getAllAppointments()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve all appointments"))
            .doOnNext(appointment -> log.debug("Retrieved appointment: id={}, customerId={}, staffId={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAllAppointments request"))
            .doOnError(error -> log.error("Error retrieving all appointments: {}", error.getMessage(), error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Appointment>> getAppointmentById(@PathVariable UUID id) {
        log.info("Request received: GET /api/appointments/{} - retrieving appointment by ID", id);
        return appointmentService.getAppointmentById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointment by ID: {}", id))
            .map(appointment -> {
                log.info("Appointment found: id={}, customerId={}, staffId={}, status={}", 
                        appointment.id(), appointment.customerId(), appointment.staffId(), appointment.status());
                return ResponseEntity.ok(appointment);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Appointment not found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error retrieving appointment by ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Appointment>> createAppointment(@Valid @RequestBody Appointment appointment) {
        log.info("Request received: POST /api/appointments - creating new appointment for customerId={}, staffId={}, serviceId={}, appointmentDate={}", 
                appointment.customerId(), appointment.staffId(), appointment.serviceId(), appointment.appointmentDate());
        return appointmentService.createAppointment(appointment)
            .doOnSubscribe(subscription -> log.debug("Starting to create appointment for customer: {}", appointment.customerId()))
            .map(createdAppointment -> {
                log.info("Appointment created successfully: id={}, customerId={}, staffId={}, serviceId={}, dateTime={}, status={}", 
                        createdAppointment.id(), createdAppointment.customerId(), createdAppointment.staffId(), 
                        createdAppointment.serviceId(), createdAppointment.appointmentDate(), createdAppointment.status());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
            })
            .doOnError(error -> log.error("Error creating appointment for customerId={}, staffId={}, serviceId={}: {}", 
                    appointment.customerId(), appointment.staffId(), appointment.serviceId(), error.getMessage(), error));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Appointment>> updateAppointment(@PathVariable UUID id, 
                                                              @Valid @RequestBody Appointment appointment) {
        log.info("Request received: PUT /api/appointments/{} - updating appointment with customerId={}, staffId={}, serviceId={}, dateTime={}", 
                id, appointment.customerId(), appointment.staffId(), appointment.serviceId(), appointment.appointmentDate());
        return appointmentService.updateAppointment(id, appointment)
            .doOnSubscribe(subscription -> log.debug("Starting to update appointment: id={}", id))
            .map(updatedAppointment -> {
                log.info("Appointment updated successfully: id={}, customerId={}, staffId={}, serviceId={}, dateTime={}, status={}", 
                        updatedAppointment.id(), updatedAppointment.customerId(), updatedAppointment.staffId(), 
                        updatedAppointment.serviceId(), updatedAppointment.appointmentDate(), updatedAppointment.status());
                return ResponseEntity.ok(updatedAppointment);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Appointment not found for update with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error updating appointment with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppointment(@PathVariable UUID id) {
        log.info("Request received: DELETE /api/appointments/{} - deleting appointment", id);
        return appointmentService.getAppointmentById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to delete appointment: id={}", id))
            .flatMap(appointment -> {
                log.debug("Appointment found for deletion: id={}, customerId={}, staffId={}, status={}", 
                        appointment.id(), appointment.customerId(), appointment.staffId(), appointment.status());
                return appointmentService.deleteAppointment(id)
                    .doOnSuccess(unused -> log.info("Appointment deleted successfully: id={}", id))
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()));
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Appointment not found for deletion with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error deleting appointment with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public Flux<Appointment> getAppointmentsByCustomer(@PathVariable UUID customerId) {
        log.info("Request received: GET /api/appointments/customer/{} - retrieving appointments by customer", customerId);
        return appointmentService.getAppointmentsByCustomer(customerId)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments for customer: {}", customerId))
            .doOnNext(appointment -> log.debug("Found appointment for customer: id={}, customerId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByCustomer request for: {}", customerId))
            .doOnError(error -> log.error("Error retrieving appointments for customer {}: {}", customerId, error.getMessage(), error));
    }

    @GetMapping("/staff/{staffId}")
    public Flux<Appointment> getAppointmentsByStaff(@PathVariable UUID staffId) {
        log.info("Request received: GET /api/appointments/staff/{} - retrieving appointments by staff", staffId);
        return appointmentService.getAppointmentsByStaff(staffId)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments for staff: {}", staffId))
            .doOnNext(appointment -> log.debug("Found appointment for staff: id={}, staffId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByStaff request for: {}", staffId))
            .doOnError(error -> log.error("Error retrieving appointments for staff {}: {}", staffId, error.getMessage(), error));
    }

    @GetMapping("/service/{serviceId}")
    public Flux<Appointment> getAppointmentsByService(@PathVariable UUID serviceId) {
        log.info("Request received: GET /api/appointments/service/{} - retrieving appointments by service", serviceId);
        return appointmentService.getAppointmentsByService(serviceId)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments for service: {}", serviceId))
            .doOnNext(appointment -> log.debug("Found appointment for service: id={}, serviceId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.serviceId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByService request for: {}", serviceId))
            .doOnError(error -> log.error("Error retrieving appointments for service {}: {}", serviceId, error.getMessage(), error));
    }

    @GetMapping("/status/{status}")
    public Flux<Appointment> getAppointmentsByStatus(@PathVariable String status) {
        log.info("Request received: GET /api/appointments/status/{} - retrieving appointments by status", status);
        return appointmentService.getAppointmentsByStatus(status)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments with status: {}", status))
            .doOnNext(appointment -> log.debug("Found appointment with status: id={}, status={}, dateTime={}", 
                    appointment.id(), appointment.status(), appointment.appointmentDate()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByStatus request for: {}", status))
            .doOnError(error -> log.error("Error retrieving appointments with status {}: {}", status, error.getMessage(), error));
    }

    @GetMapping("/today")
    public Flux<Appointment> getTodayAppointments() {
        log.info("Request received: GET /api/appointments/today - retrieving today's appointments");
        return appointmentService.getTodayAppointments()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve today's appointments"))
            .doOnNext(appointment -> log.debug("Found today's appointment: id={}, customerId={}, staffId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getTodayAppointments request"))
            .doOnError(error -> log.error("Error retrieving today's appointments: {}", error.getMessage(), error));
    }

    @GetMapping("/date/{date}")
    public Flux<Appointment> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Request received: GET /api/appointments/date/{} - retrieving appointments by date", date);
        return appointmentService.getAppointmentsByDate(date)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments for date: {}", date))
            .doOnNext(appointment -> log.debug("Found appointment for date: id={}, customerId={}, staffId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByDate request for: {}", date))
            .doOnError(error -> log.error("Error retrieving appointments for date {}: {}", date, error.getMessage(), error));
    }

    @GetMapping("/date/{date}/staff/{staffId}")
    public Flux<Appointment> getAppointmentsByDateAndStaff(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable UUID staffId) {
        log.info("Request received: GET /api/appointments/date/{}/staff/{} - retrieving appointments by date and staff", date, staffId);
        return appointmentService.getAppointmentsByDateAndStaff(date, staffId)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve appointments for date: {} and staff: {}", date, staffId))
            .doOnNext(appointment -> log.debug("Found appointment for date and staff: id={}, customerId={}, staffId={}, dateTime={}, status={}", 
                    appointment.id(), appointment.customerId(), appointment.staffId(), appointment.appointmentDate(), appointment.status()))
            .doOnComplete(() -> log.info("Successfully completed getAppointmentsByDateAndStaff request for date: {} and staff: {}", date, staffId))
            .doOnError(error -> log.error("Error retrieving appointments for date {} and staff {}: {}", date, staffId, error.getMessage(), error));
    }
}
