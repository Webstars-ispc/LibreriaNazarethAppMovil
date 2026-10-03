package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.librerianazareth.data.local.TokenManager;

public class MainActivity extends BaseActivity {

    private TextView tvBienvenida;
    private boolean esInvitado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        // Modo invitado
        esInvitado = getIntent().getBooleanExtra("ES_INVITADO", false);

        // Token + datos del usuario
        TokenManager tokenManager = new TokenManager(this);
        String usuario = esInvitado ? "" : tokenManager.getUsername();
        boolean esAdmin = !esInvitado && tokenManager.isAdmin();

        if (usuario != null && !usuario.isEmpty()) {
            tvBienvenida.setText("Bienvenido/a, " + usuario);
        } else {
            tvBienvenida.setText("Bienvenido/a");
        }

        // Referencias a los botones del menú
        LinearLayout btnCatalogo      = findViewById(R.id.btnCatalogo);
        LinearLayout btnAgregar       = findViewById(R.id.btnAgregar);
        LinearLayout btnConfiguracion = findViewById(R.id.btnConfiguracion);
        LinearLayout btnContacto      = findViewById(R.id.btnContacto);
        LinearLayout btnUsuarios      = findViewById(R.id.btnUsuarios);
        LinearLayout btnVenta         = findViewById(R.id.btnVenta);
        TextView     btnCerrarSesion  = findViewById(R.id.btnCerrarSesion);

        // Visibilidad según rol / invitado
        // - Invitados: no ven "Usuarios" ni "Agregar"
        // - Empleados: no ven "Usuarios"
        if (esInvitado) {
            btnUsuarios.setVisibility(LinearLayout.GONE);
            btnAgregar.setVisibility(LinearLayout.GONE);
        } else if (!esAdmin) {
            btnUsuarios.setVisibility(LinearLayout.GONE);
        }

        btnCatalogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CatalogoActivity.class);
            startActivity(intent);
        });

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioCatalogoActivity.class);
            startActivity(intent);
        });

        btnVenta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VentasActivity.class);
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

        btnCerrarSesion.setOnClickListener(v -> {
            new TokenManager(MainActivity.this).clear();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation(R.id.nav_inicio);
    }
}