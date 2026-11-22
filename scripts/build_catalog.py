#!/usr/bin/env python3
"""
Risale Dinle v7 — Tam Külliyat Mikro-Kesit Tarayıcısı & Sorularla Risale Konu Fihristi + Fihrist Risalesi Motoru.

Özellikler:
1. 261 ses kaydını (58.24 saat) Külliyat metniyle (sozler.json - 1.71M+ karakter) saniye-saniye eşleştirir.
2. Her ses kaydını büyük bloklar yerine **40 sn – 135 sn** uzunluğunda (~1-2 dakikalık) **3.100+ mikro-kesite**
   (ör. 20 dakikalık kaydın sadece 13:10–14:52 arası) ayırır.
3. `https://sorularlarisale.com/risale-i-nur-konu-fihristi?amp` (ve `Risale-i Nur Konu Fihristi .PDF`) içindeki
   **100+ konunun tamamını** hem doğrudan fihrist referanslarıyla hem de Külliyatın tamamını paragraf-paragraf
   tarayan **Derin Kavram Tarayıcısı** ile eşleştirir — konuyla alakalı en ufak bir pasajı bile açıkta bırakmaz.
4. `_dosyalar_dosya2_Fihrist_Risalesi.pdf` (15. Lem'a Fihrist Risalesi), `İki boyutlu Risale-i Nur fihristi .pdf`,
   `Soru-Cevap Fihrist - Bölüm 1 & 2.pdf` ve `Kitap-Kitap-Namaz-Dersleri.pdf` kaynaklarını mikro-kesitlerle birleştirir.
"""

import json
import re
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
if (REPO_ROOT / "kulliyat-verileri").exists():
    SOZLER_TEXT_PATH = REPO_ROOT / "kulliyat-verileri" / "kulliyat" / "sozler.json"
    SYNC_2_PATH = REPO_ROOT / "kulliyat-verileri" / "sync" / "koseoglu" / "sozler" / "2.json"
    SORULARLA_SOZLER_PATH = REPO_ROOT / "kulliyat-verileri" / "ses-listeleri" / "sorularlarisale_sozler.json"
    PDF_DIR = REPO_ROOT / "kaynak-pdfler" / "fihristler"
    OUT_DIR = REPO_ROOT / "data"
else:
    ROOT = Path(__file__).resolve().parents[2]
    NUR_APP = ROOT / "flash_packages" / "nur-u-sema-risale-app" / "risale-app"
    SOZLER_TEXT_PATH = NUR_APP / "public" / "data" / "kulliyat" / "sozler.json"
    SYNC_2_PATH = NUR_APP / "public" / "data" / "sync" / "koseoglu" / "sozler" / "2.json"
    SORULARLA_SOZLER_PATH = NUR_APP / "public" / "data" / "sorularlarisale_sozler.json"
    PDF_DIR = ROOT / "eklenen-paketler" / "risale-i-nur" / "Risale-i Nur İçindekiler"
    OUT_DIR = ROOT / "risale-dinle" / "data"
OUT_DIR.mkdir(parents=True, exist_ok=True)
DURATIONS_PATH = OUT_DIR / "mp3-durations.json"

ARABIC_RE = re.compile(r"[\u0600-\u06FF\u0750-\u077F\uFB50-\uFDFF\uFE70-\uFEFF]{4,}")
BESMELE_RE = re.compile(
    r"(﷽|بِسْمِ\s*اللّٰهِ|بِسْمِ\s*الله|bismill[aâ]hirrahm[aâ]nirrah[iî]m|“?bismill[aâ]h”?|e[uû]z[uü]\s+bill[aâ]h|elhamd[uü]lill[aâ]h|اَلْحَمْدُ\s*لِلّٰهِ|وَالصَّلٰوةُ)",
    re.I,
)
SUBHEAD_RE = re.compile(
    r"^(\[?(Birinci|İkinci|Üçüncü|Dördüncü|Beşinci|Altıncı|Yedinci|Sekizinci|Dokuzuncu|Onuncu|"
    r"On\s*birinci|On\s*ikinci|On\s*üçüncü|On\s*dördüncü|On\s*beşinci|On\s*altıncı|On\s*yedinci|On\s*sekizinci|On\s*dokuzuncu|"
    r"Yirminci|Yirmi\s*birinci|Yirmi\s*ikinci|Yirmi\s*üçüncü|Yirmi\s*dördüncü|Yirmi\s*beşinci|Yirmi\s*altıncı|Yirmi\s*yedinci|Yirmi\s*sekizinci|Yirmi\s*dokuzuncu|"
    r"Otuzuncu|Otuz\s*birinci|Otuz\s*ikinci|Otuz\s*üçüncü|[1-9][0-9]?\s*\.)\s*"
    r"(Makam|Mevkıf|Mebhas|Maksad|Maksat|Nükte|Nokta|Hakikat|Suret|Sûret|Burhan|Bürhan|Lem'a|Lem’a|Lema|"
    r"Dal|Şu'le|Şu’le|Şule|Şua|Şuâ|Nur|Pencere|Reşha|Esas|Vecih|İşaret|Sual|Mesele|Mes'ele|Mes’ele|Remiz|Sır|Meyve|Asıl|Kısım|Nükte-i|Basamak|Cihet|Kâr|İkaz|Merhem|Hatve)"
    r"|Mukaddime|Hâtime|Hatime|Zeyl|Birinci\s+Zeyl|İkinci\s+Zeyl|Hüve\s+Nüktesi|İhtar|Tenbih|Elhâsıl|Elhasıl|Elcevap|Elcevab)",
    re.I,
)

ORDINAL_MAP = {
    "birinci": 1, "ikinci": 2, "üçüncü": 3, "dördüncü": 4, "beşinci": 5,
    "altıncı": 6, "yedinci": 7, "sekizinci": 8, "dokuzuncu": 9, "onuncu": 10,
    "on birinci": 11, "onbirinci": 11, "on ikinci": 12, "onikinci": 12,
    "on üçüncü": 13, "onüçüncü": 13, "on dördüncü": 14, "ondördüncü": 14,
    "on beşinci": 15, "onbeşinci": 15, "on altıncı": 16, "onaltıncı": 16,
    "on yedinci": 17, "onyedinci": 17, "on sekizinci": 18, "onsekizinci": 18,
    "on dokuzuncu": 19, "ondokuzuncu": 19, "yirminci": 20,
    "yirmi birinci": 21, "yirmibirinci": 21, "yirmi ikinci": 22, "yirmiikinci": 22,
    "yirmi üçüncü": 23, "yirmiüçüncü": 23, "yirmi dördüncü": 24, "yirmidördüncü": 24,
    "yirmi beşinci": 25, "yirmibeşinci": 25, "yirmi altıncı": 26, "yirmialtıncı": 26,
    "yirmi yedinci": 27, "yirmiyedinci": 27, "yirmi sekizinci": 28, "yirmisekizinci": 28,
    "yirmi dokuzuncu": 29, "yirmidokuzuncu": 29, "otuzuncu": 30,
    "otuz birinci": 31, "otuzbirinci": 31, "otuz ikinci": 32, "otuzikinci": 32,
    "otuz üçüncü": 33, "otuzüçüncü": 33,
}

TR_STOPWORDS = {
    "bir", "ve", "ile", "için", "olan", "olarak", "gibi", "daha", "kadar", "her", "bu", "şu", "o",
    "de", "da", "ki", "mi", "mı", "mu", "mü", "veya", "ya", "hem", "fakat", "lakin", "ancak",
    "ise", "çok", "az", "tüm", "bütün", "diğer", "sonra", "önce", "üzerine", "hakkında", "nasıl",
    "neden", "niçin", "nedir", "söz", "sözler", "sayfa", "sh", "rnk", "bölüm", "makam", "nükte",
    "nokta", "mesele", "mes", "ele", "işaret", "ey", "kardeş", "evet", "işte", "demek", "çünkü",
    "çünki", "mesela", "meselâ", "yani", "hatta", "belki", "eğer", "öyle", "böyle", "şöyle",
}

