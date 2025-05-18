package com.example.muszaki_shop.models;

import com.google.firebase.firestore.PropertyName;

public class Review {
    private String id;

    @PropertyName("uid")
    private String uid;

    @PropertyName("productId")
    private String productId;

    @PropertyName("email")
    private String email;

    @PropertyName("rating")
    private int rating;

    @PropertyName("comment")
    private String comment;

    @PropertyName("imageUrl")
    private String imageUrl;

    @PropertyName("createdAt")
    private long createdAt;

    // Üres konstruktor a Firestore számára
    public Review() {}

    public Review(String uid, String productId, String email, int rating, String comment, String imageUrl, long createdAt) {
        this.uid = uid;
        this.productId = productId;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @PropertyName("productId")
    public String getProductId() {
        return productId;
    }

    @PropertyName("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
