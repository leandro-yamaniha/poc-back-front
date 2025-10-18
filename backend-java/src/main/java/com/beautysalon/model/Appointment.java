package com.beautysalon.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("appointments")
public class Appointment {
    
    @PrimaryKey
    private UUID id;
    
    @Column("customer_id")
    private UUID customerId;
    
    @Column("staff_id")
    private UUID staffId;
    
    @Column("service_id")
    private UUID serviceId;
    
    @Column("appointment_date")
    private LocalDate appointmentDate;
    
    @Column("appointment_time")
    private LocalTime appointmentTime;
    
    private String status; // scheduled, confirmed, completed, cancelled
    private String notes;
    
    @Column("total_price")
    private BigDecimal totalPrice;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
    
    public Appointment() {
        this.id = UUID.randomUUID();
        this.status = "scheduled";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    public UUID getStaffId() {
        return staffId;
    }
    
    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }
    
    public UUID getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
