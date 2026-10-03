package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

//Esto sirve para leer las respuestas de auth/me/ y obtener los datos basicos del usuario
public class UserProfileResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}