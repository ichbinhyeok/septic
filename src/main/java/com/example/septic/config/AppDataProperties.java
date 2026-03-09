package com.example.septic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.data")
public record AppDataProperties(String root) {
}
