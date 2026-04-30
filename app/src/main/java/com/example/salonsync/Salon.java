package com.example.salonsync;

public class Salon {
    private String id;
    private String name;
    private String rating;
    private String imageUrl;

    public Salon(String id, String name, String rating, String imageUrl) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
}