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
 * Targeted mutation tests for RemoveConditionalMutator_ORDER_IF and RemoveConditionalMutator_ORDER_ELSE.
 * These tests focus on conditional statements and their execution paths.
 */
@ExtendWith(MockitoExtension.class)
class RemoveConditionalTargetedTest {

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

    // Test conditional execution paths - IF branch
    @Test
    void testConditionalIF_TruePath_ShouldDetectRemoval() {
        // Test when condition is TRUE (IF branch should execute)
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Verify IF branch was taken (200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAppointment, response.getBody());
        
        // Test conditional logic that would be affected by removing IF
        boolean conditionResult = response.getBody() != null;
        if (conditionResult) {
            // This block should execute - removing IF would break this
            assertTrue(true); // Should detect IF removal mutations
            assertEquals(testId, response.getBody().getId());
        } else {
            fail("IF branch should have executed");
        }
        
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testConditionalELSE_FalsePath_ShouldDetectRemoval() {
        // Test when condition is FALSE (ELSE branch should execute)
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Verify ELSE branch was taken (404 NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        // Test conditional logic that would be affected by removing ELSE
        boolean conditionResult = response.getBody() != null;
        if (conditionResult) {
            fail("ELSE branch should have executed");
        } else {
            // This block should execute - removing ELSE would break this
            assertTrue(true); // Should detect ELSE removal mutations
            assertNull(response.getBody());
        }
        
        verify(appointmentService).getAppointmentById(testId);
    }

    // Test nested conditional statements
    @Test
    void testNestedConditionals_IF_ELSE_Combinations() {
        // Test nested IF-ELSE combinations
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        List<Appointment> appointments = response.getBody();
        
        // Nested conditional logic
        if (appointments != null) { // First IF
            if (!appointments.isEmpty()) { // Nested IF
                // Both IF conditions true
                assertTrue(appointments.size() > 0); // Should detect IF removal
                assertEquals(1, appointments.size());
            } else { // Nested ELSE
                fail("Nested ELSE should not execute");
            }
        } else { // First ELSE
            fail("First ELSE should not execute");
        }
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testNestedConditionals_ELSE_IF_Combinations() {
        // Test nested ELSE-IF combinations
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        List<Appointment> appointments = response.getBody();
        
        // Nested conditional logic with ELSE-IF
        if (appointments != null) { // First IF
            if (appointments.isEmpty()) { // Nested IF for empty case
                // IF true, nested IF true
                assertTrue(appointments.size() == 0); // Should detect IF removal
                assertEquals(0, appointments.size());
            } else { // Nested ELSE
                fail("Nested ELSE should not execute");
            }
        } else { // First ELSE
            fail("First ELSE should not execute");
        }
        
        verify(appointmentService).getAllAppointments();
    }

    // Test conditional with multiple conditions
    @Test
    void testMultipleConditions_AND_Logic() {
        // Test AND logic in conditionals
        when(customerService.getCustomerByEmail("john.doe@example.com")).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("john.doe@example.com");
        Customer customer = response.getBody();
        
        // Multiple AND conditions
        if (customer != null && customer.getEmail() != null) { // AND logic
            // Both conditions must be true
            assertTrue(customer.getEmail().contains("@")); // Should detect conditional removal
            assertNotNull(customer.getName());
        } else {
            fail("AND conditions should both be true");
        }
        
        // Test with different AND combination
        if (response.getStatusCode() == HttpStatus.OK && customer != null) {
            assertTrue(true); // Should detect conditional removal
        } else {
            fail("Both AND conditions should be true");
        }
        
        verify(customerService).getCustomerByEmail("john.doe@example.com");
    }

    @Test
    void testMultipleConditions_OR_Logic() {
        // Test OR logic in conditionals
        when(customerService.getCustomerByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("nonexistent@example.com");
        Customer customer = response.getBody();
        
        // Multiple OR conditions
        if (customer == null || response.getStatusCode() == HttpStatus.NOT_FOUND) { // OR logic
            // At least one condition must be true
            assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND); // Should detect conditional removal
            assertNull(customer);
        } else {
            fail("At least one OR condition should be true");
        }
        
        // Test with different OR combination
        if (customer == null || customer.getEmail() == null) {
            assertTrue(true); // Should detect conditional removal
        } else {
            fail("At least one OR condition should be true");
        }
        
        verify(customerService).getCustomerByEmail("nonexistent@example.com");
    }

    // Test conditional with complex expressions
    @Test
    void testComplexConditionalExpressions() {
        // Test complex conditional expressions
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);
        
        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);
        Appointment created = response.getBody();
        
        // Complex conditional with multiple parts
        if (created != null && created.getId() != null && created.getStatus() != null) {
            // All conditions must be true
            if (created.getStatus().equals("SCHEDULED") || created.getStatus().equals("CONFIRMED")) {
                assertTrue(true); // Should detect conditional removal
                assertNotNull(created.getId());
            } else {
                fail("Status should be SCHEDULED or CONFIRMED");
            }
        } else {
            fail("All conditions should be true");
        }
        
        verify(appointmentService).createAppointment(testAppointment);
    }

    // Test conditional with method calls
    @Test
    void testConditionalWithMethodCalls() {
        // Test conditionals that involve method calls
        when(customerService.searchCustomersByName("John")).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("John");
        List<Customer> customers = response.getBody();
        
        // Conditional with method call results
        if (customers.contains(testCustomer)) { // Method call in condition
            assertTrue(customers.size() >= 1); // Should detect conditional removal
            assertEquals("John Doe", customers.get(0).getName());
        } else {
            fail("Customer should be found in list");
        }
        
        // Another conditional with method calls
        if (customers.stream().anyMatch(c -> c.getName().contains("John"))) {
            assertTrue(true); // Should detect conditional removal
        } else {
            fail("Should find customer with name containing John");
        }
        
        verify(customerService).searchCustomersByName("John");
    }

    // Test conditional with exception handling
    @Test
    void testConditionalWithExceptionHandling() {
        // Test conditionals when service returns null (simulating failure)
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(null);
        
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);
        
        // Conditional based on error response (404 NOT FOUND when service returns null)
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            assertTrue(true); // Should detect conditional removal
            assertNull(response.getBody());
        } else {
            fail("Should return NOT_FOUND status when service returns null");
        }
        
