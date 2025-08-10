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
public class AppointmentServiceAdditionalMutationTest {

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
    void createAppointment_ShouldSetTimestamps() {
        // Given
        Appointment newAppointment = new Appointment();
        newAppointment.setCustomerId(customerId);
        newAppointment.setStaffId(staffId);
        newAppointment.setServiceId(serviceId);
        newAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        newAppointment.setAppointmentTime(LocalTime.of(14, 30));
        newAppointment.setStatus("PENDING");
        newAppointment.setNotes("Special requests");
        newAppointment.setTotalPrice(BigDecimal.valueOf(50.0));

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.createAppointment(newAppointment);

        // Then
        assertNotNull(result);
        // Verificar se os timestamps foram definidos
        assertNotNull(newAppointment.getCreatedAt());
        assertNotNull(newAppointment.getUpdatedAt());
        verify(appointmentRepository).save(newAppointment);
    }

    @Test
    void updateAppointment_WhenAppointmentExists_ShouldUpdateAllFieldsIncludingOptional() {
        // Given
        Appointment updatedDetails = new Appointment();
        updatedDetails.setCustomerId(UUID.randomUUID());
        updatedDetails.setStaffId(UUID.randomUUID());
        updatedDetails.setServiceId(UUID.randomUUID());
        updatedDetails.setAppointmentDate(LocalDate.now().plusDays(2));
        updatedDetails.setAppointmentTime(LocalTime.of(14, 30));
        updatedDetails.setStatus("RESCHEDULED");
        updatedDetails.setNotes("Updated notes");
        updatedDetails.setTotalPrice(BigDecimal.valueOf(75.0));

        when(appointmentRepository.findById(testId)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.updateAppointment(testId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(updatedDetails.getCustomerId(), testAppointment.getCustomerId());
        assertEquals(updatedDetails.getStaffId(), testAppointment.getStaffId());
        assertEquals(updatedDetails.getServiceId(), testAppointment.getServiceId());
        assertEquals(updatedDetails.getAppointmentDate(), testAppointment.getAppointmentDate());
        assertEquals(updatedDetails.getAppointmentTime(), testAppointment.getAppointmentTime());
        assertEquals(updatedDetails.getStatus(), testAppointment.getStatus());
        assertEquals(updatedDetails.getNotes(), testAppointment.getNotes());
        assertEquals(updatedDetails.getTotalPrice(), testAppointment.getTotalPrice());
        assertNotNull(testAppointment.getUpdatedAt());
        verify(appointmentRepository).findById(testId);
        verify(appointmentRepository).save(testAppointment);
    }
}
