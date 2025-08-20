package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve all customers from the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customers")
    public Flux<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id)
            .map(customer -> ResponseEntity.ok(customer))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Customer>> createCustomer(@Valid @RequestBody Customer customer) {
        return customerService.createCustomer(customer)
            .map(createdCustomer -> ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable UUID id, 
                                                        @Valid @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer)
            .map(updatedCustomer -> ResponseEntity.ok(updatedCustomer))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable UUID id) {
        return customerService.getCustomerById(id)
            .flatMap(customer -> customerService.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<Customer> searchCustomers(@RequestParam String name) {
        return customerService.searchCustomers(name);
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Customer>> getCustomerByEmail(@PathVariable String email) {
        return customerService.findByEmail(email)
            .map(customer -> ResponseEntity.ok(customer))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
