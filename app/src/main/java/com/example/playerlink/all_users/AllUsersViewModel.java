package com.example.playerlink.all_users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ReadWriteRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.util.List;

public class AllUsersViewModel extends ViewModel {

    private ReadWriteRepositoryFirebase mRepository;
    private MutableLiveData<List<User>> mUsersLiveData = new MutableLiveData<>();

    public AllUsersViewModel() {
        mRepository = new ReadWriteRepositoryFirebase(); // Using the ReadWriteRepositoryFirebase
        fetchAllUsers();
    }

    public LiveData<List<User>> getUsersLiveData() {
        return mUsersLiveData;
    }
    public LiveData<User> getCurrentUser() {
        MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();
        mRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                if (result instanceof Result.Success) {
                    currentUserLiveData.postValue(((Result.Success<User>) result).data);
                } else {
                    // Handle error
                }
            }
        });
        return currentUserLiveData;
    }


    private void fetchAllUsers() {
        mRepository.getAllUsers(new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if (result instanceof Result.Success) {
                    mUsersLiveData.postValue(((Result.Success<List<User>>) result).data);
                } else {
                    // Handle error
                }
            }
        });
    }
}
