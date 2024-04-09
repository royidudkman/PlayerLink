package com.example.playerlink.models;

public class Message {
    private String messageId;
    private String messageText;
    private String senderId;
    private long timestamp;

    public Message() {

    }

    public Message(String messageId, String messageText, String senderId, long timestamp) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
