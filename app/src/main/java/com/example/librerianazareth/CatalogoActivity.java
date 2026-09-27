package com.example.librerianazareth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class CatalogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        setupBottomNavigation(R.id.nav_catalogo);

        // Botón agregar producto
        findViewById(R.id.fabAgregarProducto).setOnClickListener(v -> {
            Intent intent = new Intent(
                    CatalogoActivity.this,
                    FormularioCatalogoActivity.class
            );
            startActivity(intent);
        });
    }
}