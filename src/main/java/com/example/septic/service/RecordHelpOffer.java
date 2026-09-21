package com.example.septic.service;

/** Terms saved with each new intake; historical requests retain their original terms. */
public final class RecordHelpOffer {
    public static final String VERSION = "record-help-evidence-preview-29-research-v7";
    public static final String MATCHING_TERMS = "If field work may help, SepticPath may share the requester's name (if provided), email address, property address, stated service need, and optional mobile number with relevant local septic professionals so they can respond to this request. "
            + "Some participating professionals may compensate SepticPath for an introduction. "
            + "A mobile number is optional; if provided, it may be used for manual calls or service-specific texts about this request, but not automated or prerecorded marketing, unrelated solicitations, or resale for unrelated marketing. "
            + "Uploaded documents and private agency correspondence are not shared for matching unless the requester separately chooses to provide them. ";
    public static final String TERMS = "Submission, research, document review, and agency requests are free. "
            + "If we can provide a useful, property-matched official result, the free preview identifies the source, document scope, property match, questions it can answer, and material limitations. "
            + "Your property-specific answers and located source files are provided after the optional unlock; your own uploaded files remain yours. "
            + "You may then choose to unlock the source records and human-reviewed answers for US $29. "
            + "No upfront payment or automatic charge. Agency search, copy, and portal fees are additional "
            + "at cost and require your approval before they are incurred; those fees may apply even if no record is found or you do not unlock. "
            + MATCHING_TERMS;

    private RecordHelpOffer() {}
}
