package com.example.septic.service;

import java.util.Arrays;

public enum ProjectType {
    NEW_INSTALL("new_install", "New install / permit path"),
    REPLACEMENT("replacement", "Full replacement"),
    DRAINFIELD_REPLACEMENT("drainfield_replacement", "Drain field / leach field"),
    PERC_TEST("perc_test", "Perc / site test"),
    PUMPING("pumping", "Pumping / maintenance"),
    INSPECTION("inspection", "Inspection / records review"),
    BUYING_HOME("buying_home", "Buying / transfer diligence");

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
