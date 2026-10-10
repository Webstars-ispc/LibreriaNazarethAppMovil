package com.example.librerianazareth;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class LibreriaApplication extends Application {

    private static final String PREFS_AJUSTES = "app_settings";
    private static final String KEY_MODO_OSCURO = "modo_oscuro_activado";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(PREFS_AJUSTES, MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean(KEY_MODO_OSCURO, true);
        AppCompatDelegate.setDefaultNightMode(
                modoOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}