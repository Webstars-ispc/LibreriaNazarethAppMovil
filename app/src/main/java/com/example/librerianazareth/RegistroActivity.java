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
            etNombre.setError(getString(R.string.error_ingresar_tu_nombre));
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError(getString(R.string.error_ingresar_tu_apellido));
            etApellido.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsuario.setError(getString(R.string.error_ingresar_usuario));
            etUsuario.requestFocus();
            return;
        }

        if (username.length() < 3) {
            etUsuario.setError(getString(R.string.error_minimo_3));
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_ingresar_tu_correo));
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_correo_invalido));
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_ingresar_contrasena));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError(getString(R.string.error_minimo_8));
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmar)) {
            etPasswordConfirmar.setError(getString(R.string.error_contrasenas_no_coinciden));
            etPasswordConfirmar.requestFocus();
            return;
        }

        btnRegistro.setEnabled(false);
        btnRegistro.setText(R.string.registro_boton_cargando);

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
                                volverAlLogin(email, getString(R.string.msj_cuenta_creada_iniciar_sesion));
                                return;
                            }

                            tokenManager.saveTokens(body.getAccess(), body.getRefresh());
                            tokenManager.saveUsername(body.getUsername());
                            tokenManager.saveRole(body.getRole());

                            Toast.makeText(RegistroActivity.this,
                                    getString(R.string.msj_cuenta_creada_bienvenido, body.getUsername()),
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
                                getString(R.string.msj_error_red, t.getMessage()),
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
            return getString(R.string.msj_no_autorizado_registro);
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
                return getString(R.string.msj_datos_invalidos);
            }
            return getString(R.string.msj_no_se_pudo_crear_cuenta, code);
        }
        return getString(R.string.msj_datos_invalidos_detalle, detalle);
    }

    private void restaurarBoton() {
        btnRegistro.setEnabled(true);
        btnRegistro.setText(R.string.registro_boton);
    }
}