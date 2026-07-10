package com.example.septic.web;

import com.example.septic.config.AppOpsProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OpsReportAccessFilter extends OncePerRequestFilter {
    private static final String REPORT_PATH = "/ops/event-report";

    private final AppOpsProperties opsProperties;

    public OpsReportAccessFilter(AppOpsProperties opsProperties) {
        this.opsProperties = opsProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !REPORT_PATH.equals(path) && !(REPORT_PATH + "/").equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!opsProperties.reportAccessConfigured()) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return;
        }
        if (!hasValidCredentials(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"SepticPath Ops\", charset=\"UTF-8\"");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            return;
        }
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("X-Robots-Tag", "noindex, nofollow, noarchive");
        filterChain.doFilter(request, response);
    }

    private boolean hasValidCredentials(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            return false;
        }
        try {
            String credentials = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8);
            int separator = credentials.indexOf(':');
            if (separator < 0) {
                return false;
            }
            byte[] suppliedUsername = credentials.substring(0, separator).getBytes(StandardCharsets.UTF_8);
            byte[] suppliedPassword = credentials.substring(separator + 1).getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(suppliedUsername, opsProperties.reportUsername().getBytes(StandardCharsets.UTF_8))
                    && MessageDigest.isEqual(suppliedPassword, opsProperties.reportPassword().getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
