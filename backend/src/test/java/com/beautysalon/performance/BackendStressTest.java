package com.beautysalon.performance;

import com.beautysalon.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
public class BackendStressTest {

    @Container
    static final CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
            .withInitScript("init.cql")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_USERS = 100;
    private static final int REQUESTS_PER_USER = 10;
    private static final int TIMEOUT_SECONDS = 60;

    @Test
    public void testConcurrentCustomerCreation() throws Exception {
        System.out.println("🚀 Iniciando teste de stress - Criação Concorrente de Clientes");
        System.out.println("👥 Usuários simultâneos: " + CONCURRENT_USERS);
        System.out.println("📊 Requisições por usuário: " + REQUESTS_PER_USER);
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_USER; j++) {
                        long requestStart = System.currentTimeMillis();
                        
                        Customer customer = new Customer();
                        customer.setName("StressTest User " + userId + "-" + j);
                        customer.setEmail("stress" + userId + "-" + j + "@test.com");
                        customer.setPhone(String.format("1234567%03d", userId % 1000)); // 10 digits
                        customer.setAddress("Test Address " + userId + "-" + j);

                        String customerJson = objectMapper.writeValueAsString(customer);

                        MvcResult result = mockMvc.perform(post("/api/customers")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(customerJson))
                                .andReturn();

                        long requestEnd = System.currentTimeMillis();
                        responseTimes.add(requestEnd - requestStart);

                        if (result.getResponse().getStatus() == 201) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                            System.err.println("❌ Erro na criação: Status " + result.getResponse().getStatus());
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("❌ Exceção durante teste: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Calcular estatísticas
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        int totalRequests = CONCURRENT_USERS * REQUESTS_PER_USER;
        double throughput = (double) successCount.get() / (totalTime / 1000.0);

        // Relatório de resultados
        System.out.println("\n📈 RELATÓRIO DE STRESS TEST - CRIAÇÃO DE CLIENTES");
        System.out.println("=" .repeat(60));
        System.out.println("⏱️  Tempo total: " + totalTime + "ms");
        System.out.println("✅ Requisições bem-sucedidas: " + successCount.get() + "/" + totalRequests);
        System.out.println("❌ Requisições com erro: " + errorCount.get() + "/" + totalRequests);
        System.out.println("📊 Taxa de sucesso: " + String.format("%.2f%%", (double) successCount.get() / totalRequests * 100));
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⚡ Tempo de resposta médio: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("⚡ Tempo de resposta mínimo: " + minResponseTime + "ms");
        System.out.println("⚡ Tempo de resposta máximo: " + maxResponseTime + "ms");

        assertTrue(completed, "Teste não completou dentro do timeout");
        assertTrue(successCount.get() > totalRequests * 0.95, "Taxa de sucesso muito baixa: " + successCount.get() + "/" + totalRequests);
        assertTrue(avgResponseTime < 5000, "Tempo de resposta médio muito alto: " + avgResponseTime + "ms");
    }

    @Test
    public void testConcurrentCustomerRetrieval() throws Exception {
        System.out.println("🔍 Iniciando teste de stress - Busca Concorrente de Clientes");
        
        // Primeiro, criar alguns clientes para buscar
        List<String> customerIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();
            customer.setName("Retrieve Test Customer " + i);
            customer.setEmail("retrieve" + i + "@test.com");
            customer.setPhone(String.format("1234567%03d", i)); // 10 digits
            customer.setAddress("Test Address " + i);

            String customerJson = objectMapper.writeValueAsString(customer);
            MvcResult result = mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(customerJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            Customer createdCustomer = objectMapper.readValue(responseContent, Customer.class);
            customerIds.add(createdCustomer.getId().toString());
        }

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_USER; j++) {
                        long requestStart = System.currentTimeMillis();
                        
                        // Buscar um cliente aleatório
                        String customerId = customerIds.get(j % customerIds.size());
                        
                        MvcResult result = mockMvc.perform(get("/api/customers/" + customerId))
                                .andReturn();

                        long requestEnd = System.currentTimeMillis();
                        responseTimes.add(requestEnd - requestStart);

                        if (result.getResponse().getStatus() == 200) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("❌ Exceção durante busca: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Calcular estatísticas
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        int totalRequests = CONCURRENT_USERS * REQUESTS_PER_USER;
        double throughput = (double) successCount.get() / (totalTime / 1000.0);

        // Relatório de resultados
        System.out.println("\n📈 RELATÓRIO DE STRESS TEST - BUSCA DE CLIENTES");
        System.out.println("=" .repeat(60));
        System.out.println("⏱️  Tempo total: " + totalTime + "ms");
        System.out.println("✅ Requisições bem-sucedidas: " + successCount.get() + "/" + totalRequests);
        System.out.println("❌ Requisições com erro: " + errorCount.get() + "/" + totalRequests);
        System.out.println("📊 Taxa de sucesso: " + String.format("%.2f%%", (double) successCount.get() / totalRequests * 100));
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("⚡ Tempo de resposta médio: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("⚡ Tempo de resposta mínimo: " + minResponseTime + "ms");
        System.out.println("⚡ Tempo de resposta máximo: " + maxResponseTime + "ms");

        assertTrue(completed, "Teste não completou dentro do timeout");
        assertTrue(successCount.get() > totalRequests * 0.98, "Taxa de sucesso muito baixa para busca: " + successCount.get() + "/" + totalRequests);
        assertTrue(avgResponseTime < 2000, "Tempo de resposta médio muito alto para busca: " + avgResponseTime + "ms");
    }

    @Test
    public void testMixedWorkloadStress() throws Exception {
        System.out.println("🔄 Iniciando teste de stress - Carga Mista (CRUD)");
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger createCount = new AtomicInteger(0);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger deleteCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<String> createdCustomerIds = new CopyOnWriteArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_USER; j++) {
                        int operation = j % 4; // 0=CREATE, 1=READ, 2=UPDATE, 3=DELETE
                        
                        switch (operation) {
                            case 0: // CREATE
                                Customer customer = new Customer();
                                customer.setName("Mixed Test User " + userId + "-" + j);
                                customer.setEmail("mixed" + userId + "-" + j + "@test.com");
                                customer.setPhone(String.format("1234567%03d", userId % 1000)); // 10 digits
                                customer.setAddress("Mixed Address " + userId + "-" + j);

                                String customerJson = objectMapper.writeValueAsString(customer);
                                MvcResult createResult = mockMvc.perform(post("/api/customers")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(customerJson))
                                        .andReturn();

                                if (createResult.getResponse().getStatus() == 201) {
                                    createCount.incrementAndGet();
                                    String responseContent = createResult.getResponse().getContentAsString();
                                    Customer createdCustomer = objectMapper.readValue(responseContent, Customer.class);
                                    createdCustomerIds.add(createdCustomer.getId().toString());
                                } else {
                                    errorCount.incrementAndGet();
                                }
                                break;

                            case 1: // READ
                                if (!createdCustomerIds.isEmpty()) {
                                    String customerId = createdCustomerIds.get(
                                            (int) (Math.random() * createdCustomerIds.size()));
                                    MvcResult readResult = mockMvc.perform(get("/api/customers/" + customerId))
                                            .andReturn();
                                    if (readResult.getResponse().getStatus() == 200) {
                                        readCount.incrementAndGet();
                                    } else {
                                        errorCount.incrementAndGet();
                                    }
                                } else {
                                    // Se não há clientes, fazer um GET all
                                    MvcResult readAllResult = mockMvc.perform(get("/api/customers"))
                                            .andReturn();
                                    if (readAllResult.getResponse().getStatus() == 200) {
                                        readCount.incrementAndGet();
                                    } else {
                                        errorCount.incrementAndGet();
                                    }
                                }
                                break;

                            case 2: // UPDATE
                                if (!createdCustomerIds.isEmpty()) {
                                    String customerId = createdCustomerIds.get(
                                            (int) (Math.random() * createdCustomerIds.size()));
                                    Customer updateCustomer = new Customer();
                                    updateCustomer.setName("Updated User " + userId + "-" + j);
                                    updateCustomer.setEmail("updated" + userId + "-" + j + "@test.com");
                                    updateCustomer.setPhone(String.format("9876543%03d", userId % 1000)); // 10 digits
                                    updateCustomer.setAddress("Updated Address " + userId + "-" + j);

                                    String updateJson = objectMapper.writeValueAsString(updateCustomer);
                                    MvcResult updateResult = mockMvc.perform(put("/api/customers/" + customerId)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(updateJson))
                                            .andReturn();
                                    if (updateResult.getResponse().getStatus() == 200) {
                                        updateCount.incrementAndGet();
                                    } else {
                                        errorCount.incrementAndGet();
                                    }
                                }
                                break;

                            case 3: // DELETE
                                if (!createdCustomerIds.isEmpty()) {
                                    String customerId = createdCustomerIds.remove(
                                            (int) (Math.random() * createdCustomerIds.size()));
                                    MvcResult deleteResult = mockMvc.perform(delete("/api/customers/" + customerId))
                                            .andReturn();
                                    if (deleteResult.getResponse().getStatus() == 204) {
                                        deleteCount.incrementAndGet();
                                    } else {
                                        errorCount.incrementAndGet();
                                    }
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("❌ Exceção durante carga mista: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS * 2, TimeUnit.SECONDS); // Mais tempo para carga mista
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        int totalOperations = createCount.get() + readCount.get() + updateCount.get() + deleteCount.get();
        double throughput = (double) totalOperations / (totalTime / 1000.0);

        // Relatório de resultados
        System.out.println("\n📈 RELATÓRIO DE STRESS TEST - CARGA MISTA");
        System.out.println("=" .repeat(60));
        System.out.println("⏱️  Tempo total: " + totalTime + "ms");
        System.out.println("➕ Operações CREATE: " + createCount.get());
        System.out.println("🔍 Operações READ: " + readCount.get());
        System.out.println("✏️  Operações UPDATE: " + updateCount.get());
        System.out.println("🗑️  Operações DELETE: " + deleteCount.get());
        System.out.println("❌ Operações com erro: " + errorCount.get());
        System.out.println("📊 Total de operações: " + totalOperations);
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " ops/s");
        System.out.println("📊 Taxa de sucesso: " + String.format("%.2f%%", (double) totalOperations / (totalOperations + errorCount.get()) * 100));

        assertTrue(completed, "Teste não completou dentro do timeout");
        assertTrue(totalOperations > 0, "Nenhuma operação foi executada com sucesso");
        assertTrue(errorCount.get() < totalOperations * 0.1, "Taxa de erro muito alta: " + errorCount.get() + " de " + (totalOperations + errorCount.get()));
    }
}
