package com.example.librerianazareth.data;

import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ============================================================
    // AUTENTICACIÓN
    // ============================================================

    @POST("api/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest request);


    // ============================================================
    // USUARIOS - CRUD ADMIN
    // ============================================================

    @GET("api/auth/usuarios/")
    Call<List<Usuario>> getUsuarios();

    @POST("api/auth/usuarios/create/")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @PUT("api/auth/usuarios/{id}/")
    Call<Usuario> actualizarUsuario(
            @Path("id") int id,
            @Body Usuario usuario
    );

    @DELETE("api/auth/usuarios/{id}/")
    Call<Void> eliminarUsuario(
            @Path("id") int id
    );
}