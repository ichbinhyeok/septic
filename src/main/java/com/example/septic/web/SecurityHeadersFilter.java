package com.example.septic.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {
    private static final String CONTENT_SECURITY_POLICY = String.join("; ",
            "default-src 'self'",
            "base-uri 'self'",
            "object-src 'none'",
            "frame-ancestors 'none'",
            "form-action 'self'",
            "img-src 'self' data: https:",
            "font-src 'self' https://fonts.gstatic.com",
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
            "style-src-attr 'unsafe-inline'",
            "script-src 'self' 'unsafe-inline' https://www.googletagmanager.com https://www.google-analytics.com https://static.cloudflareinsights.com",
            "script-src-attr 'unsafe-inline'",
            "connect-src 'self' https://www.google-analytics.com https://region1.google-analytics.com https://www.google.com https://www.googletagmanager.com https://cloudflareinsights.com https://static.cloudflareinsights.com"
    );
    private static final String EMBED_CONTENT_SECURITY_POLICY = CONTENT_SECURITY_POLICY.replace(
            "frame-ancestors 'none'", "frame-ancestors *"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=(), payment=()");
        if (isEmbeddableChecker(request)) {
            response.setHeader("Content-Security-Policy", EMBED_CONTENT_SECURITY_POLICY);
        } else {
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        }
        if ("https".equals(originalScheme(request))) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        filterChain.doFilter(request, response);
        forwardEmptyHtmlNotFound(request, response);
    }

    private void forwardEmptyHtmlNotFound(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (response.getStatus() != HttpStatus.NOT_FOUND.value()
                || response.isCommitted()
                || response.getContentType() != null
                || !isHtmlNavigation(request)
                || request.getRequestURI().contains(".")) {
            return;
        }

        response.resetBuffer();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value());
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
        request.getRequestDispatcher("/error").forward(request, response);
    }

    private boolean isHtmlNavigation(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !"HEAD".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String accept = request.getHeader("Accept");
        return accept == null
                || accept.isBlank()
                || accept.contains("text/html")
                || accept.contains("*/*");
    }

    private boolean isEmbeddableChecker(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/embed/septic-bedroom-permit-checker".equals(path)
                || "/embed/septic-bedroom-permit-checker/".equals(path)
                || "/embed/septic-record-finder".equals(path)
                || "/embed/septic-record-finder/".equals(path);
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

    private String firstHeaderValue(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.split(",")[0].trim();
    }
}
