package com.beautysalon.reactive.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void create_ShouldCreateAppointmentWithDefaultValues() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = Appointment.create(customerId, serviceId, staffId, appointmentDate, "Regular appointment");
        
        assertNotNull(appointment.id());
        assertEquals(customerId, appointment.customerId());
        assertEquals(serviceId, appointment.serviceId());
        assertEquals(staffId, appointment.staffId());
        assertEquals(appointmentDate, appointment.appointmentDate());
        assertEquals("SCHEDULED", appointment.status());
        assertEquals("Regular appointment", appointment.notes());
        assertNotNull(appointment.createdAt());
        assertNotNull(appointment.updatedAt());
    }

    @Test
    void create_WithNullNotes_ShouldHandleNull() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = Appointment.create(customerId, serviceId, staffId, appointmentDate, null);
        
        assertNotNull(appointment.id());
        assertEquals(customerId, appointment.customerId());
        assertEquals(serviceId, appointment.serviceId());
        assertEquals(staffId, appointment.staffId());
        assertEquals(appointmentDate, appointment.appointmentDate());
        assertEquals("SCHEDULED", appointment.status());
        assertNull(appointment.notes());
    }

    @Test
    void constructor_ShouldGenerateIdAndTimestamps() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = new Appointment(null, customerId, serviceId, staffId, appointmentDate, null, "Test notes", null, null);
        
        assertNotNull(appointment.id());
        assertEquals(customerId, appointment.customerId());
        assertEquals(serviceId, appointment.serviceId());
        assertEquals(staffId, appointment.staffId());
        assertEquals(appointmentDate, appointment.appointmentDate());
        assertEquals("SCHEDULED", appointment.status());
        assertEquals("Test notes", appointment.notes());
        assertNotNull(appointment.createdAt());
        assertNotNull(appointment.updatedAt());
    }

    @Test
    void constructor_WithExistingId_ShouldPreserveId() {
        UUID existingId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        
        Appointment appointment = new Appointment(existingId, customerId, serviceId, staffId, appointmentDate, "CONFIRMED", "Existing appointment", existingCreatedAt, null);
        
        assertEquals(existingId, appointment.id());
        assertEquals(customerId, appointment.customerId());
        assertEquals(serviceId, appointment.serviceId());
        assertEquals(staffId, appointment.staffId());
        assertEquals(appointmentDate, appointment.appointmentDate());
        assertEquals("CONFIRMED", appointment.status());
        assertEquals("Existing appointment", appointment.notes());
        assertEquals(existingCreatedAt, appointment.createdAt());
        assertNotNull(appointment.updatedAt());
    }

    @Test
    void constructor_WithNullStatus_ShouldDefaultToScheduled() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = new Appointment(null, customerId, serviceId, staffId, appointmentDate, null, "Notes", null, null);
        
        assertEquals("SCHEDULED", appointment.status());
    }

    @Test
    void withUpdatedFields_ShouldUpdateSpecifiedFields() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime originalDate = LocalDateTime.now().plusDays(1);
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        
        Appointment original = Appointment.create(customerId, serviceId, staffId, originalDate, "Original notes");
        
        Appointment updated = original.withUpdatedFields(newDate, "CONFIRMED", "Updated notes");
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.customerId(), updated.customerId());
        assertEquals(original.serviceId(), updated.serviceId());
        assertEquals(original.staffId(), updated.staffId());
        assertEquals(newDate, updated.appointmentDate());
        assertEquals("CONFIRMED", updated.status());
        assertEquals("Updated notes", updated.notes());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_WithNullValues_ShouldPreserveOriginalValues() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment original = Appointment.create(customerId, serviceId, staffId, appointmentDate, "Original notes");
        
        Appointment updated = original.withUpdatedFields(null, null, null);
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.customerId(), updated.customerId());
        assertEquals(original.serviceId(), updated.serviceId());
        assertEquals(original.staffId(), updated.staffId());
        assertEquals(original.appointmentDate(), updated.appointmentDate());
        assertEquals(original.status(), updated.status());
        assertEquals(original.notes(), updated.notes());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void withUpdatedFields_PartialUpdate_ShouldUpdateOnlySpecifiedFields() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment original = Appointment.create(customerId, serviceId, staffId, appointmentDate, "Original notes");
        
        Appointment updated = original.withUpdatedFields(null, "CONFIRMED", null);
        
        assertEquals(original.id(), updated.id());
        assertEquals(original.appointmentDate(), updated.appointmentDate());
        assertEquals("CONFIRMED", updated.status());
        assertEquals(original.notes(), updated.notes());
        assertEquals(original.createdAt(), updated.createdAt());
        assertNotEquals(original.updatedAt(), updated.updatedAt());
    }

    @Test
    void equals_ShouldCompareAllFields() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        LocalDateTime now = LocalDateTime.now();
        
        Appointment appointment1 = new Appointment(id, customerId, serviceId, staffId, appointmentDate, "SCHEDULED", "Notes", now, now);
        Appointment appointment2 = new Appointment(id, customerId, serviceId, staffId, appointmentDate, "SCHEDULED", "Notes", now, now);
        Appointment appointment3 = new Appointment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), appointmentDate.plusDays(1), "CONFIRMED", "Different notes", now, now);
        
        assertEquals(appointment1, appointment2);
        assertNotEquals(appointment1, appointment3);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        LocalDateTime now = LocalDateTime.now();
        
        Appointment appointment1 = new Appointment(id, customerId, serviceId, staffId, appointmentDate, "SCHEDULED", "Notes", now, now);
        Appointment appointment2 = new Appointment(id, customerId, serviceId, staffId, appointmentDate, "SCHEDULED", "Notes", now, now);
        
        assertEquals(appointment1.hashCode(), appointment2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment appointment = Appointment.create(customerId, serviceId, staffId, appointmentDate, "Test notes");
        String toString = appointment.toString();
        
        assertTrue(toString.contains(customerId.toString()));
        assertTrue(toString.contains(serviceId.toString()));
        assertTrue(toString.contains(staffId.toString()));
        assertTrue(toString.contains("SCHEDULED"));
        assertTrue(toString.contains("Test notes"));
    }

    @Test
    void status_ShouldSupportDifferentStatuses() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        
        Appointment scheduled = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "SCHEDULED", "Notes", null, null);
        Appointment confirmed = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "CONFIRMED", "Notes", null, null);
        Appointment inProgress = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "IN_PROGRESS", "Notes", null, null);
        Appointment completed = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "COMPLETED", "Notes", null, null);
        Appointment cancelled = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "CANCELLED", "Notes", null, null);
        Appointment noShow = new Appointment(null, customerId, serviceId, staffId, appointmentDate, "NO_SHOW", "Notes", null, null);
        
        assertEquals("SCHEDULED", scheduled.status());
        assertEquals("CONFIRMED", confirmed.status());
        assertEquals("IN_PROGRESS", inProgress.status());
        assertEquals("COMPLETED", completed.status());
        assertEquals("CANCELLED", cancelled.status());
        assertEquals("NO_SHOW", noShow.status());
    }

    @Test
    void statusEnum_ShouldContainAllExpectedValues() {
        Appointment.Status[] statuses = Appointment.Status.values();
        
        assertEquals(6, statuses.length);
        assertEquals(Appointment.Status.SCHEDULED, statuses[0]);
        assertEquals(Appointment.Status.CONFIRMED, statuses[1]);
        assertEquals(Appointment.Status.IN_PROGRESS, statuses[2]);
        assertEquals(Appointment.Status.COMPLETED, statuses[3]);
        assertEquals(Appointment.Status.CANCELLED, statuses[4]);
        assertEquals(Appointment.Status.NO_SHOW, statuses[5]);
    }

    @Test
    void appointmentDate_ShouldSupportFutureDates() {
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        LocalDateTime futureDate = LocalDateTime.now().plusWeeks(2);
        
        Appointment appointment = Appointment.create(customerId, serviceId, staffId, futureDate, "Future appointment");
        
        assertEquals(futureDate, appointment.appointmentDate());
        assertTrue(appointment.appointmentDate().isAfter(LocalDateTime.now()));
    }
}
