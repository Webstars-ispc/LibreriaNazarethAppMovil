package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.adapter.ProductoAdapter;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Producto;
import com.example.librerianazareth.data.model.ProductoResponse;
import com.example.librerianazareth.data.local.TokenManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogoActivity extends BaseActivity implements ProductoAdapter.OnProductoClickListener {

    private RecyclerView rvProductos;
    private ProductoAdapter productoAdapter;
    private ProgressBar progressBar;
    private TextView tvEstado;
    private TextView tvPagina;
    private Button btnAnterior, btnSiguiente;
    private EditText etBuscarProducto;

    private int paginaActual = 1;
    private int totalPaginas = 1;
    private String textoBusqueda = "";

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        setupBottomNavigation(R.id.nav_catalogo);

        // Referencias
        rvProductos       = findViewById(R.id.rvProductos);
        progressBar       = findViewById(R.id.progressBar);
        tvEstado          = findViewById(R.id.tvEstado);
        tvPagina          = findViewById(R.id.tvPagina);
        btnAnterior       = findViewById(R.id.btnAnterior);
        btnSiguiente      = findViewById(R.id.btnSiguiente);
        etBuscarProducto  = findViewById(R.id.etBuscarProducto);

        // RecyclerView + Adapter
        TokenManager tm = new TokenManager(this);
        productoAdapter = new ProductoAdapter(this, tm.isAdmin());
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);

        // Botones de paginación
        btnAnterior.setOnClickListener(v -> {
            if (paginaActual > 1) {
                paginaActual--;
                cargarPagina(paginaActual);
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (paginaActual < totalPaginas) {
                paginaActual++;
                cargarPagina(paginaActual);
            }
        });

        // Botón agregar producto (solo con sesión iniciada: el backend exige JWT)
        View fabAgregar = findViewById(R.id.fabAgregarProducto);
        if (!tm.isLoggedIn()) {
            fabAgregar.setVisibility(View.GONE);
        }
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogoActivity.this, FormularioCatalogoActivity.class);
            startActivity(intent);
        });

        // Escáner
        ImageView ivEscanearCodigo = findViewById(R.id.ivEscanearCodigo);
        ivEscanearCodigo.setOnClickListener(v -> iniciarEscaner());

        // Búsqueda con debounce (500 ms)
        etBuscarProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    textoBusqueda = s.toString().trim();
                    paginaActual = 1;
                    cargarPagina(paginaActual);
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Primera carga
        cargarPagina(paginaActual);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPagina(paginaActual);
    }

    private void cargarPagina(int page) {
        progressBar.setVisibility(View.VISIBLE);
        tvEstado.setVisibility(View.GONE);
        rvProductos.setVisibility(View.GONE);

        Call<ProductoResponse> call = textoBusqueda.isEmpty()
                ? RetrofitClient.getApi(this).getProductos(page)
                : RetrofitClient.getApi(this).getProductos(page, textoBusqueda);

        call.enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductoResponse> call,
                                   @NonNull Response<ProductoResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(CatalogoActivity.this, R.string.msj_sesion_expirada, Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 403) {
                        mostrarEstado(getString(R.string.msj_sin_permiso_catalogo));
                    } else {
                        mostrarEstado(getString(R.string.msj_error_cargar_productos));
                    }
                    return;
                }

                ProductoResponse page = response.body();
                List<Producto> productos = page.getResults();

                if (productos == null || productos.isEmpty()) {
                    mostrarEstado(textoBusqueda.isEmpty()
                            ? getString(R.string.msj_sin_productos)
                            : getString(R.string.msj_sin_resultados_productos));
                    tvPagina.setText(getString(R.string.pagina_actual, paginaActual));
                    return;
                }

                rvProductos.setVisibility(View.VISIBLE);
                productoAdapter.setProductos(productos);

                // Calcular total de páginas (10 por página)
                totalPaginas = (int) Math.ceil(page.getCount() / 10.0);
                tvPagina.setText(getString(R.string.pagina_de, paginaActual, totalPaginas));

                // Habilitar/deshabilitar botones
                btnAnterior.setEnabled(paginaActual > 1);
                btnSiguiente.setEnabled(paginaActual < totalPaginas);
            }

            @Override
            public void onFailure(@NonNull Call<ProductoResponse> call,
                                  @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                mostrarEstado(getString(R.string.msj_error_red, t.getMessage()));
            }
        });
    }

    private void mostrarEstado(String mensaje) {
        tvEstado.setText(mensaje);
        tvEstado.setVisibility(View.VISIBLE);
        rvProductos.setVisibility(View.GONE);
    }

    // ESCÁNER
    private void iniciarEscaner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.escanear_prompt));
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
                etBuscarProducto.setText(codigo);
                etBuscarProducto.setSelection(codigo.length());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // (editar / eliminar)
    @Override
    public void onEditar(Producto producto) {
        Intent intent = new Intent(CatalogoActivity.this, FormularioCatalogoActivity.class);
        intent.putExtra("modo_edicion", true);
        intent.putExtra("producto_id", producto.getId());
        intent.putExtra("nombre", producto.getNombre());
        intent.putExtra("descripcion", producto.getDescripcion());
        intent.putExtra("codigo_barras", producto.getCodigoBarras());
        intent.putExtra("rubro", producto.getRubro());
        intent.putExtra("marca", producto.getMarca() != null ? producto.getMarca() : -1);
        intent.putExtra("precio_costo", producto.getPrecioCosto());
        intent.putExtra("precio_venta", producto.getPrecioVenta());
        intent.putExtra("stock", producto.getStock());
        startActivity(intent);
    }

    @Override
    public void onEliminar(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_eliminar_producto_titulo)
                .setMessage(getString(R.string.dialog_eliminar_producto_msg, producto.getNombre()))
                .setPositiveButton(R.string.eliminar, (dialog, which) ->
                        RetrofitClient.getApi(this).eliminarProducto(producto.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call,
                                                           @NonNull Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(CatalogoActivity.this,
                                                    R.string.msj_producto_eliminado, Toast.LENGTH_SHORT).show();
                                            cargarPagina(paginaActual);
                                        } else {
                                            Toast.makeText(CatalogoActivity.this,
                                                    R.string.msj_error_eliminar, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call,
                                                          @NonNull Throwable t) {
                                        Toast.makeText(CatalogoActivity.this,
                                                R.string.msj_error_conexion, Toast.LENGTH_SHORT).show();
                                    }
                                }))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}