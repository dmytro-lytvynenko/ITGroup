package com.example.rmi.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomiser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomiser customOpenApi() {
        return openApi -> {
            openApi.info(new Info().title("Example API")
                                    .version("1.0")
                                    .description("This is a sample Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
        };
    }
}
