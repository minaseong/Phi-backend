package com.backend.phi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.backend.phi.entity")
@EnableJpaRepositories("com.backend.phi.repository")

public class PhiApplication {
    static void main(String[] args) {
        SpringApplication.run(PhiApplication.class, args);
    }
}