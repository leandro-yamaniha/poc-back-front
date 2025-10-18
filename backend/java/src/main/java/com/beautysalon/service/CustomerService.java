package com.beautysalon.service;

import com.beautysalon.config.MetricsConfiguration;
import com.beautysalon.model.Customer;
import com.beautysalon.repository.CustomerRepository;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Cacheable(value = "customers", key = "'all'")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    @Cacheable(value = "customers", key = "#id")
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }
    
    @Cacheable(value = "customers", key = "'email:' + #email")
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    @Cacheable(value = "customers", key = "'search:' + #name")
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContaining("%" + name + "%");
    }
    
    @CacheEvict(value = "customers", allEntries = true)
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        return customerRepository.save(customer);
    }
    
    @CachePut(value = "customers", key = "#id")
    @CacheEvict(value = "customers", key = "'all'")
    public Customer updateCustomer(UUID id, Customer customerDetails) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setName(customerDetails.getName());
            customer.setEmail(customerDetails.getEmail());
            customer.setPhone(customerDetails.getPhone());
            customer.setAddress(customerDetails.getAddress());
            customer.setUpdatedAt(Instant.now());
            return customerRepository.save(customer);
        }
        return null;
    }
    
    @CacheEvict(value = "customers", allEntries = true)
    public boolean deleteCustomer(UUID id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
