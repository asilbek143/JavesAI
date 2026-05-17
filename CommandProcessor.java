package com.jarves.ai.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.media.AudioManager;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jarves AI - O'zbek tilida buyruqlarni qayta ishlash
 * Foydalanuvchi nima desa, shu buyruqni bajaradi
 */
public class CommandProcessor {

    private final Context context;

    // Buyruq turlari
    public static final String CMD_CALL = "CALL";
    public static final String CMD_SMS = "SMS";
    public static final String CMD_OPEN_APP = "OPEN_APP";
    public static final String CMD_ALARM = "ALARM";
    public static final String CMD_FLASHLIGHT = "FLASHLIGHT";
    public static final String CMD_VOLUME = "VOLUME";
    public static final String CMD_CAMERA = "CAMERA";
    public static final String CMD_SEARCH = "SEARCH";
    public static final String CMD_YOUTUBE = "YOUTUBE";
    public static final String CMD_WIFI = "WIFI";
    public static final String CMD_VIBRATE = "VIBRATE";
    public static final String CMD_CALCULATOR = "CALCULATOR";
    public static final String CMD_MAP = "MAP";
    public static final String CMD_BROWSER = "BROWSER";
    public static final String CMD_SETTINGS = "SETTINGS";
    public static final String CMD_TIMER = "TIMER";

    // Ilovalar lug'ati (O'zbek nomi -> package nomi)
    private static final Map<String, String> APP_PACKAGES = new HashMap<String, String>() {{
        put("youtube", "com.google.android.youtube");
        put("instagram", "com.instagram.android");
        put("telegram", "org.telegram.messenger");
        put("whatsapp", "com.whatsapp");
        put("facebook", "com.facebook.katana");
        put("twitter", "com.twitter.android");
        put("tiktok", "com.zhiliaoapp.musically");
        put("maps", "com.google.android.apps.maps");
        put("gmail", "com.google.android.gm");
        put("chrome", "com.android.chrome");
        put("kamera", "android.media.action.IMAGE_CAPTURE");
        put("muzika", "com.google.android.music");
        put("spotify", "com.spotify.music");
        put("clock", "com.google.android.deskclock");
        put("kalender", "com.google.android.calendar");
        put("kontaktlar", "com.android.contacts");
        put("sozlamalar", "com.android.settings");
        put("playstore", "com.android.vending");
        put("uzum", "uz.uzum.bank");
        put("payme", "uz.paycom.payme");
        put("click", "uz.click");
    }};

    public CommandProcessor(Context context) {
        this.context = context;
    }

    /**
     * Foydalanuvchi matni qaysi buyruqqa tegishli ekanligini aniqlaydi
     */
    public String detectCommand(String input) {
        String lower = input.toLowerCase().trim();

        // QONG'IROQ buyruqlari
        if (matches(lower, "qo'ng'iroq qil", "ring qil", "chaqir", "telefon qil",
                "call qil", "aloqa qil", "zang ur")) {
            return CMD_CALL;
        }

        // SMS buyruqlari
        if (matches(lower, "sms yubor", "xabar yubor", "message yubor",
                "sms jo'nat", "yoz", "xat yubor")) {
            return CMD_SMS;
        }

        // ILOVA OCHISH
        if (matches(lower, "och ", "oching", "ishga tushir", "start qil",
                "otkir", "run qil", "launch")) {
            return CMD_OPEN_APP;
        }

        // BUDILNIK / SIGNAL
        if (matches(lower, "budilnik qo'y", "uyg'otuvchi qo'y", "signal qo'y",
                "alarm qo'y", "soat qo'y", "uyg'ot")) {
            return CMD_ALARM;
        }

        // FONAR
        if (matches(lower, "fonar yoq", "fonar o'chir", "flashlight",
                "chiroq yoq", "chiroq o'chir", "torch")) {
            return CMD_FLASHLIGHT;
        }

        // OVOZ
        if (matches(lower, "ovozni oshir", "ovoz oshir", "balandroq",
                "ovozni kamayt", "ovoz kamayt", "pastroq", "soket",
                "jim qil", "mute", "ovoz")) {
            return CMD_VOLUME;
        }

        // KAMERA
        if (matches(lower, "rasm ol", "foto ol", "kamera och",
                "selfie ol", "video ol", "suratga ol")) {
            return CMD_CAMERA;
        }

        // QIDIRUV
        if (matches(lower, "qidir", "izla", "search qil", "google da qidir",
                "topib ber", "search")) {
            return CMD_SEARCH;
        }

        // YOUTUBE
        if (matches(lower, "youtube", "video ko'r", "film ko'r", "qo'shiq",
                "musiqa qo'y")) {
            return CMD_YOUTUBE;
        }

        // XARITA / MANZIL
        if (matches(lower, "manzil top", "xarita", "yo'l ko'rsat",
                "qayerda", "navigation", "maps")) {
            return CMD_MAP;
        }

        // TAYMER
        if (matches(lower, "taymer qo'y", "timer qo'y", "vaqt qo'y",
                "minute", "soniya", "daqiqa")) {
            return CMD_TIMER;
        }

        // TEBRANISH
        if (matches(lower, "tebrat", "vibrate", "tebranish")) {
            return CMD_VIBRATE;
        }

        // SOZLAMALAR
        if (matches(lower, "sozlama", "setting", "wifi", "bluetooth",
                "internet", "ma'lumot")) {
            return CMD_SETTINGS;
        }

        // BRAUZER
        if (matches(lower, "saytni och", "website", "brauzer", "internet och",
                "http", "www", ".com", ".uz")) {
            return CMD_BROWSER;
        }

        // KALKULATOR
        if (matches(lower, "hisobla", "calculator", "qo'sh", "ayir",
                "ko'payt", "bo'l", "+", "-", "*", "hisobla")) {
            return CMD_CALCULATOR;
        }

        return null; // AI ga yubor
    }

