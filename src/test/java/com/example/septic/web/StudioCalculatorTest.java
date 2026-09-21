package com.example.septic.web;

import com.example.septic.service.EstimatorResult;
import com.example.septic.service.LeadStorageService;
import com.example.septic.service.ProjectType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.studio-preview.enabled=true", "app.storage.root=./build/test-storage"})
@AutoConfigureMockMvc
class StudioCalculatorTest {
    @Autowired MockMvc mvc;
    @MockitoBean LeadStorageService storage;
    static final String PATH = "/design-preview/studio/calculator/";

    @Test void initialAndPrefilledFormsPreserveContext() throws Exception {
        mvc.perform(get(PATH)).andExpect(status().isOk()).andExpect(view().name("studio/calculator"))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("/studio-calculator.css?v=")))
                .andExpect(content().string(containsString("calculator-landscape-v2.webp")))
                .andExpect(content().string(containsString("Plan the project.")))
                .andExpect(content().string(not(containsString("calc-companion"))))
                .andExpect(content().string(not(containsString("/app.css"))));
        var model = mvc.perform(get(PATH).param("state", "TN").param("projectType", "replacement")
                .param("bedrooms", "4").param("recordsMode", "true").param("recordTankCapacity", "1500 gal")
                .param("county", "Knox").param("quoteMode", "true").param("serviceNeed", "alarm"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("1500 gal")))
                .andExpect(content().string(containsString("This form is not emergency dispatch.")))
                .andReturn().getModelAndView().getModel();
        var form = (EstimateForm) model.get("estimateForm");
        assertThat(form.getStateCode()).isEqualTo("TN");
        assertThat(form.getBedrooms()).isEqualTo(4);
        assertThat(((QuoteLeadForm) model.get("quoteLeadForm")).getCountyNameValue()).isEqualTo("Knox");
        verifyNoInteractions(storage);
    }

    @Test void drainfieldModeUsesTheIntegratedFieldWorkspaceAndResult() throws Exception {
        mvc.perform(get(PATH).param("state", "GA").param("projectType", "drainfield_replacement"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-calculator-mode=\"drainfield\"")))
                .andExpect(content().string(containsString("oak-pasture-v1.webp")))
                .andExpect(content().string(containsString("Read the field.")))
                .andExpect(content().string(containsString("name=\"noClearReplacementArea\"")));

        mvc.perform(post(PATH)
                        .param("stateCode", "GA")
                        .param("projectType", "drainfield_replacement")
                        .param("bedrooms", "4")
                        .param("soilPercStatus", "failed")
                        .param("accessDifficulty", "hard")
                        .param("timeline", "this_month")
                        .param("highWaterTableOrShallowBedrock", "true")
                        .param("noClearReplacementArea", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Price the field.")))
                .andExpect(content().string(containsString("Alternative field layout or site-specific redesign likely")))
                .andExpect(content().string(containsString("Reserve-area or layout risk is the main blocker")));
    }

    @Test void tankAndPumpModesUseIntegratedPremiumWorkspaces() throws Exception {
        mvc.perform(get(PATH).param("mode", "tank_size").param("state", "GA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-calculator-mode=\"tank_size\"")))
                .andExpect(content().string(containsString("Size the tank.")))
                .andExpect(content().string(containsString("name=\"occupancyProfile\"")));
        mvc.perform(post(PATH).param("calculatorMode", "tank_size").param("stateCode", "GA")
                        .param("projectType", "new_install").param("bedrooms", "4")
                        .param("occupancyProfile", "high").param("garbageDisposal", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Georgia capacity outlook")))
                .andExpect(content().string(containsString("Conservative planning band")));

        mvc.perform(get(PATH).param("mode", "pump_schedule"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-calculator-mode=\"pump_schedule\"")))
                .andExpect(content().string(containsString("Set the rhythm.")))
                .andExpect(content().string(containsString("name=\"tankSizeGallons\"")));
        mvc.perform(post(PATH).param("calculatorMode", "pump_schedule").param("projectType", "pumping")
                        .param("tankSizeGallons", "1000").param("occupants", "5")
                        .param("usageProfile", "full_time").param("garbageDisposal", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your septic care rhythm")))
                .andExpect(content().string(containsString("About every 2 to 3 years")));
    }

    @Test void allProjectTypesMatchOriginalResultsAndRenderEveryExplanation() throws Exception {
        for (var type : ProjectType.values()) {
            var params = new org.springframework.util.LinkedMultiValueMap<String,String>();
            params.add("stateCode", "AL"); params.add("projectType", type.value()); params.add("bedrooms", "4");
            params.add("occupants", "9"); params.add("garbageDisposal", "true"); params.add("additionalKitchen", "true");
            params.add("highWaterTableOrShallowBedrock", "true"); params.add("soilPercStatus", "unknown");
            params.add("accessDifficulty", "easy"); params.add("timeline", "researching");
            params.add("recordsMode", "true"); params.add("recordSystemType", "Gravity");
            var original = mvc.perform(post("/septic-system-cost-calculator/").params(params))
                    .andExpect(status().isOk()).andReturn().getModelAndView().getModel().get("result");
            var response = mvc.perform(post(PATH).params(params)).andExpect(status().isOk())
                    .andExpect(view().name("studio/calculator")).andReturn();
            var result = (EstimatorResult) response.getModelAndView().getModel().get("result");
            assertThat(result).isEqualTo(original);
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            assertThat(html).contains(result.formattedTotalCostRange(), result.formattedBaselineLane(), result.formattedComplexSiteLane(),
                    result.pricePrecisionNote(), result.officialMinimumNote(), result.localOverrideNote(), result.costAnchorNote(),
                    "Official fees and private project costs are different layers.", "Gravity", "id=\"quote-request\"");
            for (var group : java.util.List.of(result.officialBasis(), result.heuristicAdjustments(), result.methodologyLimits(),
                    result.costDrivers(), result.checklist(), result.ruleHighlights(), result.permitPathSteps(), result.sourceLabels())) {
                for (var value : group) assertThat(html).contains(value);
            }
            assertThat(html.split("<h1", -1)).hasSize(2);
            assertThat(html).contains("calc-report-cover", "data-edit-property", "id=\"property-inputs\"");
            assertThat(html).contains("id=\"understand-estimate\"", "id=\"calculation-evidence\"",
                    "id=\"records-investigation\"", "Roane County, Tennessee", "Illustrative landscape",
                    "href=\"/design-preview/studio/work/roane/\"", "Ask about my property", "data-open-service",
                    "Records research—not a construction quote or site inspection.");
            assertThat(html.indexOf("id=\"understand-estimate\""))
                    .isLessThan(html.indexOf("id=\"records-investigation\""));
            assertThat(html.indexOf("id=\"records-investigation\""))
                    .isLessThan(html.indexOf("id=\"quote-request\""));
            assertThat(html.indexOf("id=\"result-top\"")).isLessThan(html.indexOf("id=\"cost-estimator-form\""));
            assertThat(html).doesNotContain("metric-card", "/pages.css");
        }
        verifyNoInteractions(storage);
    }

    @Test void allStatesCanCalculate() throws Exception {
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            mvc.perform(post(PATH).param("stateCode", state.stateCode()).param("bedrooms", "3"))
                    .andExpect(status().isOk()).andExpect(model().attributeExists("result"));
        }
        verifyNoInteractions(storage);
    }

    @Test void missingStateAndMalformedInputsDoNotStoreAnything() throws Exception {
        for (String state : java.util.List.of("", "ZZ")) {
            mvc.perform(post(PATH).param("stateCode", state).param("recordTankCapacity", "1200 gal").param("recordsMode", "true"))
                    .andExpect(status().isOk()).andExpect(content().string(containsString("Choose the property state")))
                    .andExpect(content().string(containsString("1200 gal")));
        }
        mvc.perform(post(PATH).param("bedrooms", "invalid")).andExpect(status().isBadRequest());
        verifyNoInteractions(storage);
    }

    @Test void quoteValidationStaysInStudioAndDoesNotSave() throws Exception {
        mvc.perform(post(PATH + "quote/").param("stateCode", "TN").param("email", "invalid"))
                .andExpect(status().isOk()).andExpect(view().name("studio/calculator"))
                .andExpect(content().string(containsString("Finish the required fields")))
                .andExpect(content().string(containsString("value=\"invalid\"")));
        verifyNoInteractions(storage);
    }

    @Test void successfulQuoteUsesSharedStorageWithMockedEffects() throws Exception {
        when(storage.saveQuoteLead(any(), any(), any(), anyString(), any())).thenReturn("preview-test-id");
        mvc.perform(post(PATH + "quote/").param("stateCode", "TN").param("fullName", "Test Person")
                .param("email", "test@example.com").param("phone", "5551234567").param("zipCode", "37901")
                .param("consentAccepted", "true"))
                .andExpect(status().isOk()).andExpect(view().name("studio/calculator"))
                .andExpect(content().string(containsString("preview-test-id")))
                .andExpect(content().string(containsString("Project details received")));
        verify(storage).saveQuoteLead(any(), any(), any(), eq("/septic-system-cost-calculator/"), any());
    }
}
