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
 * Mutation tests specifically targeting ConditionalsBoundaryMutator and RemoveConditionalMutator_ORDER_IF/ELSE.
 * These tests focus on boundary conditions, comparison operators, and conditional logic.
 */
@ExtendWith(MockitoExtension.class)
class BoundaryConditionsMutationTest {

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

    // Test boundary conditions in Optional.isPresent() vs Optional.isEmpty()
    @Test
    void testOptionalBoundaryConditions_Present() {
        // Test the boundary between present and empty Optional
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // This should detect mutations in Optional.map() conditional logic
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(testAppointment, response.getBody());
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testOptionalBoundaryConditions_Empty() {
        // Test the opposite boundary condition
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // This should detect mutations in Optional.orElse() conditional logic
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody());
        verify(appointmentService).getAppointmentById(testId);
    }

    // Test boundary conditions in null checks
    @Test
    void testNullCheckBoundaryConditions_NotNull() {
        // Test when object is not null
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(testAppointment);
        
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);
        
        // This should detect mutations in != null checks
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    @Test
    void testNullCheckBoundaryConditions_Null() {
        // Test when object is null
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(null);
        
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);
        
        // This should detect mutations in == null checks
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    // Test boundary conditions in boolean logic
    @Test
    void testBooleanBoundaryConditions_True() {
        // Test when boolean is true
        when(appointmentService.deleteAppointment(testId)).thenReturn(true);
        
        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);
        
        // This should detect mutations in boolean == true checks
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService).deleteAppointment(testId);
    }

    @Test
    void testBooleanBoundaryConditions_False() {
        // Test when boolean is false
        when(appointmentService.deleteAppointment(testId)).thenReturn(false);
        
        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);
        
        // This should detect mutations in boolean == false checks
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(appointmentService).deleteAppointment(testId);
    }

    // Test boundary conditions with collections
    @Test
    void testCollectionBoundaryConditions_Empty() {
        // Test empty collection boundary
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // This should detect mutations in isEmpty() vs !isEmpty() conditions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionBoundaryConditions_NonEmpty() {
        // Test non-empty collection boundary
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // This should detect mutations in size() > 0 vs size() >= 1 conditions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        verify(appointmentService).getAllAppointments();
    }

    // Test boundary conditions with size comparisons
    @Test
    void testSizeBoundaryConditions_SingleElement() {
        // Test boundary at size = 1
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);
        
        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();
        
        // This should detect mutations in size() == 1 vs size() != 1
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(customerService).getAllCustomers();
    }

    @Test
    void testSizeBoundaryConditions_MultipleElements() {
        // Test boundary at size > 1
        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setName("Jane Doe");
        customer2.setEmail("jane.doe@example.com");
        
        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);
        
        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();
        
        // This should detect mutations in size() > 1 vs size() >= 2
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(customerService).getAllCustomers();
    }

    // Test boundary conditions with string comparisons
    @Test
    void testStringBoundaryConditions_EmptyString() {
        // Test empty string boundary
        when(customerService.searchCustomersByName("")).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("");
        
        // This should detect mutations in string.isEmpty() vs !string.isEmpty()
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(customerService).searchCustomersByName("");
    }

    @Test
    void testStringBoundaryConditions_NonEmptyString() {
        // Test non-empty string boundary
        String searchTerm = "John";
        when(customerService.searchCustomersByName(searchTerm)).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers(searchTerm);
        
        // This should detect mutations in string.length() > 0 vs string.length() >= 1
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(customerService).searchCustomersByName(searchTerm);
    }

    // Test boundary conditions with date comparisons
    @Test
    void testDateBoundaryConditions_Today() {
        // Test boundary at current date
        LocalDate today = LocalDate.now();
        when(appointmentService.getAppointmentsByDate(today)).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(today);
        
        // This should detect mutations in date.equals() vs date.isEqual()
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(appointmentService).getAppointmentsByDate(today);
    }

    @Test
    void testDateBoundaryConditions_Future() {
        // Test boundary at future date
        LocalDate futureDate = LocalDate.now().plusDays(1);
        when(appointmentService.getAppointmentsByDate(futureDate)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDate(futureDate);
        
        // This should detect mutations in date.isAfter() vs date.isEqual()
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(appointmentService).getAppointmentsByDate(futureDate);
    }

    // Test boundary conditions with UUID comparisons
    @Test
    void testUUIDBoundaryConditions_SameUUID() {
        // Test UUID equality boundary
        when(customerService.getCustomerById(testId)).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerById(testId);
        
        // This should detect mutations in uuid.equals() vs uuid == 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).getCustomerById(testId);
    }

    @Test
    void testUUIDBoundaryConditions_DifferentUUID() {
        // Test UUID inequality boundary
        UUID differentId = UUID.randomUUID();
        when(customerService.getCustomerById(differentId)).thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerById(differentId);
        
        // This should detect mutations in !uuid.equals() vs uuid != 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).getCustomerById(differentId);
    }
}
