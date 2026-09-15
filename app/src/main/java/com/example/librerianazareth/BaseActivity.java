package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected void setupBottomNavigation(int itemSeleccionado) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;

        // Marcar el ítem actual como seleccionado
        bottomNav.setSelectedItemId(itemSeleccionado);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                if (!(this instanceof MainActivity)) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                return true;
            } else if (id == R.id.nav_catalogo) {
                if (!(this instanceof CatalogoActivity)) {
                    startActivity(new Intent(this, CatalogoActivity.class));
                }
                return true;
            } else if (id == R.id.nav_configuracion) {
                if (!(this instanceof ConfiguracionActivity)) {
                    startActivity(new Intent(this, ConfiguracionActivity.class));
                }
                return true;
            } else if (id == R.id.nav_contacto) {
                if (!(this instanceof ContactoActivity)) {
                    startActivity(new Intent(this, ContactoActivity.class));
                }
                return true;
            }
            return false;
        });
    }
}