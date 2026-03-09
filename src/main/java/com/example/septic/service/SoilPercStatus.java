package com.example.septic.service;

import java.util.Arrays;

public enum SoilPercStatus {
    UNKNOWN("unknown", "Unknown"),
    PASSED("passed", "Passed"),
    FAILED("failed", "Failed"),
    POOR_DRAINAGE("poor_drainage", "Poor drainage suspected");

    private final String value;
    private final String label;

    SoilPercStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static SoilPercStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
