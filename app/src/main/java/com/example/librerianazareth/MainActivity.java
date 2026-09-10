package com.example.librerianazareth;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);

        String usuario = getIntent().getStringExtra("EXTRA_USUARIO");
        if (usuario != null) {
            tvBienvenida.setText("¡Bienvenido/a, " + usuario + "!");
        } else {
            tvBienvenida.setText("¡Bienvenido!");
        }
    }
}
