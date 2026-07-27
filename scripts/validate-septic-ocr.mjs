import fs from "node:fs/promises";
import path from "node:path";
import { createRequire } from "node:module";

const [toolRoot, resultPath] = process.argv.slice(2);
if (!toolRoot || !resultPath) {
    throw new Error("Usage: node validate-septic-ocr.mjs <tool-root> <result-path>");
}

const requireFromTools = createRequire(path.join(path.resolve(toolRoot), "package.json"));
const sharp = requireFromTools("sharp");
const { createWorker } = requireFromTools("tesseract.js");

const fixtures = [
    {
        name: "permit-bedroom-flow",
        purpose: "bedrooms",
        stateCode: "TN",
        lines: [
            "SEPTIC SYSTEM FINAL APPROVAL",
            "Permit Number: SSDS-2026-1842",
            "Approved for 4 bedrooms.",
            "Design flow: 600 GPD.",
            "Final approval date: 07/18/2026."
        ],
        expected: {
            permit_number: "SSDS-2026-1842",
            approved_bedrooms: "4",
            design_flow: "600 GPD",
            approval_date: "07/18/2026",
            final_approval: "Dated"
        }
    },
    {
        name: "aowe-low-pressure",
        purpose: "bedrooms",
        stateCode: "NC",
        lines: [
            "AUTHORIZATION FOR WASTEWATER SYSTEM",
            "Permit ID: AOWE-24-7811",
            "Number of Bedrooms: 3",
            "Daily Design Flow: 360 GPD",
            "System Type: Low Pressure Pipe",
            "Final approval date: 2026-06-03"
        ],
        expected: {
            permit_number: "AOWE-24-7811",
            approved_bedrooms: "3",
            design_flow: "360 GPD",
            system_type: "Low-pressure pipe",
            approval_date: "2026-06-03",
            final_approval: "Dated"
        }
    },
    {
        name: "gravity-tank-record",
        purpose: "repair",
        stateCode: "OR",
        lines: [
            "EXISTING SYSTEM EVALUATION",
            "Record Number: OR-ESER-44819",
            "Existing gravity system.",
            "Septic tank capacity: 1000 gallons.",
            "Previous repair dated 04/11/2019.",
            "The record drawing shows the installed system."
        ],
        expected: {
            permit_number: "OR-ESER-44819",
            system_type: "Gravity system",
            tank_capacity: "1000 gallons",
            repair_history: "Mentioned",
            layout: "Mentioned"
        }
    },
    {
        name: "mound-record-drawing",
        purpose: "location",
        stateCode: "WA",
        lines: [
            "ON-SITE SEWAGE RECORD DRAWING",
            "Permit No: KC-2025-9914",
            "Bedrooms designed for: 5",
            "Operational Capacity: 600 gals/day",
            "Installed mound system.",
            "The record drawing shows the primary and reserve area."
        ],
        expected: {
            permit_number: "KC-2025-9914",
            approved_bedrooms: "5",
            design_flow: "600 GPD",
            system_type: "Mound system",
            layout: "Mentioned",
            reserve_area: "Mentioned"
        }
    },
    {
        name: "aerobic-certification",
        purpose: "buying",
        stateCode: "CA",
        lines: [
            "EXISTING SYSTEM CERTIFICATION",
            "Permit No: OWTS-26-4408",
            "Approved Bedrooms: 4",
            "System Type: Aerobic Treatment Unit",
            "Septic Tank Volume: 1250 gal.",
            "Inspection date: 06/24/2026"
        ],
        expected: {
            permit_number: "OWTS-26-4408",
            approved_bedrooms: "4",
            system_type: "Aerobic treatment unit",
            tank_capacity: "1250 gallons",
            approval_date: "06/24/2026",
            final_approval: "Dated"
        }
    },
    {
        name: "drip-repair",
        purpose: "repair",
        stateCode: "CO",
        lines: [
            "REPAIR PERMIT RECORD",
            "Permit Number: OWTS-R-10442",
            "System Type: Drip Dispersal",
            "Tank capacity: 1000 gallons",
            "Repair permit number R-10442 was issued."
        ],
        expected: {
            permit_number: "OWTS-R-10442",
            system_type: "Drip dispersal",
            tank_capacity: "1000 gallons",
            repair_history: "Mentioned"
        }
    },
    {
        name: "owner-conventional",
        purpose: "owner",
        stateCode: "GA",
        lines: [
            "COUNTY ENVIRONMENTAL HEALTH RECORD",
            "Record No: EH-2024-1109",
            "System Type: Conventional Gravity",
            "Approved bedrooms: 4"
        ],
        expected: {
            permit_number: "EH-2024-1109",
            system_type: "Conventional system",
            approved_bedrooms: "4"
        }
    },
    {
        name: "maximum-aerobic",
        purpose: "bedrooms",
        stateCode: "AZ",
        lines: [
            "ONSITE WASTEWATER APPLICATION",
            "Application ID: APP-260077",
            "Maximum 6 bedrooms",
            "Maximum daily flow: 900 GPD",
            "Proposed aerobic treatment unit system."
        ],
        expected: {
            permit_number: "APP-260077",
            approved_bedrooms: "6",
            design_flow: "900 GPD",
            system_type: "Aerobic treatment unit"
        }
    },
    {
        name: "location-only",
        purpose: "location",
        stateCode: "IN",
        lines: [
            "SEPTIC AS-BUILT",
            "Record ID: OSS-88219",
            "As-built shows the tank, distribution box, and drain field.",
            "Reserve area shown southeast of the primary field."
        ],
        expected: {
            permit_number: "OSS-88219",
            layout: "Mentioned",
            reserve_area: "Mentioned"
        }
    },
    {
        name: "negative-approval",
        purpose: "bedrooms",
        stateCode: "TN",
        lines: [
            "INCOMPLETE PROPERTY FILE",
            "Permit Number: SSDS-1102",
            "Approved for 3 bedrooms.",
            "No final approval or design flow document was included."
        ],
        expected: {
            permit_number: "SSDS-1102",
            approved_bedrooms: "3"
        }
    }
];

