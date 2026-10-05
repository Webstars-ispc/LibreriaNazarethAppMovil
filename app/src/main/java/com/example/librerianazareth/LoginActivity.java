package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.local.TokenManager;
import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private TextView tvModoInvitado;
    private TextView tvIrAlRegistro;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario      = findViewById(R.id.etUsuario);
        etPassword     = findViewById(R.id.etPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        tvModoInvitado = findViewById(R.id.tvModoInvitado);
        tvIrAlRegistro = findViewById(R.id.tvIrAlRegistro);
        tokenManager   = new TokenManager(this);

        String emailPrellenado = getIntent().getStringExtra("EXTRA_EMAIL");
        if (emailPrellenado != null && !emailPrellenado.isEmpty()) {
            etUsuario.setText(emailPrellenado);
        }

        btnLogin.setOnClickListener(v -> intentarLogin());

        // Ir a la pantalla de registro
        tvIrAlRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Modo invitado
        tvModoInvitado.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("EXTRA_USUARIO", "Invitado");
            intent.putExtra("ES_INVITADO", true);
            startActivity(intent);
            finish();
        });
    }

    private void intentarLogin() {
        String email    = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones locales
        if (email.isEmpty()) {
            etUsuario.setError("Ingresá tu correo");
            etUsuario.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Ingresá tu contraseña");
            etPassword.requestFocus();
            return;
        }

        // Deshabilitar el botón para evitar doble click
        btnLogin.setEnabled(false);
        btnLogin.setText("Ingresando...");

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getApi(this)
                .login(request)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call,
                                           @NonNull Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();

                            // 1) Guardar tokens
                            tokenManager.saveTokens(body.getAccess(), body.getRefresh());

                            // 2) Traer el perfil (username) y navegar
                            cargarPerfilYNavegar();

                        } else {
                            restaurarBoton();
                            Toast.makeText(LoginActivity.this,
                                    "Credenciales inválidas",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call,
                                          @NonNull Throwable t) {
                        restaurarBoton();
                        Toast.makeText(LoginActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cargarPerfilYNavegar() {
        RetrofitClient.getApi(this)
                .me()
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserProfileResponse> call,
                                           @NonNull Response<UserProfileResponse> response) {

                        String username = "";
                        if (response.isSuccessful() && response.body() != null) {
                            username = response.body().getUsername();
                            tokenManager.saveRole(response.body().getRole());
                        }
                        tokenManager.saveUsername(username);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("EXTRA_USUARIO", username);
                        intent.putExtra("ES_INVITADO", false);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserProfileResponse> call,
                                          @NonNull Throwable t) {
                        // El login ya fue exitoso; navegamos igual
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("EXTRA_USUARIO", "");
                        intent.putExtra("ES_INVITADO", false);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void restaurarBoton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("INGRESAR");
    }
}