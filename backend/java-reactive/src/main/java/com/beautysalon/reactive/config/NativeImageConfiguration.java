package com.beautysalon.reactive.config;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.model.Staff;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração centralizada de hints para Native Image
 * Fornece metadados de compilação AOT para GraalVM
 */
@Configuration(proxyBeanMethods = false)
@RegisterReflectionForBinding({
    // Domain Models - Registra para reflexão completa
    Customer.class,
    Service.class,
    Staff.class,
    Appointment.class,
    
    // Cassandra Driver Classes - Classes principais do driver
    com.datastax.oss.driver.api.core.CqlSession.class,
    com.datastax.oss.driver.api.core.cql.Row.class,
    com.datastax.oss.driver.api.core.cql.ResultSet.class,
    com.datastax.oss.driver.api.core.cql.Statement.class,
    com.datastax.oss.driver.api.core.metadata.Metadata.class,
    com.datastax.oss.driver.api.core.config.DriverConfig.class,
    com.datastax.oss.driver.api.core.context.DriverContext.class,
    
    // Reactor Classes - Para programação reativa
    reactor.core.publisher.Mono.class,
    reactor.core.publisher.Flux.class,
    
    // Java Time Classes - Usadas nos modelos
    java.time.LocalDateTime.class,
    java.util.UUID.class,
    java.math.BigDecimal.class
})
public class NativeImageConfiguration {
    
    /**
     * Esta classe serve como ponto central para configurações de Native Image
     * usando a anotação @RegisterReflectionForBinding do Spring AOT.
     * 
     * A anotação @RegisterReflectionForBinding registra automaticamente:
     * - Todos os construtores declarados
     * - Todos os métodos declarados
     * - Todos os campos declarados
     * 
     * Isso complementa as configurações programáticas em NativeRuntimeHints.java
     * e as anotações individuais nos modelos.
     * 
     * O Spring Boot AOT também detecta automaticamente:
     * - Repositórios Spring Data (gera proxies dinâmicos)
     * - Controllers (para reflexão)
     * - Services (para reflexão)
     * - Configurações (para reflexão)
     */
}
