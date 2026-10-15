package com.ispc.librerianazareth.ui;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ispc.librerianazareth.RegistroActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistroAccessibilityTest {

    @Rule
    public ActivityScenarioRule<RegistroActivity> activityRule =
            new ActivityScenarioRule<>(RegistroActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void testRegistroActivityEsAccesible() {
        // Solo abre RegistroActivity para que el scanner la analice.
    }
}