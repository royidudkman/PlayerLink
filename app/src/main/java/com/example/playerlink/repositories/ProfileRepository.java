package com.example.playerlink.repositories;

import com.example.playerlink.models.Game;
import com.example.playerlink.models.User;

import java.util.List;

public interface ProfileRepository {

    public void changeUsername(String userId, String newUsername, RepositoryCallback<Void> callback);
    public void getFriendsList(String userId, RepositoryCallback<List<User>> callback);
    public void getChatsAndUsers(String currentUserId, RepositoryCallback<List<User>> callback);
    public void updateGamesList(String userId, List<Game> games, RepositoryCallback<Void> callback);
    public void getGamesList(String userId, RepositoryCallback<List<Game>> callback);
}
