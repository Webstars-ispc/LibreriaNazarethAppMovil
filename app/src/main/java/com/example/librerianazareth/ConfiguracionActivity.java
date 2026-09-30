package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionActivity extends BaseActivity {

    private boolean esInvitado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        esInvitado = getIntent().getBooleanExtra("ES_INVITADO", false);

        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        if (esInvitado) {
            btnCerrarSesion.setText("Salir del modo invitado");
        }
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());

        setupBottomNavigation(R.id.nav_configuracion);
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(esInvitado ? "Salir del modo invitado" : "Cerrar sesión")
                .setMessage(esInvitado
                        ? "¿Querés salir del modo invitado?"
                        : "¿Estás seguro de que querés cerrar sesión?")
                .setPositiveButton(esInvitado ? "Salir" : "Cerrar sesión", (dialog, which) -> {
                    Intent intent = new Intent(ConfiguracionActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
