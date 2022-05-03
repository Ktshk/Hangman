package com.example.hangman;

public class User implements Comparable<User>{
    private String name;
    private int rating;

    public User(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public int compareTo(User user) {
        return -Integer.compare(rating, user.rating);
    }
}
