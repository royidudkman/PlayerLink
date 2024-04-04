package com.example.playerlink.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.playerlink.Result;
import com.example.playerlink.models.Message;
import com.example.playerlink.repositories.ReadWriteRepository;
import com.example.playerlink.repositories.ReadWriteRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ReadWriteRepository repository;
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageIdLiveData = new MutableLiveData<>();

    public ChatViewModel() {
        repository = new ReadWriteRepositoryFirebase();
    }

    public LiveData<List<Message>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<String> getMessageIdLiveData() {
        return messageIdLiveData;
    }

    public void readMessages(String chatId) {
        repository.readMessages(chatId, new RepositoryCallback<List<Message>>() {
            @Override
            public void onComplete(Result<List<Message>> result) {
                if (result instanceof Result.Success) {
                    messagesLiveData.postValue(((Result.Success<List<Message>>) result).data);
                } else {
                    // Handle error
                }
            }
        });
    }

    public void sendMessage(String chatId, Message message) {
        repository.sendMessage(chatId, message, new RepositoryCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                if (result instanceof Result.Success) {
                    messageIdLiveData.postValue(((Result.Success<String>) result).data);
                    readMessages(chatId);
                } else {
                    // Handle error
                }
            }
        });
    }



}
