package com.example.librerianazareth.data.local;
import com.example.librerianazareth.data.NetworkConstants;
import android.content.Context;
import android.content.SharedPreferences;


//guarda y lee tokens JWT desde SharedPreferences
public class TokenManager {

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(NetworkConstants.PREFS_AUTH,Context.MODE_PRIVATE);
    }

    //Guardar tokens
    public void saveTokens(String access, String refresh) {
        prefs.edit()
                .putString(NetworkConstants.KEY_ACCESS,access)
                .putString(NetworkConstants.KEY_REFRESH,refresh)
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

    //Devulve true si hay access token guardado
    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }
}
