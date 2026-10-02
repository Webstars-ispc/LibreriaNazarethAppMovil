package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.adapter.VentaAdapter;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.VentaListPageResponse;
import com.example.librerianazareth.data.model.VentaListResponse;

import java.util.List;

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
                        totalPaginas = (int) Math.ceil(page.getCount() / 10.0);
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

    // Por ahora "Ver más" solo muestra un Toast, se tiene que hacer un modal con detalles
    @Override
    public void onVerMas(VentaListResponse venta) {
        Toast.makeText(this,
                "Detalle de venta #" + venta.getId() + " (próximo commit)",
                Toast.LENGTH_SHORT).show();
    }
}