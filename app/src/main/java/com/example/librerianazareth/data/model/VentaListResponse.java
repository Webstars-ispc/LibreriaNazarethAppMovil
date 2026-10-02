package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class VentaListResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("usuario_nombre")
    private String usuarioNombre;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("total")
    private String total;

    public int getId() { return id; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public String getFecha() { return fecha; }
    public String getTotal() { return total; }
}