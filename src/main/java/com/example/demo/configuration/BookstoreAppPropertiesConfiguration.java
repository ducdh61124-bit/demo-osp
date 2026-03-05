package com.example.demo.configuration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookstore.app")
public class BookstoreAppPropertiesConfiguration {
    private String name;
    private String verson;
    private String email;
    private boolean maintenanceMode;
}
