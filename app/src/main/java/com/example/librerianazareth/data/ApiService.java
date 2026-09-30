package com.example.librerianazareth.data;

import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.Marca;
import com.example.librerianazareth.data.model.Producto;
import com.example.librerianazareth.data.model.ProductoResponse;
import com.example.librerianazareth.data.model.RegisterRequest;
import com.example.librerianazareth.data.model.Rubro;
import com.example.librerianazareth.data.model.UserProfileResponse;
import com.example.librerianazareth.data.model.Usuario;
import com.example.librerianazareth.data.model.UsuarioResponse;
import com.example.librerianazareth.data.model.VentaRequest;
import com.example.librerianazareth.data.model.VentaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Definición de todos los endpoints de la API REST.
 *
 * CÓMO AGREGAR UN ENDPOINT NUEVO:
 *   1. Crear el modelo del request/response en data/model/
 *   2. Agregar el método acá con la anotación correspondiente: VER EJEMPLO DE LOGIN LINEA 30
 *      - @GET("ruta/")                → Call<TipoRespuesta> miMetodo();
 *      - @POST("ruta/")               → Call<TipoRespuesta> miMetodo(@Body TipoRequest req);
 *      - @PUT("ruta/{id}/")           → Call<TipoRespuesta> miMetodo(@Path("id") int id, @Body TipoRequest req);
 *      - @DELETE("ruta/{id}/")        → Call<Void> miMetodo(@Path("id") int id);
 *   3. Llamarlo desde tu Activity.java:
 *      RetrofitClient.getApi(this).miMetodo(...).enqueue(new Callback<...>() {...});
 *
 * IMPORTANTE: no borrar métodos de otros. Si hay conflicto de merge, AVISAR.
 * IMPORTANTE: todos los endpoints están en el REPO DE BACKEND --> carpeta DATABASE
 */
public interface ApiService {

    // ============================================================
    // AUTENTICACIÓN
    // ============================================================

    @POST("api/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/auth/me/")
    Call<UserProfileResponse> me();

    // ============================================================
    // PRODUCTOS
    // ============================================================

    @GET("api/productos/")
    Call<ProductoResponse> buscarProductoPorCodigo(
            @Query("codigo_barras") String codigoBarras
    );

    @GET("api/productos/")
    Call<ProductoResponse> getProductos(
            @Query("page") int page,
            @Query("search") String search
    );

    @GET("api/productos/")
    Call<ProductoResponse> getProductos(@Query("page") int page);

    @POST("api/productos/")
    Call<Producto> crearProducto(@Body Producto producto);

    @GET("api/productos/{id}/")
    Call<Producto> getProducto(@Path("id") int id);

    @PUT("api/productos/{id}/")
    Call<Producto> actualizarProducto(@Path("id") int id, @Body Producto producto);

    @DELETE("api/productos/{id}/")
    Call<Void> eliminarProducto(@Path("id") int id);

    // ============================================================
    // RUBROS
    // ============================================================

    @GET("api/rubros/")
    Call<List<Rubro>> getRubros();

    // ============================================================
    // MARCAS
    // ============================================================

    @GET("api/marcas/")
    Call<List<Marca>> getMarcas();

    // ============================================================
    // USUARIOS
    // ============================================================

    @GET("api/usuarios/")
    Call<UsuarioResponse> getUsuarios(
            @Query("page") int page,
            @Query("search") String search
    );

    @GET("api/usuarios/")
    Call<UsuarioResponse> getUsuarios(@Query("page") int page);

    @POST("api/usuarios/")
    Call<Usuario> crearUsuario(@Body RegisterRequest request);

    @GET("api/usuarios/{id}/")
    Call<Usuario> getUsuario(@Path("id") int id);

    @PUT("api/usuarios/{id}/")
    Call<Usuario> actualizarUsuario(@Path("id") int id, @Body RegisterRequest request);

    @DELETE("api/usuarios/{id}/")
    Call<Void> eliminarUsuario(@Path("id") int id);

    // ============================================================
    // VENTAS
    // ============================================================

    @POST("api/ventas/")
    Call<VentaResponse> registrarVenta(
            @Body VentaRequest venta
    );
}