package com.beautysalon.validation;

import com.beautysalon.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
    "spring.cassandra.keyspace-name=beauty_salon_test",
    "spring.cassandra.schema-action=create_if_not_exists"
})
public class CustomerValidationTest {

    @Container
    static final CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
            .withInitScript("init.cql")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> cassandra.getHost());
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "beauty_salon_test");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testValidCustomerCreation() throws Exception {
        Customer customer = new Customer();
        customer.setName("Valid Customer");
        customer.setEmail("valid@test.com");
        customer.setPhone("1234567890"); // 10 digits - should be valid
        customer.setAddress("Valid Address");

        String customerJson = objectMapper.writeValueAsString(customer);
        System.out.println("🔍 Testing valid customer JSON: " + customerJson);

        MvcResult result = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andReturn();

        System.out.println("📊 Response Status: " + result.getResponse().getStatus());
        System.out.println("📊 Response Body: " + result.getResponse().getContentAsString());
        System.out.println("📊 Response Headers: " + result.getResponse().getHeaderNames());
    }

    @Test
    public void testInvalidCustomerCreation() throws Exception {
        Customer customer = new Customer();
        customer.setName(""); // Invalid - blank name
        customer.setEmail("invalid-email"); // Invalid - bad email format
        customer.setPhone("123"); // Invalid - too short
        customer.setAddress(""); // Invalid - blank address

        String customerJson = objectMapper.writeValueAsString(customer);
        System.out.println("🔍 Testing invalid customer JSON: " + customerJson);

        MvcResult result = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andReturn();

        System.out.println("📊 Response Status: " + result.getResponse().getStatus());
        System.out.println("📊 Response Body: " + result.getResponse().getContentAsString());
        System.out.println("📊 Response Headers: " + result.getResponse().getHeaderNames());
    }
}
