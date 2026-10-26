package com.ispc.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ispc.librerianazareth.data.RetrofitClient;
import com.ispc.librerianazareth.data.local.TokenManager;
import com.ispc.librerianazareth.data.model.RegisterPublicRequest;
import com.ispc.librerianazareth.data.model.RegisterPublicResponse;
import com.ispc.librerianazareth.security.PasswordValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etUsuario, etEmail, etPassword, etPasswordConfirmar;
    private ImageView ivMostrarPassword, ivMostrarPasswordConfirmar;
    private Button btnRegistro;
    private TextView tvIrAlLogin;
    private TextView tvRequisitosPassword;
    private TokenManager tokenManager;

    private boolean passwordVisible = false;
    private boolean passwordConfirmarVisible = false;

    private int colorCumple;
    private int colorFalta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre                   = findViewById(R.id.etRegistroNombre);
        etApellido                 = findViewById(R.id.etRegistroApellido);
        etUsuario                  = findViewById(R.id.etRegistroUsuario);
        etEmail                    = findViewById(R.id.etRegistroEmail);
        etPassword                 = findViewById(R.id.etRegistroPassword);
        etPasswordConfirmar        = findViewById(R.id.etRegistroPasswordConfirmar);
        ivMostrarPassword          = findViewById(R.id.ivMostrarPassword);
        ivMostrarPasswordConfirmar = findViewById(R.id.ivMostrarPasswordConfirmar);
        btnRegistro                = findViewById(R.id.btnRegistro);
        tvIrAlLogin                = findViewById(R.id.tvIrAlLogin);
        tvRequisitosPassword       = findViewById(R.id.tvRequisitosPassword);
        tokenManager               = new TokenManager(this);

        // Colores para los requisitos
        colorFalta  = ContextCompat.getColor(this, R.color.rojo_error);
        colorCumple = android.graphics.Color.parseColor("#4CAF50");

        btnRegistro.setOnClickListener(v -> intentarRegistro());

        tvIrAlLogin.setOnClickListener(v -> finish());

        // Toggle de contraseña principal
        ivMostrarPassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            togglePassword(etPassword, ivMostrarPassword, passwordVisible);
        });

        // Toggle de confirmar contraseña
        ivMostrarPasswordConfirmar.setOnClickListener(v -> {
            passwordConfirmarVisible = !passwordConfirmarVisible;
            togglePassword(etPasswordConfirmar, ivMostrarPasswordConfirmar, passwordConfirmarVisible);
        });

        // Validación en tiempo real de los requisitos de contraseña
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarRequisitos(s.toString());
            }
        });
        actualizarRequisitos(etPassword.getText().toString());
    }

    /** Alterna la visibilidad de un campo de contraseña. */
    private void togglePassword(EditText campo, ImageView icono, boolean visible) {
        if (visible) {
            campo.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            icono.setImageResource(R.drawable.ic_ojo_abierto);
        } else {
            campo.setTransformationMethod(PasswordTransformationMethod.getInstance());
            icono.setImageResource(R.drawable.ic_ojo_cerrado);
        }
        campo.setSelection(campo.getText().length());
    }

    /** Actualiza el TextView único con los requisitos faltantes o el mensaje de éxito. */
    private void actualizarRequisitos(String password) {
        if (password.isEmpty()) {
            tvRequisitosPassword.setText(R.string.req_password_titulo);
            tvRequisitosPassword.setTextColor(colorFalta);
            return;
        }

        StringBuilder faltantes = new StringBuilder();

        if (password.length() < 8) faltantes.append("8 caracteres, ");
        if (!password.matches(".*[A-Z].*")) faltantes.append("una mayúscula, ");
        if (!password.matches(".*[a-z].*")) faltantes.append("una minúscula, ");
        if (!password.matches(".*[0-9].*")) faltantes.append("un número, ");
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))
            faltantes.append("un símbolo, ");

        if (faltantes.length() == 0) {
            tvRequisitosPassword.setText(R.string.req_password_ok);
            tvRequisitosPassword.setTextColor(colorCumple);
        } else {
            String lista = faltantes.substring(0, faltantes.length() - 2);
            tvRequisitosPassword.setText(getString(R.string.req_password_faltan, lista));
            tvRequisitosPassword.setTextColor(colorFalta);
        }
    }

    private void intentarRegistro() {
        String nombre    = etNombre.getText().toString().trim();
        String apellido  = etApellido.getText().toString().trim();
        String username  = etUsuario.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String confirmar = etPasswordConfirmar.getText().toString().trim();

        boolean hayError = false;
        EditText primerCampoConError = null;

        // --- NOMBRE ---
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_ingresar_tu_nombre));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etNombre;
        } else {
            etNombre.setError(null);
        }

        // --- APELLIDO ---
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError(getString(R.string.error_ingresar_tu_apellido));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etApellido;
        } else {
            etApellido.setError(null);
        }

        // --- USUARIO ---
        if (TextUtils.isEmpty(username)) {
            etUsuario.setError(getString(R.string.error_ingresar_usuario));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etUsuario;
        } else if (username.length() < 3) {
            etUsuario.setError(getString(R.string.error_minimo_3));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etUsuario;
        } else {
            etUsuario.setError(null);
        }

        // --- EMAIL ---
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_ingresar_tu_correo));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etEmail;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_correo_invalido));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etEmail;
        } else {
            etEmail.setError(null);
        }

        // --- CONTRASEÑA ---
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_ingresar_contrasena));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etPassword;
        } else {
            String errorPassword = PasswordValidator.validate(password);
            if (errorPassword != null) {
                etPassword.setError(errorPassword);
                hayError = true;
                if (primerCampoConError == null) primerCampoConError = etPassword;
            } else {
                etPassword.setError(null);
            }
        }

        // --- CONFIRMAR CONTRASEÑA ---
        if (TextUtils.isEmpty(confirmar)) {
            etPasswordConfirmar.setError(getString(R.string.error_ingresar_contrasena));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etPasswordConfirmar;
        } else if (!password.equals(confirmar)) {
            etPasswordConfirmar.setError(getString(R.string.error_contrasenas_no_coinciden));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etPasswordConfirmar;
        } else {
            etPasswordConfirmar.setError(null);
        }

        // Si hay algún error, enfocamos el primer campo con error y mostramos un Toast
        if (hayError) {
            if (primerCampoConError != null) {
                primerCampoConError.requestFocus();
            }
            Toast.makeText(this, R.string.msj_completar_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        // --- TODO OK: continuamos con el registro ---
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