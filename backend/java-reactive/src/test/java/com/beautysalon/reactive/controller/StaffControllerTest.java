package com.beautysalon.reactive.controller;

import com.beautysalon.reactive.model.Staff;
import com.beautysalon.reactive.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private StaffService staffService;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        StaffController staffController = new StaffController(staffService);
        webTestClient = WebTestClient.bindToController(staffController).build();
        
        testStaff = Staff.create(
            "Jane Smith",
            "jane@beautysalon.com",
            "+1234567890",
            "Stylist",
            List.of("Haircut", "Color")
        );
    }

    @Test
    void getAllStaff_ShouldReturnStaff() {
        when(staffService.getAllStaff()).thenReturn(Flux.just(testStaff));

        webTestClient.get()
            .uri("/api/staff")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Staff.class)
            .hasSize(1);
    }

    @Test
    void getStaffById_WhenExists_ShouldReturnStaff() {
        UUID id = UUID.randomUUID();
        when(staffService.getStaffById(id)).thenReturn(Mono.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Staff.class);
    }

    @Test
    void getStaffById_WhenNotExists_ShouldReturn404() {
        UUID id = UUID.randomUUID();
        when(staffService.getStaffById(id)).thenReturn(Mono.empty());

        webTestClient.get()
            .uri("/api/staff/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createStaff_WithValidData_ShouldReturnCreated() {
        when(staffService.createStaff(any(Staff.class))).thenReturn(Mono.just(testStaff));

        webTestClient.post()
            .uri("/api/staff")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testStaff)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Staff.class);
    }

    @Test
    void createStaff_WithInvalidData_ShouldReturn400() {
        Staff invalidStaff = Staff.create("", "invalid-email", "", "", List.of());

        webTestClient.post()
            .uri("/api/staff")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidStaff)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void updateStaff_WhenExists_ShouldReturnUpdated() {
        UUID id = UUID.randomUUID();
        when(staffService.updateStaff(eq(id), any(Staff.class))).thenReturn(Mono.just(testStaff));

        webTestClient.put()
            .uri("/api/staff/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testStaff)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Staff.class);
    }

    @Test
    void deleteStaff_WhenExists_ShouldReturn204() {
        UUID id = UUID.randomUUID();
        when(staffService.getStaffById(id)).thenReturn(Mono.just(testStaff));
        when(staffService.deleteStaff(id)).thenReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/staff/{id}", id)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void getActiveStaff_ShouldReturnActiveStaff() {
        when(staffService.getActiveStaff()).thenReturn(Flux.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/active")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Staff.class)
            .hasSize(1);
    }

    @Test
    void getStaffByRole_ShouldReturnFilteredStaff() {
        String role = "Stylist";
        when(staffService.getStaffByRole(role)).thenReturn(Flux.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/role/{role}", role)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Staff.class)
            .hasSize(1);
    }

    @Test
    void getStaffByEmail_WhenExists_ShouldReturnStaff() {
        String email = "jane@beautysalon.com";
        when(staffService.findByEmail(email)).thenReturn(Mono.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/email/{email}", email)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Staff.class);
    }

    @Test
    void searchStaff_ShouldReturnMatchingStaff() {
        String searchTerm = "Jane";
        when(staffService.searchStaff(searchTerm)).thenReturn(Flux.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/search?name={name}", searchTerm)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Staff.class)
            .hasSize(1);
    }

    @Test
    void getActiveStaffByRole_ShouldReturnActiveStaffInRole() {
        String role = "Stylist";
        when(staffService.getActiveStaffByRole(role)).thenReturn(Flux.just(testStaff));

        webTestClient.get()
            .uri("/api/staff/role/{role}/active", role)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Staff.class)
            .hasSize(1);
    }

    @Test
    void staffFlux_ShouldEmitCorrectSequence() {
        Staff staff1 = Staff.create("John Doe", "john@salon.com", "+1111111111", "Manager", List.of("Management"));
        Staff staff2 = Staff.create("Jane Smith", "jane@salon.com", "+2222222222", "Stylist", List.of("Haircut"));
        
        Flux<Staff> staffFlux = Flux.just(staff1, staff2);
        
        StepVerifier.create(staffFlux)
            .expectNext(staff1)
            .expectNext(staff2)
            .verifyComplete();
    }

    @Test
    void staffMono_ShouldEmitSingleStaff() {
        Mono<Staff> staffMono = Mono.just(testStaff);
        
        StepVerifier.create(staffMono)
            .expectNext(testStaff)
            .verifyComplete();
    }
}
