package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private UUID customerId;
    private UUID serviceId;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        AppointmentController appointmentController = new AppointmentController(appointmentService);
        webTestClient = WebTestClient.bindToController(appointmentController).build();
        
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
    void getAllAppointments_ShouldReturnAppointments() {
        when(appointmentService.getAllAppointments()).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentById_WhenExists_ShouldReturnAppointment() {
        UUID id = UUID.randomUUID();
        when(appointmentService.getAppointmentById(id)).thenReturn(Mono.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Appointment.class);
    }

    @Test
    void getAppointmentById_WhenNotExists_ShouldReturn404() {
        UUID id = UUID.randomUUID();
        when(appointmentService.getAppointmentById(id)).thenReturn(Mono.empty());

        webTestClient.get()
            .uri("/api/appointments/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createAppointment_WithValidData_ShouldReturnCreated() {
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(Mono.just(testAppointment));

        webTestClient.post()
            .uri("/api/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testAppointment)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Appointment.class);
    }

    @Test
    void createAppointment_WithInvalidData_ShouldReturn400() {
        Appointment invalidAppointment = Appointment.create(
            null, null, null, null, ""
        );

        webTestClient.post()
            .uri("/api/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidAppointment)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void updateAppointment_WhenExists_ShouldReturnUpdated() {
        UUID id = UUID.randomUUID();
        when(appointmentService.updateAppointment(eq(id), any(Appointment.class))).thenReturn(Mono.just(testAppointment));

        webTestClient.put()
            .uri("/api/appointments/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testAppointment)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Appointment.class);
    }

    @Test
    void deleteAppointment_WhenExists_ShouldReturn204() {
        UUID id = UUID.randomUUID();
        when(appointmentService.getAppointmentById(id)).thenReturn(Mono.just(testAppointment));
        when(appointmentService.deleteAppointment(id)).thenReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/appointments/{id}", id)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void getAppointmentsByCustomer_ShouldReturnCustomerAppointments() {
        when(appointmentService.getAppointmentsByCustomer(customerId)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/customer/{customerId}", customerId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentsByStaff_ShouldReturnStaffAppointments() {
        when(appointmentService.getAppointmentsByStaff(staffId)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/staff/{staffId}", staffId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentsByService_ShouldReturnServiceAppointments() {
        when(appointmentService.getAppointmentsByService(serviceId)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/service/{serviceId}", serviceId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentsByDate_ShouldReturnDateAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(appointmentService.getAppointmentsByDate(date)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/date/{date}", date)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentsByStatus_ShouldReturnStatusAppointments() {
        String status = "SCHEDULED";
        when(appointmentService.getAppointmentsByStatus(status)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/status/{status}", status)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getTodayAppointments_ShouldReturnTodayAppointments() {
        when(appointmentService.getTodayAppointments()).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/today")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void getAppointmentsByDateAndStaff_ShouldReturnFilteredAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(appointmentService.getAppointmentsByDateAndStaff(date, staffId)).thenReturn(Flux.just(testAppointment));

        webTestClient.get()
            .uri("/api/appointments/date/{date}/staff/{staffId}", date, staffId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class)
            .hasSize(1);
    }

    @Test
    void appointmentFlux_ShouldEmitCorrectSequence() {
        Appointment appointment1 = Appointment.create(
            customerId, serviceId, staffId,
            LocalDateTime.now().plusDays(1),
            "First appointment"
        );
        Appointment appointment2 = Appointment.create(
            customerId, serviceId, staffId,
            LocalDateTime.now().plusDays(2),
            "Second appointment"
        );
        
        Flux<Appointment> appointmentFlux = Flux.just(appointment1, appointment2);
        
        StepVerifier.create(appointmentFlux)
            .expectNext(appointment1)
            .expectNext(appointment2)
            .verifyComplete();
    }

    @Test
    void appointmentMono_ShouldEmitSingleAppointment() {
        Mono<Appointment> appointmentMono = Mono.just(testAppointment);
        
        StepVerifier.create(appointmentMono)
            .expectNext(testAppointment)
            .verifyComplete();
    }
}
