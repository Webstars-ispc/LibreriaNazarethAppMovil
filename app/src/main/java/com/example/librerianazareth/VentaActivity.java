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

import com.example.librerianazareth.data.model.ItemVenta;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        // Escáner
        ivEscanearVenta.setOnClickListener(v -> iniciarEscaner());

        // Botón Agregar
        btnAgregarProductoVenta.setOnClickListener(v -> agregarPorCodigo());

        // Enter en el EditText también agrega
        etCodigoVenta.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_SEND) {
                agregarPorCodigo();
                return true;
            }
            return false;
        });

        btnCancelarVenta.setOnClickListener(v -> finish());

        btnConfirmarVenta.setOnClickListener(v -> {
            // TODO: en el próximo commit, acá va el POST a /api/ventas/
            Toast.makeText(this, "En el próximo commit 😉", Toast.LENGTH_SHORT).show();
        });

        renderizarCarrito();
    }

    // ESCÁNER
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

    // AGREGAR PRODUCTO POR CÓDIGO (por ahora hardcodeado)
    private void agregarPorCodigo() {
        String codigo = etCodigoVenta.getText().toString().trim();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingresá o escaneá un código", Toast.LENGTH_SHORT).show();
            return;
        }

        // ---- Simulación de búsqueda hasta conectar el backend ----
        ItemVenta encontrado = buscarProductoHardcodeado(codigo);

        if (encontrado == null) {
            Toast.makeText(this, "Producto no encontrado (código: " + codigo + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si ya está en el carrito, incrementar cantidad
        for (ItemVenta item : carrito) {
            if (item.getProductoId() == encontrado.getProductoId()) {
                item.setCantidad(item.getCantidad() + 1);
                renderizarCarrito();
                etCodigoVenta.setText("");
                return;
            }
        }

        // Si no está, agregarlo
        carrito.add(encontrado);
        renderizarCarrito();
        etCodigoVenta.setText("");
    }

    /**
     * Datos temporales hasta conectar base de datos
     * Códigos de prueba:
     *   111 → Cartuchera       $3.400
     *   222 → Lapicera Bic     $300
     *   333 → Cuaderno A4      $1.200
     *   444 → Goma de borrar   $150
     *   555 → Regla 30cm       $450
     */
    private ItemVenta buscarProductoHardcodeado(String codigo) {
        switch (codigo) {
            case "111": return new ItemVenta(1, "Cartuchera",     3400.0, 1);
            case "222": return new ItemVenta(2, "Lapicera Bic",    300.0, 1);
            case "333": return new ItemVenta(3, "Cuaderno A4",    1200.0, 1);
            case "444": return new ItemVenta(4, "Goma de borrar",  150.0, 1);
            case "555": return new ItemVenta(5, "Regla 30cm",      450.0, 1);
            default:    return null;
        }
    }

    // RENDER
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