package com.example.playerlink.fragments.register_login_fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

public class LoginViewModel extends ViewModel {
    private AuthRepositoryFirebase loginRep;
    private final MutableLiveData<Result<User>> userSignInStatus = new MutableLiveData<>();
    private final MutableLiveData<Result<User>> currentUser = new MutableLiveData<>();



    public LoginViewModel(AuthRepositoryFirebase loginRep) {
        this.loginRep = loginRep;
        fetchCurrentUser();
    }

    private void fetchCurrentUser() {
        currentUser.postValue(new Result.Loading<>(null));
        loginRep.CurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                currentUser.postValue(result);
            }
        });
    }
    public void Login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            userSignInStatus.postValue(new Result.Error<>(new Exception("Empty email or password")));
        } else {
            userSignInStatus.postValue(new Result.Loading<>(null));
            loginRep.Login(email, password, new RepositoryCallback<User>() {
                @Override
                public void onComplete(Result<User> result) {
                    userSignInStatus.postValue(result);
                    if (result instanceof Result.Success) {
                        fetchCurrentUser();
                    }
                }
            });
        }
    }
    public LiveData<Result<User>> getUserSignInStatus() {
        return userSignInStatus;
    }
    public LiveData<Result<User>> getCurrentUser() {
        return currentUser;
    }

}
