package com.example.septic.service;

import java.util.Arrays;

public enum ProjectType {
    NEW_INSTALL("new_install", "New install"),
    REPLACEMENT("replacement", "Replace existing"),
    DRAINFIELD_REPLACEMENT("drainfield_replacement", "Drain field replacement"),
    PERC_TEST("perc_test", "Perc test"),
    PUMPING("pumping", "Pumping"),
    INSPECTION("inspection", "Inspection"),
    BUYING_HOME("buying_home", "Buying a home");

    private final String value;
    private final String label;

    ProjectType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static ProjectType fromValue(String value) {
        return Arrays.stream(values())
                .filter(projectType -> projectType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(NEW_INSTALL);
    }
}
