package com.example.salonsync;

public class Salon {
    private String name;
    private String rating;
    private String distance;

    public Salon(String name, String rating, String distance) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
    }

    public String getName() { return name; }
    public String getRating() { return rating; }
    public String getDistance() { return distance; }
}