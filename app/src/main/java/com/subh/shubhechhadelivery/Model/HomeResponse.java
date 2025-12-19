package com.subh.shubhechhadelivery.Model;

import java.util.ArrayList;

public class HomeResponse {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int status;
    public String message;
    public Data data;

    public class Data{
        public ArrayList<TodaysShop> getTodays_shop() {
            return todays_shop;
        }

        public void setTodays_shop(ArrayList<TodaysShop> todays_shop) {
            this.todays_shop = todays_shop;
        }

        public ArrayList<Order> getOrders() {
            return orders;
        }

        public void setOrders(ArrayList<Order> orders) {
            this.orders = orders;
        }

        public ArrayList<TodaysShop> todays_shop;
        public ArrayList<Order> orders;
    }

    public class Order{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public int getItems_count() {
            return items_count;
        }

        public void setItems_count(int items_count) {
            this.items_count = items_count;
        }

        public String getItem_images() {
            return item_images;
        }

        public void setItem_images(String item_images) {
            this.item_images = item_images;
        }

        public ArrayList<String> getItem_images_array() {
            return item_images_array;
        }

        public void setItem_images_array(ArrayList<String> item_images_array) {
            this.item_images_array = item_images_array;
        }

        public int id;
        public String orderno;
        public String created_at;
        public String status;
        public String total;
        public int shop_id;
        public int items_count;
        public String item_images;
        public ArrayList<String> item_images_array;
    }

    public class TodaysShop{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getActive_status() {
            return active_status;
        }

        public void setActive_status(String active_status) {
            this.active_status = active_status;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public int id;
        public String name;
        public String image;
        public String mobile;
        public String address;
        public String active_status;
        public String image_path;
    }
}
