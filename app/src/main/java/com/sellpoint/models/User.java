package com.sellpoint.models;

public class User {
    private String shop_name;
    private String full_name;
    private String mobile;
    private String email;
    private String password;
    private String date;
    private String expiryDate;

    public User(String shop_name, String full_name, String mobile, String email, String password,
                String date, String expiryDate) {
        this.shop_name = shop_name;
        this.full_name = full_name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.date = date;
        this.expiryDate = expiryDate;
    }

    // Getters and setters if needed


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
