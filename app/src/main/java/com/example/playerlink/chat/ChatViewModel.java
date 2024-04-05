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

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ReadWriteRepository repository;
    private final DatabaseReference chatsRef;
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isChatInitialized = new MutableLiveData<>(false);
    private ChildEventListener childEventListener;

    public ChatViewModel() {
        repository = new ReadWriteRepositoryFirebase();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("chats");
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
                    observeMessages(chatId, null);
                } else {
                    // Handle error
                }
            }
        });
    }

    public LiveData<Boolean> getIsChatInitialized() {
        return isChatInitialized;
    }
    public void initializeChat(String chatId) {
        if (!isChatInitialized.getValue()) {
            observeMessages(chatId, null);
            isChatInitialized.postValue(true);
        }
    }

    private void observeMessages(String chatId, final RepositoryCallback<List<Message>> callback) {

        if (childEventListener == null) {
            childEventListener = chatsRef.child(chatId).child("messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // A new message has been added to the chat
                    Message message = dataSnapshot.getValue(Message.class);
                    List<Message> messages = messagesLiveData.getValue();
                    if (messages == null) {
                        messages = new ArrayList<>();
                    }
                    messages.add(message);
                    messagesLiveData.postValue(messages);
                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // Handle message changed if needed
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // Handle message removed if needed
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // Handle message moved if needed
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled if needed
                    callback.onComplete(new Result.Error<>(databaseError.toException()));
                }
            });
        }


    }



    @Override
    protected void onCleared() {
        super.onCleared();
        if (childEventListener != null) {
            // Remove the ChildEventListener
            chatsRef.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

}
