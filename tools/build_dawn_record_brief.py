from pathlib import Path
import subprocess

from PIL import Image as PILImage
from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import Image, Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle, PageBreak


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "storage" / "operations" / "sources" / "williamson-4545-scenic-hills-coc-20260918.pdf"
OUTPUT = ROOT / "output" / "pdf" / "dawn-4545-scenic-hills-record-brief.pdf"
WORK = ROOT / "tmp" / "pdfs" / "dawn-record-brief"

NAVY = colors.HexColor("#102A43")
BLUE = colors.HexColor("#2878D0")
PALE_BLUE = colors.HexColor("#EAF3FC")
GREEN = colors.HexColor("#17805C")
PALE_GREEN = colors.HexColor("#E8F6F0")
AMBER = colors.HexColor("#A96500")
PALE_AMBER = colors.HexColor("#FFF4DB")
INK = colors.HexColor("#25313C")
MUTED = colors.HexColor("#647585")
LINE = colors.HexColor("#D9E1E8")
PAPER = colors.HexColor("#F8FAFC")


def header_footer(canvas, doc):
    canvas.saveState()
    width, height = letter
    canvas.setFillColor(NAVY)
    canvas.rect(0, height - 0.44 * inch, width, 0.44 * inch, fill=1, stroke=0)
    canvas.setFillColor(colors.white)
    canvas.setFont("Helvetica-Bold", 9)
    canvas.drawString(0.58 * inch, height - 0.28 * inch, "SEPTICPATH  /  RECORD BRIEF")
    canvas.setFillColor(MUTED)
    canvas.setFont("Helvetica", 7.2)
    canvas.drawString(0.58 * inch, 0.34 * inch, "Independent record interpretation - not an inspection, survey, or current field locate")
    canvas.drawRightString(width - 0.58 * inch, 0.34 * inch, str(doc.page))
    canvas.restoreState()


styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="Hero", parent=styles["Title"], fontName="Helvetica-Bold", fontSize=20, leading=23, textColor=NAVY, spaceAfter=6))
styles.add(ParagraphStyle(name="Deck", parent=styles["BodyText"], fontName="Helvetica", fontSize=9.3, leading=12, textColor=MUTED, spaceAfter=8))
styles.add(ParagraphStyle(name="Section", parent=styles["Heading2"], fontName="Helvetica-Bold", fontSize=11.5, leading=13.5, textColor=NAVY, spaceBefore=7, spaceAfter=5))
styles.add(ParagraphStyle(name="Body", parent=styles["BodyText"], fontName="Helvetica", fontSize=8.3, leading=10.7, textColor=INK))
styles.add(ParagraphStyle(name="BodySmall", parent=styles["BodyText"], fontName="Helvetica", fontSize=7.5, leading=9.5, textColor=INK))
styles.add(ParagraphStyle(name="Label", parent=styles["BodyText"], fontName="Helvetica-Bold", fontSize=7.2, leading=8.5, textColor=MUTED, spaceAfter=2))
styles.add(ParagraphStyle(name="CardTitle", parent=styles["BodyText"], fontName="Helvetica-Bold", fontSize=9.1, leading=11, textColor=NAVY, spaceAfter=3))
styles.add(ParagraphStyle(name="CardBody", parent=styles["BodyText"], fontName="Helvetica", fontSize=7.8, leading=10, textColor=INK))
styles.add(ParagraphStyle(name="Source", parent=styles["BodyText"], fontName="Helvetica", fontSize=6.5, leading=8, textColor=MUTED))


def p(text, style="Body"):
    return Paragraph(text, styles[style])


def card(title, body, bg=PALE_BLUE, accent=BLUE, width=6.84 * inch):
    table = Table([[[p(title, "CardTitle"), p(body, "CardBody")]]], colWidths=[width])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), bg),
        ("BOX", (0, 0), (-1, -1), 0.7, accent),
        ("LINEBEFORE", (0, 0), (0, -1), 4, accent),
        ("LEFTPADDING", (0, 0), (-1, -1), 9),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 7),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    return table


def fact_table(rows):
    data = [[p("RECORD FINDING", "Label"), p("WHAT THE COUNTY FILE SHOWS", "Label")]]
    for left, right in rows:
        data.append([p(left, "BodySmall"), p(right, "BodySmall")])
    table = Table(data, colWidths=[1.55 * inch, 5.29 * inch], repeatRows=1)
    style = [
        ("BACKGROUND", (0, 0), (-1, 0), NAVY),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.45, LINE),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]
    for idx in range(2, len(data), 2):
        style.append(("BACKGROUND", (0, idx), (-1, idx), PAPER))
    table.setStyle(TableStyle(style))
    return table


