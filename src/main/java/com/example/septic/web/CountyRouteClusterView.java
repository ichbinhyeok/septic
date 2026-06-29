package com.example.septic.web;

import java.util.List;

public record CountyRouteClusterView(
        String stateCode,
        String stateName,
        String statePath,
        String heading,
        String summary,
        String metricLabel,
        int liveCountyCount,
        PageLink stateRecordsLink,
        PageLink permitProcessLink,
        List<PageLink> countyLinks
) {
}
