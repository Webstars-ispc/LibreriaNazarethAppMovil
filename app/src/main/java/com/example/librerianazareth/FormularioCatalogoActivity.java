package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Marca;
import com.example.librerianazareth.data.model.Producto;
import com.example.librerianazareth.data.model.Rubro;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioCatalogoActivity extends BaseActivity {

    private EditText etNombre, etDescripcion, etCodigoBarras, etPrecioCosto, etPrecioVenta, etStock;
    private Spinner spinnerRubro, spinnerMarca;
    private Button btnGuardar, btnCancelar;
    private ImageView ivEscanearCodigoProducto;

    private boolean modoEdicion = false;
    private int productoId = -1;

    private List<Rubro> rubrosList = new ArrayList<>();
    private List<Marca> marcasList = new ArrayList<>();

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

        ivEscanearCodigoProducto.setOnClickListener(v -> iniciarEscaner());

        btnCancelar.setOnClickListener(v -> finish());

        // Cargar rubros y marcas desde el backend
        cargarRubrosYMarcas();

        // Verificar modo edición
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        if (modoEdicion) {
            productoId = getIntent().getIntExtra("producto_id", -1);
            etNombre.setText(getIntent().getStringExtra("nombre"));
            etDescripcion.setText(getIntent().getStringExtra("descripcion"));
            etCodigoBarras.setText(getIntent().getStringExtra("codigo_barras"));
            etPrecioCosto.setText(String.valueOf(getIntent().getDoubleExtra("precio_costo", 0)));
            etPrecioVenta.setText(String.valueOf(getIntent().getDoubleExtra("precio_venta", 0)));
            etStock.setText(String.valueOf(getIntent().getIntExtra("stock", 0)));
        }

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void cargarRubrosYMarcas() {
        // Cargar rubros
        RetrofitClient.getApi(this).getRubros().enqueue(new Callback<List<Rubro>>() {
            @Override
            public void onResponse(Call<List<Rubro>> call, Response<List<Rubro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rubrosList = response.body();
                    List<String> rubroNombres = new ArrayList<>();
                    rubroNombres.add("Seleccionar rubro");
                    for (Rubro r : rubrosList) {
                        rubroNombres.add(r.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            FormularioCatalogoActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            rubroNombres
                    );
                    spinnerRubro.setAdapter(adapter);

                    // Si es edición, seleccionar el rubro correcto
                    if (modoEdicion) {
                        int rubroId = getIntent().getIntExtra("rubro", -1);
                        for (int i = 0; i < rubrosList.size(); i++) {
                            if (rubrosList.get(i).getId() == rubroId) {
                                spinnerRubro.setSelection(i + 1); // +1 por "Seleccionar rubro"
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Rubro>> call, Throwable t) {
                // Fallback a opciones hardcoded si falla
                String[] rubros = {"Seleccionar rubro", "Librería", "Escolar", "Oficina", "Arte"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FormularioCatalogoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        rubros
                );
                spinnerRubro.setAdapter(adapter);
            }
        });

        // Cargar marcas
        RetrofitClient.getApi(this).getMarcas().enqueue(new Callback<List<Marca>>() {
            @Override
            public void onResponse(Call<List<Marca>> call, Response<List<Marca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    marcasList = response.body();
                    List<String> marcaNombres = new ArrayList<>();
                    marcaNombres.add("Seleccionar marca");
                    for (Marca m : marcasList) {
                        marcaNombres.add(m.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            FormularioCatalogoActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            marcaNombres
                    );
                    spinnerMarca.setAdapter(adapter);

                    // Si es edición, seleccionar la marca correcta
                    if (modoEdicion) {
                        int marcaId = getIntent().getIntExtra("marca", -1);
                        for (int i = 0; i < marcasList.size(); i++) {
                            if (marcasList.get(i).getId() == marcaId) {
                                spinnerMarca.setSelection(i + 1); // +1 por "Seleccionar marca"
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Marca>> call, Throwable t) {
                // Fallback a opciones hardcoded si falla
                String[] marcas = {"Seleccionar marca", "Bic", "Faber Castell", "Pelikan", "Sin marca"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FormularioCatalogoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        marcas
                );
                spinnerMarca.setAdapter(adapter);
            }
        });
    }

    private void guardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String codigoBarras = etCodigoBarras.getText().toString().trim();
        String precioCostoStr = etPrecioCosto.getText().toString().trim();
        String precioVentaStr = etPrecioVenta.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresá el nombre del producto");
            etNombre.requestFocus();
            return;
        }

        if (spinnerRubro.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccioná un rubro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(precioCostoStr)) {
            etPrecioCosto.setError("Ingresá el precio de costo");
            etPrecioCosto.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(precioVentaStr)) {
            etPrecioVenta.setError("Ingresá el precio de venta");
            etPrecioVenta.requestFocus();
            return;
        }

        double precioCosto = Double.parseDouble(precioCostoStr);
        double precioVenta = Double.parseDouble(precioVentaStr);

        if (precioVenta <= 0) {
            etPrecioVenta.setError("El precio de venta debe ser mayor a 0");
            etPrecioVenta.requestFocus();
            return;
        }

        if (precioVenta < precioCosto) {
            etPrecioVenta.setError("El precio de venta no puede ser menor al de costo");
            etPrecioVenta.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stockStr)) {
            etStock.setError("Ingresá el stock");
            etStock.requestFocus();
            return;
        }

        int stock = Integer.parseInt(stockStr);

        if (stock < 0) {
            etStock.setError("El stock no puede ser negativo");
            etStock.requestFocus();
            return;
        }

        // Obtener IDs de rubro y marca
        int rubroId = -1;
        int marcaId = -1;

        if (spinnerRubro.getSelectedItemPosition() > 0 && spinnerRubro.getSelectedItemPosition() <= rubrosList.size()) {
            rubroId = rubrosList.get(spinnerRubro.getSelectedItemPosition() - 1).getId();
        }

        if (spinnerMarca.getSelectedItemPosition() > 0 && spinnerMarca.getSelectedItemPosition() <= marcasList.size()) {
            marcaId = marcasList.get(spinnerMarca.getSelectedItemPosition() - 1).getId();
        }

        Producto producto = new Producto(nombre, descripcion, codigoBarras, rubroId, marcaId, precioCosto, precioVenta, stock);

        btnGuardar.setEnabled(false);

        if (modoEdicion && productoId != -1) {
            RetrofitClient.getApi(this).actualizarProducto(productoId, producto)
                    .enqueue(new Callback<Producto>() {
                        @Override
                        public void onResponse(Call<Producto> call, Response<Producto> response) {
                            btnGuardar.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioCatalogoActivity.this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioCatalogoActivity.this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Producto> call, Throwable t) {
                            btnGuardar.setEnabled(true);
                            Toast.makeText(FormularioCatalogoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            RetrofitClient.getApi(this).crearProducto(producto)
                    .enqueue(new Callback<Producto>() {
                        @Override
                        public void onResponse(Call<Producto> call, Response<Producto> response) {
                            btnGuardar.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioCatalogoActivity.this, "Producto creado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioCatalogoActivity.this, "Error al crear producto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Producto> call, Throwable t) {
                            btnGuardar.setEnabled(true);
                            Toast.makeText(FormularioCatalogoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
