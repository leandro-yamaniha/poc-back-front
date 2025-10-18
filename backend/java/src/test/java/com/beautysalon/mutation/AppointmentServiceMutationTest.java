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
public class AppointmentServiceMutationTest {

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
    void updateAppointment_WhenAppointmentExists_ShouldUpdateAllFields() {
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
        verify(appointmentRepository).findById(testId);
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void updateAppointment_WhenAppointmentDoesNotExist_ShouldReturnNull() {
        // Given
        Appointment updatedDetails = new Appointment();
        updatedDetails.setStatus("RESCHEDULED");

        when(appointmentRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Appointment result = appointmentService.updateAppointment(testId, updatedDetails);

        // Then
        assertNull(result);
        verify(appointmentRepository).findById(testId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
