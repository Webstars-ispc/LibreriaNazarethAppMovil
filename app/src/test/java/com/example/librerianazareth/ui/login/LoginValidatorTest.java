package com.example.librerianazareth.ui.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests de clases de equivalencia y valores límite del LoginValidator.
 *
 * AUT-UNIT-03
 */
public class LoginValidatorTest {

    @Test
    public void emailConFormatoValido_esValido() {
        // Arrange
        String email = "juanperez@gmail.com";

        // Act
        boolean resultado = LoginValidator.isValidEmail(email);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void emailVacio_esInvalido() {
        // Arrange
        String email = "";

        // Act
        boolean resultado = LoginValidator.isValidEmail(email);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void emailFueraDeFormato_esInvalido() {
        // Arrange
        String email = "juanperez-gmail";

        // Act
        boolean resultado = LoginValidator.isValidEmail(email);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void passwordEnElLimite_esValida() {
        // Arrange (justo en el límite: 6 caracteres)
        String password = "123456";

        // Act
        boolean resultado = LoginValidator.isPasswordLongEnough(password);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void passwordDebajoDelLimite_esInvalida() {
        // Arrange (una menos que el mínimo: 5 caracteres)
        String password = "12345";

        // Act
        boolean resultado = LoginValidator.isPasswordLongEnough(password);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void datosValidos_validateDevuelveNull() {
        // Arrange
        String email = "juan@librerianazareth.com";
        String password = "Juan2026!";

        // Act
        String error = LoginValidator.validate(email, password);

        // Assert
        assertNull(error);
    }

    @Test
    public void emailVacio_validateDevuelveMensajeDeError() {
        // Arrange
        String email = "";
        String password = "Juan2026!";

        // Act
        String error = LoginValidator.validate(email, password);

        // Assert
        assertEquals("Ingresá tu correo", error);
    }
}
