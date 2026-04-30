package com.example.salonsync;

public class User {
    public String name, phone, city, gender, password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String phone, String city, String gender, String password) {
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.gender = gender;
        this.password = password;
    }
}