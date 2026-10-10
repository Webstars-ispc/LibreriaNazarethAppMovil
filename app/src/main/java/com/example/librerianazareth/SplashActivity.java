package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.local.JwtUtils;
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

                String accessToken = tokenManager.getAccessToken();

                // Hay sesión solo si existe el token y NO está expirado
                boolean sesionValida = accessToken != null
                        && !JwtUtils.isExpired(accessToken);

                Intent intent;
                if (sesionValida) {
                    // Token válido → al menú principal
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Sin token o token expirado → limpiar y al login
                    if (accessToken != null) {
                        tokenManager.clear();
                    }
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}