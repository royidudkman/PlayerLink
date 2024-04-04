package com.example.playerlink.fragments.register_login_fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

public class RegisterViewModel extends ViewModel {
    private AuthRepositoryFirebase registerRep;
    private MutableLiveData<Result<User>> registerResult = new MutableLiveData<>();

    public RegisterViewModel(AuthRepositoryFirebase loginRep){
        this.registerRep = loginRep;
    }

    public void Register(String email, String password, String displayName) {
        registerResult.postValue(new Result.Loading<>(null));

        registerRep.Register(email, password, displayName, new RepositoryCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                registerResult.postValue(result);
            }
        });
    }
    public LiveData<Result<User>> getRegisterResult() {
        return registerResult;
    }
}