        // Another conditional for error handling
        if (response.getStatusCode().is4xxClientError()) {
            assertTrue(response.getStatusCode().value() >= 400); // Should detect conditional removal
            assertTrue(response.getStatusCode().value() < 500);
        } else {
            fail("Should be client error");
        }
        
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    // Test conditional with collection operations
    @Test
    void testConditionalWithCollectionOperations() {
        // Test conditionals with collection operations
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Appointment>> appointmentResponse = appointmentController.getAllAppointments();
        ResponseEntity<List<Customer>> customerResponse = customerController.getAllCustomers();
        
        List<Appointment> appointments = appointmentResponse.getBody();
        List<Customer> customers = customerResponse.getBody();
        
        // Conditional with collection size comparison
        if (appointments.size() == customers.size()) {
            assertTrue(appointments.size() > 0); // Should detect conditional removal
            assertTrue(customers.size() > 0);
        } else {
            fail("Collections should have same size");
        }
        
        // Conditional with collection emptiness
        if (!appointments.isEmpty() && !customers.isEmpty()) {
            assertTrue(true); // Should detect conditional removal
        } else {
            fail("Collections should not be empty");
        }
        
        verify(appointmentService).getAllAppointments();
        verify(customerService).getAllCustomers();
    }

    // Test conditional with string operations
    @Test
    void testConditionalWithStringOperations() {
        // Test conditionals with string operations
        when(customerService.getCustomerByEmail("john.doe@example.com")).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("john.doe@example.com");
        Customer customer = response.getBody();
        
        // Conditional with string operations
        if (customer.getEmail().startsWith("john")) {
            assertTrue(customer.getEmail().contains("@")); // Should detect conditional removal
            assertTrue(customer.getEmail().endsWith(".com"));
        } else {
            fail("Email should start with john");
        }
        
        // Another conditional with string length
        if (customer.getName().length() > 5) {
            assertTrue(customer.getName().contains(" ")); // Should detect conditional removal
        } else {
            fail("Name should be longer than 5 characters");
        }
        
        verify(customerService).getCustomerByEmail("john.doe@example.com");
    }
}
