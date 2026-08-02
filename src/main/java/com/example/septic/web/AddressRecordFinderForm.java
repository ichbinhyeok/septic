package com.example.septic.web;

import java.util.Locale;
import java.util.Set;

public record AddressRecordFinderForm(String address) {
    private static final Set<String> STATE_CODES = Set.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
            "DC"
    );

    public boolean isUsable() {
        String value = normalizedAddress();
        if (value.length() < 8 || value.length() > 180 || !value.matches(".*\\d.*") || !value.matches(".*[A-Za-z].*")) {
            return false;
        }

        String upper = value.toUpperCase(Locale.US);
        boolean hasState = STATE_CODES.stream().anyMatch(code -> upper.matches(".*(?:^|[\\s,])" + code + "(?:[\\s,]|$).*"));
        boolean hasZip = upper.matches(".*\\b\\d{5}(?:-\\d{4})?\\s*$");
        long commas = value.chars().filter(character -> character == ',').count();
        int words = value.split("\\s+").length;
        boolean hasLocalityShape = commas >= 2 || words >= 5;
        return hasLocalityShape && (hasState || hasZip);
    }

    public String normalizedAddress() {
        return address == null ? "" : address.trim();
    }
}
