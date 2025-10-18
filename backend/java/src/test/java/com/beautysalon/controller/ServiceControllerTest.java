package com.beautysalon.controller;

import com.beautysalon.model.Service;
import com.beautysalon.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

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
        when(serviceService.getAllServices()).thenReturn(services);

        // Act
        ResponseEntity<List<Service>> response = serviceController.getAllServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Corte de Cabelo", response.getBody().get(0).getName());
    }

    @Test
    void testGetActiveServices() {
        // Arrange
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getActiveServices()).thenReturn(services);

        // Act
        ResponseEntity<List<Service>> response = serviceController.getActiveServices();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Corte de Cabelo", response.getBody().get(0).getName());
    }

    @Test
    void testGetServiceById_ServiceExists() {
        // Arrange
        when(serviceService.getServiceById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        ResponseEntity<Service> response = serviceController.getServiceById(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Corte de Cabelo", response.getBody().getName());
    }

    @Test
    void testGetServiceById_ServiceNotExists() {
        // Arrange
        when(serviceService.getServiceById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Service> response = serviceController.getServiceById(serviceId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetServicesByCategory() {
        // Arrange
        String category = "Cabelo";
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getServicesByCategory(category)).thenReturn(services);

        // Act
        ResponseEntity<List<Service>> response = serviceController.getServicesByCategory(category);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(category, response.getBody().get(0).getCategory());
    }

    @Test
    void testGetActiveServicesByCategory() {
        // Arrange
        String category = "Cabelo";
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getActiveServicesByCategory(category)).thenReturn(services);

        // Act
        ResponseEntity<List<Service>> response = serviceController.getActiveServicesByCategory(category);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(category, response.getBody().get(0).getCategory());
    }

    @Test
    void testCreateService_Success() {
        // Arrange
        when(serviceService.createService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Service> response = serviceController.createService(testService);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Corte de Cabelo", response.getBody().getName());
    }

    @Test
    void testCreateService_Exception() {
        // Arrange
        when(serviceService.createService(any(Service.class))).thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<Service> response = serviceController.createService(testService);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateService_Success() {
        // Arrange
        when(serviceService.updateService(eq(serviceId), any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Service> response = serviceController.updateService(serviceId, testService);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Corte de Cabelo", response.getBody().getName());
    }

    @Test
    void testUpdateService_NotFound() {
        // Arrange
        when(serviceService.updateService(eq(serviceId), any(Service.class))).thenReturn(null);

        // Act
        ResponseEntity<Service> response = serviceController.updateService(serviceId, testService);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteService_Success() {
        // Arrange
        when(serviceService.deleteService(serviceId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = serviceController.deleteService(serviceId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteService_NotFound() {
        // Arrange
        when(serviceService.deleteService(serviceId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = serviceController.deleteService(serviceId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
