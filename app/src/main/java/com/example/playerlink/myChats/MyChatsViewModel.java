package com.example.playerlink.myChats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.playerlink.Result;
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
    private final MutableLiveData<User> mCurrentUserLiveData = new MutableLiveData<>();

    public MyChatsViewModel() {
        chatRepository = new ChatRepositoryFirebase();
        profileRepository = new ProfileRepositoryFirebase();
        fetchCurrentUser();
    }

    public LiveData<List<User>> getChatsWithUsersLiveData() {
        return mChatsWithUsersLiveData;
    }
    public LiveData<User> getCurrentUser() {
        return mCurrentUserLiveData;
    }


    private void fetchCurrentUser() {
        chatRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                if (result instanceof Result.Success) {
                    mCurrentUserLiveData.postValue(((Result.Success<User>) result).data);
                    // Fetch chats with users only when current user data is available
                    fetchChatsWithUsers(((Result.Success<User>) result).data.getUserId());
                } else {
                    // Handle error
                }
            }
        });
    }

    public void fetchChatsWithUsers(String currentUserId) {
        profileRepository.getChatsAndUsers(currentUserId, new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if (result instanceof Result.Success) {
                    mChatsWithUsersLiveData.postValue(((Result.Success<List<User>>) result).data);
                } else {
                    // Handle error
                }
            }
        });
    }

}
