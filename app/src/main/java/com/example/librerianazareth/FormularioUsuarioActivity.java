package com.example.librerianazareth;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        // Campos del formulario
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario = findViewById(R.id.etCorreoUsuario);
        etContrasenaUsuario = findViewById(R.id.etContrasenaUsuario);

        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        btnCancelarUsuario = findViewById(R.id.btnCancelarUsuario);

        // Comprobar si estamos editando un usuario
        boolean modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);

        if (modoEdicion) {

            String nombre = getIntent().getStringExtra("nombre_usuario");
            String correo = getIntent().getStringExtra("correo_usuario");

            if (nombre != null) {
                etNombreUsuario.setText(nombre);
            }

            if (correo != null) {
                etCorreoUsuario.setText(correo);
            }
        }

        // Botón guardar
        btnGuardarUsuario.setOnClickListener(v -> {

            String nombre = etNombreUsuario.getText().toString().trim();
            String correo = etCorreoUsuario.getText().toString().trim();
            String contrasena = etContrasenaUsuario.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {

                Toast.makeText(
                        this,
                        "Completá todos los campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Toast.makeText(
                    this,
                    modoEdicion
                            ? "Usuario actualizado correctamente"
                            : "Usuario guardado correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });

        // Botón cancelar
        btnCancelarUsuario.setOnClickListener(v -> finish());
    }
}