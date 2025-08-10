package com.beautysalon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    void constructor_ShouldInitializeWithDefaults() {
        // When
        Customer newCustomer = new Customer();

        // Then
        assertNotNull(newCustomer.getId());
        assertNotNull(newCustomer.getCreatedAt());
        assertNotNull(newCustomer.getUpdatedAt());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Given
        UUID testId = UUID.randomUUID();

        // When
        customer.setId(testId);

        // Then
        assertEquals(testId, customer.getId());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Given
        String testName = "Maria Silva";

        // When
        customer.setName(testName);

        // Then
        assertEquals(testName, customer.getName());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        // Given
        String testEmail = "maria@email.com";

        // When
        customer.setEmail(testEmail);

        // Then
        assertEquals(testEmail, customer.getEmail());
    }

    @Test
    void setAndGetPhone_ShouldWorkCorrectly() {
        // Given
        String testPhone = "(11) 99999-1111";

        // When
        customer.setPhone(testPhone);

        // Then
        assertEquals(testPhone, customer.getPhone());
    }

    @Test
    void setAndGetAddress_ShouldWorkCorrectly() {
        // Given
        String testAddress = "Rua das Flores, 123";

        // When
        customer.setAddress(testAddress);

        // Then
        assertEquals(testAddress, customer.getAddress());
    }

    @Test
    void setAndGetCreatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        customer.setCreatedAt(testTime);

        // Then
        assertEquals(testTime, customer.getCreatedAt());
    }

    @Test
    void setAndGetUpdatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        customer.setUpdatedAt(testTime);

        // Then
        assertEquals(testTime, customer.getUpdatedAt());
    }

    @Test
    void customerFields_ShouldBeNullByDefault() {
        // Given
        Customer newCustomer = new Customer();

        // Then
        assertNull(newCustomer.getName());
        assertNull(newCustomer.getEmail());
        assertNull(newCustomer.getPhone());
        assertNull(newCustomer.getAddress());
    }

    @Test
    void customerWithAllFields_ShouldRetainAllValues() {
        // Given
        UUID testId = UUID.randomUUID();
        String testName = "João Santos";
        String testEmail = "joao@email.com";
        String testPhone = "(11) 88888-2222";
        String testAddress = "Rua Nova, 456";
        Instant testTime = Instant.now();

        // When
        customer.setId(testId);
        customer.setName(testName);
        customer.setEmail(testEmail);
        customer.setPhone(testPhone);
        customer.setAddress(testAddress);
        customer.setCreatedAt(testTime);
        customer.setUpdatedAt(testTime);

        // Then
        assertEquals(testId, customer.getId());
        assertEquals(testName, customer.getName());
        assertEquals(testEmail, customer.getEmail());
        assertEquals(testPhone, customer.getPhone());
        assertEquals(testAddress, customer.getAddress());
        assertEquals(testTime, customer.getCreatedAt());
        assertEquals(testTime, customer.getUpdatedAt());
    }
}
