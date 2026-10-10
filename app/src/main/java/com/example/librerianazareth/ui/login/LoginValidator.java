package com.example.librerianazareth.ui.login;

import java.util.regex.Pattern;

/**
 * Validador de los campos del login.
 *
 * Es una clase de lógica pura (no depende del framework Android) para poder
 * testearla con JUnit en la máquina local (capa de tests unitarios).
 */
public final class LoginValidator {

    /** Longitud mínima aceptada para la contraseña. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** Formato de email aceptado: algo@algo.dominio (dominio de 2+ letras). */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private LoginValidator() {
        // Clase utilitaria: no se instancia
    }

    /** @return true si el email tiene un formato válido. */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /** @return true si la contraseña alcanza la longitud mínima. */
    public static boolean isPasswordLongEnough(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Valida email y contraseña.
     *
     * @return null si todo es válido; si no, el mensaje de error a mostrar.
     */
    public static String validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Ingresá tu correo";
        }
        if (!isValidEmail(email)) {
            return "Ingresá un correo válido";
        }
        if (password == null || password.isEmpty()) {
            return "Ingresá tu contraseña";
        }
        if (!isPasswordLongEnough(password)) {
            return "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres";
        }
        return null;
    }
}
