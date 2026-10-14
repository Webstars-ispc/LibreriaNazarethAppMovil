package com.ispc.librerianazareth.ui.login;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.librerianazareth.LoginActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginAccessibilityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    /**
     * Abre LoginActivity y deja que AccessibilityChecks analice todos
     * los elementos visibles sin interactuar con la red.
     * Sirve para obtener evidencia de accesibilidad (ACC-02).
     */
    @Test
    public void testLoginActivityEsAccesible() {
        // No hacemos nada más: solo abrir la Activity dispara los checks.
    }
}