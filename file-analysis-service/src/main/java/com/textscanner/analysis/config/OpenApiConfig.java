package com.textscanner.analysis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI fileAnalysisOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Analysis Service API")
                        .description("Service for analyzing text files and detecting plagiarism")
                        .version("1.0"));
    }
} 