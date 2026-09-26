package com.example.librerianazareth;

import android.os.Bundle;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FormularioUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);
        Button btnCancelar = findViewById(R.id.btnCancelarUsuario);

        btnCancelar.setOnClickListener(v -> finish());
    }
}
