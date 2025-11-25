package com.subh.shubhechhadelivery.Model;


public class WorkPlace {
    private String id;
    private String storeName;
    private String storeImage;
    private String address;

    public WorkPlace() {
    }

    public WorkPlace(String id, String storeName, String storeImage, String address) {
        this.id = id;
        this.storeName = storeName;
        this.storeImage = storeImage;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}