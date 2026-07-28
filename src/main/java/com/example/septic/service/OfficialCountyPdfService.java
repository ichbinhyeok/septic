package com.example.septic.service;

import com.example.septic.web.OfficialCountyPdfForm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

@Service
public class OfficialCountyPdfService {
    private static final int MAX_PDF_BYTES = 5_000_000;
    private static final Map<String, URI> OFFICIAL_PDFS = Map.of(
            "NC::alamance-county",
            URI.create("https://eh.alamancecountync.gov/wp-content/uploads/sites/27/2019/06/Information-Request-Edited-Form.pdf"),
            "TX::denton-county",
            URI.create("https://www.dentoncounty.gov/DocumentCenter/View/10774/Public-Information-Request-Form-PDF")
    );

    private final HttpClient httpClient;

    public OfficialCountyPdfService() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    OfficialCountyPdfService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public boolean supports(String countyKey) {
        return OFFICIAL_PDFS.containsKey(countyKey);
    }

    public PreparedPdf prepare(OfficialCountyPdfForm form) throws IOException, InterruptedException {
        String countyKey = form == null ? "" : safe(form.countyKey(), 80);
        URI officialPdf = OFFICIAL_PDFS.get(countyKey);
        if (officialPdf == null) {
            throw new IllegalArgumentException("This county does not have a verified fillable official PDF.");
        }

        HttpRequest request = HttpRequest.newBuilder(officialPdf)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/pdf")
                .header("User-Agent", "SepticPath/1.0 official-form-preparation")
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("The county PDF is currently unavailable.");
        }
        byte[] source = response.body();
        if (source.length == 0 || source.length > MAX_PDF_BYTES) {
            throw new IOException("The county PDF response was empty or too large.");
        }

        Map<String, String> values = countyValues(form);
        try (PDDocument document = Loader.loadPDF(source);
             ByteArrayOutputStream output = new ByteArrayOutputStream(source.length + 32_000)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IOException("The county PDF is no longer fillable.");
            }
            acroForm.setNeedAppearances(true);
            values.forEach((fieldName, value) -> setIfPresent(acroForm, fieldName, value));
            document.save(output);
            return new PreparedPdf(output.toByteArray(), fileName(countyKey));
        }
    }

    Map<String, String> countyValues(OfficialCountyPdfForm form) {
        Map<String, String> input = form.fields() == null ? Map.of() : form.fields();
        Map<String, String> values = new LinkedHashMap<>();
        if ("NC::alamance-county".equals(form.countyKey())) {
            put(values, "Requesters Name", input.get("requesterName"));
            put(values, "Requesters Mailing Address 1", input.get("requesterMailingAddress"));
            put(values, "Requesters Mailing Address 2", input.get("requesterMailingAddress2"));
            put(values, "Email Address", input.get("requesterEmail"));
            put(values, "Fax Number", input.get("requesterFax"));
            put(values, "Phone Number", input.get("requesterPhone"));
            put(values, "1 GPIN Parcel ID Number", form.parcel());
            put(values, "2 Old Tax Map Number", input.get("oldTaxMap"));
            put(values, "3 Subdivision Name", input.get("subdivision"));
            put(values, "SD Lot Number", input.get("lotNumber"));
            put(values, "4  Property Address", form.address());
            put(values, "5  Directions to the Property", input.get("directions"));
            put(values, "6  Present Owner of the Property", input.get("owner"));
            String previousOwners = safe(input.get("previousOwner"), 450);
            if (!previousOwners.isBlank()) {
                int split = Math.min(100, previousOwners.length());
                put(values, "It is important to provide as much information as possible 1",
                        previousOwners.substring(0, split));
                if (split < previousOwners.length()) {
                    put(values, "It is important to provide as much information as possible 2",
                            previousOwners.substring(split).stripLeading());
                }
            }
            put(values, "8  Date septic was installed", input.get("yearInstalled"));
            put(values, "9  Date home was built", input.get("yearBuilt"));
            putSelection(values, "Copy of well permit", input.get("wellPermitRequested"));
            putSelection(values, "Copy of septic permit", input.get("septicPermitRequested"));
            putSelection(values, "Copy of existing water sample results", input.get("waterSampleRequested"));
            putSelection(values, "Copy of soil evaluation", input.get("soilEvaluationRequested"));
        } else if ("TX::denton-county".equals(form.countyKey())) {
            put(values, "Requestor’s Name", input.get("requesterName"));
            put(values, "Date", input.get("requestDate"));
            put(values, "Mailing Address", input.get("requesterMailingAddress"));
            put(values, "Phone", input.get("requesterPhone"));
            put(values, "Email Address", input.get("requesterEmail"));
            String request = safe(input.get("specificInformation"), 450);
            for (int index = 0; index < 5 && !request.isBlank(); index += 1) {
                int split = Math.min(90, request.length());
                put(values, String.valueOf(index + 1), request.substring(0, split));
                request = request.substring(split).stripLeading();
            }
            String deliveryChoice = safe(input.get("deliveryChoice"), 80);
            String notificationChoice = safe(input.get("notificationChoice"), 80);
            if ("Receive copies and pick them up".equals(deliveryChoice)) {
                put(values, "Check Box1", "Yes");
                put(values, "Check Box2", "Yes");
                put(values, "Email".equals(notificationChoice) ? "Check Box3" : "Check Box4", "Yes");
            } else if ("Inspect originals".equals(deliveryChoice)) {
                put(values, "Check Box5", "Yes");
                put(values, "Email".equals(notificationChoice) ? "Check Box6" : "Check Box7", "Yes");
            }
        }
        return values;
    }

    private void putSelection(Map<String, String> values, String fieldName, String selection) {
        if ("Request this record".equals(selection)) {
            values.put(fieldName, "X");
        }
    }

    private void put(Map<String, String> values, String fieldName, String value) {
        String safeValue = safe(value, 450);
        if (!safeValue.isBlank()) {
            values.put(fieldName, safeValue);
        }
    }

    private void setIfPresent(PDAcroForm acroForm, String fieldName, String value) {
        PDField field = acroForm.getField(fieldName);
        if (field == null) {
            return;
        }
        try {
            field.setValue(value);
        } catch (IOException | IllegalArgumentException ignored) {
            // Preserve the official PDF even if a county changes one individual field.
        }
    }

    private String safe(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String normalized = value.replace('\u0000', ' ').strip();
        return normalized.length() <= maxLength ? normalized : normalized.substring(0, maxLength);
    }

    private String fileName(String countyKey) {
        return countyKey.toLowerCase()
                .replace("::", "-")
                .replaceAll("[^a-z0-9-]", "")
                + "-official-form-prepared.pdf";
    }

    public record PreparedPdf(byte[] bytes, String fileName) {}
}
