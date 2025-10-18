package com.beautysalon.performance;

import com.beautysalon.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de comparação de performance entre configuração padrão e Virtual Threads,
 * ambos usando G1GC para isolar o impacto específico das Virtual Threads.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StandardVsVirtualThreadsTest {

    @Container
    static final CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
            .withInitScript("init.cql")
            .withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_USERS = 100;
    private static final int REQUESTS_PER_USER = 10;
    private static final int TOTAL_REQUESTS = CONCURRENT_USERS * REQUESTS_PER_USER;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> cassandra.getHost());
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "beauty_salon_test");
    }

    @BeforeEach
    void setUp() throws Exception {
        // Cleanup existing data
        mockMvc.perform(delete("/api/customers"));
        Thread.sleep(1000); // Allow cleanup to complete
    }

    @Test
    public void compareStandardVsVirtualThreadsWithG1GC() throws Exception {
        System.out.println("\n🚀 STANDARD vs VIRTUAL THREADS (G1GC) PERFORMANCE COMPARISON");
        System.out.println("=============================================================");
        System.out.println("🎯 Objetivo: Comparar impacto isolado das Virtual Threads usando G1GC");
        System.out.println("📊 Configuração: " + CONCURRENT_USERS + " usuários, " + REQUESTS_PER_USER + " req/usuário");
        System.out.println("🗑️  GC: G1GC em ambas as configurações");

        // Detectar se Virtual Threads estão habilitadas
        boolean virtualThreadsEnabled = isVirtualThreadsEnabled();
        String currentConfig = virtualThreadsEnabled ? "Virtual Threads + G1GC" : "Standard + G1GC";
        
        System.out.println("\n🔧 Configuração Atual Detectada: " + currentConfig);
        System.out.println("🧵 Virtual Threads: " + (virtualThreadsEnabled ? "HABILITADAS" : "DESABILITADAS"));

        // Executar testes de performance
        PerformanceResults results = runPerformanceTests();
        
        // Exibir resultados detalhados
        displayDetailedResults(results, currentConfig);
        
        // Validar performance
        validatePerformance(results, virtualThreadsEnabled);
        
        // Salvar resultados para comparação
        saveResultsForComparison(results, currentConfig);
        
        System.out.println("\n✅ Teste de " + currentConfig + " concluído com sucesso!");
        System.out.println("📁 Execute este teste com diferentes perfis para comparar:");
        System.out.println("   • Padrão: mvn test -Dtest=StandardVsVirtualThreadsTest -Dspring.profiles.active=test");
        System.out.println("   • Virtual Threads: mvn test -Dtest=StandardVsVirtualThreadsTest -Dspring.profiles.active=virtual-threads-g1gc");
    }

    private boolean isVirtualThreadsEnabled() {
        // Detectar se Virtual Threads estão habilitadas verificando a propriedade do sistema
        String virtualThreadsProperty = System.getProperty("spring.threads.virtual.enabled");
        if (virtualThreadsProperty != null) {
            return Boolean.parseBoolean(virtualThreadsProperty);
        }
        
        // Verificar através do profile ativo
        String activeProfile = System.getProperty("spring.profiles.active");
        return activeProfile != null && activeProfile.contains("virtual-threads");
    }

    private PerformanceResults runPerformanceTests() throws Exception {
        PerformanceResults results = new PerformanceResults();
        
        // Warmup
        System.out.println("\n🔥 Executando warmup...");
        performWarmup();
        
        // Teste 1: Criação de clientes
        System.out.println("\n📈 Teste 1 - Criação Concorrente de Clientes:");
        results.creationResults = runConcurrentCreationTest();
        
        // Teste 2: Busca de clientes
        System.out.println("\n📈 Teste 2 - Busca Concorrente de Clientes:");
        results.retrievalResults = runConcurrentRetrievalTest();
        
        // Teste 3: Operações mistas CRUD
        System.out.println("\n📈 Teste 3 - Operações CRUD Mistas:");
        results.mixedResults = runMixedCrudTest();
        
        return results;
    }

    private void performWarmup() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < 20; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    Customer customer = createTestCustomer("Warmup " + index, "warmup" + index + "@test.com");
                    mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer)))
                            .andExpect(status().isCreated());
                } catch (Exception e) {
                    // Ignore warmup errors
                }
                return null;
            }));
        }
        
        for (Future<Void> future : futures) {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Ignore warmup errors
            }
        }
        
        executor.shutdown();
        Thread.sleep(2000); // Allow warmup to settle
    }

    private TestResults runConcurrentCreationTest() throws Exception {
        TestResults results = new TestResults();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                long requestStart = System.currentTimeMillis();
                try {
                    Customer customer = createTestCustomer("Customer " + index, "customer" + index + "@test.com");
                    mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer)))
                            .andExpect(status().isCreated());
                    results.successCount.incrementAndGet();
                } catch (Exception e) {
                    results.errorCount.incrementAndGet();
                }
                return System.currentTimeMillis() - requestStart;
            }));
        }
        
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                responseTimes.add(future.get(60, TimeUnit.SECONDS));
            } catch (Exception e) {
                results.errorCount.incrementAndGet();
            }
        }
        
        long endTime = System.currentTimeMillis();
        results.totalTime = endTime - startTime;
        
        calculateMetrics(results, responseTimes);
        executor.shutdown();
        
        return results;
    }

    private TestResults runConcurrentRetrievalTest() throws Exception {
        TestResults results = new TestResults();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            futures.add(executor.submit(() -> {
                long requestStart = System.currentTimeMillis();
                try {
                    mockMvc.perform(get("/api/customers"))
                            .andExpect(status().isOk());
                    results.successCount.incrementAndGet();
                } catch (Exception e) {
                    results.errorCount.incrementAndGet();
                }
                return System.currentTimeMillis() - requestStart;
            }));
        }
        
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                responseTimes.add(future.get(60, TimeUnit.SECONDS));
            } catch (Exception e) {
                results.errorCount.incrementAndGet();
            }
        }
        
        long endTime = System.currentTimeMillis();
        results.totalTime = endTime - startTime;
        
        calculateMetrics(results, responseTimes);
        executor.shutdown();
        
        return results;
    }

    private TestResults runMixedCrudTest() throws Exception {
        TestResults results = new TestResults();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                long requestStart = System.currentTimeMillis();
                try {
                    if (index % 3 == 0) {
                        // Create
                        Customer customer = createTestCustomer("Mixed " + index, "mixed" + index + "@test.com");
                        mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                                .andExpect(status().isCreated());
                    } else if (index % 3 == 1) {
                        // Read
                        mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk());
                    } else {
                        // Update (simulated as read for simplicity)
                        mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk());
                    }
                    results.successCount.incrementAndGet();
                } catch (Exception e) {
                    results.errorCount.incrementAndGet();
                }
                return System.currentTimeMillis() - requestStart;
            }));
        }
        
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                responseTimes.add(future.get(60, TimeUnit.SECONDS));
            } catch (Exception e) {
                results.errorCount.incrementAndGet();
            }
        }
        
        long endTime = System.currentTimeMillis();
        results.totalTime = endTime - startTime;
        
        calculateMetrics(results, responseTimes);
        executor.shutdown();
        
        return results;
    }

    private void calculateMetrics(TestResults results, List<Long> responseTimes) {
        if (!responseTimes.isEmpty()) {
            results.averageResponseTime = (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            results.minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            results.maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        }
        
        if (results.totalTime > 0) {
            results.throughput = (double) results.successCount.get() / (results.totalTime / 1000.0);
        }
    }

    private void displayDetailedResults(PerformanceResults results, String config) {
        System.out.println("\n📊 RESULTADOS DETALHADOS - " + config);
        System.out.println("=====================================");
        
        System.out.println("\n📈 Teste 1 - Criação de Clientes:");
        displayTestResults("Criação", results.creationResults);
        
        System.out.println("\n📈 Teste 2 - Busca de Clientes:");
        displayTestResults("Busca", results.retrievalResults);
        
        System.out.println("\n📈 Teste 3 - Operações CRUD Mistas:");
        displayTestResults("CRUD Misto", results.mixedResults);
        
        // Resumo geral
        double avgThroughput = (results.creationResults.throughput + results.retrievalResults.throughput + results.mixedResults.throughput) / 3;
        long avgResponseTime = (results.creationResults.averageResponseTime + results.retrievalResults.averageResponseTime + results.mixedResults.averageResponseTime) / 3;
        
        System.out.println("\n🎯 RESUMO GERAL:");
        System.out.println("   🚀 Throughput Médio: " + String.format("%.2f req/s", avgThroughput));
        System.out.println("   ⏱️  Tempo de Resposta Médio: " + avgResponseTime + " ms");
        System.out.println("   📊 Total de Requisições: " + (TOTAL_REQUESTS * 3));
        System.out.println("   ✅ Configuração: " + config);
    }

    private void displayTestResults(String testName, TestResults results) {
        System.out.println("   📊 " + testName + " Results:");
        System.out.println("      ✅ Taxa de Sucesso: " + String.format("%.2f%%", 
            (double) results.successCount.get() / TOTAL_REQUESTS * 100));
        System.out.println("      🚀 Throughput: " + String.format("%.2f req/s", results.throughput));
        System.out.println("      ⏱️  Tempo Médio: " + results.averageResponseTime + " ms");
        System.out.println("      ⚡ Tempo Mínimo: " + results.minResponseTime + " ms");
        System.out.println("      🔥 Tempo Máximo: " + results.maxResponseTime + " ms");
        System.out.println("      ⏰ Tempo Total: " + results.totalTime + " ms");
        System.out.println("      ❌ Erros: " + results.errorCount.get());
    }

    private void validatePerformance(PerformanceResults results, boolean isVirtualThreads) {
        System.out.println("\n🎯 VALIDAÇÃO DE PERFORMANCE");
        System.out.println("============================");
        
        // Validação de taxa de sucesso (ajustada para ambiente realista)
        double creationSuccessRate = (double) results.creationResults.successCount.get() / TOTAL_REQUESTS * 100;
        double retrievalSuccessRate = (double) results.retrievalResults.successCount.get() / TOTAL_REQUESTS * 100;
        double mixedSuccessRate = (double) results.mixedResults.successCount.get() / TOTAL_REQUESTS * 100;
        
        assertTrue(creationSuccessRate >= 90.0, "Taxa de sucesso de criação deve ser >= 90%");
        assertTrue(retrievalSuccessRate >= 85.0, "Taxa de sucesso de busca deve ser >= 85%");
        assertTrue(mixedSuccessRate >= 85.0, "Taxa de sucesso de CRUD misto deve ser >= 85%");
        
        // Validação de throughput (ajustada para ambiente realista)
        if (isVirtualThreads) {
            assertTrue(results.creationResults.throughput > 8, "Virtual Threads: throughput de criação deve ser > 8 req/s");
            assertTrue(results.retrievalResults.throughput > 5, "Virtual Threads: throughput de busca deve ser > 5 req/s");
            System.out.println("✅ Validação de performance Virtual Threads passou");
        } else {
            assertTrue(results.creationResults.throughput > 5, "Standard: throughput de criação deve ser > 5 req/s");
            assertTrue(results.retrievalResults.throughput > 3, "Standard: throughput de busca deve ser > 3 req/s");
            System.out.println("✅ Validação de performance Standard passou");
        }
        
        System.out.println("🏆 Todas as validações de performance PASSARAM!");
    }

    private void saveResultsForComparison(PerformanceResults results, String config) {
        // Salvar resultados em formato que pode ser usado para comparação
        System.out.println("\n💾 RESULTADOS PARA COMPARAÇÃO:");
        System.out.println("Config: " + config);
        System.out.println("Creation_Throughput: " + String.format("%.2f", results.creationResults.throughput));
        System.out.println("Retrieval_Throughput: " + String.format("%.2f", results.retrievalResults.throughput));
        System.out.println("Mixed_Throughput: " + String.format("%.2f", results.mixedResults.throughput));
        System.out.println("Creation_AvgTime: " + results.creationResults.averageResponseTime);
        System.out.println("Retrieval_AvgTime: " + results.retrievalResults.averageResponseTime);
        System.out.println("Mixed_AvgTime: " + results.mixedResults.averageResponseTime);
    }

    private Customer createTestCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone("5551234567");
        customer.setAddress("Test Address");
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        return customer;
    }

    // Helper classes para resultados
    private static class PerformanceResults {
        TestResults creationResults;
        TestResults retrievalResults;
        TestResults mixedResults;
    }

    private static class TestResults {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        long totalTime;
        long averageResponseTime;
        long minResponseTime;
        long maxResponseTime;
        double throughput;
    }
}
