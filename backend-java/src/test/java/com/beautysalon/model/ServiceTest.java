package com.beautysalon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    @Test
    void constructor_ShouldInitializeWithDefaults() {
        // When
        Service newService = new Service();

        // Then
        assertNotNull(newService.getId());
        assertNotNull(newService.getCreatedAt());
        assertNotNull(newService.getUpdatedAt());
        assertTrue(newService.getIsActive());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Given
        UUID testId = UUID.randomUUID();

        // When
        service.setId(testId);

        // Then
        assertEquals(testId, service.getId());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Given
        String testName = "Corte de Cabelo";

        // When
        service.setName(testName);

        // Then
        assertEquals(testName, service.getName());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Given
        String testDescription = "Corte moderno e estiloso";

        // When
        service.setDescription(testDescription);

        // Then
        assertEquals(testDescription, service.getDescription());
    }

    @Test
    void setAndGetDuration_ShouldWorkCorrectly() {
        // Given
        Integer testDuration = 60;

        // When
        service.setDuration(testDuration);

        // Then
        assertEquals(testDuration, service.getDuration());
    }

    @Test
    void setAndGetPrice_ShouldWorkCorrectly() {
        // Given
        BigDecimal testPrice = BigDecimal.valueOf(50.00);

        // When
        service.setPrice(testPrice);

        // Then
        assertEquals(testPrice, service.getPrice());
    }

    @Test
    void setAndGetCategory_ShouldWorkCorrectly() {
        // Given
        String testCategory = "cabelo";

        // When
        service.setCategory(testCategory);

        // Then
        assertEquals(testCategory, service.getCategory());
    }

    @Test
    void setAndGetIsActive_ShouldWorkCorrectly() {
        // Given
        Boolean testActive = false;

        // When
        service.setIsActive(testActive);

        // Then
        assertEquals(testActive, service.getIsActive());
    }

    @Test
    void setAndGetCreatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        service.setCreatedAt(testTime);

        // Then
        assertEquals(testTime, service.getCreatedAt());
    }

    @Test
    void setAndGetUpdatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        service.setUpdatedAt(testTime);

        // Then
        assertEquals(testTime, service.getUpdatedAt());
    }

    @Test
    void serviceFields_ShouldBeNullByDefault() {
        // Given
        Service newService = new Service();

        // Then
        assertNull(newService.getName());
        assertNull(newService.getDescription());
        assertNull(newService.getDuration());
        assertNull(newService.getPrice());
        assertNull(newService.getCategory());
    }

    @Test
    void serviceWithAllFields_ShouldRetainAllValues() {
        // Given
        UUID testId = UUID.randomUUID();
        String testName = "Manicure";
        String testDescription = "Cuidado das unhas";
        Integer testDuration = 45;
        BigDecimal testPrice = BigDecimal.valueOf(30.00);
        String testCategory = "unhas";
        Boolean testActive = true;
        Instant testTime = Instant.now();

        // When
        service.setId(testId);
        service.setName(testName);
        service.setDescription(testDescription);
        service.setDuration(testDuration);
        service.setPrice(testPrice);
        service.setCategory(testCategory);
        service.setIsActive(testActive);
        service.setCreatedAt(testTime);
        service.setUpdatedAt(testTime);

        // Then
        assertEquals(testId, service.getId());
        assertEquals(testName, service.getName());
        assertEquals(testDescription, service.getDescription());
        assertEquals(testDuration, service.getDuration());
        assertEquals(testPrice, service.getPrice());
        assertEquals(testCategory, service.getCategory());
        assertEquals(testActive, service.getIsActive());
        assertEquals(testTime, service.getCreatedAt());
        assertEquals(testTime, service.getUpdatedAt());
    }

    @Test
    void isActive_ShouldDefaultToTrue() {
        // Given
        Service newService = new Service();

        // Then
        assertTrue(newService.getIsActive());
    }
}
