package com.example.librerianazareth;
import com.example.librerianazareth.data.model.ItemVenta;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentaActivity extends BaseActivity {

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

        llListaCarrito    = findViewById(R.id.llListaCarrito);
        tvTotalVenta      = findViewById(R.id.tvTotalVenta);
        tvCarritoVacio    = findViewById(R.id.tvCarritoVacio);
        btnCancelarVenta  = findViewById(R.id.btnCancelarVenta);
        btnConfirmarVenta = findViewById(R.id.btnConfirmarVenta);

        btnCancelarVenta.setOnClickListener(v -> finish());

        btnConfirmarVenta.setOnClickListener(v -> {
            // TODO: en el próximo commit, acá va el POST a /api/ventas/
        });

        // ---- Datos hardcodeados (solo para ver la pantalla) ----
        carrito.add(new ItemVenta(1, "Cartuchera",      3400.0, 1));
        carrito.add(new ItemVenta(2, "Lapicera Bic",     300.0, 2));
        carrito.add(new ItemVenta(3, "Cuaderno A4",     1200.0, 1));

        renderizarCarrito();
    }

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