package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.local.TokenManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TokenManager tokenManager = new TokenManager(SplashActivity.this);

                Intent intent;
                if (tokenManager.isLoggedIn()) {
                    // Ya hay token guardado → al menú principal
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // No hay token → al login
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}