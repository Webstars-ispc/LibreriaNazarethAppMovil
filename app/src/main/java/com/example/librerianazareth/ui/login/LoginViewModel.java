package com.example.librerianazareth.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.UserProfileResponse;
import com.example.librerianazareth.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel de la pantalla de login.
 *
 * Recibe el AuthRepository por constructor (sin Context) para poder testearlo
 * con Mockito en la capa de tests unitarios.
 */
public class LoginViewModel extends ViewModel {

    public enum Status { IDLE, LOADING, SUCCESS, ERROR }

    /** Estado inmutable que observa la Activity. */
    public static class LoginUiState {
        public final Status status;
        public final String message;
        public final String accessToken;
        public final String refreshToken;
        public final String username;
        public final String role;

        private LoginUiState(Status status, String message,
                             String accessToken, String refreshToken,
                             String username, String role) {
            this.status = status;
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.username = username;
            this.role = role;
        }

        static LoginUiState idle() {
            return new LoginUiState(Status.IDLE, null, null, null, null, null);
        }

        static LoginUiState loading() {
            return new LoginUiState(Status.LOADING, null, null, null, null, null);
        }

        static LoginUiState error(String message) {
            return new LoginUiState(Status.ERROR, message, null, null, null, null);
        }

        static LoginUiState success(String access, String refresh, String username, String role) {
            return new LoginUiState(Status.SUCCESS, null, access, refresh, username, role);
        }
    }

    private final AuthRepository repository;
    private final MutableLiveData<LoginUiState> state = new MutableLiveData<>(LoginUiState.idle());

    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<LoginUiState> getState() {
        return state;
    }

    /**
     * Valida los campos y, si están ok, inicia sesión y trae el perfil.
     */
    public void login(String email, String password) {
        String validationError = LoginValidator.validate(email, password);
        if (validationError != null) {
            state.setValue(LoginUiState.error(validationError));
            return;
        }

        state.setValue(LoginUiState.loading());

        repository.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();
                    cargarPerfil(body.getAccess(), body.getRefresh());
                } else {
                    state.setValue(LoginUiState.error("Credenciales inválidas"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,
                                  @NonNull Throwable t) {
                state.setValue(LoginUiState.error("Error de red: " + t.getMessage()));
            }
        });
    }

    private void cargarPerfil(final String access, final String refresh) {
        repository.getProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call,
                                   @NonNull Response<UserProfileResponse> response) {
                String username = "";
                String role = "";
                if (response.isSuccessful() && response.body() != null) {
                    username = response.body().getUsername();
                    role = response.body().getRole();
                }
                state.setValue(LoginUiState.success(access, refresh, username, role));
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call,
                                  @NonNull Throwable t) {
                // El login ya fue exitoso: se continúa sin datos de perfil.
                state.setValue(LoginUiState.success(access, refresh, "", ""));
            }
        });
    }
}