const variants = [
    { name: "clean", transform: image => image.png() },
    {
        name: "low-contrast",
        transform: image => image.linear(0.55, 105).jpeg({ quality: 68, chromaSubsampling: "4:4:4" })
    },
    {
        name: "skewed",
        transform: image => image.rotate(1.1, { background: "#ffffff" }).jpeg({ quality: 72 })
    },
    {
        name: "faint-low-resolution",
        transform: image => image
            .linear(0.48, 118)
            .resize({ width: 760 })
            .blur(0.5)
            .jpeg({ quality: 52 })
    }
];

function escapeXml(value) {
    return value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
}

function fixtureSvg(fixture) {
    const rows = fixture.lines.map((line, index) =>
        `<text x="115" y="${185 + index * 100}" font-size="${index === 0 ? 42 : 35}" ` +
        `font-weight="${index === 0 ? 700 : 400}">${escapeXml(line)}</text>`
    ).join("");
    return Buffer.from(`
        <svg width="1400" height="1800" xmlns="http://www.w3.org/2000/svg">
            <rect width="1400" height="1800" fill="#ffffff"/>
            <rect x="70" y="65" width="1260" height="1660" fill="none" stroke="#444" stroke-width="3"/>
            <g font-family="Arial, Helvetica, sans-serif" fill="#151515">${rows}</g>
            <line x1="110" y1="245" x2="1290" y2="245" stroke="#555" stroke-width="2"/>
        </svg>
    `);
}

await fs.mkdir(path.dirname(resultPath), { recursive: true });
const worker = await createWorker("eng", 1, {
    cachePath: path.join(path.dirname(resultPath), "tessdata-cache"),
    logger: () => {}
});
const samples = [];
try {
    for (const fixture of fixtures) {
        for (const variant of variants) {
            const base = sharp(fixtureSvg(fixture), { density: 200 }).greyscale();
            const image = await variant.transform(base).toBuffer();
            const startedAt = Date.now();
            const recognition = await worker.recognize(image);
            samples.push({
                fixture: fixture.name,
                variant: variant.name,
                purpose: fixture.purpose,
                stateCode: fixture.stateCode,
                expected: fixture.expected,
                recognizedText: recognition.data.text,
                meanConfidence: Number(recognition.data.confidence.toFixed(2)),
                durationMs: Date.now() - startedAt
            });
            process.stdout.write(".");
        }
    }
} finally {
    await worker.terminate();
}

const output = {
    generatedAt: new Date().toISOString(),
    engine: "tesseract.js 7.0.0 (Tesseract WebAssembly)",
    fixtureCount: fixtures.length,
    variantCount: variants.length,
    sampleCount: samples.length,
    variants: variants.map(variant => variant.name),
    samples
};
await fs.writeFile(resultPath, JSON.stringify(output, null, 2));
process.stdout.write(`\nGenerated ${samples.length} OCR samples at ${resultPath}\n`);
