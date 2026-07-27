param(
    [string]$RegistryPath = "data/raw/source_registry.csv",
    [string]$OutputPath = "reports/source-health.json",
    [int]$TimeoutSeconds = 20,
    [int]$ThrottleLimit = 20
)

$ErrorActionPreference = "Stop"
python tools/check_source_health.py `
    --registry $RegistryPath `
    --output $OutputPath `
    --timeout $TimeoutSeconds `
    --workers $ThrottleLimit `
    --fail-on-actionable
exit $LASTEXITCODE
