package com.example.septic.web;

import com.example.septic.config.AppSiteProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
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
        String redirectUrl = redirectUrl(request);
        if (redirectUrl == null) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    private String redirectUrl(HttpServletRequest request) {
        boolean redirectCanonicalOrigin = shouldRedirectCanonicalOrigin(request);
        boolean redirectTrailingSlash = shouldRedirectTrailingSlash(request);
        if (!redirectCanonicalOrigin && !redirectTrailingSlash) {
            return null;
        }

        return UriComponentsBuilder.newInstance()
                .scheme(canonicalScheme)
                .host(canonicalHost)
                .port(shouldIncludePort(canonicalScheme, canonicalUri.getPort()) ? canonicalUri.getPort() : -1)
                .path(redirectTrailingSlash ? request.getRequestURI() + "/" : request.getRequestURI())
                .query(request.getQueryString())
                .build(true)
                .toUriString();
    }

    private boolean shouldRedirectCanonicalOrigin(HttpServletRequest request) {
        String requestHost = request.getServerName();
        String effectiveScheme = originalScheme(request);
        if (!isCanonicalHost(requestHost) && !isEquivalentWwwVariant(requestHost)) {
            return false;
        }

        if (!canonicalScheme.equalsIgnoreCase(effectiveScheme)) {
            return true;
        }

        if (!isCanonicalHost(requestHost)) {
            return true;
        }

        return normalizedPort(originalPort(request, effectiveScheme), effectiveScheme) != canonicalPort;
    }

    private boolean shouldRedirectTrailingSlash(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !"HEAD".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String requestHost = request.getServerName();
        if (!isCanonicalHost(requestHost) && !isEquivalentWwwVariant(requestHost)) {
            return false;
        }

        String path = request.getRequestURI();
        if (path == null || path.isBlank() || "/".equals(path) || path.endsWith("/")) {
            return false;
        }

        if (path.startsWith("/events/") || "/quote-request".equals(path)) {
            return false;
        }

        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return !lastSegment.contains(".");
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

    private String originalScheme(HttpServletRequest request) {
        String forwardedProto = firstHeaderValue(request, "X-Forwarded-Proto");
        if (forwardedProto != null) {
            return forwardedProto.toLowerCase(Locale.ROOT);
        }

        String cfVisitor = request.getHeader("CF-Visitor");
        if (cfVisitor != null) {
            String normalized = cfVisitor.toLowerCase(Locale.ROOT);
            if (normalized.contains("\"scheme\":\"https\"")) {
                return "https";
            }
            if (normalized.contains("\"scheme\":\"http\"")) {
                return "http";
            }
        }

        return request.getScheme();
    }

    private int originalPort(HttpServletRequest request, String effectiveScheme) {
        String forwardedPort = firstHeaderValue(request, "X-Forwarded-Port");
        if (forwardedPort != null) {
            try {
                return Integer.parseInt(forwardedPort);
            } catch (NumberFormatException ignored) {
                return normalizedPort(-1, effectiveScheme);
            }
        }
        if (!effectiveScheme.equalsIgnoreCase(request.getScheme())) {
            return normalizedPort(-1, effectiveScheme);
        }
        return request.getServerPort();
    }

    private String firstHeaderValue(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.split(",")[0].trim();
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
