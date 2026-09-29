package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class DetalleVentaResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("producto")
    private int productoId;

    @SerializedName("cantidad")
    private int cantidad;

    @SerializedName("precio_unitario")
    private String precioUnitario;

    @SerializedName("subtotal")
    private String subtotal;

    public int getId() { return id; }
    public int getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public String getPrecioUnitario() { return precioUnitario; }
    public String getSubtotal() { return subtotal; }
}