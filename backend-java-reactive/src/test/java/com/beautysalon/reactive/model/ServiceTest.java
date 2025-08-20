package com.beautysalon.reactive.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void create_ShouldCreateServiceWithDefaultValues() {
        Service service = Service.create("Haircut", "Professional haircut", new BigDecimal("50.00"), 60, "Hair");
        
        assertNotNull(service.id());
        assertEquals("Haircut", service.name());
        assertEquals("Professional haircut", service.description());
        assertEquals(new BigDecimal("50.00"), service.price());
        assertEquals(60, service.durationMinutes());
        assertEquals("Hair", service.category());
        assertTrue(service.active());
        assertNotNull(service.createdAt());
        assertNotNull(service.updatedAt());
    }

    @Test
    void create_WithNullDescription_ShouldHandleNull() {
        Service service = Service.create("Basic Cut", null, new BigDecimal("30.00"), 45, "Hair");
        
        assertNotNull(service.id());
        assertEquals("Basic Cut", service.name());
        assertNull(service.description());
        assertEquals(new BigDecimal("30.00"), service.price());
        assertEquals(45, service.durationMinutes());
        assertEquals("Hair", service.category());
        assertTrue(service.active());
    }

    @Test
    void constructor_ShouldGenerateIdAndTimestamps() {
        Service service = new Service(null, "Massage", "Relaxing massage", new BigDecimal("80.00"), 90, "Wellness", null, null, null);
        
        assertNotNull(service.id());
        assertEquals("Massage", service.name());
        assertEquals("Relaxing massage", service.description());
        assertEquals(new BigDecimal("80.00"), service.price());
        assertEquals(90, service.durationMinutes());
        assertEquals("Wellness", service.category());
        assertTrue(service.active());
        assertNotNull(service.createdAt());
        assertNotNull(service.updatedAt());
    }

    @Test
    void constructor_WithExistingId_ShouldPreserveId() {
        UUID existingId = UUID.randomUUID();
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        
        Service service = new Service(existingId, "Facial", "Deep cleansing facial", new BigDecimal("75.00"), 75, "Skincare", false, existingCreatedAt, null);
        
        assertEquals(existingId, service.id());
        assertEquals("Facial", service.name());
        assertEquals("Deep cleansing facial", service.description());
        assertEquals(new BigDecimal("75.00"), service.price());
        assertEquals(75, service.durationMinutes());
        assertEquals("Skincare", service.category());
        assertFalse(service.active());
        assertEquals(existingCreatedAt, service.createdAt());
        assertNotNull(service.updatedAt());
    }

    @Test
    void constructor_WithNullActive_ShouldDefaultToTrue() {
        Service service = new Service(null, "Manicure", "Professional manicure", new BigDecimal("35.00"), 45, "Nails", null, null, null);
        
        assertTrue(service.active());
    }

    @Test
    void withUpdatedFields_ShouldUpdateSpecifiedFields() {
        Service original = Service.create("Original Service", "Original description", new BigDecimal("50.00"), 60, "Original");
        
        Service updated = original.withUpdatedFields("Updated Service", "Updated description", new BigDecimal("75.00"), 90, "Updated", false);
        
        assertEquals(original.id(), updated.id());
        assertEquals("Updated Service", updated.name());
        assertEquals("Updated description", updated.description());
        assertEquals(new BigDecimal("75.00"), updated.price());
        assertEquals(90, updated.durationMinutes());
        assertEquals("Updated", updated.category());
        assertFalse(updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_WithNullValues_ShouldPreserveOriginalValues() {
        Service original = Service.create("Original Service", "Original description", new BigDecimal("50.00"), 60, "Original");
        
        Service updated = original.withUpdatedFields(null, null, null, null, null, null);
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.name(), updated.name());
        assertEquals(original.description(), updated.description());
        assertEquals(original.price(), updated.price());
        assertEquals(original.durationMinutes(), updated.durationMinutes());
        assertEquals(original.category(), updated.category());
        assertEquals(original.active(), updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_PartialUpdate_ShouldUpdateOnlySpecifiedFields() {
        Service original = Service.create("Original Service", "Original description", new BigDecimal("50.00"), 60, "Original");
        
        Service updated = original.withUpdatedFields("New Name", null, new BigDecimal("100.00"), null, null, false);
        
        assertEquals(original.id(), updated.id());
        assertEquals("New Name", updated.name());
        assertEquals(original.description(), updated.description());
        assertEquals(new BigDecimal("100.00"), updated.price());
        assertEquals(original.durationMinutes(), updated.durationMinutes());
        assertEquals(original.category(), updated.category());
        assertFalse(updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void equals_ShouldCompareAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Service service1 = new Service(id, "Haircut", "Professional cut", new BigDecimal("50.00"), 60, "Hair", true, now, now);
        Service service2 = new Service(id, "Haircut", "Professional cut", new BigDecimal("50.00"), 60, "Hair", true, now, now);
        Service service3 = new Service(UUID.randomUUID(), "Massage", "Relaxing massage", new BigDecimal("80.00"), 90, "Wellness", true, now, now);
        
        assertEquals(service1, service2);
        assertNotEquals(service1, service3);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Service service1 = new Service(id, "Haircut", "Professional cut", new BigDecimal("50.00"), 60, "Hair", true, now, now);
        Service service2 = new Service(id, "Haircut", "Professional cut", new BigDecimal("50.00"), 60, "Hair", true, now, now);
        
        assertEquals(service1.hashCode(), service2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        Service service = Service.create("Haircut", "Professional haircut", new BigDecimal("50.00"), 60, "Hair");
        String toString = service.toString();
        
        assertTrue(toString.contains("Haircut"));
        assertTrue(toString.contains("Professional haircut"));
        assertTrue(toString.contains("50.00"));
        assertTrue(toString.contains("60"));
        assertTrue(toString.contains("Hair"));
    }

    @Test
    void bigDecimalComparison_ShouldWorkCorrectly() {
        Service service1 = Service.create("Service1", "Description", new BigDecimal("50.00"), 60, "Category");
        Service service2 = Service.create("Service2", "Description", new BigDecimal("50.0"), 60, "Category");
        
        assertEquals(0, service1.price().compareTo(service2.price()));
    }

    @Test
    void durationMinutes_ShouldBePositive() {
        Service service = Service.create("Quick Service", "Fast service", new BigDecimal("25.00"), 15, "Quick");
        
        assertTrue(service.durationMinutes() > 0);
    }

    @Test
    void price_ShouldBeNonNegative() {
        Service freeService = Service.create("Free Consultation", "Free consultation", BigDecimal.ZERO, 30, "Consultation");
        Service paidService = Service.create("Premium Service", "Premium service", new BigDecimal("100.00"), 120, "Premium");
        
        assertTrue(freeService.price().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(paidService.price().compareTo(BigDecimal.ZERO) > 0);
    }
}
