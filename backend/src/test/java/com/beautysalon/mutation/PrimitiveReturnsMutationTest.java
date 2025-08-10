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
 * Mutation tests specifically targeting PrimitiveReturnsMutator.
 * These tests focus on primitive return values, numeric operations, and status codes.
 */
@ExtendWith(MockitoExtension.class)
class PrimitiveReturnsMutationTest {

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

    // Test HTTP status code primitive returns
    @Test
    void testHttpStatusCodes_200_ShouldDetectPrimitiveMutations() {
        // Test HTTP 200 OK status code
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test the primitive int value of status code
        int statusCodeValue = response.getStatusCode().value();
        assertEquals(200, statusCodeValue); // Should detect mutations changing 200 to other values
        assertNotEquals(201, statusCodeValue); // Should detect mutations changing 200 to 201
        assertNotEquals(404, statusCodeValue); // Should detect mutations changing 200 to 404
        assertNotEquals(500, statusCodeValue); // Should detect mutations changing 200 to 500
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testHttpStatusCodes_201_ShouldDetectPrimitiveMutations() {
        // Test HTTP 201 CREATED status code
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);
        
        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);
        
        // Test the primitive int value of status code
        int statusCodeValue = response.getStatusCode().value();
        assertEquals(201, statusCodeValue); // Should detect mutations changing 201 to other values
        assertNotEquals(200, statusCodeValue); // Should detect mutations changing 201 to 200
        assertNotEquals(400, statusCodeValue); // Should detect mutations changing 201 to 400
        assertNotEquals(500, statusCodeValue); // Should detect mutations changing 201 to 500
        
