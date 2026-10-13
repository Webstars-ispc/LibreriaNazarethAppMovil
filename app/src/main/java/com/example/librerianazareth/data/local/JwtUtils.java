package com.example.librerianazareth.data.local;

import android.util.Base64;

import org.json.JSONObject;

/**
 * Utilidades para leer datos de un JWT sin verificar la firma.
 * Sirve para saber si el token de acceso ya expiró (claim "exp").
 */
public final class JwtUtils {

    private JwtUtils() {
    }

    /**
     * Devuelve la fecha de expiración (en milisegundos) del token.
     * Si el token es inválido o no tiene "exp", devuelve 0.
     */
    public static long getExpirationMillis(String token) {
        if (token == null || token.isEmpty()) {
            return 0L;
        }

        try {
            String[] partes = token.split("\\.");
            if (partes.length < 2) {
                return 0L;
            }

            byte[] payload = Base64.decode(
                    partes[1],
                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
            );

            JSONObject json = new JSONObject(new String(payload, "UTF-8"));
            if (!json.has("exp")) {
                return 0L;
            }

            return json.getLong("exp") * 1000L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Devuelve true si el token está expirado, es inválido o no tiene "exp".
     */
    public static boolean isExpired(String token) {
        long exp = getExpirationMillis(token);
        return exp == 0L || exp <= System.currentTimeMillis();
    }
}
