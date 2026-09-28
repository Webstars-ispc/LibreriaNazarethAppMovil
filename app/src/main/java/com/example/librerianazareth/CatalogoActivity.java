package com.example.librerianazareth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CatalogoActivity extends BaseActivity {

    private EditText etBuscarProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        setupBottomNavigation(R.id.nav_catalogo);

        etBuscarProducto = findViewById(R.id.etBuscarProducto);

        // Botón agregar producto
        findViewById(R.id.fabAgregarProducto).setOnClickListener(v -> {
            Intent intent = new Intent(
                    CatalogoActivity.this,
                    FormularioCatalogoActivity.class
            );
            startActivity(intent);
        });

        // Ícono del escáner de código de barras
        ImageView ivEscanearCodigo = findViewById(R.id.ivEscanearCodigo);
        ivEscanearCodigo.setOnClickListener(v -> iniciarEscaner());
    }

    private void iniciarEscaner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        // Solo códigos de barras 1D (EAN, UPC, Code128, etc.)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Apuntá al código de barras");
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
                etBuscarProducto.setText(codigo);
                etBuscarProducto.setSelection(codigo.length());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}