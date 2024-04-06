package com.example.playerlink.models;

public class UserDTO {
    private String userId;
    private String userName;
    private String userEmail;

    public UserDTO() {
        // Default constructor required for Firebase
    }

    public UserDTO(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
// Getters and setters
}
