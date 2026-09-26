package com.example.librerianazareth.data;

import android.content.Context;
import androidx.annotation.NonNull;
import com.example.librerianazareth.data.local.TokenManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor agrega la cabecera "Authorization: Bearer <token>" a cada petición,siempre que haya un token guardado en TokenManager.
 * Si no hay token (ej. antes del login), no agrega nada y la petición sale como anónima.
 * NO modificar sin avisar al equipo.
 */
public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = tokenManager.getAccessToken();

        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        if (token != null && !token.isEmpty()) {
            builder.header(
                    NetworkConstants.HEADER_AUTHORIZATION,
                    NetworkConstants.TOKEN_PREFIX + token
            );
        }

        return chain.proceed(builder.build());
    }
}