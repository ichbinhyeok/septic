package com.example.septic.web;

import com.example.septic.data.model.CountyRecordsPage;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Joins the official access route and the preparation profile at one boundary.
 * Catalog entries remain deliberately hand-reviewed; this registry prevents the
 * two layers from silently drifting apart as county coverage expands.
 */
public final class CountyWorkflowRegistry {

    private static final Pattern TEMPLATE_TOKEN = Pattern.compile("\\{\\{([A-Za-z][A-Za-z0-9]*)}}");
    private static final Set<String> SHARED_TOKENS = Set.of("address", "parcel", "documents");

    static {
        validateCatalogs();
    }

    private CountyWorkflowRegistry() {
    }

    public static CountyWorkflowProfileView findOrBaseline(CountyRecordsPage page) {
        return new CountyWorkflowProfileView(
                CountyAccessProfileCatalog.findOrBaseline(page),
                CountyAcquisitionProfileCatalog.find(page.key())
        );
    }

    static void validateCatalogs() {
        for (CountyAcquisitionProfileView acquisition : CountyAcquisitionProfileCatalog.all()) {
            CountyAccessProfileView access = CountyAccessProfileCatalog.find(acquisition.countyKey());
            if (access == null) {
                throw new IllegalStateException("Acquisition profile has no county access route: "
                        + acquisition.countyKey());
            }
            if (!acquisition.countyKey().equals(access.countyKey())) {
                throw new IllegalStateException("County workflow key mismatch: " + acquisition.countyKey());
            }
            validateFields(acquisition);
            if (acquisition.generatedRequestEnabled()) {
                validateTemplateTokens(acquisition);
            }
        }
    }

    private static void validateFields(CountyAcquisitionProfileView profile) {
        Set<String> keys = new HashSet<>();
        for (CountyAcquisitionFieldView field : profile.fields()) {
            if (field.key() == null || field.key().isBlank() || field.label() == null || field.label().isBlank()) {
                throw new IllegalStateException("Blank acquisition field key or label: " + profile.countyKey());
            }
            if (!keys.add(field.key())) {
                throw new IllegalStateException("Duplicate acquisition field key " + field.key()
                        + " in " + profile.countyKey());
            }
        }
        if (profile.requestedDocuments() == null || profile.requestedDocuments().isEmpty()) {
            throw new IllegalStateException("County workflow has no expected document outcome: "
                    + profile.countyKey());
        }
    }

    private static void validateTemplateTokens(CountyAcquisitionProfileView profile) {
        Set<String> allowed = new HashSet<>(SHARED_TOKENS);
        profile.fields().stream().map(CountyAcquisitionFieldView::key).forEach(allowed::add);
        Matcher matcher = TEMPLATE_TOKEN.matcher(profile.requestTemplate());
        while (matcher.find()) {
            if (!allowed.contains(matcher.group(1))) {
                throw new IllegalStateException("Unknown request-template token " + matcher.group()
                        + " in " + profile.countyKey());
            }
        }
    }
}