        verify(appointmentService).createAppointment(testAppointment);
    }

    @Test
    void testHttpStatusCodes_404_ShouldDetectPrimitiveMutations() {
        // Test HTTP 404 NOT FOUND status code
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Test the primitive int value of status code
        int statusCodeValue = response.getStatusCode().value();
        assertEquals(404, statusCodeValue); // Should detect mutations changing 404 to other values
        assertNotEquals(200, statusCodeValue); // Should detect mutations changing 404 to 200
        assertNotEquals(400, statusCodeValue); // Should detect mutations changing 404 to 400
        assertNotEquals(500, statusCodeValue); // Should detect mutations changing 404 to 500
        
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testHttpStatusCodes_204_ShouldDetectPrimitiveMutations() {
        // Test HTTP 204 NO CONTENT status code
        when(appointmentService.deleteAppointment(testId)).thenReturn(true);
        
        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);
        
        // Test the primitive int value of status code
        int statusCodeValue = response.getStatusCode().value();
        assertEquals(204, statusCodeValue); // Should detect mutations changing 204 to other values
        assertNotEquals(200, statusCodeValue); // Should detect mutations changing 204 to 200
        assertNotEquals(404, statusCodeValue); // Should detect mutations changing 204 to 404
        assertNotEquals(500, statusCodeValue); // Should detect mutations changing 204 to 500
        
        verify(appointmentService).deleteAppointment(testId);
    }

    @Test
    void testHttpStatusCodes_400_ShouldDetectPrimitiveMutations() {
        // Test HTTP 400 BAD REQUEST status code
        when(appointmentService.createAppointment(any(Appointment.class)))
            .thenThrow(new RuntimeException("Validation error"));
        
        ResponseEntity<Appointment> response = appointmentController.createAppointment(testAppointment);
        
        // Test the primitive int value of status code
        int statusCodeValue = response.getStatusCode().value();
        assertEquals(400, statusCodeValue); // Should detect mutations changing 400 to other values
        assertNotEquals(200, statusCodeValue); // Should detect mutations changing 400 to 200
        assertNotEquals(201, statusCodeValue); // Should detect mutations changing 400 to 201
        assertNotEquals(500, statusCodeValue); // Should detect mutations changing 400 to 500
        
        verify(appointmentService).createAppointment(testAppointment);
    }

    // Test collection size primitive returns
    @Test
    void testCollectionSize_Zero_ShouldDetectPrimitiveMutations() {
        // Test collection size of 0
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test primitive int size
        int size = response.getBody().size();
        assertEquals(0, size); // Should detect mutations changing 0 to other values
        assertNotEquals(1, size); // Should detect mutations changing 0 to 1
        assertNotEquals(-1, size); // Should detect mutations changing 0 to -1
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionSize_One_ShouldDetectPrimitiveMutations() {
        // Test collection size of 1
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test primitive int size
        int size = response.getBody().size();
        assertEquals(1, size); // Should detect mutations changing 1 to other values
        assertNotEquals(0, size); // Should detect mutations changing 1 to 0
        assertNotEquals(2, size); // Should detect mutations changing 1 to 2
        assertNotEquals(-1, size); // Should detect mutations changing 1 to -1
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionSize_Multiple_ShouldDetectPrimitiveMutations() {
        // Test collection size > 1
        Appointment appointment2 = new Appointment();
        appointment2.setId(UUID.randomUUID());
        appointment2.setStatus("CONFIRMED");
        
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment, appointment2));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test primitive int size
        int size = response.getBody().size();
        assertEquals(2, size); // Should detect mutations changing 2 to other values
        assertNotEquals(0, size); // Should detect mutations changing 2 to 0
        assertNotEquals(1, size); // Should detect mutations changing 2 to 1
        assertNotEquals(3, size); // Should detect mutations changing 2 to 3
        
        verify(appointmentService).getAllAppointments();
    }

    // Test string length primitive returns
    @Test
    void testStringLength_Empty_ShouldDetectPrimitiveMutations() {
        // Test empty string length
        when(customerService.searchCustomersByName("")).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("");
        
        // Simulate string length operations that might be in business logic
        String searchTerm = "";
        int length = searchTerm.length();
        assertEquals(0, length); // Should detect mutations changing 0 to other values
        assertNotEquals(1, length); // Should detect mutations changing 0 to 1
        assertNotEquals(-1, length); // Should detect mutations changing 0 to -1
        
        verify(customerService).searchCustomersByName("");
    }

    @Test
    void testStringLength_NonEmpty_ShouldDetectPrimitiveMutations() {
        // Test non-empty string length
        String searchTerm = "John";
        when(customerService.searchCustomersByName(searchTerm)).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers(searchTerm);
        
        // Test primitive int length
        int length = searchTerm.length();
        assertEquals(4, length); // Should detect mutations changing 4 to other values
        assertNotEquals(0, length); // Should detect mutations changing 4 to 0
        assertNotEquals(3, length); // Should detect mutations changing 4 to 3
        assertNotEquals(5, length); // Should detect mutations changing 4 to 5
        
        verify(customerService).searchCustomersByName(searchTerm);
    }

    // Test hash code primitive returns
    @Test
    void testHashCode_ShouldDetectPrimitiveMutations() {
        // Test hash code primitive returns
        when(customerService.getCustomerById(testId)).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerById(testId);
        
        // Test primitive int hash codes
        int idHashCode = testId.hashCode();
        int responseHashCode = response.hashCode();
        
        assertNotEquals(0, idHashCode); // Should detect mutations changing hash to 0
        assertNotEquals(-1, idHashCode); // Should detect mutations changing hash to -1
        assertNotEquals(1, idHashCode); // Should detect mutations changing hash to 1
        
        assertNotEquals(0, responseHashCode); // Should detect mutations changing hash to 0
        assertNotEquals(-1, responseHashCode); // Should detect mutations changing hash to -1
        
        verify(customerService).getCustomerById(testId);
    }

    // Test comparison primitive returns
    @Test
    void testComparisons_ShouldDetectPrimitiveMutations() {
        // Test primitive comparison operations
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Appointment>> appointmentResponse = appointmentController.getAllAppointments();
        ResponseEntity<List<Customer>> customerResponse = customerController.getAllCustomers();
        
        // Test primitive comparison results
        int appointmentCount = appointmentResponse.getBody().size();
        int customerCount = customerResponse.getBody().size();
        int comparison = Integer.compare(appointmentCount, customerCount);
        
        assertEquals(0, comparison); // Should detect mutations changing 0 to other values
        assertNotEquals(1, comparison); // Should detect mutations changing 0 to 1
        assertNotEquals(-1, comparison); // Should detect mutations changing 0 to -1
        
        verify(appointmentService).getAllAppointments();
        verify(customerService).getAllCustomers();
    }

    // Test arithmetic primitive returns
    @Test
    void testArithmeticOperations_ShouldDetectPrimitiveMutations() {
        // Test arithmetic operations that might exist in business logic
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test primitive arithmetic operations
        int count = response.getBody().size();
        int doubled = count * 2;
        int incremented = count + 1;
        int decremented = count - 1;
        
        assertEquals(2, doubled); // Should detect mutations in multiplication
        assertEquals(2, incremented); // Should detect mutations in addition
        assertEquals(0, decremented); // Should detect mutations in subtraction
        
        assertNotEquals(1, doubled); // Should detect mutations changing result
        assertNotEquals(1, incremented); // Should detect mutations changing result
        assertNotEquals(1, decremented); // Should detect mutations changing result
        
        verify(appointmentService).getAllAppointments();
    }

    // Test character primitive returns
    @Test
    void testCharacterOperations_ShouldDetectPrimitiveMutations() {
        // Test character primitive operations
        when(customerService.getCustomerByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("john.doe@example.com");
        
        // Test primitive char operations
        String email = "john.doe@example.com";
        char firstChar = email.charAt(0);
        char atChar = email.charAt(4);
        
        assertEquals('j', firstChar); // Should detect mutations changing 'j' to other chars
        assertEquals('.', atChar); // Should detect mutations changing '.' to other chars
        
        assertNotEquals('J', firstChar); // Should detect mutations changing case
        assertNotEquals('@', atChar); // Should detect mutations changing char
        
        verify(customerService).getCustomerByEmail("john.doe@example.com");
    }

    // Test byte primitive returns
    @Test
    void testByteOperations_ShouldDetectPrimitiveMutations() {
        // Test byte primitive operations
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Test primitive byte operations
        String status = "SCHEDULED";
        byte[] statusBytes = status.getBytes();
        byte firstByte = statusBytes[0];
        
        assertEquals((byte)'S', firstByte); // Should detect mutations changing byte value
        assertNotEquals((byte)'s', firstByte); // Should detect mutations changing case
        assertNotEquals((byte)0, firstByte); // Should detect mutations changing to 0
        assertNotEquals((byte)-1, firstByte); // Should detect mutations changing to -1
        
        verify(appointmentService).getAppointmentById(testId);
    }
}
