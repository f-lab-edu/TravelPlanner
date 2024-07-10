package com.roamgram.travelDiary.common.swagger;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/authentication/**")
                .build();
    }

    @Bean
    public GroupedOpenApi secureApi() {
        return GroupedOpenApi.builder()
                .group("secure")
                .pathsToExclude("/authentication/**")
                .addOperationCustomizer(operationCustomizer())
                .build();
    }

    private OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            if (!operation.getTags().contains("public")) {
                operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            }
            return operation;
        };
    }
}