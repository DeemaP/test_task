package com.scoring.test_task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scoringOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Organization Scoring API")
                        .description("Automated organization scoring using Camunda DMN and Elasticsearch." +
                                "\n\nTest project for Elfin company.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Dmitrii Pazinich")
                                .email("pazinich.d@yandex.ru")
                                .url("https://github.com/deemap")));
    }
}