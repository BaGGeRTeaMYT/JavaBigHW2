package com.textscanner.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FileAnalysisServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileAnalysisServiceApplication.class, args);
    }
} 