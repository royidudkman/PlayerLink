package com.example.playerlink.repositories;

import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;

import java.util.List;

public interface ChatRepository {
    public void getCurrentUser(final RepositoryCallback<User> callback);
    public void getAllUsers(final RepositoryCallback<List<User>> callback);

    public void readMessages(String chatId, final RepositoryCallback<List<Message>> callback);
    public void sendMessage(String chatId, Message message, final RepositoryCallback<String> callback);
    public void getLastMessage(String chatId, final RepositoryCallback<Message> callback);
}
