package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffService.class);
    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
        log.info("StaffService initialized with repository: {}", staffRepository.getClass().getSimpleName());
    }

    public Flux<Staff> getAllStaff() {
        log.debug("Service: Starting to retrieve all staff ordered by creation date");
        return staffRepository.findAll()
            .doOnSubscribe(subscription -> log.debug("Repository call: findAll()"))
            .doOnNext(staff -> log.trace("Service: Retrieved staff from repository: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved all staff from repository"))
            .doOnError(error -> log.error("Service: Error retrieving all staff: {}", error.getMessage(), error));
    }

    public Flux<Staff> getActiveStaff() {
        log.debug("Service: Starting to retrieve active staff");
        return staffRepository.findByActiveTrue()
            .doOnSubscribe(subscription -> log.debug("Repository call: findByActiveTrue()"))
            .doOnNext(staff -> log.trace("Service: Retrieved active staff from repository: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.debug("Service: Successfully retrieved active staff from repository"))
            .doOnError(error -> log.error("Service: Error retrieving active staff: {}", error.getMessage(), error));
    }

    public Mono<Staff> getStaffById(UUID id) {
        log.debug("Service: Starting to retrieve staff by ID: {}", id);
        return staffRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({})", id))
            .doOnNext(staff -> log.debug("Service: Found staff: id={}, name={}, role={}, email={}", 
                    staff.id(), staff.name(), staff.role(), staff.email()))
            .doOnSuccess(staff -> {
                if (staff == null) {
                    log.debug("Service: No staff found with ID: {}", id);
                }
            })
            .doOnError(error -> log.error("Service: Error retrieving staff by ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Staff> createStaff(Staff staff) {
        log.debug("Service: Starting to create staff: name={}, role={}, email={}, phone={}", 
                staff.name(), staff.role(), staff.email(), staff.phone());
        
        Staff newStaff = Staff.create(
            staff.name(),
            staff.email(),
            staff.phone(),
            staff.role(),
            staff.specialties()
        );
        
        log.debug("Service: Created staff object with ID: {}", newStaff.id());
        
        return staffRepository.save(newStaff)
            .doOnSubscribe(subscription -> log.debug("Repository call: save() for staff ID: {}", newStaff.id()))
            .doOnNext(savedStaff -> log.info("Service: Staff saved successfully: id={}, name={}, role={}, email={}", 
                    savedStaff.id(), savedStaff.name(), savedStaff.role(), savedStaff.email()))
            .doOnError(error -> log.error("Service: Error creating staff with name={}, role={}, email={}: {}", 
                    staff.name(), staff.role(), staff.email(), error.getMessage(), error));
    }

    public Mono<Staff> updateStaff(UUID id, Staff staff) {
        log.debug("Service: Starting to update staff: id={}, name={}, role={}, email={}", 
                id, staff.name(), staff.role(), staff.email());
        
        return staffRepository.findById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: findById({}) for update", id))
            .doOnNext(existingStaff -> log.debug("Service: Found existing staff for update: id={}, name={}, role={}", 
                    existingStaff.id(), existingStaff.name(), existingStaff.role()))
            .flatMap(existingStaff -> {
                Staff updatedStaff = existingStaff.withUpdatedFields(
                    staff.name(),
                    staff.email(),
                    staff.phone(),
                    staff.role(),
                    staff.specialties(),
                    staff.active()
                );
                log.debug("Service: Created updated staff object: id={}, name={}, role={}, email={}, active={}", 
                        updatedStaff.id(), updatedStaff.name(), updatedStaff.role(), updatedStaff.email(), updatedStaff.active());
                
                return staffRepository.save(updatedStaff)
                    .doOnSubscribe(sub -> log.debug("Repository call: save() for updated staff ID: {}", updatedStaff.id()))
                    .doOnNext(savedStaff -> log.info("Service: Staff updated successfully: id={}, name={}, role={}, email={}, active={}", 
                            savedStaff.id(), savedStaff.name(), savedStaff.role(), savedStaff.email(), savedStaff.active()));
            })
            .doOnError(error -> log.error("Service: Error updating staff with ID {}: {}", id, error.getMessage(), error));
    }

    public Mono<Void> deleteStaff(UUID id) {
        log.debug("Service: Starting to delete staff with ID: {}", id);
        return staffRepository.deleteById(id)
            .doOnSubscribe(subscription -> log.debug("Repository call: deleteById({})", id))
            .doOnSuccess(unused -> log.info("Service: Staff deleted successfully from repository: id={}", id))
            .doOnError(error -> log.error("Service: Error deleting staff with ID {}: {}", id, error.getMessage(), error));
    }

    public Flux<Staff> getStaffByRole(String role) {
        log.debug("Service: Starting to retrieve staff by role: {}", role);
        return staffRepository.findByRole(role)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByRole({})", role))
            .doOnNext(staff -> log.trace("Service: Found staff with role: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.debug("Service: Completed staff retrieval by role: {}", role))
            .doOnError(error -> log.error("Service: Error retrieving staff by role {}: {}", role, error.getMessage(), error));
    }

    public Flux<Staff> getActiveStaffByRole(String role) {
        log.debug("Service: Starting to retrieve active staff by role: {}", role);
        return staffRepository.findByRoleAndActiveTrue(role)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByRoleAndActiveTrue({})", role))
            .doOnNext(staff -> log.trace("Service: Found active staff with role: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.debug("Service: Completed active staff retrieval by role: {}", role))
            .doOnError(error -> log.error("Service: Error retrieving active staff by role {}: {}", role, error.getMessage(), error));
    }

    public Flux<Staff> searchStaff(String name) {
        log.debug("Service: Starting to search staff with name containing: {}", name);
        return staffRepository.findByNameContainingIgnoreCase(name)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByNameContainingIgnoreCase({})", name))
            .doOnNext(staff -> log.trace("Service: Found staff in search: id={}, name={}, role={}", staff.id(), staff.name(), staff.role()))
            .doOnComplete(() -> log.debug("Service: Completed staff search for name: {}", name))
            .doOnError(error -> log.error("Service: Error searching staff with name {}: {}", name, error.getMessage(), error));
    }

    public Mono<Staff> findByEmail(String email) {
        log.debug("Service: Starting to find staff by email: {}", email);
        return staffRepository.findByEmail(email)
            .doOnSubscribe(subscription -> log.debug("Repository call: findByEmail({})", email))
            .doOnNext(staff -> log.debug("Service: Found staff by email: id={}, name={}, role={}, email={}", 
                    staff.id(), staff.name(), staff.role(), staff.email()))
            .doOnSuccess(staff -> {
                if (staff == null) {
                    log.debug("Service: No staff found with email: {}", email);
                }
            })
            .doOnError(error -> log.error("Service: Error finding staff by email {}: {}", email, error.getMessage(), error));
    }
}
