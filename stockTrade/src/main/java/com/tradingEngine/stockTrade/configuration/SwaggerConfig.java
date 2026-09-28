package com.tradingEngine.stockTrade.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI mySwaggerOpenAPIConfig() {

        return new OpenAPI()
                // 1. App Info
                .info(new Info()
                        .title("Stock Trading Engine API")
                        .version("1.0")
                        .description("High-concurrency Order matching and Settlement engine built with Spring Boot.")
                .contact(new Contact()
                        .name("Yash")
                        .email("yashjadhav4883@gmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://springdoc.org")))
                         //Global JWT Security Setup for Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components( new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

}
