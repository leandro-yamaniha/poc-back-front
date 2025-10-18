package com.beautysalon.reactive.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void create_ShouldCreateCustomerWithDefaultValues() {
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        
        assertNotNull(customer.id());
        assertEquals("John Doe", customer.name());
        assertEquals("john@example.com", customer.email());
        assertEquals("+1234567890", customer.phone());
        assertEquals("123 Main St", customer.address());
        assertNotNull(customer.createdAt());
        assertNotNull(customer.updatedAt());
    }

    @Test
    void create_WithNullValues_ShouldHandleNulls() {
        Customer customer = Customer.create("John Doe", null, null, null);
        
        assertNotNull(customer.id());
        assertEquals("John Doe", customer.name());
        assertNull(customer.email());
        assertNull(customer.phone());
        assertNull(customer.address());
        assertNotNull(customer.createdAt());
        assertNotNull(customer.updatedAt());
    }

    @Test
    void constructor_ShouldGenerateIdAndTimestamps() {
        Customer customer = new Customer(null, "Jane Smith", "jane@example.com", "+9876543210", "456 Oak Ave", null, null);
        
        assertNotNull(customer.id());
        assertEquals("Jane Smith", customer.name());
        assertEquals("jane@example.com", customer.email());
        assertEquals("+9876543210", customer.phone());
        assertEquals("456 Oak Ave", customer.address());
        assertNotNull(customer.createdAt());
        assertNotNull(customer.updatedAt());
    }

    @Test
    void constructor_WithExistingId_ShouldPreserveId() {
        UUID existingId = UUID.randomUUID();
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        
        Customer customer = new Customer(existingId, "Bob Wilson", "bob@example.com", "+5555555555", "789 Pine St", existingCreatedAt, null);
        
        assertEquals(existingId, customer.id());
        assertEquals("Bob Wilson", customer.name());
        assertEquals("bob@example.com", customer.email());
        assertEquals("+5555555555", customer.phone());
        assertEquals("789 Pine St", customer.address());
        assertEquals(existingCreatedAt, customer.createdAt());
        assertNotNull(customer.updatedAt());
    }

    @Test
    void withUpdatedFields_ShouldUpdateSpecifiedFields() {
        Customer original = Customer.create("Original Name", "original@example.com", "+1111111111", "Original Address");
        
        Customer updated = original.withUpdatedFields("Updated Name", "updated@example.com", "+2222222222", "Updated Address");
        
        assertEquals(original.id(), updated.id());
        assertEquals("Updated Name", updated.name());
        assertEquals("updated@example.com", updated.email());
        assertEquals("+2222222222", updated.phone());
        assertEquals("Updated Address", updated.address());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_WithNullValues_ShouldPreserveOriginalValues() {
        Customer original = Customer.create("Original Name", "original@example.com", "+1111111111", "Original Address");
        
        Customer updated = original.withUpdatedFields(null, null, null, null);
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.name(), updated.name());
        assertEquals(original.email(), updated.email());
        assertEquals(original.phone(), updated.phone());
        assertEquals(original.address(), updated.address());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_PartialUpdate_ShouldUpdateOnlySpecifiedFields() {
        Customer original = Customer.create("Original Name", "original@example.com", "+1111111111", "Original Address");
        
        Customer updated = original.withUpdatedFields("New Name", null, "+9999999999", null);
        
        assertEquals(original.id(), updated.id());
        assertEquals("New Name", updated.name());
        assertEquals(original.email(), updated.email());
        assertEquals("+9999999999", updated.phone());
        assertEquals(original.address(), updated.address());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void equals_ShouldCompareAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Customer customer1 = new Customer(id, "John Doe", "john@example.com", "+1234567890", "123 Main St", now, now);
        Customer customer2 = new Customer(id, "John Doe", "john@example.com", "+1234567890", "123 Main St", now, now);
        Customer customer3 = new Customer(UUID.randomUUID(), "Jane Smith", "jane@example.com", "+9876543210", "456 Oak Ave", now, now);
        
        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Customer customer1 = new Customer(id, "John Doe", "john@example.com", "+1234567890", "123 Main St", now, now);
        Customer customer2 = new Customer(id, "John Doe", "john@example.com", "+1234567890", "123 Main St", now, now);
        
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        Customer customer = Customer.create("John Doe", "john@example.com", "+1234567890", "123 Main St");
        String toString = customer.toString();
        
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("+1234567890"));
        assertTrue(toString.contains("123 Main St"));
    }
}
