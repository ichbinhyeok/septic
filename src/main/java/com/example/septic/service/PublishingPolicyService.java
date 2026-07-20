package com.example.septic.service;

import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PublishingPolicyService {
    private static final List<String> WORKFLOW_COST_SLUGS = List.of(
            "septic-replacement-cost",
            "perc-test-cost",
            "septic-inspection-cost",
            "drain-field-replacement-cost",
            "septic-pumping-cost",
            "failed-perc-test-septic",
            "septic-replacement-area",
            "wet-yard-over-septic-drain-field"
    );
    /*
     * Reviewed against GSC on 2026-07-20. These states had observed perc-cost
     * demand or a live state perc page already earning impressions. Keep the
     * remaining routes useful and crawlable, but do not ask Google to index
     * fifty near-parallel pages before demand or direct cost evidence exists.
     */
    private static final Set<String> PERC_COST_DEMAND_STATES = Set.of(
            "AR", "GA", "MD", "MO", "NJ", "OH", "OK", "OR", "TN", "WI", "WV"
    );

    private final ResearchDataService researchDataService;

    public PublishingPolicyService(ResearchDataService researchDataService) {
        this.researchDataService = researchDataService;
    }

    public boolean isIndexableStateMoneyPage(StateMoneyPage stateMoneyPage, StateProfile state) {
        if (stateMoneyPage == null || state == null || !state.isPublished() || !stateMoneyPage.isPublished()) {
            return false;
        }
        return switch (stateMoneyPage.contentSlug()) {
            case "septic-records-checklist" -> hasRecordsSource(state)
                    || hasCountyRecordsPages(state.stateCode())
                    || hasLocalAuthoritySource(state);
            case "septic-permit-process" -> hasLocalAuthoritySource(state)
                    && hasItems(state.permitPathSteps(), 3);
            case "buying-a-house-with-a-septic-system" -> hasText(state.buyerInspectionTrigger())
                    && (hasCountyRecordsPages(state.stateCode())
                    || hasStateCostProfile(state.stateCode()));
            case "perc-test-cost" -> isCostReopenCandidate(stateMoneyPage, state)
                    && PERC_COST_DEMAND_STATES.contains(state.stateCode());
            case "septic-replacement-cost", "septic-inspection-cost" ->
                    isCostReopenCandidate(stateMoneyPage, state)
                            && hasStateCostProfile(state.stateCode());
            default -> {
                if (WORKFLOW_COST_SLUGS.contains(stateMoneyPage.contentSlug())) {
                    yield isCostReopenCandidate(stateMoneyPage, state);
                }
                yield true;
            }
        };
    }

    public boolean isCostReopenCandidate(StateMoneyPage stateMoneyPage, StateProfile state) {
        if (stateMoneyPage == null || state == null || !WORKFLOW_COST_SLUGS.contains(stateMoneyPage.contentSlug())) {
            return false;
        }
        return hasCostDecisionInputs(stateMoneyPage)
                && hasCostWorkflowEvidence(state);
    }

    public boolean allowDirectQuote(StateMoneyPage stateMoneyPage, StateProfile state) {
        if (!isIndexableStateMoneyPage(stateMoneyPage, state)) {
            return false;
        }
        return switch (stateMoneyPage.contentSlug()) {
            case "septic-replacement-cost", "drain-field-replacement-cost" ->
                    researchDataService.findStateCostProfile(state.stateCode()).isPresent();
            default -> false;
        };
    }

    private boolean hasCostDecisionInputs(StateMoneyPage stateMoneyPage) {
        return hasItems(stateMoneyPage.driverBullets(), 2)
                && hasItems(stateMoneyPage.lowEndBreakers(), 1)
                && hasItems(stateMoneyPage.quotePrepChecklist(), 1)
                && hasItems(stateMoneyPage.officialSourceIds(), 1);
    }

    private boolean hasCostWorkflowEvidence(StateProfile state) {
        return researchDataService.findStateCostProfile(state.stateCode()).isPresent()
                || hasCountyRecordsPages(state.stateCode())
                || hasRecordsSource(state)
                || hasLocalAuthoritySource(state)
                || hasText(state.siteEvalSummary())
                || hasText(state.permitSummary())
                || hasText(state.specialAreaNote())
                || hasText(state.maintenanceInspectionNote())
                || hasText(state.buyerInspectionTrigger());
    }

    private boolean hasLocalAuthoritySource(StateProfile state) {
        return hasItems(state.localAuthoritySourceIds(), 1);
    }

    private boolean hasRecordsSource(StateProfile state) {
        return hasItems(state.recordsLookupSourceIds(), 1);
    }

    private boolean hasCountyRecordsPages(String stateCode) {
        return !researchDataService.listPublicCountyRecordsPages(stateCode).isEmpty();
    }

    private boolean hasStateCostProfile(String stateCode) {
        return researchDataService.findStateCostProfile(stateCode).isPresent();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
