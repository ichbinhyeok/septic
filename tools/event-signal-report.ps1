param(
    [int]$Days = 7,
    [string]$StorageRoot = "./storage"
)

$ErrorActionPreference = "Stop"

$eventsRoot = Join-Path $StorageRoot "events"
if (-not (Test-Path -LiteralPath $eventsRoot)) {
    Write-Host "No events directory found at $eventsRoot"
    exit 0
}

$cutoff = (Get-Date).AddDays(-1 * $Days)
$eventFiles = Get-ChildItem -LiteralPath $eventsRoot -Recurse -Filter "*.ndjson" |
    Where-Object { $_.LastWriteTime -ge $cutoff } |
    Sort-Object FullName

if (-not $eventFiles) {
    Write-Host "No event files found for the last $Days days."
    exit 0
}

$events = foreach ($file in $eventFiles) {
    Get-Content -LiteralPath $file.FullName | Where-Object { $_.Trim() } | ForEach-Object {
        try {
            $_ | ConvertFrom-Json
        } catch {
            $null
        }
    }
}

$navClicks = $events | Where-Object { $_.eventType -eq "internal_navigation_click" }
$webVitals = $events | Where-Object { $_.eventType -eq "web_vital" }
$quoteLeads = $events | Where-Object { $_.eventType -eq "quote_form_submitted" }

Write-Host ""
Write-Host "Organic signal report, last $Days days"
Write-Host "Events: $($events.Count) | nav clicks: $($navClicks.Count) | web vitals: $($webVitals.Count) | quote leads: $($quoteLeads.Count)"

Write-Host ""
Write-Host "Top source contexts"
$navClicks |
    Group-Object sourceContext |
    Sort-Object Count -Descending |
    Select-Object -First 15 @{Name="sourceContext";Expression={$_.Name}}, Count |
    Format-Table -AutoSize

Write-Host ""
Write-Host "Top target paths"
$navClicks |
    Group-Object targetPath |
    Sort-Object Count -Descending |
    Select-Object -First 15 @{Name="targetPath";Expression={$_.Name}}, Count |
    Format-Table -AutoSize

Write-Host ""
Write-Host "Web vitals needing work"
$webVitals |
    Where-Object { $_.rating -ne "good" } |
    Group-Object metricName, rating, sourcePage |
    Sort-Object Count -Descending |
    Select-Object -First 20 @{Name="metric/rating/page";Expression={$_.Name}}, Count |
    Format-Table -AutoSize
