package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentService appointmentService;
    private Appointment testAppointment;
    private UUID customerId;
    private UUID serviceId;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentRepository);
        
        customerId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        staffId = UUID.randomUUID();
        
        testAppointment = Appointment.create(
            customerId,
            serviceId,
            staffId,
            LocalDateTime.now().plusDays(1),
            "Regular appointment"
        );
    }

    @Test
    void getAllAppointments_ShouldReturnAllAppointments() {
        when(appointmentRepository.findAllByOrderByAppointmentDateDesc()).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAllAppointments();

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentById_WhenExists_ShouldReturnAppointment() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Mono.just(testAppointment));

        Mono<Appointment> result = appointmentService.getAppointmentById(id);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentById_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Appointment> result = appointmentService.getAppointmentById(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void createAppointment_ShouldReturnCreatedAppointment() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(Mono.just(testAppointment));

        Mono<Appointment> result = appointmentService.createAppointment(testAppointment);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void updateAppointment_WhenExists_ShouldReturnUpdatedAppointment() {
        UUID id = UUID.randomUUID();
        Appointment updatedAppointment = Appointment.create(
            customerId,
            serviceId,
            staffId,
            LocalDateTime.now().plusDays(2),
            "Updated appointment"
        );
        
        when(appointmentRepository.findById(id)).thenReturn(Mono.just(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(Mono.just(updatedAppointment));

        Mono<Appointment> result = appointmentService.updateAppointment(id, updatedAppointment);

        StepVerifier.create(result)
            .expectNext(updatedAppointment)
            .verifyComplete();
    }

    @Test
    void updateAppointment_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Appointment> result = appointmentService.updateAppointment(id, testAppointment);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteAppointment_WhenExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = appointmentService.deleteAppointment(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteAppointment_WhenNotExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = appointmentService.deleteAppointment(id);

        StepVerifier.create(result)
            .verifyComplete();
    }


    @Test
    void getAppointmentsByCustomer_ShouldReturnCustomerAppointments() {
        when(appointmentRepository.findByCustomerId(customerId)).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByCustomer(customerId);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentsByStaff_ShouldReturnStaffAppointments() {
        when(appointmentRepository.findByStaffId(staffId)).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByStaff(staffId);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentsByService_ShouldReturnServiceAppointments() {
        when(appointmentRepository.findByServiceId(serviceId)).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByService(serviceId);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentsByDate_ShouldReturnDateAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(appointmentRepository.findByAppointmentDateBetween(any(), any())).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByDate(date);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentsByStatus_ShouldReturnStatusAppointments() {
        String status = "SCHEDULED";
        when(appointmentRepository.findByStatus(status)).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByStatus(status);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getTodayAppointments_ShouldReturnTodayAppointments() {
        when(appointmentRepository.findByAppointmentDateBetween(any(), any())).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getTodayAppointments();

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void getAppointmentsByDateAndStaff_ShouldReturnFilteredAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        when(appointmentRepository.findByStaffIdAndAppointmentDateBetween(staffId, startOfDay, endOfDay)).thenReturn(Flux.just(testAppointment));

        Flux<Appointment> result = appointmentService.getAppointmentsByDateAndStaff(date, staffId);

        StepVerifier.create(result)
            .expectNext(testAppointment)
            .verifyComplete();
    }

    @Test
    void appointmentFlux_ShouldHandleMultipleAppointments() {
        Appointment appointment1 = Appointment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().plusDays(1), "Appointment 1");
        Appointment appointment2 = Appointment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().plusDays(2), "Appointment 2");
        
        when(appointmentRepository.findAllByOrderByAppointmentDateDesc()).thenReturn(Flux.just(appointment1, appointment2));
        
        StepVerifier.create(appointmentService.getAllAppointments())
            .expectNext(appointment1)
            .expectNext(appointment2)
            .verifyComplete();
    }

    @Test
    void appointmentFlux_ShouldHandleEmptyResult() {
        when(appointmentRepository.findAllByOrderByAppointmentDateDesc()).thenReturn(Flux.empty());
        
        StepVerifier.create(appointmentService.getAllAppointments())
            .verifyComplete();
    }

    @Test
    void appointmentFlux_ShouldHandleError() {
        when(appointmentRepository.findAllByOrderByAppointmentDateDesc()).thenReturn(Flux.error(new RuntimeException("Database error")));
        
        StepVerifier.create(appointmentService.getAllAppointments())
            .expectError(RuntimeException.class)
            .verify();
    }
}
