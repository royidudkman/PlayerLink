package com.example.playerlink.models;

import java.util.List;

public class Game {
    private String name;
    private double rating;
    private String image;

    public Game(String name, double rating, String image) {
        this.name = name;
        this.rating = rating;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }
}
