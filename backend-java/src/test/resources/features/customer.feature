Feature: Customer Management

  Scenario: Create a new customer
    Given the customer service is available
    When I create a customer with name "John Doe" and email "john.doe@example.com"
    Then the customer should be created successfully
    And the customer should have a valid ID

  Scenario: Get customer by ID
    Given a customer exists with ID "123e4567-e89b-12d3-a456-426614174000"
    When I request the customer with ID "123e4567-e89b-12d3-a456-426614174000"
    Then the customer details should be returned
    And the customer name should be "John Doe"
