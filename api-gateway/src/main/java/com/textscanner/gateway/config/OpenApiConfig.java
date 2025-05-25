package com.textscanner.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    @ConditionalOnMissingBean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Text Scanner API")
                        .description("API Gateway for Text Scanner microservices")
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi storeApi() {
        return GroupedOpenApi.builder()
                .group("file-storing")
                .pathsToMatch("/api/files/**")
                .build();
    }

    @Bean
    public GroupedOpenApi analysisApi() {
        return GroupedOpenApi.builder()
                .group("file-analysis")
                .pathsToMatch("/api/analysis/**")
                .build();
    }
} 