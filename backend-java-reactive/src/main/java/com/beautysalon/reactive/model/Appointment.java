package com.beautysalon.reactive.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("appointments")
public record Appointment(
    @PrimaryKey @Id UUID id,
    @NotNull(message = "Customer ID is required") UUID customerId,
    @NotNull(message = "Service ID is required") UUID serviceId,
    @NotNull(message = "Staff ID is required") UUID staffId,
    @NotNull(message = "Appointment date is required") LocalDateTime appointmentDate,
    String status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Appointment {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = "SCHEDULED";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public static Appointment create(UUID customerId, UUID serviceId, UUID staffId, 
                                   LocalDateTime appointmentDate, String notes) {
        return new Appointment(null, customerId, serviceId, staffId, appointmentDate, null, notes, null, null);
    }

    public Appointment withUpdatedFields(LocalDateTime appointmentDate, String status, String notes) {
        return new Appointment(
            this.id,
            this.customerId,
            this.serviceId,
            this.staffId,
            appointmentDate != null ? appointmentDate : this.appointmentDate,
            status != null ? status : this.status,
            notes != null ? notes : this.notes,
            this.createdAt,
            LocalDateTime.now()
        );
    }

    public enum Status {
        SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    }
}
