package com.textscanner.filestore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class HomeController {
    
    @GetMapping("/error")
    public String home() {
        return "File Storage Service is running";
    }
} 