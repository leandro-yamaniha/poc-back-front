package com.beautysalon.reactive;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.repository.CustomerRepository;
import com.beautysalon.reactive.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    private Customer testCustomer;
    private UUID testId;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
        testId = UUID.randomUUID();
        testCustomer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        when(customerRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(Flux.just(testCustomer));

        StepVerifier.create(customerService.getAllCustomers())
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void getCustomerById_WhenExists_ShouldReturnCustomer() {
        when(customerRepository.findById(testId))
            .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.getCustomerById(testId))
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void getCustomerById_WhenNotExists_ShouldReturnEmpty() {
        when(customerRepository.findById(testId))
            .thenReturn(Mono.empty());

        StepVerifier.create(customerService.getCustomerById(testId))
            .verifyComplete();
    }

    @Test
    void createCustomer_ShouldSaveAndReturnCustomer() {
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.createCustomer(testCustomer))
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void updateCustomer_WhenExists_ShouldUpdateAndReturn() {
        Customer updatedCustomer = testCustomer.withUpdatedFields("Jane Doe", null, null, null);
        
        when(customerRepository.findById(testId))
            .thenReturn(Mono.just(testCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(Mono.just(updatedCustomer));

        StepVerifier.create(customerService.updateCustomer(testId, updatedCustomer))
            .expectNext(updatedCustomer)
            .verifyComplete();
    }

    @Test
    void deleteCustomer_ShouldCallRepository() {
        when(customerRepository.deleteById(testId))
            .thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(testId))
            .verifyComplete();
    }

    @Test
    void searchCustomers_ShouldReturnMatchingCustomers() {
        when(customerRepository.findByNameContainingIgnoreCase("John"))
            .thenReturn(Flux.just(testCustomer));

        StepVerifier.create(customerService.searchCustomers("John"))
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void findByEmail_ShouldReturnCustomer() {
        when(customerRepository.findByEmail("john@example.com"))
            .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.findByEmail("john@example.com"))
            .expectNext(testCustomer)
            .verifyComplete();
    }
}
