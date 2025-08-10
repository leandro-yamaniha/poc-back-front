package com.beautysalon.mutation;

import com.beautysalon.controller.AppointmentController;
import com.beautysalon.model.Appointment;
import com.beautysalon.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Mutation tests for AppointmentController to achieve 90% mutation coverage.
 * Focuses on detecting mutations in:
 * - Conditional logic (if/else, null checks)
 * - Return values and status codes
 * - Exception handling
 * - Method calls and parameters
 */
@ExtendWith(MockitoExtension.class)
class AppointmentControllerMutationTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Appointment testAppointment;
    private UUID testId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        testId = UUID.randomUUID();
        testAppointment = new Appointment();
        testAppointment.setId(testId);
        testAppointment.setCustomerId(UUID.randomUUID());
        testAppointment.setStaffId(UUID.randomUUID());
        testAppointment.setServiceId(UUID.randomUUID());
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        testAppointment.setStatus("SCHEDULED");
        testAppointment.setNotes("Test appointment");
    }

    // Test mutations in getAppointmentById - conditional logic and return values
    @Test
    void getAppointmentById_WhenAppointmentExists_ShouldReturnOk() {
        // This test detects mutations in Optional.map() and ResponseEntity.ok()
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));

        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAppointment, response.getBody());
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void getAppointmentById_WhenAppointmentNotExists_ShouldReturnNotFound() {
        // This test detects mutations in Optional.orElse() and ResponseEntity.notFound()
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());

        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void getAppointmentById_WithNullId_ShouldHandleGracefully() {
        // Test boundary condition - null ID
        when(appointmentService.getAppointmentById(null)).thenReturn(Optional.empty());

        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(appointmentService).getAppointmentById(null);
    }

    // Test mutations in createAppointment - exception handling and status codes
    @Test
    void createAppointment_WhenSuccessful_ShouldReturnCreated() {
        // This test detects mutations in HttpStatus.CREATED and try-catch logic
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);

        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAppointment, response.getBody());
        verify(appointmentService).createAppointment(testAppointment);
    }

    @Test
    void createAppointment_WhenServiceThrowsException_ShouldReturnBadRequest() {
        // This test detects mutations in catch block and HttpStatus.BAD_REQUEST
        when(appointmentService.createAppointment(any(Appointment.class)))
            .thenThrow(new RuntimeException("Validation error"));

        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).createAppointment(testAppointment);
    }

    @Test
    void createAppointment_WithNullAppointment_ShouldReturnBadRequest() {
        // Test boundary condition - null appointment
        when(appointmentService.createAppointment(null))
            .thenThrow(new IllegalArgumentException("Appointment cannot be null"));

        ResponseEntity<Appointment> response = appointmentController.createAppointment(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(appointmentService).createAppointment(null);
    }

    // Test mutations in updateAppointment - conditional logic
    @Test
    void updateAppointment_WhenAppointmentExists_ShouldReturnOk() {
        // This test detects mutations in null check and ResponseEntity.ok()
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(testAppointment);

        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAppointment, response.getBody());
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    @Test
    void updateAppointment_WhenAppointmentNotExists_ShouldReturnNotFound() {
        // This test detects mutations in null check and ResponseEntity.notFound()
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(null);

        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    // Test mutations in deleteAppointment - boolean logic and conditional returns
    @Test
    void deleteAppointment_WhenSuccessful_ShouldReturnNoContent() {
        // This test detects mutations in boolean return and HttpStatus.NO_CONTENT
        when(appointmentService.deleteAppointment(testId)).thenReturn(true);

        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService).deleteAppointment(testId);
    }

    @Test
    void deleteAppointment_WhenNotFound_ShouldReturnNotFound() {
        // This test detects mutations in boolean logic and conditional branches
        when(appointmentService.deleteAppointment(testId)).thenReturn(false);

        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(appointmentService).deleteAppointment(testId);
    }

    // Test mutations in getAllAppointments - method calls and return values
    @Test
    void getAllAppointments_ShouldReturnOkWithList() {
        // This test detects mutations in method calls and ResponseEntity.ok()
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void getAllAppointments_WhenEmptyList_ShouldReturnOkWithEmptyList() {
        // Test boundary condition - empty list
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(appointmentService).getAllAppointments();
    }

    // Test mutations in getAppointmentsByDate - parameter passing and method calls
    @Test
    void getAppointmentsByDate_ShouldReturnOkWithList() {
        // This test detects mutations in parameter passing and method calls
        LocalDate testDate = LocalDate.now();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByDate(testDate)).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(testDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
        verify(appointmentService).getAppointmentsByDate(testDate);
    }

    // Test mutations in getAppointmentsByCustomer - UUID parameter handling
    @Test
    void getAppointmentsByCustomer_ShouldReturnOkWithList() {
        // This test detects mutations in UUID parameter passing
        UUID customerId = UUID.randomUUID();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByCustomer(customerId)).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByCustomer(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
        verify(appointmentService).getAppointmentsByCustomer(customerId);
    }

    // Test mutations in getAppointmentsByStatus - String parameter handling
    @Test
    void getAppointmentsByStatus_ShouldReturnOkWithList() {
        // This test detects mutations in String parameter passing
        String status = "SCHEDULED";
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByStatus(status)).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByStatus(status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
        verify(appointmentService).getAppointmentsByStatus(status);
    }

    @Test
    void getAppointmentsByStatus_WithNullStatus_ShouldHandleGracefully() {
        // Test boundary condition - null status
        when(appointmentService.getAppointmentsByStatus(null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByStatus(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(appointmentService).getAppointmentsByStatus(null);
    }

    // Test mutations in getAppointmentsByDateAndStaff - multiple parameters
    @Test
    void getAppointmentsByDateAndStaff_ShouldReturnOkWithList() {
        // This test detects mutations in multiple parameter passing
        LocalDate testDate = LocalDate.now();
        UUID staffId = UUID.randomUUID();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAppointmentsByDateAndStaff(testDate, staffId)).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDateAndStaff(testDate, staffId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
        verify(appointmentService).getAppointmentsByDateAndStaff(testDate, staffId);
    }

    // Integration test using MockMvc to test HTTP layer mutations
    @Test
    void createAppointment_HttpLayer_ShouldReturnCreated() throws Exception {
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppointment)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(appointmentService).createAppointment(any(Appointment.class));
    }

    @Test
    void getAppointmentById_HttpLayer_ShouldReturnOk() throws Exception {
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));

        mockMvc.perform(get("/api/appointments/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void getAppointmentById_HttpLayer_WhenNotFound_ShouldReturn404() throws Exception {
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/{id}", testId))
                .andExpect(status().isNotFound());

        verify(appointmentService).getAppointmentById(testId);
    }
}
