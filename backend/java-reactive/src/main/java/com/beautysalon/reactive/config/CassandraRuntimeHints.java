package com.beautysalon.reactive.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Runtime Hints for Cassandra Driver
 * Ensures Cassandra classes are available for reflection in native image
 */
@Configuration
@ImportRuntimeHints(CassandraRuntimeHints.CassandraHintsRegistrar.class)
public class CassandraRuntimeHints {

    /**
     * Registrar for Cassandra Driver runtime hints
     */
    static class CassandraHintsRegistrar implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register Cassandra core classes
            registerCassandraClasses(hints);
            
            // Register Cassandra resources
            registerCassandraResources(hints);
        }
        
        /**
         * Register Cassandra core classes for reflection
         */
        private void registerCassandraClasses(RuntimeHints hints) {
            // CqlSession
            hints.reflection()
                .registerType(CqlSession.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
            
            // Row
            hints.reflection()
                .registerType(Row.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        }
        
        /**
         * Register Cassandra configuration resources
         */
        private void registerCassandraResources(RuntimeHints hints) {
            hints.resources()
                .registerPattern("reference.conf")
                .registerPattern("com/datastax/**/*.properties")
                .registerPattern("com/datastax/**/*.conf");
        }
    }
}
