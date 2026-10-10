package com.example.librerianazareth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.model.ProductoResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductoApiTest {

    private MockWebServer servidor;
    private ApiService apiService;

    @Before
    public void preparar() throws IOException {
        servidor = new MockWebServer();
        servidor.start();

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(servidor.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @After
    public void cerrar() throws IOException {
        if (servidor != null) {
            servidor.shutdown();
        }
    }

    @Test
    public void respuesta200_convierteJsonEnProductos() throws Exception {
        String json = "{"
                + "\"count\":1,"
                + "\"next\":null,"
                + "\"previous\":null,"
                + "\"results\":[{"
                + "\"id\":1,"
                + "\"nombre\":\"Cuaderno\","
                + "\"descripcion\":\"Cuaderno escolar\","
                + "\"codigo_barras\":\"123456\","
                + "\"rubro\":1,"
                + "\"rubro_nombre\":\"Librería\","
                + "\"marca\":null,"
                + "\"marca_nombre\":null,"
                + "\"precio_costo\":1000.0,"
                + "\"precio_venta\":1500.0,"
                + "\"stock\":10"
                + "}]"
                + "}";

        servidor.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(json));

        Response<ProductoResponse> respuesta =
                apiService.getProductos(1).execute();

        assertEquals(200, respuesta.code());
        assertNotNull(respuesta.body());
        assertEquals(1, respuesta.body().getCount());
        assertNotNull(respuesta.body().getResults());
        assertFalse(respuesta.body().getResults().isEmpty());
        assertEquals("Cuaderno",
                respuesta.body().getResults().get(0).getNombre());

        assertEquals("/api/productos/?page=1",
                servidor.takeRequest().getPath());
    }

    @Test
    public void respuesta500_informaErrorDelServidor() throws Exception {
        servidor.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Error interno del servidor\"}"));

        Response<ProductoResponse> respuesta =
                apiService.getProductos(1).execute();

        assertEquals(500, respuesta.code());
        assertFalse(respuesta.isSuccessful());
        assertNotNull(respuesta.errorBody());
    }
}
