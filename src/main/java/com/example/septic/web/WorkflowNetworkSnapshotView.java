package com.example.septic.web;

import java.util.List;

public record WorkflowNetworkSnapshotView(
        String heading,
        String summary,
        List<String> bullets,
        List<PageLink> featuredStateLinks,
        int liveCountyCount,
        int countyBackedStateCount,
        int countyFirstStateCount
) {
}
