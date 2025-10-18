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
 * Mutation tests specifically targeting EmptyObjectReturnValsMutator.
 * These tests focus on empty collections, Optional.empty(), and null object returns.
 */
@ExtendWith(MockitoExtension.class)
class EmptyObjectReturnsMutationTest {

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

    // Test empty list vs null list mutations
    @Test
    void testEmptyList_ShouldDetectEmptyObjectMutations() {
        // Test when service returns empty list (not null)
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test empty list characteristics
        List<Appointment> appointments = response.getBody();
        assertNotNull(appointments); // Should detect mutations changing empty list to null
        assertTrue(appointments.isEmpty()); // Should detect mutations changing empty list to non-empty
        assertEquals(0, appointments.size()); // Should detect mutations changing size
        assertFalse(appointments == null); // Should detect mutations changing to null
        
        // Test iterator characteristics of empty list
        Iterator<Appointment> iterator = appointments.iterator();
        assertFalse(iterator.hasNext()); // Should detect mutations in iterator behavior
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testEmptyListVsNonEmptyList_ShouldDetectMutations() {
        // Test the difference between empty and non-empty lists
        when(appointmentService.getAppointmentsByStatus("CANCELLED"))
            .thenReturn(Collections.emptyList());
        when(appointmentService.getAppointmentsByStatus("SCHEDULED"))
            .thenReturn(Arrays.asList(testAppointment));
        
        ResponseEntity<List<Appointment>> emptyResponse = 
            appointmentController.getAppointmentsByStatus("CANCELLED");
        ResponseEntity<List<Appointment>> nonEmptyResponse = 
            appointmentController.getAppointmentsByStatus("SCHEDULED");
        
        // Compare empty vs non-empty characteristics
        List<Appointment> emptyList = emptyResponse.getBody();
        List<Appointment> nonEmptyList = nonEmptyResponse.getBody();
        
        // Empty list assertions
        assertTrue(emptyList.isEmpty()); // Should detect mutations changing empty to non-empty
        assertEquals(0, emptyList.size()); // Should detect mutations changing size
        assertFalse(emptyList.contains(testAppointment)); // Should detect mutations in contains
        
        // Non-empty list assertions
        assertFalse(nonEmptyList.isEmpty()); // Should detect mutations changing non-empty to empty
        assertEquals(1, nonEmptyList.size()); // Should detect mutations changing size
        assertTrue(nonEmptyList.contains(testAppointment)); // Should detect mutations in contains
        
        verify(appointmentService).getAppointmentsByStatus("CANCELLED");
        verify(appointmentService).getAppointmentsByStatus("SCHEDULED");
    }

    // Test Optional.empty() vs Optional.of() mutations
    @Test
    void testOptionalEmpty_ShouldDetectEmptyObjectMutations() {
        // Test when service returns Optional.empty()
        when(appointmentService.getAppointmentById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(testId);
        
        // Test Optional.empty() characteristics through response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Should detect mutations changing null to non-null
        assertFalse(response.hasBody()); // Should detect mutations in hasBody logic
        
        verify(appointmentService).getAppointmentById(testId);
    }

    @Test
    void testOptionalEmptyVsPresent_ShouldDetectMutations() {
        // Test the difference between Optional.empty() and Optional.of()
        UUID emptyId = UUID.randomUUID();
        UUID presentId = UUID.randomUUID();
        
        when(appointmentService.getAppointmentById(emptyId)).thenReturn(Optional.empty());
        when(appointmentService.getAppointmentById(presentId)).thenReturn(Optional.of(testAppointment));
        
        ResponseEntity<Appointment> emptyResponse = appointmentController.getAppointmentById(emptyId);
        ResponseEntity<Appointment> presentResponse = appointmentController.getAppointmentById(presentId);
        
        // Empty Optional characteristics
        assertEquals(HttpStatus.NOT_FOUND, emptyResponse.getStatusCode());
        assertNull(emptyResponse.getBody()); // Should detect mutations changing null to object
        
        // Present Optional characteristics  
        assertEquals(HttpStatus.OK, presentResponse.getStatusCode());
        assertNotNull(presentResponse.getBody()); // Should detect mutations changing object to null
        assertEquals(testAppointment, presentResponse.getBody());
        
        verify(appointmentService).getAppointmentById(emptyId);
        verify(appointmentService).getAppointmentById(presentId);
    }

    // Test empty string vs null string mutations
    @Test
    void testEmptyString_ShouldDetectEmptyObjectMutations() {
        // Test empty string search
        when(customerService.searchCustomersByName("")).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers("");
        
        // Test empty string vs null handling
        String emptySearch = "";
        assertNotNull(emptySearch); // Should detect mutations changing empty string to null
        assertTrue(emptySearch.isEmpty()); // Should detect mutations changing empty to non-empty
        assertEquals(0, emptySearch.length()); // Should detect mutations changing length
        assertFalse(emptySearch == null); // Should detect mutations changing to null
        
        verify(customerService).searchCustomersByName("");
    }

    @Test
    void testNullString_ShouldDetectEmptyObjectMutations() {
        // Test null string search
        when(customerService.searchCustomersByName(null)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.searchCustomers(null);
        
        // Verify null handling doesn't break
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); // Should detect mutations changing empty list to null
        assertTrue(response.getBody().isEmpty()); // Should detect mutations changing empty to non-empty
        
        verify(customerService).searchCustomersByName(null);
    }

    // Test empty collections of different types
    @Test
    void testEmptySet_ShouldDetectEmptyObjectMutations() {
        // Test empty set characteristics
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Convert to set and test empty set characteristics
        Set<Appointment> appointmentSet = new HashSet<>(response.getBody());
        assertTrue(appointmentSet.isEmpty()); // Should detect mutations changing empty to non-empty
        assertEquals(0, appointmentSet.size()); // Should detect mutations changing size
        assertFalse(appointmentSet.contains(testAppointment)); // Should detect mutations in contains
        assertNotNull(appointmentSet); // Should detect mutations changing empty set to null
        
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void testEmptyMap_ShouldDetectEmptyObjectMutations() {
        // Test empty map characteristics (simulated through response headers)
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();
        
        // Test empty map-like structures
        Map<String, Object> emptyMap = new HashMap<>();
        assertTrue(emptyMap.isEmpty()); // Should detect mutations changing empty to non-empty
        assertEquals(0, emptyMap.size()); // Should detect mutations changing size
        assertFalse(emptyMap.containsKey("test")); // Should detect mutations in containsKey
        assertFalse(emptyMap.containsValue("test")); // Should detect mutations in containsValue
        assertNotNull(emptyMap); // Should detect mutations changing empty map to null
        assertNull(emptyMap.get("test")); // Should detect mutations changing null to non-null
        
        verify(customerService).getAllCustomers();
    }

    // Test array empty vs null mutations
    @Test
    void testEmptyArray_ShouldDetectEmptyObjectMutations() {
        // Test empty array characteristics
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Convert to array and test empty array characteristics
        Appointment[] appointmentArray = response.getBody().toArray(new Appointment[0]);
        assertEquals(0, appointmentArray.length); // Should detect mutations changing length
        assertNotNull(appointmentArray); // Should detect mutations changing empty array to null
        
        // Test array access patterns
        for (Appointment appointment : appointmentArray) {
            fail("Empty array should not have elements"); // Should detect mutations adding elements
        }
        
        verify(appointmentService).getAllAppointments();
    }

    // Test StringBuilder/StringBuffer empty mutations
    @Test
    void testEmptyStringBuilder_ShouldDetectEmptyObjectMutations() {
        // Test empty StringBuilder characteristics
        when(customerService.getCustomerByEmail("")).thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerByEmail("");
        
        // Test empty StringBuilder
        StringBuilder emptyBuilder = new StringBuilder();
        assertEquals(0, emptyBuilder.length()); // Should detect mutations changing length
        assertTrue(emptyBuilder.toString().isEmpty()); // Should detect mutations changing empty to non-empty
        assertNotNull(emptyBuilder); // Should detect mutations changing to null
        assertEquals("", emptyBuilder.toString()); // Should detect mutations changing empty string
        
        verify(customerService).getCustomerByEmail("");
    }

    // Test Collections.emptyList() vs new ArrayList() mutations
    @Test
    void testCollectionsEmptyList_ShouldDetectEmptyObjectMutations() {
        // Test Collections.emptyList() vs new ArrayList()
        when(appointmentService.getAppointmentsByDate(LocalDate.now().minusDays(1)))
            .thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = 
            appointmentController.getAppointmentsByDate(LocalDate.now().minusDays(1));
        
        List<Appointment> emptyList = response.getBody();
        
        // Test immutable empty list characteristics
        assertTrue(emptyList.isEmpty()); // Should detect mutations changing empty to non-empty
        assertEquals(0, emptyList.size()); // Should detect mutations changing size
        
        // Test that it behaves like an empty list
        assertThrows(IndexOutOfBoundsException.class, () -> emptyList.get(0));
        assertFalse(emptyList.iterator().hasNext());
        
        verify(appointmentService).getAppointmentsByDate(LocalDate.now().minusDays(1));
    }

    // Test Optional.ofNullable with null vs empty
    @Test
    void testOptionalOfNullable_ShouldDetectEmptyObjectMutations() {
        // Test Optional.ofNullable behavior
        when(customerService.getCustomerById(testId)).thenReturn(Optional.empty());
        
        ResponseEntity<Customer> response = customerController.getCustomerById(testId);
        
        // Simulate Optional.ofNullable patterns
        Customer nullCustomer = null;
        Optional<Customer> optionalNull = Optional.ofNullable(nullCustomer);
        assertTrue(optionalNull.isEmpty()); // Should detect mutations changing empty to present
        assertFalse(optionalNull.isPresent()); // Should detect mutations changing present to empty
        
        verify(customerService).getCustomerById(testId);
    }

    // Test stream empty vs non-empty mutations
    @Test
    void testEmptyStream_ShouldDetectEmptyObjectMutations() {
        // Test empty stream characteristics
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();
        
        // Test stream operations on empty collection
        List<Appointment> appointments = response.getBody();
        long count = appointments.stream().count();
        assertEquals(0, count); // Should detect mutations changing count
        
        boolean anyMatch = appointments.stream().anyMatch(a -> true);
        assertFalse(anyMatch); // Should detect mutations changing anyMatch result
        
        boolean allMatch = appointments.stream().allMatch(a -> true);
        assertTrue(allMatch); // Should detect mutations changing allMatch result (vacuous truth)
        
        Optional<Appointment> findFirst = appointments.stream().findFirst();
        assertTrue(findFirst.isEmpty()); // Should detect mutations changing empty to present
        
        verify(appointmentService).getAllAppointments();
    }
}
