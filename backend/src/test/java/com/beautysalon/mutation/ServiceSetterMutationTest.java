package com.beautysalon.mutation;

import com.beautysalon.model.Appointment;
import com.beautysalon.model.Service;
import com.beautysalon.model.Staff;
import com.beautysalon.repository.AppointmentRepository;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.repository.StaffRepository;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.service.ServiceService;
import com.beautysalon.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Targeted mutation tests for surviving VoidMethodCallMutator mutations in service classes.
 * These tests specifically target setter calls that are being removed by mutations.
 */
@ExtendWith(MockitoExtension.class)
class ServiceSetterMutationTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private ServiceRepository serviceRepository;
    
    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private AppointmentService appointmentService;
    
    @InjectMocks
    private ServiceService serviceService;
    
    @InjectMocks
    private StaffService staffService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(appointmentRepository, serviceRepository, staffRepository);
    }

    /**
     * Test AppointmentService createAppointment setter calls
     * Targets surviving mutations:
     * - removed call to com/beautysalon/model/Appointment::setCreatedAt
     * - removed call to com/beautysalon/model/Appointment::setUpdatedAt
     */
    @Test
    @DisplayName("Test AppointmentService createAppointment setter calls for VoidMethodCallMutator")
    void testAppointmentService_CreateAppointment_SetterCalls() {
        // Given
        Appointment appointment = new Appointment();
        appointment.setCustomerId(UUID.randomUUID());
        appointment.setStaffId(UUID.randomUUID());
        appointment.setServiceId(UUID.randomUUID());
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setStatus("SCHEDULED");
        
        // Create a spy to track method calls
        Appointment appointmentSpy = spy(appointment);
        
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointmentSpy);
        
        // When
        appointmentService.createAppointment(appointmentSpy);
        
        // Then - Verify setter calls that VoidMethodCallMutator could remove
        verify(appointmentSpy).setCreatedAt(any(Instant.class)); // setCreatedAt mutation
        verify(appointmentSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
        verify(appointmentRepository).save(appointmentSpy);
    }

    /**
     * Test AppointmentService updateAppointment setter calls
     * Targets surviving mutations for all business field setters
     */
    @Test
    @DisplayName("Test AppointmentService updateAppointment setter calls for VoidMethodCallMutator")
    void testAppointmentService_UpdateAppointment_SetterCalls() {
        // Given
        UUID appointmentId = UUID.randomUUID();
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);
        existingAppointment.setStatus("SCHEDULED");
        
        Appointment appointmentSpy = spy(existingAppointment);
        
        Appointment updateDetails = new Appointment();
        updateDetails.setCustomerId(UUID.randomUUID());
        updateDetails.setStaffId(UUID.randomUUID());
        updateDetails.setServiceId(UUID.randomUUID());
        updateDetails.setAppointmentDate(LocalDate.now().plusDays(1));
        updateDetails.setAppointmentTime(LocalTime.of(14, 30));
        updateDetails.setStatus("CONFIRMED");
        updateDetails.setNotes("Updated notes");
        updateDetails.setTotalPrice(new BigDecimal("150.00"));
        
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointmentSpy));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointmentSpy);
        
        // When
        appointmentService.updateAppointment(appointmentId, updateDetails);
        
        // Then - Verify all setter calls that VoidMethodCallMutator could remove
        verify(appointmentSpy).setCustomerId(updateDetails.getCustomerId()); // setCustomerId mutation
        verify(appointmentSpy).setStaffId(updateDetails.getStaffId()); // setStaffId mutation
        verify(appointmentSpy).setServiceId(updateDetails.getServiceId()); // setServiceId mutation
        verify(appointmentSpy).setAppointmentDate(updateDetails.getAppointmentDate()); // setAppointmentDate mutation
        verify(appointmentSpy).setAppointmentTime(updateDetails.getAppointmentTime()); // setAppointmentTime mutation
        verify(appointmentSpy).setStatus(updateDetails.getStatus()); // setStatus mutation
        verify(appointmentSpy).setNotes(updateDetails.getNotes()); // setNotes mutation
        verify(appointmentSpy).setTotalPrice(updateDetails.getTotalPrice()); // setTotalPrice mutation
        verify(appointmentSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
    }

    /**
     * Test ServiceService createService setter calls
     * Targets surviving mutations:
     * - removed call to com/beautysalon/model/Service::setCreatedAt
     * - removed call to com/beautysalon/model/Service::setUpdatedAt
     */
    @Test
    @DisplayName("Test ServiceService createService setter calls for VoidMethodCallMutator")
    void testServiceService_CreateService_SetterCalls() {
        // Given
        Service service = new Service();
        service.setName("Test Service");
        service.setDescription("Test Description");
        service.setDuration(60);
        service.setPrice(new BigDecimal("100.00"));
        service.setCategory("Hair");
        service.setIsActive(true);
        
        // Create a spy to track method calls
        Service serviceSpy = spy(service);
        
        when(serviceRepository.save(any(Service.class))).thenReturn(serviceSpy);
        
        // When
        serviceService.createService(serviceSpy);
        
        // Then - Verify setter calls that VoidMethodCallMutator could remove
        verify(serviceSpy).setCreatedAt(any(Instant.class)); // setCreatedAt mutation
        verify(serviceSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
        verify(serviceRepository).save(serviceSpy);
    }

    /**
     * Test ServiceService updateService setter calls
     * Targets surviving mutations for all business field setters
     */
    @Test
    @DisplayName("Test ServiceService updateService setter calls for VoidMethodCallMutator")
    void testServiceService_UpdateService_SetterCalls() {
        // Given
        UUID serviceId = UUID.randomUUID();
        Service existingService = new Service();
        existingService.setId(serviceId);
        existingService.setName("Old Service");
        
        Service serviceSpy = spy(existingService);
        
        Service updateDetails = new Service();
        updateDetails.setName("Updated Service");
        updateDetails.setDescription("Updated Description");
        updateDetails.setDuration(90);
        updateDetails.setPrice(new BigDecimal("150.00"));
        updateDetails.setCategory("Updated Category");
        updateDetails.setIsActive(true);
        
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceSpy));
        when(serviceRepository.save(any(Service.class))).thenReturn(serviceSpy);
        
        // When
        serviceService.updateService(serviceId, updateDetails);
        
        // Then - Verify all setter calls that VoidMethodCallMutator could remove
        verify(serviceSpy).setName(updateDetails.getName()); // setName mutation
        verify(serviceSpy).setDescription(updateDetails.getDescription()); // setDescription mutation
        verify(serviceSpy).setDuration(updateDetails.getDuration()); // setDuration mutation
        verify(serviceSpy).setPrice(updateDetails.getPrice()); // setPrice mutation
        verify(serviceSpy).setCategory(updateDetails.getCategory()); // setCategory mutation
        verify(serviceSpy).setIsActive(updateDetails.getIsActive()); // setIsActive mutation
        verify(serviceSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
    }

    /**
     * Test StaffService createStaff setter calls
     * Targets surviving mutations:
     * - removed call to com/beautysalon/model/Staff::setCreatedAt
     * - removed call to com/beautysalon/model/Staff::setUpdatedAt
     */
    @Test
    @DisplayName("Test StaffService createStaff setter calls for VoidMethodCallMutator")
    void testStaffService_CreateStaff_SetterCalls() {
        // Given
        Staff staff = new Staff();
        staff.setName("Test Staff");
        staff.setEmail("test@example.com");
        staff.setPhone("+1234567890");
        staff.setRole("Stylist");
        staff.setSpecialties(Set.of("Hair"));
        staff.setIsActive(true);
        
        // Create a spy to track method calls
        Staff staffSpy = spy(staff);
        
        when(staffRepository.save(any(Staff.class))).thenReturn(staffSpy);
        
        // When
        staffService.createStaff(staffSpy);
        
        // Then - Verify setter calls that VoidMethodCallMutator could remove
        verify(staffSpy).setCreatedAt(any(Instant.class)); // setCreatedAt mutation
        verify(staffSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
        verify(staffRepository).save(staffSpy);
    }

    /**
     * Test StaffService updateStaff setter calls
     * Targets surviving mutations for all business field setters
     */
    @Test
    @DisplayName("Test StaffService updateStaff setter calls for VoidMethodCallMutator")
    void testStaffService_UpdateStaff_SetterCalls() {
        // Given
        UUID staffId = UUID.randomUUID();
        Staff existingStaff = new Staff();
        existingStaff.setId(staffId);
        existingStaff.setName("Old Staff");
        
        Staff staffSpy = spy(existingStaff);
        
        Staff updateDetails = new Staff();
        updateDetails.setName("Updated Staff");
        updateDetails.setEmail("updated@example.com");
        updateDetails.setPhone("+1987654321");
        updateDetails.setRole("Senior Stylist");
        updateDetails.setSpecialties(Set.of("Hair", "Makeup"));
        updateDetails.setIsActive(true);
        
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffSpy));
        when(staffRepository.save(any(Staff.class))).thenReturn(staffSpy);
        
        // When
        staffService.updateStaff(staffId, updateDetails);
        
        // Then - Verify all setter calls that VoidMethodCallMutator could remove
        verify(staffSpy).setName(updateDetails.getName()); // setName mutation
        verify(staffSpy).setEmail(updateDetails.getEmail()); // setEmail mutation
        verify(staffSpy).setPhone(updateDetails.getPhone()); // setPhone mutation
        verify(staffSpy).setRole(updateDetails.getRole()); // setRole mutation
        verify(staffSpy).setSpecialties(updateDetails.getSpecialties()); // setSpecialties mutation
        verify(staffSpy).setIsActive(updateDetails.getIsActive()); // setIsActive mutation
        verify(staffSpy).setUpdatedAt(any(Instant.class)); // setUpdatedAt mutation
    }
}
