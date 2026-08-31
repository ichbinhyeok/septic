package com.example.septic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.closing-risk-notification")
public record ClosingRiskNotificationProperties(String sender, String recipient) {
    public boolean isConfigured() {
        return sender != null && !sender.isBlank() && recipient != null && !recipient.isBlank();
    }
}
