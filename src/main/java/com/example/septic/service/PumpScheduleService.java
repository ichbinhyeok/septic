package com.example.septic.service;

import com.example.septic.data.model.SourceRecord;
import com.example.septic.web.PumpScheduleForm;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PumpScheduleService {
    private final ResearchDataService researchDataService;

    public PumpScheduleService(ResearchDataService researchDataService) {
        this.researchDataService = researchDataService;
    }

    public PumpScheduleResult estimate(PumpScheduleForm form) {
        int tankSize = normalizeTankSize(form.getTankSizeGallons());
        int occupants = Math.max(1, form.getOccupants() == null ? 3 : form.getOccupants());
        UsageProfile usageProfile = UsageProfile.fromValue(form.getUsageProfile());

        int effectiveLoad = occupants;
        if (form.isGarbageDisposal()) {
            effectiveLoad += 1;
        }
        if (usageProfile == UsageProfile.SEASONAL) {
            effectiveLoad = Math.max(1, effectiveLoad - 1);
        }

        double gallonsPerPerson = tankSize / (double) effectiveLoad;

        String pumpingCadence;
        if (gallonsPerPerson < 260) {
            pumpingCadence = "About every 2 to 3 years";
        } else if (gallonsPerPerson < 360) {
            pumpingCadence = "About every 3 to 4 years";
        } else {
            pumpingCadence = "About every 4 to 5 years";
        }

        String inspectionCadence;
        if (usageProfile == UsageProfile.SEASONAL) {
            inspectionCadence = "Do a homeowner check at least yearly and plan a professional inspection about every 2 to 3 years.";
        } else if (form.isGarbageDisposal() || occupants >= 5) {
            inspectionCadence = "Do a homeowner check yearly and plan a professional inspection about every 1 to 2 years.";
        } else {
            inspectionCadence = "Do a homeowner check yearly and plan a professional inspection about every 2 years.";
        }

        List<String> drivers = new ArrayList<>();
        drivers.add("Tank size and occupant load are the biggest pumping-cadence drivers.");
        if (form.isGarbageDisposal()) {
            drivers.add("Garbage disposal use can shorten the pumping interval because more solids reach the tank.");
        }
        if (usageProfile == UsageProfile.SEASONAL) {
            drivers.add("Seasonal use can lengthen the pumping interval slightly, but it should not replace annual checks.");
        } else {
            drivers.add("Full-time use keeps the system under steady load, so delayed pumping is riskier.");
        }

        List<String> sourceLabels = researchDataService.getSources(List.of("nat_epa_01", "cost_ha_01")).stream()
                .map(this::formatSourceLabel)
                .toList();

        return new PumpScheduleResult(
                tankSize,
                occupants,
                usageProfile.label(),
                inspectionCadence,
                pumpingCadence,
                "Routine pumping is usually a lower-cost maintenance item than replacement. Current public anchors still cluster around roughly $250 to $650 per pumping visit, with inspection costs often separate.",
                "This cadence is a homeowner planning range. Local system type, inspection rules, and actual sludge accumulation can change the final schedule.",
                drivers,
                sourceLabels
        );
    }

    private int normalizeTankSize(Integer tankSizeGallons) {
        int raw = tankSizeGallons == null ? 1000 : tankSizeGallons;
        if (raw <= 1000) {
            return 1000;
        }
        if (raw <= 1250) {
            return 1250;
        }
        if (raw <= 1500) {
            return 1500;
        }
        return 1750;
    }

    private String formatSourceLabel(SourceRecord sourceRecord) {
        return sourceRecord.agencyName() + " - " + sourceRecord.title();
    }
}
