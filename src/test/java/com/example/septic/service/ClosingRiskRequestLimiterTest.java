package com.example.septic.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClosingRiskRequestLimiterTest {

    @Test
    void limitsEachForwardedVisitorWithoutTreatingTheProxyAsEveryVisitor() {
        ClosingRiskRequestLimiter limiter = new ClosingRiskRequestLimiter();
        MockHttpServletRequest firstVisitor = requestFrom("198.51.100.10");
        for (int attempt = 0; attempt < 5; attempt++) {
            assertTrue(limiter.allow(firstVisitor));
        }
        assertFalse(limiter.allow(firstVisitor));
        assertTrue(limiter.allow(requestFrom("198.51.100.11")));
    }

    private MockHttpServletRequest requestFrom(String address) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "192.0.2.44, " + address);
        return request;
    }
}
