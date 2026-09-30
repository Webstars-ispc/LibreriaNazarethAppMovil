package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.ItemVenta;
import com.example.librerianazareth.data.model.ItemVentaRequest;
import com.example.librerianazareth.data.model.Producto;
import com.example.librerianazareth.data.model.ProductoResponse;
import com.example.librerianazareth.data.model.VentaRequest;
import com.example.librerianazareth.data.model.VentaResponse;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaActivity extends BaseActivity {

    private EditText etCodigoVenta;
    private ImageView ivEscanearVenta;
    private Button btnAgregarProductoVenta;
    private LinearLayout llListaCarrito;
    private TextView tvTotalVenta;
    private TextView tvCarritoVacio;
    private Button btnCancelarVenta;
    private Button btnConfirmarVenta;

    private final List<ItemVenta> carrito = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        setupBottomNavigation(0);

        etCodigoVenta           = findViewById(R.id.etCodigoVenta);
        ivEscanearVenta         = findViewById(R.id.ivEscanearVenta);
        btnAgregarProductoVenta = findViewById(R.id.btnAgregarProductoVenta);
        llListaCarrito          = findViewById(R.id.llListaCarrito);
        tvTotalVenta            = findViewById(R.id.tvTotalVenta);
        tvCarritoVacio          = findViewById(R.id.tvCarritoVacio);
        btnCancelarVenta        = findViewById(R.id.btnCancelarVenta);
        btnConfirmarVenta       = findViewById(R.id.btnConfirmarVenta);

        ivEscanearVenta.setOnClickListener(v -> iniciarEscaner());
        btnAgregarProductoVenta.setOnClickListener(v -> agregarProducto());

        etCodigoVenta.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_SEND) {
                agregarProducto();
                return true;
            }
            return false;
        });

        btnCancelarVenta.setOnClickListener(v -> finish());

        // ⬇️ CONFIRMAR VENTA
        btnConfirmarVenta.setOnClickListener(v -> confirmarVenta());

        renderizarCarrito();
    }

    // ---------------------------------------------------------------
    // ESCÁNER
    // ---------------------------------------------------------------
    private void iniciarEscaner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
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
            if (result.getContents() != null) {
                String codigo = result.getContents();
                etCodigoVenta.setText(codigo);
                etCodigoVenta.setSelection(codigo.length());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ---------------------------------------------------------------
    // AGREGAR PRODUCTO (código → nombre)
    // ---------------------------------------------------------------
    private void agregarProducto() {
        final String texto = etCodigoVenta.getText().toString().trim();

        if (texto.isEmpty()) {
            Toast.makeText(this, "Ingresá o escaneá un código o nombre", Toast.LENGTH_SHORT).show();
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
                        } else {
                            buscarPorNombre(texto);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VentaActivity.this,
                                "Error de red: " + t.getMessage(),
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
                                    "Producto no encontrado",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Producto> resultados = response.body().getResults();

                        if (resultados.size() == 1) {
                            agregarItemAlCarrito(resultados.get(0));
                            etCodigoVenta.setText("");
                        } else {
                            mostrarDialogoSeleccion(resultados);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VentaActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarDialogoSeleccion(List<Producto> productos) {
        String[] items = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            items[i] = String.format(Locale.getDefault(),
                    "%s - $%.0f", p.getNombre(), p.getPrecioVenta());
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccioná un producto")
                .setItems(items, (dialog, which) -> {
                    agregarItemAlCarrito(productos.get(which));
                    etCodigoVenta.setText("");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarItemAlCarrito(Producto producto) {
        int id = producto.getId();

        for (ItemVenta item : carrito) {
            if (item.getProductoId() == id) {
                item.setCantidad(item.getCantidad() + 1);
                renderizarCarrito();
                return;
            }
        }

        carrito.add(new ItemVenta(
                id,
                producto.getNombre(),
                producto.getPrecioVenta(),
                1
        ));
        renderizarCarrito();
    }

    // ---------------------------------------------------------------
    // CONFIRMAR VENTA (POST /api/ventas/)
    // ---------------------------------------------------------------
    private void confirmarVenta() {
        if (carrito.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Armar la lista de items para el request
        List<ItemVentaRequest> items = new ArrayList<>();
        for (ItemVenta item : carrito) {
            items.add(new ItemVentaRequest(item.getProductoId(), item.getCantidad()));
        }

        VentaRequest request = new VentaRequest(items);

        // Deshabilitar el botón mientras se procesa
        btnConfirmarVenta.setEnabled(false);
        btnConfirmarVenta.setText("Procesando...");

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
                                    "Venta #" + venta.getId() + " registrada",
                                    Toast.LENGTH_LONG).show();

                            // Limpiar carrito
                            carrito.clear();
                            renderizarCarrito();

                            // TODO (próximo commit): abrir TicketActivity con "venta"

                        } else {
                            // Error del backend (400, 401, 500, etc.)
                            String mensaje = "Error al registrar la venta";
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
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void restaurarBotonConfirmar() {
        btnConfirmarVenta.setEnabled(true);
        btnConfirmarVenta.setText("Confirmar venta");
    }

    // ---------------------------------------------------------------
    // RENDER DEL CARRITO
    // ---------------------------------------------------------------
    private void renderizarCarrito() {
        llListaCarrito.removeAllViews();

        if (carrito.isEmpty()) {
            tvCarritoVacio.setVisibility(View.VISIBLE);
            tvTotalVenta.setText("TOTAL: $0");
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
            tvPrecio.setText(String.format(Locale.getDefault(),
                    "$%.0f", item.getPrecioUnitario()));
            tvCantidad.setText(String.valueOf(item.getCantidad()));
            tvSubtotal.setText(String.format(Locale.getDefault(),
                    "$%.0f", item.getSubtotal()));

            btnMenos.setOnClickListener(v -> {
                if (item.getCantidad() > 1) {
                    item.setCantidad(item.getCantidad() - 1);
                    renderizarCarrito();
                }
            });

            btnMas.setOnClickListener(v -> {
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
        tvTotalVenta.setText(String.format(Locale.getDefault(), "TOTAL: $%.0f", total));
    }
}