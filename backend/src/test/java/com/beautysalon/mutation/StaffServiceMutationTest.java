package com.beautysalon.mutation;

import com.beautysalon.model.Staff;
import com.beautysalon.repository.StaffRepository;
import com.beautysalon.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceMutationTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        testStaff = new Staff();
        testStaff.setId(testId);
        testStaff.setName("John Doe");
        testStaff.setEmail("john.doe@example.com");
        testStaff.setPhone("123-456-7890");
        testStaff.setSpecialties(new HashSet<>(Arrays.asList("Haircut", "Coloring")));
        testStaff.setCreatedAt(Instant.now());
        testStaff.setUpdatedAt(Instant.now());
    }

    @Test
    void createStaff_ShouldSetTimestamps() {
        // Given
        Staff newStaff = new Staff();
        newStaff.setName("Jane Smith");
        newStaff.setEmail("jane.smith@example.com");
        newStaff.setPhone("098-765-4321");
        newStaff.setSpecialties(new HashSet<>(Arrays.asList("Manicure", "Pedicure")));

        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        // When
        Staff result = staffService.createStaff(newStaff);

        // Then
        assertNotNull(result);
        // Verificar se os timestamps foram definidos
        assertNotNull(newStaff.getCreatedAt());
        assertNotNull(newStaff.getUpdatedAt());
        verify(staffRepository).save(newStaff);
    }

    @Test
    void updateStaff_WhenStaffExists_ShouldUpdateAllFields() {
        // Given
        Staff updatedDetails = new Staff();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated.email@example.com");
        updatedDetails.setPhone("111-222-3333");
        updatedDetails.setSpecialties(new HashSet<>(Arrays.asList("Facial", "Massage")));

        when(staffRepository.findById(testId)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

        // When
        Staff result = staffService.updateStaff(testId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(updatedDetails.getName(), testStaff.getName());
        assertEquals(updatedDetails.getEmail(), testStaff.getEmail());
        assertEquals(updatedDetails.getPhone(), testStaff.getPhone());
        assertEquals(updatedDetails.getSpecialties(), testStaff.getSpecialties());
        assertNotNull(testStaff.getUpdatedAt());
        verify(staffRepository).findById(testId);
        verify(staffRepository).save(testStaff);
    }

    @Test
    void updateStaff_WhenStaffDoesNotExist_ShouldReturnNull() {
        // Given
        Staff updatedDetails = new Staff();
        updatedDetails.setName("Updated Name");

        when(staffRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        Staff result = staffService.updateStaff(testId, updatedDetails);

        // Then
        assertNull(result);
        verify(staffRepository).findById(testId);
        verify(staffRepository, never()).save(any(Staff.class));
    }
}
