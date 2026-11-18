"""Render the fictional educational diagram; no customer records are used."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'src/main/resources/static/images/guides'
FONT_DIR = Path('C:/Windows/Fonts')
image = Image.new('RGB', (1440, 1100), '#f6f4ec')
draw = ImageDraw.Draw(image)

def text(x, y, value, size=25, color='#263e35', bold=False):
    font = ImageFont.truetype(str(FONT_DIR / ('arialbd.ttf' if bold else 'arial.ttf')), size)
    draw.text((x, y), value, font=font, fill=color)

def line(points, color='#456650', width=4):
    draw.line(points, fill=color, width=width)

def dimension(x1, x2, y, label):
    line([(x1,y),(x2,y)], '#b7683c', 3)
    draw.polygon([(x1,y),(x1+12,y-7),(x1+12,y+7)], fill='#b7683c')
    draw.polygon([(x2,y),(x2-12,y-7),(x2-12,y+7)], fill='#b7683c')
    text((x1+x2)/2-45, y-48, label, 28, '#9a4d28', True)

text(60,42,'READING A SEPTIC AS-BUILT',22,bold=True)
text(60,85,'Read the units. Follow the endpoints.',45,bold=True)
text(60,150,'Fictional teaching diagram  /  Not to scale  /  Not a construction plan',24)
draw.rounded_rectangle((50,215,1390,735),radius=15,fill='#fffef9',outline='#cdd7ca',width=2)
# The referenced house corner and tank edge share y=400: the labeled distance
# is between the two indicated points, not a projected diagonal measurement.
draw.rectangle((110,400,350,600),fill='#e4e8dc',outline='#456650',width=4)
text(165,490,'HOUSE',28,bold=True)
draw.rounded_rectangle((550,400,790,555),radius=16,fill='#c8d8c4',outline='#456650',width=4)
text(595,420,'TANK',27,bold=True);text(578,470,'1,000 gal',31,bold=True)
line([(350,475),(550,475)],width=6);text(391,490,'pipe',22)
line([(350,300),(350,400)],'#b7683c',2);line([(550,300),(550,400)],'#b7683c',2)
dimension(350,550,310,'20 ft')
for x, label in [(350,'A'),(550,'B')]:
    draw.ellipse((x-9,391,x+9,409),fill='#b7683c')
    text(x-35,418,label,26,'#9a4d28',True)
line([(790,475),(890,475)],width=6)
draw.rectangle((890,450,940,500),fill='#dfe7d8',outline='#456650',width=3)
line([(940,475),(980,475),(980,365),(1040,365)],width=5)
line([(980,475),(980,600),(1040,600)],width=5)
line([(1040,365),(1270,365)],width=12);line([(1040,600),(1330,600)],width=12)
dimension(1040,1270,315,'60 ft');dimension(1040,1330,670,'80 ft')
text(872,520,'D-box',23);text(110,650,'A: house corner',24,bold=True);text(420,650,'B: tank edge',24,bold=True)
for x,num,title,lines in [
    (60,'01','OFFSET',['20 ft connects A to B.','Not the lid or tank center.']),
    (515,'02','LINE LENGTH',['60 + 80 = 140 linear ft.','Length is not square feet.']),
    (970,'03','CAPACITY',['1,000 gal labels the tank.','It is not bedroom approval.'])]:
    text(x,785,num,24,'#9a4d28',True);text(x,828,title,28,bold=True)
    for i,value in enumerate(lines):text(x,882+i*35,value,23)
line([(60,995),(1380,995)],'#cdd7ca',2)
text(60,1024,'SepticPath  |  Check document stage, property match and approval pages.',24)
image.save(OUT/'septic-as-built-dimensions-explained.png',optimize=True)
image.save(OUT/'septic-as-built-dimensions-explained.webp',quality=88,method=6)
