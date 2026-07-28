package com.example.septic.web;

import java.util.List;

public record CountyAcquisitionProfileView(
        String countyKey,
        String agency,
        String channelLabel,
        String feeLabel,
        String timingLabel,
        String userCheckpoint,
        String recipientEmail,
        String phone,
        String emailSubject,
        String preparationNote,
        String requestTemplate,
        List<CountyAcquisitionFieldView> fields,
        List<String> requestedDocuments
) {
    public boolean officialFieldPackVerified() {
        return switch (countyKey) {
            case "VA::prince-william-county",
                 "TN::hamilton-county",
                 "NC::alamance-county",
                 "TN::knox-county",
                 "NC::lincoln-county",
                 "GA::dekalb-county",
                 "TN::blount-county",
                 "MD::st-marys-county",
                 "NY::suffolk-county",
                 "AZ::maricopa-county",
                 "NC::brunswick-county",
                 "NC::forsyth-county",
                 "TX::denton-county",
                 "NJ::gloucester-county",
                 "MD::prince-georges-county",
                 "CO::adams-county",
                 "TN::wilson-county",
                 "TN::montgomery-county" -> true;
            default -> false;
        };
    }

    public boolean generatedRequestEnabled() {
        // County-authored forms and portals remain the submission authority.
        // Do not enable locally authored request text without documented agency acceptance.
        return false;
    }

    public String acquisitionMethod() {
        return switch (countyKey) {
            case "VA::prince-william-county", "TN::hamilton-county", "MD::st-marys-county",
                 "AZ::maricopa-county", "NC::brunswick-county",
                 "MD::prince-georges-county", "CO::adams-county" -> "official_search";
            case "NC::alamance-county", "TX::denton-county" -> "official_pdf";
            case "TX::tarrant-county", "NC::lincoln-county", "GA::dekalb-county",
                 "TN::blount-county", "TN::knox-county", "NJ::gloucester-county",
                 "TN::wilson-county", "TN::montgomery-county" -> "official_portal";
            case "NC::forsyth-county" -> "official_contact_form";
            case "NY::suffolk-county", "TN::sevier-county", "NC::guilford-county" -> "official_phone";
            case "TX::brazoria-county", "OH::mahoning-county" -> "official_contact";
            default -> "official_route";
        };
    }

    public String methodHeading() {
        return switch (acquisitionMethod()) {
            case "official_search" -> "Search the official county system";
            case "official_pdf" -> "Complete the county's official PDF";
            case "official_portal" -> "Submit through the official request portal";
            case "official_contact_form" -> "Use the official Environmental Health contact form";
            case "official_contact" -> "Contact the official OSSF office";
            case "official_phone" -> "Call with the county's required property clues ready";
            case "official_route_blocked" -> "The county's required document link is currently broken";
            default -> "Use the current official route";
        };
    }

    public String methodInstruction() {
        if ("TX::tarrant-county".equals(countyKey)) {
            return "Use the accessible OSSF office page to confirm whether the county, a contract city, or an ETJ owns the file. JustFOIA is an optional formal-request fallback and may be access-restricted.";
        }
        if ("NC::lincoln-county".equals(countyKey)) {
            return "The accessible Environmental Health page confirms that a septic-record request needs the property address and/or parcel number. Use its phone contact first; open NextRequest only when the portal is reachable.";
        }
        if ("TN::blount-county".equals(countyKey)) {
            return "Use the county Records Center, choose Developmental Services Requests, and carry the verified SSDS fields prepared here into that form.";
        }
        if ("TN::knox-county".equals(countyKey)) {
            return "The county's linked PDF is broken, but its county-branded public Jotform is live. Prepare the exact live fields here, then complete the final Submit step in that form.";
        }
        if ("AZ::maricopa-county".equals(countyKey)) {
            return "The free route only publishes an agreement, reCAPTCHA, and Submit step before the search. Open that route first; expand the paid research pack here only if the free search is empty or incomplete.";
        }
        if ("MD::st-marys-county".equals(countyKey)) {
            return "Search the county's current replacement GIS by address or Tax ID first. Open the official PIA fallback pack only when the mapped Health Department records are missing or incomplete.";
        }
        if ("NJ::gloucester-county".equals(countyKey)) {
            return "Use the county's preferred OPRA form. Transfer the verified requester, property, certification, delivery, record-description, and electronic-signature fields; reCAPTCHA and final submission remain on the county site.";
        }
        if ("MD::prince-georges-county".equals(countyKey)) {
            return "Continue as Guest and choose the verified address or application-number search. Use Momentum only if the Health Department information request is still needed.";
        }
        if ("CO::adams-county".equals(countyKey)) {
            return "Search by address, parcel, owner, or permit number and check both the Before 2023 and 2023 & After layers before opening a new permit route.";
        }
        if ("TN::wilson-county".equals(countyKey) || "TN::montgomery-county".equals(countyKey)) {
            return "Transfer the verified TDEC Formstack fields, choose Division of Water Resources and the correct county, then complete any upload, signature, citizenship proof, and final submission on TDEC.";
        }
        if ("TN::sevier-county".equals(countyKey)) {
            return "TDEC directs Sevier users to the county. The county page is 403 and its old indexed PDF is now 404, so call for the current file-search route instead of using a recreated form.";
        }
        if ("NC::guilford-county".equals(countyKey)) {
            return "Call 336-641-7613 between 8 a.m. and 10 a.m. for system type and location when an updated file exists; use the county records portal only when copies are needed.";
        }
        if ("OH::mahoning-county".equals(countyKey)) {
            return "The county accepts public-record requests by email but does not publish a dedicated septic form. Carry the property facts into your own factual message.";
        }
        return switch (acquisitionMethod()) {
            case "official_search" -> "Use the verified property identifiers shown here, open the official search, and return to record what the search produced.";
            case "official_pdf" -> "Use the county PDF itself. The checklist helps collect verified fields, but it does not replace, sign, or submit the official document.";
            case "official_portal" -> "Open the county portal, complete its current fields there, and save the confirmation or request number before returning.";
            case "official_contact_form" -> "The county publishes a general Environmental Health form with a Septic category, not a dedicated historical-record form. Submit only the factual question you intend to ask.";
            case "official_contact" -> "The county publishes an OSSF contact but not a dedicated historical-record intake. Use the official contact without a locally generated request body.";
            case "official_phone" -> "The county says homeowners should call with the property tax-map number, approximate original construction year, and subdivision map/lot when available.";
            case "official_route_blocked" -> "The official page identifies a PDF-and-email process, but the linked PDF is unavailable. Use the verified county email or phone and do not use a recreated form.";
            default -> "Follow the official route without substituting locally generated wording.";
        };
    }

    public boolean officialRecipientVerified() {
        return switch (countyKey) {
            case "NC::alamance-county",
                 "MD::st-marys-county",
                 "TX::brazoria-county",
                 "OH::mahoning-county" -> true;
            default -> false;
        };
    }

    public boolean officialUsesPropertyAddress() {
        return switch (countyKey) {
            case "VA::prince-william-county",
                 "TX::tarrant-county",
                 "TN::hamilton-county",
                 "NC::alamance-county",
                 "NC::lincoln-county",
                 "TN::blount-county",
                 "MD::st-marys-county",
                 "NC::brunswick-county",
                 "NC::forsyth-county",
                 "NJ::gloucester-county" -> true;
            default -> false;
        };
    }

    public boolean officialUsesParcelIdentifier() {
        return switch (countyKey) {
            case "VA::prince-william-county",
                 "NC::alamance-county",
                 "NC::lincoln-county",
                 "MD::st-marys-county",
                 "NC::brunswick-county",
                 "NY::suffolk-county" -> true;
            default -> false;
        };
    }

    public boolean officialPropertyAddressRequired() {
        return switch (countyKey) {
            case "TN::hamilton-county", "NJ::gloucester-county" -> true;
            default -> false;
        };
    }

    public boolean officialParcelIdentifierRequired() {
        return "NY::suffolk-county".equals(countyKey);
    }

    public boolean requiresAddressOrParcel() {
        return switch (countyKey) {
            case "VA::prince-william-county", "NC::lincoln-county",
                 "MD::st-marys-county", "NC::brunswick-county" -> true;
            default -> false;
        };
    }

    public boolean fallbackFieldPack() {
        return "AZ::maricopa-county".equals(countyKey) || "MD::st-marys-county".equals(countyKey);
    }

    public String fallbackFieldPackHeading() {
        return switch (countyKey) {
            case "AZ::maricopa-county" -> "Paid research fallback — only after the free search";
            case "MD::st-marys-county" -> "Official PIA fallback — only if GIS is incomplete";
            default -> "Official fallback fields";
        };
    }

    public String fallbackFieldPackInstruction() {
        return switch (countyKey) {
            case "AZ::maricopa-county" -> "Opening this section activates the county's paid-form requirements. Standard research is $30; expedited research is $60.";
            case "MD::st-marys-county" -> "Opening this section activates the county PDF requirements. The GIS search itself does not require these requester details.";
            default -> "Use these fields only for the fallback route.";
        };
    }

    public boolean requiresOfficialDocumentSelection() {
        return "NC::alamance-county".equals(countyKey);
    }

    public String preparationSummary() {
        if (officialFieldPackVerified()) {
            return "We separate required and optional county fields, keep the confirmed inputs on this device, and make every value individually copyable.";
        }
        return "We preserve the property facts, show the current official route, published cost and timing, and flag any office or jurisdiction decision that still needs confirmation.";
    }

    public String userOnlyAction() {
        return userCheckpoint;
    }

    public String manualCompletionBoundary() {
        return "Review the prepared details, complete any county-only login, signature, CAPTCHA, or payment-consent step that appears, then submit in your own name.";
    }

    public String preparationSheetLabel() {
        return switch (acquisitionMethod()) {
            case "official_pdf" -> "Official PDF transfer sheet";
            case "official_search" -> "Official search carry sheet";
            case "official_portal" -> "Official portal transfer sheet";
            case "official_contact_form" -> "Official contact-form carry sheet";
            case "official_phone" -> "County call sheet";
            case "official_contact" -> "Official contact carry sheet";
            case "official_route_blocked" -> "Verified fallback carry sheet";
            default -> "Official-route preparation sheet";
        };
    }

    public String preparationSheetInstruction() {
        return switch (acquisitionMethod()) {
            case "official_pdf" -> "Keep this beside the county PDF and transfer each prepared value into the matching official field.";
            case "official_search" -> "Keep this beside the county search so the address, parcel ID, and search clues stay in one place.";
            case "official_portal" -> "Keep this beside the county portal and move through the prepared values in field order.";
            case "official_contact_form" -> "Keep this beside the county form and transfer only the factual details that match its current fields.";
            case "official_phone" -> "Keep this open during the call so every published property clue is ready when the county asks.";
            case "official_contact", "official_route_blocked" -> "Keep the verified property facts and official fallback together while you contact the county.";
            default -> "Keep the prepared property facts beside the county's current official route.";
        };
    }

    public boolean supportsPreparedOfficialPdf() {
        return "NC::alamance-county".equals(countyKey) || "TX::denton-county".equals(countyKey);
    }

    public String preparedPdfRemainder() {
        return switch (countyKey) {
            case "NC::alamance-county" -> "The selected record types are marked in the original county PDF. Review the form, then email it to the address printed on the PDF.";
            case "TX::denton-county" -> "The delivery and notification boxes are marked in the original county PDF. Your signature remains blank for you to complete.";
            default -> "";
        };
    }

    public boolean hasEmail() {
        return recipientEmail != null && !recipientEmail.isBlank();
    }

    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }

    public List<CountyAcquisitionFieldView> requiredFields() {
        return fields.stream()
                .filter(CountyAcquisitionFieldView::required)
                .toList();
    }

    public List<CountyAcquisitionFieldView> optionalFields() {
        return fields.stream()
                .filter(field -> !field.required())
                .toList();
    }
}
