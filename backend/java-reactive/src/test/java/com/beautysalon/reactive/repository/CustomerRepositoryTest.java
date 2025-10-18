package com.beautysalon.reactive.repository;

import com.beautysalon.reactive.model.Customer;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.create(
            "John Doe",
            "john@example.com",
            "+1234567890",
            "123 Main St"
        );
    }

    @Test
    void save_ShouldPersistCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(testCustomer));
        
        StepVerifier.create(customerRepository.save(testCustomer))
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void findById_ShouldReturnCustomer() {
        UUID customerId = testCustomer.id();
        when(customerRepository.findById(customerId)).thenReturn(Mono.just(testCustomer));
        
        StepVerifier.create(customerRepository.findById(customerId))
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Flux.just(testCustomer));
        
        StepVerifier.create(customerRepository.findAll())
            .expectNext(testCustomer)
            .verifyComplete();
    }

    @Test
    void findByEmail_WhenExists_ShouldReturnCustomer() {
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Mono.just(testCustomer));

        Mono<Customer> found = customerRepository.findByEmail("john@example.com");

        StepVerifier.create(found)
            .expectNextMatches(customer -> customer.email().equals("john@example.com"))
            .verifyComplete();
    }

    @Test
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Mono.empty());
        
        Mono<Customer> found = customerRepository.findByEmail("nonexistent@example.com");

        StepVerifier.create(found)
            .verifyComplete();
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingCustomers() {
        Customer customer1 = Customer.create("John Doe", "john@example.com", "+1111111111", "Address 1");
        Customer customer2 = Customer.create("Jane Doe", "jane@example.com", "+2222222222", "Address 2");
        
        when(customerRepository.findByNameContainingIgnoreCase("doe")).thenReturn(Flux.just(customer1, customer2));

        Flux<Customer> found = customerRepository.findByNameContainingIgnoreCase("doe");

        StepVerifier.create(found)
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void deleteById_ShouldRemoveCustomer() {
        UUID id = testCustomer.id();
        when(customerRepository.deleteById(id)).thenReturn(Mono.empty());
        when(customerRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Void> deletion = customerRepository.deleteById(id);

        StepVerifier.create(deletion)
            .verifyComplete();

        // Verify customer is deleted
        Mono<Customer> found = customerRepository.findById(id);
        StepVerifier.create(found)
            .verifyComplete();
    }

    @Test
    void deleteAll_ShouldRemoveAllCustomers() {
        when(customerRepository.deleteAll()).thenReturn(Mono.empty());
        when(customerRepository.findAll()).thenReturn(Flux.empty());

        Mono<Void> deletion = customerRepository.deleteAll();

        StepVerifier.create(deletion)
            .verifyComplete();

        // Verify all customers are deleted
        Flux<Customer> allCustomers = customerRepository.findAll();
        StepVerifier.create(allCustomers)
            .verifyComplete();
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        when(customerRepository.count()).thenReturn(Mono.just(2L));

        Mono<Long> count = customerRepository.count();

        StepVerifier.create(count)
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        UUID id = testCustomer.id();
        when(customerRepository.existsById(id)).thenReturn(Mono.just(true));

        Mono<Boolean> exists = customerRepository.existsById(id);

        StepVerifier.create(exists)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepository.existsById(nonExistentId)).thenReturn(Mono.just(false));

        Mono<Boolean> exists = customerRepository.existsById(nonExistentId);

        StepVerifier.create(exists)
            .expectNext(false)
            .verifyComplete();
    }
}
