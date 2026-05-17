package com.jarves.ai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarves.ai.R;
import com.jarves.ai.adapters.ChatAdapter;
import com.jarves.ai.models.ChatMessage;
import com.jarves.ai.utils.CommandProcessor;
import com.jarves.ai.utils.GeminiApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    // UI elementlar
    private RecyclerView recyclerView;
    private EditText inputField;
    private ImageButton sendButton;
    private ImageButton micButton;
    private TextView statusText;
    private View typingIndicator;

    // Logic
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private CommandProcessor commandProcessor;
    private GeminiApi geminiApi;

    // Permission kodlari
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SPEECH_REQUEST_CODE = 200;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initTTS();
        initComponents();
        checkPermissions();
        showWelcomeMessage();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);
        micButton = findViewById(R.id.micButton);
        statusText = findViewById(R.id.statusText);
        typingIndicator = findViewById(R.id.typingIndicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
        micButton.setOnClickListener(v -> startVoiceInput());
    }

    private void initComponents() {
        commandProcessor = new CommandProcessor(this);
        geminiApi = new GeminiApi();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // O'zbek tili yo'q bo'lsa, rus yoki ingliz tilida gapiradi
            int result = tts.setLanguage(new Locale("uz", "UZ"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.getDefault());
            }
            ttsReady = true;
        }
    }

    private void showWelcomeMessage() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            addBotMessage("Salom! Men Jarves AI man. Sizga qanday yordam bera olaman?\n\n" +
                    "🔹 Qo'ng'iroq qilish\n" +
                    "🔹 SMS yuborish\n" +
                    "🔹 Ilova ochish\n" +
                    "🔹 Savollarga javob berish\n" +
                    "🔹 Va boshqa ko'p narsalar!");
        }, 500);
    }

    private void sendMessage() {
        String text = inputField.getText().toString().trim();
        if (text.isEmpty()) return;

        inputField.setText("");
        addUserMessage(text);
        processUserInput(text);
    }

    private void processUserInput(String input) {
        showTyping(true);

        // Avval buyruq ekanligini tekshir
        String command = commandProcessor.detectCommand(input);

        if (command != null) {
            // Buyruqni bajar
            executeCommand(command, input);
        } else {
            // AI ga yubor
            askAI(input);
        }
    }

    private void executeCommand(String command, String originalInput) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showTyping(false);
            String response = commandProcessor.executeCommand(command, originalInput);
            addBotMessage(response);
            speak(response);
        }, 800);
    }

    private void askAI(String question) {
        geminiApi.ask(question, new GeminiApi.Callback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> {
                    showTyping(false);
                    addBotMessage(response);
                    speak(response);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showTyping(false);
                    String fallback = commandProcessor.getFallbackResponse(question);
                    addBotMessage(fallback);
                    speak(fallback);
                });
            }
        });
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_USER));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_BOT));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    private void showTyping(boolean show) {
        typingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            typingIndicator.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
        }
    }

    private void speak(String text) {
        if (ttsReady && tts != null) {
            // Emoji va maxsus belgilarni olib tashla
            String cleanText = text.replaceAll("[^\\p{L}\\p{N}\\s.,!?'-]", "");
            tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uz-UZ");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "uz-UZ");
        intent.putExtra(RecognizerIntent.EXTRA_ALSO_RECOGNIZE_LANGUAGE, "ru-RU");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Gapiring...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Ovoz tanish xizmati mavjud emas", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                inputField.setText(spokenText);
                sendMessage();
            }
        }
    }

    private void checkPermissions() {
        List<String> needed = new ArrayList<>();
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needed.add(perm);
            }
        }
        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "Ba'zi xususiyatlar cheklangan bo'lishi mumkin", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
