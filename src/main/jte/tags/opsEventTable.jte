@import com.example.septic.web.EventAnalyticsRow
@param String heading
@param String description
@param java.util.List<EventAnalyticsRow> rows = java.util.List.of()
@param String emptyLabel

<section class="ops-event-table-section">
    <div class="ops-event-table-section__head">
        <div>
            <h3>${heading}</h3>
            <p>${description}</p>
        </div>
    </div>
    @if (rows.isEmpty())
        <p class="ops-event-table-section__empty">${emptyLabel}</p>
    @else
        <div class="ops-event-table-wrap">
            <table class="ops-event-table">
                <thead>
                    <tr>
                        <th scope="col">Page</th>
                        <th scope="col">CTA / action</th>
                        <th scope="col">Context</th>
                        <th scope="col">7d</th>
                        <th scope="col">28d</th>
                        <th scope="col">Last seen</th>
                    </tr>
                </thead>
                <tbody>
                    @for (var row : rows)
                        <tr>
                            <td><code>${row.sourcePage()}</code></td>
                            <td>${row.detail()}</td>
                            <td><code>${row.sourceContext()}</code></td>
                            <td>${row.lastSevenDays()}</td>
                            <td>${row.lastTwentyEightDays()}</td>
                            <td>${row.lastSeenAt()}</td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    @endif
</section>
