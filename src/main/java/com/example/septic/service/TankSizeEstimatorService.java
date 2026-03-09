package com.example.septic.service;

import com.example.septic.web.EstimateForm;
import com.example.septic.web.PumpScheduleForm;
import com.example.septic.web.TankSizeForm;
import org.springframework.stereotype.Service;

@Service
public class TankSizeEstimatorService {
    private final EstimatorService estimatorService;
    private final PumpScheduleService pumpScheduleService;

    public TankSizeEstimatorService(EstimatorService estimatorService, PumpScheduleService pumpScheduleService) {
        this.estimatorService = estimatorService;
        this.pumpScheduleService = pumpScheduleService;
    }

    public TankSizeEstimatorResult estimate(TankSizeForm form) {
        OccupancyProfile occupancyProfile = OccupancyProfile.fromValue(form.getOccupancyProfile());
        EstimateForm estimateForm = new EstimateForm();
        estimateForm.setStateCode(form.getStateCode());
        estimateForm.setProjectType(ProjectType.NEW_INSTALL.value());
        estimateForm.setBedrooms(form.getBedrooms());
        estimateForm.setOccupants(estimatedOccupants(form.getBedrooms(), occupancyProfile));
        estimateForm.setGarbageDisposal(form.isGarbageDisposal());
        estimateForm.setAdditionalKitchen(form.isAdditionalKitchen());
        estimateForm.setSoilPercStatus(SoilPercStatus.UNKNOWN.value());
        estimateForm.setAccessDifficulty(AccessDifficulty.EASY.value());
        estimateForm.setTimeline(TimelinePreference.RESEARCHING.value());

        EstimatorResult estimate = estimatorService.estimate(estimateForm);

        PumpScheduleForm pumpScheduleForm = new PumpScheduleForm();
        int planningTankSize = occupancyProfile == OccupancyProfile.HIGH
                ? estimate.recommendedTankHighGallons()
                : estimate.recommendedTankLowGallons();
        pumpScheduleForm.setTankSizeGallons(planningTankSize);
        pumpScheduleForm.setOccupants(estimatedOccupants(form.getBedrooms(), occupancyProfile));
        pumpScheduleForm.setGarbageDisposal(form.isGarbageDisposal());
        pumpScheduleForm.setUsageProfile(occupancyProfile == OccupancyProfile.SEASONAL
                ? UsageProfile.SEASONAL.value()
                : UsageProfile.FULL_TIME.value());

        PumpScheduleResult pumpSchedule = pumpScheduleService.estimate(pumpScheduleForm);

        String occupancyNote = switch (occupancyProfile) {
            case BALANCED -> "This profile keeps the size recommendation centered on bedroom-based planning with a typical full-time household load.";
            case HIGH -> "This profile widens the recommendation because occupant load is running higher than bedroom count alone suggests.";
            case SEASONAL -> "This profile can soften the pumping cadence estimate a bit, but it does not aggressively shrink the tank recommendation.";
        };

        return new TankSizeEstimatorResult(
                estimate,
                pumpSchedule,
                occupancyProfile.label(),
                occupancyNote
        );
    }

    private Integer estimatedOccupants(Integer bedrooms, OccupancyProfile occupancyProfile) {
        int safeBedrooms = bedrooms == null || bedrooms < 1 ? 3 : bedrooms;
        return switch (occupancyProfile) {
            case BALANCED -> safeBedrooms * 2;
            case HIGH -> safeBedrooms * 2 + 2;
            case SEASONAL -> Math.max(2, safeBedrooms);
        };
    }
}
