package com.beautysalon.service;

import com.beautysalon.model.Staff;
import com.beautysalon.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceSimpleTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        staffId = UUID.randomUUID();
        testStaff = new Staff();
        testStaff.setId(staffId);
        testStaff.setName("Ana Silva");
        testStaff.setEmail("ana.silva@beautysalon.com");
        testStaff.setPhone("(11) 99999-9999");
        testStaff.setRole("Cabeleireira");
        testStaff.setIsActive(true);
    }

    @Test
    void testGetAllStaff() {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepository.findAll()).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAllStaff();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStaff.getName(), result.get(0).getName());
        verify(staffRepository).findAll();
    }

    @Test
    void testGetActiveStaff() {
        // Arrange
        List<Staff> activeStaff = Arrays.asList(testStaff);
        when(staffRepository.findActiveStaff()).thenReturn(activeStaff);

        // Act
        List<Staff> result = staffService.getActiveStaff();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStaff.getName(), result.get(0).getName());
        verify(staffRepository).findActiveStaff();
    }

    @Test
    void testGetStaffById_StaffExists() {
        // Arrange
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(testStaff));

        // Act
        Optional<Staff> result = staffService.getStaffById(staffId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testStaff.getName(), result.get().getName());
        verify(staffRepository).findById(staffId);
    }

    @Test
    void testGetStaffById_StaffNotExists() {
        // Arrange
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Act
        Optional<Staff> result = staffService.getStaffById(staffId);

        // Assert
        assertFalse(result.isPresent());
        verify(staffRepository).findById(staffId);
    }

    @Test
    void testGetStaffByRole() {
        // Arrange
        String role = "Cabeleireira";
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepository.findByRole(role)).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getStaffByRole(role);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
        verify(staffRepository).findByRole(role);
    }

    @Test
    void testGetActiveStaffByRole() {
        // Arrange
        String role = "Cabeleireira";
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepository.findActiveByRole(role)).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getActiveStaffByRole(role);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
        verify(staffRepository).findActiveByRole(role);
    }

    @Test
    void testCreateStaff() {
        // Arrange
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        // Act
        Staff result = staffService.createStaff(testStaff);

        // Assert
        assertNotNull(result);
        assertEquals(testStaff.getName(), result.getName());
        assertEquals(testStaff.getEmail(), result.getEmail());
        verify(staffRepository).save(testStaff);
    }

    @Test
    void testUpdateStaff_StaffExists() {
        // Arrange
        Staff updatedStaff = new Staff();
        updatedStaff.setName("Ana Santos");
        updatedStaff.setRole("Manicure");
        
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        // Act
        Staff result = staffService.updateStaff(staffId, updatedStaff);

        // Assert
        assertNotNull(result);
        verify(staffRepository).findById(staffId);
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void testUpdateStaff_StaffNotExists() {
        // Arrange
        Staff updatedStaff = new Staff();
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Act
        Staff result = staffService.updateStaff(staffId, updatedStaff);

        // Assert
        assertNull(result);
        verify(staffRepository).findById(staffId);
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void testDeleteStaff_StaffExists() {
        // Arrange
        when(staffRepository.existsById(staffId)).thenReturn(true);
        doNothing().when(staffRepository).deleteById(staffId);

        // Act
        boolean result = staffService.deleteStaff(staffId);

        // Assert
        assertTrue(result);
        verify(staffRepository).existsById(staffId);
        verify(staffRepository).deleteById(staffId);
    }

    @Test
    void testDeleteStaff_StaffNotExists() {
        // Arrange
        when(staffRepository.existsById(staffId)).thenReturn(false);

        // Act
        boolean result = staffService.deleteStaff(staffId);

        // Assert
        assertFalse(result);
        verify(staffRepository).existsById(staffId);
        verify(staffRepository, never()).deleteById(staffId);
    }
}
