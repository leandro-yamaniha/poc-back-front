package com.beautysalon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff();
    }

    @Test
    void constructor_ShouldInitializeWithDefaults() {
        // When
        Staff newStaff = new Staff();

        // Then
        assertNotNull(newStaff.getId());
        assertNotNull(newStaff.getCreatedAt());
        assertNotNull(newStaff.getUpdatedAt());
        assertTrue(newStaff.getIsActive());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Given
        UUID testId = UUID.randomUUID();

        // When
        staff.setId(testId);

        // Then
        assertEquals(testId, staff.getId());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Given
        String testName = "Ana Silva";

        // When
        staff.setName(testName);

        // Then
        assertEquals(testName, staff.getName());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        // Given
        String testEmail = "ana@salao.com";

        // When
        staff.setEmail(testEmail);

        // Then
        assertEquals(testEmail, staff.getEmail());
    }

    @Test
    void setAndGetPhone_ShouldWorkCorrectly() {
        // Given
        String testPhone = "(11) 99999-3333";

        // When
        staff.setPhone(testPhone);

        // Then
        assertEquals(testPhone, staff.getPhone());
    }

    @Test
    void setAndGetRole_ShouldWorkCorrectly() {
        // Given
        String testRole = "hairdresser";

        // When
        staff.setRole(testRole);

        // Then
        assertEquals(testRole, staff.getRole());
    }

    @Test
    void setAndGetSpecialties_ShouldWorkCorrectly() {
        // Given
        Set<String> testSpecialties = Set.of("corte", "coloração");

        // When
        staff.setSpecialties(testSpecialties);

        // Then
        assertEquals(testSpecialties, staff.getSpecialties());
    }

    @Test
    void setAndGetIsActive_ShouldWorkCorrectly() {
        // Given
        Boolean testActive = false;

        // When
        staff.setIsActive(testActive);

        // Then
        assertEquals(testActive, staff.getIsActive());
    }

    @Test
    void setAndGetCreatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        staff.setCreatedAt(testTime);

        // Then
        assertEquals(testTime, staff.getCreatedAt());
    }

    @Test
    void setAndGetUpdatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        staff.setUpdatedAt(testTime);

        // Then
        assertEquals(testTime, staff.getUpdatedAt());
    }

    @Test
    void staffFields_ShouldBeNullByDefault() {
        // Given
        Staff newStaff = new Staff();

        // Then
        assertNull(newStaff.getName());
        assertNull(newStaff.getEmail());
        assertNull(newStaff.getPhone());
        assertNull(newStaff.getRole());
        assertNull(newStaff.getSpecialties());
    }

    @Test
    void staffWithAllFields_ShouldRetainAllValues() {
        // Given
        UUID testId = UUID.randomUUID();
        String testName = "Carlos Santos";
        String testEmail = "carlos@salao.com";
        String testPhone = "(11) 88888-4444";
        String testRole = "manager";
        Set<String> testSpecialties = Set.of("gestão", "atendimento");
        Boolean testActive = true;
        Instant testTime = Instant.now();

        // When
        staff.setId(testId);
        staff.setName(testName);
        staff.setEmail(testEmail);
        staff.setPhone(testPhone);
        staff.setRole(testRole);
        staff.setSpecialties(testSpecialties);
        staff.setIsActive(testActive);
        staff.setCreatedAt(testTime);
        staff.setUpdatedAt(testTime);

        // Then
        assertEquals(testId, staff.getId());
        assertEquals(testName, staff.getName());
        assertEquals(testEmail, staff.getEmail());
        assertEquals(testPhone, staff.getPhone());
        assertEquals(testRole, staff.getRole());
        assertEquals(testSpecialties, staff.getSpecialties());
        assertEquals(testActive, staff.getIsActive());
        assertEquals(testTime, staff.getCreatedAt());
        assertEquals(testTime, staff.getUpdatedAt());
    }

    @Test
    void isActive_ShouldDefaultToTrue() {
        // Given
        Staff newStaff = new Staff();

        // Then
        assertTrue(newStaff.getIsActive());
    }

    @Test
    void specialties_CanBeEmptySet() {
        // Given
        Set<String> emptySpecialties = Set.of();

        // When
        staff.setSpecialties(emptySpecialties);

        // Then
        assertEquals(emptySpecialties, staff.getSpecialties());
        assertTrue(staff.getSpecialties().isEmpty());
    }
}
