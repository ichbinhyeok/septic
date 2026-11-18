package com.example.septic.web;

import java.util.Map;

/** Published fee examples and official routes for checking the price of the actual site work. */
public record PercCostAnswer(String heading, String answer, String sourceLabel, String sourceUrl) {
    private static final Map<String, PercCostAnswer> ANSWERS = Map.ofEntries(
            Map.entry("AR", new PercCostAnswer(
                    "Price the soil work and permit separately",
                    "Ask an Arkansas Designated Representative for the soil evaluation and design price. The county health unit can identify the permit charge for your project.",
                    "Arkansas fees and Designated Representative directory",
                    "https://healthy.arkansas.gov/programs-services/public-health-safety/onsite-wastewater/"
            )),
            Map.entry("CA", new PercCostAnswer(
                    "San Diego County example: $792 for county review",
                    "The county fee covers layout review for a perc test or soil profile. The schedule took effect July 11, 2026. Get a separate quote for field testing and excavation.",
                    "San Diego County onsite wastewater fee schedule",
                    "https://www.sandiegocounty.gov/content/dam/sdc/deh/lwqd/lwqd_feeschedule.pdf"
            )),
            Map.entry("CO", new PercCostAnswer(
                    "Get the site evaluation and county permit prices separately",
                    "For a new septic system in Jefferson County, use Public Health’s septic guidance and fee schedule. Ask the evaluator what the quote covers, including soil pits, the report and the design.",
                    "Jefferson County septic permit and evaluation guidance",
                    "https://www.jeffco.us/2379/Septic-Systems"
            )),
            Map.entry("IN", new PercCostAnswer(
                    "Ask a registered soil scientist for the field price",
                    "Hamilton County directs homeowners to a registered soil scientist for the on-site evaluation. Ask for a soils report quote, then confirm the separate permit fee with county Environmental Health.",
                    "Hamilton County soil scientist and permit instructions",
                    "https://www.hamiltoncounty.in.gov/300/Permits"
            )),
            Map.entry("MD", new PercCostAnswer(
                    "Anne Arundel County example: $450 for a platted lot",
                    "Anne Arundel lists $750 per lot for an unplatted subdivision. For another county, confirm its fee and ask who pays for excavation.",
                    "Anne Arundel County perc testing fee schedule",
                    "https://www.aahealth.org/environmental-health/permit-fees"
            )),
            Map.entry("NV", new PercCostAnswer(
                    "Confirm which soil report your office accepts before getting a quote",
                    "Nevada DEP describes both percolation testing and an accepted alternative soil investigation. Confirm the required method with the permitting office, then price the qualified evaluator and agency review separately.",
                    "Nevada DEP soil evaluation guidance",
                    "https://ndep.nv.gov/uploads/water-wpc-resources-publications-docs/WTS-25_-_Soil_Surveys.Added_.March_.2023.pdf"
            )),
            Map.entry("NY", new PercCostAnswer(
                    "Check whether the county bundles the test into the permit",
                    "Start with your county health department’s fee schedule. Tompkins County tests deep-hole soils first and may require a perc test afterward. Ask which services the permit fee includes before hiring an excavator.",
                    "Tompkins County onsite wastewater fees and permit steps",
                    "https://www.tompkinscountyny.gov/All-Departments/Whole-Health/EH/OWTS"
            )),
            Map.entry("OH", new PercCostAnswer(
                    "Ask the local health district for the review fee",
                    "Use Ohio Department of Health guidance to identify the soil evaluation and design you need. Confirm the local health district’s fee and ask the evaluator whether the quote includes excavation and a written report.",
                    "Ohio Department of Health soils and design guidance",
                    "https://odh.ohio.gov/know-our-programs/sewage-treatment-systems/information-for-homeowners/soils-designs-and-site-drainage"
            )),
            Map.entry("OK", new PercCostAnswer(
                    "Get a soil profile quote from an Oklahoma DEQ-certified profiler",
                    "Ask the profiler to price the site visit and written profile and tell you who arranges the test holes. Confirm any separate agency charges with the local DEQ office.",
                    "Oklahoma DEQ certified soil profiler directory",
                    "https://oklahoma.gov/deq/divisions/ecls/on-site-sewage-programs/deq-certified-soil-profilers.html"
            )),
            Map.entry("TN", new PercCostAnswer(
                    "TDEC example: $400 for a new conventional permit",
                    "The state application fee covers conventional systems up to 1,000 gallons per day. Ask your field office or contract county if a private soils map or consultant is also required.",
                    "TDEC septic construction permit fee schedule",
                    "https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/permit-water-septic-system-construction-permit.html"
            )),
            Map.entry("TX", new PercCostAnswer(
                    "Quote the OSSF site evaluation before pricing the system",
                    "Identify the local OSSF permitting authority through TCEQ. Ask for its application fee and get a separate site evaluation quote from a licensed evaluator or professional engineer.",
                    "TCEQ’s guide to the authorized OSSF permitting office",
                    "https://www.tceq.texas.gov/permitting/ossf/ossfpermits.html"
            )),
            Map.entry("UT", new PercCostAnswer(
                    "Ask whether you need a separate percolation test",
                    "Weber-Morgan Health Department evaluates the soils pit first. When it calls for a separate perc test, an independent provider performs it. Price the application, excavation and any separate test with the responsible office or provider.",
                    "Weber-Morgan wastewater evaluation steps",
                    "https://webermorganhealth.gov/services/environmental-health/water-quality/"
            )),
            Map.entry("WI", new PercCostAnswer(
                    "Get a soil tester’s quote and the sanitary permit fee",
                    "Ask a Wisconsin-certified soil tester to price the soil evaluation and report. Confirm the sanitary permit and plan review charges with the office administering your private onsite wastewater system.",
                    "Wisconsin DSPS onsite wastewater program",
                    "https://dsps.wi.gov/pages/Programs/POWTS/Default.aspx"
            )),
            Map.entry("WY", new PercCostAnswer(
                    "Find the small wastewater authority before requesting prices",
                    "Wyoming DEQ’s jurisdiction list identifies where DEQ issues the permit and where another authority handles it. Ask the listed office for the current application fee and the site or soils work required for your parcel.",
                    "Wyoming DEQ small wastewater jurisdiction and forms",
                    "https://deq.wyoming.gov/water-quality/water-wastewater/permitting/small-wastewater-systems/"
            ))
    );

    static PercCostAnswer forState(String stateCode) {
        return ANSWERS.get(stateCode);
    }
}
