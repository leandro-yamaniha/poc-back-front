package com.beautysalon.reactive.native_tests;

import com.beautysalon.reactive.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Native Image build
 * These tests validate that the native executable works correctly with Cassandra
 * 
 * Run with: -Dnative.image.test=true
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "native.image.test", matches = "true")
class NativeImageIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void shouldStartApplicationSuccessfully() {
        // Verify application context loads
        assertThat(webTestClient).isNotNull();
    }

    @Test
    void shouldAccessHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void shouldListCustomers() {
        webTestClient.get()
                .uri("/api/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class);
    }

    @Test
    void shouldCreateCustomer() {
        Customer newCustomer = Customer.create(
                "Native Test User",
                "native@test.com",
                "+5511999999999",
                "Native Test Address"
        );

        webTestClient.post()
                .uri("/api/customers")
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.id()).isNotNull();
                    assertThat(customer.name()).isEqualTo("Native Test User");
                    assertThat(customer.email()).isEqualTo("native@test.com");
                });
    }

    @Test
    void shouldGetCustomerById() {
        // First create a customer
        Customer created = webTestClient.post()
                .uri("/api/customers")
                .bodyValue(Customer.create("Test", "test@test.com", "+5511999999999", "Address"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();

        // Then retrieve it
        webTestClient.get()
                .uri("/api/customers/{id}", created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.id()).isEqualTo(created.id());
                    assertThat(customer.name()).isEqualTo("Test");
                });
    }

    @Test
    void shouldUpdateCustomer() {
        // Create a customer
        Customer created = webTestClient.post()
                .uri("/api/customers")
                .bodyValue(Customer.create("Original", "original@test.com", "+5511999999999", "Address"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();

        // Update it
        Customer updated = created.withUpdatedFields("Updated", null, null, null);

        webTestClient.put()
                .uri("/api/customers/{id}", created.id())
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.name()).isEqualTo("Updated");
                    assertThat(customer.email()).isEqualTo("original@test.com");
                });
    }

    @Test
    void shouldDeleteCustomer() {
        // Create a customer
        Customer created = webTestClient.post()
                .uri("/api/customers")
                .bodyValue(Customer.create("ToDelete", "delete@test.com", "+5511999999999", "Address"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();

        // Delete it
        webTestClient.delete()
                .uri("/api/customers/{id}", created.id())
                .exchange()
                .expectStatus().isNoContent();

        // Verify it's deleted
        webTestClient.get()
                .uri("/api/customers/{id}", created.id())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldSearchCustomersByName() {
        // Create a customer with unique name
        String uniqueName = "SearchTest" + System.currentTimeMillis();
        webTestClient.post()
                .uri("/api/customers")
                .bodyValue(Customer.create(uniqueName, "search@test.com", "+5511999999999", "Address"))
                .exchange()
                .expectStatus().isCreated();

        // Search for it
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/customers/search")
                        .queryParam("name", uniqueName)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .value(customers -> {
                    assertThat(customers).isNotEmpty();
                    assertThat(customers).anyMatch(c -> c.name().equals(uniqueName));
                });
    }
}
