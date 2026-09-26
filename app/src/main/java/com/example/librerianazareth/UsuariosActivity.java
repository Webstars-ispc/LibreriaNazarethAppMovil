package com.example.librerianazareth;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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
        android.widget.EditText buscador = findViewById(R.id.etBuscarUsuario);

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
    }
}