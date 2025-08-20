package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAllByOrderByCreatedAtDesc();
    }

    public Mono<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    public Mono<Customer> createCustomer(Customer customer) {
        return customerRepository.save(Customer.create(
            customer.name(),
            customer.email(),
            customer.phone(),
            customer.address()
        ));
    }

    public Mono<Customer> updateCustomer(UUID id, Customer customer) {
        return customerRepository.findById(id)
            .flatMap(existingCustomer -> {
                Customer updatedCustomer = existingCustomer.withUpdatedFields(
                    customer.name(),
                    customer.email(),
                    customer.phone(),
                    customer.address()
                );
                return customerRepository.save(updatedCustomer);
            });
    }

    public Mono<Void> deleteCustomer(UUID id) {
        return customerRepository.deleteById(id);
    }

    public Flux<Customer> searchCustomers(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public Mono<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
