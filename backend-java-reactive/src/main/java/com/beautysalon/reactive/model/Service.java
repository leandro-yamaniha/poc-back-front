package com.beautysalon.reactive.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("services")
public record Service(
    @PrimaryKey @Id UUID id,
    @NotBlank(message = "Name is required") String name,
    String description,
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    Integer durationMinutes,
    @NotBlank(message = "Category is required") String category,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Service {
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

    public static Service create(String name, String description, BigDecimal price, 
                               Integer durationMinutes, String category) {
        return new Service(null, name, description, price, durationMinutes, category, null, null, null);
    }

    public Service withUpdatedFields(String name, String description, BigDecimal price, 
                                   Integer durationMinutes, String category, Boolean active) {
        return new Service(
            this.id,
            name != null ? name : this.name,
            description != null ? description : this.description,
            price != null ? price : this.price,
            durationMinutes != null ? durationMinutes : this.durationMinutes,
            category != null ? category : this.category,
            active != null ? active : this.active,
            this.createdAt,
            LocalDateTime.now()
        );
    }
}