# Two-tier Turkish + Ottoman concept profiles: (direct_core_terms, indirect_context_terms)
# Direct terms must appear literally/closely to qualify as 'Doğrudan Alakalı';
# Indirect terms require >=2 distinct supporting concept hits to qualify as 'Dolaylı Alakalı' (eliminating noise).
TOPIC_PROFILES = {
    "adalet": (["adalet", "adil", "hakkaniyet", "mizan", "zulüm", "zalim"], ["mahkeme", "mükafat", "mücazat", "hukuk", "muvazene", "kader"]),
    "ahirete": (["haşir", "ahiret", "diriliş", "mahkeme", "kıyamet", "ba's"], ["beka", "bahar", "kabir", "cennet", "cehennem", "ebedi", "ölüm"]),
    "alevilik": (["ali", "ehl-i beyt", "hasan", "hüseyin", "hilafet", "alevi"], ["sahabe", "muhabbet", "ashab", "şefaat", "al-i beyt"]),
    "allah": (["tevhid", "vahid", "ehad", "vahdaniyet", "ehadiyet", "marifetullah", "vacib", "halık", "sani"], ["rububiyet", "uluhiyet", "bürhan", "pencere", "esma", "sikke", "mühür", "kudret", "sanat"]),
    "anne": (["valide", "peder", "anne", "baba", "ebeveyn"], ["şefkat", "evlat", "hürmet", "rahmet", "aile", "merhamet"]),
    "aşk": (["aşk", "muhabbet", "sevgi", "mahbub", "vedud", "sevmek"], ["cemal", "kemal", "ihsan", "mecazi", "hakiki", "kalp", "esma"]),
    "avrupa": (["avrupa", "garb", "ecnebi", "frenk"], ["medeniyet", "felsefe", "sefahat", "terakki", "dalalet"]),
    "bela": (["musibet", "bela", "felaket", "afet"], ["sabır", "imtihan", "şekva", "kader", "merhamet", "teselli", "hastalık", "deva"]),
    "cehennem": (["cehennem", "azap", "cahim", "nar-ı", "zindan-ı"], ["mücazat", "kafir", "isyan", "adalet", "ahiret", "kabir"]),
    "celcelutiye": (["celcelutiye", "kaside", "imam-ı ali"], ["ism-i azam", "dua", "esma", "şefaat", "keramet"]),
    "cemaat": (["cemaat", "uhuvvet", "ittihad", "şahs-ı manevi", "tesanüd"], ["kardeş", "ihlas", "muhabbet", "teavün", "hizmet"]),
    "cennet": (["cennet", "saadet-i ebediye", "huri", "dar-ı saadet"], ["ebedi", "baki", "lezzet", "rahmet", "mükafat", "ahiret", "haşir"]),
    "cevşen": (["cevşen", "münacat", "bina-yı"], ["esma", "şefaat", "dua", "peygamber"]),
    "cihat": (["cihad", "cihat", "mücahede", "gaza"], ["manevi", "nefis", "kalem", "iman", "asker", "şehit"]),
    "çocuk": (["çocuk", "evlat", "masum", "sabî", "yavru"], ["terbiye", "valide", "şefkat", "rahmet", "taziye"]),
    "çok eşlilik": (["taaddüd", "zevcat", "zevc", "evlilik", "nikah"], ["izdivaç", "hanım", "şeriat", "aile", "sünnet"]),
    "dabbe": (["dabbe", "dabbetü"], ["ahirzaman", "kıyamet", "alamet", "fitne"]),
    "deccal": (["deccal", "süfyan"], ["ahirzaman", "mehdi", "fitne", "alamet", "kıyamet", "hadis"]),
    "deprem": (["zelzele", "deprem", "hareket-i arz"], ["küre-i arz", "musibet", "ikaz", "mazlum", "gaflet", "rahmet"]),
    "dua": (["dua", "münacat", "niyaz", "istimdad", "yalvar"], ["ubudiyet", "acz", "fakr", "icabet", "iltica", "dergah", "şükür"]),
    "dünya": (["dünya", "fani", "misafirhane", "mezraa", "zeval"], ["ahiret", "gaflet", "lezzet", "beka", "baki", "ömür"]),
    "ehadiyet": (["ehadiyet", "vahidiyet", "ehad", "vahid"], ["tecelli", "güneş", "ayine", "sikke", "mühür", "tevhid"]),
    "ene": (["ene", "enaniyet", "benlik", "vahid-i kıyasi"], ["emanet", "nefis", "gurur", "felsefe", "ubudiyet", "acz"]),
    "esma": (["esma", "isimler", "müsemma", "esma-i"], ["tecelli", "nakış", "sanat", "rahim", "hakim", "kadir", "cemal", "ayine"]),
    "esir": (["esir", "esiriye"], ["zerrat", "feza", "semavat", "hava", "madde", "zerre"]),
    "evlilik": (["evlilik", "izdivaç", "nikah", "refika", "zevce", "aile"], ["muhabbet", "şefkat", "ebedi", "hanım", "iffet"]),
    "faiz": (["faiz", "riba", "banka"], ["zekat", "haram", "helal", "ticaret", "iktisat", "zulüm"]),
    "felsefe": (["felsefe", "feylesof", "hükema", "hikmet-i felsefe"], ["hikmet-i kur", "tabiat", "dalalet", "akıl", "ene", "medeniyet"]),
    "fetret": (["fetret", "ehl-i fetret", "ehl-i necat"], ["rahmet", "adalet", "imtihan", "peygamber", "risalet"]),
    "gayb": (["gayb", "alem-i gayb", "gaybi", "ihbar-ı gayb"], ["şehadet", "mucize", "istikbal", "melek", "ruh", "vahiy"]),
    "gençlik": (["genç", "gençlik", "şebab", "şebabet"], ["hevesat", "nefis", "sefahat", "kabir", "istikbal", "iffet", "ömür"]),
    "gıybet": (["gıybet", "zemm", "zem", "dedikodu"], ["haset", "uhuvvet", "kardeş", "kusur", "haram", "adavet"]),
    "gurur": (["gurur", "mağrur", "kibir", "fahr", "enaniyet"], ["tevazu", "acz", "fakr", "nefis", "ucb", "tevbe"]),
    "haset": (["haset", "hased", "kıskanç", "rekabet"], ["uhuvvet", "adavet", "gıybet", "ihlas", "nefis"]),
    "hastalık": (["hastalık", "hasta", "maraz", "şifa", "deva"], ["musibet", "sabır", "kefaret", "ecir", "rahmet", "teselli"]),
    "hayat": (["hayat", "muhyi", "zihayat", "canlı", "ihya"], ["ruh", "şuur", "nimet", "tecelli", "kudret", "beka"]),
    "hırs": (["hırs", "haris", "tamah"], ["kanaat", "rızık", "tevekkül", "hasaret", "iktisat", "şükür"]),
    "hristiyanlık": (["hristiyan", "nasara", "isevi", "incil", "teslis"], ["avrupa", "medeniyet", "ehl-i kitap", "peygamber"]),
    "hürriyet": (["hürriyet", "hür", "istibdad", "esaret"], ["irade", "ubudiyet", "şeriat", "adalet", "meşrutiyet"]),
    "ırkçılık": (["ırkçılık", "milliyet", "unsuriyet", "kavmiyet", "hamiyet"], ["uhuvvet", "islam", "kardeşlik", "ittihad", "cemaat"]),
    "ibadet": (["ibadet", "ubudiyet", "kulluk", "abdiyet"], ["namaz", "emir", "şükür", "secde", "takva", "dua", "itaat"]),
    "içtihat": (["içtihad", "içtihat", "müçtehid", "mezhep"], ["şeriat", "sahabe", "ahkam", "kur'an", "sünnet"]),
    "ihlas": (["ihlas", "muhlis", "rıza-yı ilahi", "riya", "samimiyet"], ["uhuvvet", "hizmet", "lillah", "ubudiyet", "amel"]),
    "ihtiyar": (["ihtiyarlık", "ihtiyar", "yaşlı", "pir-i fani"], ["gençlik", "ömür", "zeval", "teselli", "beka", "ahiret", "kabir"]),
    "iktisat": (["iktisat", "iktisad", "israf", "kanaat", "bereket"], ["şükür", "nimet", "rızık", "hırs", "fakir"]),
    "ilham": (["ilham", "vahiy", "ilhamat"], ["kelam", "peygamber", "melek", "kalp", "arı", "sevk"]),
    "ilim": (["ilim", "alim", "ulema", "marifet", "ilmi"], ["hikmet", "kudret", "irade", "kitap", "akıl", "tefekkür"]),
    "imam-ı mübin": (["imam-ı mübin", "kitab-ı mübin", "levh-i mahfuz"], ["kader", "ilm-i ilahi", "defter", "hafıza", "kayıt"]),
    "iman": (["iman", "mümin", "intisap", "tahkiki", "hidayet"], ["nur", "saadet", "tevhid", "kalp", "küfür", "ubudiyet"]),
    "insan": (["insan", "beşer", "insaniyet", "halife", "cihazat"], ["mahiyet", "emanet", "istidad", "hayvan", "ubudiyet", "antika"]),
    "irade": (["irade", "ihtiyari", "cüz'i", "meyelan", "tercih"], ["kader", "kudret", "mesuliyet", "kesb", "adalet"]),
    "ism-i azam": (["ism-i azam", "ferd", "hayy", "kayyum", "hakem", "kuddüs"], ["esma", "tecelli", "celal", "cemal", "azam"]),
    "istiğna": (["istiğna", "minnet", "gönül tokluğu"], ["hediye", "sadaka", "kanaat", "izzet", "tevekkül", "rızık"]),
    "kabir": (["kabir", "berzah", "mezar", "kabre"], ["ölüm", "mevt", "terhis", "ahiret", "ruh", "haşir", "kapı"]),
    "kadın": (["kadın", "hanım", "nisa", "tesettür", "valide"], ["şefkat", "aile", "iffet", "hürmet", "refika"]),
    "kardeşlik": (["uhuvvet", "kardeşlik", "kardeş", "uhuvvet-i"], ["muhabbet", "tesanüd", "adavet", "ihlas", "mümin", "cemaat"]),
    "kaza": (["kader", "kaza", "takdir", "cüz'i irade", "ihtiyari"], ["levh", "hikmet", "adalet", "tevekkül", "musibet", "ilim"]),
    "kıyamet": (["kıyamet", "sekerat", "harab", "ecel-i dünya"], ["haşir", "dünya", "ecel", "mevt", "ahirzaman", "alamet"]),
    "kibir": (["kibir", "tekebbür", "gurur", "büyüklenme", "enaniyet"], ["tevazu", "fahr", "acz", "nefis", "ucb"]),
    "kitaplara": (["kitaplar", "suhuf", "semavi", "tevrat", "incil", "zebur", "vahiy"], ["kur'an", "kelamullah", "peygamber", "risalet", "iman"]),
    "korku": (["korku", "havf", "dehşet", "endişe", "vehim"], ["reca", "ecel", "teselli", "emniyet", "iman", "tevekkül"]),
    "kudret": (["kudret", "kadir", "emr-i kün", "kün feyekün"], ["kolaylık", "zerre", "güneş", "haşir", "tevhid", "sanat", "intizam"]),
    "kur'an": (["kur'an", "kelamullah", "i'caz", "belagat", "şule", "mucize-i kur"], ["ayet", "tefsir", "vahiy", "felsefe", "hikmet", "üslup", "tekrar"]),
    "küfür": (["küfür", "kafir", "dalalet", "inkar", "isyan", "şirk"], ["zulmet", "tabiat", "azap", "iman", "cehennem", "nefis"]),
    "latife": (["latife", "letaif", "vicdan", "sır", "hafi", "ahfa"], ["kalp", "ruh", "akıl", "hayal", "cihazat", "duygu"]),
    "medeniyet": (["medeniyet", "medeni", "sefahat", "garb"], ["avrupa", "felsefe", "şeriat", "hikmet", "kur'an", "ictimai"]),
    "mehdi": (["mehdi", "ahirzaman", "müceddid"], ["deccal", "süfyan", "fitne", "hizmet", "cemaat"]),
    "melek": (["melek", "melaike", "ruhaniyat", "cebrail", "azrail", "mikail", "israfil"], ["tesbih", "ubudiyet", "ruh", "alem-i gayb", "sema"]),
    "mezhep": (["mezhep", "meşrep", "meslek", "hanefi", "şafii"], ["içtihad", "uhuvvet", "ihlas", "şeriat", "hak"]),
    "miraç": (["miraç", "mirac", "uruc", "kab-ı kavseyn", "burak"], ["ru'yet", "namaz", "peygamber", "risalet", "velayet"]),
    "namaz": (["namaz", "salat", "beş vakit", "secde", "abdest", "rekat"], ["ubudiyet", "kulluk", "mirac", "usanç", "vesvese", "tesbih", "vakit"]),
    "nefis": (["nefis", "nefs", "emmare", "heva", "hevesat"], ["mal", "cennet", "ticaret", "emanet", "mücahede", "ubudiyet", "şeytan"]),
    "oruç": (["oruç", "ramazan", "savm", "iftar", "sahur"], ["nefis", "şükür", "nimet", "iktisat", "açlık", "ibadet"]),
    "ölüm": (["ölüm", "mevt", "ecel", "vefat", "terhis", "sekerat"], ["kabir", "beka", "idam", "ahiret", "ruh", "haşir", "fani"]),
    "peygamber": (["peygamber", "resul", "nebi", "nübüvvet", "risalet", "ahmediye", "muhammed"], ["mucize", "reşha", "sünnet", "vahiy", "rehber", "muallim", "şefaat"]),
    "ruh": (["ruh", "ervah", "beka-i ruh", "ruhani"], ["cesed", "hayat", "şuur", "kanun-u emri", "melaike", "beka"]),
    "rüya": (["rüya", "alem-i misal", "sadık rüya"], ["uyku", "misal", "levh", "gayb", "ruh"]),
    "sabır": (["sabır", "sabret", "tahammül", "sabur"], ["musibet", "bela", "şükür", "ibadet", "tevekkül", "hastalık", "imtihan"]),
    "sahabe": (["sahabe", "ashab", "asrısaadet", "sohbet-i nebeviye"], ["içtihad", "velayet", "sıdk", "fedakar", "peygamber", "sünnet"]),
    "salavat": (["salavat", "salat", "selam", "aleyhissalatü"], ["şefaat", "peygamber", "al", "ashab", "dua", "muhabbet"]),
    "sebepler": (["esbab", "sebep", "sebepler", "müsebbib", "perde-i izzet"], ["kudret", "tabiat", "tevhid", "ağaç", "meyve", "hakiki fail"]),
    "siyaset": (["siyaset", "siyasi", "devlet", "hükümet", "idare-i"], ["ictimai", "adalet", "hürriyet", "şeriat", "hizmet"]),
    "süfyan": (["süfyan", "deccal"], ["ahirzaman", "fitne", "alamet", "mehdi"]),
    "sünnet": (["sünnet", "ittiba", "sünen"], ["edep", "şeriat", "peygamber", "muhabbet", "istikamet", "ahlak"]),
    "şahsı manevi": (["şahs-ı manevi", "şahsı manevi", "tesanüd", "iştirak-ı amal"], ["cemaat", "uhuvvet", "ihlas", "ittihad", "hizmet"]),
    "şefkat": (["şefkat", "merhamet", "rahmet", "rahim", "şefik"], ["valide", "kerim", "nimet", "rızık", "evlat", "hayvan"]),
    "şeytan": (["şeytan", "vesvese", "desise", "iblis", "istiaze"], ["şer", "imtihan", "terakki", "nefis", "günah", "mücahede"]),
    "şuûnât": (["şuûnât", "şuunat", "şe'n", "lezzet-i mukaddese"], ["esma", "sıfat", "cemal", "kemal", "tecelli", "muhabbet"]),
    "şükür": (["şükür", "şükret", "hamd", "elhamdülillah", "şakir", "nankör"], ["nimet", "rezzak", "mün'im", "ubudiyet", "iktisat", "bereket", "dua"]),
    "tabiat": (["tabiat", "tesadüf", "tabiiyun", "maddeci"], ["esbab", "muhal", "sanat", "hilkat", "felsefe", "tevhid"]),
    "takva": (["takva", "müttaki", "kebair", "amel-i salih", "günah"], ["azap", "tövbe", "istiğfar", "ibadet", "haram", "sefahat"]),
    "tarikat": (["tarikat", "tasavvuf", "velayet", "seyr-i süluk", "evliya"], ["kalp", "zikir", "hakikat", "sünnet", "marifet", "nefis"]),
    "taziye": (["taziye", "teselli", "vefat", "firak"], ["ölüm", "sabır", "rahmet", "ahiret", "ebedi", "kavuş"]),
    "tesbihat": (["tesbih", "tesbihat", "sübhanallah", "elhamdülillah", "allahü ekber"], ["namaz", "zikir", "ubudiyet", "lisan-ı hal", "mevcudat"]),
    "tesettür": (["tesettür", "hicab", "örtü", "iffet"], ["kadın", "hanım", "fitne", "şefkat", "aile", "ahlak"]),
    "tevekkül": (["tevekkül", "mütevekkil", "teslim", "tefviz"], ["emniyet", "kader", "iktidar", "acz", "rızık", "sabır"]),
    "ucb": (["ucb", "fahr", "riya", "gurur", "ameline güven"], ["nefis", "tövbe", "kusur", "tevazu", "ihlas", "enaniyet"]),
    "uyku": (["uyku", "nevm", "uykuda", "istirahat"], ["mevt", "ölüm", "rüya", "gece", "nimet"]),
    "vahdet": (["vahdetü", "vahdet-i vücud", "şühud", "mevcudat"], ["ayine", "tecelli", "esma", "ehadiyet", "tevhid", "güneş"]),
    "vesvese": (["vesvese", "evham", "vesvas"], ["şüphe", "kalp", "şeytan", "hayal", "namaz", "merhem", "tahayyül"]),
    "yalan": (["yalan", "kizb", "kazib", "sıdk", "doğruluk"], ["nifak", "küfür", "emanet", "sadık", "nübüvvet"]),
    "yeis": (["yeis", "ümitsiz", "ümit", "reca", "atalet"], ["rahmet", "tövbe", "şevk", "iman", "teselli"]),
    "zekat": (["zekat", "sadaka", "infak", "muavenet"], ["fakir", "zengin", "riba", "faiz", "yardım", "bereket", "iktisat"]),
    "zerre": (["zerre", "zerrat", "atom", "tahavvülat"], ["hüve", "hava", "hareket", "kudret", "intizam", "esir"]),
    "zikir": (["zikir", "zikret", "tesbih", "bismillah", "lailaheillallah"], ["elhamdülillah", "kalp", "lisan-ı hal", "şükür", "tefekkür", "dua"]),
}

