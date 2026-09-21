package com.example.septic.web;

import java.util.List;

public record InstitutionRecordsExperience(
        String stateCode,
        String agencyLabel,
        String programLabel,
        String heroTitle,
        String heroIntro,
        String outcomePromise,
        String officialUrl,
        String officialLabel,
        String fallbackUrl,
        String fallbackLabel,
        String areaLabel,
        String areaPlaceholder,
        String parcelLabel,
        String parcelPlaceholder,
        String ownerPlaceholder,
        String yearPlaceholder,
        String requestRecipient,
        String requestSubject,
        String searchInstruction,
        String noResultExplanation,
        String proofTitle,
        String proofBody,
        String heroImage,
        List<String> routeSteps,
        List<String> documents,
        List<InstitutionRouteRule> routeRules
) {}
