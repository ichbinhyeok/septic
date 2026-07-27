[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolRoot = Join-Path $projectRoot 'build\ocr-validation\tools'
$resultPath = Join-Path $projectRoot 'build\ocr-validation\ocr-results.json'

New-Item -ItemType Directory -Force -Path $toolRoot | Out-Null
if (-not (Test-Path (Join-Path $toolRoot 'node_modules\tesseract.js'))) {
    npm install --prefix $toolRoot --no-save --no-package-lock tesseract.js@7.0.0 sharp@0.34.3
    if ($LASTEXITCODE -ne 0) {
        throw 'Could not install the pinned local OCR validation tools.'
    }
}

node (Join-Path $PSScriptRoot 'validate-septic-ocr.mjs') $toolRoot $resultPath
if ($LASTEXITCODE -ne 0) {
    throw 'OCR image recognition benchmark failed.'
}

& (Join-Path $projectRoot 'gradlew.bat') test `
    --tests com.example.septic.OcrBenchmarkReplayTests `
    "-PocrBenchmarkResults=$resultPath"
if ($LASTEXITCODE -ne 0) {
    throw 'OCR-to-property-field replay benchmark did not meet its quality gates.'
}

Write-Output "OCR benchmark results: $resultPath"
Write-Output "Pipeline summary: $(Join-Path $projectRoot 'build\ocr-validation\pipeline-summary.json')"
