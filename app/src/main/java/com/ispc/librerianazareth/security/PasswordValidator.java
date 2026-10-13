package com.ispc.librerianazareth.security;

/**
 * Validador de la política de contraseña segura del proyecto.
 *
 * Reglas:
 *   - mínimo {@value #MIN_PASSWORD_LENGTH} caracteres
 *   - al menos una letra
 *   - al menos un dígito
 *   - al menos un carácter especial (no alfanumérico)
 *
 * Es una clase de lógica pura (no depende del framework Android) para poder
 * testearla con JUnit en la máquina local, igual que LoginValidator.
 *
 * El backend aplica las MISMAS reglas con RobustPasswordValidator, así que la
 * app solo avisa antes de enviar para no hacer el usuario esperar la respuesta.
 */
public final class PasswordValidator {

    /** Longitud mínima aceptada para la contraseña. */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /** Caracteres aceptados como "especiales". */
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?\"'\\~`";

    private PasswordValidator() {
        // Clase utilitaria: no se instancia
    }

    /**
     * Valida la contraseña contra la política de seguridad.
     *
     * @param password la contraseña ingresada por el usuario.
     * @return null si es válida; si no, el mensaje de error a mostrar.
     */
    public static String validate(String password) {
        if (password == null || password.isEmpty()) {
            return "Ingresá una contraseña";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres";
        }

        boolean tieneLetra = false;
        boolean tieneDigito = false;
        boolean tieneEspecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) {
                tieneLetra = true;
            } else if (Character.isDigit(c)) {
                tieneDigito = true;
            }
            if (SPECIAL_CHARS.indexOf(c) >= 0) {
                tieneEspecial = true;
            }
        }

        if (!tieneLetra || !tieneDigito || !tieneEspecial) {
            return "La contraseña debe combinar letras, números y al menos un carácter especial";
        }

        return null;
    }
}
