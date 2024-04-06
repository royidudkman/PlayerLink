package com.example.playerlink.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;

    private List<Game> myGames;
    private List<User> myFriends;



    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String userName, String userEmail) {
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

    public List<Game> getMyGames() {
        return myGames;
    }

    public void setMyGames(List<Game> myGames) {
        this.myGames = myGames;
    }

    public List<User> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(List<User> myFriends) {
        this.myFriends = myFriends;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
