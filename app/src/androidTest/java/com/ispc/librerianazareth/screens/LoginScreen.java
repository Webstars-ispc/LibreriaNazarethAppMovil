package com.ispc.librerianazareth.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.ispc.librerianazareth.R;

/**
 * Screen Object para LoginActivity (POM - Page Object Model).
 * AUT-UI-02: R.id privados, métodos de negocio, navegación devuelve pantalla destino.
 */
public class LoginScreen {

    // R.id privados y centralizados
    private final int emailField    = R.id.etUsuario;
    private final int passwordField = R.id.etPassword;
    private final int loginButton   = R.id.btnLogin;

    // Acciones de negocio
    public LoginScreen escribirEmail(String email) {
        onView(withId(emailField)).perform(typeText(email), closeSoftKeyboard());
        return this;
    }

    public LoginScreen escribirPassword(String password) {
        onView(withId(passwordField)).perform(typeText(password), closeSoftKeyboard());
        return this;
    }

    // La navegación devuelve la pantalla destino
    public MainScreen clickLogin() {
        onView(withId(loginButton)).perform(click());
        return new MainScreen();
    }
}