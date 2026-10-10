package com.example.librerianazareth.data.repository;

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.model.LoginRequest;
import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.UserProfileResponse;

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

    public Call<UserProfileResponse> getProfile() {
        return apiService.me();
    }
}
