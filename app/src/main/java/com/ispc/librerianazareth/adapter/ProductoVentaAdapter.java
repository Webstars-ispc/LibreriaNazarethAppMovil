package com.ispc.librerianazareth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.librerianazareth.R;
import com.ispc.librerianazareth.data.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoVentaAdapter extends RecyclerView.Adapter<ProductoVentaAdapter.ProductoVentaViewHolder> {

    public interface OnProductoVentaClickListener {
        void onProductoClick(Producto producto);
    }

    private List<Producto> productos = new ArrayList<>();
    private final OnProductoVentaClickListener listener;

    public ProductoVentaAdapter(OnProductoVentaClickListener listener) {
        this.listener = listener;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoVentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoVentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVentaViewHolder holder, int position) {
        Producto p = productos.get(position);

        // En Venta no se edita ni elimina: ocultamos esos botones
        holder.btnEditar.setVisibility(View.GONE);
        holder.btnEliminar.setVisibility(View.GONE);

        // Datos del producto (mismo formato que Catálogo)
        holder.tvNombre.setText(p.getNombre());

        String rubro = p.getRubroNombre() != null ? p.getRubroNombre() : "";
        String marca = p.getMarcaNombre() != null ? p.getMarcaNombre() : "";
        holder.tvRubro.setText(rubro);
        holder.tvMarca.setText(marca.isEmpty()
                ? ""
                : holder.itemView.getContext().getString(R.string.producto_marca_prefijo, marca));

        holder.tvPrecio.setText(holder.itemView.getContext()
                .getString(R.string.producto_precio, p.getPrecioVenta()));
        holder.tvStock.setText(holder.itemView.getContext()
                .getString(R.string.producto_stock, p.getStock()));

        // Click en todo el ítem → agregar al carrito
        holder.itemView.setOnClickListener(v -> listener.onProductoClick(p));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductoVentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRubro, tvMarca, tvPrecio, tvStock;
        View btnEditar, btnEliminar;

        ProductoVentaViewHolder(@NonNull View itemView) {
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