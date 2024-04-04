package com.example.playerlink.repositories;

import com.example.playerlink.models.Chat;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;

import java.util.List;

public interface ReadWriteRepository {

    public void getAllUsers(final RepositoryCallback<List<User>> callback);

    public void readMessages(String chatId, final RepositoryCallback<List<Message>> callback);
    public void sendMessage(String chatId, Message message, final RepositoryCallback<String> callback);
}