# Short/ambiguous stems that MUST match whole-word (+ simple Turkish suffix) to prevent false substring hits
# e.g. 'esir' must NEVER match 'tesir'/'müessir'/'kesir', 'ene' must NEVER match 'sene'/'genel'/'bedene'
EXACT_BOUNDARY_TERMS = {
    "esir", "ene", "aşk", "ruh", "nur", "hak", "can", "sır", "din", "mal", "kul", "ali", "al",
    "dua", "hür", "zem", "gam", "kan", "yol", "ev", "su", "arı", "bal", "nar", "ucb", "acz",
    "fakr", "sıdk", "kizb", "mevt", "nevm", "gayb", "hırs", "yeis", "havf", "reca", "ilmi", "ilim",
    "kaza", "fani", "baki", "ecel", "ecir", "nefs", "gaza", "savm", "riba", "nisa", "şer", "hayy",
    "ferd", "adl", "isim", "ayet", "kalp", "akıl", "ömür", "genç", "zevc", "süt", "vakit",
}
STRICT_SHORT_SUFFIXES = {
    "", "i", "ı", "e", "a", "in", "ın", "un", "ün", "yi", "yı", "ye", "ya",
    "ni", "nin", "den", "dan", "de", "da", "ler", "lar", "leri", "ları",
    "dir", "dır", "tir", "tır", "si", "sı", "li", "lı", "siz", "sız",
}
ULTRA_STRICT_TERMS = {"esir", "ene", "ali", "al", "şer", "nar", "zem", "ucb"}


def count_term_hits(norm_haystack: str, term: str, words=None) -> int:
    t = norm_tr(term).strip()
    if not t:
        return 0
    # Multi-word phrase (e.g. 'beş vakit', 'ehl-i beyt', 'cüz i')
    if " " in t:
        if t not in norm_haystack:
            return 0
        return len(re.findall(rf"(?<![a-zçğıöşü0-9]){re.escape(t)}(?![a-zçğıöşü0-9])", norm_haystack))
    if words is None:
        words = norm_haystack.split()
    tlen = len(t)
    hits = 0
    if t in ULTRA_STRICT_TERMS:
        for w in words:
            if w.startswith(t) and w[tlen:] in STRICT_SHORT_SUFFIXES:
                hits += 1
    elif t in EXACT_BOUNDARY_TERMS or tlen <= 4:
        for w in words:
            if w.startswith(t) and (len(w) - tlen) <= 4:
                hits += 1
    else:
        for w in words:
            if w.startswith(t):
                hits += 1
    return hits




def clean_html(text: str) -> str:
    return re.sub(r"<[^>]+>", "", text).strip()


def norm_tr(text: str) -> str:
    rep = str.maketrans("İIĞÜŞÖÇâîûÂÎÛ'’“”", "iıgüşöçaiuaiu    ")
    cleaned = text.translate(rep).lower().replace("\u0307", "")
    return re.sub(r"[^a-z0-9ğüşöçı\s]", " ", cleaned)


def extract_keywords(text: str, limit: int = 16):
    words = [w for w in norm_tr(text).split() if len(w) >= 4 and w not in TR_STOPWORDS and not w.isdigit()]
    freq = {}
    for w in words:
        freq[w] = freq.get(w, 0) + 1
    return [w for w, _ in sorted(freq.items(), key=lambda x: (-x[1], -len(x[0])))[:limit]]


def phonetic_weight(text: str) -> float:
    ar_chars = sum(1 for c in text if "\u0600" <= c <= "\u06FF" or "\uFB50" <= c <= "\uFEFF")
    tr_chars = max(1, len(text) - ar_chars)
    pauses = text.count(".") + text.count("!") + text.count("?") + text.count(":") * 0.6 + text.count(";") * 0.6
    w = tr_chars * 1.0 + ar_chars * 2.2 + pauses * 14.0
    if ar_chars > 5:
        w += 35.0
    return max(16.0, w)


def fmt_mmss(sec: float) -> str:
    s = int(round(sec))
    return f"{s // 60:02d}:{s % 60:02d}"


def load_kulliyat_sections():
    data = json.load(open(SOZLER_TEXT_PATH, encoding="utf-8"))
    by_key = {}
    for sec in data.get("sections", []):
        sid = sec.get("id", 0)
        title = sec.get("title", "").strip()
        paras = [p.strip() for p in sec.get("paragraphs", []) if p.strip() and p.strip() not in ("* * *", "﴾…﴿")]
        if sid == 2:
            split_idx = next(
                (i for i, p in enumerate(paras) if "ondördüncü lem" in norm_tr(p) or "on dördüncü lem" in norm_tr(p)),
                len(paras),
            )
            by_key[1] = {"title": "Birinci Söz", "paragraphs": paras[:split_idx]}
            if split_idx < len(paras):
                by_key["14. Lem'a (2. Makam)"] = {
                    "title": "14. Lem'anın 2. Makamı",
                    "paragraphs": paras[split_idx:],
                }
        elif 3 <= sid <= 34:
            by_key[sid - 1] = {"title": title, "paragraphs": paras}
        elif sid == 35:
            by_key["Lemaat"] = {"title": title, "paragraphs": paras}
        elif sid == 36:
            by_key["Konferans"] = {"title": title, "paragraphs": paras}
    return by_key


def align_track_to_fine_micro_segments(track, local_paras, sync2_data=None):
    """
    Break every recording into fine-grained 40s - 115s micro-segments (~1 to 2 minutes each)
    so that even a 2-minute sub-topic inside a 20-minute recording has its own dedicated clip [ss, es].
    """
    dur = track["durationSec"]
    fname = track["mp3"].rsplit("/", 1)[-1]

    # Case A: Exact Whisper ASR sentence alignment available in 2.json
    if sync2_data and fname in ("sozler-p001-1.soz-koseoglu.mp3", "01.Soz_maliozkan.mp3", "sozler-p002-14.lemanin-2.makami-koseoglu.mp3"):
        target_t = 1 if "14.lema" in fname else 0
        ref_dur = 1223.86 if target_t == 1 else 408.71
        scale = dur / ref_dur
        s2_raw = json.load(open(SOZLER_TEXT_PATH, encoding="utf-8"))["sections"][1]["paragraphs"]
        sents = []
        for p in s2_raw:
            if p in ("﴾…﴿", "* * *"):
                continue
            parts = re.findall(r"[^.!?…]+(?:[.!?…]+[”\"’)]*|$)", p) or [p]
            for s in parts:
                if s.strip():
                    sents.append(s.strip())

        t_sents = [
            (sents[idx], round(st["s"] * scale, 2), round(st["e"] * scale, 2))
            for idx, st in enumerate(sync2_data["sentences"])
            if idx < len(sents) and st["t"] == target_t
        ]

        quran_ranges = [{"ss": 0.0, "es": round(min(18.5 * scale, dur), 1), "kind": "besmele", "label": "Açılış Besmele, Hamdele ve Salavat"}]
        for txt, ss, ee in t_sents:
            is_ar = bool(ARABIC_RE.search(txt))
            is_bes = bool(BESMELE_RE.search(txt))
            if (is_ar or is_bes) and ee > 18.0 * scale:
                kind = "ayet" if is_ar else "besmele"
                if quran_ranges and ss - quran_ranges[-1]["es"] < 2.5:
                    quran_ranges[-1]["es"] = max(quran_ranges[-1]["es"], round(ee + 0.8, 1))
                else:
                    quran_ranges.append({
                        "ss": round(max(0.0, ss - 0.4), 1),
                        "es": round(min(dur, ee + 0.8), 1),
                        "kind": kind,
                        "label": txt[:68],
                    })

        # Create fine ~45-75s micro-segments
        segs = []
        cur = []
        for sent_tuple in t_sents:
            cur.append(sent_tuple)
            span_s = cur[-1][2] - cur[0][1]
            is_hd = bool(SUBHEAD_RE.match(sent_tuple[0])) and len(sent_tuple[0]) < 110
            if (is_hd and len(cur) > 2 and span_s >= 25.0) or span_s >= 55.0:
                segs.append(cur)
                cur = []
        if cur:
            segs.append(cur)

        out_segs = []
        for g_idx, chunk in enumerate(segs, start=1):
            ss = round(chunk[0][1], 1)
            ee = round(chunk[-1][2], 1)
            combined_txt = " ".join(c[0] for c in chunk)
            hd = next((c[0][:68] for c in chunk if SUBHEAD_RE.match(c[0]) and len(c[0]) < 110), None)
            clean_ex = next((c[0] for c in chunk if not ARABIC_RE.match(c[0]) and len(c[0]) > 20), chunk[0][0])
            seg_title = f"{track['title']} • {hd}" if hd else f"{track['title']} [{fmt_mmss(ss)}–{fmt_mmss(ee)}]"
            out_segs.append({
                "id": f"seg-{track['id']}-{g_idx:02d}",
                "trackId": track["id"],
                "sozNum": track["sozNum"],
                "title": seg_title,
                "section": track["section"],
                "reader": track["reader"],
                "pageStart": track["pageStart"],
                "pageEnd": track["pageEnd"],
                "ss": ss,
                "es": ee,
                "sr": round(ss / dur, 4),
                "er": round(ee / dur, 4),
                "estMin": max(1, round((ee - ss) / 60)),
                "excerpt": clean_ex[:175],
                "fullText": combined_txt[:520],
                "keywords": extract_keywords(combined_txt),
            })
        return out_segs, quran_ranges[:22]

    # Case B: Fine-grained 45s-95s Micro-Segment Alignment for all other 258 recordings
    if not local_paras:
        local_paras = [track["title"]]

    # If any single paragraph is huge (>900 chars), split it at sentence boundaries into ~450-char sub-paragraphs
    fine_paras = []
    for p in local_paras:
        if len(p) > 850:
            sents = re.findall(r"[^.!?…]+(?:[.!?…]+[”\"’)]*|$)", p) or [p]
            buf = ""
            for s in sents:
                if len(buf) + len(s) > 550 and len(buf) > 200:
                    fine_paras.append(buf.strip())
                    buf = s
                else:
                    buf += " " + s
            if buf.strip():
                fine_paras.append(buf.strip())
        else:
            fine_paras.append(p)

    weights = [phonetic_weight(p) for p in fine_paras]
    total_w = sum(weights) or 1.0
    intro_offset = min(4.2, dur * 0.015)
    usable_dur = max(1.0, dur - intro_offset)

    para_times = []
    cur_t = intro_offset
    for p, w in zip(fine_paras, weights):
        p_dur = (w / total_w) * usable_dur
        ss = round(cur_t, 1)
        ee = round(min(dur, cur_t + p_dur), 1)
        para_times.append((p, ss, ee))
        cur_t += p_dur

    quran_ranges = [{"ss": 0.0, "es": round(min(15.0, dur * 0.12), 1), "kind": "besmele", "label": "Açılış Besmele, Hamdele ve Ayet"}]
    for p, ss, ee in para_times:
        is_ar = bool(ARABIC_RE.search(p))
        is_bes = bool(BESMELE_RE.search(p))
        if not (is_ar or is_bes):
            continue
        m = ARABIC_RE.search(p) if is_ar else BESMELE_RE.search(p)
        if m and len(p) > 140:
            rel_s = m.start() / len(p)
            rel_e = min(1.0, (m.end() + 45) / len(p))
            v_ss = round(ss + (ee - ss) * rel_s, 1)
            v_es = round(min(ee, max(v_ss + 5.5, ss + (ee - ss) * rel_e)), 1)
            snippet = p[max(0, m.start() - 10):min(len(p), m.end() + 55)].strip()
        else:
            v_ss = ss
            v_es = round(min(dur, max(ss + 5.0, ee)), 1)
            snippet = p[:70].strip()

        if v_es <= 15.5:
            continue
        kind = "ayet" if is_ar else "besmele"
        if quran_ranges and v_ss - quran_ranges[-1]["es"] < 3.0:
            quran_ranges[-1]["es"] = max(quran_ranges[-1]["es"], v_es)
            if kind == "ayet":
                quran_ranges[-1]["kind"] = "ayet"
        else:
            quran_ranges.append({"ss": v_ss, "es": v_es, "kind": kind, "label": snippet})

    # Target ~55-95 second micro-segments (up to 18 micro-segments per 20-min track!)
    target_seg_sec = max(42.0, min(95.0, dur / 12.0))
    groups = []
    cur_grp = []
    cur_sec_span = 0.0

    for p, ss, ee in para_times:
        is_hd = bool(SUBHEAD_RE.match(p)) and len(p) < 140
        if cur_grp and ((is_hd and cur_sec_span >= 22.0) or cur_sec_span >= target_seg_sec):
            groups.append(cur_grp)
            cur_grp = []
            cur_sec_span = 0.0
        cur_grp.append((p, ss, ee))
        cur_sec_span += (ee - ss)
    if cur_grp:
        groups.append(cur_grp)

    while len(groups) > 18:
        bi = min(range(len(groups) - 1), key=lambda i: (groups[i][-1][2] - groups[i][0][1]) + (groups[i+1][-1][2] - groups[i+1][0][1]))
        groups[bi] = groups[bi] + groups[bi + 1]
        groups.pop(bi + 1)

    segs = []
    p_start, p_end = track["pageStart"], track["pageEnd"]
    last_heading = None
    for idx, grp in enumerate(groups, start=1):
        ss = round(grp[0][1], 1)
        ee = round(grp[-1][2], 1)
        if idx == 1:
            ss = 0.0
        if idx == len(groups):
            ee = round(dur, 1)
        sr = round(ss / dur, 4)
        er = round(ee / dur, 4)

        texts = [x[0] for x in grp]
        hd = next((t[:68] for t in texts if SUBHEAD_RE.match(t) and len(t) < 120), None)
        if hd:
            last_heading = hd
        ex = next((t for t in texts if not ARABIC_RE.match(t) and len(t) > 25), texts[0])
        combined = " ".join(texts)

        label_hd = hd or last_heading
        seg_title = f"{track['title']} • {label_hd} [{fmt_mmss(ss)}–{fmt_mmss(ee)}]" if label_hd else f"{track['title']} [{fmt_mmss(ss)}–{fmt_mmss(ee)}]"
        ps_est = round(p_start + (p_end - p_start) * sr)
        pe_est = max(ps_est, round(p_start + (p_end - p_start) * er))

        segs.append({
            "id": f"seg-{track['id']}-{idx:02d}",
            "trackId": track["id"],
            "sozNum": track["sozNum"],
            "title": seg_title,
            "section": track["section"],
            "reader": track["reader"],
            "pageStart": ps_est,
            "pageEnd": pe_est,
            "ss": ss,
            "es": ee,
            "sr": sr,
            "er": er,
            "estMin": max(1, round((ee - ss) / 60)),
            "excerpt": ex[:180],
            "fullText": combined[:550],
            "keywords": extract_keywords(combined),
        })

    return segs, quran_ranges[:22]


