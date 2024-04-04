package com.example.playerlink.models;

public class Chat {
    private String chatId;
    private String otherUserId;
    private String lastMessage;
    private long timestamp;

    public Chat() {
        // Default constructor required for Firebase
    }

    public Chat(String otherUserId, String lastMessage, long timestamp) {
        this.otherUserId = otherUserId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
