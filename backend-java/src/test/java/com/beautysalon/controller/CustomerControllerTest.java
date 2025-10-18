package com.beautysalon.controller;

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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(customerId);
        testCustomer.setName("João Silva");
        testCustomer.setEmail("joao@email.com");
        testCustomer.setPhone("11999999999");
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act
        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).getName());
    }

    @Test
    void testGetCustomerById_CustomerExists() {
        // Arrange
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerById(customerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getName());
    }

    @Test
    void testGetCustomerById_CustomerNotExists() {
        // Arrange
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerById(customerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetCustomerByEmail_CustomerExists() {
        // Arrange
        String email = "joao@email.com";
        when(customerService.getCustomerByEmail(email)).thenReturn(Optional.of(testCustomer));

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerByEmail(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getEmail());
    }

    @Test
    void testGetCustomerByEmail_CustomerNotExists() {
        // Arrange
        String email = "inexistente@email.com";
        when(customerService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerByEmail(email);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testSearchCustomers() {
        // Arrange
        String searchName = "João";
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.searchCustomersByName(searchName)).thenReturn(customers);

        // Act
        ResponseEntity<List<Customer>> response = customerController.searchCustomers(searchName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).getName());
    }

    @Test
    void testCreateCustomer_Success() {
        // Arrange
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act
        ResponseEntity<Customer> response = customerController.createCustomer(testCustomer);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getName());
    }

    @Test
    void testCreateCustomer_Exception() {
        // Arrange
        when(customerService.createCustomer(any(Customer.class))).thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<Customer> response = customerController.createCustomer(testCustomer);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        when(customerService.updateCustomer(eq(customerId), any(Customer.class))).thenReturn(testCustomer);

        // Act
        ResponseEntity<Customer> response = customerController.updateCustomer(customerId, testCustomer);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getName());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        when(customerService.updateCustomer(eq(customerId), any(Customer.class))).thenReturn(null);

        // Act
        ResponseEntity<Customer> response = customerController.updateCustomer(customerId, testCustomer);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        when(customerService.deleteCustomer(customerId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(customerId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Arrange
        when(customerService.deleteCustomer(customerId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(customerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
