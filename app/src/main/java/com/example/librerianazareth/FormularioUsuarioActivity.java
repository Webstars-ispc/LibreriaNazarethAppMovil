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

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        tvTituloFormulario = findViewById(R.id.tvTituloFormulario);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario = findViewById(R.id.etContrasenaUsuario);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario = findViewById(R.id.btnCancelarUsuario);

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

            tvTituloFormulario.setText("Editar usuario");

            String nombre = getIntent().getStringExtra(
                    "nombre_usuario"
            );

            String correo = getIntent().getStringExtra(
                    "correo_usuario"
            );

            if (nombre != null) {
                etNombreUsuario.setText(nombre);
            }

            if (correo != null) {
                etCorreoUsuario.setText(correo);
            }

            etContrasenaUsuario.setHint(
                    "Contraseña (dejar vacío para no cambiar)"
            );
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

        String username = etNombreUsuario
                .getText()
                .toString()
                .trim();

        String email = etCorreoUsuario
                .getText()
                .toString()
                .trim();

        String password = etContrasenaUsuario
                .getText()
                .toString()
                .trim();

        String role = spinnerRol
                .getSelectedItem()
                .toString();

        // Validar usuario
        if (TextUtils.isEmpty(username)) {
            etNombreUsuario.setError(
                    "Ingresá un usuario"
            );
            etNombreUsuario.requestFocus();
            return;
        }

        // Validar correo
        if (TextUtils.isEmpty(email)) {
            etCorreoUsuario.setError(
                    "Ingresá un correo"
            );
            etCorreoUsuario.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etCorreoUsuario.setError(
                    "Correo inválido"
            );
            etCorreoUsuario.requestFocus();
            return;
        }

        // Contraseña obligatoria al crear
        if (!modoEdicion &&
                TextUtils.isEmpty(password)) {

            etContrasenaUsuario.setError(
                    "Ingresá una contraseña"
            );
            etContrasenaUsuario.requestFocus();
            return;
        }

        // Mínimo 8 caracteres
        if (!TextUtils.isEmpty(password) &&
                password.length() < 8) {

            etContrasenaUsuario.setError(
                    "Mínimo 8 caracteres"
            );
            etContrasenaUsuario.requestFocus();
            return;
        }

        // Crear objeto Usuario
        Usuario usuario = new Usuario(
                username,
                email,
                password,
                role
        );

        // Obtener servicio de API
        ApiService apiService =
                RetrofitClient.getApi(this);

        // Crear usuario en el backend
        apiService.crearUsuario(usuario)
                .enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(
                            Call<Usuario> call,
                            Response<Usuario> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    FormularioUsuarioActivity.this,
                                    "Usuario guardado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();
                        } else {

                        String detalle = "";

                        try {
                            if (response.errorBody() != null) {
                                detalle = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            detalle = e.getMessage();
                        }

                        Toast.makeText(
                                FormularioUsuarioActivity.this,
                                "Error " + response.code() + ": " + detalle,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    }

                    @Override
                    public void onFailure(
                            Call<Usuario> call,
                            Throwable t) {

                        Toast.makeText(
                                FormularioUsuarioActivity.this,
                                "Error de conexión: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}