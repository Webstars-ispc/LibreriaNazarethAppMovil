package com.ispc.librerianazareth.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.ispc.librerianazareth.R;

/**
 * Screen Object para MainActivity.
 */
public class MainScreen {

    private final int welcomeText = R.id.tvBienvenida;

    public MainScreen verificarPantallaCargada() {
        onView(withId(welcomeText)).check(matches(isDisplayed()));
        return this;
    }
}