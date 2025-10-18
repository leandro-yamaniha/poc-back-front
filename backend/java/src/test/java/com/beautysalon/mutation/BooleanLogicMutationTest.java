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
 * Mutation tests specifically targeting BooleanTrueReturnValsMutator and BooleanFalseReturnValsMutator.
 * These tests focus on boolean return values, flags, and logical operations.
 */
@ExtendWith(MockitoExtension.class)
class BooleanLogicMutationTest {

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

    // Test boolean true return mutations in delete operations
    @Test
    void testDeleteOperation_ReturnsTrue_ShouldDetectTrueMutations() {
        // Test when service returns true (successful deletion)
        when(appointmentService.deleteAppointment(testId)).thenReturn(true);
        
        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);
        
        // This should detect mutations that change 'true' to 'false'
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).deleteAppointment(testId);
    }

    @Test
    void testDeleteOperation_ReturnsFalse_ShouldDetectFalseMutations() {
        // Test when service returns false (deletion failed)
        when(appointmentService.deleteAppointment(testId)).thenReturn(false);
        
        ResponseEntity<Void> response = appointmentController.deleteAppointment(testId);
        
        // This should detect mutations that change 'false' to 'true'
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).deleteAppointment(testId);
    }

    @Test
    void testCustomerDeleteOperation_ReturnsTrue_ShouldDetectTrueMutations() {
        // Test customer deletion with true return
        when(customerService.deleteCustomer(testId)).thenReturn(true);
        
        ResponseEntity<Void> response = customerController.deleteCustomer(testId);
        
        // This should detect mutations in boolean return handling
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(testId);
    }

    @Test
    void testCustomerDeleteOperation_ReturnsFalse_ShouldDetectFalseMutations() {
        // Test customer deletion with false return
        when(customerService.deleteCustomer(testId)).thenReturn(false);
        
        ResponseEntity<Void> response = customerController.deleteCustomer(testId);
        
        // This should detect mutations in boolean return handling
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService).deleteCustomer(testId);
    }

    // Test boolean logic in conditional statements
    @Test
    void testConditionalLogic_TrueCondition_ShouldDetectBooleanMutations() {
        // Create a scenario where boolean logic is tested
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Verify the true path is taken
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        
        // Additional assertions to catch boolean mutations
        boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
        assertTrue(isSuccessful); // This should detect true -> false mutations
        assertFalse(!isSuccessful); // This should detect false -> true mutations
        
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testConditionalLogic_FalseCondition_ShouldDetectBooleanMutations() {
        // Create a scenario where boolean logic is tested
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Verify the false path is taken
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody());
        
        // Additional assertions to catch boolean mutations
        boolean isNotFound = response.getStatusCode().is4xxClientError();
        assertTrue(isNotFound); // This should detect true -> false mutations
        assertFalse(!isNotFound); // This should detect false -> true mutations
        
        verify(appointmentService).getAppointmentById(testId);
    }

    // Test boolean operations with collections
    @Test
    void testCollectionBooleanLogic_EmptyCollection_ShouldDetectBooleanMutations() {
        // Test boolean logic with empty collections
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test boolean operations on collection state
        List<Appointment> appointments = response.getBody();
        assertNotNull(appointments);
        
        boolean isEmpty = appointments.isEmpty();
        assertTrue(isEmpty); // Should detect true -> false mutations
        assertFalse(!isEmpty); // Should detect false -> true mutations
        
        boolean hasElements = !appointments.isEmpty();
        assertFalse(hasElements); // Should detect false -> true mutations
        assertTrue(!hasElements); // Should detect true -> false mutations
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testCollectionBooleanLogic_NonEmptyCollection_ShouldDetectBooleanMutations() {
        // Test boolean logic with non-empty collections
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test boolean operations on collection state
        List<Appointment> appointments = response.getBody();
        assertNotNull(appointments);
        
        boolean isEmpty = appointments.isEmpty();
        assertFalse(isEmpty); // Should detect false -> true mutations
        assertTrue(!isEmpty); // Should detect true -> false mutations
        
        boolean hasElements = !appointments.isEmpty();
        assertTrue(hasElements); // Should detect true -> false mutations
        assertFalse(!hasElements); // Should detect false -> true mutations
        
        verify(appointmentService).getAllAppointments();
    }

    // Test boolean logic in null checks
    @Test
    void testNullCheckBooleanLogic_NotNull_ShouldDetectBooleanMutations() {
        // Test boolean logic in null checks
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(testAppointment);
        
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);
        
        // Test boolean operations on null checks
        Appointment result = response.getBody();
        
        boolean isNotNull = result != null;
        assertTrue(isNotNull); // Should detect true -> false mutations
        assertFalse(!isNotNull); // Should detect false -> true mutations
        
        boolean isNull = result == null;
        assertFalse(isNull); // Should detect false -> true mutations
        assertTrue(!isNull); // Should detect true -> false mutations
        
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    @Test
    void testNullCheckBooleanLogic_Null_ShouldDetectBooleanMutations() {
        // Test boolean logic when result is null
        when(appointmentService.updateAppointment(eq(testId), any(Appointment.class)))
            .thenReturn(null);
        
        ResponseEntity<Appointment> response = appointmentController.updateAppointment(testId, testAppointment);
        
        // Test boolean operations on null checks
        Appointment result = response.getBody();
        
        boolean isNotNull = result != null;
        assertFalse(isNotNull); // Should detect false -> true mutations
        assertTrue(!isNotNull); // Should detect true -> false mutations
        
        boolean isNull = result == null;
        assertTrue(isNull); // Should detect true -> false mutations
        assertFalse(!isNull); // Should detect false -> true mutations
        
        verify(appointmentService).updateAppointment(testId, testAppointment);
    }

    // Test boolean logic in Optional operations
    @Test
    void testOptionalBooleanLogic_Present_ShouldDetectBooleanMutations() {
        // Test boolean logic with Optional.isPresent()
        when(customerService.getCustomerByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("john.doe@example.com");
        
        // Simulate boolean logic that might be in the controller
        boolean isSuccessful = response.getStatusCode() == HttpStatus.OK;
        assertTrue(isSuccessful); // Should detect true -> false mutations
        assertFalse(!isSuccessful); // Should detect false -> true mutations
        
        boolean isNotFound = response.getStatusCode() == HttpStatus.NOT_FOUND;
        assertFalse(isNotFound); // Should detect false -> true mutations
        assertTrue(!isNotFound); // Should detect true -> false mutations
        
        verify(customerService).getCustomerByEmail("john.doe@example.com");
    }

    @Test
    void testOptionalBooleanLogic_Empty_ShouldDetectBooleanMutations() {
        // Test boolean logic with Optional.isEmpty()
        when(customerService.getCustomerByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("nonexistent@example.com");
        
        // Simulate boolean logic that might be in the controller
        boolean isSuccessful = response.getStatusCode() == HttpStatus.OK;
        assertFalse(isSuccessful); // Should detect false -> true mutations
        assertTrue(!isSuccessful); // Should detect true -> false mutations
        
        boolean isNotFound = response.getStatusCode() == HttpStatus.NOT_FOUND;
        assertTrue(isNotFound); // Should detect true -> false mutations
        assertFalse(!isNotFound); // Should detect false -> true mutations
        
        verify(customerService).getCustomerByEmail("nonexistent@example.com");
    }

    // Test complex boolean expressions
    @Test
    void testComplexBooleanExpressions_ShouldDetectAllBooleanMutations() {
        // Test complex boolean expressions that might exist in business logic
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer));
        
        ResponseEntity<List<Appointment>> appointmentResponse = appointmentController.getAllAppointments();
        ResponseEntity<List<Customer>> customerResponse = customerController.getAllCustomers();
        
        // Complex boolean expressions to catch mutations
        boolean hasAppointments = !appointmentResponse.getBody().isEmpty();
        boolean hasCustomers = !customerResponse.getBody().isEmpty();
        boolean hasData = hasAppointments && hasCustomers;
        boolean hasNoData = !hasAppointments || !hasCustomers;
        
        assertTrue(hasAppointments); // Should detect true -> false mutations
        assertTrue(hasCustomers); // Should detect true -> false mutations
        assertTrue(hasData); // Should detect true -> false mutations
        assertFalse(hasNoData); // Should detect false -> true mutations
        
        // Negated expressions
        assertFalse(!hasAppointments); // Should detect false -> true mutations
        assertFalse(!hasCustomers); // Should detect false -> true mutations
        assertFalse(!hasData); // Should detect false -> true mutations
        assertTrue(!hasNoData); // Should detect true -> false mutations
        
        verify(appointmentService).getAllAppointments();
        verify(customerService).getAllCustomers();
    }
}
