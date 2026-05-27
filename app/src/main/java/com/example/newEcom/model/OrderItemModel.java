package com.example.newEcom.model;

import com.google.firebase.Timestamp;

public class OrderItemModel {

    private int orderId;
    private int productId;
    private String name;
    private String image;
    private int price;
    private int quantity;
    private Timestamp timestamp;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String comments;

    // 🔴 Required empty constructor for Firebase
    public OrderItemModel(){}

    public OrderItemModel(int orderId, int productId, String name, String image,
                          int price, int quantity, Timestamp timestamp,
                          String fullName, String email, String phoneNumber,
                          String address, String comments) {

        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.comments = comments;
    }

    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getComments() { return comments; }

    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setComments(String comments) { this.comments = comments; }

}