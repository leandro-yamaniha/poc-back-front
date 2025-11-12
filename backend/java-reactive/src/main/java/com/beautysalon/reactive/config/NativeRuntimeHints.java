package com.beautysalon.reactive.config;

import com.beautysalon.reactive.model.Appointment;
import com.beautysalon.reactive.model.Customer;
import com.beautysalon.reactive.model.Service;
import com.beautysalon.reactive.model.Staff;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Native Runtime Hints Configuration for GraalVM Native Image
 * Registers reflection and resource hints for AOT compilation
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeRuntimeHints.BeautySalonRuntimeHints.class)
public class NativeRuntimeHints {

    /**
     * Registrar for Beauty Salon application runtime hints
     */
    static class BeautySalonRuntimeHints implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register domain models for reflection
            registerModels(hints);
            
            // Register application resources
            registerResources(hints);
            
            // Register Cassandra driver hints
            registerCassandraHints(hints);
        }
        
        /**
         * Register domain models for reflection access
         */
        private void registerModels(RuntimeHints hints) {
            // Customer model
            hints.reflection()
                .registerType(Customer.class, hint -> hint
                    .withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.DECLARED_FIELDS
                    ));
            
            // Service model
            hints.reflection()
                .registerType(Service.class, hint -> hint
                    .withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.DECLARED_FIELDS
                    ));
            
            // Staff model
            hints.reflection()
                .registerType(Staff.class, hint -> hint
                    .withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.DECLARED_FIELDS
                    ));
            
            // Appointment model
            hints.reflection()
                .registerType(Appointment.class, hint -> hint
                    .withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.DECLARED_FIELDS
                    ));
        }
        
        /**
         * Register application resources for native image
         */
        private void registerResources(RuntimeHints hints) {
            // Application configuration files
            hints.resources()
                .registerPattern("application*.yml")
                .registerPattern("application*.yaml")
                .registerPattern("application*.properties")
                .registerPattern("META-INF/spring.factories")
                .registerPattern("META-INF/spring/*")
                .registerPattern("META-INF/spring.components")
                .registerPattern("META-INF/services/com.datastax.oss.driver.*")
                .registerPattern("reference.conf");
            
            // JCTools reflection hints
            registerJCToolsHints(hints);
        }
        
        /**
         * Register JCTools specific hints for native image compatibility
         */
        private void registerJCToolsHints(RuntimeHints hints) {
            // Direct registration of problematic fields - no need for Class.forName
            // Register the JCTools classes in Netty using string-based registration
            
            // Registering exact problem class with producerIndex field directly
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerIndexField",
                    builder -> builder
                        .withMembers(MemberCategory.DECLARED_FIELDS)
                        .withField("producerIndex")) // Explicitly register the problematic field
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueue",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess", 
                    builder -> builder
                        .withMembers(
                            MemberCategory.DECLARED_FIELDS,
                            MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.INVOKE_PUBLIC_METHODS
                        )
                        .withField("UNSAFE") // Explicitly register UNSAFE field
                        .withMethod("fieldOffset", 
                            java.util.Arrays.asList(
                                org.springframework.aot.hint.TypeReference.of(Class.class), 
                                org.springframework.aot.hint.TypeReference.of(String.class)
                            ), 
                            org.springframework.aot.hint.ExecutableMode.INVOKE));
            
            // Additional Netty classes that might be needed
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.util.internal.PlatformDependent",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_PUBLIC_METHODS))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.buffer.PoolThreadCache",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_PUBLIC_METHODS))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.buffer.PoolThreadCache$MemoryRegionCache",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_PUBLIC_METHODS))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "io.netty.buffer.PoolThreadCache$SubPageMemoryRegionCache",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_PUBLIC_METHODS));
        }
        
        /**
         * Register Cassandra driver specific hints for native image compatibility
         */
        private void registerCassandraHints(RuntimeHints hints) {
            // Driver Core
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.context.DefaultDriverContext",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                    ))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.session.DefaultSession",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                    ));
            
            // Protocol handlers
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.protocol.FrameCodec",
                    builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.protocol.ProtocolV3ClientCodecs",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
                    ));
                    
            // Channel handlers
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.channel.ChannelFactory",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                    ))
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.channel.InFlightHandler",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                    ));
                    
            // Auth providers
            hints.reflection()
                .registerTypeIfPresent(getClass().getClassLoader(),
                    "com.datastax.oss.driver.internal.core.auth.PlainTextAuthProvider",
                    builder -> builder.withMembers(
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
                    ));
        }
    }
}
