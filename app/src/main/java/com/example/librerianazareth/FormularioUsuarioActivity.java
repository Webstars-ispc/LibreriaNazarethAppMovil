package com.example.librerianazareth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioUsuarioActivity extends AppCompatActivity {

    private EditText etNombreUsuario;
    private EditText etCorreoUsuario;
    private EditText etContrasenaUsuario;
    private Button btnGuardarUsuario;
    private Button btnCancelarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario = findViewById(R.id.etContrasenaUsuario);

        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario = findViewById(R.id.btnCancelarUsuario);

        boolean modoEdicion =
                getIntent().getBooleanExtra("modo_edicion", false);

        if (modoEdicion) {

            String nombre =
                    getIntent().getStringExtra("nombre_usuario");

            String correo =
                    getIntent().getStringExtra("correo_usuario");

            if (nombre != null) {
                etNombreUsuario.setText(nombre);
            }

            if (correo != null) {
                etCorreoUsuario.setText(correo);
            }
        }

        btnGuardarUsuario.setOnClickListener(v -> {

            String nombre =
                    etNombreUsuario.getText().toString().trim();

            String correo =
                    etCorreoUsuario.getText().toString().trim();

            String contrasena =
                    etContrasenaUsuario.getText().toString().trim();

            if (nombre.isEmpty()
                    || correo.isEmpty()
                    || contrasena.isEmpty()) {

                Toast.makeText(
                        this,
                        "Completá todos los campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Crear usuario para enviar al backend
            Usuario usuario = new Usuario(
                    nombre,
                    correo,
                    contrasena,
                    "Cliente"
            );

            ApiService apiService =
                    RetrofitClient.getApi(this);

            Call<Usuario> llamada =
                    apiService.crearUsuario(usuario);

            llamada.enqueue(new Callback<Usuario>() {

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

                        Toast.makeText(
                                FormularioUsuarioActivity.this,
                                "Error al guardar usuario: "
                                        + response.code(),
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
        });

        btnCancelarUsuario.setOnClickListener(v -> finish());
    }
}