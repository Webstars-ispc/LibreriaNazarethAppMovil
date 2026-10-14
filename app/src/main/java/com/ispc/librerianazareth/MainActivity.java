package com.ispc.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ispc.librerianazareth.data.local.TokenManager;

public class MainActivity extends BaseActivity {

    private TextView tvBienvenida;
    private TextView btnCerrarSesionMain;
    private boolean esInvitado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnCerrarSesionMain = findViewById(R.id.btnCerrarSesionMain);

        // Modo invitado
        esInvitado = getIntent().getBooleanExtra("ES_INVITADO", false);

        // Token + datos del usuario
        TokenManager tokenManager = new TokenManager(this);
        String usuario = esInvitado ? "" : tokenManager.getUsername();
        boolean esAdmin = !esInvitado && tokenManager.isAdmin();
        android.util.Log.d(
                "ROL_APP",
                "Rol guardado: [" + tokenManager.getRole()
                        + "] | esAdmin: " + esAdmin
                        + " | esInvitado: " + esInvitado
        );

        if (usuario != null && !usuario.isEmpty()) {
            tvBienvenida.setText(getString(R.string.main_bienvenido_usuario, usuario));
        } else {
            tvBienvenida.setText(R.string.main_bienvenido);
        }

        // Referencias a los botones del menú
        LinearLayout btnCatalogo      = findViewById(R.id.btnCatalogo);
        LinearLayout btnAgregar       = findViewById(R.id.btnAgregar);
        LinearLayout btnConfiguracion = findViewById(R.id.btnConfiguracion);
        LinearLayout btnContacto      = findViewById(R.id.btnContacto);
        LinearLayout btnUsuarios      = findViewById(R.id.btnUsuarios);
        LinearLayout btnVenta         = findViewById(R.id.btnVenta);

        // Visibilidad según rol / invitado
        // - Invitados: no ven "Usuarios" ni "Agregar"
        // - Empleados: no ven "Usuarios"
        if (esInvitado) {
            btnUsuarios.setVisibility(View.GONE);
            btnAgregar.setVisibility(View.GONE);
        } else if (!esAdmin) {
            btnUsuarios.setVisibility(View.GONE);
        }

        // Botón "Cerrar sesión": oculto para invitados
        if (esInvitado) {
            btnCerrarSesionMain.setVisibility(View.GONE);
        } else {
            btnCerrarSesionMain.setVisibility(View.VISIBLE);
            btnCerrarSesionMain.setOnClickListener(v -> confirmarCerrarSesion());
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
            Intent intent;
            if (esAdmin) {
                intent = new Intent(MainActivity.this, VentasActivity.class);
            } else {
                intent = new Intent(MainActivity.this, VentaActivity.class);
            }
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

    // ---------------------------------------------------------------
    // CERRAR SESIÓN
    // ---------------------------------------------------------------
    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_cerrar_sesion)
                .setMessage(R.string.dialog_cerrar_sesion_msg)
                .setPositiveButton(R.string.dialog_cerrar_sesion, (dialog, which) -> {
                    new TokenManager(this).clear();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}