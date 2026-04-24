package com.tasku.core.infrastructure.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.tasku.core")
@EntityScan(basePackages = "com.tasku.core.infrastructure.persistence.jpa.entity")
@EnableJpaRepositories(basePackages = "com.tasku.core.infrastructure.persistence.jpa.repository")
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
