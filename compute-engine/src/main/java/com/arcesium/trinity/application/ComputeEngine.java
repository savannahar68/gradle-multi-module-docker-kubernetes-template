package com.example.org.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication(scanBasePackages = "com.example.org")
@Service
public class ComputeEngine {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ComputeEngine.class, args);
    }
}