def prepare_sketch():
    WORK.mkdir(parents=True, exist_ok=True)
    subprocess.run(["pdftoppm", "-f", "1", "-singlefile", "-png", "-r", "180", str(SOURCE), str(WORK / "coc")], check=True)
    image = PILImage.open(WORK / "coc.png")
    width, height = image.size
    crop = image.crop((0, int(height * 0.25), width, int(height * 0.93)))
    crop_path = WORK / "coc-layout-crop.png"
    crop.save(crop_path)
    return crop_path


def build():
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    sketch = prepare_sketch()
    doc = SimpleDocTemplate(
        str(OUTPUT),
        pagesize=letter,
        leftMargin=0.58 * inch,
        rightMargin=0.58 * inch,
        topMargin=0.68 * inch,
        bottomMargin=0.62 * inch,
        title="SepticPath Record Brief - 4545 Scenic Hills Lane",
        author="SepticPath",
        subject="Williamson County septic permit and completion record interpretation",
    )
    story = [Spacer(1, 0.07 * inch)]
    story.append(p("Septic Record Brief", "Hero"))
    story.append(p("4545 Scenic Hills Lane, Franklin, Tennessee 37064<br/>Map 040 / Parcel 023.00", "Deck"))

    meta = Table([
        [p("COUNTY RESPONSE", "Label"), p("RECORD PERIOD", "Label"), p("PROPERTY MATCH", "Label")],
        [p("Permit + Certificate of Completion", "BodySmall"), p("March-August 1994", "BodySmall"), p("Verified against address and parcel", "BodySmall")],
    ], colWidths=[2.48 * inch, 1.62 * inch, 2.74 * inch])
    meta.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), PAPER),
        ("BOX", (0, 0), (-1, -1), 0.5, LINE),
        ("INNERGRID", (0, 0), (-1, -1), 0.5, LINE),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, 0), 7),
        ("BOTTOMPADDING", (0, 0), (-1, 0), 2),
        ("TOPPADDING", (0, 1), (-1, 1), 2),
        ("BOTTOMPADDING", (0, 1), (-1, 1), 7),
    ]))
    story.extend([meta, Spacer(1, 0.10 * inch)])

    story.append(card(
        "Documentary result: APPROVED AND COMPLETED IN 1994",
        "Williamson County returned the construction permit and Certificate of Completion for this exact property and stated that these are all the records it has. Together, they establish the approved capacity, core system design, construction approval, and relative system layout.",
        PALE_GREEN,
        GREEN,
    ))

    story.append(p("Verified county findings", "Section"))
    story.append(fact_table([
        ("Approved capacity", "Three-bedroom residential system."),
        ("Permit", "New installation permit issued March 7, 1994."),
        ("Completion", "Construction approved August 16, 1994."),
        ("Tank system", "1,000-gallon septic tank plus a separately required 1,000-gallon pump tank."),
        ("Disposal field", "Alternating system; permit specifies 370 linear feet in four trenches, 36 inches wide and 24 inches deep."),
        ("Required features", "Curtain/interceptor drain, flow-diversion valve, and sewage pump."),
        ("Other county files", "No separate repair permit or soil map was supplied; the County described these two records as its complete holding."),
    ]))

    story.append(p("How to use the layout", "Section"))
    story.append(card(
        "The sketch is a relative guide",
        "The completion sketch places the house near the upper-left portion of the drawing, the tank/pump area near the house, and the disposal lines extending to the right side of the house. Scenic Hills Drive is drawn along the bottom edge, with the curved driveway rising toward the home.",
        PALE_BLUE,
        BLUE,
    ))
    story.append(Spacer(1, 0.07 * inch))
    story.append(card(
        "Before digging or construction",
        "The records are historical sketches and are not survey-grade as-built coordinates. A licensed septic professional should physically locate the current tank, pump tank, and field lines before excavation, additions, drive work, or landscaping.",
        PALE_AMBER,
        AMBER,
    ))

    story.append(Spacer(1, 0.08 * inch))
    story.append(p("Sources: Williamson County construction permit dated March 7, 1994; Williamson County Certificate of Completion dated August 16, 1994; County response dated September 18, 2026.", "Source"))

    story.append(PageBreak())
    story.append(Spacer(1, 0.07 * inch))
    story.append(p("County Completion Sketch", "Hero"))
    story.append(p("A reading guide to the original 1994 Certificate of Completion sketch. The unaltered County PDF is included separately.", "Deck"))
    story.append(Image(str(sketch), width=6.84 * inch, height=5.45 * inch))
    story.append(Spacer(1, 0.08 * inch))
    story.append(fact_table([
        ("Bottom edge", "Scenic Hills Drive frontage."),
        ("Left side", "Curved driveway leading toward the house."),
        ("Near the house", "Tank/pump area shown beside and downslope of the house."),
        ("Right side", "Alternating disposal-field lines shown extending away from the house."),
    ]))
    story.append(Spacer(1, 0.08 * inch))
    story.append(p("The sketch is useful for orientation, but it should not be used to place excavation equipment or establish setbacks without a physical locate and any required current approvals.", "Body"))

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
    print(OUTPUT)


if __name__ == "__main__":
    build()
