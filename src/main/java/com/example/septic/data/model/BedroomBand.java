package com.example.septic.data.model;

import java.util.List;

public record BedroomBand(
        String label,
        Integer minBedrooms,
        Integer maxBedrooms,
        Integer minTankGallons,
        List<String> sourceIds,
        String verificationStatus
) {
    public boolean matches(int bedrooms) {
        boolean meetsMin = minBedrooms == null || bedrooms >= minBedrooms;
        boolean meetsMax = maxBedrooms == null || bedrooms <= maxBedrooms;
        return meetsMin && meetsMax;
    }
}
