package com.example.muszaki_shop.models;

import com.google.firebase.firestore.PropertyName;

public class CartItem {
    @PropertyName("productId")
    private String productId;
    private String name;
    private String imageUrl;
    private int price;
    private int quantity;

    public CartItem() {
        // Üres konstruktor a Firestore számára
    }

    public CartItem(String productId, String name, String imageUrl, int price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    @PropertyName("productId")
    public String getProductId() {
        return productId;
    }

    @PropertyName("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
} 