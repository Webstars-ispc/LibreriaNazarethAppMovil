package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        String usuario = getIntent().getStringExtra("EXTRA_USUARIO");
        if (usuario != null && !usuario.isEmpty()) {
            tvBienvenida.setText("Bienvenido/a, " + usuario);
        } else {
            tvBienvenida.setText("Bienvenido/a");
        }

        LinearLayout btnCatalogo = findViewById(R.id.btnCatalogo);
        LinearLayout btnProductos = findViewById(R.id.btnProductos);
        LinearLayout btnConfiguracion = findViewById(R.id.btnConfiguracion);
        LinearLayout btnContacto = findViewById(R.id.btnContacto);
        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCatalogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioCatalogoActivity.class);
            startActivity(intent);
        });

        btnProductos.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show();
        });

        btnConfiguracion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfiguracionActivity.class);
            startActivity(intent);
        });

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
