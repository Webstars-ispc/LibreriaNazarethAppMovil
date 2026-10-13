package com.ispc.librerianazareth.data.repository;

import com.ispc.librerianazareth.data.ApiService;
import com.ispc.librerianazareth.data.model.LoginRequest;
import com.ispc.librerianazareth.data.model.LoginResponse;
import com.ispc.librerianazareth.data.model.UserProfileResponse;

import retrofit2.Call;

/**
 * Repositorio de autenticación.
 *
 * Recibe el ApiService por constructor: eso permite inyectar una implementación
 * real (Retrofit) en producción o MockWebServer / un mock en los tests.
 */
public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<LoginResponse> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password));
    }

    public Call<UserProfileResponse> getProfile(String accessToken) {
        return apiService.me("Bearer " + accessToken);
    }
}
