package com.beautysalon.service;

import com.beautysalon.model.Service;
import com.beautysalon.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    public List<Service> getActiveServices() {
        return serviceRepository.findActiveServices();
    }
    
    public Optional<Service> getServiceById(UUID id) {
        return serviceRepository.findById(id);
    }
    
    public List<Service> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }
    
    public List<Service> getActiveServicesByCategory(String category) {
        return serviceRepository.findActiveByCategoryServices(category);
    }
    
    public Service createService(Service service) {
        service.setCreatedAt(Instant.now());
        service.setUpdatedAt(Instant.now());
        return serviceRepository.save(service);
    }
    
    public Service updateService(UUID id, Service serviceDetails) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            service.setName(serviceDetails.getName());
            service.setDescription(serviceDetails.getDescription());
            service.setDuration(serviceDetails.getDuration());
            service.setPrice(serviceDetails.getPrice());
            service.setCategory(serviceDetails.getCategory());
            service.setIsActive(serviceDetails.getIsActive());
            service.setUpdatedAt(Instant.now());
            return serviceRepository.save(service);
        }
        return null;
    }
    
    public boolean deleteService(UUID id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
