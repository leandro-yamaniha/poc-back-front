package com.beautysalon.integration;

import com.beautysalon.model.Customer;
import com.beautysalon.service.CustomerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
public class CustomerStepDefinitions {

    @Container
    static final CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
            .withInitScript("init.cql")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Garantir que o container esteja iniciado
        if (!cassandra.isRunning()) {
            cassandra.start();
        }
        registry.add("spring.cassandra.contact-points", () -> cassandra.getHost());
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "beauty_salon_test");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer createdCustomer;
    private MvcResult lastResult;

    @Given("the customer service is available")
    public void the_customer_service_is_available() throws Exception {
        // Verificar se o serviço está disponível fazendo uma requisição simples
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @When("I create a customer with name {string} and email {string}")
    public void i_create_a_customer_with_name_and_email(String name, String email) throws Exception {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone("123456789");
        customer.setAddress("123 Main St");

        String customerJson = objectMapper.writeValueAsString(customer);

        lastResult = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andReturn();

        if (lastResult.getResponse().getStatus() == 201) {
            String responseContent = lastResult.getResponse().getContentAsString();
            createdCustomer = objectMapper.readValue(responseContent, Customer.class);
        }
    }

    @Then("the customer should be created successfully")
    public void the_customer_should_be_created_successfully() {
        assertEquals(201, lastResult.getResponse().getStatus());
        assertNotNull(createdCustomer);
        assertNotNull(createdCustomer.getId());
        assertNotNull(createdCustomer.getCreatedAt());
        assertNotNull(createdCustomer.getUpdatedAt());
    }

    @Then("the customer should have a valid ID")
    public void the_customer_should_have_a_valid_id() {
        assertNotNull(createdCustomer.getId());
        assertDoesNotThrow(() -> UUID.fromString(createdCustomer.getId().toString()));
    }

    @Given("a customer exists with ID {string}")
    public void a_customer_exists_with_id(String id) {
        // Criar um cliente com o ID especificado
        Customer customer = new Customer();
        customer.setId(UUID.fromString(id));
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("123456789");
        customer.setAddress("123 Main St");
        
        // Salvar o cliente diretamente usando o serviço
        createdCustomer = customerService.createCustomer(customer);
    }

    @When("I request the customer with ID {string}")
    public void i_request_the_customer_with_id(String id) throws Exception {
        lastResult = mockMvc.perform(get("/api/customers/" + id))
                .andReturn();

        if (lastResult.getResponse().getStatus() == 200) {
            String responseContent = lastResult.getResponse().getContentAsString();
            createdCustomer = objectMapper.readValue(responseContent, Customer.class);
        }
    }

    @Then("the customer details should be returned")
    public void the_customer_details_should_be_returned() {
        assertEquals(200, lastResult.getResponse().getStatus());
        assertNotNull(createdCustomer);
    }

    @Then("the customer name should be {string}")
    public void the_customer_name_should_be(String name) {
        assertNotNull(createdCustomer);
        assertEquals(name, createdCustomer.getName());
    }
}
