package com.example.septic.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.site")
public record AppSiteProperties(String baseUrl) {
    public AppSiteProperties {
        URI uri = URI.create(baseUrl);
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("app.site.base-url must be an absolute URL");
        }
    }

    public URI baseUri() {
        return URI.create(baseUrl);
    }
}
