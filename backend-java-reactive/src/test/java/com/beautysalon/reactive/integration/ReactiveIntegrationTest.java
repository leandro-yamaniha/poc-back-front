package com.beautysalon.reactive.integration;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.repository.CustomerRepository;
import com.beautysalon.reactive.repository.ServiceRepository;
import com.beautysalon.reactive.repository.StaffRepository;
import com.beautysalon.reactive.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ReactiveIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private ServiceRepository serviceRepository;

    @MockitoBean
    private StaffRepository staffRepository;

    @MockitoBean
    private AppointmentRepository appointmentRepository;

    @Test
    void customerEndpoints_ShouldWorkEndToEnd() {
        Customer customer = Customer.create(
            "John Doe",
            "john@example.com",
            "+1234567890",
            "123 Main St"
        );

        // Mock repository calls
        when(customerRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(customer));

        // Test GET all customers
        webTestClient.get()
            .uri("/api/customers")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Customer.class);

        // Test POST create customer
        webTestClient.post()
            .uri("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customer)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Customer.class)
            .value(created -> {
                assert created.name().equals("John Doe");
                assert created.email().equals("john@example.com");
            });
    }

    @Test
    void serviceEndpoints_ShouldWorkEndToEnd() {
        Service service = Service.create(
            "Haircut",
            "Professional haircut",
            new BigDecimal("50.00"),
            60,
            "Hair"
        );

        // Mock repository calls
        when(serviceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(service));
        when(serviceRepository.save(any(Service.class))).thenReturn(Mono.just(service));

        // Test GET all services
        webTestClient.get()
            .uri("/api/services")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Service.class);

        // Test POST create service
        webTestClient.post()
            .uri("/api/services")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(service)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Service.class)
            .value(created -> {
                assert created.name().equals("Haircut");
                assert created.price().equals(new BigDecimal("50.00"));
            });
    }

    @Test
    void staffEndpoints_ShouldWorkEndToEnd() {
        Staff staff = Staff.create(
            "Jane Smith",
            "jane@beautysalon.com",
            "+1234567890",
            "Stylist",
            List.of("Haircut", "Color")
        );

        // Mock repository calls
        when(staffRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(staff));
        when(staffRepository.save(any(Staff.class))).thenReturn(Mono.just(staff));

        // Test GET all staff
        webTestClient.get()
            .uri("/api/staff")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Staff.class);

        // Test POST create staff
        webTestClient.post()
            .uri("/api/staff")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(staff)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Staff.class)
            .value(created -> {
                assert created.name().equals("Jane Smith");
                assert created.role().equals("Stylist");
            });
    }

    @Test
    void appointmentEndpoints_ShouldWorkEndToEnd() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = Appointment.create(
            customerId,
            serviceId,
            staffId,
            appointmentDate,
            "Regular appointment"
        );

        // Mock repository calls
        when(appointmentRepository.findAllByOrderByAppointmentDateDesc()).thenReturn(Flux.just(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(Mono.just(appointment));

        // Test GET all appointments
        webTestClient.get()
            .uri("/api/appointments")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class);

        // Test POST create appointment
        webTestClient.post()
            .uri("/api/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(appointment)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Appointment.class)
            .value(created -> {
                assert created.customerId().equals(customerId);
                assert created.serviceId().equals(serviceId);
                assert created.staffId().equals(staffId);
            });
        webTestClient.get()
            .uri("/api/appointments/today")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Appointment.class);
    }

    @Test
    void reactiveStreams_ShouldHandleBackpressure() {
        // Test Flux backpressure handling
        Flux<Integer> numbers = Flux.range(1, 1000)
            .onBackpressureBuffer(100);

        StepVerifier.create(numbers.take(10))
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    void reactiveStreams_ShouldHandleErrors() {
        // Test error handling in reactive streams
        Flux<String> errorFlux = Flux.just("valid", "data")
            .concatWith(Flux.error(new RuntimeException("Test error")))
            .onErrorReturn("fallback");

        StepVerifier.create(errorFlux)
            .expectNext("valid")
            .expectNext("data")
            .expectNext("fallback")
            .verifyComplete();
    }

    @Test
    void reactiveStreams_ShouldHandleEmpty() {
        // Test empty stream handling
        Mono<String> emptyMono = Mono.empty();
        Flux<String> emptyFlux = Flux.empty();

        StepVerifier.create(emptyMono)
            .verifyComplete();

        StepVerifier.create(emptyFlux)
            .verifyComplete();
    }

    @Test
    void testController_ShouldReturnHelloMessage() {
        webTestClient.get()
            .uri("/api/test/hello")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("Hello from Beauty Salon Reactive Backend!");
    }

    @Test
    void swaggerEndpoints_ShouldBeAccessible() {
        // Test OpenAPI documentation endpoint
        webTestClient.get()
            .uri("/v3/api-docs")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk();

        // Test Swagger UI endpoint
        webTestClient.get()
            .uri("/swagger-ui/index.html")
            .accept(MediaType.TEXT_HTML)
            .exchange()
            .expectStatus().isOk();
    }
}
