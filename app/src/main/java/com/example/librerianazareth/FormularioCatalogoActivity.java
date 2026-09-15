package com.example.librerianazareth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FormularioCatalogoActivity extends BaseActivity {

    private EditText etNombre, etDescripcion, etCodigoBarras, etPrecioCosto, etPrecioVenta, etStock;
    private Spinner spinnerRubro, spinnerMarca;
    private Button btnGuardar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_catalogo);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCodigoBarras = findViewById(R.id.etCodigoBarras);
        etPrecioCosto = findViewById(R.id.etPrecioCosto);
        etPrecioVenta = findViewById(R.id.etPrecioVenta);
        etStock = findViewById(R.id.etStock);
        spinnerRubro = findViewById(R.id.spinnerRubro);
        spinnerMarca = findViewById(R.id.spinnerMarca);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        String[] rubros = {"Seleccionar rubro", "Librería", "Escolar", "Oficina", "Arte"};
        String[] marcas = {"Seleccionar marca", "Bic", "Faber Castell", "Pelikan", "Sin marca"};

        ArrayAdapter<String> adapterRubro = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rubros);
        spinnerRubro.setAdapter(adapterRubro);

        ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas);
        spinnerMarca.setAdapter(adapterMarca);

        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(this, "Producto guardado (demo)", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancelar.setOnClickListener(v -> finish());
    }
}
