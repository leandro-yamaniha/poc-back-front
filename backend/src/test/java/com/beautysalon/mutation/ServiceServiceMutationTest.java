package com.beautysalon.mutation;

import com.beautysalon.model.Service;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceMutationTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private Service testService;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        testService = new Service();
        testService.setId(testId);
        testService.setName("Haircut");
        testService.setDescription("Professional haircut service");
        testService.setDuration(30);
        testService.setPrice(BigDecimal.valueOf(25.0));
        testService.setCategory("Hair");
        testService.setCreatedAt(Instant.now());
        testService.setUpdatedAt(Instant.now());
    }

    @Test
    void createService_ShouldSetTimestamps() {
        // Given
        Service newService = new Service();
        newService.setName("Manicure");
        newService.setDescription("Nail care service");
        newService.setDuration(45);
        newService.setPrice(BigDecimal.valueOf(30.0));
        newService.setCategory("Nails");

        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Service result = serviceService.createService(newService);

        // Then
        assertNotNull(result);
        // Verificar se os timestamps foram definidos
        assertNotNull(newService.getCreatedAt());
        assertNotNull(newService.getUpdatedAt());
        verify(serviceRepository).save(newService);
    }

    @Test
    void updateService_WhenServiceExists_ShouldUpdateAllFields() {
        // Given
        Service updatedDetails = new Service();
        updatedDetails.setName("Updated Haircut");
        updatedDetails.setDescription("Premium haircut service");
        updatedDetails.setDuration(40);
        updatedDetails.setPrice(BigDecimal.valueOf(35.0));
        updatedDetails.setCategory("Premium Hair");

        when(serviceRepository.findById(testId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Service result = serviceService.updateService(testId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(updatedDetails.getName(), testService.getName());
        assertEquals(updatedDetails.getDescription(), testService.getDescription());
        assertEquals(updatedDetails.getDuration(), testService.getDuration());
        assertEquals(updatedDetails.getPrice(), testService.getPrice());
        assertEquals(updatedDetails.getCategory(), testService.getCategory());
        assertNotNull(testService.getUpdatedAt());
        verify(serviceRepository).findById(testId);
        verify(serviceRepository).save(testService);
    }

    @Test
    void updateService_WhenServiceDoesNotExist_ShouldReturnNull() {
        // Given
        Service updatedDetails = new Service();
        updatedDetails.setName("Updated Service");

        when(serviceRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Service result = serviceService.updateService(testId, updatedDetails);

        // Then
        assertNull(result);
        verify(serviceRepository).findById(testId);
        verify(serviceRepository, never()).save(any(Service.class));
    }
}
