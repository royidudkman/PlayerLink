package com.example.playerlink.all_users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ChatRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

public class AllUsersViewModel extends ViewModel {

    private ChatRepositoryFirebase mRepository;
    private MutableLiveData<List<User>> mUsersLiveData = new MutableLiveData<>();
//    private final MutableLiveData<User> mCurrentUserLiveData = new MutableLiveData<>();
    private User currentUser = LoginFragment.GetCurrentUser();

    public AllUsersViewModel() {
        mRepository = new ChatRepositoryFirebase(); // Using the ReadWriteRepositoryFirebase
//        fetchCurrentUser();
        fetchAllUsers();
    }

    public LiveData<List<User>> getUsersLiveData() {
        return mUsersLiveData;
    }
//    public LiveData<User> getCurrentUser() {
//        return mCurrentUserLiveData;
//    }


//    private void fetchCurrentUser() {
//        mRepository.getCurrentUser(new RepositoryCallback<User>() {
//            @Override
//            public void onComplete(Result<User> result) {
//                if (result instanceof Result.Success) {
//                    mCurrentUserLiveData.postValue(((Result.Success<User>) result).data);
//                } else {
//                    // Handle error
//                }
//            }
//        });
//    }

    private void fetchAllUsers() {
        mRepository.getAllUsers(new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if (result instanceof Result.Success) {
                    // Exclude current user from the list of users
//                    User currentUser = mCurrentUserLiveData.getValue();
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
