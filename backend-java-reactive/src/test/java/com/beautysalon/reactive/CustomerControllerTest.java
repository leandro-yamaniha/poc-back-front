package com.beautysalon.reactive;

import com.beautysalon.reactive.controller.CustomerController;
import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    @Test
    void getAllCustomers_ShouldReturnCustomers() {
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        when(customerService.getAllCustomers()).thenReturn(Flux.just(customer));

        webTestClient.get()
            .uri("/api/customers")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Customer.class)
            .hasSize(1);
    }

    @Test
    void getCustomerById_WhenExists_ShouldReturnCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        when(customerService.getCustomerById(id)).thenReturn(Mono.just(customer));

        webTestClient.get()
            .uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Customer.class);
    }

    @Test
    void getCustomerById_WhenNotExists_ShouldReturn404() {
        UUID id = UUID.randomUUID();
        when(customerService.getCustomerById(id)).thenReturn(Mono.empty());

        webTestClient.get()
            .uri("/api/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createCustomer_WithValidData_ShouldReturnCreated() {
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(Mono.just(customer));

        webTestClient.post()
            .uri("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customer)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Customer.class);
    }

    @Test
    void createCustomer_WithInvalidData_ShouldReturn400() {
        Customer invalidCustomer = Customer.create("", "invalid-email", "", "");

        webTestClient.post()
            .uri("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidCustomer)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void updateCustomer_WhenExists_ShouldReturnUpdated() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(Mono.just(customer));

        webTestClient.put()
            .uri("/api/customers/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customer)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Customer.class);
    }

    @Test
    void deleteCustomer_WhenExists_ShouldReturn204() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        when(customerService.getCustomerById(id)).thenReturn(Mono.just(customer));
        when(customerService.deleteCustomer(id)).thenReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/customers/{id}", id)
            .exchange()
            .expectStatus().isNoContent();
    }
}
