package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class FormularioCatalogoActivity extends BaseActivity {

    private EditText etNombre, etDescripcion, etCodigoBarras, etPrecioCosto, etPrecioVenta, etStock;
    private Spinner spinnerRubro, spinnerMarca;
    private Button btnGuardar, btnCancelar;
    private ImageView ivEscanearCodigoProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_catalogo);

        setupBottomNavigation(0);

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
        ivEscanearCodigoProducto = findViewById(R.id.ivEscanearCodigoProducto);

        String[] rubros = {"Seleccionar rubro", "Librería", "Escolar", "Oficina", "Arte"};
        String[] marcas = {"Seleccionar marca", "Bic", "Faber Castell", "Pelikan", "Sin marca"};

        ArrayAdapter<String> adapterRubro = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rubros);
        spinnerRubro.setAdapter(adapterRubro);

        ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas);
        spinnerMarca.setAdapter(adapterMarca);

        // Escáner de código de barras
        ivEscanearCodigoProducto.setOnClickListener(v -> iniciarEscaner());

        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(this, "Producto guardado (demo)", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancelar.setOnClickListener(v -> finish());
    }

    private void iniciarEscaner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Apuntá al código de barras del producto");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            } else {
                String codigo = result.getContents();
                etCodigoBarras.setText(codigo);
                etCodigoBarras.setSelection(codigo.length());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}