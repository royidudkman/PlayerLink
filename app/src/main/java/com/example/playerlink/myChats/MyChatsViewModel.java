package com.example.playerlink.myChats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.playerlink.Result;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ChatRepositoryFirebase;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class MyChatsViewModel extends ViewModel {
    private ChatRepositoryFirebase chatRepository;
    private ProfileRepositoryFirebase profileRepository;
    private MutableLiveData<List<User>> mChatsWithUsersLiveData = new MutableLiveData<>();
    private User currentUser = LoginFragment.GetCurrentUser();


    public MyChatsViewModel() {
        chatRepository = new ChatRepositoryFirebase();
        profileRepository = new ProfileRepositoryFirebase();
        fetchChatsWithUsers(currentUser.getUserId());
    }

    public LiveData<List<User>> getChatsWithUsersLiveData() {
        return mChatsWithUsersLiveData;
    }


    public void fetchChatsWithUsers(String currentUserId) {
        profileRepository.getChatsAndUsers(currentUserId, new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if (result instanceof Result.Success) {
                    mChatsWithUsersLiveData.postValue(((Result.Success<List<User>>) result).data);
                } else {

                }
            }
        });
    }

}
