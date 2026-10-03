package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class ItemVentaRequest {

    @SerializedName("producto_id")
    private final int productoId;

    @SerializedName("cantidad")
    private final int cantidad;

    public ItemVentaRequest(int productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public int getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
}