package com.beautysalon.service;

import com.beautysalon.model.Customer;
import com.beautysalon.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceSimpleTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(testId);
        testCustomer.setName("Maria Silva");
        testCustomer.setEmail("maria@email.com");
        testCustomer.setPhone("(11) 99999-1111");
        testCustomer.setAddress("Rua das Flores, 123");
        testCustomer.setCreatedAt(Instant.now());
        testCustomer.setUpdatedAt(Instant.now());
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        // Given
        List<Customer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        // When
        List<Customer> actualCustomers = customerService.getAllCustomers();

        // Then
        assertEquals(expectedCustomers, actualCustomers);
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        // Given
        when(customerRepository.findById(testId)).thenReturn(Optional.of(testCustomer));

        // When
        Optional<Customer> result = customerService.getCustomerById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository).findById(testId);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(customerRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerService.getCustomerById(testId);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository).findById(testId);
    }

    @Test
    void createCustomer_ShouldSaveAndReturnCustomer() {
        // Given
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        Customer result = customerService.createCustomer(testCustomer);

        // Then
        assertEquals(testCustomer, result);
        verify(customerRepository).save(testCustomer);
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void updateCustomer_WhenCustomerExists_ShouldUpdateAndReturnCustomer() {
        // Given
        Customer updatedDetails = new Customer();
        updatedDetails.setName("Maria Santos");
        updatedDetails.setEmail("maria.santos@email.com");
        updatedDetails.setPhone("(11) 88888-2222");
        updatedDetails.setAddress("Rua Nova, 456");

        when(customerRepository.findById(testId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        Customer result = customerService.updateCustomer(testId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals("Maria Santos", testCustomer.getName());
        assertEquals("maria.santos@email.com", testCustomer.getEmail());
        assertEquals("(11) 88888-2222", testCustomer.getPhone());
        assertEquals("Rua Nova, 456", testCustomer.getAddress());
        verify(customerRepository).findById(testId);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldReturnTrue() {
        // Given
        when(customerRepository.existsById(testId)).thenReturn(true);

        // When
        boolean result = customerService.deleteCustomer(testId);

        // Then
        assertTrue(result);
        verify(customerRepository).existsById(testId);
        verify(customerRepository).deleteById(testId);
    }

    @Test
    void deleteCustomer_WhenCustomerDoesNotExist_ShouldReturnFalse() {
        // Given
        when(customerRepository.existsById(testId)).thenReturn(false);

        // When
        boolean result = customerService.deleteCustomer(testId);

        // Then
        assertFalse(result);
        verify(customerRepository).existsById(testId);
        verify(customerRepository, never()).deleteById(testId);
    }

    @Test
    void getCustomerByEmail_WhenCustomerExists_ShouldReturnCustomer() {
        // Given
        String email = "maria@email.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(testCustomer));

        // When
        Optional<Customer> result = customerService.getCustomerByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository).findByEmail(email);
    }

    @Test
    void getCustomerByEmail_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@email.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerService.getCustomerByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository).findByEmail(email);
    }

    @Test
    void searchCustomersByName_WhenCustomersFound_ShouldReturnCustomers() {
        // Given
        String searchName = "Maria";
        List<Customer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findByNameContaining("%" + searchName + "%")).thenReturn(expectedCustomers);

        // When
        List<Customer> result = customerService.searchCustomersByName(searchName);

        // Then
        assertEquals(expectedCustomers, result);
        assertEquals(1, result.size());
        assertEquals(testCustomer, result.get(0));
        verify(customerRepository).findByNameContaining("%" + searchName + "%");
    }

    @Test
    void searchCustomersByName_WhenNoCustomersFound_ShouldReturnEmptyList() {
        // Given
        String searchName = "NonExistent";
        List<Customer> emptyList = Arrays.asList();
        when(customerRepository.findByNameContaining("%" + searchName + "%")).thenReturn(emptyList);

        // When
        List<Customer> result = customerService.searchCustomersByName(searchName);

        // Then
        assertTrue(result.isEmpty());
        verify(customerRepository).findByNameContaining("%" + searchName + "%");
    }

    @Test
    void updateCustomer_WhenCustomerDoesNotExist_ShouldReturnNull() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        Customer updatedDetails = new Customer();
        updatedDetails.setName("New Name");
        updatedDetails.setEmail("new@email.com");

        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Customer result = customerService.updateCustomer(nonExistentId, updatedDetails);

        // Then
        assertNull(result);
        verify(customerRepository).findById(nonExistentId);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
