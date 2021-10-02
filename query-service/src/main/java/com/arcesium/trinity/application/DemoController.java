package com.example.org.application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.example.org")
@RestController
public class DemoController {

    @GetMapping("/")
    public String home() {
        return "Hello Ignite";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoController.class, args);
    }
}
