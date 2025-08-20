package com.beautysalon.reactive.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.beautysalon.reactive.repository.CustomerRepository;
import com.beautysalon.reactive.repository.ServiceRepository;
import com.beautysalon.reactive.repository.StaffRepository;
import com.beautysalon.reactive.repository.AppointmentRepository;
import org.mockito.Mockito;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    @Primary
    public ServiceRepository serviceRepository() {
        return Mockito.mock(ServiceRepository.class);
    }

    @Bean
    @Primary
    public StaffRepository staffRepository() {
        return Mockito.mock(StaffRepository.class);
    }

    @Bean
    @Primary
    public AppointmentRepository appointmentRepository() {
        return Mockito.mock(AppointmentRepository.class);
    }
}
