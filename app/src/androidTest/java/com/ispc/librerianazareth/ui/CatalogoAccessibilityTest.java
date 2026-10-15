package com.ispc.librerianazareth.ui;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.librerianazareth.CatalogoActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CatalogoAccessibilityTest {

    @Rule
    public ActivityScenarioRule<CatalogoActivity> activityRule =
            new ActivityScenarioRule<>(CatalogoActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void testCatalogoActivityEsAccesible() {
        // Solo abre CatalogoActivity para que el scanner la analice.
        // OJO: si requiere login previo, puede redirigir o crashear.
    }
}