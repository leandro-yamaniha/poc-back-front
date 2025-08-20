package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.service.ServiceService;
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

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public Flux<Service> getAllServices() {
        return serviceService.getAllServices();
    }

    @GetMapping("/active")
    public Flux<Service> getActiveServices() {
        return serviceService.getActiveServices();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Service>> getServiceById(@PathVariable UUID id) {
        return serviceService.getServiceById(id)
            .map(service -> ResponseEntity.ok(service))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Service>> createService(@Valid @RequestBody Service service) {
        return serviceService.createService(service)
            .map(createdService -> ResponseEntity.status(HttpStatus.CREATED).body(createdService));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Service>> updateService(@PathVariable UUID id, 
                                                      @Valid @RequestBody Service service) {
        return serviceService.updateService(id, service)
            .map(updatedService -> ResponseEntity.ok(updatedService))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteService(@PathVariable UUID id) {
        return serviceService.getServiceById(id)
            .flatMap(service -> serviceService.deleteService(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public Flux<Service> getServicesByCategory(@PathVariable String category) {
        return serviceService.getServicesByCategory(category);
    }

    @GetMapping("/category/{category}/active")
    public Flux<Service> getActiveServicesByCategory(@PathVariable String category) {
        return serviceService.getActiveServicesByCategory(category);
    }

    @GetMapping("/search")
    public Flux<Service> searchServices(@RequestParam String name) {
        return serviceService.searchServices(name);
    }
}
