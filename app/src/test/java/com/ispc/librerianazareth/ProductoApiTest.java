package com.ispc.librerianazareth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ispc.librerianazareth.data.ApiService;
import com.ispc.librerianazareth.data.model.ProductoResponse;
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
                apiService.getProductosPaginados(1, null).execute();

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
                apiService.getProductosPaginados(1, null).execute();

        assertEquals(500, respuesta.code());
        assertFalse(respuesta.isSuccessful());
        assertNotNull(respuesta.errorBody());
    }

    // AUT-NET-03: un endpoint protegido sin credencial válida devuelve 401.
    @Test
    public void respuesta401_sinCredencial_informaNoAutorizado() throws Exception {
        servidor.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"detail\":\"Authentication credentials were not provided.\"}"));

        Response<ProductoResponse> respuesta =
                apiService.getProductosPaginados(1, null).execute();

        assertEquals(401, respuesta.code());
        assertFalse(respuesta.isSuccessful());
        assertNotNull(respuesta.errorBody());
    }

    // AUT-NET-04: una respuesta con JSON inválido no debe romper silenciosamente.
    @Test
    public void jsonMalformado_lanzaExcepcionDeParseo() throws Exception {
        servidor.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"count\":1,\"results\":[{\"nombre\":"));

        boolean lanzoExcepcion = false;
        try {
            apiService.getProductosPaginados(1, null).execute();
        } catch (Exception e) {
            lanzoExcepcion = true;
        }

        assertTrue("Un JSON malformado debe lanzar excepción de parseo", lanzoExcepcion);
    }

    // AUT-NET-05: la búsqueda por código de barras envía la query correcta al servidor.
    @Test
    public void busquedaPorCodigo_enviaQueryCorrecta() throws Exception {
        String json = "{"
                + "\"count\":1,"
                + "\"next\":null,"
                + "\"previous\":null,"
                + "\"results\":[{"
                + "\"id\":7,"
                + "\"nombre\":\"Marcador\","
                + "\"descripcion\":null,"
                + "\"codigo_barras\":\"999888\","
                + "\"rubro\":1,"
                + "\"rubro_nombre\":\"Librería\","
                + "\"marca\":null,"
                + "\"marca_nombre\":null,"
                + "\"precio_costo\":100.0,"
                + "\"precio_venta\":200.0,"
                + "\"stock\":5"
                + "}]"
                + "}";

        servidor.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(json));

        Response<ProductoResponse> respuesta =
                apiService.buscarProductoPorCodigo("999888").execute();

        assertEquals(200, respuesta.code());
        assertNotNull(respuesta.body());
        assertEquals("Marcador", respuesta.body().getResults().get(0).getNombre());
        assertEquals("/api/productos/?codigo_barras=999888",
                servidor.takeRequest().getPath());
    }
}