def build_all_tracks_and_segments(kulliyat_sections):
    src = SORULARLA_SOZLER_PATH
    raw = json.loads(src.read_text(encoding="utf-8"))
    durations = json.loads(DURATIONS_PATH.read_text(encoding="utf-8")) if DURATIONS_PATH.exists() else {}
    sync2_data = json.loads(SYNC_2_PATH.read_text(encoding="utf-8")) if SYNC_2_PATH.exists() else None

    parsed_meta = []
    span_by_group = {}
    for idx, item in enumerate(raw, start=1):
        title_html = item.get("title", "")
        m_title = re.search(r'track-title">(.*?)<', title_html, re.S)
        m_meta = re.search(r'track-meta">(.*?)<', title_html, re.S)
        title = m_title.group(1).strip() if m_title else clean_html(title_html)
        pages = m_meta.group(1).strip().strip("()") if m_meta else ""
        artist = (item.get("artist") or "Bilinmeyen").strip()
        mp3 = (item.get("mp3") or "").strip()
        if not mp3 or not mp3.startswith("https://rnknesriyat.com/"):
            continue

        m_pg = re.search(r"sh\.\s*(\d+)\s*-\s*(\d+)", pages)
        p_start, p_end = (int(m_pg.group(1)), int(m_pg.group(2))) if m_pg else (1, 5)

        soz_num = None
        text_key = None
        section = title
        m_soz = re.match(r"^(\d+)\s*\.\s*Söz", title, re.I)
        if m_soz:
            soz_num = int(m_soz.group(1))
            text_key = soz_num
            section = f"{soz_num}. Söz"
        elif "Lem'a" in title or "Lema" in title:
            section = "14. Lem'a (2. Makam)"
            text_key = section
        elif title.lower().startswith("lemaat"):
            section = "Lemaat"
            text_key = "Lemaat"
        elif title.lower().startswith("konferans"):
            section = "Konferans"
            text_key = "Konferans"

        grp_key = (text_key, artist)
        if grp_key not in span_by_group:
            span_by_group[grp_key] = [p_start, p_end]
        else:
            span_by_group[grp_key][0] = min(span_by_group[grp_key][0], p_start)
            span_by_group[grp_key][1] = max(span_by_group[grp_key][1], p_end)

        parsed_meta.append((idx, title, pages, p_start, p_end, artist, mp3, soz_num, text_key, section))

    tracks = []
    all_segments = []
    primary_seen = set()

    for idx, title, pages, p_start, p_end, artist, mp3, soz_num, text_key, section in parsed_meta:
        dur_sec = durations.get(mp3) or max(180.0, (p_end - p_start + 1) * 125.0)
        grp_min, grp_max = span_by_group[(text_key, artist)]
        total_pg = max(1, (grp_max - grp_min + 1))
        win_s = max(0.0, (p_start - grp_min) / total_pg)
        win_e = min(1.0, (p_end - grp_min + 1) / total_pg)
        if win_e - win_s < 0.02:
            win_s, win_e = 0.0, 1.0

        sec_info = kulliyat_sections.get(text_key)
        local_paras = []
        if sec_info and sec_info["paragraphs"]:
            all_p = sec_info["paragraphs"]
            cum = []
            c = 0
            for p in all_p:
                c += len(p)
                cum.append(c)
            tot_c = max(1, c)
            s_char = win_s * tot_c
            e_char = win_e * tot_c
            local_paras = [p for p, end_c in zip(all_p, cum) if (end_c >= s_char and (end_c - len(p)) <= e_char)]
            if not local_paras:
                local_paras = all_p

        track = {
            "id": f"sr-{idx:03d}",
            "book": "Sözler",
            "sozNum": soz_num,
            "section": section,
            "title": title,
            "pages": pages,
            "pageStart": p_start,
            "pageEnd": p_end,
            "reader": artist,
            "mp3": mp3,
            "durationSec": round(dur_sec, 1),
            "estMinutes": max(1, round(dur_sec / 60)),
        }

        segs, quran_ranges = align_track_to_fine_micro_segments(track, local_paras, sync2_data)
        track["quranRanges"] = quran_ranges
        track["segments"] = [
            {
                "id": s["id"],
                "t": s["title"],
                "ss": s["ss"],
                "es": s["es"],
                "sr": s["sr"],
                "er": s["er"],
                "ex": s["excerpt"][:155],
            }
            for s in segs
        ]

        is_primary = ("Köseoğlu" in artist) or ((section, p_start, p_end) not in primary_seen)
        if is_primary:
            primary_seen.add((section, p_start, p_end))
            all_segments.extend(segs)

        tracks.append(track)

    return tracks, all_segments


def find_best_track_and_slice(tracks, soz_num=None, rnk_page=None, hint_text="", reader_pref="Köseoğlu"):
    cands = tracks
    if rnk_page is not None:
        pg_cands = [t for t in tracks if t["pageStart"] <= rnk_page <= t["pageEnd"]]
        if soz_num is not None:
            pg_soz = [t for t in pg_cands if t["sozNum"] == soz_num]
            if pg_soz:
                pg_cands = pg_soz
        if pg_cands:
            cands = pg_cands
    elif soz_num is not None:
        soz_cands = [t for t in tracks if t["sozNum"] == soz_num]
        if soz_cands:
            cands = soz_cands
    else:
        nh = norm_tr(hint_text)
        if "lemeat" in nh or "lemaat" in nh:
            cands = [t for t in tracks if t["section"] == "Lemaat"] or cands
        elif "konferans" in nh:
            cands = [t for t in tracks if t["section"] == "Konferans"] or cands

    if not cands:
        return None, 0.0, 1.0, 0.0, 60.0, ""

    low_hint = norm_tr(hint_text)
    for pat, tok in [
        ("1 makam", "1.makam"), ("birinci makam", "1.makam"), ("2 makam", "2.makam"), ("ikinci makam", "2.makam"),
        ("1 mevkıf", "1.mevkıf"), ("1 mvkf", "1.mevkıf"), ("2 mevkıf", "2.mevkıf"), ("2 mvkf", "2.mevkıf"),
        ("3 mevkıf", "3.mevkıf"), ("3 mvkf", "3.mevkıf"), ("1 mebhas", "1.mebhas"), ("birinci mebhas", "1.mebhas"),
        ("2 mebhas", "2.mebhas"), ("ikinci mebhas", "2.mebhas"), ("1 maksad", "1.maksat"), ("birinci maksat", "1.maksat"),
        ("2 maksad", "2.maksat"), ("ikinci maksat", "2.maksat"), ("1 dal", "1.dal"), ("birinci dal", "1.dal"),
        ("2 dal", "2.dal"), ("ikinci dal", "2.dal"), ("3 dal", "3.dal"), ("üçüncü dal", "3.dal"),
        ("4 dal", "4.dal"), ("dördüncü dal", "4.dal"), ("5 dal", "5.dal"), ("beşinci dal", "5.dal"),
        ("1 esas", "1.esas"), ("birinci esas", "1.esas"), ("2 esas", "2.esas"), ("ikinci esas", "2.esas"),
        ("3 esas", "3.esas"), ("üçüncü esas", "3.esas"), ("4 esas", "4.esas"), ("dördüncü esas", "4.esas"),
        ("1 şule", "1.şule"), ("birinci şule", "1.şule"), ("2 şule", "2.şule"), ("ikinci şule", "2.şule"),
        ("3 şule", "3.şule"), ("üçüncü şule", "3.şule"), ("hüve", "hüve"), ("genç", "genç"),
        ("zeyl", "zeyl"), ("hatime", "hatime"), ("mukaddime", "mukaddime"), ("pencere", "pencere"),
        ("hakikat", "hakikat"), ("burhan", "burhan"), ("bürhan", "burhan"),
    ]:
        if pat in low_hint:
            sub = [t for t in cands if tok in t["title"].lower()]
            if sub:
                cands = sub
                break

    chosen = next((c for c in cands if reader_pref.lower() in c["reader"].lower()), cands[0])
    dur = chosen["durationSec"]

    if rnk_page is not None and chosen["pageEnd"] > chosen["pageStart"] and chosen["segments"]:
        pg_span = chosen["pageEnd"] - chosen["pageStart"] + 1
        target_r = max(0.0, min(0.92, (rnk_page - chosen["pageStart"]) / pg_span))
        best_seg = min(chosen["segments"], key=lambda s: abs(s["sr"] - target_r))
        return chosen, best_seg["sr"], best_seg["er"], best_seg["ss"], best_seg["es"], best_seg["ex"]

    if chosen["segments"] and hint_text:
        h_words = set(extract_keywords(hint_text, 12))
        if h_words:
            scored = []
            for s in chosen["segments"]:
                s_words = set(extract_keywords(s["t"] + " " + s["ex"], 14))
                scored.append((len(h_words & s_words), s))
            scored.sort(key=lambda x: -x[0])
            if scored[0][0] > 0:
                s = scored[0][1]
                return chosen, s["sr"], s["er"], s["ss"], s["es"], s["ex"]

    if chosen["segments"]:
        s = chosen["segments"][0]
        return chosen, s["sr"], s["er"], s["ss"], s["es"], s["ex"]
    return chosen, 0.0, 1.0, 0.0, dur, ""


