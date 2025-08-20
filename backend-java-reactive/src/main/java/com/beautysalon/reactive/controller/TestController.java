package com.beautysalon.reactive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test operations for SpringDoc validation")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Test endpoint", description = "Simple test endpoint to validate SpringDoc integration")
    @ApiResponse(responseCode = "200", description = "Successfully returned hello message")
    public Mono<String> hello() {
        return Mono.just("Hello from SpringDoc WebFlux!");
    }
}
