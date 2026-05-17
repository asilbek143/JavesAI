package com.jarves.ai.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gemini AI API bilan muloqot
 * O'zbek tilida javob beradi
 */
public class GeminiApi {

    private static final String TAG = "GeminiApi";

    // Bepul Gemini API key olish: https://aistudio.google.com/app/apikey
    // Quyidagi o'rinni o'z API kalingiz bilan almashtiring:
    private static final String API_KEY = "YOUR_GEMINI_API_KEY_HERE";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    private static final String SYSTEM_PROMPT =
            "Sen Jarves AI degan yordamchi dastursan. " +
            "Faqat O'zbek tilida javob ber. " +
            "Qisqa, aniq va foydali javob ber. " +
            "Emojlardan foydalanib javobni qiziqarli qil. " +
            "Agar foydalanuvchi O'zbek tilida gapirsa, O'zbek tilida javob ber.";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onResponse(String response);
        void onError(String error);
    }

    public void ask(String question, Callback callback) {
        executor.execute(() -> {
            try {
                String response = callGeminiAPI(question);
                mainHandler.post(() -> callback.onResponse(response));
            } catch (Exception e) {
                Log.e(TAG, "API xatosi: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    private String callGeminiAPI(String question) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);

        // JSON so'rov tuzish
        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();

        // Tizim xabari
        JSONObject systemContent = new JSONObject();
        systemContent.put("role", "user");
        JSONArray systemParts = new JSONArray();
        JSONObject systemPart = new JSONObject();
        systemPart.put("text", SYSTEM_PROMPT + "\n\nFoydalanuvchi: " + question);
        systemParts.put(systemPart);
        systemContent.put("parts", systemParts);
        contents.put(systemContent);

        requestBody.put("contents", contents);

        // Javob sozlamalari
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 512);
        requestBody.put("generationConfig", generationConfig);

        // So'rov yuborish
        OutputStream os = connection.getOutputStream();
        os.write(requestBody.toString().getBytes("UTF-8"));
        os.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // JSON javobini parse qilish
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject content = candidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
        } else {
            throw new Exception("API xatosi: " + responseCode);
        }

        return "Kechirasiz, javob olishda xatolik yuz berdi.";
    }
}
