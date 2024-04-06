package com.example.playerlink.repositories;

import androidx.annotation.NonNull;

import com.example.playerlink.Result;
import com.example.playerlink.models.Game;
import com.example.playerlink.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileRepositoryFirebase implements ProfileRepository {

    private final Executor executor;
    private final DatabaseReference usersRef;

    public ProfileRepositoryFirebase() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        this.executor = executorService;
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public void changeUsername(String userId, String newUsername, RepositoryCallback<Void> callback) {
        usersRef.child(userId).child("userName").setValue(newUsername)
                .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
    }

    @Override
    public void getFriendsList(String userId, RepositoryCallback<List<User>> callback) {
        usersRef.child(userId).child("myFriends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> friends = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User friend = snapshot.getValue(User.class);
                    friends.add(friend);
                }
                callback.onComplete(new Result.Success<>(friends));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }

    public void getChatsAndUsers(String currentUserId, RepositoryCallback<List<User>> callback) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> otherUserIds = new ArrayList<>();

                // Loop through all chat IDs
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    // Check if the chat ID contains the current user's ID
                    if (chatId.contains(currentUserId)) {
                        // Extract the other user's ID from the chat ID
                        String[] userIds = chatId.split("_with_");
                        if (userIds.length == 2) {
                            String otherUserId = userIds[0].equals(currentUserId) ? userIds[1] : userIds[0];
                            otherUserIds.add(otherUserId);
                        }
                    }
                }

                // Fetch user data for each other user ID
                List<User> users = new ArrayList<>();
                for (String userId : otherUserIds) {
                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                users.add(user);
                                // Check if all users have been fetched
                                if (users.size() == otherUserIds.size()) {
                                    callback.onComplete(new Result.Success<>(users));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onComplete(new Result.Error<>(databaseError.toException()));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }


    private void updateUser(String userId, DatabaseReference userRef, RepositoryCallback<Void> callback) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    // Update the user's data
                    // For example, if myFriends list is updated, set it back to Firebase
                    userRef.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                            .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
                } else {
                    callback.onComplete(new Result.Error<>(new Exception("User not found")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }


    @Override
    public void updateGamesList(String userId, List<Game> games, RepositoryCallback<Void> callback) {
        // Update games list in Firebase
        usersRef.child(userId).child("myGames").setValue(games)
                .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
    }

    @Override
    public void getGamesList(String userId, RepositoryCallback<List<Game>> callback) {
        usersRef.child(userId).child("myGames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Game> games = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game game = snapshot.getValue(Game.class);
                    games.add(game);
                }
                callback.onComplete(new Result.Success<>(games));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }
}
