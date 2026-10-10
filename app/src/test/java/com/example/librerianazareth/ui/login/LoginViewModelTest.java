package com.example.librerianazareth.ui.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.librerianazareth.data.model.LoginResponse;
import com.example.librerianazareth.data.model.UserProfileResponse;
import com.example.librerianazareth.data.repository.AuthRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tests del LoginViewModel con el repositorio reemplazado por un @Mock.
 *
 * AUT-UNIT-02
 */
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository repository;
    @Mock
    private Call<LoginResponse> loginCall;
    @Mock
    private Call<UserProfileResponse> profileCall;

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new LoginViewModel(repository);
    }

    @Test
    public void credencialesValidas_cargaPerfil_estadoSuccess() {
        // Arrange
        when(repository.login(anyString(), anyString())).thenReturn(loginCall);
        when(repository.getProfile()).thenReturn(profileCall);

        LoginResponse loginResponse = mock(LoginResponse.class);
        when(loginResponse.getAccess()).thenReturn("access-token");
        when(loginResponse.getRefresh()).thenReturn("refresh-token");

        UserProfileResponse profile = mock(UserProfileResponse.class);
        when(profile.getUsername()).thenReturn("Juan");
        when(profile.getRole()).thenReturn("Empleado");

        // Act
        viewModel.login("juan@librerianazareth.com", "Juan2026!");

        ArgumentCaptor<Callback<LoginResponse>> loginCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(loginCall).enqueue(loginCaptor.capture());
        loginCaptor.getValue().onResponse(loginCall, Response.success(loginResponse));

        ArgumentCaptor<Callback<UserProfileResponse>> profileCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(profileCall).enqueue(profileCaptor.capture());
        profileCaptor.getValue().onResponse(profileCall, Response.success(profile));

        // Assert
        LoginViewModel.LoginUiState state = viewModel.getState().getValue();
        assertNotNull(state);
        assertEquals(LoginViewModel.Status.SUCCESS, state.status);
        assertEquals("access-token", state.accessToken);
        assertEquals("refresh-token", state.refreshToken);
        assertEquals("Juan", state.username);
        assertEquals("Empleado", state.role);
    }

    @Test
    public void fallaDeRed_estadoErrorYNoConsultaPerfil() {
        // Arrange
        when(repository.login(anyString(), anyString())).thenReturn(loginCall);

        // Act
        viewModel.login("juan@librerianazareth.com", "Juan2026!");

        ArgumentCaptor<Callback<LoginResponse>> loginCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(loginCall).enqueue(loginCaptor.capture());
        loginCaptor.getValue().onFailure(loginCall, new IOException("timeout"));

        // Assert
        assertEquals(LoginViewModel.Status.ERROR, viewModel.getState().getValue().status);
        verify(repository, never()).getProfile();
    }

    @Test
    public void credencialesRechazadas_estadoError() {
        // Arrange
        when(repository.login(anyString(), anyString())).thenReturn(loginCall);

        // Act
        viewModel.login("juan@librerianazareth.com", "Juan2026!");

        ArgumentCaptor<Callback<LoginResponse>> loginCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(loginCall).enqueue(loginCaptor.capture());
        loginCaptor.getValue().onResponse(loginCall, Response.success(null));

        // Assert
        assertEquals(LoginViewModel.Status.ERROR, viewModel.getState().getValue().status);
    }

    @Test
    public void camposVacios_estadoErrorSinLlamarAlRepositorio() {
        // Arrange (email y password vacíos)

        // Act
        viewModel.login("", "");

        // Assert
        assertEquals(LoginViewModel.Status.ERROR, viewModel.getState().getValue().status);
        verifyNoInteractions(repository);
    }
}
