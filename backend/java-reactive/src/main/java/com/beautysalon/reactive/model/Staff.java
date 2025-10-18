package com.beautysalon.reactive.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table("staff")
public record Staff(
    @PrimaryKey @Id UUID id,
    @NotBlank(message = "Name is required") String name,
    @Email(message = "Invalid email format") String email,
    @Pattern(regexp = "\\+?[1-9]\\d{1,14}", message = "Invalid phone number") String phone,
    @NotBlank(message = "Role is required") String role,
    List<String> specialties,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Staff {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (active == null) {
            active = true;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public static Staff create(String name, String email, String phone, String role, List<String> specialties) {
        return new Staff(null, name, email, phone, role, specialties, null, null, null);
    }

    public Staff withUpdatedFields(String name, String email, String phone, String role, 
                                 List<String> specialties, Boolean active) {
        return new Staff(
            this.id,
            name != null ? name : this.name,
            email != null ? email : this.email,
            phone != null ? phone : this.phone,
            role != null ? role : this.role,
            specialties != null ? specialties : this.specialties,
            active != null ? active : this.active,
            this.createdAt,
            LocalDateTime.now()
        );
    }
}
