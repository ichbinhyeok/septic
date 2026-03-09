package com.example.septic.service;

import java.util.Arrays;

public enum OccupancyProfile {
    BALANCED("balanced", "Balanced household"),
    HIGH("high", "High occupancy"),
    SEASONAL("seasonal", "Seasonal or lighter use");

    private final String value;
    private final String label;

    OccupancyProfile(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static OccupancyProfile fromValue(String value) {
        return Arrays.stream(values())
                .filter(profile -> profile.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(BALANCED);
    }
}
