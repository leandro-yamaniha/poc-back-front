package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Validated
@Tag(name = "Customers", description = "Customer management operations")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
        log.info("CustomerController initialized successfully");
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve all customers from the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customers")
    public Flux<Customer> getAllCustomers() {
        log.info("Request received: GET /api/customers - retrieving all customers");
        return customerService.getAllCustomers()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve all customers"))
            .doOnNext(customer -> log.debug("Retrieved customer: id={}, name={}", customer.id(), customer.name()))
            .doOnComplete(() -> log.info("Successfully completed getAllCustomers request"))
            .doOnError(error -> log.error("Error retrieving all customers: {}", error.getMessage(), error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable UUID id) {
        log.info("Request received: GET /api/customers/{} - retrieving customer by ID", id);
        return customerService.getCustomerById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve customer by ID: {}", id))
            .map(customer -> {
                log.info("Customer found: id={}, name={}", customer.id(), customer.name());
                return ResponseEntity.ok(customer);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Customer not found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error retrieving customer by ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Customer>> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("Request received: POST /api/customers - creating new customer with name={}, email={}", 
                customer.name(), customer.email());
        return customerService.createCustomer(customer)
            .doOnSubscribe(subscription -> log.debug("Starting to create customer: {}", customer.name()))
            .map(createdCustomer -> {
                log.info("Customer created successfully: id={}, name={}, email={}", 
                        createdCustomer.id(), createdCustomer.name(), createdCustomer.email());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
            })
            .doOnError(error -> log.error("Error creating customer with name={}, email={}: {}", 
                    customer.name(), customer.email(), error.getMessage(), error));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable UUID id, 
                                                        @Valid @RequestBody Customer customer) {
        log.info("Request received: PUT /api/customers/{} - updating customer with name={}, email={}", 
                id, customer.name(), customer.email());
        return customerService.updateCustomer(id, customer)
            .doOnSubscribe(subscription -> log.debug("Starting to update customer: id={}", id))
            .map(updatedCustomer -> {
                log.info("Customer updated successfully: id={}, name={}, email={}", 
                        updatedCustomer.id(), updatedCustomer.name(), updatedCustomer.email());
                return ResponseEntity.ok(updatedCustomer);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Customer not found for update with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error updating customer with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable UUID id) {
        log.info("Request received: DELETE /api/customers/{} - deleting customer", id);
        return customerService.getCustomerById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to delete customer: id={}", id))
            .flatMap(customer -> {
                log.debug("Customer found for deletion: id={}, name={}", customer.id(), customer.name());
                return customerService.deleteCustomer(id)
                    .doOnSuccess(unused -> log.info("Customer deleted successfully: id={}", id))
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()));
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Customer not found for deletion with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error deleting customer with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<Customer> searchCustomers(@RequestParam String name) {
        log.info("Request received: GET /api/customers/search?name={} - searching customers", name);
        return customerService.searchCustomers(name)
            .doOnSubscribe(subscription -> log.debug("Starting to search customers with name: {}", name))
            .doOnNext(customer -> log.debug("Found customer in search: id={}, name={}", customer.id(), customer.name()))
            .doOnComplete(() -> log.info("Successfully completed customer search for name: {}", name))
            .doOnError(error -> log.error("Error searching customers with name {}: {}", name, error.getMessage(), error));
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Customer>> getCustomerByEmail(@PathVariable String email) {
        log.info("Request received: GET /api/customers/email/{} - retrieving customer by email", email);
        return customerService.findByEmail(email)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve customer by email: {}", email))
            .map(customer -> {
                log.info("Customer found by email: id={}, name={}, email={}", 
                        customer.id(), customer.name(), customer.email());
                return ResponseEntity.ok(customer);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Customer not found with email: {}", email);
                }
            })
            .doOnError(error -> log.error("Error retrieving customer by email {}: {}", email, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
