package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VentaResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("usuario")
    private int usuarioId;

    @SerializedName("usuario_nombre")
    private String usuarioNombre;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("total")
    private String total;

    @SerializedName("detalles")
    private List<DetalleVentaResponse> detalles;

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public String getFecha() { return fecha; }
    public String getTotal() { return total; }
    public List<DetalleVentaResponse> getDetalles() { return detalles; }
}