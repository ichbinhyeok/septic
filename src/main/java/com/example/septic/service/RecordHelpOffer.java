package com.example.septic.service;

/** Terms saved with each new intake; historical requests retain their original terms. */
public final class RecordHelpOffer {
    public static final String VERSION = "record-help-free-results-local-followup-v8";
    public static final String MATCHING_TERMS = "If field work may help, SepticPath may share the requester's name (if provided), email address, phone number, property address, role, stated purpose and timeframe with relevant local septic professionals so they can respond to this request. "
            + "Some participating professionals may compensate SepticPath for an introduction. "
            + "The phone number may be used for manual calls or service-specific texts about this request, but not automated or prerecorded marketing, unrelated solicitations, or resale for unrelated marketing. "
            + "Uploaded documents and private agency correspondence are not shared for matching unless the requester separately chooses to provide them. ";
    public static final String TERMS = "Submission, research, document review, agency requests, and delivery of the records and answers we can provide are free. "
            + "We explain the source, property match, findings and material limitations; availability and agency response times vary. Your own uploaded files remain yours. "
            + "No card, paid unlock or automatic charge. Agency search, copy, and portal fees are additional "
            + "at cost and require your approval before they are incurred; those fees may apply even if no record is found. "
            + "Local professionals charge separately for their work. An introduction does not guarantee availability, price or response time. "
            + MATCHING_TERMS;

    private RecordHelpOffer() {}
}
