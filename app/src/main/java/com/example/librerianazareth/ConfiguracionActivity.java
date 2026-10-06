package com.example.librerianazareth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

public class ConfiguracionActivity extends BaseActivity {

    private static final String PREFS_AJUSTES = "app_settings";
    private static final String KEY_NOTIFICACIONES = "notificaciones_activadas";
    private static final String KEY_MODO_OSCURO = "modo_oscuro_activado";

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

        configurarSwitches();

        setupBottomNavigation(R.id.nav_configuracion);
    }

    private void configurarSwitches() {
        final SharedPreferences prefs = getSharedPreferences(PREFS_AJUSTES, MODE_PRIVATE);

        Switch switchNotificaciones = findViewById(R.id.switchNotificaciones);
        Switch switchModoOscuro = findViewById(R.id.switchModoOscuro);

        // Cargar estado guardado
        switchNotificaciones.setChecked(prefs.getBoolean(KEY_NOTIFICACIONES, true));
        switchModoOscuro.setChecked(prefs.getBoolean(KEY_MODO_OSCURO, true));

        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIFICACIONES, isChecked).apply();
            Toast.makeText(this,
                    isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas",
                    Toast.LENGTH_SHORT).show();
        });

        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_MODO_OSCURO, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(esInvitado ? "Salir del modo invitado" : "Cerrar sesión")
                .setMessage(esInvitado
                        ? "¿Querés salir del modo invitado?"
                        : "¿Estás seguro de que querés cerrar sesión?")
                .setPositiveButton(esInvitado ? "Salir" : "Cerrar sesión", (dialog, which) -> {
                    // Se limpian los tokens y los ajustes locales
                    new com.example.librerianazareth.data.local.TokenManager(this).clear();
                    getSharedPreferences(PREFS_AJUSTES, MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(ConfiguracionActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
