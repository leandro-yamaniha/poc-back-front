package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
@Validated
public class StaffController {

    private static final Logger log = LoggerFactory.getLogger(StaffController.class);
    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
        log.info("StaffController initialized successfully");
    }

    @GetMapping
    public Flux<Staff> getAllStaff() {
        log.info("Request received: GET /api/staff - retrieving all staff");
        return staffService.getAllStaff()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve all staff"))
            .doOnNext(staff -> log.debug("Retrieved staff: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.info("Successfully completed getAllStaff request"))
            .doOnError(error -> log.error("Error retrieving all staff: {}", error.getMessage(), error));
    }

    @GetMapping("/active")
    public Flux<Staff> getActiveStaff() {
        log.info("Request received: GET /api/staff/active - retrieving active staff");
        return staffService.getActiveStaff()
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve active staff"))
            .doOnNext(staff -> log.debug("Retrieved active staff: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.info("Successfully completed getActiveStaff request"))
            .doOnError(error -> log.error("Error retrieving active staff: {}", error.getMessage(), error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Staff>> getStaffById(@PathVariable UUID id) {
        log.info("Request received: GET /api/staff/{} - retrieving staff by ID", id);
        return staffService.getStaffById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve staff by ID: {}", id))
            .map(staff -> {
                log.info("Staff found: id={}, name={}, role={}", staff.id(), staff.name(), staff.role());
                return ResponseEntity.ok(staff);
            })
            .doOnError(error -> log.error("Error retrieving staff by ID {}: {}", id, error.getMessage(), error))
            .switchIfEmpty(Mono.fromSupplier(() -> {
                log.warn("Staff not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }));
    }

    @PostMapping
    public Mono<ResponseEntity<Staff>> createStaff(@Valid @RequestBody Staff staff) {
        log.info("Request received: POST /api/staff - creating new staff with name={}, role={}, email={}", 
                staff.name(), staff.role(), staff.email());
        return staffService.createStaff(staff)
            .doOnSubscribe(subscription -> log.debug("Starting to create staff: {}", staff.name()))
            .map(createdStaff -> {
                log.info("Staff created successfully: id={}, name={}, role={}, email={}", 
                        createdStaff.id(), createdStaff.name(), createdStaff.role(), createdStaff.email());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
            })
            .doOnError(error -> log.error("Error creating staff with name={}, role={}, email={}: {}", 
                    staff.name(), staff.role(), staff.email(), error.getMessage(), error));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Staff>> updateStaff(@PathVariable UUID id, 
                                                  @Valid @RequestBody Staff staff) {
        log.info("Request received: PUT /api/staff/{} - updating staff with name={}, role={}, email={}", 
                id, staff.name(), staff.role(), staff.email());
        return staffService.updateStaff(id, staff)
            .doOnSubscribe(subscription -> log.debug("Starting to update staff: id={}", id))
            .map(updatedStaff -> {
                log.info("Staff updated successfully: id={}, name={}, role={}, email={}", 
                        updatedStaff.id(), updatedStaff.name(), updatedStaff.role(), updatedStaff.email());
                return ResponseEntity.ok(updatedStaff);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Staff not found for update with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error updating staff with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStaff(@PathVariable UUID id) {
        log.info("Request received: DELETE /api/staff/{} - deleting staff", id);
        return staffService.getStaffById(id)
            .doOnSubscribe(subscription -> log.debug("Starting to delete staff: id={}", id))
            .flatMap(staff -> {
                log.debug("Staff found for deletion: id={}, name={}, role={}", staff.id(), staff.name(), staff.role());
                return staffService.deleteStaff(id)
                    .doOnSuccess(unused -> log.info("Staff deleted successfully: id={}", id))
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()));
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Staff not found for deletion with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Error deleting staff with ID {}: {}", id, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public Flux<Staff> getStaffByRole(@PathVariable String role) {
        log.info("Request received: GET /api/staff/role/{} - retrieving staff by role", role);
        return staffService.getStaffByRole(role)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve staff by role: {}", role))
            .doOnNext(staff -> log.debug("Found staff with role: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.info("Successfully completed getStaffByRole request for: {}", role))
            .doOnError(error -> log.error("Error retrieving staff by role {}: {}", role, error.getMessage(), error));
    }

    @GetMapping("/role/{role}/active")
    public Flux<Staff> getActiveStaffByRole(@PathVariable String role) {
        log.info("Request received: GET /api/staff/role/{}/active - retrieving active staff by role", role);
        return staffService.getActiveStaffByRole(role)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve active staff by role: {}", role))
            .doOnNext(staff -> log.debug("Found active staff with role: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.info("Successfully completed getActiveStaffByRole request for: {}", role))
            .doOnError(error -> log.error("Error retrieving active staff by role {}: {}", role, error.getMessage(), error));
    }

    @GetMapping("/search")
    public Flux<Staff> searchStaff(@RequestParam String name) {
        log.info("Request received: GET /api/staff/search?name={} - searching staff", name);
        return staffService.searchStaff(name)
            .doOnSubscribe(subscription -> log.debug("Starting to search staff with name: {}", name))
            .doOnNext(staff -> log.debug("Found staff in search: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.info("Successfully completed staff search for name: {}", name))
            .doOnError(error -> log.error("Error searching staff with name {}: {}", name, error.getMessage(), error));
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Staff>> getStaffByEmail(@PathVariable String email) {
        log.info("Request received: GET /api/staff/email/{} - retrieving staff by email", email);
        return staffService.findByEmail(email)
            .doOnSubscribe(subscription -> log.debug("Starting to retrieve staff by email: {}", email))
            .map(staff -> {
                log.info("Staff found by email: id={}, name={}, role={}, email={}", 
                        staff.id(), staff.name(), staff.role(), staff.email());
                return ResponseEntity.ok(staff);
            })
            .doOnSuccess(response -> {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Staff not found with email: {}", email);
                }
            })
            .doOnError(error -> log.error("Error retrieving staff by email {}: {}", email, error.getMessage(), error))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
