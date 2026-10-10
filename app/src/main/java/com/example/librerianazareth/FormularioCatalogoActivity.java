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
import com.example.librerianazareth.data.model.Pagina;
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
    private String errorRubros = null;

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
        RetrofitClient.getApi(this).getRubros().enqueue(new Callback<Pagina<Rubro>>() {
            @Override
            public void onResponse(Call<Pagina<Rubro>> call, Response<Pagina<Rubro>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getResults() != null) {
                    rubrosList = response.body().getResults();
                    errorRubros = null;
                    List<String> rubroNombres = new ArrayList<>();
                    rubroNombres.add(getString(R.string.spinner_seleccionar_rubro));
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
                } else {
                    errorRubros = mensajeError(response);
                    mostrarErrorRubros();
                }
            }

            @Override
            public void onFailure(Call<Pagina<Rubro>> call, Throwable t) {
                errorRubros = getString(R.string.msj_sin_conexion);
                mostrarErrorRubros();
            }
        });

        // Cargar marcas
        RetrofitClient.getApi(this).getMarcas().enqueue(new Callback<Pagina<Marca>>() {
            @Override
            public void onResponse(Call<Pagina<Marca>> call, Response<Pagina<Marca>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getResults() != null) {
                    marcasList = response.body().getResults();
                    List<String> marcaNombres = new ArrayList<>();
                    marcaNombres.add(getString(R.string.spinner_seleccionar_marca));
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
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            FormularioCatalogoActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[]{getString(R.string.msj_no_se_pudo_cargar)}
                    );
                    spinnerMarca.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Pagina<Marca>> call, Throwable t) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FormularioCatalogoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[]{getString(R.string.msj_no_se_pudo_cargar)}
                );
                spinnerMarca.setAdapter(adapter);
            }
        });
    }

    /** Deja el spinner de rubros en estado de error (no se puede guardar sin rubros reales). */
    private void mostrarErrorRubros() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{getString(R.string.msj_no_se_pudieron_cargar_rubros)}
        );
        spinnerRubro.setAdapter(adapter);
        Toast.makeText(this, errorRubros, Toast.LENGTH_LONG).show();
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
            etNombre.setError(getString(R.string.error_nombre_producto));
            etNombre.requestFocus();
            return;
        }

        if (spinnerRubro.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.msj_seleccionar_rubro, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(precioCostoStr)) {
            etPrecioCosto.setError(getString(R.string.error_precio_costo));
            etPrecioCosto.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(precioVentaStr)) {
            etPrecioVenta.setError(getString(R.string.error_precio_venta));
            etPrecioVenta.requestFocus();
            return;
        }

        double precioCosto;
        double precioVenta;
        try {
            precioCosto = Double.parseDouble(precioCostoStr.replace(",", "."));
            precioVenta = Double.parseDouble(precioVentaStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.msj_precios_invalidos, Toast.LENGTH_SHORT).show();
            return;
        }

        if (precioVenta <= 0) {
            etPrecioVenta.setError(getString(R.string.error_precio_venta_mayor_cero));
            etPrecioVenta.requestFocus();
            return;
        }

        if (precioVenta < precioCosto) {
            etPrecioVenta.setError(getString(R.string.error_precio_venta_menor_costo));
            etPrecioVenta.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stockStr)) {
            etStock.setError(getString(R.string.error_stock));
            etStock.requestFocus();
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            etStock.setError(getString(R.string.error_stock_entero));
            etStock.requestFocus();
            return;
        }

        if (stock < 0) {
            etStock.setError(getString(R.string.error_stock_negativo));
            etStock.requestFocus();
            return;
        }

        // Obtener IDs de rubro y marca
        int rubroId = -1;
        int marcaId = -1;

        if (rubrosList.isEmpty()) {
            Toast.makeText(this, errorRubros != null
                            ? errorRubros
                            : getString(R.string.msj_error_rubros_reintentar),
                    Toast.LENGTH_LONG).show();
            return;
        }

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
                                Toast.makeText(FormularioCatalogoActivity.this, R.string.msj_producto_actualizado, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioCatalogoActivity.this, mensajeError(response), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Producto> call, Throwable t) {
                            btnGuardar.setEnabled(true);
                            Toast.makeText(FormularioCatalogoActivity.this, getString(R.string.msj_error_conexion_detalle, t.getMessage()), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            RetrofitClient.getApi(this).crearProducto(producto)
                    .enqueue(new Callback<Producto>() {
                        @Override
                        public void onResponse(Call<Producto> call, Response<Producto> response) {
                            btnGuardar.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioCatalogoActivity.this, R.string.msj_producto_creado, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioCatalogoActivity.this, mensajeError(response), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Producto> call, Throwable t) {
                            btnGuardar.setEnabled(true);
                            Toast.makeText(FormularioCatalogoActivity.this, getString(R.string.msj_error_conexion_detalle, t.getMessage()), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /** Convierte la respuesta HTTP de error en un mensaje entendible para el usuario. */
    private String mensajeError(Response<?> response) {
        int code = response.code();
        if (code == 401) return getString(R.string.msj_sesion_expirada_larga);
        if (code == 403) return getString(R.string.msj_sin_permiso_modificar_producto);
        if (code == 404) return getString(R.string.msj_producto_no_encontrado);
        if (code == 400) {
            String detalle = "";
            try {
                if (response.errorBody() != null) {
                    detalle = response.errorBody().string();
                }
            } catch (Exception ignored) {
            }
            // Django responde {"campo": ["mensaje"]} o {"detail": "..."}
            detalle = detalle.replaceAll("[\\[\\]\"]", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("detail:", "")
                    .trim();
            if (detalle.isEmpty()) {
                return getString(R.string.msj_datos_invalidos);
            }
            return getString(R.string.msj_datos_invalidos_detalle, detalle);
        }
        return getString(R.string.msj_error_servidor_codigo, code);
    }

    private void iniciarEscaner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.escanear_prompt_producto));
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
                Toast.makeText(this, R.string.msj_escaneo_cancelado, Toast.LENGTH_SHORT).show();
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
