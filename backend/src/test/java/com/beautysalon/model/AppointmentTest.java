package com.beautysalon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
    }

    @Test
    void constructor_ShouldInitializeWithDefaults() {
        // When
        Appointment newAppointment = new Appointment();

        // Then
        assertNotNull(newAppointment.getId());
        assertNotNull(newAppointment.getCreatedAt());
        assertNotNull(newAppointment.getUpdatedAt());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Given
        UUID testId = UUID.randomUUID();

        // When
        appointment.setId(testId);

        // Then
        assertEquals(testId, appointment.getId());
    }

    @Test
    void setAndGetCustomerId_ShouldWorkCorrectly() {
        // Given
        UUID testCustomerId = UUID.randomUUID();

        // When
        appointment.setCustomerId(testCustomerId);

        // Then
        assertEquals(testCustomerId, appointment.getCustomerId());
    }

    @Test
    void setAndGetStaffId_ShouldWorkCorrectly() {
        // Given
        UUID testStaffId = UUID.randomUUID();

        // When
        appointment.setStaffId(testStaffId);

        // Then
        assertEquals(testStaffId, appointment.getStaffId());
    }

    @Test
    void setAndGetServiceId_ShouldWorkCorrectly() {
        // Given
        UUID testServiceId = UUID.randomUUID();

        // When
        appointment.setServiceId(testServiceId);

        // Then
        assertEquals(testServiceId, appointment.getServiceId());
    }

    @Test
    void setAndGetAppointmentDate_ShouldWorkCorrectly() {
        // Given
        LocalDate testDate = LocalDate.of(2025, 8, 15);

        // When
        appointment.setAppointmentDate(testDate);

        // Then
        assertEquals(testDate, appointment.getAppointmentDate());
    }

    @Test
    void setAndGetAppointmentTime_ShouldWorkCorrectly() {
        // Given
        LocalTime testTime = LocalTime.of(14, 30);

        // When
        appointment.setAppointmentTime(testTime);

        // Then
        assertEquals(testTime, appointment.getAppointmentTime());
    }

    @Test
    void setAndGetStatus_ShouldWorkCorrectly() {
        // Given
        String testStatus = "confirmed";

        // When
        appointment.setStatus(testStatus);

        // Then
        assertEquals(testStatus, appointment.getStatus());
    }

    @Test
    void setAndGetNotes_ShouldWorkCorrectly() {
        // Given
        String testNotes = "Cliente preferiu horário da tarde";

        // When
        appointment.setNotes(testNotes);

        // Then
        assertEquals(testNotes, appointment.getNotes());
    }

    @Test
    void setAndGetTotalPrice_ShouldWorkCorrectly() {
        // Given
        BigDecimal testPrice = BigDecimal.valueOf(75.50);

        // When
        appointment.setTotalPrice(testPrice);

        // Then
        assertEquals(testPrice, appointment.getTotalPrice());
    }

    @Test
    void setAndGetCreatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        appointment.setCreatedAt(testTime);

        // Then
        assertEquals(testTime, appointment.getCreatedAt());
    }

    @Test
    void setAndGetUpdatedAt_ShouldWorkCorrectly() {
        // Given
        Instant testTime = Instant.now();

        // When
        appointment.setUpdatedAt(testTime);

        // Then
        assertEquals(testTime, appointment.getUpdatedAt());
    }

    @Test
    void appointmentFields_ShouldBeNullByDefault() {
        // Given
        Appointment newAppointment = new Appointment();

        // Then
        assertNull(newAppointment.getCustomerId());
        assertNull(newAppointment.getStaffId());
        assertNull(newAppointment.getServiceId());
        assertNull(newAppointment.getAppointmentDate());
        assertNull(newAppointment.getAppointmentTime());
        assertEquals("scheduled", newAppointment.getStatus()); // Status has default value
        assertNull(newAppointment.getNotes());
        assertNull(newAppointment.getTotalPrice());
    }

    @Test
    void appointmentWithAllFields_ShouldRetainAllValues() {
        // Given
        UUID testId = UUID.randomUUID();
        UUID testCustomerId = UUID.randomUUID();
        UUID testStaffId = UUID.randomUUID();
        UUID testServiceId = UUID.randomUUID();
        LocalDate testDate = LocalDate.of(2025, 8, 20);
        LocalTime testTime = LocalTime.of(16, 0);
        String testStatus = "scheduled";
        String testNotes = "Primeira consulta";
        BigDecimal testPrice = BigDecimal.valueOf(100.00);
        Instant testInstant = Instant.now();

        // When
        appointment.setId(testId);
        appointment.setCustomerId(testCustomerId);
        appointment.setStaffId(testStaffId);
        appointment.setServiceId(testServiceId);
        appointment.setAppointmentDate(testDate);
        appointment.setAppointmentTime(testTime);
        appointment.setStatus(testStatus);
        appointment.setNotes(testNotes);
        appointment.setTotalPrice(testPrice);
        appointment.setCreatedAt(testInstant);
        appointment.setUpdatedAt(testInstant);

        // Then
        assertEquals(testId, appointment.getId());
        assertEquals(testCustomerId, appointment.getCustomerId());
        assertEquals(testStaffId, appointment.getStaffId());
        assertEquals(testServiceId, appointment.getServiceId());
        assertEquals(testDate, appointment.getAppointmentDate());
        assertEquals(testTime, appointment.getAppointmentTime());
        assertEquals(testStatus, appointment.getStatus());
        assertEquals(testNotes, appointment.getNotes());
        assertEquals(testPrice, appointment.getTotalPrice());
        assertEquals(testInstant, appointment.getCreatedAt());
        assertEquals(testInstant, appointment.getUpdatedAt());
    }

    @Test
    void appointmentTime_CanHandleDifferentTimeFormats() {
        // Given
        LocalTime morningTime = LocalTime.of(9, 0);
        LocalTime afternoonTime = LocalTime.of(15, 30);
        LocalTime eveningTime = LocalTime.of(20, 45);

        // When & Then
        appointment.setAppointmentTime(morningTime);
        assertEquals(morningTime, appointment.getAppointmentTime());

        appointment.setAppointmentTime(afternoonTime);
        assertEquals(afternoonTime, appointment.getAppointmentTime());

        appointment.setAppointmentTime(eveningTime);
        assertEquals(eveningTime, appointment.getAppointmentTime());
    }

    @Test
    void totalPrice_CanHandleDecimalValues() {
        // Given
        BigDecimal priceWithCents = BigDecimal.valueOf(45.75);
        BigDecimal roundPrice = BigDecimal.valueOf(50.00);

        // When & Then
        appointment.setTotalPrice(priceWithCents);
        assertEquals(priceWithCents, appointment.getTotalPrice());

        appointment.setTotalPrice(roundPrice);
        assertEquals(roundPrice, appointment.getTotalPrice());
    }
}
