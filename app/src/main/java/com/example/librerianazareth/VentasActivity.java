package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.adapter.VentaAdapter;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.NetworkConstants;
import com.example.librerianazareth.data.model.VentaListPageResponse;
import com.example.librerianazareth.data.model.VentaListResponse;
import com.example.librerianazareth.data.model.DetalleVentaResponse;
import com.example.librerianazareth.data.model.VentaResponse;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasActivity extends BaseActivity implements VentaAdapter.OnVentaClickListener {

    private RecyclerView rvVentas;
    private VentaAdapter ventaAdapter;
    private ProgressBar progressBar;
    private TextView tvEstado;
    private TextView tvPagina;
    private Spinner spinnerFiltro;
    private Button btnAnterior, btnSiguiente;

    private static final String FILTRO_HOY = "hoy";
    private static final String FILTRO_MES = "mes";

    private int paginaActual = 1;
    private int totalPaginas = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        setupBottomNavigation(0);

        rvVentas      = findViewById(R.id.rvVentas);
        progressBar   = findViewById(R.id.progressBarVentas);
        tvEstado      = findViewById(R.id.tvEstadoVentas);
        tvPagina      = findViewById(R.id.tvPaginaVentas);
        spinnerFiltro = findViewById(R.id.spinnerFiltroVentas);
        btnAnterior   = findViewById(R.id.btnAnteriorVentas);
        btnSiguiente  = findViewById(R.id.btnSiguienteVentas);

        ventaAdapter = new VentaAdapter(this);
        rvVentas.setLayoutManager(new LinearLayoutManager(this));
        rvVentas.setAdapter(ventaAdapter);

        String[] opciones = { "Ventas del día", "Ventas del mes" };
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                opciones
        );
        spinnerFiltro.setAdapter(adapterSpinner);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Reiniciar a página 1 cuando cambia el filtro
                paginaActual = 1;
                String filtro = (position == 0) ? FILTRO_HOY : FILTRO_MES;
                cargarPagina(filtro, paginaActual);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnAnterior.setOnClickListener(v -> {
            if (paginaActual > 1) {
                paginaActual--;
                cargarPagina(getFiltroActual(), paginaActual);
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (paginaActual < totalPaginas) {
                paginaActual++;
                cargarPagina(getFiltroActual(), paginaActual);
            }
        });

        findViewById(R.id.fabNuevaVenta).setOnClickListener(v -> {
            startActivity(new Intent(VentasActivity.this, VentaActivity.class));
        });
    }

    private String getFiltroActual() {
        return (spinnerFiltro.getSelectedItemPosition() == 1) ? FILTRO_MES : FILTRO_HOY;
    }

    private void cargarPagina(String filtro, int page) {
        progressBar.setVisibility(View.VISIBLE);
        tvEstado.setVisibility(View.GONE);
        rvVentas.setVisibility(View.GONE);

        RetrofitClient.getApi(this)
                .getVentas(filtro, page)
                .enqueue(new Callback<VentaListPageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VentaListPageResponse> call,
                                           @NonNull Response<VentaListPageResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarEstado("Error al cargar ventas");
                            return;
                        }

                        VentaListPageResponse page = response.body();
                        List<VentaListResponse> ventas = page.getResults();

                        if (ventas == null || ventas.isEmpty()) {
                            mostrarEstado("No hay ventas para mostrar");
                            tvPagina.setText("Pág. " + paginaActual);
                            btnAnterior.setEnabled(false);
                            btnSiguiente.setEnabled(false);
                            return;
                        }

                        rvVentas.setVisibility(View.VISIBLE);
                        ventaAdapter.setVentas(ventas);

                        // Calcular total de páginas
                        totalPaginas = (int) Math.ceil(page.getCount() / (double) NetworkConstants.PAGE_SIZE);
                        tvPagina.setText("Pág. " + paginaActual + "/" + totalPaginas);

                        btnAnterior.setEnabled(paginaActual > 1);
                        btnSiguiente.setEnabled(paginaActual < totalPaginas);
                    }

                    @Override
                    public void onFailure(@NonNull Call<VentaListPageResponse> call,
                                          @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        mostrarEstado("Error de red: " + t.getMessage());
                    }
                });
    }

    private void mostrarEstado(String mensaje) {
        tvEstado.setText(mensaje);
        tvEstado.setVisibility(View.VISIBLE);
        rvVentas.setVisibility(View.GONE);
    }

    // "Ver más" con detalles
    @Override
    public void onVerMas(VentaListResponse venta) {
        RetrofitClient.getApi(this)
                .getVenta(venta.getId())
                .enqueue(new Callback<VentaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VentaResponse> call,
                                           @NonNull Response<VentaResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(VentasActivity.this,
                                    "No se pudo cargar el detalle",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mostrarModalDetalle(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<VentaResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VentasActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarModalDetalle(VentaResponse venta) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_detalle_venta, null);

        TextView tvNumero   = view.findViewById(R.id.tvDetalleNumero);
        TextView tvFecha    = view.findViewById(R.id.tvDetalleFecha);
        TextView tvUsuario  = view.findViewById(R.id.tvDetalleUsuario);
        TextView tvTotal    = view.findViewById(R.id.tvDetalleTotal);
        LinearLayout llProd = view.findViewById(R.id.llDetalleProductos);

        tvNumero.setText("Venta #" + venta.getId());
        tvFecha.setText("Fecha: " + formatearFecha(venta.getFecha()));
        tvUsuario.setText("Usuario: " + venta.getUsuarioNombre());
        tvTotal.setText("TOTAL: $" + venta.getTotal());

        if (venta.getDetalles() != null) {
            for (DetalleVentaResponse d : venta.getDetalles()) {
                View itemView = LayoutInflater.from(this)
                        .inflate(R.layout.item_detalle_venta, llProd, false);

                TextView tvCant     = itemView.findViewById(R.id.tvDetalleCantidad);
                TextView tvNombre   = itemView.findViewById(R.id.tvDetalleNombre);
                TextView tvSubtotal = itemView.findViewById(R.id.tvDetalleSubtotal);

                tvCant.setText(d.getCantidad() + "x");
                tvNombre.setText(d.getProductoNombre() != null ? d.getProductoNombre() : "(sin nombre)");
                tvSubtotal.setText("$" + d.getSubtotal());

                llProd.addView(itemView);
            }
        }

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private String formatearFecha(String isoFecha) {
        if (isoFecha == null) return "";
        try {
            java.text.SimpleDateFormat input =
                    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            java.util.Date date = input.parse(isoFecha.substring(0, 19));

            java.text.SimpleDateFormat output =
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return isoFecha;
        }
    }
}