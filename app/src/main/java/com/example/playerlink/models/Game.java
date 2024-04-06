package com.example.playerlink.models;

import java.util.List;

public class Game {
    private String name;
    private double rating;
    private String image;
    private boolean selected = false;

    public Game(String name, double rating, String image) {
        this.name = name;
        this.rating = rating;
        this.image = image;
    }

    public Game(){

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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected(){
        return selected;
    }
}
