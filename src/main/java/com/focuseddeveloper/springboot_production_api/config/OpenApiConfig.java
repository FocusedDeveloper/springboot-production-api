package com.focuseddeveloper.springboot_production_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("School API")
                        .version("1.0")
                        .description("API documentation for the School API application. A simple Spring Boot application with REST endpoints for managing students and courses."))
        ;
    }
}
