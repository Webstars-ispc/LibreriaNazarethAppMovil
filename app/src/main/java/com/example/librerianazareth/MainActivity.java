package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {

    private TextView tvBienvenida;
    private boolean esInvitado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        String usuario = getIntent().getStringExtra("EXTRA_USUARIO");
        esInvitado = getIntent().getBooleanExtra("ES_INVITADO", false);

        if (usuario != null && !usuario.isEmpty()) {
            tvBienvenida.setText("Bienvenido/a, " + usuario);
        } else {
            tvBienvenida.setText("Bienvenido/a");
        }

        LinearLayout btnCatalogo = findViewById(R.id.btnCatalogo);
        LinearLayout btnAgregar = findViewById(R.id.btnAgregar);
        LinearLayout btnConfiguracion = findViewById(R.id.btnConfiguracion);
        LinearLayout btnContacto = findViewById(R.id.btnContacto);
        LinearLayout btnUsuarios = findViewById(R.id.btnUsuarios);

        // Ocultar "Usuarios" y "Agregar" para invitados
        if (esInvitado) {
            btnUsuarios.setVisibility(LinearLayout.GONE);
            btnAgregar.setVisibility(LinearLayout.GONE);
        }

        btnCatalogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CatalogoActivity.class);
            startActivity(intent);
        });

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioCatalogoActivity.class);
            startActivity(intent);
        });

        btnConfiguracion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfiguracionActivity.class);
            intent.putExtra("ES_INVITADO", esInvitado);
            startActivity(intent);
        });

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
            startActivity(intent);
        });

        btnUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UsuariosActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation(R.id.nav_inicio);
    }
}
