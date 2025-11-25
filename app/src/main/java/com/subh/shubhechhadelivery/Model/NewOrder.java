package com.subh.shubhechhadelivery.Model;

public class NewOrder {
    private String orderId;
    private String storeName;
    private String ownerName;
    private String location;
    private String status;
    private String date;

    public NewOrder() {
        // Default constructor required for Firebase
    }

    public NewOrder(String orderId, String storeName, String ownerName, String location, String status, String date) {
        this.orderId = orderId;
        this.storeName = storeName;
        this.ownerName = ownerName;
        this.location = location;
        this.status = status;
        this.date = date;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }
}