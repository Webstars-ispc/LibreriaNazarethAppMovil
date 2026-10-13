package com.example.librerianazareth.data;

import android.content.Context;
import androidx.annotation.NonNull;
import com.example.librerianazareth.data.local.TokenManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * Interceptor agrega la cabecera "Authorization: Bearer <token>" a cada petición,
 * siempre que haya un token guardado en TokenManager.
 * Si no hay token (ej. antes del login), no agrega nada y la petición sale como anónima.
 * Además, ante una respuesta 401, renueva el access token usando el refresh token
 * (POST api/auth/refresh/) y reintenta la petición original UNA sola vez.
 * NO modificar sin avisar al equipo.
 */
public class AuthInterceptor implements Interceptor {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final TokenManager tokenManager;
    private final OkHttpClient refreshClient;
    private final AtomicBoolean refreshing = new AtomicBoolean(false);

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
        // Cliente sin interceptor: evita recursión al renovar el token.
        this.refreshClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = tokenManager.getAccessToken();

        Request.Builder builder = original.newBuilder();

        if (original.header(NetworkConstants.HEADER_AUTHORIZATION) == null
                && token != null && !token.isEmpty()) {
            builder.header(
                    NetworkConstants.HEADER_AUTHORIZATION,
                    NetworkConstants.TOKEN_PREFIX + token
            );
        }

        Response response = chain.proceed(builder.build());

        if (response.code() == 401
                && !isRefreshRequest(original)
                && refreshing.compareAndSet(false, true)) {
            try {
                Response refreshed = refreshAccessToken();
                if (refreshed != null && refreshed.isSuccessful()) {
                    String newAccess = parseAccessToken(refreshed.body() != null
                            ? refreshed.body().string() : null);
                    refreshed.close();
                    if (newAccess != null) {
                        tokenManager.saveTokens(newAccess, tokenManager.getRefreshToken());
                        response.close();
                        return chain.proceed(original.newBuilder()
                                .header(
                                        NetworkConstants.HEADER_AUTHORIZATION,
                                        NetworkConstants.TOKEN_PREFIX + newAccess
                                )
                                .build());
                    }
                } else if (refreshed != null) {
                    refreshed.close();
                }
            } finally {
                refreshing.set(false);
            }
        }
        return response;
    }

    private boolean isRefreshRequest(Request request) {
        return request.url().encodedPath().endsWith("/api/auth/refresh/");
    }

    private Response refreshAccessToken() throws IOException {
        String refresh = tokenManager.getRefreshToken();
        if (refresh == null || refresh.isEmpty()) {
            return null;
        }
        String body;
        try {
            body = new JSONObject().put("refresh", refresh).toString();
        } catch (org.json.JSONException e) {
            return null;
        }
        Request request = new Request.Builder()
                .url(requestUrlFor(NetworkConstants.BASE_URL + "api/auth/refresh/"))
                .post(RequestBody.create(JSON, body))
                .build();
        return refreshClient.newCall(request).execute();
    }

    private String parseAccessToken(String json) {
        if (json == null) {
            return null;
        }
        try {
            return new JSONObject(json).optString("access", null);
        } catch (Exception e) {
            return null;
        }
    }

    private okhttp3.HttpUrl requestUrlFor(String url) {
        return okhttp3.HttpUrl.get(url);
    }
}