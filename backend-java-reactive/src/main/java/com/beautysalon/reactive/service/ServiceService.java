package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceService {

    private static final Logger log = LoggerFactory.getLogger(ServiceService.class);
    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
        log.info("ServiceService initialized with repository: {}", serviceRepository.getClass().getSimpleName());
    }

    public Flux<Service> getAllServices() {
        log.debug("Service: Starting to retrieve all services");
        return serviceRepository.findAll()
            .doOnSubscribe(subscription -> log.debug("Repository call: findAll()"))
            .doOnNext(service -> log.trace("Service: Retrieved service from repository: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved all services from repository"))
            .doOnError(error -> log.error("Service: Error retrieving all services: {}", error.getMessage(), error));
    }

    public Flux<Service> getActiveServices() {
        log.debug("Service: Starting to retrieve active services");
        return serviceRepository.findByActiveTrue()
            .doOnSubscribe(subscription -> log.debug("Repository call: findByActiveTrue()"))
            .doOnNext(service -> log.trace("Service: Retrieved active service from repository: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved active services from repository"))
            .doOnError(error -> log.error("Service: Error retrieving active services: {}", error.getMessage(), error));
    }

    public Mono<Service> getServiceById(UUID id) {
        log.debug("Service: Starting to retrieve service by ID: {}", id);
        return serviceRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({})", id))
            .doOnNext(service -> log.debug("Service: Found service: id={}, name={}, category={}, price={}", 
                    service.id(), service.name(), service.category(), service.price()))
            .doOnSuccess(service -> {
                if (service == null) {
                    log.debug("Service: No service found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Service: Error retrieving service by ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Service> createService(Service service) {
        log.debug("Service: Starting to create service: name={}, category={}, price={}, duration={}", 
                service.name(), service.category(), service.price(), service.durationMinutes());
        
        Service newService = Service.create(
            service.name(),
            service.description(),
            service.price(),
            service.durationMinutes(),
            service.category()
        );
        
        log.debug("Service: Created service object with ID: {}", newService.id());
        
        return serviceRepository.save(newService)
            .doOnSubscribe(subscription -> log.debug("Repository call: save() for service ID: {}", newService.id()))
            .doOnNext(savedService -> log.info("Service: Service saved successfully: id={}, name={}, category={}, price={}", 
                    savedService.id(), savedService.name(), savedService.category(), savedService.price()))
            .doOnError(error -> log.error("Service: Error creating service with name={}, category={}: {}", 
                    service.name(), service.category(), error.getMessage(), error));
    }

    public Mono<Service> updateService(UUID id, Service service) {
        log.debug("Service: Starting to update service: id={}, name={}, category={}, price={}", 
                id, service.name(), service.category(), service.price());
        
        return serviceRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({}) for update", id))
            .doOnNext(existingService -> log.debug("Service: Found existing service for update: id={}, name={}, category={}", 
                    existingService.id(), existingService.name(), existingService.category()))
            .flatMap(existingService -> {
                Service updatedService = existingService.withUpdatedFields(
                    service.name(),
                    service.description(),
                    service.price(),
                    service.durationMinutes(),
                    service.category(),
                    service.active()
                );
                log.debug("Service: Created updated service object: id={}, name={}, category={}, price={}, active={}", 
                        updatedService.id(), updatedService.name(), updatedService.category(), updatedService.price(), updatedService.active());
                
                return serviceRepository.save(updatedService)
                    .doOnSubscribe(sub -> log.debug("Repository call: save() for updated service ID: {}", updatedService.id()))
                    .doOnNext(savedService -> log.info("Service: Service updated successfully: id={}, name={}, category={}, price={}, active={}", 
                            savedService.id(), savedService.name(), savedService.category(), savedService.price(), savedService.active()));
            })
            .doOnError(error -> log.error("Service: Error updating service with ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Void> deleteService(UUID id) {
        log.debug("Service: Starting to delete service with ID: {}", id);
        return serviceRepository.deleteById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: deleteById({})", id))
            .doOnSuccess(unused -> log.info("Service: Service deleted successfully from repository: id={}", id))
            .doOnError(error -> log.error("Service: Error deleting service with ID {}: {}", id, error.getMessage(), error));
    }

    public Flux<Service> getServicesByCategory(String category) {
        log.debug("Service: Starting to retrieve services by category: {}", category);
        return serviceRepository.findByCategory(category)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByCategory({})", category))
            .doOnNext(service -> log.trace("Service: Found service in category: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.debug("Service: Completed service retrieval by category: {}", category))
            .doOnError(error -> log.error("Service: Error retrieving services by category {}: {}", category, error.getMessage(), error));
    }

    public Flux<Service> getActiveServicesByCategory(String category) {
        log.debug("Service: Starting to retrieve active services by category: {}", category);
        return serviceRepository.findByCategoryAndActiveTrue(category)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByCategoryAndActiveTrue({})", category))
            .doOnNext(service -> log.trace("Service: Found active service in category: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.debug("Service: Completed active service retrieval by category: {}", category))
            .doOnError(error -> log.error("Service: Error retrieving active services by category {}: {}", category, error.getMessage(), error));
    }

    public Flux<Service> searchServices(String name) {
        log.debug("Service: Starting to search services with name containing: {}", name);
        return serviceRepository.findByNameContainingIgnoreCase(name)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByNameContainingIgnoreCase({})", name))
            .doOnNext(service -> log.trace("Service: Found service in search: id={}, name={}, category={}", service.id(), service.name(), service.category()))
            .doOnComplete(() -> log.debug("Service: Completed service search for name: {}", name))
            .doOnError(error -> log.error("Service: Error searching services with name {}: {}", name, error.getMessage(), error));
    }
}
