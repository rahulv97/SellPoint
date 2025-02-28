package com.sellpoint.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UsersModel implements Parcelable {

    String id;
    String shopName;
    String ownerName;
    String mobile;
    String email;
    String password;
    String creationDate;
    String expiryDate;

    public UsersModel(String id, String shopName, String ownerName, String mobile, String email, String password,
                      String creationDate, String expiryDate){
        this.id = id;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
        this.expiryDate = expiryDate;
    }

    protected UsersModel(Parcel in) {
        id = in.readString();
        shopName = in.readString();
        ownerName = in.readString();
        mobile = in.readString();
        email = in.readString();
        password = in.readString();
        creationDate = in.readString();
        expiryDate = in.readString();
    }

    public static final Creator<UsersModel> CREATOR = new Creator<UsersModel>() {
        @Override
        public UsersModel createFromParcel(Parcel in) {
            return new UsersModel(in);
        }

        @Override
        public UsersModel[] newArray(int size) {
            return new UsersModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(shopName);
        dest.writeString(ownerName);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(creationDate);
        dest.writeString(expiryDate);
    }
}
