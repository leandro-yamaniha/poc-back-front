package com.beautysalon.config;

import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

/**
 * Configuration for Virtual Threads in Spring Boot WebFlux with Java 21+
 * 
 * Virtual Threads provide lightweight concurrency with minimal memory overhead
 * and improved scalability for I/O-bound operations in reactive applications.
 */
@Configuration
@EnableAsync
public class VirtualThreadsConfig {

    /**
     * Configure Undertow to use Virtual Threads for blocking operations
     * Note: WebFlux is already non-blocking, but Virtual Threads help with
     * any blocking operations that might occur in the reactive chain
     */
    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        UndertowReactiveWebServerFactory factory = new UndertowReactiveWebServerFactory();
        
        // Configure Undertow worker options for better performance
        factory.addBuilderCustomizers(builder -> {
            builder.setWorkerOption(org.xnio.Options.WORKER_TASK_CORE_THREADS, 1);
            builder.setWorkerOption(org.xnio.Options.WORKER_TASK_MAX_THREADS, 1000);
            builder.setWorkerOption(org.xnio.Options.WORKER_TASK_KEEPALIVE, 60000);
        });
        
        return factory;
    }

    /**
     * Virtual Thread Task Executor for async operations
     * This is useful for any blocking operations that need to be performed
     * outside the reactive pipeline
     */
    @Bean("virtualThreadTaskExecutor")
    public AsyncTaskExecutor virtualThreadTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Virtual Thread Executor for general purpose async tasks
     * Can be used with @Async annotation or injected directly
     */
    @Bean("virtualThreadExecutor")
    public java.util.concurrent.ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Scheduler for reactive operations that need to switch to Virtual Threads
     * This allows reactive chains to offload blocking operations to Virtual Threads
     */
    @Bean("virtualThreadScheduler")
    public reactor.core.scheduler.Scheduler virtualThreadScheduler() {
        return reactor.core.scheduler.Schedulers.fromExecutor(virtualThreadExecutor());
    }
}
