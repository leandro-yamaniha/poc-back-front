package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.service.AppointmentService;
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

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public Flux<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Appointment>> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
            .map(appointment -> ResponseEntity.ok(appointment))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Appointment>> createAppointment(@Valid @RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment)
            .map(createdAppointment -> ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Appointment>> updateAppointment(@PathVariable UUID id, 
                                                              @Valid @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(id, appointment)
            .map(updatedAppointment -> ResponseEntity.ok(updatedAppointment))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppointment(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
            .flatMap(appointment -> appointmentService.deleteAppointment(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public Flux<Appointment> getAppointmentsByCustomer(@PathVariable UUID customerId) {
        return appointmentService.getAppointmentsByCustomer(customerId);
    }

    @GetMapping("/staff/{staffId}")
    public Flux<Appointment> getAppointmentsByStaff(@PathVariable UUID staffId) {
        return appointmentService.getAppointmentsByStaff(staffId);
    }

    @GetMapping("/service/{serviceId}")
    public Flux<Appointment> getAppointmentsByService(@PathVariable UUID serviceId) {
        return appointmentService.getAppointmentsByService(serviceId);
    }

    @GetMapping("/status/{status}")
    public Flux<Appointment> getAppointmentsByStatus(@PathVariable String status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @GetMapping("/today")
    public Flux<Appointment> getTodayAppointments() {
        return appointmentService.getTodayAppointments();
    }

    @GetMapping("/date/{date}")
    public Flux<Appointment> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAppointmentsByDate(date);
    }

    @GetMapping("/date/{date}/staff/{staffId}")
    public Flux<Appointment> getAppointmentsByDateAndStaff(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable UUID staffId) {
        return appointmentService.getAppointmentsByDateAndStaff(date, staffId);
    }
}
