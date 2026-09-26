package com.example.librerianazareth.data;
import com.example.librerianazareth.BuildConfig;

/**
 * Constantes compartidas de la capa de red.
 * NO modificar sin avisar al equipo.
 */
public final class NetworkConstants {

    private NetworkConstants() {
        // Evita instanciación
    }

    /** URL base de la API (se resuelve desde local.properties). */
    public static final String BASE_URL = BuildConfig.API_BASE_URL;

    /** Cabecera HTTP para el token JWT. */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /** Prefijo del token JWT. */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** Preferencias compartidas donde se guardan los tokens. */
    public static final String PREFS_AUTH = "auth_prefs";

    /** Clave del token de acceso. */
    public static final String KEY_ACCESS = "access_token";

    /** Clave del token de refresco. */
    public static final String KEY_REFRESH = "refresh_token";
}