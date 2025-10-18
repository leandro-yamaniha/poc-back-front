package com.beautysalon.controller;

import com.beautysalon.model.Staff;
import com.beautysalon.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffControllerTest {

    @Mock
    private StaffService staffService;

    @InjectMocks
    private StaffController staffController;

    private Staff testStaff;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        staffId = UUID.randomUUID();
        testStaff = new Staff();
        testStaff.setId(staffId);
        testStaff.setName("Maria Silva");
        testStaff.setEmail("maria@beautysalon.com");
        testStaff.setPhone("11888888888");
        testStaff.setRole("Cabeleireira");
        Set<String> specialties = new HashSet<>();
        specialties.add("Cortes femininos");
        testStaff.setSpecialties(specialties);
        testStaff.setIsActive(true);
    }

    @Test
    void testGetAllStaff() {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        // Act
        ResponseEntity<List<Staff>> response = staffController.getAllStaff();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Maria Silva", response.getBody().get(0).getName());
    }

    @Test
    void testGetActiveStaff() {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getActiveStaff()).thenReturn(staffList);

        // Act
        ResponseEntity<List<Staff>> response = staffController.getActiveStaff();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Maria Silva", response.getBody().get(0).getName());
    }

    @Test
    void testGetStaffById_StaffExists() {
        // Arrange
        when(staffService.getStaffById(staffId)).thenReturn(Optional.of(testStaff));

        // Act
        ResponseEntity<Staff> response = staffController.getStaffById(staffId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getName());
    }

    @Test
    void testGetStaffById_StaffNotExists() {
        // Arrange
        when(staffService.getStaffById(staffId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Staff> response = staffController.getStaffById(staffId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetStaffByRole() {
        // Arrange
        String role = "Cabeleireira";
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getStaffByRole(role)).thenReturn(staffList);

        // Act
        ResponseEntity<List<Staff>> response = staffController.getStaffByRole(role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(role, response.getBody().get(0).getRole());
    }

    @Test
    void testGetActiveStaffByRole() {
        // Arrange
        String role = "Cabeleireira";
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getActiveStaffByRole(role)).thenReturn(staffList);

        // Act
        ResponseEntity<List<Staff>> response = staffController.getActiveStaffByRole(role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(role, response.getBody().get(0).getRole());
    }

    @Test
    void testCreateStaff_Success() {
        // Arrange
        when(staffService.createStaff(any(Staff.class))).thenReturn(testStaff);

        // Act
        ResponseEntity<Staff> response = staffController.createStaff(testStaff);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getName());
    }

    @Test
    void testCreateStaff_Exception() {
        // Arrange
        when(staffService.createStaff(any(Staff.class))).thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<Staff> response = staffController.createStaff(testStaff);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateStaff_Success() {
        // Arrange
        when(staffService.updateStaff(eq(staffId), any(Staff.class))).thenReturn(testStaff);

        // Act
        ResponseEntity<Staff> response = staffController.updateStaff(staffId, testStaff);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getName());
    }

    @Test
    void testUpdateStaff_NotFound() {
        // Arrange
        when(staffService.updateStaff(eq(staffId), any(Staff.class))).thenReturn(null);

        // Act
        ResponseEntity<Staff> response = staffController.updateStaff(staffId, testStaff);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteStaff_Success() {
        // Arrange
        when(staffService.deleteStaff(staffId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = staffController.deleteStaff(staffId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteStaff_NotFound() {
        // Arrange
        when(staffService.deleteStaff(staffId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = staffController.deleteStaff(staffId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
