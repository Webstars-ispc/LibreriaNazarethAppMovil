package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("codigo_barras")
    private String codigoBarras;

    @SerializedName("rubro")
    private int rubro;

    @SerializedName("rubro_nombre")
    private String rubroNombre;

    @SerializedName("marca")
    private int marca;

    @SerializedName("marca_nombre")
    private String marcaNombre;

    @SerializedName("precio_costo")
    private double precioCosto;

    @SerializedName("precio_venta")
    private double precioVenta;

    @SerializedName("stock")
    private int stock;

    public Producto() {}

    public Producto(String nombre, String descripcion, String codigoBarras,
                    int rubro, int marca, double precioCosto,
                    double precioVenta, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigoBarras = codigoBarras;
        this.rubro = rubro;
        this.marca = marca;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public int getRubro() { return rubro; }
    public void setRubro(int rubro) { this.rubro = rubro; }

    public String getRubroNombre() { return rubroNombre; }
    public void setRubroNombre(String rubroNombre) { this.rubroNombre = rubroNombre; }

    public int getMarca() { return marca; }
    public void setMarca(int marca) { this.marca = marca; }

    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }

    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
