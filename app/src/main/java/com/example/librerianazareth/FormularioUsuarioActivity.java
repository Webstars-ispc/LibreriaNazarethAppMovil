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

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.RegisterRequest;
import com.example.librerianazareth.data.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioUsuarioActivity extends AppCompatActivity {

    private TextView tvTituloFormulario;
    private EditText etNombre;
    private EditText etApellido;
    private EditText etNombreUsuario;
    private EditText etCorreoUsuario;
    private EditText etContrasenaUsuario;
    private Spinner spinnerRol;
    private Button btnGuardarUsuario;
    private Button btnCancelarUsuario;

    private boolean modoEdicion = false;
    private int usuarioId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);

        tvTituloFormulario  = findViewById(R.id.tvTituloFormulario);
        etNombre            = findViewById(R.id.etNombre);
        etApellido          = findViewById(R.id.etApellido);
        etNombreUsuario     = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario     = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario = findViewById(R.id.etContrasenaUsuario);
        spinnerRol          = findViewById(R.id.spinnerRol);
        btnGuardarUsuario   = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario  = findViewById(R.id.btnCancelarUsuario);

        // Roles aceptados por el backend
        String[] roles = {"Empleado", "Administrador"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRol.setAdapter(adapter);

        // Comprobar si estamos editando
        modoEdicion = getIntent().getBooleanExtra(
                "modo_edicion",
                false
        );

        if (modoEdicion) {
            tvTituloFormulario.setText(R.string.form_usuario_titulo_editar);
            usuarioId = getIntent().getIntExtra("usuario_id", -1);

            String nombre = getIntent().getStringExtra("nombre_usuario");
            String apellido = getIntent().getStringExtra("apellido_usuario");
            String username = getIntent().getStringExtra("username_usuario");
            String correo = getIntent().getStringExtra("correo_usuario");
            String rol = getIntent().getStringExtra("rol_usuario");

            if (username != null) {
                etNombreUsuario.setText(username);
            } else if (nombre != null) {
                etNombreUsuario.setText(nombre);
            }
            if (nombre != null) etNombre.setText(nombre);
            if (apellido != null) etApellido.setText(apellido);
            if (correo != null) etCorreoUsuario.setText(correo);

            etContrasenaUsuario.setHint(
                    R.string.msj_contrasena_dejar_vacio
            );

            // Seleccionar rol actual
            if (rol != null) {
                for (int i = 0; i < roles.length; i++) {
                    if (roles[i].equalsIgnoreCase(rol)) {
                        spinnerRol.setSelection(i);
                        break;
                    }
                }
            }
        }

        // Guardar
        btnGuardarUsuario.setOnClickListener(
                v -> guardarUsuario()
        );

        // Cancelar
        btnCancelarUsuario.setOnClickListener(
                v -> finish()
        );
    }

    private void guardarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String username = etNombreUsuario.getText().toString().trim();
        String email = etCorreoUsuario.getText().toString().trim();
        String password = etContrasenaUsuario.getText().toString().trim();
        String role = spinnerRol.getSelectedItem().toString();

        // Validaciones
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_ingresar_nombre));
            etNombre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError(getString(R.string.error_ingresar_apellido));
            etApellido.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etNombreUsuario.setError(getString(R.string.error_ingresar_usuario));
            etNombreUsuario.requestFocus();
            return;
        }
        if (username.length() < 3) {
            etNombreUsuario.setError(getString(R.string.error_minimo_3));
            etNombreUsuario.requestFocus();
            return;
        }

        // Validar correo
        if (TextUtils.isEmpty(email)) {
            etCorreoUsuario.setError(
                    getString(R.string.error_ingresar_correo)
            );
            etCorreoUsuario.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etCorreoUsuario.setError(
                    getString(R.string.error_correo_invalido)
            );
            etCorreoUsuario.requestFocus();
            return;
        }

        // Contraseña obligatoria al crear
        if (!modoEdicion &&
                TextUtils.isEmpty(password)) {

            etContrasenaUsuario.setError(
                    getString(R.string.error_ingresar_contrasena)
            );
            etContrasenaUsuario.requestFocus();
            return;
        }

        // Mínimo 8 caracteres
        if (!TextUtils.isEmpty(password) &&
                password.length() < 8) {

            etContrasenaUsuario.setError(
                    getString(R.string.error_minimo_8)
            );
            etContrasenaUsuario.requestFocus();
            return;
        }

        btnGuardarUsuario.setEnabled(false);

        RegisterRequest req = new RegisterRequest(username, nombre, apellido, email, password, role);

        if (modoEdicion && usuarioId != -1) {
            RetrofitClient.getApi(this).actualizarUsuario(usuarioId, req)
                    .enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                            btnGuardarUsuario.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_usuario_actualizado, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_error_actualizar_usuario, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            btnGuardarUsuario.setEnabled(true);
                            Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_error_conexion, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            RetrofitClient.getApi(this).crearUsuario(req)
                    .enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                            btnGuardarUsuario.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_usuario_guardado, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_error_crear_usuario, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            btnGuardarUsuario.setEnabled(true);
                            Toast.makeText(FormularioUsuarioActivity.this, R.string.msj_error_conexion, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}