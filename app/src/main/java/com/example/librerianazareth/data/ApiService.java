package com.example.librerianazareth.data;

import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    // ============================================================
    // PRODUCTOS
    // ============================================================
    //
    // @GET("api/productos/")
    // Call<List<Producto>> getProductos();
    //
    // @POST("api/productos/")
    // Call<Producto> crearProducto(@Body Producto producto);
    //
    // @GET("api/productos/{id}/")
    // Call<Producto> getProducto(@Path("id") int id);
    //
    // @PUT("api/productos/{id}/")
    // Call<Producto> actualizarProducto(@Path("id") int id, @Body Producto producto);
    //
    // @DELETE("api/productos/{id}/")
    // Call<Void> eliminarProducto(@Path("id") int id);

    // ============================================================
    // RUBROS, MARCAS, USUARIOS, VENTAS...
    // Cada uno agrega lo suyo aca
    // ============================================================
}