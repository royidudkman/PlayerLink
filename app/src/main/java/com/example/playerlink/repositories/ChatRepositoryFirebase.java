package com.example.playerlink.repositories;

import androidx.annotation.NonNull;

import com.example.playerlink.Result;
import com.example.playerlink.models.Chat;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepositoryFirebase implements ChatRepository {
    private final Executor executor;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference usersRef;
    private final DatabaseReference chatsRef;


    public ChatRepositoryFirebase() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        this.executor = executorService;
        usersRef = database.getReference("users");
        chatsRef = database.getReference("chats");
    }

    @Override
    public void getCurrentUser(final RepositoryCallback<User> callback) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        callback.onComplete(new Result.Success<>(user));
                    } else {
                        callback.onComplete(new Result.Error<>(new Exception("User data not found")));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onComplete(new Result.Error<>(databaseError.toException()));
                }
            });
        } else {
            callback.onComplete(new Result.Error<>(new Exception("Firebase user is null")));
        }
    }

    @Override
    public void getAllUsers(final RepositoryCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                callback.onComplete(new Result.Success<>(users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }


    @Override
    public void readMessages(String chatId, final RepositoryCallback<List<Message>> callback) {
        chatsRef.child(chatId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                    Message message = messageSnapshot.getValue(Message.class);
                    messages.add(message);
                }

                callback.onComplete(new Result.Success<>(messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }

    private void retrieveMessages(String chatId, final RepositoryCallback<List<Message>> callback) {
        chatsRef.child(chatId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                    Message message = messageSnapshot.getValue(Message.class);
                    messages.add(message);
                }

                callback.onComplete(new Result.Success<>(messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void sendMessage(String originalChatId, final Message message, final RepositoryCallback<String> callback) {

        chatsRef.child(originalChatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    sendMessageToChat(originalChatId, message, callback);
                } else {

                    String[] userIds = originalChatId.split("_with_");
                    if (userIds.length == 2) {
                        String swappedChatId = userIds[1] + "_with_" + userIds[0];
                        chatsRef.child(swappedChatId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Swapped chat ID exists, use it
                                    sendMessageToChat(swappedChatId, message, callback);
                                } else {

                                    String newChatId = originalChatId;
                                    createNewChat(newChatId, message, callback);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.onComplete(new Result.Error<>(databaseError.toException()));
                            }
                        });
                    } else {
                        callback.onComplete(new Result.Error<>(new Exception("Invalid chat ID format")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }

    private void sendMessageToChat(String chatId, final Message message, final RepositoryCallback<String> callback) {
        DatabaseReference chatRef = chatsRef.child(chatId).child("messages");
        String messageId = chatRef.push().getKey();
        message.setMessageId(messageId);
        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onComplete(new Result.Success<>(messageId));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onComplete(new Result.Error<>(e));
                    }
                });
    }

    private void createNewChat(String chatId, final Message message, final RepositoryCallback<String> callback) {
        Chat chat = new Chat();

        chatsRef.child(chatId).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMessageToChat(chatId, message, callback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onComplete(new Result.Error<>(e));
                    }
                });
    }

    @Override
    public void getLastMessage(String chatId, final RepositoryCallback<Message> callback) {

        DatabaseReference messagesRef = chatsRef.child(chatId).child("messages");

        messagesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        callback.onComplete(new Result.Success<>(message));
                        return;
                    }
                } else {
                    callback.onComplete(new Result.Error<>(new Exception("No messages found")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }

    @Override
    public void deleteMessage(String chatId, Message message, final RepositoryCallback<Boolean> callback) {
        DatabaseReference messageRef = chatsRef.child(chatId).child("messages").child(message.getMessageId());
        message.setMessageText("This message was deleted");
        messageRef.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onComplete(new Result.Success<>(true));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onComplete(new Result.Error<>(e));
                    }
                });
    }

}
