package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.local.TokenManager;
import com.example.librerianazareth.data.model.RegisterPublicRequest;
import com.example.librerianazareth.data.model.RegisterPublicResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etUsuario, etEmail, etPassword, etPasswordConfirmar;
    private Button btnRegistro;
    private TextView tvIrAlLogin;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre           = findViewById(R.id.etRegistroNombre);
        etApellido         = findViewById(R.id.etRegistroApellido);
        etUsuario          = findViewById(R.id.etRegistroUsuario);
        etEmail             = findViewById(R.id.etRegistroEmail);
        etPassword          = findViewById(R.id.etRegistroPassword);
        etPasswordConfirmar = findViewById(R.id.etRegistroPasswordConfirmar);
        btnRegistro         = findViewById(R.id.btnRegistro);
        tvIrAlLogin         = findViewById(R.id.tvIrAlLogin);
        tokenManager        = new TokenManager(this);

        btnRegistro.setOnClickListener(v -> intentarRegistro());

        tvIrAlLogin.setOnClickListener(v -> finish());
    }

    private void intentarRegistro() {
        String nombre    = etNombre.getText().toString().trim();
        String apellido  = etApellido.getText().toString().trim();
        String username  = etUsuario.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String confirmar = etPasswordConfirmar.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresá tu nombre");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("Ingresá tu apellido");
            etApellido.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsuario.setError("Ingresá un usuario");
            etUsuario.requestFocus();
            return;
        }

        if (username.length() < 3) {
            etUsuario.setError("Mínimo 3 caracteres");
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresá tu correo");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo inválido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresá una contraseña");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Mínimo 8 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmar)) {
            etPasswordConfirmar.setError("Las contraseñas no coinciden");
            etPasswordConfirmar.requestFocus();
            return;
        }

        btnRegistro.setEnabled(false);
        btnRegistro.setText("Registrando...");

        RegisterPublicRequest request =
                new RegisterPublicRequest(username, nombre, apellido, email, password);

        RetrofitClient.getApi(this)
                .registerPublic(request)
                .enqueue(new Callback<RegisterPublicResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RegisterPublicResponse> call,
                                           @NonNull Response<RegisterPublicResponse> response) {
                        RegisterPublicResponse body = response.body();

                        if (response.isSuccessful() && body != null) {
                            if (body.getAccess() == null || body.getRefresh() == null) {
                                volverAlLogin(email, "Cuenta creada. Iniciá sesión.");
                                return;
                            }

                            tokenManager.saveTokens(body.getAccess(), body.getRefresh());
                            tokenManager.saveUsername(body.getUsername());
                            tokenManager.saveRole(body.getRole());

                            Toast.makeText(RegistroActivity.this,
                                    "Cuenta creada. Bienvenido/a, " + body.getUsername(),
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            intent.putExtra("EXTRA_USUARIO", body.getUsername());
                            intent.putExtra("ES_INVITADO", false);
                            startActivity(intent);
                            finish();

                        } else {
                            restaurarBoton();
                            Toast.makeText(RegistroActivity.this,
                                    mensajeError(response),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegisterPublicResponse> call,
                                          @NonNull Throwable t) {
                        restaurarBoton();
                        Toast.makeText(RegistroActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void volverAlLogin(String email, String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("EXTRA_EMAIL", email);
        startActivity(intent);
        finish();
    }

    private String mensajeError(Response<?> response) {
        int code = response.code();
        if (code == 401 || code == 403) {
            return "No autorizado para registrarse.";
        }

        String detalle = "";
        try {
            if (response.errorBody() != null) {
                detalle = response.errorBody().string();
            }
        } catch (Exception ignored) {
        }

        detalle = detalle.replaceAll("[\\[\\]\"]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("detail:", "")
                .trim();

        if (detalle.isEmpty()) {
            if (code == 400) {
                return "Datos inválidos. Revisá los campos del formulario.";
            }
            return "No se pudo crear la cuenta (error " + code + ")";
        }
        return "Datos inválidos: " + detalle;
    }

    private void restaurarBoton() {
        btnRegistro.setEnabled(true);
        btnRegistro.setText("REGISTRARME");
    }
}