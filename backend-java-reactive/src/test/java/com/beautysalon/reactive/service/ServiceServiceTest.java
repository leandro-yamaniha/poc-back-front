package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    private ServiceService serviceService;
    private Service testService;

    @BeforeEach
    void setUp() {
        serviceService = new ServiceService(serviceRepository);
        testService = Service.create(
            "Haircut",
            "Professional haircut service",
            new BigDecimal("50.00"),
            60,
            "Hair"
        );
    }

    @Test
    void getAllServices_ShouldReturnAllServices() {
        when(serviceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(testService));

        Flux<Service> result = serviceService.getAllServices();

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
        
        verify(serviceRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getServiceById_WhenExists_ShouldReturnService() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Mono.just(testService));

        Mono<Service> result = serviceService.getServiceById(id);

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void getServiceById_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Service> result = serviceService.getServiceById(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void createService_ShouldReturnCreatedService() {
        when(serviceRepository.save(any(Service.class))).thenReturn(Mono.just(testService));

        Mono<Service> result = serviceService.createService(testService);

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void updateService_WhenExists_ShouldReturnUpdatedService() {
        UUID id = UUID.randomUUID();
        Service updatedService = Service.create(
            "Updated Haircut",
            "Updated description",
            new BigDecimal("60.00"),
            75,
            "Hair"
        );
        
        when(serviceRepository.findById(id)).thenReturn(Mono.just(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(Mono.just(updatedService));

        Mono<Service> result = serviceService.updateService(id, updatedService);

        StepVerifier.create(result)
            .expectNext(updatedService)
            .verifyComplete();
    }

    @Test
    void updateService_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Service> result = serviceService.updateService(id, testService);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteService_WhenExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = serviceService.deleteService(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteService_WhenNotExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = serviceService.deleteService(id);

        StepVerifier.create(result)
            .verifyComplete();
        
        verify(serviceRepository).deleteById(id);
    }

    @Test
    void getActiveServices_ShouldReturnActiveServices() {
        when(serviceRepository.findByActiveTrue()).thenReturn(Flux.just(testService));

        Flux<Service> result = serviceService.getActiveServices();

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void getServicesByCategory_ShouldReturnFilteredServices() {
        String category = "Hair";
        when(serviceRepository.findByCategory(category)).thenReturn(Flux.just(testService));

        Flux<Service> result = serviceService.getServicesByCategory(category);

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void getActiveServicesByCategory_ShouldReturnActiveServicesInCategory() {
        String category = "Hair";
        when(serviceRepository.findByCategoryAndActiveTrue(category)).thenReturn(Flux.just(testService));

        Flux<Service> result = serviceService.getActiveServicesByCategory(category);

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void searchServices_ShouldReturnMatchingServices() {
        String searchTerm = "Hair";
        when(serviceRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(Flux.just(testService));

        Flux<Service> result = serviceService.searchServices(searchTerm);

        StepVerifier.create(result)
            .expectNext(testService)
            .verifyComplete();
    }

    @Test
    void serviceFlux_ShouldHandleMultipleServices() {
        Service service1 = Service.create("Service 1", "Description 1", BigDecimal.valueOf(50.0), 60, "BEAUTY");
        Service service2 = Service.create("Service 2", "Description 2", BigDecimal.valueOf(75.0), 90, "WELLNESS");
        
        when(serviceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(service1, service2));
        
        StepVerifier.create(serviceService.getAllServices())
            .expectNext(service1)
            .expectNext(service2)
            .verifyComplete();
    }

    @Test
    void serviceFlux_ShouldHandleEmptyResult() {
        when(serviceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.empty());
        
        StepVerifier.create(serviceService.getAllServices())
            .verifyComplete();
    }

    @Test
    void serviceFlux_ShouldHandleError() {
        when(serviceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.error(new RuntimeException("Database error")));
        
        StepVerifier.create(serviceService.getAllServices())
            .expectError(RuntimeException.class)
            .verify();
    }
}
