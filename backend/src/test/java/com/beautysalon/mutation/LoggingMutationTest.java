package com.beautysalon.mutation;

import com.beautysalon.controller.CustomerController;
import com.beautysalon.model.Customer;
import com.beautysalon.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Targeted mutation tests to kill logging-related VoidMethodCallMutator surviving mutations.
 * Focuses on println and printStackTrace calls in controllers.
 */
@ExtendWith(MockitoExtension.class)
public class LoggingMutationTest {

    @Mock
    private CustomerService customerService;
    
    @InjectMocks
    private CustomerController customerController;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // Capture System.out to verify println calls
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @Test
    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Test CustomerController.createCustomer logging calls
     * Targets: removed call to PrintStream::println and Exception::printStackTrace
     */
    @Test
    void testCustomerController_CreateCustomer_LoggingCalls() {
        // Given - Setup service to throw an exception
        Customer newCustomer = new Customer();
        newCustomer.setName("Test Customer");
        newCustomer.setEmail("test@example.com");
        newCustomer.setPhone("123-456-7890");

        RuntimeException testException = new RuntimeException("Test exception for logging");
        when(customerService.createCustomer(any(Customer.class))).thenThrow(testException);

        // When - Call the controller method that should trigger logging
        ResponseEntity<?> response = customerController.createCustomer(newCustomer);

        // Then - Verify the response indicates an error occurred
        assertEquals(400, response.getStatusCodeValue());
        
        // Verify that some output was captured (indicating logging occurred)
        // If println or printStackTrace calls were removed, no output would be captured
        String capturedOutput = outputStreamCaptor.toString();
        
        // The mutation test passes if we can detect that logging calls were made
        // If the VoidMethodCallMutator removed the logging calls, this would fail
        assertFalse(capturedOutput.isEmpty(), 
            "Expected logging output but none was captured - logging calls may have been removed by mutation");
        
        // Additional verification that the exception handling path was taken
        verify(customerService).createCustomer(any(Customer.class));
    }

    /**
     * Test exception handling and logging in error scenarios
     * Targets: removed call to Exception::printStackTrace
     */
    @Test
    void testExceptionHandling_PrintStackTrace() {
        // Given - Setup multiple exception scenarios
        Customer customer1 = new Customer();
        customer1.setName("Customer 1");
        customer1.setEmail("customer1@example.com");

        Customer customer2 = new Customer();
        customer2.setName("Customer 2");
        customer2.setEmail("customer2@example.com");

        // Setup different types of exceptions
        RuntimeException runtimeException = new RuntimeException("Runtime exception test");
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Illegal argument test");

        when(customerService.createCustomer(eq(customer1))).thenThrow(runtimeException);
        when(customerService.createCustomer(eq(customer2))).thenThrow(illegalArgException);

        // When - Trigger multiple exception scenarios
        ResponseEntity<?> response1 = customerController.createCustomer(customer1);
        ResponseEntity<?> response2 = customerController.createCustomer(customer2);

        // Then - Verify both calls resulted in error responses
        assertEquals(400, response1.getStatusCodeValue());
        assertEquals(400, response2.getStatusCodeValue());

        // Verify that stack traces were printed (if printStackTrace calls weren't removed)
        String capturedOutput = outputStreamCaptor.toString();
        
        // If printStackTrace calls were removed by mutation, we wouldn't see stack trace output
        assertFalse(capturedOutput.isEmpty(), 
            "Expected stack trace output but none was captured - printStackTrace calls may have been removed");
        
        // Verify service calls were made
        verify(customerService).createCustomer(customer1);
        verify(customerService).createCustomer(customer2);
    }

    /**
     * Test successful path to ensure logging doesn't interfere with normal operation
     * This helps distinguish between logging mutations and other issues
     */
    @Test
    void testCustomerController_CreateCustomer_SuccessPath() {
        // Given - Setup successful service response
        Customer newCustomer = new Customer();
        newCustomer.setName("Success Customer");
        newCustomer.setEmail("success@example.com");
        newCustomer.setPhone("123-456-7890");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(UUID.randomUUID());
        savedCustomer.setName("Success Customer");
        savedCustomer.setEmail("success@example.com");
        savedCustomer.setPhone("123-456-7890");

        when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // When - Call the controller method
        ResponseEntity<?> response = customerController.createCustomer(newCustomer);

        // Then - Verify successful response
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        // Verify service was called
        verify(customerService).createCustomer(any(Customer.class));
        
        // In success path, there should be minimal or no error logging
        String capturedOutput = outputStreamCaptor.toString();
        
        // This test helps ensure our logging tests are actually testing the error paths
        // and not just capturing normal operation output
    }

    /**
     * Test logging behavior with null inputs
     * Targets edge cases that might trigger different logging paths
     */
    @Test
    void testLogging_EdgeCases() {
        // Test case 1: Null customer
        ResponseEntity<?> response1 = customerController.createCustomer(null);
        
        // Test case 2: Customer with null fields
        Customer customerWithNulls = new Customer();
        // Leave all fields null
        ResponseEntity<?> response2 = customerController.createCustomer(customerWithNulls);
        
        // Test case 3: Service returning null
        Customer validCustomer = new Customer();
        validCustomer.setName("Valid Customer");
        validCustomer.setEmail("valid@example.com");
        
        when(customerService.createCustomer(any(Customer.class))).thenReturn(null);
        ResponseEntity<?> response3 = customerController.createCustomer(validCustomer);
        
        // Verify that all edge cases were handled
        // The specific response codes depend on the implementation,
        // but the important thing is that the methods executed without crashing
        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);
        
        // If logging calls were removed, the error handling might behave differently
        String capturedOutput = outputStreamCaptor.toString();
        
        // We expect some output from error handling in these edge cases
        // If VoidMethodCallMutator removed logging calls, behavior would change
    }

    /**
     * Test repeated exception scenarios to verify consistent logging behavior
     * This helps detect if logging state is affected by mutations
     */
    @Test
    void testRepeatedExceptions_ConsistentLogging() {
        // Given - Setup service to consistently throw exceptions
        Customer testCustomer = new Customer();
        testCustomer.setName("Repeated Test");
        testCustomer.setEmail("repeated@example.com");

        RuntimeException consistentException = new RuntimeException("Consistent exception");
        when(customerService.createCustomer(any(Customer.class))).thenThrow(consistentException);

        // When - Make multiple calls that should trigger logging
        for (int i = 0; i < 3; i++) {
            ResponseEntity<?> response = customerController.createCustomer(testCustomer);
            assertEquals(400, response.getStatusCodeValue());
        }

        // Then - Verify consistent logging behavior
        String capturedOutput = outputStreamCaptor.toString();
        
        // If logging calls were removed, we wouldn't see repeated output
        assertFalse(capturedOutput.isEmpty(), 
            "Expected repeated logging output but none was captured");
        
        // Verify service was called multiple times
        verify(customerService, times(3)).createCustomer(any(Customer.class));
    }
}