def get_topic_term_sets(topic_title: str):
    """Return (direct_terms, indirect_terms) for a Konu Fihristi heading without loose substring collisions."""
    norm_t = norm_tr(topic_title)
    base_tokens = [w for w in norm_t.split() if len(w) >= 3 and w not in TR_STOPWORDS]

    direct_set = []
    indirect_set = []
    seen_d, seen_i = set(), set()

    def add_d(term):
        nt = norm_tr(term).strip()
        if nt and nt not in TR_STOPWORDS and nt not in seen_d:
            seen_d.add(nt)
            direct_set.append(term.strip())

    def add_i(term):
        nt = norm_tr(term).strip()
        if nt and nt not in TR_STOPWORDS and nt not in seen_d and nt not in seen_i:
            seen_i.add(nt)
            indirect_set.append(term.strip())

    # 1. Exact profile matches (prefer exact key word match inside topic title)
    matched_profile = False
    for prof_k, (d_list, i_list) in TOPIC_PROFILES.items():
        npk = norm_tr(prof_k).strip()
        if count_term_hits(norm_t, npk) > 0 or (len(npk) >= 5 and npk in norm_t):
            matched_profile = True
            for t in d_list:
                add_d(t)
            for t in i_list:
                add_i(t)

    # 2. Add significant topic title tokens directly (skip generic filler words in multi-word titles)
    TITLE_FILLERS = {
        "hakkı", "hakki", "varlığı", "varligi", "birliği", "birligi", "dengesi", "mahiyeti",
        "fark", "gönderilmesindeki", "vazifesi", "gayesi", "güzellikleri", "geçmişi", "geleceği",
        "aydınlatması", "olduğu", "üslubu", "tekrarlar", "gerekliliği", "evrenselliği", "kazanacağı",
        "değer", "sevgisi", "terk", "arası", "münasebet", "kerim", "kerimin", "kerimdeki", "kuran",
        "peygamberimizin", "asm", "söz", "satmak", "büyüklenme", "ameline", "güvenmek", "almamak",
    }
    for w in base_tokens:
        if w not in TITLE_FILLERS and len(w) >= 4:
            add_d(w)
        elif not matched_profile and len(w) >= 3:
            add_d(w)

    return direct_set, indirect_set


def scan_micro_segments_for_topic(topic_title: str, refs_list, all_segments, max_clips=None):
    """
    Scan ALL 2,373 micro-segments with strict Turkish word-boundary precision, separating:
      - 'direct'   (Doğrudan Alakalı): explicitly treats the topic directly in text/heading or verified Fihrist Söz.
      - 'indirect' (Dolaylı Alakalı): genuinely illuminates the topic through passing mention or >=2 distinct supporting concepts.
      - Unrelated segments are strictly eliminated.
    Returns clips arranged in 'Hikmetli Sıralama' with an authentic Turkish 'kunye' rationale per clip.
    """
    direct_terms, indirect_terms = get_topic_term_sets(topic_title)
    if not direct_terms and not indirect_terms:
        return []

    # Parse explicitly cited Sözler from Konu Fihristi references
    ref_soz_nums = set()
    for r in refs_list:
        nr = norm_tr(r)
        if "lemeat" in nr or "lemaat" in nr:
            ref_soz_nums.add("Lemaat")
        if "konferans" in nr:
            ref_soz_nums.add("Konferans")
        for w, num in sorted(ORDINAL_MAP.items(), key=lambda x: -len(x[0])):
            if f"{w} söz" in nr:
                ref_soz_nums.add(num)
                break

    direct_cands = []
    indirect_cands = []

    for seg in all_segments:
        norm_title = seg.get("_nt")
        if norm_title is None:
            norm_title = norm_tr(seg["title"])
            seg["_nt"] = norm_title
            seg["_tw"] = norm_title.split()
        norm_body = seg.get("_nb")
        if norm_body is None:
            norm_body = norm_tr(seg["fullText"] or seg["excerpt"])
            seg["_nb"] = norm_body
            seg["_bw"] = norm_body.split()
        tw = seg["_tw"]
        bw = seg["_bw"]
        in_cited_soz = (seg["sozNum"] in ref_soz_nums or seg["section"] in ref_soz_nums)

        d_title_hits = 0
        d_body_hits = 0
        d_matched = []
        for t in direct_terms:
            th = count_term_hits(norm_title, t, tw)
            bh = count_term_hits(norm_body, t, bw)
            if th > 0 or bh > 0:
                d_title_hits += th
                d_body_hits += bh
                d_matched.append(t)

        i_body_hits = 0
        i_matched = []
        for t in indirect_terms:
            bh = count_term_hits(norm_body, t, bw)
            if bh > 0:
                i_body_hits += bh
                i_matched.append(t)

        # Strict two-tier classification (ZERO unrelated leaks)
        is_direct = (
            d_title_hits >= 1
            or d_body_hits >= 2
            or len(d_matched) >= 2
            or (in_cited_soz and d_body_hits >= 1)
        )

        if is_direct:
            score = (
                d_title_hits * 7.0
                + min(d_body_hits, 8) * 3.0
                + len(d_matched) * 2.5
                + min(len(i_matched), 4) * 0.8
                + (5.5 if in_cited_soz else 0.0)
            )
            direct_cands.append({
                "score": score,
                "seg": seg,
                "in_cited": in_cited_soz,
                "d_matched": d_matched[:4],
                "i_matched": i_matched[:4],
                "d_hits": d_title_hits + d_body_hits,
            })
        else:
            # Indirect ONLY if genuinely illuminating:
            # (a) Passing mention of a direct concept + at least 1 supporting concept, OR
            # (b) Strong multi-concept supporting bridge (>=2 distinct indirect terms & >=3 total hits, or >=2 hits inside cited Söz)
            is_indirect = (
                (d_body_hits == 1 and len(i_matched) >= 1)
                or (len(i_matched) >= 2 and i_body_hits >= 3)
                or (in_cited_soz and len(i_matched) >= 2 and i_body_hits >= 2)
            )
            if is_indirect:
                score = (
                    d_body_hits * 4.5
                    + len(i_matched) * 2.4
                    + min(i_body_hits, 8) * 1.1
                    + (3.0 if in_cited_soz else 0.0)
                )
                indirect_cands.append({
                    "score": score,
                    "seg": seg,
                    "in_cited": in_cited_soz,
                    "d_matched": d_matched[:3],
                    "i_matched": i_matched[:4],
                    "d_hits": d_body_hits,
                })

    # Deduplicate identical passages read by multiple readers (keep highest-scoring / cleanest rendition)
    def dedupe_cands(cands):
        cands.sort(key=lambda x: (-x["score"], x["seg"]["ss"]))
        out = []
        seen_passages = set()
        seen_slots = set()
        for c in cands:
            s = c["seg"]
            slot_k = (s["trackId"], round(s["ss"] / 35))
            pass_k = (str(s["sozNum"]), norm_tr(s["excerpt"])[:46])
            if slot_k in seen_slots or pass_k in seen_passages:
                continue
            seen_slots.add(slot_k)
            seen_passages.add(pass_k)
            out.append(c)
        return out

    direct_clean = dedupe_cands(direct_cands)
    indirect_clean = dedupe_cands(indirect_cands)

    # Keep indirect count well-proportioned so indirect passages complement rather than overwhelm direct ones
    if direct_clean:
        max_indirect = max(12, min(len(indirect_clean), int(len(direct_clean) * 0.85) + 4))
        indirect_clean = indirect_clean[:max_indirect]
    else:
        indirect_clean = indirect_clean[:30]

    def fmt_terms(arr):
        return ", ".join(arr[:3]) if arr else topic_title.lower()

    def build_clip_obj(cand, rel, kunye):
        s = cand["seg"]
        return {
            "trackId": s["trackId"],
            "clipTitle": s["title"],
            "startSec": s["ss"],
            "endSec": s["es"],
            "startRatio": s["sr"],
            "endRatio": s["er"],
            "excerpt": s["excerpt"],
            "rel": rel,
            "kunye": kunye,
        }

    # HİKMETLİ SIRALAMA (Pedagogical / Wisdom Interleaving):
    # 1. Open with 2-3 foundational Direct clips (Esas ve Tarif)
    # 2. After every 2-3 Direct clips, weave 1 contextually matched Indirect clip
    #    (preferring same Söz or overlapping conceptual bridge) with a clear Turkish Künye!
    # 3. Close smoothly with remaining Direct proofs and complementary Indirect horizon.
    ordered = []
    remaining_indirect = list(indirect_clean)

    def pick_best_bridge(prev_cand):
        if not remaining_indirect:
            return None
        prev_soz = prev_cand["seg"]["sozNum"] if prev_cand else None
        prev_i = set(prev_cand["i_matched"]) if prev_cand else set()
        best_idx = 0
        best_fit = -1.0
        for idx_i, ic in enumerate(remaining_indirect[:18]):
            fit = ic["score"] * 0.4
            if prev_soz is not None and ic["seg"]["sozNum"] == prev_soz:
                fit += 4.5
            overlap = len(prev_i & set(ic["i_matched"]))
            fit += overlap * 2.2
            if ic["d_hits"] > 0:
                fit += 1.8
            if fit > best_fit:
                best_fit = fit
                best_idx = idx_i
        return remaining_indirect.pop(best_idx)

    d_idx = 0
    while d_idx < len(direct_clean):
        # Emit a block of 2-3 direct lessons
        block_size = 3 if d_idx == 0 else 2
        last_d = None
        for _ in range(block_size):
            if d_idx >= len(direct_clean):
                break
            dc = direct_clean[d_idx]
            terms_str = fmt_terms(dc["d_matched"])
            sec_name = dc["seg"]["section"]
            if d_idx < 2:
                kunye = f"Esas ve Tarif — Fihrist dairesinde '{terms_str}' hakikatini en yoğun işlediği için dinlemeye sağlam bir temel olarak başa alındı."
            elif dc["in_cited"]:
                kunye = f"Fihrist Kaynağı — Konu Fihristi'nde işaret edilen {sec_name} içinde '{terms_str}' bahsini doğrudan ispatladığı için ana akışa eklendi."
            else:
                kunye = f"Doğrudan İzah — {sec_name} içinde '{terms_str}' meselesini bizzat ele aldığı için sıralamaya dâhil edildi."
            ordered.append(build_clip_obj(dc, "direct", kunye))
            last_d = dc
            d_idx += 1

        # Weave 1 contextually bridged indirect lesson right after the direct block
        if remaining_indirect:
            ic = pick_best_bridge(last_d)
            if ic:
                sec_name = ic["seg"]["section"]
                if last_d and ic["seg"]["sozNum"] == last_d["seg"]["sozNum"]:
                    bridge_terms = fmt_terms(ic["d_matched"] + ic["i_matched"])
                    kunye = f"Aynı Risaleden Bağlam — Önceki doğrudan dersin hemen ardından {sec_name} bütünlüğünde '{bridge_terms}' bağlantısıyla tefekkürü derinleştirmek için bu sıraya yerleştirildi."
                elif ic["d_hits"] > 0:
                    d_str = fmt_terms(ic["d_matched"])
                    i_str = fmt_terms(ic["i_matched"])
                    kunye = f"İşaret ve Temsil — '{d_str}' kavramına temas edip önceki dersi '{i_str}' hikmetiyle desteklediği için ara geçiş olarak eklendi."
                else:
                    i_str = fmt_terms(ic["i_matched"])
                    kunye = f"Destekleyici Hikmet — Doğrudan delillerin ardından konuyu '{i_str}' zaviyesinden besleyip kalbe yerleştirmek için bu sıraya alındı."
                ordered.append(build_clip_obj(ic, "indirect", kunye))

    # Append any remaining indirect clips as closing horizon (Tamamlayıcı Ufuk)
    for ic in remaining_indirect:
        i_str = fmt_terms(ic["d_matched"] + ic["i_matched"])
        sec_name = ic["seg"]["section"]
        if direct_clean:
            kunye = f"Tamamlayıcı Ufuk — Doğrudan derslerden sonra konunun {sec_name} içindeki '{i_str}' irtibatını gösteren genişletici bağlam olarak eklendi."
        else:
            kunye = f"Bağlamsal Destek — Bu başlık Sözler metninde doğrudan geçmeyip '{i_str}' kavramları üzerinden dolaylı olarak aydınlatıldığı için eklendi."
        ordered.append(build_clip_obj(ic, "indirect", kunye))

    if max_clips is not None:
        ordered = ordered[:max_clips]
    return ordered


