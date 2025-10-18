package com.beautysalon.mutation;

import com.beautysalon.model.Appointment;
import com.beautysalon.repository.AppointmentRepository;
import com.beautysalon.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceFinalMutationTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private UUID testId;
    private UUID customerId;
    private UUID staffId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        staffId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        
        testAppointment = new Appointment();
        testAppointment.setId(testId);
        testAppointment.setCustomerId(customerId);
        testAppointment.setStaffId(staffId);
        testAppointment.setServiceId(serviceId);
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        testAppointment.setAppointmentTime(LocalTime.of(10, 0));
        testAppointment.setStatus("CONFIRMED");
        testAppointment.setCreatedAt(Instant.now());
        testAppointment.setUpdatedAt(Instant.now());
    }

    @Test
    void createAppointment_ShouldSetTimestampsExplicitly() {
        // Given
        Appointment newAppointment = new Appointment();
        newAppointment.setCustomerId(customerId);
        newAppointment.setStaffId(staffId);
        newAppointment.setServiceId(serviceId);
        newAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        newAppointment.setAppointmentTime(LocalTime.of(14, 30));
        newAppointment.setStatus("PENDING");

        // Configurar o mock para capturar o argumento passado para save
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            return appointment;
        });

        // When
        Appointment result = appointmentService.createAppointment(newAppointment);

        // Then
        assertNotNull(result);
        // Verificar explicitamente se os timestamps foram definidos com valores não nulos
        verify(appointmentRepository).save(argThat(appointment -> 
            appointment.getCreatedAt() != null && appointment.getUpdatedAt() != null));
    }

    @Test
    void createAppointment_ShouldCallSetCreatedAtAndSetUpdatedAt() {
        // Given
        Appointment newAppointment = new Appointment();
        newAppointment.setCustomerId(customerId);
        newAppointment.setStaffId(staffId);
        newAppointment.setServiceId(serviceId);
        newAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        newAppointment.setAppointmentTime(LocalTime.of(14, 30));
        newAppointment.setStatus("PENDING");

        // Usar um spy para verificar se os métodos setCreatedAt e setUpdatedAt são chamados
        Appointment spyAppointment = spy(newAppointment);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            return appointment;
        });

        // When
        appointmentService.createAppointment(spyAppointment);

        // Then
        // Verificar se os métodos setCreatedAt e setUpdatedAt foram chamados
        verify(spyAppointment).setCreatedAt(any(Instant.class));
        verify(spyAppointment).setUpdatedAt(any(Instant.class));
    }

    @Test
    void updateAppointment_WhenAppointmentExists_ShouldUpdateTimestamp() {
        // Given
        Appointment updatedDetails = new Appointment();
        updatedDetails.setCustomerId(UUID.randomUUID());
        updatedDetails.setStaffId(UUID.randomUUID());
        updatedDetails.setServiceId(UUID.randomUUID());
        updatedDetails.setAppointmentDate(LocalDate.now().plusDays(2));
        updatedDetails.setAppointmentTime(LocalTime.of(14, 30));
        updatedDetails.setStatus("RESCHEDULED");

        when(appointmentRepository.findById(testId)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Capturar o estado antes da atualização
        Instant oldUpdatedAt = testAppointment.getUpdatedAt();

        // When
        Appointment result = appointmentService.updateAppointment(testId, updatedDetails);

        // Then
        assertNotNull(result);
        // Verificar que o timestamp de atualização foi modificado
        assertNotNull(testAppointment.getUpdatedAt(), "UpdatedAt should be set");
        assertNotEquals(oldUpdatedAt, testAppointment.getUpdatedAt(), "UpdatedAt should be updated");
        verify(appointmentRepository).findById(testId);
        verify(appointmentRepository).save(testAppointment);
    }
}
