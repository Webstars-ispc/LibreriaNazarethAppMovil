package com.example.librerianazareth;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UsuariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        // Botón agregar usuario
        findViewById(R.id.fabAgregarUsuario).setOnClickListener(v -> {
            Intent intent = new Intent(
                    UsuariosActivity.this,
                    FormularioUsuarioActivity.class
            );
            startActivity(intent);
        });

        // Buscador de usuarios
        EditText buscador = findViewById(R.id.etBuscarUsuario);

        View usuario1 = findViewById(R.id.cardUsuario1);
        View usuario2 = findViewById(R.id.cardUsuario2);

        buscador.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                String texto = s.toString().toLowerCase().trim();

                boolean mostrarBaltazar =
                        "Baltazar Ledesma".toLowerCase().contains(texto);

                boolean mostrarSalvador =
                        "Salvador Ledesma".toLowerCase().contains(texto);

                usuario1.setVisibility(
                        mostrarBaltazar ? View.VISIBLE : View.GONE
                );

                usuario2.setVisibility(
                        mostrarSalvador ? View.VISIBLE : View.GONE
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Botón EDITAR de Baltazar
        Button btnEditarUsuario1 = findViewById(R.id.btnEditarUsuario1);

        btnEditarUsuario1.setOnClickListener(v -> {

            Intent intent = new Intent(
                    UsuariosActivity.this,
                    FormularioUsuarioActivity.class
            );

            intent.putExtra("modo_edicion", true);
            intent.putExtra("nombre_usuario", "Baltazar Ledesma");
            intent.putExtra("correo_usuario", "baltazar@email.com");

            startActivity(intent);
        });

        // Botón EDITAR de Salvador
        Button btnEditarUsuario2 = findViewById(R.id.btnEditarUsuario2);

        btnEditarUsuario2.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "EDITAR SALVADOR FUNCIONA", android.widget.Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(
                    UsuariosActivity.this,
                    FormularioUsuarioActivity.class
            );

            intent.putExtra("modo_edicion", true);
            intent.putExtra("nombre_usuario", "Salvador Ledesma");
            intent.putExtra("correo_usuario", "salvador@email.com");

            startActivity(intent);

        });
        // Botón ELIMINAR de Salvador
        Button btnEliminarUsuario2 = findViewById(R.id.btnEliminarUsuario2);

        btnEliminarUsuario2.setOnClickListener(v -> {
            usuario2.setVisibility(View.GONE);
        });
    }
}
