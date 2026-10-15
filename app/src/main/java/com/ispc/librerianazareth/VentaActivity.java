package com.ispc.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.librerianazareth.adapter.ProductoVentaAdapter;
import com.ispc.librerianazareth.data.RetrofitClient;
import com.ispc.librerianazareth.data.model.ItemVenta;
import com.ispc.librerianazareth.data.model.ItemVentaRequest;
import com.ispc.librerianazareth.data.model.Producto;
import com.ispc.librerianazareth.data.model.ProductoResponse;
import com.ispc.librerianazareth.data.model.VentaRequest;
import com.ispc.librerianazareth.data.model.VentaResponse;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaActivity extends BaseActivity {

    private EditText etCodigoVenta;
    private ImageView ivEscanearVenta;
    private CardView cvResultadosVenta;
    private RecyclerView rvResultadosVenta;
    private LinearLayout llListaCarrito;
    private TextView tvTotalVenta;
    private TextView tvCarritoVacio;
    private Button btnCancelarVenta;
    private Button btnConfirmarVenta;

    private ProductoVentaAdapter productoAdapter;
    private final List<ItemVenta> carrito = new ArrayList<>();

    // Debounce para búsqueda en vivo (nombre)
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500;

    // Debounce para código escrito a mano
    private static final long BARCODE_DELAY_MS = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        setupBottomNavigation(0);

        // Referencias
        etCodigoVenta     = findViewById(R.id.etCodigoVenta);
        ivEscanearVenta   = findViewById(R.id.ivEscanearVenta);
        cvResultadosVenta = findViewById(R.id.cvResultadosVenta);
        rvResultadosVenta = findViewById(R.id.rvResultadosVenta);
        llListaCarrito    = findViewById(R.id.llListaCarrito);
        tvTotalVenta      = findViewById(R.id.tvTotalVenta);
        tvCarritoVacio    = findViewById(R.id.tvCarritoVacio);
        btnCancelarVenta  = findViewById(R.id.btnCancelarVenta);
        btnConfirmarVenta = findViewById(R.id.btnConfirmarVenta);

        // Adapter para resultados en vivo
        productoAdapter = new ProductoVentaAdapter(this::agregarProductoDesdeLista);
        rvResultadosVenta.setLayoutManager(new LinearLayoutManager(this));
        rvResultadosVenta.setAdapter(productoAdapter);

        // Escáner
        ivEscanearVenta.setOnClickListener(v -> iniciarEscaner());

        // Enter en el teclado → forzar búsqueda manual
        etCodigoVenta.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_SEND) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                ocultarResultados();
                agregarProducto();
                return true;
            }
            return false;
        });

        // Búsqueda en vivo con debounce
        etCodigoVenta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                final String query = s.toString().trim();

                if (query.isEmpty()) {
                    ocultarResultados();
                    return;
                }

                if (esCodigoDeBarras(query)) {
                    // Código numérico completo → esperar y auto-agregar
                    ocultarResultados();
                    searchRunnable = () -> agregarProducto();
                    searchHandler.postDelayed(searchRunnable, BARCODE_DELAY_MS);
                    return;
                }

                // Texto → buscar en vivo por nombre
                searchRunnable = () -> buscarProductosEnVivo(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCancelarVenta.setOnClickListener(v -> finish());
        btnConfirmarVenta.setOnClickListener(v -> confirmarVenta());

        renderizarCarrito();
    }

    // ---------------------------------------------------------------
    // BÚSQUEDA EN VIVO
    // ---------------------------------------------------------------
    private void buscarProductosEnVivo(String query) {
        RetrofitClient.getApi(this)
                .buscarProductoPorNombre(query)
                .enqueue(new Callback<ProductoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoResponse> call,
                                           @NonNull Response<ProductoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getResults() == null
                                || response.body().getResults().isEmpty()) {
                            ocultarResultados();
                            return;
                        }

                        productoAdapter.setProductos(response.body().getResults());
                        cvResultadosVenta.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoResponse> call,
                                          @NonNull Throwable t) {
                        ocultarResultados();
                    }
                });
    }

    private void agregarProductoDesdeLista(Producto producto) {
        agregarItemAlCarrito(producto);
        etCodigoVenta.setText("");
        ocultarResultados();
    }

    private void ocultarResultados() {
        cvResultadosVenta.setVisibility(View.GONE);
        productoAdapter.setProductos(new ArrayList<>());
    }

    private boolean esCodigoDeBarras(String texto) {
        return texto.matches("\\d+") && texto.length() >= 8;
    }

    // ---------------------------------------------------------------
    // ESCÁNER
    // ---------------------------------------------------------------
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
            if (result.getContents() != null) {
                String codigo = result.getContents();

                // Cancelamos cualquier debounce pendiente
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                // Seteamos el texto sin disparar búsqueda en vivo
                // (el TextWatcher detecta código pero ya lo cancelamos arriba)
                etCodigoVenta.setText(codigo);
                etCodigoVenta.setSelection(codigo.length());

                // Auto-agregamos directo porque el código viene completo y único
                agregarProducto();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ---------------------------------------------------------------
    // AGREGAR PRODUCTO (por código de barras o Enter)
    // ---------------------------------------------------------------
    private void agregarProducto() {
        final String texto = etCodigoVenta.getText().toString().trim();

        if (texto.isEmpty()) {
            Toast.makeText(this, R.string.msj_ingresar_escanear_codigo, Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApi(this)
                .buscarProductoPorCodigo(texto)
                .enqueue(new Callback<ProductoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoResponse> call,
                                           @NonNull Response<ProductoResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResults() != null
                                && !response.body().getResults().isEmpty()) {
                            agregarItemAlCarrito(response.body().getResults().get(0));
                            etCodigoVenta.setText("");
                            ocultarResultados();
                        } else {
                            buscarPorNombre(texto);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VentaActivity.this,
                                getString(R.string.msj_error_red, t.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void buscarPorNombre(String texto) {
        RetrofitClient.getApi(this)
                .buscarProductoPorNombre(texto)
                .enqueue(new Callback<ProductoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoResponse> call,
                                           @NonNull Response<ProductoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getResults() == null
                                || response.body().getResults().isEmpty()) {
                            Toast.makeText(VentaActivity.this,
                                    R.string.msj_producto_no_encontrado,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Producto> resultados = response.body().getResults();

                        if (resultados.size() == 1) {
                            agregarItemAlCarrito(resultados.get(0));
                            etCodigoVenta.setText("");
                            ocultarResultados();
                        } else {
                            mostrarDialogoSeleccion(resultados);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VentaActivity.this,
                                getString(R.string.msj_error_red, t.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarDialogoSeleccion(List<Producto> productos) {
        String[] items = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            items[i] = getString(R.string.producto_precio_seleccion,
                    p.getNombre(), p.getPrecioVenta());
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_seleccionar_producto)
                .setItems(items, (dialog, which) -> {
                    agregarItemAlCarrito(productos.get(which));
                    etCodigoVenta.setText("");
                    ocultarResultados();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void agregarItemAlCarrito(Producto producto) {
        int id = producto.getId();

        if (producto.getStock() <= 0) {
            Toast.makeText(this,
                    "Sin stock disponible: " + producto.getNombre(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (ItemVenta item : carrito) {
            if (item.getProductoId() == id) {
                if (item.getCantidad() >= item.getStock()) {
                    Toast.makeText(this,
                            "Stock máximo alcanzado para: " + item.getNombre(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                item.setCantidad(item.getCantidad() + 1);
                renderizarCarrito();
                return;
            }
        }

        carrito.add(new ItemVenta(
                id,
                producto.getNombre(),
                producto.getPrecioVenta(),
                producto.getStock(),
                1
        ));
        renderizarCarrito();
    }

    // ---------------------------------------------------------------
    // CONFIRMAR VENTA
    // ---------------------------------------------------------------
    private void confirmarVenta() {
        if (carrito.isEmpty()) {
            Toast.makeText(this, R.string.carrito_vacio, Toast.LENGTH_SHORT).show();
            return;
        }

        for (ItemVenta item : carrito) {
            if (item.getCantidad() > item.getStock()) {
                Toast.makeText(this,
                        "Stock insuficiente para: " + item.getNombre()
                                + " (disponible: " + item.getStock() + ")",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        List<ItemVentaRequest> items = new ArrayList<>();
        for (ItemVenta item : carrito) {
            items.add(new ItemVentaRequest(item.getProductoId(), item.getCantidad()));
        }

        VentaRequest request = new VentaRequest(items);

        btnConfirmarVenta.setEnabled(false);
        btnConfirmarVenta.setText(R.string.btn_procesando);

        RetrofitClient.getApi(this)
                .registrarVenta(request)
                .enqueue(new Callback<VentaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VentaResponse> call,
                                           @NonNull Response<VentaResponse> response) {
                        restaurarBotonConfirmar();

                        if (response.isSuccessful() && response.body() != null) {
                            VentaResponse venta = response.body();

                            Toast.makeText(VentaActivity.this,
                                    getString(R.string.msj_venta_registrada, venta.getId()),
                                    Toast.LENGTH_LONG).show();

                            carrito.clear();
                            renderizarCarrito();

                        } else {
                            String mensaje = getString(R.string.msj_error_registrar_venta);
                            try {
                                if (response.errorBody() != null) {
                                    mensaje = response.errorBody().string();
                                }
                            } catch (Exception ignored) { }

                            Toast.makeText(VentaActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VentaResponse> call,
                                          @NonNull Throwable t) {
                        restaurarBotonConfirmar();
                        Toast.makeText(VentaActivity.this,
                                getString(R.string.msj_error_red, t.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void restaurarBotonConfirmar() {
        btnConfirmarVenta.setEnabled(true);
        btnConfirmarVenta.setText(R.string.confirmar_venta);
    }

    // ---------------------------------------------------------------
    // RENDER DEL CARRITO
    // ---------------------------------------------------------------
    private void renderizarCarrito() {
        llListaCarrito.removeAllViews();

        if (carrito.isEmpty()) {
            tvCarritoVacio.setVisibility(View.VISIBLE);
            tvTotalVenta.setText(R.string.total_default);
            return;
        }

        tvCarritoVacio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < carrito.size(); i++) {
            final int index = i;
            final ItemVenta item = carrito.get(i);

            View itemView = inflater.inflate(R.layout.item_venta_producto, llListaCarrito, false);

            TextView tvNombre   = itemView.findViewById(R.id.tvNombreItemVenta);
            TextView tvPrecio   = itemView.findViewById(R.id.tvPrecioItemVenta);
            TextView tvCantidad = itemView.findViewById(R.id.tvCantidadItemVenta);
            TextView tvSubtotal = itemView.findViewById(R.id.tvSubtotalItemVenta);
            Button   btnMenos   = itemView.findViewById(R.id.btnMenosItemVenta);
            Button   btnMas     = itemView.findViewById(R.id.btnMasItemVenta);
            View     btnEliminar = itemView.findViewById(R.id.ivEliminarItemVenta);

            tvNombre.setText(item.getNombre());
            tvPrecio.setText(getString(R.string.precio_format, item.getPrecioUnitario()));
            tvCantidad.setText(String.valueOf(item.getCantidad()));
            tvSubtotal.setText(getString(R.string.precio_format, item.getSubtotal()));

            btnMenos.setOnClickListener(v -> {
                if (item.getCantidad() > 1) {
                    item.setCantidad(item.getCantidad() - 1);
                    renderizarCarrito();
                }
            });

            btnMas.setOnClickListener(v -> {
                if (item.getCantidad() >= item.getStock()) {
                    Toast.makeText(VentaActivity.this,
                            "Stock máximo alcanzado (" + item.getStock() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                item.setCantidad(item.getCantidad() + 1);
                renderizarCarrito();
            });

            btnEliminar.setOnClickListener(v -> {
                carrito.remove(index);
                renderizarCarrito();
            });

            llListaCarrito.addView(itemView);
        }

        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (ItemVenta item : carrito) {
            total += item.getSubtotal();
        }
        tvTotalVenta.setText(getString(R.string.total_format, total));
    }
}