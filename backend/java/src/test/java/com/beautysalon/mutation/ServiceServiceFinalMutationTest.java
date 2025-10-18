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
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceFinalMutationTest {

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
        testService.setPrice(BigDecimal.valueOf(30.0));
        testService.setCategory("Hair");
        testService.setDuration(30);
        testService.setIsActive(true);
        testService.setCreatedAt(Instant.now());
        testService.setUpdatedAt(Instant.now());
    }

    @Test
    void createService_ShouldSetTimestampsExplicitly() {
        // Given
        Service newService = new Service();
        newService.setName("Manicure");
        newService.setDescription("Professional manicure service");
        newService.setPrice(BigDecimal.valueOf(25.0));
        newService.setCategory("Nails");
        newService.setDuration(45);
        newService.setIsActive(true);

        // Configurar o mock para capturar o argumento passado para save
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service service = invocation.getArgument(0);
            return service;
        });

        // When
        Service result = serviceService.createService(newService);

        // Then
        assertNotNull(result);
        // Verificar explicitamente se os timestamps foram definidos com valores não nulos
        verify(serviceRepository).save(argThat(service -> 
            service.getCreatedAt() != null && service.getUpdatedAt() != null));
    }

    @Test
    void createService_ShouldCallSetCreatedAtAndSetUpdatedAt() {
        // Given
        Service newService = new Service();
        newService.setName("Manicure");
        newService.setDescription("Professional manicure service");
        newService.setPrice(BigDecimal.valueOf(25.0));
        newService.setCategory("Nails");
        newService.setDuration(45);
        newService.setIsActive(true);

        // Usar um spy para verificar se os métodos setCreatedAt e setUpdatedAt são chamados
        Service spyService = spy(newService);
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service service = invocation.getArgument(0);
            return service;
        });

        // When
        serviceService.createService(spyService);

        // Then
        // Verificar se os métodos setCreatedAt e setUpdatedAt foram chamados
        verify(spyService).setCreatedAt(any(Instant.class));
        verify(spyService).setUpdatedAt(any(Instant.class));
    }

    @Test
    void updateService_WhenServiceExists_ShouldUpdateTimestamp() {
        // Given
        Service updatedDetails = new Service();
        updatedDetails.setName("Updated Service");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setPrice(BigDecimal.valueOf(35.0));
        updatedDetails.setCategory("Updated Category");
        updatedDetails.setDuration(60);
        updatedDetails.setIsActive(false);

        when(serviceRepository.findById(testId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // Capturar o estado antes da atualização
        Instant oldUpdatedAt = testService.getUpdatedAt();

        // When
        Service result = serviceService.updateService(testId, updatedDetails);

        // Then
        assertNotNull(result);
        // Verificar que o timestamp de atualização foi modificado
        assertNotNull(testService.getUpdatedAt(), "UpdatedAt should be set");
        assertNotEquals(oldUpdatedAt, testService.getUpdatedAt(), "UpdatedAt should be updated");
        verify(serviceRepository).findById(testId);
        verify(serviceRepository).save(testService);
    }
}
