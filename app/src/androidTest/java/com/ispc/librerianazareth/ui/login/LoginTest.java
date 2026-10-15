package com.ispc.librerianazareth.ui.login;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.librerianazareth.LoginActivity;
import com.ispc.librerianazareth.screens.LoginScreen;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    // AUT-UI-01: flujo aislado con ActivityScenarioRule
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    // ACC-01: AccessibilityChecks activo para toda la clase
    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void testFlujoCriticoLoginExitoso() {
        // AUT-UI-02: el test NO contiene onView ni withId
        LoginScreen loginScreen = new LoginScreen();

        loginScreen
                .escribirEmail("qa.empleado@librerianazareth.com")
                .escribirPassword("QaEmpleado2026!")
                .clickLogin()
                .verificarPantallaCargada();

        // AUT-UI-03: sin Thread.sleep()
    }
}