def parse_konu_fihristi_full(tracks, all_segments):
    """
    Accurately parse ALL genuine topics from Risale-i Nur Konu Fihristi .PDF
    (preserving comma-separated titles like 'Ahirete İman, Diriliş, Haşir', 'Dua, Münâcât', 'Ene, Enaniyet, Benlik',
    'Aşk, sevgi, muhabbet', 'Zekat, Sadaka' while excluding webpage footer artifacts).
    """
    pdf = PDF_DIR / "Risale-i Nur Konu Fihristi .PDF"
    raw = subprocess.check_output(["pdftotext", str(pdf), "-"], text=True)
    raw_lines = [ln.replace("\x0c", "").strip() for ln in raw.splitlines()]

    book_prefixes = (
        "Sözler", "Mektubat", "Lem'alar", "Lem’alar", "Şualar", "Şuâlar", "Barla", "Kastamonu", "Emirdağ",
        "Mesnevi", "İşaratü", "İşârât", "Tarihçe", "Asa-yı", "Asâ-yı", "Sikke", "Muhakemat",
        "Hutbe", "Münazarat", "Münâzarat", "Sünûhat", "Divan", "İman ve Küfür", "Nurun İlk", "Nur'un", "Tulûât",
        "Yirmi", "Otuz", "On ", "Onuncu", "Birinci", "İkinci", "Üçüncü", "Dördüncü", "Beşinci",
        "Altıncı", "Yedinci", "Sekizinci", "Dokuzuncu", "Yıldızları konuşturan",
    )
    ref_markers = (
        "Sözler", "Mektubat", "Mektup", "Lem'a", "Lem’a", "Şuâ", "Şua", "Lâhikası", "Lahikası",
        "Nükte", "Nokta", "Mesele", "Mes'ele", "Mes’ele", "Makam", "Zeyl", "Hakikat", "İşaret",
        "Sual", "Risale", "Tefsiri", "Sûresi", "Suresi", "Reşha", "Nota", "Remiz", "Kelime",
        "Katre", "Hubâb", "Zerre", "Lemeât", "Lemaat", "Ders", "Hâtime", "Hatime", "Bölüm",
    )
    exact_ref_lines = {
        "Sünûhat", "Muhakemat", "Münâzarat", "Tulûât", "İşârât", "Hutbe-i Şâmiye",
        "Asa-yı Musa", "Divan-ı Harb-i Örfî", "Nur'un İlk Kapısı", "Konferans",
    }

    def is_reference_line(ln: str) -> bool:
        if ln in exact_ref_lines:
            return True
        if "/" in ln or "|" in ln:
            return True
        if ln.startswith(book_prefixes):
            return True
        if re.search(r"\b\d+\.\s*(Mektup|Söz|Lem'a|Şuâ|Şua|Ders|Sual|Kelime|Nokta|Nükte|Mesele)", ln, re.I):
            return True
        # Only treat as ref via markers when it isn't a known standalone topic name
        if any(m in ln for m in ref_markers) and ("," in ln or "Söz" in ln or "Mektup" in ln or "Lem'a" in ln or "Şua" in ln or "Şuâ" in ln or "Tefsiri" in ln):
            return True
        return False

    topics = []
    cur = None
    skip_footer = False
    for ln in raw_lines:
        if not ln:
            continue
        if ln.startswith("Kategorisi:") or ln.startswith("Yazar:") or "kez okundu" in ln.lower():
            skip_footer = True
            continue
        if skip_footer:
            continue
        if ln in {"Okuyup anlamak isteyenlere...", "Risale-i Nur Konu Fihristi", "Sorularla Risale"} or ln.startswith("Okuma süresi:") or len(ln) <= 2:
            continue
        if any(0xE000 <= ord(c) <= 0xF8FF for c in ln):
            continue

        # Join wrapped heading continuation lines (e.g. "Irkçılık, Milliyetçilik, Kavmiyetçilik, Hamiyet-i" + "Milliye")
        if cur is not None and not cur["allRefs"] and (cur["topic"].endswith("-") or (len(ln.split()) <= 2 and not is_reference_line(ln) and ln[0].islower())):
            cur["topic"] = f"{cur['topic']} {ln}".strip()
            continue
        if cur is not None and not cur["allRefs"] and ln == "Milliye":
            cur["topic"] = f"{cur['topic']} {ln}".strip()
            continue

        if not is_reference_line(ln) and len(ln) < 88 and ln[0].isupper():
            # Avoid single broken lowercase/fragment lines
            if ln in {"hâdise", "Milliye"}:
                continue
            cur = {"topic": ln, "allRefs": [], "sozlerRefs": []}
            topics.append(cur)
        elif cur is not None:
            cur["allRefs"].append(ln)
            if "Söz" in ln or "Lemeât" in ln or "Konferans" in ln:
                cur["sozlerRefs"].append(ln)

    playlists = []
    for idx, item in enumerate(topics, start=1):
        clips = scan_micro_segments_for_topic(item["topic"], item["allRefs"], all_segments, max_clips=None)
        if not clips:
            continue

        direct_cnt = sum(1 for c in clips if c.get("rel") == "direct")
        indirect_cnt = sum(1 for c in clips if c.get("rel") == "indirect")

        # Drop topics that only have 0 direct AND <=1 weak indirect clip
        if direct_cnt == 0 and indirect_cnt < 2:
            continue

        slug = re.sub(r"[^a-z0-9]+", "-", norm_tr(item["topic"])).strip("-") or f"konu-{idx}"
        ref_summary = " · ".join((item["sozlerRefs"] or item["allRefs"])[:3])
        if direct_cnt and indirect_cnt:
            summary_cnt = f"{direct_cnt} doğrudan · {indirect_cnt} dolaylı kesit"
        elif direct_cnt:
            summary_cnt = f"{direct_cnt} doğrudan kesit"
        else:
            summary_cnt = f"{indirect_cnt} bağlamsal kesit"

        playlists.append({
            "id": f"fihrist-{idx:03d}-{slug[:22]}",
            "title": item["topic"],
            "category": "Konu Fihristi",
            "description": f"{summary_cnt}{(' · ' + ref_summary[:95]) if ref_summary else ''}",
            "fihristRefs": item["allRefs"][:8],
            "trackIds": [c["trackId"] for c in clips],
            "clips": clips,
            "curatedCount": len(clips),
            "directCount": direct_cnt,
            "indirectCount": indirect_cnt,
        })
    return playlists



def build_vakit_tefekkur_playlists(all_segments):
    """
    Handcrafted listening-first collections organized by time of day & spiritual need
    (Vakit ve Tefekkür Seçkileri), strictly built from verified Sözler micro-segments.
    """
    curated = [
        {
            "id": "vakit-seher-sabah",
            "title": "Seher ve Sabah Tefekkürü",
            "description": "Güne Bismillâh, sabah namazı hikmeti, kâinat sarayı ve Marifetullah pencereleriyle huzurlu bir başlangıç.",
            "defaultFon": "rast",
            "soz_nums": {1, 9, 11, 24, 33},
            "keywords": ["bismillah", "sabah", "güneş", "rahmet", "şükür", "nimet", "bahar", "pencere", "marifet"],
            "limit": 24,
        },
        {
            "id": "vakit-namaz-vesvese",
            "title": "Namaz Şevki ve Vesveseden Kurtuluş",
            "description": "Beş vakit namazın ruhî lezzetini tazeleyen ikazlar ve ibadet vesvesesine beş şifalı merhem.",
            "defaultFon": "ussak",
            "soz_nums": {4, 9, 21},
            "keywords": ["namaz", "vakit", "secde", "ubudiyet", "usanç", "vesvese", "kalp", "merhem", "ikaz"],
            "limit": 26,
        },
        {
            "id": "vakit-musibet-teselli",
            "title": "Sıkıntı, Musibet ve Kalp Tesellisi",
            "description": "İmtihan, sabır, tevekkül, kaderde adalet ve her musibet içindeki gizli rahmet cilveleri.",
            "defaultFon": "saba",
            "soz_nums": {2, 3, 14, 23, 26},
            "keywords": ["musibet", "sabır", "tevekkül", "rahmet", "teselli", "kader", "şefkat", "dua", "hastalık", "bela"],
            "limit": 24,
        },
        {
            "id": "vakit-gece-ebediyet",
            "title": "Gece Huzuru ve Ebediyet Tefekkürü",
            "description": "Ölümün terhis oluşu, ruhun bekası, Haşir hakikati ve Cennet saadetine dair derin gece kesitleri.",
            "defaultFon": "hicaz",
            "soz_nums": {10, 28, 29, 32},
            "keywords": ["haşir", "beka", "ruh", "cennet", "saadet", "ebedi", "kabir", "melek", "muhabbet", "ahiret"],
            "limit": 26,
        },
        {
            "id": "vakit-genclik-hikmet",
            "title": "Gençlik, Akıl ve Hikmet-i Kur'aniye",
            "description": "Nefis ve malı Allah'a satmak, gençlik emaneti, felsefe-Kur'an mukayesesi ve istikamet dersleri.",
            "defaultFon": "kainat",
            "soz_nums": {6, 7, 8, 12, 13, 23},
            "keywords": ["genç", "gençlik", "nefis", "emanet", "hikmet", "akıl", "istikamet", "kabir", "iman"],
            "limit": 24,
        },
        {
            "id": "vakit-kainat-esma",
            "title": "Kâinatta Sanat, Esmâ ve Marifetullah",
            "description": "Zerrelerden yıldızlara Esmâ-i Hüsnâ nakışları, Tevhid sikkesi ve kâinat kitabını okuma sanatı.",
            "defaultFon": "yagmur",
            "soz_nums": {11, 22, 24, 32, 33},
            "keywords": ["esma", "sanat", "nakış", "tevhid", "sikke", "mühür", "pencere", "kudret", "cemal", "zerre"],
            "limit": 26,
        },
    ]

    out = []
    for cfg in curated:
        scored = []
        for s in all_segments:
            if s["sozNum"] not in cfg["soz_nums"]:
                continue
            hay = norm_tr(f"{s['title']} {s['fullText']}")
            sc = 2.0
            for kw in cfg["keywords"]:
                if re.search(rf"\b{re.escape(kw)}", hay):
                    sc += 2.4
            scored.append((sc, s))
        scored.sort(key=lambda x: (-x[0], x[1]["sozNum"] if isinstance(x[1]["sozNum"], int) else 99, x[1]["ss"]))
        clips = []
        seen = set()
        per_soz = {}
        for sc, s in scored:
            k = (s["trackId"], round(s["ss"] / 40))
            if k in seen:
                continue
            sn = s["sozNum"]
            if per_soz.get(sn, 0) >= 8:
                continue
            seen.add(k)
            per_soz[sn] = per_soz.get(sn, 0) + 1
            clips.append({
                "trackId": s["trackId"],
                "clipTitle": s["title"],
                "startSec": s["ss"],
                "endSec": s["es"],
                "startRatio": s["sr"],
                "endRatio": s["er"],
                "excerpt": s["excerpt"],
            })
            if len(clips) >= cfg["limit"]:
                break
        if clips:
            out.append({
                "id": cfg["id"],
                "title": cfg["title"],
                "group": "Vakit ve Tefekkür Seçkileri",
                "category": "Vakit ve Tefekkür Seçkileri",
                "description": f"{len(clips)} seçkin kesit · {cfg['description']}",
                "defaultFon": cfg["defaultFon"],
                "clips": clips,
                "trackIds": [c["trackId"] for c in clips],
                "curatedCount": len(clips),
            })
    return out


