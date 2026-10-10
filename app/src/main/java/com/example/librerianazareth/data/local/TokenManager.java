package com.example.librerianazareth.data.local;

import android.content.Context;

import com.example.librerianazareth.data.NetworkConstants;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

//guarda y lee tokens JWT desde SharedPreferences CIFRADAS
public class TokenManager {

    private final android.content.SharedPreferences prefs;

    public TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            this.prefs = EncryptedSharedPreferences.create(
                    context,
                    NetworkConstants.PREFS_AUTH,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("No se pudieron inicializar las preferencias cifradas", e);
        }
    }

    //Guardar tokens
    public void saveTokens(String access, String refresh) {
        prefs.edit()
                .putString(NetworkConstants.KEY_ACCESS, access)
                .putString(NetworkConstants.KEY_REFRESH, refresh)
                .apply();
    }

    //Devuelve token access
    public String getAccessToken() {
        return prefs.getString(NetworkConstants.KEY_ACCESS, null);
    }

    //Devuelve token refresh
    public String getRefreshToken() {
        return prefs.getString(NetworkConstants.KEY_REFRESH, null);
    }

    //Logout
    public void clear() {
        prefs.edit().clear().apply();
    }

    //Devuelve true si hay access token guardado
    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    // --- Username del usuario logueado ---
    public void saveUsername(String username) {
        prefs.edit().putString("username", username).apply();
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    // --- Rol del usuario ---

    public void saveRole(String role) {
        prefs.edit().putString("role", role).apply();
    }

    public String getRole() {
        return prefs.getString("role", "");
    }

    // --- Helpers de rol ---

    public boolean isAdmin() {
        return "Administrador".equalsIgnoreCase(getRole());
    }

    public boolean isEmpleado() {
        return "Empleado".equalsIgnoreCase(getRole());
    }
}