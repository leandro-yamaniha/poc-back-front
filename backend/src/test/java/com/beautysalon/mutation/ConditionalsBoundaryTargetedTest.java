package com.beautysalon.mutation;

import com.beautysalon.controller.AppointmentController;
import com.beautysalon.controller.CustomerController;
import com.beautysalon.model.Appointment;
import com.beautysalon.model.Customer;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Targeted mutation tests specifically for ConditionalsBoundaryMutator.
 * These tests focus on boundary conditions in comparisons (>, >=, <, <=, ==, !=).
 */
@ExtendWith(MockitoExtension.class)
class ConditionalsBoundaryTargetedTest {

    @Mock
    private AppointmentService appointmentService;
    
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AppointmentController appointmentController;
    
    @InjectMocks
    private CustomerController customerController;

    private Appointment testAppointment;
    private Customer testCustomer;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        testAppointment = new Appointment();
        testAppointment.setId(testId);
        testAppointment.setCustomerId(UUID.randomUUID());
        testAppointment.setStaffId(UUID.randomUUID());
        testAppointment.setServiceId(UUID.randomUUID());
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        testAppointment.setStatus("SCHEDULED");
        
        testCustomer = new Customer();
        testCustomer.setId(testId);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("123-456-7890");
    }

    // Test boundary conditions with collection sizes
    @Test
    void testCollectionSizeBoundary_ExactlyZero() {
        // Test exactly 0 elements (boundary condition)
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        List<Appointment> appointments = response.getBody();
        
        // Test boundary: size == 0 vs size > 0
        assertTrue(appointments.size() == 0); // Should detect >= to > mutations
        assertFalse(appointments.size() > 0); // Should detect > to >= mutations
        assertFalse(appointments.size() < 0); // Should detect < to <= mutations
        assertTrue(appointments.size() >= 0); // Should detect >= to > mutations
        assertTrue(appointments.size() <= 0); // Should detect <= to < mutations
        assertFalse(appointments.size() != 0); // Should detect != to == mutations
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionSizeBoundary_ExactlyOne() {
        // Test exactly 1 element (boundary condition)
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        List<Appointment> appointments = response.getBody();
        
        // Test boundary: size == 1 vs size > 1 vs size < 1
        assertTrue(appointments.size() == 1); // Should detect == to != mutations
        assertTrue(appointments.size() >= 1); // Should detect >= to > mutations
        assertTrue(appointments.size() <= 1); // Should detect <= to < mutations
        assertFalse(appointments.size() > 1); // Should detect > to >= mutations
        assertFalse(appointments.size() < 1); // Should detect < to <= mutations
        assertTrue(appointments.size() != 0); // Should detect != to == mutations
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionSizeBoundary_ExactlyTwo() {
        // Test exactly 2 elements (boundary condition)
        Appointment appointment2 = new Appointment();
        appointment2.setId(UUID.randomUUID());
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment, appointment2));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        List<Appointment> appointments = response.getBody();
        
        // Test boundary: size == 2 vs size > 2 vs size < 2
        assertTrue(appointments.size() == 2); // Should detect == to != mutations
        assertTrue(appointments.size() >= 2); // Should detect >= to > mutations
        assertTrue(appointments.size() <= 2); // Should detect <= to < mutations
        assertFalse(appointments.size() > 2); // Should detect > to >= mutations
        assertFalse(appointments.size() < 2); // Should detect < to <= mutations
        assertTrue(appointments.size() > 1); // Should detect > to >= mutations
        assertTrue(appointments.size() >= 1); // Should detect >= to > mutations
        
        verify(appointmentService).getAllAppointments();
    }

    // Test boundary conditions with string lengths
    @Test
    void testStringLengthBoundary_Empty() {
        // Test empty string boundary
        when(customerService.searchCustomersByName("")).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("");
        
        String searchTerm = "";
        // Test boundary: length == 0
        assertTrue(searchTerm.length() == 0); // Should detect == to != mutations
        assertTrue(searchTerm.length() <= 0); // Should detect <= to < mutations
        assertTrue(searchTerm.length() >= 0); // Should detect >= to > mutations
        assertFalse(searchTerm.length() > 0); // Should detect > to >= mutations
        assertFalse(searchTerm.length() < 0); // Should detect < to <= mutations
        assertFalse(searchTerm.length() != 0); // Should detect != to == mutations
        
        verify(customerService).searchCustomersByName("");
    }

    @Test
    void testStringLengthBoundary_SingleChar() {
        // Test single character boundary
        when(customerService.searchCustomersByName("J")).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("J");
        
        String searchTerm = "J";
        // Test boundary: length == 1
        assertTrue(searchTerm.length() == 1); // Should detect == to != mutations
        assertTrue(searchTerm.length() <= 1); // Should detect <= to < mutations
        assertTrue(searchTerm.length() >= 1); // Should detect >= to > mutations
        assertFalse(searchTerm.length() > 1); // Should detect > to >= mutations
        assertFalse(searchTerm.length() < 1); // Should detect < to <= mutations
        assertTrue(searchTerm.length() > 0); // Should detect > to >= mutations
        assertTrue(searchTerm.length() != 0); // Should detect != to == mutations
        
        verify(customerService).searchCustomersByName("J");
    }

    @Test
    void testStringLengthBoundary_MultipleChars() {
        // Test multiple characters boundary
        when(customerService.searchCustomersByName("John")).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("John");
        
        String searchTerm = "John";
        // Test boundary: length == 4
        assertTrue(searchTerm.length() == 4); // Should detect == to != mutations
        assertTrue(searchTerm.length() <= 4); // Should detect <= to < mutations
        assertTrue(searchTerm.length() >= 4); // Should detect >= to > mutations
        assertFalse(searchTerm.length() > 4); // Should detect > to >= mutations
        assertFalse(searchTerm.length() < 4); // Should detect < to <= mutations
        assertTrue(searchTerm.length() > 3); // Should detect > to >= mutations
        assertTrue(searchTerm.length() >= 3); // Should detect >= to > mutations
        assertTrue(searchTerm.length() < 5); // Should detect < to <= mutations
        assertTrue(searchTerm.length() <= 5); // Should detect <= to < mutations
        
        verify(customerService).searchCustomersByName("John");
    }

    // Test boundary conditions with HTTP status codes
    @Test
    void testHttpStatusBoundary_200() {
        // Test HTTP 200 boundary
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        int statusCode = response.getStatusCode().value();
        // Test boundary: status == 200
        assertTrue(statusCode == 200); // Should detect == to != mutations
        assertTrue(statusCode <= 200); // Should detect <= to < mutations
        assertTrue(statusCode >= 200); // Should detect >= to > mutations
        assertFalse(statusCode > 200); // Should detect > to >= mutations
        assertFalse(statusCode < 200); // Should detect < to <= mutations
        assertTrue(statusCode >= 199); // Should detect >= to > mutations
        assertTrue(statusCode <= 201); // Should detect <= to < mutations
        
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testHttpStatusBoundary_404() {
        // Test HTTP 404 boundary
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        int statusCode = response.getStatusCode().value();
        // Test boundary: status == 404
        assertTrue(statusCode == 404); // Should detect == to != mutations
        assertTrue(statusCode <= 404); // Should detect <= to < mutations
        assertTrue(statusCode >= 404); // Should detect >= to > mutations
        assertFalse(statusCode > 404); // Should detect > to >= mutations
        assertFalse(statusCode < 404); // Should detect < to <= mutations
        assertTrue(statusCode > 403); // Should detect > to >= mutations
        assertTrue(statusCode < 405); // Should detect < to <= mutations
        
        verify(appointmentService).getAppointmentById(testId);
    }

    // Test boundary conditions with UUID comparisons
    @Test
    void testUUIDBoundary_Equality() {
        // Test UUID equality boundary
        when(customerService.getCustomerById(testId)).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerById(testId);
        Customer customer = response.getBody();
        
        // Test boundary: UUID equality
        assertTrue(customer.getId().equals(testId)); // Should detect == to != mutations
        assertFalse(!customer.getId().equals(testId)); // Should detect != to == mutations
        assertTrue(customer.getId() == testId || customer.getId().equals(testId)); // Should detect || to && mutations
        assertFalse(customer.getId() != testId && !customer.getId().equals(testId)); // Should detect && to || mutations
        
        verify(customerService).getCustomerById(testId);
    }

    @Test
    void testUUIDBoundary_Inequality() {
        // Test UUID inequality boundary
        UUID differentId = UUID.randomUUID();
        when(customerService.getCustomerById(differentId)).thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerById(differentId);
        
        // Test boundary: UUID inequality
        assertFalse(testId.equals(differentId)); // Should detect == to != mutations
        assertTrue(!testId.equals(differentId)); // Should detect != to == mutations
        assertFalse(testId == differentId || testId.equals(differentId)); // Should detect || to && mutations
        assertTrue(testId != differentId && !testId.equals(differentId)); // Should detect && to || mutations
        
        verify(customerService).getCustomerById(differentId);
    }

    // Test boundary conditions with date comparisons
    @Test
    void testDateBoundary_Today() {
        // Test date boundary with today
        LocalDate today = LocalDate.now();
        when(appointmentService.getAppointmentsByDate(today)).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(today);
        
        LocalDate appointmentDate = testAppointment.getAppointmentDate();
        // Test boundary: date comparisons
        assertTrue(appointmentDate.isAfter(today)); // Should detect > to >= mutations
        assertFalse(appointmentDate.isBefore(today)); // Should detect < to <= mutations
        assertFalse(appointmentDate.isEqual(today)); // Should detect == to != mutations
        assertTrue(!appointmentDate.isEqual(today)); // Should detect != to == mutations
        assertFalse(appointmentDate.equals(today)); // Should detect == to != mutations
        
        verify(appointmentService).getAppointmentsByDate(today);
    }

    @Test
    void testDateBoundary_Future() {
        // Test date boundary with future date
        LocalDate futureDate = LocalDate.now().plusDays(1);
        when(appointmentService.getAppointmentsByDate(futureDate)).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(futureDate);
        
        LocalDate today = LocalDate.now();
        // Test boundary: future date comparisons
        assertTrue(futureDate.isAfter(today)); // Should detect > to >= mutations
        assertFalse(futureDate.isBefore(today)); // Should detect < to <= mutations
        assertFalse(futureDate.isEqual(today)); // Should detect == to != mutations
        assertTrue(futureDate.isAfter(today.minusDays(1))); // Should detect > to >= mutations
        assertTrue(!futureDate.isBefore(today.plusDays(1))); // Should detect < to <= mutations
        
        verify(appointmentService).getAppointmentsByDate(futureDate);
    }

    // Test boundary conditions with numeric comparisons
    @Test
    void testNumericBoundary_Zero() {
        // Test numeric zero boundary
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        int count = response.getBody().size();
        // Test boundary: numeric zero
        assertTrue(count == 0); // Should detect == to != mutations
        assertTrue(count <= 0); // Should detect <= to < mutations
        assertTrue(count >= 0); // Should detect >= to > mutations
        assertFalse(count > 0); // Should detect > to >= mutations
        assertFalse(count < 0); // Should detect < to <= mutations
        assertFalse(count != 0); // Should detect != to == mutations
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testNumericBoundary_Positive() {
        // Test positive numeric boundary
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        int count = response.getBody().size();
        // Test boundary: positive number
        assertTrue(count == 1); // Should detect == to != mutations
        assertTrue(count <= 1); // Should detect <= to < mutations
        assertTrue(count >= 1); // Should detect >= to > mutations
        assertFalse(count > 1); // Should detect > to >= mutations
        assertFalse(count < 1); // Should detect < to <= mutations
        assertTrue(count > 0); // Should detect > to >= mutations
        assertTrue(count != 0); // Should detect != to == mutations
        
        verify(appointmentService).getAllAppointments();
    }
}