    /**
     * Buyruqni bajaradi va javob qaytaradi
     */
    public String executeCommand(String command, String originalInput) {
        switch (command) {
            case CMD_CALL:
                return executeCall(originalInput);
            case CMD_SMS:
                return executeSms(originalInput);
            case CMD_OPEN_APP:
                return executeOpenApp(originalInput);
            case CMD_ALARM:
                return executeAlarm(originalInput);
            case CMD_FLASHLIGHT:
                return executeFlashlight(originalInput);
            case CMD_VOLUME:
                return executeVolume(originalInput);
            case CMD_CAMERA:
                return executeCamera();
            case CMD_SEARCH:
                return executeSearch(originalInput);
            case CMD_YOUTUBE:
                return executeYoutube(originalInput);
            case CMD_MAP:
                return executeMap(originalInput);
            case CMD_TIMER:
                return executeTimer(originalInput);
            case CMD_VIBRATE:
                return executeVibrate();
            case CMD_SETTINGS:
                return executeSettings(originalInput);
            case CMD_BROWSER:
                return executeBrowser(originalInput);
            case CMD_CALCULATOR:
                return executeCalculator(originalInput);
            default:
                return "Kechirasiz, bu buyruqni tushunmadim.";
        }
    }

    // ==================== BUYRUQLAR ====================

