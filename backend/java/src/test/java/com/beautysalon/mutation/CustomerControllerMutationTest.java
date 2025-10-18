package com.beautysalon.mutation;

import com.beautysalon.controller.CustomerController;
import com.beautysalon.model.Customer;
import com.beautysalon.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Mutation tests for CustomerController to detect mutations in:
 * - Conditional logic and null checks
 * - Return values and HTTP status codes
 * - Exception handling
 * - Method parameter passing
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerMutationTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(testId);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("123-456-7890");
    }

    // Test mutations in getCustomerById - Optional handling and conditional returns
    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnOk() {
        when(customerService.getCustomerById(testId)).thenReturn(Optional.of(testCustomer));

        ResponseEntity<Customer> response = customerController.getCustomerById(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).getCustomerById(testId);
    }

    @Test
    void getCustomerById_WhenCustomerNotExists_ShouldReturnNotFound() {
        when(customerService.getCustomerById(testId)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getCustomerById(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).getCustomerById(testId);
    }

    // Test mutations in createCustomer - exception handling and status codes
    @Test
    void createCustomer_WhenSuccessful_ShouldReturnCreated() {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Customer> response = customerController.createCustomer(testCustomer);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).createCustomer(testCustomer);
    }

    @Test
    void createCustomer_WhenServiceThrowsException_ShouldReturnBadRequest() {
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new RuntimeException("Validation error"));

        ResponseEntity<Customer> response = customerController.createCustomer(testCustomer);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).createCustomer(testCustomer);
    }

    // Test mutations in updateCustomer - null checks and conditional logic
    @Test
    void updateCustomer_WhenCustomerExists_ShouldReturnOk() {
        when(customerService.updateCustomer(eq(testId), any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Customer> response = customerController.updateCustomer(testId, testCustomer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).updateCustomer(testId, testCustomer);
    }

    @Test
    void updateCustomer_WhenCustomerNotExists_ShouldReturnNotFound() {
        when(customerService.updateCustomer(eq(testId), any(Customer.class))).thenReturn(null);

        ResponseEntity<Customer> response = customerController.updateCustomer(testId, testCustomer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).updateCustomer(testId, testCustomer);
    }

    // Test mutations in deleteCustomer - boolean logic
    @Test
    void deleteCustomer_WhenSuccessful_ShouldReturnNoContent() {
        when(customerService.deleteCustomer(testId)).thenReturn(true);

        ResponseEntity<Void> response = customerController.deleteCustomer(testId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(testId);
    }

    @Test
    void deleteCustomer_WhenNotFound_ShouldReturnNotFound() {
        when(customerService.deleteCustomer(testId)).thenReturn(false);

        ResponseEntity<Void> response = customerController.deleteCustomer(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService).deleteCustomer(testId);
    }

    // Test mutations in getAllCustomers - method calls and return handling
    @Test
    void getAllCustomers_ShouldReturnOkWithList() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
        verify(customerService).getAllCustomers();
    }

    @Test
    void getAllCustomers_WhenEmptyList_ShouldReturnOkWithEmptyList() {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(customerService).getAllCustomers();
    }

    // Test mutations in getCustomerByEmail - parameter passing and Optional handling
    @Test
    void getCustomerByEmail_WhenCustomerExists_ShouldReturnOk() {
        String email = "john.doe@example.com";
        when(customerService.getCustomerByEmail(email)).thenReturn(Optional.of(testCustomer));

        ResponseEntity<Customer> response = customerController.getCustomerByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).getCustomerByEmail(email);
    }

    @Test
    void getCustomerByEmail_WhenCustomerNotExists_ShouldReturnNotFound() {
        String email = "nonexistent@example.com";
        when(customerService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getCustomerByEmail(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).getCustomerByEmail(email);
    }

    // Test mutations in searchCustomers - parameter passing and list handling
    @Test
    void searchCustomers_ShouldReturnOkWithList() {
        String searchName = "John";
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.searchCustomersByName(searchName)).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.searchCustomers(searchName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
        verify(customerService).searchCustomersByName(searchName);
    }

    @Test
    void searchCustomers_WhenNoResults_ShouldReturnOkWithEmptyList() {
        String searchName = "NonExistent";
        when(customerService.searchCustomersByName(searchName)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Customer>> response = customerController.searchCustomers(searchName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(customerService).searchCustomersByName(searchName);
    }

    // Boundary condition tests to catch edge case mutations
    @Test
    void getCustomerById_WithNullId_ShouldHandleGracefully() {
        when(customerService.getCustomerById(null)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getCustomerById(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService).getCustomerById(null);
    }

    @Test
    void createCustomer_WithNullCustomer_ShouldReturnBadRequest() {
        when(customerService.createCustomer(null))
            .thenThrow(new IllegalArgumentException("Customer cannot be null"));

        ResponseEntity<Customer> response = customerController.createCustomer(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(customerService).createCustomer(null);
    }

    @Test
    void getCustomerByEmail_WithNullEmail_ShouldHandleGracefully() {
        when(customerService.getCustomerByEmail(null)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getCustomerByEmail(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService).getCustomerByEmail(null);
    }

    @Test
    void searchCustomers_WithNullName_ShouldReturnEmptyList() {
        when(customerService.searchCustomersByName(null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Customer>> response = customerController.searchCustomers(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(customerService).searchCustomersByName(null);
    }
}
