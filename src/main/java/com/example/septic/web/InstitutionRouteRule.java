package com.example.septic.web;

import java.util.List;

/**
 * A verified custody branch used by an institution-led record workspace.
 * Empty area and requester-relation lists make a rule the state default.
 */
public record InstitutionRouteRule(
        String key,
        List<String> areaMatches,
        String requesterRelation,
        Integer minimumYear,
        Integer maximumYear,
        String custodian,
        String routeNote,
        String primaryUrl,
        String primaryLabel,
        String secondaryUrl,
        String secondaryLabel,
        String requestRecipient,
        String contactMode,
        String artifactHeading,
        String artifactLabel
) {}
