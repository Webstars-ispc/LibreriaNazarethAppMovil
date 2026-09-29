package com.example.librerianazareth.data;

import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.ProductoResponse;
import com.example.librerianazareth.data.model.VentaRequest;
import com.example.librerianazareth.data.model.VentaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    // ============================================================
    // PRODUCTOS
    // ============================================================

    @GET("api/productos/")
    Call<List<ProductoResponse>> buscarProductoPorCodigo(
            @Query("codigo_barras") String codigoBarras
    );

    // ============================================================
    // RUBROS, MARCAS, USUARIOS, VENTAS...
    // Cada uno agrega lo suyo aca
    // ============================================================

    @POST("api/ventas/")
    Call<VentaResponse> registrarVenta(
            @Body VentaRequest venta
    );
}