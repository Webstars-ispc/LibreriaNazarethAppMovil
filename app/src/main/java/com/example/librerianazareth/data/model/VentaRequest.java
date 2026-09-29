package com.example.librerianazareth.data.model;

import java.util.List;

public class VentaRequest {

    private final List<ItemVentaRequest> productos;

    public VentaRequest(List<ItemVentaRequest> productos) {
        this.productos = productos;
    }

    public List<ItemVentaRequest> getProductos() {
        return productos;
    }
}