@import com.example.septic.data.model.SourceRecord
@param SourceRecord source

@if (source.contentVerifiedAt() != null && !source.contentVerifiedAt().isBlank())
    <span>Content reviewed: ${source.contentVerifiedAt()}</span>
@endif
@if (source.hasHttpCheck())
    <span>${source.httpCheckLabel()}: ${source.lastHttpCheckedAt()}</span>
@endif
@if (source.verificationMethod() != null && !source.verificationMethod().isBlank())
    <span>Method: ${source.verificationMethod().replace('_', ' ')}</span>
@endif
@if (source.reviewStatus() != null && source.reviewStatus().startsWith("manual_"))
    <span>Manual reachability review queued</span>
@endif
