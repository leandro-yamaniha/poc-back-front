package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.service.ServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@Validated
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);
    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
        log.info("ServiceController initialized successfully");
    }

    @GetMapping
    public Flux<Service> getAllServices() {
        log.info("Request received: GET /api/services - retrieving all services");
        return serviceService.getAllServices()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve all services"))
            .doOnNext(service -> log.debug("Retrieved service: id={}, name={}", service.id(), service.name()))
            .doOnComplete(() -> log.info("Successfully completed getAllServices request"))
            .doOnError(error -> log.error("Error retrieving all services: {}", error.getMessage(), error));
    }

    @GetMapping("/active")
    public Flux<Service> getActiveServices() {
        log.info("Request received: GET /api/services/active - retrieving active services");
        return serviceService.getActiveServices()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve active services"))
            .doOnNext(service -> log.debug("Retrieved active service: id={}, name={}", service.id(), service.name()))
            .doOnComplete(() -> log.info("Successfully completed getActiveServices request"))
            .doOnError(error -> log.error("Error retrieving active services: {}", error.getMessage(), error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Service>> getServiceById(@PathVariable UUID id) {
        log.info("Request received: GET /api/services/{} - retrieving service by ID", id);
        return serviceService.getServiceById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve service by ID: {}", id))
            .map(service -> {
                log.info("Service found: id={}, name={}", service.id(), service.name());
                return ResponseEntity.ok(service);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Service not found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error retrieving service by ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Service>> createService(@Valid @RequestBody Service service) {
        log.info("Request received: POST /api/services - creating new service with name={}, category={}", 
                service.name(), service.category());
        return serviceService.createService(service)
            .doOnSubscribe(subscription -> log.debug("Starting to create service: {}", service.name()))
            .map(createdService -> {
                log.info("Service created successfully: id={}, name={}, category={}", 
                        createdService.id(), createdService.name(), createdService.category());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
            })
            .doOnError(error -> log.error("Error creating service with name={}, category={}: {}", 
                    service.name(), service.category(), error.getMessage(), error));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Service>> updateService(@PathVariable UUID id, 
                                                      @Valid @RequestBody Service service) {
        log.info("Request received: PUT /api/services/{} - updating service with name={}, category={}", 
                id, service.name(), service.category());
        return serviceService.updateService(id, service)
            .doOnSubscribe(subscription -> log.debug("Starting to update service: id={}", id))
            .map(updatedService -> {
                log.info("Service updated successfully: id={}, name={}, category={}", 
                        updatedService.id(), updatedService.name(), updatedService.category());
                return ResponseEntity.ok(updatedService);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Service not found for update with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error updating service with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteService(@PathVariable UUID id) {
        log.info("Request received: DELETE /api/services/{} - deleting service", id);
        return serviceService.getServiceById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to delete service: id={}", id))
            .flatMap(service -> {
                log.debug("Service found for deletion: id={}, name={}", service.id(), service.name());
                return serviceService.deleteService(id)
                    .doOnSuccess(unused -> log.info("Service deleted successfully: id={}", id))
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()));
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Service not found for deletion with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error deleting service with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public Flux<Service> getServicesByCategory(@PathVariable String category) {
        log.info("Request received: GET /api/services/category/{} - retrieving services by category", category);
        return serviceService.getServicesByCategory(category)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve services by category: {}", category))
            .doOnNext(service -> log.debug("Found service in category: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.info("Successfully completed getServicesByCategory request for: {}", category))
            .doOnError(error -> log.error("Error retrieving services by category {}: {}", category, error.getMessage(), error));
    }

    @GetMapping("/category/{category}/active")
    public Flux<Service> getActiveServicesByCategory(@PathVariable String category) {
        log.info("Request received: GET /api/services/category/{}/active - retrieving active services by category", category);
        return serviceService.getActiveServicesByCategory(category)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve active services by category: {}", category))
            .doOnNext(service -> log.debug("Found active service in category: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.info("Successfully completed getActiveServicesByCategory request for: {}", category))
            .doOnError(error -> log.error("Error retrieving active services by category {}: {}", category, error.getMessage(), error));
    }

    @GetMapping("/search")
    public Flux<Service> searchServices(@RequestParam String name) {
        log.info("Request received: GET /api/services/search?name={} - searching services", name);
        return serviceService.searchServices(name)
            .doOnSubscribe(subscription -> log.debug("Starting to search services with name: {}", name))
            .doOnNext(service -> log.debug("Found service in search: id={}, name={}", service.id(), service.name()))
            .doOnComplete(() -> log.info("Successfully completed service search for name: {}", name))
            .doOnError(error -> log.error("Error searching services with name {}: {}", name, error.getMessage(), error));
    }
}
