package com.example.septic.service;

import java.util.Arrays;

public enum UsageProfile {
    FULL_TIME("full_time", "Full-time use"),
    SEASONAL("seasonal", "Seasonal or lighter use");

    private final String value;
    private final String label;

    UsageProfile(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static UsageProfile fromValue(String value) {
        return Arrays.stream(values())
                .filter(profile -> profile.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(FULL_TIME);
    }
}
