package com.example.salonsync;

public class Booking {
    private String bookingId;
    private String userId;
    private String userName;
    private String salonId;
    private String salonName;
    private String salonAddress;
    private String service;
    private String dateTime;
    private String price;
    private String status;

    public Booking() {
        // Default constructor for Firebase
    }

    public Booking(String bookingId, String userId, String userName, String salonId, String salonName, String salonAddress, String service, String dateTime, String price, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userName = userName;
        this.salonId = salonId;
        this.salonName = salonName;
        this.salonAddress = salonAddress;
        this.service = service;
        this.dateTime = dateTime;
        this.price = price;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getSalonId() { return salonId; }
    public String getSalonName() { return salonName; }
    public String getSalonAddress() { return salonAddress; }
    public String getService() { return service; }
    public String getDateTime() { return dateTime; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
}