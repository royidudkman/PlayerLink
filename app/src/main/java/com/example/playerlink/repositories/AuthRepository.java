package com.example.playerlink.repositories;

import com.example.playerlink.models.User;

public interface AuthRepository {

    public void CurrentUser(final RepositoryCallback<User> callback);
    void Login(String email, String password, final RepositoryCallback<User> callback);

    public void Register(String email, String password, String displayName, final RepositoryCallback<User> callback);

    public void logout();
}
