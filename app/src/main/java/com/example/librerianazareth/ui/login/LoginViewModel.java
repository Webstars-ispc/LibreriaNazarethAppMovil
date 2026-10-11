
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

public class LoginViewModel extends ViewModel {

    public enum Status {
        IDLE, LOADING, SUCCESS, ERROR
    }

    public static class LoginUiState {
        public final Status status;
        public final String message;
        public final String accessToken;
        public final String refreshToken;
        public final String username;
        public final String role;

        private LoginUiState(
                Status status,
                String message,
                String accessToken,
                String refreshToken,
                String username,
                String role
        ) {
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

        static LoginUiState success(
                String access,
                String refresh,
                String username,
                String role
        ) {
            return new LoginUiState(
                    Status.SUCCESS, null, access, refresh, username, role
            );
        }
    }

    private final AuthRepository repository;
    private final MutableLiveData<LoginUiState> state =
            new MutableLiveData<>(LoginUiState.idle());

    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<LoginUiState> getState() {
        return state;
    }

    public void login(String email, String password) {
        String validationError = LoginValidator.validate(email, password);

        if (validationError != null) {
            state.setValue(LoginUiState.error(validationError));
            return;
        }

        state.setValue(LoginUiState.loading());

        repository.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<LoginResponse> call,
                    @NonNull Response<LoginResponse> response
            ) {
                if (!response.isSuccessful() || response.body() == null) {
                    state.setValue(LoginUiState.error("Credenciales inválidas"));
                    return;
                }

                LoginResponse body = response.body();
                String access = body.getAccess();
                String refresh = body.getRefresh();

                if (access == null || access.isEmpty()
                        || refresh == null || refresh.isEmpty()) {
                    state.setValue(LoginUiState.error(
                            "El servidor no devolvió los tokens de acceso."
                    ));
                    return;
                }

                cargarPerfil(access, refresh);
            }

            @Override
            public void onFailure(
                    @NonNull Call<LoginResponse> call,
                    @NonNull Throwable t
            ) {
                state.setValue(LoginUiState.error(
                        "Error de red al iniciar sesión: " + t.getMessage()
                ));
            }
        });
    }

    private void cargarPerfil(final String access, final String refresh) {

        // Consulta el perfil usando el token recién recibido.
        repository.getProfile(access).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<UserProfileResponse> call,
                    @NonNull Response<UserProfileResponse> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse perfil = response.body();

                    String username = perfil.getUsername();
                    String role = perfil.getRole();

                    android.util.Log.d(
                            "ROL_APP",
                            "Perfil recibido. Rol: " + role
                    );

                    if (role == null || role.trim().isEmpty()) {
                        state.setValue(LoginUiState.error(
                                "El perfil no contiene un rol de usuario."
                        ));
                        return;
                    }

                    state.setValue(LoginUiState.success(
                            access, refresh, username, role
                    ));
                } else {
                    android.util.Log.e(
                            "ROL_APP",
                            "No se pudo cargar el perfil. HTTP: " + response.code()
                    );

                    state.setValue(LoginUiState.error(
                            "No se pudo cargar el perfil. Código HTTP: "
                                    + response.code()
                    ));
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<UserProfileResponse> call,
                    @NonNull Throwable t
            ) {
                android.util.Log.e(
                        "ROL_APP",
                        "Error de conexión al consultar el perfil",
                        t
                );

                state.setValue(LoginUiState.error(
                        "Error de conexión al consultar el perfil."
                ));
            }
        });
    }
}
