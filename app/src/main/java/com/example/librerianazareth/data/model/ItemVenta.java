package com.example.librerianazareth.data.model;

public class ItemVenta {

    private final int productoId;
    private final String nombre;
    private final double precioUnitario;
    private final int stock;
    private int cantidad;

    public ItemVenta(int productoId, String nombre, double precioUnitario, int stock, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
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
    public int getStock() { return stock; }

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