package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterPublicRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public RegisterPublicRequest(String username, String nombre, String apellido,
                                 String email, String password) {
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}