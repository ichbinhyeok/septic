from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "output" / "Cole_County_No_Records_Email_Excerpt.png"


def font(name: str, size: int):
    path = Path("C:/Windows/Fonts") / name
    return ImageFont.truetype(str(path), size)


REG = font("arial.ttf", 25)
SMALL = font("arial.ttf", 20)
TINY = font("arial.ttf", 16)
BOLD = font("arialbd.ttf", 25)
TITLE = font("arialbd.ttf", 44)
QUOTE = font("arialbd.ttf", 34)


def wrap(draw, text, fnt, max_width):
    words = text.split()
    lines, current = [], ""
    for word in words:
        candidate = word if not current else current + " " + word
        if draw.textbbox((0, 0), candidate, font=fnt)[2] <= max_width:
            current = candidate
        else:
            if current:
                lines.append(current)
            current = word
    if current:
        lines.append(current)
    return lines


img = Image.new("RGB", (1600, 1200), "#eef1f3")
d = ImageDraw.Draw(img)
d.rounded_rectangle((80, 55, 1520, 1145), radius=6, fill="white", outline="#d5d9dd", width=2)

x, y = 165, 125
d.text((x, y), "ORIGINAL AGENCY EMAIL EXCERPT", font=BOLD, fill="#5f6368")
y += 55
d.text((x, y), "Cole County Sunshine Request Response", font=TITLE, fill="#1f2933")
y += 100

rows = [
    ("From", "Chloe Craig-Baker <ccraigbaker@colecounty.org>"),
    ("To", "Shinhyeok Park <shinhyeok22@gmail.com>"),
    ("Date", "September 18, 2026, 10:18 AM CDT"),
    ("Subject", "RE: EXTERNAL: [External] Online Form Submittal: Cole County Sunshine Request Form"),
]
for label, value in rows:
    d.text((x, y), label, font=BOLD, fill="#5f6368")
    lines = wrap(d, value, REG, 1100)
    for i, line in enumerate(lines):
        d.text((x + 145, y + i * 35), line, font=REG, fill="#202124")
    y += max(48, len(lines) * 35 + 10)

y += 10
d.line((x, y, 1435, y), fill="#dadce0", width=2)
y += 50

quote_top = y
quote_lines = wrap(d, "Cole County does not have any records for this request.", QUOTE, 1120)
quote_h = 65 + len(quote_lines) * 50
d.rounded_rectangle((x, quote_top, 1435, quote_top + quote_h), radius=5, fill="#f5f8fa")
d.rectangle((x, quote_top, x + 9, quote_top + quote_h), fill="#174e74")
for i, line in enumerate(quote_lines):
    d.text((x + 42, quote_top + 34 + i * 50), line, font=QUOTE, fill="#172b3a")
y = quote_top + quote_h + 52

d.text((x, y), "REQUEST REFERENCED IN THE QUOTED EMAIL THREAD", font=BOLD, fill="#374151")
y += 52
context = [
    ("Property", "2901 Smith Ln, Jefferson City, MO 65101"),
    ("Parcel", "PID 1502040000002002 / 15-02-04-0000-002-002"),
    ("Requested records", "Complete Cole County Health Department OWTS property file, including permits, evaluations, plans, inspections, repairs, and location drawings."),
]
for label, value in context:
    d.text((x, y), label, font=BOLD, fill="#5f6368")
    lines = wrap(d, value, SMALL, 1040)
    for i, line in enumerate(lines):
        d.text((x + 260, y + i * 31), line, font=SMALL, fill="#202124")
    y += max(46, len(lines) * 31 + 10)

footer = ("Prepared from the original Gmail message (Message ID 1a0b51944ab003b4). "
          "The agency response is reproduced verbatim. The quoted request was condensed to property identifiers "
          "and record scope; no agency wording was added.")
y = 1050
d.line((x, y - 25, 1435, y - 25), fill="#dadce0", width=2)
for i, line in enumerate(wrap(d, footer, TINY, 1260)):
    d.text((x, y + i * 24), line, font=TINY, fill="#6b7280")

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT, optimize=True)
print(OUT)
