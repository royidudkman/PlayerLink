package com.example.playerlink.all_users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ReadWriteRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

public class AllUsersViewModel extends ViewModel {

    private ReadWriteRepositoryFirebase mRepository;
    private MutableLiveData<List<User>> mUsersLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> mCurrentUserLiveData = new MutableLiveData<>();

    public AllUsersViewModel() {
        mRepository = new ReadWriteRepositoryFirebase(); // Using the ReadWriteRepositoryFirebase
        fetchCurrentUser();
        fetchAllUsers();
    }

    public LiveData<List<User>> getUsersLiveData() {
        return mUsersLiveData;
    }
    public LiveData<User> getCurrentUser() {
        return mCurrentUserLiveData;
    }
//    public LiveData<User> getCurrentUser() {
//        MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();
//        mRepository.getCurrentUser(new RepositoryCallback<User>() {
//            @Override
//            public void onComplete(Result<User> result) {
//                if (result instanceof Result.Success) {
//                    currentUserLiveData.postValue(((Result.Success<User>) result).data);
//                } else {
//                    // Handle error
//                }
//            }
//        });
//        return currentUserLiveData;
//    }


    private void fetchCurrentUser() {
        mRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                if (result instanceof Result.Success) {
                    mCurrentUserLiveData.postValue(((Result.Success<User>) result).data);
                } else {
                    // Handle error
                }
            }
        });
    }

    private void fetchAllUsers() {
        mRepository.getAllUsers(new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if (result instanceof Result.Success) {
                    // Exclude current user from the list of users
                    User currentUser = mCurrentUserLiveData.getValue();
                    if (currentUser != null) {
                        List<User> allUsers = ((Result.Success<List<User>>) result).data;
                        List<User> filteredUsers = new ArrayList<>();
                        for (User user : allUsers) {
                            if (!user.getUserId().equals(currentUser.getUserId())) {
                                filteredUsers.add(user);
                            }
                        }
                        mUsersLiveData.postValue(filteredUsers);
                    }
                } else {
                    // Handle error
                }
            }
        });
    }
}
