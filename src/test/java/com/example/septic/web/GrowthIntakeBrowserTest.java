package com.example.septic.web;

import com.example.septic.service.ClosingRiskNotificationService;
import com.example.septic.service.ClosingRiskRequestLimiter;
import com.example.septic.service.LeadStorageService;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "app.studio-preview.enabled=false", "app.storage.root=./build/growth-intake-browser-storage"})
class GrowthIntakeBrowserTest {
    @LocalServerPort int port;
    @MockitoBean LeadStorageService storage;
    @MockitoBean ClosingRiskNotificationService notifications;
    @MockitoBean ClosingRiskRequestLimiter limiter;

    @Test void rejectsMalformedPhoneBeforePostingAndAcceptsFormattedNumber() {
        when(limiter.allow(any())).thenReturn(true);
        when(storage.saveClosingRiskRequest(any(), anyString(), any())).thenReturn("qa-growth-only");
        WebDriver browser = browser();
        try {
            browser.get(base() + "/offer-prep-septic-file-check/?intent=location&state=NC");
            fill(browser, "propertyAddress", "123 Fictional Lane, Example NC 28000");
            fill(browser, "email", "qa@example.com");
            fill(browser, "phone", "abcdefghij");
            new Select(browser.findElement(By.name("transactionRole"))).selectByValue("owner");
            click(browser, By.cssSelector("label[for=field-consentAccepted]"));
            JavascriptExecutor js = (JavascriptExecutor) browser;
            js.executeScript("window.qaEvents=[]; window.gtag=(...args)=>window.qaEvents.push(args);");
            submit(browser);
            new WebDriverWait(browser, Duration.ofSeconds(10)).until(d -> d.findElement(By.cssSelector("[data-intake-client-errors]")).isDisplayed());
            assertThat(browser.findElement(By.cssSelector("[data-intake-client-errors]")).getText()).contains("Phone number", "10–15 digits");
            assertThat(((Number) js.executeScript("return qaEvents.filter(e=>e[1]==='record_help_invalid_phone').length")).intValue()).isEqualTo(1);
            assertThat(((Number) js.executeScript("return qaEvents.filter(e=>e[1]==='record_help_form_submit_attempted'&&e[2].validation_state==='blocked').length")).intValue()).isEqualTo(1);
            assertThat(js.executeScript("return JSON.stringify(qaEvents)").toString()).doesNotContain("qa@example.com", "Fictional Lane", "abcdefghij");
            verify(storage, never()).saveClosingRiskRequest(any(), anyString(), any());
            verifyNoInteractions(notifications);
            browser.findElement(By.name("phone")).clear();
            fill(browser, "phone", "+1 (704) 555-0100");
            assertThat(browser.findElement(By.cssSelector("[data-intake-client-errors]")).isDisplayed()).isFalse();
            submit(browser);
            new WebDriverWait(browser, Duration.ofSeconds(10)).until(d -> !d.findElements(By.cssSelector("[data-intake-success]")).isEmpty()
                    && d.findElement(By.cssSelector("[data-intake-success]")).getText().contains("qa-growth-only"));
            assertThat(browser.findElement(By.cssSelector("[data-intake-success]")).getText()).contains("qa-growth-only");
        } finally { browser.quit(); }
    }

    @Test void missingFileModeIsVisibleAndSwitchingToResearchRemovesUploadRequirement() {
        WebDriver browser = browser();
        try {
            browser.get(base() + "/offer-prep-septic-file-check/?mode=review");
            submit(browser);
            new WebDriverWait(browser, Duration.ofSeconds(10)).until(d -> d.findElement(By.cssSelector("[data-intake-client-errors]")).isDisplayed());
            assertThat(browser.findElement(By.cssSelector("[data-intake-client-errors]")).getText()).contains("Source files");
            click(browser, By.cssSelector(".intake-modes label:has([value=research])"));
            assertThat(((JavascriptExecutor) browser).executeScript("return document.querySelector('[name=documents]').validity.valid")).isEqualTo(true);
            assertThat(browser.findElement(By.cssSelector("[data-intake-client-errors]")).getText()).doesNotContain("Source files:");
            verify(storage, never()).saveClosingRiskRequest(any(), anyString(), any());
            verifyNoInteractions(notifications);
        } finally { browser.quit(); }
    }

    @Test void finderContextPrefillsPurposeWithoutClaimingRoleOrOverwritingAnExplicitGoal() {
        WebDriver browser = browser();
        try {
            browser.get(base() + "/septic-record-finder/");
            ((JavascriptExecutor) browser).executeScript("sessionStorage.setItem('septicpath_record_help_context',JSON.stringify({savedAt:Date.now(),address:'123 Fictional Lane',stateCode:'NC',countyName:'Iredell',purpose:'location',status:'route_ready'}));");
            browser.get(base() + "/offer-prep-septic-file-check/?intent=missing&source=%2Fseptic-record-finder%2F");
            assertThat(new Select(browser.findElement(By.name("helpPurpose"))).getFirstSelectedOption().getAttribute("value")).isEqualTo("location");
            assertThat(new Select(browser.findElement(By.name("transactionRole"))).getFirstSelectedOption().getAttribute("value")).isEmpty();
            assertThat(new Select(browser.findElement(By.name("recordStatus"))).getFirstSelectedOption().getAttribute("value")).isEqualTo("not_started");
            browser.get(base() + "/offer-prep-septic-file-check/?intent=building&state=NC");
            assertThat(new Select(browser.findElement(By.name("helpPurpose"))).getFirstSelectedOption().getAttribute("value")).isEqualTo("building");
        } finally { browser.quit(); }
    }

    private String base() { return "http://localhost:" + port; }
    private WebDriver browser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,1000");
        return new ChromeDriver(options);
    }
    private void fill(WebDriver driver, String name, String value) { driver.findElement(By.name(name)).sendKeys(value); }
    private void click(WebDriver driver, By selector) {
        var target = driver.findElement(selector);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',behavior:'instant'});", target);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                "const r=arguments[0].getBoundingClientRect();const h=document.elementFromPoint(r.x+r.width/2,r.y+r.height/2);return h&&arguments[0].contains(h);", target)));
        target.click();
    }
    private void submit(WebDriver driver) {
        var button = driver.findElement(By.cssSelector("[data-intake-form] [type=submit]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',behavior:'instant'});", button);
        button.click();
    }
}
