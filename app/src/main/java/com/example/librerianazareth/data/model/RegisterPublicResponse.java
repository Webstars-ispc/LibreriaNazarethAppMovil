package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterPublicResponse {

    @SerializedName("refresh")
    private String refresh;

    @SerializedName("access")
    private String access;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("role")
    private String role;

    public String getRefresh() { return refresh; }
    public String getAccess() { return access; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRole() { return role; }
}