package com.beautysalon.reactive.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("customers")
public record Customer(
    @PrimaryKey @Id UUID id,
    @NotBlank(message = "Name is required") String name,
    @Email(message = "Invalid email format") String email,
    @Pattern(regexp = "\\+?[1-9]\\d{1,14}", message = "Invalid phone number") String phone,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Customer {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public static Customer create(String name, String email, String phone, String address) {
        return new Customer(null, name, email, phone, address, null, null);
    }

    public Customer withUpdatedFields(String name, String email, String phone, String address) {
        return new Customer(
            this.id,
            name != null ? name : this.name,
            email != null ? email : this.email,
            phone != null ? phone : this.phone,
            address != null ? address : this.address,
            this.createdAt,
            LocalDateTime.now()
        );
    }
}
