package com.example.librerianazareth;

import android.os.Bundle;
import android.widget.Button;

public class VentaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        setupBottomNavigation(0);

        Button btnCancelarVenta = findViewById(R.id.btnCancelarVenta);
        btnCancelarVenta.setOnClickListener(v -> finish());
    }
}