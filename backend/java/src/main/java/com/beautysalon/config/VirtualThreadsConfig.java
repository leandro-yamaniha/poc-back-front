package com.beautysalon.config;

import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executors;

/**
 * Configuration for Virtual Threads in Spring Boot 3.x with Java 21+
 * 
 * Virtual Threads provide lightweight concurrency with minimal memory overhead
 * and improved scalability for I/O-bound operations.
 */
@Configuration
@EnableAsync
public class VirtualThreadsConfig implements WebMvcConfigurer {

    /**
     * Configure Tomcat to use Virtual Threads for request processing
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }

    /**
     * Virtual Thread Task Executor for async operations
     */
    @Bean("virtualThreadTaskExecutor")
    public AsyncTaskExecutor virtualThreadTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Configure async support with Virtual Threads
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(virtualThreadTaskExecutor());
        configurer.setDefaultTimeout(30000); // 30 seconds timeout
    }

    /**
     * Virtual Thread Executor for general purpose async tasks
     */
    @Bean("virtualThreadExecutor")
    public java.util.concurrent.ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
