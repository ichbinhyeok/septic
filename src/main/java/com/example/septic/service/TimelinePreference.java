package com.example.septic.service;

import java.util.Arrays;

public enum TimelinePreference {
    THIS_MONTH("this_month", "This month"),
    ONE_TO_THREE_MONTHS("one_to_three_months", "1-3 months"),
    RESEARCHING("researching", "Researching");

    private final String value;
    private final String label;

    TimelinePreference(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static TimelinePreference fromValue(String value) {
        return Arrays.stream(values())
                .filter(timeline -> timeline.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(RESEARCHING);
    }
}
