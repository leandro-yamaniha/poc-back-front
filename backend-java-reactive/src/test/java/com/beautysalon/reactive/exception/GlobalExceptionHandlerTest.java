package com.beautysalon.reactive.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebExchangeBindException webExchangeBindException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        FieldError fieldError1 = new FieldError("customer", "name", "Name is required");
        FieldError fieldError2 = new FieldError("customer", "email", "Email is invalid");
        List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);

        when(webExchangeBindException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleValidationErrors(webExchangeBindException);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
                assertEquals("Validation Failed", body.get("error"));
                assertNotNull(body.get("timestamp"));
                assertTrue(body.get("timestamp") instanceof LocalDateTime);
                
                @SuppressWarnings("unchecked")
                Map<String, String> fieldErrorsMap = (Map<String, String>) body.get("fieldErrors");
                assertNotNull(fieldErrorsMap);
                assertEquals(2, fieldErrorsMap.size());
                assertEquals("Name is required", fieldErrorsMap.get("name"));
                assertEquals("Email is invalid", fieldErrorsMap.get("email"));
            })
            .verifyComplete();
    }

    @Test
    void handleValidationErrors_WithEmptyFieldErrors_ShouldReturnEmptyFieldErrorsMap() {
        // Arrange
        when(webExchangeBindException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleValidationErrors(webExchangeBindException);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                @SuppressWarnings("unchecked")
                Map<String, String> fieldErrorsMap = (Map<String, String>) body.get("fieldErrors");
                assertNotNull(fieldErrorsMap);
                assertTrue(fieldErrorsMap.isEmpty());
            })
            .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestWithMessage() {
        // Arrange
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
                assertEquals("Bad Request", body.get("error"));
                assertEquals(errorMessage, body.get("message"));
                assertNotNull(body.get("timestamp"));
                assertTrue(body.get("timestamp") instanceof LocalDateTime);
            })
            .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_WithNullMessage_ShouldHandleNull() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException();

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
                assertEquals("Bad Request", body.get("error"));
                assertNull(body.get("message"));
                assertNotNull(body.get("timestamp"));
            })
            .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "Something went wrong";
        Exception exception = new RuntimeException(errorMessage);

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleGenericException(exception);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
                assertEquals("Internal Server Error", body.get("error"));
                assertEquals(errorMessage, body.get("message"));
                assertNotNull(body.get("timestamp"));
                assertTrue(body.get("timestamp") instanceof LocalDateTime);
            })
            .verifyComplete();
    }

    @Test
    void handleGenericException_WithNullMessage_ShouldHandleNull() {
        // Arrange
        Exception exception = new RuntimeException();

        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleGenericException(exception);

        // Assert
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
                assertEquals("Internal Server Error", body.get("error"));
                assertNull(body.get("message"));
                assertNotNull(body.get("timestamp"));
            })
            .verifyComplete();
    }

    @Test
    void handleGenericException_WithDifferentExceptionTypes_ShouldHandleAll() {
        // Test with different exception types
        Exception[] exceptions = {
            new RuntimeException("Runtime error"),
            new NullPointerException("Null pointer"),
            new IllegalStateException("Illegal state"),
            new UnsupportedOperationException("Unsupported operation")
        };

        for (Exception exception : exceptions) {
            Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleGenericException(exception);

            StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
                    assertEquals("Internal Server Error", body.get("error"));
                    assertEquals(exception.getMessage(), body.get("message"));
                    assertNotNull(body.get("timestamp"));
                })
                .verifyComplete();
        }
    }

    @Test
    void allHandlers_ShouldReturnMonoResponseEntity() {
        // Test that all handlers return the correct reactive type
        
        // Validation error handler
        when(webExchangeBindException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        
        Mono<ResponseEntity<Map<String, Object>>> validationResult = 
            globalExceptionHandler.handleValidationErrors(webExchangeBindException);
        assertNotNull(validationResult);
        
        // IllegalArgument handler
        Mono<ResponseEntity<Map<String, Object>>> illegalArgResult = 
            globalExceptionHandler.handleIllegalArgumentException(new IllegalArgumentException("test"));
        assertNotNull(illegalArgResult);
        
        // Generic exception handler
        Mono<ResponseEntity<Map<String, Object>>> genericResult = 
            globalExceptionHandler.handleGenericException(new Exception("test"));
        assertNotNull(genericResult);
    }

    @Test
    void errorResponseStructure_ShouldBeConsistent() {
        // Test that all error responses have consistent structure
        IllegalArgumentException exception = new IllegalArgumentException("Test error");
        
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleIllegalArgumentException(exception);

        StepVerifier.create(result)
            .assertNext(response -> {
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                // Check required fields are present
                assertTrue(body.containsKey("timestamp"));
                assertTrue(body.containsKey("status"));
                assertTrue(body.containsKey("error"));
                assertTrue(body.containsKey("message"));
                
                // Check field types
                assertTrue(body.get("timestamp") instanceof LocalDateTime);
                assertTrue(body.get("status") instanceof Integer);
                assertTrue(body.get("error") instanceof String);
            })
            .verifyComplete();
    }

    @Test
    void timestamp_ShouldBeRecentForAllHandlers() {
        LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);
        
        // Test IllegalArgumentException handler
        IllegalArgumentException exception = new IllegalArgumentException("Test");
        Mono<ResponseEntity<Map<String, Object>>> result = globalExceptionHandler.handleIllegalArgumentException(exception);

        StepVerifier.create(result)
            .assertNext(response -> {
                Map<String, Object> body = response.getBody();
                assertNotNull(body);
                
                LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
                LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);
                
                assertTrue(timestamp.isAfter(beforeCall));
                assertTrue(timestamp.isBefore(afterCall));
            })
            .verifyComplete();
    }
}
