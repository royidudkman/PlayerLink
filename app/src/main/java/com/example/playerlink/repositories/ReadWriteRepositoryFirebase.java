package com.example.playerlink.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.playerlink.Result;
import com.example.playerlink.models.Chat;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ReadWriteRepository;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class ReadWriteRepositoryFirebase implements ReadWriteRepository {
    private final Executor executor;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference usersRef;
    private final DatabaseReference chatsRef;


    public ReadWriteRepositoryFirebase() {
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


    private String swapChatId(String originalChatId) {
        String[] userIds = originalChatId.split("_with_");
        String swappedChatId = userIds[1] + "_with_" + userIds[0];

        return swappedChatId;
    }

    @Override
    public void readMessages(String originalChatId, final RepositoryCallback<List<Message>> callback) {
        final String[] chatId = {originalChatId};

        chatsRef.child(originalChatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    chatId[0] = originalChatId;
                } else {

                    chatId[0] = swapChatId(originalChatId);
                }

                // Now that we have the correct chatId, retrieve messages
               // retrieveMessages(chatId[0], callback);
                observeMessages(chatId[0], callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void observeMessages(String chatId, final RepositoryCallback<List<Message>> callback) {
        chatsRef.child(chatId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Parse Message objects from DataSnapshot
                    Message message = messageSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                // Pass the list of all messages to the callback
                callback.onComplete(new Result.Success<>(messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }




    private void retrieveMessages(String chatId, final RepositoryCallback<List<Message>> callback) {
        chatsRef.child(chatId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Process the dataSnapshot to retrieve messages
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Parse Message objects from DataSnapshot
                    Message message = messageSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                // Pass the retrieved messages to the callback
                callback.onComplete(new Result.Success<>(messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    @Override
    public void sendMessage(String originalChatId, final Message message, final RepositoryCallback<String> callback) {
        // Check if the original chat ID exists
        chatsRef.child(originalChatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Original chat ID exists, use it
                    sendMessageToChat(originalChatId, message, callback);
                } else {
                    // Original chat ID doesn't exist, try swapping user IDs and check if the chat exists
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
                                    // Neither original nor swapped chat ID exists, create a new chat
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
        Chat chat = new Chat(); // Create new chat object if needed
        // Add other chat properties if necessary
        chatsRef.child(chatId).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Chat created successfully, now send the message
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

}
