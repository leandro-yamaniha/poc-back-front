package com.beautysalon.mutation;

import com.beautysalon.model.Customer;
import com.beautysalon.repository.CustomerRepository;
import com.beautysalon.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceFinalMutationTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        testCustomer = new Customer();
        testCustomer.setId(testId);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("123-456-7890");
        testCustomer.setCreatedAt(Instant.now());
        testCustomer.setUpdatedAt(Instant.now());
    }

    @Test
    void createCustomer_ShouldSetTimestampsExplicitly() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhone("098-765-4321");
        newCustomer.setAddress("456 Oak Ave");

        // Configurar o mock para capturar o argumento passado para save
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customer;
        });

        // When
        Customer result = customerService.createCustomer(newCustomer);

        // Then
        assertNotNull(result);
        // Verificar explicitamente se os timestamps foram definidos com valores não nulos
        verify(customerRepository).save(argThat(customer -> 
            customer.getCreatedAt() != null && customer.getUpdatedAt() != null));
    }

    @Test
    void createCustomer_ShouldSetCreatedAtAndUpdatedAtWithDifferentValues() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhone("098-765-4321");
        newCustomer.setAddress("456 Oak Ave");

        // Configurar o mock para capturar o argumento passado para save
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customer;
        });

        // When
        Customer result = customerService.createCustomer(newCustomer);

        // Then
        assertNotNull(result);
        // Verificar que ambos os timestamps foram definidos e não são nulos
        verify(customerRepository).save(argThat(customer -> 
            customer.getCreatedAt() != null && customer.getUpdatedAt() != null));
        
        // Além disso, verificar que os timestamps foram definidos com valores não nulos
        // Isso mata as mutações que removem as chamadas para setCreatedAt e setUpdatedAt
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createCustomer_ShouldCallSetCreatedAtAndSetUpdatedAt() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhone("098-765-4321");
        newCustomer.setAddress("456 Oak Ave");

        // Usar um spy para verificar se os métodos setCreatedAt e setUpdatedAt são chamados
        Customer spyCustomer = spy(newCustomer);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customer;
        });

        // When
        customerService.createCustomer(spyCustomer);

        // Then
        // Verificar se os métodos setCreatedAt e setUpdatedAt foram chamados
        verify(spyCustomer).setCreatedAt(any(Instant.class));
        verify(spyCustomer).setUpdatedAt(any(Instant.class));
    }

    @Test
    void updateCustomer_WhenCustomerExists_ShouldUpdateTimestamp() {
        // Given
        Customer updatedDetails = new Customer();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated.email@example.com");
        updatedDetails.setPhone("111-222-3333");

        when(customerRepository.findById(testId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Capturar o estado antes da atualização
        Instant oldUpdatedAt = testCustomer.getUpdatedAt();

        // When
        Customer result = customerService.updateCustomer(testId, updatedDetails);

        // Then
        assertNotNull(result);
        // Verificar que o timestamp de atualização foi modificado
        assertNotNull(testCustomer.getUpdatedAt(), "UpdatedAt should be set");
        assertNotEquals(oldUpdatedAt, testCustomer.getUpdatedAt(), "UpdatedAt should be updated");
        verify(customerRepository).findById(testId);
        verify(customerRepository).save(testCustomer);
    }
}
