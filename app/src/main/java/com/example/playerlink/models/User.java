package com.example.playerlink.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.playerlink.R;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;
    private List<String> myGames;
    private String imageString = null;


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

    public List<String> getMyGames() {
        return myGames;
    }

    public void setMyGames(List<String> myGames) {
        this.myGames = myGames;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public Bitmap getUserImage() {
        if (imageString == null) {
            return null;
        }

        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void setUserImage(Bitmap image) {
        if (image == null) {
            this.imageString = null;
            return;
        }

        Bitmap resizedImage = Bitmap.createScaledBitmap(image, 500, 500, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        this.imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

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
