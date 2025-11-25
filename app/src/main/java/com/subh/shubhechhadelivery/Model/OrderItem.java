package com.subh.shubhechhadelivery.Model;

public class OrderItem {
    private String itemName;
    private String itemAmount;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(String itemName, String itemAmount, int quantity) {
        this.itemName = itemName;
        this.itemAmount = itemAmount;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
