package com.example.septic.web;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.CountyWorkflowStructureData;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SearchResponseTarget;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.data.model.StateQueuePlan;
import com.example.septic.service.AccessDifficulty;
import com.example.septic.service.BrunswickPermitLookupService;
import com.example.septic.service.CensusAddressLookupService;
import com.example.septic.service.CountyContentQualityService;
import com.example.septic.service.DrainfieldEstimatorResult;
import com.example.septic.service.DrainfieldEstimatorService;
import com.example.septic.service.EventAnalyticsService;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.service.LeadStorageService;
import com.example.septic.service.ProjectType;
import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SeoService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.service.SitemapService;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.service.StateQueuePlanService;
import com.example.septic.service.TankSizeEstimatorResult;
import com.example.septic.service.TankSizeEstimatorService;
import com.example.septic.service.ThurstonRecordLookupService;
import com.example.septic.service.TimelinePreference;
import com.example.septic.service.PumpScheduleResult;
import com.example.septic.service.PumpScheduleService;
import com.example.septic.service.OccupancyProfile;
import com.example.septic.service.OfficialCountyPdfService;
import com.example.septic.service.PublishingPolicyService;
import com.example.septic.service.UsStateDirectoryService;
import com.example.septic.service.UsageProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class SiteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class);
    private static final List<String> CORE_STATE_CODES = List.of("GA", "PA", "CT", "OR", "MA", "FL");
    private static final List<String> ORGANIC_SPRINT_STATE_CODES = List.of("TN", "NC", "TX", "SC", "AL", "IN", "GA");
    private static final List<String> RECORDS_ACCESS_INDEX_STATE_CODES = List.of("TN", "IN", "NC", "SC", "NY");
    private static final String PERMIT_LOOKUP_SLUG = "septic-permit-lookup";
    private static final String RECORDS_ONLINE_SLUG = "how-to-find-septic-records-online";
    private static final String RECORDS_BY_COUNTY_SLUG = "septic-records-by-county";
    private static final String PERMIT_SEARCH_BY_ADDRESS_SLUG = "septic-permit-search-by-address";
    private static final String PERMIT_RECORDS_REQUEST_SLUG = "septic-permit-records-request";
    private static final String AS_BUILT_RECORDS_SLUG = "septic-as-built-records";
    private static final String TANK_LOCATION_RECORDS_SLUG = "septic-tank-location-records";
    private static final String INSPECTION_LETTER_SLUG = "septic-inspection-letter";
    private static final String TRANSFER_COMPLIANCE_SLUG = "septic-transfer-compliance";
    private static final String OFFICIAL_LOOKUP_TOOLS_SLUG = "official-septic-lookup-tools";
    private static final String RECORDS_REQUEST_BUILDER_SLUG = "septic-records-request-builder";
    private static final String TDEC_RECORDS_SLUG = "tdec-septic-records";
    private static final String NC_PERMIT_LOOKUP_SLUG = "north-carolina-septic-permit-lookup";
    private static final String TX_OSSF_RECORDS_SLUG = "texas-ossf-records-search";
    private static final String FL_OSTDS_LOOKUP_SLUG = "florida-ostds-permit-lookup";
    private static final String DHEC_PERMIT_LOOKUP_SLUG = "dhec-septic-permit-lookup";
    private static final String TENNESSEE_PROPERTY_ASSESSMENT_URL = "https://assessment.cot.tn.gov/TPAD";
    private static final String TENNESSEE_SSDS_PROGRAM_URL =
            "https://www.tn.gov/environment/permits/water/septic-systems-permits.html";
    private static final String TENNESSEE_SSDS_RECORD_SEARCH_URL =
            "https://tdec.tn.gov/document-viewer/search/stp";
    private static final String TENNESSEE_PUBLIC_RECORDS_URL =
            "https://www.tn.gov/environment/contacts/public-records-request.html";
    private static final String TENNESSEE_FIELD_OFFICES_URL =
            "https://www.tn.gov/environment/contacts/field-offices.html";
    private static final String TENNESSEE_ONLINE_SERVICE_URL =
            "https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/wr-sds-online-application-for-ground-water-protection-services.html";
    private static final Set<String> TENNESSEE_CONTRACT_COUNTIES = Set.of(
            "blount", "davidson", "hamilton", "jefferson", "knox", "madison", "sevier", "shelby", "williamson"
    );
    private static final List<String> TENNESSEE_COUNTY_NAMES = List.of(
            "Anderson", "Bedford", "Benton", "Bledsoe", "Blount", "Bradley", "Campbell", "Cannon",
            "Carroll", "Carter", "Cheatham", "Chester", "Claiborne", "Clay", "Cocke", "Coffee",
            "Crockett", "Cumberland", "Davidson", "Decatur", "DeKalb", "Dickson", "Dyer", "Fayette",
            "Fentress", "Franklin", "Gibson", "Giles", "Grainger", "Greene", "Grundy", "Hamblen",
            "Hamilton", "Hancock", "Hardeman", "Hardin", "Hawkins", "Haywood", "Henderson", "Henry",
            "Hickman", "Houston", "Humphreys", "Jackson", "Jefferson", "Johnson", "Knox", "Lake",
            "Lauderdale", "Lawrence", "Lewis", "Lincoln", "Loudon", "Macon", "Madison", "Marion",
            "Marshall", "Maury", "McMinn", "McNairy", "Meigs", "Monroe", "Montgomery", "Moore",
            "Morgan", "Obion", "Overton", "Perry", "Pickett", "Polk", "Putnam", "Rhea", "Roane",
            "Robertson", "Rutherford", "Scott", "Sequatchie", "Sevier", "Shelby", "Smith", "Stewart",
            "Sullivan", "Sumner", "Tipton", "Trousdale", "Unicoi", "Union", "Van Buren", "Warren",
            "Washington", "Wayne", "Weakley", "White", "Williamson", "Wilson"
    );
    private static final Set<String> FLORIDA_DEP_PERMIT_COUNTIES = Set.of(
            "bay", "calhoun", "escambia", "franklin", "gadsden", "gulf", "holmes", "jackson",
            "jefferson", "leon", "liberty", "marion", "okaloosa", "santa rosa", "wakulla", "walton", "washington"
    );
    private static final List<String> FLORIDA_COUNTY_NAMES = List.of(
            "Alachua", "Baker", "Bay", "Bradford", "Brevard", "Broward", "Calhoun", "Charlotte",
            "Citrus", "Clay", "Collier", "Columbia", "DeSoto", "Dixie", "Duval", "Escambia",
            "Flagler", "Franklin", "Gadsden", "Gilchrist", "Glades", "Gulf", "Hamilton", "Hardee",
            "Hendry", "Hernando", "Highlands", "Hillsborough", "Holmes", "Indian River", "Jackson",
            "Jefferson", "Lafayette", "Lake", "Lee", "Leon", "Levy", "Liberty", "Madison", "Manatee",
            "Marion", "Martin", "Miami-Dade", "Monroe", "Nassau", "Okaloosa", "Okeechobee", "Orange",
            "Osceola", "Palm Beach", "Pasco", "Pinellas", "Polk", "Putnam", "Santa Rosa", "Sarasota",
            "Seminole", "St. Johns", "St. Lucie", "Sumter", "Suwannee", "Taylor", "Union", "Volusia",
            "Wakulla", "Walton", "Washington"
    );
    private static final List<String> SOUTH_CAROLINA_COUNTY_NAMES = List.of(
            "Abbeville", "Aiken", "Allendale", "Anderson", "Bamberg", "Barnwell", "Beaufort", "Berkeley",
            "Calhoun", "Charleston", "Cherokee", "Chester", "Chesterfield", "Clarendon", "Colleton", "Darlington",
            "Dillon", "Dorchester", "Edgefield", "Fairfield", "Florence", "Georgetown", "Greenville", "Greenwood",
            "Hampton", "Horry", "Jasper", "Kershaw", "Lancaster", "Laurens", "Lee", "Lexington", "McCormick",
            "Marion", "Marlboro", "Newberry", "Oconee", "Orangeburg", "Pickens", "Richland", "Saluda",
            "Spartanburg", "Sumter", "Union", "Williamsburg", "York"
    );
    private static final Set<String> DIRECT_ONLINE_RECORD_SEARCH_COUNTIES = Set.of(
            "NC:craven", "NC:dare", "NC:franklin", "NC:henderson", "NC:johnston"
    );
    private static final List<String> RECORDS_INTENT_HUB_SLUGS = List.of(
            PERMIT_LOOKUP_SLUG,
            RECORDS_ONLINE_SLUG,
            RECORDS_BY_COUNTY_SLUG,
            PERMIT_SEARCH_BY_ADDRESS_SLUG,
            PERMIT_RECORDS_REQUEST_SLUG,
            AS_BUILT_RECORDS_SLUG,
            TANK_LOCATION_RECORDS_SLUG,
            INSPECTION_LETTER_SLUG,
            OFFICIAL_LOOKUP_TOOLS_SLUG,
            RECORDS_REQUEST_BUILDER_SLUG,
            TDEC_RECORDS_SLUG,
            NC_PERMIT_LOOKUP_SLUG,
            TX_OSSF_RECORDS_SLUG,
            FL_OSTDS_LOOKUP_SLUG,
            DHEC_PERMIT_LOOKUP_SLUG
    );
    private static final Map<String, String> RECORDS_AUTHORITY_STATE_CODES = Map.of(
            TDEC_RECORDS_SLUG, "TN",
            NC_PERMIT_LOOKUP_SLUG, "NC",
            TX_OSSF_RECORDS_SLUG, "TX",
            FL_OSTDS_LOOKUP_SLUG, "FL",
            DHEC_PERMIT_LOOKUP_SLUG, "SC"
    );
    private static final Map<String, List<String>> STATE_RECORDS_PRIORITY_COUNTY_SLUGS = Map.of(
            "TX", List.of("tarrant-county"),
            "CA", List.of("san-bernardino-county"),
            "TN", List.of("hamilton-county"),
            "NC", List.of(
                    "buncombe-county",
                    "wake-county",
                    "union-county",
                    "pitt-county",
                    "pender-county",
                    "johnston-county",
                    "alamance-county",
                    "guilford-county"
            ),
            "IN", List.of(
                    "porter-county",
                    "monroe-county",
                    "brown-county",
                    "bartholomew-county",
                    "grant-county",
                    "la-porte-county",
                    "washington-county",
                    "tippecanoe-county",
                    "st-joseph-county"
            ),
            "SC", List.of(
                    "horry-county",
                    "greenville-county",
                    "spartanburg-county",
                    "charleston-county",
                    "anderson-county",
                    "richland-county",
                    "pickens-county",
                    "aiken-county"
            )
    );
    private static final List<String> PERMIT_LOOKUP_STATE_SLUGS = List.of(
            "septic-records-checklist",
            "septic-permit-process"
    );
    private static final List<String> TRANSFER_COMPLIANCE_STATE_SLUGS = List.of(
            "septic-records-checklist",
            "septic-permit-process",
            "buying-a-house-with-a-septic-system"
    );
    private static final Map<String, Integer> COUNTY_SEARCH_RESPONSE_BOOSTS = Map.ofEntries(
            Map.entry("NC::wake-county", 150),
            Map.entry("TN::davidson-county", 146),
            Map.entry("NC::alamance-county", 140),
            Map.entry("TX::tarrant-county", 132),
            Map.entry("TX::denton-county", 124),
            Map.entry("CA::san-bernardino-county", 120),
            Map.entry("MD::st-marys-county", 118),
            Map.entry("NJ::cape-may-county", 118),
            Map.entry("NJ::gloucester-county", 116),
            Map.entry("OH::hamilton-county", 114),
            Map.entry("AZ::pima-county", 110),
            Map.entry("GA::jackson-county", 108),
            Map.entry("TN::hamilton-county", 106),
            Map.entry("TN::blount-county", 104),
            Map.entry("TX::travis-county", 102),
            Map.entry("TX::comal-county", 118),
            Map.entry("NC::buncombe-county", 112),
            Map.entry("NC::brunswick-county", 96),
            Map.entry("NC::cabarrus-county", 94),
            Map.entry("NC::forsyth-county", 92),
            Map.entry("MD::harford-county", 90),
            Map.entry("MD::cecil-county", 88),
            Map.entry("MD::prince-georges-county", 86),
            Map.entry("VA::prince-william-county", 84)
    );
    private static final Map<String, List<String>> COUNTY_SEARCH_RESPONSE_QUERIES = Map.ofEntries(
            Map.entry("NC::wake-county", List.of(
                    "wake county septic records",
                    "wake county septic permit lookup",
                    "wake county septic permit search"
            )),
            Map.entry("TN::davidson-county", List.of(
                    "davidson county septic records",
                    "davidson county septic permit lookup",
                    "davidson county septic records request"
            )),
            Map.entry("NC::alamance-county", List.of(
                    "alamance county septic records",
                    "alamance county permits",
                    "alamance county septic permit lookup"
            )),
            Map.entry("TX::tarrant-county", List.of(
                    "tarrant county septic records",
                    "tarrant county septic permit lookup",
                    "tarrant county septic inspection records"
            )),
            Map.entry("TX::denton-county", List.of(
                    "denton county septic records",
                    "denton county septic permit lookup",
                    "denton county septic permit search"
            )),
            Map.entry("CA::san-bernardino-county", List.of(
                    "san bernardino county septic certification",
                    "septic certification san bernardino",
                    "san bernardino county septic records"
            )),
            Map.entry("MD::st-marys-county", List.of(
                    "st marys county septic records",
                    "st marys county septic permit lookup",
                    "st marys county perc records"
            )),
            Map.entry("NJ::cape-may-county", List.of(
                    "cape may county septic records",
                    "cape may county septic permit lookup",
                    "cape may county septic as-built records"
            )),
            Map.entry("NJ::gloucester-county", List.of(
                    "gloucester county septic records",
                    "gloucester county septic permit lookup",
                    "gloucester county septic records request"
            )),
            Map.entry("OH::hamilton-county", List.of(
                    "hamilton county septic inspection records",
                    "hamilton county septic records",
                    "hamilton county septic permit lookup"
            )),
            Map.entry("AZ::pima-county", List.of(
                    "pima county septic records",
                    "pima county septic records search",
                    "pima county septic permit lookup"
            )),
            Map.entry("GA::jackson-county", List.of(
                    "jackson county septic records",
                    "jackson county permits",
                    "jackson county septic permit search by address"
            )),
            Map.entry("TN::hamilton-county", List.of(
                    "hamilton county septic records",
                    "hamilton county septic inspection records",
                    "hamilton county septic permit lookup"
            )),
            Map.entry("TN::blount-county", List.of(
                    "blount county septic records",
                    "blount county septic permit lookup",
                    "blount county septic records request"
            )),
            Map.entry("TX::comal-county", List.of(
                    "comal county septic permit search",
                    "comal county septic records",
                    "comal county ossf permit lookup"
            )),
            Map.entry("NC::buncombe-county", List.of(
                    "buncombe county septic permit lookup",
                    "buncombe county septic records",
                    "buncombe county well and septic records"
            )),
            Map.entry("TX::travis-county", List.of(
                    "travis county septic records",
                    "travis county septic permit lookup",
                    "travis county septic services"
            )),
            Map.entry("NC::brunswick-county", List.of(
                    "brunswick county septic records",
                    "brunswick county septic permit lookup",
                    "brunswick county septic permit search"
            )),
            Map.entry("NC::cabarrus-county", List.of(
                    "cabarrus county septic records",
                    "cabarrus county septic permit lookup",
                    "cabarrus county septic records request"
            )),
            Map.entry("NC::forsyth-county", List.of(
                    "forsyth county septic records",
                    "forsyth county septic permit lookup",
                    "forsyth county septic records request"
            )),
            Map.entry("MD::harford-county", List.of(
                    "harford county septic records",
                    "harford county septic permit lookup",
                    "harford county perc records"
            )),
            Map.entry("MD::cecil-county", List.of(
                    "cecil county septic records",
                    "cecil county septic permit lookup",
                    "cecil county septic records request"
            )),
            Map.entry("MD::prince-georges-county", List.of(
                    "prince george's county septic records",
                    "prince george's county septic permit lookup",
                    "prince george's county momentum septic permit"
            )),
            Map.entry("VA::prince-william-county", List.of(
                    "prince william county septic records",
                    "prince william county septic permit lookup",
                    "prince william county septic inspection records"
            ))
    );
    private static final Map<String, List<String>> STATE_RECORDS_RESPONSE_QUERIES = Map.ofEntries(
            Map.entry("TN", List.of(
                    "tennessee septic records by county",
                    "tennessee contract county septic records",
                    "tennessee county septic permit records",
                    "tennessee septic inspection letter",
                    "tennessee septic repair permit records"
            )),
            Map.entry("IN", List.of(
                    "how to find septic tank records online free",
                    "septic system lookup",
                    "indiana septic records",
                    "indiana septic permit lookup",
                    "county septic records",
                    "septic tank location records"
            )),
            Map.entry("NC", List.of(
                    "north carolina septic records",
                    "alamance county septic records",
                    "buncombe county septic permit lookup",
                    "where can i find septic tank records",
                    "septic tank permit records",
                    "carteret county septic permit search",
                    "union county septic permit lookup"
            )),
            Map.entry("TX", List.of(
                    "texas septic records",
                    "tarrant county septic records",
                    "denton county septic records",
                    "travis county septic records",
                    "septic permit search by address texas",
                    "comal county septic permit search"
            )),
            Map.entry("AL", List.of(
                    "alabama septic records",
                    "alabama septic permit lookup",
                    "county septic records alabama",
                    "septic permit search by address alabama",
                    "perc test records alabama"
            )),
            Map.entry("GA", List.of(
                    "georgia septic records",
                    "georgia septic permit lookup",
                    "county septic records georgia",
                    "jackson county septic records",
                    "septic permit search by address georgia"
            )),
            Map.entry("SC", List.of(
                    "south carolina septic records",
                    "scdes septic records",
                    "south carolina septic permit lookup",
                    "greenville county septic records",
                    "d-1740 septic file"
            )),
            Map.entry("CA", List.of(
                    "california septic records",
                    "san bernardino county septic certification",
                    "septic certification san bernardino",
                    "california septic permit lookup",
                    "county environmental health septic records"
            )),
            Map.entry("MD", List.of(
                    "maryland septic records",
                    "st marys county septic records",
                    "harford county septic records",
                    "cecil county septic records",
                    "maryland perc records"
            )),
            Map.entry("NJ", List.of(
                    "new jersey septic records",
                    "cape may county septic records",
                    "gloucester county septic records",
                    "new jersey septic permit lookup",
                    "septic as-built records new jersey"
            )),
            Map.entry("OH", List.of(
                    "ohio septic records",
                    "hamilton county septic inspection records",
                    "ohio septic permit lookup",
                    "county septic records ohio",
                    "septic inspection records ohio"
            )),
            Map.entry("AZ", List.of(
                    "arizona septic records",
                    "pima county septic records",
                    "arizona septic permit lookup",
                    "septic records search arizona",
                    "county septic records arizona"
            )),
            Map.entry("VA", List.of(
                    "virginia septic records",
                    "prince william county septic records",
                    "virginia septic permit lookup",
                    "county health septic records virginia",
                    "septic inspection records virginia"
            ))
    );
    private static final String INDIANA_RECORDS_PACKET_PATH = "/for-professionals/records-packet/indiana/";
    private static final String NEW_YORK_BUYER_PACKET_PATH = "/for-professionals/buyer-diligence-packet/new-york/";
    private static final String TENNESSEE_INSPECTION_PACKET_PATH = "/for-professionals/inspection-letter-packet/tennessee/";
    private static final String NORTH_CAROLINA_LISTING_PACKET_PATH = "/for-professionals/listing-permit-packet/north-carolina/";
    private static final String SOUTH_CAROLINA_PERMIT_PACKET_PATH = "/for-professionals/permit-prep-packet/south-carolina/";
    private static final String STATE_EDITORIAL_NOTE = "This page is maintained as conservative homeowner guidance and updated when linked official materials or local workflow notes change.";
    private static final String CONTENT_EDITORIAL_NOTE = "This page is a planning hub. Use the linked state-specific pages when rule style, local authority, or records workflow differences matter.";
    private static final EditorialProfile STATE_PAGE_PREPARER = new EditorialProfile(
            "SepticPath Editorial Team",
            "Planning editor",
            "Turns state rules, permit friction, and buyer-risk signals into estimate-first homeowner guidance."
    );
    private static final EditorialProfile CONTENT_PAGE_PREPARER = new EditorialProfile(
            "SepticPath Editorial Team",
            "Content editor",
            "Keeps national pages aligned with the estimator, state guides, and the highest-intent next steps."
    );
    private static final EditorialProfile SOURCE_REVIEWER = new EditorialProfile(
            "SepticPath Source Review",
            "Source reviewer",
            "Checks official links, verification dates, and local workflow notes before a page stays public."
    );

    private final ResearchDataService researchDataService;
    private final EstimatorService estimatorService;
    private final LeadStorageService leadStorageService;
    private final SeoService seoService;
    private final SitemapService sitemapService;
    private final DrainfieldEstimatorService drainfieldEstimatorService;
    private final TankSizeEstimatorService tankSizeEstimatorService;
    private final PumpScheduleService pumpScheduleService;
    private final StateQueuePlanService stateQueuePlanService;
    private final UsStateDirectoryService usStateDirectoryService;
    private final PublishingPolicyService publishingPolicyService;
    private final CensusAddressLookupService censusAddressLookupService;
    private final BrunswickPermitLookupService brunswickPermitLookupService;
    private final ThurstonRecordLookupService thurstonRecordLookupService;
    private final EventAnalyticsService eventAnalyticsService;
    private final CountyContentQualityService countyContentQualityService;
    private final SepticDocumentAnalysisService septicDocumentAnalysisService;
    private final OfficialCountyPdfService officialCountyPdfService;

    public SiteController(
            ResearchDataService researchDataService,
            EstimatorService estimatorService,
            LeadStorageService leadStorageService,
            SeoService seoService,
            SitemapService sitemapService,
            DrainfieldEstimatorService drainfieldEstimatorService,
            TankSizeEstimatorService tankSizeEstimatorService,
            PumpScheduleService pumpScheduleService,
            StateQueuePlanService stateQueuePlanService,
            UsStateDirectoryService usStateDirectoryService,
            PublishingPolicyService publishingPolicyService,
            CensusAddressLookupService censusAddressLookupService,
            BrunswickPermitLookupService brunswickPermitLookupService,
            ThurstonRecordLookupService thurstonRecordLookupService,
            EventAnalyticsService eventAnalyticsService,
            CountyContentQualityService countyContentQualityService,
            SepticDocumentAnalysisService septicDocumentAnalysisService,
            OfficialCountyPdfService officialCountyPdfService
    ) {
        this.researchDataService = researchDataService;
        this.estimatorService = estimatorService;
        this.leadStorageService = leadStorageService;
        this.seoService = seoService;
        this.sitemapService = sitemapService;
        this.drainfieldEstimatorService = drainfieldEstimatorService;
        this.tankSizeEstimatorService = tankSizeEstimatorService;
        this.pumpScheduleService = pumpScheduleService;
        this.stateQueuePlanService = stateQueuePlanService;
        this.usStateDirectoryService = usStateDirectoryService;
        this.publishingPolicyService = publishingPolicyService;
        this.censusAddressLookupService = censusAddressLookupService;
        this.brunswickPermitLookupService = brunswickPermitLookupService;
        this.thurstonRecordLookupService = thurstonRecordLookupService;
        this.eventAnalyticsService = eventAnalyticsService;
        this.countyContentQualityService = countyContentQualityService;
        this.septicDocumentAnalysisService = septicDocumentAnalysisService;
        this.officialCountyPdfService = officialCountyPdfService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", seoService.homePage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        WorkflowNetworkSnapshotView workflowNetworkSnapshot = workflowNetworkSnapshot();
        model.addAttribute("featuredStates", publicStates.stream()
                .filter(state -> "anchor".equalsIgnoreCase(state.launchTier()))
                .toList());
        model.addAttribute("spotlightStates", publicStates.stream()
                .sorted(Comparator
                        .comparing((StateProfile state) -> !"anchor".equalsIgnoreCase(state.launchTier()))
                        .thenComparing(StateProfile::stateName))
                .limit(6)
                .toList());
        model.addAttribute("countyFinderLinks", countyFinderLinks());
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        model.addAttribute("countySpecificRouteCount", CountyAccessProfileCatalog.countySpecificProfileCount());
        model.addAttribute("preparedFieldPackCount", CountyAcquisitionProfileCatalog.preparedFieldPackCount());
        model.addAttribute("featuredIntentPages", homeGrowthSpotlights());
        model.addAttribute("countyRouteClusters", countyRouteClusters(8, 4));
        model.addAttribute("liveGuideCount", publicStates.size());
        model.addAttribute("liveIntentCount", researchDataService.getPublicStateMoneyPages().size());
        model.addAttribute("liveCountyCount", workflowNetworkSnapshot.liveCountyCount());
        model.addAttribute("countyBackedStateCount", workflowNetworkSnapshot.countyBackedStateCount());
        model.addAttribute("workflowNetworkSnapshot", workflowNetworkSnapshot);
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - publicStates.size(), 0));
        return "pages/home";
    }

    @GetMapping({"/ops/event-report", "/ops/event-report/"})
    public String eventReport(Model model, HttpServletResponse response) {
        response.setHeader("X-Robots-Tag", "noindex, nofollow, noarchive");
        response.setHeader("Cache-Control", "no-store");
        model.addAttribute("page", new PageMeta(
                "SepticPath Event Report",
                "Internal operations report for click and artifact behavior signals.",
                seoService.absoluteUrl("/ops/event-report/"),
                "noindex,nofollow,noarchive",
                List.of()
        ));
        model.addAttribute("report", eventAnalyticsService.report());
        return "pages/event-report";
    }

    @GetMapping({"/septic-record-finder", "/septic-record-finder/"})
    public String recordFinder(Model model) {
        model.addAttribute("page", seoService.recordFinderPage());
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        return "pages/record-finder";
    }

    @GetMapping({"/septic-records-access-index", "/septic-records-access-index/"})
    public String recordsAccessIndex(Model model) {
        List<RecordsAccessIndexStateView> indexStates = recordsAccessIndexStates();
        List<StateProfile> countyRouteStates = countyRouteStates();
        List<CountyFinderLinkView> countyFinderLinks = countyFinderLinks(totalCountyRouteCount());
        String dataLastUpdated = recordsAccessIndexDataLastUpdated(countyFinderLinks);
        model.addAttribute("page", seoService.recordsAccessIndexPage(
                dataLastUpdated,
                countyFinderLinks.size(),
                countyRouteStates.size()
        ));
        model.addAttribute("indexStates", indexStates);
        model.addAttribute("countyFinderLinks", countyFinderLinks);
        model.addAttribute("countyRouteStates", countyRouteStates);
        model.addAttribute("stateDirectory", countyRouteStates.stream()
                .map(state -> countyRouteCluster(state, 0))
                .toList());
        model.addAttribute("countyRouteStateCount", countyRouteStates.size());
        model.addAttribute("dataLastUpdated", dataLastUpdated);
        model.addAttribute("priorityCountyRouteCount", indexStates.stream()
                .mapToInt(RecordsAccessIndexStateView::countyRouteCount)
                .sum());
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        return "pages/records-access-index";
    }

    @GetMapping(value = {"/septic-records-access-index.csv", "/septic-records-access-index.csv/"}, produces = "text/csv")
    @ResponseBody
    public ResponseEntity<String> recordsAccessIndexCsv() {
        List<CountyFinderLinkView> countyFinderLinks = countyFinderLinks(totalCountyRouteCount());
        List<String> rows = new ArrayList<>();
        rows.add(csvRow(
                "state_code",
                "state",
                "county",
                "route_type",
                "first_artifact",
                "confidence_score",
                "confidence_label",
                "official_source_count",
                "parcel_anchor",
                "last_reviewed",
                "official_records_url",
                "septicpath_guide_url"
        ));
        countyFinderLinks.stream()
                .sorted(Comparator
                        .comparing(CountyFinderLinkView::stateName)
                        .thenComparing(CountyFinderLinkView::countyName))
                .map(link -> csvRow(
                        link.stateCode(),
                        link.stateName(),
                        link.countyName(),
                        link.requestMethodLabel(),
                        link.firstArtifactLabel(),
                        Integer.toString(link.confidenceScore()),
                        link.confidenceLabel(),
                        Integer.toString(link.officialSourceCount()),
                        Boolean.toString(link.parcelAnchorAvailable()),
                        link.lastReviewedAt(),
                        link.recordsUrl(),
                        link.absoluteUrl()
                ))
                .forEach(rows::add);
        String body = String.join("\r\n", rows) + "\r\n";
        String dataLastUpdated = recordsAccessIndexDataLastUpdated(countyFinderLinks);
        long lastModified = LocalDate.parse(dataLastUpdated)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"septicpath-records-access-index.csv\"")
                .header("Link", "<" + seoService.absoluteUrl("/septic-records-access-index/")
                        + ">; rel=\"canonical\", <" + seoService.absoluteUrl("/septic-records-access-index.csv")
                        + ">; rel=\"alternate\"; type=\"text/csv\"")
                .header("Cache-Control", "public, max-age=3600")
                .lastModified(lastModified)
                .eTag("\"" + Integer.toHexString(body.hashCode()) + "\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(body);
    }

    @GetMapping({"/offer-prep-septic-file-check", "/offer-prep-septic-file-check/"})
    public String offerPrepSepticFileCheck(Model model) {
        model.addAttribute("page", seoService.offerPrepFileCheckPage());
        model.addAttribute("offerPrepStates", offerPrepStates());
        model.addAttribute("priorityCountyRouteCount", offerPrepStates().stream()
                .map(StateProfile::stateCode)
                .mapToInt(stateCode -> researchDataService.listPublicCountyRecordsPages(stateCode).size())
                .sum());
        return "pages/offer-prep-septic-file-check";
    }

    @GetMapping({"/embed/septic-record-finder", "/embed/septic-record-finder/"})
    public String recordFinderEmbed(Model model) {
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        return "pages/record-finder-embed";
    }

    @GetMapping({"/septic-bedroom-permit-checker", "/septic-bedroom-permit-checker/"})
    public String bedroomPermitChecker(Model model) {
        model.addAttribute("page", seoService.bedroomPermitCheckerPage());
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        model.addAttribute("bedroomCheckerEmbedUrl", seoService.absoluteUrl("/embed/septic-bedroom-permit-checker/"));
        return "pages/bedroom-permit-checker";
    }

    @GetMapping({"/embed/septic-bedroom-permit-checker", "/embed/septic-bedroom-permit-checker/"})
    public String bedroomPermitCheckerEmbed(Model model) {
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        return "pages/bedroom-permit-checker-embed";
    }

    @PostMapping(value = "/api/address-record-finder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AddressRecordFinderResult> addressRecordFinder(@RequestBody AddressRecordFinderForm form) {
        if (form == null || !form.isUsable()) {
            return ResponseEntity.badRequest().body(new AddressRecordFinderResult(
                    "invalid",
                    "Enter a full U.S. property address",
                    "Include the street, city, and a state abbreviation or ZIP so the county can be resolved reliably.",
                    "", "", "", "", "", "", "", List.of(), List.of()
            ));
        }

        CensusAddressLookupService.CensusAddressLookupResult lookup = censusAddressLookupService.lookup(form.normalizedAddress());
        if (lookup.status() == CensusAddressLookupService.CensusAddressLookupResult.Status.NOT_FOUND) {
            return ResponseEntity.ok(new AddressRecordFinderResult(
                    "not_found",
                    "We could not resolve that county",
                    "Check the street number, city, state, and ZIP. You can still search the county route manually.",
                    "", "", "", "", "County records by county", "/septic-records-by-county/", "", List.of(), List.of()
            ));
        }
        if (lookup.status() == CensusAddressLookupService.CensusAddressLookupResult.Status.UNAVAILABLE) {
            return ResponseEntity.ok(new AddressRecordFinderResult(
                    "unavailable",
                    "County lookup is temporarily unavailable",
                    "Use the county finder while the address resolver reconnects. No address was saved.",
                    "", "", "", "", "Search county records", "/septic-records-by-county/", "", List.of(), List.of()
            ));
        }

        return ResponseEntity.ok(addressRecordFinderRoute(lookup));
    }

    @PostMapping(value = "/api/brunswick-permit-lookup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BrunswickPermitLookupService.LookupResult> brunswickPermitLookup(
            @RequestBody BrunswickPermitLookupForm form
    ) {
        String address = form == null ? "" : form.address();
        String parcelId = form == null ? "" : form.parcelId();
        BrunswickPermitLookupService.LookupResult result = brunswickPermitLookupService.lookup(address, parcelId);
        return "invalid".equals(result.status())
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok(result);
    }

    @PostMapping(value = "/api/thurston-record-lookup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ThurstonRecordLookupService.LookupResult> thurstonRecordLookup(
            @RequestBody ThurstonRecordLookupForm form
    ) {
        ThurstonRecordLookupService.LookupResult result = thurstonRecordLookupService.lookup(
                form == null ? "" : form.parcelId());
        return "invalid".equals(result.status())
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok(result);
    }

    @PostMapping(
            value = "/api/county-records/prepare-official-pdf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> prepareOfficialCountyPdf(@RequestBody OfficialCountyPdfForm form) {
        try {
            OfficialCountyPdfService.PreparedPdf prepared = officialCountyPdfService.prepare(form);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "attachment; filename=\"" + prepared.fileName() + "\"")
                    .body(prepared.bytes());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(Map.of(
                    "status", "invalid",
                    "message", exception.getMessage()
            ));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", "unavailable", "message", "The official county PDF could not be retrieved."));
        } catch (java.io.IOException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", "unavailable", "message", exception.getMessage()));
        }
    }

    @PostMapping(
            value = "/api/septic-document-analyzer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> septicDocumentAnalyzer(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "purpose", required = false) String purpose,
            @RequestParam(name = "stateCode", required = false) String stateCode,
            @RequestParam(name = "countyName", required = false) String countyName
    ) {
        try {
            return ResponseEntity.ok(septicDocumentAnalysisService.analyze(file, purpose, stateCode, countyName));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "invalid",
                    "heading", "We could not analyze that file",
                    "summary", exception.getMessage()
            ));
        } catch (RejectedExecutionException exception) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "3")
                    .body(Map.of(
                            "status", "busy",
                            "heading", "Document analysis is busy",
                            "summary", "Wait a few seconds and try the same file again. The file was not saved."
                    ));
        } catch (Exception exception) {
            LOGGER.warn("Document analysis failed", exception);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "unavailable",
                    "heading", "Document analysis is temporarily unavailable",
                    "summary", "The file was not saved. Try a searchable PDF or plain-text export."
            ));
        }
    }

    @GetMapping({"/states", "/states/"})
    public String stateCoverage(Model model) {
        List<StateCoverageCardView> coverageCards = buildStateCoverageCards();
        WorkflowNetworkSnapshotView workflowNetworkSnapshot = workflowNetworkSnapshot();
        model.addAttribute("page", seoService.stateCoveragePage());
        model.addAttribute("liveStates", coverageCards.stream()
                .filter(StateCoverageCardView::published)
                .toList());
        model.addAttribute("priorityQueuePlans", stateQueuePlanService.topPlans(10).stream()
                .filter(plan -> researchDataService.findStateByCode(plan.stateCode())
                        .filter(StateProfile::isPublished)
                        .isEmpty())
                .map(this::stateQueuePlanView)
                .toList());
        model.addAttribute("featuredIntentPages", coverageGrowthSpotlights());
        model.addAttribute("countyFinderLinks", countyFinderLinks());
        model.addAttribute("countyRouteStates", countyRouteStates());
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        model.addAttribute("countyRouteClusters", countyRouteClusters(16, 4));
        model.addAttribute("queuedStates", coverageCards.stream()
                .filter(card -> !card.published())
                .toList());
        model.addAttribute("liveGuideCount", researchDataService.getPublicStateProfiles().size());
        model.addAttribute("liveIntentCount", researchDataService.getPublicStateMoneyPages().size());
        model.addAttribute("liveCountyCount", workflowNetworkSnapshot.liveCountyCount());
        model.addAttribute("countyBackedStateCount", workflowNetworkSnapshot.countyBackedStateCount());
        model.addAttribute("workflowNetworkSnapshot", workflowNetworkSnapshot);
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - researchDataService.getPublicStateProfiles().size(), 0));
        return "pages/state-coverage";
    }

    @GetMapping({"/about", "/about/"})
    public String about(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "About SepticPath",
                        "Who operates SepticPath, how official-source records routes are reviewed, and what this independent planning tool can and cannot establish.",
                        "/about/"
                ),
                "About this project",
                "An independent records-first planning product.",
                "SepticPath helps U.S. homeowners, buyers, sellers, and property professionals move from an address to the public septic file, identify what the file does not prove, and prepare the next question. It is not affiliated with a state, county, health department, or records vendor.",
                Arrays.asList(
                        new SitePageSection(
                                "Who operates and reviews it",
                                "SepticPath is operated as an independent web product. The Editorial Team and Source Review labels describe product functions, not named licensed engineers, inspectors, attorneys, or government officials.",
                                List.of(
                                        "Editorial work turns public records instructions into property-task checklists and keeps claims inside the cited source boundary.",
                                        "Source review checks official destinations, visible form or search fields, review dates, and fallback instructions before a route is described as county-specific.",
                                        "No professional credential is implied. Engineering, inspection, legal, permitting, and property-condition decisions remain with the appropriate qualified professional or agency."
                                )
                        ),
                        new SitePageSection(
                                "What this site is for",
                                "The goal is to reduce records and permit uncertainty without pretending a public file proves current condition or approval.",
                                List.of(
                                        "Address and county routing to reviewed official records paths.",
                                        "State-aware pages with official-source links, agency attribution, and last-reviewed dates.",
                                        "Document checks and conservative cost planning after the record path is clear."
                                )
                        ),
                        new SitePageSection(
                                "How the workflow is verified",
                                "Route checks use test properties or non-sensitive public examples and stop before any government submission, payment, signature, or claim about a real property's status.",
                                List.of(
                                        "The address resolver is tested for valid, incomplete, and unmatched U.S. addresses without retaining an address in a server-side property database.",
                                        "County workflows are checked for the official owner, required search clues, expected artifact, blocked-route fallback, and the difference between an empty search and a written no-record response.",
                                        "Coverage and the downloadable access index expose route depth and review dates so changes can be audited and corrected."
                                )
                        ),
                        new SitePageSection(
                                "What this site is not",
                                "This product is intentionally conservative where inputs are weak or local conditions are unknown.",
                                List.of(
                                        "Not engineering design software.",
                                        "Not permit-ready calculation software.",
                                        "Not a code-compliance certification tool."
                                )
                        ),
                        new SitePageSection(
                                "How data is handled",
                                "Research data is versioned in files and reviewed before it becomes publishable guidance. Runtime leads and events are stored separately for auditability and export.",
                                List.of(
                                        "Official sources are preferred for rules and permit process context.",
                                        "Commercial sources are used only for broad public cost anchors.",
                                        "Where rules are unclear, the estimate widens instead of inventing certainty."
                                )
                        )
                ),
                "Records first, source-transparent",
                "Inspect the public route dataset, methodology, and correction path before relying on a workflow. Every property-specific conclusion still needs the official file or office response."
        );
    }

    @GetMapping({"/privacy-policy", "/privacy-policy/"})
    public String privacyPolicy(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Privacy Policy",
                        "What information this site collects, why it is stored, and how lead requests are handled.",
                        "/privacy-policy/"
                ),
                "Privacy policy",
                "What this site collects and why.",
                "This page describes the current handling of form submissions and site interaction data for SepticPath. It is an operational policy page, not legal advice.",
                Arrays.asList(
                        new SitePageSection(
                                "Information collected",
                                "What is handled depends on the action you choose. Browsing public guidance does not create a property file on our servers.",
                                List.of(
                                        "Quote and contact forms store the details you submit, such as name, email, phone, ZIP code, project answers, message, consent text, and submission time.",
                                        "Anonymous measurement can record page and tool actions, county route, general workflow status, referrer, device/browser information, and network information. Property address, parcel ID, request number, email, and phone are not intentionally sent as analytics event fields.",
                                        "An address entered in the record finder is used to resolve a county through the U.S. Census lookup. It is not added to a SepticPath server-side property database."
                                )
                        ),
                        new SitePageSection(
                                "Browser-only task data",
                                "Some workflow information stays in your own browser so an official-site handoff does not erase your work.",
                                List.of(
                                        "County-task clues, request progress, and confirmation notes can remain in local browser storage for up to 30 days and can be cleared from the workflow.",
                                        "Uploaded documents are processed to create an extracted summary. Original files and OCR images are not intentionally retained; a saved workspace is created only when you choose to download it.",
                                        "Clearing site data in your browser also removes browser-only task state."
                                )
                        ),
                        new SitePageSection(
                                "Why submitted information is stored",
                                "Submitted form records support the exact action shown at the time of submission.",
                                List.of(
                                        "To answer contact, correction, or privacy requests.",
                                        "To preserve a consent snapshot, submission time, and estimate context attached to a quote-help request.",
                                        "To measure whether record and estimate workflows are useful without treating a request confirmation as a record obtained."
                                )
                        ),
                        new SitePageSection(
                                "Sharing, retention, and your choices",
                                "A quote-help submission may be reviewed for routing to an appropriate service provider, but submission does not mean a provider is available or has accepted the project.",
                                List.of(
                                        "Do not submit payment-card, bank-account, government-ID, or other highly sensitive personal information through the forms.",
                                        "Operational form records and audit logs are retained while needed for inquiry handling, consent records, abuse prevention, and legitimate operations; a fixed deletion period is not yet promised.",
                                        "Use the Privacy request option on the contact page to ask what was stored or request deletion. Include the lead or contact reference when available."
                                )
                        )
                ),
                "Control the handoff",
                "Government submission stays under your control. SepticPath prepares and tracks the task but does not silently submit a county request in your name."
        );
    }

    @GetMapping({"/editorial-standards", "/editorial-standards/"})
    public String editorialStandards(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Editorial Standards",
                        "How SepticPath builds, reviews, updates, and limits source-backed septic planning guidance.",
                        "/editorial-standards/"
                ),
                "Editorial standards",
                "How this site is reviewed, updated, and kept inside planning-tool boundaries.",
                "This page explains how SepticPath turns public rules, file paths, and official-source context into homeowner-facing planning guidance without pretending to be permit software or engineering design.",
                Arrays.asList(
                        new SitePageSection(
                                "What we prefer as evidence",
                                "Official state, county, district, or delegated authority sources come first whenever they are available and readable enough to support a homeowner-facing explanation.",
                                List.of(
                                        "Rules, forms, manuals, and official local office directories are preferred for workflow claims.",
                                        "Public cost anchors are used only as broad planning context, not as permit truth.",
                                        "When sources conflict or stay vague, the page should widen uncertainty instead of inventing precision."
                                )
                        ),
                        new SitePageSection(
                                "How pages are reviewed",
                                "Published pages are expected to carry source references, reviewed-against copy, and a last-reviewed date tied to the workflow described on the page.",
                                List.of(
                                        "State guides are reviewed against official state-level or delegated local sources.",
                                        "State intent pages are reviewed against the sources tied to that exact workflow and state.",
                                        "FAQ, CTA, and internal links are written to move the user toward the next real file, office, or estimate step."
                                )
                        ),
                        new SitePageSection(
                                "What we deliberately avoid",
                                "The product is intentionally conservative where local file context, site conditions, or delegated authority rules could change the answer.",
                                List.of(
                                        "We do not present outputs as engineered design recommendations.",
                                        "We do not present outputs as permit approval or code-compliance decisions.",
                                        "We do not narrow cost or scope ranges aggressively when source coverage is weak."
                                )
                        )
                ),
                "Trust the source trail",
                "Use the pages as planning guidance, then confirm the local file, reviewing office, and site conditions before relying on a quote or design decision."
        );
    }

    @GetMapping({"/methodology", "/methodology/"})
    public String methodology(Model model) {
        return renderTrustOperationsPage(
                model,
                seoService.basicPage(
                        "SepticPath Methodology",
                        "How SepticPath turns official septic sources, county records paths, and conservative estimate logic into homeowner planning pages.",
                        "/methodology/"
                ),
                methodologyOperations()
        );
    }

    @GetMapping({"/source-policy", "/source-policy/"})
    public String sourcePolicy(Model model) {
        return renderTrustOperationsPage(
                model,
                seoService.basicPage(
                        "Source Policy",
                        "How SepticPath prioritizes official septic sources, local records paths, verification dates, and correction requests.",
                        "/source-policy/"
                ),
                sourcePolicyOperations()
        );
    }

    @GetMapping({"/coverage", "/coverage/"})
    public String coverage(Model model) {
        return renderTrustOperationsPage(
                model,
                seoService.basicPage(
                        "SepticPath Coverage",
                        "Live coverage counts for SepticPath state guides, workflow pages, county records pages, and official-source depth.",
                        "/coverage/"
                ),
                coverageOperations()
        );
    }

    @GetMapping({"/terms-of-use", "/terms-of-use/"})
    public String termsOfUse(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Terms of Use",
                        "The core use conditions for this estimate-only septic planning website.",
                        "/terms-of-use/"
                ),
                "Terms of use",
                "Use this site as a planning tool, not as engineering or legal approval.",
                "These terms describe the intended use of the public estimator and related content. They set the operating boundaries for a planning tool, not a permit or compliance service.",
                Arrays.asList(
                        new SitePageSection(
                                "Estimate-only use",
                                "Results are planning estimates designed to help users ask better questions before speaking with local septic professionals.",
                                List.of(
                                        "Outputs are not engineered designs.",
                                        "Outputs are not code-compliance determinations.",
                                        "Outputs are not permit approvals or official state calculations."
                                )
                        ),
                        new SitePageSection(
                                "User responsibility",
                                "Users remain responsible for confirming local permit rules, system feasibility, and contractor qualifications.",
                                List.of(
                                        "County and local authorities may override state-level general guidance.",
                                        "Actual cost depends on site evaluation, system type, access, and local scope.",
                                        "Homebuyers should still request system records and inspection evidence before closing."
                                )
                        ),
                        new SitePageSection(
                                "Commercial use and availability",
                                "The site may evolve, change coverage, or stop accepting quote requests without notice if source coverage, partner availability, or product scope changes.",
                                List.of(
                                        "Content may be updated when sources change or pages are re-verified.",
                                        "Quote matching is not guaranteed in every state or project category.",
                                        "No warranty is made that any estimate range will match a final contractor proposal."
                                )
                        )
                ),
                "Trust the workflow, not fake precision",
                "Use the estimate to narrow the likely range, then verify locally and collect real quotes."
        );
    }

    @GetMapping({"/contact", "/contact/"})
    public String contact(Model model) {
        return renderContactPage(model, new ContactRequestForm(), false, null);
    }

    @PostMapping({"/contact", "/contact/"})
    public String submitContactRequest(
            @Valid @ModelAttribute ContactRequestForm contactRequestForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return renderContactPage(model, contactRequestForm, true, null);
        }

        String requestId = leadStorageService.saveContactRequest(contactRequestForm, "/contact/", request);
        return renderContactPage(model, new ContactRequestForm(), false, requestId);
    }

    @GetMapping({"/for-professionals/records-packet/indiana", "/for-professionals/records-packet/indiana/"})
    public String indianaRecordsPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("indiana")
                .orElseThrow(() -> new StateNotFoundException("indiana"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "indiana")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/indiana"));
        StateMoneyPage buyerPage = researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", "indiana")
                .orElseThrow(() -> new StateNotFoundException("buying-a-house-with-a-septic-system/indiana"));
        String packetUrl = seoService.absoluteUrl(INDIANA_RECORDS_PACKET_PATH);
        List<PageLink> allCountyLinks = countyRecordPageLinks(state.stateCode());
        List<PageLink> countyLinks = java.util.stream.Stream.concat(
                        allCountyLinks.stream().filter(link -> link.path().contains("/howard-county/")),
                        stateRecordsCountyLinks(state.stateCode(), allCountyLinks).stream()
                )
                .distinct()
                .limit(4)
                .toList();
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "Indiana septic records packet for buyer agents and coordinators",
                "Use this share page when the real blocker is the county file, sewer-availability note, or local board history behind an Indiana septic story. The first send should narrow into the records workflow, not a broad cost page.",
                "Records packet",
                "Public noindex handoff for Indiana file checks",
                "This packet is meant to shorten the first buyer or seller explanation when the next real move is county records, existing permits, or local-board confirmation.",
                "The packet should move the recipient into the Indiana records lookup first, then into the county page or official file source that matches the parcel.",
                "Indiana septic records and county file check",
                """
Hi,

Before we rely on the current septic story, start with this Indiana records packet:
%s

Open the Indiana records lookup first. If the parcel is already clearly tied to a county health office, use one of the linked county pages right after that.

The goal is to confirm the file, the local office, and any sewer-availability note before we treat a quote or seller summary as the real answer.
""".formatted(packetUrl),
                new PageLink(
                        recordsPage.title(),
                        recordsPage.path(state.slug()),
                        "Start with the Indiana records lookup before you price the downside. That page is the pinned first move because Indiana's county-first file path changes the next action faster than a broad guide."
                ),
                List.of(
                        new PageLink(
                                buyerPage.title(),
                                buyerPage.path(state.slug()),
                                "Use the Indiana buyer workflow after the file path is clearer and the next question shifts from record retrieval to deal risk."
                        ),
                        new PageLink(
                                "Indiana Septic Guide",
                                "/septic-system-cost-calculator/indiana/",
                                "Use the broader Indiana guide only when you still need statewide permit-path and sewer-gating context around the file."
                        )
                ),
                countyLinks,
                workflowPacketSources(
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A buyer or seller cannot produce a septic file with enough confidence for closing.",
                        "You need a county or local board path before you let the conversation drift into quote mode.",
                        "The parcel story may break if sanitary sewer is available or the local board file contradicts the seller summary."
                ),
                state.recordsToRequest(),
                List.of(
                        "Confirm the county before you send this packet so the recipient can move into the right county page fast.",
                        "Send the Indiana records lookup first, and use the county page only when the parcel already has a clear county office.",
                        "Keep the estimator and quote links out of the first send unless the file is already strong enough to trust."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "Indiana Septic Records Packet for Professionals | SepticPath",
                        "Public noindex Indiana septic records packet for buyer agents and coordinators who need a county-file-first handoff before a quote story.",
                        INDIANA_RECORDS_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/buyer-diligence-packet/new-york", "/for-professionals/buyer-diligence-packet/new-york/"})
    public String newYorkBuyerDiligencePacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("new-york")
                .orElseThrow(() -> new StateNotFoundException("new-york"));
        StateMoneyPage buyerPage = researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", "new-york")
                .orElseThrow(() -> new StateNotFoundException("buying-a-house-with-a-septic-system/new-york"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "new-york")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/new-york"));
        String packetUrl = seoService.absoluteUrl(NEW_YORK_BUYER_PACKET_PATH);
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "New York septic buyer diligence packet for agents and coordinators",
                "Use this when the deal needs Appendix 75-A, county health file, and waiver history clarified before anyone pretends the septic story is routine. The first send should frame the deal risk, then hand off into records.",
                "Buyer diligence packet",
                "Public noindex handoff for New York septic deals",
                "This packet is for buyer-side teams who keep repeating the same file and waiver explanation in New York transactions.",
                "The packet should move the recipient into the New York buyer workflow first, then into the New York records lookup and official file sources.",
                "New York septic due diligence before closing",
                """
Hi,

Start with this New York septic buyer diligence packet:
%s

Open the New York buyer workflow first. It then hands off into the records lookup and the official file path that matters for Appendix 75-A, waiver history, and county health review.

The goal is to settle the file and local authority story before we treat the septic issue like a simple inspection line item.
""".formatted(packetUrl),
                new PageLink(
                        buyerPage.title(),
                        buyerPage.path(state.slug()),
                        "Start with the New York buyer workflow when the deal risk is still broader than one records request. That page pins the first move around buyer diligence, then narrows into the records file."
                ),
                List.of(
                        new PageLink(
                                recordsPage.title(),
                                recordsPage.path(state.slug()),
                                "Use the New York records lookup right after the buyer page when the next move is pulling the Appendix 75-A file, waiver history, or county health record."
                        ),
                        new PageLink(
                                "New York Septic Guide",
                                "/septic-system-cost-calculator/new-york/",
                                "Use the broader New York guide only when you still need statewide design-baseline context around the file."
                        )
                ),
                List.of(),
                workflowPacketSources(
                        buyerPage.officialSourceIds(),
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A buyer needs the septic file story clarified before inspection or repair conversations drift.",
                        "The deal is already moving, but Appendix 75-A, waiver history, or county health review is still unclear.",
                        "You need a reusable buyer-facing explanation that keeps the next move on records instead of generic fear or quote shopping."
                ),
                state.recordsToRequest(),
                List.of(
                        "Use the buyer packet first when the main problem is deal diligence, not a stand-alone records request.",
                        "Expect the next internal click to be the New York records lookup, not the broad state guide.",
                        "Do not lead with estimate ranges until the buyer understands the file quality and waiver story."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "New York Septic Buyer Diligence Packet for Professionals | SepticPath",
                        "Public noindex New York septic buyer diligence packet for agents and coordinators who need an Appendix 75-A and county-file-first handoff.",
                        NEW_YORK_BUYER_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/inspection-letter-packet/tennessee", "/for-professionals/inspection-letter-packet/tennessee/"})
    public String tennesseeInspectionLetterPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("tennessee")
                .orElseThrow(() -> new StateNotFoundException("tennessee"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "tennessee")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/tennessee"));
        String packetUrl = seoService.absoluteUrl(TENNESSEE_INSPECTION_PACKET_PATH);
        List<PageLink> countyLinks = countyRecordPageLinks(state.stateCode()).stream().limit(4).toList();
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "Tennessee septic inspection-letter packet for buyer agents and lenders",
                "Use this share page when a Tennessee sale, mortgage, or subdivision file needs the status of an existing septic system documented before the team treats the listing or a contractor quote as the answer. TDEC identifies inspection letters as a routine transaction service, but contract counties use their own route.",
                "Inspection-letter packet",
                "Public noindex handoff for Tennessee transaction files",
                "This packet gives a buyer agent, listing coordinator, lender, or owner one clean first send: identify whether the parcel uses the TDEC route or a contract-county route, then pull the inspection-letter and permit-file story together.",
                "The packet should move the recipient into the Tennessee records route first, then into the named county path when the parcel belongs to a contract county or already has a local file owner.",
                "Tennessee septic inspection letter and permit file check",
                """
Hi,

Before we rely on the current septic story for this Tennessee transaction, use this inspection-letter packet:
%s

Start with the Tennessee records route. It will identify whether the parcel belongs to a TDEC route or a contract-county route, then help us request the construction permit, repair history, and inspection-letter status.

The goal is to document the existing-system file for the sale or mortgage before the conversation turns into a generic inspection or replacement quote.
""".formatted(packetUrl),
                new PageLink(
                        recordsPage.title(),
                        recordsPage.path(state.slug()),
                        "Start with the Tennessee records route because the first real decision is whether the property uses a contract-county office or the TDEC service lane for the inspection-letter and permit file."
                ),
                List.of(
                        new PageLink(
                                "Septic Inspection Letter",
                                "/septic-inspection-letter/",
                                "Use this after the Tennessee route when the team needs to separate an inspection-letter request from the broader permit-copy and records workflow."
                        ),
                        new PageLink(
                                "Tennessee Septic Guide",
                                "/septic-system-cost-calculator/tennessee/",
                                "Use the statewide guide only after the transaction team understands the file owner, inspection-letter path, and repair-versus-replacement question."
                        )
                ),
                countyLinks,
                workflowPacketSources(
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A sale, mortgage, or subdivision file needs written status for an existing septic system.",
                        "The listing, lender, or buyer file has not established whether a permit, repair history, or inspection letter exists.",
                        "The parcel may belong to a Tennessee contract county, so the state online-service route may not be the right first submission."
                ),
                state.recordsToRequest(),
                List.of(
                        "Confirm the county before sending this packet because Tennessee contract counties use their own septic-service routes.",
                        "Send the Tennessee records route first, then use a linked county page only when the parcel has a clear local office path.",
                        "Keep replacement pricing out of the first send until the permit, repair, and inspection-letter story is documented."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "Tennessee Septic Inspection Letter Packet for Professionals | SepticPath",
                        "Public noindex Tennessee transaction packet for buyer agents, lenders, and coordinators who need an inspection-letter and permit-file-first handoff.",
                        TENNESSEE_INSPECTION_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/listing-permit-packet/north-carolina", "/for-professionals/listing-permit-packet/north-carolina/"})
    public String northCarolinaListingPermitPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("north-carolina")
                .orElseThrow(() -> new StateNotFoundException("north-carolina"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "north-carolina")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/north-carolina"));
        String packetUrl = seoService.absoluteUrl(NORTH_CAROLINA_LISTING_PACKET_PATH);
        List<PageLink> countyLinks = countyRecordPageLinks(state.stateCode()).stream().limit(4).toList();
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "North Carolina septic listing-permit packet for brokers and coordinators",
                "Use this before a North Carolina listing launch, price change, showing, or buyer reply when a bedroom count and the septic permit file may not match. The North Carolina Real Estate Commission says a broker should not advertise more bedrooms than the septic permit allows, so the first send has to confirm the county file rather than normalize an unsupported bedroom number.",
                "Listing-permit packet",
                "Public noindex handoff for North Carolina bedroom-capacity checks",
                "This packet turns a possible listing-capacity mismatch into a narrow records task: compare the listing and official permit, ask the county health department for the controlling file, and preserve the written response before marketing or negotiating around bedroom count.",
                "The packet should open the bedroom permit checker with North Carolina selected, then move the recipient into the North Carolina county-records route that owns the actual permit file.",
                "North Carolina septic bedroom permit and listing file check",
                """
Hi,

Before we rely on the advertised bedroom count for this North Carolina property, use this listing-permit packet:
%s

Start with the North Carolina bedroom permit checker. Then use the North Carolina records route to ask the county health department for the controlling septic permit, approved bedroom count, approval history, and any written clarification.

The goal is to keep the listing and transaction file tied to the permit record before we market, negotiate, or describe the property capacity.
""".formatted(packetUrl),
                new PageLink(
                        "North Carolina Septic Bedroom Permit Checker",
                        "/septic-bedroom-permit-checker/?state=NC",
                        "Start by comparing the advertised bedrooms with the official septic permit count. The checker flags a records question only, then hands the user into the county file route for the controlling answer."
                ),
                List.of(
                        new PageLink(
                                recordsPage.title(),
                                recordsPage.path(state.slug()),
                                "Use the North Carolina records page right after the checker to find the county environmental-health file owner and request the permit, authorization, and operation record."
                        ),
                        new PageLink(
                                "North Carolina Septic Guide",
                                "/septic-system-cost-calculator/north-carolina/",
                                "Use the broader North Carolina guide only when the permit ladder or site-approval story still needs context after the bedroom question is clear."
                        )
                ),
                countyLinks,
                workflowPacketSources(
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A listing, seller, tax, or inspection source does not clearly match the septic permit bedroom count.",
                        "A buyer, broker, coordinator, or lender needs the county health file before relying on the marketed capacity.",
                        "The permit may be under the original builder or owner, so the current address alone may not resolve the file."
                ),
                List.of(
                        "The septic permit or approval showing the supported bedroom count or design capacity.",
                        "Any improvement permit, construction authorization, operation permit, certificate of completion, amendment, or prior site record tied to the property.",
                        "A written county response when the permit is missing, conflicts with another record, or is filed under an original owner or builder."
                ),
                List.of(
                        "Send the preselected North Carolina checker first, then preserve the result as a records question rather than a compliance conclusion.",
                        "Send the county records route when the checker flags a mismatch, missing permit, or conflicting source.",
                        "Do not use a tax card, seller statement, or room count alone as a septic-capacity conclusion."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "North Carolina Septic Listing Permit Packet for Professionals | SepticPath",
                        "Public noindex North Carolina handoff for brokers and coordinators who need to compare listing bedrooms with the county septic permit file.",
                        NORTH_CAROLINA_LISTING_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/permit-prep-packet/south-carolina", "/for-professionals/permit-prep-packet/south-carolina/"})
    public String southCarolinaPermitPrepPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("south-carolina")
                .orElseThrow(() -> new StateNotFoundException("south-carolina"));
        StateMoneyPage permitPage = researchDataService.findPublicStateMoneyPage("septic-permit-process", "south-carolina")
                .orElseThrow(() -> new StateNotFoundException("septic-permit-process/south-carolina"));
        String packetUrl = seoService.absoluteUrl(SOUTH_CAROLINA_PERMIT_PACKET_PATH);
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "South Carolina septic permit prep packet for installers and coordinators",
                "Use this when the real blocker is D-1740, site review, permit-copy retrieval, or county routing in South Carolina. The first send should narrow into the permit workflow before anyone treats the project like a standard install quote.",
                "Permit prep packet",
                "Public noindex handoff for South Carolina permit prep",
                "This packet is for teams that keep re-explaining South Carolina permit routing, permit-copy retrieval, and local office handoff before the job is truly permit-ready.",
                "The packet should move the recipient into the South Carolina permit workflow first, then into the permit links and county routing inside that page.",
                "South Carolina septic permit prep and D-1740 path",
                """
Hi,

Use this South Carolina permit prep packet as the first reference:
%s

Open the South Carolina permit workflow first. It points to the permit links and county routing that matter for D-1740, site review, and permit-copy questions.

The goal is to settle the permit path before we frame the project as a normal install or replacement quote.
""".formatted(packetUrl),
                new PageLink(
                        permitPage.title(),
                        permitPage.path(state.slug()),
                        "Start with the South Carolina permit workflow when D-1740, local office routing, or permit-copy retrieval is the real bottleneck."
                ),
                List.of(
                        new PageLink(
                                "South Carolina Septic Guide",
                                "/septic-system-cost-calculator/south-carolina/",
                                "Use the broader South Carolina guide only when you still need statewide permit context around the local office path."
                        )
                ),
                List.of(),
                workflowPacketSources(
                        permitPage.officialSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.recordsLookupSourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "The recipient still needs the permit sequence clarified before site work or contractor scheduling starts.",
                        "D-1740, site approval, or permit-copy retrieval is still an open question.",
                        "You need a sendable explanation that narrows the recipient into the permit path instead of a broad cost discussion."
                ),
                permitPage.quotePrepChecklist(),
                List.of(
                        "Send this before an estimate when D-1740 or permit-copy friction is still unresolved.",
                        "Expect the next move to be the permit workflow page and then the official source links inside it.",
                        "Keep quote and estimator links out of the first handoff until the permit path is actually clearer."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "South Carolina Septic Permit Prep Packet for Professionals | SepticPath",
                        "Public noindex South Carolina septic permit prep packet for installers and coordinators who need a D-1740 and permit-path-first handoff.",
                        SOUTH_CAROLINA_PERMIT_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping(value = {"/robots.txt"}, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robotsTxt() {
        return sitemapService.robotsTxt();
    }

    @GetMapping(value = {"/sitemap.xml"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapXml() {
        return sitemapService.sitemapXml();
    }

    @GetMapping(value = {"/sitemap-county.xml"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String countySitemapXml() {
        return sitemapService.countySitemapXml();
    }

    @GetMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculator(
            @RequestParam(name = "state", required = false) String stateCode,
            @RequestParam(name = "projectType", required = false) String projectType,
            @RequestParam(name = "bedrooms", required = false) Integer bedrooms,
            @RequestParam(name = "recordsMode", defaultValue = "false") boolean recordsMode,
            @RequestParam(name = "recordSystemType", defaultValue = "") String recordSystemType,
            @RequestParam(name = "recordTankCapacity", defaultValue = "") String recordTankCapacity,
            @RequestParam(name = "recordDesignFlow", defaultValue = "") String recordDesignFlow,
            @RequestParam(name = "sourcePageHint", required = false) String sourcePageHint,
            @RequestParam(name = "quoteMode", defaultValue = "false") boolean quoteMode,
            Model model
    ) {
        EstimateForm estimateForm = new EstimateForm();
        if (stateCode != null && usStateDirectoryService.findByCode(stateCode).isPresent()) {
            estimateForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        if (projectType != null) {
            estimateForm.setProjectType(ProjectType.fromValue(projectType).value());
        }
        if (bedrooms != null && bedrooms >= 1 && bedrooms <= 10) {
            estimateForm.setBedrooms(bedrooms);
        }
        if (isValidSourcePageHint(sourcePageHint)) {
            estimateForm.setSourcePageHint(sourcePageHint);
        }
        model.addAttribute("recordsMode", recordsMode);
        model.addAttribute("recordSystemType", boundedRecordContext(recordSystemType));
        model.addAttribute("recordTankCapacity", boundedRecordContext(recordTankCapacity));
        model.addAttribute("recordDesignFlow", boundedRecordContext(recordDesignFlow));
        return renderCalculator(model, estimateForm, null, QuoteLeadForm.fromEstimateForm(estimateForm), null, false, quoteMode);
    }

    @GetMapping({"/septic-tank-size-estimator", "/septic-tank-size-estimator/"})
    public String tankSizeEstimator(
            @RequestParam(name = "state", required = false) String stateCode,
            Model model
    ) {
        TankSizeForm tankSizeForm = new TankSizeForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).filter(StateProfile::isPublished).isPresent()) {
            tankSizeForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        return renderTankSizeEstimator(model, tankSizeForm, null);
    }

    @PostMapping({"/septic-tank-size-estimator", "/septic-tank-size-estimator/"})
    public String calculateTankSize(@ModelAttribute TankSizeForm tankSizeForm, Model model) {
        TankSizeEstimatorResult result = tankSizeEstimatorService.estimate(tankSizeForm);
        return renderTankSizeEstimator(model, tankSizeForm, result);
    }

    @GetMapping({"/septic-pump-schedule-estimator", "/septic-pump-schedule-estimator/"})
    public String pumpScheduleEstimator(Model model) {
        return renderPumpScheduleEstimator(model, new PumpScheduleForm(), null);
    }

    @PostMapping({"/septic-pump-schedule-estimator", "/septic-pump-schedule-estimator/"})
    public String calculatePumpSchedule(@ModelAttribute PumpScheduleForm pumpScheduleForm, Model model) {
        PumpScheduleResult result = pumpScheduleService.estimate(pumpScheduleForm);
        return renderPumpScheduleEstimator(model, pumpScheduleForm, result);
    }

    @GetMapping({"/drain-field-estimator", "/drain-field-estimator/"})
    public String drainfieldEstimator(
            @RequestParam(name = "state", required = false) String stateCode,
            Model model
    ) {
        DrainfieldEstimatorForm drainfieldEstimatorForm = new DrainfieldEstimatorForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).filter(StateProfile::isPublished).isPresent()) {
            drainfieldEstimatorForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        return renderDrainfieldEstimator(model, drainfieldEstimatorForm, null);
    }

    @PostMapping({"/drain-field-estimator", "/drain-field-estimator/"})
    public String calculateDrainfield(@ModelAttribute DrainfieldEstimatorForm drainfieldEstimatorForm, Model model) {
        DrainfieldEstimatorResult result = drainfieldEstimatorService.estimate(drainfieldEstimatorForm);
        return renderDrainfieldEstimator(model, drainfieldEstimatorForm, result);
    }

    @PostMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculate(
            @ModelAttribute EstimateForm estimateForm,
            @RequestParam(name = "recordsMode", defaultValue = "false") boolean recordsMode,
            @RequestParam(name = "recordSystemType", defaultValue = "") String recordSystemType,
            @RequestParam(name = "recordTankCapacity", defaultValue = "") String recordTankCapacity,
            @RequestParam(name = "recordDesignFlow", defaultValue = "") String recordDesignFlow,
            Model model
    ) {
        if (estimateForm.getStateCode() == null
                || usStateDirectoryService.findByCode(estimateForm.getStateCode()).isEmpty()) {
            model.addAttribute("calculatorError", "Choose the property state before showing an estimate.");
            model.addAttribute("recordsMode", recordsMode);
            model.addAttribute("recordSystemType", boundedRecordContext(recordSystemType));
            model.addAttribute("recordTankCapacity", boundedRecordContext(recordTankCapacity));
            model.addAttribute("recordDesignFlow", boundedRecordContext(recordDesignFlow));
            return renderCalculator(model, estimateForm, null, QuoteLeadForm.fromEstimateForm(estimateForm), null, false, false);
        }
        EstimatorResult result = estimatorService.estimate(estimateForm);
        model.addAttribute("recordsMode", recordsMode);
        model.addAttribute("recordSystemType", boundedRecordContext(recordSystemType));
        model.addAttribute("recordTankCapacity", boundedRecordContext(recordTankCapacity));
        model.addAttribute("recordDesignFlow", boundedRecordContext(recordDesignFlow));
        return renderCalculator(model, estimateForm, result, QuoteLeadForm.fromEstimateForm(estimateForm), null, false, true);
    }

    private String boundedRecordContext(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().substring(0, Math.min(value.trim().length(), 120));
    }

    @PostMapping({"/quote-request", "/quote-request/"})
    public String submitQuote(
            @Valid @ModelAttribute QuoteLeadForm quoteLeadForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        EstimateForm estimateForm = quoteLeadForm.toEstimateForm();
        EstimatorResult result = estimatorService.estimate(estimateForm);

        if (bindingResult.hasErrors()) {
            return renderCalculator(model, estimateForm, result, quoteLeadForm, null, true, true);
        }

        String leadId = leadStorageService.saveQuoteLead(
                quoteLeadForm,
                estimateForm,
                result,
                "/septic-system-cost-calculator/",
                request
        );
        QuoteLeadForm clearedQuoteForm = QuoteLeadForm.fromEstimateForm(estimateForm);
        return renderCalculator(model, estimateForm, result, clearedQuoteForm, leadId, false, true);
    }

    @PostMapping(value = {"/events/nav-click", "/events/nav-click/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> recordNavigationClick(
            @RequestBody NavigationClickForm navigationClickForm,
            HttpServletRequest request
    ) {
        if (!isTrackableInternalPath(navigationClickForm.sourcePage())
                || !isTrackableNavigationTarget(navigationClickForm.targetPath())) {
            return ResponseEntity.noContent().build();
        }

        leadStorageService.saveNavigationClick(
                navigationClickForm.sourcePage(),
                navigationClickForm.sourceContext(),
                navigationClickForm.targetPath(),
                navigationClickForm.targetType(),
                navigationClickForm.targetLabel(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = {"/events/artifact-action", "/events/artifact-action/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> recordArtifactAction(
            @RequestBody ArtifactActionForm artifactActionForm,
            HttpServletRequest request
    ) {
        if (!isTrackableInternalPath(artifactActionForm.sourcePage())
                || !isTrackableArtifactAction(artifactActionForm.sourceContext())
                || !isTrackableArtifactAction(artifactActionForm.action())
                || !isTrackableArtifactAction(artifactActionForm.artifactType())) {
            return ResponseEntity.noContent().build();
        }

        leadStorageService.saveArtifactAction(
                artifactActionForm.sourcePage(),
                artifactActionForm.sourceContext(),
                artifactActionForm.action(),
                artifactActionForm.artifactType(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = {"/events/workflow-stage", "/events/workflow-stage/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> recordWorkflowStage(
            @RequestBody WorkflowStageForm workflowStageForm,
            HttpServletRequest request
    ) {
        if (!isTrackableInternalPath(workflowStageForm.sourcePage())
                || !isTrackableArtifactAction(workflowStageForm.sourceContext())
                || !isTrackableWorkflowRunId(workflowStageForm.workflowRunId())
                || !isTrackableCountyKey(workflowStageForm.countyKey())
                || !isTrackableWorkflowStage(workflowStageForm.stage())
                || !isTrackableWorkflowOutcome(workflowStageForm.outcome())) {
            return ResponseEntity.noContent().build();
        }

        leadStorageService.saveWorkflowStage(
                workflowStageForm.sourcePage(),
                workflowStageForm.sourceContext(),
                workflowStageForm.workflowRunId(),
                workflowStageForm.countyKey(),
                workflowStageForm.stage(),
                workflowStageForm.outcome(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = {"/events/web-vital", "/events/web-vital/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> recordWebVital(
            @RequestBody WebVitalForm webVitalForm,
            HttpServletRequest request
    ) {
        if (!isTrackableInternalPath(webVitalForm.sourcePage())
                || !isTrackableWebVital(webVitalForm.metricName(), webVitalForm.value())) {
            return ResponseEntity.noContent().build();
        }

        leadStorageService.saveWebVital(
                webVitalForm.metricName(),
                webVitalForm.value(),
                webVitalForm.rating(),
                webVitalForm.sourcePage(),
                webVitalForm.navigationType(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/septic-system-cost-calculator/{stateSlug}", "/septic-system-cost-calculator/{stateSlug}/"})
    public String stateGuide(@PathVariable String stateSlug, Model model) {
        Optional<StateProfile> stateProfile = researchDataService.findStateBySlug(stateSlug);
        if (stateProfile.filter(StateProfile::isPublished).isPresent()) {
            return renderPublishedStateGuide(model, stateProfile.orElseThrow());
        }
        UsStateDirectoryService.UsStateReference stateReference = stateProfile
                .map(state -> new UsStateDirectoryService.UsStateReference(state.stateCode(), state.stateName()))
                .or(() -> usStateDirectoryService.findBySlug(stateSlug))
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        return renderQueuedStateGuide(model, stateReference);
    }

    private String renderPublishedStateGuide(Model model, StateProfile state) {
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        List<StateRuleFactView> stateRuleFacts = stateRuleFactViews(state.stateCode());
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<CoreStateComparisonRow> coreStateComparisonRows = coreStateComparisonRows(state);
        List<PageLink> countyRecordLinks = countyRecordPageLinks(state.stateCode());
        List<StateMoneyPage> sortedStateMoneyPages = researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                .filter(page -> !page.isCanonicalAlias())
                .sorted(Comparator
                        .comparingInt((StateMoneyPage page) -> stateMoneyPagePriorityScore(state, page))
                        .reversed()
                        .thenComparing(StateMoneyPage::title))
                .toList();
        List<StateMoneyPage> indexableStateMoneyPages = sortedStateMoneyPages.stream()
                .filter(page -> publishingPolicyService.isIndexableStateMoneyPage(page, state))
                .toList();
        StateCountyWorkflowSynthesisView guideCountyWorkflowSynthesis = sortedStateMoneyPages.stream()
                .filter(page -> "septic-records-checklist".equals(page.contentSlug()))
                .findFirst()
                .map(page -> stateCountyWorkflowSynthesis(page, state))
                .orElse(null);
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());

        model.addAttribute("page", seoService.stateGuide(state, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", localAuthoritySources.stream().findFirst().orElse(null));
        model.addAttribute("primaryRecordsLookupSource", recordsLookupSources.stream().findFirst().orElse(null));
        model.addAttribute("stateMoneyPages", indexableStateMoneyPages);
        model.addAttribute("featuredStateMoneyPages", indexableStateMoneyPages.stream().limit(5).toList());
        model.addAttribute("plannedStateMoneyPages", sortedStateMoneyPages.stream()
                .filter(page -> !publishingPolicyService.isIndexableStateMoneyPage(page, state))
                .toList());
        model.addAttribute("featuredStateWorkflowLinks", indexableStateMoneyPages.stream()
                .limit(5)
                .map(page -> stateGuideHeroWorkflowLink(page, state))
                .toList());
        model.addAttribute("stateRuleFacts", stateRuleFacts);
        model.addAttribute("guideFaqs", seoService.stateGuideFaqs(state));
        model.addAttribute("guideHeading", seoService.stateGuideHeading(state));
        model.addAttribute("stateQuickAnswer", seoService.stateGuideQuickAnswer(state));
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("alabamaPercPlanningRange", statePlanningRange("AL", "perc_test"));
        model.addAttribute("alabamaCountyCostRoutes", "AL".equals(state.stateCode())
                ? alabamaCountyCostRoutes()
                : List.of());
        model.addAttribute("coreStateComparisonRows", coreStateComparisonRows);
        model.addAttribute("countyRecordLinks", countyRecordLinks);
        model.addAttribute("featuredCountyRecordLinks", countyRecordLinks.stream().limit(30).toList());
        model.addAttribute("guideOfficialFilePathRows", guideOfficialFilePathRows(
                state,
                localAuthoritySources.stream().findFirst().orElse(null),
                recordsLookupSources.stream().findFirst().orElse(null),
                guideCountyWorkflowSynthesis
        ));
        model.addAttribute("guideGrowthSearchLinks", guideGrowthSearchLinks(state, countyRecordLinks));
        model.addAttribute("guideGrowthAnswerRows", guideGrowthAnswerRows(state, guideCountyWorkflowSynthesis));
        researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                .ifPresentOrElse(
                        recordsPage -> {
                            model.addAttribute("stateRecordsLookupPath", recordsPage.path(state.slug()));
                            model.addAttribute("stateRecordsLookupTitle", recordsPage.title());
                        },
                        () -> {
                            model.addAttribute("stateRecordsLookupPath", null);
                            model.addAttribute("stateRecordsLookupTitle", "");
                        }
                );
        model.addAttribute("guideCountyWorkflowSynthesis", guideCountyWorkflowSynthesis);
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Cites " + sources.size()
                + " official sources and " + countyRecordLinks.size()
                + " live county workflow pages. The date below is the latest page or source review activity.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", "This " + state.stateName()
                + " guide is maintained as conservative homeowner guidance and changes when its linked sources or local workflow notes change.");
        return "pages/state-guide";
    }

    private List<AlabamaCountyCostRouteView> alabamaCountyCostRoutes() {
        String stateForms = "https://www.alabamapublichealth.gov/onsite/forms.html";
        return List.of(
                new AlabamaCountyCostRouteView(
                        "Jefferson County",
                        "A county-specific query produced a Google Search click in the reviewed 90-day window.",
                        "County application available",
                        "https://jcdh.org/SitePages/Programs-Services/EnvironmentalHealth/CommunityEnvironmentalProtection/OnsiteSewDisp.aspx",
                        "Open JCDH onsite sewage instructions",
                        "205-930-1230",
                        "https://jcdh.org/SitePages/Misc/PdfViewer?AdminUploadId=370",
                        "Open Jefferson County permit application",
                        "Prepare the applicant contact, property location, subdivision and lot data, residence type, bedroom count, water source, sewer distance, and plot plan. A registered engineer, land surveyor, or soil classifier completes the professional soil section.",
                        "JCDH reviews professional site data and performs its own field review. Its page says fees vary; do not substitute the statewide $150-$250 public-evaluation band.",
                        "What is the current fee, how many copies are required, and can I submit the application digitally or must I deliver the listed copies?",
                        ""
                ),
                new AlabamaCountyCostRouteView(
                        "Madison County",
                        "Madison septic-permit and records queries are already appearing in Search Console.",
                        "Environmental office confirmation",
                        "https://www.alabamapublichealth.gov/madison/environmental-services.html",
                        "Open Madison Environmental Services",
                        "256-533-8726",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the property address or parcel ID, owner name, project type, bedroom count, sewer-availability answer, existing Permit to Install or Approval for Use, and any soil report or plot plan.",
                        "The reviewed county page confirms onsite permitting and inspection but does not publish whether Madison participates in the $150-$250 public Site Evaluation Program.",
                        "Does Madison County perform the public site evaluation, what is the current application fee, and which form and submission channel should I use for this parcel?",
                        "/septic-records-checklist/alabama/madison-county/"
                ),
                new AlabamaCountyCostRouteView(
                        "Morgan County",
                        "A Morgan County septic-permit query is already appearing in Search Console.",
                        "Call Environmental",
                        "https://www.alabamapublichealth.gov/morgan/contact.html",
                        "Open Morgan County contact page",
                        "256-340-6563",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the parcel address, owner, new-versus-repair scope, bedroom count, sewer answer, existing file number, and any prior soil or site plan before calling.",
                        "The official contact page routes septic requests to Environmental but does not publish public-evaluation participation or a county-specific fee.",
                        "For this parcel, do you provide the public site evaluation or require a private registered professional, and what must accompany the application?",
                        "/septic-records-checklist/alabama/morgan-county/"
                ),
                new AlabamaCountyCostRouteView(
                        "Blount County",
                        "A Blount County query surfaced on the Alabama cost page in Search Console.",
                        "Call Environmental",
                        "https://www.alabamapublichealth.gov/blount/environmental-services.html",
                        "Open Blount Environmental Services",
                        "205-274-2120",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the property address or parcel ID, owner, dwelling type, bedrooms, sewer answer, project type, and every existing permit, Approval for Use, soil report, or plot plan.",
                        "Blount publishes onsite sewage application and permit responsibility, but the reviewed source does not publish its Site Evaluation Program status or exact fee.",
                        "Which current application applies, does the county perform the site evaluation, and where do I submit the completed packet and fee?",
                        "/septic-records-checklist/alabama/blount-county/"
                ),
                new AlabamaCountyCostRouteView(
                        "Cullman County",
                        "A Cullman query surfaced on the Alabama cost page in Search Console.",
                        "Call Environmental",
                        "https://www.alabamapublichealth.gov/cullman/contact.html",
                        "Open Cullman County contact page",
                        "256-734-0243",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the parcel identity, owner, project and dwelling details, sewer answer, existing permit history, and any professional soil or plot-plan material.",
                        "Cullman lists onsite sewage applications and permits. Its reviewed pages do not publish public-evaluation participation or a parcel-specific fee.",
                        "Does the Environmental Office perform the public site evaluation, what is the exact fee, and do you accept the CEP-2 by mail, in person, or another channel?",
                        "/septic-records-checklist/alabama/cullman-county/"
                ),
                new AlabamaCountyCostRouteView(
                        "Butler County",
                        "A Butler County query surfaced on the Alabama cost page in Search Console.",
                        "Temporary office / call first",
                        "https://www.alabamapublichealth.gov/butler/contact.html",
                        "Open Butler County contact page",
                        "334-382-3154",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the property and owner details, project type, bedroom count, sewer answer, existing septic file, and soil or plot-plan evidence. Confirm the current Environmental/WIC location before travel.",
                        "Butler publishes onsite sewage applications and permits, but not its Site Evaluation Program participation or exact septic fee. The county is operating from updated locations after the former building fire.",
                        "Which Environmental location handles this parcel, do you provide the site evaluation, and what form, fee, copies, and submission method are required?",
                        ""
                ),
                new AlabamaCountyCostRouteView(
                        "Cherokee County",
                        "A Cherokee County query surfaced on the Alabama cost page in Search Console.",
                        "Call Environmental",
                        "https://www.alabamapublichealth.gov/cherokee/environmental-services.html",
                        "Open Cherokee Environmental Services",
                        "256-927-7322",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the parcel and owner identity, system purpose, dwelling and bedroom details, sewer answer, existing permits, and any site or soil evidence.",
                        "Cherokee confirms onsite sewage and septic permitting, but the reviewed sources do not publish public-evaluation participation or the exact fee.",
                        "Does Cherokee County perform the public site evaluation, which current form applies, and where should I send or deliver the completed packet?",
                        ""
                ),
                new AlabamaCountyCostRouteView(
                        "Dallas County",
                        "A Dallas County query surfaced on the Alabama cost page in Search Console.",
                        "Call Environmental Health",
                        "https://www.alabamapublichealth.gov/dallas/environmental-services.html",
                        "Open Dallas Environmental Office",
                        "334-872-5887",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare parcel location, owner, project scope, bedroom count, sewer answer, any existing Approval for Use or permit number, and soil or plot-plan material.",
                        "Dallas confirms that Community Environmental Protection handles onsite sewage. The reviewed source does not publish Site Evaluation Program participation or a septic fee.",
                        "For this parcel, is a county site evaluation available, what fee applies, and should the completed form be mailed, delivered, faxed, or submitted another way?",
                        ""
                ),
                new AlabamaCountyCostRouteView(
                        "Covington County",
                        "An Andalusia perc-test query surfaced on the Alabama cost page in Search Console.",
                        "Call Health Department / Environmental",
                        "https://www.alabamapublichealth.gov/covington/contact.html",
                        "Open Covington County contact page",
                        "334-222-1175",
                        stateForms,
                        "Open current ADPH CEP forms",
                        "Prepare the property address or parcel ID, owner, project type, dwelling and bedroom details, sewer answer, existing permit history, and any soil report or plot plan.",
                        "Covington lists onsite sewage applications and permits, but the reviewed pages do not publish public-evaluation participation or the exact fee.",
                        "Does Covington perform the public site evaluation for this parcel, what is the current fee, and what submission channel and attachments are required?",
                        ""
                )
        );
    }

    private String renderQueuedStateGuide(Model model, UsStateDirectoryService.UsStateReference stateReference) {
        Optional<StateQueuePlan> queuePlan = stateQueuePlanService.findByStateCode(stateReference.stateCode());
        java.util.ArrayList<String> starterPaths = new java.util.ArrayList<>();
        queuePlan.map(StateQueuePlan::recommendedPath).ifPresent(starterPaths::add);
        for (String path : List.of(
                "/septic-permit-process/",
                "/septic-records-checklist/",
                "/buying-a-house-with-a-septic-system/",
                "/septic-replacement-cost/",
                "/perc-test-cost/"
        )) {
            if (!starterPaths.contains(path)) {
                starterPaths.add(path);
            }
        }
        model.addAttribute("page", seoService.queuedStateGuide(stateReference.stateName(), stateReference.slug()));
        model.addAttribute("stateCode", stateReference.stateCode());
        model.addAttribute("stateName", stateReference.stateName());
        model.addAttribute("queuePlan", queuePlan.map(plan -> stateQueuePlanView(plan, stateReference)).orElse(null));
        model.addAttribute("starterLinks", pageLinks(starterPaths, "septic-system-cost-calculator", null));
        model.addAttribute("benchmarkGuides", pageLinks(
                CORE_STATE_CODES.stream()
                        .map(researchDataService::findStateByCode)
                        .flatMap(Optional::stream)
                        .map(state -> "/septic-system-cost-calculator/" + state.slug() + "/")
                        .toList(),
                "septic-system-cost-calculator",
                null
        ));
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - researchDataService.getPublicStateProfiles().size(), 0));
        return "pages/state-guide-queued";
    }

    @GetMapping({
            "/septic-replacement-cost", "/septic-replacement-cost/",
            "/septic-tank-size", "/septic-tank-size/",
            "/perc-test-cost", "/perc-test-cost/",
            "/drain-field-replacement-cost", "/drain-field-replacement-cost/",
            "/failed-perc-test-septic", "/failed-perc-test-septic/",
            "/septic-replacement-area", "/septic-replacement-area/",
            "/wet-yard-over-septic-drain-field", "/wet-yard-over-septic-drain-field/",
            "/septic-pumping-cost", "/septic-pumping-cost/",
            "/septic-inspection-cost", "/septic-inspection-cost/",
            "/buying-a-house-with-a-septic-system", "/buying-a-house-with-a-septic-system/",
            "/how-to-find-septic-records-online", "/how-to-find-septic-records-online/",
            "/septic-records-by-county", "/septic-records-by-county/",
            "/septic-permit-search-by-address", "/septic-permit-search-by-address/",
            "/septic-permit-records-request", "/septic-permit-records-request/",
            "/septic-records-request-builder", "/septic-records-request-builder/",
            "/septic-as-built-records", "/septic-as-built-records/",
            "/septic-tank-location-records", "/septic-tank-location-records/",
            "/septic-inspection-letter", "/septic-inspection-letter/",
            "/official-septic-lookup-tools", "/official-septic-lookup-tools/",
            "/tdec-septic-records", "/tdec-septic-records/",
            "/north-carolina-septic-permit-lookup", "/north-carolina-septic-permit-lookup/",
            "/texas-ossf-records-search", "/texas-ossf-records-search/",
            "/florida-ostds-permit-lookup", "/florida-ostds-permit-lookup/",
            "/dhec-septic-permit-lookup", "/dhec-septic-permit-lookup/",
            "/septic-permit-lookup", "/septic-permit-lookup/",
            "/septic-permit-process", "/septic-permit-process/",
            "/septic-records-checklist", "/septic-records-checklist/",
            "/septic-transfer-compliance", "/septic-transfer-compliance/"
    })
    public String contentPage(org.springframework.web.context.request.WebRequest request, Model model) {
        String path = request.getDescription(false).replace("uri=", "");
        String slug = path.replaceFirst("^/", "").replaceFirst("/$", "");
        ContentPage contentPage = researchDataService.findPublicContentPage(slug)
                .orElseThrow(() -> new StateNotFoundException(slug));
        List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries = rankedStateEntriesForContentPage(contentPage);
        List<StateMoneyPageLink> stateMoneyPageLinks = rankedStateEntries.stream()
                .map(entry -> new StateMoneyPageLink(
                        entry.getKey().title(),
                        entry.getValue().stateName(),
                        entry.getValue().stateCode(),
                        stateSurfaceRouteTitle(entry.getKey(), entry.getValue()),
                        entry.getKey().path(entry.getValue().slug()),
                        stateSurfaceSignalView(contentPage, entry.getKey(), entry.getValue())))
                .toList();
        List<ContentEvidenceLaneView> contentEvidenceLanes = rankedStateEntries.stream()
                .limit(6)
                .map(entry -> contentEvidenceLane(entry.getKey(), entry.getValue()))
                .toList();
        ContentWorkflowCoverageView contentWorkflowCoverage = contentWorkflowCoverage(contentPage, rankedStateEntries);
        List<PageLink> internalLinks = pageLinks(contentPage.internalLinkTargets(), contentPage.slug(), null);
        boolean permitLookupSurface = isPermitLookupHub(contentPage);
        boolean fanoutRestrictedSurface = PERMIT_LOOKUP_SLUG.equals(contentPage.slug());
        int stateSpecificRenderLimit = fanoutRestrictedSurface ? 18 : stateMoneyPageLinks.size();
        List<StateMoneyPageLink> renderedStateMoneyPageLinks = stateMoneyPageLinks.stream()
                .limit(stateSpecificRenderLimit)
                .toList();
        List<StateProfile> renderedStates = renderedStatesForContentPage(
                contentPage,
                fanoutRestrictedSurface ? 18 : Integer.MAX_VALUE);
        List<PageLink> permitLookupCountyLinks = renderedPermitLookupCountyLinks(
                contentPage,
                permitLookupCountyLaunchpadLinks(contentPage),
                fanoutRestrictedSurface);
        List<CountyRouteClusterView> countyRouteClusters = permitLookupSurface
                ? countyRouteClusters(contentPage, fanoutRestrictedSurface ? 4 : 5, fanoutRestrictedSurface ? 2 : 3)
                : List.of();
        List<CountyFinderLinkView> countyFinderLinks = permitLookupSurface
                ? countyFinderLinksForContentPage(contentPage, fanoutRestrictedSurface ? 18 : 12)
                : List.of();
        List<CountyFinderLinkView> directOnlineCountyFinderLinks = RECORDS_BY_COUNTY_SLUG.equals(contentPage.slug())
                ? directOnlineCountyFinderLinks()
                : List.of();
        List<PageLink> renderedInternalLinks = renderedInternalLinks(contentPage, internalLinks, fanoutRestrictedSurface);
        List<CountyWorkflowFieldView> contentOfficialFilePathRows = contentOfficialFilePathRows(
                contentPage,
                rankedStateEntries,
                countyFinderLinks
        );
        String lastReviewedAt = latestVerifiedAt(List.of(), contentPage.reviewedAt(), contentPage.updatedAt());

        model.addAttribute("page", seoService.contentPage(contentPage, CONTENT_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("contentQuickAnswer", seoService.contentQuickAnswer(contentPage));
        model.addAttribute("percPlanningRange", "perc-test-cost".equals(contentPage.slug())
                ? nationalPlanningRange("perc_test")
                : "");
        model.addAttribute("contentPage", contentPage);
        model.addAttribute("states", renderedStates);
        model.addAttribute("stateMoneyPageLinks", renderedStateMoneyPageLinks);
        model.addAttribute("featuredStateMoneyPageLinks", renderedStateMoneyPageLinks.stream().limit(8).toList());
        model.addAttribute("contentEvidenceLanes", contentEvidenceLanes);
        model.addAttribute("featuredContentEvidenceLanes", contentEvidenceLanes.stream().limit(3).toList());
        model.addAttribute("contentWorkflowCoverage", contentWorkflowCoverage);
        model.addAttribute("contentOfficialFilePathRows", contentOfficialFilePathRows);
        model.addAttribute("internalLinks", renderedInternalLinks);
        model.addAttribute("featuredInternalLinks", renderedInternalLinks.stream().limit(5).toList());
        model.addAttribute("secondaryInternalLinks", renderedInternalLinks.stream().skip(5).toList());
        model.addAttribute("permitLookupCountyLinks", permitLookupCountyLinks);
        model.addAttribute("featuredPermitLookupCountyLinks", permitLookupCountyLinks.stream().limit(6).toList());
        model.addAttribute("secondaryPermitLookupCountyLinks", permitLookupCountyLinks.stream().skip(6).toList());
        model.addAttribute("countyFinderLinks", countyFinderLinks);
        model.addAttribute("directOnlineCountyFinderLinks", directOnlineCountyFinderLinks);
        model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
        model.addAttribute("countyRouteClusters", countyRouteClusters);
        model.addAttribute("calculatorPath", primaryActionPathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("contentQuotePath", shouldLeadWithStateWorkflow(contentPage)
                ? null
                : contentQuotePathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("calculatorCtaHeading", contentActionHeading(contentPage));
        model.addAttribute("calculatorCtaLabel", contentActionLabel(contentPage));
        model.addAttribute("calculatorCtaNote", contentActionNote(contentPage));
        model.addAttribute("calculatorCtaTargetType", contentActionTargetType(contentPage));
        model.addAttribute("secondaryActionPath", secondaryActionPathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("secondaryActionLabel", secondaryActionLabel(contentPage));
        model.addAttribute("secondaryActionNote", secondaryActionNote(contentPage));
        model.addAttribute("secondaryActionTargetType", secondaryActionTargetType(contentPage));
        model.addAttribute("officialLookupPrimaryUrl", officialLookupPrimaryUrl(contentPage));
        model.addAttribute("officialLookupPrimaryLabel", officialLookupPrimaryLabel(contentPage));
        model.addAttribute("editorialPreparedBy", CONTENT_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", contentEvidenceLanes.isEmpty()
                ? "Cites the linked state pages, county workflow network, and source policy. The date below is the latest page or source review activity."
                : "Cites " + contentEvidenceLanes.size() + " source-backed state pages plus their county workflow network. The date below is the latest page or source review activity.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", "The " + contentPage.title()
                + " page is maintained as conservative homeowner guidance and changes when its evidence or workflow changes.");
        if (TDEC_RECORDS_SLUG.equals(contentPage.slug())) {
            model.addAttribute("tennesseeCountyRoutes", tennesseeCountyRoutes());
            return "pages/tdec-records-page";
        }
        if (NC_PERMIT_LOOKUP_SLUG.equals(contentPage.slug())) {
            model.addAttribute("northCarolinaCountyRoutes", researchDataService
                    .listPublicCountyRecordsPages("NC").stream()
                    .sorted(Comparator.comparing(CountyRecordsPage::countyName))
                    .toList());
            return "pages/north-carolina-records-page";
        }
        if (TX_OSSF_RECORDS_SLUG.equals(contentPage.slug())) {
            model.addAttribute("texasCountyRoutes", researchDataService
                    .listPublicCountyRecordsPages("TX").stream()
                    .sorted(Comparator.comparing(CountyRecordsPage::countyName))
                    .toList());
            return "pages/texas-ossf-records-page";
        }
        if (FL_OSTDS_LOOKUP_SLUG.equals(contentPage.slug())) {
            model.addAttribute("floridaCountyRoutes", floridaCountyRoutes());
            return "pages/florida-ostds-records-page";
        }
        if (DHEC_PERMIT_LOOKUP_SLUG.equals(contentPage.slug())) {
            model.addAttribute("southCarolinaCountyRoutes", southCarolinaCountyRoutes());
            return "pages/south-carolina-records-page";
        }
        if (Set.of(
                "how-to-find-septic-records-online",
                "septic-records-by-county",
                "septic-permit-search-by-address",
                "septic-permit-lookup",
                "septic-as-built-records",
                "septic-tank-location-records"
        ).contains(contentPage.slug())) {
            return "pages/national-records-page";
        }
        return "pages/content-page";
    }

    private List<TennesseeCountyRouteView> tennesseeCountyRoutes() {
        Map<String, CountyRecordsPage> detailedRoutes = researchDataService.listPublicCountyRecordsPages("TN").stream()
                .collect(Collectors.toMap(
                        page -> page.countyName().replaceFirst("(?i)\\s+County$", "").toLowerCase(Locale.US),
                        page -> page,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        return TENNESSEE_COUNTY_NAMES.stream()
                .map(countyName -> {
                    String countyKey = countyName.toLowerCase(Locale.US).replace(" ", "-");
                    CountyRecordsPage detailedRoute = detailedRoutes.get(countyName.toLowerCase(Locale.US));
                    return new TennesseeCountyRouteView(
                            countyName + " County",
                            countyKey,
                            TENNESSEE_CONTRACT_COUNTIES.contains(countyKey),
                            detailedRoute == null ? "" : detailedRoute.path("tennessee")
                    );
                })
                .toList();
    }

    private List<FloridaCountyRouteView> floridaCountyRoutes() {
        return FLORIDA_COUNTY_NAMES.stream()
                .map(countyName -> {
                    String countyKey = countyName.toLowerCase(Locale.US).replace(" ", "-").replace(".", "");
                    String authorityKey = countyName.toLowerCase(Locale.US).replace(".", "");
                    return new FloridaCountyRouteView(
                            countyName + " County",
                            countyKey,
                            FLORIDA_DEP_PERMIT_COUNTIES.contains(authorityKey),
                            floridaVerifiedRecordsUrl(countyKey),
                            floridaVerifiedRecordsLabel(countyKey),
                            floridaVerifiedRecordsInstructions(countyKey)
                    );
                })
                .toList();
    }

    private String floridaVerifiedRecordsUrl(String countyKey) {
        return switch (countyKey) {
            case "hillsborough" -> "https://s1.ebridge.com/ebridge/3.0/default.aspx";
            case "pasco" -> "https://s2.ebridge.com/ebridge/3.0/default.aspx";
            case "orange" -> "https://orange.floridahealth.gov/programs-and-services/environmental-public-health/onsite-sewage-disposal/";
            default -> "";
        };
    }

    private String floridaVerifiedRecordsLabel(String countyKey) {
        return switch (countyKey) {
            case "hillsborough", "pasco" -> "Open county eBridge records";
            case "orange" -> "Open Orange County OSTDS office";
            default -> "";
        };
    }

    private String floridaVerifiedRecordsInstructions(String countyKey) {
        return switch (countyKey) {
            case "hillsborough" -> "Public eBridge login: user public, password publicuser, cabinet HCHD. Search Program, Permit, street address, ZIP, date, or document type. The official guide says most files go back at least three years.";
            case "pasco" -> "Public eBridge login: user public, password public, cabinet PASCODOH. Choose OSTDS and the needed permit or inspection document type, then search by the available property clues.";
            case "orange" -> "Orange County DOH remains the permit and inspection contact. Its official OSTDS page provides the current phone, application email, and inspection path; it does not advertise a public property-record search.";
            default -> "";
        };
    }

    private List<SouthCarolinaCountyRouteView> southCarolinaCountyRoutes() {
        Map<String, CountyRecordsPage> detailedRoutes = researchDataService.listPublicCountyRecordsPages("SC").stream()
                .collect(Collectors.toMap(
                        page -> page.countyName().replaceFirst("(?i)\\s+County$", "").toLowerCase(Locale.US),
                        page -> page,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        return SOUTH_CAROLINA_COUNTY_NAMES.stream()
                .map(countyName -> {
                    String countyKey = countyName.toLowerCase(Locale.US).replace(" ", "-");
                    CountyRecordsPage detailedRoute = detailedRoutes.get(countyName.toLowerCase(Locale.US));
                    return new SouthCarolinaCountyRouteView(
                            countyName + " County",
                            countyKey,
                            detailedRoute == null ? "" : detailedRoute.path("south-carolina")
                    );
                })
                .toList();
    }

    @GetMapping({
            "/septic-records-checklist/{stateSlug}/{countySlug}", "/septic-records-checklist/{stateSlug}/{countySlug}/"
    })
    public String countyRecordsPage(@PathVariable String stateSlug, @PathVariable String countySlug, Model model) {
        CountyRecordsPage countyPage = researchDataService.findPublicCountyRecordsPage(stateSlug, countySlug)
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/" + stateSlug + "/" + countySlug));
        StateProfile state = researchDataService.findStateByCode(countyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(countyPage.officialSourceIds());
        List<PageLink> internalLinks = pageLinks(
                countyPage.internalLinkTargets(),
                "septic-records-checklist",
                state.stateCode()
        );
        String lastReviewedAt = latestVerifiedAt(
                sources,
                countyPage.reviewedAt(),
                countyPage.updatedAt(),
                state.lastVerifiedAt()
        );
        CountyLocalContentView countyLocalContent = countyContentQualityService.build(countyPage, sources);
        CountyWorkflowProfileView countyWorkflowProfile = CountyWorkflowRegistry.findOrBaseline(countyPage);
        CountyAccessProfileView countyAccessProfile = countyWorkflowProfile.access();
        CountyAcquisitionProfileView countyAcquisitionProfile = countyWorkflowProfile.acquisition();

        model.addAttribute("page", seoService.countyRecordsPage(countyPage, state, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("countyPage", countyPage);
        model.addAttribute("countyAccessProfile", countyAccessProfile);
        model.addAttribute("countyAcquisitionProfile", countyAcquisitionProfile);
        model.addAttribute("countyLocalContent", countyLocalContent);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("countySeoHeading", countySeoHeading(countyPage));
        model.addAttribute("countySeoIntro", countySeoIntro(countyPage, state, countyLocalContent));
        model.addAttribute("countyQuickAnswer", countyQuickAnswer(countyPage, countyLocalContent));
        model.addAttribute("countySearchQueries", countySearchQueries(countyPage, state));
        model.addAttribute("countySearchResponse", countySearchResponse(countyPage, state));
        model.addAttribute("countyLeadProjectLabel", projectTypeLabel(countyLeadProjectType(countyPage)));
        model.addAttribute("countyEstimatePath", countyEstimatePath(countyPage, state));
        model.addAttribute("countyQuotePath", countyQuotePath(countyPage, state));
        model.addAttribute("countyWorkflowStructure", countyWorkflowStructure(countyPage, state));
        model.addAttribute("countyIntentRoutes", countyIntentRoutes(countyPage, state));
        model.addAttribute("countyAvailabilitySummary", countyAvailabilitySummary(countyPage, state, sources, lastReviewedAt));
        model.addAttribute("countyAvailabilityRows", countyAvailabilityRows(countyPage, state));
        model.addAttribute("countyOfficialFilePathRows", countyOfficialFilePathRows(countyPage, state, sources));
        model.addAttribute("countyRequestOptions", countyRequestOptions(countyPage, state));
        model.addAttribute("internalLinks", internalLinks);
        model.addAttribute("featuredInternalLinks", internalLinks.stream().limit(4).toList());
        model.addAttribute("siblingCountyRoutes", siblingCountyRoutes(countyPage, state, 8));
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Cites " + sources.size()
                + " official county or state sources. The date below is the latest page or source review activity.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", "This " + countyPage.countyName() + ", " + state.stateName()
                + " route is maintained as conservative homeowner guidance and changes when its official file path changes.");
        return "pages/county-records-page";
    }

    private String countySeoHeading(CountyRecordsPage countyPage) {
        CountyAccessProfileView accessProfile = CountyAccessProfileCatalog.find(countyPage.key());
        if (accessProfile != null) {
            return accessProfile.heading();
        }
        if ("TN::hamilton-county".equals(countyPage.key())) {
            return "Hamilton County septic inspection records and permit lookup";
        }
        if ("TN::blount-county".equals(countyPage.key())) {
            return "Blount County TN septic records and SSDS request";
        }
        if ("NC::alamance-county".equals(countyPage.key())) {
            return "Alamance County NC septic permit lookup and records";
        }
        if ("MD::st-marys-county".equals(countyPage.key())) {
            return "St. Mary's County septic records and GIS lookup";
        }
        return countyPage.countyName() + " septic permit lookup and records request";
    }

    private String countyQuickAnswer(CountyRecordsPage countyPage, CountyLocalContentView localContent) {
        CountyAccessProfileView accessProfile = CountyAccessProfileCatalog.find(countyPage.key());
        if (accessProfile != null) {
            return accessProfile.summary();
        }
        return switch (countyPage.key()) {
            case "TN::blount-county" ->
                    "Use Blount County Environmental Health's SSDS request to pull the septic file. For a loan closing, use the separate inspection-letter request because the county says the SSDS form is not a closing letter.";
            case "NC::alamance-county" ->
                    "Start with Alamance County Environmental Health and pull the latest improvement permit or existing-system inspection. Then check for any malfunction investigation or repair permit tied to the parcel.";
            case "MD::st-marys-county" ->
                    "Search St. Mary's County environmental health records in the official GIS by address or Tax ID. If the file is thin or the system is failing, continue through the county repair-perc route.";
            default -> completeSentence(countyRecordAction(countyPage.recordsLabel()))
                    + " Search with the property address or parcel ID when available. Ask the office for: "
                    + completeSentence(countyFirstArtifact(countyPage))
                    + " The file owner is " + countyPage.officeLabel()
                    + "; contact it before treating an empty online search as proof that no record exists.";
        };
    }

    private String countyRecordAction(String label) {
        String trimmed = label == null ? "" : label.trim();
        if (trimmed.matches("(?i)^(open|search|request|use|start|submit|visit|download|complete)\\b.*")) {
            return trimmed;
        }
        return "Open " + trimmed;
    }

    private String completeSentence(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.matches(".*[.!?]$") ? trimmed : trimmed + ".";
    }

    private String countySeoIntro(
            CountyRecordsPage countyPage,
            StateProfile state,
            CountyLocalContentView localContent
    ) {
        CountyAccessProfileView accessProfile = CountyAccessProfileCatalog.find(countyPage.key());
        if (accessProfile != null) {
            return accessProfile.summary() + " Completion means " + accessProfile.completionLabel() + ".";
        }
        if ("TN::hamilton-county".equals(countyPage.key())) {
            return "Search Hamilton County, TN septic inspection records, permits, and completion certificates through the official county route. Use the address-search tips and Groundwater Department fallback before treating the file as missing.";
        }
        if (localContent.priorityPage()) {
            return localContent.introCopy();
        }
        return "Use this " + countyPage.countyName() + ", " + state.stateCode()
                + " route for septic permit lookup, records requests, address or parcel searches, as-built files, inspection letters, and county office routing before you trust a quote.";
    }

    private List<String> countySearchQueries(CountyRecordsPage countyPage, StateProfile state) {
        LinkedHashSet<String> queries = new LinkedHashSet<>(
                countySearchResponseQueries(countyPage)
        );
        queries.add(countyPage.countyName() + " " + state.stateCode() + " septic permit lookup");
        queries.add(countyPage.countyName() + " septic records request");
        queries.add(countyPage.countyName() + " septic permit search by address");
        queries.add(countyPage.countyName() + " septic as-built records");
        return queries.stream().limit(7).toList();
    }

    private CountySearchResponseView countySearchResponse(CountyRecordsPage countyPage, StateProfile state) {
        int boost = countySearchResponseBoost(countyPage);
        if (boost <= 0) {
            return null;
        }

        List<String> queryExamples = countySearchQueries(countyPage, state).stream()
                .limit(5)
                .toList();
        String firstQuery = queryExamples.stream().findFirst()
                .orElse(countyPage.countyName() + " septic records");
        String firstArtifact = countyFirstArtifact(countyPage);
        String combinedText = countyCombinedText(countyPage);
        String requestMethod = countyRequestMethodLabel(countyPage, combinedText);
        String responseTier = boost >= 116
                ? "Priority county route"
                : boost >= 100
                        ? "Established county route"
                        : "County route guide";
        String priorityLabel = "Official records route";
        String heading = countyPage.countyName() + " records and permit guide";
        String summary = "Use this county route to find the office, official lookup, first file, and fallback needed for septic records, permit questions, inspections, or a parcel-specific request.";
        List<CountyWorkflowFieldView> dossierRows = List.of(
                new CountyWorkflowFieldView(
                        "Start here",
                        "For " + firstQuery + ", start with the county office, official route, and first artifact before opening a broad state guide."
                ),
                new CountyWorkflowFieldView(
                        "First artifact",
                        firstArtifact
                ),
                new CountyWorkflowFieldView(
                        "Request method",
                        requestMethod + ". The page should make the official record path, request wording, and fallback office visible without another search."
                ),
                new CountyWorkflowFieldView(
                        "When the lookup stalls",
                        countyPage.hasParcelAnchor()
                                ? "Lead with the parcel or TMS anchor, then move into the septic file request so address-only searchers do not bounce."
                                : "Lead with the county record path and ask for the parcel identifier, permit copy, as-built, final approval, inspection letter, or written no-record response."
                )
        );
        List<PageLink> actionLinks = List.of(
                new PageLink(
                        "Search by address guide",
                        "/septic-permit-search-by-address/",
                        "Use when the visitor has an address, APN, TMS, owner name, or parcel clue but not the official septic file yet."
                ),
                new PageLink(
                        "Permit records request",
                        "/septic-permit-records-request/",
                        "Use when the county needs exact request language for permit copies, as-builts, inspection letters, or no-record responses."
                ),
                new PageLink(
                        "As-built records guide",
                        "/septic-as-built-records/",
                        "Use when the searcher needs tank, field, reserve-area, site sketch, or installed-layout proof."
                )
        );
        return new CountySearchResponseView(
                responseTier,
                priorityLabel,
                heading,
                summary,
                queryExamples,
                dossierRows,
                actionLinks
        );
    }

    private List<CountyWorkflowFieldView> countyOfficialFilePathRows(
            CountyRecordsPage countyPage,
            StateProfile state,
            List<SourceRecord> sources
    ) {
        String combinedText = countyCombinedText(countyPage);
        SourceRecord primarySource = sources == null ? null : sources.stream().findFirst().orElse(null);
        String sourceOwner = countyPage.officeLabel();
        String firstArtifact = countyFirstArtifact(countyPage);
        String requestMethod = countyRequestMethodLabel(countyPage, combinedText);
        String lookupClue = countyPage.hasParcelAnchor()
                ? "Start by capturing " + countyPage.parcelAnchorLabel()
                        + ", then carry that parcel, TMS, APN, owner, or address into the septic file request."
                : "For " + countyPage.countyName()
                        + ", carry the street address, parcel ID, owner name, legal description, subdivision, or prior permit clue into the county records route.";
        String ownerNote = sourceOwner + ". Verify whether this office owns the full septic file or only the first handoff before treating the result as complete.";
        String requestNote = requestMethod + ": " + countyRecordAction(countyPage.recordsLabel())
                + ", ask for " + firstArtifact
                + ", and keep the state route nearby if the county sends part of the file to a regional or delegated office.";
        String fallbackNote = "If the " + countyPage.countyName()
                + " search returns no match, ask " + countyPage.officeLabel()
                + " for a written no-record response and the next owning office before assuming the property has no septic history.";

        return List.of(
                new CountyWorkflowFieldView(
                        "File owner",
                        ownerNote
                ),
                new CountyWorkflowFieldView(
                        "Lookup clue",
                        lookupClue
                ),
                new CountyWorkflowFieldView(
                        "First artifact",
                        firstArtifact
                ),
                new CountyWorkflowFieldView(
                        "Request method",
                        requestNote
                ),
                new CountyWorkflowFieldView(
                        "No-record fallback",
                        fallbackNote
                ),
                new CountyWorkflowFieldView(
                        "State handoff",
                        "If the county route stalls, move back to the " + state.stateName()
                                + " records page with the same parcel clues instead of restarting with a broad web search."
                )
        );
    }

    private List<String> countySearchResponseQueries(CountyRecordsPage countyPage) {
        LinkedHashSet<String> queries = new LinkedHashSet<>();
        researchDataService.findSearchResponseTarget("county_records", countyPage.key())
                .map(SearchResponseTarget::queryList)
                .filter(items -> !items.isEmpty())
                .ifPresent(queries::addAll);
        queries.addAll(COUNTY_SEARCH_RESPONSE_QUERIES.getOrDefault(countyPage.key(), List.of()));
        return queries.stream().toList();
    }

    private int countySearchResponseBoost(CountyRecordsPage countyPage) {
        return researchDataService.findSearchResponseTarget("county_records", countyPage.key())
                .map(SearchResponseTarget::boostValue)
                .filter(boost -> boost > 0)
                .orElseGet(() -> COUNTY_SEARCH_RESPONSE_BOOSTS.getOrDefault(countyPage.key(), 0));
    }

    private String countyLeadProjectType(CountyRecordsPage countyPage) {
        String haystack = String.join(" ",
                nullToEmpty(countyPage.title()),
                nullToEmpty(countyPage.metaDescription()),
                nullToEmpty(countyPage.introCopy()),
                nullToEmpty(countyPage.uniqueAngle()),
                nullToEmpty(countyPage.targetReader())
        ).toLowerCase(Locale.US);
        if (haystack.contains("repair")
                || haystack.contains("replacement")
                || haystack.contains("modification")
                || haystack.contains("failure")
                || haystack.contains("failed")) {
            return ProjectType.REPLACEMENT.value();
        }
        if (haystack.contains("buyer")
                || haystack.contains("seller")
                || haystack.contains("agent")
                || haystack.contains("transfer")
                || haystack.contains("closing")) {
            return ProjectType.BUYING_HOME.value();
        }
        if (haystack.contains("inspection") || haystack.contains("letter")) {
            return ProjectType.INSPECTION.value();
        }
        return ProjectType.INSPECTION.value();
    }

    private String countyEstimatePath(CountyRecordsPage countyPage, StateProfile state) {
        return appendSourcePageHint(
                "/septic-system-cost-calculator/?state=" + state.stateCode() + "&projectType=" + countyLeadProjectType(countyPage),
                countyPage.path(state.slug())
        );
    }

    private String countyQuotePath(CountyRecordsPage countyPage, StateProfile state) {
        String estimatePath = countyEstimatePath(countyPage, state);
        return estimatePath + (estimatePath.contains("?") ? "&" : "?") + "quoteMode=true#quote-request";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @GetMapping({"/perc-test-cost/alabama", "/perc-test-cost/alabama/"})
    public ResponseEntity<Void> alabamaPercTestCanonicalRedirect() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create("/septic-system-cost-calculator/alabama/"))
                .build();
    }

    @GetMapping({
            "/septic-replacement-cost/{stateSlug}", "/septic-replacement-cost/{stateSlug}/",
            "/perc-test-cost/{stateSlug}", "/perc-test-cost/{stateSlug}/",
            "/failed-perc-test-septic/{stateSlug}", "/failed-perc-test-septic/{stateSlug}/",
            "/septic-replacement-area/{stateSlug}", "/septic-replacement-area/{stateSlug}/",
            "/wet-yard-over-septic-drain-field/{stateSlug}", "/wet-yard-over-septic-drain-field/{stateSlug}/",
            "/buying-a-house-with-a-septic-system/{stateSlug}", "/buying-a-house-with-a-septic-system/{stateSlug}/",
            "/drain-field-replacement-cost/{stateSlug}", "/drain-field-replacement-cost/{stateSlug}/",
            "/septic-pumping-cost/{stateSlug}", "/septic-pumping-cost/{stateSlug}/",
            "/septic-inspection-cost/{stateSlug}", "/septic-inspection-cost/{stateSlug}/",
            "/septic-permit-process/{stateSlug}", "/septic-permit-process/{stateSlug}/",
            "/septic-records-checklist/{stateSlug}", "/septic-records-checklist/{stateSlug}/"
    })
    public String stateMoneyPage(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        String path = request.getRequestURI().replaceFirst("^/", "").replaceFirst("/$", "");
        String contentSlug = path.substring(0, path.lastIndexOf('/'));

        StateMoneyPage stateMoneyPage = researchDataService.findPublicStateMoneyPage(contentSlug, stateSlug)
                .orElseThrow(() -> new StateNotFoundException(path));
        StateProfile state = researchDataService.findStateByCode(stateMoneyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(stateMoneyPage.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        SourceRecord primaryLocalAuthoritySource = localAuthoritySources.stream().findFirst().orElse(null);
        SourceRecord primaryRecordsLookupSource = recordsLookupSources.stream().findFirst().orElse(null);
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<PageLink> internalLinks = pageLinks(
                mergedPaths(stateMoneyPage.internalLinkTargets(), defaultStateCrossLinks(stateMoneyPage, state)),
                stateMoneyPage.contentSlug(),
                state.stateCode()
        );
        List<PageLink> countyRecordLinks = supportsCountyWorkflowSummary(stateMoneyPage.contentSlug())
                ? countyRecordPageLinks(state.stateCode())
                : List.of();
        StateCountyWorkflowSynthesisView countyWorkflowSynthesis = stateCountyWorkflowSynthesis(stateMoneyPage, state);
        StateWorkflowDecisionView workflowDecision = stateWorkflowDecisionView(
                stateMoneyPage,
                state,
                countyWorkflowSynthesis,
                primaryLocalAuthoritySource,
                primaryRecordsLookupSource
        );
        StateCostScopeView costScopeView = stateCostScopeView(
                stateMoneyPage,
                state,
                countyWorkflowSynthesis
        );
        StateRecordsSearchResponseView stateRecordsSearchResponse = stateRecordsSearchResponse(
                stateMoneyPage,
                state,
                primaryRecordsLookupSource,
                primaryLocalAuthoritySource,
                countyRecordLinks,
                countyWorkflowSynthesis
        );
        String calculatorPath = stateMoneyCalculatorPath(stateMoneyPage, state);
        StateMoneyPrimaryAction primaryAction = stateMoneyPrimaryAction(
                stateMoneyPage,
                state,
                stateActionCopy,
                countyRecordLinks,
                primaryLocalAuthoritySource,
                primaryRecordsLookupSource,
                countyWorkflowSynthesis
        );
        String lastReviewedAt = latestVerifiedAt(
                sources,
                stateMoneyPage.reviewedAt(),
                stateMoneyPage.updatedAt(),
                state.lastVerifiedAt()
        );
        boolean showQuoteCta = publishingPolicyService.allowDirectQuote(stateMoneyPage, state);

        model.addAttribute("page", seoService.stateMoneyPage(stateMoneyPage, state, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("stateMoneyPage", stateMoneyPage);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", primaryLocalAuthoritySource);
        model.addAttribute("primaryRecordsLookupSource", primaryRecordsLookupSource);
        model.addAttribute("primaryAction", primaryAction);
        model.addAttribute("calculatorPath", calculatorPath);
        model.addAttribute("quotePath", stateMoneyQuotePath(calculatorPath));
        model.addAttribute("showQuoteCta", showQuoteCta);
        model.addAttribute("guidePath", "/septic-system-cost-calculator/" + state.slug() + "/");
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("internalLinks", internalLinks);
        model.addAttribute("featuredInternalLinks", internalLinks.stream().limit(5).toList());
        model.addAttribute("secondaryInternalLinks", internalLinks.stream().skip(5).toList());
        model.addAttribute("countyRecordLinks", countyRecordLinks);
        model.addAttribute("featuredCountyRecordLinks", featuredStateMoneyCountyLinks(
                stateMoneyPage,
                state,
                countyRecordLinks
        ));
        model.addAttribute("countyWorkflowSynthesis", countyWorkflowSynthesis);
        model.addAttribute("stateRecordsSearchResponse", stateRecordsSearchResponse);
        model.addAttribute("stateOfficialFilePathRows", stateOfficialFilePathRows(
                stateMoneyPage,
                state,
                primaryRecordsLookupSource,
                primaryLocalAuthoritySource,
                countyWorkflowSynthesis
        ));
        model.addAttribute("searchIntentOpportunities", searchIntentOpportunities(
                stateMoneyPage,
                state,
                primaryRecordsLookupSource,
                primaryLocalAuthoritySource
        ).stream()
                .filter(opportunity -> !"state_money_page".equals(opportunity.targetType())
                        || isIndexableEditorialPath(opportunity.actionPath()))
                .toList());
        model.addAttribute("workflowDecision", workflowDecision);
        model.addAttribute("costScopeView", costScopeView);
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Cites " + sources.size()
                + " official sources tied to this state workflow. The date below is the latest page or source review activity.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", "This " + stateMoneyPage.title()
                + " page is maintained as conservative homeowner guidance and changes when its state evidence or workflow changes.");
        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug())
                && Set.of("AL", "IN", "NC", "SC", "TN", "TX").contains(state.stateCode())) {
            model.addAttribute("totalCountyRouteCount", totalCountyRouteCount());
            return "pages/state-records-page";
        }
        return "pages/state-money-page";
    }

    @ModelAttribute("projectTypes")
    public ProjectType[] projectTypes() {
        return ProjectType.values();
    }

    @ModelAttribute("soilStatuses")
    public SoilPercStatus[] soilStatuses() {
        return SoilPercStatus.values();
    }

    @ModelAttribute("accessLevels")
    public AccessDifficulty[] accessLevels() {
        return AccessDifficulty.values();
    }

    @ModelAttribute("timelines")
    public TimelinePreference[] timelines() {
        return TimelinePreference.values();
    }

    @ModelAttribute("occupancyProfiles")
    public OccupancyProfile[] occupancyProfiles() {
        return OccupancyProfile.values();
    }

    @ModelAttribute("usageProfiles")
    public UsageProfile[] usageProfiles() {
        return UsageProfile.values();
    }

    private String renderCalculator(
            Model model,
            EstimateForm estimateForm,
            EstimatorResult result,
            QuoteLeadForm quoteLeadForm,
            String leadId,
            boolean quoteHasErrors,
            boolean showQuotePanel
    ) {
        ContentPage calculatorLanding = researchDataService.findPublicContentPage("septic-system-cost-calculator")
                .orElse(null);
        model.addAttribute("page", seoService.calculatorPage());
        model.addAttribute("states", calculatorStateOptions());
        model.addAttribute("estimateForm", estimateForm);
        model.addAttribute("result", result);
        model.addAttribute("quoteLeadForm", quoteLeadForm);
        model.addAttribute("leadId", leadId);
        model.addAttribute("quoteHasErrors", quoteHasErrors);
        model.addAttribute("showQuotePanel", showQuotePanel || result != null || leadId != null || quoteHasErrors);
        model.addAttribute("calculatorLanding", calculatorLanding);
        model.addAttribute("calculatorLandingLinks", calculatorLanding == null
                ? List.of()
                : pageLinks(calculatorLanding.internalLinkTargets(), calculatorLanding.slug(), null));
        model.addAttribute("costEvidence", result == null
                ? List.of()
                : costEvidenceViews(result.stateCode(), estimateForm.getProjectType()));
        return "pages/calculator";
    }

    private List<StateOptionView> calculatorStateOptions() {
        return usStateDirectoryService.allStates().stream()
                .map(state -> new StateOptionView(state.stateCode(), state.stateName()))
                .toList();
    }

    private boolean isTrackableInternalPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        return path.startsWith("/") && !path.startsWith("//") && !path.startsWith("/events/");
    }

    private boolean isTrackableNavigationTarget(String target) {
        if (isTrackableInternalPath(target)) {
            return true;
        }
        if (target == null || target.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(target);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && uri.getUserInfo() == null
                    && uri.getPort() == -1
                    && uri.getRawQuery() == null
                    && uri.getRawFragment() == null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isTrackableArtifactAction(String value) {
        return value != null && value.matches("[a-z][a-z0-9_]{1,63}");
    }

    private boolean isTrackableWorkflowRunId(String value) {
        return value != null && value.matches("[A-Za-z0-9-]{8,64}");
    }

    private boolean isTrackableCountyKey(String value) {
        return value != null && (value.isBlank() || value.matches("[A-Z]{2}::[a-z0-9-]{2,72}"));
    }

    private boolean isTrackableWorkflowStage(String value) {
        if (value == null) {
            return false;
        }
        return switch (value) {
            case "workflow_viewed", "preparation_started", "preparation_ready",
                    "official_route_opened", "official_returned", "outcome_recorded",
                    "request_submitted", "record_reported", "document_reviewed",
                    "document_handoff", "property_file_ready", "task_finished" -> true;
            default -> false;
        };
    }

    private boolean isTrackableWorkflowOutcome(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return switch (value) {
            case "artifact", "partial", "not_found_online", "blocked", "request_submitted",
                    "followup_due", "no_record_response", "wrong_agency", "repair_issue",
                    "professional_help", "found", "missing", "pending" -> true;
            default -> false;
        };
    }

    private boolean isTrackableWebVital(String metricName, Double value) {
        if (metricName == null || value == null || !Double.isFinite(value) || value < 0) {
            return false;
        }
        return switch (metricName) {
            case "LCP", "CLS", "INP", "FCP", "TTFB" -> value <= 600_000;
            default -> false;
        };
    }

    private String renderTankSizeEstimator(Model model, TankSizeForm tankSizeForm, TankSizeEstimatorResult result) {
        model.addAttribute("page", seoService.tankSizeEstimatorPage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        StateProfile selectedState = researchDataService.findStateByCode(tankSizeForm.getStateCode())
                .filter(StateProfile::isPublished)
                .orElseGet(() -> preferredTankSizeState(publicStates));
        model.addAttribute("states", publicStates);
        model.addAttribute("tankSizeForm", tankSizeForm);
        model.addAttribute("result", result);
        model.addAttribute("selectedState", selectedState);
        model.addAttribute("tankSizeFaqs", seoService.tankSizeEstimatorFaqs());
        model.addAttribute("stateRuleFacts", selectedState == null ? List.of() : stateRuleFactViews(selectedState.stateCode()));
        return "pages/tank-size-estimator";
    }

    private String renderDrainfieldEstimator(
            Model model,
            DrainfieldEstimatorForm drainfieldEstimatorForm,
            DrainfieldEstimatorResult result
    ) {
        model.addAttribute("page", seoService.drainfieldEstimatorPage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        StateProfile selectedState = researchDataService.findStateByCode(drainfieldEstimatorForm.getStateCode())
                .filter(StateProfile::isPublished)
                .orElseGet(() -> preferredTankSizeState(publicStates));
        String selectedDrainfieldPagePath = "";
        String selectedDrainfieldPageTitle = "";
        if (selectedState != null) {
            Optional<StateMoneyPage> drainfieldPage = researchDataService.findPublicStateMoneyPage("drain-field-replacement-cost", selectedState.slug());
            if (drainfieldPage.isPresent()) {
                selectedDrainfieldPagePath = drainfieldPage.get().path(selectedState.slug());
                selectedDrainfieldPageTitle = drainfieldPage.get().title();
            }
        }
        List<StateMoneyPageLink> drainfieldStateLinks = researchDataService.listPublicStateMoneyPagesForContent("drain-field-replacement-cost").stream()
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .filter(StateProfile::isPublished)
                        .map(state -> new StateMoneyPageLink(page.title(), state.stateName(), state.stateCode(), page.path(state.slug())))
                        .stream())
                .limit(8)
                .toList();
        model.addAttribute("states", publicStates);
        model.addAttribute("drainfieldEstimatorForm", drainfieldEstimatorForm);
        model.addAttribute("result", result);
        model.addAttribute("selectedState", selectedState);
        model.addAttribute("drainfieldFaqs", seoService.drainfieldEstimatorFaqs());
        model.addAttribute("drainfieldStateLinks", drainfieldStateLinks);
        model.addAttribute("selectedDrainfieldPagePath", selectedDrainfieldPagePath);
        model.addAttribute("selectedDrainfieldPageTitle", selectedDrainfieldPageTitle);
        return "pages/drainfield-estimator";
    }

    private StateProfile preferredTankSizeState(List<StateProfile> publicStates) {
        if (publicStates.isEmpty()) {
            return null;
        }
        return researchDataService.findStateByCode("GA")
                .filter(StateProfile::isPublished)
                .orElse(publicStates.get(0));
    }

    private List<StateCoverageCardView> buildStateCoverageCards() {
        return usStateDirectoryService.allStates().stream()
                .map(stateReference -> researchDataService.findStateByCode(stateReference.stateCode())
                        .filter(StateProfile::isPublished)
                        .map(this::publishedCoverageCard)
                        .orElseGet(() -> queuedCoverageCard(stateReference)))
                .sorted(Comparator
                        .comparing((StateCoverageCardView card) -> !card.published())
                        .thenComparingInt(card -> card.published()
                                ? 0
                                : stateQueuePlanService.findByStateCode(card.stateCode())
                                        .map(StateQueuePlan::priorityRank)
                                        .orElse(Integer.MAX_VALUE))
                        .thenComparing(StateCoverageCardView::stateName))
                .toList();
    }

    private TrustOperationsPageView methodologyOperations() {
        WorkflowNetworkSnapshotView snapshot = workflowNetworkSnapshot();
        int stateGuideCount = researchDataService.getPublicStateProfiles().size();
        int stateWorkflowCount = researchDataService.getPublicStateMoneyPages().size();
        int countyPageCount = researchDataService.getPublicCountyRecordsPages().size();
        int sourceBackedPageCount = sourceBackedPageCount();
        int sourceCount = publishedSourceRecords().size();

        return new TrustOperationsPageView(
                "Methodology",
                "How pages earn the right to be public.",
                "SepticPath is built around records, permit paths, buyer diligence, and conservative planning estimates. A page should change the next action, expose the file path, or narrow uncertainty with a visible source trail.",
                "Public quality gate",
                String.valueOf(sourceBackedPageCount),
                "Live source-backed state, workflow, and county pages. Thin state-name swaps are kept out of the index path.",
                List.of(
                        new TrustMetricView("State guides", String.valueOf(stateGuideCount), "Published only after a state source set, local override note, and homeowner action path exist."),
                        new TrustMetricView("State workflow pages", String.valueOf(stateWorkflowCount), "Records, permit, buyer, inspection, replacement, and cost pages tied back to state context."),
                        new TrustMetricView("County records pages", String.valueOf(countyPageCount), "Local file paths for county-level records, request methods, and quote gates."),
                        new TrustMetricView("Official sources", String.valueOf(sourceCount), "Distinct source records currently backing the public research layer.")
                ),
                List.of(
                        new TrustLaneView(
                                "Gate 1",
                                "A page needs a job beyond ranking.",
                                "The target reader must be obvious: buyer, seller, owner, agent, or contractor. If the page cannot tell that user what to pull, ask, or verify next, it should not be a money page.",
                                "Browse coverage",
                                "/coverage/"
                        ),
                        new TrustLaneView(
                                "Gate 2",
                                "Official-source context comes before estimate confidence.",
                                "State rules, county records offices, delegated authority pages, forms, and file request paths are preferred over generic cost claims. Weak source coverage widens the answer instead of pretending certainty.",
                                "Read source policy",
                                "/source-policy/"
                        ),
                        new TrustLaneView(
                                "Gate 3",
                                "County pages must add local workflow detail.",
                                "A county page needs an office path, records path, request method, low-end breaker, and a next action that a real user can take from the page.",
                                "Open county routes",
                                "/septic-records-by-county/"
                        ),
                        new TrustLaneView(
                                "Gate 4",
                                "The estimate is downstream of the file.",
                                "The cost calculator and state guides are used after the record, permit, buyer, or site-risk question is clearer. This keeps the site from over-selling fake precision.",
                                "Open estimator",
                                "/septic-system-cost-calculator/"
                        )
                ),
                "Strongest live workflow backbones",
                "These states currently have the deepest blend of state workflow pages, county file paths, source count, confidence, and verification date.",
                coverageRows(8),
                List.of(),
                "Methodology only matters if it changes publishing behavior.",
                "Use these standards to decide what gets built next: add pages only when they create a better file path, stronger local routing, or a more honest planning range.",
                List.of(
                        new PageLink("Open source policy", "/source-policy/", "See how evidence is prioritized."),
                        new PageLink("Open coverage", "/coverage/", "Inspect live state, workflow, and county depth."),
                        new PageLink("Open editorial standards", "/editorial-standards/", "Read the broader review boundaries.")
                )
        );
    }

    private TrustOperationsPageView sourcePolicyOperations() {
        List<SourceRecord> sourceRecords = publishedSourceRecords();
        long verifiedSources = sourceRecords.stream()
                .filter(source -> hasText(source.contentVerifiedAt()))
                .count();
        long localSources = sourceRecords.stream()
                .filter(source -> hasText(source.countyOrLocal()))
                .filter(source -> !"no".equalsIgnoreCase(source.countyOrLocal()))
                .count();
        long finalOrOfficialSources = sourceRecords.stream()
                .filter(source -> hasText(source.draftOrFinalStatus()))
                .filter(source -> !"draft".equalsIgnoreCase(source.draftOrFinalStatus()))
                .count();

        return new TrustOperationsPageView(
                "Source policy",
                "The source trail is part of the product.",
                "Septic pages can look correct while sending users to the wrong office, stale form, or overconfident cost number. This policy defines which sources get used, when uncertainty is preserved, and where corrections go.",
                "Source registry",
                String.valueOf(sourceRecords.size()),
                "Distinct source records are currently attached to public state, workflow, and county pages.",
                List.of(
                        new TrustMetricView("Verified sources", String.valueOf(verifiedSources), "Source records with a stored last-verified date."),
                        new TrustMetricView("County or local sources", String.valueOf(localSources), "Local offices, county record paths, or delegated authority sources."),
                        new TrustMetricView("Final/official status", String.valueOf(finalOrOfficialSources), "Sources marked as final, official, or otherwise not draft-only in the registry."),
                        new TrustMetricView("Correction route", "Public", "Source corrections are routed through the public contact form.")
                ),
                List.of(
                        new TrustLaneView(
                                "Priority 1",
                                "Official public sources beat paraphrase.",
                                "State agencies, county health departments, delegated local authority pages, permit forms, public record request pages, and official manuals are preferred for rules and workflow claims.",
                                "Contact corrections",
                                "/contact/"
                        ),
                        new TrustLaneView(
                                "Priority 2",
                                "Local workflow can override state-level comfort.",
                                "If a county office, parcel system, or delegated authority changes the user's next step, the local path must be made visible before a broad statewide summary.",
                                "Open county routes",
                                "/septic-records-by-county/"
                        ),
                        new TrustLaneView(
                                "Priority 3",
                                "Cost evidence stays conservative.",
                                "Public cost anchors are planning context. They do not outrank permit files, site evaluation, replacement-area reality, or local approval sequence.",
                                "Open cost estimator",
                                "/septic-system-cost-calculator/"
                        ),
                        new TrustLaneView(
                                "Priority 4",
                                "Conflicts create wider guidance, not invented certainty.",
                                "When sources conflict, stay vague, or delegate decisions locally, the page should flag the uncertainty and route users to the office or file that can resolve it.",
                                "Read methodology",
                                "/methodology/"
                        )
                ),
                "Source-backed states to inspect first",
                "These rows show where the public network has the most source depth and local workflow surface today.",
                coverageRows(8),
                List.of(),
                "The correction loop is deliberately public.",
                "If a source changes, a county link moves, or a page overstates certainty, the right next action is a source correction request, then a page update tied to the affected workflow.",
                List.of(
                        new PageLink("Submit source correction", "/contact/", "Route a broken source, stale form, or correction note."),
                        new PageLink("Read methodology", "/methodology/", "See the page-quality gate."),
                        new PageLink("Open coverage", "/coverage/", "Inspect where the network is deepest.")
                )
        );
    }

    private TrustOperationsPageView coverageOperations() {
        WorkflowNetworkSnapshotView snapshot = workflowNetworkSnapshot();
        int stateGuideCount = researchDataService.getPublicStateProfiles().size();
        int stateWorkflowCount = researchDataService.getPublicStateMoneyPages().size();
        int countyPageCount = researchDataService.getPublicCountyRecordsPages().size();
        int sourceCount = publishedSourceRecords().size();
        int countySpecificRouteCount = CountyAccessProfileCatalog.countySpecificProfileCount();
        int preparedFieldPackCount = CountyAcquisitionProfileCatalog.preparedFieldPackCount();
        int startingPointOnlyCount = Math.max(0, countyPageCount - countySpecificRouteCount);

        return new TrustOperationsPageView(
                "Coverage",
                "Live coverage, source depth, and county workflow density.",
                "This is the operational map behind the public site. It separates broad coverage from pages that are actually strong enough to route users into records, permits, buyer diligence, and local files.",
                "County workflow routes",
                String.valueOf(countyPageCount),
                snapshot.summary(),
                List.of(
                        new TrustMetricView("Published state guides", String.valueOf(stateGuideCount), "Public state guide pages with official-source context."),
                        new TrustMetricView("Published workflow pages", String.valueOf(stateWorkflowCount), "State-specific records, permit, buyer, inspection, replacement, and related pages."),
                        new TrustMetricView("County-backed states", String.valueOf(snapshot.countyBackedStateCount()), "States with at least one live county records workflow route."),
                        new TrustMetricView("Official sources", String.valueOf(sourceCount), "Distinct source records attached to public state, workflow, and county pages."),
                        new TrustMetricView("County-specific handoffs", String.valueOf(countySpecificRouteCount), "Routes with a reviewed county-specific search, request, phone, or office handoff."),
                        new TrustMetricView("Prepared official field packs", String.valueOf(preparedFieldPackCount), "Routes whose preparation fields were checked against a current or archived county-authored form, portal, search, or phone instruction."),
                        new TrustMetricView("Official starting points only", String.valueOf(startingPointOnlyCount), "Published county pages that confirm an official source but do not claim a verified county-specific intake.")
                ),
                List.of(
                        new TrustLaneView(
                                "Index queue",
                                "Manual indexing should start with dense workflow states.",
                                "Prioritize states where county records pages and state workflow pages reinforce each other. Those pages have the best chance to satisfy records and permit intent without feeling thin.",
                                "Open records hub",
                                "/septic-records-checklist/"
                        ),
                        new TrustLaneView(
                                "Expansion rule",
                                "More URLs are useful only after the workflow is real.",
                                "A new page should add source depth, a county route, a request script, a file artifact, or a quote gate. Otherwise it dilutes the network.",
                                "Read methodology",
                                "/methodology/"
                        ),
                        new TrustLaneView(
                                "Refresh rule",
                                "Verification dates are part of the quality surface.",
                                "Rows with older state verification dates or thin county depth should be reviewed before they become the next manual indexing targets.",
                                "Read source policy",
                                "/source-policy/"
                        ),
                        new TrustLaneView(
                                "User route",
                                "Coverage has to shorten the next click.",
                                "The strongest pages push users from broad national pages into exact state/county paths, not into more explanatory copy.",
                                "Open permit lookup",
                                "/septic-permit-lookup/"
                        )
                ),
                "Coverage rows to use for prioritization",
                "Sorted by county records depth, then state workflow depth. Use this table for manual indexing and next-page selection.",
                coverageRows(18),
                countyFinderLinks(50),
                "The next expansion should be selective, not massive.",
                "The network gets stronger when each new page increases source density, county specificity, or task completion. That is the pSEO standard to hold.",
                List.of(
                        new PageLink("Open methodology", "/methodology/", "Use the publishing quality gate."),
                        new PageLink("Open source policy", "/source-policy/", "Check evidence and correction rules."),
                        new PageLink("Open state guides", "/states/", "Browse public state coverage.")
                )
        );
    }

    private int sourceBackedPageCount() {
        return researchDataService.getPublicStateProfiles().size()
                + researchDataService.getPublicStateMoneyPages().size()
                + researchDataService.getPublicCountyRecordsPages().size();
    }

    private List<CoverageStateRowView> coverageRows(int limit) {
        return researchDataService.getPublicStateProfiles().stream()
                .map(state -> {
                    int workflowPageCount = researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
                    int countyPageCount = researchDataService.listPublicCountyRecordsPages(state.stateCode()).size();
                    int sourceCount = sourceCountForState(state);
                    String path = researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                            .map(page -> page.path(state.slug()))
                            .orElse("/septic-system-cost-calculator/" + state.slug() + "/");
                    return new CoverageStateRowView(
                            state.stateName(),
                            state.stateCode(),
                            path,
                            workflowPageCount,
                            countyPageCount,
                            sourceCount + " sources",
                            confidenceLabel(state.confidenceScore()),
                            firstNonBlank(state.lastVerifiedAt(), "Verification date queued")
                    );
                })
                .sorted(Comparator
                        .comparingInt(CoverageStateRowView::countyPageCount)
                        .reversed()
                        .thenComparingInt(CoverageStateRowView::workflowPageCount)
                        .reversed()
                        .thenComparing(CoverageStateRowView::stateName))
                .limit(limit)
                .toList();
    }

    private int sourceCountForState(StateProfile state) {
        Set<String> sourceIds = new LinkedHashSet<>();
        addSourceIds(sourceIds, state.officialSourceIds());
        addSourceIds(sourceIds, state.localAuthoritySourceIds());
        addSourceIds(sourceIds, state.recordsLookupSourceIds());
        researchDataService.listPublicStateMoneyPages(state.stateCode())
                .forEach(page -> addSourceIds(sourceIds, page.officialSourceIds()));
        researchDataService.listPublicCountyRecordsPages(state.stateCode())
                .forEach(page -> addSourceIds(sourceIds, page.officialSourceIds()));
        return sourceIds.size();
    }

    private List<SourceRecord> publishedSourceRecords() {
        return researchDataService.getSources(new ArrayList<>(publishedSourceIds()));
    }

    private Set<String> publishedSourceIds() {
        Set<String> sourceIds = new LinkedHashSet<>();
        for (StateProfile state : researchDataService.getPublicStateProfiles()) {
            addSourceIds(sourceIds, state.officialSourceIds());
            addSourceIds(sourceIds, state.localAuthoritySourceIds());
            addSourceIds(sourceIds, state.recordsLookupSourceIds());
        }
        researchDataService.getPublicStateMoneyPages()
                .forEach(page -> addSourceIds(sourceIds, page.officialSourceIds()));
        researchDataService.getPublicCountyRecordsPages()
                .forEach(page -> addSourceIds(sourceIds, page.officialSourceIds()));
        return sourceIds;
    }

    private void addSourceIds(Set<String> sourceIds, List<String> values) {
        if (values == null) {
            return;
        }
        values.stream()
                .filter(this::hasText)
                .forEach(sourceIds::add);
    }

    private WorkflowNetworkSnapshotView workflowNetworkSnapshot() {
        List<StateCountyBackbone> backbones = researchDataService.getPublicStateProfiles().stream()
                .map(state -> {
                    int countyCount = researchDataService.listPublicCountyRecordsPages(state.stateCode()).size();
                    int workflowPageCount = researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
                    Optional<StateMoneyPage> recordsPage = researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                            .filter(page -> "septic-records-checklist".equals(page.contentSlug()))
                            .findFirst();
                    String path = recordsPage
                            .map(page -> page.path(state.slug()))
                            .orElse("/septic-system-cost-calculator/" + state.slug() + "/");
                    String title = recordsPage
                            .map(page -> state.stateName() + " county records workflow")
                            .orElse(state.stateName() + " septic guide");
                    String note = countyCount + " live county workflow pages | "
                            + workflowPageCount + " live state workflow pages";
                    return new StateCountyBackbone(state, countyCount, workflowPageCount, new PageLink(title, path, note));
                })
                .filter(backbone -> backbone.countyCount() > 0 || backbone.workflowPageCount() > 0)
                .sorted(Comparator
                        .comparingInt(StateCountyBackbone::countyCount)
                        .reversed()
                        .thenComparingInt(StateCountyBackbone::workflowPageCount)
                        .reversed()
                        .thenComparing(backbone -> backbone.state().stateName()))
                .toList();

        int liveCountyCount = backbones.stream()
                .mapToInt(StateCountyBackbone::countyCount)
                .sum();
        int countyBackedStateCount = (int) backbones.stream()
                .filter(backbone -> backbone.countyCount() > 0)
                .count();
        int countyFirstStateCount = (int) backbones.stream()
                .filter(backbone -> backbone.countyCount() >= 2)
                .count();
        int liveWorkflowPageCount = backbones.stream()
                .mapToInt(StateCountyBackbone::workflowPageCount)
                .sum();
        String topStates = backbones.stream()
                .filter(backbone -> backbone.countyCount() > 0)
                .limit(5)
                .map(backbone -> backbone.state().stateName())
                .collect(Collectors.joining(", "));

        return new WorkflowNetworkSnapshotView(
                "Live county workflow backbone",
                "The public network now carries " + liveWorkflowPageCount
                        + " source-backed state workflow pages and " + liveCountyCount
                        + " live county workflow pages across " + countyBackedStateCount
                        + " states.",
                List.of(
                        countyFirstStateCount + " states already have enough county depth to route users into county-first follow-up instead of a generic state-only answer.",
                        topStates.isBlank()
                                ? "The strongest live states already connect records, permit, buyer, and transfer questions back to actual local files."
                                : "The thickest live county workflow backbones today are " + topStates + ".",
                        "Use those county-backed state pages when file owner, permit closeout, transfer artifact, or quote-gate differences matter more than a statewide average."
                ),
                backbones.stream()
                        .filter(backbone -> backbone.countyCount() > 0)
                        .limit(5)
                        .map(StateCountyBackbone::link)
                        .toList(),
                liveCountyCount,
                countyBackedStateCount,
                countyFirstStateCount
        );
    }

    private List<PageLink> homeGrowthSpotlights() {
        return ORGANIC_SPRINT_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .filter(StateProfile::isPublished)
                .map(state -> researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                        .sorted(Comparator
                                .comparingInt((StateMoneyPage page) -> growthPageScore(page, state))
                                .reversed()
                                .thenComparing(StateMoneyPage::title))
                        .findFirst()
                        .map(page -> growthSpotlightLink(page, state))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<PageLink> coverageGrowthSpotlights() {
        return researchDataService.getPublicStateProfiles().stream()
                .flatMap(state -> researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                        .sorted(Comparator
                                .comparingInt((StateMoneyPage page) -> growthPageScore(page, state))
                                .reversed()
                                .thenComparing(StateMoneyPage::title))
                        .limit(2)
                        .map(page -> Map.entry(page, state)))
                .sorted(Comparator
                        .comparingInt((Map.Entry<StateMoneyPage, StateProfile> entry) -> growthPageScore(entry.getKey(), entry.getValue()))
                        .reversed()
                        .thenComparing(entry -> entry.getValue().stateName())
                        .thenComparing(entry -> entry.getKey().title()))
                .limit(12)
                .map(entry -> growthSpotlightLink(entry.getKey(), entry.getValue()))
                .toList();
    }

    private PageLink growthSpotlightLink(StateMoneyPage page, StateProfile state) {
        return new PageLink(
                page.title(),
                page.path(state.slug()),
                state.stateName() + " | " + firstNonBlank(page.uniqueAngle(), page.metaDescription())
        );
    }

    private PageLink stateGuideHeroWorkflowLink(StateMoneyPage page, StateProfile state) {
        return new PageLink(
                stateGuideHeroWorkflowLabel(page),
                page.path(state.slug()),
                page.title()
        );
    }

    private String stateSurfaceRouteTitle(StateMoneyPage page, StateProfile state) {
        return switch (page.contentSlug()) {
            case "septic-records-checklist" -> state.stateName() + " records workflow";
            case "septic-permit-process" -> state.stateName() + " permit workflow";
            case "buying-a-house-with-a-septic-system" -> state.stateName() + " buyer workflow";
            case "septic-inspection-cost" -> state.stateName() + " inspection file path";
            case "perc-test-cost" -> state.stateName() + " perc path";
            case "failed-perc-test-septic" -> state.stateName() + " failed-perc path";
            case "septic-replacement-cost" -> state.stateName() + " replacement path";
            case "drain-field-replacement-cost" -> state.stateName() + " drain field path";
            case "septic-replacement-area" -> state.stateName() + " replacement-area path";
            case "wet-yard-over-septic-drain-field" -> state.stateName() + " field-failure path";
            case "septic-pumping-cost" -> state.stateName() + " maintenance path";
            default -> state.stateName() + " state page";
        };
    }

    private String stateGuideHeroWorkflowLabel(StateMoneyPage page) {
        return switch (page.contentSlug()) {
            case "septic-permit-process" -> "Open permit workflow";
            case "septic-records-checklist" -> "Open records lookup";
            case "buying-a-house-with-a-septic-system" -> "Open buyer workflow";
            case "perc-test-cost" -> "Open perc page";
            case "failed-perc-test-septic" -> "Open failed-perc page";
            case "septic-replacement-cost" -> "Open replacement path";
            case "drain-field-replacement-cost" -> "Open drain field page";
            case "septic-replacement-area" -> "Open replacement-area guide";
            case "wet-yard-over-septic-drain-field" -> "Open wet-yard guide";
            case "septic-inspection-cost" -> "Open inspection page";
            default -> "Open state page";
        };
    }

    private int growthPageScore(StateMoneyPage page, StateProfile state) {
        int score = switch (page.contentSlug()) {
            case "septic-permit-process" -> 120;
            case "septic-records-checklist" -> 116;
            case "buying-a-house-with-a-septic-system" -> 112;
            case "septic-replacement-cost" -> 108;
            case "perc-test-cost" -> 104;
            case "drain-field-replacement-cost" -> 78;
            case "failed-perc-test-septic" -> 74;
            case "septic-replacement-area" -> 70;
            case "wet-yard-over-septic-drain-field" -> 66;
            case "septic-inspection-cost" -> 60;
            case "septic-pumping-cost" -> 24;
            default -> 40;
        };

        score += switch (state.stateCode()) {
            case "TN" -> 28;
            case "AL", "IN" -> 20;
            case "TX" -> 16;
            case "NC" -> 14;
            case "WA" -> 18;
            case "NJ" -> 12;
            case "MO" -> 8;
            default -> 0;
        };

        if (CORE_STATE_CODES.contains(state.stateCode())) {
            score += 30;
        }
        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 8;
        }
        if (page.highlightBuyerTrigger()) {
            score += 4;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private StateCoverageCardView publishedCoverageCard(StateProfile state) {
        int liveIntentCount = researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
        String confidenceLabel = confidenceLabel(state.confidenceScore());
        String metaLine = liveIntentCount + " live intent pages"
                + " | " + size(state.officialSourceIds()) + " official sources"
                + (confidenceLabel.isBlank() ? "" : " | " + confidenceLabel);
        return new StateCoverageCardView(
                state.stateCode(),
                state.stateName(),
                true,
                "Live guide",
                "live",
                state.pageAngle(),
                metaLine,
                "/septic-system-cost-calculator/" + state.slug() + "/",
                "Open live guide"
        );
    }

    private StateCoverageCardView queuedCoverageCard(UsStateDirectoryService.UsStateReference stateReference) {
        Optional<StateQueuePlan> queuePlan = stateQueuePlanService.findByStateCode(stateReference.stateCode());
        if (queuePlan.isPresent()) {
            PageLink recommendedLink = pageLink(queuePlan.get().recommendedPath(), "septic-system-cost-calculator", null);
            return new StateCoverageCardView(
                    stateReference.stateCode(),
                    stateReference.stateName(),
                    false,
                    "Priority #" + queuePlan.get().priorityRank(),
                    "queued",
                    queuePlan.get().launchAngle(),
                    queuePlan.get().rolloutWave() + " | Start with " + recommendedLink.title(),
                    "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                    "Open rollout plan"
            );
        }
        return new StateCoverageCardView(
                stateReference.stateCode(),
                stateReference.stateName(),
                false,
                "Research queue",
                "queued",
                "Official-source guide work has not been published yet. Use the national estimator while this state moves through the source and workflow review queue.",
                "Queued for 50-state rollout | not yet published",
                "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                "Open research page"
        );
    }

    private String renderPumpScheduleEstimator(Model model, PumpScheduleForm pumpScheduleForm, PumpScheduleResult result) {
        model.addAttribute("page", seoService.pumpScheduleEstimatorPage());
        model.addAttribute("pumpScheduleForm", pumpScheduleForm);
        model.addAttribute("result", result);
        return "pages/pump-schedule-estimator";
    }

    private String renderSitePage(
            Model model,
            PageMeta page,
            String eyebrow,
            String heading,
            String intro,
            List<SitePageSection> sections,
            String calloutTitle,
            String calloutBody
    ) {
        model.addAttribute("page", page);
        model.addAttribute("eyebrow", eyebrow);
        model.addAttribute("heading", heading);
        model.addAttribute("intro", intro);
        model.addAttribute("sections", sections);
        model.addAttribute("calloutTitle", calloutTitle);
        model.addAttribute("calloutBody", calloutBody);
        return "pages/site-page";
    }

    private String renderTrustOperationsPage(Model model, PageMeta page, TrustOperationsPageView operationsPage) {
        model.addAttribute("page", page);
        model.addAttribute("operationsPage", operationsPage);
        return "pages/trust-operations-page";
    }

    private String renderWorkflowPacketPage(Model model, PageMeta page, WorkflowPacketView packet) {
        model.addAttribute("page", page);
        model.addAttribute("packet", packet);
        return "pages/workflow-packet-page";
    }

    private String renderContactPage(
            Model model,
            ContactRequestForm contactRequestForm,
            boolean contactHasErrors,
            String contactRequestId
    ) {
        model.addAttribute("page", seoService.basicPage(
                "Contact",
                "Contact the project for general questions, source corrections, privacy requests, and partnership inquiries.",
                "/contact/"
        ));
        model.addAttribute("contactRequestForm", contactRequestForm);
        model.addAttribute("contactHasErrors", contactHasErrors);
        model.addAttribute("contactRequestId", contactRequestId);
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        return "pages/contact-page";
    }

    private String calculatorPathForModule(String calculatorModule) {
        return switch (calculatorModule) {
            case "tank_size_estimator" -> "/septic-tank-size-estimator/";
            case "pump_schedule_estimator" -> "/septic-pump-schedule-estimator/";
            case "drainfield_estimator" -> "/drain-field-estimator/";
            default -> "/septic-system-cost-calculator/";
        };
    }

    private String primaryActionPathForContentPage(ContentPage contentPage, String sourcePage) {
        if (RECORDS_REQUEST_BUILDER_SLUG.equals(contentPage.slug())) {
            return "#records-request-builder";
        }
        if (shouldLeadWithStateWorkflow(contentPage)) {
            return "#state-pages";
        }
        return calculatorPathForContentPage(contentPage, sourcePage);
    }

    private String officialLookupPrimaryUrl(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case TDEC_RECORDS_SLUG -> TENNESSEE_SSDS_PROGRAM_URL;
            case NC_PERMIT_LOOKUP_SLUG -> "https://www.deq.nc.gov/about/divisions/water-resources/water-resources-public-information/public-records";
            case TX_OSSF_RECORDS_SLUG -> "https://www.tceq.texas.gov/permitting/ossf";
            case FL_OSTDS_LOOKUP_SLUG -> "https://floridadep.gov/water/onsite-sewage";
            case DHEC_PERMIT_LOOKUP_SLUG -> "https://des.sc.gov/permits-regulations/septic-tanks";
            default -> null;
        };
    }

    private String officialLookupPrimaryLabel(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case TDEC_RECORDS_SLUG -> "Open official Tennessee SSDS page";
            case NC_PERMIT_LOOKUP_SLUG -> "Open North Carolina public records";
            case TX_OSSF_RECORDS_SLUG -> "Open TCEQ OSSF information";
            case FL_OSTDS_LOOKUP_SLUG -> "Open Florida OSTDS information";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Open SCDES septic records";
            default -> "";
        };
    }

    private String secondaryActionPathForContentPage(ContentPage contentPage, String sourcePage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return calculatorPathForContentPage(contentPage, sourcePage);
    }

    private String calculatorPathForContentPage(ContentPage contentPage, String sourcePage) {
        String modulePath = calculatorPathForModule(contentPage.calculatorModule());
        if (!"/septic-system-cost-calculator/".equals(modulePath)) {
            return modulePath;
        }
        if (contentPage.calculatorProjectType() == null || contentPage.calculatorProjectType().isBlank()) {
            return appendSourcePageHint(modulePath, sourcePage);
        }
        return appendSourcePageHint("/septic-system-cost-calculator/?projectType=" + contentPage.calculatorProjectType(), sourcePage);
    }

    private String contentQuotePathForContentPage(ContentPage contentPage, String sourcePage) {
        String calculatorPath = calculatorPathForContentPage(contentPage, sourcePage);
        if ("/drain-field-estimator/".equals(calculatorPath)) {
            calculatorPath = appendSourcePageHint("/septic-system-cost-calculator/?projectType=drainfield_replacement", sourcePage);
        }
        if (!calculatorPath.startsWith("/septic-system-cost-calculator/")) {
            return null;
        }
        return calculatorPath + (calculatorPath.contains("?") ? "&" : "?") + "quoteMode=true#quote-request";
    }

    private String appendSourcePageHint(String path, String sourcePage) {
        if (sourcePage == null || sourcePage.isBlank() || !path.startsWith("/")) {
            return path;
        }
        return org.springframework.web.util.UriComponentsBuilder.fromUriString(path)
                .queryParam("sourcePageHint", sourcePage)
                .build()
                .toUriString();
    }

    private boolean isValidSourcePageHint(String sourcePageHint) {
        return sourcePageHint != null
                && !sourcePageHint.isBlank()
                && sourcePageHint.startsWith("/")
                && !sourcePageHint.startsWith("//");
    }

    private String contentActionHeading(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Use the replacement estimate before you compare contractor quotes.";
            case "perc-test-cost" -> "Open a state perc page first.";
            case "drain-field-replacement-cost" -> "Open a state drain field page first.";
            case "failed-perc-test-septic" -> "Open a state failed-perc page first.";
            case "septic-replacement-area" -> "Use the field-layout estimate before you assume the parcel still has a viable backup area.";
            case "wet-yard-over-septic-drain-field" -> "Use the field-failure estimate before you treat a wet yard as a small repair story.";
            case "septic-inspection-cost" -> "Use the inspection-risk estimate after you know what the file is missing.";
            case "buying-a-house-with-a-septic-system" -> "Open a state buyer page first.";
            case RECORDS_ONLINE_SLUG -> "Open the real records path before pricing the septic story.";
            case RECORDS_BY_COUNTY_SLUG -> "Open the county records path first.";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Turn the address search into a real file path.";
            case PERMIT_RECORDS_REQUEST_SLUG -> "Send the records request before you trust the price story.";
            case RECORDS_REQUEST_BUILDER_SLUG -> "Build the records request before another search.";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Find the installed layout before you estimate the field story.";
            case INSPECTION_LETTER_SLUG -> "Separate the letter workflow from a generic permit copy.";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Open the official lookup tool before another overview.";
            case TDEC_RECORDS_SLUG -> "Route the parcel to the office before you treat the file as missing.";
            case NC_PERMIT_LOOKUP_SLUG -> "Find the county health file before broad North Carolina research.";
            case TX_OSSF_RECORDS_SLUG -> "Route Texas OSSF records through the county or TCEQ lane.";
            case FL_OSTDS_LOOKUP_SLUG -> "Start Florida OSTDS lookup with the county DOH path.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Translate DHEC searches into the current SCDES septic route.";
            case PERMIT_LOOKUP_SLUG -> "Open a state permit lookup path first.";
            case "septic-permit-process" -> "Open a state permit page first.";
            case "septic-records-checklist" -> "Open a state records lookup first.";
            case TRANSFER_COMPLIANCE_SLUG -> "Open a state transfer page first.";
            case "septic-tank-size" -> "Open the tank size estimator before you guess the minimum gallon band.";
            case "septic-pumping-cost" -> "Open the pump schedule estimator before you assume a maintenance cadence.";
            default -> "Use the main estimator before you ask for quotes.";
        };
    }

    private String contentActionLabel(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Run a replacement planning estimate";
            case "perc-test-cost" -> "Open state perc pages";
            case "drain-field-replacement-cost" -> "Open state drain field pages";
            case "failed-perc-test-septic" -> "Open state failed-perc pages";
            case "septic-replacement-area" -> "Run a replacement-area estimate";
            case "wet-yard-over-septic-drain-field" -> "Run a field-failure estimate";
            case "septic-inspection-cost" -> "Run an inspection-scope estimate";
            case "buying-a-house-with-a-septic-system" -> "Open state buyer pages";
            case RECORDS_ONLINE_SLUG -> "Open records lookup pages";
            case RECORDS_BY_COUNTY_SLUG -> "Open county records pages";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Open address lookup routes";
            case PERMIT_RECORDS_REQUEST_SLUG -> "Open records request routes";
            case RECORDS_REQUEST_BUILDER_SLUG -> "Build a request script";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Open as-built record routes";
            case INSPECTION_LETTER_SLUG -> "Open inspection-letter routes";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Open official lookup tools";
            case TDEC_RECORDS_SLUG -> "Open Tennessee records routes";
            case NC_PERMIT_LOOKUP_SLUG -> "Open North Carolina records routes";
            case TX_OSSF_RECORDS_SLUG -> "Open Texas OSSF records routes";
            case FL_OSTDS_LOOKUP_SLUG -> "Open Florida OSTDS records routes";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Open South Carolina DHEC/SCDES routes";
            case PERMIT_LOOKUP_SLUG -> "Open state permit lookup pages";
            case "septic-permit-process" -> "Open state permit pages";
            case "septic-records-checklist" -> "Open state records lookup pages";
            case TRANSFER_COMPLIANCE_SLUG -> "Open state transfer pages";
            case "septic-tank-size" -> "Open the tank size estimator";
            case "septic-pumping-cost" -> "Open the pump schedule estimator";
            default -> "Open the main estimator";
        };
    }

    private String contentActionNote(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Prefill the replacement lane first so field condition, restoration, and system-class risk show up before you talk price.";
            case "perc-test-cost" -> "Perc, soil-test, and site-evaluation language changes by state, so the state-specific page is the faster first move. Use the estimate after the local file and site path are clearer.";
            case "drain-field-replacement-cost" -> "Field replacement, reserve-area risk, and redesign pressure vary by state and file history, so the state-specific page is the faster first move. Use the estimate after the layout and records path are clearer.";
            case "failed-perc-test-septic" -> "Failed perc results turn into different permit, reserve-area, and redesign stories by state, so the state-specific page is the faster first move. Use the estimate after the review path is clearer.";
            case "septic-replacement-area" -> "Use the drain field lane when reserve area, replacement footprint, or code-complying layout risk is the main blocker.";
            case "wet-yard-over-septic-drain-field" -> "Use the drain field lane when seepage, odor, or soggy ground near the field is already visible.";
            case "septic-inspection-cost" -> "Pull the permit file, as-built, pumping history, and O&M records first, then use the estimate to judge whether the visit is routine diligence or leverage for a bigger next step.";
            case "buying-a-house-with-a-septic-system" -> "Transfer rules, county records, inspection triggers, and bedroom-use mismatches vary enough that the state-specific page is the faster first move.";
            case RECORDS_ONLINE_SLUG -> "Start with the state or county lookup route that can name the actual file owner, then use the estimate after the permit, as-built, or repair record path is clearer.";
            case RECORDS_BY_COUNTY_SLUG -> "The county page is the fast path when the county is already known. Use it to identify the file owner and first artifact before opening the estimate.";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Search by address first, then retry with parcel, owner, legal description, subdivision, or permit number before treating a missing result as real.";
            case PERMIT_RECORDS_REQUEST_SLUG -> "The request should name the exact artifact needed, because permit copy, as-built, final approval, repair file, and inspection letter answer different questions.";
            case RECORDS_REQUEST_BUILDER_SLUG -> "Choose the record type, reason, state, and parcel clues once so the request language is precise enough for the office to act on.";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Use the layout record to decide whether tank, field, reserve area, or addition risk is changing the scope before you estimate.";
            case INSPECTION_LETTER_SLUG -> "A permit copy may not satisfy a closing or lender letter, so resolve the letter path before the price conversation carries weight.";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Start with the official source when one exists, then fall back to county request language when the search tool does not expose the file.";
            case TDEC_RECORDS_SLUG -> "Start on the working Tennessee SSDS program page, then use the field-office or public-records route that owns the parcel file. Treat the TDEC record search as optional while it returns 403 for some users.";
            case NC_PERMIT_LOOKUP_SLUG -> "North Carolina records usually resolve through county environmental health, so use the county route or request script when a statewide page is too broad.";
            case TX_OSSF_RECORDS_SLUG -> "Texas OSSF records often depend on the authorized agent or county, so use the state OSSF context and county handoff together.";
            case FL_OSTDS_LOOKUP_SLUG -> "Florida OSTDS records are handled through county health workflows, eBridge-style archives, or county-specific public records paths.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Many searchers still type DHEC, but the current South Carolina path should point them into SCDES septic tank, ePermitting, county contact, D-1740, and permit-copy workflows.";
            case PERMIT_LOOKUP_SLUG -> "The useful lookup is usually state plus county: open the state records or permit path first, then follow the county file route when the state page exposes one.";
            case "septic-permit-process" -> "The first real answer is usually which office, file, or site-review step controls this property, so start with the state-specific permit page before you model the cost.";
            case "septic-records-checklist" -> "County records lookup, permit search, and as-built availability vary enough that the state-specific records page is the faster first move.";
            case TRANSFER_COMPLIANCE_SLUG -> "Transfer problems usually resolve through records, permit path, buyer timing, and county workflow, so open the state-specific page before you try to compress everything into one quote number.";
            case "septic-tank-size" -> "Use the dedicated estimator when bedroom count, occupancy profile, or disposal load matter more than a full project quote.";
            case "septic-pumping-cost" -> "Use the dedicated estimator when cadence, use profile, and tank size matter more than a one-time pumping invoice.";
            default -> "Use your state and project assumptions first, then verify locally.";
        };
    }

    private String contentActionTargetType(ContentPage contentPage) {
        if (RECORDS_REQUEST_BUILDER_SLUG.equals(contentPage.slug())) {
            return "internal_section";
        }
        if (shouldLeadWithStateWorkflow(contentPage)) {
            return "state_money_page_directory";
        }
        return switch (contentPage.calculatorModule()) {
            case "tank_size_estimator" -> "tank_size_estimator";
            case "pump_schedule_estimator" -> "pump_schedule_estimator";
            case "drainfield_estimator" -> "drainfield_estimator";
            default -> "calculator";
        };
    }

    private String secondaryActionLabel(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.slug()) {
            case "perc-test-cost" -> "Run a site-risk estimate";
            case "drain-field-replacement-cost" -> "Run a drain field replacement estimate";
            case "failed-perc-test-septic" -> "Run a failed-perc estimate";
            case "buying-a-house-with-a-septic-system" -> "Run a buyer due-diligence estimate";
            case RECORDS_ONLINE_SLUG -> "Run a records-aware estimate";
            case RECORDS_BY_COUNTY_SLUG -> "Run a county-aware estimate";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Run an address-lookup estimate";
            case PERMIT_RECORDS_REQUEST_SLUG -> "Run a records-request estimate";
            case RECORDS_REQUEST_BUILDER_SLUG -> "Run a records-aware estimate";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Run a layout-risk estimate";
            case INSPECTION_LETTER_SLUG -> "Run a letter-aware estimate";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Run a lookup-aware estimate";
            case TDEC_RECORDS_SLUG -> "Run a Tennessee records-aware estimate";
            case NC_PERMIT_LOOKUP_SLUG -> "Run a North Carolina records-aware estimate";
            case TX_OSSF_RECORDS_SLUG -> "Run a Texas OSSF-aware estimate";
            case FL_OSTDS_LOOKUP_SLUG -> "Run a Florida OSTDS-aware estimate";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Run a South Carolina records-aware estimate";
            case PERMIT_LOOKUP_SLUG -> "Run a lookup-aware estimate";
            case "septic-permit-process" -> "Run a permit-path estimate";
            case "septic-records-checklist" -> "Run a records-aware estimate";
            case TRANSFER_COMPLIANCE_SLUG -> "Run a transfer-risk estimate";
            default -> "Open the main estimator";
        };
    }

    private String secondaryActionNote(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.slug()) {
            case "perc-test-cost" -> "Use the estimate after you know whether the real blocker is a perc result, a broader site evaluation, or county permit routing.";
            case "drain-field-replacement-cost" -> "Use the estimate after you know whether the live issue is field-only scope, replacement area, or a wider redesign path.";
            case "failed-perc-test-septic" -> "Use the estimate after you know whether the live issue is retesting, reserve area, permit routing, or a broader redesign path.";
            case "buying-a-house-with-a-septic-system" -> "Pull the permit file, as-built, pumping history, and bedroom-use story first, then use the estimate to judge whether the deal risk is routine diligence, a credit fight, or a wider replacement problem.";
            case RECORDS_ONLINE_SLUG -> "Use the estimate after the lookup shows whether the missing artifact is a routine request, a county routing problem, or a record gap that widens the downside.";
            case RECORDS_BY_COUNTY_SLUG -> "Use the estimate after the county file owner, first artifact, and repair or closeout trail are clear enough to support a price story.";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Use the estimate after the address search has been checked against parcel, owner, legal-description, or permit-number search paths.";
            case PERMIT_RECORDS_REQUEST_SLUG -> "Use the estimate after the records request shows whether the missing artifact is routine, delayed, or a real file gap.";
            case RECORDS_REQUEST_BUILDER_SLUG -> "Use the estimate after the generated request clarifies whether the missing file is a permit copy, as-built, final approval, repair record, or letter workflow.";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Use the estimate after the as-built or replacement layout risk is clear enough to separate locate work from design or replacement scope.";
            case INSPECTION_LETTER_SLUG -> "Use the estimate after the office confirms whether the letter is records-only, inspection-based, lender-specific, or blocked by file issues.";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Use the estimate after the official lookup or county fallback shows whether the missing record changes the downside.";
            case TDEC_RECORDS_SLUG -> "Use the estimate after TDEC or the county route confirms whether the Tennessee parcel file is routine, missing, or repair-linked.";
            case NC_PERMIT_LOOKUP_SLUG -> "Use the estimate after the county environmental health file or request route clarifies what North Carolina artifact is missing.";
            case TX_OSSF_RECORDS_SLUG -> "Use the estimate after the Texas OSSF county route clarifies whether the issue is permit history, approved plan, maintenance, or repair.";
            case FL_OSTDS_LOOKUP_SLUG -> "Use the estimate after the county DOH or archive route clarifies whether the Florida OSTDS record is available or needs a formal request.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Use the estimate after SCDES or the county route clarifies whether the issue is D-1740, permit copy, final inspection, or a missing file.";
            case PERMIT_LOOKUP_SLUG -> "Use the estimate after the lookup tells you whether the file gap is routine, a county routing problem, or a repair-permit risk.";
            case "septic-permit-process" -> "Use the estimate after you know whether the real blocker is authority routing, project classification, or site review.";
            case "septic-records-checklist" -> "Use the estimate after you know which missing record is actually changing the downside.";
            case TRANSFER_COMPLIANCE_SLUG -> "Use the estimate after you know whether the live issue is missing records, permit sequence, buyer inspection timing, or a county-file gap that changes the downside.";
            default -> "Use your state and project assumptions first, then verify locally.";
        };
    }

    private String secondaryActionTargetType(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.calculatorModule()) {
            case "tank_size_estimator" -> "tank_size_estimator";
            case "pump_schedule_estimator" -> "pump_schedule_estimator";
            case "drainfield_estimator" -> "drainfield_estimator";
            default -> "calculator";
        };
    }

    private boolean shouldLeadWithStateWorkflow(ContentPage contentPage) {
        return "workflow_page".equals(contentPage.intentType())
                || "buyer_page".equals(contentPage.intentType())
                || "perc-test-cost".equals(contentPage.slug())
                || "drain-field-replacement-cost".equals(contentPage.slug())
                || "failed-perc-test-septic".equals(contentPage.slug());
    }

    private boolean isTransferComplianceHub(ContentPage contentPage) {
        return TRANSFER_COMPLIANCE_SLUG.equals(contentPage.slug());
    }

    private boolean isPermitLookupHub(ContentPage contentPage) {
        return RECORDS_INTENT_HUB_SLUGS.contains(contentPage.slug());
    }

    private List<CountyWorkflowFieldView> contentOfficialFilePathRows(
            ContentPage contentPage,
            List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries,
            List<CountyFinderLinkView> countyFinderLinks
    ) {
        if (!isPermitLookupHub(contentPage)) {
            return List.of();
        }
        return List.of(
                new CountyWorkflowFieldView(
                        "File owner",
                        contentFileOwner(contentPage)
                ),
                new CountyWorkflowFieldView(
                        "Lookup clue",
                        contentLookupClue(contentPage)
                ),
                new CountyWorkflowFieldView(
                        "First artifact",
                        contentFirstArtifact(contentPage)
                ),
                new CountyWorkflowFieldView(
                        "Request method",
                        contentRequestMethod(contentPage)
                ),
                new CountyWorkflowFieldView(
                        "No-record fallback",
                        contentNoRecordFallback(contentPage)
                ),
                new CountyWorkflowFieldView(
                        "Fast handoff",
                        contentFastHandoff(contentPage, rankedStateEntries, countyFinderLinks)
                )
        );
    }

    private String contentFileOwner(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case TDEC_RECORDS_SLUG -> "The Tennessee Environmental Field Office or contract county that can locate the parcel's SSDS permit, layout, final approval, repair history, or archived file.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Current South Carolina septic routing starts with SCDES, then resolves through ePermitting, county contacts, or the office holding the D-1740 and permit-copy trail.";
            case TX_OSSF_RECORDS_SLUG -> "Texas OSSF records usually resolve through the county, authorized agent, or local permitting authority after the TCEQ program context is clear.";
            case NC_PERMIT_LOOKUP_SLUG -> "North Carolina septic records usually resolve through county environmental health rather than one statewide public lookup.";
            case FL_OSTDS_LOOKUP_SLUG -> "Florida OSTDS records usually resolve through county health, county archives, or a county-specific public records path.";
            case RECORDS_BY_COUNTY_SLUG -> "The file owner is usually the named county health, environmental health, development services, or delegated local office.";
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "The real owner is the office that can match the address to the parcel, permit history, and septic file owner.";
            case PERMIT_RECORDS_REQUEST_SLUG, RECORDS_REQUEST_BUILDER_SLUG -> "The file owner is whichever state, county, regional, or delegated office can provide a written response tied to the parcel.";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "The file owner is the office that stores the installed layout, site sketch, final approval, or scanned permit package.";
            case INSPECTION_LETTER_SLUG -> "The file owner may be different from the letter issuer, so separate permit-copy records from closing, lender, or inspection-letter workflows.";
            default -> "Start with the official state or county source, then verify the local office that actually owns the septic file.";
        };
    }

    private String contentLookupClue(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Start with street address, then retry with parcel ID, APN/TMS, owner name, legal description, subdivision, street-only search, or prior permit number.";
            case RECORDS_BY_COUNTY_SLUG -> "Start with state and county, then carry address, parcel, owner, and permit clues into the county route.";
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "Carry parcel ID, owner, permit number, installation year, subdivision, and any prior repair or addition clue into the layout request.";
            case TDEC_RECORDS_SLUG -> "Use parcel, address, owner, county, permit number, and regional-office clues before treating a missing TDEC result as a missing file.";
            case TX_OSSF_RECORDS_SLUG -> "Use county, city-limit or ETJ status, parcel, legal description, subdivision, and owner clues before assuming TCEQ itself holds the file.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Use county, parcel/TMS, address, owner, D-1740 wording, and ePermitting clues because many searches still say DHEC while the route is now SCDES.";
            default -> "Carry address, parcel/APN/TMS, owner, legal description, subdivision, county, and any prior permit number into the next official route.";
        };
    }

    private String contentFirstArtifact(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "As-built, site sketch, installed layout, tank and drain-field location, reserve-area note, or scanned approval package.";
            case PERMIT_RECORDS_REQUEST_SLUG, RECORDS_REQUEST_BUILDER_SLUG -> "Permit copy, as-built, final approval, repair file, inspection letter, or written no-record response.";
            case INSPECTION_LETTER_SLUG -> "Inspection letter, final approval, permit copy, lender letter, or written explanation of why the office cannot issue one.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "D-1740 application trail, septic permit copy, final inspection, county contact note, or written no-record response.";
            case TDEC_RECORDS_SLUG -> "TDEC SSDS permit record, inspection note, repair-permit trail, county fallback, or written no-record response.";
            case TX_OSSF_RECORDS_SLUG -> "OSSF permit history, approved plan, site evaluation, license-to-operate or final approval, maintenance note, or repair file.";
            case FL_OSTDS_LOOKUP_SLUG -> "OSTDS permit, final approval, site plan, county health archive record, repair file, or written no-record response.";
            default -> "Permit copy, as-built, final approval, inspection letter, repair history, or written no-record response tied to the parcel.";
        };
    }

    private String contentRequestMethod(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Search the official route first, then convert the same address and parcel clues into a request if the lookup does not expose the file.";
            case PERMIT_RECORDS_REQUEST_SLUG, RECORDS_REQUEST_BUILDER_SLUG -> "Send a precise request that names the parcel, record type, reason, and fallback question instead of asking vaguely for septic records.";
            case RECORDS_BY_COUNTY_SLUG -> "Open the county page first, then use the request wording when the portal, clerk, or health office does not expose the artifact directly.";
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> "Use the official tool first when it exists, then fall back to county route, records request, or written no-record wording.";
            case TDEC_RECORDS_SLUG -> "Confirm the SSDS program on TN.gov, then contact the field office or submit a public-records request with the parcel clues. Use the restricted direct search only as an optional attempt.";
            case DHEC_PERMIT_LOOKUP_SLUG -> "Translate DHEC intent into current SCDES/ePermitting/county contact steps, then request the permit copy or D-1740 trail.";
            default -> "Start with the official source, carry property identifiers into the route, then request the exact artifact if the record is not visible.";
        };
    }

    private String contentNoRecordFallback(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG -> "If no layout is found, ask for a written no-record response plus the office that may hold archived, scanned, repair, or pre-digital layout files.";
            case INSPECTION_LETTER_SLUG -> "If no letter can be issued, ask whether a permit copy, final approval, inspection note, or written file status can substitute.";
            default -> "If the lookup has no match, ask for a written no-record response and the next office that owns archived, regional, delegated, or pre-digital septic files.";
        };
    }

    private String contentFastHandoff(
            ContentPage contentPage,
            List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries,
            List<CountyFinderLinkView> countyFinderLinks
    ) {
        String stateRoute = rankedStateEntries == null ? null : rankedStateEntries.stream()
                .findFirst()
                .map(entry -> stateSurfaceRouteTitle(entry.getKey(), entry.getValue()))
                .orElse(null);
        String countyRoute = countyFinderLinks == null ? null : countyFinderLinks.stream()
                .findFirst()
                .map(link -> link.countyName() + ", " + link.stateCode() + " county route")
                .orElse(null);
        if (RECORDS_BY_COUNTY_SLUG.equals(contentPage.slug()) && hasText(countyRoute)) {
            return "Start with " + countyRoute + " when the county is known; use the state page only when the county route is not clear.";
        }
        if (hasText(stateRoute) && hasText(countyRoute)) {
            return "Use " + stateRoute + " for the state lane, then drop into " + countyRoute + " when the search already names a local office.";
        }
        if (hasText(stateRoute)) {
            return "Use " + stateRoute + " as the first live state route, then move to county or request wording when the file is still not visible.";
        }
        return "Use official tools, county records pages, and request wording in that order so the searcher does not restart on Google.";
    }

    private List<CountyWorkflowFieldView> guideOfficialFilePathRows(
            StateProfile state,
            SourceRecord primaryLocalAuthoritySource,
            SourceRecord primaryRecordsLookupSource,
            StateCountyWorkflowSynthesisView guideCountyWorkflowSynthesis
    ) {
        SourceRecord fileSource = primaryRecordsLookupSource != null
                ? primaryRecordsLookupSource
                : primaryLocalAuthoritySource;
        String officialOwner = fileSource == null
                ? state.agencyName()
                : sourceDisplayName(fileSource);
        String firstArtifact = firstNonBlank(
                guideCountyWorkflowSynthesis == null || guideCountyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : guideCountyWorkflowSynthesis.firstArtifacts().get(0),
                firstOf(state.recordsToRequest()),
                "Permit copy, as-built, final approval, inspection note, repair history, or written no-record response."
        );
        String countyPattern = firstNonBlank(
                guideCountyWorkflowSynthesis == null || guideCountyWorkflowSynthesis.structureHighlights().isEmpty()
                        ? null
                        : guideCountyWorkflowSynthesis.structureHighlights().get(0).value(),
                state.whoToCallFirst()
        );
        String requestMethod = primaryRecordsLookupSource != null
                ? "Open " + primaryRecordsLookupSource.title() + " first, then use county or regional request wording if the parcel file is not visible."
                : "Start with " + state.whoToCallFirst() + ", then ask which local office owns the old septic file before pricing.";

        return List.of(
                new CountyWorkflowFieldView(
                        "File owner",
                        officialOwner + ". Use this owner check before treating the state guide as a price page."
                ),
                new CountyWorkflowFieldView(
                        "First artifact",
                        firstArtifact
                ),
                new CountyWorkflowFieldView(
                        "Request method",
                        requestMethod
                ),
                new CountyWorkflowFieldView(
                        "County pattern",
                        countyPattern
                ),
                new CountyWorkflowFieldView(
                        "No-record fallback",
                        "If the " + state.stateName() + " file is not visible, ask for a written no-record response and the office that owns archived, delegated, county, or pre-digital septic records."
                ),
                new CountyWorkflowFieldView(
                        "Estimate gate",
                        "Run the " + state.stateName() + " calculator path after the file owner, parcel clue, or missing artifact is clear enough that the number is not flattening a records problem."
                )
        );
    }

    private List<PageLink> guideGrowthSearchLinks(StateProfile state, List<PageLink> countyRecordLinks) {
        if (!isGuideGrowthState(state.stateCode())) {
            return List.of();
        }

        List<PageLink> links = new ArrayList<>();
        researchDataService.findPublicStateMoneyPage("perc-test-cost", state.slug())
                .filter(page -> publishingPolicyService.isIndexableStateMoneyPage(page, state))
                .ifPresent(page -> links.add(new PageLink(
                        state.stateName() + " perc test cost",
                        page.path(state.slug()),
                        "Use this when the search is really about perc cost, soil review, site evaluation, or the county file behind the number."
                )));
        researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                .filter(page -> publishingPolicyService.isIndexableStateMoneyPage(page, state))
                .ifPresent(page -> links.add(new PageLink(
                        state.stateName() + " permit records",
                        page.path(state.slug()),
                        "Use this when the searcher needs the permit copy, as-built, final approval, inspection letter, or no-record fallback."
                )));
        links.add(new PageLink(
                "Search by address",
                "/septic-permit-search-by-address/",
                "Use address, parcel, owner, legal description, APN, TMS, or prior permit clues before the user restarts on Google."
        ));
        growthCountyRecordLink(state, countyRecordLinks)
                .ifPresent(links::add);
        links.add(new PageLink(
                "Records request wording",
                "/septic-permit-records-request/",
                "Use this when the office needs exact request language for permit copies, as-builts, final approvals, repair files, or no-record responses."
        ));
        return links.stream().limit(5).toList();
    }

    private Optional<PageLink> growthCountyRecordLink(StateProfile state, List<PageLink> countyRecordLinks) {
        if (countyRecordLinks == null || countyRecordLinks.isEmpty()) {
            return Optional.empty();
        }
        String preferredCountySlug = switch (state.stateCode()) {
            case "AL" -> "madison-county";
            case "TN" -> "blount-county";
            case "NC" -> "alamance-county";
            case "IN" -> "elkhart-county";
            case "SC" -> "greenville-county";
            case "TX" -> "tarrant-county";
            case "GA" -> "fulton-county";
            case "WV" -> "kanawha-county";
            case "MO" -> "st-louis-county";
            default -> "";
        };
        Optional<PageLink> preferred = countyRecordLinks.stream()
                .filter(link -> hasText(preferredCountySlug) && link.path().contains("/" + preferredCountySlug + "/"))
                .findFirst();
        PageLink baseLink = preferred.orElseGet(() -> countyRecordLinks.get(0));
        return Optional.of(new PageLink(
                baseLink.compactTitle(),
                baseLink.path(),
                "Use this county record example when the search already has a local office, county name, address, or parcel clue."
        ));
    }

    private List<CountyWorkflowFieldView> guideGrowthAnswerRows(
            StateProfile state,
            StateCountyWorkflowSynthesisView guideCountyWorkflowSynthesis
    ) {
        if (!isGuideGrowthState(state.stateCode())) {
            return List.of();
        }

        String firstArtifact = firstNonBlank(
                guideCountyWorkflowSynthesis == null || guideCountyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : guideCountyWorkflowSynthesis.firstArtifacts().get(0),
                firstOf(state.recordsToRequest()),
                "permit copy, as-built, final approval, inspection letter, or no-record response"
        );
        String holdQuote = firstNonBlank(
                guideCountyWorkflowSynthesis == null || guideCountyWorkflowSynthesis.holdQuoteChecks().isEmpty()
                        ? null
                        : guideCountyWorkflowSynthesis.holdQuoteChecks().get(0),
                "the local file owner, parcel clue, or missing artifact is still unclear"
        );
        String fileBeforeEstimateLabel = "AL".equals(state.stateCode())
                ? "Alabama county health file before the estimate"
                : state.stateName() + " county file before the estimate";

        return List.of(
                new CountyWorkflowFieldView(
                        "Answer the cost search first",
                        growthCostSearchAnswer(state)
                ),
                new CountyWorkflowFieldView(
                        fileBeforeEstimateLabel,
                        "Pull the county or state records path before treating a low septic, permit, perc, or inspection number as usable. Start with " + firstArtifact + "."
                ),
                new CountyWorkflowFieldView(
                        "Do not lose address-search users",
                        "If the searcher has an address, parcel clue, owner name, legal description, APN, or TMS, send them into the permit-search-by-address route before they restart in Google."
                ),
                new CountyWorkflowFieldView(
                        "Hold pricing until",
                        holdQuote
                )
        );
    }

    private String growthCostSearchAnswer(StateProfile state) {
        return switch (state.stateCode()) {
            case "AL" -> "How much is a perc test in Alabama depends on the county health file, prior soil review, Permit to Install status, and whether an Approval for Use already exists.";
            case "TN" -> "Tennessee septic and perc cost searches depend on whether the TDEC SSDS record, repair permit, inspection note, or county fallback already explains the parcel.";
            case "NC" -> "North Carolina septic permit and perc searches depend on the county environmental health file, improvement permit, construction authorization, and operations permit path.";
            case "IN" -> "Indiana septic cost searches depend on the county health permit file, soil report, sewer-availability issue, and whether the record can prove the existing system.";
            case "SC" -> "South Carolina septic cost searches depend on the SCDES/D-1740 path, permit copy, final inspection, and county contact that can verify the file.";
            case "TX" -> "Texas OSSF cost searches depend on the county or authorized agent file, ETJ or city-limit status, approved plan, and license-to-operate trail.";
            case "GA" -> "Georgia septic cost searches depend on county office routing, soil analysis, permit records, and whether the parcel file already shows the last approved system.";
            case "WV" -> "West Virginia septic cost searches depend on the local health file, sewage permit application, sanitarian record, and whether the property has a written file trail.";
            case "MO" -> "Missouri septic cost searches depend on the local authority, soil morphology or permit path, county file, and whether old records need a request.";
            default -> state.stateName() + " septic cost searches depend on the local file owner, first artifact, and whether the missing record changes the downside.";
        };
    }

    private boolean isGuideGrowthState(String stateCode) {
        return switch (stateCode) {
            case "AL", "TN", "NC", "IN", "SC", "TX", "GA", "WV", "MO" -> true;
            default -> false;
        };
    }

    private String latestVerifiedAt(List<SourceRecord> sources, String... pageDates) {
        Stream<String> sourceDates = sources == null
                ? Stream.empty()
                : sources.stream().map(SourceRecord::contentVerifiedAt);
        Stream<String> explicitDates = pageDates == null ? Stream.empty() : Arrays.stream(pageDates);
        return Stream.concat(sourceDates, explicitDates)
                .filter(this::isIsoDate)
                .max(String::compareTo)
                .orElse("");
    }

    private boolean isIsoDate(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private StateActionCopy stateActionCopy(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> new StateActionCopy(
                    "Estimate before the county file pull",
                    "Iowa quote conversations get more real once you know which county office or county sanitarian holds the file and whether the time-of-transfer record is already in view."
            );
            case "KS" -> new StateActionCopy(
                    "Estimate before the soil-profile check",
                    "Kansas quote conversations get more real once you know which local sanitary code controls the parcel and whether the lot is still on a straightforward soil-profile path."
            );
            case "NE" -> new StateActionCopy(
                    "Estimate before the permit filing",
                    "Nebraska quote conversations get more real once you know whether the parcel already has a registered-system file and whether the state permit path is still clean."
            );
            case "NM" -> new StateActionCopy(
                    "Estimate before the buyer file check",
                    "New Mexico quote conversations get more real once you know whether the permit-search result, homeowner notice, and transfer paperwork support the seller story."
            );
            case "UT" -> new StateActionCopy(
                    "Estimate before the health-district handoff",
                    "Utah quote conversations get more real once you know which local health department owns the file and whether soil logs, percolation tests, or an operating-permit wrinkle are already in play."
            );
            case "WV" -> new StateActionCopy(
                    "Estimate after the local file check",
                    "West Virginia quotes get real after you confirm the local health file, the sewage permit application, and any sanitarian or public-record trail."
            );
            case "SD" -> new StateActionCopy(
                    "Estimate before the permit certificate",
                    "South Dakota quote conversations get more real once you know whether the installation certificate, inspection sequence, and any city-run inspection exception are already in play."
            );
            case "ID" -> new StateActionCopy(
                    "Estimate before the site evaluation",
                    "Idaho quote conversations get more real once you know which public health district owns the file and whether the site evaluation or permit record is already in play."
            );
            case "NV" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Nevada quote conversations get more real once you know whether NDEP, Southern Nevada Health District, or another local path holds the file and whether the inspection and as-built trail is actually complete."
            );
            case "DE" -> new StateActionCopy(
                    "Estimate before the permit-file pull",
                    "Delaware quote conversations get more real once you know whether the DNREC report trail is usable and whether a county building-permit handoff changes the septic path."
            );
            case "ND" -> new StateActionCopy(
                    "Estimate before the local permit call",
                    "North Dakota quote conversations get more real once you know which local public health unit holds the permit file and whether inspection or complaint history is already in view."
            );
            case "WY" -> new StateActionCopy(
                    "Estimate before the county site check",
                    "Wyoming quote conversations get more real once you know which county issues the permit under DEQ delegation and whether perc, site-plan, or engineer-design friction is already in play."
            );
            case "AK" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Alaska quote conversations get more real once you know whether the local DEC office or the Municipality of Anchorage holds the approved-system record and whether difficult-site notes already widen the path."
            );
            case "HI" -> new StateActionCopy(
                    "Estimate before the cesspool-upgrade path",
                    "Hawaii quote conversations get more real once you know whether a cesspool trigger is active and whether the county building-permit handoff and approval-to-use file are already in play."
            );
            case "ME" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Maine quote conversations get more real once you know whether the town office can surface the HHE-200 and whether the Local Plumbing Inspector trail actually supports the property story."
            );
            case "NH" -> new StateActionCopy(
                    "Estimate before the approval-status check",
                    "New Hampshire quote conversations get more real once you know whether the property is operationally approved for the intended use and whether OneStop or archive records actually support the file story."
            );
            case "RI" -> new StateActionCopy(
                    "Estimate after the DEM file pull",
                    "Rhode Island quotes get real after you confirm the DEM search results, the full file, and any suitability or advanced-technology trigger."
            );
            case "VT" -> new StateActionCopy(
                    "Estimate before the regional-office handoff",
                    "Vermont quote conversations get more real once you know whether the parcel already has a state-issued permit, whether the Town changes the path, and which regional office owns the next filing."
            );
            case "MT" -> new StateActionCopy(
                    "Estimate before the lot-review check",
                    "Montana quote conversations get more real once you know whether the lot already has COSA or sanitary restrictions, whether the local health department still owns the drainfield permit, and whether DEQ-4 site-risk paperwork already widens the project."
            );
            case "AL" -> new StateActionCopy(
                    "Check Alabama county records before the estimate",
                    "Alabama quote conversations get more real once the county health file, Permit to Install, soil or perc test, and Approval for Use record are visible."
            );
            case "AR" -> new StateActionCopy(
                    "Estimate before the health-unit call",
                    "Arkansas quote conversations get more real once you know which county health unit holds the file and whether a permit copy or lot-suitability issue is already in view."
            );
            case "MS" -> new StateActionCopy(
                    "Estimate before the file lookup",
                    "Mississippi quote conversations get more real once you know whether the county health file and public-record trail can actually surface a site-evaluation record or Permit or Recommendation."
            );
            case "IN" -> new StateActionCopy(
                    "Estimate before the county permit call",
                    "Indiana quote conversations get more real once you know which county office holds the file and whether sewer availability or local ordinance variation changes the onsite path."
            );
            case "OK" -> new StateActionCopy(
                    "Estimate before the soil-test request",
                    "Oklahoma quote conversations get more real once you know which local DEQ office handles the parcel and whether the site still sits on a conventional soil-test path."
            );
            case "KY" -> new StateActionCopy(
                    "Estimate before the local health file pull",
                    "Kentucky quote conversations get more real once you know whether the local health department already holds the site-evaluation and permit file behind the property story."
            );
            case "MN" -> new StateActionCopy(
                    "Estimate before the disclosure check",
                    "Minnesota quote conversations get more real once you know which local SSTS program controls the sale and whether disclosure or compliance-inspection friction is already in play."
            );
            case "IL" -> new StateActionCopy(
                    "Estimate before the local file pull",
                    "Illinois quote conversations get more real once you know which county or local health department holds the file and whether an evaluation form or flagged condition is already in view."
            );
            case "MD" -> new StateActionCopy(
                    "Estimate before the property-transfer file search",
                    "Maryland quote conversations get more real once you know which local approving authority holds the file and whether a PTI-backed transfer record is already in play."
            );
            case "WI" -> new StateActionCopy(
                    "Estimate with county maintenance tracking in mind",
                    "Wisconsin quote conversations get more real once you know which county or delegated agent owns the file and whether maintenance-tracking and inspection records are current."
            );
            case "LA" -> new StateActionCopy(
                    "Estimate before the parish health unit call",
                    "Louisiana quote conversations get more real once you know whether the parish office still treats the parcel as a septic path and whether sewer availability or packet friction changes the site story."
            );
            case "AZ" -> new StateActionCopy(
                    "Estimate before site approval",
                    "Arizona quote conversations get more real once you know which county controls the permit file and whether the site-investigation paperwork is already on record."
            );
            case "CO" -> new StateActionCopy(
                    "Estimate before calling the local public health agency",
                    "Colorado quote conversations get more real once you know which local public health agency owns the file and whether site-and-soil or transfer-of-title paperwork is already in play."
            );
            case "VA" -> new StateActionCopy(
                    "Estimate before the local health-district call",
                    "Virginia quote conversations get more real once you know whether the system is conventional or AOSS and whether operation-permit or inspection records are already in the file."
            );
            case "TN" -> new StateActionCopy(
                    "Estimate before the permit-file pull",
                    "Tennessee quote conversations get more real once you know whether the parcel runs through a contract county or TDEC contact and whether a repair permit or inspection letter is already in the file."
            );
            case "SC" -> new StateActionCopy(
                    "Estimate after the permit-copy pull",
                    "South Carolina quotes get real after you confirm the SCDES office, the D-1740 path, the permit copy, and final-inspection status."
            );
            case "CA" -> new StateActionCopy(
                    "Estimate before the county file pull",
                    "California usually gets real once you know the local agency path and whether the property sits in a default Tier 1 workflow or a LAMP-driven local program."
            );
            case "TX" -> new StateActionCopy(
                    "Estimate before calling the permitting authority",
                    "Texas quote conversations get sharper once you know the local permitting authority and whether the site evaluation is already on file."
            );
            case "NY" -> new StateActionCopy(
                    "Estimate with Appendix 75-A context",
                    "New York questions often turn on Appendix 75-A, county health files, and any waiver history rather than the seller's simple septic summary."
            );
            case "OH" -> new StateActionCopy(
                    "Estimate before calling the health district",
                    "Ohio quote conversations get more real once you know which local health department holds the permit file and whether the property already has an operation-permit or inspection history."
            );
            case "MI" -> new StateActionCopy(
                    "Estimate before the local file pull",
                    "Michigan questions get more real once you know which local health department holds the file and whether failure evidence or system-location uncertainty is already on record."
            );
            case "GA" -> new StateActionCopy(
                    "Estimate after the county file pull",
                    "Georgia quotes get real after you confirm the county office, the permit file, the soil analysis, and the garbage-disposal sizing rule."
            );
            case "PA" -> new StateActionCopy(
                    "Estimate before calling the SEO",
                    "Pennsylvania often turns into a records and local SEO workflow fast, so it helps to walk in with a realistic planning range first."
            );
            case "CT" -> new StateActionCopy(
                    "Estimate with design flow context",
                    "Connecticut questions often turn on bedroom count and potential-bedroom logic, not just what fixtures you see today."
            );
            case "OR" -> new StateActionCopy(
                    "Estimate before site evaluation",
                    "Oregon homeowners usually need a planning range before the site evaluation and permit path narrow the real system options."
            );
            case "MA" -> new StateActionCopy(
                    "Estimate with Title 5 timing in mind",
                    "Massachusetts buyers and sellers usually need to line up the estimate with Title 5 timing, records, and inspection results."
            );
            case "FL" -> new StateActionCopy(
                    "Estimate after the county path check",
                    "Florida homeowners should confirm whether the local path runs through a county health department or a DEP-managed county before comparing quotes."
            );
            case "WA" -> new StateActionCopy(
                    "Estimate before calling the LHJ",
                    "Washington workflows usually move faster when you know whether the local health jurisdiction will ask for records, O&M history, or advanced-system context."
            );
            case "NJ" -> new StateActionCopy(
                    "Estimate with management rules in mind",
                    "New Jersey costs can shift once management-area rules, local health review, or Pinelands context enter the conversation."
            );
            case "NC" -> new StateActionCopy(
                    "Estimate before the permit ladder",
                    "North Carolina homeowners usually get better quote conversations when they understand the improvement-permit sequence before pricing systems."
            );
            case "MO" -> new StateActionCopy(
                    "Estimate after confirming local authority",
                    "Missouri can route homeowners through different county or local authorities, so the planning estimate is strongest after that first local check."
            );
            default -> new StateActionCopy(
                    "Open the main calculator",
                    "Use your state and project assumptions first, then verify the actual permit path locally."
            );
        };
    }

    private StatePlanningSnapshot statePlanningSnapshot(String stateCode) {
        StateCostProfile costProfile = researchDataService.findStateCostProfile(stateCode).orElse(null);
        if (costProfile == null) {
            return null;
        }

        ProjectCostAnchor nationalReplacement = researchDataService.findNationalAnchor("replacement").orElse(null);
        int nationalReplacementMid = nationalReplacement != null ? nationalReplacement.mid() : 0;
        String comparisonNote = nationalReplacementMid > 0
                ? replacementComparison(costProfile.replacementMid(), nationalReplacementMid)
                : "Planning-only snapshot built from public cost anchors and broad state price-level adjustments.";

        return new StatePlanningSnapshot(
                money(costProfile.installMid()),
                money(costProfile.replacementMid()),
                range(costProfile.percLow(), costProfile.percHigh()),
                range(costProfile.pumpingLow(), costProfile.pumpingHigh()),
                comparisonNote
        );
    }

    private String replacementComparison(Integer replacementMid, int nationalReplacementMid) {
        if (replacementMid == null) {
            return "Replacement planning midpoint is still under review for this state.";
        }

        int delta = replacementMid - nationalReplacementMid;
        int percent = (int) Math.round(Math.abs(delta) * 100.0 / nationalReplacementMid);
        if (Math.abs(delta) < 250) {
            return "Replacement planning midpoint runs close to the current national planning midpoint.";
        }
        if (delta > 0) {
            return "Replacement planning midpoint runs about " + percent + "% above the current national planning midpoint.";
        }
        return "Replacement planning midpoint runs about " + percent + "% below the current national planning midpoint.";
    }

    private String money(Integer amount) {
        if (amount == null) {
            return "Under review";
        }
        return String.format(Locale.US, "$%,d", amount);
    }

    private String range(Integer low, Integer high) {
        if (low == null || high == null) {
            return "Under review";
        }
        return money(low) + " to " + money(high);
    }

    private List<CoreStateComparisonRow> coreStateComparisonRows(StateProfile currentState) {
        if (!CORE_STATE_CODES.contains(currentState.stateCode())) {
            return List.of();
        }

        return CORE_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .map(state -> new CoreStateComparisonRow(
                        state.stateName(),
                        state.slug(),
                        state.whoToCallFirst(),
                        firstListItem(state.recordsToRequest(), "Local septic permit and inspection records."),
                        firstListItem(state.lowEndRiskChecks(), "Local review and site constraints can erase the low end quickly."),
                        nextBestIntentTitle(state),
                        nextBestIntentPath(state),
                        state.stateCode().equals(currentState.stateCode())
                ))
                .toList();
    }

    private StateQueuePlanView stateQueuePlanView(StateQueuePlan plan) {
        UsStateDirectoryService.UsStateReference stateReference = usStateDirectoryService.findByCode(plan.stateCode())
                .orElseThrow(() -> new StateNotFoundException(plan.stateCode()));
        return stateQueuePlanView(plan, stateReference);
    }

    private StateQueuePlanView stateQueuePlanView(StateQueuePlan plan, UsStateDirectoryService.UsStateReference stateReference) {
        return new StateQueuePlanView(
                stateReference.stateCode(),
                stateReference.stateName(),
                plan.priorityRank(),
                plan.rolloutWave(),
                plan.whyNow(),
                plan.launchAngle(),
                "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                pageLink(plan.recommendedPath(), "septic-system-cost-calculator", null),
                plan.researchTasks()
        );
    }

    private String nextBestIntentTitle(StateProfile state) {
        return findPriorityStateMoneyPage(state)
                .map(StateMoneyPage::title)
                .orElse("Open the main cost calculator");
    }

    private String nextBestIntentPath(StateProfile state) {
        return findPriorityStateMoneyPage(state)
                .map(page -> page.path(state.slug()))
                .orElse("/septic-system-cost-calculator/?state=" + state.stateCode());
    }

    private Optional<StateMoneyPage> findPriorityStateMoneyPage(StateProfile state) {
        return researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                .max(Comparator
                        .comparingInt((StateMoneyPage page) -> stateMoneyPagePriorityScore(state, page))
                        .thenComparing(StateMoneyPage::title));
    }

    private ContentEvidenceLaneView contentEvidenceLane(StateMoneyPage page, StateProfile state) {
        List<SourceRecord> sources = researchDataService.getSources(page.officialSourceIds()).stream()
                .limit(3)
                .toList();
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());
        String reviewedAgainst = "Cites " + sources.size() + " official source" + (sources.size() == 1 ? "" : "s")
                + " tied to the " + state.stateName() + " workflow; the date is the latest review activity.";
        return new ContentEvidenceLaneView(
                page.title(),
                state.stateName(),
                page.path(state.slug()),
                page.uniqueAngle(),
                reviewedAgainst,
                lastReviewedAt,
                sources
        );
    }

    private ContentWorkflowCoverageView contentWorkflowCoverage(
            ContentPage contentPage,
            List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries
    ) {
        List<Map.Entry<StateMoneyPage, StateProfile>> featuredEntries = rankedStateEntries.stream()
                .limit(6)
                .toList();
        if (featuredEntries.isEmpty()) {
            return null;
        }

        List<String> stateCodes = featuredEntries.stream()
                .map(entry -> entry.getValue().stateCode())
                .distinct()
                .toList();
        long stateCount = stateCodes.size();
        long statePagesCount = featuredEntries.size();
        long countyCount = stateCodes.stream()
                .mapToLong(code -> researchDataService.listPublicCountyRecordsPages(code).size())
                .sum();
        long countyBackedStates = stateCodes.stream()
                .filter(code -> !researchDataService.listPublicCountyRecordsPages(code).isEmpty())
                .count();

        String heading = "What the live state pages already resolve";
        String summary = "This national page is backed by " + statePagesCount + " source-backed state workflow pages across "
                + stateCount + " states, with " + countyCount + " live county workflow pages already underneath those states.";
        List<String> bullets = List.of(
                countyBackedStates + " of those states already route users into county-first follow-up before pricing.",
                "The linked state pages are where file owner, permit closeout, transfer artifact, and quote-gate differences stop being generic.",
                "Use this national page to frame the problem, then move into the state page once you need a real office, file, or county branch."
        );
        return new ContentWorkflowCoverageView(heading, summary, bullets);
    }

    private record StateCountyBackbone(
            StateProfile state,
            int countyCount,
            int workflowPageCount,
            PageLink link
    ) {
    }

    @SafeVarargs
    private final List<SourceRecord> workflowPacketSources(List<String>... sourceIdGroups) {
        return Stream.of(sourceIdGroups)
                .filter(java.util.Objects::nonNull)
                .flatMap(List::stream)
                .filter(sourceId -> sourceId != null && !sourceId.isBlank())
                .distinct()
                .map(researchDataService::findSource)
                .flatMap(Optional::stream)
                .limit(5)
                .toList();
    }

    private List<PageLink> countyRecordPageLinks(String stateCode) {
        return researchDataService.listPublicCountyRecordsPages(stateCode).stream()
                .sorted(Comparator
                        .comparingInt(this::countyRecordPriorityScore)
                        .reversed()
                        .thenComparing(CountyRecordsPage::countyName))
                .map(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> new PageLink(
                                page.title(),
                                page.path(state.slug()),
                                page.uniqueAngle()
                        )))
                .flatMap(Optional::stream)
                .toList();
    }

    private String nationalPlanningRange(String projectType) {
        return researchDataService.findNationalAnchor(projectType)
                .map(anchor -> range(anchor.low(), anchor.high()))
                .orElse("Under review");
    }

    private String statePlanningRange(String stateCode, String projectType) {
        return researchDataService.findStateCostProfile(stateCode)
                .map(profile -> profile.anchorForProjectType(projectType))
                .map(anchor -> range(anchor.low(), anchor.high()))
                .orElseGet(() -> nationalPlanningRange(projectType));
    }

    private List<RecordsAccessIndexStateView> recordsAccessIndexStates() {
        return RECORDS_ACCESS_INDEX_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .filter(StateProfile::isPublished)
                .map(state -> {
                    List<CountyFinderLinkView> countyLinks = researchDataService
                            .listPublicCountyRecordsPages(state.stateCode()).stream()
                            .map(page -> countyFinderLink(page, state))
                            .toList();
                    String packetPath = recordsPacketPath(state.stateCode());
                    return researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                            .map(recordsPage -> new RecordsAccessIndexStateView(
                                    state.stateCode(),
                                    state.stateName(),
                                    recordsPage.path(state.slug()),
                                    packetPath,
                                    recordsPacketLabel(state.stateCode()),
                                    recordsAccessFirstStep(state.stateCode()),
                                    countyLinks.size(),
                                    (int) countyLinks.stream().filter(this::isDirectOnlineRecordRoute).count(),
                                    (int) countyLinks.stream().filter(link -> link.confidenceScore() >= 82).count()
                            ));
                })
                .flatMap(Optional::stream)
                .toList();
    }

    private boolean isDirectOnlineRecordRoute(CountyFinderLinkView link) {
        return DIRECT_ONLINE_RECORD_SEARCH_COUNTIES.contains(
                link.stateCode() + ":" + normalizeCountyFinderText(link.countyName()).replace(" county", "")
        );
    }

    private String recordsPacketPath(String stateCode) {
        return switch (stateCode) {
            case "TN" -> TENNESSEE_INSPECTION_PACKET_PATH;
            case "IN" -> INDIANA_RECORDS_PACKET_PATH;
            case "NC" -> NORTH_CAROLINA_LISTING_PACKET_PATH;
            case "SC" -> SOUTH_CAROLINA_PERMIT_PACKET_PATH;
            case "NY" -> NEW_YORK_BUYER_PACKET_PATH;
            default -> "/septic-records-request-builder/";
        };
    }

    private String recordsPacketLabel(String stateCode) {
        return switch (stateCode) {
            case "TN" -> "Inspection-letter handoff";
            case "IN" -> "Records request packet";
            case "NC" -> "Listing permit packet";
            case "SC" -> "Permit-prep packet";
            case "NY" -> "Buyer diligence packet";
            default -> "Records request packet";
        };
    }

    private String recordsAccessFirstStep(String stateCode) {
        return switch (stateCode) {
            case "TN" -> "Resolve the parcel and owner, then use the TDEC SSDS route or the county file owner.";
            case "IN" -> "Open the local health or county records route before treating a statewide answer as the file.";
            case "NC" -> "Start with county Environmental Health and carry the address or parcel into its permit-file path.";
            case "SC" -> "Use the SCDES permit path, then request the D-1740 or county-held file when the search is incomplete.";
            case "NY" -> "Identify the county or district file owner, then keep the permit, inspection, and transfer artifacts together for buyer review.";
            default -> "Open the state records path, then use the county route that owns the parcel file.";
        };
    }

    private List<CountyFinderLinkView> countyFinderLinks() {
        return countyFinderLinks(totalCountyRouteCount());
    }

    private List<StateProfile> countyRouteStates() {
        Set<String> stateCodes = researchDataService.getPublicCountyRecordsPages().stream()
                .map(CountyRecordsPage::stateCode)
                .collect(Collectors.toSet());
        return researchDataService.getPublicStateProfiles().stream()
                .filter(state -> stateCodes.contains(state.stateCode()))
                .sorted(Comparator.comparing(StateProfile::stateName))
                .toList();
    }

    private String recordsAccessIndexDataLastUpdated(List<CountyFinderLinkView> countyLinks) {
        Stream<String> reviewedDates = countyLinks == null
                ? Stream.empty()
                : countyLinks.stream().map(CountyFinderLinkView::lastReviewedAt);
        return Stream.concat(
                        Stream.of(researchDataService.countyRecordsPagesGeneratedAt()),
                        reviewedDates
                )
                .filter(this::isIsoDate)
                .max(String::compareTo)
                .orElseThrow(() -> new IllegalStateException("Records access index requires a valid review date"));
    }

    private List<StateProfile> offerPrepStates() {
        return RECORDS_ACCESS_INDEX_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .filter(StateProfile::isPublished)
                .toList();
    }

    private List<CountyFinderLinkView> directOnlineCountyFinderLinks() {
        return countyFinderLinks(totalCountyRouteCount()).stream()
                .filter(this::isDirectOnlineRecordRoute)
                .toList();
    }

    private List<CountyFinderLinkView> countyFinderLinksForContentPage(ContentPage contentPage, int limit) {
        Optional<String> authorityStateCode = recordsAuthorityStateCode(contentPage);
        if (authorityStateCode.isPresent()) {
            return countyFinderLinks(authorityStateCode.orElseThrow(), limit);
        }
        return countyFinderLinks(limit);
    }

    private List<CountyFinderLinkView> countyFinderLinks(int limit) {
        return countyFinderLinks(null, limit);
    }

    private List<CountyFinderLinkView> countyFinderLinks(String stateCode, int limit) {
        List<CountyRecordsPage> countyPages = researchDataService.getPublicCountyRecordsPages().stream()
                .filter(page -> stateCode == null || stateCode.equals(page.stateCode()))
                .sorted(Comparator
                        .comparingInt(this::countyRecordPriorityScore)
                        .reversed()
                        .thenComparing(CountyRecordsPage::stateCode)
                        .thenComparing(CountyRecordsPage::countyName))
                .limit(limit)
                .toList();
        List<CountyFinderLinkView> links = new ArrayList<>();
        for (CountyRecordsPage page : countyPages) {
            researchDataService.findStateByCode(page.stateCode())
                    .map(state -> countyFinderLink(page, state))
                    .ifPresent(links::add);
        }
        return links;
    }

    @GetMapping({"/api/county-finder", "/api/county-finder/"})
    @ResponseBody
    public ResponseEntity<List<CountyFinderLinkView>> countyFinderSearch(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "method", defaultValue = "") String method,
            @RequestParam(name = "artifact", defaultValue = "") String artifact,
            @RequestParam(name = "confidence", defaultValue = "") String confidence,
            @RequestParam(name = "state", defaultValue = "") String stateCode,
            @RequestParam(name = "parcelOnly", defaultValue = "false") boolean parcelOnly
    ) {
        List<CountyFinderLinkView> matches = countyFinderLinks(totalCountyRouteCount()).stream()
                .filter(link -> countyFinderMatches(link, query, method, artifact, confidence, stateCode, parcelOnly))
                .toList();
        return ResponseEntity.ok()
                .header("X-County-Finder-Match-Count", Integer.toString(matches.size()))
                .body(matches.stream().limit(18).toList());
    }

    private AddressRecordFinderResult addressRecordFinderRoute(
            CensusAddressLookupService.CensusAddressLookupResult lookup
    ) {
        Optional<StateProfile> state = researchDataService.findStateByCode(lookup.stateCode())
                .filter(StateProfile::isPublished);
        if (state.isEmpty()) {
            return new AddressRecordFinderResult(
                    "unsupported",
                    "We found " + lookup.countyName() + " County, but its records route is not published yet",
                    "Open the national county finder and carry the same address into the local health or permitting office.",
                    lookup.stateCode(), "", lookup.countyName(), lookup.matchedAddress(),
                    "Search county records", "/septic-records-by-county/", "", List.of(), List.of()
            );
        }

        Optional<CountyRecordsPage> countyPage = researchDataService.listPublicCountyRecordsPages(lookup.stateCode()).stream()
                .filter(page -> countyNamesMatch(page.countyName(), lookup.countyName()))
                .findFirst();
        if (countyPage.isPresent()) {
            CountyRecordsPage page = countyPage.get();
            if ("TN".equals(state.get().stateCode())) {
                return tennesseeRecordRelay(lookup, state.get(), page);
            }
            CountyAccessProfileView accessProfile = CountyAccessProfileCatalog.findOrBaseline(page);
            String routeMessage = accessProfile.countySpecific()
                    ? "This county-specific route has a reviewed official handoff. The government site or office still controls the final search, signature, payment, and submission."
                    : "This is an official starting point, not a verified county-specific intake. Use the property clues here, then follow only the fields and steps the official source currently publishes.";
            return new AddressRecordFinderResult(
                    "county_route",
                    page.countyName() + ", " + state.get().stateName() + " records route",
                    routeMessage,
                    state.get().stateCode(), state.get().stateName(), page.countyName(), lookup.matchedAddress(),
                    "Open " + page.countyName() + " records", page.path(state.get().slug()), page.recordsUrl(),
                    page.officeLabel(), page.contactLine(), latestVerifiedAt(
                            researchDataService.getSources(page.officialSourceIds()),
                            page.reviewedAt(),
                            page.updatedAt(),
                            researchDataService.countyRecordsPagesGeneratedAt()
                    ),
                    List.of(), List.of()
            );
        }

        Optional<StateMoneyPage> stateRecordsPage = researchDataService
                .findPublicStateMoneyPage("septic-records-checklist", state.get().slug());
        if (stateRecordsPage.isPresent()) {
            StateMoneyPage page = stateRecordsPage.get();
            return new AddressRecordFinderResult(
                    "state_route",
                    lookup.countyName() + " County, " + state.get().stateName() + " records route",
                    "A verified county page is not live yet, so start with the state records workflow and use the resolved county plus your address or parcel clue.",
                    state.get().stateCode(), state.get().stateName(), lookup.countyName(), lookup.matchedAddress(),
                    "Open " + state.get().stateName() + " records", page.path(state.get().slug()), "", List.of(), List.of()
            );
        }

        return new AddressRecordFinderResult(
                "unsupported",
                "We found " + lookup.countyName() + " County, " + state.get().stateName(),
                "Use the county finder to choose the best available public records path for this address.",
                state.get().stateCode(), state.get().stateName(), lookup.countyName(), lookup.matchedAddress(),
                "Search county records", "/septic-records-by-county/", "", List.of(), List.of()
        );
    }

    private AddressRecordFinderResult tennesseeRecordRelay(
            CensusAddressLookupService.CensusAddressLookupResult lookup,
            StateProfile state,
            CountyRecordsPage countyPage
    ) {
        String countyKey = normalizeCountyFinderText(countyPage.countyName()).replace(" county", "");
        boolean contractCounty = TENNESSEE_CONTRACT_COUNTIES.contains(countyKey);
        List<AddressRecordFinderAction> actions = new ArrayList<>();
        actions.add(new AddressRecordFinderAction(
                "Open " + countyPage.countyName() + " records", countyPage.path(state.slug()), "county_records_page", false, true
        ));
        actions.add(new AddressRecordFinderAction(
                "Find parcel or prior owner", TENNESSEE_PROPERTY_ASSESSMENT_URL, "official_property_lookup", true, false
        ));
        actions.add(new AddressRecordFinderAction(
                "Find your TDEC field office", TENNESSEE_FIELD_OFFICES_URL, "official_contact", true, false
        ));
        actions.add(new AddressRecordFinderAction(
                "Open TDEC public records request", TENNESSEE_PUBLIC_RECORDS_URL, "official_request", true, false
        ));
        actions.add(new AddressRecordFinderAction(
                "Open Tennessee SSDS program", TENNESSEE_SSDS_PROGRAM_URL, "official_source", true, false
        ));
        actions.add(new AddressRecordFinderAction(
                "Check listing bedrooms", "/septic-bedroom-permit-checker/", "internal_tool", false, false
        ));
        if (!contractCounty) {
            actions.add(new AddressRecordFinderAction(
                    "Request inspection letter", TENNESSEE_ONLINE_SERVICE_URL, "official_request", true, false
            ));
        }

        String serviceNote = contractCounty
                ? "This is one of Tennessee's contract or metropolitan counties, so open the county route first before relying on the state online-service lane."
                : "For a sale, mortgage, or buyer diligence file, the Tennessee online service includes the inspection-letter route."
                ;
        return new AddressRecordFinderResult(
                "county_route",
                countyPage.countyName() + ", Tennessee public records relay",
                "Start with the local route, then carry the parcel ID and owner clues into the field-office or public-records request. " + serviceNote,
                state.stateCode(), state.stateName(), countyPage.countyName(), lookup.matchedAddress(),
                "Open " + countyPage.countyName() + " records", countyPage.path(state.slug()), countyPage.recordsUrl(),
                countyPage.officeLabel(), countyPage.contactLine(), latestVerifiedAt(
                        researchDataService.getSources(countyPage.officialSourceIds()),
                        countyPage.reviewedAt(),
                        countyPage.updatedAt(),
                        researchDataService.countyRecordsPagesGeneratedAt()
                ),
                actions,
                List.of(
                        "Use Tennessee Property Assessment Data to collect the parcel ID and current or prior owner when available.",
                        "Use the working Tennessee SSDS page to confirm the program, then send the parcel clues to the field office or public-records route for the county.",
                        "Pull the permit, layout, final approval, repair history, or written no-record response before pricing or closing."
                )
        );
    }

    private boolean countyNamesMatch(String left, String right) {
        return normalizeCountyFinderText(left).replace(" county", "")
                .equals(normalizeCountyFinderText(right).replace(" county", ""));
    }

    private boolean countyFinderMatches(
            CountyFinderLinkView link,
            String query,
            String method,
            String artifact,
            String confidence,
            String stateCode,
            boolean parcelOnly
    ) {
        String normalizedQuery = normalizeCountyFinderText(query);
        String normalizedMethod = normalizeCountyFinderText(method);
        String normalizedArtifact = normalizeCountyFinderText(artifact);
        String normalizedRequestMethod = normalizeCountyFinderText(link.requestMethodLabel());
        String normalizedFirstArtifact = normalizeCountyFinderText(link.firstArtifactLabel());

        boolean matchesQuery = normalizedQuery.isBlank() || normalizeCountyFinderText(link.searchText()).contains(normalizedQuery);
        boolean matchesMethod = switch (normalizedMethod) {
            case "online" -> normalizedRequestMethod.matches(".*\\b(online|portal|search|lookup|gis)\\b.*");
            case "request" -> normalizedRequestMethod.matches(".*\\b(request|form|email|written|copy)\\b.*");
            case "office" -> normalizedRequestMethod.matches(".*\\b(office|phone|contact|department|county)\\b.*");
            default -> true;
        };
        boolean matchesArtifact = switch (normalizedArtifact) {
            case "as built" -> normalizedFirstArtifact.contains("as built")
                    || normalizedFirstArtifact.contains("layout")
                    || normalizedFirstArtifact.contains("site sketch");
            case "no record" -> normalizedFirstArtifact.contains("no record")
                    || normalizedFirstArtifact.contains("missing")
                    || normalizedFirstArtifact.contains("written response");
            case "" -> true;
            default -> normalizedFirstArtifact.contains(normalizedArtifact);
        };
        boolean matchesConfidence = switch (confidence) {
            case "high" -> link.confidenceScore() >= 82;
            case "solid" -> link.confidenceScore() >= 70;
            default -> true;
        };
        boolean matchesState = stateCode == null
                || stateCode.isBlank()
                || stateCode.equalsIgnoreCase(link.stateCode());
        return matchesQuery && matchesMethod && matchesArtifact && matchesConfidence && matchesState
                && (!parcelOnly || link.parcelAnchorAvailable());
    }

    private String csvRow(String... values) {
        return Arrays.stream(values)
                .map(this::csvEscape)
                .collect(Collectors.joining(","));
    }

    private String csvEscape(String value) {
        String safe = value == null ? "" : value;
        if (!safe.isEmpty() && "=+@-\t\r".indexOf(safe.charAt(0)) >= 0) {
            safe = "'" + safe;
        }
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private String normalizeCountyFinderText(String value) {
        return value == null ? "" : value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", " ").trim();
    }

    private int totalCountyRouteCount() {
        return researchDataService.getPublicCountyRecordsPages().size();
    }

    private CountyFinderLinkView countyFinderLink(CountyRecordsPage page, StateProfile state) {
        String combinedText = countyCombinedText(page);
        int confidenceScore = countyAvailabilityConfidenceScore(page, combinedText);
        String confidenceLabel = countyConfidenceLabel(confidenceScore);
        String requestMethod = countyRequestMethodLabel(page, combinedText);
        String firstArtifact = countyFirstArtifact(page);
        int officialSourceCount = size(page.officialSourceIds());
        String sourceDepth = officialSourceCount + " official source" + (officialSourceCount == 1 ? "" : "s");
        String lastReviewedAt = latestVerifiedAt(
                researchDataService.getSources(page.officialSourceIds()),
                firstNonBlank(page.updatedAt(), researchDataService.countyRecordsPagesGeneratedAt())
        );
        String title = page.countyName() + ", " + state.stateCode() + " records";
        String note = firstNonBlank(
                page.recordsLabel(),
                page.officeLabel(),
                "Open the county septic records and permit file path."
        );
        String searchText = String.join(" ",
                page.countyName(),
                state.stateName(),
                state.stateCode(),
                page.title(),
                page.recordsLabel(),
                page.officeLabel(),
                page.contactLine(),
                confidenceLabel,
                requestMethod,
                firstArtifact,
                "septic permit lookup records request as built inspection letter parcel tms"
        ).toLowerCase(Locale.US);
        return new CountyFinderLinkView(
                title,
                page.path(state.slug()),
                note,
                state.stateCode(),
                state.stateName(),
                page.countyName(),
                confidenceLabel,
                confidenceScore,
                requestMethod,
                firstArtifact,
                sourceDepth,
                officialSourceCount,
                page.hasParcelAnchor(),
                lastReviewedAt,
                searchText,
                page.recordsUrl(),
                seoService.absoluteUrl(page.path(state.slug()))
        );
    }

    private List<CountyFinderLinkView> siblingCountyRoutes(CountyRecordsPage currentPage, StateProfile state, int limit) {
        return researchDataService.listPublicCountyRecordsPages(state.stateCode()).stream()
                .filter(page -> !page.countySlug().equalsIgnoreCase(currentPage.countySlug()))
                .sorted(Comparator
                        .comparingInt(this::countyRecordPriorityScore)
                        .reversed()
                        .thenComparing(CountyRecordsPage::countyName))
                .limit(limit)
                .map(page -> countyFinderLink(page, state))
                .toList();
    }

    private List<CountyIntentRouteView> countyIntentRoutes(CountyRecordsPage countyPage, StateProfile state) {
        String countyState = countyPage.countyName() + " " + state.stateName();
        String recordsPath = countyPage.recordsUrl();
        String parcelPath = countyPage.hasParcelAnchor() ? countyPage.parcelAnchorUrl() : recordsPath;
        String stateRecordsPath = "/septic-records-checklist/" + state.slug() + "/";
        String statePermitPath = researchDataService.findPublicStateMoneyPage("septic-permit-process", state.slug())
                .map(page -> page.path(state.slug()))
                .orElse("/septic-system-cost-calculator/" + state.slug() + "/");
        String buyerPath = researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", state.slug())
                .map(page -> page.path(state.slug()))
                .orElse(stateRecordsPath);

        return List.of(
                new CountyIntentRouteView(
                        "county-septic-permit-lookup",
                        "Permit lookup",
                        countyState + " septic permit lookup",
                        "Use this path when the search is really about finding the permit file, final approval, repair note, or county office that can verify the parcel story. Start with " + countyPage.recordsLabel() + ", then verify the owning office before pricing.",
                        "Open county permit record path",
                        recordsPath,
                        "official_source",
                        "Open " + state.stateName() + " permit process",
                        statePermitPath,
                        "state_money_page"
                ),
                new CountyIntentRouteView(
                        "county-septic-records-request",
                        "Records request",
                        countyState + " septic records request",
                        "Ask " + countyPage.officeLabel() + " for the " + countyPage.countyName()
                                + " septic permit copy, approval for use, repair file, inspection note, and any system diagram tied to the parcel. If the office cannot connect the request to a parcel identifier, the file story is still too weak.",
                        "Open records request path",
                        recordsPath,
                        "official_source",
                        "Open state records checklist",
                        stateRecordsPath,
                        "state_money_page"
                ),
                new CountyIntentRouteView(
                        "county-septic-permit-search-by-address",
                        "Address search",
                        countyState + " septic permit search by address",
                        countyPage.hasParcelAnchor()
                                ? "Use the parcel, TMS, owner, or property search first, then carry that identifier into the county septic records path. Address-only searches fail when the parcel anchor is missing or the county uses a different property identifier."
                                : "Start with " + countyPage.recordsLabel() + " and ask which parcel, owner, address, or legal-description field "
                                        + countyPage.officeLabel() + " needs before treating the record as missing.",
                        countyPage.hasParcelAnchor() ? "Open parcel or TMS search" : "Open county record path",
                        parcelPath,
                        "official_source",
                        "Open address-search guide",
                        "/septic-permit-search-by-address/",
                        "internal_page"
                ),
                new CountyIntentRouteView(
                        "county-septic-as-built-records",
                        "As-built",
                        countyState + " septic as-built records",
                        "For " + countyPage.countyName() + ", the as-built or system diagram can change where the tank, drain field, reserve area, or repair scope actually sits. Ask whether the county file includes a site sketch, installed layout, or approval package before trusting a field location.",
                        "Open county file path",
                        recordsPath,
                        "official_source",
                        "Open as-built records guide",
                        "/septic-as-built-records/",
                        "internal_page"
                ),
                new CountyIntentRouteView(
                        "county-septic-inspection-letter",
                        "Inspection letter",
                        countyState + " septic inspection letter",
                        "For a " + countyPage.countyName() + " sale, lender question, repair story, or occupancy file, ask whether "
                                + countyPage.officeLabel() + " can provide an inspection letter, final approval, approval for use, or written file note tied to the parcel.",
                        "Open county record path",
                        recordsPath,
                        "official_source",
                        "Open inspection letter guide",
                        "/septic-inspection-letter/",
                        "internal_page"
                ),
                new CountyIntentRouteView(
                        "county-buying-house-septic",
                        "Buyer file",
                        "Buying a house with a septic system in " + countyState,
                        "Before negotiation, inspection credits, or seller assurances in " + countyPage.countyName()
                                + ", pull the county septic file and compare it with the buyer workflow. Missing permit history, unclear location, or no inspection artifact can change the risk story fast.",
                        "Open buyer septic workflow",
                        buyerPath,
                        "state_money_page",
                        "Open county record path",
                        recordsPath,
                        "official_source"
                )
        );
    }

    private CountyAvailabilitySummaryView countyAvailabilitySummary(
            CountyRecordsPage countyPage,
            StateProfile state,
            List<SourceRecord> sources,
            String lastReviewedAt
    ) {
        String combinedText = countyCombinedText(countyPage);
        int score = countyAvailabilityConfidenceScore(countyPage, combinedText);
        String confidenceLabel = countyConfidenceLabel(score);
        String confidenceNote = countyConfidenceNote(countyPage, score);
        String requestMethod = countyRequestMethodLabel(countyPage, combinedText);
        String sourceDepth = sources.size() + " official source" + (sources.size() == 1 ? "" : "s");
        return new CountyAvailabilitySummaryView(
                confidenceLabel,
                score,
                confidenceNote,
                countyPage.recordsLabel(),
                requestMethod,
                countyFirstArtifact(countyPage),
                sourceDepth,
                lastReviewedAt
        );
    }

    private int countyAvailabilityConfidenceScore(CountyRecordsPage countyPage, String combinedText) {
        int score = 46;
        if (countyPage.hasParcelAnchor()) {
            score += 12;
        }
        if (containsAny(combinedText, "search", "lookup", "portal", "online", "gis", "parcel")) {
            score += 10;
        }
        if (containsAny(combinedText, "as-built", "site plan", "schematic", "layout", "system diagram")) {
            score += 8;
        }
        if (containsAny(combinedText, "inspection", "approval for use", "license to operate", "final approval", "certificate")) {
            score += 8;
        }
        if (size(countyPage.officialSourceIds()) >= 2) {
            score += 8;
        }
        if (size(countyPage.recordsToRequest()) >= 3) {
            score += 5;
        }
        if (countyPage.requestScriptBody() != null && !countyPage.requestScriptBody().isBlank()) {
            score += 3;
        }
        return Math.min(score, 96);
    }

    private String countyConfidenceLabel(int score) {
        if (score >= 82) {
            return "High-confidence county route";
        }
        if (score >= 68) {
            return "Usable county route";
        }
        return "County route needs follow-up";
    }

    private String countyConfidenceNote(CountyRecordsPage countyPage, int score) {
        if (score >= 82) {
            return countyPage.countyName() + " has enough official-source depth, county-specific workflow detail, and request artifacts to start with the local file before pricing.";
        }
        if (score >= 68) {
            return countyPage.countyName() + " has a usable county records path, but the user should still verify the exact office and artifact before relying on the file.";
        }
        return countyPage.countyName() + " has a starting route, but the county may require a phone, email, or state-level fallback before the record story is reliable.";
    }

    private List<CountyAvailabilityRowView> countyAvailabilityRows(CountyRecordsPage countyPage, StateProfile state) {
        String combinedText = countyCombinedText(countyPage);
        String recordsUrl = countyPage.recordsUrl();
        String recordsTarget = "official_source";
        String stateRecordsPath = "/septic-records-checklist/" + state.slug() + "/";
        List<CountyAvailabilityRowView> rows = new ArrayList<>();
        rows.add(new CountyAvailabilityRowView(
                "Parcel or property anchor",
                countyPage.hasParcelAnchor() ? "Direct anchor available" : "Verify through records office",
                countyPage.hasParcelAnchor() ? "Parcel/TMS first" : "County records fallback",
                countyPage.hasParcelAnchor()
                        ? countyPage.parcelAnchorNote()
                        : "Use the " + countyPage.countyName() + " records path and ask which address, owner, APN, TMS, or legal description field " + countyPage.officeLabel() + " needs.",
                countyPage.hasParcelAnchor() ? countyPage.parcelAnchorLabel() : countyPage.recordsLabel(),
                countyPage.hasParcelAnchor() ? countyPage.parcelAnchorUrl() : recordsUrl,
                recordsTarget,
                true,
                countyPage.hasParcelAnchor() ? "strong" : "medium"
        ));
        rows.add(new CountyAvailabilityRowView(
                "Permit copy or approval file",
                containsAny(combinedText, "permit", "approval", "license to operate", "construction authorization")
                        ? "County-specific signal found"
                        : "Ask for permit history",
                countyAvailabilityMethod(combinedText),
                firstNonBlank(
                        countyWorkflowStructure(countyPage, state).fields().stream()
                                .filter(field -> "Permit closeout signal".equals(field.label()))
                                .map(CountyWorkflowFieldView::value)
                                .findFirst()
                                .orElse(null),
                        "Ask whether the file includes the septic permit copy, final approval, repair permit, or license-to-operate artifact."
                ),
                countyPage.recordsLabel(),
                recordsUrl,
                recordsTarget,
                true,
                containsAny(combinedText, "permit", "approval", "license to operate") ? "strong" : "medium"
        ));
        rows.add(new CountyAvailabilityRowView(
                "As-built, site plan, or layout",
                containsAny(combinedText, "as-built", "site plan", "schematic", "layout", "system diagram")
                        ? "Layout signal found"
                        : "Request explicitly",
                "Record request",
                "Ask whether the " + countyPage.countyName()
                        + " file includes the installed layout, site sketch, tank location, drain field location, or approval package tied to the parcel.",
                countyPage.recordsLabel(),
                recordsUrl,
                recordsTarget,
                true,
                containsAny(combinedText, "as-built", "site plan", "schematic", "layout", "system diagram") ? "strong" : "medium"
        ));
        rows.add(new CountyAvailabilityRowView(
                "Inspection letter or transfer artifact",
                containsAny(combinedText, "inspection", "transfer", "buyer", "sale", "closing", "certificate")
                        ? "Buyer artifact likely relevant"
                        : "Use buyer checklist",
                containsAny(combinedText, "transfer", "sale", "closing") ? "Transfer check" : "Inspection check",
                countyPage.countyName() + ": " + countyTransferArtifact(countyTransferCategory(countyPage, combinedText)),
                containsAny(combinedText, "buyer", "transfer", "sale")
                        ? "Open buyer workflow"
                        : countyPage.recordsLabel(),
                containsAny(combinedText, "buyer", "transfer", "sale")
                        ? researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", state.slug())
                                .map(page -> page.path(state.slug()))
                                .orElse(stateRecordsPath)
                        : recordsUrl,
                containsAny(combinedText, "buyer", "transfer", "sale") ? "state_money_page" : recordsTarget,
                !containsAny(combinedText, "buyer", "transfer", "sale"),
                containsAny(combinedText, "inspection", "transfer", "buyer", "sale", "closing", "certificate") ? "strong" : "medium"
        ));
        rows.add(new CountyAvailabilityRowView(
                "Repair, malfunction, or modification trail",
                containsAny(combinedText, "repair", "malfunction", "failing", "violation", "modification", "alteration")
                        ? "Repair trail flagged"
                        : "Check before pricing",
                "Risk gate",
                countyPage.countyName() + ": " + countyMalfunctionSignal(countyMalfunctionCategory(countyPage, combinedText)),
                countyPage.recordsLabel(),
                recordsUrl,
                recordsTarget,
                true,
                containsAny(combinedText, "repair", "malfunction", "failing", "violation", "modification", "alteration") ? "strong" : "medium"
        ));
        return rows;
    }

    private String countyRequestMethodLabel(CountyRecordsPage countyPage, String combinedText) {
        if (countyPage.hasParcelAnchor() && containsAny(combinedText, "online", "portal", "search", "lookup", "gis")) {
            return "Online search plus county file request";
        }
        if (containsAny(combinedText, "email", "@", "request form", "records request", "opra", "foia")) {
            return "Records request or email";
        }
        if (containsAny(combinedText, "phone", "call")) {
            return "Office verification required";
        }
        return "County records path first";
    }

    private String countyAvailabilityMethod(String combinedText) {
        if (containsAny(combinedText, "online", "portal", "search", "lookup", "gis")) {
            return "Search or portal";
        }
        if (containsAny(combinedText, "request form", "records request", "email", "opra", "foia")) {
            return "Request form";
        }
        return "Office path";
    }

    private List<CountyRequestBuilderOptionView> countyRequestOptions(CountyRecordsPage countyPage, StateProfile state) {
        String countyState = countyPage.countyName() + ", " + state.stateCode();
        String parcelPrompt = countyPage.hasParcelAnchor()
                ? "I can provide the parcel, TMS, APN, owner, or address from " + countyPage.parcelAnchorLabel() + "."
                : "For the " + countyPage.countyName() + " request, I can provide the parcel, APN, owner, address, or legal description if your office needs a different identifier.";
        List<String> records = countyPage.recordsToRequest() == null ? List.of() : countyPage.recordsToRequest().stream().limit(4).toList();
        List<String> fallbackChecklist = records.isEmpty()
                ? List.of("Parcel identifier", "Septic permit copy", "As-built or site plan", "Final approval or inspection note")
                : records;
        String quoteGate = countyWorkflowStructure(countyPage, state).quoteGate();

        return List.of(
                new CountyRequestBuilderOptionView(
                        "buyer",
                        "Buyer file",
                        "Request a buyer-safe county file",
                        countyState + " septic records request for buyer diligence",
                        List.of(
                                "Hello, I am checking the septic file for a property in " + countyState + " before relying on a seller, inspection, or quote story.",
                                parcelPrompt,
                                "Please let me know whether " + countyPage.officeLabel() + " can provide the septic permit copy, as-built or site plan, final approval, inspection letter, repair history, and any transfer or sale-related record tied to this " + countyPage.countyName() + " parcel.",
                                "If another office owns part of the " + countyPage.countyName() + " file, please tell me which office or portal should be checked next."
                        ),
                        fallbackChecklist,
                        countyPage.recordsLabel(),
                        countyPage.recordsUrl(),
                        true
                ),
                new CountyRequestBuilderOptionView(
                        "owner",
                        "Owner repair",
                        "Request the repair and modification trail",
                        countyState + " septic repair or modification file check",
                        List.of(
                                "Hello, I am trying to verify the septic record trail for a property in " + countyState + " before discussing repair, replacement, or modification pricing.",
                                parcelPrompt,
                                "Please confirm whether the " + countyPage.countyName() + " file shows the installed system layout, permit history, final approval or license to operate, repair permits, complaint history, or any requirement to apply before work begins.",
                                quoteGate
                        ),
                        fallbackChecklist,
                        countyPage.recordsLabel(),
                        countyPage.recordsUrl(),
                        true
                ),
                new CountyRequestBuilderOptionView(
                        "contractor",
                        "Contractor scope",
                        "Request the scope-setting artifacts",
                        countyState + " septic permit and as-built scope request",
                        List.of(
                                "Hello, I am preparing a septic scope for a property in " + countyState + " and need to confirm the official file before pricing or permitting assumptions are made.",
                                parcelPrompt,
                                "Please identify the " + countyPage.countyName() + " record owner, the first artifact to pull, whether a permit closeout or final approval exists, and whether repair, alteration, bedroom-count, or site-review rules change the next step.",
                                "The most useful response from " + countyPage.officeLabel() + " is the permit or approval file plus any as-built, layout, inspection note, or written no-record response."
                        ),
                        fallbackChecklist,
                        countyPage.recordsLabel(),
                        countyPage.recordsUrl(),
                        true
                )
        );
    }

    private List<CountyRouteClusterView> countyRouteClusters(int stateLimit, int countiesPerState) {
        return countyRouteClusters(null, stateLimit, countiesPerState);
    }

    private List<CountyRouteClusterView> countyRouteClusters(ContentPage contentPage, int stateLimit, int countiesPerState) {
        Optional<String> authorityStateCode = recordsAuthorityStateCode(contentPage);
        return researchDataService.getPublicStateProfiles().stream()
                .filter(state -> !researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty())
                .filter(state -> authorityStateCode
                        .map(stateCode -> stateCode.equals(state.stateCode()))
                        .orElse(true))
                .sorted(Comparator
                        .comparingInt(this::countyRouteStateScore)
                        .reversed()
                        .thenComparing(StateProfile::stateName))
                .limit(stateLimit)
                .map(state -> countyRouteCluster(state, countiesPerState))
                .toList();
    }

    private CountyRouteClusterView countyRouteCluster(StateProfile state, int countiesPerState) {
        List<PageLink> countyLinks = countyRecordPageLinks(state.stateCode()).stream()
                .limit(countiesPerState)
                .toList();
        int liveCountyCount = researchDataService.listPublicCountyRecordsPages(state.stateCode()).size();
        PageLink stateRecordsLink = stateRecordsLink(state);
        PageLink permitProcessLink = statePermitProcessLink(state);
        String heading = state.stateName() + " county routes";
        String summary = liveCountyCount + " live " + state.stateName()
                + " county workflow page" + (liveCountyCount == 1 ? "" : "s")
                + " already route users toward the local record owner, permit file, or buyer-risk check.";
        String metricLabel = liveCountyCount + " county file page" + (liveCountyCount == 1 ? "" : "s");
        return new CountyRouteClusterView(
                state.stateCode(),
                state.stateName(),
                stateRecordsLink.path(),
                heading,
                summary,
                metricLabel,
                liveCountyCount,
                stateRecordsLink,
                permitProcessLink,
                countyLinks
        );
    }

    private int countyRouteStateScore(StateProfile state) {
        int countyCount = researchDataService.listPublicCountyRecordsPages(state.stateCode()).size();
        int score = countyCount * 4;
        score += researchDataService.listPublicCountyRecordsPages(state.stateCode()).stream()
                .mapToInt(this::countySearchResponseBoost)
                .sum() / 5;
        if (ORGANIC_SPRINT_STATE_CODES.contains(state.stateCode())) {
            score += 90;
        }
        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 22;
        }
        score += researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
        return score;
    }

    private PageLink stateRecordsLink(StateProfile state) {
        return researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                .map(page -> new PageLink(
                        state.stateName() + " records lookup",
                        page.path(state.slug()),
                        "Use the state records page when the county list is still too narrow or the file owner is not obvious."
                ))
                .orElseGet(() -> new PageLink(
                        state.stateName() + " septic guide",
                        "/septic-system-cost-calculator/" + state.slug() + "/",
                        "Use the state guide when a records-specific page is not live yet."
                ));
    }

    private PageLink statePermitProcessLink(StateProfile state) {
        return researchDataService.findPublicStateMoneyPage("septic-permit-process", state.slug())
                .map(page -> new PageLink(
                        state.stateName() + " permit process",
                        page.path(state.slug()),
                        "Use the permit process page when the search needs office routing, site review, or closeout context."
                ))
                .orElseGet(() -> new PageLink(
                        state.stateName() + " septic guide",
                        "/septic-system-cost-calculator/" + state.slug() + "/",
                        "Use the state guide when the permit process page is not live yet."
                ));
    }

    private List<PageLink> permitLookupCountyLaunchpadLinks(ContentPage contentPage) {
        if (!isPermitLookupHub(contentPage)) {
            return List.of();
        }
        List<String> stateCodes = recordsAuthorityStateCode(contentPage)
                .map(List::of)
                .orElseGet(() -> List.of("TN", "NC", "TX", "SC", "AL", "IN"));
        return stateCodes.stream()
                .flatMap(stateCode -> researchDataService.listPublicCountyRecordsPages(stateCode).stream())
                .sorted(Comparator
                        .comparingInt(this::countyRecordPriorityScore)
                        .reversed()
                        .thenComparing(CountyRecordsPage::countyName))
                .limit(120)
                .map(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> new PageLink(
                                page.countyName() + ", " + state.stateCode() + " permit lookup",
                                page.path(state.slug()),
                                countyLaunchpadNote(page, state)
                        )))
                .flatMap(Optional::stream)
                .toList();
    }

    private String countyLaunchpadNote(CountyRecordsPage page, StateProfile state) {
        return "Use this " + state.stateName() + " example when the user already has a county name and needs "
                + page.recordsLabel()
                + " before trusting a quote, sale file, repair story, or new permit path.";
    }

    private int countyRecordPriorityScore(CountyRecordsPage page) {
        int score = switch (page.stateCode()) {
            case "TN" -> 70;
            case "NC" -> 64;
            case "TX" -> 62;
            case "SC" -> 60;
            case "AL" -> 58;
            case "IN" -> 54;
            default -> 20;
        };
        score += switch (page.countySlug()) {
            case "davidson-county", "wake-county", "travis-county", "madison-county", "elkhart-county",
                    "howard-county", "guilford-county", "durham-county", "comal-county", "montgomery-county",
                    "greenville-county", "richland-county", "charleston-county", "horry-county" -> 34;
            case "knox-county", "alamance-county", "hays-county", "baldwin-county", "st-joseph-county",
                    "iredell-county", "grant-county", "tippecanoe-county", "parker-county", "guadalupe-county",
                    "st-clair-county", "spartanburg-county", "lexington-county", "york-county" -> 28;
            case "shelby-county", "union-county", "porter-county", "ellis-county", "bastrop-county",
                    "autauga-county", "cullman-county", "miami-county", "bartholomew-county", "monroe-county",
                    "berkeley-county", "beaufort-county", "dorchester-county" -> 24;
            case "johnston-county", "fort-bend-county", "tuscaloosa-county", "marshall-county",
                    "randolph-county", "lincoln-county", "loudon-county", "anderson-county", "aiken-county",
                    "pickens-county" -> 20;
            case "chatham-county", "orange-county", "brazoria-county", "lee-county", "la-porte-county",
                    "etowah-county", "elmore-county", "cumberland-county", "florence-county", "sumter-county" -> 16;
            case "mecklenburg-county", "williamson-county", "limestone-county", "floyd-county",
                    "shelby-county-indiana" -> 12;
            default -> 0;
        };
        score += Math.min(size(page.officialSourceIds()), 4) * 2;
        if (page.recordsLabel() != null && page.recordsLabel().toLowerCase(Locale.US).contains("search")) {
            score += 5;
        }
        if (page.recordsLabel() != null && page.recordsLabel().toLowerCase(Locale.US).contains("lookup")) {
            score += 5;
        }
        CountyAccessProfileView accessProfile = CountyAccessProfileCatalog.find(page.key());
        CountyAcquisitionProfileView acquisitionProfile = CountyAcquisitionProfileCatalog.find(page.key());
        if (accessProfile != null) {
            score += 50;
        }
        if (acquisitionProfile != null) {
            score += acquisitionProfile.hasPreparedFieldPack() ? 35 : 15;
        }
        score += countySearchDemandBoost(page.key());
        score += countySearchResponseBoost(page);
        return score;
    }

    private int countySearchDemandBoost(String countyKey) {
        return switch (countyKey) {
            case "VA::prince-william-county" -> 70;
            case "TX::tarrant-county" -> 45;
            case "TN::hamilton-county" -> 40;
            case "NC::alamance-county", "NC::lincoln-county", "TN::knox-county" -> 35;
            case "GA::dekalb-county" -> 30;
            case "AZ::maricopa-county", "MD::st-marys-county", "NY::suffolk-county",
                    "NC::brunswick-county", "NC::forsyth-county", "TN::blount-county",
                    "TX::brazoria-county", "TX::denton-county" -> 24;
            case "CA::san-bernardino-county", "MD::prince-georges-county",
                    "NJ::gloucester-county", "NC::guilford-county", "OH::mahoning-county",
                    "TN::montgomery-county", "TN::sevier-county", "TN::wilson-county" -> 18;
            default -> 0;
        };
    }

    private CountyWorkflowStructureView countyWorkflowStructure(CountyRecordsPage countyPage, StateProfile state) {
        String combinedText = countyCombinedText(countyPage);
        CountyWorkflowStructureData structure = countyPage.workflowStructure();
        List<CountyWorkflowFieldView> fields = List.of(
                new CountyWorkflowFieldView("File owner model", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.fileOwnerModel(),
                        countyFileOwnerModel(countyFileOwnerCategory(countyPage, combinedText), state.stateName())
                ))),
                new CountyWorkflowFieldView("First artifact to pull", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.firstArtifactToPull(),
                        countyFirstArtifact(countyPage)
                ))),
                new CountyWorkflowFieldView("Permit closeout signal", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.permitCloseoutSignal(),
                        countyPage.countyName() + ": " + countyPermitCloseoutSignal(countyPermitCloseoutCategory(countyPage, combinedText))
                ))),
                new CountyWorkflowFieldView("Transfer or buyer artifact", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.transferArtifact(),
                        countyPage.countyName() + ": " + countyTransferArtifact(countyTransferCategory(countyPage, combinedText))
                ))),
                new CountyWorkflowFieldView("Special program or local exception", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.specialProgramSignal(),
                        countyPage.countyName() + ": " + countySpecialProgramSignal(countySpecialProgramCategory(countyPage, combinedText))
                ))),
                new CountyWorkflowFieldView("Malfunction or repair trail", countyEvidenceText(countyPage, firstNonBlank(
                        structure == null ? null : structure.malfunctionSignal(),
                        countyPage.countyName() + ": " + countyMalfunctionSignal(countyMalfunctionCategory(countyPage, combinedText))
                )))
        );
        return new CountyWorkflowStructureView(fields, countyEvidenceText(countyPage, firstNonBlank(
                structure == null ? null : structure.quoteGate(),
                countyQuoteGate(countyPage, combinedText)
        )));
    }

    private String countyEvidenceText(CountyRecordsPage countyPage, String value) {
        if (!hasText(value)) {
            return countyPage.countyName() + " evidence is still under review.";
        }
        if (value.toLowerCase(Locale.US).contains(countyPage.countyName().toLowerCase(Locale.US))) {
            return value;
        }
        return countyPage.countyName() + " evidence: " + value;
    }

    private String countyCombinedText(CountyRecordsPage page) {
        return String.join(" ",
                nullSafe(page.introCopy()),
                nullSafe(page.uniqueAngle()),
                nullSafe(page.targetReader()),
                nullSafe(page.officeLabel()),
                nullSafe(page.recordsLabel()),
                String.join(" ", page.decisionSteps() == null ? List.of() : page.decisionSteps()),
                String.join(" ", page.recordsToRequest() == null ? List.of() : page.recordsToRequest()),
                String.join(" ", page.lowEndBreakers() == null ? List.of() : page.lowEndBreakers())
        ).toLowerCase(Locale.US);
    }

    private String countyFileOwnerModel(String combinedText, StateProfile state) {
        return countyFileOwnerModel(countyFileOwnerCategoryText(combinedText), state.stateName());
    }

    private String countyFileOwnerCategoryText(String combinedText) {
        if (containsAny(combinedText, "board of health", "ceha", "municipal", "municipality", "incorporated town")) {
            return "split_local";
        }
        if (containsAny(combinedText, "local health department", "health district", "regional office", "contract county")) {
            return "district_health";
        }
        return "county_first";
    }

    private String countyFirstArtifact(CountyRecordsPage countyPage) {
        if (countyPage.recordsToRequest() != null && !countyPage.recordsToRequest().isEmpty()) {
            return countyPage.recordsToRequest().get(0);
        }
        return "The first county file or permit artifact tied to the parcel.";
    }

    private String countyPermitCloseoutSignal(String category) {
        return countyPermitCloseoutSignalFromCategory(category);
    }

    private String countyTransferArtifact(String category) {
        return countyTransferArtifactFromCategory(category);
    }

    private String countySpecialProgramSignal(String category) {
        return countySpecialProgramSignalFromCategory(category);
    }

    private String countyMalfunctionSignal(String category) {
        return countyMalfunctionSignalFromCategory(category);
    }

    private String countyQuoteGate(CountyRecordsPage countyPage, String combinedText) {
        if (!"generic".equals(countyMalfunctionCategory(countyPage, combinedText))) {
            return "Do not move into pricing until the county repair or malfunction trail is clear, because the job may already be wider than the visible system story.";
        }
        if (!"county_first".equals(countyFileOwnerCategory(countyPage, combinedText))) {
            return "Do not move into pricing until you know which office actually owns the file, because a split authority path can make the first answer misleading.";
        }
        if (!"generic".equals(countyPermitCloseoutCategory(countyPage, combinedText))) {
            return "Do not move into pricing until the county closeout artifact is visible, because the permit mention alone may not prove the system is actually cleared for use.";
        }
        return "Do not move into pricing until the county file is strong enough to show the right parcel, the right office, and the last real approval or inspection signal.";
    }

    private boolean supportsCountyWorkflowSummary(String contentSlug) {
        return "septic-records-checklist".equals(contentSlug)
                || "septic-permit-process".equals(contentSlug)
                || "buying-a-house-with-a-septic-system".equals(contentSlug)
                || isWorkflowCostSlug(contentSlug);
    }

    private boolean isWorkflowCostSlug(String contentSlug) {
        return isReplacementWorkflowCostSlug(contentSlug)
                || isInspectionWorkflowCostSlug(contentSlug)
                || isPercWorkflowCostSlug(contentSlug)
                || isPumpingWorkflowCostSlug(contentSlug);
    }

    private boolean isReplacementWorkflowCostSlug(String contentSlug) {
        return "septic-replacement-cost".equals(contentSlug)
                || "drain-field-replacement-cost".equals(contentSlug)
                || "failed-perc-test-septic".equals(contentSlug)
                || "septic-replacement-area".equals(contentSlug)
                || "wet-yard-over-septic-drain-field".equals(contentSlug);
    }

    private boolean isInspectionWorkflowCostSlug(String contentSlug) {
        return "septic-inspection-cost".equals(contentSlug);
    }

    private boolean isPercWorkflowCostSlug(String contentSlug) {
        return "perc-test-cost".equals(contentSlug);
    }

    private boolean isPumpingWorkflowCostSlug(String contentSlug) {
        return "septic-pumping-cost".equals(contentSlug);
    }

    private StateCountyWorkflowSynthesisView stateCountyWorkflowSynthesis(StateMoneyPage stateMoneyPage, StateProfile state) {
        if (!supportsCountyWorkflowSummary(stateMoneyPage.contentSlug())) {
            return null;
        }
        List<CountyRecordsPage> countyPages = researchDataService.listPublicCountyRecordsPages(state.stateCode());
        if (countyPages.size() < 2) {
            return null;
        }

        Comparator<CountyPatternType> patternComparator = "septic-records-checklist".equals(stateMoneyPage.contentSlug())
                ? Comparator
                        .comparingInt((CountyPatternType pattern) -> countyPatternPriority(pattern, stateMoneyPage.contentSlug()))
                        .reversed()
                        .thenComparing(Comparator.comparingInt(
                                (CountyPatternType pattern) -> countyPagesMatchingPattern(countyPages, pattern).size()
                        ).reversed())
                        .thenComparingInt(CountyPatternType::displayOrder)
                : Comparator
                        .comparingInt((CountyPatternType pattern) -> countyPagesMatchingPattern(countyPages, pattern).size())
                        .reversed()
                        .thenComparing(Comparator.comparingInt(
                                (CountyPatternType pattern) -> countyPatternPriority(pattern, stateMoneyPage.contentSlug())
                        ).reversed())
                        .thenComparingInt(CountyPatternType::displayOrder);

        List<CountyPatternType> topPatterns = Stream.of(CountyPatternType.values())
                .filter(pattern -> countyPages.stream().anyMatch(page -> countyMatchesPattern(page, pattern)))
                .sorted(patternComparator)
                .limit(3)
                .toList();

        if (topPatterns.isEmpty()) {
            return null;
        }

        List<StateCountyWorkflowSignalView> signals = topPatterns.stream()
                .map(pattern -> {
                    List<CountyRecordsPage> matches = countyPagesMatchingPattern(countyPages, pattern);
                    String exampleCounties = matches.stream()
                            .map(CountyRecordsPage::countyName)
                            .distinct()
                            .limit(3)
                            .reduce((left, right) -> left + ", " + right)
                            .orElse(state.stateName() + " county pages");
                    String coverageNote = "Seen across " + matches.stream()
                            .map(CountyRecordsPage::countySlug)
                            .distinct()
                            .count() + " live county pages.";
                    return new StateCountyWorkflowSignalView(
                            pattern.label(),
                            pattern.summary(state.stateName()),
                            exampleCounties,
                            coverageNote,
                            "For " + stateMoneyPage.title() + ", first ask for " + lowerCaseFirst(pattern.firstArtifact())
                    );
                })
                .toList();

        List<CountyWorkflowFieldView> structureHighlights = List.of(
                stateStructureHighlight(
                        "Most common file owner pattern",
                        countyPages,
                        this::countyFileOwnerCategory,
                        category -> countyFileOwnerAggregateText(category, state.stateName())
                ),
                stateStructureHighlight(
                        "Most common permit closeout signal",
                        countyPages,
                        this::countyPermitCloseoutCategory,
                        category -> state.stateName() + " county evidence: " + countyPermitCloseoutAggregateText(category)
                ),
                stateStructureHighlight(
                        "Most common buyer or transfer artifact",
                        countyPages,
                        this::countyTransferCategory,
                        category -> state.stateName() + " county evidence: " + countyTransferAggregateText(category)
                ),
                stateStructureHighlight(
                        "Most common special program or exception",
                        countyPages,
                        this::countySpecialProgramCategory,
                        category -> state.stateName() + " county evidence: " + countySpecialProgramAggregateText(category)
                ),
                stateStructureHighlight(
                        "Most common malfunction or repair trail",
                        countyPages,
                        this::countyMalfunctionCategory,
                        category -> state.stateName() + " county evidence: " + countyMalfunctionAggregateText(category)
                ),
                stateStructureHighlight(
                        "Most common quote gate",
                        countyPages,
                        this::countyQuoteGateCategory,
                        category -> countyQuoteGateAggregateText(category, state.stateName())
                )
        );

        List<String> firstArtifacts = topPatterns.stream()
                .map(pattern -> stateMoneyPage.title() + " file check: " + pattern.firstArtifact())
                .distinct()
                .limit(4)
                .toList();

        List<String> countyDropTriggers = topPatterns.stream()
                .map(pattern -> stateMoneyPage.title() + " county trigger: " + pattern.countyDropTrigger())
                .distinct()
                .limit(4)
                .toList();

        List<String> holdQuoteChecks = topPatterns.stream()
                .map(pattern -> stateMoneyPage.title() + " quote hold: " + pattern.holdQuoteCheck())
                .distinct()
                .limit(4)
                .toList();

        return new StateCountyWorkflowSynthesisView(
                countyWorkflowEyebrow(stateMoneyPage.contentSlug()),
                countyWorkflowHeading(stateMoneyPage.contentSlug(), state.stateName()),
                countyWorkflowIntro(stateMoneyPage.contentSlug(), state.stateName(), countyPages.stream()
                        .map(CountyRecordsPage::countySlug)
                        .distinct()
                        .count()),
                signals,
                structureHighlights,
                firstArtifacts,
                countyDropTriggers,
                holdQuoteChecks,
                countyWorkflowFirstArtifactsHeading(stateMoneyPage.contentSlug()),
                countyWorkflowDropHeading(stateMoneyPage.contentSlug()),
                countyWorkflowHoldQuoteHeading(stateMoneyPage.contentSlug())
        );
    }

    private String lowerCaseFirst(String value) {
        if (value == null || value.isBlank()) {
            return "the first official county artifact";
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private CountyWorkflowFieldView stateStructureHighlight(
            String label,
            List<CountyRecordsPage> countyPages,
            java.util.function.Function<CountyRecordsPage, String> categoryResolver,
            java.util.function.Function<String, String> textResolver
    ) {
        java.util.Map<String, Long> counts = countyPages.stream()
                .map(categoryResolver)
                .collect(Collectors.groupingBy(category -> category, LinkedHashMap::new, Collectors.counting()));

        String topCategory = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("generic");

        long topCount = counts.getOrDefault(topCategory, 0L);
        return new CountyWorkflowFieldView(
                label,
                textResolver.apply(topCategory) + " Seen in " + topCount + " county pages."
        );
    }

    private String countyFileOwnerCategory(CountyRecordsPage countyPage) {
        return countyFileOwnerCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyFileOwnerCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().fileOwnerCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        return countyFileOwnerCategoryText(combinedText);
    }

    private String countyFileOwnerModel(String category, String stateName) {
        return switch (category) {
            case "split_local" -> "The county path is split. Confirm whether the real septic file sits with the county, a municipality, or a local board before treating one office as the full answer.";
            case "state_or_regional" -> "The practical file owner starts with the statewide septic program, then resolves through the county or regional contact path that can confirm the parcel file.";
            case "district_health" -> "The real file likely lives with a county or district health office rather than a generic statewide desk. Confirm the exact local office before moving into pricing.";
            case "county_engineer" -> "The real file is county-first here and usually runs through a named engineering or development-services office rather than a generic statewide desk.";
            case "county_public_health", "county_environmental_health", "county_first" -> "The real file is county-first here once you reach the named local health or environmental office.";
            default -> stateName + " looks county-first here. Start with the named county office and only widen out if the file points to another local authority.";
        };
    }

    private String countyFileOwnerAggregateText(String category, String stateName) {
        return switch (category) {
            case "split_local" -> "Many county workflows in " + stateName + " split the real file between county health, a municipality, or a local board.";
            case "state_or_regional" -> "Many county workflows in " + stateName + " start with statewide septic routing, then resolve through a county or regional contact path.";
            case "district_health" -> "Many county workflows in " + stateName + " still turn on identifying the correct district or local health office first.";
            case "county_engineer" -> "Many county workflows in " + stateName + " are county-first once you reach the named engineering or development-services office.";
            case "county_public_health", "county_environmental_health", "county_first" -> "Many county workflows in " + stateName + " are county-first once you reach the named local health or environmental office.";
            default -> "Many county workflows in " + stateName + " are county-first once you reach the named local office.";
        };
    }

    private String countyPermitCloseoutCategory(CountyRecordsPage countyPage) {
        return countyPermitCloseoutCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyPermitCloseoutCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().permitCloseoutCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "operation permit", "license-to-operate")) {
            return "use_approval";
        }
        if (containsAny(combinedText, "completion certificate", "certificate of completion", "certificate of occupancy", "final inspection")) {
            return "completion_artifact";
        }
        if (containsAny(combinedText, "construction authorization", "improvement permit", "sanitary construction permit")) {
            return "permit_ladder";
        }
        return "generic";
    }

    private String countyPermitCloseoutSignalFromCategory(String category) {
        return switch (category) {
            case "use_approval" -> "Look for the operating or use approval, because the county treats that as the proof that the system can still be used as described.";
            case "completion_artifact" -> "Look for the closeout or completion artifact, not just the application. The county workflow suggests the job is not really closed without that final signal.";
            case "permit_ladder" -> "The permit ladder matters here. Pull the approval step that shows the county moved beyond preliminary review into an actual buildable path.";
            default -> "Do not stop at the first permit mention. Look for the county artifact that proves the job actually cleared the last approval step.";
        };
    }

    private String countyPermitCloseoutAggregateText(String category) {
        return switch (category) {
            case "use_approval" -> "The most common county closeout signal is an operating or use approval rather than a bare permit application.";
            case "completion_artifact" -> "The most common county closeout signal is a completion or final inspection artifact.";
            case "final_approval_signal" -> "The most common county closeout signal is the final inspection, Permit to Construct, or similar closeout note rather than the first application.";
            case "permit_ladder" -> "The most common county closeout signal is a permit ladder step that proves the parcel moved beyond preliminary review.";
            default -> "County files often need a stronger closeout artifact than the first permit mention.";
        };
    }

    private String countyTransferCategory(CountyRecordsPage countyPage) {
        return countyTransferCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyTransferCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().transferCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "pti", "property status report", "real-estate evaluation", "transfer inspection", "inspection letter")) {
            return "formal_transfer_artifact";
        }
        if (containsAny(combinedText, "closing", "buyer", "seller", "refinance", "loan")) {
            return "deal_side_risk";
        }
        return "generic";
    }

    private String countyTransferArtifactFromCategory(String category) {
        return switch (category) {
            case "formal_transfer_artifact" -> "This county has a buyer-side artifact that matters more than a generic permit copy. Pull the transfer or status document before you treat the sale as routine.";
            case "deal_side_risk" -> "The county file already hints at deal-side risk. Pull the inspection or transaction-facing document before you negotiate credits or timing.";
            default -> "If the property is being sold, ask whether the county keeps a buyer, transfer, or status artifact that changes closing risk.";
        };
    }

    private String countyTransferAggregateText(String category) {
        return switch (category) {
            case "formal_transfer_artifact" -> "The most common buyer-side county artifact is a formal transfer, status, or real-estate evaluation record.";
            case "deal_side_risk" -> "County pages in this state often surface buyer, seller, or lender risk before the deal reaches pricing.";
            default -> "County pages still matter for buyer diligence even when the transfer artifact is less standardized.";
        };
    }

    private String countySpecialProgramCategory(CountyRecordsPage countyPage) {
        return countySpecialProgramCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countySpecialProgramCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().specialProgramCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "bay restoration fund", "brf", "bat", "critical area")) {
            return "grant_upgrade";
        }
        if (containsAny(combinedText, "pinelands", "service contract", "management plan", "management program", "o&m", "maintenance permit")) {
            return "managed_obligation";
        }
        if (containsAny(combinedText, "sewer", "lamp", "nitrogen", "reserve", "special-area")) {
            return "local_exception";
        }
        return "generic";
    }

    private String countySpecialProgramSignalFromCategory(String category) {
        return switch (category) {
            case "grant_upgrade" -> "A grant or upgrade program may already control the path. Treat BRF, BAT, or Critical Area paperwork as part of the real file, not as optional context.";
            case "managed_obligation" -> "There may be a long-tail management obligation on the property. Pull the service, management, or maintenance file before treating ownership costs as simple.";
            case "local_exception" -> "A local exception or area rule may already be changing the septic path. Check that program file before trusting the easiest replacement or reuse story.";
            case "statewide_with_county_routing" -> "A statewide program may still depend on county or regional routing before the parcel file is actually confirmed.";
            default -> "Ask whether this parcel sits in any local program, exception area, or managed lane that changes the normal septic workflow.";
        };
    }

    private String countySpecialProgramAggregateText(String category) {
        return switch (category) {
            case "grant_upgrade" -> "County pages in this state often route through BRF, BAT, Critical Area, or another upgrade-program file before replacement is straightforward.";
            case "managed_obligation" -> "County pages in this state often surface management plans, service contracts, or long-tail O&M obligations before the file is really clean.";
            case "local_exception" -> "County pages in this state often turn on a local exception, sewer branch, reserve-area limit, or other area rule before the normal path applies.";
            case "statewide_with_county_routing" -> "County pages in this state often start at a statewide program but still need county or regional routing before the file is actionable.";
            default -> "County pages in this state still need a special-program check even when no single program dominates the workflow.";
        };
    }

    private String countyMalfunctionCategory(CountyRecordsPage countyPage) {
        return countyMalfunctionCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyMalfunctionCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().malfunctionCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "malfunction", "complaint", "violation", "failing system")) {
            return "complaint_trail";
        }
        if (containsAny(combinedText, "repair permit", "repair questionnaire", "repair area", "off-lot discharge")) {
            return "repair_branch";
        }
        return "generic";
    }

    private String countyMalfunctionSignalFromCategory(String category) {
        return switch (category) {
            case "complaint_trail" -> "There is a live failure or complaint trail in play. That history is usually more important than the first quote or seller summary.";
            case "repair_branch" -> "The county repair branch matters here. Pull the repair or failure-side file before assuming the cheapest visible scope is still available.";
            default -> "If there are signs of failure, ask for the repair, complaint, or malfunction trail before you trust a clean-looking system story.";
        };
    }

    private String countyMalfunctionAggregateText(String category) {
        return switch (category) {
            case "complaint_trail" -> "County pages in this state often surface a complaint, violation, or failing-system trail before any clean pricing story is safe.";
            case "repair_branch" -> "County pages in this state often move into a repair, malfunction, or off-lot-discharge branch before the low-end scope is real.";
            default -> "County pages in this state still reward checking the repair or malfunction side before trusting the simplest system story.";
        };
    }

    private String countyQuoteGateCategory(CountyRecordsPage countyPage) {
        return countyQuoteGateCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyQuoteGateCategory(CountyRecordsPage countyPage, String combinedText) {
        if (!"generic".equals(countyMalfunctionCategory(countyPage, combinedText))) {
            return "repair_path";
        }
        if (countyFileOwnerNeedsOfficeResolution(countyFileOwnerCategory(countyPage, combinedText))) {
            return "office_split";
        }
        if (!"generic".equals(countyPermitCloseoutCategory(countyPage, combinedText))) {
            return "closeout_artifact";
        }
        if (!"generic".equals(countyTransferCategory(countyPage, combinedText))) {
            return "buyer_artifact";
        }
        return "county_file";
    }

    private boolean countyFileOwnerNeedsOfficeResolution(String category) {
        return "split_local".equals(category) || "office_split".equals(category) || "state_or_regional".equals(category);
    }

    private String countyQuoteGateAggregateText(String category, String stateName) {
        return stateName + " county evidence: " + switch (category) {
            case "repair_path" -> "The most common quote gate is a repair, malfunction, or failing-system branch that has to be cleared before pricing is trustworthy.";
            case "office_split" -> "The most common quote gate is figuring out which local office actually owns the file before a buyer or contractor trusts the first answer.";
            case "closeout_artifact" -> "The most common quote gate is waiting for the county closeout or use artifact instead of trusting the first permit mention.";
            case "buyer_artifact" -> "The most common quote gate is pulling the buyer-side or transfer artifact before the property story reaches pricing.";
            default -> "The most common quote gate is making sure the county file still proves the right parcel, the right office, and the last real approval signal.";
        };
    }

    private int countyPatternPriority(CountyPatternType pattern, String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> switch (pattern) {
                case PERMIT -> 6;
                case AUTHORITY -> 5;
                case LOOKUP -> 4;
                case BRF -> 3;
                case COMPLAINT -> 2;
                case TRANSFER -> 1;
            };
            case "buying-a-house-with-a-septic-system" -> switch (pattern) {
                case TRANSFER -> 6;
                case LOOKUP -> 5;
                case AUTHORITY -> 4;
                case PERMIT -> 3;
                case BRF -> 2;
                case COMPLAINT -> 1;
            };
            case "septic-inspection-cost" -> switch (pattern) {
                case LOOKUP -> 6;
                case COMPLAINT -> 5;
                case TRANSFER -> 4;
                case AUTHORITY -> 3;
                case PERMIT -> 2;
                case BRF -> 1;
            };
            case "perc-test-cost" -> switch (pattern) {
                case PERMIT -> 6;
                case LOOKUP -> 5;
                case AUTHORITY -> 4;
                case BRF -> 3;
                case COMPLAINT -> 2;
                case TRANSFER -> 1;
            };
            case "septic-pumping-cost" -> switch (pattern) {
                case LOOKUP -> 6;
                case TRANSFER -> 5;
                case AUTHORITY -> 4;
                case COMPLAINT -> 3;
                case PERMIT -> 2;
                case BRF -> 1;
            };
            default -> switch (pattern) {
                case LOOKUP -> isReplacementWorkflowCostSlug(contentSlug) ? 4 : 6;
                case AUTHORITY -> isReplacementWorkflowCostSlug(contentSlug) ? 3 : 5;
                case PERMIT -> isReplacementWorkflowCostSlug(contentSlug) ? 5 : 4;
                case TRANSFER -> isReplacementWorkflowCostSlug(contentSlug) ? 1 : 3;
                case BRF -> isReplacementWorkflowCostSlug(contentSlug) ? 4 : 2;
                case COMPLAINT -> isReplacementWorkflowCostSlug(contentSlug) ? 6 : 1;
            };
        };
    }

    private String countyWorkflowEyebrow(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "County Permit Summary";
            case "buying-a-house-with-a-septic-system" -> "County Buyer Summary";
            case "septic-inspection-cost" -> "County Inspection Summary";
            case "perc-test-cost" -> "County Site-Review Summary";
            case "septic-pumping-cost" -> "County Maintenance Summary";
            default -> isReplacementWorkflowCostSlug(contentSlug) ? "County Replacement Summary" : "State Pattern Summary";
        };
    }

    private String countyWorkflowHeading(String contentSlug, String stateName) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "How county permit paths usually break down in " + stateName;
            case "buying-a-house-with-a-septic-system" -> "How county due diligence usually breaks down in " + stateName;
            case "septic-inspection-cost" -> "How county inspection files usually break down in " + stateName;
            case "perc-test-cost" -> "How county site-review files usually break down in " + stateName;
            case "septic-pumping-cost" -> "How county maintenance files usually break down in " + stateName;
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "How county replacement files usually break down in " + stateName
                    : "How county files usually break down in " + stateName;
        };
    }

    private String countyWorkflowIntro(String contentSlug, String stateName, long countyCount) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "These county pages show the local permit branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which permit desk, closeout artifact, or local file matters before you treat the permit path like routine paperwork.";
            case "buying-a-house-with-a-septic-system" -> "These county pages show the due-diligence branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which local file, transfer artifact, or management trail matters before you treat the deal like a generic inspection question.";
            case "septic-inspection-cost" -> "These county pages show the inspection-file branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which pumping log, transfer artifact, or failing-system trail matters before you price the inspection scope like routine fieldwork.";
            case "perc-test-cost" -> "These county pages show the site-review branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which parcel file, permit lane, or redesign trigger matters before you price soils, perc, or site-evaluation work like a generic first step.";
            case "septic-pumping-cost" -> "These county pages show the maintenance branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which operating history, pumping log, or maintenance obligation matters before you price this like a simple tank visit.";
            default -> "These county pages show the local branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which county file, replacement branch, or failure-side trigger matters before you treat the first cost number like the final answer.";
        };
    }

    private String countyWorkflowFirstArtifactsHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "First county permit artifacts to pull";
            case "buying-a-house-with-a-septic-system" -> "First county buyer artifacts to pull";
            case "septic-inspection-cost" -> "First county inspection artifacts to pull";
            case "perc-test-cost" -> "First county site-review artifacts to pull";
            case "septic-pumping-cost" -> "First county maintenance artifacts to pull";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "First county replacement artifacts to pull"
                    : "First county artifacts to pull";
        };
    }

    private String countyWorkflowDropHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "Drop to a county permit page when";
            case "buying-a-house-with-a-septic-system" -> "Drop to a county page when the deal risk turns local";
            case "septic-inspection-cost" -> "Drop to a county inspection page when";
            case "perc-test-cost" -> "Drop to a county site-review page when";
            case "septic-pumping-cost" -> "Drop to a county maintenance page when";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "Drop to a county replacement page when"
                    : "Drop to a county page when";
        };
    }

    private String countyWorkflowHoldQuoteHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "Do not schedule permit pricing yet when";
            case "buying-a-house-with-a-septic-system" -> "Do not treat this as a routine deal yet when";
            case "septic-inspection-cost" -> "Do not price inspection scope yet when";
            case "perc-test-cost" -> "Do not price site-review scope yet when";
            case "septic-pumping-cost" -> "Do not price maintenance scope yet when";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "Do not price replacement scope yet when"
                    : "Do not quote yet when";
        };
    }

    private List<CountyRecordsPage> countyPagesMatchingPattern(List<CountyRecordsPage> countyPages, CountyPatternType pattern) {
        return countyPages.stream()
                .filter(page -> countyMatchesPattern(page, pattern))
                .toList();
    }

    private boolean countyMatchesPattern(CountyRecordsPage page, CountyPatternType pattern) {
        String combinedText = String.join(" ",
                nullSafe(page.introCopy()),
                nullSafe(page.uniqueAngle()),
                nullSafe(page.recordsLabel()),
                nullSafe(page.officeLabel()),
                String.join(" ", page.decisionSteps() == null ? List.of() : page.decisionSteps()),
                String.join(" ", page.recordsToRequest() == null ? List.of() : page.recordsToRequest()),
                String.join(" ", page.lowEndBreakers() == null ? List.of() : page.lowEndBreakers())
        ).toLowerCase(Locale.US);
        return pattern.matches(combinedText);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private enum CountyPatternType {
        LOOKUP(1, "Parcel and records lookup",
                "County files often start with parcel, GIS, permit-search, or formal document-request lookup before anyone trusts the seller summary.",
                "Parcel identifier, address, owner name, or permit number needed to pull the county file.",
                "You already have the parcel, address, or owner in hand and the next real move is pulling the county file.",
                "Do not move into quote mode while the parcel, GIS, or records-request trail is still missing."),
        AUTHORITY(2, "File owner and local office split",
                "%s counties often split the real file owner between county health, a municipality, a board of health, or another local office. The first win is identifying the right desk.",
                "The exact county, municipal, board-of-health, or CEHA office that actually owns the septic file.",
                "The story mentions a town, local board, or other office that does not sound like the main county file owner.",
                "Hold off on pricing if the caller still does not know which office actually owns the septic file."),
        PERMIT(3, "Permit ladder and closeout file",
                "Many county files are not one permit receipt. They usually widen into permit ladders, operation approvals, completion certificates, or reuse and addition branches.",
                "Improvement permit, construction authorization, operation permit, sanitary construction permit, or completion certificate.",
                "The project involves an addition, reuse, repair, or change-of-use instead of a simple existing-system lookup.",
                "Do not trust a clean reuse story until the permit ladder and closeout artifact are both visible."),
        TRANSFER(4, "Transfer and buyer diligence",
                "Buyer and transfer risk often lives in inspection, property-status, PTI, or completion artifacts rather than a generic permit copy.",
                "Transfer inspection, property status report, PTI-backed record, or buyer-side completion proof.",
                "The real question is closing risk, lender diligence, or inspection leverage rather than basic permit history.",
                "Do not jump to quote mode while the buyer or lender still lacks the transfer-side inspection or status artifact."),
        BRF(5, "Grant and special-program file",
                "Some counties add a separate BRF, BAT, Critical Area, or sewer-connection lane that can change both timing and ownership cost.",
                "BRF or BAT application, Critical Area note, sewer-connection alternative, or upgrade-program file.",
                "The parcel may be in a Critical Area, failing-system, or upgrade-program lane where grant and replacement rules change the next step.",
                "Do not frame the job as a simple replacement if grant, BAT, Critical Area, or sewer-connection rules might still control the path."),
        COMPLAINT(6, "Repair and malfunction trail",
                "Repair questionnaires, malfunction complaints, or violation files often tell you more than a clean-looking estimate or seller note.",
                "Repair questionnaire, malfunction complaint, violation notice, or repair-permit history.",
                "There are failure symptoms, complaint history, or repair questions already in play and the state page is still too abstract.",
                "Stop before quoting if there are failure symptoms, complaint history, or an unresolved repair trail in the county file.");

        private final int displayOrder;
        private final String label;
        private final String summary;
        private final String firstArtifact;
        private final String countyDropTrigger;
        private final String holdQuoteCheck;

        CountyPatternType(
                int displayOrder,
                String label,
                String summary,
                String firstArtifact,
                String countyDropTrigger,
                String holdQuoteCheck
        ) {
            this.displayOrder = displayOrder;
            this.label = label;
            this.summary = summary;
            this.firstArtifact = firstArtifact;
            this.countyDropTrigger = countyDropTrigger;
            this.holdQuoteCheck = holdQuoteCheck;
        }

        int displayOrder() {
            return displayOrder;
        }

        String label() {
            return label;
        }

        String summary(String stateName) {
            return summary.formatted(stateName);
        }

        String firstArtifact() {
            return firstArtifact;
        }

        String countyDropTrigger() {
            return countyDropTrigger;
        }

        String holdQuoteCheck() {
            return holdQuoteCheck;
        }

        boolean matches(String text) {
            return switch (this) {
                case LOOKUP -> containsAny(text,
                        "parcel", "gis", "request for document", "records search", "file search",
                        "property status", "tax map", "apn", "permit search", "opra");
                case AUTHORITY -> containsAny(text,
                        "local health department", "municipal", "board of health", "ceha",
                        "local approving authority", "incorporated town", "municipality",
                        "regional contact", "county or regional", "statewide septic routing");
                case PERMIT -> containsAny(text,
                        "operation permit", "construction authorization", "completion certificate",
                        "certificate of occupancy", "improvement permit", "existing system approval",
                        "sanitary construction permit", "certificate of completion", "interim permit",
                        "d-1740", "permit to construct", "final inspection", "site-review");
                case TRANSFER -> containsAny(text,
                        "transfer", "buyer", "property status report", "pti", "real estate", "closing");
                case BRF -> containsAny(text,
                        "bay restoration fund", "brf", "critical area", "bat", "nitrogen");
                case COMPLAINT -> containsAny(text,
                        "malfunction", "repair permit", "repair questionnaire", "complaint",
                        "violation", "failing system");
            };
        }
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private List<PageLink> pageLinks(List<String> paths, String sourceSlug, String sourceStateCode) {
        if (paths == null || paths.isEmpty()) {
            return List.of();
        }
        return paths.stream()
                .map(this::canonicalEditorialPath)
                .filter(path -> path != null && !path.isBlank())
                .filter(this::isIndexableEditorialPath)
                .distinct()
                .map(path -> pageLink(path, sourceSlug, sourceStateCode))
                .sorted(Comparator
                        .comparingInt((PageLink link) -> relatedLinkScore(sourceSlug, sourceStateCode, link.path()))
                        .reversed()
                .thenComparing(PageLink::title))
                .toList();
    }

    private boolean isIndexableEditorialPath(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return true;
        }
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length != 2) {
            return true;
        }
        Optional<StateMoneyPage> candidate = researchDataService.findStateMoneyPage(parts[0], parts[1]);
        if (candidate.isEmpty()) {
            return true;
        }
        return researchDataService.findPublicStateBySlug(parts[1])
                .map(state -> publishingPolicyService.isIndexableStateMoneyPage(candidate.get(), state))
                .orElse(false);
    }

    private List<PageLink> renderedInternalLinks(ContentPage contentPage, List<PageLink> internalLinks, boolean fanoutRestrictedSurface) {
        if (!fanoutRestrictedSurface || internalLinks.isEmpty()) {
            return internalLinks;
        }
        LinkedHashMap<String, PageLink> selected = new LinkedHashMap<>();
        internalLinks.stream()
                .limit(10)
                .forEach(link -> selected.putIfAbsent(link.path(), link));
        List<String> mustKeepPaths = switch (contentPage.slug()) {
            case PERMIT_LOOKUP_SLUG -> List.of(
                    "/" + RECORDS_ONLINE_SLUG + "/",
                    "/" + RECORDS_BY_COUNTY_SLUG + "/",
                    "/" + PERMIT_SEARCH_BY_ADDRESS_SLUG + "/",
                    "/" + PERMIT_RECORDS_REQUEST_SLUG + "/"
            );
            default -> List.of();
        };
        for (String path : mustKeepPaths) {
            internalLinks.stream()
                    .filter(link -> path.equals(link.path()))
                    .findFirst()
                    .ifPresent(link -> selected.putIfAbsent(link.path(), link));
        }
        return selected.values().stream().toList();
    }

    private List<PageLink> renderedPermitLookupCountyLinks(ContentPage contentPage, List<PageLink> countyLinks, boolean fanoutRestrictedSurface) {
        if (countyLinks.isEmpty()) {
            return List.of();
        }
        LinkedHashMap<String, PageLink> selected = new LinkedHashMap<>();
        countyLinks.stream()
                .limit(fanoutRestrictedSurface ? 12 : 8)
                .forEach(link -> selected.putIfAbsent(link.path(), link));
        List<String> mustKeepPaths = switch (contentPage.slug()) {
            case PERMIT_LOOKUP_SLUG -> List.of(
                    "/septic-records-checklist/tennessee/davidson-county/",
                    "/septic-records-checklist/south-carolina/greenville-county/",
                    "/septic-records-checklist/north-carolina/durham-county/",
                    "/septic-records-checklist/north-carolina/iredell-county/",
                    "/septic-records-checklist/texas/comal-county/",
                    "/septic-records-checklist/indiana/grant-county/",
                    "/septic-records-checklist/texas/montgomery-county/",
                    "/septic-records-checklist/texas/fort-bend-county/",
                    "/septic-records-checklist/alabama/shelby-county/",
                    "/septic-records-checklist/indiana/st-joseph-county/"
            );
            default -> List.of();
        };
        for (String path : mustKeepPaths) {
            countyLinks.stream()
                    .filter(link -> path.equals(link.path()))
                    .findFirst()
                    .ifPresent(link -> selected.putIfAbsent(link.path(), link));
        }
        return selected.values().stream().toList();
    }

    private PageLink pageLink(String path, String sourceSlug, String sourceStateCode) {
        String title = calculatorLinkTitle(path)
                .or(() -> stateGuideLinkTitle(path))
                .or(() -> countyRecordsPageLinkTitle(path))
                .or(() -> stateMoneyPageLinkTitle(path))
                .or(() -> contentPageLinkTitle(path))
                .orElseGet(() -> prettifyPath(path));
        return new PageLink(title, path, relatedLinkNote(sourceSlug, sourceStateCode, path));
    }

    private List<String> mergedPaths(List<String> primaryPaths, List<String> supplementalPaths) {
        return Stream.concat(
                        primaryPaths == null ? Stream.<String>empty() : primaryPaths.stream(),
                        supplementalPaths == null ? Stream.<String>empty() : supplementalPaths.stream()
                )
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .toList();
    }

    private List<String> defaultStateCrossLinks(StateMoneyPage stateMoneyPage, StateProfile state) {
        List<String> targetSlugs = switch (stateMoneyPage.contentSlug()) {
            case "septic-inspection-cost" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "wet-yard-over-septic-drain-field",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic"
            );
            case "septic-records-checklist" -> List.of(
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-replacement-area"
            );
            case "buying-a-house-with-a-septic-system" -> List.of(
                    "septic-records-checklist",
                    "septic-inspection-cost",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "wet-yard-over-septic-drain-field",
                    "septic-replacement-area"
            );
            case "drain-field-replacement-cost" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "septic-replacement-area",
                    "wet-yard-over-septic-drain-field",
                    "failed-perc-test-septic"
            );
            case "failed-perc-test-septic" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "perc-test-cost"
            );
            case "septic-replacement-area" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic"
            );
            case "wet-yard-over-septic-drain-field" -> List.of(
                    "septic-inspection-cost",
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area"
            );
            default -> List.of();
        };

        return targetSlugs.stream()
                .map(targetSlug -> researchDataService.findPublicStateMoneyPage(targetSlug, state.slug()))
                .flatMap(Optional::stream)
                .filter(page -> publishingPolicyService.isIndexableStateMoneyPage(page, state))
                .map(page -> page.path(state.slug()))
                .toList();
    }

    private String canonicalEditorialPath(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if (!"/septic-system-cost-calculator/".equals(normalizedPath) && !"/septic-system-cost-calculator".equals(normalizedPath)) {
            return path;
        }

        Map<String, List<String>> queryParams = uri.getQueryParams();
        String stateCode = queryParams.getOrDefault("state", List.of()).stream().findFirst().orElse(null);
        String projectType = queryParams.getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
        Optional<StateProfile> state = researchDataService.findStateByCode(stateCode);

        if (state.isPresent() && projectType != null) {
            Optional<String> contentSlug = contentSlugForProjectType(projectType);
            if (contentSlug.isPresent()) {
                Optional<StateMoneyPage> stateMoneyPage = researchDataService.findPublicStateMoneyPage(contentSlug.get(), state.get().slug());
                if (stateMoneyPage.isPresent()) {
                    return stateMoneyPage.get().path(state.get().slug());
                }
            }
        }

        if (state.isPresent()) {
            return "/septic-system-cost-calculator/" + state.get().slug() + "/";
        }

        return contentSlugForProjectType(projectType)
                .map(contentSlug -> "/" + contentSlug + "/")
                .orElse(path);
    }

    private Optional<String> contentSlugForProjectType(String projectType) {
        if (projectType == null || projectType.isBlank()) {
            return Optional.empty();
        }
        return switch (projectType) {
            case "replacement" -> Optional.of("septic-replacement-cost");
            case "perc_test" -> Optional.of("perc-test-cost");
            case "drainfield_replacement" -> Optional.of("drain-field-replacement-cost");
            case "inspection" -> Optional.of("septic-inspection-cost");
            default -> Optional.empty();
        };
    }

    private Optional<String> calculatorLinkTitle(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if ("/septic-system-cost-calculator/".equals(normalizedPath) || "/septic-system-cost-calculator".equals(normalizedPath)) {
            Map<String, List<String>> queryParams = uri.getQueryParams();
            String stateCode = queryParams.getOrDefault("state", List.of()).stream().findFirst().orElse(null);
            String projectType = queryParams.getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
            Optional<StateProfile> state = researchDataService.findStateByCode(stateCode);
            if (state.isPresent() && projectType != null) {
                return Optional.of(state.get().stateName() + " " + projectTypeLabel(projectType) + " estimate");
            }
            if (state.isPresent()) {
                return Optional.of(state.get().stateName() + " septic cost estimate");
            }
            return Optional.of("Main septic cost calculator");
        }
        if ("/septic-tank-size-estimator/".equals(normalizedPath) || "/septic-tank-size-estimator".equals(normalizedPath)) {
            return Optional.of("Septic tank size estimator");
        }
        if ("/septic-pump-schedule-estimator/".equals(normalizedPath) || "/septic-pump-schedule-estimator".equals(normalizedPath)) {
            return Optional.of("Septic pump schedule estimator");
        }
        if ("/drain-field-estimator/".equals(normalizedPath) || "/drain-field-estimator".equals(normalizedPath)) {
            return Optional.of("Drain field replacement estimator");
        }
        return Optional.empty();
    }

    private List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntriesForContentPage(ContentPage contentPage) {
        Stream<StateMoneyPage> stateMoneyPages = stateMoneyPageSlugsForContentPage(contentPage)
                        .flatMap(slug -> researchDataService.listPublicStateMoneyPagesForContent(slug).stream())
                .distinct();
        Optional<String> authorityStateCode = recordsAuthorityStateCode(contentPage);

        Map<String, Map.Entry<StateMoneyPage, StateProfile>> bestRouteByState = stateMoneyPages
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> Map.entry(page, state))
                        .stream())
                .filter(entry -> publishingPolicyService.isIndexableStateMoneyPage(
                        entry.getKey(),
                        entry.getValue()
                ))
                .filter(entry -> authorityStateCode
                        .map(stateCode -> stateCode.equals(entry.getValue().stateCode()))
                        .orElse(true))
                .sorted(Comparator
                        .comparingInt((Map.Entry<StateMoneyPage, StateProfile> entry) -> contentStateLinkScore(contentPage, entry.getKey(), entry.getValue()))
                        .reversed()
                        .thenComparing(entry -> entry.getValue().stateName())
                        .thenComparing(entry -> entry.getKey().title()))
                .collect(Collectors.toMap(
                        entry -> entry.getValue().stateCode(),
                        entry -> entry,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));
        return List.copyOf(bestRouteByState.values());
    }

    private StateSurfaceSignalView stateSurfaceSignalView(ContentPage contentPage, StateMoneyPage page, StateProfile state) {
        int linkScore = contentStateLinkScore(contentPage, page, state);
        int observedBoost = observedIntentSignalBoost(contentPage.slug(), state.stateCode(), page.contentSlug());
        int pageSourceCount = size(page.officialSourceIds());
        int backingSourceCount = distinctSourceCount(
                page.officialSourceIds(),
                state.officialSourceIds(),
                state.localAuthoritySourceIds(),
                state.recordsLookupSourceIds()
        );
        int localRiskCheckCount = Math.max(size(page.lowEndBreakers()), size(state.lowEndRiskChecks()));
        boolean exactMatch = contentPage.slug().equals(page.contentSlug());
        boolean anchorState = "anchor".equalsIgnoreCase(state.launchTier());
        boolean hasCountyRecords = !researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty();
        boolean leadWithStateWorkflow = shouldLeadWithStateWorkflow(contentPage);
        boolean stateAwareTool = supportsStateAwareTool(contentPage);

        String workflowFitLabel = linkScore >= 48 || observedBoost >= 16
                ? "Strong"
                : linkScore >= 34 || observedBoost >= 8
                        ? "Good"
                        : "Emerging";
        List<String> workflowReasons = new ArrayList<>();
        if (exactMatch) {
            workflowReasons.add("the live page already matches this intent directly");
        } else if (isPermitLookupHub(contentPage)) {
            workflowReasons.add("the lookup route already resolves through a live records or permit workflow page");
        } else if (isTransferComplianceHub(contentPage)) {
            workflowReasons.add("the transfer route already resolves through a live supporting workflow page");
        }
        if (anchorState) {
            workflowReasons.add("it is an anchor launch state");
        }
        if (observedBoost >= 12) {
            workflowReasons.add("observed search and lead signals are already strong");
        } else if (observedBoost >= 8) {
            workflowReasons.add("observed intent signals are already present");
        }
        if (hasCountyRecords) {
            workflowReasons.add("county file pages are already live");
        }
        String workflowFitPrefix = switch (workflowFitLabel) {
            case "Strong" -> "a strong";
            case "Good" -> "a good";
            default -> "an emerging";
        };
        String workflowFitNote = workflowReasons.isEmpty()
                ? state.stateName() + " is live, but the local wedge is still more directional than dominant."
                : state.stateName() + " is " + workflowFitPrefix + " local wedge because " + joinWithAnd(workflowReasons) + ".";

        Double confidence = state.confidenceScore();
        String confidenceText = confidenceLabel(confidence);
        String evidenceDepthLabel = backingSourceCount >= 7 || (pageSourceCount >= 3 && confidence != null && confidence >= 0.75)
                ? "Source-backed"
                : backingSourceCount >= 4 || (pageSourceCount >= 2 && confidence != null && confidence >= 0.6)
                        ? "Solid"
                        : "Developing";
        String evidenceDepthNote = confidenceText.isBlank()
                ? "Backed by " + backingSourceCount + " distinct official or workflow source"
                        + (backingSourceCount == 1 ? "" : "s")
                        + " across the state page, records path, and authority notes."
                : "Backed by " + backingSourceCount + " distinct official or workflow source"
                        + (backingSourceCount == 1 ? "" : "s")
                        + " and a " + confidenceText.toLowerCase(Locale.US) + " state profile.";

        String toolHandoffLabel;
        String toolHandoffNote;
        String riskSuffix = localRiskCheckCount >= 3
                ? " It already surfaces " + localRiskCheckCount + " local risk checks that can widen the downside."
                : "";
        if (leadWithStateWorkflow) {
            if (!stateAwareTool) {
                toolHandoffLabel = "Use after local workflow";
                toolHandoffNote = "The next tool step does not carry " + state.stateName()
                        + " directly, so keep the state page open as the backstop." + riskSuffix;
            } else if (backingSourceCount >= 6 || observedBoost >= 8 || hasCountyRecords) {
                toolHandoffLabel = "Ready after one local check";
                toolHandoffNote = state.stateName()
                        + " can follow into the tool, but narrow the file, permit, or buyer lane first." + riskSuffix;
            } else {
                toolHandoffLabel = "Cautious handoff";
                toolHandoffNote = "The tool can take " + state.stateName()
                        + ", but one local workflow check should happen before the number carries much weight." + riskSuffix;
            }
        } else if (stateAwareTool && (backingSourceCount >= 4 || (confidence != null && confidence >= 0.6))) {
            toolHandoffLabel = "Ready now";
            toolHandoffNote = "The tool can carry " + state.stateName()
                    + " directly, so this page can hand off fast and use the state page only as a backstop." + riskSuffix;
        } else if (stateAwareTool) {
            toolHandoffLabel = "Ready with backstop";
            toolHandoffNote = "Start in the tool with " + state.stateName()
                    + " attached, then open the state page if the result still feels broad." + riskSuffix;
        } else {
            toolHandoffLabel = "Use with local backstop";
            toolHandoffNote = "The tool step does not hold " + state.stateName()
                    + " context directly, so keep the local page beside the estimate." + riskSuffix;
        }

        return new StateSurfaceSignalView(
                workflowFitLabel,
                workflowFitNote,
                evidenceDepthLabel,
                evidenceDepthNote,
                toolHandoffLabel,
                toolHandoffNote
        );
    }

    private int contentStateLinkScore(ContentPage contentPage, StateMoneyPage page, StateProfile state) {
        if (isPermitLookupHub(contentPage)) {
            return permitLookupStateLinkScore(page, state);
        }
        if (isTransferComplianceHub(contentPage)) {
            return transferComplianceStateLinkScore(page, state);
        }
        int score = 0;
        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 20;
        }
        score += (int) Math.round((state.confidenceScore() == null ? 0.0 : state.confidenceScore()) * 10);
        score += Math.min(size(page.officialSourceIds()), 3) * 3;
        score += Math.min(size(page.lowEndBreakers()), 2);
        if (contentPage.slug().equals(page.contentSlug())) {
            score += 12;
        }
        score += observedIntentSignalBoost(contentPage.slug(), state.stateCode(), page.contentSlug());
        return score;
    }

    private int observedIntentSignalBoost(String contentSlug, String stateCode, String pageContentSlug) {
        return switch (contentSlug) {
            case PERMIT_LOOKUP_SLUG, RECORDS_ONLINE_SLUG, RECORDS_BY_COUNTY_SLUG, PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG, AS_BUILT_RECORDS_SLUG, TANK_LOCATION_RECORDS_SLUG, INSPECTION_LETTER_SLUG -> switch (pageContentSlug) {
                case "septic-records-checklist" -> switch (stateCode) {
                    case "TN" -> 34;
                    case "NC", "TX" -> 20;
                    case "AL", "IN" -> 16;
                    case "GA" -> 12;
                    default -> 0;
                };
                case "septic-permit-process" -> switch (stateCode) {
                    case "TN" -> 28;
                    case "NC", "TX", "AL" -> 14;
                    case "SC", "IN" -> 10;
                    default -> 0;
                };
                default -> 0;
            };
            case "septic-records-checklist" -> "septic-records-checklist".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "TN" -> 30;
                        case "NC", "TX" -> 18;
                        case "IN" -> 18;
                        case "AL", "GA" -> 12;
                        default -> 0;
                    }
                    : 0;
            case "septic-permit-process" -> "septic-permit-process".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "TN" -> 18;
                        case "NC", "TX", "AL" -> 12;
                        case "SC" -> 14;
                        case "NE", "RI" -> 8;
                        default -> 0;
                    }
                    : 0;
            case "buying-a-house-with-a-septic-system" -> "buying-a-house-with-a-septic-system".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "NY" -> 16;
                        default -> 0;
                    }
                    : 0;
            case "perc-test-cost" -> "perc-test-cost".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "AL" -> 22;
                        case "GA" -> 20;
                        case "SC" -> 8;
                        default -> 0;
                    }
                    : 0;
            default -> 0;
        };
    }

    private Stream<String> stateMoneyPageSlugsForContentPage(ContentPage contentPage) {
        if (isPermitLookupHub(contentPage)) {
            return PERMIT_LOOKUP_STATE_SLUGS.stream();
        }
        if (isTransferComplianceHub(contentPage)) {
            return TRANSFER_COMPLIANCE_STATE_SLUGS.stream();
        }
        return Stream.of(contentPage.slug());
    }

    private Optional<String> recordsAuthorityStateCode(ContentPage contentPage) {
        return contentPage == null
                ? Optional.empty()
                : Optional.ofNullable(RECORDS_AUTHORITY_STATE_CODES.get(contentPage.slug()));
    }

    private List<StateProfile> renderedStatesForContentPage(ContentPage contentPage, int limit) {
        Optional<String> authorityStateCode = recordsAuthorityStateCode(contentPage);
        return researchDataService.getPublicStateProfiles().stream()
                .filter(state -> authorityStateCode
                        .map(stateCode -> stateCode.equals(state.stateCode()))
                        .orElse(true))
                .limit(limit)
                .toList();
    }

    private int permitLookupStateLinkScore(StateMoneyPage page, StateProfile state) {
        int score = stateMoneyPagePriorityScore(state, page);
        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> 34;
            case "septic-permit-process" -> 28;
            default -> -100;
        };
        if (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty()) {
            score += 12;
        }
        score += switch (state.stateCode()) {
            case "TN" -> 34;
            case "NC" -> 22;
            case "TX" -> 20;
            case "AL", "IN" -> 16;
            case "GA", "SC" -> 10;
            default -> 0;
        };
        if ("septic-records-checklist".equals(page.contentSlug())) {
            score += switch (state.stateCode()) {
                case "TN" -> 12;
                case "NC", "TX" -> 8;
                case "AL", "IN" -> 6;
                default -> 0;
            };
        }
        if (page.highlightBuyerTrigger()) {
            score += 3;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private int transferComplianceStateLinkScore(StateMoneyPage page, StateProfile state) {
        int score = stateMoneyPagePriorityScore(state, page);
        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> 28;
            case "septic-permit-process" -> 24;
            case "buying-a-house-with-a-septic-system" -> 20;
            default -> -100;
        };
        if (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty()) {
            score += 10;
        }
        score += switch (state.stateCode()) {
            case "GA" -> 22;
            case "AL" -> 20;
            case "IN" -> 12;
            case "CO", "NC", "NY" -> 8;
            default -> 0;
        };
        if ("septic-records-checklist".equals(page.contentSlug())) {
            score += switch (state.stateCode()) {
                case "GA" -> 10;
                case "AL" -> 8;
                case "IN" -> 4;
                default -> 0;
            };
        }
        if (page.highlightBuyerTrigger()) {
            score += 4;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private boolean supportsStateAwareTool(ContentPage contentPage) {
        return switch (calculatorPathForModule(contentPage.calculatorModule())) {
            case "/septic-system-cost-calculator/", "/septic-tank-size-estimator/", "/drain-field-estimator/" -> true;
            default -> false;
        };
    }

    private int stateMoneyPagePriorityScore(StateProfile state, StateMoneyPage page) {
        int score = Math.min(size(page.officialSourceIds()), 3) * 3
                + Math.min(size(page.decisionSteps()), 4)
                + Math.min(size(page.lowEndBreakers()), 3)
                + Math.min(size(page.quotePrepChecklist()), 3);

        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 2;
        }

        if (state.confidenceScore() != null && state.confidenceScore() < 0.7) {
            score += switch (page.contentSlug()) {
                case "septic-records-checklist", "septic-inspection-cost" -> 4;
                default -> 0;
            };
        }

        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> (hasItems(state.recordsToRequest(), 2) ? 14 : 6)
                    + (hasItems(state.recordsLookupSourceIds(), 1) ? 8 : 0)
                    + (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty() ? 5 : 0);
            case "buying-a-house-with-a-septic-system" -> (hasText(state.buyerInspectionTrigger()) ? 15 : 0)
                    + (hasText(state.specialAreaNote()) ? 3 : 0);
            case "septic-inspection-cost" -> (hasText(state.maintenanceInspectionNote()) ? 10 : 0)
                    + (hasText(state.buyerInspectionTrigger()) ? 6 : 0);
            case "septic-permit-process" -> (hasItems(state.permitPathSteps(), 3) ? 13 : 6)
                    + (hasText(state.whoToCallFirst()) ? 4 : 0)
                    + (hasItems(state.localAuthoritySourceIds(), 1) ? 6 : 0);
            case "septic-replacement-cost" -> (researchDataService.findStateCostProfile(state.stateCode())
                    .map(StateCostProfile::replacementMid)
                    .filter(value -> value != null)
                    .isPresent() ? 9 : 0)
                    + (hasItems(state.lowEndRiskChecks(), 2) ? 4 : 0);
            case "perc-test-cost" -> (hasText(state.siteEvalSummary()) ? 9 : 0)
                    + switch (state.stateCode()) {
                        case "AL", "GA" -> 10;
                        default -> 0;
                    };
            case "failed-perc-test-septic" -> (hasText(state.siteEvalSummary()) ? 9 : 0)
                    + (hasItems(state.recordsLookupSourceIds(), 1) ? 4 : 0);
            case "septic-replacement-area" -> (hasText(state.siteEvalSummary()) ? 8 : 0)
                    + (hasItems(state.recordsToRequest(), 2) ? 4 : 0);
            case "wet-yard-over-septic-drain-field" -> (hasText(state.siteEvalSummary()) ? 8 : 0)
                    + (hasItems(state.recordsToRequest(), 2) ? 4 : 0);
            case "drain-field-replacement-cost" -> hasText(state.siteEvalSummary()) ? 6 : 0;
            case "septic-pumping-cost" -> hasText(state.maintenanceInspectionNote()) ? 7 : 0;
            default -> 0;
        };

        return score;
    }

    private Optional<String> stateGuideLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        String prefix = "/septic-system-cost-calculator/";
        if (normalizedPath != null && normalizedPath.startsWith(prefix)) {
            String stateSlug = normalizedPath.substring(prefix.length()).replaceFirst("/$", "");
            if (!stateSlug.isBlank() && !stateSlug.contains("/")) {
                return researchDataService.findStateBySlug(stateSlug)
                        .map(StateProfile::stateName)
                        .or(() -> usStateDirectoryService.findBySlug(stateSlug).map(UsStateDirectoryService.UsStateReference::stateName))
                        .map(stateName -> stateName + " septic guide");
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private StateRecordsSearchResponseView stateRecordsSearchResponse(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            SourceRecord primaryRecordsLookupSource,
            SourceRecord primaryLocalAuthoritySource,
            List<PageLink> countyRecordLinks,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return null;
        }

        List<String> queryExamples = stateRecordsQueryExamples(state);
        List<PageLink> countyLinks = stateRecordsCountyLinks(state.stateCode(), countyRecordLinks);
        String firstQuery = queryExamples.stream()
                .findFirst()
                .orElse(state.stateName().toLowerCase(Locale.US) + " septic records");
        String officialPath = primaryRecordsLookupSource != null
                ? primaryRecordsLookupSource.title()
                : primaryLocalAuthoritySource != null
                        ? primaryLocalAuthoritySource.title()
                        : state.agencyName();
        String firstArtifact = firstNonBlank(
                firstOf(state.recordsToRequest()),
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.firstArtifacts().get(0),
                "The permit copy, as-built, final approval, inspection letter, or written no-record response tied to the parcel."
        );
        String countyRouteSummary = countyLinks.isEmpty()
                ? "Keep the state route focused on office ownership, request language, parcel identifiers, and official source depth."
                : "Route county-known searches into " + compactCountyRouteList(countyLinks)
                        + " before the visitor has to run another search.";

        List<CountyWorkflowFieldView> responseRows = List.of(
                new CountyWorkflowFieldView(
                        "Search capture",
                        "Answer " + firstQuery + " with the file owner, the official route, and the first artifact before the page becomes another broad state overview."
                ),
                new CountyWorkflowFieldView(
                        "Official path",
                        "Use " + officialPath + " as the verification lane, then clarify whether the request belongs with a state office, regional contact, contract county, or local health department."
                ),
                new CountyWorkflowFieldView(
                        "County handoff",
                        countyRouteSummary
                ),
                new CountyWorkflowFieldView(
                        "Proof to pull",
                        firstArtifact
                )
        );

        List<PageLink> actionLinks = new ArrayList<>();
        if ("TN".equals(state.stateCode())) {
            actionLinks.add(new PageLink(
                    "TDEC search and 403 guide",
                    "/tdec-septic-records/",
                    "Use the dedicated TDEC page for SSDS search fields, 403 fallback, field-office routing, and contract-county exceptions."
            ));
        }
        actionLinks.addAll(List.of(
                new PageLink(
                        "County records index",
                        "/septic-records-by-county/",
                        "Open the state-to-county route list when the county is known."
                ),
                new PageLink(
                        "Search by address",
                        "/septic-permit-search-by-address/",
                        "Use when the visitor has an address, parcel, APN, TMS, owner, or legal-description clue."
                ),
                new PageLink(
                        "Records request wording",
                        "/septic-permit-records-request/",
                        "Use when the next move is a permit copy, as-built, inspection letter, or no-record response."
                ),
                new PageLink(
                        "As-built file route",
                        "/septic-as-built-records/",
                        "Use when tank, field, reserve-area, site sketch, or layout proof changes the next decision."
                )
        ));

        return new StateRecordsSearchResponseView(
                "Official records route",
                stateRecordsPriorityLabel(state.stateCode()),
                state.stateName() + " records lookup guide",
                stateRecordsResponseSummary(state),
                queryExamples,
                responseRows,
                countyLinks,
                actionLinks
        );
    }

    private List<CountyWorkflowFieldView> stateOfficialFilePathRows(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            SourceRecord primaryRecordsLookupSource,
            SourceRecord primaryLocalAuthoritySource,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return List.of();
        }

        SourceRecord fileSource = primaryRecordsLookupSource != null
                ? primaryRecordsLookupSource
                : primaryLocalAuthoritySource;
        String officialOwner = fileSource == null
                ? state.agencyName()
                : sourceDisplayName(fileSource);
        String firstArtifact = firstNonBlank(
                firstOf(state.recordsToRequest()),
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.firstArtifacts().get(0),
                "Permit copy, as-built, final approval, inspection letter, repair history, or written no-record response."
        );
        String countyDrop = firstNonBlank(
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.countyDropTriggers().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.countyDropTriggers().get(0),
                "When the county is known, move from the state route into the county record page before sending the visitor to another broad search."
        );
        String requestMethod = stateRecordsRequestMethod(state, primaryRecordsLookupSource);

        return List.of(
                new CountyWorkflowFieldView(
                        "File owner",
                        officialOwner + ". Treat this as the first verification lane, not as proof that every county file is online."
                ),
                new CountyWorkflowFieldView(
                        "First artifact",
                        firstArtifact
                ),
                new CountyWorkflowFieldView(
                        "Request method",
                        requestMethod
                ),
                new CountyWorkflowFieldView(
                        "County drop trigger",
                        countyDrop
                ),
                new CountyWorkflowFieldView(
                        "No-record fallback",
                        stateRecordsNoRecordFallback(state)
                ),
                new CountyWorkflowFieldView(
                        "Address clue",
                        stateRecordsAddressClue(state)
                )
        );
    }

    private String stateRecordsResponseSummary(StateProfile state) {
        if ("TN".equals(state.stateCode())) {
            return "Use this Tennessee county guide after the statewide search identifies the likely file owner. Open the dedicated TDEC search guide for SSDS fields and 403 help; stay here to choose the field office, contract county, or county record route.";
        }
        return "Use this " + state.stateName() + " guide to pick the right office, pull the first file, and jump to a county route when the property location is already known.";
    }

    private String stateRecordsRequestMethod(StateProfile state, SourceRecord primaryRecordsLookupSource) {
        return switch (state.stateCode()) {
            case "IN" -> "Start with the county or local health department that owns residential onsite records. Ask for the permit, site plan or design, soil report, inspection or closeout record, and any operating-permit history tied to the parcel.";
            case "NC" -> "Start with county environmental health. Ask for the improvement permit, construction authorization, operation permit or certificate of completion, as-built drawing, repair file, and any written no-record response.";
            case "SC" -> "Start with the SCDES office that covers the county. Ask for the existing permit copy and final-inspection status; use ePermitting for a new D-1740 application, not as proof that an older permit file is online.";
            case "TN" -> "Start with the dedicated TDEC search guide, then move to the correct field office or contract county for the permit copy, inspection letter, repair permit, layout, or written no-record response.";
            default -> primaryRecordsLookupSource != null
                    ? "Start with " + primaryRecordsLookupSource.title() + ", then use county or regional contact wording if the portal does not show the parcel file."
                    : "Start with the official state or local authority route, then ask which county, regional, or delegated office owns old septic files.";
        };
    }

    private String stateRecordsNoRecordFallback(StateProfile state) {
        return switch (state.stateCode()) {
            case "IN" -> "A blank web search is not a no-record finding. Ask the county or local health department to check archived permits, site plans, soil reports, inspection records, and any operating-permit file.";
            case "NC" -> "A county portal miss is not proof that no file exists. Ask county environmental health for a written no-record response and whether older, paper, repair, or renamed-address files need a staff search.";
            case "SC" -> "If SCDES cannot locate a permit copy, ask for that response in writing. For an older home or a system that still cannot be located, SCDES directs the owner to a licensed septic contractor for field location.";
            case "TN" -> "A TDEC 403 or empty result is not proof that no file exists. Use the field-office or contract-county route and request the permit, layout, inspection letter, repair history, or a written no-record response.";
            default -> "If the " + state.stateName() + " lookup has no match, ask for a written no-record response and the office that owns archived, regional, contract-county, or pre-digital septic files.";
        };
    }

    private String stateRecordsAddressClue(StateProfile state) {
        return switch (state.stateCode()) {
            case "IN" -> "Carry the property address, owner name, parcel number, county, and any permit or application number into the local health department request.";
            case "NC" -> "Carry the property address, parcel or PIN, owner name, county, subdivision or lot, and any permit or application number into the environmental health request.";
            case "SC" -> "SCDES asks for the tax map number, lot and block, physical address, installation or build date, original permit holder, and subdivision name when available.";
            case "TN" -> "Carry the property address, parcel, current and prior owner, subdivision, lot, legal description, and any permit number into the TDEC or contract-county request.";
            default -> "Carry the " + state.stateName() + " address, parcel/APN/TMS, owner, legal description, subdivision, and any prior permit number into the next request.";
        };
    }

    private List<String> stateRecordsQueryExamples(StateProfile state) {
        List<String> observedQueries = stateRecordsResponseQueries(state);
        if (observedQueries != null && !observedQueries.isEmpty()) {
            return observedQueries.stream().limit(8).toList();
        }
        String stateName = state.stateName().toLowerCase(Locale.US);
        return List.of(
                stateName + " septic records",
                stateName + " septic permit lookup",
                stateName + " county septic records",
                "septic permit search by address " + stateName,
                "septic as-built records " + stateName
        );
    }

    private boolean hasStateRecordsSearchResponseTarget(StateProfile state) {
        return researchDataService.findSearchResponseTarget("state_records", state.stateCode()).isPresent()
                || STATE_RECORDS_RESPONSE_QUERIES.containsKey(state.stateCode());
    }

    private List<String> stateRecordsResponseQueries(StateProfile state) {
        LinkedHashSet<String> queries = new LinkedHashSet<>();
        if ("TN".equals(state.stateCode())) {
            queries.addAll(STATE_RECORDS_RESPONSE_QUERIES.getOrDefault(state.stateCode(), List.of()));
        }
        researchDataService.findSearchResponseTarget("state_records", state.stateCode())
                .map(SearchResponseTarget::queryList)
                .filter(items -> !items.isEmpty())
                .ifPresent(items -> items.stream()
                        .filter(item -> !"TN".equals(state.stateCode()) || !item.toLowerCase(Locale.US).contains("tdec"))
                        .forEach(queries::add));
        if (!"TN".equals(state.stateCode())) {
            queries.addAll(STATE_RECORDS_RESPONSE_QUERIES.getOrDefault(state.stateCode(), List.of()));
        }
        return queries.isEmpty() ? null : queries.stream().toList();
    }

    private List<PageLink> stateRecordsCountyLinks(String stateCode, List<PageLink> countyRecordLinks) {
        if (countyRecordLinks == null || countyRecordLinks.isEmpty()) {
            return List.of();
        }
        LinkedHashMap<String, PageLink> selectedLinks = new LinkedHashMap<>();
        STATE_RECORDS_PRIORITY_COUNTY_SLUGS.getOrDefault(stateCode, List.of()).forEach(preferredSlug ->
                countyRecordLinks.stream()
                        .filter(link -> link.path().contains("/" + preferredSlug + "/"))
                        .findFirst()
                        .ifPresent(link -> selectedLinks.putIfAbsent(link.path(), link))
        );

        Set<String> boostedCountySlugs = countySearchResponseSlugs(stateCode);
        countyRecordLinks.stream()
                .filter(link -> boostedCountySlugs.stream().anyMatch(slug -> link.path().contains("/" + slug + "/")))
                .limit(8)
                .forEach(link -> selectedLinks.putIfAbsent(link.path(), link));
        if (!selectedLinks.isEmpty()) {
            return selectedLinks.values().stream().limit(8).toList();
        }
        return countyRecordLinks.stream().limit(6).toList();
    }

    private List<PageLink> featuredStateMoneyCountyLinks(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            List<PageLink> countyRecordLinks
    ) {
        if (countyRecordLinks == null || countyRecordLinks.isEmpty()) {
            return List.of();
        }
        if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return countyRecordLinks.stream().limit(30).toList();
        }

        LinkedHashMap<String, PageLink> orderedLinks = new LinkedHashMap<>();
        stateRecordsCountyLinks(state.stateCode(), countyRecordLinks)
                .forEach(link -> orderedLinks.put(link.path(), link));
        countyRecordLinks.forEach(link -> orderedLinks.putIfAbsent(link.path(), link));
        return orderedLinks.values().stream().limit(30).toList();
    }

    private Set<String> countySearchResponseSlugs(String stateCode) {
        String prefix = stateCode + "::";
        LinkedHashSet<String> slugs = COUNTY_SEARCH_RESPONSE_BOOSTS.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> key.substring(prefix.length()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        researchDataService.listSearchResponseTargets("county_records").stream()
                .map(SearchResponseTarget::key)
                .filter(key -> key.startsWith(prefix))
                .map(key -> key.substring(prefix.length()))
                .forEach(slugs::add);
        return slugs;
    }

    private String stateRecordsPriorityLabel(String stateCode) {
        return switch (stateCode) {
            case "TN" -> "County routing after the statewide TDEC search";
            case "IN" -> "How-to-find records and system-lookup searches";
            case "NC" -> "County health file and permit-lookup searches";
            case "TX" -> "County OSSF records and address-search handoff";
            case "AL" -> "County health records and perc-file handoff";
            case "GA" -> "County permit file and address-search handoff";
            case "SC" -> "SCDES, county, and D-1740 record searches";
            default -> "State record lookup and county route handoff";
        };
    }

    private String compactCountyRouteList(List<PageLink> countyLinks) {
        String compactList = countyLinks.stream()
                .map(PageLink::compactTitle)
                .filter(this::hasText)
                .limit(3)
                .collect(Collectors.joining(", "));
        return hasText(compactList) ? compactList : "the strongest county routes";
    }

    private List<SearchIntentOpportunityView> searchIntentOpportunities(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            SourceRecord primaryRecordsLookupSource,
            SourceRecord primaryLocalAuthoritySource
    ) {
        if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return List.of();
        }
        String stateRecordsPath = stateMoneyPage.path(state.slug());
        String recordsPath = primaryRecordsLookupSource != null
                ? primaryRecordsLookupSource.url()
                : stateRecordsPath;
        String contactPath = primaryLocalAuthoritySource != null
                ? primaryLocalAuthoritySource.url()
                : recordsPath;
        String recordsTargetType = primaryRecordsLookupSource != null ? "official_source" : "state_money_page";
        String contactTargetType = primaryLocalAuthoritySource != null ? "official_source" : recordsTargetType;
        String countyAnchorPath = stateRecordsPath + "#county-pages";

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && "TN".equals(state.stateCode())) {
            return List.of(
                    new SearchIntentOpportunityView(
                            "tennessee-septic-records",
                            "County records",
                            "Tennessee septic records by county",
                            "Use this route after the statewide search when the next question is which field office, contract county, or county workflow owns the permit file, inspection letter, or repair record.",
                            "Open Tennessee county routes",
                            countyAnchorPath,
                            "page_anchor"
                    ),
                    new SearchIntentOpportunityView(
                            "tdec-septic-records",
                            "TDEC records",
                            "TDEC septic records",
                            "Use the dedicated TDEC guide for accepted search fields, the current SSDS route, 403 handling, and the field-office or contract-county fallback before treating the record as missing.",
                            "Open TDEC search guide",
                            "/tdec-septic-records/",
                            "internal_page"
                    ),
                    new SearchIntentOpportunityView(
                            "state-of-tn-septic-records",
                            "State of TN",
                            "State of TN septic records",
                            "Use the state route to identify the permit owner, then jump into the county pages when the search needs a local file owner, parcel clue, or inspection-letter path.",
                            "Open Tennessee county routes",
                            countyAnchorPath,
                            "page_anchor"
                    ),
                    new SearchIntentOpportunityView(
                            "tennessee-septic-permit-lookup",
                            "Permit lookup",
                            "Tennessee septic permit lookup",
                            "For permit lookup searches, the useful answer is not a generic rule summary. It is the file path: TDEC, contract county, permit history, inspection letter, and repair-permit trail.",
                            "Open Tennessee permit contact",
                            contactPath,
                            contactTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "blount-county-septic-records",
                            "Blount County",
                            "Blount County septic records",
                            "Use this route when the search has already named Blount County and the user needs the county file path, permit copy, inspection letter, or written no-record response.",
                            "Open Blount County records",
                            "/septic-records-checklist/tennessee/blount-county/",
                            "county_records_page"
                    ),
                    new SearchIntentOpportunityView(
                            "hamilton-county-septic-inspection-records",
                            "Hamilton County",
                            "Hamilton County septic inspection records",
                            "Use this route when the searcher needs Hamilton County inspection records, permit history, address clues, or the file trail behind a buyer or repair decision.",
                            "Open Hamilton County records",
                            "/septic-records-checklist/tennessee/hamilton-county/",
                            "county_records_page"
                    )
            );
        }

        return switch (state.stateCode()) {
            case "NC" -> List.of(
                    new SearchIntentOpportunityView(
                            "north-carolina-septic-permit-lookup",
                            "Permit lookup",
                            "North Carolina septic permit lookup",
                            "Start with the county environmental health file and confirm the improvement permit, construction authorization, operation permit, site sketch, or repair record tied to the parcel.",
                            "Open North Carolina records path",
                            recordsPath,
                            recordsTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "north-carolina-county-septic-records",
                            "County records",
                            "North Carolina county septic records",
                            "Use this when the search is really about the local health department file owner, not a broad statewide cost page.",
                            "Open county records routes",
                            countyAnchorPath,
                            "page_anchor"
                    ),
                    new SearchIntentOpportunityView(
                            "north-carolina-as-built-records",
                            "As-built file",
                            "North Carolina septic as-built and permit file",
                            "Pull the as-built, authorization history, and operation record before trusting a buyer story, addition plan, repair quote, or replacement estimate.",
                            "Open permit records request guide",
                            "/septic-permit-records-request/",
                            "related_internal"
                    ),
                    new SearchIntentOpportunityView(
                            "wake-county-septic-records",
                            "Wake County",
                            "Wake County septic permit records",
                            "Go straight to Wake County when the search already names the county. Use iMAPS and the Permit Portal trail to match the parcel, permit, and septic-file history.",
                            "Open Wake County records",
                            "/septic-records-checklist/north-carolina/wake-county/",
                            "county_records_page"
                    )
            );
            case "IN" -> List.of(
                    new SearchIntentOpportunityView(
                            "indiana-septic-permit-lookup",
                            "Permit lookup",
                            "Indiana septic permit lookup",
                            "Start with the county or local health department file, then confirm the site file, local board record, and sewer-availability gate before pricing the next step.",
                            "Open Indiana records path",
                            recordsPath,
                            recordsTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "indiana-county-septic-records",
                            "County records",
                            "Indiana county septic records",
                            "Use this when the parcel location is known and the useful click is the local file owner, not another statewide overview.",
                            "Open county records routes",
                            countyAnchorPath,
                            "page_anchor"
                    ),
                    new SearchIntentOpportunityView(
                            "indiana-septic-site-file",
                            "Site file",
                            "Indiana septic site file and local board record",
                            "Look for the site file, permit history, local board path, and sewer-availability note before trusting a buyer, inspection, or replacement story.",
                            "Open as-built records guide",
                            "/septic-as-built-records/",
                            "related_internal"
                    ),
                    new SearchIntentOpportunityView(
                            "st-joseph-county-septic-records",
                            "St. Joseph County",
                            "St. Joseph County septic records",
                            "Use the St. Joseph County health-file route when the county is known and the search needs septic schematic records, permit history, soil reports, or a local records request.",
                            "Open St. Joseph County records",
                            "/septic-records-checklist/indiana/st-joseph-county/",
                            "county_records_page"
                    )
            );
            case "SC" -> List.of(
                    new SearchIntentOpportunityView(
                            "south-carolina-septic-permit-lookup",
                            "Permit lookup",
                            "South Carolina septic permit lookup",
                            "Start with SCDES routing, the permit copy, D-1740 history, and final-inspection status before treating the system story as complete.",
                            "Open South Carolina records path",
                            recordsPath,
                            recordsTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "south-carolina-scdes-records",
                            "SCDES records",
                            "SCDES septic records and permit copy",
                            "Use this when the searcher needs the county or regional contact that can confirm the permit copy and D-1740 file.",
                            "Open SCDES contact path",
                            contactPath,
                            contactTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "south-carolina-d1740-file",
                            "D-1740 file",
                            "South Carolina D-1740 septic file",
                            "Match the D-1740 application, permit copy, final inspection, and parcel story before trusting a sale or replacement quote.",
                            "Open permit records request guide",
                            "/septic-permit-records-request/",
                            "related_internal"
                    ),
                    new SearchIntentOpportunityView(
                            "greenville-county-septic-records",
                            "Greenville County",
                            "Greenville County septic records",
                            "Use the Greenville County route when the search needs a parcel or TMS anchor, SCDES permit copy, D-1740 history, or final-inspection trail for that property.",
                            "Open Greenville County records",
                            "/septic-records-checklist/south-carolina/greenville-county/",
                            "county_records_page"
                    )
            );
            case "AL" -> List.of(
                    new SearchIntentOpportunityView(
                            "alabama-septic-permit-lookup",
                            "Permit lookup",
                            "Alabama septic permit lookup",
                            "Start with the county health department file, then confirm the Permit to Install, Approval for Use, and any soil or perc test history tied to the parcel.",
                            "Open Alabama records path",
                            recordsPath,
                            recordsTargetType
                    ),
                    new SearchIntentOpportunityView(
                            "alabama-county-septic-records",
                            "County records",
                            "Alabama county septic records",
                            "Use this when the useful answer is the county office holding the permit copy, Approval for Use, or old system diagram.",
                            "Open county records routes",
                            countyAnchorPath,
                            "page_anchor"
                    ),
                    new SearchIntentOpportunityView(
                            "alabama-perc-test-records",
                            "Perc test file",
                            "Alabama perc test and soil record",
                            "Confirm whether soil testing or a percolation test is already in the file before treating the low-end estimate as realistic.",
                            "Open Alabama perc cost path",
                            "#alabama-perc-cost",
                            "page_anchor"
                    )
            );
            default -> List.of(
                    new SearchIntentOpportunityView(
                            state.slug() + "-septic-permit-lookup",
                            "Permit lookup",
                            state.stateName() + " septic permit lookup",
                            "Start with the official " + state.stateName() + " records or local authority path, then confirm the permit file before trusting a quote, buyer story, or repair plan.",
                            "Open records path",
                            recordsPath,
                            recordsTargetType
                    ),
                    new SearchIntentOpportunityView(
                            state.slug() + "-county-septic-records",
                            "County records",
                            state.stateName() + " county septic records",
                            "Use this in " + state.stateName() + " when the county file owner, parcel clue, or local records request is the real next step.",
                            "Open county records routes",
                            countyAnchorPath,
                            "page_anchor"
                    )
            );
        };
    }

    private StateMoneyPrimaryAction stateMoneyPrimaryAction(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateActionCopy stateActionCopy,
            List<PageLink> countyRecordLinks,
            SourceRecord primaryLocalAuthoritySource,
            SourceRecord primaryRecordsLookupSource,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        String calculatorPath = stateMoneyCalculatorPath(stateMoneyPage, state);

        if ("TN".equals(state.stateCode())
                && "septic-records-checklist".equals(stateMoneyPage.contentSlug())
                && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Search the official Tennessee records route first",
                    sourceActionHeading(stateMoneyPage, state, primaryRecordsLookupSource),
                    countyAwareNote(
                            "Start with the TDEC SSDS route for a Tennessee-wide search, then use the county cards below when the property county is already known or the state route needs a local handoff.",
                            countyWorkflowSynthesis
                    ),
                    "Open Tennessee official records source",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_tennessee_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county file lookup",
                    "Open county record lookup paths",
                    countyAwareNote(
                            "Use the county page first when the state lookup is still too broad and the real blocker is a county file, permit-search result, site-review note, or local records form.",
                            countyWorkflowSynthesis
                    ),
                    "Open county record lookup paths",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("septic-permit-process".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county permit desk",
                    "Open county permit pages",
                    countyAwareNote(
                            "Use the county page first when the state permit path is still too broad and the real blocker is a county permit desk, closeout file, or local repair branch.",
                            countyWorkflowSynthesis
                    ),
                    "Open county permit pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to county diligence",
                    "Open county diligence pages",
                    countyAwareNote(
                            "Use the county page first when the buyer page is still too broad and the real blocker is a local file, transfer artifact, or maintenance obligation tied to the property.",
                            countyWorkflowSynthesis
                    ),
                    "Open county diligence pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county replacement file",
                    "Open county replacement pages",
                    countyAwareNote(
                            "Use the county page first when the replacement number is still broad and the real blocker is a failure-side file, reserve-area rule, sewer branch, or local replacement lane.",
                            countyWorkflowSynthesis
                    ),
                    "Open county replacement pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isInspectionWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county inspection file",
                    "Open county inspection pages",
                    countyAwareNote(
                            "Use the county page first when the inspection number is still broad and the real blocker is a pumping log, operating-history file, transfer artifact, or failure trail tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open county inspection pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isPercWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county site-review file",
                    "Open county site-review pages",
                    countyAwareNote(
                            "Use the county page first when the perc or site-review number is still broad and the real blocker is a parcel file, permit lane, redesign trigger, or local evaluator path.",
                            countyWorkflowSynthesis
                    ),
                    "Open county site-review pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isPumpingWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county maintenance file",
                    "Open county maintenance pages",
                    countyAwareNote(
                            "Use the county page first when the pumping number is still broad and the real blocker is a maintenance log, O&M requirement, or inspection cadence tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open county maintenance pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the file first",
                    sourceActionHeading(stateMoneyPage, state, primaryRecordsLookupSource),
                    countyAwareNote(
                            "Open the official records path before you compress a records problem into one planning number or quote request.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Verify the file owner first",
                    sourceActionHeading(stateMoneyPage, state, primaryLocalAuthoritySource),
                    countyAwareNote(
                            "Use the office that controls the septic file before you trust a seller summary or start chasing quotes.",
                            countyWorkflowSynthesis
                    ),
                    "Open local authority source",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Check the local replacement desk first",
                    primaryLocalAuthoritySource.title(),
                    countyAwareNote(
                            state.stateName() + " replacement questions usually turn on the local authority, failure lane, or sewer branch before the planning range matters.",
                            countyWorkflowSynthesis
                    ),
                    "Open local authority source",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the replacement file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Replacement pricing gets more honest once the county file shows the live failure branch, the parcel history, and the last real approval signal.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug())
                && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the file before pricing buyer risk",
                    "Open the official records path",
                    countyAwareNote(
                            "Buyer risk gets concrete once the permit, as-built, inspection, and maintenance file is in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if (isPercWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            if (primaryLocalAuthoritySource != null) {
                return new StateMoneyPrimaryAction(
                        "Check the site-review desk first",
                        primaryLocalAuthoritySource.title(),
                        countyAwareNote(
                                state.stateName() + " site-review questions usually turn on the local authority, parcel lane, and approval path before a perc number means much.",
                                countyWorkflowSynthesis
                        ),
                        "Open local authority source",
                        primaryLocalAuthoritySource.url(),
                        "state_money_primary_authority_source",
                        "official_source",
                        false
                );
            }
            return new StateMoneyPrimaryAction(
                    "Pull the site-review file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Site-review pricing gets more honest once the county file shows the parcel history, prior approval lane, and redesign trigger behind the lot.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug())
                && researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug()).isPresent()) {
            return new StateMoneyPrimaryAction(
                    "Pull the file before pricing buyer risk",
                    "Open the " + state.stateName() + " records lookup",
                    countyAwareNote(
                            state.stateName() + " buyer risk gets concrete once the permit file, as-built, and local record path are in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open " + state.stateName() + " records lookup",
                    "/septic-records-checklist/" + state.slug() + "/",
                    "state_money_primary_records_page",
                    "state_money_page",
                    false
            );
        }

        if (isPumpingWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the maintenance file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Pumping pricing gets more honest once the county file shows the last pumping, inspection, and O&M signal tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-inspection-cost".equals(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the inspection file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Inspection pricing gets more honest once the permit, as-built, pumping, and maintenance file is in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-permit-process".equals(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Check the permit desk first",
                    sourceActionHeading(stateMoneyPage, state, primaryLocalAuthoritySource),
                    countyAwareNote(
                            state.stateName() + " permit questions usually turn on the local authority and approval path before the planning range matters.",
                            countyWorkflowSynthesis
                    ),
                    "Open permit authority",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        return new StateMoneyPrimaryAction(
                "Run the state estimate",
                stateActionCopy.buttonLabel(),
                countyAwareNote(stateActionCopy.supportingNote(), countyWorkflowSynthesis),
                "Run the estimate",
                calculatorPath,
                "state_money_primary_calculator",
                "calculator",
                true
        );
    }

    private String sourceActionHeading(StateMoneyPage stateMoneyPage, StateProfile state, SourceRecord source) {
        if ("SC".equals(state.stateCode())) {
            if ("septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
                return "SCDES permit-copy request path";
            }
            if ("septic-permit-process".equals(stateMoneyPage.contentSlug())) {
                return "SCDES county or regional contact";
            }
        }
        return source.title();
    }

    private String countyAwareNote(String baseNote, StateCountyWorkflowSynthesisView countyWorkflowSynthesis) {
        if (countyWorkflowSynthesis == null) {
            return baseNote;
        }
        String firstArtifact = countyWorkflowSynthesis.firstArtifacts().isEmpty() ? null : countyWorkflowSynthesis.firstArtifacts().get(0);
        String holdQuote = countyWorkflowSynthesis.holdQuoteChecks().isEmpty() ? null : countyWorkflowSynthesis.holdQuoteChecks().get(0);
        if (hasText(firstArtifact) && hasText(holdQuote)) {
            return baseNote + " Pull first: " + firstArtifact + " Hold pricing when " + holdQuote.toLowerCase() + ".";
        }
        if (hasText(firstArtifact)) {
            return baseNote + " Pull first: " + firstArtifact;
        }
        if (hasText(holdQuote)) {
            return baseNote + " Hold pricing when " + holdQuote.toLowerCase() + ".";
        }
        return baseNote;
    }

    private StateWorkflowDecisionView stateWorkflowDecisionView(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis,
            SourceRecord primaryLocalAuthoritySource,
            SourceRecord primaryRecordsLookupSource
    ) {
        if (countyWorkflowSynthesis == null) {
            return null;
        }
        String firstArtifact = countyWorkflowSynthesis.firstArtifacts().isEmpty()
                ? "The county-side permit, file, or inspection record tied to the parcel."
                : countyWorkflowSynthesis.firstArtifacts().get(0);
        String countyDrop = countyWorkflowSynthesis.countyDropTriggers().isEmpty()
                ? "The state page is still too broad and the real blocker is a county file or office."
                : countyWorkflowSynthesis.countyDropTriggers().get(0);
        String holdQuote = countyWorkflowSynthesis.holdQuoteChecks().isEmpty()
                ? "the county file is strong enough to show the right parcel and last real approval signal"
                : countyWorkflowSynthesis.holdQuoteChecks().get(0);
        String route = stateMoneyPage.contentSlug();
        String firstMove;
        String heading;
        String intro;

        if ("septic-permit-process".equals(route)) {
            heading = "Decision router for " + state.stateName() + " permit work";
            intro = "Identify the county permit branch and its closeout artifact before pricing the work.";
            firstMove = primaryLocalAuthoritySource != null
                    ? "Confirm the county permit desk and the closeout artifact that proves the system actually cleared the last approval step."
                    : "Identify the county permit desk and the closeout artifact before treating the permit path like routine paperwork.";
        } else if ("buying-a-house-with-a-septic-system".equals(route)) {
            heading = "Decision router for " + state.stateName() + " buyer diligence";
            intro = "Match the property to its local file and transfer artifact before negotiating timing, credits, or scope.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Match the seller story to the county file and the buyer-side artifact before you negotiate credits, timing, or scope."
                    : "Resolve the local file and buyer-side artifact before you treat the deal like a routine inspection question.";
        } else if (isInspectionWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " inspection pricing";
            intro = "Pull the county file and operating history before treating the inspection as routine scope.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county inspection, pumping, and operating-history file before you price a routine inspection scope."
                    : "Resolve the county inspection file and the last operating-history artifact before you trust a routine inspection number.";
        } else if (isPercWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " perc and site-review pricing";
            intro = "Resolve the parcel file, permit lane, and redesign trigger before treating the first site-review number as final.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county parcel file and confirm the site-review or permit lane before you price soils, perc, or redesign work."
                    : "Resolve the county site-review lane and the first parcel artifact before you treat the first perc number like the real scope.";
        } else if (isPumpingWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " pumping and maintenance pricing";
            intro = "Confirm the maintenance lane and last service artifact before pricing the visit as routine pumping.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county pumping, inspection, or O&M file before you price this like a basic tank visit."
                    : "Resolve the county maintenance lane and the last service artifact before you trust a routine pumping number.";
        } else if (isReplacementWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " replacement pricing";
            intro = "Confirm the county file, failure branch, and approval constraint before relying on one replacement number.";
            firstMove = primaryRecordsLookupSource != null
                    ? "For " + stateMoneyPage.title() + ", pull the county file and confirm the live repair, failure, reserve-area, or sewer branch before you trust one replacement number."
                    : "Resolve the county file, the local replacement branch, and the last real approval artifact before you treat the first number like the real scope.";
        } else {
            heading = "Decision router for " + state.stateName() + " records work";
            intro = "Resolve the county file owner and first usable artifact before relying on a property or project claim.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county file and match it to the parcel before you trust any seller, owner, or contractor story."
                    : "Resolve the county file owner and first parcel artifact before you compress this into one estimate or quote.";
        }

        return new StateWorkflowDecisionView(
                "Decision router",
                heading,
                intro,
                List.of(
                        new CountyWorkflowFieldView("Resolve first", firstMove),
                        new CountyWorkflowFieldView("Pull first", firstArtifact),
                        new CountyWorkflowFieldView("Escalate to county when", countyDrop),
                        new CountyWorkflowFieldView("Hold pricing when", holdQuote)
                )
        );
    }

    private StateCostScopeView stateCostScopeView(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        if (!isWorkflowCostSlug(stateMoneyPage.contentSlug())) {
            return null;
        }

        String route = stateMoneyPage.contentSlug();
        String clearFirst = firstNonBlank(
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.firstArtifacts().get(0),
                firstOf(state.recordsToRequest()),
                "The county file, parcel identifier, and latest approval artifact tied to this system."
        );
        String lowEndBreaker = firstNonBlank(
                firstOf(stateMoneyPage.lowEndBreakers()),
                firstOf(state.lowEndRiskChecks()),
                "The county file still leaves a permit, repair, or location branch unresolved."
        );
        String countyWidener = firstNonBlank(
                preferredCostWidener(countyWorkflowSynthesis, route),
                firstOf(stateMoneyPage.driverBullets()),
                state.specialAreaNote(),
                "A county-side permit, repair, or maintenance lane is still widening the scope."
        );
        String midpointGate = firstNonBlank(
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.holdQuoteChecks().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.holdQuoteChecks().get(0),
                "the county file still leaves the failure branch, permit lane, or maintenance obligation unresolved"
        );

        List<String> scopeWideners = Stream.concat(
                        safeList(stateMoneyPage.driverBullets()).stream(),
                        safeList(stateMoneyPage.lowEndBreakers()).stream()
                )
                .filter(this::hasText)
                .distinct()
                .limit(6)
                .toList();

        List<String> readinessChecks = Stream.concat(
                        safeList(stateMoneyPage.quotePrepChecklist()).stream(),
                        safeList(countyWorkflowSynthesis == null ? null : countyWorkflowSynthesis.holdQuoteChecks()).stream()
                )
                .filter(this::hasText)
                .distinct()
                .limit(6)
                .toList();

        return new StateCostScopeView(
                "Cost scope router",
                costScopeHeading(route, state.stateName()),
                costScopeIntro(route, state.stateName()),
                List.of(
                        new CountyWorkflowFieldView("Clear first", clearFirst),
                        new CountyWorkflowFieldView("Low-end breaker", lowEndBreaker),
                        new CountyWorkflowFieldView("County widener", countyWidener),
                        new CountyWorkflowFieldView("Stop trusting midpoint when", midpointGate)
                ),
                costScopeWidenersHeading(route, state.stateName()),
                scopeWideners,
                costReadinessHeading(route),
                readinessChecks
        );
    }

    private String costScopeHeading(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "What actually widens " + stateName + " inspection pricing";
            case "perc-test-cost" -> "What actually widens " + stateName + " site-review pricing";
            case "septic-pumping-cost" -> "What actually widens " + stateName + " pumping and maintenance pricing";
            default -> "What actually widens " + stateName + " replacement pricing";
        };
    }

    private String costScopeIntro(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "Use this router before you trust the midpoint. It separates a routine inspection visit from the county artifacts and failure trails that make the scope wider in " + stateName + ".";
            case "perc-test-cost" -> "Use this router before you trust the first perc or site-review number. It separates a routine soils visit from the parcel, redesign, and permit branches that widen the scope in " + stateName + ".";
            case "septic-pumping-cost" -> "Use this router before you trust a basic pumping number. It separates a routine service visit from the operating history, inspection cadence, and maintenance obligations that widen the scope in " + stateName + ".";
            default -> "Use this router before you trust the midpoint. It separates a straightforward replacement story from the county file, failure lane, and redesign triggers that widen the real scope in " + stateName + ".";
        };
    }

    private String costScopeWidenersHeading(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "What keeps widening " + stateName + " inspection scope";
            case "perc-test-cost" -> "What keeps widening " + stateName + " site-review scope";
            case "septic-pumping-cost" -> "What keeps widening " + stateName + " maintenance scope";
            default -> "What keeps widening " + stateName + " replacement scope";
        };
    }

    private String costReadinessHeading(String route) {
        return switch (route) {
            case "septic-inspection-cost" -> "What to line up before you price inspection scope";
            case "perc-test-cost" -> "What to line up before you price site-review scope";
            case "septic-pumping-cost" -> "What to line up before you price maintenance scope";
            default -> "What to line up before you price replacement scope";
        };
    }

    private String preferredCostWidener(StateCountyWorkflowSynthesisView countyWorkflowSynthesis, String route) {
        if (countyWorkflowSynthesis == null || countyWorkflowSynthesis.structureHighlights().isEmpty()) {
            return null;
        }
        String preferredLabel = switch (route) {
            case "septic-inspection-cost" -> "Most common malfunction or repair trail";
            case "perc-test-cost" -> "Most common permit closeout signal";
            case "septic-pumping-cost" -> "Most common special program or exception";
            default -> "Most common malfunction or repair trail";
        };
        return countyWorkflowSynthesis.structureHighlights().stream()
                .filter(field -> preferredLabel.equals(field.label()))
                .map(CountyWorkflowFieldView::value)
                .findFirst()
                .orElseGet(() -> countyWorkflowSynthesis.structureHighlights().get(0).value());
    }

    private String firstOf(List<String> values) {
        return safeList(values).stream()
                .filter(this::hasText)
                .findFirst()
                .orElse(null);
    }

    private String sourceDisplayName(SourceRecord source) {
        if (source == null) {
            return "";
        }
        if (hasText(source.agencyName()) && hasText(source.title())) {
            return source.agencyName() + " | " + source.title();
        }
        if (hasText(source.title())) {
            return source.title();
        }
        if (hasText(source.agencyName())) {
            return source.agencyName();
        }
        return "Official source";
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String stateMoneyCalculatorPath(StateMoneyPage stateMoneyPage, StateProfile state) {
        return "/septic-system-cost-calculator/?state=" + state.stateCode()
                + "&projectType=" + stateMoneyPage.calculatorProjectType()
                + "&sourcePageHint=" + stateMoneyPage.path(state.slug());
    }

    private String stateMoneyQuotePath(String calculatorPath) {
        return calculatorPath + "&quoteMode=true#quote-request";
    }

    private Optional<String> stateMoneyPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 2) {
            return researchDataService.findStateMoneyPage(parts[0], parts[1])
                    .map(StateMoneyPage::title);
        }
        return Optional.empty();
    }

    private Optional<String> countyRecordsPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 3 && "septic-records-checklist".equals(parts[0])) {
            return researchDataService.findCountyRecordsPage(parts[1], parts[2])
                    .map(CountyRecordsPage::title);
        }
        return Optional.empty();
    }

    private Optional<String> contentPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String slug = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "");
        if (slug.isBlank()) {
            return Optional.empty();
        }
        return researchDataService.findContentPage(slug)
                .map(ContentPage::title);
    }

    private String normalizePath(String path) {
        return UriComponentsBuilder.fromUriString(path).build().getPath();
    }

    private String projectTypeLabel(String projectType) {
        return switch (projectType) {
            case "replacement" -> "replacement";
            case "perc_test" -> "perc test";
            case "drainfield_replacement" -> "drain field";
            case "pumping" -> "pumping";
            case "inspection" -> "inspection";
            case "buying_home" -> "buyer";
            default -> "project";
        };
    }

    private String prettifyPath(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null || normalizedPath.isBlank() || "/".equals(normalizedPath)) {
            return "Home";
        }
        String lastSegment = normalizedPath.replaceFirst("/$", "");
        lastSegment = lastSegment.substring(lastSegment.lastIndexOf('/') + 1);
        return Arrays.stream(lastSegment.split("-"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .reduce((left, right) -> left + " " + right)
                .orElse("Related page");
    }

    private int relatedLinkScore(String sourceSlug, String sourceStateCode, String targetPath) {
        String normalizedPath = normalizePath(targetPath);
        if (normalizedPath == null) {
            return 0;
        }

        int score = 0;
        if (isCalculatorPath(normalizedPath)) {
            score += 20;
            if ("septic-system-cost-calculator".equals(sourceSlug)) {
                score += switch (calculatorProjectTypeFromPath(targetPath)) {
                    case "replacement" -> 8;
                    case "inspection" -> 7;
                    case "perc_test" -> 6;
                    default -> 0;
                };
            }
        }

        Optional<String> targetStateSlug = stateSlugFromPath(normalizedPath);
        if (sourceStateCode != null && targetStateSlug.isPresent()) {
            Optional<StateProfile> targetState = researchDataService.findStateBySlug(targetStateSlug.get());
            if (targetState.filter(state -> state.stateCode().equalsIgnoreCase(sourceStateCode)).isPresent()) {
                score += 18;
            }
        }

        String targetContentSlug = targetContentSlug(normalizedPath);
        if (targetContentSlug != null) {
            List<String> preferredTargets = preferredTargetSlugs(sourceSlug);
            int preferredIndex = preferredTargets.indexOf(targetContentSlug);
            if (preferredIndex >= 0) {
                score += 30 - (preferredIndex * 4);
            }
            if (targetContentSlug.equals(sourceSlug)) {
                score -= 10;
            }
        }

        if (targetStateSlug.isPresent()) {
            score += 4;
        }

        if (isCountyRecordsPath(normalizedPath)) {
            score += switch (sourceSlug) {
                case RECORDS_ONLINE_SLUG -> 48;
                case RECORDS_BY_COUNTY_SLUG -> 50;
                case PERMIT_SEARCH_BY_ADDRESS_SLUG, PERMIT_RECORDS_REQUEST_SLUG -> 47;
                case AS_BUILT_RECORDS_SLUG, INSPECTION_LETTER_SLUG -> 44;
                case PERMIT_LOOKUP_SLUG -> 46;
                case TRANSFER_COMPLIANCE_SLUG -> 42;
                case "septic-records-checklist" -> 18;
                default -> 8;
            };
            score += targetStateSlug
                    .flatMap(researchDataService::findStateBySlug)
                    .map(StateProfile::stateCode)
                    .map(stateCode -> switch (stateCode) {
                        case "TN" -> 14;
                        case "NC", "TX" -> 10;
                        case "GA" -> 10;
                        case "AL" -> 8;
                        case "IN" -> 4;
                        default -> 0;
                    })
                    .orElse(0);
        }

        return score;
    }

    private String relatedLinkNote(String sourceSlug, String sourceStateCode, String targetPath) {
        String normalizedPath = normalizePath(targetPath);
        if (normalizedPath == null) {
            return "Open the next task that names the required file, office, or decision.";
        }

        if (isCalculatorPath(normalizedPath)) {
            String projectType = calculatorProjectTypeFromPath(targetPath);
            if (sourceStateCode != null && projectType != null) {
                return "Run the estimate with " + sourceStateCode + " and " + projectTypeLabel(projectType) + " prefilled before you compare local quotes.";
            }
            return "Use the estimator when you still need a planning range before committing to one narrative.";
        }

        Optional<String> guideStateSlug = stateSlugFromPath(normalizedPath);
        if (normalizedPath.startsWith("/septic-system-cost-calculator/") && guideStateSlug.isPresent()) {
            return researchDataService.findStateBySlug(guideStateSlug.get())
                    .map(StateProfile::stateName)
                    .or(() -> usStateDirectoryService.findBySlug(guideStateSlug.get()).map(UsStateDirectoryService.UsStateReference::stateName))
                    .map(stateName -> "Open the " + stateName + " guide for permit path, local office, and records workflow context.")
                    .orElse("Open the state guide for permit path and records context.");
        }

        if (isCountyRecordsPath(normalizedPath)) {
            if (TRANSFER_COMPLIANCE_SLUG.equals(sourceSlug)) {
                return relatedLinkContext(sourceSlug, sourceStateCode)
                        + "Use this when closing risk turns on a county file, certification letter, or local health-office workflow instead of one statewide summary.";
            }
            return relatedLinkContext(sourceSlug, sourceStateCode)
                    + "Use this when the next step is a county file, certification letter, or local health-office workflow rather than a broader state page.";
        }

        String contentSlug = targetContentSlug(normalizedPath);
        if (contentSlug != null) {
            String intentNote = switch (contentSlug) {
                case "septic-replacement-cost" -> "Use this when failure scope or full replacement risk is the real blocker.";
                case "perc-test-cost" -> "Use this when soil, perc, or site-approval uncertainty is driving the decision.";
                case "drain-field-replacement-cost" -> "Use this when the field layout may be the real problem rather than the tank alone.";
                case "failed-perc-test-septic" -> "Use this when a failed or weak perc result is forcing a bigger field or system decision.";
                case "septic-replacement-area" -> "Use this when reserve area or replacement-layout viability is the real blocker.";
                case "wet-yard-over-septic-drain-field" -> "Use this when seepage, odor, or soggy ground near the field is driving urgency.";
                case "septic-pumping-cost" -> "Use this when maintenance cadence or advanced-system upkeep is the open question.";
                case "septic-inspection-cost" -> "Use this when due-diligence scope or inspection leverage matters more than a generic average.";
                case "buying-a-house-with-a-septic-system" -> "Use this when the property deal, not just the system price, is driving risk.";
                case RECORDS_ONLINE_SLUG -> "Use this when the searcher needs the fastest route from broad records intent to the right state or county file owner.";
                case RECORDS_BY_COUNTY_SLUG -> "Use this when the county is already known and the next click should be a local file owner, not another broad overview.";
                case PERMIT_SEARCH_BY_ADDRESS_SLUG -> "Use this when an address search needs to turn into a county or state permit file path.";
                case PERMIT_RECORDS_REQUEST_SLUG -> "Use this when the user needs to request the permit copy, as-built, final approval, repair file, or inspection letter from the right office.";
                case AS_BUILT_RECORDS_SLUG -> "Use this when the installed layout, site sketch, or final approval can change the repair, addition, or replacement scope.";
                case INSPECTION_LETTER_SLUG -> "Use this when the user needs to distinguish a records pull from a closing, lender, or inspection-letter workflow.";
                case PERMIT_LOOKUP_SLUG -> "Use this when the searcher needs one permit lookup doorway before choosing the state records or permit path.";
                case "septic-permit-process" -> "Use this when the next office, permit step, or approval sequence is the real bottleneck.";
                case "septic-records-checklist" -> "Use this when the file is thinner than the current seller, owner, or contractor story.";
                case TRANSFER_COMPLIANCE_SLUG -> "Use this when records, permits, buyer timing, and county workflow need to be resolved together.";
                case "septic-tank-size" -> "Use this when bedroom sizing and minimum gallon band matter more than a full project quote.";
                default -> "Open the task that most closely matches the unresolved file, office, or property decision.";
            };
            return relatedLinkContext(sourceSlug, sourceStateCode) + intentNote;
        }

        return "Open the next task that names the required file, office, or decision.";
    }

    private String relatedLinkContext(String sourceSlug, String sourceStateCode) {
        String statePrefix = hasText(sourceStateCode) ? sourceStateCode + " " : "";
        return hasText(sourceStateCode)
                ? "For " + statePrefix + "property work, "
                : "For this property task, ";
    }

    private List<String> preferredTargetSlugs(String sourceSlug) {
        return switch (sourceSlug) {
            case "septic-system-cost-calculator" -> List.of(
                    TRANSFER_COMPLIANCE_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-replacement-cost",
                    "septic-inspection-cost",
                    "perc-test-cost",
                    "septic-records-checklist",
                    "buying-a-house-with-a-septic-system",
                    "septic-tank-size",
                    "septic-pumping-cost"
            );
            case "septic-replacement-cost" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "perc-test-cost",
                    "buying-a-house-with-a-septic-system",
                    "drain-field-replacement-cost"
            );
            case "perc-test-cost" -> List.of(
                    "failed-perc-test-septic",
                    "septic-replacement-area",
                    PERMIT_LOOKUP_SLUG,
                    "septic-permit-process",
                    "septic-replacement-cost",
                    "septic-records-checklist",
                    "drain-field-replacement-cost"
            );
            case "drain-field-replacement-cost" -> List.of(
                    "septic-replacement-area",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "wet-yard-over-septic-drain-field",
                    "failed-perc-test-septic",
                    "perc-test-cost",
                    "septic-system-cost-calculator"
            );
            case "failed-perc-test-septic" -> List.of(
                    "perc-test-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case "septic-replacement-area" -> List.of(
                    "drain-field-replacement-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "failed-perc-test-septic",
                    "perc-test-cost",
                    "septic-system-cost-calculator"
            );
            case "wet-yard-over-septic-drain-field" -> List.of(
                    "septic-inspection-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "septic-replacement-cost",
                    "septic-system-cost-calculator"
            );
            case "septic-pumping-cost" -> List.of("septic-tank-size", "septic-system-cost-calculator", "septic-inspection-cost");
            case "septic-inspection-cost" -> List.of(
                    INSPECTION_LETTER_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "buying-a-house-with-a-septic-system",
                    "wet-yard-over-septic-drain-field",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-system-cost-calculator"
            );
            case "buying-a-house-with-a-septic-system" -> List.of(
                    INSPECTION_LETTER_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    "septic-records-checklist",
                    "septic-inspection-cost",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "wet-yard-over-septic-drain-field",
                    "septic-replacement-area",
                    "septic-replacement-cost"
            );
            case "septic-permit-process" -> List.of(
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    "septic-records-checklist",
                    "septic-replacement-cost",
                    "buying-a-house-with-a-septic-system",
                    "septic-system-cost-calculator"
            );
            case "septic-records-checklist" -> List.of(
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-replacement-area",
                    "buying-a-house-with-a-septic-system",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "septic-replacement-cost"
            );
            case TRANSFER_COMPLIANCE_SLUG -> List.of(
                    RECORDS_ONLINE_SLUG,
                    INSPECTION_LETTER_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "buying-a-house-with-a-septic-system",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case PERMIT_LOOKUP_SLUG -> List.of(
                    RECORDS_ONLINE_SLUG,
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    TRANSFER_COMPLIANCE_SLUG,
                    "buying-a-house-with-a-septic-system",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case RECORDS_ONLINE_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    TRANSFER_COMPLIANCE_SLUG,
                    "buying-a-house-with-a-septic-system",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case RECORDS_BY_COUNTY_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    RECORDS_ONLINE_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    TRANSFER_COMPLIANCE_SLUG,
                    "buying-a-house-with-a-septic-system",
                    "septic-system-cost-calculator"
            );
            case PERMIT_SEARCH_BY_ADDRESS_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    RECORDS_ONLINE_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "buying-a-house-with-a-septic-system",
                    "septic-system-cost-calculator"
            );
            case PERMIT_RECORDS_REQUEST_SLUG -> List.of(
                    RECORDS_REQUEST_BUILDER_SLUG,
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    RECORDS_ONLINE_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    TRANSFER_COMPLIANCE_SLUG,
                    "septic-system-cost-calculator"
            );
            case AS_BUILT_RECORDS_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    INSPECTION_LETTER_SLUG,
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-system-cost-calculator"
            );
            case INSPECTION_LETTER_SLUG -> List.of(
                    RECORDS_REQUEST_BUILDER_SLUG,
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    "septic-inspection-cost",
                    "buying-a-house-with-a-septic-system",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    TRANSFER_COMPLIANCE_SLUG
            );
            case OFFICIAL_LOOKUP_TOOLS_SLUG -> List.of(
                    RECORDS_REQUEST_BUILDER_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    TDEC_RECORDS_SLUG,
                    NC_PERMIT_LOOKUP_SLUG,
                    TX_OSSF_RECORDS_SLUG,
                    FL_OSTDS_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    "septic-records-checklist"
            );
            case RECORDS_REQUEST_BUILDER_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    DHEC_PERMIT_LOOKUP_SLUG,
                    TDEC_RECORDS_SLUG,
                    NC_PERMIT_LOOKUP_SLUG,
                    TX_OSSF_RECORDS_SLUG,
                    FL_OSTDS_LOOKUP_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG
            );
            case DHEC_PERMIT_LOOKUP_SLUG -> List.of(
                    OFFICIAL_LOOKUP_TOOLS_SLUG,
                    RECORDS_REQUEST_BUILDER_SLUG,
                    RECORDS_BY_COUNTY_SLUG,
                    PERMIT_SEARCH_BY_ADDRESS_SLUG,
                    PERMIT_RECORDS_REQUEST_SLUG,
                    AS_BUILT_RECORDS_SLUG,
                    INSPECTION_LETTER_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-system-cost-calculator"
            );
            default -> List.of();
        };
    }

    private boolean isCalculatorPath(String normalizedPath) {
        return "/septic-system-cost-calculator/".equals(normalizedPath)
                || "/septic-system-cost-calculator".equals(normalizedPath)
                || "/septic-tank-size-estimator/".equals(normalizedPath)
                || "/septic-tank-size-estimator".equals(normalizedPath)
                || "/septic-pump-schedule-estimator/".equals(normalizedPath)
                || "/septic-pump-schedule-estimator".equals(normalizedPath)
                || "/drain-field-estimator/".equals(normalizedPath)
                || "/drain-field-estimator".equals(normalizedPath);
    }

    private String calculatorProjectTypeFromPath(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if (!"/septic-system-cost-calculator/".equals(normalizedPath) && !"/septic-system-cost-calculator".equals(normalizedPath)) {
            return null;
        }
        return uri.getQueryParams().getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
    }

    private Optional<String> stateSlugFromPath(String normalizedPath) {
        String prefix = "/septic-system-cost-calculator/";
        if (normalizedPath.startsWith(prefix)) {
            String stateSlug = normalizedPath.substring(prefix.length()).replaceFirst("/$", "");
            if (!stateSlug.isBlank() && !stateSlug.contains("/")) {
                return Optional.of(stateSlug);
            }
        }

        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 2 || (parts.length == 3 && "septic-records-checklist".equals(parts[0]))) {
            return Optional.of(parts[1]);
        }
        return Optional.empty();
    }

    private String targetContentSlug(String normalizedPath) {
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 1) {
            return parts[0];
        }
        if (parts.length == 2) {
            return parts[0];
        }
        if (parts.length == 3 && "septic-records-checklist".equals(parts[0])) {
            return parts[0];
        }
        return null;
    }

    private boolean isCountyRecordsPath(String normalizedPath) {
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        return parts.length == 3 && "septic-records-checklist".equals(parts[0]);
    }

    private List<StateRuleFactView> stateRuleFactViews(String stateCode) {
        return researchDataService.listPublicStateRuleFacts(stateCode).stream()
                .map(fact -> {
                    SourceRecord source = researchDataService.findSource(fact.sourceId()).orElse(null);
                    return new StateRuleFactView(
                            fact.label(),
                            fact.renderedValue(),
                            fact.note(),
                            firstNonBlank(fact.effectiveDate(), source != null ? source.effectiveDate() : null),
                            firstNonBlank(fact.lastVerifiedAt(), source != null ? source.contentVerifiedAt() : null),
                            confidenceLabel(fact.confidence()),
                            source != null ? source.agencyName() : "",
                            source != null ? source.title() : "",
                            source != null ? source.url() : "",
                            fact.sourceSection(),
                            source != null ? source.trustLevel() : "",
                            source != null ? source.draftOrFinalStatus() : ""
                    );
                })
                .toList();
    }

    private List<CostEvidenceView> costEvidenceViews(String stateCode, String projectType) {
        return researchDataService.listCostEvidence(stateCode, projectType).stream()
                .map(evidence -> new CostEvidenceView(
                        evidence.title(),
                        costEvidenceValueSummary(evidence),
                        evidence.note(),
                        evidence.sourceIds().stream()
                                .map(researchDataService::findSource)
                                .flatMap(Optional::stream)
                                .map(source -> source.agencyName() + ": " + source.title())
                                .reduce((left, right) -> left + " | " + right)
                                .orElse("Source under review")
                ))
                .toList();
    }

    private String costEvidenceValueSummary(com.example.septic.data.model.CostEvidence evidence) {
        if (evidence.multiplier() != null) {
            return "Multiplier " + String.format(Locale.US, "%.3f", evidence.multiplier());
        }
        if (evidence.low() != null && evidence.high() != null) {
            String range = money(evidence.low()) + " to " + money(evidence.high());
            if (evidence.mid() != null) {
                return range + " | midpoint about " + money(evidence.mid());
            }
            return range;
        }
        return "Planning evidence";
    }

    private String confidenceLabel(Double confidence) {
        if (confidence == null) {
            return "";
        }
        if (confidence >= 0.9) {
            return "Very high confidence";
        }
        if (confidence >= 0.75) {
            return "High confidence";
        }
        if (confidence >= 0.6) {
            return "Moderate confidence";
        }
        return "Directional confidence";
    }

    @SafeVarargs
    private final int distinctSourceCount(List<String>... sourceIdGroups) {
        return (int) Stream.of(sourceIdGroups)
                .filter(group -> group != null)
                .flatMap(List::stream)
                .filter(sourceId -> sourceId != null && !sourceId.isBlank())
                .distinct()
                .count();
    }

    private String joinWithAnd(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        if (values.size() == 1) {
            return values.get(0);
        }
        if (values.size() == 2) {
            return values.get(0) + " and " + values.get(1);
        }
        return String.join(", ", values.subList(0, values.size() - 1))
                + ", and " + values.get(values.size() - 1);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }

    private int size(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private String firstListItem(List<String> items, String fallback) {
        if (items == null || items.isEmpty()) {
            return fallback;
        }
        return items.get(0);
    }
}
