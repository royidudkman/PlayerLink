package com.example.playerlink.repositories;

import com.example.playerlink.models.Game;
import com.example.playerlink.models.User;

import java.util.List;

public interface ProfileRepository {

    public void changeUsername(String userId, String newUsername, RepositoryCallback<Void> callback);
    public void updateUserImage(String userId, String imageString, RepositoryCallback<Void> callback);
    public void getChatsAndUsers(String currentUserId, RepositoryCallback<List<User>> callback);
    public void updateGamesList(String userId, List<String> games, RepositoryCallback<Void> callback);
    public void getGamesList(String userId, RepositoryCallback<List<String>> callback);
    public void getUsersByGame(String game, String currentUserId, RepositoryCallback<List<User>> callback);
}
