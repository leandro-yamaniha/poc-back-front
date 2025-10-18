package com.beautysalon.reactive.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    @Test
    void create_ShouldCreateStaffWithDefaultValues() {
        List<String> specialties = List.of("Haircut", "Color");
        Staff staff = Staff.create("Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties);
        
        assertNotNull(staff.id());
        assertEquals("Jane Smith", staff.name());
        assertEquals("jane@salon.com", staff.email());
        assertEquals("+1234567890", staff.phone());
        assertEquals("Stylist", staff.role());
        assertEquals(specialties, staff.specialties());
        assertTrue(staff.active());
        assertNotNull(staff.createdAt());
        assertNotNull(staff.updatedAt());
    }

    @Test
    void create_WithNullValues_ShouldHandleNulls() {
        Staff staff = Staff.create("John Doe", null, null, "Manager", null);
        
        assertNotNull(staff.id());
        assertEquals("John Doe", staff.name());
        assertNull(staff.email());
        assertNull(staff.phone());
        assertEquals("Manager", staff.role());
        assertNull(staff.specialties());
        assertTrue(staff.active());
    }

    @Test
    void create_WithEmptySpecialties_ShouldHandleEmptyList() {
        List<String> emptySpecialties = List.of();
        Staff staff = Staff.create("Bob Wilson", "bob@salon.com", "+5555555555", "Receptionist", emptySpecialties);
        
        assertEquals(emptySpecialties, staff.specialties());
        assertTrue(staff.specialties().isEmpty());
    }

    @Test
    void constructor_ShouldGenerateIdAndTimestamps() {
        List<String> specialties = List.of("Massage", "Aromatherapy");
        Staff staff = new Staff(null, "Alice Johnson", "alice@salon.com", "+9876543210", "Therapist", specialties, null, null, null);
        
        assertNotNull(staff.id());
        assertEquals("Alice Johnson", staff.name());
        assertEquals("alice@salon.com", staff.email());
        assertEquals("+9876543210", staff.phone());
        assertEquals("Therapist", staff.role());
        assertEquals(specialties, staff.specialties());
        assertTrue(staff.active());
        assertNotNull(staff.createdAt());
        assertNotNull(staff.updatedAt());
    }

    @Test
    void constructor_WithExistingId_ShouldPreserveId() {
        UUID existingId = UUID.randomUUID();
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        List<String> specialties = List.of("Manicure", "Pedicure");
        
        Staff staff = new Staff(existingId, "Carol Davis", "carol@salon.com", "+1111111111", "Nail Technician", specialties, false, existingCreatedAt, null);
        
        assertEquals(existingId, staff.id());
        assertEquals("Carol Davis", staff.name());
        assertEquals("carol@salon.com", staff.email());
        assertEquals("+1111111111", staff.phone());
        assertEquals("Nail Technician", staff.role());
        assertEquals(specialties, staff.specialties());
        assertFalse(staff.active());
        assertEquals(existingCreatedAt, staff.createdAt());
        assertNotNull(staff.updatedAt());
    }

    @Test
    void constructor_WithNullActive_ShouldDefaultToTrue() {
        Staff staff = new Staff(null, "David Brown", "david@salon.com", "+2222222222", "Barber", List.of("Beard Trim"), null, null, null);
        
        assertTrue(staff.active());
    }

    @Test
    void withUpdatedFields_ShouldUpdateSpecifiedFields() {
        List<String> originalSpecialties = List.of("Haircut");
        List<String> updatedSpecialties = List.of("Haircut", "Color", "Styling");
        Staff original = Staff.create("Original Name", "original@salon.com", "+1111111111", "Junior Stylist", originalSpecialties);
        
        Staff updated = original.withUpdatedFields("Updated Name", "updated@salon.com", "+2222222222", "Senior Stylist", updatedSpecialties, false);
        
        assertEquals(original.id(), updated.id());
        assertEquals("Updated Name", updated.name());
        assertEquals("updated@salon.com", updated.email());
        assertEquals("+2222222222", updated.phone());
        assertEquals("Senior Stylist", updated.role());
        assertEquals(updatedSpecialties, updated.specialties());
        assertFalse(updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_WithNullValues_ShouldPreserveOriginalValues() {
        List<String> specialties = List.of("Facial", "Skincare");
        Staff original = Staff.create("Original Name", "original@salon.com", "+1111111111", "Esthetician", specialties);
        
        Staff updated = original.withUpdatedFields(null, null, null, null, null, null);
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.name(), updated.name());
        assertEquals(original.email(), updated.email());
        assertEquals(original.phone(), updated.phone());
        assertEquals(original.role(), updated.role());
        assertEquals(original.specialties(), updated.specialties());
        assertEquals(original.active(), updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_PartialUpdate_ShouldUpdateOnlySpecifiedFields() {
        List<String> originalSpecialties = List.of("Massage");
        List<String> newSpecialties = List.of("Massage", "Hot Stone");
        Staff original = Staff.create("Original Name", "original@salon.com", "+1111111111", "Therapist", originalSpecialties);
        
        Staff updated = original.withUpdatedFields("New Name", null, "+9999999999", null, newSpecialties, null);
        
        assertEquals(original.id(), updated.id());
        assertEquals("New Name", updated.name());
        assertEquals(original.email(), updated.email());
        assertEquals("+9999999999", updated.phone());
        assertEquals(original.role(), updated.role());
        assertEquals(newSpecialties, updated.specialties());
        assertEquals(original.active(), updated.active());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void equals_ShouldCompareAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        List<String> specialties = List.of("Haircut", "Color");
        
        Staff staff1 = new Staff(id, "Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties, true, now, now);
        Staff staff2 = new Staff(id, "Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties, true, now, now);
        Staff staff3 = new Staff(UUID.randomUUID(), "John Doe", "john@salon.com", "+9876543210", "Manager", List.of("Management"), true, now, now);
        
        assertEquals(staff1, staff2);
        assertNotEquals(staff1, staff3);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        List<String> specialties = List.of("Haircut", "Color");
        
        Staff staff1 = new Staff(id, "Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties, true, now, now);
        Staff staff2 = new Staff(id, "Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties, true, now, now);
        
        assertEquals(staff1.hashCode(), staff2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        List<String> specialties = List.of("Haircut", "Color");
        Staff staff = Staff.create("Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties);
        String toString = staff.toString();
        
        assertTrue(toString.contains("Jane Smith"));
        assertTrue(toString.contains("jane@salon.com"));
        assertTrue(toString.contains("+1234567890"));
        assertTrue(toString.contains("Stylist"));
    }

    @Test
    void specialties_ShouldSupportMultipleValues() {
        List<String> multipleSpecialties = List.of("Haircut", "Color", "Highlights", "Balayage", "Keratin Treatment");
        Staff staff = Staff.create("Expert Stylist", "expert@salon.com", "+1234567890", "Senior Stylist", multipleSpecialties);
        
        assertEquals(5, staff.specialties().size());
        assertTrue(staff.specialties().contains("Haircut"));
        assertTrue(staff.specialties().contains("Color"));
        assertTrue(staff.specialties().contains("Highlights"));
        assertTrue(staff.specialties().contains("Balayage"));
        assertTrue(staff.specialties().contains("Keratin Treatment"));
    }

    @Test
    void specialties_ShouldBeImmutable() {
        List<String> specialties = List.of("Haircut", "Color");
        Staff staff = Staff.create("Jane Smith", "jane@salon.com", "+1234567890", "Stylist", specialties);
        
        // The returned list should be the same reference or immutable
        List<String> returnedSpecialties = staff.specialties();
        assertEquals(specialties, returnedSpecialties);
    }

    @Test
    void role_ShouldSupportDifferentRoles() {
        Staff stylist = Staff.create("Stylist Name", "stylist@salon.com", "+1111111111", "Stylist", List.of("Haircut"));
        Staff manager = Staff.create("Manager Name", "manager@salon.com", "+2222222222", "Manager", List.of("Management"));
        Staff receptionist = Staff.create("Receptionist Name", "receptionist@salon.com", "+3333333333", "Receptionist", List.of("Customer Service"));
        
        assertEquals("Stylist", stylist.role());
        assertEquals("Manager", manager.role());
        assertEquals("Receptionist", receptionist.role());
    }
}