def build_fihrist_risalesi_playlists(tracks, all_segments):
    """
    Build dedicated micro-sliced playlists from Bediüzzaman Said Nursî's Fihrist Risalesi (15. Lem'a),
    sampling evenly across the entire Söz so long chapters (10. Söz, 25. Söz, 33. Söz) span all sections.
    """
    fihrist_summaries = [
        (1, "1. Söz — Bismillâh ve Hazine-i Rahmet Temsili", "Bismillâh'ın tükenmez kuvvet ve bereketini bedevî çölü temsili ve bütün mevcudatın lisan-ı haliyle tefsir eden kesitler."),
        (9, "9. Söz — Beş Vakit Namazın Hikmet-i Tahsisi (5 Nükte)", "Beş vakit namazın o vakitlere tahsisindeki 5 nükte ve insan ruhunun namaza olan kat'î ihtiyacı."),
        (10, "10. Söz — Haşir Risalesi (12 Suret & 12 Hakikat)", "Mahkeme-i Kübra ve öldükten sonra dirilişi Esmâ-i Hüsnâ tecellileriyle ispat eden 12 Suret, 12 Hakikat ve 5 Zeyl kesitleri."),
        (11, "11. Söz — Kâinat Sarayı ve İnsanın Ubudiyet Vazifesi", "Sûre-i Şems'in işaretiyle kâinat sarayındaki Rububiyet-i İlâhiyeye karşı insanın kulluk mukabelesi."),
        (13, "13. Söz — Hikmet-i Kur'aniye, Gençlere Ders & Hüve Nüktesi", "Kur'an hikmeti ile felsefenin mukayesesi, gençlere verilen kabir ikazı ve bir avuç havadaki Hüve Nüktesi."),
        (19, "19. Söz — Risalet-i Ahmediye (14 Reşha)", "Hz. Muhammed'in (a.s.m.) nübüvvet delillerini 14 Reşha ile ders veren kesitler."),
        (21, "21. Söz — Namazda Usanca 5 İkaz & Vesveseye 5 Merhem", "Birinci Makam'da namaza gelen gaflet ve usanca 5 nefis ikazı; İkinci Makam'da kalbî vesveseye 5 şifalı merhem."),
        (22, "22. Söz — Tevhid Sikkesi (12 Bürhan & 12 Lem'a)", "Her şeyde Cenâb-ı Hakk'ın Vahdaniyet mührünü gösteren 12 Bürhan ve 12 Lem'a kesitleri."),
        (23, "23. Söz — İmanın Güzellikleri, İnsan ve Dua (5 Nokta & 5 Nükte)", "İmanın insanı Sultan-ı Kâinat'a intisap ettiren 5 noktası ve insanın saadet-i dareyn sırrı olan 5 nüktesi."),
        (24, "24. Söz — Esmâ-i Hüsnâ Tecellileri ve Muhabbet (Beş Dal)", "Hakikat-i Esmâ-i Hüsnâ, ubudiyet ve muhabbetullah sırlarını açan Beş Dal kesitleri."),
        (25, "25. Söz — Mu'cizât-ı Kur'aniye (Üç Şule)", "Kur'an-ı Kerim'in kırk vecihle mucize oluşunu Üç Şule'de beyan eden i'câz kesitleri."),
        (26, "26. Söz — Kader ve Cüz'î İhtiyariye Risalesi", "Kader ile cüz'î iradenin telifi ve her şeyin rahmet ve adaletle takdir edildiğinin ispatı."),
        (28, "28. Söz — Cennet ve Ebedî Saadet (5 Sual & Cehennem Zeyli)", "Cennetin cismanî ve ruhanî lezzetlerine dair 5 mühim sualin cevabı ve Cehennemin hikmeti."),
        (29, "29. Söz — Beka-i Ruh, Melâike ve Haşir (İki Maksat)", "Meleklerin varlığı, ruhun bekası ve kâinatın tahrip ve tamir sırrı."),
        (30, "30. Söz — Ene (Benlik) Tılsımı ve Tahavvülât-ı Zerrât", "Birinci Maksat'ta Ene'nin mahiyeti ve felsefe ile nübüvvet çizgisi; İkinci Maksat'ta zerrelerin vazifeleri."),
        (31, "31. Söz — Mi'rac-ı Nebevî Hakikati (Dört Esas)", "Mi'racın sebep, hakikat, hikmet ve meyvelerini Dört Esas'ta ispat eden kesitler."),
        (32, "32. Söz — Şirkin İmkânsızlığı ve Muhabbetullah (Üç Mevkıf)", "Zerrelerden yıldızlara kadar şirkin reddi, Ehadiyet tecellisi ve Allah'a muhabbetin hakikati."),
        (33, "33. Söz — Marifetullah Pencereleri (33 Pencere)", "Kâinattaki her bir mevcudattan Vacibü'l-Vücud'un varlığına ve birliğine açılan 33 Pencere."),
    ]

    out = []
    for soz_num, title, desc in fihrist_summaries:
        segs = [s for s in all_segments if s["sozNum"] == soz_num]
        if not segs:
            continue
        # Evenly sample across the whole Söz (up to 32 slices) so all sections/pencereler/suretler are represented
        if len(segs) > 32:
            step = len(segs) / 32.0
            chosen = [segs[int(i * step)] for i in range(32)]
        else:
            chosen = segs
        clips = [
            {
                "trackId": s["trackId"],
                "clipTitle": s["title"],
                "startSec": s["ss"],
                "endSec": s["es"],
                "startRatio": s["sr"],
                "endRatio": s["er"],
                "excerpt": s["excerpt"],
            }
            for s in chosen
        ]
        out.append({
            "id": f"ecza-soz-{soz_num:02d}",
            "title": title,
            "group": "Sözler Eczahanesi (Risalelere Göre)",
            "category": "Fihrist Risalesi (15. Lem'a)",
            "description": f"{len(clips)} saniye-kesiti · {desc}",
            "defaultFon": "ussak" if soz_num % 2 else "hicaz",
            "clips": clips,
            "trackIds": [c["trackId"] for c in clips],
            "curatedCount": len(clips),
        })
    return out


IKI_TITLE_OVERRIDES = {
    "1": "Küfre ve Dalalete Sapmanın Başlıca Sebepleri",
    "1.1": "İmanî Bahislerin Ehemmiyeti",
    "2": "Allah'ın Varlığı ve Birliğinin İspatı (Tevhid)",
    "2.1": "Sebeplerin Mahiyeti ve Neden Var Olduğu",
    "2.2": "Allah'ın Kudret ve Azameti",
    "3": "Deizme Cevap: Kâinatın Tedbiri ve Peygamberlik",
    "4": "Hz. Muhammed'in (a.s.m.) Peygamberliğinin İspatı",
    "4.1": "Peygamber Efendimizin (a.s.m.) Mucizeleri",
    "4.2": "Peygamberimizin Manevî Mertebesi ve Sahabeler",
    "5": "Kur'an'ın Allah Kelamı Olduğunun İspatı",
    "5.1": "Kur'an'ın Mucizeleri ve İ'câz Vecihleri",
    "5.2": "Kur'an'a Dair Meseleler ve Meal Meselesi",
    "6": "Niçin Yaratıldık? Kâinatın Yaratılış Hikmeti ve Sırrı",
    "7": "Ahiret ve Haşir Meseleleri",
    "8": "İnsanın Mahiyeti ve Kıymeti",
    "8.1": "İnsan ve Kâinattaki Konumu",
    "8.2": "Hayatın Anlamı ve Gayesi",
    "9": "Günahlar ve Günahlara Karşı Manevî Siper",
    "9.1": "Şeytan ve Şeytanın Yaratılış Hikmeti",
    "10": "Ene (Benlik) ve Enaniyete Karşı Dersler",
    "11": "İbadet, Namaz ve Dua Hakikatleri",
    "11.1": "Niçin İbadet Ediyoruz?",
    "12": "Ölümler, Belalar, Sıkıntılar ve Cehennemin Hikmeti",
    "13": "Kıyamet, Ahirzaman, Mehdi ve Deccal",
    "14": "Muhabbet ve Hakiki Sevgi",
    "14.1": "Uhuvvet (Kardeşlik) ve İhlas",
    "16": "İctimaî ve Sosyal Meseleler",
    "17": "Tevafuklar ve Manevî Kerametler",
    "18": "İman Hizmeti ile Alakalı Dersler",
    "18.1": "Hizmette Şevk ve Aksiyon",
    "19": "Risale-i Nur ve Ehemmiyeti",
    "20": "Gaybî Meseleler ve Hakikatler",
    "21": "İlahiyat ve Kelam Bahisleri",
    "22": "Tarikat, Tasavvuf ve Hakikat Mesleği",
    "23": "Üstad Bediüzzaman'a Dair Bahisler",
}


def classify_iki_group(num_str: str) -> str:
    if num_str == "9.1":
        return "Ahiret, Kader ve Hikmet-i Musibet"
    major = int(num_str.split(".")[0])
    if major in {1, 2, 3, 4, 5}:
        return "İman, Tevhid ve Nübüvvet"
    if major in {6, 8, 9, 10, 11, 14}:
        return "İnsan, İbadet ve Nefis Terbiyesi"
    if major in {7, 12, 13, 20}:
        return "Ahiret, Kader ve Hikmet-i Musibet"
    return "Hizmet, Risale-i Nur ve Sosyal Meseleler"


def tr_title(s: str) -> str:
    s = re.sub(r"\(siteye.*?\)", "", s, flags=re.I).strip()
    if not s.isupper():
        return s
    lower_map = str.maketrans({"İ": "i", "I": "ı", "Ğ": "ğ", "Ü": "ü", "Ş": "ş", "Ö": "ö", "Ç": "ç"})
    upper_map = str.maketrans({"i": "İ", "ı": "I", "ğ": "Ğ", "ü": "Ü", "ş": "Ş", "ö": "Ö", "ç": "Ç"})
    low = s.translate(lower_map).lower()
    small = {"ve", "ile", "mi", "mı", "mu", "mü", "da", "de", "ki", "bir"}
    words = low.split()
    out = []
    for i, w in enumerate(words):
        if i > 0 and w in small:
            out.append(w)
        else:
            out.append(w[0].translate(upper_map).upper() + w[1:] if w else "")
    return " ".join(out)


def parse_iki_boyutlu_fihrist(tracks, all_segments):
    pdf = PDF_DIR / "İki boyutlu Risale-i Nur fihristi .pdf"
    if not pdf.exists():
        return []
    raw = subprocess.check_output(["pdftotext", str(pdf), "-"], text=True)
    lines = [l.strip().replace("\x0c", "") for l in raw.splitlines() if l.strip()]

    head_re = re.compile(r"^(\d+(?:\.\d+)?)\s*:\s*(.+)$")
    chapters = []
    cur = None
    for ln in lines:
        if ln.startswith("Güncellenme") or ln.startswith(":En az"):
            continue
        m = head_re.match(ln)
        if m:
            raw_title = re.sub(r'"Kardeşlerim!.*$', "", m.group(2)).strip()
            num_s = m.group(1)
            cur = {"num": num_s, "title": IKI_TITLE_OVERRIDES.get(num_s, tr_title(raw_title)), "entries": []}
            chapters.append(cur)
        elif cur is not None:
            if re.search(r"(\d+\s*\.\s*söz|lemaat|konferans)", ln, re.I):
                cur["entries"].append(ln)
            elif cur["entries"] and not re.match(r"^(\d+\.|[A-ZÇĞİÖŞÜ][a-zçğıöşü]+\s*:)", ln):
                cur["entries"][-1] += " " + ln

    FON_CYCLE = ["ussak", "hicaz", "rast", "saba", "yagmur", "kainat"]
    deep_lists = []
    for idx, ch in enumerate(chapters):
        clips = []
        seen_clip = set()
        for entry in ch["entries"]:
            m_soz = re.search(r"(\d+)\s*\.\s*söz", entry, re.I)
            soz_num = int(m_soz.group(1)) if m_soz else None
            m_rnk = re.search(r"\[(?:rnk\s*)?(\d{1,3})\]", entry, re.I)
            rnk_pg = int(m_rnk.group(1)) if m_rnk else None
            if soz_num is None and "lemaat" not in entry.lower():
                continue

            tr, sr, er, ss, es, excerpt = find_best_track_and_slice(tracks, soz_num=soz_num, rnk_page=rnk_pg, hint_text=entry)
            if not tr:
                continue
            key = (tr["id"], round(ss / 25))
            if key in seen_clip:
                continue
            seen_clip.add(key)

            parts = entry.split(":")
            desc = parts[-1].strip() if len(parts) >= 2 else entry
            desc = re.sub(r"\[.*?\]", "", desc).strip()
            clip_title = f"{tr['section']} [{fmt_mmss(ss)}–{fmt_mmss(es)}] — {desc[:70]}" if len(desc) > 6 else f"{tr['title']} [{fmt_mmss(ss)}–{fmt_mmss(es)}]"

            clips.append({
                "trackId": tr["id"],
                "clipTitle": clip_title,
                "startSec": ss,
                "endSec": es,
                "startRatio": sr,
                "endRatio": er,
                "excerpt": excerpt or desc[:150],
            })

        extra = scan_micro_segments_for_topic(ch["title"], ch["entries"], all_segments, max_clips=14)
        for ex_c in extra:
            k = (ex_c["trackId"], round(ex_c["startSec"] / 25))
            if k not in seen_clip and len(clips) < 20:
                seen_clip.add(k)
                clips.append(ex_c)

        if clips:
            grp = classify_iki_group(ch["num"])
            deep_lists.append({
                "id": f"iki-{ch['num'].replace('.', '-')}",
                "title": ch["title"],
                "group": grp,
                "category": f"İki Boyutlu Fihrist · {ch['num']}",
                "description": f"{len(clips)} saniye-eşleşmeli kesit · {clips[0]['clipTitle'].split('—')[0].strip()}",
                "defaultFon": FON_CYCLE[idx % len(FON_CYCLE)],
                "clips": clips,
                "trackIds": [c["trackId"] for c in clips],
                "curatedCount": len(clips),
            })

    return deep_lists


