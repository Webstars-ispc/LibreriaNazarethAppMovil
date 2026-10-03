package com.example.librerianazareth.data.model;

public class ItemVenta {

    private final int productoId;
    private final String nombre;
    private final double precioUnitario;
    private int cantidad;

    public ItemVenta(int productoId, String nombre, double precioUnitario, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }
}