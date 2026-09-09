#!/usr/bin/env python3
"""Build a branded SepticPath record summary and an annotated permit page.

The case-specific facts and annotation coordinates live in a JSON file so the
same report format can be reused without putting customer details in source.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path

from PIL import Image as PILImage
from PIL import ImageDraw, ImageFont
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import (
    Image,
    KeepTogether,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


INK = colors.HexColor("#172C38")
INK_SOFT = colors.HexColor("#40535D")
MUTED = colors.HexColor("#697981")
LINE = colors.HexColor("#D9E0DD")
PAPER = colors.HexColor("#F5F7F6")
ACCENT = colors.HexColor("#08705F")
ACCENT_PALE = colors.HexColor("#E4F2EE")
WARNING = colors.HexColor("#8A5A14")
WARNING_PALE = colors.HexColor("#FBF3DF")


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    candidates = [
        Path("C:/Windows/Fonts/arialbd.ttf" if bold else "C:/Windows/Fonts/arial.ttf"),
        Path("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"),
    ]
    for candidate in candidates:
        if candidate.exists():
            return ImageFont.truetype(str(candidate), size=size)
    return ImageFont.load_default()


def annotate_image(source: Path, target: Path, annotations: list[dict]) -> None:
    image = PILImage.open(source).convert("RGBA")
    overlay = PILImage.new("RGBA", image.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    label_font = load_font(31, bold=True)
    note_font = load_font(24)

    palette = {
        "green": ((8, 112, 95, 255), (228, 242, 238, 238)),
        "orange": ((169, 72, 42, 255), (243, 227, 220, 238)),
        "blue": ((35, 91, 148, 255), (225, 236, 248, 238)),
        "gold": ((138, 90, 20, 255), (251, 243, 223, 238)),
    }

    for item in annotations:
        stroke, fill = palette[item.get("color", "green")]
        box = tuple(item["box"])
        draw.rounded_rectangle(box, radius=12, outline=stroke, width=7)

        anchor = tuple(item["anchor"])
        label = item["label"]
        subtitle = item.get("subtitle")
        label_box = draw.textbbox((0, 0), label, font=label_font)
        subtitle_box = draw.textbbox((0, 0), subtitle, font=note_font) if subtitle else (0, 0, 0, 0)
        width = max(label_box[2], subtitle_box[2]) + 34
        height = 54 + (38 if subtitle else 0)
        lx, ly = tuple(item["label_at"])
        draw.rounded_rectangle((lx, ly, lx + width, ly + height), radius=10, fill=fill, outline=stroke, width=3)
        draw.text((lx + 17, ly + 10), label, fill=stroke, font=label_font)
        if subtitle:
            draw.text((lx + 17, ly + 51), subtitle, fill=(64, 83, 93, 255), font=note_font)

        start = (lx, ly + height // 2)
        if anchor[0] > lx + width:
            start = (lx + width, ly + height // 2)
        draw.line((start, anchor), fill=stroke, width=6)
        radius = 9
        draw.ellipse((anchor[0] - radius, anchor[1] - radius, anchor[0] + radius, anchor[1] + radius), fill=stroke)

    banner_height = 78
    draw.rectangle((0, 0, image.width, banner_height), fill=(23, 44, 56, 245))
    draw.text((34, 18), "SEPTICPATH ANNOTATION - HISTORICAL SKETCH, NOT TO SCALE", fill="white", font=load_font(30, bold=True))

    composed = PILImage.alpha_composite(image, overlay).convert("RGB")
    target.parent.mkdir(parents=True, exist_ok=True)
    composed.save(target, quality=95)


def build_styles() -> dict[str, ParagraphStyle]:
    base = getSampleStyleSheet()
    return {
        "eyebrow": ParagraphStyle(
            "Eyebrow", parent=base["Normal"], fontName="Helvetica-Bold", fontSize=8,
            leading=10, textColor=ACCENT, spaceAfter=8, tracking=1.2,
        ),
        "title": ParagraphStyle(
            "Title", parent=base["Title"], fontName="Helvetica-Bold", fontSize=24,
            leading=28, textColor=INK, alignment=TA_LEFT, spaceAfter=8,
        ),
        "subtitle": ParagraphStyle(
            "Subtitle", parent=base["Normal"], fontName="Helvetica", fontSize=10,
            leading=15, textColor=INK_SOFT, spaceAfter=14,
        ),
        "section": ParagraphStyle(
            "Section", parent=base["Heading2"], fontName="Helvetica-Bold", fontSize=12,
            leading=15, textColor=INK, spaceBefore=10, spaceAfter=7,
        ),
        "body": ParagraphStyle(
            "Body", parent=base["BodyText"], fontName="Helvetica", fontSize=9,
            leading=13, textColor=INK_SOFT, spaceAfter=6,
        ),
        "small": ParagraphStyle(
            "Small", parent=base["BodyText"], fontName="Helvetica", fontSize=7.5,
            leading=10.5, textColor=MUTED,
        ),
        "fact_label": ParagraphStyle(
            "FactLabel", parent=base["BodyText"], fontName="Helvetica-Bold", fontSize=7,
            leading=9, textColor=MUTED, spaceAfter=2,
        ),
        "fact_value": ParagraphStyle(
            "FactValue", parent=base["BodyText"], fontName="Helvetica-Bold", fontSize=9.5,
            leading=12, textColor=INK,
        ),
        "bullet": ParagraphStyle(
            "Bullet", parent=base["BodyText"], fontName="Helvetica", fontSize=8.5,
            leading=12, leftIndent=12, firstLineIndent=-8, textColor=INK_SOFT, spaceAfter=4,
        ),
    }


def fact_cell(label: str, value: str, styles: dict[str, ParagraphStyle]) -> list:
    return [Paragraph(label.upper(), styles["fact_label"]), Paragraph(value, styles["fact_value"])]


def page_frame(canvas, doc) -> None:
    canvas.saveState()
    width, height = letter
    canvas.setFillColor(PAPER)
    canvas.rect(0, 0, width, height, fill=1, stroke=0)
    canvas.setStrokeColor(LINE)
    canvas.line(42, 30, width - 42, 30)
    canvas.setFillColor(MUTED)
    canvas.setFont("Helvetica", 7)
    canvas.drawString(42, 18, "SepticPath public-record research summary")
    canvas.drawRightString(width - 42, 18, f"Page {doc.page}")
    canvas.restoreState()


def build_report(config: dict, annotated_path: Path, output_path: Path, logo_path: Path | None) -> None:
    styles = build_styles()
    doc = SimpleDocTemplate(
        str(output_path), pagesize=letter, rightMargin=42, leftMargin=42,
        topMargin=38, bottomMargin=42, title=config["title"], author="SepticPath",
    )
    story = []

    header_data = []
    if logo_path and logo_path.exists():
        header_data.append(Image(str(logo_path), width=0.42 * inch, height=0.42 * inch))
    header_data.append(Paragraph("<b>SepticPath</b><br/><font size='7' color='#697981'>PUBLIC-RECORD WORKFLOW</font>", styles["body"]))
    header = Table([header_data], colWidths=[0.5 * inch, 6.7 * inch] if len(header_data) == 2 else [7.2 * inch])
    header.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "MIDDLE"), ("LEFTPADDING", (0, 0), (-1, -1), 0), ("RIGHTPADDING", (0, 0), (-1, -1), 6), ("BOTTOMPADDING", (0, 0), (-1, -1), 8)]))
    story.extend([header, Paragraph("RECORD RETRIEVAL COMPLETE", styles["eyebrow"]), Paragraph(config["title"], styles["title"]), Paragraph(config["subtitle"], styles["subtitle"])])

    status = Table([[Paragraph(f"<b>Outcome:</b> {config['outcome']}", styles["body"])]], colWidths=[7.3 * inch])
    status.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), ACCENT_PALE), ("BOX", (0, 0), (-1, -1), 0.8, ACCENT), ("LEFTPADDING", (0, 0), (-1, -1), 12), ("RIGHTPADDING", (0, 0), (-1, -1), 12), ("TOPPADDING", (0, 0), (-1, -1), 9), ("BOTTOMPADDING", (0, 0), (-1, -1), 4)]))
    story.extend([status, Spacer(1, 10), Paragraph("What the agency record shows", styles["section"])])

    facts = config["facts"]
    rows = []
    for i in range(0, len(facts), 2):
        left = fact_cell(facts[i]["label"], facts[i]["value"], styles)
        right = fact_cell(facts[i + 1]["label"], facts[i + 1]["value"], styles) if i + 1 < len(facts) else ""
        rows.append([left, right])
    facts_table = Table(rows, colWidths=[3.62 * inch, 3.62 * inch])
    facts_table.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), colors.white), ("GRID", (0, 0), (-1, -1), 0.5, LINE), ("VALIGN", (0, 0), (-1, -1), "TOP"), ("LEFTPADDING", (0, 0), (-1, -1), 10), ("RIGHTPADDING", (0, 0), (-1, -1), 10), ("TOPPADDING", (0, 0), (-1, -1), 8), ("BOTTOMPADDING", (0, 0), (-1, -1), 8)]))
    story.append(facts_table)

    story.append(Paragraph("How to read the sketch", styles["section"]))
    for item in config["sketch_notes"]:
        story.append(Paragraph(f"• {item}", styles["bullet"]))

    caution_content = [Paragraph("IMPORTANT LIMIT", styles["fact_label"]), Paragraph(config["caution"], styles["body"])]
    caution = Table([[caution_content]], colWidths=[7.3 * inch])
    caution.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), WARNING_PALE), ("LINEBEFORE", (0, 0), (0, -1), 4, WARNING), ("LEFTPADDING", (0, 0), (-1, -1), 12), ("RIGHTPADDING", (0, 0), (-1, -1), 12), ("TOPPADDING", (0, 0), (-1, -1), 8), ("BOTTOMPADDING", (0, 0), (-1, -1), 4)]))
    story.extend([Spacer(1, 9), caution, Paragraph("Recommended next step", styles["section"]), Paragraph(config["next_step"], styles["body"]), Spacer(1, 7), Paragraph(config["source_note"], styles["small"])])

    story.extend([
        PageBreak(),
        Paragraph("ANNOTATED AGENCY SKETCH", styles["eyebrow"]),
        Paragraph("Where the system appears in the historical drawing", styles["title"]),
        Paragraph("Colored callouts were added by SepticPath. The underlying agency page remains unchanged in the separately attached original PDF.", styles["subtitle"]),
        Paragraph("Orientation note: the sketch labels the road and driveway, but it has no north arrow or survey scale. Right and behind below are drawing-relative directions only.", styles["small"]),
        Spacer(1, 6),
    ])
    img = PILImage.open(annotated_path)
    iw, ih = img.size
    # Leave room for the page title, explanatory copy, orientation note, and
    # footer so the annotated sheet stays on the same page.
    max_width, max_height = 7.3 * inch, 6.8 * inch
    scale = min(max_width / iw, max_height / ih)
    story.append(Image(str(annotated_path), width=iw * scale, height=ih * scale))

    output_path.parent.mkdir(parents=True, exist_ok=True)
    doc.build(story, onFirstPage=page_frame, onLaterPages=page_frame)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--config", required=True, type=Path)
    parser.add_argument("--source-image", required=True, type=Path)
    parser.add_argument("--logo", type=Path)
    parser.add_argument("--output-pdf", required=True, type=Path)
    parser.add_argument("--output-annotated", required=True, type=Path)
    args = parser.parse_args()

    config = json.loads(args.config.read_text(encoding="utf-8"))
    annotate_image(args.source_image, args.output_annotated, config["annotations"])
    build_report(config, args.output_annotated, args.output_pdf, args.logo)


if __name__ == "__main__":
    main()
