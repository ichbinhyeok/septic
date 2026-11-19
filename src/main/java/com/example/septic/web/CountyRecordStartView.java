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
            return southCarolinaPermitCopies(county);
        }
        if ("TN".equals(county.stateCode()) && access.hasSecondaryAction()
                && access.primaryUrl().endsWith("/document-viewer/search/stp")) {
            return new CountyRecordStartView(
                    access.primaryUrl(), "Open the TDEC septic records viewer", identifiers(access, guide), "",
                    access.secondaryUrl(), access.secondaryLabel()
            );
        }
        return switch (access.countyKey()) {
            case "AL::blount-county" -> new CountyRecordStartView(
                    "tel:+12059471076", "Call Blount County Environmental Health", identifiers(access, guide),
                    "Ask for the Permit to Install and Approval for Use. If the office cannot match the address, offer the parcel number or an earlier owner’s name.",
                    county.recordsUrl(), "View the Blount County contact page"
            );
            case "AL::cullman-county" -> new CountyRecordStartView(
                    "tel:+12567340243", "Call Cullman County Environmental Health", identifiers(access, guide),
                    "Ask the Environmental Office for the existing onsite sewage permit and Approval for Use. If the file is missing, ask whether they can search by permit number or parcel.",
                    "https://www.alabamapublichealth.gov/cullman/contact.html", "View the Cullman County contact page"
            );
            case "OH::hamilton-county" -> new CountyRecordStartView(
                    "https://cagisportal.maps.arcgis.com/apps/webappviewer/index.html?id=7ee4bccae951450fb43a0c8c9f597902",
                    "Open the inspection map",
                    "Search by street address, then select the system marker.",
                    "The map excludes Cincinnati, Norwood and Springdale."
            );
            case "NC::pitt-county" -> new CountyRecordStartView(
                    access.primaryUrl(), "Open Pitt County permit search",
                    "Street address or permit number. Keep the tax parcel number for an office request.",
                    "Choose Search in the county portal. If the search is empty or the permit has no site plan attached, ask Environmental Health for the existing septic file.",
                    "tel:+12529023200", "Call Environmental Health"
            );
            case "IN::tippecanoe-county" -> new CountyRecordStartView(
                    "https://www.tippecanoe.in.gov/569/Sewage-Disposal",
                    "Contact the Health Department for a diagram",
                    "Site address and any parcel or permit number.",
                    "If the department cannot locate the diagram by address, give them the parcel or permit number."
            );
            case "IN::decatur-county" -> new CountyRecordStartView(
                    county.recordsUrl(), "Open Decatur County Environmental Health", identifiers(access, guide),
                    "Contact the onsite sewage sanitarian at 812-663-8301, extension 7715, for the residential permit and installation inspection. If needed, ask whether they can search using the parcel or an earlier owner."
            );
            case "IN::shelby-county" -> new CountyRecordStartView(
                    county.recordsUrl(), "Open Shelby County’s Environmental Division", identifiers(access, guide),
                    "Ask for the original permit and any repair inspection sign-off. Shelby County says its records go back to 1955. If the address has changed, have the parcel and prior address ready."
            );
            default -> standardStart(county, access, guide);
        };
    }

    private static CountyRecordStartView standardStart(
            CountyRecordsPage county, CountyAccessProfileView access, CountySearchGuideView guide
    ) {
        if ("AL".equals(county.stateCode())) {
            return new CountyRecordStartView(county.officeUrl(), "Open " + county.officeLabel(),
                    identifiers(access, guide),
                    "Ask for the Permit to Install and Approval for Use. If the office cannot match the address, ask if they can search by parcel or prior owner."
            );
        }
        String note = "IN".equals(county.stateCode())
                ? "Ask " + county.officeLabel() + " for the existing permit and any site drawing. If the address returns no result, check with the office using the parcel number."
                : access.primaryAccessNote();
        String label = access.countySpecific() ? access.primaryLabel() : "Open the official records route";
        if (showAlternateAtStart(county, access)) {
            return new CountyRecordStartView(access.primaryUrl(), label, identifiers(access, guide), note,
                    access.secondaryUrl(), access.secondaryLabel());
        }
        return new CountyRecordStartView(access.primaryUrl(), label, identifiers(access, guide), note);
    }

    private static boolean showAlternateAtStart(CountyRecordsPage county, CountyAccessProfileView access) {
        if ("SC".equals(county.stateCode()) || !access.countySpecific() || !access.hasSecondaryAction()
                || access.primaryUrl().equals(access.secondaryUrl())) {
            return false;
        }
        return switch (access.mode()) {
            case "portal_with_fallback", "state_search_with_fallback", "metadata_only", "jurisdiction_first" -> true;
            case "phone_assisted" -> "NC::wake-county".equals(access.countyKey());
            default -> false;
        };
    }

    private static CountyRecordStartView southCarolinaPermitCopies(CountyRecordsPage county) {
        // SCDES lists its Customer Support line separately for copies of permits and final inspections.
        String parcelUrl = switch (county.countySlug()) {
            case "anderson-county", "beaufort-county", "berkeley-county", "richland-county" -> county.parcelAnchorUrl();
            case "dorchester-county" -> "https://www.arcgis.com/home/item.html?id=c5b2bd07c3b84ce98d09c6611377e89c";
            case "florence-county" -> "https://arc2000.florenceco.org/flocoparcelsnew/flocoparcelsnew.html";
            case "sumter-county" -> "https://sumtercity.maps.arcgis.com/apps/webappviewer/index.html?id=aa383018b7d847deaf23163aedf70704";
            case "york-county" -> "https://experience.arcgis.com/experience/e827d330f20a4508aa6777bf2c0b94e3";
            default -> "";
        };
        String parcelLabel = switch (county.countySlug()) {
            case "florence-county" -> "Open the Florence County parcel map";
            case "sumter-county" -> "Open the Sumter City–County parcel map";
            default -> "Find the parcel in " + county.countyName();
        };
        return new CountyRecordStartView("tel:+18557312504", "Call SCDES for a permit copy",
                "Property address, tax map number if known, and approximate installation year.",
                "Call 855-731-2504 for a permit copy or final inspection. If the address doesn’t match, confirm the tax map number with the county and try again.",
                parcelUrl, parcelLabel);
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
