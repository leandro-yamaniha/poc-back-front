package com.beautysalon.service;

import com.beautysalon.model.Service;
import com.beautysalon.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceSimpleTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private Service testService;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        testService = new Service();
        testService.setId(serviceId);
        testService.setName("Corte de Cabelo");
        testService.setDescription("Corte profissional masculino");
        testService.setPrice(new BigDecimal("50.00"));
        testService.setDuration(60);
        testService.setCategory("Cabelo");
        testService.setIsActive(true);
    }

    @Test
    void testGetAllServices() {
        // Arrange
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findAll()).thenReturn(services);

        // Act
        List<Service> result = serviceService.getAllServices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testService.getName(), result.get(0).getName());
        verify(serviceRepository).findAll();
    }

    @Test
    void testGetActiveServices() {
        // Arrange
        List<Service> activeServices = Arrays.asList(testService);
        when(serviceRepository.findActiveServices()).thenReturn(activeServices);

        // Act
        List<Service> result = serviceService.getActiveServices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testService.getName(), result.get(0).getName());
        verify(serviceRepository).findActiveServices();
    }

    @Test
    void testGetServiceById_ServiceExists() {
        // Arrange
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        Optional<Service> result = serviceService.getServiceById(serviceId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testService.getName(), result.get().getName());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void testGetServiceById_ServiceNotExists() {
        // Arrange
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        Optional<Service> result = serviceService.getServiceById(serviceId);

        // Assert
        assertFalse(result.isPresent());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void testGetServicesByCategory() {
        // Arrange
        String category = "Cabelo";
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByCategory(category)).thenReturn(services);

        // Act
        List<Service> result = serviceService.getServicesByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory());
        verify(serviceRepository).findByCategory(category);
    }

    @Test
    void testGetActiveServicesByCategory() {
        // Arrange
        String category = "Cabelo";
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findActiveByCategoryServices(category)).thenReturn(services);

        // Act
        List<Service> result = serviceService.getActiveServicesByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory());
        verify(serviceRepository).findActiveByCategoryServices(category);
    }

    @Test
    void testCreateService() {
        // Arrange
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // Act
        Service result = serviceService.createService(testService);

        // Assert
        assertNotNull(result);
        assertEquals(testService.getName(), result.getName());
        assertEquals(testService.getPrice(), result.getPrice());
        verify(serviceRepository).save(testService);
    }

    @Test
    void testUpdateService_ServiceExists() {
        // Arrange
        Service updatedService = new Service();
        updatedService.setName("Corte Premium");
        updatedService.setPrice(new BigDecimal("75.00"));
        
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // Act
        Service result = serviceService.updateService(serviceId, updatedService);

        // Assert
        assertNotNull(result);
        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void testUpdateService_ServiceNotExists() {
        // Arrange
        Service updatedService = new Service();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        Service result = serviceService.updateService(serviceId, updatedService);

        // Assert
        assertNull(result);
        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void testDeleteService_ServiceExists() {
        // Arrange
        when(serviceRepository.existsById(serviceId)).thenReturn(true);
        doNothing().when(serviceRepository).deleteById(serviceId);

        // Act
        boolean result = serviceService.deleteService(serviceId);

        // Assert
        assertTrue(result);
        verify(serviceRepository).existsById(serviceId);
        verify(serviceRepository).deleteById(serviceId);
    }

    @Test
    void testDeleteService_ServiceNotExists() {
        // Arrange
        when(serviceRepository.existsById(serviceId)).thenReturn(false);

        // Act
        boolean result = serviceService.deleteService(serviceId);

        // Assert
        assertFalse(result);
        verify(serviceRepository).existsById(serviceId);
        verify(serviceRepository, never()).deleteById(serviceId);
    }
}
