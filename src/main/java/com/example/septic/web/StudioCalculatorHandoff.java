package com.example.septic.web;

import com.example.septic.service.ProjectType;
import java.util.Arrays;

/** Carries planning inputs only: never an address, contact detail, or verified permit fact. */
public final class StudioCalculatorHandoff {
    private StudioCalculatorHandoff() {}

    public static String link(EstimateForm form) {
        var target = org.springframework.web.util.UriComponentsBuilder.fromPath("/design-preview/studio/intake/")
                .queryParam("from", "calculator");
        if (form.getStateCode() != null && !form.getStateCode().isBlank()) {
            target.queryParam("state", form.getStateCode());
        }
        target.queryParam("project", ProjectType.fromValue(form.getProjectType()).value())
                .queryParam("bedrooms", form.getBedrooms())
                .fragment("intake-form");
        return target.build().encode().toUriString();
    }

    public static void prefill(ClosingRiskCheckForm form, String project, String bedrooms) {
        var selected = Arrays.stream(ProjectType.values()).filter(p -> p.value().equals(project)).findFirst();
        if (selected.isEmpty()) return;
        String bedroomNote = "";
        if (bedrooms != null && bedrooms.matches("[0-9]{1,2}")) {
            int count = Integer.parseInt(bedrooms);
            if (count >= 1 && count <= 20) bedroomNote = " Planning input: " + count + " bedrooms (not verified against a permit).";
        }
        form.setConcern("Project: " + selected.get().label() + "." + bedroomNote
                + " Please research available septic records and explain what they establish for this project.");
        form.setRecordType("septic");
        form.setSourceContext("studio_calculator");
        form.setSourcePageHint("/design-preview/studio/calculator/");
        form.setEntryPageHint("/design-preview/studio/calculator/");
    }
}
