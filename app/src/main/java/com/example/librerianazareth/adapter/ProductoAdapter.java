package com.example.librerianazareth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.R;
import com.example.librerianazareth.data.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public interface OnProductoClickListener {
        void onEditar(Producto producto);
        void onEliminar(Producto producto);
    }

    private List<Producto> productos = new ArrayList<>();
    private final OnProductoClickListener listener;

    public ProductoAdapter(OnProductoClickListener listener) {
        this.listener = listener;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto p = productos.get(position);

        holder.tvNombre.setText(p.getNombre());

        String rubro = p.getRubroNombre() != null ? p.getRubroNombre() : "";
        String marca = p.getMarcaNombre() != null ? p.getMarcaNombre() : "";
        holder.tvRubro.setText(rubro);
        holder.tvMarca.setText(marca.isEmpty() ? "" : "- " + marca);

        holder.tvPrecio.setText(String.format(Locale.getDefault(), "$%.2f", p.getPrecioVenta()));
        holder.tvStock.setText(String.format(Locale.getDefault(), "Stock: %d", p.getStock()));

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(p));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRubro, tvMarca, tvPrecio, tvStock;
        Button btnEditar, btnEliminar;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvRubro = itemView.findViewById(R.id.tvRubroProducto);
            tvMarca = itemView.findViewById(R.id.tvMarcaProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            tvStock = itemView.findViewById(R.id.tvStockProducto);
            btnEditar = itemView.findViewById(R.id.btnEditarProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProducto);
        }
    }
}
