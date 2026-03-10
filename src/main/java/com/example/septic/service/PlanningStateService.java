package com.example.septic.service;

import com.example.septic.data.model.StateProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PlanningStateService {
    private static final List<String> FALLBACK_OFFICIAL_SOURCE_IDS = List.of("nat_epa_01", "nat_epa_03");

    private final ResearchDataService researchDataService;
    private final UsStateDirectoryService usStateDirectoryService;

    public PlanningStateService(ResearchDataService researchDataService, UsStateDirectoryService usStateDirectoryService) {
        this.researchDataService = researchDataService;
        this.usStateDirectoryService = usStateDirectoryService;
    }

    public Optional<StateProfile> findPlanningStateByCode(String stateCode) {
        return researchDataService.findStateByCode(stateCode)
                .or(() -> usStateDirectoryService.findByCode(stateCode).map(this::fallbackStateProfile));
    }

    public StateProfile planningStateByCode(String stateCode) {
        return findPlanningStateByCode(stateCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateCode));
    }

    private StateProfile fallbackStateProfile(UsStateDirectoryService.UsStateReference stateReference) {
        String stateName = stateReference.stateName();
        String lastVerifiedAt = researchDataService.stateProfilesGeneratedAt();
        return new StateProfile(
                stateReference.stateCode(),
                stateName,
                "State guide research queue",
                "queue",
                "queued",
                stateName + " is still using a conservative national planning profile while the official-source state guide and local workflow map are being researched.",
                "research_queue",
                "A source-backed " + stateName + " septic guide is not published yet. This estimate uses national planning anchors and keeps the range conservative until the state and local workflow details are verified.",
                null,
                null,
                List.of(),
                "No state-specific garbage disposal sizing rule is published yet in the current " + stateName + " source set.",
                "Treat the local county, health department, or onsite wastewater authority as the first verification step until the public " + stateName + " guide is published.",
                List.of(
                        "Start with the local permitting or health authority before trusting the low end of the range.",
                        "Ask whether a permit file, as-built drawing, or inspection history already exists for the property.",
                        "Use this estimate as a planning range only until the " + stateName + " source set is published."
                ),
                "Local site evaluation, soil conditions, and permitting workflow are still the main reasons to keep the " + stateName + " range conservative.",
                List.of(
                        "The public " + stateName + " guide is still in the research queue, so this estimate uses generic homeowner-safe sizing logic.",
                        "Local permit path, records availability, and site constraints can change the real project scope quickly.",
                        "Use the state queue page and workflow pages before you treat the low end as real."
                ),
                "Local authority rules can materially change the next step because the public " + stateName + " source set is still being built.",
                "high",
                lastVerifiedAt,
                FALLBACK_OFFICIAL_SOURCE_IDS,
                List.of(),
                List.of(),
                List.of(
                        "Identify the local health, county, or onsite wastewater office first.",
                        "Ask whether permit, inspection, or repair records already exist.",
                        "Carry site unknowns into the estimate instead of assuming the cheapest visible range."
                ),
                "Start with the local permitting or health authority that handles septic or onsite wastewater review for the property.",
                List.of(
                        "Permit or installation file tied to the property",
                        "Any as-built drawing or repair history",
                        "Inspection, pumping, or system maintenance records if they exist"
                ),
                List.of(
                        "Unknown site and soil conditions can erase the low end quickly.",
                        "Missing records can hide a system type or repair history that changes the real scope.",
                        "Local permit path and restoration work often matter more than the tank number alone."
                ),
                "Treat local scheduling and permit review as the main timeline variable until the source-backed " + stateName + " guide is live.",
                "Buyers should pull permit, as-built, and inspection records early because the public " + stateName + " guide is still being verified.",
                "No state-specific homeowner inspection cadence has been published yet in the current " + stateName + " source set.",
                "This state is still in the research queue, so the practical differentiator is the local authority and records path rather than a published statewide sizing rule.",
                0.35,
                List.of(
                        "Find official statewide onsite wastewater or septic program sources",
                        "Find a local authority directory or permitting path",
                        "Find records lookup or file-request workflow"
                )
        );
    }
}
