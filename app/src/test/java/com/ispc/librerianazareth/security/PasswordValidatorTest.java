package com.ispc.librerianazareth.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests de la política de contraseña segura (AUT-SEC-PASS).
 *
 * Reglas cubiertas: longitud mínima, letra, dígito y carácter especial.
 */
public class PasswordValidatorTest {

    @Test
    public void passwordCompleta_esValida() {
        // Arrange
        String password = "Juan2026!";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertNull(error);
    }

    @Test
    public void passwordEnElLimite_esValida() {
        // Arrange (justo en el límite: 8 caracteres, letras + dígito + especial)
        String password = "Abc1@xyz";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertNull(error);
    }

    @Test
    public void passwordDebajoDelLimite_devuelveMensajeDeError() {
        // Arrange (una menos que el mínimo: 7 caracteres)
        String password = "Ab1@xy";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertNotNull(error);
        assertEquals("La contraseña debe tener al menos 8 caracteres", error);
    }

    @Test
    public void passwordSinDigito_devuelveMensajeDeError() {
        // Arrange
        String password = "Abcdefg!";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertEquals(
                "La contraseña debe combinar letras, números y al menos un carácter especial",
                error
        );
    }

    @Test
    public void passwordSinCaracterEspecial_devuelveMensajeDeError() {
        // Arrange
        String password = "Abcdefg1";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertEquals(
                "La contraseña debe combinar letras, números y al menos un carácter especial",
                error
        );
    }

    @Test
    public void passwordSoloNumeros_devuelveMensajeDeError() {
        // Arrange
        String password = "12345678";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertEquals(
                "La contraseña debe combinar letras, números y al menos un carácter especial",
                error
        );
    }

    @Test
    public void passwordVacia_devuelveMensajeDeError() {
        // Arrange
        String password = "";

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertEquals("Ingresá una contraseña", error);
    }

    @Test
    public void passwordNula_devuelveMensajeDeError() {
        // Arrange
        String password = null;

        // Act
        String error = PasswordValidator.validate(password);

        // Assert
        assertEquals("Ingresá una contraseña", error);
    }
}
