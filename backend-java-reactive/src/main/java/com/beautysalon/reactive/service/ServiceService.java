package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Flux<Service> getAllServices() {
        return serviceRepository.findAllByOrderByCreatedAtDesc();
    }

    public Flux<Service> getActiveServices() {
        return serviceRepository.findByActiveTrue();
    }

    public Mono<Service> getServiceById(UUID id) {
        return serviceRepository.findById(id);
    }

    public Mono<Service> createService(Service service) {
        return serviceRepository.save(Service.create(
            service.name(),
            service.description(),
            service.price(),
            service.durationMinutes(),
            service.category()
        ));
    }

    public Mono<Service> updateService(UUID id, Service service) {
        return serviceRepository.findById(id)
            .flatMap(existingService -> {
                Service updatedService = existingService.withUpdatedFields(
                    service.name(),
                    service.description(),
                    service.price(),
                    service.durationMinutes(),
                    service.category(),
                    service.active()
                );
                return serviceRepository.save(updatedService);
            });
    }

    public Mono<Void> deleteService(UUID id) {
        return serviceRepository.deleteById(id);
    }

    public Flux<Service> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    public Flux<Service> getActiveServicesByCategory(String category) {
        return serviceRepository.findByCategoryAndActiveTrue(category);
    }

    public Flux<Service> searchServices(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name);
    }
}
