package com.example.septic.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.paid-unlock")
public record PaidUnlockProperties(
        boolean enabled,
        String environment,
        String clientId,
        String clientSecret,
        String webhookId,
        int downloadTtlHours,
        int maxDownloads
) {
    public PaidUnlockProperties {
        environment = environment == null ? "sandbox" : environment.trim().toLowerCase();
        if (!environment.equals("sandbox") && !environment.equals("live")) {
            throw new IllegalArgumentException("app.paid-unlock.environment must be sandbox or live");
        }
        downloadTtlHours = downloadTtlHours <= 0 ? 168 : downloadTtlHours;
        maxDownloads = maxDownloads <= 0 ? 5 : maxDownloads;
    }

    public boolean isConfigured() {
        return enabled
                && clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank()
                && webhookId != null && !webhookId.isBlank();
    }

    public String apiBaseUrl() {
        return environment.equals("live")
                ? "https://api-m.paypal.com"
                : "https://api-m.sandbox.paypal.com";
    }

    public String javascriptSdkUrl() {
        String host = environment.equals("live") ? "https://www.paypal.com" : "https://www.sandbox.paypal.com";
        return host + "/sdk/js?client-id="
                + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&currency=USD&intent=capture";
    }
}
