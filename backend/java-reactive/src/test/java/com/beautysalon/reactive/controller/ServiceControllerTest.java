package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.service.ServiceService;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private ServiceService serviceService;

    private Service testService;

    @BeforeEach
    void setUp() {
        ServiceController serviceController = new ServiceController(serviceService);
        webTestClient = WebTestClient.bindToController(serviceController).build();
        
        testService = Service.create(
            "Haircut",
            "Professional haircut service",
            new BigDecimal("50.00"),
            60,
            "Hair"
        );
    }

    @Test
    void getAllServices_ShouldReturnServices() {
        when(serviceService.getAllServices()).thenReturn(Flux.just(testService));

        webTestClient.get()
            .uri("/api/services")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Service.class)
            .hasSize(1);
    }

    @Test
    void getServiceById_WhenExists_ShouldReturnService() {
        UUID id = UUID.randomUUID();
        when(serviceService.getServiceById(id)).thenReturn(Mono.just(testService));

        webTestClient.get()
            .uri("/api/services/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Service.class);
    }

    @Test
    void getServiceById_WhenNotExists_ShouldReturn404() {
        UUID id = UUID.randomUUID();
        when(serviceService.getServiceById(id)).thenReturn(Mono.empty());

        webTestClient.get()
            .uri("/api/services/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createService_WithValidData_ShouldReturnCreated() {
        when(serviceService.createService(any(Service.class))).thenReturn(Mono.just(testService));

        webTestClient.post()
            .uri("/api/services")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testService)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Service.class);
    }

    @Test
    void createService_WithInvalidData_ShouldReturn400() {
        Service invalidService = Service.create("", "", new BigDecimal("-1"), 0, "");

        webTestClient.post()
            .uri("/api/services")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidService)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void updateService_WhenExists_ShouldReturnUpdated() {
        UUID id = UUID.randomUUID();
        when(serviceService.updateService(eq(id), any(Service.class))).thenReturn(Mono.just(testService));

        webTestClient.put()
            .uri("/api/services/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testService)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Service.class);
    }

    @Test
    void deleteService_WhenExists_ShouldReturn204() {
        UUID id = UUID.randomUUID();
        when(serviceService.getServiceById(id)).thenReturn(Mono.just(testService));
        when(serviceService.deleteService(id)).thenReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/services/{id}", id)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void getActiveServices_ShouldReturnActiveServices() {
        when(serviceService.getActiveServices()).thenReturn(Flux.just(testService));

        webTestClient.get()
            .uri("/api/services/active")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Service.class)
            .hasSize(1);
    }

    @Test
    void getServicesByCategory_ShouldReturnFilteredServices() {
        String category = "Hair";
        when(serviceService.getServicesByCategory(category)).thenReturn(Flux.just(testService));

        webTestClient.get()
            .uri("/api/services/category/{category}", category)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Service.class)
            .hasSize(1);
    }

    @Test
    void searchServices_ShouldReturnMatchingServices() {
        String searchTerm = "Hair";
        when(serviceService.searchServices(searchTerm)).thenReturn(Flux.just(testService));

        webTestClient.get()
            .uri("/api/services/search?name={name}", searchTerm)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Service.class)
            .hasSize(1);
    }

    @Test
    void getActiveServicesByCategory_ShouldReturnActiveServicesInCategory() {
        String category = "Hair";
        when(serviceService.getActiveServicesByCategory(category)).thenReturn(Flux.just(testService));

        webTestClient.get()
            .uri("/api/services/category/{category}/active", category)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Service.class)
            .hasSize(1);
    }

    @Test
    void serviceFlux_ShouldEmitCorrectSequence() {
        Service service1 = Service.create("Haircut", "Basic cut", new BigDecimal("30.00"), 45, "Hair");
        Service service2 = Service.create("Massage", "Relaxing massage", new BigDecimal("80.00"), 90, "Wellness");
        
        Flux<Service> serviceFlux = Flux.just(service1, service2);
        
        StepVerifier.create(serviceFlux)
            .expectNext(service1)
            .expectNext(service2)
            .verifyComplete();
    }
}
