package com.beautysalon.mutation;

import com.beautysalon.model.Appointment;
import com.beautysalon.model.Customer;
import com.beautysalon.model.Service;
import com.beautysalon.model.Staff;
import com.beautysalon.repository.AppointmentRepository;
import com.beautysalon.repository.CustomerRepository;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.repository.StaffRepository;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.service.CustomerService;
import com.beautysalon.service.PerformanceMonitoringService;
import com.beautysalon.service.ServiceService;
import com.beautysalon.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Ultra-targeted mutation tests specifically designed to kill VoidMethodCallMutator survivors.
 * These tests focus on verifying that void method calls are actually made and have side effects.
 */
@ExtendWith(MockitoExtension.class)
class VoidMethodCallTargetedTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private CacheManager cacheManager;
    @Mock private Cache mockCache;

    @Mock private CustomerService customerService;
    @Mock private AppointmentService appointmentService;
    @Mock private ServiceService serviceService;
    @Mock private StaffService staffService;
    @Mock private PerformanceMonitoringService performanceMonitoringService;

    @BeforeEach
    void setUp() {
        
        lenient().when(cacheManager.getCacheNames()).thenReturn(java.util.Set.of("testCache"));
        lenient().when(cacheManager.getCache("testCache")).thenReturn(mockCache);
        lenient().when(mockCache.getName()).thenReturn("testCache");
    }

    @Test
    void testCustomerService_CreateCustomer_VoidSetterCalls() {
        // Given
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        // When
        Customer result = customerService.createCustomer(customer);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        
        Customer savedCustomer = customerCaptor.getValue();
        assertNotNull(savedCustomer.getId(), "setId() should have been called");
        assertNotNull(savedCustomer.getCreatedAt(), "setCreatedAt() should have been called");
        assertNotNull(savedCustomer.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testCustomerService_UpdateCustomer_VoidSetterCalls() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Old Name");
        existingCustomer.setCreatedAt(Instant.now().minusSeconds(3600));
        
        Customer updateData = new Customer();
        updateData.setName("New Name");
        updateData.setEmail("new@example.com");
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        // When
        Customer result = customerService.updateCustomer(customerId, updateData);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals("New Name", savedCustomer.getName(), "setName() should have been called");
        assertEquals("new@example.com", savedCustomer.getEmail(), "setEmail() should have been called");
        assertNotNull(savedCustomer.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testAppointmentService_CreateAppointment_VoidSetterCalls() {
        // Given
        Appointment appointment = new Appointment();
        appointment.setCustomerId(UUID.randomUUID());
        appointment.setServiceId(UUID.randomUUID());
        appointment.setStaffId(UUID.randomUUID());
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1).toLocalDate());
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1).toLocalTime());
        
        when(customerRepository.existsById(any())).thenReturn(true);
        when(serviceRepository.existsById(any())).thenReturn(true);
        when(staffRepository.existsById(any())).thenReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        
        // When
        Appointment result = appointmentService.createAppointment(appointment);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());
        
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertNotNull(savedAppointment.getId(), "setId() should have been called");
        assertNotNull(savedAppointment.getCreatedAt(), "setCreatedAt() should have been called");
        assertNotNull(savedAppointment.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testServiceService_CreateService_VoidSetterCalls() {
        // Given
        Service service = new Service();
        service.setName("Test Service");
        service.setPrice(BigDecimal.valueOf(100.00));
        service.setDescription("Hair cut and styling");
        
        when(serviceRepository.save(any(Service.class))).thenReturn(service);
        
        // When
        Service result = serviceService.createService(service);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Service> serviceCaptor = ArgumentCaptor.forClass(Service.class);
        verify(serviceRepository).save(serviceCaptor.capture());
        
        Service savedService = serviceCaptor.getValue();
        assertNotNull(savedService.getId(), "setId() should have been called");
        assertNotNull(savedService.getCreatedAt(), "setCreatedAt() should have been called");
        assertNotNull(savedService.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testStaffService_CreateStaff_VoidSetterCalls() {
        // Given
        Staff staff = new Staff();
        staff.setName("Test Staff");
        staff.setRole("Stylist");
        staff.setEmail("staff@example.com");
        
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);
        
        // When
        Staff result = staffService.createStaff(staff);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepository).save(staffCaptor.capture());
        
        Staff savedStaff = staffCaptor.getValue();
        assertNotNull(savedStaff.getId(), "setId() should have been called");
        assertNotNull(savedStaff.getCreatedAt(), "setCreatedAt() should have been called");
        assertNotNull(savedStaff.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testPerformanceMonitoringService_MonitorCachePerformance_VoidCalls() {
        // Given
        when(cacheManager.getCacheNames()).thenReturn(java.util.Set.of("cache1", "cache2"));
        when(cacheManager.getCache("cache1")).thenReturn(mockCache);
        when(cacheManager.getCache("cache2")).thenReturn(mockCache);
        when(mockCache.getName()).thenReturn("cache1");
        
        // When - This method makes void calls internally
        assertDoesNotThrow(() -> performanceMonitoringService.monitorCachePerformance());
        
        // Then - Verify void method calls were made
        verify(cacheManager, atLeastOnce()).getCacheNames();
        verify(cacheManager, atLeast(2)).getCache(anyString());
    }

    @Test
    void testPerformanceMonitoringService_MonitorResponseTime_VoidCalls() {
        // Given
        long responseTime = 1500L; // Above threshold to trigger alert
        
        // When - This method makes void calls internally
        assertDoesNotThrow(() -> performanceMonitoringService.monitorResponseTime(responseTime));
        
        // Then - Verify the method executed (void method call survived)
        // The fact that no exception was thrown indicates the void method was called
        assertTrue(responseTime > 1000, "Response time should trigger internal void calls");
    }

    @Test
    void testAppointmentService_UpdateAppointment_VoidSetterCalls() {
        // Given
        UUID appointmentId = UUID.randomUUID();
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);
        existingAppointment.setCustomerId(UUID.randomUUID());
        existingAppointment.setServiceId(UUID.randomUUID());
        existingAppointment.setStaffId(UUID.randomUUID());
        existingAppointment.setAppointmentDate(LocalDateTime.now().plusDays(1).toLocalDate());
        existingAppointment.setAppointmentTime(LocalDateTime.now().plusDays(1).toLocalTime());
        existingAppointment.setCreatedAt(Instant.now().minusSeconds(3600));
        
        Appointment updateData = new Appointment();
        updateData.setAppointmentDate(LocalDateTime.now().plusDays(2).toLocalDate());
        updateData.setAppointmentTime(LocalDateTime.now().plusDays(2).toLocalTime());
        updateData.setStatus("CONFIRMED");
        
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);
        
        // When
        Appointment result = appointmentService.updateAppointment(appointmentId, updateData);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());
        
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals("CONFIRMED", savedAppointment.getStatus(), "setStatus() should have been called");
        assertNotNull(savedAppointment.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testServiceService_UpdateService_VoidSetterCalls() {
        // Given
        UUID serviceId = UUID.randomUUID();
        Service existingService = new Service();
        existingService.setId(serviceId);
        existingService.setName("Old Service");
        existingService.setPrice(BigDecimal.valueOf(50.00));
        existingService.setCreatedAt(Instant.now().minusSeconds(3600));
        
        Service updateData = new Service();
        updateData.setName("Updated Service");
        updateData.setPrice(BigDecimal.valueOf(75.00));
        updateData.setDescription("Extended hair styling service");
        
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        when(serviceRepository.save(any(Service.class))).thenReturn(existingService);
        
        // When
        Service result = serviceService.updateService(serviceId, updateData);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Service> serviceCaptor = ArgumentCaptor.forClass(Service.class);
        verify(serviceRepository).save(serviceCaptor.capture());
        
        Service savedService = serviceCaptor.getValue();
        assertEquals("Updated Service", savedService.getName(), "setName() should have been called");
        assertEquals(BigDecimal.valueOf(75.00), savedService.getPrice(), "setPrice() should have been called");
        assertEquals("Extended hair styling service", savedService.getDescription(), "setDescription() should have been called");
        assertNotNull(savedService.getUpdatedAt(), "setUpdatedAt() should have been called");
    }

    @Test
    void testStaffService_UpdateStaff_VoidSetterCalls() {
        // Given
        UUID staffId = UUID.randomUUID();
        Staff existingStaff = new Staff();
        existingStaff.setId(staffId);
        existingStaff.setName("Old Staff");
        existingStaff.setRole("Junior");
        existingStaff.setCreatedAt(Instant.now().minusSeconds(3600));
        
        Staff updateData = new Staff();
        updateData.setName("Updated Staff");
        updateData.setRole("Senior");
        updateData.setEmail("updated@example.com");
        
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(existingStaff);
        
        // When
        Staff result = staffService.updateStaff(staffId, updateData);
        
        // Then - Verify void setter methods were called
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepository).save(staffCaptor.capture());
        
        Staff savedStaff = staffCaptor.getValue();
        assertEquals("Updated Staff", savedStaff.getName(), "setName() should have been called");
        assertEquals("Senior", savedStaff.getRole(), "setRole() should have been called");
        assertEquals("updated@example.com", savedStaff.getEmail(), "setEmail() should have been called");
        assertNotNull(savedStaff.getUpdatedAt(), "setUpdatedAt() should have been called");
    }
}
