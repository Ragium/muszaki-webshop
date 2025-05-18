package com.example.muszaki_shop.models;

public class User {


    private String uid;
    private String email;
    private String name;
    private String phone;
    private String address;
    private long createdAt;

    // Üres konstruktor a Firestore számára
    public User() {}

    public User(String uid, String email, String name, String phone, String address) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.createdAt = System.currentTimeMillis();
    }

    // Getter és setter metódusok
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
} 