    private String executeCall(String input) {
        // Raqam yoki ism topish
        String phone = extractPhoneNumber(input);
        String name = extractContactName(input);

        if (phone != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(callIntent);
                return "✅ " + phone + " raqamiga qo'ng'iroq qilinmoqda...";
            } catch (SecurityException e) {
                // Ruxsat yo'q
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phone));
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialIntent);
                return "📞 Telefon ilovasi ochildi. Tasdiqlang.";
            }
        } else if (name != null) {
            // Kontaktlardan qidirish
            String contactPhone = findContactByName(name);
            if (contactPhone != null) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactPhone));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(callIntent);
                    return "✅ " + name + "ga qo'ng'iroq qilinmoqda...";
                } catch (SecurityException e) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + contactPhone));
                    dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialIntent);
                    return "📞 " + name + " topildi. Tasdiqlang.";
                }
            } else {
                return "❌ \"" + name + "\" kontaktlar ro'yxatidan topilmadi.\nRaqamni to'g'ridan to'g'ri ayting.";
            }
        } else {
            return "❓ Kimga qo'ng'iroq qilish kerak?\nMisol: \"Akamga qo'ng'iroq qil\" yoki \"998901234567 ga ring qil\"";
        }
    }

    private String executeSms(String input) {
        String phone = extractPhoneNumber(input);
        String message = extractSmsText(input);

        if (phone == null) {
            // Kontakt ismini qidirish
            String name = extractContactName(input);
            if (name != null) {
                phone = findContactByName(name);
            }
        }

        if (phone != null && message != null) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                return "✅ SMS yuborildi!\n📱 Raqam: " + phone + "\n💬 Xabar: " + message;
            } catch (Exception e) {
                // Fallback: SMS ilovasini och
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + phone));
                smsIntent.putExtra("sms_body", message);
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
                return "📱 SMS ilovasi ochildi. Yuborishni tasdiqlang.";
            }
        } else if (phone != null) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + phone));
            smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(smsIntent);
            return "📱 SMS ilovasi ochildi. Xabarni yozing.";
        } else {
            return "❓ Kimga SMS yuborish kerak? Raqam yoki ismni ayting.\nMisol: \"Akamga SMS yubor: Salom, yaxshimisan?\"";
        }
    }

    private String executeOpenApp(String input) {
        String lower = input.toLowerCase();

        for (Map.Entry<String, String> entry : APP_PACKAGES.entrySet()) {
            if (lower.contains(entry.getKey())) {
                String packageName = entry.getValue();

                // Maxsus holatlar
                if (entry.getKey().equals("kamera")) {
                    return executeCamera();
                }
                if (entry.getKey().equals("sozlamalar")) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return "⚙️ Sozlamalar ochildi!";
                }

                PackageManager pm = context.getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(launchIntent);
                    return "✅ " + entry.getKey() + " ochildi!";
                } else {
                    // Play Store dan yuklab olish
                    Intent playIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + packageName));
                    playIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(playIntent);
                        return "📥 " + entry.getKey() + " o'rnatilmagan. Play Store ochildi.";
                    } catch (Exception e) {
                        return "❌ " + entry.getKey() + " topilmadi.";
                    }
                }
            }
        }

        return "❓ Qaysi ilovani ochish kerak? Ilova nomini ayting.\nMasalan: \"Telegramni och\" yoki \"YouTubeni ishga tushir\"";
    }

    private String executeAlarm(String input) {
        // Vaqtni aniqlash
        int[] time = extractTime(input);
        if (time != null) {
            Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
            alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, time[0]);
            alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, time[1]);
            alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Jarves AI signali");
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
            return "⏰ Budilnik qo'yildi: " + String.format("%02d:%02d", time[0], time[1]);
        }
        return "❓ Qaysi vaqtga signal qo'yish kerak?\nMisol: \"Soat 7:00 ga budilnik qo'y\"";
    }

    private String executeFlashlight(String input) {
        String lower = input.toLowerCase();
        boolean turnOn = !lower.contains("o'chir") && !lower.contains("ochir");

        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, turnOn);
            return turnOn ? "🔦 Fonar yoqildi!" : "🔦 Fonar o'chirildi!";
        } catch (Exception e) {
            return "❌ Fonar ishlatib bo'lmadi: " + e.getMessage();
        }
    }

    private String executeVolume(String input) {
        String lower = input.toLowerCase();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (matches(lower, "jim qil", "mute", "soket")) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            return "🔇 Telefon jimga qo'yildi!";
        } else if (matches(lower, "oshir", "baland", "kattaroq")) {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            return "🔊 Ovoz oshirildi!";
        } else if (matches(lower, "kamayt", "past", "kichik")) {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            return "🔉 Ovoz kamaytirildi!";
        } else if (matches(lower, "tebranish", "vibrate")) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            return "📳 Tebranish rejimi yoqildi!";
        }
        return "🔊 Ovoz: " + audioManager.getStreamVolume(AudioManager.STREAM_RING) + "/" +
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    private String executeCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(cameraIntent);
            return "📸 Kamera ochildi!";
        } catch (Exception e) {
            return "❌ Kamera ochilmadi.";
        }
    }

    private String executeSearch(String input) {
        // "qidir" dan keyingi so'zlarni olish
        String query = extractAfterKeyword(input, "qidir", "izla", "search", "topib ber");
        if (query == null) query = input;

        Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        searchIntent.putExtra("query", query);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(searchIntent);
        } catch (Exception e) {
            // Fallback to browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
        return "🔍 Google da qidirilmoqda: \"" + query + "\"";
    }

    private String executeYoutube(String input) {
        String query = extractAfterKeyword(input, "youtube", "ko'r", "qo'shiq", "musiqa");
        if (query != null && !query.isEmpty()) {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query)));
            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(youtubeIntent);
            return "▶️ YouTube da qidirilmoqda: \"" + query + "\"";
        } else {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(youtubeIntent);
            return "▶️ YouTube ochildi!";
        }
    }

    private String executeMap(String input) {
        String location = extractAfterKeyword(input, "manzil", "xarita", "qayerda", "yo'l");
        if (location != null && !location.isEmpty()) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + Uri.encode(location)));
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mapIntent);
            return "🗺️ Xaritada qidirilmoqda: \"" + location + "\"";
        } else {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0"));
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mapIntent);
            return "🗺️ Xarita ochildi!";
        }
    }

    private String executeTimer(String input) {
        int seconds = extractSeconds(input);
        if (seconds > 0) {
            Intent timerIntent = new Intent(AlarmClock.ACTION_SET_TIMER);
            timerIntent.putExtra(AlarmClock.EXTRA_LENGTH, seconds);
            timerIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
            timerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(timerIntent);

            String timeStr = formatSeconds(seconds);
            return "⏱️ Taymer qo'yildi: " + timeStr;
        }
        return "❓ Qancha vaqtga taymer qo'yish kerak?\nMisol: \"5 daqiqaga taymer qo'y\"";
    }

    private String executeVibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
            return "📳 Telefon tebratildi!";
        }
        return "❌ Tebranish moslamasi topilmadi.";
    }

    private String executeSettings(String input) {
        String lower = input.toLowerCase();
        Intent intent;

        if (lower.contains("wifi") || lower.contains("internet")) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        } else if (lower.contains("bluetooth")) {
            intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        } else if (lower.contains("til") || lower.contains("language")) {
            intent = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
        } else if (lower.contains("ekran") || lower.contains("display")) {
            intent = new Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
        } else {
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return "⚙️ Sozlamalar ochildi!";
    }

    private String executeBrowser(String input) {
        String url = extractUrl(input);
        if (url == null) {
            url = "https://www.google.com";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
        return "🌐 Brauzer ochildi: " + url;
    }

    private String executeCalculator(String input) {
        // Oddiy hisoblash
        try {
            String expression = input.replaceAll("[^0-9+\\-*/().,]", "").trim();
            if (!expression.isEmpty()) {
                // TODO: Math parser kutubxonasini qo'shish
                Intent calcIntent = new Intent();
                calcIntent.setAction(Intent.ACTION_MAIN);
                calcIntent.addCategory(Intent.CATEGORY_APP_CALCULATOR);
                calcIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(calcIntent);
                return "🧮 Kalkulator ochildi!";
            }
        } catch (Exception e) {
            // ignore
        }
        Intent calcIntent = new Intent();
        calcIntent.setAction(Intent.ACTION_MAIN);
        calcIntent.addCategory(Intent.CATEGORY_APP_CALCULATOR);
        calcIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(calcIntent);
        return "🧮 Kalkulator ochildi!";
    }

    // ==================== YORDAMCHI METODLAR ====================

    private boolean matches(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    private String extractPhoneNumber(String input) {
        Pattern pattern = Pattern.compile("\\+?[0-9]{9,13}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String extractContactName(String input) {
        String[] callKeywords = {"qo'ng'iroq qil", "ring qil", "chaqir", "telefon qil",
                "sms yubor", "xabar yubor", "ga ", "ga"};

        String lower = input.toLowerCase();
        for (String keyword : callKeywords) {
            int idx = lower.indexOf(keyword);
            if (idx > 0) {
                String before = input.substring(0, idx).trim();
                // Oxirgi so'z ism bo'lishi mumkin
                String[] words = before.split("\\s+");
                if (words.length > 0) {
                    return words[words.length - 1];
                }
            }
        }

        // "Akamga", "Opamga", "Doʻstimga" formatlarini aniqlash
        Pattern namePattern = Pattern.compile("(\\w+)ga\\s+(qo'ng'iroq|ring|sms|xabar)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = namePattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String findContactByName(String name) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phone = cursor.getString(phoneIndex);
            cursor.close();
            return phone;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    private String extractSmsText(String input) {
        // ":" dan keyingi matnni olish
        int colonIdx = input.indexOf(":");
        if (colonIdx >= 0 && colonIdx < input.length() - 1) {
            return input.substring(colonIdx + 1).trim();
        }
        // "\"" ichidagi matnni olish
        Pattern quotedPattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = quotedPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private int[] extractTime(String input) {
        // "7:30" yoki "7 30" formatlarini aniqlash
        Pattern timePattern = Pattern.compile("(\\d{1,2})[:. ](\\d{2})");
        Matcher matcher = timePattern.matcher(input);
        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = Integer.parseInt(matcher.group(2));
            if (hour < 24 && minute < 60) {
                return new int[]{hour, minute};
            }
        }

        // "7 da" yoki "soat 7" formatlarini aniqlash
        Pattern hourPattern = Pattern.compile("(\\d{1,2})\\s*(da|soat|erta|kechqurun|tushda)?", Pattern.CASE_INSENSITIVE);
        Matcher hourMatcher = hourPattern.matcher(input);
        if (hourMatcher.find()) {
            int hour = Integer.parseInt(hourMatcher.group(1));
            if (hour < 24) {
                return new int[]{hour, 0};
            }
        }

        return null;
    }

    private int extractSeconds(String input) {
        String lower = input.toLowerCase();
        int totalSeconds = 0;

        // Soat
        Pattern hourPattern = Pattern.compile("(\\d+)\\s*soat");
        Matcher hourMatcher = hourPattern.matcher(lower);
        if (hourMatcher.find()) {
            totalSeconds += Integer.parseInt(hourMatcher.group(1)) * 3600;
        }

        // Daqiqa / minut
        Pattern minPattern = Pattern.compile("(\\d+)\\s*(daqiqa|minut|minute)");
        Matcher minMatcher = minPattern.matcher(lower);
        if (minMatcher.find()) {
            totalSeconds += Integer.parseInt(minMatcher.group(1)) * 60;
        }

        // Soniya
        Pattern secPattern = Pattern.compile("(\\d+)\\s*soniya");
        Matcher secMatcher = secPattern.matcher(lower);
        if (secMatcher.find()) {
            totalSeconds += Integer.parseInt(secMatcher.group(1));
        }

        // Faqat raqam bo'lsa (daqiqa deb olamiz)
        if (totalSeconds == 0) {
            Pattern numPattern = Pattern.compile("(\\d+)");
            Matcher numMatcher = numPattern.matcher(input);
            if (numMatcher.find()) {
                totalSeconds = Integer.parseInt(numMatcher.group(1)) * 60;
            }
        }

        return totalSeconds;
    }

    private String formatSeconds(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(" soat ");
        if (minutes > 0) sb.append(minutes).append(" daqiqa ");
        if (secs > 0) sb.append(secs).append(" soniya");
        return sb.toString().trim();
    }

    private String extractAfterKeyword(String input, String... keywords) {
        String lower = input.toLowerCase();
        for (String keyword : keywords) {
            int idx = lower.indexOf(keyword);
            if (idx >= 0) {
                String after = input.substring(idx + keyword.length()).trim();
                // "qil", "och", "yoq" kabi so'zlarni olib tashla
                after = after.replaceAll("^(qil|och|yoq|boshlat|ishga tushir)\\s*", "").trim();
                if (!after.isEmpty()) return after;
            }
        }
        return null;
    }

    private String extractUrl(String input) {
        Pattern urlPattern = Pattern.compile("(https?://[\\w\\-.]+(\\.[\\w\\-.]+)+(/[\\w\\-./?%&=]*)?)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = urlPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }

        // "www." dan boshlangan
        Pattern wwwPattern = Pattern.compile("(www\\.[\\w\\-.]+(\\.[\\w\\-.]+)+)");
        Matcher wwwMatcher = wwwPattern.matcher(input);
        if (wwwMatcher.find()) {
            return "https://" + wwwMatcher.group();
        }

        return null;
    }

    public String getFallbackResponse(String input) {
        String lower = input.toLowerCase();

        if (matches(lower, "salom", "assalom", "hey", "hi")) {
            return "Salom! Sizga qanday yordam bera olaman? 😊";
        }
        if (matches(lower, "rahmat", "tashakkur", "sog'ol")) {
            return "Xush kelibsiz! Yana yordam kerak bo'lsa, aytavering! 😊";
        }
        if (matches(lower, "kim", "nima", "sen")) {
            return "Men Jarves AI man — sizning shaxsiy AI yordamchingiz! 🤖\nTelefoningizni boshqarishga, savollarga javob berishga va ko'p boshqa ishlarga yordam bera olaman.";
        }
        if (matches(lower, "qanday", "yaxshi", "ahvollar")) {
            return "Juda yaxshi, rahmat! Sizning xizmatingizdaman. Nima qilishim kerak? 😊";
        }
        if (matches(lower, "vaqt", "soat")) {
            Calendar cal = Calendar.getInstance();
            return String.format("🕐 Hozirgi vaqt: %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }

        return "Kechirasiz, bu so'rovni tushunmadim. Iltimos, boshqacha so'rang yoki quyidagi buyruqlardan birini ishlating:\n" +
                "• Qo'ng'iroq qilish\n• SMS yuborish\n• Ilova ochish\n• Qidirish";
    }
}
