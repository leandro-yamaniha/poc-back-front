package com.beautysalon.reactive.config;

import io.netty.channel.Channel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Runtime Hints for Project Reactor and Netty
 * Ensures reactive types are available for reflection in native image
 */
@Configuration
@ImportRuntimeHints(ReactorRuntimeHints.ReactorHintsRegistrar.class)
public class ReactorRuntimeHints {

    /**
     * Registrar for Reactor and Netty runtime hints
     */
    static class ReactorHintsRegistrar implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register Reactor types
            registerReactorTypes(hints);
            
            // Register Netty types
            registerNettyTypes(hints);
            
            // Register Netty resources
            registerNettyResources(hints);
        }
        
        /**
         * Register Project Reactor types for reflection
         */
        private void registerReactorTypes(RuntimeHints hints) {
            // Mono
            hints.reflection()
                .registerType(Mono.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
            
            // Flux
            hints.reflection()
                .registerType(Flux.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        }
        
        /**
         * Register Netty types for reflection
         */
        private void registerNettyTypes(RuntimeHints hints) {
            // Channel
            hints.reflection()
                .registerType(Channel.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        }
        
        /**
         * Register Netty configuration resources
         */
        private void registerNettyResources(RuntimeHints hints) {
            hints.resources()
                .registerPattern("META-INF/native-image/io.netty/**/*")
                .registerPattern("META-INF/native-image/reactor/**/*");
        }
    }
}
