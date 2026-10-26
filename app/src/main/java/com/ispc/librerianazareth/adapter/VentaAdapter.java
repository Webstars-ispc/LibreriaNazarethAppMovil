package com.ispc.librerianazareth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.librerianazareth.R;
import com.ispc.librerianazareth.data.model.VentaListResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    public interface OnVentaClickListener {
        void onVerMas(VentaListResponse venta);
        void onCancelar(VentaListResponse venta); // Cambiado de onEliminar a onCancelar
    }

    private List<VentaListResponse> ventas = new ArrayList<>();
    private final OnVentaClickListener listener;
    private final boolean esAdmin;

    public VentaAdapter(OnVentaClickListener listener, boolean esAdmin) {
        this.listener = listener;
        this.esAdmin = esAdmin;
    }

    public void setVentas(List<VentaListResponse> ventas) {
        this.ventas = ventas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        VentaListResponse v = ventas.get(position);

        holder.tvNumeroVenta.setText(holder.itemView.getContext().getString(R.string.venta_numero, v.getId()));
        holder.tvFechaVenta.setText(formatearFecha(v.getFecha()));
        holder.tvTotalVentaItem.setText(holder.itemView.getContext().getString(R.string.venta_total_item, v.getTotal()));

        holder.btnVerMas.setOnClickListener(view -> listener.onVerMas(v));

        // Mostrar botón cancelar solo si es admin
        if (esAdmin) {
            holder.btnCancelarVenta.setVisibility(View.VISIBLE);
            holder.btnCancelarVenta.setOnClickListener(view -> listener.onCancelar(v));
        } else {
            holder.btnCancelarVenta.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }

    private String formatearFecha(String isoFecha) {
        if (isoFecha == null) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(isoFecha.substring(0, 19));

            SimpleDateFormat output = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            return output.format(date);
        } catch (ParseException e) {
            return isoFecha;
        }
    }

    static class VentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroVenta, tvFechaVenta, tvTotalVentaItem;
        Button btnVerMas, btnCancelarVenta;

        VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroVenta = itemView.findViewById(R.id.tvNumeroVenta);
            tvFechaVenta = itemView.findViewById(R.id.tvFechaVenta);
            tvTotalVentaItem = itemView.findViewById(R.id.tvTotalVentaItem);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);
            btnCancelarVenta = itemView.findViewById(R.id.btnCancelarVenta);
        }
    }
}