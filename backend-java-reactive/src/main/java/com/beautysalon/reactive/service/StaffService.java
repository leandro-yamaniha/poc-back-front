package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public Flux<Staff> getAllStaff() {
        return staffRepository.findAllByOrderByCreatedAtDesc();
    }

    public Flux<Staff> getActiveStaff() {
        return staffRepository.findByActiveTrue();
    }

    public Mono<Staff> getStaffById(UUID id) {
        return staffRepository.findById(id);
    }

    public Mono<Staff> createStaff(Staff staff) {
        return staffRepository.save(Staff.create(
            staff.name(),
            staff.email(),
            staff.phone(),
            staff.role(),
            staff.specialties()
        ));
    }

    public Mono<Staff> updateStaff(UUID id, Staff staff) {
        return staffRepository.findById(id)
            .flatMap(existingStaff -> {
                Staff updatedStaff = existingStaff.withUpdatedFields(
                    staff.name(),
                    staff.email(),
                    staff.phone(),
                    staff.role(),
                    staff.specialties(),
                    staff.active()
                );
                return staffRepository.save(updatedStaff);
            });
    }

    public Mono<Void> deleteStaff(UUID id) {
        return staffRepository.deleteById(id);
    }

    public Flux<Staff> getStaffByRole(String role) {
        return staffRepository.findByRole(role);
    }

    public Flux<Staff> getActiveStaffByRole(String role) {
        return staffRepository.findByRoleAndActiveTrue(role);
    }

    public Flux<Staff> searchStaff(String name) {
        return staffRepository.findByNameContainingIgnoreCase(name);
    }

    public Mono<Staff> findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }
}
