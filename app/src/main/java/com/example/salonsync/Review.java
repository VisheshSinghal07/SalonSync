package com.example.salonsync;

public class Review {
    private String userName;
    private String date;
    private String comment;
    private float rating;

    public Review(String userName, String date, String comment, float rating) {
        this.userName = userName;
        this.date = date;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUserName() { return userName; }
    public String getDate() { return date; }
    public String getComment() { return comment; }
    public float getRating() { return rating; }
}