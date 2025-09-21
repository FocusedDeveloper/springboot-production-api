package com.focuseddeveloper.springboot_production_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
        .components(new Components().addSecuritySchemes("bearer-jwt", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("School API")
                        .version("1.0")
                        .description("API documentation for the School API application. A simple Spring Boot application with REST endpoints for managing students and courses."))
        ;
    }
}
