package com.example.septic.web;

import java.util.List;

public record TrustOperationsPageView(
        String eyebrow,
        String heading,
        String intro,
        String sideLabel,
        String sideValue,
        String sideBody,
        List<TrustMetricView> metrics,
        List<TrustLaneView> lanes,
        String rowSectionTitle,
        String rowSectionIntro,
        List<CoverageStateRowView> coverageRows,
        List<CountyFinderLinkView> priorityCountyRoutes,
        String ctaHeading,
        String ctaBody,
        List<PageLink> nextLinks
) {
}
