package com.example.playerlink.fragments.register_login_fragments;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.playerlink.repositories.AuthRepositoryFirebase;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepositoryFirebase authRepository;

    public ViewModelFactory(AuthRepositoryFirebase authRepository) {
        this.authRepository = authRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authRepository);
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
