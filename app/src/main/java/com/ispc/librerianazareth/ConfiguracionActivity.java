package com.ispc.librerianazareth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.ispc.librerianazareth.data.local.TokenManager;

public class ConfiguracionActivity extends BaseActivity {

    private static final String PREFS_AJUSTES = "app_settings";
    private static final String KEY_NOTIFICACIONES = "notificaciones_activadas";
    private static final String KEY_MODO_OSCURO = "modo_oscuro_activado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());

        configurarSwitches();

        setupBottomNavigation(R.id.nav_configuracion);
    }

    private void configurarSwitches() {
        final SharedPreferences prefs = getSharedPreferences(PREFS_AJUSTES, MODE_PRIVATE);

        SwitchCompat switchNotificaciones = findViewById(R.id.switchNotificaciones);
        SwitchCompat switchModoOscuro = findViewById(R.id.switchModoOscuro);

        // Cargar estado guardado inicial
        switchNotificaciones.setChecked(prefs.getBoolean(KEY_NOTIFICACIONES, true));
        switchModoOscuro.setChecked(prefs.getBoolean(KEY_MODO_OSCURO, true));

        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIFICACIONES, isChecked).apply();
            Toast.makeText(this,
                    isChecked ? getString(R.string.msj_notif_activadas) : getString(R.string.msj_notif_desactivadas),
                    Toast.LENGTH_SHORT).show();
        });

        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 1. Guardamos la preferencia usando apply()
            prefs.edit().putBoolean(KEY_MODO_OSCURO, isChecked).apply();

            // 2. Le decimos a AppCompat que cambie el modo
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // 3. Usamos un Handler para esperar 100ms y luego recrear la pantalla.
            // Esto evita el parpadeo y asegura que el nuevo tema se aplique bien.
            new Handler(Looper.getMainLooper()).postDelayed(this::recreate, 100);
        });
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_cerrar_sesion)
                .setMessage(R.string.dialog_cerrar_sesion_msg)
                .setPositiveButton(R.string.dialog_cerrar_sesion, (dialog, which) -> {
                    // Se limpian los tokens y los ajustes locales
                    new TokenManager(this).clear();
                    getSharedPreferences(PREFS_AJUSTES, MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(ConfiguracionActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}