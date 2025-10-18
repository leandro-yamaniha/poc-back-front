package com.beautysalon.reactive.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI beautySalonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beauty Salon Management API")
                        .description("Reactive REST API for Beauty Salon Management System built with Spring Boot WebFlux")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Beauty Salon Team")
                                .email("contact@beautysalon.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.beautysalon.com")
                                .description("Production Server")
                ));
    }
}
