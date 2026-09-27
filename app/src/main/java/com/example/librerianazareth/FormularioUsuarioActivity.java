package com.example.librerianazareth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FormularioUsuarioActivity extends AppCompatActivity {

    private TextView tvTituloFormulario;
    private EditText etNombreUsuario;
    private EditText etCorreoUsuario;
    private EditText etContrasenaUsuario;
    private Spinner spinnerRol;
    private Button btnGuardarUsuario;
    private Button btnCancelarUsuario;

    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);

        // Referencias a las vistas
        tvTituloFormulario  = findViewById(R.id.tvTituloFormulario);
        etNombreUsuario     = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario     = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario = findViewById(R.id.etContrasenaUsuario);
        spinnerRol          = findViewById(R.id.spinnerRol);
        btnGuardarUsuario   = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario  = findViewById(R.id.btnCancelarUsuario);

        // Spinner con los roles que acepta el backend
        String[] roles = {"Empleado", "Administrador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        // ¿Venimos en modo edición?
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);

        if (modoEdicion) {

            tvTituloFormulario.setText("Editar usuario");

            String nombre = getIntent().getStringExtra("nombre_usuario");
            String correo = getIntent().getStringExtra("correo_usuario");

            if (nombre != null) {
                etNombreUsuario.setText(nombre);
            }

            if (correo != null) {
                etCorreoUsuario.setText(correo);
            }

            // En edición la contraseña es opcional
            etContrasenaUsuario.setHint("Contraseña (dejar vacío para no cambiar)");
        }

        // Botón guardar
        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());

        // Botón cancelar
        btnCancelarUsuario.setOnClickListener(v -> finish());
    }

    private void guardarUsuario() {

        String username = etNombreUsuario.getText().toString().trim();
        String email    = etCorreoUsuario.getText().toString().trim();
        String password = etContrasenaUsuario.getText().toString().trim();
        String role     = spinnerRol.getSelectedItem().toString();

        // Validaciones (mismas reglas que el backend)
        if (TextUtils.isEmpty(username)) {
            etNombreUsuario.setError("Ingresá un usuario");
            etNombreUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etCorreoUsuario.setError("Ingresá un correo");
            etCorreoUsuario.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etCorreoUsuario.setError("Correo inválido");
            etCorreoUsuario.requestFocus();
            return;
        }

        // En alta la contraseña es obligatoria; en edición es opcional
        if (!modoEdicion && TextUtils.isEmpty(password)) {
            etContrasenaUsuario.setError("Ingresá una contraseña");
            etContrasenaUsuario.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(password) && password.length() < 8) {
            etContrasenaUsuario.setError("Mínimo 8 caracteres");
            etContrasenaUsuario.requestFocus();
            return;
        }

        // -------------------------------------------------------------
        // ACÁ VA LA LLAMADA AL BACKEND (Retrofit)
        //
        // JSON esperado por el RegisterSerializer:
        // {
        //   "username": "...",
        //   "email":    "...",
        //   "password": "...",
        //   "role":     "Empleado" | "Administrador"
        // }
        //IMPORTANTE SI HACE EL JSON DE OTRA FORMA VA A SALIR ERROR.
        // RegisterRequest req = new RegisterRequest(username, email, password, role);
        // apiService.register(req).enqueue(...);
        // -------------------------------------------------------------

        Toast.makeText(
                this,
                modoEdicion
                        ? "Usuario actualizado correctamente"
                        : "Usuario guardado correctamente",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}