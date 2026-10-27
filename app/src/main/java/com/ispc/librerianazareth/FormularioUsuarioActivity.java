package com.ispc.librerianazareth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ispc.librerianazareth.data.RetrofitClient;
import com.ispc.librerianazareth.data.model.RegisterRequest;
import com.ispc.librerianazareth.data.model.Usuario;
import com.ispc.librerianazareth.security.PasswordValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioUsuarioActivity extends AppCompatActivity {

    private TextView tvTituloFormulario;
    private EditText etNombre, etApellido, etNombreUsuario, etCorreoUsuario, etContrasenaUsuario;
    private Spinner spinnerRol;
    private Button btnGuardarUsuario, btnCancelarUsuario;
    private TextView tvRequisitosPasswordForm;

    private boolean modoEdicion = false;
    private int usuarioId = -1;

    private int colorCumple;
    private int colorFalta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);

        tvTituloFormulario      = findViewById(R.id.tvTituloFormulario);
        etNombre                = findViewById(R.id.etNombre);
        etApellido              = findViewById(R.id.etApellido);
        etNombreUsuario         = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario         = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario     = findViewById(R.id.etContrasenaUsuario);
        spinnerRol              = findViewById(R.id.spinnerRol);
        btnGuardarUsuario       = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario      = findViewById(R.id.btnCancelarUsuario);
        tvRequisitosPasswordForm = findViewById(R.id.tvRequisitosPasswordForm);

        // Colores
        colorFalta  = ContextCompat.getColor(this, R.color.rojo_error);
        colorCumple = android.graphics.Color.parseColor("#4CAF50");

        // Roles
        String[] roles = {"Empleado", "Administrador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        // Modo edición
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);

        if (modoEdicion) {
            tvTituloFormulario.setText(R.string.form_usuario_titulo_editar);
            usuarioId = getIntent().getIntExtra("usuario_id", -1);

            String nombre = getIntent().getStringExtra("nombre_usuario");
            String apellido = getIntent().getStringExtra("apellido_usuario");
            String username = getIntent().getStringExtra("username_usuario");
            String correo = getIntent().getStringExtra("correo_usuario");
            String rol = getIntent().getStringExtra("rol_usuario");

            if (username != null) etNombreUsuario.setText(username);
            if (nombre != null) etNombre.setText(nombre);
            if (apellido != null) etApellido.setText(apellido);
            if (correo != null) etCorreoUsuario.setText(correo);

            etContrasenaUsuario.setHint(R.string.msj_contrasena_dejar_vacio);

            if (rol != null) {
                for (int i = 0; i < roles.length; i++) {
                    if (roles[i].equalsIgnoreCase(rol)) {
                        spinnerRol.setSelection(i);
                        break;
                    }
                }
            }
        }

        // --- LÓGICA DE REQUISITOS EN TIEMPO REAL ---
        etContrasenaUsuario.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvRequisitosPasswordForm.setVisibility(View.VISIBLE);
                    actualizarRequisitos(s.toString());
                } else {
                    tvRequisitosPasswordForm.setVisibility(View.GONE);
                }
            }
        });

        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());
        btnCancelarUsuario.setOnClickListener(v -> finish());
    }

    private void actualizarRequisitos(String password) {
        StringBuilder faltantes = new StringBuilder();

        if (password.length() < 8)                         faltantes.append("8 caracteres, ");
        if (!password.matches(".*[A-Z].*"))                faltantes.append("una mayúscula, ");
        if (!password.matches(".*[a-z].*"))                faltantes.append("una minúscula, ");
        if (!password.matches(".*[0-9].*"))                faltantes.append("un número, ");
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))
            faltantes.append("un símbolo, ");

        if (faltantes.length() == 0) {
            tvRequisitosPasswordForm.setText(R.string.req_password_ok);
            tvRequisitosPasswordForm.setTextColor(colorCumple);
        } else {
            String lista = faltantes.substring(0, faltantes.length() - 2);
            tvRequisitosPasswordForm.setText(getString(R.string.req_password_faltan, lista));
            tvRequisitosPasswordForm.setTextColor(colorFalta);
        }
    }

    private void guardarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String username = etNombreUsuario.getText().toString().trim();
        String email = etCorreoUsuario.getText().toString().trim();
        String password = etContrasenaUsuario.getText().toString().trim();
        String role = spinnerRol.getSelectedItem().toString();

        // --- VALIDACIÓN ACUMULATIVA ---
        boolean hayError = false;
        EditText primerCampoConError = null;

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_ingresar_nombre));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etNombre;
        } else etNombre.setError(null);

        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError(getString(R.string.error_ingresar_apellido));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etApellido;
        } else etApellido.setError(null);

        if (TextUtils.isEmpty(username)) {
            etNombreUsuario.setError(getString(R.string.error_ingresar_usuario));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etNombreUsuario;
        } else if (username.length() < 3) {
            etNombreUsuario.setError(getString(R.string.error_minimo_3));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etNombreUsuario;
        } else etNombreUsuario.setError(null);

        if (TextUtils.isEmpty(email)) {
            etCorreoUsuario.setError(getString(R.string.error_ingresar_correo));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etCorreoUsuario;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etCorreoUsuario.setError(getString(R.string.error_correo_invalido));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etCorreoUsuario;
        } else etCorreoUsuario.setError(null);

        // Contraseña (obligatoria al crear, opcional al editar)
        if (!modoEdicion && TextUtils.isEmpty(password)) {
            etContrasenaUsuario.setError(getString(R.string.error_ingresar_contrasena));
            hayError = true;
            if (primerCampoConError == null) primerCampoConError = etContrasenaUsuario;
        } else if (!TextUtils.isEmpty(password)) {
            String errorPassword = PasswordValidator.validate(password);
            if (errorPassword != null) {
                etContrasenaUsuario.setError(errorPassword);
                hayError = true;
                if (primerCampoConError == null) primerCampoConError = etContrasenaUsuario;
            } else {
                etContrasenaUsuario.setError(null);
            }
        } else {
            etContrasenaUsuario.setError(null);
        }

        if (hayError) {
            if (primerCampoConError != null) primerCampoConError.requestFocus();
            Toast.makeText(this, R.string.msj_completar_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        // --- ENVÍO AL BACKEND ---
        btnGuardarUsuario.setEnabled(false);

        RegisterRequest req = new RegisterRequest(username, nombre, apellido, email, password, role);

        if (modoEdicion && usuarioId != -1) {
            RetrofitClient.getApi(this).actualizarUsuario(usuarioId, req).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    btnGuardarUsuario.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_usuario_actualizado, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String detalle = "";
                        try {
                            if (response.errorBody() != null) detalle = response.errorBody().string();
                        } catch (Exception e) { detalle = e.getMessage(); }
                        android.util.Log.e("ACTUALIZAR_USUARIO", "HTTP " + response.code() + " | " + detalle);
                        Toast.makeText(FormularioUsuarioActivity.this, "Error HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    btnGuardarUsuario.setEnabled(true);
                    android.util.Log.e("ACTUALIZAR_USUARIO", "Error de conexión", t);
                    Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_error_conexion, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            RetrofitClient.getApi(this).crearUsuario(req).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    btnGuardarUsuario.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(FormularioUsuarioActivity.this, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String detalle = "";
                        try {
                            if (response.errorBody() != null) detalle = response.errorBody().string();
                        } catch (Exception e) { detalle = e.getMessage(); }
                        android.util.Log.e("CREAR_USUARIO", "HTTP " + response.code() + " | " + detalle);
                        Toast.makeText(FormularioUsuarioActivity.this, "Error HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    btnGuardarUsuario.setEnabled(true);
                    android.util.Log.e("CREAR_USUARIO", "Error de conexión", t);
                    Toast.makeText(FormularioUsuarioActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}