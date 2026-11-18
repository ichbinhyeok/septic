package com.example.septic.web;

import com.example.septic.data.model.CountyRecordsPage;
import java.util.stream.Collectors;

public record CountyRecordStartView(
        String officialUrl, String officialLabel, String identifiers, String note,
        String alternateUrl, String alternateLabel
) {
    private CountyRecordStartView(String officialUrl, String officialLabel, String identifiers, String note) {
        this(officialUrl, officialLabel, identifiers, note, "", "");
    }

    static CountyRecordStartView from(CountyRecordsPage county, CountyAccessProfileView access, CountySearchGuideView guide) {
        if ("SC".equals(county.stateCode()) && access.primaryUrl().endsWith("/how-locate-septic-tank")) {
            CountyRecordStartView regionalOffice = southCarolinaRecordsOffice(county);
            if (regionalOffice != null) {
                return regionalOffice;
            }
        }
        if ("TN".equals(county.stateCode()) && access.hasSecondaryAction()
                && access.primaryUrl().endsWith("/document-viewer/search/stp")) {
            return new CountyRecordStartView(
                    access.primaryUrl(), "Open the TDEC septic records viewer", identifiers(access, guide), "",
                    access.secondaryUrl(), access.secondaryLabel()
            );
        }
        return switch (access.countyKey()) {
            case "OH::hamilton-county" -> new CountyRecordStartView(
                    "https://cagisportal.maps.arcgis.com/apps/webappviewer/index.html?id=7ee4bccae951450fb43a0c8c9f597902",
                    "Open the inspection map",
                    "Search by street address, then select the system marker.",
                    "The map excludes Cincinnati, Norwood and Springdale."
            );
            case "IN::tippecanoe-county" -> new CountyRecordStartView(
                    "https://www.tippecanoe.in.gov/569/Sewage-Disposal",
                    "Contact the Health Department for a diagram",
                    "Site address and any parcel or permit number.",
                    ""
            );
            default -> new CountyRecordStartView(
                    access.primaryUrl(),
                    access.countySpecific() ? access.primaryLabel() : "Open the official records route",
                    identifiers(access, guide),
                    ""
            );
        };
    }

    private static CountyRecordStartView southCarolinaRecordsOffice(CountyRecordsPage county) {
        // SCDES routes existing permit questions through the regional onsite wastewater offices.
        String officeNumber = switch (county.countySlug()) {
            case "aiken-county" -> "8036421637";
            case "beaufort-county" -> "8438461030";
            case "berkeley-county", "dorchester-county" -> "8439530150";
            case "florence-county" -> "8436614825";
            case "lexington-county", "richland-county" -> "8038960620";
            case "pickens-county" -> "8643723273";
            case "sumter-county" -> "8037786548";
            case "york-county" -> "8032857461";
            default -> "";
        };
        if (officeNumber.isEmpty()) {
            return null;
        }
        String parcelUrl = switch (county.countySlug()) {
            case "beaufort-county", "berkeley-county", "richland-county" -> county.parcelAnchorUrl();
            case "dorchester-county" -> "https://www.arcgis.com/home/item.html?id=c5b2bd07c3b84ce98d09c6611377e89c";
            case "florence-county" -> "https://arc2000.florenceco.org/flocoparcelsnew/flocoparcelsnew.html";
            case "york-county" -> "https://experience.arcgis.com/experience/e827d330f20a4508aa6777bf2c0b94e3";
            default -> "";
        };
        return new CountyRecordStartView("tel:+1" + officeNumber, "Call SCDES for the existing permit",
                "Property address, tax map number if known, and approximate installation year.",
                "Ask for the permit copy and any attached layout.",
                parcelUrl, "Find the parcel in " + county.countyName());
    }

    private static String identifiers(CountyAccessProfileView access, CountySearchGuideView guide) {
        if (guide != null && !guide.guide().identifiers().isBlank()) {
            return guide.guide().identifiers();
        }
        if (!access.countySpecific() || access.requiredInputs().isEmpty()) {
            return "Property address and, if available, the parcel number.";
        }
        return access.requiredInputs().stream().limit(2).collect(Collectors.joining(", "));
    }

    public boolean opensNewWindow() {
        return officialUrl.startsWith("http");
    }
}
