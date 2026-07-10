package com.example.septic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ops")
public record AppOpsProperties(String reportUsername, String reportPassword) {
    public boolean reportAccessConfigured() {
        return reportUsername != null && !reportUsername.isBlank()
                && reportPassword != null && !reportPassword.isBlank();
    }
}
