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
    public void updateUserImage(String userId, String imageString, RepositoryCallback<Void> callback) {
        usersRef.child(userId).child("imageString").setValue(imageString)
                .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
    }



    public void getChatsAndUsers(String currentUserId, RepositoryCallback<List<User>> callback) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> otherUserIds = new ArrayList<>();


                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();

                    if (chatId.contains(currentUserId)) {

                        String[] userIds = chatId.split("_with_");
                        if (userIds.length == 2) {
                            String otherUserId = userIds[0].equals(currentUserId) ? userIds[1] : userIds[0];
                            otherUserIds.add(otherUserId);
                        }
                    }
                }


                List<User> users = new ArrayList<>();
                for (String userId : otherUserIds) {
                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                users.add(user);

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
    public void updateGamesList(String userId, List<String> games, RepositoryCallback<Void> callback) {

        if(games.isEmpty()){
            usersRef.child(userId).child("myGames").removeValue()
                    .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                    .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
        } else{
            usersRef.child(userId).child("myGames").setValue(games)
                    .addOnSuccessListener(aVoid -> callback.onComplete(new Result.Success<>(null)))
                    .addOnFailureListener(e -> callback.onComplete(new Result.Error<>(e)));
        }

    }

    @Override
    public void getGamesList(String userId, RepositoryCallback<List<String>> callback) {
        usersRef.child(userId).child("myGames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> games = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String game = snapshot.getValue(String.class);
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

    @Override
    public void getUsersByGame(String game, String currentUserId, RepositoryCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        if (user.getMyGames() != null && user.getMyGames().contains(game)) {
                            users.add(user);
                        } else if (user.getMyGames() == null && game.equals("Filter by game")) {
                            users.add(user);
                        }
                    }
                }
                callback.onComplete(new Result.Success<>(users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(new Result.Error<>(databaseError.toException()));
            }
        });
    }


}
