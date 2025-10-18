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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceFinalMutationTest {

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
    void createStaff_ShouldSetTimestampsExplicitly() {
        // Given
        Staff newStaff = new Staff();
        newStaff.setName("Jane Smith");
        newStaff.setEmail("jane.smith@example.com");
        newStaff.setPhone("098-765-4321");
        newStaff.setSpecialties(new HashSet<>(Arrays.asList("Manicure", "Pedicure")));

        // Configurar o mock para capturar o argumento passado para save
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            return staff;
        });

        // When
        Staff result = staffService.createStaff(newStaff);

        // Then
        assertNotNull(result);
        // Verificar explicitamente se os timestamps foram definidos com valores não nulos
        verify(staffRepository).save(argThat(staff -> 
            staff.getCreatedAt() != null && staff.getUpdatedAt() != null));
    }

    @Test
    void createStaff_ShouldCallSetCreatedAtAndSetUpdatedAt() {
        // Given
        Staff newStaff = new Staff();
        newStaff.setName("Jane Smith");
        newStaff.setEmail("jane.smith@example.com");
        newStaff.setPhone("098-765-4321");
        newStaff.setSpecialties(new HashSet<>(Arrays.asList("Manicure", "Pedicure")));

        // Usar um spy para verificar se os métodos setCreatedAt e setUpdatedAt são chamados
        Staff spyStaff = spy(newStaff);
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            return staff;
        });

        // When
        staffService.createStaff(spyStaff);

        // Then
        // Verificar se os métodos setCreatedAt e setUpdatedAt foram chamados
        verify(spyStaff).setCreatedAt(any(Instant.class));
        verify(spyStaff).setUpdatedAt(any(Instant.class));
    }

    @Test
    void updateStaff_WhenStaffExists_ShouldUpdateAllFieldsIncludingRoleAndIsActive() {
        // Given
        Staff existingStaff = new Staff();
        existingStaff.setId(testId);
        existingStaff.setName("John Doe");
        existingStaff.setEmail("john.doe@example.com");
        existingStaff.setPhone("123-456-7890");
        existingStaff.setRole("STYLIST");
        existingStaff.setSpecialties(new HashSet<>(Arrays.asList("Haircut", "Coloring")));
        existingStaff.setIsActive(true);
        existingStaff.setCreatedAt(Instant.now().minusSeconds(3600));
        existingStaff.setUpdatedAt(Instant.now().minusSeconds(3600));
        
        Staff updatedDetails = new Staff();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated.email@example.com");
        updatedDetails.setPhone("111-222-3333");
        updatedDetails.setSpecialties(new HashSet<>(Arrays.asList("Facial", "Massage")));
        updatedDetails.setRole("Senior Specialist"); // Testando explicitamente o campo role
        updatedDetails.setIsActive(false); // Testando explicitamente o campo isActive

        when(staffRepository.findById(testId)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            return staff;
        });

        // When
        Staff result = staffService.updateStaff(testId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(updatedDetails.getName(), result.getName());
        assertEquals(updatedDetails.getEmail(), result.getEmail());
        assertEquals(updatedDetails.getPhone(), result.getPhone());
        assertEquals(updatedDetails.getSpecialties(), result.getSpecialties());
        assertEquals(updatedDetails.getRole(), result.getRole());
        assertEquals(updatedDetails.getIsActive(), result.getIsActive());
        verify(staffRepository).findById(testId);
        verify(staffRepository).save(argThat(staff -> 
            staff.getUpdatedAt() != null));
    }

    @Test
    void updateStaff_WhenStaffExists_ShouldSetUpdatedAtExplicitly() {
        // Given
        Instant oldUpdatedAt = Instant.now().minusSeconds(3600);
        
        Staff existingStaff = new Staff();
        existingStaff.setId(testId);
        existingStaff.setName("John Doe");
        existingStaff.setEmail("john.doe@example.com");
        existingStaff.setPhone("123-456-7890");
        existingStaff.setRole("STYLIST");
        existingStaff.setSpecialties(new HashSet<>(Arrays.asList("Haircut", "Coloring")));
        existingStaff.setIsActive(true);
        existingStaff.setCreatedAt(Instant.now().minusSeconds(3600));
        existingStaff.setUpdatedAt(oldUpdatedAt);
        
        Staff updatedDetails = new Staff();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated.email@example.com");
        updatedDetails.setPhone("111-222-3333");
        updatedDetails.setSpecialties(new HashSet<>(Arrays.asList("Facial", "Massage")));
        updatedDetails.setRole("Senior Specialist");
        updatedDetails.setIsActive(false);

        when(staffRepository.findById(testId)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            return staff;
        });

        // When
        Staff result = staffService.updateStaff(testId, updatedDetails);

        // Then
        assertNotNull(result);
        verify(staffRepository).findById(testId);
        verify(staffRepository).save(argThat(staff -> 
            staff.getUpdatedAt() != null && staff.getUpdatedAt().isAfter(oldUpdatedAt)));
    }
}
