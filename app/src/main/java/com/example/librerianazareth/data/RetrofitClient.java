package com.example.librerianazareth.data;

import android.content.Context;
import com.example.librerianazareth.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionSpec;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Retrofit: Es la ÚNICA forma en que las Activities deben comunicarse con la API.
 *
 * Uso:
 *   RetrofitClient.getApi(this)
 *       .login(new LoginRequest(email, password))
 *       .enqueue(new Callback<LoginResponse>() { ... });
 *
 * ¿Qué hace?
 *   - Configura la URL base (desde BuildConfig.API_BASE_URL).
 *   - Agrega el token JWT a cada petición (vía AuthInterceptor).
 *   - Loguea peticiones y respuestas (solo en modo debug).
 *   - Serializa/deserializa JSON automáticamente (Gson).
 *
 * NO modificar sin avisar al equipo. En caso de conflicto AVISAR
 */
public final class RetrofitClient {

    private static volatile Retrofit retrofitInstance;
    private static volatile ApiService apiInstance;

    /** Constructor privado: no se instancia, es una clase utilitaria. */
    private RetrofitClient() { }

    /**
     * Devuelve la instancia única de ApiService.
     * @param context Context de la Activity (se usa getApplicationContext() internamente para evitar memory leaks).
     * @return ApiService listo para usar.
     */
    public static ApiService getApi(Context context) {
        if (apiInstance == null) {
            synchronized (RetrofitClient.class) {
                if (apiInstance == null) {
                    apiInstance = getRetrofit(context.getApplicationContext())
                            .create(ApiService.class);
                }
            }
        }
        return apiInstance;
    }

    private static Retrofit getRetrofit(Context context) {
        if (retrofitInstance == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = buildRetrofit(context);
                }
            }
        }
        return retrofitInstance;
    }

    private static Retrofit buildRetrofit(Context context) {
        // Logging: solo se activa en builds de debug
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        // Forzar TLS 1.2+ y pinning del certificado del backend en producción
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                .build();
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(NetworkConstants.PINNED_DOMAIN, NetworkConstants.CERT_SHA256_PIN)
                .build();

        // Cliente HTTP con interceptores y timeouts
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(java.util.Collections.singletonList(tlsSpec))
                .certificatePinner(certificatePinner)
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Instancia de Retrofit
        return new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}