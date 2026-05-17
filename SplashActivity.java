package com.jarves.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jarves.ai.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView logoText = findViewById(R.id.logoText);
        TextView taglineText = findViewById(R.id.taglineText);

        // Logo animatsiyasi
        ScaleAnimation scaleAnim = new ScaleAnimation(
                0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnim.setDuration(800);
        scaleAnim.setFillAfter(true);

        AlphaAnimation alphaAnim = new AlphaAnimation(0f, 1f);
        alphaAnim.setDuration(1000);
        alphaAnim.setFillAfter(true);

        logoText.startAnimation(scaleAnim);
        taglineText.startAnimation(alphaAnim);

        // 2.5 soniyadan keyin asosiy ekranga o'tish
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }
}
