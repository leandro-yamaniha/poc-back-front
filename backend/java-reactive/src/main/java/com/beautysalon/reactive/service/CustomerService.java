package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        log.info("CustomerService initialized with repository: {}", customerRepository.getClass().getSimpleName());
    }

    public Flux<Customer> getAllCustomers() {
        log.debug("Service: Starting to retrieve all customers");
        return customerRepository.findAll()
            .doOnSubscribe(subscription -> log.debug("Repository call: findAll()"))
            .doOnNext(customer -> log.trace("Service: Retrieved customer from repository: id={}, name={}", customer.id(), customer.name()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved all customers from repository"))
            .doOnError(error -> log.error("Service: Error retrieving all customers: {}", error.getMessage(), error));
    }

    public Mono<Customer> getCustomerById(UUID id) {
        log.debug("Service: Starting to retrieve customer by ID: {}", id);
        return customerRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({})", id))
            .doOnNext(customer -> log.debug("Service: Found customer: id={}, name={}, email={}", 
                    customer.id(), customer.name(), customer.email()))
            .doOnSuccess(customer -> {
                if (customer == null) {
                    log.debug("Service: No customer found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Service: Error retrieving customer by ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Customer> createCustomer(Customer customer) {
        log.debug("Service: Starting to create customer: name={}, email={}, phone={}", 
                customer.name(), customer.email(), customer.phone());
        
        Customer newCustomer = Customer.create(
            customer.name(),
            customer.email(),
            customer.phone(),
            customer.address()
        );
        
        log.debug("Service: Created customer object with ID: {}", newCustomer.id());
        
        return customerRepository.save(newCustomer)
            .doOnSubscribe(subscription -> log.debug("Repository call: save() for customer ID: {}", newCustomer.id()))
            .doOnNext(savedCustomer -> log.info("Service: Customer saved successfully: id={}, name={}, email={}", 
                    savedCustomer.id(), savedCustomer.name(), savedCustomer.email()))
            .doOnError(error -> log.error("Service: Error creating customer with name={}, email={}: {}", 
                    customer.name(), customer.email(), error.getMessage(), error));
    }

    public Mono<Customer> updateCustomer(UUID id, Customer customer) {
        log.debug("Service: Starting to update customer: id={}, name={}, email={}", 
                id, customer.name(), customer.email());
        
        return customerRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({}) for update", id))
            .doOnNext(existingCustomer -> log.debug("Service: Found existing customer for update: id={}, name={}", 
                    existingCustomer.id(), existingCustomer.name()))
            .flatMap(existingCustomer -> {
                Customer updatedCustomer = existingCustomer.withUpdatedFields(
                    customer.name(),
                    customer.email(),
                    customer.phone(),
                    customer.address()
                );
                log.debug("Service: Created updated customer object: id={}, name={}, email={}", 
                        updatedCustomer.id(), updatedCustomer.name(), updatedCustomer.email());
                
                return customerRepository.save(updatedCustomer)
                    .doOnSubscribe(sub -> log.debug("Repository call: save() for updated customer ID: {}", updatedCustomer.id()))
                    .doOnNext(savedCustomer -> log.info("Service: Customer updated successfully: id={}, name={}, email={}", 
                            savedCustomer.id(), savedCustomer.name(), savedCustomer.email()));
            })
            .doOnError(error -> log.error("Service: Error updating customer with ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Void> deleteCustomer(UUID id) {
        log.debug("Service: Starting to delete customer with ID: {}", id);
        return customerRepository.deleteById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: deleteById({})", id))
            .doOnSuccess(unused -> log.info("Service: Customer deleted successfully from repository: id={}", id))
            .doOnError(error -> log.error("Service: Error deleting customer with ID {}: {}", id, error.getMessage(), error));
    }

    public Flux<Customer> searchCustomers(String name) {
        log.debug("Service: Starting to search customers with name containing: {}", name);
        return customerRepository.findByNameContainingIgnoreCase(name)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByNameContainingIgnoreCase({})", name))
            .doOnNext(customer -> log.trace("Service: Found customer in search: id={}, name={}", customer.id(), customer.name()))
            .doOnComplete(() -> log.debug("Service: Completed customer search for name: {}", name))
            .doOnError(error -> log.error("Service: Error searching customers with name {}: {}", name, error.getMessage(), error));
    }

    public Mono<Customer> findByEmail(String email) {
        log.debug("Service: Starting to find customer by email: {}", email);
        return customerRepository.findByEmail(email)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByEmail({})", email))
            .doOnNext(customer -> log.debug("Service: Found customer by email: id={}, name={}, email={}", 
                    customer.id(), customer.name(), customer.email()))
            .doOnSuccess(customer -> {
                if (customer == null) {
                    log.debug("Service: No customer found with email: {}", email);
                }
            })
            .doOnError(error -> log.error("Service: Error finding customer by email {}: {}", email, error.getMessage(), error));
    }
}
