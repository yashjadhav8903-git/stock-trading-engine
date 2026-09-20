package com.tradingEngine.stockTrade.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

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
                        .url("https://springdoc.org")));
    }

}
