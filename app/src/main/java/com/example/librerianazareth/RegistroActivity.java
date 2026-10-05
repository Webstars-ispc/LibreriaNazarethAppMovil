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
import com.example.librerianazareth.data.model.RegisterPublicRequest;
import com.example.librerianazareth.data.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etUsuario, etEmail, etPassword, etPasswordConfirmar;
    private Button btnRegistro;
    private TextView tvIrAlLogin;

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
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(@NonNull Call<Usuario> call,
                                           @NonNull Response<Usuario> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this,
                                    "Cuenta creada. Podés iniciar sesión.",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                            intent.putExtra("EXTRA_EMAIL", email);
                            startActivity(intent);
                            finish();

                        } else {
                            restaurarBoton();
                            Toast.makeText(RegistroActivity.this,
                                    mensajeError(response.code()),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Usuario> call,
                                          @NonNull Throwable t) {
                        restaurarBoton();
                        Toast.makeText(RegistroActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String mensajeError(int code) {
        if (code == 400) {
            return "Datos inválidos. Revisá el usuario y el correo.";
        }
        if (code == 401 || code == 403) {
            return "No autorizado para registrarse.";
        }
        if (code == 409) {
            return "Ese usuario o correo ya está registrado.";
        }
        return "No se pudo crear la cuenta (error " + code + ")";
    }

    private void restaurarBoton() {
        btnRegistro.setEnabled(true);
        btnRegistro.setText("REGISTRARME");
    }
}