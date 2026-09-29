package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class ProductoResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("codigo_barras")
    private String codigoBarras;

    @SerializedName("precio_costo")
    private String precioCosto;

    @SerializedName("precio_venta")
    private String precioVenta;

    @SerializedName("stock")
    private int stock;

    @SerializedName("rubro")
    private int rubroId;

    @SerializedName("marca")
    private Integer marcaId;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getPrecioCosto() { return precioCosto; }
    public String getPrecioVenta() { return precioVenta; }
    public int getStock() { return stock; }
    public int getRubroId() { return rubroId; }
    public Integer getMarcaId() { return marcaId; }

    public double getPrecioVentaDouble() {
        try {
            return Double.parseDouble(precioVenta);
        } catch (Exception e) {
            return 0.0;
        }
    }
}