package com.beautysalon.controller;

import com.beautysalon.model.Appointment;
import com.beautysalon.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private Appointment testAppointment;
    private UUID appointmentId;
    private UUID customerId;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        staffId = UUID.randomUUID();
        
        testAppointment = new Appointment();
        testAppointment.setId(appointmentId);
        testAppointment.setCustomerId(customerId);
        testAppointment.setStaffId(staffId);
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
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(appointmentId, response.getBody().get(0).getId());
    }

    @Test
    void testGetAppointmentById_AppointmentExists() {
        // Arrange
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));

        // Act
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(appointmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentId, response.getBody().getId());
    }

    @Test
    void testGetAppointmentById_AppointmentNotExists() {
        // Arrange
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(appointmentId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAppointmentsByDate() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 15);
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByDate(date)).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(date, response.getBody().get(0).getAppointmentDate());
    }

    @Test
    void testGetAppointmentsByCustomer() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByCustomer(customerId)).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByCustomer(customerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(customerId, response.getBody().get(0).getCustomerId());
    }

    @Test
    void testGetAppointmentsByStaff() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByStaff(staffId)).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByStaff(staffId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(staffId, response.getBody().get(0).getStaffId());
    }

    @Test
    void testGetAppointmentsByStatus() {
        // Arrange
        String status = "scheduled";
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByStatus(status)).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByStatus(status);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(status, response.getBody().get(0).getStatus());
    }

    @Test
    void testGetAppointmentsByDateAndStaff() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 15);
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByDateAndStaff(date, staffId)).thenReturn(appointments);

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDateAndStaff(date, staffId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(date, response.getBody().get(0).getAppointmentDate());
        assertEquals(staffId, response.getBody().get(0).getStaffId());
    }

    @Test
    void testCreateAppointment_Success() {
        // Arrange
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentId, response.getBody().getId());
    }

    @Test
    void testCreateAppointment_Exception() {
        // Arrange
        when(appointmentService.createAppointment(any(Appointment.class))).thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateAppointment_Success() {
        // Arrange
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class))).thenReturn(testAppointment);

        // Act
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(appointmentId, testAppointment);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentId, response.getBody().getId());
    }

    @Test
    void testUpdateAppointment_NotFound() {
        // Arrange
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class))).thenReturn(null);

        // Act
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(appointmentId, testAppointment);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteAppointment_Success() {
        // Arrange
        when(appointmentService.deleteAppointment(appointmentId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = appointmentController.deleteAppointment(appointmentId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteAppointment_NotFound() {
        // Arrange
        when(appointmentService.deleteAppointment(appointmentId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = appointmentController.deleteAppointment(appointmentId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
