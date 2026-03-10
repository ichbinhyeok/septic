package com.example.septic.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UsStateDirectoryService {
    private static final List<UsStateReference> ALL_STATES = List.of(
            new UsStateReference("AL", "Alabama"),
            new UsStateReference("AK", "Alaska"),
            new UsStateReference("AZ", "Arizona"),
            new UsStateReference("AR", "Arkansas"),
            new UsStateReference("CA", "California"),
            new UsStateReference("CO", "Colorado"),
            new UsStateReference("CT", "Connecticut"),
            new UsStateReference("DE", "Delaware"),
            new UsStateReference("FL", "Florida"),
            new UsStateReference("GA", "Georgia"),
            new UsStateReference("HI", "Hawaii"),
            new UsStateReference("ID", "Idaho"),
            new UsStateReference("IL", "Illinois"),
            new UsStateReference("IN", "Indiana"),
            new UsStateReference("IA", "Iowa"),
            new UsStateReference("KS", "Kansas"),
            new UsStateReference("KY", "Kentucky"),
            new UsStateReference("LA", "Louisiana"),
            new UsStateReference("ME", "Maine"),
            new UsStateReference("MD", "Maryland"),
            new UsStateReference("MA", "Massachusetts"),
            new UsStateReference("MI", "Michigan"),
            new UsStateReference("MN", "Minnesota"),
            new UsStateReference("MS", "Mississippi"),
            new UsStateReference("MO", "Missouri"),
            new UsStateReference("MT", "Montana"),
            new UsStateReference("NE", "Nebraska"),
            new UsStateReference("NV", "Nevada"),
            new UsStateReference("NH", "New Hampshire"),
            new UsStateReference("NJ", "New Jersey"),
            new UsStateReference("NM", "New Mexico"),
            new UsStateReference("NY", "New York"),
            new UsStateReference("NC", "North Carolina"),
            new UsStateReference("ND", "North Dakota"),
            new UsStateReference("OH", "Ohio"),
            new UsStateReference("OK", "Oklahoma"),
            new UsStateReference("OR", "Oregon"),
            new UsStateReference("PA", "Pennsylvania"),
            new UsStateReference("RI", "Rhode Island"),
            new UsStateReference("SC", "South Carolina"),
            new UsStateReference("SD", "South Dakota"),
            new UsStateReference("TN", "Tennessee"),
            new UsStateReference("TX", "Texas"),
            new UsStateReference("UT", "Utah"),
            new UsStateReference("VT", "Vermont"),
            new UsStateReference("VA", "Virginia"),
            new UsStateReference("WA", "Washington"),
            new UsStateReference("WV", "West Virginia"),
            new UsStateReference("WI", "Wisconsin"),
            new UsStateReference("WY", "Wyoming")
    );

    public List<UsStateReference> allStates() {
        return ALL_STATES;
    }

    public Optional<UsStateReference> findByCode(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return Optional.empty();
        }
        String normalizedStateCode = stateCode.toUpperCase(Locale.US);
        return ALL_STATES.stream()
                .filter(state -> state.stateCode().equals(normalizedStateCode))
                .findFirst();
    }

    public Optional<UsStateReference> findBySlug(String stateSlug) {
        if (stateSlug == null || stateSlug.isBlank()) {
            return Optional.empty();
        }
        String normalizedStateSlug = stateSlug.toLowerCase(Locale.US);
        return ALL_STATES.stream()
                .filter(state -> state.slug().equals(normalizedStateSlug))
                .findFirst();
    }

    public record UsStateReference(String stateCode, String stateName) {
        public String slug() {
            return stateName.toLowerCase(Locale.US).replace(" ", "-");
        }
    }
}
