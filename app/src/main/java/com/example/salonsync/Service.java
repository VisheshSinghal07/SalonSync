package com.example.salonsync;

public class Service {
    private String name;
    private String duration;
    private int price;
    private boolean isSelected;

    public Service(String name, String duration, int price) {
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.isSelected = false;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public int getPrice() { return price; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}