package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.NetworkConstants;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.local.JwtUtils;
import com.example.librerianazareth.data.local.TokenManager;
import com.example.librerianazareth.data.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::verificarSesion, 2500);
    }

    private void verificarSesion() {
        TokenManager tokenManager = new TokenManager(this);

        String accessToken = tokenManager.getAccessToken();

        // 1) Sin token o token expirado localmente → al login
        if (accessToken == null || JwtUtils.isExpired(accessToken)) {
            if (accessToken != null) {
                tokenManager.clear();
            }
            irALogin();
            return;
        }

        // 2) Token localmente válido → confirmar contra el backend
        String authHeader = NetworkConstants.TOKEN_PREFIX + accessToken;

        RetrofitClient.getApi(this)
                .me(authHeader)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserProfileResponse> call,
                                           @NonNull Response<UserProfileResponse> response) {
                        if (response.isSuccessful()) {
                            // Token válido → al menú principal
                            irAMain();
                        } else {
                            // 401 (expirado), 403, 500, etc. → limpiar y al login
                            tokenManager.clear();
                            irALogin();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserProfileResponse> call,
                                          @NonNull Throwable t) {
                        // Error de red → limpiar y al login
                        tokenManager.clear();
                        irALogin();
                    }
                });
    }

    private void irAMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void irALogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}