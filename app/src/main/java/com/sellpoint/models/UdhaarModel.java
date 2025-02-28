package com.sellpoint.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UdhaarModel implements Parcelable {

    String id;
    String person_name;
    String purchase_price;
    String description;
    String address;
    String mobile;
    String return_date;
    String creation_date;
    String status;
    String person_image;
    String id_proof;

    public UdhaarModel(String id, String person_name, String purchase_price,String description,String address,
                       String mobile,String return_date,String creation_date,String status,String person_image,
                       String id_proof){

        this.id = id;
        this.person_name = person_name;
        this.purchase_price = purchase_price;
        this.description = description;
        this.address = address;
        this.mobile = mobile;
        this.return_date = return_date;
        this.creation_date = creation_date;
        this.status = status;
        this.person_image = person_image;
        this.id_proof = id_proof;

    }

    protected UdhaarModel(Parcel in) {
        id = in.readString();
        person_name = in.readString();
        purchase_price = in.readString();
        description = in.readString();
        address = in.readString();
        mobile = in.readString();
        return_date = in.readString();
        creation_date = in.readString();
        status = in.readString();
        person_image = in.readString();
        id_proof = in.readString();
    }

    public static final Creator<UdhaarModel> CREATOR = new Creator<UdhaarModel>() {
        @Override
        public UdhaarModel createFromParcel(Parcel in) {
            return new UdhaarModel(in);
        }

        @Override
        public UdhaarModel[] newArray(int size) {
            return new UdhaarModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerson_image() {
        return person_image;
    }

    public void setPerson_image(String person_image) {
        this.person_image = person_image;
    }

    public String getId_proof() {
        return id_proof;
    }

    public void setId_proof(String id_proof) {
        this.id_proof = id_proof;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(person_name);
        dest.writeString(purchase_price);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(mobile);
        dest.writeString(return_date);
        dest.writeString(creation_date);
        dest.writeString(status);
        dest.writeString(person_image);
        dest.writeString(id_proof);
    }
}
