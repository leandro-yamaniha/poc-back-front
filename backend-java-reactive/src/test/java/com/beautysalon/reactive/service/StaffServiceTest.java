package com.beautysalon.reactive.service;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    private StaffService staffService;
    private Staff testStaff;

    @BeforeEach
    void setUp() {
        staffService = new StaffService(staffRepository);
        testStaff = Staff.create(
            "Jane Smith",
            "jane@beautysalon.com",
            "+1234567890",
            "Stylist",
            List.of("Haircut", "Color")
        );
    }

    @Test
    void getAllStaff_ShouldReturnAllStaff() {
        when(staffRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(testStaff));

        Flux<Staff> result = staffService.getAllStaff();

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
        
        verify(staffRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getStaffById_WhenExists_ShouldReturnStaff() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findById(id)).thenReturn(Mono.just(testStaff));

        Mono<Staff> result = staffService.getStaffById(id);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void getStaffById_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Staff> result = staffService.getStaffById(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void createStaff_ShouldReturnCreatedStaff() {
        when(staffRepository.save(any(Staff.class))).thenReturn(Mono.just(testStaff));

        Mono<Staff> result = staffService.createStaff(testStaff);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void updateStaff_WhenExists_ShouldReturnUpdatedStaff() {
        UUID id = UUID.randomUUID();
        Staff updatedStaff = Staff.create(
            "Jane Doe",
            "jane.doe@beautysalon.com",
            "+1234567890",
            "Senior Stylist",
            List.of("Haircut", "Color", "Treatment")
        );
        
        when(staffRepository.findById(id)).thenReturn(Mono.just(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(Mono.just(updatedStaff));

        Mono<Staff> result = staffService.updateStaff(id, updatedStaff);

        StepVerifier.create(result)
            .expectNext(updatedStaff)
            .verifyComplete();
    }

    @Test
    void updateStaff_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findById(id)).thenReturn(Mono.empty());

        Mono<Staff> result = staffService.updateStaff(id, testStaff);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteStaff_WhenExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(staffRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = staffService.deleteStaff(id);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void deleteStaff_WhenNotExists_ShouldComplete() {
        UUID id = UUID.randomUUID();
        when(staffRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = staffService.deleteStaff(id);

        StepVerifier.create(result)
            .verifyComplete();
        
        verify(staffRepository).deleteById(id);
    }

    @Test
    void getActiveStaff_ShouldReturnActiveStaff() {
        when(staffRepository.findByActiveTrue()).thenReturn(Flux.just(testStaff));

        Flux<Staff> result = staffService.getActiveStaff();

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void getStaffByRole_ShouldReturnFilteredStaff() {
        String role = "Stylist";
        when(staffRepository.findByRole(role)).thenReturn(Flux.just(testStaff));

        Flux<Staff> result = staffService.getStaffByRole(role);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void getActiveStaffByRole_ShouldReturnActiveStaffInRole() {
        String role = "Stylist";
        when(staffRepository.findByRoleAndActiveTrue(role)).thenReturn(Flux.just(testStaff));

        Flux<Staff> result = staffService.getActiveStaffByRole(role);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void getStaffByEmail_WhenExists_ShouldReturnStaff() {
        String email = "jane@beautysalon.com";
        when(staffRepository.findByEmail(email)).thenReturn(Mono.just(testStaff));

        Mono<Staff> result = staffService.findByEmail(email);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void getStaffByEmail_WhenNotExists_ShouldReturnEmpty() {
        String email = "nonexistent@example.com";
        when(staffRepository.findByEmail(email)).thenReturn(Mono.empty());

        Mono<Staff> result = staffService.findByEmail(email);

        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void searchStaff_ShouldReturnMatchingStaff() {
        String searchTerm = "Jane";
        when(staffRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(Flux.just(testStaff));

        Flux<Staff> result = staffService.searchStaff(searchTerm);

        StepVerifier.create(result)
            .expectNext(testStaff)
            .verifyComplete();
    }

    @Test
    void staffFlux_ShouldHandleMultipleStaff() {
        Staff staff1 = Staff.create("John Doe", "john@example.com", "123-456-7890", "STYLIST", List.of("Haircut", "Coloring"));
        Staff staff2 = Staff.create("Jane Smith", "jane@example.com", "098-765-4321", "THERAPIST", List.of("Massage", "Facial"));
        
        when(staffRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.just(staff1, staff2));
        
        StepVerifier.create(staffService.getAllStaff())
            .expectNext(staff1)
            .expectNext(staff2)
            .verifyComplete();
    }

    @Test
    void staffFlux_ShouldHandleEmptyResult() {
        when(staffRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.empty());
        
        StepVerifier.create(staffService.getAllStaff())
            .verifyComplete();
    }

    @Test
    void staffFlux_ShouldHandleError() {
        when(staffRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Flux.error(new RuntimeException("Database error")));
        
        StepVerifier.create(staffService.getAllStaff())
            .expectError(RuntimeException.class)
            .verify();
    }
}
