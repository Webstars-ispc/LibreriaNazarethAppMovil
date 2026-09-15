package com.example.librerianazareth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CatalogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        setupBottomNavigation(R.id.nav_catalogo);
    }
}