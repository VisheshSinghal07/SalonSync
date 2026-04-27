package com.example.salonsync;

public class TimeSlot {
    private String time;
    private boolean isAvailable;
    private boolean isSelected;

    public TimeSlot(String time, boolean isAvailable) {
        this.time = time;
        this.isAvailable = isAvailable;
        this.isSelected = false;
    }

    public String getTime() { return time; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}