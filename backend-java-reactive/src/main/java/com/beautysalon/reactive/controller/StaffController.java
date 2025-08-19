package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.service.StaffService;
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

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public Flux<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/active")
    public Flux<Staff> getActiveStaff() {
        return staffService.getActiveStaff();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Staff>> getStaffById(@PathVariable UUID id) {
        return staffService.getStaffById(id)
            .map(staff -> ResponseEntity.ok(staff))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Staff>> createStaff(@Valid @RequestBody Staff staff) {
        return staffService.createStaff(staff)
            .map(createdStaff -> ResponseEntity.status(HttpStatus.CREATED).body(createdStaff));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Staff>> updateStaff(@PathVariable UUID id, 
                                                  @Valid @RequestBody Staff staff) {
        return staffService.updateStaff(id, staff)
            .map(updatedStaff -> ResponseEntity.ok(updatedStaff))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStaff(@PathVariable UUID id) {
        return staffService.getStaffById(id)
            .flatMap(staff -> staffService.deleteStaff(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public Flux<Staff> getStaffByRole(@PathVariable String role) {
        return staffService.getStaffByRole(role);
    }

    @GetMapping("/role/{role}/active")
    public Flux<Staff> getActiveStaffByRole(@PathVariable String role) {
        return staffService.getActiveStaffByRole(role);
    }

    @GetMapping("/search")
    public Flux<Staff> searchStaff(@RequestParam String name) {
        return staffService.searchStaff(name);
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Staff>> getStaffByEmail(@PathVariable String email) {
        return staffService.findByEmail(email)
            .map(staff -> ResponseEntity.ok(staff))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
