$ErrorActionPreference = "Stop"

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$corpusDirectory = Join-Path $repositoryRoot "src\test\resources\septic-document-corpus"
$manifestPath = Join-Path $corpusDirectory "corpus.csv"
$entries = Import-Csv -LiteralPath $manifestPath
$downloaded = 0
$failed = @()

foreach ($entry in $entries) {
    $destination = Join-Path $corpusDirectory ($entry.id + ".pdf")
    try {
        Invoke-WebRequest `
            -UseBasicParsing `
            -Uri $entry.url `
            -Headers @{ "User-Agent" = "SepticPath document validation/1.0" } `
            -OutFile $destination `
            -TimeoutSec 60

        $signature = [System.IO.File]::ReadAllBytes($destination)
        if ($signature.Length -lt 4 `
                -or $signature[0] -ne 0x25 `
                -or $signature[1] -ne 0x50 `
                -or $signature[2] -ne 0x44 `
                -or $signature[3] -ne 0x46) {
            Remove-Item -LiteralPath $destination
            throw "Response was not a PDF"
        }
        $downloaded++
        Write-Output ("OK " + $entry.id)
    } catch {
        $failed += [PSCustomObject]@{
            id = $entry.id
            url = $entry.url
            error = $_.Exception.Message
        }
        Write-Warning ("FAILED " + $entry.id + ": " + $_.Exception.Message)
    }
}

Write-Output ("Downloaded {0} of {1} official documents." -f $downloaded, $entries.Count)
if ($failed.Count -gt 0) {
    $failed | Format-Table -Wrap
    exit 1
}
