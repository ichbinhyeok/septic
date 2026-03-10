package com.example.septic.web;

import com.example.septic.config.AppSiteProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CanonicalHostFilter extends OncePerRequestFilter {
    private final URI canonicalUri;
    private final String canonicalScheme;
    private final String canonicalHost;
    private final int canonicalPort;

    public CanonicalHostFilter(AppSiteProperties siteProperties) {
        this.canonicalUri = siteProperties.baseUri();
        this.canonicalScheme = canonicalUri.getScheme();
        this.canonicalHost = canonicalUri.getHost();
        this.canonicalPort = normalizedPort(canonicalUri.getPort(), canonicalScheme);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!shouldRedirect(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String redirectUrl = UriComponentsBuilder.newInstance()
                .scheme(canonicalScheme)
                .host(canonicalHost)
                .port(shouldIncludePort(canonicalScheme, canonicalUri.getPort()) ? canonicalUri.getPort() : -1)
                .path(request.getRequestURI())
                .query(request.getQueryString())
                .build(true)
                .toUriString();

        response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    private boolean shouldRedirect(HttpServletRequest request) {
        String requestHost = request.getServerName();
        if (!isCanonicalHost(requestHost) && !isEquivalentWwwVariant(requestHost)) {
            return false;
        }

        if (!canonicalScheme.equalsIgnoreCase(request.getScheme())) {
            return true;
        }

        if (!isCanonicalHost(requestHost)) {
            return true;
        }

        return normalizedPort(request.getServerPort(), request.getScheme()) != canonicalPort;
    }

    private boolean isCanonicalHost(String host) {
        return host != null && canonicalHost.equalsIgnoreCase(host);
    }

    private boolean isEquivalentWwwVariant(String host) {
        if (host == null) {
            return false;
        }
        return stripWww(host).equalsIgnoreCase(stripWww(canonicalHost));
    }

    private String stripWww(String host) {
        return host.toLowerCase().startsWith("www.") ? host.substring(4) : host;
    }

    private int normalizedPort(int port, String scheme) {
        if (port > 0) {
            return port;
        }
        return switch (scheme) {
            case "https" -> 443;
            case "http" -> 80;
            default -> -1;
        };
    }

    private boolean shouldIncludePort(String scheme, int port) {
        if (port < 0) {
            return false;
        }
        return !("https".equalsIgnoreCase(scheme) && port == 443)
                && !("http".equalsIgnoreCase(scheme) && port == 80);
    }
}
