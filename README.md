# 🤖 Jarves AI — O'zbek tilida Android AI Yordamchisi

## 📱 Nima qila oladi?

| Buyruq (O'zbek tilida) | Natija |
|------------------------|--------|
| "Akamga qo'ng'iroq qil" | Kontaktdan topib qo'ng'iroq qiladi |
| "998901234567 ga ring qil" | To'g'ridan telefon qiladi |
| "Akamga SMS yubor: Salom" | SMS yuboradi |
| "Telegramni och" | Telegram ilovasini ochadi |
| "YouTubeda musiqa qo'y" | YouTube ochadi |
| "Soat 7:00 ga budilnik qo'y" | Signal qo'yadi |
| "5 daqiqaga taymer qo'y" | Taymer ishga tushiradi |
| "Fonar yoq" | Flashlight yoqadi |
| "Ovozni oshir" | Ovoz balandlashadi |
| "Jim qil" | Telefon jimga o'tadi |
| "Toshkentni xaritada ko'rsat" | Xarita ochadi |
| "Google da AI qidir" | Brauzerda qidiradi |
| "Selfie ol" | Kamerani ochadi |
| "Tebrat" | Telefon tebranadi |
| "Hozir soat nechchi?" | Vaqtni aytadi |
| Istalgan savol | Gemini AI javob beradi |

---

## 🚀 O'rnatish

### Talab:
- Android Studio (Hedgehog yoki yangi)
- Android 7.0+ (API 24+)
- Internet ulanish

### Qadamlar:

**1. Loyihani oching:**
```
File → Open → JarvesAI papkasini tanlang
```

**2. Gemini API kaliti oling (BEPUL):**
```
https://aistudio.google.com/app/apikey saytiga kiring
→ "Create API Key" bosing
→ Kalit nusxalang
```

**3. API kalitini kiriting:**

`GeminiApi.java` faylini oching:
```java
private static final String API_KEY = "YOUR_GEMINI_API_KEY_HERE";
//                                     ^^^^ SHU YERGA O'Z KALINGIZNI QOYING
```

**4. Qurilmaga yuklang:**
```
Run → Run 'app' yoki Shift+F10
```

---

## 📁 Loyiha tuzilmasi

```
JarvesAI/
├── app/src/main/
│   ├── AndroidManifest.xml          ← Ruxsatlar
│   ├── java/com/jarves/ai/
│   │   ├── activities/
│   │   │   ├── MainActivity.java    ← Asosiy ekran
│   │   │   ├── SplashActivity.java  ← Kirish ekrani
│   │   │   └── SettingsActivity.java
│   │   ├── adapters/
│   │   │   └── ChatAdapter.java     ← Chat ko'rsatish
│   │   ├── models/
│   │   │   └── ChatMessage.java     ← Xabar modeli
│   │   ├── services/
│   │   │   ├── JarvesService.java   ← Fon xizmati
│   │   │   └── BootReceiver.java    ← Telefon yoqilganda
│   │   └── utils/
│   │       ├── CommandProcessor.java ← 🌟 ASOSIY: Buyruqlar
│   │       └── GeminiApi.java       ← AI javoblari
│   └── res/
│       ├── layout/                  ← Ekran dizaynlari
│       ├── drawable/                ← Shakllar va rasmlar
│       ├── values/                  ← Ranglar, matnlar
│       └── anim/                    ← Animatsiyalar
```

---

## 🔧 Yangi buyruq qo'shish

`CommandProcessor.java` faylidagi `detectCommand()` metodiga qo'shing:

```java
// Misol: "Parolni o'zgartir" buyrug'i
if (matches(lower, "parolni o'zgartir", "shifr o'zgartir")) {
    return "CHANGE_PASSWORD";
}
```

So'ng `executeCommand()` ga qo'shing:
```java
case "CHANGE_PASSWORD":
    // O'z kodingizni yozing
    return "🔐 Parol o'zgartirish ochildi!";
```

---

## ✨ Imkoniyatlar

- ✅ O'zbek tilida buyruqlar
- ✅ Kontaktlardan ism bo'yicha qidirish
- ✅ Gemini AI bilan savol-javob
- ✅ Ovozli kiritish (mikrofon)
- ✅ Ovozli javob (TTS)
- ✅ Telefon yoqilganda avtomatik ishga tushish
- ✅ Zamonaviy qora tema dizayn

---

## 🆓 Bepul API

Gemini API oyiga **1,000,000+ so'rov** bepul!
👉 https://aistudio.google.com/app/apikey

---

## 📞 Muammo bo'lsa?

Ko'p muammolar:
1. **API kalit kiritilmagan** → `GeminiApi.java` ni tekshiring
2. **Ruxsat berilmagan** → Telefon sozlamalaridan ruxsat bering
3. **Kontakt topilmadi** → To'liq ismni ayting yoki raqamni kiriting