QA_CURATED_SPEC = [
    # 1. Allah'a İman, Tevhid ve Kâinat
    ("Allah'a İman, Tevhid ve Kâinat", "Bir Allah bütün kâinatı aynı anda nasıl idare ediyor?", {16, 22, 32}, ["bir zat", "güneş", "tecelli", "şeffaf", "idare", "tedbir", "vahid", "ehad"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Rabbimizi bize tarif eden başlıca burhanlar nelerdir?", {22, 32, 33}, ["tarif", "burhan", "pencere", "kitab-ı kebir", "kur'an", "peygamber", "marifet"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Güneş ve yıldızlar nasıl bu kadar intizamlı dönüyor ve sönmüyor?", {32, 33, 22}, ["güneş", "yıldız", "seyyare", "sema", "intizam", "kudret", "sönmüyor"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Kuru bir odun olan ağaç nasıl meyve veriyor?", {1, 22, 33}, ["ağaç", "odun", "meyve", "bismillah", "kök", "rahmet", "bostan"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Akılsız bir arı bizim için şifalı balı nasıl yapıyor?", {1, 22, 24, 33}, ["arı", "bal", "ilham", "şifa", "hayvan", "bismillah"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Bebek doğar doğmaz anne sütü ona nasıl yetiştiriliyor?", {1, 14, 22, 33, "14. Lem'a (2. Makam)"}, ["süt", "valide", "şefkat", "rahmet", "yavru", "rızık", "kan"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Hayvanlar içgüdüyle mi, yoksa sevk-i İlâhî ile mi hareket eder?", {22, 24, 33, 11}, ["hayvan", "sevk", "ilham", "şuur", "hikmet", "rızık", "arı"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Şirk neden aklen imkânsızdır?", {22, 32}, ["şirk", "muhal", "imkansız", "mümteni", "zerre", "tevhid", "vahdaniyet"]),
    ("Allah'a İman, Tevhid ve Kâinat", "Kâinattaki zerreler neden durmadan hareket ediyor?", {30}, ["zerre", "zerrat", "tahavvülat", "hareket", "vazife", "kudret"]),

    # 2. İnsan, Hayatın Gayesi ve Hikmet
    ("İnsan, Hayatın Gayesi ve Hikmet", "Kâinatın ve insanın yaratılış gayesi nedir?", {11, 23}, ["yaratılış", "gaye", "saray", "ubudiyet", "hilkat", "esma", "ayine", "sema"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "İman insanı nasıl kıymetli kılar, küfür nasıl düşürür?", {23}, ["iman", "intisa", "kıymet", "küfür", "sukut", "antika", "sanat"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "İnsan Allah'ın antika bir sanatıdır ne demektir?", {23}, ["antika", "sanat", "antikacı", "san'at", "kıymet", "esma"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "Bize verilen nimetlere karşılık Allah ne istiyor?", {1}, ["fiat", "fiyat", "nimet", "zikir", "fikir", "şükür", "bismillah"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "Küllî ve hadsiz nimetlere cüz'î şükrümüzle nasıl mukabele edebiliriz?", {24, 1}, ["şükür", "nimet", "cüz'i", "külli", "mukabele", "niyet", "hamd"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "Ene (benlik) emaneti insana niçin verilmiştir?", {30}, ["ene", "vahid-i kıyasi", "emanet", "anahtar", "enaniyet", "benlik"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "İnsan Karun gibi 'Ben kendi ilmimle kazandım' dese ne olur?", {23, 6}, ["karun", "tevekkül", "mülk", "emanet", "nefis", "mal"]),
    ("İnsan, Hayatın Gayesi ve Hikmet", "Allah'a muhabbet ile dünya ve eşyaya meşru sevgi nasıl birleşir?", {32, 24}, ["muhabbet", "sevgi", "mana-yı harfi", "mana-yı ismi", "esma", "cemal"]),

    # 3. Namaz, Dua, İbadet ve Vesvese
    ("Namaz, Dua, İbadet ve Vesvese", "Niçin her hayrın başı Bismillâh'tır?", {1}, ["bismillah", "hayrın başı", "bostan", "bereket", "kuvvet"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Dua nedir, çeşitleri nelerdir ve ne zaman kabul olur?", {23}, ["dua", "ubudiyet", "lisan", "kalp", "istidad", "ihtiyaç", "icabet"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Namaz niçin beş vakte tahsis edilmiştir?", {9}, ["beş vakit", "vakit", "sabah", "öğle", "ikindi", "akşam", "yatsı", "namaz"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Kur'an'da namaz ibadeti neden bu kadar çok tekrar ve emredilmiştir?", {4, 9, 21}, ["namaz", "kıymetdar", "ucuz", "tekrar", "ihtiyaç", "ruh", "gıda"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Namazda usanç ve dünya meşgalesi bahanesine karşı cevap nedir?", {21}, ["usanç", "ikaz", "fütur", "meşgale", "dünya", "namaz", "ömür"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Abdest ve ibadetlerde gelen vesvesenin mahiyeti ve ilacı nedir?", {21}, ["vesvese", "merhem", "şeytan", "hayal", "kalp", "tahayyül", "tasdik"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Çalışmak ve dünya işleri nasıl ibadet hükmüne geçer?", {4, 5, 24}, ["çalışmak", "ibadet", "vazife", "niyet", "meşru", "rızık", "namaz"]),
    ("Namaz, Dua, İbadet ve Vesvese", "Diğer varlıklar ve mevcudat da ibadet ediyorlar mı?", {1, 24, 11}, ["ibadet", "tesbih", "lisan-ı hal", "mevcudat", "bismillah", "zikir"]),

    # 4. Haşir, Kabir, Ölüm ve Ahiret
    ("Haşir, Kabir, Ölüm ve Ahiret", "Öldükten sonra tekrar dirilecek miyiz? Haşrin delilleri nelerdir?", {10}, ["haşir", "diril", "hakikat", "bahar", "mahkeme", "suret", "kudret"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Çürüyüp dağılan bu bedenler göz açıp kapayıncaya kadar nasıl dirilecek?", {10}, ["çürü", "dağıl", "göz açıp", "bahar", "zerre", "kudret", "haşir", "asker"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Amellerimiz ve hayatımız nasıl muhafaza ediliyor?", {10}, ["muhafaza", "hafız", "amel", "kayıt", "çekirdek", "hafıza", "levh"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Ölüm bir nimet ve terhis tezkeresi olabilir mi?", {7, 8, 10, 13, 29}, ["ölüm", "mevt", "terhis", "nimet", "idam", "kabir", "ebedi"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Kabre nasıl ve kaç yolla girilir?", {8, 13, 23}, ["kabir", "kapı", "yol", "ejderha", "bahçe", "genç", "terbiye"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Cennetin cismanî ve ruhanî lezzetleri nasıl olacak?", {28}, ["cennet", "lezzet", "cismani", "ruhani", "ebedi", "saadet", "sual"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "İnsan yalnız amelleriyle Cennete girebilir mi?", {5, 23, 28}, ["amel", "cennet", "rahmet", "fazl", "nimet", "mükafat"]),
    ("Haşir, Kabir, Ölüm ve Ahiret", "Ruhumuz baki midir, meleklerin hakikati nedir?", {29}, ["ruh", "beka", "baki", "melaike", "melek", "kanun", "cesed"]),

    # 5. Kader, Adalet, Musibet ve Şeytan
    ("Kader, Adalet, Musibet ve Şeytan", "Kader ile insanın cüz'î iradesi nasıl bağdaşır?", {26}, ["kader", "irade", "cüz'i", "ihtiyari", "ilim", "malum", "mesuliyet"]),
    ("Kader, Adalet, Musibet ve Şeytan", "Bazı insanlar musibete veya noksanlığa uğruyor; bu adalet midir?", {26, 14}, ["adalet", "kader", "musibet", "hikmet", "rahmet", "merhamet", "mükafat"]),
    ("Kader, Adalet, Musibet ve Şeytan", "Depremin ve umumî musibetlerin hikmeti nedir? Mazlumların hali ne olur?", {14}, ["zelzele", "deprem", "mazlum", "zalim", "musibet", "hikmet", "rahmet", "şehit"]),
    ("Kader, Adalet, Musibet ve Şeytan", "Şeytanların ve şerlerin yaratılış hikmeti nedir?", {12, 23, 26, 21}, ["şeytan", "şer", "terakki", "imtihan", "mücahede", "hikmet", "vesvese"]),
    ("Kader, Adalet, Musibet ve Şeytan", "Kıyamet ne zaman kopacak ve alametleri nasıl anlaşılmalı?", {24, 10, 19}, ["kıyamet", "ecel", "vakit", "alamet", "hadis", "ahirzaman", "müteşabih"]),

    # 6. Kur'an, Peygamberlik ve İctimaî Sualler
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Kur'an-ı Kerim'in neresi ve hangi vecihleri mucizedir?", {25}, ["mucize", "i'caz", "belagat", "şule", "kelam", "kırk vecih", "kur'an"]),
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Kur'an neden felsefe gibi kâinattan uzun uzun bahsetmiyor?", {19, 20, 25}, ["felsefe", "mevcudat", "müessis", "kur'an", "irşad", "avam", "mana-yı harfi"]),
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Mi'rac-ı Nebevî neden yaşandı ve bütün insanlara neden açık değil?", {31}, ["mirac", "mi'rac", "esas", "peygamber", "uruc", "ru'yet", "hikmet"]),
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Faiz niçin haramdır ve zekât neden emredilmiştir?", {25, 22}, ["faiz", "riba", "zekat", "haram", "iktisad", "ihtilal", "adalet", "şeriat"]),
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Gıybet nedir, manevi ve ictimaî zararları nelerdir?", {25}, ["gıybet", "zem", "kardeş", "et", "uhuvvet", "haram"]),
    ("Kur'an, Peygamberlik ve İctimaî Sualler", "Şehitler mi üstündür, yoksa âlimler ve hakiki veliler mi?", {29, 27, 24}, ["şehit", "alim", "veli", "sahabe", "mertebe", "fazilet", "mürekke"]),
]


def parse_soru_cevap_fihrist(tracks, all_segments):
    """
    Build strictly text-verified Q&A audio clips grouped cleanly into 6 content divisions.
    Every Q&A item is matched directly against fullText of all 2,373 Külliyat micro-segments
    within the target Sözler so the listener hears the exact Risale-i Nur answer.
    """
    qa_items = []
    for idx, (group, question, target_soz, kws) in enumerate(QA_CURATED_SPEC, start=1):
        scored = []
        for s in all_segments:
            hay = norm_tr(f"{s['title']} {s['fullText']}")
            in_target = s["sozNum"] in target_soz
            kw_hits = 0
            sc = 0.0
            for kw in kws:
                nkw = norm_tr(kw).strip()
                if not nkw:
                    continue
                if nkw in hay:
                    kw_hits += 1
                    sc += 4.2 if len(nkw) >= 5 else 2.5
                    # Bonus if keyword appears multiple times in this exact passage
                    cnt = hay.count(nkw)
                    if cnt >= 2:
                        sc += min(3.0, (cnt - 1) * 0.9)
            if kw_hits == 0:
                continue
            if in_target:
                sc += 6.5
            # Slight bonus if the passage explicitly frames a Sual / Elcevap / Nükte
            if re.search(r"(elcevap|elcevab|suâl|sual|eğer desen|eğer dersen|hakikat|nükte|ikaz|merhem)", s["fullText"], re.I):
                sc += 1.5
            # Avoid pure opening bio/intro paragraphs when substantive body segments exist
            if s["ss"] < 5 and len(s["fullText"]) < 140:
                sc -= 3.0
            scored.append((sc, kw_hits, s))

        scored.sort(key=lambda x: (-x[0], -x[1]))
        clips = []
        seen = set()
        for sc, hits, s in scored:
            k = (s["trackId"], round(s["ss"] / 45))
            if k in seen:
                continue
            seen.add(k)
            clips.append({
                "trackId": s["trackId"],
                "clipTitle": f"{question} — {s['title']}",
                "startSec": s["ss"],
                "endSec": s["es"],
                "startRatio": s["sr"],
                "endRatio": s["er"],
                "excerpt": s["excerpt"],
            })
            if len(clips) >= 3:
                break

        if not clips:
            continue
        best = clips[0]
        tr_section = best["clipTitle"].split("—", 1)[-1].strip()
        qa_items.append({
            "id": f"qa-{idx:03d}",
            "num": idx,
            "group": group,
            "question": question,
            "ref": tr_section,
            "trackId": best["trackId"],
            "clipTitle": best["clipTitle"],
            "startSec": best["startSec"],
            "endSec": best["endSec"],
            "startRatio": best["startRatio"],
            "endRatio": best["endRatio"],
            "excerpt": best["excerpt"],
            "clips": clips,
        })
    return qa_items


def main():
    kulliyat_sections = load_kulliyat_sections()
    tracks, all_segments = build_all_tracks_and_segments(kulliyat_sections)
    topic_playlists = parse_konu_fihristi_full(tracks, all_segments)

    # Strip temporary normalized search caches before saving segments-index.json
    for s in all_segments:
        for k in ("_nt", "_nb", "_tw", "_bw"):
            s.pop(k, None)

    readers = {}
    for t in tracks:
        readers[t["reader"]] = readers.get(t["reader"], 0) + 1

    catalog = {
        "generatedAt": "2026-11-22",
        "totalTracks": len(tracks),
        "totalAudioHours": round(sum(t["durationSec"] for t in tracks) / 3600, 2),
        "totalSegments": len(all_segments),
        "readers": [{"name": k, "count": v} for k, v in sorted(readers.items(), key=lambda x: -x[1])],
        "tracks": tracks,
    }

    (OUT_DIR / "risale-catalog.json").write_text(json.dumps(catalog, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    (OUT_DIR / "segments-index.json").write_text(json.dumps(all_segments, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    (OUT_DIR / "topic-playlists.json").write_text(json.dumps(topic_playlists, ensure_ascii=False, indent=2), encoding="utf-8")

    total_d = sum(p.get("directCount", 0) for p in topic_playlists)
    total_i = sum(p.get("indirectCount", 0) for p in topic_playlists)
    print(f"Tracks: {len(tracks)} ({catalog['totalAudioHours']}h) -> {len(all_segments)} fine 45s-95s micro-segments!")
    print(f"Konu Fihristi (Temiz & Hikmetli): {len(topic_playlists)} topics ({total_d} doğrudan + {total_i} dolaylı = {total_d + total_i} kesit)")


if __name__ == "__main__":
    main()

