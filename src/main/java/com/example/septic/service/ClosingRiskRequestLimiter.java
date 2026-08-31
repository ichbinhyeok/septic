package com.example.septic.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ClosingRiskRequestLimiter {
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_TRACKED_CLIENTS = 10_000;
    private static final Duration WINDOW = Duration.ofHours(1);
    private final Map<String, ArrayDeque<Instant>> attempts = new ConcurrentHashMap<>();

    public boolean allow(HttpServletRequest request) {
        Instant now = Instant.now();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String key = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.substring(forwardedFor.lastIndexOf(',') + 1).trim();
        if (key == null || !key.matches("[0-9A-Fa-f:.]{1,64}")) {
            key = request.getRemoteAddr();
        }
        if (key == null || !key.matches("[0-9A-Fa-f:.]{1,64}")) {
            key = "unknown";
        }
        if (!attempts.containsKey(key) && attempts.size() >= MAX_TRACKED_CLIENTS) {
            discardExpiredClientHistories(now);
            if (attempts.size() >= MAX_TRACKED_CLIENTS) {
                return false;
            }
        }
        ArrayDeque<Instant> history = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (history) {
            Instant cutoff = now.minus(WINDOW);
            while (!history.isEmpty() && history.peekFirst().isBefore(cutoff)) {
                history.removeFirst();
            }
            if (history.size() >= MAX_REQUESTS_PER_HOUR) {
                return false;
            }
            history.addLast(now);
            return true;
        }
    }

    private void discardExpiredClientHistories(Instant now) {
        Instant cutoff = now.minus(WINDOW);
        attempts.forEach((key, history) -> {
            synchronized (history) {
                while (!history.isEmpty() && history.peekFirst().isBefore(cutoff)) {
                    history.removeFirst();
                }
                if (history.isEmpty()) {
                    attempts.remove(key, history);
                }
            }
        });
    }
}
