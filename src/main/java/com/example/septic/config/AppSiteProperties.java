package com.example.septic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.site")
public record AppSiteProperties(String baseUrl) {
}
