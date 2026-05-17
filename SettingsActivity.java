package com.jarves.ai.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jarves.ai.R;

public class SettingsActivity extends AppCompatActivity {

    private EditText apiKeyField;
    private Switch ttsSwitch;
    private Switch voiceSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("jarves_prefs", MODE_PRIVATE);

        apiKeyField = findViewById(R.id.apiKeyField);
        ttsSwitch = findViewById(R.id.ttsSwitch);
        voiceSwitch = findViewById(R.id.voiceSwitch);

        // Saqlangan sozlamalarni yuklash
        apiKeyField.setText(prefs.getString("gemini_api_key", ""));
        ttsSwitch.setChecked(prefs.getBoolean("tts_enabled", true));
        voiceSwitch.setChecked(prefs.getBoolean("voice_enabled", true));

        findViewById(R.id.saveButton).setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gemini_api_key", apiKeyField.getText().toString().trim());
        editor.putBoolean("tts_enabled", ttsSwitch.isChecked());
        editor.putBoolean("voice_enabled", voiceSwitch.isChecked());
        editor.apply();

        Toast.makeText(this, "✅ Sozlamalar saqlandi!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
