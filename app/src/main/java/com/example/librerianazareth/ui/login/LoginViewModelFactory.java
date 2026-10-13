package com.example.librerianazareth.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.librerianazareth.data.repository.AuthRepository;

/**
 * Fábrica que inyecta el AuthRepository en el LoginViewModel
 * (el ViewModel no tiene constructor sin argumentos).
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepository repository;

    public LoginViewModelFactory(AuthRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(repository);
        }
        throw new IllegalArgumentException("ViewModel desconocido: " + modelClass.getName());
    }
}
