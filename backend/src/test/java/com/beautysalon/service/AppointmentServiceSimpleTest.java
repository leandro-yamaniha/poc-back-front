package com.beautysalon.service;

import com.beautysalon.model.Appointment;
import com.beautysalon.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class AppointmentServiceSimpleTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private UUID appointmentId;
    private UUID customerId;
    private UUID staffId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        staffId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        
        testAppointment = new Appointment();
        testAppointment.setId(appointmentId);
        testAppointment.setCustomerId(customerId);
        testAppointment.setStaffId(staffId);
        testAppointment.setServiceId(serviceId);
        testAppointment.setAppointmentDate(LocalDate.of(2025, 8, 15));
        testAppointment.setAppointmentTime(LocalTime.of(14, 30));
        testAppointment.setStatus("scheduled");
        testAppointment.setTotalPrice(new BigDecimal("75.00"));
        testAppointment.setNotes("Cliente preferencial");
    }

    @Test
    void testGetAllAppointments() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAllAppointments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getCustomerId(), result.get(0).getCustomerId());
        verify(appointmentRepository).findAll();
    }

    @Test
    void testGetAppointmentById_AppointmentExists() {
        // Arrange
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAppointment.getCustomerId(), result.get().getCustomerId());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void testGetAppointmentById_AppointmentNotExists() {
        // Arrange
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        // Assert
        assertFalse(result.isPresent());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void testGetAppointmentsByDate() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 15);
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByAppointmentDate(date)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByDate(date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(date, result.get(0).getAppointmentDate());
        verify(appointmentRepository).findByAppointmentDate(date);
    }

    @Test
    void testGetAppointmentsByCustomer() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByCustomerId(customerId)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByCustomer(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
        verify(appointmentRepository).findByCustomerId(customerId);
    }

    @Test
    void testGetAppointmentsByStaff() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByStaffId(staffId)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByStaff(staffId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(staffId, result.get(0).getStaffId());
        verify(appointmentRepository).findByStaffId(staffId);
    }

    @Test
    void testGetAppointmentsByStatus() {
        // Arrange
        String status = "scheduled";
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByStatus(status)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(appointmentRepository).findByStatus(status);
    }

    @Test
    void testGetAppointmentsByDateAndStaff() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 15);
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByDateAndStaff(date, staffId)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.getAppointmentsByDateAndStaff(date, staffId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(date, result.get(0).getAppointmentDate());
        assertEquals(staffId, result.get(0).getStaffId());
        verify(appointmentRepository).findByDateAndStaff(date, staffId);
    }

    @Test
    void testCreateAppointment() {
        // Arrange
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.createAppointment(testAppointment);

        // Assert
        assertNotNull(result);
        assertEquals(testAppointment.getCustomerId(), result.getCustomerId());
        assertEquals(testAppointment.getTotalPrice(), result.getTotalPrice());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void testUpdateAppointment_AppointmentExists() {
        // Arrange
        Appointment updatedAppointment = new Appointment();
        updatedAppointment.setStatus("confirmed");
        updatedAppointment.setTotalPrice(new BigDecimal("85.00"));
        
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.updateAppointment(appointmentId, updatedAppointment);

        // Assert
        assertNotNull(result);
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testUpdateAppointment_AppointmentNotExists() {
        // Arrange
        Appointment updatedAppointment = new Appointment();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act
        Appointment result = appointmentService.updateAppointment(appointmentId, updatedAppointment);

        // Assert
        assertNull(result);
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testDeleteAppointment_AppointmentExists() {
        // Arrange
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        // Act
        boolean result = appointmentService.deleteAppointment(appointmentId);

        // Assert
        assertTrue(result);
        verify(appointmentRepository).existsById(appointmentId);
        verify(appointmentRepository).deleteById(appointmentId);
    }

    @Test
    void testDeleteAppointment_AppointmentNotExists() {
        // Arrange
        when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

        // Act
        boolean result = appointmentService.deleteAppointment(appointmentId);

        // Assert
        assertFalse(result);
        verify(appointmentRepository).existsById(appointmentId);
        verify(appointmentRepository, never()).deleteById(appointmentId);
    }
}
