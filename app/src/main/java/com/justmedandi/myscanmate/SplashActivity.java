package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoImage);

        // Animasi masuk
        logo.animate().alpha(1f).translationY(0f).setDuration(1200).start();

        // Setelah delay, lanjut ke LoginActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, activity_login.class));
            finish();
        }, SPLASH_DELAY);
    }
}
